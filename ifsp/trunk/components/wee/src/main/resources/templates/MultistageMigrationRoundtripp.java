package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

//TODO AL: EXTEND THIS TEMPLATE SO THAT THE XCDL FOR INPUT AND OUTPUT FILE ARE CREATED
//TODO SS: extend compareDigoIdentical method
public class MultistageMigrationRoundtripp extends WorkflowTemplateHelper implements WorkflowTemplate {

	private WorkflowResult wfResult;
	private URI processingDigo;

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

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
     */
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
            	
            	this.processingDigo = dgoA.getPermanentUri();
            	
                try {
                    // Migrate Object round-tripp
                    DigitalObject dgoB = runMigration(migrate1,dgoA,false);
                    DigitalObject dgoC = runMigration(migrate2,dgoB,false);
                    DigitalObject dgoD = runMigration(migrate3,dgoC,false);
                    DigitalObject dgoE = runMigration(migrate4,dgoD,true);
                    
                    //perform object comparison
                    boolean c1 = this.compareDigoIdentical(dgoA, dgoE);
                    boolean c2 = this.compareDigoIdentical(dgoB, dgoD);
                    
                    WorkflowResultItem wfResultItem = new WorkflowResultItem(dgoA,
                    		WorkflowResultItem.GENERAL_WORKFLOW_ACTION,
                    		System.currentTimeMillis());
                    this.wfResult.addWorkflowResultItem(wfResultItem);
                    wfResultItem.addLogInfo("Comparing Object A-E and B-D success? "+(c1&c2));

                } catch (Exception e) {
                	String err = "workflow execution error for digitalObject #" + count;            
                    WorkflowResultItem wfResultItem = new WorkflowResultItem(dgoA,
                    		WorkflowResultItem.GENERAL_WORKFLOW_ACTION,
                    		System.currentTimeMillis());
                    wfResultItem.addLogInfo(err+" "+e);
                    wfResult.addWorkflowResultItem(wfResultItem);
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
	    		String err = "No parameter for: 'migrate_to/to_filetype' specified";
	    		wfResultItem.addLogInfo(err);
	    		throw new Exception(err);
	    	}

	    	//document
			wfResultItem.setInputDigitalObject(digO);
			wfResultItem.setServiceParameters(parameterList);
			wfResultItem.setStartTime(System.currentTimeMillis());
			
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
			return migrateResult.getDigitalObject();
		} catch (Exception e) {
			wfResultItem.addLogInfo("Migration failed "+e);
			throw e;
		}
    }
    
    /**
     * Should run a comparison service on the content - for now just checking 
     * if the checksum of the content is identical.
     * @param digo1
     * @param digo2
     * @return
     */
    private boolean compareDigoIdentical(DigitalObject digo1, DigitalObject digo2) throws Exception{
    	Checksum s1 = digo1.getContent().getChecksum();
    	Checksum s2 = digo2.getContent().getChecksum();
    	
    	WorkflowResultItem wfResultItem = new WorkflowResultItem("Compare.Digital.Objects",System.currentTimeMillis());
    	this.wfResult.addWorkflowResultItem(wfResultItem);
    	wfResultItem.setAboutExecutionDigoRef(this.processingDigo);
    	
    	try {
    		String err = "err: Cannot computate checksum for digital objects in comparison";
			if ((s1 == null) || (s2 == null)) {
				throw new Exception(err);
			}else if((s1.getValue()==null)||(s1.getValue().equals(""))){
				throw new Exception(err);
			}else if((s2.getValue()==null)||(s2.getValue().equals(""))){
				throw new Exception(err);
			}else{
				wfResultItem.addLogInfo("checksum object1: "+s1.toString());
		    	wfResultItem.addLogInfo("checksum object2: "+s2.toString());
		    	
				//now do the comparison
				if(s1.getValue().equals(s2.getValue())){
					wfResultItem.addLogInfo("Object comparison: success");
					wfResultItem.setEndTime(System.currentTimeMillis());
					return true;
				}
				else{
					wfResultItem.addLogInfo("Object comparison: false");
					wfResultItem.setEndTime(System.currentTimeMillis());
					return false;			
				}
			}
		}catch(Exception e){
			wfResultItem.addLogInfo(e+"");
			throw e;
		}

    }

}
