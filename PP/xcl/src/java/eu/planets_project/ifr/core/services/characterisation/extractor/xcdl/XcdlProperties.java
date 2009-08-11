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

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreator.PropertyName;
import eu.planets_project.services.datatypes.Property;

/**
 * Access to XCDL properties based on XML processing only (no dependencies on
 * the JAXB-generated classes).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class XcdlProperties implements XcdlAccess {

    /* XcdlAccess implementation: */

    private static final Namespace NS = Namespace
            .getNamespace("http://www.planets-project.eu/xcl/schemas/xcl");

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
                URI propUri = XcdlProperties.makePropertyURI(name);
                Property p = new Property.Builder(propUri).name(name).value(
                        value).type(labVal.getChildText("type", NS)).build();
                properties.add(p);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return XcdlParser.fixPropertiesForXcdl(properties);
    }

    /**
     * @param args unused
     */
    public static void main(final String[] args) {
        XcdlProperties xcdlProperties = new XcdlProperties(
                new File(
                        "PP/xcl/src/java/eu/planets_project/ifr/core/services/characterisation/extractor/xcdl/xcdl.xml"));
        List<Property> properties = xcdlProperties.getProperties();
        for (Property property : properties) {
            System.out.println(property);
        }
    }

    /* Relatively unrelated: static methods for handling property uris: */

    static final String URI_ROOT = "http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#";

    private File xcdlFile;

    /**
     * @param xcdlFile The XCDL file
     */
    public XcdlProperties(final File xcdlFile) {
        this.xcdlFile = xcdlFile;
    }

    /**
     * @param name The property name
     * @return A uniform URI for the property
     */
    public static URI makePropertyURI(final String name) {
        try {
            URI propUri = new URI(XcdlProperties.URI_ROOT + name);
            return propUri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param properties The XCDL properties, possibly including norm data and
     *        set properties to be filtered
     * @return A new list containing only the property objects that are actual
     *         XCDL properties (no norm data or property sets)
     */
    public static List<Property> realProperties(final List<Property> properties) {
        List<Property> result = new ArrayList<Property>();
        for (Property property : properties) {
            if (property.getType() != null
                    && property.getType().equalsIgnoreCase(
                            PropertyName.PROPERTY.s)) {
                result.add(property);
            }
        }
        return result;
    }

    /**
     * @param uri The property URI
     * @return The raw property name (e.g. imageHeight)
     */
    public static String getNameFromUri(final URI uri) {
        return uri.toString().split("#")[1];
    }

}
