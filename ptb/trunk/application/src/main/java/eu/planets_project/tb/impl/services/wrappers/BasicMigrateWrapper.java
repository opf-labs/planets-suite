/**
 * 
 */
package eu.planets_project.tb.impl.services.wrappers;

import java.net.URI;

import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.tb.impl.services.util.PlanetsServiceExplorer;

/**
 * This is a wrapper class that upgrades all supported Migrate service
 * interfaces to the same level.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
@SuppressWarnings("deprecation")
public class BasicMigrateWrapper implements Migrate {
    /** */
    private static final Log log = LogFactory.getLog(BasicMigrateWrapper.class);

    PlanetsServiceExplorer pse = null;
    Service service = null;
    Migrate m = null;
    BasicMigrateOneBinary bmob = null;

    /**
     * Construct based on a service explorer.
     * 
     * @param pse
     */
    public BasicMigrateWrapper(PlanetsServiceExplorer pse) {
        this.pse = pse;
        service = Service.create(pse.getWsdlLocation(), pse.getQName());
        try {
            if (pse.getQName().equals(BasicMigrateOneBinary.QNAME)) {
                bmob = (BasicMigrateOneBinary) service.getPort(pse.getServiceClass());
            } else {
                m = (Migrate) service.getPort(pse.getServiceClass());
            }
        } catch( Exception e ) {
            log.error("Failed to instanciate service "+ pse.getQName() +" at "+pse.getWsdlLocation() + " : Exception - "+e);
            bmob = null;
            m = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        if ( pse.getQName().equals(BasicMigrateOneBinary.QNAME)) {
            ServiceDescription.Builder sd = new ServiceDescription.Builder(
                    pse.getWsdlLocation().getPath(), pse
                            .getServiceClass().getCanonicalName());
            sd.description("This is a basic migration service with no service description or other metadata.");
            sd.author("[unknown]");
            sd.serviceProvider(pse.getWsdlLocation().getAuthority());
            return sd.build();
        } else {
            return m.describe();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project
     * .services.datatypes.DigitalObject, java.net.URI, java.net.URI,
     * eu.planets_project.services.datatypes.Parameters)
     */
    public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, Parameters parameters) {

        // Transform the DO into a single binary, if that is sane
        byte[] binary = null;
        if (digitalObject.getContent() == null
                && digitalObject.getContained() != null) {
            return new MigrateResult(null, ServiceUtils
                    .createErrorReport("This service cannot deal with composite digital objects.") );
        }

        // Invoke the service based on the type (QName):
        if (pse.getQName().equals(BasicMigrateOneBinary.QNAME)) {
            // Basic Migrate One Binary:
            byte[] bresult = bmob.basicMigrateOneBinary(binary);

            DigitalObject ndo = new DigitalObject.Builder(digitalObject
                        .getPermanentUrl(), Content.byValue(bresult)).build();

            return new MigrateResult(ndo, new ServiceReport());

        } else {
            // Migrate:
            return m.migrate(digitalObject, inputFormat, outputFormat,
                    parameters);
        }
    }

}
