/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
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
    private static final String IDENTIFY_FORMAT = STAGE_IDENTIFY+".do.format";
    
    /** Statically define the observable properties. FIXME Should be built from the TechReg */
    private static HashMap<String,MeasurementImpl> observables;
    static {
        observables = new HashMap<String,MeasurementImpl>();
        try {

            // The service succeeded
            observables.put( 
                    IDENTIFY_SUCCESS,
                    new MeasurementImpl(
                            new URI( TecRegMockup.URIServicePropertyRoot+"success" ), 
                            "Service succeeded", "",
                            "Did this service execute successfully?  Returns true/false.", 
                            STAGE_IDENTIFY, MeasurementImpl.TYPE_SERVICE)
                    );
            // The service time
            observables.put( 
                IDENTIFY_SERVICE_TIME,
                new MeasurementImpl(
                        new URI( TecRegMockup.URIServicePropertyRoot+"wallclock" ), 
                        "Service execution time", "seconds",
                        "The wall-clock time taken to execute the service, in seconds.", 
                        STAGE_IDENTIFY, MeasurementImpl.TYPE_SERVICE)
                );
        
            // The measured type
            observables.put( 
                    IDENTIFY_FORMAT,
                new MeasurementImpl(
                        new URI( TecRegMockup.URIDigitalObjectPropertyRoot+"basic/format" ), 
                        "The format of the Digital Object", "",
                        "The format of a Digital Object, specified as a Planets Format URI.", 
                        STAGE_IDENTIFY, MeasurementImpl.TYPE_DIGITALOBJECT)
                );
        } catch (URISyntaxException e) { 
        }
        
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
    public Collection<MeasurementImpl> getObservables() {
        return observables.values();
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
        WorkflowResult wr = null;
        HashMap<String,MeasurementImpl> measurements = new HashMap<String, MeasurementImpl>();
        // Deep copy:
        for( String key : observables.keySet()) {
            measurements.put(key, new MeasurementImpl(observables.get(key)) );
        }
        measurements.get(IDENTIFY_SERVICE_TIME).setValue(""+((msAfter-msBefore)/1000.0));
        log.info("Got timing: "+((msAfter-msBefore)/1000.0));
        // Now record
        if( success && identify.getTypes() != null && identify.getTypes().size() > 0 ) {
            URI format_uri = identify.getTypes().get(0);
            
            measurements.get(IDENTIFY_SUCCESS).setValue("true");
            measurements.get(IDENTIFY_FORMAT).setValue(format_uri.toString());
            
            wr = new WorkflowResult(measurements.values(), WorkflowResult.RESULT_URI, format_uri, identify.getReport() );
        } else {
            // Build in a 'service success' property.
            measurements.get(IDENTIFY_SUCCESS).setValue("false");
            // Create a ServiceReport from the exception.
            ServiceReport sr = new ServiceReport();
            sr.setErrorState(ServiceReport.ERROR);
            sr.setError(exceptionReport);
            if( identify != null && identify.getReport() != null )
                sr.setInfo(identify.getReport().toString());
            wr = new WorkflowResult(measurements.values(), null, null, sr);
        }
        return wr;
    }

}
