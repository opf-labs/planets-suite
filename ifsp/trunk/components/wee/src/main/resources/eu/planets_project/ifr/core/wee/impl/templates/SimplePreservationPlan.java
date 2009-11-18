package eu.planets_project.ifr.core.wee.impl.templates;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.planets_project.ifr.core.techreg.formats.api.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
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
import eu.planets_project.services.modify.Modify;
import eu.planets_project.services.modify.ModifyResult;

public class SimplePreservationPlan extends WorkflowTemplateHelper implements WorkflowTemplate {

    private transient ReportingLog log = initLog();

    /**
     * @return A reporting log
     */
    private ReportingLog initLog() {
        return new ReportingLog(Logger.getLogger(SimplePreservationPlan.class));
    }

    private Identify identify1;
    private Migrate migrate1;
    private Identify identify2;

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
        WorkflowResult wfResult = null;
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
                    String[] types = runIdentification1(dgo, wfResult);
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
                            dgo = runMigrationService(dgo, fr.createExtensionUri(ext), wfResult);
                            objects.add(dgo);
                            log.info(new Message("Migration", new Parameter("Input", ext), new Parameter("Result", dgo
                                    .getTitle())));
                        }
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    
                    types = runIdentification2(dgo, wfResult);
										log.info(new Message("Identification2", new Parameter("File", dgo.getTitle()), new Parameter(
                            "Result", Arrays.asList(types).toString())));

                    
                } catch (Exception e) {
                    log.error("workflow execution error for digitalObject #" + count);
                    log.error(e.getClass() + ": " + e.getMessage());
                    System.out.println(e);
                }
                count++;
            }
        } finally {
            /* A final message: */
            List<URL> results = WorkflowTemplateHelper.reference(objects, log.getOutputFolder());
            log.trace(WorkflowTemplateHelper.link(results));
            /* Now write the stuff to disk: */
            File reportFile = log.reportAsFile();
            File logFile = log.logAsFile();
            System.out.println("Wrote report to: " + reportFile.getAbsolutePath());
            /* And return a result object: */
            try {
            		//URL reportURL = reportFile.toURL();
								String outFolder = "http://"+this.getHostName()+":80/data/wee/id-"+log.getTime();
            		URL reportURL = new URL(outFolder+"/wf-report.html");
                wfResult = new WorkflowResult(reportURL, logFile.toURL(), results);
                System.out.println("Workflow result: " + wfResult);
                return wfResult;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new SimplePreservationPlan().log.debug("Stuff!");
    }
    
    public String getHostName() {
    	try {
        java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
    
        // Get IP Address
        byte[] ipAddr = addr.getAddress();
    
        // Get hostname
        //String hostname = addr.getHostName();
        String hostname = addr.getHostAddress();
       	return hostname;
       	
    	} catch (Exception e) {
    		System.out.println(e);
    	}
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
        List<Parameter> parameterList = new ArrayList<Parameter>();	 	
        IdentifyResult results = identify1.identify(digo,parameterList);
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        if(report.getType() == Type.ERROR){
            String s = "Service execution failed: " + report.getMessage();
            log.info(s);
            throw new Exception(s);
        }
        if(types.size()<1){
            String s = "The specified file type is currently not supported by this workflow";
            log.info(s);
            throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count=0;
        for (URI uri : types) {
            strings[count] = uri.toASCIIString();
            log.info(strings[count]);
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
    		log.info("identification 2 - got digo with title: " + digo.getTitle());
        List<Parameter> parameterList = new ArrayList<Parameter>();
        IdentifyResult results = identify2.identify(digo,parameterList);
        log.info("identification 2 - results: " + results);
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        if(report.getType() == Type.ERROR){
            String s = "Service execution failed: " + report.getMessage();
            log.info(s);
            throw new Exception(s);
        }
        if(types.size()<1){
            String s = "The specified file type is currently not supported by this workflow";
            log.info(s);
            throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count=0;
        for (URI uri : types) {
            strings[count] = uri.toASCIIString();
            log.info(strings[count]);
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
    private DigitalObject runMigrationService(DigitalObject digO, URI migrateFromURI, WorkflowResult wfresult) throws Exception{
        /*
         * defines the input and output format to migrate
         * input and output uri format: "planets:fmt/ext/tiff"
         * input and output format parameters are defined by the workflowTemplate interface and should be moved into the FormatRegistry
         * FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
         */
        //the migrateFrom is the input from the initial identification service
        
        //rr
        //URI migrateFromURI = new URI(migrateFrom);
        
        
        //the migrateTo is fixed and given by the serviceCallConfig XML
        URI migrateToURI = this.getServiceCallConfigs(this.migrate1).
        getPropertyAsURI(SER_PARAM_MIGRATE_TO);

        log.info("runMigrationService: "+migrateFromURI +" "+migrateToURI);
        /*
         * This workflow uses the Service configuration parameters:
         * compressionType and compressionQuality
         * These values should be added from 
         * @see eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.ImageMagickMigrationsTestHelper
         * into the technical registry!
         * 
         * Within this workflow we've decided to use as many parameters of those two as configured within the xml config,
         * but if one is not available (i.e. null) we still try to invoke the migration service (and use the default config of the service)
         */
        List<Parameter> parameterList = new ArrayList<Parameter>();

        Parameter pCompressionType = this.getServiceCallConfigs(this.migrate1).
        getPropertyAsParameter("compressionType");
        if(pCompressionType!=null){
            parameterList.add(pCompressionType);
        }
        Parameter pCompressionQuality = this.getServiceCallConfigs(this.migrate1).
        getPropertyAsParameter("compressionQuality");
        if(pCompressionQuality!=null){
            parameterList.add(pCompressionQuality);
        }

        /*
         * Now call the migration service
         */
        MigrateResult migrateResult = this.migrate1.migrate(digO, migrateFromURI, migrateToURI, parameterList);
        ServiceReport report = migrateResult.getReport();

        if(report.getType() == Type.ERROR){
            String s = "Service execution failed: " + report.getMessage();
            log.info(s);
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
}
