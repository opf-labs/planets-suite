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

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.services.wrappers.MigrateWrapper;

/**
 * This is the class that carries the code specific to invoking an Migrate experiment.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MigrateWorkflow implements ExperimentWorkflow {
    private static Log log = LogFactory.getLog(MigrateWorkflow.class);

    /** External property keys */
    public static final String PARAM_SERVICE = "migrate.service";
    public static final String PARAM_FROM = "migrate.from";
    public static final String PARAM_TO = "migrate.to";
    
    /** Internal keys for easy referral to the service+stage combinations. */
    private static final String STAGE_MIGRATE = "Migrate";
    private static final String MIGRATE_SUCCESS = STAGE_MIGRATE+".service.success";
    private static final String MIGRATE_SERVICE_TIME = STAGE_MIGRATE+".service.time";
    
    /** Statically define the observable properties. FIXME Should be built from the TechReg */
    private static HashMap<String,MeasurementImpl> observables;
    static {
        observables = new HashMap<String,MeasurementImpl>();
        try {

            // The service succeeded
            observables.put( 
                    MIGRATE_SUCCESS,
                    new MeasurementImpl(
                            new URI( TecRegMockup.URIServicePropertyRoot+"success" ), 
                            "Service succeeded", "",
                            "Did this service execute successfully?  Returns true/false.", 
                            STAGE_MIGRATE, MeasurementImpl.TYPE_SERVICE)
                    );
            // The service time
            observables.put( 
                MIGRATE_SERVICE_TIME,
                new MeasurementImpl(
                        new URI( TecRegMockup.URIServicePropertyRoot+"wallclock" ), 
                        "Service execution time", "seconds",
                        "The wall-clock time taken to execute the service, in seconds.", 
                        STAGE_MIGRATE, MeasurementImpl.TYPE_SERVICE)
                );
        
        } catch (URISyntaxException e) { 
        }
        
    }
    
    /* ------------------------------------------------------------- */
    
    /** Parameters for the workflow execution etc */
    HashMap<String, String> parameters = new HashMap<String,String>();
    /** The holder for the identifier service. */
    Migrate migrator = null;

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
        // Attempt to connect to the Migrate service.
        migrator = new MigrateWrapper( new URL(this.parameters.get(PARAM_SERVICE)) );
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#execute(eu.planets_project.services.datatypes.DigitalObject, java.util.HashMap)
     */
    public WorkflowResult execute( DigitalObject dob ) {

        // Invoke the service, timing it along the way:
        boolean success = true;
        String exceptionReport = "";
        MigrateResult migrated = null;
        long msBefore = 0, msAfter = 0;
        URI from = null, to = null;
        msBefore = System.currentTimeMillis();
        try {
            from = new URI(this.parameters.get(PARAM_FROM));
            to = new URI(this.parameters.get(PARAM_TO));
            migrated = migrator.migrate(dob, from, to, null);
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
        measurements.get(MIGRATE_SERVICE_TIME).setValue(""+((msAfter-msBefore)/1000.0));
        log.info("Got timing: "+((msAfter-msBefore)/1000.0));
        // Now record
        if( success && migrated.getDigitalObject() != null ) {
            
            measurements.get(MIGRATE_SUCCESS).setValue("true");
            
            // FIXME: Take the digital object, and give it a sensible name, using the new format extension.
            DigitalObject.Builder newdob = new DigitalObject.Builder(migrated.getDigitalObject());
            if( to != null ) {
                Format f = ServiceBrowser.fr.getFormatForURI(to);
                newdob.title( dob.getTitle()+"."+f.getExtensions().iterator().next());
            }
            wr = new WorkflowResult(measurements.values(), 
                    WorkflowResult.RESULT_DIGITAL_OBJECT, newdob.build(), migrated.getReport() );
        } else {
            // Build in a 'service success' property.
            measurements.get(MIGRATE_SUCCESS).setValue("false");
            // Create a ServiceReport from the exception.
            ServiceReport sr = new ServiceReport();
            sr.setErrorState(ServiceReport.ERROR);
            sr.setError(exceptionReport);
            sr.setInfo(migrated.getReport().toString());
            wr = new WorkflowResult(measurements.values(), null, null, sr);
        }
        return wr;
    }

}
