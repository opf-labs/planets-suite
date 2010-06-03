/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;


/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentStageBean {
    
    private String name;
    
    private String description;
    

    /**
     * @param stageIdentify
     * @param string
     */
    public ExperimentStageBean(String name , String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
}
