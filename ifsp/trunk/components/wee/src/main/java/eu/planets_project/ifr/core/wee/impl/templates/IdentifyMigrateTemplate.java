package eu.planets_project.ifr.core.wee.impl.templates;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
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

public class IdentifyMigrateTemplate extends WorkflowTemplateHelper implements
        WorkflowTemplate {

    private static final ReportingLog log = new ReportingLog(Logger
            .getLogger(IdentifyMigrateTemplate.class));

    /**
     * Identify service
     */
    private Identify identify;

    /**
     * Migrate service
     */
    private Migrate migrate;

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
     */
    public String describe() {
        return "The structure of a workflow is defined within its execute method. This specific workflow tests the migrate interface";
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
     */
    public WorkflowResult execute() {
        WorkflowResult wfResult = null;
        int count = 0;
        List<DigitalObject> objects = new ArrayList<DigitalObject>();

        String metadata;
        try {
            for (DigitalObject dgo : this.getData()) {
                metadata = null;
                log.info("Processing file #" + (count + 1));
                try {
                    // Identify
                    String[] types = runIdentification(dgo, wfResult);
                    log.info(new Message("Identification", new Parameter(
                            "File", dgo.getTitle()), new Parameter("Result",
                            Arrays.asList(types).toString())));

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
                        FormatRegistry fr = FormatRegistryFactory
                                .getFormatRegistry();
                        String ext = fr.getFirstExtension(new URI(types[0]));
                        log.info("Getting extension: " + ext);
                        if (ext != null) {
                            dgo = runMigrateService(dgo, fr
                                    .createExtensionUri(ext), wfResult);
                            objects.add(dgo);
                            log.info(new Message("Migration", new Parameter(
                                    "Input", ext), new Parameter("Result", dgo
                                    .getTitle())));
                        }
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    log.error("workflow execution error for digitalObject #"
                            + count);
                    log.error(e.getClass() + ": " + e.getMessage());
                }
                count++;
            }
        } finally {
            File reportFile = log.reportAsFile();
            File logFile = log.logAsFile();
            List<URL> results = WorkflowTemplateHelper.reference(objects, log
                    .getOutputFolder());
            System.out.println("Wrote report to: "
                    + reportFile.getAbsolutePath());
             
            System.out.println("jboss home url:"+System.getProperty("jboss.home.url"));
              
            try {
                wfResult = new WorkflowResult(reportFile.toURL(), logFile
                        .toURL(), results);
                System.out.println("Workflow result: " + wfResult);
                return wfResult;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        log.debug("Stuff!");
    }

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

    private String[] runIdentification(DigitalObject digo,
            WorkflowResult wfresult) throws Exception {
        log.info("STEP 1: Identification...");
        List<Parameter> parameterList = new ArrayList<Parameter>();
        IdentifyResult results = identify.identify(digo, parameterList);
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            log.debug(s);
            throw new Exception(s);
        }

        if (types.size() < 1) {
            String s = "The specified file type is currently not supported by this workflow";
            log.debug(s);
            throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count = 0;
        for (URI uri : types) {
            strings[count] = uri.toASCIIString();
            log.debug(strings[count]);
            count++;
        }
        return strings;
    }

    private DigitalObject runMigrateService(DigitalObject digO,
            URI migrateFromURI, WorkflowResult wfresult) throws Exception {
        log.info("STEP 2: Migrating...");
        // URI migrateFromURI = new URI(migrateFrom);
        URI migrateToURI = this.getServiceCallConfigs(this.migrate)
                .getPropertyAsURI(SER_PARAM_MIGRATE_TO);

        // Create service parameter list
        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter pCompressionType = this.getServiceCallConfigs(this.migrate)
                .getPropertyAsParameter("compressionType");
        if (pCompressionType != null) {
            parameterList.add(pCompressionType);
        }

        Parameter pCompressionQuality = this
                .getServiceCallConfigs(this.migrate).getPropertyAsParameter(
                        "compressionQuality");
        if (pCompressionQuality != null) {
            parameterList.add(pCompressionQuality);
        }

        MigrateResult migrateResult = this.migrate.migrate(digO,
                migrateFromURI, migrateToURI, parameterList);
        ServiceReport report = migrateResult.getReport();

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            log.debug(s);
            throw new Exception(s);
        }
        return migrateResult.getDigitalObject();
    }

}
