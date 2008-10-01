package eu.planets_project.ifr.core.simple.impl;

import java.io.Serializable;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.DigitalObject;
import eu.planets_project.ifr.core.common.services.migrate.MigrateOneDigitalObject;

/**
 * DigitalObjectMigration testing service. This service does nothing except to
 * implement the MigrateOneDigitalObject interface to allow real-world testing
 * of digital objects.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = DigitalObjectMigration.NAME, serviceName = MigrateOneDigitalObject.NAME, targetNamespace = PlanetsServices.NS)
@Local(MigrateOneDigitalObject.class)
@Remote(MigrateOneDigitalObject.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class DigitalObjectMigration implements MigrateOneDigitalObject,
        Serializable {
    /***/
    private static final long serialVersionUID = 2127494848765937613L;
    /***/
    static final String NAME = "DigitalObjectMigration";

    /**
     * {@inheritDoc}
     * 
     * @see eu.planets_project.ifr.core.common.services.migrate.MigrateOneDigitalObject#migrate(eu.planets_project.ifr.core.common.services.datatypes.DigitalObject)
     */
    @WebMethod(operationName = MigrateOneDigitalObject.NAME, action = PlanetsServices.NS
            + "/" + MigrateOneDigitalObject.NAME)
    @WebResult(name = MigrateOneDigitalObject.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + MigrateOneDigitalObject.NAME, partName = MigrateOneDigitalObject.NAME
            + "Result")
    public DigitalObject migrate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + MigrateOneDigitalObject.NAME, partName = "digitalObject") final DigitalObject digitalObject) {
        /*
         * We just return a new digital object with the same required arguments
         * as the given:
         */
        DigitalObject newDO = new DigitalObject.Builder(digitalObject
                .getPermanentUrl(), digitalObject.getContent()).build();
        return newDO;
    }

}
