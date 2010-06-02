/**
 * 
 */
package eu.planets_project.ifr.core.wee.api;

import java.io.IOException;
import java.io.Serializable;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;

import java.util.UUID;
import java.util.logging.Logger;


/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 10.11.2008
 * 
 */
public enum WorkflowExecutionStatus {
    
	//status of a submitted workflow can either be: 
	SUBMITTED, RUNNING, COMPLETED, FAILED;

}
