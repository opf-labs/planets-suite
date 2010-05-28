/**
 * 
 */
package eu.planets_project.ifr.core.storage.common;

/**
 * This is a starting point for a JCR AccessManager.
 * 
 * It allows role-based access logic, over any JAAS-compliant security layer.
 * 
 * Tested over the WebDAV interface and proxying the authentication down 
 * from the web or EJB layers.  See the expert data browser for an example, and 
 * Data Registry services should use this too (when we have a WS policy).
 * 
 * Taken from:
 * http://wiki.apache.org/jackrabbit/SimpleJbossAccessManager
 */

import java.io.FileInputStream;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;

public class PlanetsJCRAccessManager implements AccessManager {
    private static Logger log = Logger.getLogger(PlanetsJCRAccessManager.class.getName());

    /**
     * Subject whose access rights this AccessManager should reflect
     */
    protected Subject subject;

    /**
     * hierarchy manager used for ACL-based access control model
     */
    protected HierarchyManager hierMgr;

    private boolean initialized;

    protected boolean system;
    protected boolean anonymous;

    /**
     * Empty constructor
     */
    public PlanetsJCRAccessManager() {
        initialized = false;
        anonymous = true;
        system = false;
    }

    //--------------------------------------------------------< AccessManager >
    /**
     * {@inheritDoc}
     */
    public void init(AMContext context)
            throws AccessDeniedException, Exception {
        if (initialized) {
            throw new IllegalStateException("already initialized");
        }

        subject = context.getSubject();
        hierMgr = context.getHierarchyManager();
        Set<Principal> ps = subject.getPrincipals();
        
        
        Properties rolemaps = new Properties();
        String rolemaploc = context.getHomeDir() + "/rolemappings.properties";
        FileInputStream rolefs = new FileInputStream(rolemaploc);
        rolemaps.load(rolefs);
        rolefs.close();
        log.info("Load jbossgroup role mappings from " + rolemaploc);
        
        for (Principal p : ps){
          log.warning(p.getName());
            if (p.getName().equalsIgnoreCase("Roles")){
              log.warning("listing roles: "+p);
              log.warning("listing class: " + p.getClass().toString());
              
              java.security.acl.Group sg = (java.security.acl.Group)p;
              log.warning("listing baseroles: "+sg);
              Enumeration<java.security.Principal> em = 
                  (Enumeration<java.security.Principal>) sg.members();
              while (em.hasMoreElements()) {
                  java.security.Principal myp = em.nextElement();
                  String role = rolemaps.getProperty(myp.getName());
                  log.info("Found role " + myp.getName() + " :: " + role);
                  
                  if (role != null && role.equalsIgnoreCase("full")){
                      system = true;
                      anonymous = false;
                  } else if (role != null && role.equalsIgnoreCase("read")){
                      anonymous = true;
                  }
                }
            }
        }
        


        // @todo check permission to access given workspace based on principals
        initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() throws Exception {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        initialized = false;
    }

    /**
     * {@inheritDoc}
     */
    public void checkPermission(ItemId id, int permissions)
            throws AccessDeniedException, ItemNotFoundException,
            RepositoryException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        if (system) {
            // system has always all permissions
            return;
        } else if (anonymous) {
            // anonymous is always denied WRITE & REMOVE permissions
            if ((permissions & WRITE) == WRITE
                    || (permissions & REMOVE) == REMOVE) {
                throw new AccessDeniedException();
            }
        }else{
            //no permissions
            throw new AccessDeniedException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGranted(ItemId id, int permissions)
            throws ItemNotFoundException, RepositoryException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        if (system) {
            // system has always all permissions
            return true;
        } else if (anonymous) {
            // anonymous is always denied WRITE & REMOVE premissions
            if ((permissions & WRITE) == WRITE
                    || (permissions & REMOVE) == REMOVE) {
                return false;
            }else{
                return true;
            }
        }
        //default to false
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canAccess(String workspaceName)
            throws NoSuchWorkspaceException, RepositoryException {
        
        if (system || anonymous) return true;
        
        return false;
    }
    
    /** 
     * Correct authentication to use correct name and password
     * See https://issues.apache.org/jira/browse/JCR-1584
     * 
     * This code can be used to proxy the web-layer authentication down into the JCR.
     * However, it is not clear how we are going to do this with Web Services,
     * and what our policy will be on anonymous access etc.
     * 
     * For examples of securing web services using HTTP-BASIC-OVER-SSL see:
     * See http://jbws.dyndns.org/mediawiki/index.php?title=Security_and_attachments_sample#The_newspaper_edition_endpoint_2
     * 
     * 
     * {@see eu.planets_project.ifr.core.storage.common.PlanetsJCRAccessManager}
     * 
     * @return
     */
    public static Session loginJCRLocal() {
        Subject alreadyAuthenticated;
        try {
            InitialContext ic = new InitialContext();
            alreadyAuthenticated = (Subject)ic.lookup("java:comp/env/security/subject");
        } catch( NamingException e ) {
            log.severe("Failed while looking up the security Subject. "+e);
            return null;
        }
        
        // Create a doAs class
        PrivilegedAction<Session> pas =  new PrivilegedAction<Session>() { 
            public Session run() {
                try {
                    InitialContext context = new InitialContext();
                    Repository repository = (Repository) context.lookup("java:jcr/local");
                    return repository.login(); 
                } catch( NamingException e ) {
                    log.severe("Failed while looking up the security Subject. "+e);
                    return null;
                } catch( LoginException e ) {
                    log.severe("Caught LoginException "+e);
                    return null;
                } catch( RepositoryException e ) {
                    log.severe("Caught RepositoryException "+e);
                    return null;
                }
            } 
        };
        
        // Try to login:
        Session session = (Session) Subject.doAs( alreadyAuthenticated, pas);
        
        return session;
    }
    
    
}
