package eu.planets_project.ifr.core.registry.utils;

import java.net.URL;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.planets_project.ifr.core.registry.gui.old.RegistryBeanOld;
import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.characterise.BasicCharacteriseOneBinary;
import eu.planets_project.services.characterise.BasicCharacteriseOneBinaryXCELtoBinary;
import eu.planets_project.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;
import eu.planets_project.services.characterise.DetermineProperties;
import eu.planets_project.services.compare.BasicCompareFormatProperties;
import eu.planets_project.services.compare.BasicCompareTwoXcdlReferences;
import eu.planets_project.services.compare.BasicCompareTwoXcdlValues;
import eu.planets_project.services.compare.CompareMultipleXcdlReferences;
import eu.planets_project.services.compare.CompareMultipleXcdlValues;
import eu.planets_project.services.identify.BasicIdentifyOneBinary;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.validate.BasicValidateOneBinary;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.view.CreateView;

/**
 * 
 * @author <a href="mailto:andrew.jackson@bl.uk">Andy Jackson</a>
 *
 */
@SuppressWarnings("deprecation")
public class PlanetsServiceExplorer {
	private static Log log = LogFactory.getLog(PlanetsServiceExplorer.class);

    private URL wsdlLocation = null;
    private QName qName = null;
    
    // Create a static hashmap, mapping QNames to the interfaces:
    private static HashMap<QName, Class<?>> classmap = new HashMap<QName, Class<?>>();
    static {
    	classmap.put(BasicCharacteriseOneBinary.QNAME, BasicCharacteriseOneBinary.class);
    	classmap.put(BasicCharacteriseOneBinaryXCELtoBinary.QNAME, BasicCharacteriseOneBinaryXCELtoBinary.class);
    	classmap.put(BasicCharacteriseOneBinaryXCELtoURI.QNAME, BasicCharacteriseOneBinaryXCELtoURI.class);
        classmap.put(DetermineProperties.QNAME, DetermineProperties.class);
        classmap.put(BasicCompareFormatProperties.QNAME, BasicCompareFormatProperties.class);
        classmap.put(BasicCompareTwoXcdlReferences.QNAME, BasicCompareTwoXcdlReferences.class);
        classmap.put(BasicCompareTwoXcdlValues.QNAME, BasicCompareTwoXcdlValues.class);
        classmap.put(CompareMultipleXcdlReferences.QNAME, CompareMultipleXcdlReferences.class);
        classmap.put(CompareMultipleXcdlValues.QNAME, CompareMultipleXcdlValues.class);
        classmap.put(BasicIdentifyOneBinary.QNAME, BasicIdentifyOneBinary.class);
        classmap.put(Identify.QNAME, Identify.class);
        classmap.put(IdentifyOneBinary.QNAME, IdentifyOneBinary.class);
        classmap.put(BasicMigrateOneBinary.QNAME, BasicMigrateOneBinary.class);
        classmap.put(Migrate.QNAME, Migrate.class);
        classmap.put(BasicValidateOneBinary.QNAME, BasicValidateOneBinary.class);
        classmap.put(Validate.QNAME, Validate.class);
        classmap.put(CreateView.QNAME, CreateView.class);
    }

    /**
     * Probes for the QName on construction.
     * @param wsdlLocation The location of the WSDL of the service.
     */
    public PlanetsServiceExplorer(URL wsdlLocation) {
    	log.info("Creating new instance");
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
    	log.info("determining qname");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Using factory get an instance of document builder
        DocumentBuilder db;
        try {
        	log.info("new doc builder");
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        // parse using builder to get DOM representation of the XML file
        Document dom;
        try {
        	log.debug("parsing wsdl");
            dom = db.parse(wsdlLocation.openStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // get the root elememt
        Element root = dom.getDocumentElement();
        log.debug("getting root element");
        return new QName(root.getAttribute("targetNamespace"), root
                .getAttribute("name"));
    }

}
