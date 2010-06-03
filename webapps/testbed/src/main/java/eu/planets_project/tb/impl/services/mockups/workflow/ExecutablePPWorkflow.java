package eu.planets_project.tb.impl.services.mockups.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/*
 * TODO These Workflow classes should not be required anymore (?) after moving to wee and changing observables
 * Currently it allows to define the workflow stages and the definition of objectives that shall be measured in a certain stage
 */
@Deprecated
public class ExecutablePPWorkflow implements ExperimentWorkflow {
    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ExecutablePPWorkflow.class);
    
    /** Internal keys for easy referral to the service+stage combinations. */
    public static final String STAGE_PRE_WF_EXECUTION = "Before Workflow Execution";
    public static final String STAGE_WORKFLOW_EXECUTION = "Workflow Execution";
    public static final String STAGE_POST_WF_EXECUTION = "After Workflow Execution";
    
    private static HashMap<String,List<MeasurementImpl>> manualObservables;
    /** Statically define the automatically observable properties. */
    private static HashMap<String,List<MeasurementImpl>> observables;
    
    static {
        observables = new HashMap<String,List<MeasurementImpl>>();
        manualObservables = new HashMap<String,List<MeasurementImpl>>();
    }

	public WorkflowResult execute(DigitalObject dob) {
		// TODO AL: need to hook the WEE execution in here!!!
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getManualObservables()
	 */
	public HashMap<String, List<MeasurementImpl>> getManualObservables() {
		return manualObservables;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getObservables()
	 */
	public HashMap<String, List<MeasurementImpl>> getObservables() {
		return observables;
	}
	
	@Deprecated
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getStages()
	 */
	public List<ExperimentStageBean> getStages() {
        List<ExperimentStageBean> stages = new Vector<ExperimentStageBean>();
        stages.add( new ExperimentStageBean(STAGE_PRE_WF_EXECUTION, "Objectives measured in the Before Workflow Execution phase"));
        stages.add( new ExperimentStageBean(STAGE_WORKFLOW_EXECUTION, "Objectives measured in the During Workflow Execution phase"));
        stages.add( new ExperimentStageBean(STAGE_POST_WF_EXECUTION, "Objectives measured in the After Workflow Execution phase"));
        return stages;
    }

	@Deprecated
	public void setParameters(HashMap<String, String> parameters)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
