package eu.planets_project.ifr.core.wee.impl.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 02.12.2009
 */
public class JIPDemonstrationWorkflow_v1_02122009 extends
		WorkflowTemplateHelper implements WorkflowTemplate {

	/**
	 * Migrate service #1 migrate A>B (e.g. TIF to PNM)
	 */
	private Migrate migrate1;

	/**
	 * Migrate service #2 migrate B>C (e.g. PNM to JP2)
	 */
	private Migrate migrate2;

	/**
	 * Migrate service #3 migrate C>B (e.g. JP2 to XCDL)
	 */
	private Migrate migrate3;

	/**
	 * Identify service QA: checks if the service output is of the expected type
	 * Updates Metadata on the DigitalObject
	 */
	private Identify identify;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs a migration A>B>C and ingests all DigitalObjects into the Planets JCR repository"
				+ "After every migration step an identify service is called to collect additional metadata (e.g. for Service output QA)";
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

				// store the digital object in jcr repository
				//URI uriA = this.storeDigitalObjectInRepository(dgoA,new URI("planets://localhost:8080/dr/planets-jcr"));
				//dgoA = this.getDataRegistry().retrieve(uriA);

				// document all general actions for this digital object
				WorkflowResultItem wfResultItem = new WorkflowResultItem(dgoA.getPermanentUri(),
						WorkflowResultItem.GENERAL_WORKFLOW_ACTION, System
								.currentTimeMillis(),this.getWorkflowReportingLogger());
				this.addWFResultItem(wfResultItem);
				wfResultItem.addLogInfo("working on workflow template: "+this.getClass().getName());

				// start executing on digital ObjectA
				this.processingDigo = dgoA;

				try {
					// Migrate Object round-trip
						wfResultItem.addLogInfo("starting migration A-B");
					DigitalObject dgoB = runMigration(migrate1, dgoA, false);
						wfResultItem.addLogInfo("completed migration A-B");
						wfResultItem.addLogInfo("starting migration B-C");
					DigitalObject dgoC = runMigration(migrate2, dgoB, false);
						wfResultItem.addLogInfo("completed migration B-C");
						wfResultItem.addLogInfo("starting migration C-D");
					DigitalObject dgoD = runMigration(migrate3, dgoC, true);
						wfResultItem.addLogInfo("completed migration C-D");

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
	 * Runs the migration service on a given digital object. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the default repository
	 */
	private DigitalObject runMigration(Migrate migrationService,
			DigitalObject digO, boolean endOfRoundtripp) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo.getPermanentUri(), 
				migrationService, 
				digO.getPermanentUri(),
				endOfRoundtripp);
		
		URI migResDigoRef = migrWrapper.runMigration();
		return this.getDataRegistry().retrieve(migResDigoRef);
		
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
