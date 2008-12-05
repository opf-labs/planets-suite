/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.impl.model.ExperimentExecutionImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Entity
@XmlRootElement(name = "ExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExecutionRecordImpl {
    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** The experiment this belongs to */
    @ManyToOne
    private ExperimentExecutionImpl experimentExecution;
    
    // The source Digital Object - original URL.
    private String digitalObjectSource;
    
    // The identity of the internally cached copy (from the DataHandler)
    private String digitalObjectReferenceCopy;
    
    /** The sequence of stages of this experiment. */
    @OneToMany(mappedBy="executionRecord")
    private Collection<ExecutionStageRecordImpl> stages;
    
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
     * @return the experimentExecution
     */
    public ExperimentExecutionImpl getExperimentExecution() {
        return experimentExecution;
    }

    /**
     * @param experimentExecution the experimentExecution to set
     */
    public void setExperimentExecution(ExperimentExecutionImpl experimentExecution) {
        this.experimentExecution = experimentExecution;
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
    public Collection<ExecutionStageRecordImpl> getStages() {
        return stages;
    }

    /**
     * @param stages the stages to set
     */
    public void setStages(Collection<ExecutionStageRecordImpl> stages) {
        this.stages = stages;
    }
    
}
