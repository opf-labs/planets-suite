/**
 * 
 */
package eu.planets_project.tb.impl.model.eval;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;

/**
 * This is the Testbed's notion of a property measurement.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
//@Entity
@XmlRootElement(name = "Measurement")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementImpl implements Serializable {
//    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
//    @ManyToOne
//    protected ExecutionStageRecordImpl executionStageRecord;

    protected URI identifier;
    
    protected String name;

    protected String description;
    
    protected String unit;
    
    protected String value;
    
    protected String stage;
    
    protected String type;
    public static final String TYPE_SERVICE = "Service";
    public static final String TYPE_DIGITALOBJECT = "Digital Object";

//    this should be an identifer uri and a value, and possibly a stage.  That is all!
    
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
    public MeasurementImpl(URI identifier, String name, String unit, String description, 
            String stage, String type) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.unit = unit;
        this.stage = stage;
        this.type = type;
        this.description = description;
    }

    /**
     * Copy constructor
     * @param m
     */
    public MeasurementImpl( MeasurementImpl m ) {
        this(m.identifier, m.name, m.unit, m.description, m.stage, m.type);
    }
    


    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[id:"+this.identifier+", name:"+this.name+", unit:"+this.unit+", desc:"+this.description+", stage:"+this.stage+", type:"+this.type+", value:"+this.value+"]";
    }
    
    /**
     * 
     * @return
     */
    public boolean isUnitDefined() {
        if( this.unit == null ) return false;
        if( "".equals(this.unit)) return false;
        return true;
    }
    
}
