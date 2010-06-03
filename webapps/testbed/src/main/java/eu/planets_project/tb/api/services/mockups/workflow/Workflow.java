package eu.planets_project.tb.api.services.mockups.workflow;

import java.io.File;

import eu.planets_project.tb.api.model.eval.EvaluationExecutable;

public interface Workflow {
	
	public EvaluationExecutable execute(File f1, File f2);

}
