/**
 * 
 */
package eu.planets_project.tb.impl.services.util;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.view.CreateView;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
public class PlanetsServiceExplorer {

    private URL wsdlLocation = null;
    private QName qName = null;
    
    // Create a static hashmap, mapping QNames to the interfaces:
    private static HashMap<QName, Class<?>> classmap = new HashMap<QName, Class<?>>();
    static {
        classmap.put( Migrate.QNAME, Migrate.class );
        classmap.put( Identify.QNAME, Identify.class );
        classmap.put( Validate.QNAME, Validate.class );
        classmap.put( Characterise.QNAME, Characterise.class );
        classmap.put( CreateView.QNAME, CreateView.class );
        classmap.put( Compare.QNAME, Compare.class );
    }

    /**
     * Probes for the QName on construction.
     * @param wsdlLocation The location of the WSDL of the service.
     */
    public PlanetsServiceExplorer(URL wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
        this.qName = determineServiceQNameFromWsdl();
    }

    /**
     * @return the wsdlLocation
     */
    public URL getWsdlLocation() {
        return wsdlLocation;
    }

    /**
     * @return the qName
     */
    public QName getQName() {
        return qName;
    }

    /**
     * Attempts to instanciate a service, and so checks if the thing is
     * essentially working.
     * 
     * @param wsdlLocation
     * @return
     */
    public boolean isServiceInstanciable() {
        Service service = Service.create(wsdlLocation, qName);
        PlanetsService s = (PlanetsService) service.getPort(getServiceClass());
        if ( s  != null ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return
     */
    public Class<?> getServiceClass() {
        return classmap.get(qName);
    }

    /**
     * This method examines a given service end-point and attempt to determine
     * the QName of the wsdl:service.
     * 
     * @param wsdlLocation
     * @return
     * @throws IOException
     * @throws SAXException
     */
    private QName determineServiceQNameFromWsdl() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Using factory get an instance of document builder
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        // parse using builder to get DOM representation of the XML file
        Document dom;
        try {
            dom = db.parse(wsdlLocation.openStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // get the root elememt
        Element root = dom.getDocumentElement();
        return new QName(root.getAttribute("targetNamespace"), root
                .getAttribute("name"));
    }
}
