/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Embeddable
@XmlRootElement(name = "ExecutionStage")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExecutionStageRecordImpl implements Serializable {
    /** */
    private static final Log log = LogFactory.getLog(ExecutionStageRecordImpl.class);
    /** */
    private static final long serialVersionUID = 5405314146855620431L;

//    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;
    
    // The name of this stage:
    private String stage;
    
    /** The endpoint invoked during this stage */
    private URL endpoint;

    /** The record of the service description at this time */
//   @OneToOne(cascade={CascadeType.ALL})
//    @OneToOne
    private ServiceRecordImpl serviceRecord;
    
    // The set of measured properties.
//    @OneToMany
    private Vector<MeasurementRecordImpl> measurements = new Vector<MeasurementRecordImpl>();
//    private List<MeasurementRecordImpl> measurements;
    
    /**
     * @param stagePreMigrate
     */
    public ExecutionStageRecordImpl(String stageName) {
        this.stage = stageName;
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
     * @return the endpoint
     */
    public URL getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(URL endpoint) {
        this.endpoint = endpoint;
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
        if( serviceRecord != null) {
            log.info("Setting service record: name = "+ serviceRecord.getServiceName());
        }
        this.serviceRecord = serviceRecord;
    }

    /**
     * @return the measurements
     */
    public List<MeasurementRecordImpl> getMeasurements() {
        return measurements;
    }

    /**
     * @param measurements the measurements to set
     */
    public void setMeasurements(List<MeasurementRecordImpl> measurements) {
        this.measurements = new Vector<MeasurementRecordImpl>(measurements);
    }

    
}
