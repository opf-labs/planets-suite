/**
 * 
 */
package eu.planets_project.tb.impl.model.measure;

import java.io.Serializable;
import java.net.URI;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget.TargetType;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * This is the Testbed's notion of a property measurement.
 * 
 * These measurements may be generated in a workflow, by a service, or by a user.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Embeddable
@XmlRootElement(name = "Measurement")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementImpl implements Serializable {
    
    /** */
    private static final long serialVersionUID = 2724034034191132672L;

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    //@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @XmlTransient
    MeasurementEventImpl event;
    
    protected String identifier;
    
    protected String value;
    
    /** */
    protected String name;
    
    /** */
    protected String description;
    
    /** */
    protected String unit;

    /** */
    protected String type;
    
    /*
     * If the target was one or more digital object(s).
     */
    
    /** If this is about one or more digital objects, then the digital objects that were measured go here. 
     * As Data Registry URIs, stored as Strings. */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    protected MeasurementTarget target = new MeasurementTarget();

    /** For JAXB */
    @SuppressWarnings("unused")
    public MeasurementImpl() { }

    /**
     * @param m  
     */
    public MeasurementImpl(MeasurementEventImpl event ) {
        this.event = event;
    }
    
    /**
     * 
     * @param event
     * @param m
     */
    public MeasurementImpl(MeasurementEventImpl event, MeasurementImpl m) {
        this.event = event;
        this.identifier = m.getIdentifier();
        this.value = m.getValue();
        this.name = m.name;
        this.unit = m.unit;
        this.description = m.description;
        this.target = m.target;
    }
    
    /**
     * @param identifier
     * @param name
     * @param unit
     * @param description
     * @param stage
     * @param type
     */
    private MeasurementImpl(MeasurementEventImpl event, String identifier, String name, String unit, String description, String type, MeasurementTarget target ) {
        super();
        this.event = event;
        this.identifier = identifier;
        this.name = name;
        this.unit = unit;
        this.description = description;
        this.type = type;
        this.target = target;
    }
    
    protected MeasurementImpl(MeasurementEventImpl event, URI identifier, String name, String unit, String description, String type, MeasurementTarget target ) {
        this(event, identifier.toASCIIString(), name, unit, description, type, target );
    }

    /**
     * @param identifier
     * @param value
     */
    public MeasurementImpl(URI identifier, String value) {
        this.identifier = identifier.toASCIIString();
        this.value = value;
    }

    /**
     * @param identifier
     * @param value
     */
    public MeasurementImpl(String identifier, String value) {
        this.identifier = identifier;
        this.value = value;
    }

    /**
     * @param m
     */
    public MeasurementImpl(MeasurementImpl m) {
        this(m.event, m.identifier, m.name, m.unit, m.description, m.type, m.target );
    }

    /**
     * @param me
     * @param p
     */
    public MeasurementImpl(MeasurementEventImpl event, Property p) {
        this.event = event;
        this.identifier = p.getUri().toASCIIString();
        this.name = p.getName();
        this.unit = p.getUnit();
        this.description = p.getDescription();
        this.type = p.getType();
        this.value = p.getValue();
        this.target = null;
    }

    /**
     * @param m
     * @return
     */
    public MeasurementImpl clone() {
        return new MeasurementImpl(this.event, this.identifier, this.name, this.unit, this.description, this.type, this.target );
    }

    /**
     * 
     */
    public static MeasurementImpl create( URI identifier, String name, String unit, String description, 
            String type, MeasurementTarget target ) {
        return new MeasurementImpl( null, identifier, name, unit, description, type, target );
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
     * @return the unitt
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
        return "[id:"+this.id+", identifier:"+this.identifier+", name:"+this.name+", unit:"+this.unit+", desc:"+this.description+", type:"+this.type+", value:"+this.value+"]";
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
     * @param measurementEventImpl
     */
    public void setEvent(MeasurementEventImpl event) {
       this.event = event;
    }

    /**
     * @return
     */
    public MeasurementEventImpl getEvent() {
        return this.event;
    }

    /**
     * @return the targetType
     */
    public MeasurementTarget getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(MeasurementTarget target) {
        this.target = target;
    }

    /**
     * @return
     */
    public Property toProperty() {
        Property.Builder pb = new Property.Builder(this.getIdentifierUri());
        pb.name(name).description(description).type(type).value(value).unit(unit);
        return pb.build();
    }

}
