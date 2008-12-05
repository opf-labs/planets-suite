/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
//@Entity
@XmlRootElement(name = "ExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExecutionRecordImpl implements Serializable {
//    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** The experiment this belongs to */
//    @ManyToOne
    private ExperimentExecutableImpl experimentExecutable;
    
    // The source Digital Object - original URL.
    private String digitalObjectSource;
    
    // The identity of the internally cached copy (from the DataHandler)
    private String digitalObjectReferenceCopy;
    
    // The date of this invocation:
    private Calendar date;
    
    /** The sequence of stages of this experiment. */
    private Vector<ExecutionStageRecordImpl> stages;
    
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
     * @return the experimentExecutable
     */
    public ExperimentExecutableImpl getExperimentExecutable() {
        return experimentExecutable;
    }

    /**
     * @param experimentExecutable the experimentExecutable to set
     */
    public void setExperimentExecutable(ExperimentExecutableImpl experimentExecutable) {
        this.experimentExecutable = experimentExecutable;
    }

    /**
     * @return the digitalObjectSource
     */
    public String getDigitalObjectSource() {
        return digitalObjectSource;
    }

    /**
     * @param digitalObjectSource the digitalObjectSource to set
     */
    public void setDigitalObjectSource(String digitalObjectSource) {
        this.digitalObjectSource = digitalObjectSource;
    }

    /**
     * @return the digitalObjectReferenceCopy
     */
    public String getDigitalObjectReferenceCopy() {
        return digitalObjectReferenceCopy;
    }

    /**
     * @param digitalObjectReferenceCopy the digitalObjectReferenceCopy to set
     */
    public void setDigitalObjectReferenceCopy(String digitalObjectReferenceCopy) {
        this.digitalObjectReferenceCopy = digitalObjectReferenceCopy;
    }

    /**
     * @return the stages
     */
    public Vector<ExecutionStageRecordImpl> getStages() {
        return stages;
    }

    /**
     * @param stages the stages to set
     */
    public void setStages(Vector<ExecutionStageRecordImpl> stages) {
        this.stages = stages;
    }

    /**
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }
    
}
