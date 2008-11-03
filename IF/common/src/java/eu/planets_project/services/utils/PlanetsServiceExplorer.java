/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.identify.BasicIdentifyOneBinary;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateOneBinary;
import eu.planets_project.services.validate.BasicValidateOneBinary;
import eu.planets_project.services.validate.Validate;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
public class PlanetsServiceExplorer {

    private URL wsdlLocation = null;
    private QName qName = null;

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
     * Determine the high-level service class for this service type.
     * e.g. the Basic forms are wrapped up in higher level forms.
     * @return
     */
    public Class<? extends PlanetsService> getServiceClass() {

        // Unqualified services cannot be dealt with:
        if (qName == null)
            return null;

        // Determine class of service:
        if (qName.equals(Migrate.QNAME) || qName.equals(MigrateOneBinary.QNAME)
                || qName.equals(BasicMigrateOneBinary.QNAME)) {
            return Migrate.class;
        } else if (qName.equals(Identify.QNAME)
                || qName.equals(BasicIdentifyOneBinary.QNAME)) {
            return Identify.class;
        } else if (qName.equals(Validate.QNAME)
                || qName.equals(BasicValidateOneBinary.QNAME)) {
            return Validate.class;
        }

        // Otherwise, this is and unrecognised service:
        return null;
    }
    
    /**
     * 
     * @return
     */
    public boolean isDeprecated() {
        if( qName.getLocalPart().startsWith("Basic")) return true;
        return false;
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
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // get the root elememt
        Element root = dom.getDocumentElement();
        return new QName(root.getAttribute("targetNamespace"), root
                .getAttribute("name"));
    }
}
