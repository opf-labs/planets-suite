package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
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
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.migrate.Migrate;


/**
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 * @since 21.04.2010
 */
public class MigrateJcrTemplate extends
		WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** URI to use for digital object repository creation. */
	private static final URI PERMANENT_URI_PATH = URI.create("/ait/data/migrate");
	
	private static final String FORMAT_EVENT_TYPE = "JCRupdate";
	private static final String MIGRATE_EVENT = "planets://repository/event/migrate";

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
		       "It implements digiatal object migration, insert in JCR and update in JCR.";
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
			// Migration service
			DigitalObject dgoB = null;
        	wfResultItem.addLogInfo("STEP 1: Starting migration");
			URI dgoBRef = runMigration(migrate, dgoA.getPermanentUri(), true);
			wfResultItem.addLogInfo("Completed migration. URI: " + dgoBRef);
			
			// Add migration resulting text to metadata
			if (dgoBRef != null) {
				try {
				    DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
					URI baseUri = new PDURI(dgoBRef.normalize()).formDataRegistryRootURI();
					wfResultItem.addLogInfo("base URI " + baseUri);
			
					dgoB = dataRegistry.getDigitalObjectManager(baseUri).retrieve(dgoBRef);	
					if (dgoB != null && dgoB.getPermanentUri() != null) {
		    			Event eMigration = buildEvent(dgoB.getPermanentUri());
						dgoB = addEvent(dgoB, eMigration);
		         	}

				} catch (Exception e) {
					wfResultItem.addLogInfo("migration error: " + e.getMessage());
				}
			}
			
			// Insert in JCR repository
            wfResultItem.addLogInfo("STEP 2: Insert in JCR repository. initial digital object: " + dgoB.toString());
      	    // Manage the Digital Object Data Registry:
            wfResultItem.addLogInfo("Initialize JCR repository instance.");
            JcrDigitalObjectManagerImpl dodm = 
            	 (JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
      	    dgoB = dodm.store(PERMANENT_URI_PATH, dgoB, true);
         	wfResultItem.addLogInfo("Completed storing in JCR repository: " + dgoB.toString());
         	                                 
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
	 * Create an identification event.
	 * @return The created event
	 */
	public Event buildEvent(URI format){
		List<Property> pList = new ArrayList<Property>();
		Property pIdentificationContent = new Property.Builder(URI.create(FORMAT_EVENT_TYPE))
        	.name("content by reference")
        	.value(format.toString())
        	.description("This is a link to original document")
        	.unit("URI")
        	.type("digital object migration")
        	.build();
		pList.add(pIdentificationContent);
		Event eIdentifyFormat = new Event(
				MIGRATE_EVENT, System.currentTimeMillis() + "", new Double(100), 
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
	public static DigitalObject addEvent(DigitalObject digitalObject, Event newEvent)
    {
		DigitalObject res = null;
		
    	if (digitalObject != null && newEvent != null)
    	{
	    	DigitalObject.Builder b = new DigitalObject.Builder(digitalObject.getContent());
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (digitalObject.getPermanentUri() != null) b.permanentUri(digitalObject.getPermanentUri());
	    	if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getMetadata() != null) 
		    	b.metadata((Metadata[]) digitalObject.getMetadata().toArray(new Metadata[0]));
		    if (digitalObject.getEvents() != null)
		    {
				List<Event> eventList = digitalObject.getEvents();
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


}
