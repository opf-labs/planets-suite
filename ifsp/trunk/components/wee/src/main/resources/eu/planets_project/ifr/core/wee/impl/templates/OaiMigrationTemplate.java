package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.migrate.Migrate;


/**
 * This is migration template for OAI registries
 */
public class OaiMigrationTemplate extends
		WorkflowTemplateHelper implements WorkflowTemplate {

	/**
	 * The migration service to execute upon all inputs
	 */
	private Migrate migrate;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs the migration of the OAI regitry data";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
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
			// start executing on digital Object
			this.processingDigo = dgo;

			// Single Migration
			wfResultItem.addLogInfo("starting migration A-B");
			URI dgoBRef = runMigration(migrate, dgo.getPermanentUri(), true);
				wfResultItem.addLogInfo("completed migration A-B");

			wfResultItem
				.addLogInfo("successfully completed workflow for digitalObject with permanent uri:"
						+ processingDigo);
			wfResultItem.setEndTime(System.currentTimeMillis());
		} catch (Exception e) {
			String err = "workflow execution error for digitalObject  with permanent uri: " + processingDigo
			+ "";
			wfResultItem.addLogInfo(err + " " + e);
			wfResultItem.setEndTime(System.currentTimeMillis());
		}
		return this.getWFResult();

	}

	/**
	 * Runs the migration service on a given digital object reference. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the specified repository
	 */
	private URI runMigration(Migrate migrationService,
			URI digORef, boolean endOfRoundtripp) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo.getPermanentUri(), 
				migrationService, 
				digORef, 
				new URI("planets://localhost:8080/dr/experiment-files"),
				endOfRoundtripp);
		
		return migrWrapper.runMigration();

	}

	
    /** {@inheritDoc} */
    public WorkflowResult finalizeExecution() {
    	this.getWFResult().setEndTime(System.currentTimeMillis());
		LogReferenceCreatorWrapper.createLogReferences(this);
		return this.getWFResult();
    }
	
}
