/**
 * 
 */
package eu.planets_project.ifr.core.techreg.api.formats;

import eu.planets_project.ifr.core.techreg.impl.formats.FormatRegistryImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.rmi.PortableRemoteObject;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public final class FormatRegistryFactory {
    /** The cached instance. */
    private static FormatRegistry registry = null;

    /** Enforce non-instantiability with a private constructor. */
    private FormatRegistryFactory() {}

    private static Log log = LogFactory.getLog(FormatRegistryFactory.class);

    /**
     * Hook up to an instance of the Planets format registry.
     * @return A format registry, as discovered via JNDI; or a local instance,
     *         if the lookup failed.
     */
    public static FormatRegistry getFormatRegistry() {
        if (registry == null) {
            if (System.getProperty("pserv.test.context") != null) {
                registry = new FormatRegistryImpl();
            } else {
                try {
                    Context jndiContext = new javax.naming.InitialContext();
                    Object ref = jndiContext
                            .lookup("planets-project.eu/FormatRegistry/remote");
                    FormatRegistry um = (FormatRegistry) PortableRemoteObject
                            .narrow(ref, FormatRegistry.class);
                    registry = um;
                } catch ( Exception e ) {
                    log.error("Failure during lookup of the FormatRegistry PortableRemoteObject: "
                                    + e.toString(),e);
                    //e.printStackTrace();
                    
                    /*
                     * We might not be able to retrieve via JNDI, and have not
                     * set the property that is checked above, for instance when
                     * running a JUnit test directly, so here we return a local
                     * instance too:
                     */
                    registry = new FormatRegistryImpl();
                    
                }
            }
        }
        return registry;
    }
}
