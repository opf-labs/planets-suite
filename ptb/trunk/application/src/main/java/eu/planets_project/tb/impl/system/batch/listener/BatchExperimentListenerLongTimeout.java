package eu.planets_project.tb.impl.system.batch.listener;

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.JMSException;
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
@MessageDriven( name = "BatchExperimentListener_longTimeoute", 
		activationConfig =  {
			@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
			@ActivationConfigProperty(propertyName="destination", propertyValue="queue/tbBatchExecQueue_longTimeout"),
			@ActivationConfigProperty(propertyName="maxSession", propertyValue="2")
		})
public class BatchExperimentListenerLongTimeout implements MessageListener{
	private static final Log log = LogFactory.getLog(BatchExperimentListenerShortTimeout.class);
    @Resource
    public MessageDrivenContext mdc;
    //set timeOut for polling on this message as specified within properties file. if no result could be pulled in
    //within this time - the execution is assumed to have failed. (or only partially fulfilled)
    public final int timeOutMillis;
    //the period between re-querying the workflow system on this ticket
    public final int sleep = 30000;
    
    
    public BatchExperimentListenerLongTimeout(){
    	//constructor (even if empty) - required for MDB creation
    	
    	//specify the timeout for this listener
    	BackendProperties bp = new BackendProperties();
    	int timeout = Integer.parseInt(bp.getProperty(bp.TIMEOUT_AUTO_APPROVED_EXPERIMENTS));
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
