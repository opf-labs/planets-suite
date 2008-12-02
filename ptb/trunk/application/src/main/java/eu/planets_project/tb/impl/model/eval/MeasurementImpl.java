/**
 * 
 */
package eu.planets_project.tb.impl.model.eval;

import java.net.URI;

/**
 * This is the Testbed's notion of a property, one that can be stored in the DB.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MeasurementImpl {

    protected URI identifier;
    
    protected String name;

    protected String unit;
    
    protected String description;
    
    protected String stage;
    
    protected String type;
    public static final String TYPE_SERVICE = "Service";
    public static final String TYPE_DIGITALOBJECT = "Digital Object";
    
    protected boolean selected = true;
    
    protected String value;
    
    /** */
    public MeasurementImpl() { }
    
    /**
     * @param identifier
     * @param name
     * @param unit
     * @param description
     * @param stage
     * @param type
     */
    public MeasurementImpl(URI identifier, String name, String unit,
            String description, String stage, String type) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.unit = unit;
        this.description = description;
        this.stage = stage;
        this.type = type;
    }



    /**
     * @return the identifier
     */
    public URI getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(URI identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    
    
}
