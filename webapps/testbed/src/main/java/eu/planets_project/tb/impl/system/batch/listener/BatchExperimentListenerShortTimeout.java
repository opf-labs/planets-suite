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

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.system.BackendProperties;

/**
 * A MessageDriven Bean for receiving tickets of currently (externally) processed
 * workflows and polls in a given period of time for updates
 * updates can include: (partially) updating experiment data, metadata as percentage of completion , etc.
 * but at least must inform that a workflow execution terminated or timed-out
 * 
 * timeout is set to 5 minutes for polling on an execution result
 * up to 5 beans of this type for processing messages at the same time are allowed.
 * 
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 21.10.2009
 *
 */
@MessageDriven( name = "BatchExperimentListener_shortTimeoute", 
		activationConfig =  {
			@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
			@ActivationConfigProperty(propertyName="destination", propertyValue="queue/tbBatchExecQueue_shortTimeout"),
			@ActivationConfigProperty(propertyName="maxSession", propertyValue="5")
		})
public class BatchExperimentListenerShortTimeout implements MessageListener{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(BatchExperimentListenerShortTimeout.class);
    @Resource
    public MessageDrivenContext mdc;
    //set timeOut for polling on this message as specified within properties file. if no result could be pulled in
    //within this time - the execution is assumed to have failed. (or only partially fulfilled)
    public long timeOutMillis;
    
    
    public BatchExperimentListenerShortTimeout(){
    	//constructor (even if empty) - required for MDB creation
    	
    	//specify the timeout for this listener
    	BackendProperties bp = new BackendProperties();
    	long timeout = Long.parseLong(bp.getProperty(BackendProperties.TIMEOUT_AUTO_APPROVED_EXPERIMENTS));
    	timeOutMillis = timeout * 1000;
    	
    }

	/* 
	 * Poll the specified system. if no data available sleep for 30 seconds and re-poll, etc.
	 * up to the timout was reached, then notify on the timeout and continue with next message in the queue.
	 * (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message m) {
		BatchExperimentListenerImpl listener = new BatchExperimentListenerImpl();
		listener.doOnMessage(m, timeOutMillis, Thread.currentThread());
	}


}
