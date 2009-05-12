/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.view.CreateView;
import eu.planets_project.services.view.CreateViewResult;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;

/**
 * This is the class that carries the code specific to invoking an Identify experiment.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ViewerWorkflow implements ExperimentWorkflow {
    private static Log log = LogFactory.getLog(ViewerWorkflow.class);

    /** External property keys */
    public static final String PARAM_SERVICE = "viewer.service";

    /** Internal keys for easy referral to the service+stage combinations. */
    public static final String STAGE_CREATEVIEW = "Create View Session";
    public static final String STAGE_ACCESSVIEW = "Access View Session";
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getStages()
     */
    public List<ExperimentStageBean> getStages() {
        List<ExperimentStageBean> stages = new Vector<ExperimentStageBean>();
        stages.add( new ExperimentStageBean(STAGE_CREATEVIEW, "Create View Session."));
        stages.add( new ExperimentStageBean(STAGE_ACCESSVIEW, "Access View Session."));
        return stages;
    }

    private static HashMap<String,List<MeasurementImpl>> manualObservables;
    /** Statically define the observable properties. FIXME Should be built from the TechReg */
    private static HashMap<String,List<MeasurementImpl>> observables;
    static {

        // Now set up the hash:
        observables = new HashMap<String,List<MeasurementImpl>>();
        observables.put(STAGE_CREATEVIEW, new Vector<MeasurementImpl>() );
        // The service succeeded
        observables.get(STAGE_CREATEVIEW).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_SUCCESS) );
        // The service time
        observables.get(STAGE_CREATEVIEW).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME) );
        // The object size:
        observables.get(STAGE_CREATEVIEW).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_DO_SIZE) );
        
        manualObservables = new HashMap<String,List<MeasurementImpl>>();
        manualObservables.put(STAGE_CREATEVIEW, new Vector<MeasurementImpl>() );

    }

    /* ------------------------------------------------------------- */

    /** Parameters for the workflow execution etc */
    HashMap<String, String> parameters = new HashMap<String,String>();
    /** The holder for the identifier service. */
    Service service = null;
    CreateView viewMaker = null;
    URL serviceEndpoint = null;

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
        serviceEndpoint = new URL( this.parameters.get(PARAM_SERVICE));
        service = Service.create(serviceEndpoint, CreateView.QNAME);
        viewMaker = service.getPort(CreateView.class);
    }
    
    public HashMap<String, String> getParameters(){
    	return this.parameters;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#execute(eu.planets_project.services.datatypes.DigitalObject, java.util.HashMap)
     */
    public WorkflowResult execute( DigitalObject dob ) {
        if( dob != null ) {
            log.info("Executing workflow on DigitalObject, title: " + dob.getTitle() );
        }
        
        // Add into a list.
        List<DigitalObject> digitalObjects = new ArrayList<DigitalObject>();
        digitalObjects.add(dob);

        // Invoke the service, timing it along the way:
        boolean success = true;
        String exceptionReport = "";
        CreateViewResult identify = null;
        long msBefore = 0, msAfter = 0;
        msBefore = System.currentTimeMillis();
        try {
            identify = viewMaker.createView(digitalObjects, null);
        } catch( Exception e ) {
            success = false;
            exceptionReport = "<p>Service Invocation Failed!<br/>" + e + "</p>";
        }
        msAfter = System.currentTimeMillis();

        // Now prepare the result:
        WorkflowResult wr = new WorkflowResult();
        
        // Record this one-stage experiment:
        ExecutionStageRecordImpl idStage = new ExecutionStageRecordImpl(STAGE_CREATEVIEW);
        wr.getStages().add( idStage );
        
        // Record the endpoint of the service used for this stage.  FIXME Can this be done more automatically, from above?
        idStage.setEndpoint(serviceEndpoint);
        
        List<MeasurementRecordImpl> recs = idStage.getMeasurements();
        recs.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0) ));
        
        // Now record
        try {
            if( success && identify.getViewURL() != null ) {
                recs.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));
                collectCreateViewResults(recs, identify, dob);
                wr.setMainEndpoint(serviceEndpoint);
                wr.setResult(identify);
                wr.setResultType( WorkflowResult.RESULT_CREATEVIEW_RESULT );
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
    
    public static void collectCreateViewResults( List<MeasurementRecordImpl> recs, CreateViewResult ident, DigitalObject dob ) {
        // Store the size:
        // FIXME: This method has now been added to the Digital Object.  Change it here to dob.getContentSize();
        recs.add( new MeasurementRecordImpl(TecRegMockup.PROP_DO_SIZE, ""+IdentifyWorkflow.getContentSize(dob) ) );
        return;
    }

}
