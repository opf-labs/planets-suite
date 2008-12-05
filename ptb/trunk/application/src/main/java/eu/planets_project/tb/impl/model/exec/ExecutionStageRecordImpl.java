/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.impl.model.eval.MeasurementImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
//@Entity
@XmlRootElement(name = "ExecutionStage")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExecutionStageRecordImpl implements Serializable {
//    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
    // The name of this stage:
    private String stage;

    /** The record of the service description at this time */
//   @OneToOne(cascade={CascadeType.ALL})
    private ServiceRecordImpl serviceRecord;
    
    // The set of measured properties.
//    @OneToMany(mappedBy="executionStageRecord",cascade={CascadeType.ALL})
//    private List<MeasurementImpl> measurements;
    private Vector<MeasurementImpl> measurements;
    
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
     * @return the stage
     */
    public String getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(String stage) {
        this.stage = stage;
    }

    /**
     * @return the serviceRecord
     */
    public ServiceRecordImpl getServiceRecord() {
        return serviceRecord;
    }

    /**
     * @param serviceRecord the serviceRecord to set
     */
    public void setServiceRecord(ServiceRecordImpl serviceRecord) {
        this.serviceRecord = serviceRecord;
    }

    /**
     * @return the measurements
     */
    public List<MeasurementImpl> getMeasurements() {
        return measurements;
    }

    /**
     * @param measurements the measurements to set
     */
    public void setMeasurements(List<MeasurementImpl> measurements) {
        this.measurements = new Vector(measurements);
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
    
}
