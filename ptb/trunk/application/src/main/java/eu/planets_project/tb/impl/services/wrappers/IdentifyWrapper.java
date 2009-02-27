/**
 * 
 */
package eu.planets_project.tb.impl.services.wrappers;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Types;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.identify.BasicIdentifyOneBinary;
import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.tb.impl.services.util.PlanetsServiceExplorer;

/**
 * This is a wrapper class that upgrades all supported Identify service
 * interfaces to the same level.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@SuppressWarnings("deprecation")
public class IdentifyWrapper implements Identify {

    /** */
    private static final Log log = LogFactory.getLog(IdentifyWrapper.class);

    PlanetsServiceExplorer pse = null;
    Service service = null;
    Identify i = null;
    BasicIdentifyOneBinary biob = null;
    IdentifyOneBinary iob = null;
    
    /**
     * @param wsdl The WSDL to wrap as a service.
     */
    public IdentifyWrapper( URL wsdl ) {
        this.pse = new PlanetsServiceExplorer(wsdl);
        this.init();
    }

    /**
     * @param pse Construct based on a service explorer.
     */
    public IdentifyWrapper(PlanetsServiceExplorer pse) {
        this.pse = pse;
        this.init();
    }

    /**
     * 
     */
    private void init() {
        service = Service.create(pse.getWsdlLocation(), pse.getQName());
        try {
            if (pse.getQName().equals(BasicIdentifyOneBinary.QNAME)) {
                biob = (BasicIdentifyOneBinary) service.getPort(pse.getServiceClass());
                
            } else if (pse.getQName().equals(IdentifyOneBinary.QNAME)) {
                iob = (IdentifyOneBinary) service.getPort(pse.getServiceClass());
                
            } else {
                i = (Identify) service.getPort(pse.getServiceClass());
                
            }
        } catch( Exception e ) {
            log.error("Failed to instanciate service "+ pse.getQName() +" at "+pse.getWsdlLocation() + " : Exception - "+e);
            biob = null;
            iob = null;
            i = null;
        }
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.identify.Identify#describe()
     */
    public ServiceDescription describe() {
        if ( this.isBasic() ) {
            ServiceDescription.Builder sd = new ServiceDescription.Builder(
                    pse.getWsdlLocation().getPath(), pse
                            .getServiceClass().getCanonicalName() );
            sd.description("This is a basic migration service with no service description or other metadata.");
            sd.author("[unknown]");
            sd.serviceProvider(pse.getWsdlLocation().getAuthority());
            return sd.build();
        } else {
            return i.describe();
        }
    }


    /* (non-Javadoc)
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject)
     */
    public IdentifyResult identify(DigitalObject digitalObject) {

        // Transform the DO into a single binary, if that is sane
        byte[] binary = null;
        if ( this.isBasic() 
                && digitalObject.getContent() == null
                && digitalObject.getContained() != null) {
            return new IdentifyResult(null, ServiceUtils
                    .createErrorReport("This service cannot deal with composite digital objects.") );
        }

        // Invoke the service based on the type (QName):
        if (pse.getQName().equals(BasicIdentifyOneBinary.QNAME)) {
            // Basic Migrate One Binary:
            URI bresult;
            try {
                bresult = biob.basicIdentifyOneBinary(binary);
            } catch (PlanetsException e) {
                e.printStackTrace();
                log.error("Got error: "+e);
                return  new IdentifyResult(null, ServiceUtils
                        .createExceptionErrorReport("Service failed during basic service invocation.",e ) );
            }
            List<URI> uris = new ArrayList<URI>();
            uris.add(bresult);
            return new IdentifyResult(uris, new ServiceReport());

        } else if (pse.getQName().equals(IdentifyOneBinary.QNAME)) {
            Types types = iob.identifyOneBinary(binary);
            List<URI> uris = new ArrayList<URI>();
            for( URI type : types.types ) {
                uris.add(type);
            }
            return new IdentifyResult(uris, new ServiceReport());
            
        } else {
            // Identify:
            return i.identify(digitalObject);
        }
    }

    /**
     * @return
     */
    private boolean isBasic() {
        if( pse.getQName().equals(BasicIdentifyOneBinary.QNAME) ) return true;
        if( pse.getQName().equals(IdentifyOneBinary.QNAME) ) return true;
        return false;
    }

}
