package eu.planets_project.ifr.core.wee.api.workflow.jobwrappers;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowContext;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * A migration workflow wrapper that takes care of the most common migration
 * behavior
 * <ul>
 * <li>logging proper WorkflowResult statements</li>
 * <li>persisting digital objects in the default data registry and returning objects as shared data registry URIs</li>
 * <li>creating default events</li>
 * </ul>
 * 
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 09.12.2009
 * 
 */
public class MigrationWFWrapper {

	private URI pedigreeDigoRef;
	private Migrate migrationService;
	private URI digOToMigrateRef;
	private boolean endOfRoundtripp;
	private WorkflowTemplate wfi;
	private URI inputFormat;
	private URI outputFormat;
	private URI dataRepositoryID;
	private DataRegistry dataRegistry;
	private static FormatRegistry fr;

	/**
	 * The default constructor.
	 * 
	 * @param processingTemplate:
	 *            The template that's calling the wrapper
	 * @param pedigreeDigo:
	 *            The migration processes pedigree's root element a migration
	 *            roundtripp was started from.
	 * @param migrationService:
	 *            The migration service that's being called
	 * @param digOToMigrate:
	 *            The digitalObject that's being migrated
	 * @param dataRepositoryID:
	 *            a 'planets://' scheme based repository identifier for storing the created migration results
	 *            if null - the default repository will be used
	 * @param endOfRoundtripp:
	 *            an indicator triggering the 'finalMigrationResult' flag in the
	 *            logs
	 */
	public MigrationWFWrapper(WorkflowTemplate processingTemplate,
			URI pedigreeDigoRef, Migrate migrationService,
			URI digOToMigrateRef, URI dataRepositoryID, boolean endOfRoundtripp) {

		this.wfi = processingTemplate;
		this.pedigreeDigoRef = pedigreeDigoRef;
		this.migrationService = migrationService;
		this.digOToMigrateRef = digOToMigrateRef;
		this.endOfRoundtripp = endOfRoundtripp;
		this.dataRepositoryID = dataRepositoryID;
		this.dataRegistry = DataRegistryFactory.getDataRegistry();
		this.fr = FormatRegistryFactory.getFormatRegistry();
	}
	
	/**
	 * Calls the default constructor with using the default data registry manager
	 * for storing migration outputs
	 * 
	 * @see #MigrationWFWrapper(DigitalObject, Migrate, DigitalObject, boolean)
	 */
	public MigrationWFWrapper(WorkflowTemplate processingTemplate,
			URI pedigreeDigoRef, Migrate migrationService,
			URI digOToMigrateRef, boolean endOfRoundtripp) {
		this(processingTemplate, pedigreeDigoRef, migrationService, digOToMigrateRef,
				null, endOfRoundtripp);
	}
	
	/**
	 * Calls the default constructor; using the default data registry manager
	 * for storing migration outputs; and stating endOfRoundtripp=false
	 * 
	 * @see #MigrationWFWrapper(DigitalObject, Migrate, DigitalObject, boolean)
	 */
	public MigrationWFWrapper(WorkflowTemplate processingTemplate,
			URI pedigreeDigoRef, Migrate migrationService,
			URI digOToMigrateRef) {
		this(processingTemplate, pedigreeDigoRef, migrationService, digOToMigrateRef,null,
				false);
	}

	/**
	 * Calls the default constructor with endOfRoundtripp=false and without
	 * pointing to a pedigreeDigo. The lack of a pedigree object may lead to
	 * cumbersome logs.
	 * 
	 * @see #MigrationWFWrapper(DigitalObject, Migrate, DigitalObject, boolean)
	 */
	public MigrationWFWrapper(WorkflowTemplate processingTemplate,
			Migrate migrationService, URI digOToMigrateRef) {
		this(processingTemplate, null, migrationService, digOToMigrateRef, false);
	}
	
	/**
	 * Allows to define an input format that overrides the xml config defaults
	 * @param inputFormat
	 */
	public void setInputFormat(URI inputFormat){
		this.inputFormat = inputFormat;
	}
	
	/**
	 * Allows to define an output format that overrides the xml config defaults
	 * @param outputFormat
	 */
	public void setOutputFormat(URI outputFormat){
		this.outputFormat = outputFormat;
	}
	
	
	/**
	 * Allows specifying a 'planets://' scheme based repository identifier for 
	 * storing the created migration results
	 * @param repositoryID: if null - the default repository will be used
	 */
	public void setDataRepository(URI repositoryID){
		this.dataRepositoryID = repositoryID;
	}

	/**
	 * @return an URI to the resulting digital object's data registry reference
	 * @throws Exception
	 */
	public URI runMigration() throws Exception {

		// an object used for documenting the results of a service action
		// document the service type and start-time
		WorkflowResultItem wfResultItem;
		if (endOfRoundtripp) {
			wfResultItem = new WorkflowResultItem(pedigreeDigoRef,
					WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION, System
							.currentTimeMillis(), wfi
							.getWorkflowReportingLogger());
		} else {
			wfResultItem = new WorkflowResultItem(pedigreeDigoRef,
					WorkflowResultItem.SERVICE_ACTION_MIGRATION, System
							.currentTimeMillis(), wfi
							.getWorkflowReportingLogger());
		}
		wfi.addWFResultItem(wfResultItem);

		try {
			// get all parameters that were added in the configuration file
			List<Parameter> parameterList;
			if (wfi.getServiceCallConfigs(migrationService) != null) {
				parameterList = wfi.getServiceCallConfigs(migrationService)
						.getAllPropertiesAsParameters();
			} else {
				parameterList = new ArrayList<Parameter>();
			}
			wfResultItem.setServiceParameters(parameterList);

			// get from config: migrate_to_fmt for this service
			URI migrateToURI, migrateFromURI;
			if(this.outputFormat!=null){
				//e.g. when using a identification prior to chose the output format
				migrateToURI = this.outputFormat;
			}
			else{
				//get the ones from the ServiceCallConfigs
				migrateToURI = wfi.getServiceCallConfigs(migrationService)
				.getPropertyAsURI(WorkflowTemplate.SER_PARAM_MIGRATE_TO);
			}
			wfResultItem.addLogInfo("set migrate to: " + migrateToURI);
			
			if(this.inputFormat!=null){
				//e.g. when using a identification prior to chose the input format
				migrateFromURI = this.inputFormat;
			}
			else{
				//get the ones from the ServiceCallConfigs
				migrateFromURI = wfi.getServiceCallConfigs(migrationService)
				.getPropertyAsURI(WorkflowTemplate.SER_PARAM_MIGRATE_FROM);
			}
			wfResultItem.addLogInfo("set migrate from: " + migrateFromURI);

			if ((migrateToURI == null) && (migrateFromURI == null)) {
				String err = "No parameter for: 'migrate_to/from_filetype' specified";
				wfResultItem.addLogInfo(err);
				throw new Exception(err);
			}

			// document
			wfResultItem.setInputDigitalObjectRef(digOToMigrateRef);
			wfResultItem.setServiceParameters(parameterList);
			wfResultItem.setStartTime(System.currentTimeMillis());
			// document the endpoint if available - retrieve from
			// WorkflowContext
			String endpoint = wfi.getWorkflowContext().getContextObject(
					migrationService, WorkflowContext.Property_ServiceEndpoint,
					java.lang.String.class);
			if (endpoint != null) {
				wfResultItem.setServiceEndpoint(new URL(endpoint));
			}
			ServiceDescription serDescr = migrationService.describe();
			wfResultItem.setServiceDescription(serDescr);

			//retrieve the actual digital object
			DigitalObject digoToMigrate = dataRegistry.retrieve(digOToMigrateRef);
			
			// now call the migration
			MigrateResult migrateResult = migrationService.migrate(
					digoToMigrate, migrateFromURI, migrateToURI, parameterList);

			// document
			wfResultItem.setEndTime(System.currentTimeMillis());
			ServiceReport report = migrateResult.getReport();
			// report service status and type
			wfResultItem.setServiceReport(report);
			if (report.getType() == Type.ERROR) {
				String s = "Service execution failed: " + report.getMessage();
				wfResultItem.addLogInfo(s);
				throw new Exception(s);
			}

			wfResultItem.addLogInfo("storing Digo in JCR repository");
			DigitalObject migOutput = migrateResult.getDigitalObject();

			// add Migration Event to DigitalObject
			Event migrEvent = buildMigrationOutputEvent(digoToMigrate,
					parameterList, wfResultItem.getStartTime(), wfResultItem
							.getDuration(),serDescr,endpoint);

			List<Event> lEvents = migOutput.getEvents();
			lEvents.add(migrEvent);

			// create an updated DigitalObject containing the Migration-Event
			// note, as the FileSystemDigoManager requires a title != null, we'll use a random one here
			String title = (migOutput.getTitle()==null) ? UUID.randomUUID()+"" : migOutput.getTitle();
			URI suggStorageURI = helperCreateDOMURIWithFileExtension(wfi.getWorklowInstanceID(),digOToMigrateRef,dataRepositoryID,migrateToURI);
			DigitalObject digoUpdated = new DigitalObject.Builder(migOutput
					.getContent()).title(title).permanentUri(
					migOutput.getPermanentUri()).manifestationOf(
					migOutput.getManifestationOf()).format(
					migOutput.getFormat()).metadata(
					(Metadata[]) migOutput.getMetadata().toArray(
							new Metadata[0])).events(
					(Event[]) lEvents.toArray(new Event[0])).build();

			
			// decide in which repository to store the received DigitalObject
			URI digoRef;
			if(this.dataRepositoryID!=null){
				digoRef = wfi.storeDigitalObjectInRepository(suggStorageURI, digoUpdated, dataRepositoryID);
			}
			else{
				//in this case use the default data registry manager location for persisting
				digoRef = wfi.storeDigitalObject(digoUpdated);
			}
			
			wfResultItem.addLogInfo("got digital object with permanent URI: "
					+ digoRef);
			wfResultItem.setOutputDigitalObjectRef(digoRef);
			wfResultItem.addLogInfo("migration completed");

			return digoRef;

		} catch (Exception e) {
			wfResultItem.addLogInfo("migration failed " + e);
			throw e;
		}
	}

	/**
	 * Creates an Event for the outputDigitalObject that records properties on
	 * its predecessor and on the duration the migration lasted.
	 * 
	 * @param migrInput
	 * @param migrOutput
	 * @param migrationDuration
	 * @return
	 */
	private Event buildMigrationOutputEvent(DigitalObject migrInput,
			List<Parameter> migrParameters, long startTime, long duration,
			ServiceDescription serDescr, String serEndpoint) {

		List<Property> propList = new ArrayList<Property>();
		Property pMigrDuration = new Property.Builder(URI
				.create("planets://service/migration/performance/duration"))
				.name("duration of migration")
				.value(duration + "")
				.description(
						"Measurement of the Migration.Action taken on the batch processor's end")
				.unit("time in millis").type("service characteristic").build();
		Property pSerParams = new Property.Builder(URI
				.create("planets://service/migration/configuration"))
				.name("service configuration")
				.value(migrParameters.toString())
				.description(
						"Record of the specific parameter configuration that were applied")
				.type("service characteristic").build();
		Property pOldContentLink = new Property.Builder(URI
				.create("planets://data/predecessor"))
				.name("prececessor reference")
				.value(migrInput.getPermanentUri() + "")
				.description(
						"A reference to the predecessor object the migration result was derived from.")
				.type("directional pointer").build();
		
		if(serDescr!=null && serEndpoint!=null){
			Property serDesc = new Property.Builder(URI
					.create("planets://service/migration/description"))
					.name("service description")
					.value("endpoint: "+serEndpoint+" service description: "+serDescr.toString())
					.description(
							"Information about the tool and the service that has been called")
					.type("service characteristic").build();
			propList.add(serDesc);
		}

		propList.add(pMigrDuration);
		propList.add(pSerParams);
		propList.add(pOldContentLink);

		Event migrateEvent = new Event("planets://action/service/migrate",
				startTime + "", new Long(duration).doubleValue(), wfi
						.getWEEAgent(), propList);
		return migrateEvent;

	}
	
    /**
     * Returns a repository URI which can be used for persisting this object under
     * a self-specified name.
     * No actual checking that the document format at hand really corresponds to the
     * given file extension we're setting
     * @param title
     * @param domURI the DigitalObjectManager to use 
     * @param formatID the migration's expected output format: e.g. planets:fmt/ext/jpeg
     * @return
     */
    private URI helperCreateDOMURIWithFileExtension(UUID workflowInstanceID, URI originatorURI, URI domUri, URI formatID){
        
    	String uriPath;
    	String name = null,extension = null;
        try {
	    	//1a. first try to extract some naming information from the originator
	    	if(originatorURI!=null){
	    		String path = originatorURI.getPath();
				int iSlash = path.lastIndexOf("/");
				if(iSlash!=-1){
					name = path.substring(iSlash+1,path.length());
				}else{
					name = path;
				}
				//then try to assign an extension
		    	if(formatID==null){
		     	   //FIXME: in case that formatID == null we should have a statically configured service as droid in place to extract this information
		     	  throw new Exception("no format ID specified");
		        }
		        extension = fr.getFirstExtension(formatID);
		        if(extension!=null){
		        	name+="."+extension;
		        }
	    	}
	    	//1b. or assign a random one if no originator information is available
	    	if((originatorURI==null)||(extension==null)){
	    		name = UUID.randomUUID()+"";
	    	}
	        
	        //finally build up the intended storage URI using the workflowID as folder
	    	if(workflowInstanceID!=null){
	    		uriPath = "/"+workflowInstanceID+"/"+name;
	    	}
	    	else{
	    		//in this case use some random path
	    		uriPath = "/"+UUID.randomUUID()+"/"+name;
	    	}
        
			return new URI(domUri.getScheme(),domUri.getAuthority(),domUri.getPath()+uriPath,null,null).normalize();
		
        } catch (Exception e) {
			return null;
		}
     }

}
