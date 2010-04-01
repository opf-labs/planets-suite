package eu.planets_project.tb.impl.model.measure;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * 
 */
@Embeddable
@XmlRootElement(name = "MeasurementTarget")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementTarget  implements Serializable { 

    /** */
    private static final long serialVersionUID = -2968639008752447069L;
    
    /** */
    public static MeasurementTarget SERVICE_TARGET = new MeasurementTarget(TargetType.SERVICE);
    public static MeasurementTarget SERVICE_DOB = new MeasurementTarget(TargetType.DIGITAL_OBJECT);
    public static MeasurementTarget SERVICE_DOB_PAIR = new MeasurementTarget(TargetType.DIGITAL_OBJECT_PAIR);
    public static MeasurementTarget SERVICE_WORKFLOW = new MeasurementTarget(TargetType.WORKFLOW);

    /** */
    public static final String TARGET_SERVICE = "Service";
    public static final String TARGET_DIGITALOBJECT = "Digital Object";
    public static final String TARGET_DIGITALOBJECT_DIFF = "Comparison of Two Digital Objects";
    public static final String TARGET_WORKFLOW = "Workflow";

    /** */
    public enum TargetType {
        /** */
        SERVICE,
        
        /** */
        DIGITAL_OBJECT,
        
        /** */
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
    
    /** */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    protected Vector<String> digitalObjects = new Vector<String>();
    
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

}