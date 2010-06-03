/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.wee.api.WeeManager;
import eu.planets_project.ifr.core.wee.api.WorkflowExecutionStatus;
import eu.planets_project.ifr.core.wee.api.utils.WFResultUtil;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.wsinterface.WeeService;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 * The WeeManager is a POJO and follows the Singleton Pattern. It's not implemented
 * as stateful SessionBean as the Client interface (web-service) would not able to 
 * maintain a reference the EJB's stub object and therefore would lose state.
 * 
 * All workflow submissions are laid in the FIFO Queue which then get processed asynchronously 
 * one by one by the WEE MessageListeners onMessage method. It is implemented as
 * MessageDrivenBean with a maxPoolsize of one job running simultaneously. Results are
 * reported back by the Managers's notify methods.
 *
 * TODO For now UUIDs, mappings, WFResults and execution status are kept 
 * in memory. This should be switched to a JPA solution.
 * 
 */
public class WeeManagerImpl implements WeeManager, Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -3148507118149727434L;
	private static final Log log = LogFactory.getLog(WeeManagerImpl.class);
	//contains FIFO of workflows to execute. only wfs that have not yet been executed
    private LinkedList<WorkflowInstance> localJobqueue;
    //mapping of UUIDs to submitted and already executed Workflows
    private Map<UUID,WorkflowInstance> mapUUIDtoWF;
    //mapping of UUIDs to execution status
    private Map<UUID,WorkflowExecutionStatus> mapUUIDtoExecStatus;
    //mapping of UUIDs to progress. -1..0-100
    private Map<UUID,Integer> mapUUIDtoProgress;
    //mapping of UUIDs to execution results
    private Map<UUID,WorkflowResult> mapUUIDtoExecResults;
    //the singleton ojbect
    private static WeeManager weeManager;
    //JMS configuration
    private static final String QueueConnectionFactoryName = "ConnectionFactory";
    private static final String QueueName = "queue/wfExecQueue";
    private DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
    
    
    private WeeManagerImpl(){
    	//this Pojo implements the singleton Pattern
    	 localJobqueue = new LinkedList<WorkflowInstance>();
    	 //mapUUIDtoWF = new HashMap<UUID,WorkflowInstance>();
    	 mapUUIDtoExecStatus = new HashMap<UUID,WorkflowExecutionStatus>();
    	 mapUUIDtoExecResults = new HashMap<UUID,WorkflowResult>();
    	 mapUUIDtoProgress = new HashMap<UUID,Integer>();
    	 mapUUIDtoWF = new HashMap<UUID,WorkflowInstance>();
    }
    

    /**
     * Single instance created upon class loading.
     */
    public static synchronized WeeManager getWeeManagerInstance(){
    	if(weeManager==null){
    		weeManager = new WeeManagerImpl();
    	}
    	return weeManager;
    }


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#getPositionInQueue(java.util.UUID)
	 */
	public int getPositionInQueue(UUID ticket) throws PlanetsException{
		if(this.mapUUIDtoWF.containsKey(ticket)){
			WorkflowInstance wf = mapUUIDtoWF.get(ticket);
			return this.localJobqueue.indexOf(wf);
		}
		log.debug("WEEManager: requesting position for unknown ticket #"+ticket);
		throw new PlanetsException("WEEManager: requesting position for unknown ticket #"+ticket);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#getStatus(java.util.UUID)
	 */
	public WorkflowExecutionStatus getStatus(UUID ticket) throws PlanetsException{
		if(isTicketKnown(ticket)){
			return this.mapUUIDtoExecStatus.get(ticket);
		}
		log.debug("WEEManager: requesting status for unknown ticket #"+ticket);
		throw new PlanetsException("WEEManager: requesting status for unknown ticket #"+ticket);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#submitWorkflow(eu.planets_project.ifr.core.wee.api.Workflow)
	 */
	public UUID submitWorkflow(WorkflowInstance workflow) {
		//UUID id = this.generateUUID();
		//add the wf to the queue of wfs in execution
		this.localJobqueue.add(workflow);
		//write all indices
		this.mapUUIDtoExecStatus.put(workflow.getWorkflowID(),WorkflowExecutionStatus.SUBMITTED);
		this.mapUUIDtoWF.put(workflow.getWorkflowID(),workflow);
		log.debug("WEEManager: submitted workflow with ticket #"+workflow.getWorkflowID());
		
		try{
			//and finally submit the message to the execution engines's message queue
			this.submitJobToQueue(workflow.getWorkflowID(), workflow);
			
		}catch(Exception e){
			//in case we weren't able to send the message:
			this.localJobqueue.remove(workflow);
			this.mapUUIDtoExecStatus.put(workflow.getWorkflowID(),WorkflowExecutionStatus.FAILED);
			log.error("WEEManager: error in submitting the message to the execution engine's queue",e);
		}
		
		return workflow.getWorkflowID();
	}
	
	
	/**
	 * Builds the JMS Message with the Workflow and UUID as payload and submits them
	 * on the WEE's queue for execution.
	 */
	private void submitJobToQueue(UUID id, WorkflowInstance wf) throws Exception{
		Context         ctx      = null;
	    QueueConnection cnn  = null;
	    QueueSession    sess  = null;
	    Queue           queue    = null;
	    QueueSender     sender   = null;
		try{
			ctx = new InitialContext();
		    QueueConnectionFactory factory =
		        (QueueConnectionFactory) ctx.lookup(QueueConnectionFactoryName);
		    queue = (Queue) ctx.lookup(QueueName);
		    cnn = factory.createQueueConnection();
		    sess = cnn.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
		    //e.g. a TextMessage
		    TextMessage message = sess.createTextMessage(id.toString());

			
			//defining the payload of the Message
		    /*
		     * MapMessages only for primitive java types
		     * MapMessage message = sess.createMapMessage();
		     * message.setObject("workflow", wf); -> not possible
	         * message.setString("UUID", id.toString());
		     */
		    //ObjectMessage message = sess.createObjectMessage();
		    //message.setObject(wf); -> WFInstance is fetched by execution queue via WeeManager.getWorkflowInstance
		    //message.setStringProperty("UUID", id.toString());
	        
	        //and finally send the message to the queue.
	        sender = sess.createSender(queue);
			sender.send(message);
			log.debug("WEEManager: sent message to queue, ID:"+message.getJMSMessageID());
		} finally {
		      try { if( null != sender)   sender.close();  } catch( Exception ex ) {}
		      try { if( null != sess) 	  sess.close(); }    catch( Exception ex ) {}
		      try { if( null != cnn)      cnn.close(); }     catch( Exception ex ) {}
		      try { if( null != ctx) 	  ctx.close();     } catch( Exception ex ) {}
		    }

	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#notify(java.util.UUID, eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.ifr.core.wee.api.WorkflowExecutionStatus)
	 */
	public synchronized void notify(UUID ticket, WorkflowResult wfResult, WorkflowExecutionStatus executionStatus){
		this.notify(ticket, wfResult, executionStatus,-1);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#notify(java.util.UUID, eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.ifr.core.wee.api.WorkflowExecutionStatus, java.lang.Integer)
	 */
	public synchronized void notify(UUID ticket, WorkflowResult wfResult, WorkflowExecutionStatus executionStatus, int progress){
		if(this.isTicketKnown(ticket)){
			this.mapUUIDtoExecStatus.put(ticket, executionStatus); 
			this.mapUUIDtoExecResults.put(ticket, wfResult);
			this.mapUUIDtoProgress.put(ticket, progress);
			if((executionStatus.equals(WorkflowExecutionStatus.FAILED))||(executionStatus.equals(WorkflowExecutionStatus.COMPLETED))){
				//persist the wfResult as file to disk
				persistWFResultToDisk(ticket, wfResult);
				//remove workflow from local execution queue - if it's not 'running' or 'submitted'
				removeWorkflowFromLocalQueue(ticket);
			}
		}
		log.debug("WEEManager: notify called from execution engine on status: "+executionStatus+" wfReult #"+wfResult+" ticket #"+ticket);
	}
	
	/**
	 * Persists the wfResult to disk
	 * @param wfResult
	 * @param progress
	 */
	private void persistWFResultToDisk(UUID ticket,WorkflowResult wfResult){
		//persist the wfResult as local file
		try{
			File fwfXML = WFResultUtil.marshalWorkflowResultToXMLFile(wfResult,ticket+"");
			URI drManagerID = DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/executions/").normalize();
			URI storageURI =new URI(drManagerID.getScheme(),drManagerID.getAuthority(),drManagerID.getPath()+"/"+ticket+"/wfResult-id-"+ticket+".xml",null,null).normalize();
			DigitalObject digowfXML = new DigitalObject.Builder(Content.byReference(fwfXML)).title("wfResult-id-ticket").build();
			URI uriStored = dataRegistry.getDigitalObjectManager(drManagerID).storeAsNew(storageURI,digowfXML);
			log.info("persisted WFResult for: "+ticket+" in: "+drManagerID+" under: "+uriStored);
		}catch(Exception e){
			log.debug("error marshalling wfResult->xml to disk "+e);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#notify(java.util.UUID, eu.planets_project.ifr.core.wee.api.WorkflowExecutionStatus)
	 */
	public synchronized void notify(UUID ticket, WorkflowExecutionStatus status){
		if(this.isTicketKnown(ticket)){
			this.mapUUIDtoExecStatus.put(ticket, status);
		}
		log.debug("WEEManager: notify called from execution engine on status: "+status+" ticket #"+ticket);
	}
	
	private void removeWorkflowFromLocalQueue(UUID ticket){
		WorkflowInstance executedWF = this.mapUUIDtoWF.get(ticket);
		//checkMessagesSkipped(executedWF);
		log.debug("WEEManager: removing ticket#: "+ticket+" from local job queue");
		this.localJobqueue.remove(executedWF);
	}
	
	/*
	 * TODO performs a check if any message was lost or skipped. As it's a FIFO queue
	 * no notification of a later message should arrive before the most current one in the queue.
	 * If yes - all in between are set to status 'Failure' and are taken from the queue
	 * should also be marked as 'failed' when removed from the localJobQueue
	 */
	/*private void checkMessagesSkipped(Workflow currExecWF){
		for(int i=0; i<this.localJobqueue.size(); i++){
			Workflow topWF = this.localJobqueue.getFirst();
			if(topWF.equals(currExecWF)){
				//great the engine didn't skip any messages
				break;
			}
			else{
				//remove from local queue - should also be marked as 'failed'
				this.localJobqueue.remove(topWF);
			}
		}
	}*/


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#getExecutionResult(java.util.UUID)
	 */
	public WorkflowResult getExecutionResult(UUID ticket)
			throws PlanetsException {
		
		//check if ticket is known
		if(isTicketKnown(ticket)){
			//check for (intermediate or final) results
			if(this.mapUUIDtoExecResults.containsKey(ticket)){
				//now fetch the execution's result data 
				return this.mapUUIDtoExecResults.get(ticket);
			}
			else{
				log.debug("WEEManager: requesting WEE Execution Result failed for workflow ticket #"+ticket+".");
				throw new PlanetsException("WEEManager: requesting WEE Execution Result for a scheduled but not executed or failed workflow. ticket #"+ticket);
			}
		}
		else{
			log.debug("WEEManager: requesting WEE Execution Result for unknown ticket #"+ticket);
			throw new PlanetsException("WEEManager: requesting WEE Execution Result for unknown ticket #"+ticket);
		}
	}
	
	/**
	 * Checks if a given ticket has been ever been submitted independent if it 
	 * was already executed or not
	 * @param ticket
	 * @return
	 */
	private boolean isTicketKnown(UUID ticket){
		return this.mapUUIDtoWF.containsKey(ticket);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#isExecutionCompleted(java.util.UUID)
	 */
	public boolean isExecutionCompleted(UUID ticket) {
		return this.mapUUIDtoExecStatus.get(ticket).
					equals(WorkflowExecutionStatus.COMPLETED);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#isExecutionFailed(java.util.UUID)
	 */
	public boolean isExecutionFailed(UUID ticket) {
		return this.mapUUIDtoExecStatus.get(ticket).
		equals(WorkflowExecutionStatus.FAILED);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.WeeManager#isExecutionRunning(java.util.UUID)
	 */
	public boolean isExecutionRunning(UUID ticket) {
		return this.mapUUIDtoExecStatus.get(ticket).
		equals(WorkflowExecutionStatus.RUNNING);
	}
	
	/**
	 *  A WorkflowInstance object cannot be Serialized due to the org.jboss.ws.core.jaxws.client.ClientProxy 
	 *  it contains that cannot be serialized. Therefore this method serves as a callback to the WorkflowExecutionEngine
	 *   for fetching the required object
	 * @param ticket
	 * @return
	 */
	protected WorkflowInstance getWorkflowInstance(UUID ticket) throws Exception{
		if((ticket!=null)&&(this.mapUUIDtoWF.containsKey(ticket))){
			return this.mapUUIDtoWF.get(ticket);
		}
		else{
			String err = "WeeManagerImpl error in callback from WorkflowExecutionEngine fetching object for ID "+ticket;
			log.debug(err);
			throw new Exception(err);
		}
	}
	
	
    /**
     * Hook up to a new instance of the stateless Planets WEE Service EJB. 
     * @return A WeeServiceStub, lookup via JNDI.
     */
    private WeeService getRemoteWeeServiceByJNDI() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            WeeService wee = (WeeService) PortableRemoteObject.narrow(
                    jndiContext.lookup("planets-project.eu/WeeService/remote"), WeeService.class);
            return wee;
        }catch (NamingException e) {
            log.error("Failure during lookup of the WeeService PortableRemoteObject: "+e.toString());
            return null;
        }
    }


	/** {@inheritDoc} */
	public int getProgress(UUID ticket) throws PlanetsException {
		if(this.mapUUIDtoWF.containsKey(ticket)){
			if(this.mapUUIDtoProgress.containsKey(ticket)){
				//execution taking place or complete, return progress
				return mapUUIDtoProgress.get(ticket);
			}
			else{
				//known ticket, but execution not yet started
				return -1;
			}
		}
		log.debug("WEEManager: requesting progress for unknown ticket #"+ticket);
		throw new PlanetsException("WEEManager: requesting progress for unknown ticket #"+ticket);
	}
    
}
