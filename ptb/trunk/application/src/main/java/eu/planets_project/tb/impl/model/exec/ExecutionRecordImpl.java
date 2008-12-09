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
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Embeddable
@XmlRootElement(name = "ExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExecutionRecordImpl implements Serializable {
    /** */
    private static final long serialVersionUID = -6230965529849585615L;

    //    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** The experiment this belongs to */
//    @ManyToOne
//    private ExperimentExecutableImpl experimentExecutable;
    
    // The source Digital Object - original URL.
    private String digitalObjectSource;
    
    // The identity of the internally cached copy (from the DataHandler)
    private String digitalObjectReferenceCopy;
    
    // The date of this invocation:
    private Calendar date;
    
    /** The sequence of stages of this experiment. */
//    @OneToMany
    private Vector<ExecutionStageRecordImpl> stages = new Vector<ExecutionStageRecordImpl>();
//    private List<ExecutionStageRecordImpl> stages;
   
    // The 'Result'
    private String resultType;
    private String result;
    
    // The 'Report'
    private String report;

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
//    public ExperimentExecutableImpl getExperimentExecutable() {
//        return experimentExecutable;
//    }

    /**
     * @param experimentExecutable the experimentExecutable to set
     */
//    public void setExperimentExecutable(ExperimentExecutableImpl experimentExecutable) {
//        this.experimentExecutable = experimentExecutable;
//    }

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
    public List<ExecutionStageRecordImpl> getStages() {
        return stages;
    }

    /**
     * @param stages the stages to set
     */
    public void setStages(List<ExecutionStageRecordImpl> stages) {
        this.stages = new Vector<ExecutionStageRecordImpl>(stages);
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
    
    /**
     * @return the resultType
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * @param resultType the resultType to set
     */
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the report
     */
    public String getReport() {
        return report;
    }

    /**
     * @param report the report to set
     */
    public void setReport(String report) {
        this.report = report;
    }
    
    /**
     * @return The number of measurements stored under this record.
     */
    public int getNumberOfMeasurements() {
        if( this.getStages() == null ) return 0;
        int im = 0;
        for( ExecutionStageRecordImpl exr : this.getStages()) {
            if( exr.getMeasurements() != null ) {
                im += exr.getMeasurements().size();
            }
        }
        return im;
    }

}
