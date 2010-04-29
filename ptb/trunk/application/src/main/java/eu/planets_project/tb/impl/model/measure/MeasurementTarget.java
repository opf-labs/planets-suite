package eu.planets_project.tb.impl.model.measure;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * This encapsulates a reference to the entity that the measurement pertains to.
 * 
 * The target of the measurement can be:
 *  - A service.
 *  - A digital object.
 *  - A comparison of two digital objects.
 *  - A workflow.
|* 
 * In the case of a comparison of two digital objects, the IDs of the digital objects
 * are held in the digitalObjects field.
 * 
 * If the comparative measurement was reached based on one or more properties of each
 * DigObj, then these properties are also indicated here, via getDigitalObjectProperties(i).
 * 
 * This is closely related to the PropertyComparison object that the Compare interface returns, 
 * which is mapped into a MeasurementImpl object with an appropriate MeasurementTarget.
 * 
 */
@Embeddable
@XmlRootElement(name = "MeasurementTarget")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementTarget  implements Serializable { 

    /** */
    private static final long serialVersionUID = -2968639008752447069L;
    
    /** Only used to define property measurment types. */
    public static MeasurementTarget SERVICE_TARGET = new MeasurementTarget(TargetType.SERVICE);
    public static MeasurementTarget SERVICE_DOB = new MeasurementTarget(TargetType.DIGITAL_OBJECT);
    public static MeasurementTarget SERVICE_DOB_PAIR = new MeasurementTarget(TargetType.DIGITAL_OBJECT_PAIR);
    public static MeasurementTarget SERVICE_WORKFLOW = new MeasurementTarget(TargetType.WORKFLOW);

    /** Strings to map to. */
    public static final String TARGET_SERVICE = "Service";
    public static final String TARGET_VIEW = "View";
    public static final String TARGET_DIGITALOBJECT = "Digital Object";
    public static final String TARGET_DIGITALOBJECT_DIFF = "Comparison of Two Digital Objects";
    public static final String TARGET_DIGITALOBJECT_PROP = "Digital Object Properties";
    public static final String TARGET_WORKFLOW = "Workflow";

    /** */
    public enum TargetType {
        /** Measurement about a service. */
        SERVICE,
        
        /** Measurement of a property of a digital object. */
        DIGITAL_OBJECT,
        
        /** Measurement of the comparison of two digital objects. */
        DIGITAL_OBJECT_PAIR,
        
        /** */
        WORKFLOW;

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            if( this.equals( SERVICE )) return TARGET_SERVICE;
            if( this.equals( DIGITAL_OBJECT )) return TARGET_DIGITALOBJECT;
            if( this.equals( DIGITAL_OBJECT_PAIR )) return TARGET_DIGITALOBJECT_DIFF;
            if( this.equals( WORKFLOW )) return TARGET_WORKFLOW;
            return super.toString();
        }
    }
    
    /** If this is about one or more digital objects, then the digital objects that were measured go here. 
     * As Data Registry URIs, stored as Strings. */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    protected Vector<String> digitalObjects = new Vector<String>();

    /** If this is about comparing particular properties of one or more digital objects, then
     * the properties that were compared should be stored here. */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    private Vector<Vector<Property>> digitalObjectProperties = new Vector<Vector<Property>>();

    /** */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    protected TargetType type;
    
    /**
     */
    public MeasurementTarget() { }
    
    /**
     */
    public MeasurementTarget(TargetType type) { 
        this.setType(type); 
    }
    
    /**
     * @param targetType the type to set
     */
    public void setType(TargetType type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public TargetType getType() {
        return type;
    }

    /**
     * @return the digitalObjects
     */
    public Vector<String> getDigitalObjects() {
        return digitalObjects;
    }

    /**
     * @param digitalObjects the digitalObjects to set
     */
    public void setDigitalObjects(Vector<String> digitalObjects) {
        this.digitalObjects = digitalObjects;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if( this.type == TargetType.DIGITAL_OBJECT ) {
            return lookupFileName(digitalObjects.firstElement());
        } else if( this.type == TargetType.DIGITAL_OBJECT_PAIR ) {
            return lookupFileName(digitalObjects.firstElement())
            +" "+this.lookupFileName(digitalObjects.get(1));
        } else if( this.type == TargetType.SERVICE ) {
            return "Service";
        } else {
            return "MeasurementTarget [digitalObjects=" + digitalObjects
                + ", type=" + type + "]";
        }
    }
    
    private String lookupFileName( String dobUri ) {
        DataHandler dh = DataHandlerImpl.findDataHandler();
        DigitalObjectRefBean dorb;
        try {
            dorb = dh.get(dobUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Missing file!";
        }
        return dorb.getName();
        
    }

    /**
     * @param i
     * @param secondProperties
     */
    public void setDigitalObjectProperties(int i,
            List<Property> props) {
        if( this.digitalObjectProperties == null ) 
            this.digitalObjectProperties = new Vector<Vector<Property>>();
        this.digitalObjectProperties.add(i, new Vector<Property>(props) );
    }
    
    /**
     * @param i
     * @return
     */
    public Vector<Property> getDigitalObjectProperties( int i ) {
        if( this.digitalObjectProperties == null ) return null;
        return this.digitalObjectProperties.get(i);
    }

    /**
     * @param i
     * @param property
     */
    public void setDigitalObjectProperty(int i, Property property) {
        Vector<Property> props = new Vector<Property>();
        props.add(property);
        this.setDigitalObjectProperties(i, props);
    }

}