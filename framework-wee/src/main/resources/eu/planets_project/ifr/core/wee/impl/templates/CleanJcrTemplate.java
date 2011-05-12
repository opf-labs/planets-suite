package eu.planets_project.ifr.core.wee.impl.templates;

import eu.planets_project.ifr.core.storage.impl.data.StorageDigitalObjectReference;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;


/**
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 */
public class CleanJcrTemplate extends
		WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    /**
     * Identify service to execute
     */
    private Identify identify;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template cleans JCR repository.";
	}

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#initializeExecution()
     */
	public WorkflowResult initializeExecution() {
		this.getWFResult().setStartTime(System.currentTimeMillis());
		return this.getWFResult();
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
	 */
	@SuppressWarnings("finally")
	public WorkflowResult execute(DigitalObject dgoA) {

		// document all general actions for this digital object
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				dgoA.getPermanentUri(),
				WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
				System.currentTimeMillis(),
				this.getWorkflowReportingLogger());
		this.addWFResultItem(wfResultItem);
		wfResultItem.addLogInfo("working on workflow template: " + this.getClass().getName());
		wfResultItem.addLogInfo("workflow-instance id: " + this.getWorklowInstanceID());

		// start executing on digital ObjectA
		this.processingDigo = dgoA;

		try {
			// Clean up JCR repository
            wfResultItem.addLogInfo("Clean up JCR repository. initial digital object: " + dgoA.toString());
      	    // Manage the Digital Object Data Registry:
            wfResultItem.addLogInfo("Initialize JCR repository instance.");
            JcrDigitalObjectManagerImpl dodm = 
            	 (JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
      	    dodm.removeAll();
	        
         	wfResultItem.addLogInfo("Completed clean up of JCR repository.");
            wfResultItem.setEndTime(System.currentTimeMillis());

			wfResultItem
				.addLogInfo("Successfully completed workflow for digitalObject with permanent uri:"
						+ processingDigo);
			wfResultItem.setEndTime(System.currentTimeMillis());

		} catch (Exception e) {
			String err = "workflow execution error for digitalObject with permanent uri: " + processingDigo;
			wfResultItem.addLogInfo(err + " " + e);
			wfResultItem.setEndTime(System.currentTimeMillis());
		}
		
		return this.getWFResult();
	}
	
	

	/** {@inheritDoc} */
	public WorkflowResult finalizeExecution() {
		this.getWFResult().setEndTime(System.currentTimeMillis());
		LogReferenceCreatorWrapper.createLogReferences(this);
		return this.getWFResult();
	}


}
