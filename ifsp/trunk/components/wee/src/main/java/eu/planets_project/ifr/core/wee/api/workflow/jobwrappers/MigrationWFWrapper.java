package eu.planets_project.ifr.core.wee.api.workflow.jobwrappers;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowContext;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
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
 * <li>persisting digital objects in jcr and returning objects by reference</li>
 * <li>creating default events</li>
 * </ul>
 * 
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 09.12.2009
 * 
 */
public class MigrationWFWrapper {

	private DigitalObject pedigreeDigo;
	private Migrate migrationService;
	private DigitalObject digOToMigrate;
	private boolean endOfRoundtripp;
	private WorkflowTemplate wfi;

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
	 * @param endOfRoundtripp:
	 *            an indicator triggering the 'finalMigrationResult' flag in the
	 *            logs
	 */
	public MigrationWFWrapper(WorkflowTemplate processingTemplate,
			DigitalObject pedigreeDigo, Migrate migrationService,
			DigitalObject digOToMigrate, boolean endOfRoundtripp) {

		this.wfi = processingTemplate;
		this.pedigreeDigo = pedigreeDigo;
		this.migrationService = migrationService;
		this.digOToMigrate = digOToMigrate;
		this.endOfRoundtripp = endOfRoundtripp;
	}

	/**
	 * Calls the default constructor with endOfRoundtripp=false
	 * 
	 * @see #MigrationWFWrapper(DigitalObject, Migrate, DigitalObject, boolean)
	 */
	public MigrationWFWrapper(WorkflowTemplate processingTemplate,
			DigitalObject pedigreeDigo, Migrate migrationService,
			DigitalObject digOToMigrate) {
		this(processingTemplate, pedigreeDigo, migrationService, digOToMigrate,
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
			Migrate migrationService, DigitalObject digOToMigrate) {
		this(processingTemplate, null, migrationService, digOToMigrate, false);
	}

	public DigitalObject runMigration() throws Exception {

		// an object used for documenting the results of a service action
		// document the service type and start-time
		WorkflowResultItem wfResultItem;
		if (endOfRoundtripp) {
			wfResultItem = new WorkflowResultItem(pedigreeDigo,
					WorkflowResultItem.SERVICE_ACTION_FINAL_MIGRATION, System
							.currentTimeMillis(), wfi
							.getWorkflowReportingLogger());
		} else {
			wfResultItem = new WorkflowResultItem(pedigreeDigo,
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
			URI migrateToURI = wfi.getServiceCallConfigs(migrationService)
					.getPropertyAsURI(WorkflowTemplate.SER_PARAM_MIGRATE_TO);
			wfResultItem.addLogInfo("set migrate to: " + migrateToURI);
			URI migrateFromURI = wfi.getServiceCallConfigs(migrationService)
					.getPropertyAsURI(WorkflowTemplate.SER_PARAM_MIGRATE_FROM);
			wfResultItem.addLogInfo("set migrate from: " + migrateFromURI);

			if ((migrateToURI == null) && (migrateFromURI == null)) {
				String err = "No parameter for: 'migrate_to/from_filetype' specified";
				wfResultItem.addLogInfo(err);
				throw new Exception(err);
			}

			// document
			wfResultItem.setInputDigitalObject(digOToMigrate);
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

			// now call the migration
			MigrateResult migrateResult = migrationService.migrate(
					digOToMigrate, migrateFromURI, migrateToURI, parameterList);

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
			Event migrEvent = buildMigrationOutputEvent(digOToMigrate,
					parameterList, wfResultItem.getStartTime(), wfResultItem
							.getDuration(),serDescr,endpoint);

			List<Event> lEvents = migOutput.getEvents();
			lEvents.add(migrEvent);

			// create an updated DigitalObject containing the Migration-Event
			// note, as the FileSystemDigoManager requires a title != null, we'll use a random one here
			String title = (migOutput.getTitle()==null) ? UUID.randomUUID()+"" : migOutput.getTitle();
			DigitalObject digoUpdated = new DigitalObject.Builder(migOutput
					.getContent()).title(title).permanentUri(
					migOutput.getPermanentUri()).manifestationOf(
					migOutput.getManifestationOf()).format(
					migOutput.getFormat()).metadata(
					(Metadata[]) migOutput.getMetadata().toArray(
							new Metadata[0])).events(
					(Event[]) lEvents.toArray(new Event[0])).build();

			// store the received DigitalObject in the JCR repository
			DigitalObject digoOut = wfi.storeDigitalObjectInJCR(digoUpdated);
			wfResultItem.addLogInfo("jcr: got Digo with permanent URI: "
					+ digoOut.getPermanentUri());
			wfResultItem.setOutputDigitalObject(digoOut);
			wfResultItem.addLogInfo("migration completed");

			return digoOut;

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

}
