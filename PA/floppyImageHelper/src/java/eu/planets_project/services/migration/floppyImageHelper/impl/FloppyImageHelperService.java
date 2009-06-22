package eu.planets_project.services.migration.floppyImageHelper.impl;

import java.net.URI;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migration.floppyImageHelper.api.FloppyImageHelper;
import eu.planets_project.services.migration.floppyImageHelper.api.FloppyImageHelperFactory;

/**
 * @author Peter Melms
 *
 */
@Stateless()
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = FloppyImageHelperService.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class FloppyImageHelperService implements Migrate {
	public static final String NAME = "FloppyImageHelperService";
	private static FloppyImageHelper service = null;

	public FloppyImageHelperService() {
		service = FloppyImageHelperFactory.getFloppyImageHelperInstance();
	}
	
	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, List<Parameter> parameters) {
		return service.migrate(digitalObject, inputFormat, outputFormat, parameters);
	}

	public ServiceDescription describe() {
		return service.describe();
	}
	
	
}
