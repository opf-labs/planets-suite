/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.impl.system.batch.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.impl.system.batch.TestbedBatchJob;
import eu.planets_project.tb.impl.system.batch.TestbedBatchProcessorManager;

/**
 * Common class for the different MDBs that just differ in their time-out interval
 * The Listener retrieves the specific BatchProcessor implementation as identified by the
 * TestbedBatchProcessorManagerspecified and performs check/notify operations on a given ticket.
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 24.10.2009
 *
 */
public class BatchExperimentListenerImpl {
	private static final Log log = LogFactory.getLog(BatchExperimentListenerImpl.class);
    //the period between re-querying the workflow system on this ticket
    private final int sleep = 30000;
    
	
	public void doOnMessage(Message m, long timeOutMillis, Thread thread){
		log.debug("BatchExecutionListener: doOnMessage");
		//1.check if message got redelivered before doing any processing
    	try {
			if(m.getJMSRedelivered()){
				log.debug("BatchExperimentListener_shortTimeout: onMessage: re-receive message from the queue. Not processing it");
				return;
			}
		} catch (JMSException e) {
			log.debug(e);
			return;
		}
		
		//2. extract the message: it contains the ticket for identifying the job on the workflow system
    	TextMessage msg = null;
    	String ticket="";
    	String batchProcessorID="";
        if (m instanceof TextMessage) {
            msg = (TextMessage) m;
            try {
            	 ticket = msg.getText();
                 batchProcessorID = msg.getStringProperty(BatchProcessor.QUEUE_PROPERTY_NAME_FOR_SENDING);
                 
				log.debug("BatchExperimentListener_shortTimeout: received message at timestamp: " + msg.getJMSTimestamp()+" for ticket: "+ticket+ "on batch processor: "+batchProcessorID);
			} catch (JMSException e) {
				log.debug(e);
				return;
			}
        }else{
        	return;
        }
       
        //3. get the BatchProcessing system that's notified
        TestbedBatchProcessorManager batchManager = TestbedBatchProcessorManager.getInstance();
        BatchProcessor bp = batchManager.getBatchProcessor(batchProcessorID);
        
        //check rollback and if batch processor has persistent jobs
        if((bp.getJob(ticket)==null) || (bp.getJobStatus(ticket).equals(TestbedBatchJob.NO_SUCH_JOB ))){
        	log.debug("EJBTransactionRollback - BatchProcessor no persistent job - dropp job: "+ticket);
        	return;
        }
        
        //4. check for updates and store extracted information in experiment
        long t0 = System.currentTimeMillis();
        long t1 = System.currentTimeMillis();
        
        boolean bInfomredStarted = false;
        //do until we have reached our time-out time or completed
        while(t1-t0<timeOutMillis){
        
        	//a) check started
        	//use batchProcessor specific implementation for checking on this.
        	boolean bStarted = bp.isStarted(ticket);
        	if(bStarted){
        		if(!bInfomredStarted){
	        		//callback: inform once about started
	        		bp.notifyStart(ticket, bp.getJob(ticket));
	        		log.debug("BatchExecutionListener: notify STARTED for: "+ticket);
	        		bInfomredStarted=true;
        		}
        		
        		if(bp.isRunning(ticket)){
        			TestbedBatchJob job = bp.getJob(ticket);
        			job.setStatus(TestbedBatchJob.RUNNING);
        			bp.notifyRunning(ticket, job);
	        		log.debug("BatchExecutionListener: notify RUNNING for: "+ticket);

        		}
        		
        		if(bp.isUpdated(ticket)){
        			bp.notifyUpdate(ticket, bp.getJob(ticket));
	        		log.debug("BatchExecutionListener: notify UPDATE for: "+ticket);

        		}
        		
        		if(bp.isFailed(ticket)){
        			TestbedBatchJob job = bp.getJob(ticket);
        			job.setStatus(TestbedBatchJob.FAILED);
        			bp.notifyFailed(ticket, job);
	        		log.debug("BatchExecutionListener: notify FAILED for: "+ticket);

        		}
        		
        		//check if completed
        		if(bp.isCompleted(ticket)){
        			TestbedBatchJob job = bp.getJob(ticket);
        			job.setStatus(TestbedBatchJob.DONE);
        			bp.notifyComplete(ticket, job);
	        		log.debug("BatchExecutionListener: notify COMPLETE for: "+ticket);

        			return;
        		}
        		
        	}
        	
        	//status is: still no completed - sleep and repoll
            try {
            	Thread.sleep(sleep);
    		} catch (InterruptedException e) {
    			log.debug("Error while waiting for ticket: "+ticket, e);
    			TestbedBatchJob job = bp.getJob(ticket);
    			job.setStatus(TestbedBatchJob.FAILED);
    			bp.notifyFailed(ticket, job);
    			return;
    		}
    		t1 = System.currentTimeMillis();
        }
        
        //b) in this case a time-out occurred
        TestbedBatchJob job = bp.getJob(ticket);
		job.setStatus(TestbedBatchJob.FAILED);
		job.setWorkflowFailureReport(new String("BatchExperimentListener with timeout of "+timeOutMillis/1000+" Sec. has timed-out. This normally indicates a failure within the remote workflow execution processor"));
		bp.notifyFailed(ticket, job);
		log.debug("BatchExecutionListener: notify FAILED due to time-out for: "+ticket);
	}

}
