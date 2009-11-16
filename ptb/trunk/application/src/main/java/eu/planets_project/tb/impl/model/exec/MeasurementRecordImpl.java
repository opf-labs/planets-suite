/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This is the Testbed's notion of a property, one that can be stored in the DB.
 * 
 * Modelled on the XCDL notion of a property.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Embeddable
@XmlRootElement(name = "Measurement")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementRecordImpl implements Serializable {
    /** */
    private static final long serialVersionUID = -4526459280209975324L;

    //    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
//    @ManyToOne
//    protected ExecutionStageRecordImpl executionStageRecord;

    protected String identifier;
    
    protected String value;
    
    /** */
    public MeasurementRecordImpl() { }
    
    /**
     * @param identifier
     */
    public MeasurementRecordImpl(String identifier, String value ) {
        super();
        this.identifier = identifier;
        this.value = value;
    }
    
    /**
     * 
     * @param identifier
     * @param value
     */
    public MeasurementRecordImpl( URI identifier, String value ) {
        super();
        this.identifier = identifier.toString();
        this.value = value;
    }

    /**
     * Copy constructor
     * @param m
     */
    public MeasurementRecordImpl( MeasurementRecordImpl m ) {
        this(m.identifier, m.value);
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
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

}
