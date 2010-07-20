package eu.planets_project.services.migration.floppyImageHelper.api;

import java.net.URI;
import java.util.List;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.MigrateResult;

public interface FloppyImageHelper {

	/**
	 * @see eu.planets_project.services.migrate.Migrate#describe()
	 */
	public abstract ServiceDescription describe();

	/* (non-Javadoc)
	 * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	public abstract MigrateResult migrate(DigitalObject digitalObject,
			URI inputFormat, URI outputFormat, List<Parameter> parameters);
	
}