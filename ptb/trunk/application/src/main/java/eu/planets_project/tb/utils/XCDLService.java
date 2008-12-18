/**
 * 
 */
package eu.planets_project.tb.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.xml.ws.Service;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.DetermineProperties;
import eu.planets_project.services.characterise.DeterminePropertiesResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.Properties;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.impl.system.BackendProperties;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class XCDLService implements DetermineProperties {

    /** */
    private URL extractorWsdl;
    /** */
    private Characterise extractor;
    
    /** */
    public XCDLService() {
        BackendProperties bp = new BackendProperties();
        try {
            extractorWsdl = new URL( bp.getProperty("extractor.endpoint.xcdl") );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Got Extractor endpoint: "+extractorWsdl);
        // extractorWsdl = new URI( "http://localhost:8080/pserv-pc-extractor/Extractor?wsdl" );
        Service srv = Service.create(extractorWsdl, Characterise.QNAME);
        extractor = srv.getPort(Characterise.class);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#describe()
     */
    public ServiceDescription describe() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#getMeasurableProperties(java.net.URI)
     */
    public Properties getMeasurableProperties(URI formatURI) {
        // Only cope with PRONOM IDs:
        if( ! Format.isThisAPronomURI(formatURI) ) {
            return null;
        }
        // Extract the list:
        List<FileFormatProperty> properties = extractor.listProperties(formatURI);
        if( properties == null ) return null;
        // Now create list and copy the properties into it:
        List<Property> props = new Vector<Property>();
        for( FileFormatProperty m : properties ) {
            System.out.println("Got property "+m.getId() + ", " +m.getName() + ", " + m.getDescription() );
            Property p = new Property( m.getId()+"/"+m.getName(), m.getValue());
            p.setDescription(m.getDescription());
            p.setType(m.getType());
            p.setUnit(m.getUnit());
            props.add(p);
        }
        Properties propobj = new Properties();
        propobj.setProperties(props);
        return propobj;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#measure(eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.services.datatypes.Properties, eu.planets_project.services.datatypes.Parameters)
     */
    public DeterminePropertiesResult measure(DigitalObject dob,
            Properties props, Parameters params) {
        
        return null;
    }

}
