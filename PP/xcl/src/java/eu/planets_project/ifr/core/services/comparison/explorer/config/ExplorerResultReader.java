package eu.planets_project.ifr.core.services.comparison.explorer.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.services.datatypes.Property;

/**
 * Reads a XCL Explorer tool result XML file into a list or property objects.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ExplorerResultReader {
    /** Enforce non-instantiability with a private constructor. */
    private ExplorerResultReader() {}

    /**
     * @param xml The FPM tool result XML string
     * @return The properties contained in the result file
     */
    public static List<Property> properties(final String xml) {
        SAXBuilder builder = new SAXBuilder();
        List<Property> result = new ArrayList<Property>();
        try {
            Element root = builder.build(new StringReader(xml))
                    .getRootElement();
            for (Object object : root.getChild("format")
                    .getChildren("property")) {
                Element pElem = (Element) object;
                // FIXME no metrics in, is this OK?
                // Element metricsElem = (Element) pElem.getChild("metrics");
                // List<Prop> mProps = new ArrayList<Prop>();
                // for (Object mObjects : metricsElem.getChildren("m")) {
                // Element mElem = (Element) mObjects;
                // mProps.add(Prop.name(mElem.getChildTextTrim("mName")).type(
                // mElem.getChildTextTrim("mId")).description(
                // mElem.getChildTextTrim("mDescription")).values(
                // mElem.getChildTextTrim("mType")).build());
                // }
                String type = pElem.getChildTextTrim("id").replaceAll("id", "");
                String name = pElem.getChildTextTrim("name");
                Property p = new Property.Builder(XcdlProperties
                        .makePropertyURI(type, name)).name(name).type(type)
                        .description(pElem.getChildTextTrim("description"))
                        .unit(pElem.getChildTextTrim("unit")).value(
                                pElem.getChildTextTrim("type").trim())
                        // .values(
                        // mProps.toArray(new Prop[] {}))
                        .build();
                result.add(p);

            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
