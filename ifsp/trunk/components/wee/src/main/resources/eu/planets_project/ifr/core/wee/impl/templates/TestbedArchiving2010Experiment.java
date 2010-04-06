package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowContext;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;

//TODO AL: USE VERSION IN class-name when deploying
/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 18.03.2010
 */
public class TestbedArchiving2010Experiment extends WorkflowTemplateHelper implements WorkflowTemplate {

    /**
     * Migrate service #1 migrate A>B (e.g. TIF to PNM)
     */
    private Migrate migrate1;

    /**
     * Migrate service #2 migrate B>C (e.g. PNM to JP2)
     */
    private Migrate migrate2;
    

    /**
     * Migrate service #3 migrate C>B (e.g. JP2 to PNM)
     */
    private Migrate migrate3;
    
    /**
     * Migrate service #4 migrate B>A (e.g. PNM to TIF)
     */
    private Migrate migrate4;
    
    /**
     * Identify service #1 - QA: checks if the service output is of the expected type
     */
    private Identify identify1;
    
    /**
     * Migration service #5 extracts XCDL description for input and output object
     */
    private Migrate migratexcdl1;
    
    /**
     * Comparison service #1- takes XCDL of input object - XCDL of output object
     * and runs a comparison. 
     */
    private Compare comparexcdl1;
    
    private WorkflowResult wfResult;
	private URI processingDigo;
	private static URL configForImages;
	private static DigitalObject CONFIG;
	
	static{
		try{
			//FIXME: this information should come from the data section of the configuration file
			configForImages = new URL("http://testbed.planets-project.eu/planets-testbed/cocoImage_20112009_al.xml");
			CONFIG = new DigitalObject.Builder(
			            Content.byReference(configForImages)).build();
		}catch(Exception e){
			configForImages = null;
		}
	}

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
     */
    public String describe() {
        return "This template performs a round-tripp migration A>B>C>D>E where the result objects"+
        	   "A and E are expected to be 'the same' in terms of the configured comparison properties." +
        	   "After every migration step an identify service is called to collect additional metadata (e.g. for Service output QA)";
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
     */
    @SuppressWarnings("finally")
	public WorkflowResult execute(DigitalObject dgoA) {
          	
    	//document all general actions for this digital object
    	// document all general actions for this digital object
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				dgoA.getPermanentUri(),
				WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
				System.currentTimeMillis(),
				this.getWorkflowReportingLogger());
		this.addWFResultItem(wfResultItem);
		
    	wfResultItem.addLogInfo("working on workflow template: "+this.getClass().getName());
    	wfResultItem.addLogInfo("workflow-instance id: "+this.getWorklowInstanceID());
    	
    	 //start executing on digital ObjectA
    	this.processingDigo = dgoA.getPermanentUri();
    	
        try {
        	//identify format before migration roundtripp
        	wfResultItem.addLogInfo("starting identify object A format: ");
        	URI dgoAFormat = identifyFormat(identify1,dgoA.getPermanentUri());
        	wfResultItem.addLogInfo("completed identify object A format: "+dgoAFormat);
        	
        	// Migrate Object round-trip
        		wfResultItem.addLogInfo("starting migration A-B");
            URI dgoBRef = runMigration(migrate1,dgoA.getPermanentUri(),false);
            	wfResultItem.addLogInfo("completed migration A-B");
            	wfResultItem.addLogInfo("starting migration B-C");
            URI dgoCRef = runMigration(migrate2,dgoBRef,false);
            	wfResultItem.addLogInfo("completed migration B-C");
            	wfResultItem.addLogInfo("starting migration C-D");
            URI dgoDRef = runMigration(migrate3,dgoCRef,false);
            	wfResultItem.addLogInfo("completed migration C-D");
            	wfResultItem.addLogInfo("starting migration D-E");
            //this object is documented as main experiment outcome file
            URI dgoERef = runMigration(migrate4,dgoDRef,true);
            	wfResultItem.addLogInfo("completed migration D-E");
            
            //identify format after migration roundtripp
            wfResultItem.addLogInfo("starting identify object E format: ");
            URI dgoEFormat = identifyFormat(identify1,dgoERef);
            wfResultItem.addLogInfo("completed identify object E format: "+dgoEFormat);
            
            wfResultItem.addLogInfo("starting XCDL extraction for A");
            URI dgoAXCDL = runMigration(migratexcdl1,dgoA.getPermanentUri(),false);
            	wfResultItem.addLogInfo("completed XCDL extraction for A");
            	wfResultItem.addLogInfo("starting XCDL extraction for B");
            URI dgoEXCDL = runMigration(migratexcdl1,dgoERef,false);
            	wfResultItem.addLogInfo("completed XCDL extraction for B");
            
            //perform object comparison
            	wfResultItem.addLogInfo("starting comparisson for XCDL1 and XCDL2");
            this.compareDigitalObjectsIdentical(dgoAXCDL, dgoEXCDL);
            	wfResultItem.addLogInfo("completed comparisson for XCDL1 and XCDL2");
            
            	wfResultItem.addLogInfo("successfully completed workflow for digitalObject with permanent uri:"+processingDigo);
            	wfResultItem.setEndTime(System.currentTimeMillis());
            
        } catch (Exception e) {
        	String err = "workflow execution error for digitalObject with permanent uri: "+processingDigo+"";            
            wfResultItem.addLogInfo(err+" "+e);
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
    
    /**
	 * Runs the migration service on a given digital object reference. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the specified repository
	 */
	private URI runMigration(Migrate migrationService,
			URI digORef, boolean endOfRoundtripp) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo, 
				migrationService, 
				digORef, 
				DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/executions/"),
				//new URI("planets://testbed.planets-project.eu:80/dr/experiment-files"),
				endOfRoundtripp);
		
		return migrWrapper.runMigration();

	}
 
	
    
	/**
	 * Runs the identification service on a given digital object reference and returns the
	 * first format that is found.
	 * 
	 * @param DigitalObject
	 * @return
	 * @throws Exception
	 */
	 private URI identifyFormat(Identify identifyService, URI digoRef) throws Exception{
		 
		 WorkflowResultItem wfResultItem = new WorkflowResultItem(this.processingDigo,
					WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION, System
							.currentTimeMillis());
			
		 wfResultItem.setInputDigitalObjectRef(digoRef);
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
		// document the endpoint if available - retrieve from WorkflowContext
		String endpoint = this.getWorkflowContext().getContextObject(
				identifyService, WorkflowContext.Property_ServiceEndpoint,
				java.lang.String.class);
		if (endpoint != null) {
			wfResultItem.setServiceEndpoint(new URL(endpoint));
		}
		wfResultItem.setStartTime(System.currentTimeMillis());
		
		//resolve the digital Object reference
		DigitalObject digo = this.retrieveDigitalObjectDataRegistryRef(digoRef);
		
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
    
    
    /**
     * Calls the comparison service, logs all relevant information and
     * returns ???
     * @param digo1
     * @param digo2
     * @throws Exception 
     */
    private void compareDigitalObjectsIdentical(URI digo1Ref, URI digo2Ref) throws Exception {
    
    	//creating the logger
    	WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_COMPARE,
        		System.currentTimeMillis());
         this.addWFResultItem(wfResultItem);
    	 wfResultItem.setAboutExecutionDigoRef(processingDigo);
    	 
        try {
			//FIXME: using a static list of properties ... this should move to the configuration file
			List<Parameter> configProperties = comparexcdl1.convert(CONFIG);
			
			//document
			wfResultItem.setServiceParameters(configProperties);
			wfResultItem.setStartTime(System.currentTimeMillis());
			// document the endpoint if available - retrieve from WorkflowContext
			String endpoint = this.getWorkflowContext().getContextObject(
					comparexcdl1, WorkflowContext.Property_ServiceEndpoint,
					java.lang.String.class);
			if (endpoint != null) {
				wfResultItem.setServiceEndpoint(new URL(endpoint));
			}
	        
	        DigitalObject digo1 = this.getDataRegistry().retrieve(digo1Ref);
	        DigitalObject digo2 = this.getDataRegistry().retrieve(digo2Ref);
			
	        //call the comparison service
			CompareResult result = comparexcdl1.compare(digo1, digo2,
					configProperties);
		  	
			//document
			wfResultItem.setEndTime(System.currentTimeMillis());
			ServiceReport report = result.getReport();
			//report service status and type
			wfResultItem.setServiceReport(report);
			if (report.getType() == Type.ERROR) {
				String s = "Service execution failed: " + report.getMessage();
				wfResultItem.addLogInfo(s);
				throw new Exception(s);
			}
			//document: add report on outputDigitalObject
			wfResultItem.addExtractedInformation(result.getProperties().toString());
			wfResultItem.addLogInfo("comparisson completed");
			
		} catch (Exception e) {
			wfResultItem.addLogInfo("comparisson failed: "+e);
			throw e;
		}
    }
    
}
