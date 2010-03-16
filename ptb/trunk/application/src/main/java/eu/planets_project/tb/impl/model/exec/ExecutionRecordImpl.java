/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 * This class deals with all aspects of execution for a given inputDigitalObjct record
 */
@Entity
@XmlRootElement(name = "ExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExecutionRecordImpl implements Serializable {
    private static Log log = LogFactory.getLog(ExecutionRecordImpl.class);

    /** */
    private static final long serialVersionUID = -6230965529849585615L;

    /** If the execution lead to a DigitalObject as output, stored in a data registry. */
    public static final String RESULT_DIGITALOBJECT_REF = "DigitalObjectRef";

    /** If the execution lead to a file that is stored in the local file store. */
    public static final String RESULT_DATAHANDLER_REF = "DataHandlerRef";
    
    /** If the execution lead to a list of properties. */
    public static final String RESULT_PROPERTIES_LIST = "PropertiesList";
    
    /* Properties the TB understands */
    public static final String RESULT_PROPERTY_URI = "tb.result.uri";
    public static final String RESULT_PROPERTY_INTERIM_RESULT_URI = "tb.result.uri.interim.result";
    public static final String RESULT_PROPERTY_DIGITAL_OBJECT = "tb.result.digital_object";
    public static final String RESULT_PROPERTY_CREATEVIEW_SESSION_ID = "tb.result.createview.session_id";
    public static final String RESULT_PROPERTY_CREATEVIEW_VIEW_URL = "tb.result.createview.view_url";
    public static final String RESULT_PROPERTY_CREATEVIEW_ENDPOINT_URL = "tb.result.createview.endpoint";
    
    
    /** If the execution did not have any output other than the measurements */
    public static final String RESULT_MEASUREMENTS_ONLY = "MeasurmentsOnly";
    
    /** wee batch engine related properties **/
    public static final String WFResult_LOG = "wfresult.log";
    public static final String WFResult_ActionIdentifier = "wfresult.actionidentifier";
    public static final String WFResult_Parameters = "wfresult.parameters";
    public static final String WFResult_ExtractedInformation = "wfresult.extractedInformation";
    public static final String WFResult_ActionStartTime = "wfresult.actionStartTime";
    public static final String WFResult_ActionEndTime = "wfresult.actionEndTime";
    public static final String WFResult_ServiceEndpoint = "wfresult.serviceEndpoint";
    public static final String WFResult_ServiceReport = "wfresult.serviceReport";
    public static final String WFResult_ServiceDescription = "wfresult.serviceDescription";

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;
    
    /** The experiment this belongs to */
    @ManyToOne
    @XmlTransient
    private BatchExecutionRecordImpl batch;
    
    // The source Digital Object - original URL.
    @Column(columnDefinition="VARCHAR(10000)")
    private String digitalObjectSource;
    
    // The identity of the internally cached copy (from the DataHandler)
    @Column(columnDefinition="VARCHAR(10000)")
    private String digitalObjectReferenceCopy;
    
    // A fixity check for this digital object.
    private String digitalObjectFixity;
    
    // The start date of this invocation:
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startDate;
    
    // The end date of this invocation:
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endDate;
    
    /** The sequence of stages of this experiment. */
    @OneToMany(cascade=CascadeType.ALL, mappedBy="execution", fetch=FetchType.EAGER)
    private Set<ExecutionStageRecordImpl> stages = new HashSet<ExecutionStageRecordImpl>();
    //private Vector<ExecutionStageRecordImpl> stages = new Vector<ExecutionStageRecordImpl>();
    
    /** The service invocation records */
    /* FIXME This was a bad idea - just move the new fields into the ExecutionStageRecordImpl, 
    @OneToMany(cascade=CascadeType.ALL, mappedBy="execution", fetch=FetchType.EAGER)
    private Set<InvocationRecordImpl> serviceCalls = new HashSet<InvocationRecordImpl>();
    */
   
    //FIXME Use @Lob on the things I had to make LONGBLOB
    
    // The 'Result'
    private String resultType;
    @Column(columnDefinition="VARCHAR(10000)")
    private String result;
    
    // The 'Report Log' for this digital object
    @Lob
    private Vector<String> reportLog = new Vector<String>();

    /** For JAXB */
    @SuppressWarnings("unused")
    private ExecutionRecordImpl() {
    }
    
    public ExecutionRecordImpl( BatchExecutionRecordImpl batch ) {
        this.batch = batch; 
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
     * @return the digitalObjectFixity
     */
    public String getDigitalObjectFixity() {
        return digitalObjectFixity;
    }

    /**
     * @param digitalObjectFixity the digitalObjectFixity to set
     */
    public void setDigitalObjectFixity(String digitalObjectFixity) {
        this.digitalObjectFixity = digitalObjectFixity;
    }

    /**
     * @return the stages
     */
    public Set<ExecutionStageRecordImpl> getStages() {
        return stages;
    }

    /**
     * @return the date
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * @param date the date to set
     */
    public void setStartDate(Calendar date) {
        this.startDate = date;
    }
    
    /**
     * @return the date
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * @param date the date to set
     */
    public void setEndDate(Calendar date) {
        this.endDate = date;
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
     * how did the execution of this record proceed. have all workflow steps been
     * processed properly. it's about monitoring the workflow process for this item.
     * @return the report
     */
    public List<String> getReportLog() {
        return reportLog;
    }

    /**
     * @param report the report to set
     */
    public void setReportLog(List<String> report) {
        this.reportLog = new Vector<String>(report);
    }
    
    /**
     * @return The number of automatically extractable measurements stored under this record.
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
    
    /**
     * @return The number of manual measurements stored under this record.
     */
    public int getNumberOfManualMeasurements() {
        if( this.getStages() == null ) return 0;
        int im = 0;
        for( ExecutionStageRecordImpl exr : this.getStages()) {
            if( exr.getManualMeasurements() != null ) {
                im += exr.getManualMeasurements().size();
            }
        }
        return im;
    }

    
    /* ---- Additional methods for setting and getting particular types of result ---- */
    
    public void setPropertiesListResult( Properties props ) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        props.storeToXML(bout, "Property List Testbed Result", "UTF-8");
        this.setResult(bout.toString("UTF-8"));
        this.setResultType(RESULT_PROPERTIES_LIST);
    }
    
    public Properties getPropertiesListResult() throws IOException {
        if( ! RESULT_PROPERTIES_LIST.equalsIgnoreCase(this.getResultType())) {
            return null;
        }
        Properties props = new Properties();
        ByteArrayInputStream bin = new ByteArrayInputStream( this.getResult().getBytes("UTF-8") );
        props.loadFromXML(bin);
        return props;
    }
    
    /* -- */
    
    /**
     * since 15.03.2010
     * updated to cope with shared data registry pointers for digital objects
     * it does not copy the specified object into the TB's data space but just hooks in the 
     * information into the experiment.
     * @param dobRef
     * @param exp
     * @return
     */
    public URI setDigitalObjectResult(URI dobRef, Experiment exp){
    	this.setResult(dobRef.toString());
        this.setResultType(ExecutionRecordImpl.RESULT_DATAHANDLER_REF);
        return dobRef;
    }
    
    /**
     * stores the digital object within the TB's data store and hooks in the information
     * into the experiment. 
     * @param dob
     * @param exp
     * @return
     */
    public URI setDigitalObjectResult( DigitalObject dob, Experiment exp ) {
        DataHandler dh = new DataHandlerImpl();
        URI storeUri = dh.storeDigitalObject(dob, exp);
        return setDigitalObjectResult(storeUri, exp);
    }
    
    public void setDobRefResult( String storeKey ) {
        this.setResult(storeKey);
        this.setResultType(ExecutionRecordImpl.RESULT_DATAHANDLER_REF);
    }
 
    /**
     * @return the measurements
     */
    private Set<MeasurementImpl> getMeasurements() {
        /*
            log.info("me: " + me.getAgentType()+ " " + me.getMeasurements().size() );
            for(MeasurementImpl m : me.getMeasurements() ) {
                log.info("m: "+m.toString());
            }
            */
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
//        if( this.measurementEvents.size() == 2 ) {
        /*
            // Empty:
            for( MeasurementEventImpl mev : this.measurementEvents ) {
                mev.setExperiment(null);
            }
            testbedMan.updateExperiment(this);
            */
            // Counter intuitively, this order is required, as we are relying on the Cascase to update the fields,
            // and we need to delete the back references before we delete the forward-references.
        //this.measurementEvents.clear();
            // Now add anew:
            //MeasurementEventImpl mev = new MeasurementEventImpl();
        /*
            log.info("Making a MEV...");
                log.info("Got batch: "+batch);
                for( ExecutionRecordImpl exr : batch.getRuns() ) {
                    log.info("Got batch: "+exr);
                    if( exr != null && exr.getStages() != null ) {
                        log.info("Got Stages: "+exr.getStages());
                        for( ExecutionStageRecordImpl exsr : exr.getStages() ) {
                            InvocationRecordImpl iri = new InvocationRecordImpl( exsr.getServiceRecord() );
                            iri.setExecution(exr);
                            MeasurementEventImpl me = new MeasurementEventImpl(iri);
                            iri.addMeasurementEvent(me);

                            log.info("Got Stage: "+exsr.getStage());
                            for( MeasurementRecordImpl mr : exsr.getMeasurements() ) {
                                log.info("Got measurement: "+mr);
                                // Set the back-reference, or retrieval fails:
                                MeasurementImpl m2 = new MeasurementImpl(me);
                                log.info("Looking at "+mr.getIdentifier());
                                me.addMeasurement(m2);
                            }
                            this.serviceCalls.add(iri);
                        }
                }
            }
            */
                /*
            mev.setAgentType(AGENT_TYPE.WORKFLOW);
            mev.setStage(MEASUREMENT_STAGE.EXECUTION);
            mev.setExperiment(this);
            this.measurementEvents.add(mev);
            testbedMan.updateExperiment(this);
            */
//        }
        return null;
    }

    /**
     * @return the serviceCalls
     */
    /*
    public Set<InvocationRecordImpl> getServiceCalls() {
        return serviceCalls;
    }
    */

}
