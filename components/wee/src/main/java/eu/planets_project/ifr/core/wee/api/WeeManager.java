/**
 * 
 */
package eu.planets_project.ifr.core.wee.api;


/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface WeeManager {

    /**
     * Look-up the Workflow Execution Engine.
     * 
     * @return The current WEE instance.
     */
    public WorkflowExecutionEngine getWee();
    
}
