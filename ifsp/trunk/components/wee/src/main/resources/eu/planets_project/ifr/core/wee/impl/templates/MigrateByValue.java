package eu.planets_project.ifr.core.wee.impl.templates;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;

public class MigrateByValue extends WorkflowTemplateHelper implements WorkflowTemplate {

    private transient ReportingLog log = initLog();

    /**
     * @return A reporting log
     */
    private ReportingLog initLog() {
        return new ReportingLog(Logger.getLogger(MigrateByValue.class));
    }

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
                + "SIARD service";
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
                    
                    // Migrate
                    try {
                    		String ext = dgo.getTitle().substring(dgo.getTitle().lastIndexOf('.')+1, dgo.getTitle().length());
                    		log.info("detected extension: "+ext);
                    		
                        FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
                        //String ext = fr.getFirstExtension(new URI(types[0]));
                        log.info("Getting extension: " + ext);
                        if (ext != null) {
                            DigitalObject dgoOut = runMigrateService(dgo, fr.createExtensionUri(ext), wfResult);
                            
                            objects.add(dgoOut);
                            log.info(new Message("Migration", new Parameter("Input", ext), new Parameter("Result", dgo
                                    .getTitle())));
                        }
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
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
        new MigrateByValue().log.debug("Stuff!");
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

  

    private DigitalObject runMigrateService(DigitalObject digO, URI migrateFromURI, WorkflowResult wfresult)
            throws Exception {
        log.info("STEP 2: Migrating ...");
        // URI migrateFromURI = new URI(migrateFrom);

				InputStream streamContent = digO.getContent().read();
				byte[] byteContent = FileUtils.writeInputStreamToBinary(streamContent);
				//ImmutableContent content = new ImmutableContent(byteContent);
				DigitalObjectContent content = Content.byValue(byteContent);
				digO = (new DigitalObject.Builder(digO)).content(content).build();

        URI migrateToURI = this.getServiceCallConfigs(this.migrate).getPropertyAsURI(SER_PARAM_MIGRATE_TO);
        
        // Create service parameter list
        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter pCompressionType = this.getServiceCallConfigs(this.migrate).getPropertyAsParameter("compressionType");
        if (pCompressionType != null) {
            parameterList.add(pCompressionType);
        }

        Parameter pCompressionQuality = this.getServiceCallConfigs(this.migrate).getPropertyAsParameter(
                "compressionQuality");
        if (pCompressionQuality != null) {
            parameterList.add(pCompressionQuality);
        }

        MigrateResult migrateResult = this.migrate.migrate(digO, migrateFromURI, migrateToURI, parameterList);
        
        DigitalObject dgoOut = migrateResult.getDigitalObject();
        
				//put filename without extenstion if empty
				if(dgoOut.getTitle() == null) {
					String title_ = digO.getTitle().substring(0,digO.getTitle().lastIndexOf('.'));
					if(migrateToURI.toString().toLowerCase().lastIndexOf("siard") > 0) title_ = title_ + ".siard";
					dgoOut = (new DigitalObject.Builder(dgoOut)).title(title_).build();
				}

        ServiceReport report = migrateResult.getReport();

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            log.debug(s);
            throw new Exception(s);
        }
        return dgoOut;
    }

}
