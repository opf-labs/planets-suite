package eu.planets_project.services.migration.dia.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.GenericMigrationWrapper;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.utils.DocumentLocator;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * DiaMigrationService testing service.
 * 
 * @author Bolette Ammitzbøll Jurik (bam@statsbiblioteket.dk)
 * @author Thomas Skou Hansen (tsh@statsbiblioteket.dk)
 */
@Local(Migrate.class)
@Remote(Migrate.class)
@Stateless
@WebService(
		name = DiaMigrationService.NAME, 
		serviceName = Migrate.NAME, 
		targetNamespace = PlanetsServices.NS, 
		endpointInterface = "eu.planets_project.services.migrate.Migrate")
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public final class DiaMigrationService implements Migrate, Serializable {

    /** The service name */
    static final String NAME = "DiaMigrationService";

    static final String configfile = "DiaServiceConfiguration.xml";

    /** The unique class id */
    private static final long serialVersionUID = 4596228292063217306L;

    private Logger log = Logger.getLogger(DiaMigrationService.class.getName());

    /**
     * {@inheritDoc}
     * 
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject,
     *      java.net.URI, java.net.URI,
     *      eu.planets_project.services.datatypes.Parameter)
     */
    public MigrateResult migrate(final DigitalObject digitalObject,
	    URI inputFormat, URI outputFormat, List<Parameter> parameters) {

        final DocumentLocator documentLocator =  new DocumentLocator(configfile);

	MigrateResult migrationResult;
	try {
	    // TODO: Is this the correct way to obtain the canonical name? Is it
	    // the correct canonical name?
	    GenericMigrationWrapper genericWrapper = new GenericMigrationWrapper(
		    documentLocator.getDocument(), DiaMigrationService.class
			    .getCanonicalName());

	    migrationResult = genericWrapper.migrate(digitalObject,
		    inputFormat, outputFormat, parameters);

	    // TODO: Some of this exception handling should probably be
	    // performed by the generic wrapper. However, exceptions thrown by
	    // the GenericWrapper constructor must be handled here.
	} catch (Exception e) {
	    log.severe("Migration failed for object with title '"
			    + digitalObject.getTitle()
			    + "' from input format URI: " + inputFormat
			    + " to output format URI: " + outputFormat+": "+e.getMessage());
	    ServiceReport serviceReport = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, e.toString());
	    return new MigrateResult(null, serviceReport); // FIXME! Report failure in a
						  // proper way.
	}

	return migrationResult;
    }

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {

	final DocumentLocator documentLocator = new DocumentLocator(configfile);
	try {
	    // TODO: Is this the correct way to obtain the canonical name? Is it
	    // the correct canonical name?
	    GenericMigrationWrapper genericWrapper = new GenericMigrationWrapper(
		    documentLocator.getDocument(), DiaMigrationService.class
			    .getCanonicalName());

	    return genericWrapper.describe();

	} catch (Exception e) {
	    log.severe("Failed getting service description for service: "
		    + this.getClass().getCanonicalName()+": "+e.getMessage());

	    // FIXME! Report failure in a proper way. Should we return a service
	    // description anyway? If so, then how?
	    ServiceDescription.Builder serviceDescriptionBuilder = new ServiceDescription.Builder(
		    NAME, Migrate.class.getCanonicalName());
	    return serviceDescriptionBuilder.build();
	}

	// TODO: TSH will kill this chunk when it is time....
	// try {
	// ServiceDescription.Builder serviceDescriptionBuilder = new
	// ServiceDescription.Builder(
	// NAME, Migrate.class.getCanonicalName());
	// serviceDescriptionBuilder.classname(this.getClass()
	// .getCanonicalName());
	// serviceDescriptionBuilder
	// .description("File migration service using Dia.");
	// serviceDescriptionBuilder
	// .author("Bolette Ammitzbøll Jurik <bam@statsbiblioteket.dk>, Thomas Skou Hansen <tsh@statsbiblioteket.dk>");
	// serviceDescriptionBuilder.furtherInfo(null);
	// serviceDescriptionBuilder.inputFormats(getAllowedInputFormatURIs()
	// .toArray(new URI[] {}));
	// serviceDescriptionBuilder.paths(MigrationPath.constructPaths(
	// getAllowedInputFormatURIs(), getAllowedOutputFormatURIs())
	// .toArray(new MigrationPath[] {}));
	// // serviceDescriptionBuilder.furtherInfo(null);
	// // serviceDescriptionBuilder.identifier(null);
	//
	// // serviceDescriptionBuilder.inputFormats(null);
	// // serviceDescriptionBuilder.instructions(null);
	// // serviceDescriptionBuilder.name(null);
	// // serviceDescriptionBuilder.parameters(null);
	//
	// // serviceDescriptionBuilder.paths(new
	// //
	// GenericCLIMigrationWrapper(configfile).getMigrationPaths().getAsPlanetsPaths());
	// // serviceDescriptionBuilder.properties(null);
	// // serviceDescriptionBuilder.serviceProvider(null);
	// // serviceDescriptionBuilder.tool(null);
	// // serviceDescriptionBuilder.type(null);
	// // serviceDescriptionBuilder.version(null);
	//
	// return serviceDescriptionBuilder.build();
	// } catch (Exception e) {
	// throw new Error("Failed building migration path information.", e);
	// }
    }

}
