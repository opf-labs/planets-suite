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
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 02.12.2009
 */
public class TestbedMigrationExperimentTemplate_v1_22122009 extends
		WorkflowTemplateHelper implements WorkflowTemplate {

	/**
	 * The migration service to execute upon all inputs
	 */
	private Migrate migrate1;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs the migration step of the Testbed's 'Migration' experiment type";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
	 */
	@SuppressWarnings("finally")
	public WorkflowResult execute() {
		int count = 0;
		try {
			// get the digital objects and iterate one by one
			for (DigitalObject dgoA : this.getData()) {	

				// document all general actions for this digital object
				WorkflowResultItem wfResultItem = new WorkflowResultItem(
						dgoA.getPermanentUri(),
						WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
						System.currentTimeMillis(),
						this.getWorkflowReportingLogger());
				this.addWFResultItem(wfResultItem);
				wfResultItem.addLogInfo("working on workflow template: "+this.getClass().getName());
				wfResultItem.addLogInfo("workflow-instance id: "+this.getWorklowInstanceID());

				// start executing on digital ObjectA
				this.processingDigo = dgoA;

				try {
					// Single Migration
						wfResultItem.addLogInfo("starting migration A-B");
					URI dgoBRef = runMigration(migrate1, dgoA.getPermanentUri(), true);
						wfResultItem.addLogInfo("completed migration A-B");

					//TODO: use the identification service for data enrichment (e.g. mime type of output object)
						
					wfResultItem
						.addLogInfo("successfully completed workflow for digitalObject with permanent uri:"
								+ processingDigo);
					wfResultItem.setEndTime(System.currentTimeMillis());

				} catch (Exception e) {
					String err = "workflow execution error for digitalObject #"
							+ count + " with permanent uri: " + processingDigo
							+ "";
					wfResultItem.addLogInfo(err + " " + e);
					wfResultItem.setEndTime(System.currentTimeMillis());
				}
				count++;
			}

			this.getWFResult().setEndTime(System.currentTimeMillis());
			LogReferenceCreatorWrapper.createLogReferences(this);
			return this.getWFResult();

		} catch (Exception e) {
			this.getWFResult().setEndTime(System.currentTimeMillis());
			LogReferenceCreatorWrapper.createLogReferences(this);
			return this.getWFResult();
		}
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
				//new URI("planets://testbed-dev.planets-project.ait.ac.at:80/dr/experiment-files"),
				new URI("planets://localhost:8080/dr/experiment-files"),
				endOfRoundtripp);
		
		return migrWrapper.runMigration();

	}

	/**
	 * Runs the identification service on a given digital object and returns an
	 * Array of identified id's (for Droid e.g. PronomIDs)
	 * 
	 * @param DigitalObject
	 *            the data
	 * @return
	 * @throws Exception
	 */
	/*
	 * private List<String> addIdentificationMetadata(DigitalObject digo)
	 * throws Exception{
	 * 
	 * //get all parameters that were added in the configuration file List<Parameter>
	 * parameterList; if(this.getServiceCallConfigs(identify1)!=null){
	 * parameterList =
	 * this.getServiceCallConfigs(identify1).getAllPropertiesAsParameters();
	 * }else{ parameterList = new ArrayList<Parameter>(); }
	 * 
	 * IdentifyResult results = identify1.identify(digo,parameterList);
	 * ServiceReport report = results.getReport(); List<URI> types =
	 * results.getTypes();
	 * 
	 * if(report.getType() == Type.ERROR){ String s = "Service execution failed: " +
	 * report.getMessage();
	 * 
	 * throw new Exception(s); } if(types.size()<1){ String s = "The specified
	 * file type is currently not supported by this workflow"; throw new
	 * Exception(s); }
	 * 
	 * String[] strings = new String[types.size()]; int count=0; for (URI uri :
	 * types) { strings[count] = uri.toASCIIString(); count++; } return
	 * Arrays.asList(strings);
	 * 
	 * 
	 * EINBAUEN
	 * 
	 * Metadata metadataMimeType = new Metadata(PLANETS_URI, MIME_TYPE2,
	 * IMAGE_MIME2); Metadata metadataPronomID = new Metadata(PLANETS_URI,
	 * MIME_TYPE2, IMAGE_MIME2); Metadata[] metaList = new Metadata[2];
	 * metaList[0] = META1; metaList[1] = META2;
	 * 
	 * END EINBAUEN }
	 */

}
