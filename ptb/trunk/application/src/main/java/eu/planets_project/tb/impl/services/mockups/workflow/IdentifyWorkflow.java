/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.ws.Service;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;

/**
 * This is the class that carries the code specific to invoking an Identify experiment.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class IdentifyWorkflow implements ExperimentWorkflow {
    
    /** External property keys */
    public static final String PARAM_SERVICE = "identify.service";
    
    /** Internal keys for easy referral to the service+stage combinations. */
    private static final String STAGE_IDENTIFY = "Identify";
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
    /** The stub for the identifier service. */
    Service service = null;
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
        service = Service.create(new URL(this.parameters.get(PARAM_SERVICE)), Identify.QNAME);
        identifier = service.getPort(Identify.class);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#execute(eu.planets_project.services.datatypes.DigitalObject, java.util.HashMap)
     */
    @SuppressWarnings("unchecked")
    public WorkflowResult execute( DigitalObject dob ) {

        // Invoke the service, timing it along the way:
        boolean success = true;
        IdentifyResult identify = null;
        long msBefore = 0, msAfter = 0;
        try {
            msBefore = System.currentTimeMillis();
            identify = identifier.identify(dob);
            msAfter = System.currentTimeMillis();
        } catch( Exception e ) {
            success = false;
        }
        
        // Now prepare the result:
        WorkflowResult wr = null;
        HashMap<String,MeasurementImpl> measurements = (HashMap<String, MeasurementImpl>) observables.clone();
        if( success ) {
            URI format_uri = identify.getTypes().get(0);
            
            observables.get(IDENTIFY_SUCCESS).setValue("true");
            observables.get(IDENTIFY_SERVICE_TIME).setValue(""+((msAfter-msBefore)/1000.0));
            observables.get(IDENTIFY_FORMAT).setValue(format_uri.toString());
            
            wr = new WorkflowResult(measurements.values(), WorkflowResult.RESULT_URI, format_uri, identify.getReport() );
        } else {
            // FIXME Build in a 'service success' property.
            observables.get(IDENTIFY_SUCCESS).setValue("false");
            // FIXME Create a ServiceReport from the exception.
            wr = new WorkflowResult(null,null,null,null);
        }
        return wr;
    }

}
