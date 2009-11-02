/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.WorkflowResult;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 * This class deals with all aspects of execution for a given inputDigitalObjct record
 */
@Embeddable
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
    public static final String RESULT_PROPERTY_DIGITAL_OBJECT = "tb.result.digital_object";
    public static final String RESULT_PROPERTY_CREATEVIEW_SESSION_ID = "tb.result.createview.session_id";
    public static final String RESULT_PROPERTY_CREATEVIEW_VIEW_URL = "tb.result.createview.view_url";
    public static final String RESULT_PROPERTY_CREATEVIEW_ENDPOINT_URL = "tb.result.createview.endpoint";
    
    
    /** If the execution did not have any output other than the measurements */
    public static final String RESULT_MEASUREMENTS_ONLY = "MeasurmentsOnly";
    
    /** The Digest/fixity algorithm to use. If you change this, all files will appear to have 'changed'. */
    public static final String FIXITY_ALG = "MD5";
    
    /** wee batch engine related properties **/
    public static final String WFResult_LOG = "wfresult.log";
    public static final String WFResult_ActionIdentifier = "wfresult.actionidentifier";
    public static final String WFResult_Parameters = "wfresult.parameters";
    public static final String WFResult_ExtractedInformation = "wfresult.extractedInformation";
    public static final String WFResult_ActionStartTime = "wfresult.actionStartTime";
    public static final String WFResult_ActionEndTime = "wfresult.actionEndTime";
    
    /**
     * Computes the MD5 hash of an input stream.
     * @param in The input stream to hash.
     * @return The MD% hash, encoded as a hex string.
     */
    public static String computeFixity( InputStream in ) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance( FIXITY_ALG );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        // Go through the input stream and digest.
        byte buf[] = new byte[8192];
        int n;
        try {
            while ((n = in.read(buf)) > 0) {
                md.update(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        byte hash[] = md.digest();
        return new String( Hex.encodeHex(hash) );
 
    }

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
    
    // A fixity check for this digital object.
    private String digitalObjectFixity;
    
    // The start date of this invocation:
    private Calendar startDate;
    
    // The end date of this invocation:
    private Calendar endDate;
    
    /** The sequence of stages of this experiment. */
    private Vector<ExecutionStageRecordImpl> stages = new Vector<ExecutionStageRecordImpl>();
   
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
    public List<ExecutionStageRecordImpl> getStages() {
        //if( stages == null ) stages = new Vector<ExecutionStageRecordImpl>();
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
    
    public URI setDigitalObjectResult( DigitalObject dob, Experiment exp ) {
        DataHandler dh = new DataHandlerImpl();
        URI storeUri = dh.storeDigitalObject(dob, exp);
        this.setResult(storeUri.toString());
        this.setResultType(ExecutionRecordImpl.RESULT_DATAHANDLER_REF);
        return storeUri;
    }
    
    public void setDobRefResult( String storeKey ) {
        this.setResult(storeKey);
        this.setResultType(ExecutionRecordImpl.RESULT_DATAHANDLER_REF);
    }
    
}
