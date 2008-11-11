package eu.planets_project.ifr.core.services.migration.generic.impl;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;

@Stateless
@Remote(BasicMigrateOneBinary.class)
@RemoteBinding(jndiBinding="planets-project.eu/GenericBasicMigrationServiceRemote")

@WebService( name = GenericBasicMigration.NAME, 
        serviceName = BasicMigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.BasicMigrateOneBinary")
public class GenericBasicMigration implements BasicMigrateOneBinary, Serializable
{
    /** */
    private static final long serialVersionUID = -2186431821310098736L;

    /** */
    public static final String NAME = "GenericBasicMigration";

    /**
     * 
     * @param binary
     * @return
     */
	public byte[] basicMigrateOneBinary(
	        byte[] binary)
	{
		return "Hello, World!".getBytes();
	}
}
