package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRConstants;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;


/**
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 * @since 21.04.2010
 */
public class IdentifyMigrateJcrTemplate extends
		WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Identify service URI to use for digital object insertion in JCR repository. */
	private static final URI IDENITFY_PATH = URI.create("/ait/data/wf/identify");
	private static final String IDENTIFY_EVENT = "planets://repository/event/identify";

	/** Migration service URI to use for digital object insertion in JCR repository. */
	private static final URI MIGRATE_PATH = URI.create("/ait/data/wf/migrate");
	private static final String MIGRATE_EVENT = "planets://repository/event/migrate";

	/** Event type */
	private static final String INITIAL_REF_EVENT_TYPE = "planets://events/jcr-update";

	/**
     * Identify service to execute
     */
    private Identify identify;

	/**
	 * The migration service to execute
	 */
	private Migrate migrate;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs the migration step of the Testbed's experiment. " +
		       "It implements digital object identification, migration, insert in JCR and update in JCR.";
	}

	

	public WorkflowResult initializeExecution() {
		this.getWFResult().setStartTime(System.currentTimeMillis());
		return this.getWFResult();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
	 */
	@SuppressWarnings("finally")
	public WorkflowResult execute(DigitalObject dgoA) {

		// document all general actions for this digital object
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				dgoA.getPermanentUri(),
				WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
				System.currentTimeMillis(),
				this.getWorkflowReportingLogger());
		this.addWFResultItem(wfResultItem);
		wfResultItem.addLogInfo("working on workflow template: " + this.getClass().getName());
		wfResultItem.addLogInfo("workflow-instance id: " + this.getWorklowInstanceID());

		// start executing on digital ObjectA
		this.processingDigo = dgoA;

		try {
            // Identification service for data enrichment (e.g. mime type of output object)
            String[] types = runIdentification(dgoA);
            wfResultItem.addLogInfo("Completed identification. result" + Arrays.asList(types).toString());

            // Extract metadata - will otherwise get lost between steps!
            String metadata = "";
            List<Metadata> mList = dgoA.getMetadata();
            if ((mList != null) && (mList.size() > 0)) {
                metadata = mList.get(0).getContent();
            }

            if (metadata == null) {
            	wfResultItem.addLogInfo("No metadata contained in DigitalObject!");
            } else {
            	wfResultItem.addLogInfo("Extracted metadata: " + metadata);
            }
            	
			// Insert in JCR repository
            wfResultItem.addLogInfo("STEP 2: Insert in JCR repository. initial digital object: " + dgoA.toString());
      	    // Manage the Digital Object Data Registry:
            wfResultItem.addLogInfo("Initialize JCR repository instance.");
            JcrDigitalObjectManagerImpl dodm = 
            	 (JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
      	    DigitalObject dgoB = dodm.store(IDENITFY_PATH, dgoA, true);
         	wfResultItem.addLogInfo("Completed storing in JCR repository: " + dgoB.toString());
            
         	// Enrich digital object with format information from identification service
         	if (types != null) {
         		wfResultItem.addLogInfo("Identified formats count: " + types.length);
				for (int i=0; i<types.length; i++) {
					wfResultItem.addLogInfo("type[" + i + "]: " + types[i]);
				}			

				if (types[0] != null) {
	    			Event eIdentifyFormat = buildEvent(URI.create(types[0].toString()), IDENTIFY_EVENT);
					dgoB = addEvent(dgoB, eIdentifyFormat, URI.create(types[0]), false);
	         	}
         	}

			// Update digital object in JCR repository
            wfResultItem.addLogInfo("STEP 3: Update digital object in JCR repository. initial digital object: " + 
            		dgoB.toString());
         	dgoB = dodm.updateDigitalObject(dgoB, false);
         	wfResultItem.addLogInfo("Completed update in JCR repository. result digital object: " + dgoB.toString());


			
			// Migration service
			DigitalObject dgoC = null;
        	wfResultItem.addLogInfo("STEP 4: Starting migration");
        	wfResultItem.addLogInfo("processingDigo.getPermanentUri(): " + processingDigo.getPermanentUri() + 
        			", dgoB.getPermanentUri(): " + dgoB.getPermanentUri());
        	
		    DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
		    URI baseUri = DataRegistryFactory.createDataRegistryIdFromName(DOJCRConstants.REGISTRY_NAME);
			wfResultItem.addLogInfo("base URI: " + baseUri);
			URI resUri = URI.create(baseUri.toString() + dgoB.getPermanentUri().toString());
			wfResultItem.addLogInfo("resUri: " + resUri);

			URI dgoCRef = runMigration(migrate, resUri, true);
			wfResultItem.addLogInfo("Completed migration. URI: " + dgoCRef);
			
			// Add migration resulting text to metadata
			if (dgoCRef != null) {
				try {
					baseUri = new PDURI(dgoCRef.normalize()).formDataRegistryRootURI();
					wfResultItem.addLogInfo("migration base URI: " + baseUri);
			
					dgoC = dataRegistry.getDigitalObjectManager(baseUri).retrieve(dgoCRef);	
					wfResultItem.addLogInfo("dgoC: " + dgoC.toString());
					if (dgoC != null && dgoB.getPermanentUri() != null) {
		    			Event eMigration = buildEvent(dgoB.getPermanentUri(), MIGRATE_EVENT);
						dgoC = addEvent(dgoC, eMigration, null, true);
		         	}

				} catch (Exception e) {
					wfResultItem.addLogInfo("migration error: " + e.getMessage());
				}
			}
			
			// Insert in JCR repository
            wfResultItem.addLogInfo("STEP 5: Insert in JCR repository. initial digital object: " + dgoC.toString());
      	    dgoC = dodm.store(MIGRATE_PATH, dgoC, true);
         	wfResultItem.addLogInfo("Completed storing in JCR repository: " + dgoC.toString());
         	                                 
            wfResultItem.setEndTime(System.currentTimeMillis());

			wfResultItem
				.addLogInfo("Successfully completed workflow for digitalObject with permanent uri:"
						+ processingDigo);
			wfResultItem.setEndTime(System.currentTimeMillis());

		} catch (Exception e) {
			String err = "workflow execution error for digitalObject with permanent uri: " + processingDigo;
			wfResultItem.addLogInfo(err + " " + e);
			wfResultItem.setEndTime(System.currentTimeMillis());
		}
		
		return this.getWFResult();
	}
	
	
	/**
	 * Create an event.
	 * @return The created event
	 */
	public Event buildEvent(URI ref, String eventType){
		List<Property> pList = new ArrayList<Property>();
		Property pIdentificationContent = new Property.Builder(URI.create(INITIAL_REF_EVENT_TYPE))
        	.name("Reference to the initial document")
        	.value(ref.toString())
        	.description("This is a link to original document")
        	.unit("URI")
        	.type("digital object migration")
        	.build();
		pList.add(pIdentificationContent);
		Event eIdentifyFormat = new Event(
				eventType, System.currentTimeMillis() + "", new Double(100), 
				new Agent("JCR Repository v1.0", "The Planets Jackrabbit Content Repository", "planets://data/repository"), 
				pList);
		return eIdentifyFormat;
	}

	
	/**
	 * This method changes the content value in digital object and returns changed
	 * digital object with new content value. 
	 * 
	 * @param digitalObject
	 *        This is a digital object to be updated
	 * @param newContent
	 *        This is a new digital object content
	 * @param identifiedFormat
	 *        This is a format identified by identification service
	 * @return changed digital object with new content value
	 */
	public static DigitalObject addEvent(DigitalObject digitalObject, Event newEvent, URI identifiedFormat, boolean cleanup)
    {
		DigitalObject res = null;
		
    	if (digitalObject != null && newEvent != null)
    	{
	    	DigitalObject.Builder b = new DigitalObject.Builder(digitalObject.getContent());
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (digitalObject.getPermanentUri() != null) b.permanentUri(digitalObject.getPermanentUri());
		    if (identifiedFormat != null) {
		    	b.format(identifiedFormat);
		    } else {
		    	if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    }
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getMetadata() != null) 
		    	b.metadata((Metadata[]) digitalObject.getMetadata().toArray(new Metadata[0]));
		    if (digitalObject.getEvents() != null)
		    {
				List<Event> eventList = digitalObject.getEvents();
				if (cleanup) {
					eventList.clear();
				}
				eventList.add(newEvent);
		    	b.events((Event[]) eventList.toArray(new Event[0]));
		    }
            res = b.build();
    	}
		return res;
	}
	

	/**
	 * This method changes the metadata list value in digital object and returns changed
	 * digital object with new metadata list value. 
	 * 
	 * @param digitalObject
	 *        This is a digital object to be updated
	 * @param newMetadata
	 *        This is a new digital object metadata object
	 * @return changed digital object with new metadata list value
	 */
	public static DigitalObject addMetadata(DigitalObject digitalObject, Metadata newMetadata)
    {
		DigitalObject res = null;
		
    	if (digitalObject != null && newMetadata != null)
    	{
	    	DigitalObject.Builder b = new DigitalObject.Builder(digitalObject.getContent());
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (digitalObject.getPermanentUri() != null) b.permanentUri(digitalObject.getPermanentUri());
		    if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getEvents() != null) 
		    	b.events((Event[]) digitalObject.getEvents().toArray(new Event[0]));
		    if (digitalObject.getMetadata() != null)
		    {
				List<Metadata> metadataList = digitalObject.getMetadata();
				metadataList.add(newMetadata);
		    	b.metadata((Metadata[]) metadataList.toArray(new Metadata[0]));
		    }
            res = b.build();
    	}
		return res;
	}
	

	/** {@inheritDoc} */
	public WorkflowResult finalizeExecution() {
		this.getWFResult().setEndTime(System.currentTimeMillis());
		LogReferenceCreatorWrapper.createLogReferences(this);
		return this.getWFResult();
	}

	/**
	 * Runs the migration service on a given digital object reference. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the specified repository
	 */
	private URI runMigration(Migrate migrationService,
			URI digORef, boolean endOfRoundtripp) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo.getPermanentUri(), 
				migrationService, 
				digORef, 
				DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/executions/"),
				endOfRoundtripp);
		
		return migrWrapper.runMigration();

	}

	
	/**
     * This method runs the identification service on a given digital object and returns an
	 * Array of identified id's (for Droid e.g. PronomIDs)
	 * 
	 * @param DigitalObject
	 *            the data
	 * @return
	 * @throws Exception
     */
    private String[] runIdentification(DigitalObject digo) throws Exception {        
        //an object used to document the results of a service call for the WorkflowResult
        //document the service type and start-time
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("STEP 1: Identification");
        
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
        wfResultItem.setInputDigitalObjectRef(digo.getPermanentUri());
        wfResultItem.setServiceParameters(parameterList);
        wfResultItem.setServiceEndpoint(identify.describe().getEndpoint());
        
        //have a look at the service's results
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        //report service status and type
        wfResultItem.setServiceReport(report);

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
            //document the result
            wfResultItem.addExtractedInformation(strings[count]);
            count++;
        }
        return strings;
    }


}
