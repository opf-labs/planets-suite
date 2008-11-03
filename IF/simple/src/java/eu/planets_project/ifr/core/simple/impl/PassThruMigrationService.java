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
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceDescription;
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
    /***/
    static final String NAME = "PassThruMigrationService";
    
    /***/
    private static final long serialVersionUID = 2127494848765937613L;

    /**
     * {@inheritDoc}
     * 
     * @see eu.planets_project.ifr.core.common.services.migrate.MigrateOneDigitalObject#migrate(eu.planets_project.ifr.core.common.services.datatypes.DigitalObject)
     */
    public MigrateResult migrate( final DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, Parameters parameters) {
        /*
         * We just return a new digital object with the same required arguments
         * as the given:
         */
        DigitalObject newDO = DigitalObject.of(digitalObject.toXml());
        ServiceReport report = new ServiceReport();
        return new MigrateResult(newDO, report);
    }

    
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.common.services.migrate.MigrateOneDigitalObject#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription mds = new ServiceDescription("Pass-thru non-migration migration service.", "");
        mds.setDescription("A test service, that simply passes data through.");
        mds.setAuthor("Fabian Steeg <fabian.steeg@uni-koeln.de>, Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.setClassname(this.getClass().getCanonicalName());
        mds.setType(Migrate.class.getCanonicalName());
        return mds;
    }

}
