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
import java.util.logging.Logger;


/**
 * This is the WEE background thread.  It maintains a list of 
 * jobs to be executed, and executes them.
 * 
 * The list of jobs is a nice thread-safe FIFO ConcurrentLinkedQueue.
 * http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/ConcurrentLinkedQueue.html
 * 
 * Jobs can be described by a Spring bean config xml file, with some extra 
 * info to specify which bean does what.  
    XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("myFile.xml", getClass()));
    MyBusinessObject mbo = (MyBusinessObject) bf.getBean("exampleBusinessObject");
 * 
 *   n.b. In the future, this could be some other job description, like a script.
 * 
 * How to handle inputs and outputs?  Stick them in the DR, and leave pointers in the 
 * result object?  Where are they stored?
 * 
 * Execution times can be measured using:
 * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/System.html#nanoTime()
 * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/System.html#currentTimeMillis()
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 * This message driven bean is the job processor.
 * 
 * The maxPoolSize ensure that this is a singleton, and thus only one job is processed at a time.
 */
@MessageDriven(mappedName = "jms/wfExecQueue", activationConfig =  {
        @ActivationConfigProperty(propertyName="maxPoolSize", propertyValue="1")
})
public class WorkflowExecutionEngine implements MessageListener {
    static final Logger logger = Logger.getLogger("WorkflowExecutionEngine");
    @Resource
    public MessageDrivenContext mdc;
    
    // Iteration counter
    long iters = 0;

    /* (non-Javadoc)
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message m) {
        // TODO Auto-generated method stub
        logger.info("Called with Message: "+m);
        iters++;
        logger.info("That was message #"+iters);
    }

}
