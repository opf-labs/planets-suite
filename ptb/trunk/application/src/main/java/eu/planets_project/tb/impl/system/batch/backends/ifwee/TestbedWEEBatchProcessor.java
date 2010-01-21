package eu.planets_project.tb.impl.system.batch.backends.ifwee;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import eu.planets_project.ifr.core.wee.api.WorkflowExecutionStatus;
import eu.planets_project.ifr.core.wee.api.utils.WorkflowConfigUtil;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.api.wsinterface.WeeService;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.tb.impl.services.util.wee.WeeRemoteUtil;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;
import eu.planets_project.tb.impl.system.batch.TestbedBatchProcessorManager;
import eu.planets_project.tb.impl.system.batch.backends.tbown.TestbedBatchProcessor;
import eu.planets_project.tb.impl.system.batch.listener.BatchExperimentListenerShortTimeout;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.ifr.core.wee.api.utils.WorkflowConfigUtil;

/**
 * The WEE implementation of a Testbed BatchProcessor. Provides checking procedures for events used
 * by the BatchExperimentListener (MDB) as well as notify operations for reporting back. All information
 * exchanged between this class and the MDB are passed through the TestbedBatchJOb object (e.g. WorkflowResult, etc.)
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 23.10.2009
 *
 */
public class TestbedWEEBatchProcessor implements BatchProcessor{
	
	private static final Log log = LogFactory.getLog(TestbedWEEBatchProcessor.class);
	
	private static TestbedWEEBatchProcessor instance;
	private static TestbedManager tbManager;
	private static WeeService weeService;
	private static WEEBatchExperimentTestbedUpdater weeTBUpdater;
	//make sure it's a synchronized Map
	private Map<String,TestbedBatchJob> jobs = Collections.synchronizedMap(new HashMap<String,TestbedBatchJob>());
	
    //JMS configuration
    private static final String QueueConnectionFactoryName = "ConnectionFactory";
    private static final String QueueName_shortTimeout = "queue/tbBatchExecQueue_shortTimeout";
    private static final String QueueName_longTimeout = "queue/tbBatchExecQueue_longTimeout";
	
	public static synchronized TestbedWEEBatchProcessor getInstance(){
		if (instance == null){
			instance = new TestbedWEEBatchProcessor();
			tbManager = TestbedManagerImpl.getInstance();
			weeService = WeeRemoteUtil.getInstance().getWeeService();
			weeTBUpdater = new WEEBatchExperimentTestbedUpdater();
		}
		return instance;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getBatchProcessorSystemIdentifier()
	 */
	public String getBatchProcessorSystemIdentifier() {
		return this.BATCH_QUEUE_TESTBED_WEE_LOCAL;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getJob(java.lang.String)
	 */
	public synchronized TestbedBatchJob getJob(String job_key) {
		 return jobs.get(job_key);
	}
	
	public synchronized void setJob(String job_key, TestbedBatchJob job){
		this.jobs.put(job_key, job);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getJobPercentComplete(java.lang.String)
	 */
	public synchronized int getJobPercentComplete(String job_key) {
        if(this.isCompleted(job_key)){
        	return 100;
        }
        else{
        	return -1;
        }
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getJobStatus(java.lang.String)
	 */
	public synchronized String getJobStatus(String job_key) {
        if( this.getJob(job_key) == null ) return TestbedBatchJob.NO_SUCH_JOB;
        return this.getJob(job_key).getStatus();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getPositionInQueue(java.lang.String)
	 */
	public synchronized String getPositionInQueue(String job_key) {
		if( this.getJob(job_key) == null ) return TestbedBatchJob.POSITION_NOT_SUPPORTED;
		if( this.getJobStatus(job_key).equals(TestbedBatchJob.RUNNING)){
			return TestbedBatchJob.POSITION_IN_PROGRESS;
		}
		if( this.getJobStatus(job_key).equals(TestbedBatchJob.DONE)){
			return TestbedBatchJob.POSITION_COMPLETED;
		}
		try {
			return weeService.getPositionInQueue(UUID.fromString(job_key))+"";
		} catch (Exception e) {
			log.debug("WEE getPositionInQueue error for job: "+job_key);
			return TestbedBatchJob.POSITION_NOT_SUPPORTED;
		}
	}

	@Deprecated
	public String submitBatch(long expID, ExperimentWorkflow workflow,
			Collection<String> digitalObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * since TB version 1.0
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#sumitBatch(long, java.util.List, java.lang.String, eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf)
	 */
	public String sumitBatch(long expID, List<DigitalObject> digObjs, WorkflowConf workflowConfig) {
		
		String retTicket ="";
		//0. create a TestbedBatchJob object that's used to temporarily park the results
		 TestbedBatchJob testbedBatchJob = new TestbedBatchJob( expID);

		//1. submit the Workflow to the WEE System and receive a ticket
		try {
			retTicket = "" + TestbedWEEBatchProcessor.weeService.submitWorkflow((ArrayList<DigitalObject>) digObjs, workflowConfig.getTemplate().getClazz(), new WorkflowConfigUtil().marshalWorkflowConfigToXMLTemplate(workflowConfig));
			jobs.put(retTicket, testbedBatchJob);
			
	        Experiment exp = tbManager.getExperiment(expID);   
	        //2. decide how long we want to poll for an object before from the engine before we throw a time-out
	        //2a. automatically approved experiments - use shortTimeout (5 Minutes)
	        if(exp.getExperimentApproval().getApprovalUsersIDs().contains(AdminManagerImpl.APPROVAL_AUTOMATIC_USER)){
	        	submitTicketForPollingToQueue(retTicket,this.QueueName_shortTimeout,this.getBatchProcessorSystemIdentifier());
	        }
	        //2b manually approved experiments - use longTimeout (3 Days)
	        if(!exp.getExperimentApproval().getApprovalUsersIDs().contains(AdminManagerImpl.APPROVAL_AUTOMATIC_USER)){
	        	submitTicketForPollingToQueue(retTicket,this.QueueName_longTimeout,this.getBatchProcessorSystemIdentifier());
	        }
	        return retTicket;
		} catch (Exception e) {
			log.error("Error when remote submit workflow to WEE or ticket to pollingQueue was called",e);
			return "";
		}
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#submitTicketForPollingToQueue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void submitTicketForPollingToQueue(String ticket, String queueName, String batchProcessorSystemID)throws Exception{
		Context         ctx      = null;
	    QueueConnection cnn  = null;
	    QueueSession    sess  = null;
	    Queue           queue    = null;
	    QueueSender     sender   = null;
		try{
			ctx = new InitialContext();
		    QueueConnectionFactory factory =
		        (QueueConnectionFactory) ctx.lookup(QueueConnectionFactoryName);
		    queue = (Queue) ctx.lookup(queueName);
		    cnn = factory.createQueueConnection();
		    sess = cnn.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
		    
		    //create the message to send to the MDB e.g. a TextMessage
		    TextMessage message = sess.createTextMessage(ticket);
		    message.setStringProperty(this.QUEUE_PROPERTY_NAME_FOR_SENDING, batchProcessorSystemID);

	        //and finally send the message to the queue.
	        sender = sess.createSender(queue);
			sender.send(message);
			log.debug("TestbedWEEBatchProcessor: sent message to queue, ID:"+message.getJMSMessageID());
		} finally {
		      try { if( null != sender)   sender.close();  } catch( Exception ex ) {}
		      try { if( null != sess) 	  sess.close(); }    catch( Exception ex ) {}
		      try { if( null != cnn)      cnn.close(); }     catch( Exception ex ) {}
		      try { if( null != ctx) 	  ctx.close();     } catch( Exception ex ) {}
		    }
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyComplete(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public synchronized void notifyComplete(String job_key, TestbedBatchJob job) {
		//this.setJob(job_key, job);
		WorkflowResult wfLog=null;
		try {
			wfLog = weeService.getResult(UUID.fromString(job_key));
		} catch (Exception e) {
			log.debug("error building UUID from String "+job_key+ "processNotify_WorkflowCompleted without a WorkflowResult");
		}
		weeTBUpdater.processNotify_WorkflowCompleted(job.getExpID(),wfLog);
		log.debug("callback notify wee for "+job_key);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyFailed(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public synchronized void notifyFailed(String job_key, TestbedBatchJob job) {
		//this.setJob(job_key, job);
		weeTBUpdater.processNotify_WorkflowFailed(job.getExpID(),(String)job.getWorkflowFailureReport());
		log.debug("callback notify wee for "+job_key);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyRunning(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public synchronized void notifyRunning(String job_key, TestbedBatchJob job) {
		// TODO AL: this is currently not supported by the WEE implementation
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyUpdate(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public synchronized void notifyUpdate(String job_key, TestbedBatchJob job) {
		//TODO AL: currently not used - as not supported by WEE... 
		//This could be used for pulling incremental updates in
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#notifyStart(java.lang.String, eu.planets_project.tb.impl.system.TestbedBatchJob)
	 */
	public synchronized void notifyStart(String job_key, TestbedBatchJob job) {
		//this.setJob(job_key, job);
		weeTBUpdater.processNotify_WorkflowStarted(job.getExpID());
		log.debug("callback notify wee for "+job_key);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isRunning(java.lang.String)
	 */
	public synchronized boolean isStarted(String job_key){
		try {
			String status = weeService.getStatus(UUID.fromString(job_key));
			if(
			status.equals(WorkflowExecutionStatus.RUNNING.toString())||
			status.equals(WorkflowExecutionStatus.COMPLETED.toString())||
			status.equals(WorkflowExecutionStatus.FAILED.toString())){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			log.error("unable to build UUID for "+job_key+" "+e);
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isCompleted(java.lang.String)
	 */
	public synchronized boolean isCompleted(String job_key) {
		try {
			String status = weeService.getStatus(UUID.fromString(job_key));
			if(status.equals(WorkflowExecutionStatus.COMPLETED.toString())||
			   status.equals(WorkflowExecutionStatus.FAILED.toString())){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			log.error("unable to build UUID for "+job_key+" "+e);
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isUpdated(java.lang.String)
	 */
	public synchronized boolean isUpdated(String job_key) {
		// TODO AL. This operation is currently not supported by the WEE. Would allow  checking for incremental updates
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#getWorkflowEngineResult(java.lang.String)
	 */
	public Object getWorkflowEngineResult(String job_key) {
		try {
			return weeService.getResult(UUID.fromString(job_key));
		} catch (Exception e) {
			log.debug("getWorkflowEngineResult failed "+e);
			return new WorkflowResult();
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isFailed(java.lang.String)
	 */
	public synchronized boolean isFailed(String job_key) {
		try {
			String status = weeService.getStatus(UUID.fromString(job_key));
			if(status.equals(WorkflowExecutionStatus.FAILED.toString())){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			log.error("unable to build UUID for "+job_key+" "+e);
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isRunning(java.lang.String)
	 */
	public synchronized boolean isRunning(String job_key) {
		try {
			String status = weeService.getStatus(UUID.fromString(job_key));
			if(status.equals(WorkflowExecutionStatus.RUNNING.toString())){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			log.error("unable to build UUID for "+job_key+" "+e);
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.system.batch.BatchProcessor#isQueued(java.lang.String)
	 */
	public synchronized boolean isQueued(String job_key) {
		try {
			String status = weeService.getStatus(UUID.fromString(job_key));
			if((!status.equals(WorkflowExecutionStatus.RUNNING.toString()))&&
			   (!status.equals(WorkflowExecutionStatus.COMPLETED.toString()))){
				return true;
			}
			else if(status.equals(WorkflowExecutionStatus.SUBMITTED.toString())){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			log.error("unable to build UUID for "+job_key+" "+e);
			return false;
		}
	}

}
