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

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

//TODO AL: USE VERSION IN class-name when deploying
/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 03.11.2009
 */
public class MultistageMigrationRoundtripp extends WorkflowTemplateHelper implements WorkflowTemplate {

	//FIXME: AL: LOG ONLY FOR DEBUGGING - REMOVE
	//private static Log log = LogFactory.getLog(MultistageMigrationRoundtripp.class);
	 
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
        return "This template performs a round-tripp migration A>B>C>D>F where the result objects"+
        	   "A and F are expected to be 'the same' in terms of the configured comparison properties." +
        	   "After every migration step an identify service is called to collect additional metadata (e.g. for Service output QA)";
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
        try {
        	//get the digital objects and iterate one by one
            for (DigitalObject dgoA : this.getData()) {
            	
            	//document all general actions for this digital object
            	WorkflowResultItem wfResultItem = new WorkflowResultItem(dgoA.getPermanentUri(),
                		WorkflowResultItem.GENERAL_WORKFLOW_ACTION,
                		System.currentTimeMillis());
            	 wfResult.addWorkflowResultItem(wfResultItem);
            	
            	 //start executing on digital ObjectA
            	this.processingDigo = dgoA.getPermanentUri();
            	
                try {
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
                	String err = "workflow execution error for digitalObject #" + count +" with permanent uri: "+processingDigo+"";            
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
				new URI("planets://localhost:8080/dr/experiment-files"),
				endOfRoundtripp);
		
		return migrWrapper.runMigration();

	}
 
	
    /*private DigitalObject runMigration2(Migrate migrationService, DigitalObject digO, boolean endOfRoundtripp)
            throws Exception {
 
		//an object used for documenting the results of a service action
		//document the service type and start-time
    	WorkflowResultItem wfResultItem;
    	if(endOfRoundtripp){
    		wfResultItem = new WorkflowResultItem(this.processingDigo,WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION, System.currentTimeMillis());
    	}
    	else{
    		wfResultItem = new WorkflowResultItem(this.processingDigo,WorkflowResultItem.SERVICE_ACTION_MIGRATION, System.currentTimeMillis());
    	}
		wfResult.addWorkflowResultItem(wfResultItem);
		
	    try {
	    	//get all parameters that were added in the configuration file
	    	List<Parameter> parameterList;
	    	if(this.getServiceCallConfigs(migrationService)!=null){
	        	parameterList = this.getServiceCallConfigs(migrationService).getAllPropertiesAsParameters();
	        }else{
	        	parameterList = new ArrayList<Parameter>();
	        }
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
			//wfResultItem.setInputDigitalObject(digO);
			wfResultItem.setInputDigitalObjectRef(digORef);
			wfResultItem.setServiceParameters(parameterList);
			wfResultItem.setStartTime(System.currentTimeMillis());
	        wfResultItem.setServiceEndpoint(migrationService.describe().getEndpoint());
			
			//now call the migration
			MigrateResult migrateResult = migrationService.migrate(digORef,
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
			
			//FIXME: try to hand over all digos by reference and not by value - the services should implement this!
			try{
				//return a Digo.Content by Reference
				DigitalObject digoOut = storeObjectByReference(migrateResult.getDigitalObject());
				wfResultItem.setOutputDigitalObject(digoOut);
				wfResultItem.addLogInfo("created digital object by reference");
				wfResultItem.addLogInfo("migration completed");
				try{
					//call an identification service to record more metadata
					List<String> identifiedAs = runIdentification(digoOut);
					wfResultItem.addLogInfo("output object identified as: "+identifiedAs.toString());
				}catch(Exception err){
					wfResultItem.addLogInfo("exception using the identification service: "+err);
				}
				return digoOut;
				
			}catch(Exception err){
				//return a Digo.Content as it was
				wfResultItem.setOutputDigitalObject(migrateResult
						.getDigitalObject());
				wfResultItem.addLogInfo("migration completed");
				try{
					//call an identification service to record more metadata
					List<String> identifiedAs = runIdentification(migrateResult.getDigitalObject());
					wfResultItem.addLogInfo("output object identified as: "+identifiedAs.toString());
				}catch(Exception exc){
					wfResultItem.addLogInfo("exception using the identification service: "+exc);
				}
				wfResultItem.addLogInfo("using digital object by reference");
				return migrateResult.getDigitalObject();
			}

		} catch (Exception e) {
			wfResultItem.addLogInfo("migration failed "+e);
			throw e;
		}
    }*/
    
	/**
	 * Runs the identification service on a given digital object and returns an Array of identified id's (for Droid e.g. PronomIDs)
	 * @param DigitalObject the data
	 * @return
	 * @throws Exception
	 */
	private List<String> runIdentification(URI digoRef) throws Exception{
		
		//get all parameters that were added in the configuration file
		 List<Parameter> parameterList;
        if(this.getServiceCallConfigs(identify1)!=null){
        	parameterList = this.getServiceCallConfigs(identify1).getAllPropertiesAsParameters();
        }else{
        	parameterList = new ArrayList<Parameter>();
        }
        
        DigitalObject digo = this.getDataRegistry().retrieve(digoRef);
	        
        IdentifyResult results = identify1.identify(digo,parameterList);
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();
        
        if(report.getType() == Type.ERROR){
        	String s = "Service execution failed: " + report.getMessage();
   
        	throw new Exception(s);
        }
        if(types.size()<1){
        	String s = "The specified file type is currently not supported by this workflow";
        	throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count=0;
        for (URI uri : types) {
        	strings[count] = uri.toASCIIString();
            count++;
        }
        return Arrays.asList(strings);
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
    	 wfResult.addWorkflowResultItem(wfResultItem);
    	 wfResultItem.setAboutExecutionDigoRef(processingDigo);
    	 
        try {
			//FIXME: using a static list of properties ... this should move to the configuration file
			List<Parameter> configProperties = comparexcdl1.convert(CONFIG);
			
			//document
			wfResultItem.setServiceParameters(configProperties);
			wfResultItem.setStartTime(System.currentTimeMillis());
	        wfResultItem.setServiceEndpoint(comparexcdl1.describe().getEndpoint());
	        
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
    
    
/* <-- start of DigitalObjectManager functionality
     * FIXME: In future this steps below should be performed by the Planets wide 
     * central DigitalObjectManager. 
     * - store a Digo (content by value) that was computed within the wee's workflow. 
     * and return the same Digo but just with content by reference. Note: the file-ref contained in content is a temp file.
     * Issues: How to handle Realm, lookup (JNDI?) over distributed instances, etc.
     */
    private static final String externallyReachableFiledir = "../server/default/deploy/jboss-web.deployer/ROOT.war/wee-wftemp-data";

    private DigitalObject storeObjectByReference(DigitalObject digo) throws Exception{
    	//1. get a temporary file
		File fOut = this.createTempFileInExternallyAccessableDir();
	    OutputStream out = new FileOutputStream(fOut);

	    //2. store the data as file
        byte[] buf = new byte[1024];
        int len;
        InputStream in = digo.getContent().getInputStream();
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        
        //3. build the digo by reference
        URI httpRef = this.getHttpFileRef(fOut);
        DigitalObjectContent content = Content.byReference(httpRef.toURL());
        DigitalObject ret = new DigitalObject.Builder(digo).content(content).permanentUri(digo.getPermanentUri()).build();
        return ret;
    }
    
    private File createTempFileInExternallyAccessableDir() throws IOException {
		
		File f = createTemporaryFile();
		File dir = new File(externallyReachableFiledir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File target = new File(dir.getAbsoluteFile()+"/"+f.getName());
		target.deleteOnExit();
		
		// this doesn't work on the CI - use streams instead
		// boolean success = f.renameTo(target);
		FileInputStream fis  = new FileInputStream(f);
	    FileOutputStream fos = new FileOutputStream(target);
	    try {
	        byte[] buf = new byte[1024];
	        int i = 0;
	        while ((i = fis.read(buf)) != -1) {
	            fos.write(buf, 0, i);
	        }
	        f.delete();
	    } 
	    catch (Exception e) {
	    	String err = "Problems saving the digital object's content (by value) to the externally reachable jboss-web deployer dir "+e;
	    	throw new IOException(err);
	    }
	    finally {
	        if (fis != null) fis.close();
	        if (fos != null) fos.close();
	        f.delete();
	    }
	    //log.debug("rename success? target "+target.getAbsolutePath()+" exists?: "+target.exists());
		return target;
	}
    
    private static File createTemporaryFile() throws IOException {
    	int lowerBound = 1; int upperBound = 9999;
		int random = (int) (lowerBound + Math.random() * ( upperBound - lowerBound) );
		File f = File.createTempFile("dataHandlerTemp"+random, null);
		//log.info("created temporary file:"+f.getAbsolutePath());
		f.deleteOnExit();
		return f;
    }
    
    
	private URI getHttpFileRef(File tempFileInExternalDir)throws URISyntaxException, FileNotFoundException {
		//URI file ref for file to be created
		if(!tempFileInExternalDir.canRead()){
			String err = "getHttpFileRef for "+tempFileInExternalDir +" not found";
			//log.info(err);
			throw new FileNotFoundException(err);
		}
		String authority = this.getHostAuthority();

		//URI(scheme,authority,path,query,fragement)
		URI ret = new URI("http",authority,"/wee-wftemp-data/"+tempFileInExternalDir.getName(),null,null);
		//log.info("getHttpFileRef: "+ret);
		return ret;
	}
 
    
//<--- END oF DigitalObjectManager functionality.
    
}
