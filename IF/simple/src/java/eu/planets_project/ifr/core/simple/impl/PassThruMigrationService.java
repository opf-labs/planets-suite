package eu.planets_project.ifr.core.simple.impl;

import java.io.Serializable;
import java.net.URI;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * PassThruMigrationService testing service. This service does nothing except to
 * implement the Migrate interface to allow real-world testing
 * of digital objects.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Local(Migrate.class)
@Remote(Migrate.class)
@Stateless

@WebService(name = PassThruMigrationService.NAME, 
        serviceName = Migrate.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate" )
        
public final class PassThruMigrationService implements Migrate,
        Serializable {
    /** The service name */
    static final String NAME = "PassThruMigrationService";
    
    /** The unique class id */
    private static final long serialVersionUID = 2127494848765937613L;

    /**
     * {@inheritDoc}
     * 
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
     */
    public MigrateResult migrate( final DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, Parameters parameters) {
        /*
         * We just return a new digital object with the same required arguments
         * as the given:
         */
        DigitalObject newDO = new DigitalObject.Builder(digitalObject).build();
        ServiceReport report = new ServiceReport();
        return new MigrateResult(newDO, report);
    }

    
    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Migrate.class.getCanonicalName());
        mds.description("A pass-thru test service, that simply clones and passes data through unchanged.");
        mds.author("Fabian Steeg <fabian.steeg@uni-koeln.de>, Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
    }

}
