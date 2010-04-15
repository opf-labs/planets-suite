/**
 * 
 */
package eu.planets_project.tb.impl.model.measure;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
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

import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.compare.PropertyComparison.Equivalence;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
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
    
    /** The property that has been measured. */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    private Property property = null;
    
    /** 
     * The type of the measurement, used to control how measurements are understood and displayed.
     * The details about the target of the measurment are stored in the MeasurementTarget, below.
     */
    public enum MeasurementType { 
        /** This measurement is a property related to a service. */
        SERVICE,
        /** This measurement is a property determined from a single Digital Object. */
        DOB,
        /** This measurement is a property determed by comparing two Digital Objects. */
        DOB_COMPARE
    }
    
    /** The type of the measurement. */
    private MeasurementType measurementType;
    
    /** This is the entity or entities upon which this Property was measured. */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    private MeasurementTarget target = new MeasurementTarget();

    /* ---- Agent comparison evaluation ---- */
    
    /** For comparative measurements, this records the Agent's opinion of the equivalence of the DOBs w.r.t this property. */
    private Equivalence equivalence = Equivalence.UNKNOWN;

    /* ---- User comparison evaluation ---- */
    
    /** This is the list of pre-supplied answers for the userEquivalenceDetail. String mappings
     * to 'Quite Similar' etc are held in UIResources.properties
     * TODO Consider simplifying or augmenting? For example There are two kinds of EQUAL:
     * there is precisely equal (same information in the same encoding), and there is equivalent
     * (same information in different encodings). All others are different/lossy.
     * When lossy, does Completely Different really mean Completely Lost/Missing?
     * There is also 'Supposed to be different', which applies e.g. to format evaluation. 
     */
    public enum EquivalenceStatement {
        /* The users judges the property is equal across this comparison */
        EQUAL,
        /* The users judges the property is similar across this comparison */
        SIMILAR,
        /* The users judges the property is different across this comparison */
        DIFFERENT,
        /* The users judges the property is completely different across this comparison */
        NOT_EQUAL,
        /* The users find the property is missing on one side or the other. FIXME Same as NOT_EQUAL, i.e. complete loss. */
        MISSING,
        /* The users judges the property cannot be evaluated. FIXME Is this meaningful? */
        /*
        INCOMPARABLE,
        */
        /* The users judges that no such judgement should be made. */
        NOT_APPLICABLE,
    }
    
    /** For comparative measurements, records the Experimenter's opinion of the equivalence of the DOBs w.r.t this property. */
    private EquivalenceStatement userEquivalence = null;
    
    /** For comparative measurements, the Experimenter can record a more detailed statement about the equivalence. */
    private String userEquivalenceComment = "";

    /* ---- Constructors ---- */
    
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
        this.property = new Property.Builder( m.getProperty() ).build();
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
    public MeasurementImpl(MeasurementEventImpl event, URI identifier, 
            String name, String unit, String description, String type, MeasurementTarget target ) {
        super();
        this.event = event;
        this.property = new Property.Builder(identifier).name(name).unit(unit).description(description).type(type).build();
        this.target = target;
    }

    public MeasurementImpl(MeasurementEventImpl event, String identifier, String name, String unit, String description, String type, MeasurementTarget target ) {
        this(event, URI.create(identifier), name, unit, description, type, target );
    }
    
    /**
     * @param identifier
     * @param value
     */
    public MeasurementImpl(URI identifier, String value) {
        this.property = new Property.Builder(identifier).value(value).build();
    }

    /**
     * @param m
     */
    public MeasurementImpl(MeasurementImpl m) {
        this(m.event, m.property, m.target );
    }

    /**
     * @param me
     * @param p
     */
    public MeasurementImpl(MeasurementEventImpl event, Property p) {
        this.event = event;
        this.property = new Property.Builder(p).build();
        this.target = null;
    }

    /**
     * @param event
     * @param p
     * @param target
     */
    public MeasurementImpl(MeasurementEventImpl event, Property p, MeasurementTarget target ) {
        this.event = event;
        this.property = new Property.Builder(p).build();
        this.target = target;
    }

    /**
     * @param m
     * @return
     */
    public MeasurementImpl clone() {
        return new MeasurementImpl( this.event, this.property );
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
        if( property == null ) return null;
        return property.getUri().toASCIIString();
    }
    
    public URI getIdentifierUri() {
        if( property == null ) return null;
        return property.getUri();
    }

    /**
     * @return the name
     */
    public String getName() {
        if( property == null ) return null;
        return property.getName();
    }

    /**
     * @return the type
     */
    public String getType() {
        if( property == null ) return null;
        return property.getType();
    }

    /**
     * @return the value
     */
    public String getValue() {
        if( property == null ) return null;
        return property.getValue();
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.property = new Property.Builder(this.property).value(value).build();
    }
    
    /**
     * @return the unitt
     */
    public String getUnit() {
        if( property == null ) return null;
        return property.getUnit();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        if( property == null ) return null;
        return property.getDescription();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[id:"+this.id+", property:"+this.property+"]";
    }
    
    /**
     * 
     * @return
     */
    public boolean isUnitDefined() {
        if( property.getUnit() == null ) return false;
        if( "".equals(property.getUnit())) return false;
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
        if( this.property == null ) return new Property.Builder(URI.create("planets:uri/null")).build();
        Property.Builder pb = new Property.Builder(this.property);
        if( property.getName() == null || "".equals(property.getName()) ) pb.name("[unnamed]");
        return pb.build();
    }

    /**
     * FIXME Move somewhere sensible/shared, e.g. tech-reg.
     * @return true if this is a 'format' property.
     */
    public static boolean isFormatProperty( Property p ) {
        if( TecRegMockup.PROP_DO_FORMAT.equals( p.getUri()) ) return true;
        return false;
    }

    /**
     * FIXME Move somewhere sensible/shared, e.g. tech-reg.
     * @return the Format as a FormatBean, if this is a 'format' property.
     */
    public static FormatBean getFormat( Property p ) {
        if( ! isFormatProperty(p) ) return null;
        URI fmt;
        try {
            fmt = new URI( p.getValue() );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        return new FormatBean( ServiceBrowser.fr.getFormatForUri( fmt ) );
    }

    /**
     * @return the property
     */
    public Property getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(Property property) {
        this.property = new Property.Builder(property).build();
    }

    /**
     * @return the measurementType
     */
    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    /**
     * @param measurementType the measurementType to set
     */
    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    /**
     * @return the equivalence
     */
    public Equivalence getEquivalence() {
        return equivalence;
    }

    /**
     * @param equivalence the equivalence to set
     */
    public void setEquivalence(Equivalence equivalence) {
        this.equivalence = equivalence;
    }

    /**
     * @return the userEquivalence
     */
    public EquivalenceStatement getUserEquivalence() {
        if( userEquivalence == null ) {
            if( this.getEquivalence() == Equivalence.EQUAL ) {
                return EquivalenceStatement.EQUAL;
            } else if( this.getEquivalence() == Equivalence.DIFFERENT ) {
                return EquivalenceStatement.DIFFERENT;
            } else if( this.getEquivalence() == Equivalence.MISSING ) {
                return EquivalenceStatement.MISSING;
            }
        }
        return userEquivalence;
    }

    /**
     * @param userEquivalence the userEquivalence to set
     */
    public void setUserEquivalence(EquivalenceStatement userEquivalence) {
        this.userEquivalence = userEquivalence;
    }

    /**
     * @return the userEquivalenceComment
     */
    public String getUserEquivalenceComment() {
        return userEquivalenceComment;
    }

    /**
     * @param userEquivalenceComment the userEquivalenceComment to set
     */
    public void setUserEquivalenceComment(String userEquivalenceComment) {
        this.userEquivalenceComment = userEquivalenceComment;
    }
    
}
