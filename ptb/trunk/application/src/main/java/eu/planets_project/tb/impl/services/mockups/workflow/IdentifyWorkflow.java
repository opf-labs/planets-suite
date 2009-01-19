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

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
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

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getStages()
     */
    public List<ExperimentStageBean> getStages() {
        List<ExperimentStageBean> stages = new Vector<ExperimentStageBean>();
        stages.add( new ExperimentStageBean(STAGE_IDENTIFY, "Identify a digital object."));
        return stages;
    }

    /** Statically define the observable properties. FIXME Should be built from the TechReg */
    private static HashMap<String,List<MeasurementImpl>> observables;
    static {

        try {
            PROP_IDENTIFY_FORMAT = new URI( TecRegMockup.URIDigitalObjectPropertyRoot+"basic/format" );
            PROP_IDENTIFY_METHOD = new URI( TecRegMockup.URIServicePropertyRoot + "identify/method" );
        } catch (URISyntaxException e) { 
            log.error("Error during initialisation: " +e);
            e.printStackTrace();
        }

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
        observables.get(STAGE_IDENTIFY).add( 
                new MeasurementImpl(
                        PROP_IDENTIFY_FORMAT, 
                        "The format of the Digital Object", "",
                        "The format of a Digital Object, specified as a Planets Format URI.", 
                        null, MeasurementImpl.TYPE_DIGITALOBJECT)
        );

        // The identification method employed by the service:
        observables.get(STAGE_IDENTIFY).add( 
                new MeasurementImpl(
                        PROP_IDENTIFY_METHOD, 
                        "The identification method.", "",
                        "The method the service used to identify the digital object.", 
                        null, MeasurementImpl.TYPE_SERVICE)
        );

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

    /* ------------------------------------------------------------- */

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getObservables()
     */
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        return observables;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#setParameters(java.util.HashMap)
     */
    public void setParameters(HashMap<String, String> parameters)
    throws Exception {
        this.parameters = parameters;
        // Attempt to connect to the Identify service.
        identifier = new IdentifyWrapper( new URL(this.parameters.get(PARAM_SERVICE)) );
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
            identify = identifier.identify(dob);
        } catch( Exception e ) {
            success = false;
            exceptionReport = "<p>Service Invocation Failed!<br/>" + e + "</p>";
        }
        msAfter = System.currentTimeMillis();

        // Now prepare the result:
        WorkflowResult wr = new WorkflowResult();
        // FIXME Can this be done more automatically?
        wr.getStage(STAGE_IDENTIFY).setServiceRecord(
                ServiceBrowser.createServiceRecordFromEndpoint(this.parameters.get(PARAM_SERVICE)) );
        List<MeasurementRecordImpl> recs = wr.getStage(STAGE_IDENTIFY).getMeasurements();
        recs.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0) ));
        // Store the size:
        // FIXME: This should be a proper method that scans down and works out the size.
        try {
            recs.add(new MeasurementRecordImpl(TecRegMockup.PROP_DO_SIZE, ""+dob.getContent().read().available() ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Now record
        try {
            if( success && identify.getTypes() != null && identify.getTypes().size() > 0 ) {
                recs.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));
                for( URI format_uri : identify.getTypes() ) {
                    recs.add( new MeasurementRecordImpl( PROP_IDENTIFY_FORMAT, format_uri.toString()));
                }
                for( IdentifyResult.Method method : identify.getMethods() ) {
                    recs.add( new MeasurementRecordImpl( PROP_IDENTIFY_METHOD, method.name() ));
                }
                wr.setReport(identify.getReport());
                return wr;
            }
        } catch( Exception e ) {
            exceptionReport += "<p>Failed with exception: "+e+"</p>";
        }

        // Build in a 'service failed' property.
        recs.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "false"));

        // Create a ServiceReport from the exception.
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.ERROR);
        sr.setError(exceptionReport);
        if( identify != null && identify.getReport() != null )
            sr.setInfo(identify.getReport().toString());
        wr.setReport(sr);

        return wr;
    }

}
