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
import java.util.UUID;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowContext;
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
import eu.planets_project.services.datatypes.ServiceDescription;
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
public class TestbedShowcaseOnImageComparisonTemplate_v1_05022010 extends
		WorkflowTemplateHelper implements WorkflowTemplate {


	private Identify identifyFormatA;
	
	private Migrate migrateAB;
	private Migrate migrateBC;
	
	private Compare compareAC;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs a A-B and B-C migration action, where format of A (and output of C) is automatically " +
				"determined by an Identification service." +
				"For post-migration-analysis this workflow calls a comparison service to check on similarity of A and C (e.g. in terms of PSNR) and documents that information. ";
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
				//dgoA = this.storeDigitalObjectInJCR(dgoA);

				// document all general actions for this digital object
				WorkflowResultItem wfResultItem = new WorkflowResultItem(dgoA,
						WorkflowResultItem.GENERAL_WORKFLOW_ACTION, System
								.currentTimeMillis(),this.getWorkflowReportingLogger());
				this.addWFResultItem(wfResultItem);
				wfResultItem.addLogInfo("working on workflow template: "+this.getClass().getName());

				// start executing on digital ObjectA
				this.processingDigo = dgoA;

				try {
					//run a pre-Identification service on A to determine it's format
						wfResultItem.addLogInfo("starting identification A");
					URI formatA = identifyFormat(identifyFormatA, dgoA);
						wfResultItem.addLogInfo("completed identification A");
					// Migrate Object round-trip
						wfResultItem.addLogInfo("starting migration A-B");
					DigitalObject dgoB = runMigration(migrateAB, dgoA, formatA, null, false);
						wfResultItem.addLogInfo("completed migration A-B");
						wfResultItem.addLogInfo("starting migration B-C");
					DigitalObject dgoC = runMigration(migrateBC, dgoB,null, formatA, true);
						wfResultItem.addLogInfo("completed migration B-C");
						
					//compare the object's A and C
						wfResultItem.addLogInfo("starting comparison A-C");
					runComparison(compareAC,dgoA,dgoC);
						wfResultItem.addLogInfo("completed comparison A-C");

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
	 * Runs the migration service on a given digital object. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the JCR repository
	 */
	private DigitalObject runMigration(Migrate migrationService,
			DigitalObject digO, URI inputFormat, URI outputFormat, boolean endOfRoundtripp) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo, migrationService, digO, endOfRoundtripp);
		
		//possibly using identification service to determine the input/output format
		if(inputFormat!=null){
			migrWrapper.setInputFormat(inputFormat);
		}
		if(outputFormat!=null){
			migrWrapper.setOutputFormat(outputFormat);
		}
		
		return migrWrapper.runMigration();
	}
	
	
	/**
	 *  Runs the comparison service on two digital objects 
	 * @param compareService
	 * @param digo1
	 * @param digo2
	 * @throws Exception
	 */
	private CompareResult runComparison(Compare compareService, DigitalObject digo1, DigitalObject digo2) throws Exception{
		
		WorkflowResultItem wfResultItem = new WorkflowResultItem(this.processingDigo,
				WorkflowResultItem.SERVICE_ACTION_COMPARE, System
						.currentTimeMillis());
		
		this.getWFResult().addWorkflowResultItem(wfResultItem);

		try {
			// get all parameters that were added in the configuration file
			List<Parameter> parameterList;
			if (this.getServiceCallConfigs(compareService) != null) {
				parameterList = this.getServiceCallConfigs(compareService)
						.getAllPropertiesAsParameters();
			} else {
				parameterList = new ArrayList<Parameter>();
			}

			// document
			wfResultItem.setServiceParameters(parameterList);
			wfResultItem.setStartTime(System.currentTimeMillis());
			// document the endpoint if available - retrieve from WorkflowContext
			String endpoint = this.getWorkflowContext().getContextObject(
					compareService, WorkflowContext.Property_ServiceEndpoint,
					java.lang.String.class);
			if (endpoint != null) {
				wfResultItem.setServiceEndpoint(new URL(endpoint));
			}
			ServiceDescription serDescr = compareService.describe();
			wfResultItem.setServiceDescription(serDescr);

			// now call the comparison
			CompareResult compareResult = compareService.compare(digo1,digo2, parameterList);
			
			// document
			wfResultItem.setEndTime(System.currentTimeMillis());
			ServiceReport report = compareResult.getReport();
			// report service status and type
			wfResultItem.setServiceReport(report);
			if (report.getType() == Type.ERROR) {
				String s = "Service execution failed: " + report.getMessage();
				wfResultItem.addLogInfo(s);
				throw new Exception(s);
			}
			
			//document the comparison's output
			if((compareResult.getProperties()!=null)&&(compareResult.getProperties().size()>0)){
				wfResultItem.addLogInfo("Comparing properties of object A: "+digo1.getPermanentUri()+" with object B: "+digo2.getPermanentUri());
				for(Property p : compareResult.getProperties()){
					String extractedInfo = "[name: "+p.getName()+" value: "+p.getValue()+" untit: "+p.getUnit()+" description:"+p.getDescription()+"] \n";
					wfResultItem.addExtractedInformation(extractedInfo);
				}
			}
			else{
				wfResultItem.addLogInfo("No comparison properties received");
			}

			wfResultItem.addLogInfo("comparison completed");

			return compareResult;

		} catch (Exception e) {
			wfResultItem.addLogInfo("comparison failed " + e);
			throw e;
		}
	}
	

	/**
	 * Runs the identification service on a given digital object and returns the
	 * first format that is found.
	 * 
	 * @param DigitalObject
	 * @return
	 * @throws Exception
	 */
	 private URI identifyFormat(Identify identifyService, DigitalObject digo) throws Exception{
		 
		 WorkflowResultItem wfResultItem = new WorkflowResultItem(this.processingDigo,
					WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION, System
							.currentTimeMillis());
			
		 wfResultItem.setInputDigitalObject(digo);
			this.getWFResult().addWorkflowResultItem(wfResultItem);
	  
		// get all parameters that were added in the configuration file
		List<Parameter> parameterList;
		if (this.getServiceCallConfigs(identifyService) != null) {
			parameterList = this.getServiceCallConfigs(identifyService)
					.getAllPropertiesAsParameters();
		} else {
			parameterList = new ArrayList<Parameter>();
		}
	
		// document
		wfResultItem.setServiceParameters(parameterList);
		
		//call the identification service
		IdentifyResult identifyResults = identifyService.identify(digo,parameterList);
		
		// document
		wfResultItem.setEndTime(System.currentTimeMillis());
		ServiceReport report = identifyResults.getReport(); 
		// report service status and type
		wfResultItem.setServiceReport(report);
		if (report.getType() == Type.ERROR) {
			String s = "Service execution failed: " + report.getMessage();
			wfResultItem.addLogInfo(s);
			throw new Exception(s);
		}
		
		//document the comparison's output
		URI ret = null;
		if((identifyResults.getTypes()!=null)&&(identifyResults.getTypes().size()>0)){
			wfResultItem.addLogInfo("identifying properties of object: "+digo.getPermanentUri());
			for(URI uri : identifyResults.getTypes()){
				if(ret == null){
					ret = uri;
				}
				String extractedInfo = "[uri: "+uri+"] \n";
				wfResultItem.addExtractedInformation(extractedInfo);
			}
		}
		else{
			String s = "Identification failed: format not identified";
			wfResultItem.addLogInfo(s);
			throw new Exception(s);
		}

		wfResultItem.addLogInfo("Identification completed, using format: "+ret);
		return ret;
	 }

}
