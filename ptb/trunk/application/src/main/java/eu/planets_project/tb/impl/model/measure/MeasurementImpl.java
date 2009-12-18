/**
 * 
 */
package eu.planets_project.tb.impl.model.measure;

import java.net.URI;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.impl.model.exec.InvocationRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;

/**
 * This is the Testbed's notion of a property measurement.
 * 
 * These measurments may be generated in a workflow, by a service, or by a user.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Entity
@XmlRootElement(name = "Measurement")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementImpl extends MeasurementRecordImpl {
    
    /** */
    private static final long serialVersionUID = 2724034034191132672L;
    
    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    @ManyToOne
    protected InvocationRecordImpl invocation;

    protected String identifier;
    
    protected String value;
    
    /* --------------- Measurement performed AT Stage ------------------ */
    // The name of this stage:
    protected String stage;
    
    // TODO AT WorkflowInput, WorkflowProcess, WorkflowOutput
    //  IN WorkflowStageIndex, WorkflowStageName, WorkflowAction

    /* ----------------- Data that is looked-up on demand: -------------- */

    @Transient
    protected String name;
    
    @Transient
    protected String description;
    
    @Transient
    protected String unit;
    
    @Transient
    protected String type;
    public static final String TYPE_SERVICE = "Service";
    public static final String TYPE_WORKFLOW = "Workflow";
    public static final String TYPE_DIGITALOBJECT = "Digital Object";
    public static final String TYPE_TWO_WAY_COMPARE = "Comparison of Two Digital Objects";
    
    /** For JAXB */
    @SuppressWarnings("unused")
    private MeasurementImpl() { }

    /**
     * @param m  
     */
    public MeasurementImpl(InvocationRecordImpl invocation ) {
        this.invocation = invocation;
    }
    
    /**
     * @param m  
     */
    public MeasurementImpl(InvocationRecordImpl invocation, MeasurementRecordImpl m) {
        this.invocation = invocation;
        this.identifier = m.getIdentifier();
        this.value = m.getValue();
    }
    
    /**
     * 
     * @param invocation
     * @param m
     */
    public MeasurementImpl(InvocationRecordImpl invocation, MeasurementImpl m) {
        this.invocation = invocation;
        this.identifier = m.getIdentifier();
        this.value = m.getValue();
        this.name = m.name;
        this.unit = m.unit;
        this.stage = m.stage;
        this.type = m.type;
        this.description = m.description;
    }
    
    /**
     * @param identifier
     * @param name
     * @param unit
     * @param description
     * @param stage
     * @param type
     */
    private MeasurementImpl(InvocationRecordImpl invocation, String identifier, String name, String unit, String description, 
            String stage, String type) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.unit = unit;
        this.stage = stage;
        this.type = type;
        this.description = description;
    }
    
    protected MeasurementImpl(InvocationRecordImpl invocation, URI identifier, String name, String unit, String description, 
            String stage, String type) {
        this(invocation, identifier.toASCIIString(), name, unit, description, stage, type );
    }

    /**
     * @param m
     * @return
     */
    public MeasurementImpl clone() {
        return new MeasurementImpl(this.invocation, this.identifier, this.name, this.unit, this.description, this.stage, this.type);
    }

    /**
     * 
     */
    public static MeasurementImpl create( URI identifier, String name, String unit, String description, 
            String stage, String type ) {
        return new MeasurementImpl( null, identifier, name, unit, description, 
            stage, type);
    }


    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
    
    public URI getIdentifierUri() {
        return URI.create( identifier );
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(URI identifier) {
        this.identifier = identifier.toASCIIString();
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

    /**
     * @param invocation the invocation to set
     */
    public void setInvocation(InvocationRecordImpl invocation) {
        this.invocation = invocation;
    }

    /**
     * @return the invocation
     */
    public InvocationRecordImpl getInvocation() {
        return invocation;
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

}
