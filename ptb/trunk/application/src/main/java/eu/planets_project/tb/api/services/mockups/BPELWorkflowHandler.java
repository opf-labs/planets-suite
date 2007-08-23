package eu.planets_project.tb.api.services.mockups;

/**
 * @author alindley
 * This file takes
 * a) an existing BPEL file as input and generates a ExperimentWorkflow object from it
 * b) takes an ExperimentWorkflow object and generates a valid BPEL construct
 *
 */
public interface BPELWorkflowHandler {
	
	public void exportBPELtoFile(String sPathAndFileName);
	
	public ComplexWorkflow generateComplexWorkflow();
	public SimpleWorkflow generateSimpleWorkflow();

}
