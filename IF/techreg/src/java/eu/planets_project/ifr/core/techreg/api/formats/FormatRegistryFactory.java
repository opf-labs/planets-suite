/**
 * 
 */
package eu.planets_project.ifr.core.techreg.api.formats;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.impl.formats.FormatRegistryImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class FormatRegistryFactory {
    private static Log log = LogFactory.getLog(FormatRegistryFactory.class);
    
    /**
     * Hook up to an instance of the Planets User Manager.
     * @return A UserManager, as discovered via JNDI.
     */
    public static FormatRegistry getFormatRegistry() {
        if( System.getProperty("pserv.test.context") != null ) {
            return new FormatRegistryImpl();
        }
        try{
            Context jndiContext = new javax.naming.InitialContext();
            Object ref = jndiContext.lookup("planets-project.eu/FormatRegistry/remote");
            FormatRegistry um = (FormatRegistry) PortableRemoteObject.narrow(
                    ref, FormatRegistry.class);
            return um;
        } catch (NamingException e) {
            log.error("Failure during lookup of the FormatRegistry PortableRemoteObject: "+e.toString());
            return null;
        }
    }
    

}
