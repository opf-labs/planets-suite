package eu.planets_project.services.migration.dia.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
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
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Wrapped Dia migration service implementation.
 * 
 * @author Thomas Skou Hansen (tsh@statsbiblioteket.dk)
 */
@Stateless
@WebService(name = DiaMigrationService.NAME, serviceName = Migrate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.migrate.Migrate")
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public final class DiaMigrationService implements Migrate, Serializable {

    /** The service name to use when publishing this service. **/
    static final String NAME = "DiaMigrationService";

    /**
     * The file name of the static configuration for the generic wrapping
     * framework.
     **/
    private static final String SERVICE_CONFIG_FILE_NAME = "DiaServiceConfiguration.xml";

    /** The file name of the dynamic run-time configuration **/
    private static final String RUN_TIME_CONFIGURATION_FILE_NAME = "pserv-pa-dia";

    /** The unique class id of this migration class **/
    private static final long serialVersionUID = 4596228292063217306L;

    private Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * {@inheritDoc}
     * 
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject,
     *      java.net.URI, java.net.URI,
     *      eu.planets_project.services.datatypes.Parameter)
     */
    public MigrateResult migrate(final DigitalObject digitalObject,
	    URI inputFormat, URI outputFormat, List<Parameter> parameters) {

	try {
	    final DocumentLocator documentLocator = new DocumentLocator(
		    SERVICE_CONFIG_FILE_NAME);

	    final Configuration runtimeConfiguration = ServiceConfig
		    .getConfiguration(RUN_TIME_CONFIGURATION_FILE_NAME);

	    GenericMigrationWrapper genericWrapper = new GenericMigrationWrapper(
		    documentLocator.getDocument(), runtimeConfiguration, this
			    .getClass().getCanonicalName());

	    return genericWrapper.migrate(digitalObject, inputFormat,
		    outputFormat, parameters);

	} catch (Exception exception) {
	    log.log(Level.SEVERE, "Migration failed for object with title '"
		    + digitalObject.getTitle() + "' from input format URI: "
		    + inputFormat + " to output format URI: " + outputFormat,
		    exception);

	    ServiceReport serviceReport = new ServiceReport(Type.ERROR,
		    Status.TOOL_ERROR, exception.toString());

	    return new MigrateResult(null, serviceReport);
	}
    }

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {

	final DocumentLocator documentLocator = new DocumentLocator(
		SERVICE_CONFIG_FILE_NAME);
	try {
	    final Configuration runtimeConfiguration = ServiceConfig
		    .getConfiguration(RUN_TIME_CONFIGURATION_FILE_NAME);

	    GenericMigrationWrapper genericWrapper = new GenericMigrationWrapper(
		    documentLocator.getDocument(), runtimeConfiguration, this
			    .getClass().getCanonicalName());

	    return genericWrapper.describe();

	} catch (Exception e) {
	    log.log(Level.SEVERE,
		    "Failed getting service description for service: "
			    + this.getClass().getCanonicalName(), e);

	    // FIXME! Report failure in a proper way. Should we return a service
	    // description anyway? If so, then how?
	    ServiceDescription.Builder serviceDescriptionBuilder = new ServiceDescription.Builder(
		    NAME, Migrate.class.getCanonicalName());
	    return serviceDescriptionBuilder.build();
	}
    }
}
