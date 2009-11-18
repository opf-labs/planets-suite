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

public class ModifyTemplate extends WorkflowTemplateHelper implements WorkflowTemplate {

    private transient ReportingLog log = initLog();

    /**
     * @return A reporting log
     */
    private ReportingLog initLog() {
        return new ReportingLog(Logger.getLogger(ModifyTemplate.class));
    }

    /**
     * Identify service
     */
    private Identify identify;

    /**
     * Modify service (rotate)
     */
    private Modify rotate;

    /**
     * Modify service (crop)
     */
    private Modify crop;

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

                    // Modify - rotate
                    dgo = runRotateService(dgo, types[0], metadata, wfResult);
                    log.info(new Message("Rotation", new Parameter("Metadata", metadata)));

                    // Modify - crop
                    dgo = runCropService(dgo, types[0], metadata, wfResult);
                    log.info(new Message("Cropping", new Parameter("Metadata", metadata)));

                    // Migrate to JPEG
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
                    log.error("workflow execution error for digitalObject #" + count);
                    log.error(e.getClass() + ": " + e.getMessage());
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
								String outFolder = "http://"+"localhost"+":80/data/wee/id-"+log.getTime();
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
        new ModifyTemplate().log.debug("Stuff!");
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

    private String[] runIdentification(DigitalObject digo, WorkflowResult wfresult) throws Exception {
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

    private DigitalObject runRotateService(DigitalObject digO, String inputFormat, String metadata,
            WorkflowResult wfresult) throws Exception {
        log.info("STEP 2: Rotation...");
        URI inputFormatURI = new URI(inputFormat);

        // Get rotation parameter from metadata - uses RegEx for high
        // performance and ease of use
        int skew = 0;
        if (metadata != null) {
            Pattern pageImagePattern = Pattern.compile("<pageSkew>((.|\n)*?)</pageSkew>");
            Matcher m = null;
            String sSkew = null;
            m = pageImagePattern.matcher(metadata);
            if (m.find())
                sSkew = m.group(1);

            if (sSkew != null) {
                try {
                    skew = Integer.parseInt(sSkew);
                } catch (NumberFormatException e) {
                    log.warn("Could not parse skew param: " + e.getMessage());
                }
            }
        } else {
            log.warn("No metadata available - defaulting to parameters in XML config");
        }

        // Create service parameter list
        if (skew != 0) {
            List<Parameter> parameterList = new ArrayList<Parameter>();
            log.info("Extracted skew parameter: " + skew);
            parameterList.add(new Parameter("rotateCounterClockwise", Double.toString(((double) skew) / 100)));

            ModifyResult modifyResult = this.rotate.modify(digO, inputFormatURI, parameterList);
            ServiceReport report = modifyResult.getReport();

            if (report.getType() == Type.ERROR) {
                String s = "Service execution failed: " + report.getMessage();
                log.error(s);
                throw new Exception(s);
            }

            return modifyResult.getDigitalObject();
        } else {
            log.info("Skew 0 - skipping rotate operation");
            return digO; // Skew not specified or null -> skip processing
        }
    }

    private DigitalObject runCropService(DigitalObject digO, String inputFormat, String metadata,
            WorkflowResult wfresult) throws Exception {
        log.info("STEP 3: Crop...");
        URI inputFormatURI = new URI(inputFormat);

        // Get cropping parameters from metadata - uses RegEx for high
        // performance and ease of use
        Pattern pageImagePattern = Pattern.compile("<pageCoordinates>((.|\n)*?)</pageCoordinates>");
        Matcher m = null;
        String coords = null;
        if (metadata != null) {
            m = pageImagePattern.matcher(metadata);
            if (m.find())
                coords = m.group(1);
        } else {
            log.warn("No metadata available - defaulting to parameters in XML config!");
        }

        boolean success = false;
        int top = 0;
        int left = 0;
        int bottom = 0;
        int right = 0;

        if (coords != null) {
            StringTokenizer st = new StringTokenizer(coords, ",");
            if (st.countTokens() == 4) {
                try {
                    top = Integer.parseInt(st.nextToken());
                    left = Integer.parseInt(st.nextToken());
                    bottom = Integer.parseInt(st.nextToken());
                    right = Integer.parseInt(st.nextToken());
                    success = true;
                } catch (NumberFormatException e) {
                    log.warn("Could not parse cropping params: " + e.getMessage());
                }
            }
        }

        // Create service parameter list
        List<Parameter> parameterList = new ArrayList<Parameter>();
        if (success) {
            log.info("Extracted cropping coordinates: " + left + "/" + top + "/" + right + "/" + bottom);

            parameterList.add(new Parameter("top_left_point", top + "," + left));
            parameterList.add(new Parameter("bottom_right_point", bottom + "," + right));
        } else {
            log
                    .warn("No cropping coordinates found in DigitalObject metadata - defaulting to parameters in XML config!");

            Parameter pTopLeftPoint;
            Parameter pBottomRightPoint;
            pTopLeftPoint = this.getServiceCallConfigs(this.crop).getPropertyAsParameter("top_left_point");
            if (pTopLeftPoint != null) {
                parameterList.add(pTopLeftPoint);
            }

            pBottomRightPoint = this.getServiceCallConfigs(this.crop).getPropertyAsParameter("bottom_right_point");
            if (pBottomRightPoint != null) {
                parameterList.add(pBottomRightPoint);
            }
        }

        ModifyResult modifyResult = this.crop.modify(digO, inputFormatURI, parameterList);
        ServiceReport report = modifyResult.getReport();

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            log.error(s);
            throw new Exception(s);
        }

        return modifyResult.getDigitalObject();
    }

    private DigitalObject runMigrateService(DigitalObject digO, URI migrateFromURI, WorkflowResult wfresult)
            throws Exception {
        log.info("STEP 4: Migrating to JPG...");
        // URI migrateFromURI = new URI(migrateFrom);
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
        ServiceReport report = migrateResult.getReport();

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            log.debug(s);
            throw new Exception(s);
        }
        return migrateResult.getDigitalObject();
    }

}
