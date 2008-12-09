/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.persistence.Embeddable;
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
@Embeddable
@XmlRootElement(name = "BatchExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class BatchExecutionRecordImpl implements Serializable {
    /** */
    private static final long serialVersionUID = -6230965529849585615L;

    //    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** The experiment this belongs to */
//    @ManyToOne
//    private ExperimentExecutableImpl experimentExecutable;
    
    // The date of this invocation:
    private Calendar startDate;   
    private Calendar endDate;
    // Did the workflow execution succeed?
    private boolean batchRunSucceeded;
    
    /** The sequence of stages of this experiment. */
//    @OneToMany
    private Vector<ExecutionRecordImpl> runs = new Vector<ExecutionRecordImpl>();
    //private List<ExecutionRecordImpl> runs;
    
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
     * @return the startDate
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the batchRunSucceeded
     */
    public boolean isBatchRunSucceeded() {
        return batchRunSucceeded;
    }

    /**
     * @param batchRunSucceeded the batchRunSucceeded to set
     */
    public void setBatchRunSucceeded(boolean batchRunSucceeded) {
        this.batchRunSucceeded = batchRunSucceeded;
    }

    /**
     * @return the runs
     */
    public List<ExecutionRecordImpl> getRuns() {
        return runs;
    }

    /**
     * @param runs the runs to set
     */
    public void setRuns(List<ExecutionRecordImpl> runs) {
        this.runs = new Vector<ExecutionRecordImpl>(runs);
    }

 
}
