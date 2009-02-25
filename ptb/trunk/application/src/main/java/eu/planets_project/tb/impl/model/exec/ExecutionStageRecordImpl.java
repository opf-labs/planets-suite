/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.persistence.Embeddable;
//import javax.persistence.OneToMany;
//import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;

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
     * Gets the Measurements and patches in the property data, if available.
     * @return Looks up the measurments, patching in the definitions of the properties.
     */
    public List<MeasurementImpl> getMeasuredObservables() {
        
        // Get the actual measurements:
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        List<MeasurementRecordImpl> mrl = this.getMeasurements();
        
        // Look up the observables and their definitions:
        ExpTypeBackingBean exptype = ExpTypeBackingBean.getExpTypeBean(expBean.getEtype());
        HashMap<String, List<MeasurementImpl>> observables = exptype.getObservables();
        
        // Patch the descriptions in with the results:
        List<MeasurementImpl> mobs = new ArrayList<MeasurementImpl>();
        for( MeasurementRecordImpl mr : mrl ) {
            MeasurementImpl new_m = null;
            
            // Look for matches:
            if( observables.keySet().contains( this.getStage() )) {
                for( MeasurementImpl m : observables.get( this.getStage() ) ) {
                    if( m.getIdentifier() != null && mr.getIdentifier() != null &&
                            m.getIdentifier().toString().equals( mr.getIdentifier() ) ) {
                        new_m = new MeasurementImpl(m);
                        new_m.setValue( mr.getValue() );
                    }
                }
            }
            
            // If that doesn't work, generate manually:
            if( new_m == null && mr.getIdentifier() != null ) {
                try {
                    new_m = new MeasurementImpl();
                    new_m.setIdentifier( new URI( mr.getIdentifier() ) );
                    new_m.setValue( mr.getValue() );
                    // Add to the list.
                    mobs.add(new_m);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    new_m = null;
                }
            }
            
            // Add to the results:
            if( new_m != null ) {
                //log.info("Adding observable: "+new_m.toString());
                mobs.add(new_m);
            }
        }

        return mobs;
    }

    /**
     * @param measurements the measurements to set
     */
    public void setMeasurements(List<MeasurementRecordImpl> measurements) {
        this.measurements = new Vector<MeasurementRecordImpl>(measurements);
    }

    
}
