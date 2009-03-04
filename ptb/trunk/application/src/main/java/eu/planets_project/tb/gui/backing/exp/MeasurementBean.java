/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import eu.planets_project.tb.impl.model.eval.MeasurementImpl;

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
    
    /**
     * @param measurement
     */
    public MeasurementBean(MeasurementImpl measurement) {
        this.description = measurement.getDescription();
        this.identifier = measurement.getIdentifier();
        this.name = measurement.getName();
        this.stage = measurement.getStage();
        this.type = measurement.getType();
        this.unit = measurement.getUnit();
        this.value = measurement.getValue();
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

}
