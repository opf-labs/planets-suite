/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;
//import org.jboss.ws.core.server.ServiceEndpointManager;
//import org.jboss.ws.core.server.ServiceEndpointManagerFactory;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.wee.api.WeeManager;
import eu.planets_project.ifr.core.wee.api.WorkflowExecutionEngine;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Stateful(mappedName="wee/LocalWeeManager")
@Local(WeeManager.class)
@Remote(WeeManager.class)
@LocalBinding(jndiBinding="planets-project.eu/WeeManager/local")
@RemoteBinding(jndiBinding="planets-project.eu/WeeManager/remote")
@SecurityDomain("PlanetsRealm")
public class WeeManagerImpl implements WeeManager {
    private static final Log log = PlanetsLogger.getLogger(WeeManagerImpl.class);
    
    // The WorkflowExecutionEngine
    //WorkflowExecutionEngine wee = null;
    
    /**
     * When the EJB is started up, create a WEE and run it in the background.
     */
    public WeeManagerImpl() {
        // Start the WEE:
//        wee = new WorkflowExecutionEngine();
//        wee.start();
        // Test programmatic web service deployment.
        //  Caused by: java.lang.IllegalStateException: Cannot publish endpoint from within server
        //Endpoint.publish("http://localhost:8080/wee/TestWSdeploy", new SimpleCharacterisationService() );
        
        // Service managements?
//        ServiceEndpointManagerFactory semFactory = ServiceEndpointManagerFactory.getInstance();
//        ServiceEndpointManager epManager = semFactory.getServiceEndpointManager();
//        epManager.createServiceEndpoint(seInfo);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.wee.api.WeeManager#getWee()
     */
    public WorkflowExecutionEngine getWee() {
        return null;
        //        return wee;
    }

    /**
     * Hook up to an instance of the Planets WEE Manager.
     * @return A WeeManager, as discovered via JNDI.
     */
    public static WeeManager getPlanetsWeeManager() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            WeeManager wee = (WeeManager) PortableRemoteObject.narrow(
                    jndiContext.lookup("planets-project.eu/WeeManager/remote"), WeeManager.class);
            return wee;
        }catch (NamingException e) {
            log.error("Failure during lookup of the WeeManager PortableRemoteObject: "+e.toString());
            return null;
        }
    }
    
}
