
/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * Adds some extra features to the MeasurmentImpl, of use when rendering.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MeasurementBean extends MeasurementImpl {

    /** */
    private static final long serialVersionUID = -9007264139415572106L;
    
    /** */
    protected boolean selected = true;
    
    /** Allow the 'stage' to be copied here, for easier presentation */
    private String stage;
    
    /**
     * @param measurement
     */
    public MeasurementBean(MeasurementImpl measurement) {
        super(null, measurement);
        if( measurement.getEvent() != null ) {
            this.stage = measurement.getEvent().getWorkflowStage();
        }
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the stage
     */
    public String getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(String stage) {
        this.stage = stage;
    }
    
}
