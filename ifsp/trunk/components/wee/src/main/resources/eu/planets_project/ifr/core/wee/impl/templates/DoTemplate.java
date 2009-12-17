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

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;

public class DoTemplate extends WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** URI to use for digital object repository creation. */
    public static final URI DOREP = URI.create("planets:do_perm_uri"); 
    public static final URI DOFORMAT = URI.create("planets:do_format_uri"); 
	private static final URI PERMANENT_URI_PATH = URI.create("/ait/images");
	private static final URI PERMANENT_URI_PATH_2 = URI.create("/ait");
	private static final URI PERMANENT_URI_PATH_3 = URI.create("/ait/images/tmp");

    private transient ReportingLog log = initLog();
    
    /**
     * @return A reporting log
     */
    private ReportingLog initLog() {
        return new ReportingLog(Logger.getLogger(DoTemplate.class));
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
        log.warn("Starting EXECUTE");
        WorkflowResult wfResult = null;
        int count = 0;
        List<DigitalObject> objects = new ArrayList<DigitalObject>();
        log.trace(WorkflowTemplateHelper.overview(this));
        String metadata;
        String title;  //used to note a file name
        try {
            for (DigitalObject dgo : this.getData()) {
                metadata = null;
                title = null;
                log.info("Processing file #" + (count + 1));
                try {
                    log.info("****** initial DO. " + dgo.toString());
                	  // Manage the Digital Object Data Registry:
                    log.info("R... Create JCR for digital object.");
                    JcrDigitalObjectManagerImpl dodm = 
                    	(JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
                    
        			// Prepare data for digital object - to remove later
        		    Metadata META1 = new Metadata(URI.create("http://planets-project.eu"), "meta1", "meta1");
        		    Metadata META2 = new Metadata(URI.create("http://planets-project.eu"), "meta2", "meta2");
        		    Metadata[] metaList = new Metadata[2];
        		    metaList[0] = META1;
        		    metaList[1] = META2;
        		    Event[] eventList = new Event[2];
        		    Agent agent = new Agent("id", "name", "type");
        		    List<eu.planets_project.services.datatypes.Property> propList = new ArrayList<eu.planets_project.services.datatypes.Property>();
        		    eu.planets_project.services.datatypes.Property
        		    prop1 = new eu.planets_project.services.datatypes.Property.Builder(URI.create("http://planets-project.eu"))
    		             .name("Java JVM System Properties")
    		             .value("value")
    		             .description("description")
    		             .unit("unit")
    		             .type("type")
    		             .build();
        		    eu.planets_project.services.datatypes.Property
        		    prop2 = new eu.planets_project.services.datatypes.Property.Builder(URI.create("http://planets-project.eu"))
    		             .name("Java JVM System Properties2")
    		             .value("value2")
    		             .description("description2")
    		             .unit("unit2")
    		             .type("type2")
    		             .build();
        		    propList.add(prop1);
        		    propList.add(prop2);
        		    Event event1 = new Event("summary1", "datetime1", 10.23d, agent, propList);
        		    Event event2 = new Event("summary2", "datetime2", 22.45d, agent, propList);
        		    eventList[0] = event1;
        		    eventList[1] = event2;

    				dgo = new DigitalObject.Builder(dgo.getContent())
    				 	 .title(dgo.getTitle())
                         .permanentUri(DOREP)
                         .manifestationOf(DOFORMAT)
                         .format(DOFORMAT)
                         .metadata(metaList)
                         .events(eventList)
                         .build();

                    
                    log.info("****** Store DO in JCR. " + dgo.toString());
                	DigitalObject resultDO = dodm.store(PERMANENT_URI_PATH, dgo, true);
                	DigitalObject resultDO2 = dodm.store(PERMANENT_URI_PATH_2, dgo, true);
                	DigitalObject resultDO3 = dodm.store(PERMANENT_URI_PATH_3, dgo, true);
                	URI permanentUri = resultDO.getPermanentUri();
                	URI permanentUri2 = resultDO2.getPermanentUri();
                	URI permanentUri3 = resultDO3.getPermanentUri();
                    log.info("R... Store DO in JCR res: " + permanentUri.toString());
                    log.info("R... Store DO2 in JCR res: " + permanentUri2.toString());
                    log.info("R... Store DO3 in JCR res: " + permanentUri3.toString());
                	DigitalObject tmpDO = dodm.retrieve(permanentUri, true);
                    log.info("R... result DO from JCR title: " + tmpDO.getTitle());
                    log.info("R... result DO from JCR content length: " + tmpDO.getContent().length());
                    
            		DigitalObjectContent c2 = dodm.retrieveContent(permanentUri);
            		log.info("R... retrieveContent result length: " + c2.length());

                	tmpDO = dodm.retrieve(permanentUri, false);
                    log.info("R... retrieve DO without content");
                	if (tmpDO.getContent() != null)
                	{
                       log.info("R... result DO without content from JCR content length: " + tmpDO.getContent().length());
                	}

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
                        log.info("Add title: " + dgo.getTitle());
                        title = dgo.getTitle();
												if(title.contains(".")) 
												{
													title = title.substring(0, title.lastIndexOf("."));
												}
								        URI myMigrateToURI = 
								              this.getServiceCallConfigs(this.migrate).getPropertyAsURI(SER_PARAM_MIGRATE_TO);
                        String newExtension = 
                              FormatRegistryFactory.getFormatRegistry().getFirstExtension(myMigrateToURI);
												title = title + "." + newExtension;
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
                            dgo = new DigitalObject.Builder(dgo.getContent())
                  							.title(title)
                  							.build();
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
            	URI outFolder = new URI("http",WorkflowTemplateHelper.getHostAuthority(),"/wee-gen/id-"+log.getResultsId(),null,null);
            	URL reportURL = new URL(outFolder+"/wf-report.html");
                wfResult = new WorkflowResult(reportURL, logFile.toURI().toURL(), results);
                System.out.println("Workflow result: " + wfResult);
                return wfResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new DoTemplate().log.debug("Stuff!");
    }
    
    public String getHostName() {
    	try {
        java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
    
        // Get IP Address
        //byte[] ipAddr = addr.getAddress();
    
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

    private DigitalObject runMigrateService(DigitalObject digO, URI migrateFromURI, WorkflowResult wfresult)
            throws Exception {
        log.info("STEP 2: Migrating ...");
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
