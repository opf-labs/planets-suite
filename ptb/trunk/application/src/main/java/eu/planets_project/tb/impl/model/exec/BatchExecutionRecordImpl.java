/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.model.ExperimentExecutableImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 * Contains all execution records over all input digital objects
 */
@Entity
@XmlRootElement(name = "BatchExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class BatchExecutionRecordImpl implements Serializable {
    /** */
    private static final long serialVersionUID = -6230965529849585615L;
    private static Log log = LogFactory.getLog(BatchExecutionRecordImpl.class);

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** The experiment this belongs to */
    @ManyToOne
    private ExperimentExecutableImpl executable;
    
    // The date of this invocation:
    private Calendar startDate;
    private Calendar endDate;
    // Did the workflow execution succeed?
    private boolean batchRunSucceeded;
    
    /** The sequence of invocations of this experiment, for each digital object input */
    @OneToMany(cascade=CascadeType.ALL, mappedBy="batch", fetch=FetchType.EAGER)
    private Set<ExecutionRecordImpl> runs = new HashSet<ExecutionRecordImpl>();
    
    //a batch execution record containing log, etc. information for the entire execution
    @OneToOne(cascade=CascadeType.ALL)
    private BatchWorkflowResultLogImpl wfEngineExecutionResultLog;
    
    /** For JAXB */
    @SuppressWarnings("unused")
    private BatchExecutionRecordImpl() {
        log.info("Constructing Batch Execution Record, default constructor.");
        new Exception().printStackTrace();
    }
    
    public BatchExecutionRecordImpl( ExperimentExecutableImpl executable ) {
        log.info("Constructing Batch ExecutionRecords == "+executable);
        new Exception().printStackTrace();
        if( executable != null ) log.info("Constructing Batch ExecutionRecords ID == "+ executable.getId());
        this.executable = executable;
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
     * @return the executable
     */
    public ExperimentExecutableImpl getExecutable() {
        return executable;
    }

    /**
     * @param executable the executable to set
     */
    public void setExecutable(ExperimentExecutableImpl executable) {
        this.executable = executable;
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
    public Set<ExecutionRecordImpl> getRuns() {
        return runs;
    }

    /**
     * @param runs the runs to set
     */
    public void setRuns(List<ExecutionRecordImpl> runs) {
        this.runs = new HashSet<ExecutionRecordImpl>(runs);
    }

	/**
	 * a batch execution record containing log, etc. information for the entire execution
	 * e.g. for the WEE batch processor: WorkflowResult Object
	 * @return
	 */
	public BatchWorkflowResultLogImpl getWorkflowExecutionLog() {
		return wfEngineExecutionResultLog;
	}

	/**
	 * 	/**
	 * a batch execution record containing log, etc. information for the entire execution
	 * e.g. for the WEE batch processor: WorkflowResult Object
	 * @param workflowExecutionLog
	 */
	public void setWorkflowExecutionLog(BatchWorkflowResultLogImpl workflowExecutionLog) {
		this.wfEngineExecutionResultLog = workflowExecutionLog;
	}

 
}
