package eu.planets_project.services.migration.dia.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private Log log = LogFactory.getLog(DiaMigrationService.class);

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
	    log
		    .error("Migration failed for object with title '"
			    + digitalObject.getTitle()
			    + "' from input format URI: " + inputFormat
			    + " to output format URI: " + outputFormat, e);
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
	    log.error("Failed getting service description for service: "
		    + this.getClass().getCanonicalName(), e);

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

    // TODO: TSH will kill this chunk when it is time....
    // private Set<URI> getAllowedInputFormatURIs() throws URISyntaxException {
    //
    // final HashSet<URI> inputFormatURIs = new HashSet<URI>();
    //
    // // DIA URI
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/381"));
    //
    // // SVG URIs
    // inputFormatURIs.add(new URI("info:pronom/fmt/91"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/92"));
    //
    // // PNG URIs
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/11"));
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/12"));
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/13"));
    //
    // // DXF URIs
    // inputFormatURIs.add(new URI("info:pronom/fmt/63"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/64"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/65"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/66"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/67"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/68"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/69"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/70"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/71"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/72"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/73"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/74"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/75"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/76"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/77"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/78"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/79"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/80"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/81"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/82"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/83"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/84"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/85"));
    //
    // // TODO: FIG URIs are not provided by PRONOM. Add these when possible.
    //
    // // BMP URIs
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/25"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/114"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/115"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/116"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/117"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/118"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/119"));
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/270"));
    //
    // // GIF URIs
    // inputFormatURIs.add(new URI("info:pronom/fmt/3"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/4"));
    //
    // // TODO: PNM URIs are not provided by PRONOM. Add these when possible.
    //
    // // RAS URI
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/184"));
    //
    // // TIF URIs
    // inputFormatURIs.add(new URI("info:pronom/fmt/7"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/8"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/9"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/10"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/152"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/153"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/154"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/155"));
    // inputFormatURIs.add(new URI("info:pronom/fmt/156"));
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/399"));
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/387"));
    // inputFormatURIs.add(new URI("info:pronom/x-fmt/388"));
    //
    // return inputFormatURIs;
    // }
    //
    // private Set<URI> getAllowedOutputFormatURIs() throws URISyntaxException {
    //
    // final HashSet<URI> outputFormatURIs = new HashSet<URI>();
    //
    // // CGM URI
    // outputFormatURIs.add(new URI("info:pronom/x-fmt/142"));
    //
    // // DIA URI
    // outputFormatURIs.add(new URI("info:pronom/x-fmt/381"));
    //
    // // TODO: SHAPE URI is not provided by PRONOM. Add when possible.
    //
    // // PNG URIs
    // outputFormatURIs.add(new URI("info:pronom/fmt/11"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/12"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/13"));
    //
    // // DXF URIs
    // outputFormatURIs.add(new URI("info:pronom/fmt/63"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/64"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/65"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/66"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/67"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/68"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/69"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/70"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/71"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/72"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/73"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/74"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/75"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/76"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/77"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/78"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/79"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/80"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/81"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/82"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/83"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/84"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/85"));
    //
    // // PLT URI
    // outputFormatURIs.add(new URI("info:pronom/x-fmt/83"));
    //
    // // HPGL URI
    // outputFormatURIs.add(new URI("info:pronom/fmt/293"));
    //
    // // EPS URIs
    // outputFormatURIs.add(new URI("info:pronom/fmt/122"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/123"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/124"));
    //
    // // TODO: EPSI URI is not provided by PRONOM. Add when possible.
    //
    // // SVG URIs
    // outputFormatURIs.add(new URI("info:pronom/fmt/91"));
    // outputFormatURIs.add(new URI("info:pronom/fmt/92"));
    //
    // // SVGZ URI
    // outputFormatURIs.add(new URI("info:pronom/x-fmt/109"));
    //
    // // TODO: MP URI is not provided by PRONOM. Add when possible.
    //
    // // TODO: TEX URI is not provided by PRONOM. Add when possible.
    //
    // // WPG URI
    // outputFormatURIs.add(new URI("info:pronom/x-fmt/395"));
    //
    // // TODO: FIG URI is not provided by PRONOM. Add when possible.
    //
    // // TODO: CODE URI is not provided by PRONOM. Add when possible.
    //
    // return outputFormatURIs;
    // }

}
