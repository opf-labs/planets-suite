package eu.planets_project.ifr.core.services.migration.generic.impl;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;

/**
 * GenericBasicMigration class, implements BasicMigrateOneBinary
 */
@Stateless
@Remote(BasicMigrateOneBinary.class)
@RemoteBinding(jndiBinding="planets-project.eu/GenericBasicMigrationServiceRemote")

@WebService( name = GenericBasicMigration.NAME, 
        serviceName = BasicMigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.BasicMigrateOneBinary")
public class GenericBasicMigration implements BasicMigrateOneBinary, Serializable
{
    /** The unique class ID */
    private static final long serialVersionUID = -2186431821310098736L;

    /** The class name */
    public static final String NAME = "GenericBasicMigration";

    /**
     * 
     * @param binary
     * @return a test set of bytes
     */
	public byte[] basicMigrateOneBinary(
	        byte[] binary)
	{
		return "Hello, World!".getBytes();
	}
}
