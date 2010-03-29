/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.wee.api.WeeManager;
import eu.planets_project.ifr.core.wee.api.WorkflowExecutionStatus;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.services.datatypes.DigitalObject;


/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 10.11.2008: 
 * This is the Workflow Execution Enginge (WEE) background thread.  
 * It's exposed as MessageDrivenBean with a maxPoolSize of one, which guarantees 
 * that only a single instance of the MDB is running and only one job at a time is executed.
 * Workflow jobs are submitted to the wfExecQueue as Workflow as payload object
 * which the get fetched in a FIFO order and executed by the blocking onMessage() of this class. 
 * @see http://www.jboss.org/community/docs/DOC-9899.pdf
 * 
 * How to handle inputs and outputs?  For this the Planets DigitalObject has been
 * created which contains the payload as well as the execution results. Pointers
 * are referring to the DR where the data is persisted.
 * 
 * is a blocking operation and as the maxPoolsize is restricted to one MDBean it's guaranteed that
 * only one job is processed at a time
 * @see http://docs.jboss.com/jbossas/javadoc/4.0.2/org/jboss/resource/adapter/jms/inflow/JmsActivationSpec.html on ActivationSpecProperties
 * 
 * Please note: not in use, but a thread-safe FIFO Queue implementation: ConcurrentLinkedQueue.
 * http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/ConcurrentLinkedQueue.html
 * 
 * Execution times can be measured using:
 * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/System.html#nanoTime()
 * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/System.html#currentTimeMillis()
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 * This message driven bean is the job processor.
 * 
 * The ActivationConfig: maxSession (before: maxPoolSize) ensures that this is a singleton, and thus only one job is processed at a time.
 * http://www.jboss.com/index.html?module=bb&op=viewtopic&p=3976836#3976836
 * 
 */
@MessageDriven( name = "WorkflowExecutionEngine", 
				activationConfig =  {
					@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
					@ActivationConfigProperty(propertyName="destination", propertyValue="queue/wfExecQueue"),
        			@ActivationConfigProperty(propertyName="maxSession", propertyValue="1")
				})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class WorkflowExecutionEngineImpl implements MessageListener {
	private static final Log log = LogFactory.getLog(WorkflowExecutionEngineImpl.class);
    @Resource
    public MessageDrivenContext mdc;

    
    public WorkflowExecutionEngineImpl(){
    	//empty - required for MDB creation
    }
    
    /* 
     * MessageListener interface which is used to receive a submitted workflow for execution from the queue
     * As this is a blocking operation and as the maxPoolsize is restricted to one MDBean it's guaranteed that
     * only one job is being processed at a time.
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message m) {
        
    	//check if message got redelivered before doing any processing
    	try {
			if(m.getJMSRedelivered()){
				log.debug("WorkflowExecutionEngine: onMessage: re-receive message from the queue. Not processing it");
				return;
			}
		} catch (JMSException e) {
			log.debug(e);
		}
    	
    	//1) get the WEEManager instance - required in the same JVM
        WeeManager weeManager = WeeManagerImpl.getWeeManagerInstance();
        
    	//2) extract the Message's payload	
    	WorkflowInstance wf = null;
    	UUID uuid = null; 
	    try {
	    	TextMessage msg = null;
	        if (m instanceof TextMessage) {
	            msg = (TextMessage) m;
	            log.debug("WorkflowExecutionEngine: received ObjectMessage at timestamp: " + msg.getJMSTimestamp());
	        }
	        // for ObjectMessages: uuid = UUID.fromString(msg.getStringProperty("UUID"));
	        uuid = UUID.fromString(msg.getText());
	        /*
	         *  WorkflowInstance object cannot be Serialized due to the org.jboss.ws.core.jaxws.client.ClientProxy 
	         *  it contains that cannot be serialized. Therefore a callback to the weeManager for fetching this object
	         *  is performed.
	         *  Not possible:  wf = (WorkflowInstance)msg.getObject();
	         */
	        wf = ((WeeManagerImpl)weeManager).getWorkflowInstance(uuid);
	        //for testing poolsize 1
	        //Thread.currentThread().sleep(30000);

	    } catch (Exception e) {
	        log.error("WorkflowExecutionEngine: error receiving message workflow payload or UUID",e);
	        if(uuid!=null){
	        	weeManager.notify(uuid,WorkflowExecutionStatus.FAILED);
	        }
	        return;
	    }
                
        
        //set status for the workflow inISRUNNING
	    weeManager.notify(uuid,WorkflowExecutionStatus.RUNNING);

        //3) executeWorkflow and get WF Result
	    WorkflowResult ret = null;
	    try {
	    	log.debug("WorkflowExecutionEngine: start executing wf ID: " + wf.getWorkflowID());
	    	
	    	//EXECUTES THE WF INSTANCE
			List<DigitalObject> payload = wf.getData();
			int count = 1;
			for(DigitalObject digo : payload){
				//process the payload item by item - workflowResult appends individual log items
				ret = wf.execute(digo);
				count+=1;
				int progress = (100/payload.size())*count;
				weeManager.notify(uuid, ret, WorkflowExecutionStatus.RUNNING, progress);
			}
			ret = wf.finalizeExecution();
			
			
			log.debug("WorkflowExecutionEngine: completed executing wf ID: " + wf.getWorkflowID());
		} catch (Exception e) {
			log.error("WorkflowExecutionEngine: error running Workflow.execute()",e);
			//set WeeManagerstatus 'failed'
			weeManager.notify(uuid, WorkflowExecutionStatus.FAILED);
			return;
		}
        
		//4) call WEEManager.notify to report back results and the status
        weeManager.notify(uuid, ret, WorkflowExecutionStatus.COMPLETED);
    }

}
