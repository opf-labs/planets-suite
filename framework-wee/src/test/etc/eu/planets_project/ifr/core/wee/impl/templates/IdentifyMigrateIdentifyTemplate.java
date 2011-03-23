package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;


public class IdentifyMigrateIdentifyTemplate extends WorkflowTemplateHelper implements WorkflowTemplate{

	private static final Log log = LogFactory.getLog(IdentifyMigrateIdentifyTemplate.class);
	
	/*
	 * All objects implementing the PlanetsService interface will be reflected within this workflow
	 * and therefore must be configured within the provided xml config.
	 * Naming convention: The id used within the config xml is the object's java variable name
	 */
	private Identify identify1;
	private Migrate migrate1;
	private Identify identify2;
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe(){
		return "The structure of a workflow is defined within its execute method. This specific workflow"+"\n" 
		+"1) calls a Planets identification service"+"\n"
		+"2) uses the first returned type as input to trigger a migration from the extracted fmt and a configured (fixed) target fmt"+"\n"
		+"3) the migration result is identified and compared if the expected type was returned";
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
	 */
	public WorkflowResult execute(){
		
		WorkflowResult wfResult = null;
		int count = 0;
		for(DigitalObject dgo : this.getData()){
			
			try{
				//run identification service one
				String[] sTypes1 = runIdentification1(dgo,wfResult);
				 log.debug("ran execute(): identification1: identified #"+ sTypes1.length+ "of types, using first one as migrate_from input");
				
				//call a migration service - and use output of service1 as input
				DigitalObject migDigO =  runMigrationService(dgo,sTypes1[0],wfResult);
				 log.debug("ran execute(): migration1 for digitalObject");
				
				//Call identification service2 on the migration services output.
				String[] sTypes2 = runIdentification2(migDigO, wfResult);
				 log.debug("ran execute(): identification2: identified #"+ sTypes2.length+ "of types, checking if the expected one is contained");
			
				//now compare the expected fmt to the one which was produced by the migration service
				boolean bFound = false;
				for(String type : sTypes2){
					if(type.equals(this.getExpectedMigrationResultfmt().toASCIIString())){
						bFound = true;
						log.debug("ran execute(): migration service successfully produced the expected fmt: "+type);
					}
				}
				
				//TODO still missing - writing all the information in the return object: WorkflowResult
				 
			}catch(Exception e){
				/*
				 * A workflowTemplate author needs to decided which events terminate, etc.
				 * In this case we've decided within the case of an exception, the execution on this digitalObject is interrupted and the next one is processed
				 * This events, etc. are be taken down within the WorkflowResult object
				 */
				 //TODO Write some statement to the WFResult still missing
				 log.debug("workflow execution error for digitalObject #"+count);
			}
			count++;
		}
		
		//TODO still missing: create a WorkflowResult object and plug in all digoOutputRefs, and execution status and result info
		return null;
	}
	
	
	/**
	 * Runs the identification service on a given digital object and returns an Array of PronomIDs
	 * @param DigitalObject the data
	 * @param WorkflowResult the result object to write events, information, etc.
	 * @return
	 * @throws Exception
	 */
	private String[] runIdentification1(DigitalObject digo, WorkflowResult wfresult) throws Exception{
	 	
        IdentifyResult results = identify1.identify(digo);
        ServiceReport status = results.getReport();
        List<URI> types = results.getTypes();
        
        if(status.equals(status.error)){
        	String s = "Service execution failed";
        	 log.debug(s);
        	throw new Exception(s);
        }
        if(types.size()<1){
        	String s = "The specified file type is currently not supported by this workflow";
        	 log.debug(s);
        	throw new Exception(s);
        }
        
        String[] strings = new String[types.size()];
        int count=0;
        for (URI uri : types) {
        	strings[count] = uri.toASCIIString();
        	log.debug(strings[count]);
            count++;
        }
        return strings;
	}
	

	/**
	 * @param digo
	 * @param wfresult
	 * @return
	 * @throws Exception
	 */
	private String[] runIdentification2(DigitalObject digo,WorkflowResult wfresult) throws Exception{
        IdentifyResult results = identify2.identify(digo);
        ServiceReport status = results.getReport();
        List<URI> types = results.getTypes();
        
        if(status.equals(status.error)){
        	String s = "Service execution failed";
        	 log.debug(s);
        	throw new Exception(s);
        }
        if(types.size()<1){
        	String s = "The specified file type is currently not supported by this workflow";
        	 log.debug(s);
        	throw new Exception(s);
        }
        
        String[] strings = new String[types.size()];
        int count=0;
        for (URI uri : types) {
        	strings[count] = uri.toASCIIString();
        	log.debug(strings[count]);
            count++;
        }
        return strings;
	}
	
	
	/**
	 * @param digO
	 * @param wfresult
	 * @return
	 * @throws Exception
	 */
	private DigitalObject runMigrationService(DigitalObject digO, String migrateFrom, WorkflowResult wfresult) throws Exception{
		/*
		 * defines the input and output format to migrate
		 * input and output uri format: "planets:fmt/ext/tiff"
		 * input and output format parameters are defined by the workflowTemplate interface and should be moved into the FormatRegistry
		 * FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
		 */
		//the migrateFrom is the input from the initial identification service
		URI migrateFromURI = new URI(migrateFrom);
		//the migrateTo is fixed and given by the serviceCallConfig XML
		URI migrateToURI = this.getServiceCallConfigs(this.migrate1).
				getPropertyAsURI(SER_PARAM_MIGRATE_TO);
		
		log.debug("runMigrationService: "+migrateFrom +" "+migrateToURI);
		/*
		 * This workflow uses all specified Service configuration parameters:
		 * e.g. compressionType and compressionQuality which are specific to a certain
		 * service instance as e.g. ImageMagicMigrations
		 * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrationsTestHelper
		 * 
		 * Within this workflow we've decided to use as many parameters of those two as configured within the xml config,
		 * but if one is not available (i.e. null) we still try to invoke the migration service (and use the default config of the service)
		 */
		List<Parameter> parameterList = new ArrayList<Parameter>();
		Parameters parameters = new Parameters();
		/*As long as accepted Parameters for a level-one service type aren't 
    	 *uniquely defined and named the following is not possible within a template. 
    	 *--> use getAllPropertiesAsParameter instead - this may also contain parameters a certain service does not understand
    	 */
		/*
    	Parameter pCompressionType = this.getServiceCallConfigs(this.migrate1).
				getPropertyAsParameter("compressionType");
    	if(pCompressionType!=null){
    		parameterList.add(pCompressionType);
    	}
    	Parameter pCompressionQuality = this.getServiceCallConfigs(this.migrate1).
			getPropertyAsParameter("compressionQuality");
    	if(pCompressionQuality!=null){
    		parameterList.add(pCompressionQuality);
    	}*/
    	parameterList = this.getServiceCallConfigs(this.migrate1).
    		getAllPropertiesAsParameters();
    	
    	//finally set the parameters for the object
		parameters.setParameters(parameterList);
		
		/*
		 * Now call the migration service
		 */
		MigrateResult migrateResult = this.migrate1.migrate(digO, migrateFromURI, migrateToURI, parameters);
		ServiceReport status = migrateResult.getReport();
		
		if(status.equals(status.error)){
			String s = "Service execution failed";
			 log.debug(s);
        	throw new Exception(s);
        }
		
		return migrateResult.getDigitalObject();
	}
	
	/**
	 * Extracts the fmt which the migration is expected to show - this information
	 * is configured within the xml config
	 * @return
	 * @throws Exception
	 */
	private URI getExpectedMigrationResultfmt() throws Exception{
		return this.getServiceCallConfigs(this.migrate1).
		getPropertyAsURI(SER_PARAM_MIGRATE_TO);
	}

	
/* <--    just for information  -->*/
    /*URL url = null;
    try {
	url = new URL(URL_DROID);
    } catch (MalformedURLException e) {
        e.printStackTrace();
        throw e;
    }
    Service service = Service.create(url, new QName(PlanetsServices.NS,
            Identify.NAME));
    Identify droid = service.getPort(Identify.class);*/
    //byte[] array = ByteArrayHelper.read(f1);
    
    //invoke the service and extract results
    //DigitalObject dob = new DigitalObject.Builder( Content.byValue(ByteArrayHelper.read(f1)) ).build();

}
