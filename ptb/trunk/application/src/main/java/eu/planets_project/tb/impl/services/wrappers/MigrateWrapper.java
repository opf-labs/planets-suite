/**
 * 
 */
package eu.planets_project.tb.impl.services.wrappers;

import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
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
public class MigrateWrapper implements Migrate {
    /** */
    private static final Log log = LogFactory.getLog(MigrateWrapper.class);

    PlanetsServiceExplorer pse = null;
    Service service = null;
    Migrate m = null;
    BasicMigrateOneBinary bmob = null;
    
    /**
     * @param wsdl The WSDL to wrap as a service.
     */
    public MigrateWrapper( URL wsdl ) {
        this.pse = new PlanetsServiceExplorer(wsdl);
        this.init();
    }

    /**
     * @param pse Construct based on a service explorer.
     */
    public MigrateWrapper(PlanetsServiceExplorer pse) {
        this.pse = pse;
        this.init();
    }

    /**
     * 
     */
    private void init() {
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
                            .getServiceClass().getCanonicalName() );
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
            URI outputFormat, List<Parameter> parameters) {

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

            DigitalObject ndo = new DigitalObject.Builder( ImmutableContent.byValue(bresult) )
                .permanentUri( digitalObject.getPermanentUri() ).build();

            return new MigrateResult(ndo, new ServiceReport(Type.INFO, Status.SUCCESS, "OK"));

        } else {
            // Migrate:
            return m.migrate(digitalObject, inputFormat, outputFormat,
                    parameters);
        }
    }

}
