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
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
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
public class CharacteriseWrapper implements Characterise {

    /** */
    private static final Log log = LogFactory.getLog(CharacteriseWrapper.class);

    PlanetsServiceExplorer pse = null;
    Service service = null;
    Characterise c = null;
    
    /**
     * @param wsdl The WSDL to wrap as a service.
     */
    public CharacteriseWrapper( URL wsdl ) {
        this.pse = new PlanetsServiceExplorer(wsdl);
        this.init();
    }

    /**
     * @param pse Construct based on a service explorer.
     */
    public CharacteriseWrapper(PlanetsServiceExplorer pse) {
        this.pse = pse;
        this.init();
    }

    /**
     * 
     */
    private void init() {
        service = Service.create(pse.getWsdlLocation(), pse.getQName());
        try {
            c = (Characterise) service.getPort(pse.getServiceClass());
        } catch( Exception e ) {
            log.error("Failed to instanciate service "+ pse.getQName() +" at "+pse.getWsdlLocation() + " : Exception - "+e);
            e.printStackTrace();
            c = null;
        }
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.Characterise#describe()
     */
    public ServiceDescription describe() {
        return c.describe();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.Characterise#characterise(eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.services.datatypes.Parameters)
     */
    public CharacteriseResult characterise(DigitalObject digitalObject,
            Parameters parameters) {
        return c.characterise(digitalObject, parameters);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.Characterise#listProperties(java.net.URI)
     */
    public List<FileFormatProperty> listProperties(URI formatURI) {
        return c.listProperties(formatURI);
    }

    
    
}
