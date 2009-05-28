package eu.planets_project.ifr.core.security.impl.services;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.security.api.model.Role;
import eu.planets_project.ifr.core.security.api.services.RoleManager;

/**
 * Class to manage Planets IF security Roles.
 * @author CFWilson
 *
 */
@Stateless(mappedName="security/LocalRoleManager")
@Local(RoleManager.class)
@Remote(RoleManager.class)
@LocalBinding(jndiBinding="planets-project.eu/RoleManager/local")
@RemoteBinding(jndiBinding="planets-project.eu/RoleManager/remote")
@SecurityDomain("PlanetsRealm")
public class RoleManagerImpl implements RoleManager {
	private static Log log = LogFactory.getLog(RoleManagerImpl.class);

	/**
	 * This is the JPA entity manager declaration.
	 */
	@PersistenceContext(unitName="planetsAdmin", type=PersistenceContextType.TRANSACTION)

	private EntityManager manager;
	/**
	 * This hooks into the EJB Context.
	 */
	@Resource SessionContext ctx;

	/**
	 * 
	 * @param name the name of the Role to be retrieved
	 * @return the specific role object
	 * @throws RoleNotFoundException
	 */
	public Role getRoleByName(String name) throws RoleNotFoundException{
		log.info("creating query");
		Query query = manager.createQuery("SELECT r FROM RoleImpl AS r WHERE r.name =:name");
		log.info("setting parameter name to " + name);
		query.setParameter("name", name);
		log.info("query.getsingleresult()");
		Role role = null;
		try {
			log.info("In catch block to retrieve user");
			role = (Role)query.getSingleResult();
		} catch (NoResultException e) {
			// Catch the runtime exception and set user to null
			log.info("Role doesn't exist so setting to null");
			role = null;
		}
		log.info("testing for role == null");
		if( role == null ) throw new RoleNotFoundException();
		log.info("Role name = " + role.getName());
		return role;
	}

	/**
	 * 
	 * @return a List contain all IF Role objects 
	 */
	@PermitAll
	@SuppressWarnings("unchecked")
	public List<Role> getAllRoles() {
		Query query = manager.createQuery("from RoleImpl");
		List<Role> roles = (List<Role>) query.getResultList();
		return roles;
	}

	/**
	 * Hook up to an instance of the Planets User Manager.
	 * @return A UserManager, as discovered via JNDI.
	 */
	public static RoleManager getPlanetsRoleManager() {
		try{
			Context jndiContext = new javax.naming.InitialContext();
			RoleManager rm = (RoleManager) PortableRemoteObject.narrow(
					jndiContext.lookup("planets-project.eu/RoleManager/remote"), RoleManager.class);
			return rm;
		}catch (NamingException e) {
			log.error("Failure during lookup of the UserManager PortableRemoteObject: "+e.toString());
			return null;
		}
	}

}
