package eu.planets_project.tb.impl.services.mockups.workflow;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;

//TODO AL: CHECK IF REQUIRED OR DELETE
public class ExecutablePPWorkflow implements ExperimentWorkflow {
    private static Log log = LogFactory.getLog(IdentifyWorkflow.class);

	public WorkflowResult execute(DigitalObject dob) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, List<MeasurementImpl>> getManualObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, List<MeasurementImpl>> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ExperimentStageBean> getStages() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setParameters(HashMap<String, String> parameters)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
