package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import eu.planets_project.services.datatypes.Property;

/**
 * Access to XCDL properties based on XML processing only (no dependencies on
 * the JAXB-generated classes).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class XcdlProperties implements XcdlAccess {

    private static final Namespace NS = Namespace
            .getNamespace("http://www.planets-project.eu/xcl/schemas/xcl");

    public static final String XCDLPropertyRoot = "planets:pc/xcdl/property/";

    public static URI XCDLPropertyRootUri;
    static {
        try {
            XCDLPropertyRootUri = new URI(XCDLPropertyRoot);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private File xcdlFile;

    /**
     * @param xcdlFile The XCDL file
     */
    public XcdlProperties(final File xcdlFile) {
        this.xcdlFile = xcdlFile;
    }

    /**
     * 
     */
    public static URI makePropertyURI(String id, String name) {
        try {
            URI propUri = new URI(XcdlProperties.XCDLPropertyRoot + "id" + id
                    + "/" + name);
            return propUri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccess#getProperties()
     */
    public List<Property> getProperties() {
        List<Property> properties = new ArrayList<Property>();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(xcdlFile);
            Element obj = doc.getRootElement().getChild("object", NS);
            List<?> propElems = obj.getChildren("property", NS);
            for (Object object : propElems) {
                Element e = (Element) object;
                String name = e.getChildText("name", NS);
                Element labVal = e.getChild("valueSet", NS).getChild(
                        "labValue", NS);
                String value = labVal.getChildText("val", NS);
                String id = e.getChild("name",NS).getAttributeValue("id")
                        .replaceAll("id", "");
                URI propUri = XcdlProperties.makePropertyURI(id, name);
                Property p = new Property(propUri, name, value);
                p.setType(labVal.getChildText("type", NS));
                properties.add(p);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * @param args unused
     */
    public static void main(final String[] args) {
        XcdlProperties xcdlProperties = new XcdlProperties(new File(
                "PC/extractor/src/java/eu/planets_project/xcdl/xcdl.xml"));
        List<Property> properties = xcdlProperties.getProperties();
        for (Property property : properties) {
            System.out.println(property);
        }
    }
}
