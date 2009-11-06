package eu.planets_project.ifr.core.wee.impl.templates;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

//TODO AL: EXTEND THIS TEMPLATE SO THAT THE XCDL FOR INPUT AND OUTPUT FILE ARE CREATED
//TODO SS: extend compareDigoIdentical method
//FIXME AL: USE VERSION IN class-name
/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 03.11.2009
 */
public class MultistageMigrationRoundtrippTEMP extends WorkflowTemplateHelper implements WorkflowTemplate {

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
			configForImages = new URL("http://ubuntu.planets-project.arcs.ac.at/cocoImage.xml");
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
    //TODO UPDATE DESCRIPTION
    public String describe() {
        return "This template performs a round-tripp migration A>B>C>D>F where the result objects"+
        	   "A and F as well as B and D are expected to be 'the same'.";
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
     */
    @SuppressWarnings("finally")
	public WorkflowResult execute() {
    	//init the log...
        wfResult = new WorkflowResult();
        int count = 0;
        List<DigitalObject> objects = new ArrayList<DigitalObject>();
        try {
        	//get the digital objects and iterate one by one
            for (DigitalObject dgoA : this.getData()) {
            	
            	//document all general actions for this digital object
            	WorkflowResultItem wfResultItem = new WorkflowResultItem(dgoA,
                		WorkflowResultItem.GENERAL_WORKFLOW_ACTION,
                		System.currentTimeMillis());
            	 wfResult.addWorkflowResultItem(wfResultItem);
            	
            	 //start executing on digital ObjectA
            	this.processingDigo = dgoA.getPermanentUri();
            	
                try {
                    // Migrate Object round-trip
                		wfResultItem.addLogInfo("starting migration A-B");
                    DigitalObject dgoB = runMigration(migrate1,dgoA,false);
                    	wfResultItem.addLogInfo("completed migration A-B");
                    	wfResultItem.addLogInfo("starting migration B-C");
                    DigitalObject dgoC = runMigration(migrate2,dgoB,false);
                    	wfResultItem.addLogInfo("completed migration B-C");
                    	wfResultItem.addLogInfo("starting migration C-D");
                    DigitalObject dgoD = runMigration(migrate3,dgoC,false);
                    	wfResultItem.addLogInfo("completed migration C-D");
                    	wfResultItem.addLogInfo("starting migration D-E");
                    //this object is documented as main experiment outcome file
                    DigitalObject dgoE = runMigration(migrate4,dgoD,true);
                    	wfResultItem.addLogInfo("completed migration D-E");
                    
                    	wfResultItem.addLogInfo("starting XCDL extraction for A");
                    DigitalObject dgoAXCDL = runMigration(migratexcdl1,dgoA,false);
                    	wfResultItem.addLogInfo("completed XCDL extraction for A");
                    	wfResultItem.addLogInfo("starting XCDL extraction for B");
                    DigitalObject dgoEXCDL = runMigration(migratexcdl1,dgoE,false);
                    	wfResultItem.addLogInfo("completed XCDL extraction for B");
                    
                    //perform object comparison
                    	wfResultItem.addLogInfo("starting comparisson for XCDL1 and XCDL2");
                    this.compareDigitalObjectsIdentical(dgoAXCDL, dgoEXCDL);
                    	wfResultItem.addLogInfo("completed comparisson for XCDL1 and XCDL2");
                    
                    	wfResultItem.addLogInfo("successfully completed workflow for digitalObject"+processingDigo);
                    	wfResultItem.setEndTime(System.currentTimeMillis());
                    
                } catch (Exception e) {
                	String err = "workflow execution error for digitalObject #" + count +" ("+processingDigo+")";            
                    wfResultItem.addLogInfo(err+" "+e);
                    wfResultItem.setEndTime(System.currentTimeMillis());
                }
                count++;
            }
            
            wfResult.setEndTime(System.currentTimeMillis());
        	return wfResult;
        	
        } catch(Exception e) {
        	 wfResult.setEndTime(System.currentTimeMillis());
         	return wfResult;
        }
    }
 
    private DigitalObject runMigration(Migrate migrationService, DigitalObject digO, boolean endOfRoundtripp)
            throws Exception {
 
		//an object used for documenting the results of a service action
		//document the service type and start-time
    	WorkflowResultItem wfResultItem;
    	if(endOfRoundtripp){
    		wfResultItem = new WorkflowResultItem(WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION, System.currentTimeMillis());
    	}
    	else{
    		wfResultItem = new WorkflowResultItem(WorkflowResultItem.SERVICE_ACTION_MIGRATION, System.currentTimeMillis());
    	}
		wfResult.addWorkflowResultItem(wfResultItem);
		
	    try {
	    	//static definition of which properties to compare - taking the standard set for images
	    	
	    	//get all parameters that were added in the configuration file
			List<Parameter> parameterList = this.getServiceCallConfigs(migrationService).getAllPropertiesAsParameters();
	    	wfResultItem.setServiceParameters(parameterList);
	    	
	    	// get from config: migrate_to_fmt for this service
			URI migrateToURI = this.getServiceCallConfigs(migrationService)
					.getPropertyAsURI(SER_PARAM_MIGRATE_TO);
			wfResultItem.addLogInfo("set migrate to: "+migrateToURI);
			URI migrateFromURI = this.getServiceCallConfigs(migrationService)
					.getPropertyAsURI(SER_PARAM_MIGRATE_FROM);
			wfResultItem.addLogInfo("set migrate from: "+migrateFromURI);
			
	    	if((migrateToURI==null)&&(migrateFromURI==null)){
	    		String err = "No parameter for: 'migrate_to/from_filetype' specified";
	    		wfResultItem.addLogInfo(err);
	    		throw new Exception(err);
	    	}

	    	//document
			wfResultItem.setInputDigitalObject(digO);
			wfResultItem.setServiceParameters(parameterList);
			wfResultItem.setStartTime(System.currentTimeMillis());
	        wfResultItem.setServiceEndpoint(migrationService.describe().getEndpoint());
			
			//now call the migration
			MigrateResult migrateResult = migrationService.migrate(digO,
					migrateFromURI, migrateToURI, parameterList);
			
			//document
			wfResultItem.setEndTime(System.currentTimeMillis());
			ServiceReport report = migrateResult.getReport();
			//report service status and type
			wfResultItem.setServiceReport(report);
			if (report.getType() == Type.ERROR) {
				String s = "Service execution failed: " + report.getMessage();
				wfResultItem.addLogInfo(s);
				throw new Exception(s);
			}
			//document: add report on outputDigitalObject
			wfResultItem.setOutputDigitalObject(migrateResult
					.getDigitalObject());
			wfResultItem.addLogInfo("migration completed");
			return migrateResult.getDigitalObject();
		} catch (Exception e) {
			wfResultItem.addLogInfo("migration failed "+e);
			throw e;
		}
    }
    
    
    /**
     * Calls the comparison service, logs all relevant information and
     * returns ???
     * @param digo1
     * @param digo2
     * @throws Exception 
     */
    public void compareDigitalObjectsIdentical(DigitalObject digo1, DigitalObject digo2) throws Exception {
    
    	//creating the logger
    	WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_COMPARE,
        		System.currentTimeMillis());
    	 wfResult.addWorkflowResultItem(wfResultItem);
    	 wfResultItem.setAboutExecutionDigoRef(processingDigo);
    	 
        try {
			//FIXME: using a static list of properties ... this should move to the configuration file
			List<Parameter> configProperties = comparexcdl1.convert(CONFIG);
			
			//document
			wfResultItem.setServiceParameters(configProperties);
			wfResultItem.setStartTime(System.currentTimeMillis());
	        wfResultItem.setServiceEndpoint(comparexcdl1.describe().getEndpoint());
			
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
			wfResultItem.addLogInfo("Compare failed");
			throw e;
		}
    }

}
