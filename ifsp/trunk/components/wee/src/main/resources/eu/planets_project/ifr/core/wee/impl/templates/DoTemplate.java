package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
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

    /**
     * Identify service
     */
    private Identify identify;

    /**
     * Migrate service (to JPEG)
     */
    private Migrate migrate;

	/**
	 * The current digital object
	 */
	private URI processingDigo;

    
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
	@SuppressWarnings("finally")
    public WorkflowResult execute(DigitalObject dgo) {
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				dgo.getPermanentUri(),
				WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
				System.currentTimeMillis(),
				this.getWorkflowReportingLogger());
		this.addWFResultItem(wfResultItem);
		
    	wfResultItem.addLogInfo("working on workflow template: "+this.getClass().getName());
    	wfResultItem.addLogInfo("workflow-instance id: "+this.getWorklowInstanceID());

        List<DigitalObject> objects = new ArrayList<DigitalObject>();
        String metadata;
        String title;  //used to note a file name
        try {
            metadata = null;
            title = null;
            
       	 	//start executing on digital ObjectA
        	this.processingDigo = dgo.getPermanentUri();

            try {
            	wfResultItem.addLogInfo("****** initial DO. " + dgo.toString());
            	// Manage the Digital Object Data Registry:
            	wfResultItem.addLogInfo("Create JCR for digital object.");
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

                
				wfResultItem.addLogInfo("****** Store DO in JCR. " + dgo.toString());
            	DigitalObject resultDO = dodm.store(PERMANENT_URI_PATH, dgo, true);
            	DigitalObject resultDO2 = dodm.store(PERMANENT_URI_PATH_2, dgo, true);
            	DigitalObject resultDO3 = dodm.store(PERMANENT_URI_PATH_3, dgo, true);
            	URI permanentUri = resultDO.getPermanentUri();
            	URI permanentUri2 = resultDO2.getPermanentUri();
            	URI permanentUri3 = resultDO3.getPermanentUri();
            	wfResultItem.addLogInfo("Store DO in JCR res: " + permanentUri.toString());
                wfResultItem.addLogInfo("Store DO2 in JCR res: " + permanentUri2.toString());
                wfResultItem.addLogInfo("Store DO3 in JCR res: " + permanentUri3.toString());
            	DigitalObject tmpDO = dodm.retrieve(permanentUri, true);
            	wfResultItem.addLogInfo("result DO from JCR title: " + tmpDO.getTitle());
                wfResultItem.addLogInfo("result DO from JCR content length: " + tmpDO.getContent().length());
                
        		DigitalObjectContent c2 = dodm.retrieveContent(permanentUri);
        		wfResultItem.addLogInfo("retrieveContent result length: " + c2.length());

            	tmpDO = dodm.retrieve(permanentUri, false);
            	wfResultItem.addLogInfo("retrieve DO without content");
            	if (tmpDO.getContent() != null)
            	{
            		wfResultItem.addLogInfo("result DO without content from JCR content length: " + tmpDO.getContent().length());
            	}

                // Identify
            	wfResultItem.addLogInfo("starting identify object: ");
                String[] types = runIdentification(dgo, wfResultItem);
            	wfResultItem.addLogInfo("completed identify object format: " + 
            			new Message("Identification", new Parameter("File", dgo.getTitle()), new Parameter(
                        "Result", Arrays.asList(types).toString())));

                // Extract metadata - will otherwise get lost between steps!
                List<Metadata> mList = dgo.getMetadata();

                if ((mList != null) && (mList.size() > 0)) {
                    metadata = mList.get(0).getContent();
                }

                if (metadata == null) {
                	wfResultItem.addLogInfo("No metadata contained in DigitalObject!");
                	wfResultItem.addLogInfo("Add title: " + dgo.getTitle());
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
                	wfResultItem.addLogInfo("Extracted metadata: " + metadata);
                }
                
                // Migrate
                try {
                    FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
                    String ext = fr.getFirstExtension(new URI(types[0]));
                    wfResultItem.addLogInfo("Getting extension: " + ext);
                    if (ext != null) {
                        URI uridgo = runMigration(migrate, dgo.getPermanentUri());
                        dgo = new DigitalObject.Builder(dgo.getContent())
              							.title(title)
              							.build();
                        objects.add(dgo);
                        wfResultItem.addLogInfo("Migration result uri: " + uridgo + ", result: " + 
                        		new Message("Migration", new Parameter("Input", ext), new Parameter("Result", dgo
                                .getTitle())));
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
            	wfResultItem.addLogInfo(e.getClass() + ": " + e.getMessage());
                System.out.println(e);
            }
        } finally {
            /* A final message: */
            /* And return a result object: */
            try {
            	wfResultItem.addLogInfo("successfully completed workflow for digitalObject with permanent uri: " + processingDigo);
            	wfResultItem.setEndTime(System.currentTimeMillis());
    			return this.getWFResult();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

	
    public static void main(String[] args) {
        new DoTemplate();
    }
    

    /**
     * Identification method.
     */
    private String[] runIdentification(DigitalObject digo, WorkflowResultItem wfResultItem) throws Exception {
    	wfResultItem.addLogInfo("STEP 1: Identification...");
        List<Parameter> parameterList = new ArrayList<Parameter>();
        IdentifyResult results = identify.identify(digo, parameterList);
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            wfResultItem.addLogInfo(s);
            throw new Exception(s);
        }

        if (types.size() < 1) {
            String s = "The specified file type is currently not supported by this workflow";
            wfResultItem.addLogInfo(s);
            throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count = 0;
        for (URI uri : types) {
            strings[count] = uri.toASCIIString();
            wfResultItem.addLogInfo(strings[count]);
            count++;
        }
        return strings;
    }

    /**
	 * Runs the migration service on a given digital object reference. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the specified repository
	 */
	private URI runMigration(Migrate migrationService,
			URI digORef) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo, 
				migrationService, 
				digORef, 
				new URI("planets://localhost:8080/dr/experiment-files"),
				true);
		
		return migrWrapper.runMigration();

	}

    
    /** {@inheritDoc} */
    public WorkflowResult finalizeExecution() {
    	this.getWFResult().setEndTime(System.currentTimeMillis());
		LogReferenceCreatorWrapper.createLogReferences(this);
		return this.getWFResult();
    }

}
