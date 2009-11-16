/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.services.wrappers.IdentifyWrapper;

/**
 * This is the class that carries the code specific to invoking an Identify experiment.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class IdentifyWorkflow implements ExperimentWorkflow {
    private static Log log = LogFactory.getLog(IdentifyWorkflow.class);

    /** External property keys */
    public static final String PARAM_SERVICE = "identify.service";

    /** Internal keys for easy referral to the service+stage combinations. */
    public static final String STAGE_IDENTIFY = "Identify";
    
    private static final String IDENTIFY_SUCCESS = STAGE_IDENTIFY+".service.success";
    private static final String IDENTIFY_SERVICE_TIME = STAGE_IDENTIFY+".service.time";
    private static final String IDENTIFY_METHOD = STAGE_IDENTIFY+".method";
    private static final String IDENTIFY_FORMAT = STAGE_IDENTIFY+".do.format";
    private static final String IDENTIFY_DO_SIZE = STAGE_IDENTIFY+".do.size";

    /** Observable properties for this service type */
    public static URI PROP_IDENTIFY_FORMAT;
    public static URI PROP_IDENTIFY_METHOD;
    public static MeasurementImpl MEASURE_IDENTIFY_FORMAT;
    public static MeasurementImpl MEASURE_IDENTIFY_METHOD;

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getStages()
     */
    public List<ExperimentStageBean> getStages() {
        List<ExperimentStageBean> stages = new Vector<ExperimentStageBean>();
        stages.add( new ExperimentStageBean(STAGE_IDENTIFY, "Identify a digital object."));
        return stages;
    }

    private static HashMap<String,List<MeasurementImpl>> manualObservables;
    /** Statically define the observable properties. FIXME Should be built from the TechReg */
    private static HashMap<String,List<MeasurementImpl>> observables;
    static {

        // Set up URIs
        try {
            PROP_IDENTIFY_FORMAT = new URI( TecRegMockup.URIDigitalObjectPropertyRoot+"basic/format" );
            PROP_IDENTIFY_METHOD = new URI( TecRegMockup.URIServicePropertyRoot + "identify/method" );
        } catch (URISyntaxException e) { 
            log.error("Error during initialisation: " +e);
            e.printStackTrace();
        }
        
        // Set up properties:
        MEASURE_IDENTIFY_FORMAT = new MeasurementImpl(
                PROP_IDENTIFY_FORMAT, 
                "The format of the Digital Object", "",
                "The format of a Digital Object, specified as a Planets Format URI.", 
                null, MeasurementImpl.TYPE_DIGITALOBJECT);
        MEASURE_IDENTIFY_METHOD = new MeasurementImpl(
                PROP_IDENTIFY_METHOD, 
                "The identification method.", "",
                "The method the service used to identify the digital object.", 
                null, MeasurementImpl.TYPE_SERVICE);

        // Now set up the hash:
        observables = new HashMap<String,List<MeasurementImpl>>();
        observables.put(STAGE_IDENTIFY, new Vector<MeasurementImpl>() );
        // The service succeeded
        observables.get(STAGE_IDENTIFY).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_SUCCESS) );
        // The service time
        observables.get(STAGE_IDENTIFY).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME) );
        // The object size:
        observables.get(STAGE_IDENTIFY).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_DO_SIZE) );
        
        // The measured type:
        observables.get(STAGE_IDENTIFY).add( MEASURE_IDENTIFY_FORMAT );

        // The identification method employed by the service:
        observables.get(STAGE_IDENTIFY).add( MEASURE_IDENTIFY_METHOD );
        
        
        manualObservables = new HashMap<String,List<MeasurementImpl>>();
        manualObservables.put(STAGE_IDENTIFY, new Vector<MeasurementImpl>() );

        /*
        observables.put( IDENTIFY_SUCCESS, 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_SUCCESS, STAGE_IDENTIFY) );
        // The service time
        observables.put( IDENTIFY_SERVICE_TIME, 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME, STAGE_IDENTIFY) );
        // The object size:
        observables.put( IDENTIFY_DO_SIZE, 
                TecRegMockup.getObservable(TecRegMockup.PROP_DO_SIZE, STAGE_IDENTIFY) );
        // The measured type:
        observables.put( 
                IDENTIFY_FORMAT,
                new MeasurementImpl(
                        PROP_IDENTIFY_FORMAT, 
                        "The format of the Digital Object", "",
                        "The format of a Digital Object, specified as a Planets Format URI.", 
                        STAGE_IDENTIFY, MeasurementImpl.TYPE_DIGITALOBJECT)
        );

        // The identification method employed by the service:
        observables.put( 
                IDENTIFY_METHOD,
                new MeasurementImpl(
                        PROP_IDENTIFY_METHOD, 
                        "The identification method.", "",
                        "The method the service used to identify the digital object.", 
                        STAGE_IDENTIFY, MeasurementImpl.TYPE_SERVICE)
        );
         */

    }

    /* ------------------------------------------------------------- */

    /** Parameters for the workflow execution etc */
    HashMap<String, String> parameters = new HashMap<String,String>();
    /** The holder for the identifier service. */
    Identify identifier = null;
    URL identifierEndpoint = null;

    /* ------------------------------------------------------------- */

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getObservables()
     */
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        return observables;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getManualObservables()
     */
    public HashMap<String,List<MeasurementImpl>> getManualObservables() {
    	return manualObservables;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#setParameters(java.util.HashMap)
     */
    public void setParameters(HashMap<String, String> parameters)
    throws Exception {
        this.parameters = parameters;
        // Attempt to connect to the Identify service.
        identifierEndpoint = new URL( this.parameters.get(PARAM_SERVICE));
        identifier = new IdentifyWrapper( identifierEndpoint );
    }
    
    public HashMap<String, String> getParameters(){
    	return this.parameters;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#execute(eu.planets_project.services.datatypes.DigitalObject, java.util.HashMap)
     */
    public WorkflowResult execute( DigitalObject dob ) {

        // Invoke the service, timing it along the way:
        boolean success = true;
        String exceptionReport = "";
        IdentifyResult identify = null;
        long msBefore = 0, msAfter = 0;
        msBefore = System.currentTimeMillis();
        try {
            identify = identifier.identify(dob,null);
        } catch( Exception e ) {
            success = false;
            exceptionReport = "<p>Service Invocation Failed!<br/>" + e + "</p>";
        }
        msAfter = System.currentTimeMillis();

        // Now prepare the result:
        WorkflowResult wr = new WorkflowResult();
        
        // Record this one-stage experiment:
        ExecutionStageRecordImpl idStage = new ExecutionStageRecordImpl(STAGE_IDENTIFY);
        wr.getStages().add( idStage );
        
        // Record the endpoint of the service used for this stage.  FIXME Can this be done more automatically, from above?
        idStage.setEndpoint(identifierEndpoint);
        
        List<MeasurementRecordImpl> recs = idStage.getMeasurements();
        recs.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0) ));
        
        // Now record
        try {
            if( success && identify.getTypes() != null && identify.getTypes().size() > 0 ) {
                recs.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));
                collectIdentifyResults(recs, identify, dob);
                wr.setReport(identify.getReport());
                return wr;
            }
        } catch( Exception e ) {
            exceptionReport += "<p>Failed with exception: "+e+"</p>";
        }

        // Build in a 'service failed' property.
        recs.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "false"));

        // Create a ServiceReport from the exception.
        // TODO can we distinguish tool and install error here?
        ServiceReport sr = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                "No info");
        if (identify != null && identify.getReport() != null) {
            String info = identify.getReport().toString();
            sr = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, info);
        }
        wr.setReport(sr);

        return wr;
    }
    
    public static void collectIdentifyResults( List<MeasurementRecordImpl> recs, IdentifyResult ident, DigitalObject dob ) {
        if( ident == null ) return;
        if( ident.getTypes() != null ) {
            for( URI format_uri : ident.getTypes() ) {
                if( format_uri != null ) {
                    recs.add( new MeasurementRecordImpl( PROP_IDENTIFY_FORMAT, format_uri.toString()));
        }
            }
        }
        if( ident.getMethod() != null ) {
            recs.add( new MeasurementRecordImpl( PROP_IDENTIFY_METHOD, ident.getMethod().name() ));
        }
        // Store the size:
        // FIXME: This method has now been added to the Digital Object.  Change it here to dob.getContentSize();
        recs.add( new MeasurementRecordImpl(TecRegMockup.PROP_DO_SIZE, ""+getContentSize(dob) ) );
        return;
    }

    /*
     * Recursive method for computing the total size.
     * TODO A badly-formed DigitalObject could cause this method to recurse forever. Can that be stopped?
     */
    static long getContentSize( DigitalObject dob ) {
        long bytes = 0;
        // Get the size at this level:
        if( dob.getContent() != null ) {
            bytes += getSizeOfContent(dob.getContent());
        }
        // Return the total:
        return bytes;
    }

    /*
     * Attempts to determine the size of the content of a particular DigitalObject.
     */
    private static long getSizeOfContent( DigitalObjectContent con ) {
        if( con == null ) return 0;
        try {
            return con.read().available();   
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
