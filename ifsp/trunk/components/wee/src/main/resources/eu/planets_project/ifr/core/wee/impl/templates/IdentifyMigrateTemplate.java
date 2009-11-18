package eu.planets_project.ifr.core.wee.impl.templates;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import eu.planets_project.ifr.core.techreg.formats.api.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

public class IdentifyMigrateTemplate extends WorkflowTemplateHelper implements WorkflowTemplate {

    private transient ReportingLog log = initLog();

    /**
     * @return A reporting log
     */
    private ReportingLog initLog() {
        return new ReportingLog(Logger.getLogger(IdentifyMigrateTemplate.class));
    }

    /**
     * Identify service
     */
    private Identify identify;

    /**
     * Migrate service (to JPEG)
     */
    private Migrate migrate;

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
     */
    public String describe() {
        return "The structure of a workflow is defined within its execute method. This specific workflow tests the "
                + "modify interface";
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
     */
    public WorkflowResult execute() {
        /* We want fresh logs and report for every run: */
        log = initLog();
        WorkflowResult wfResult = new WorkflowResult();
        int count = 0;
        List<DigitalObject> objects = new ArrayList<DigitalObject>();
        log.trace(WorkflowTemplateHelper.overview(this));
        String metadata;
        try {
            for (DigitalObject dgo : this.getData()) {
                metadata = null;
                log.info("Processing file #" + (count + 1));
                try {
                    // Identify
                    String[] types = runIdentification(dgo, wfResult);
                    log.info(new Message("Identification", new Parameter("File", dgo.getTitle()), new Parameter(
                            "Result", Arrays.asList(types).toString())));

                    // Extract metadata - will otherwise get lost between steps!
                    List<Metadata> mList = dgo.getMetadata();
                    if ((mList != null) && (mList.size() > 0)) {
                        metadata = mList.get(0).getContent();
                    }

                    if (metadata == null) {
                        log.warn("No metadata contained in DigitalObject!");
                    } else {
                        log.info("Extracted metadata: " + metadata);
                    }
                    
                    // Migrate
                    try {
                        FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
                        String ext = fr.getFirstExtension(new URI(types[0]));
                        log.info("Getting extension: " + ext);
                        if (ext != null) {
                            dgo = runMigrateService(dgo, fr.createExtensionUri(ext), wfResult);
                            objects.add(dgo);
                            log.info(new Message("Migration", new Parameter("Input", ext), new Parameter("Result", dgo
                                    .getTitle())));
                        }
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                	String err = "workflow execution error for digitalObject #" + count;
                    log.error(err);
                    log.error(e.getClass() + ": " + e.getMessage());
                    System.out.println(e);
                    WorkflowResultItem wfResultItem = new WorkflowResultItem(dgo,
                    		WorkflowResultItem.GENERAL_WORKFLOW_ACTION,
                    		System.currentTimeMillis());
                    wfResultItem.addLogInfo(err);
                    wfResult.addWorkflowResultItem(wfResultItem);
                }
                count++;
            }
        } finally {
        	//document endTime of the workflow
        	wfResult.setEndTime(System.currentTimeMillis());
        	
            /* A final message: */
            List<URL> results = WorkflowTemplateHelper.reference(objects, log.getOutputFolder());
            log.trace(WorkflowTemplateHelper.link(results));
            /* Now write the stuff to disk: */
            File reportFile = log.reportAsFile();
            File logFile = log.logAsFile();
            System.out.println("Wrote report to: " + reportFile.getAbsolutePath());
            /* And return a result object: */
            try {
            	URI outFolder = new URI("http",this.getHostAuthority(),"/data/wee/id-"+log.getTime(),null,null);
				//String outFolder = "http://"+getHostName()+"/data/wee/id-"+log.getTime();
            	URL reportURL = new URL(""+outFolder+"/wf-report.html");
            	URL logFileURL = new URL(""+outFolder+"/wf-log.txt");
            	//wfResult = new WorkflowResult(reportURL, logFile.toURL(), results);           
            	
            	wfResult.setReport(reportURL);
                wfResult.setLog(logFileURL);
                wfResult.setResults(results);
                
            	System.out.println("Workflow result: " + wfResult);
                return wfResult;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
    }

    public static void main(String[] args) {
        new IdentifyMigrateTemplate().log.debug("Stuff!");
    }
    
    //FIXME the localHost cannot be reached externally but the results may note be
    //available on the instance the WEE is running
   /* public String getHostName() {
    	try {
        java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
    
        // Get IP Address
        byte[] ipAddr = addr.getAddress();
    
        // Get hostname
        //String hostname = addr.getHostName();
        String hostname = addr.getHostAddress();
       	return hostname;
    	return this.getHostAuthority();
       	
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return null;
    }*/

    /**
     * Warning! Overrides the standard getData() Method of the
     * WorkflowTemplateHelper with hardcoded reference to BL files stored on the
     * local file system
     * @override
     * @return the BL files to be cropped public List<DigitalObject> getData() {
     *         // Reference to files on file system
     *         SimpleBLNewspaperDigitalObjectManagerImpl blnImpl = new
     *         SimpleBLNewspaperDigitalObjectManagerImpl("c:\\bl\\"); // List
     *         List<URI> identifiers = blnImpl.list(null);
     *         log.info("Processing " + identifiers.size() + " files"); //
     *         Retrieve ArrayList<DigitalObject> digObjects = new
     *         ArrayList<DigitalObject>(); for (URI id : identifiers) { try {
     *         digObjects.add(blnImpl.retrieve(id)); } catch
     *         (DigitalObjectNotFoundException e) { log.warn(e.getMessage()); }
     *         } return digObjects; }
     */

    private String[] runIdentification(DigitalObject digo, WorkflowResult wfresult) throws Exception {
        log.info("STEP 1: Identification...");
        
        //an object used to ducument the results of a service call for the WorkflowResult
        //document the service type and start-time
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
        wfresult.addWorkflowResultItem(wfResultItem);
        
        //get the parameters that were passed along in the configuration
        List<Parameter> parameterList;
        if(this.getServiceCallConfigs(identify)!=null){
        	parameterList = this.getServiceCallConfigs(identify).getAllPropertiesAsParameters();
        }else{
        	parameterList = new ArrayList<Parameter>();
        }
  
        //now actually execute the identify operation of the service
        IdentifyResult results = identify.identify(digo, parameterList);
        
        //document the end-time and input digital object and the params
        wfResultItem.setEndTime(System.currentTimeMillis());
        wfResultItem.setInputDigitalObject(digo);
        wfResultItem.setServiceParameters(parameterList);
        wfResultItem.setServiceEndpoint(identify.describe().getEndpoint());
        
        //have a look at the service's results
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        //report service status and type
        wfResultItem.setServiceReport(report);

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            log.debug(s);
            wfResultItem.addLogInfo(s);
            throw new Exception(s);
        }

        if (types.size() < 1) {
            String s = "The specified file type is currently not supported by this workflow";
            log.debug(s);
            wfResultItem.addLogInfo(s);
            throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count = 0;
        for (URI uri : types) {
            strings[count] = uri.toASCIIString();
            log.debug(strings[count]);
            //document the result
            wfResultItem.addExtractedInformation(strings[count]);
            count++;
        }
        return strings;
    }

    
    private DigitalObject runMigrateService(DigitalObject digO, URI migrateFromURI, WorkflowResult wfresult)
            throws Exception {
        log.info("STEP 2: Migrating ...");
 
		//an object used to ducument the results of a service call for the WorkflowResult
		//document the service type and start-time
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION, System
						.currentTimeMillis());
		wfresult.addWorkflowResultItem(wfResultItem);
		
	    try {
			// URI migrateFromURI = new URI(migrateFrom);
			URI migrateToURI = this.getServiceCallConfigs(this.migrate)
					.getPropertyAsURI(SER_PARAM_MIGRATE_TO);
			// Create service parameter list
			List<Parameter> parameterList = new ArrayList<Parameter>();
			Parameter pCompressionType = this.getServiceCallConfigs(
					this.migrate).getPropertyAsParameter("compressionType");
			if (pCompressionType != null) {
				parameterList.add(pCompressionType);
			}
			Parameter pCompressionQuality = this.getServiceCallConfigs(
					this.migrate).getPropertyAsParameter("compressionQuality");
			if (pCompressionQuality != null) {
				parameterList.add(pCompressionQuality);
			}
			wfResultItem.setServiceEndpoint(migrate.describe().getEndpoint());
			wfResultItem.setStartTime(System.currentTimeMillis());
			wfResultItem.setInputDigitalObject(digO);
			wfResultItem.setServiceParameters(parameterList);
			
			//migrate
			MigrateResult migrateResult = this.migrate.migrate(digO,
					migrateFromURI, migrateToURI, parameterList);
			
			wfResultItem.setEndTime(System.currentTimeMillis());
			wfResultItem.addLogInfo("migration from: "+migrateFromURI+" to: "+migrateToURI+" took place");
			ServiceReport report = migrateResult.getReport();
			//report service status and type
			wfResultItem.setServiceReport(report);
			if (report.getType() == Type.ERROR) {
				String s = "Service execution failed: " + report.getMessage();
				log.debug(s);
				wfResultItem.addLogInfo(s);
				throw new Exception(s);
			}
			//add report on outputDigitalObject
			wfResultItem.setOutputDigitalObject(migrateResult
					.getDigitalObject());
			return migrateResult.getDigitalObject();
		} catch (Exception e) {
			wfResultItem.addLogInfo("Migration failed "+e);
			throw e;
		}
    }

}
