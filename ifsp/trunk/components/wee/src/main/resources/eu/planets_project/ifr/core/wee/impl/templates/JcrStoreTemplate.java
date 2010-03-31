package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;

import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;


public class JcrStoreTemplate extends WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** URI to use for digital object repository creation. */
	private static final URI PERMANENT_URI_PATH = URI.create("/ait/images");


    /**
     * Identify service. At least one service must be defined in the template.
     */
    private Identify identify;

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
     */
    public String describe() {
        return "The structure of a workflow is defined within its execute method. This specific workflow tests the "
                + "storing of the digital object in JCR repository.";
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
     */
	@SuppressWarnings("finally")
    public WorkflowResult execute(DigitalObject dgo) {
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				dgo.getPermanentUri(),
				WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
				System.currentTimeMillis(),
				this.getWorkflowReportingLogger());
		this.addWFResultItem(wfResultItem);
		
    	wfResultItem.addLogInfo("working on workflow template: "+this.getClass().getName());
    	wfResultItem.addLogInfo("workflow-instance id: "+this.getWorklowInstanceID());
    	
        try {
            wfResultItem.addLogInfo("****** initial DO. " + dgo.toString());
        	  // Manage the Digital Object Data Registry:
            wfResultItem.addLogInfo("Create JCR for digital object.");
            JcrDigitalObjectManagerImpl dodm = 
            	(JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
            wfResultItem.addLogInfo("****** digital object before store.");
        	dgo = dodm.store(PERMANENT_URI_PATH, dgo, true);
        	wfResultItem.addLogInfo("****** digital object after store: " + dgo.toString());
        	URI permanentUri = dgo.getPermanentUri();
            wfResultItem.addLogInfo("Store DO in JCR res: " + permanentUri.toString());
        	DigitalObject tmpDO = dodm.retrieve(permanentUri, true);
            wfResultItem.addLogInfo("result DO from JCR content length: " + tmpDO.getContent().length());
            wfResultItem.addLogInfo("result DO from JCR after retrieve: " + tmpDO.toString());
            wfResultItem.setEndTime(System.currentTimeMillis());

		} catch (Exception e) {
			wfResultItem.addLogInfo("exception: "+e.toString()+" for DO: "+dgo.toString());
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
