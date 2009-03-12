package eu.planets_project.ifr.core.services.comparison.fpm.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import eu.planets_project.services.datatypes.Prop;

/**
 * Reads a FPM tool result XML file into a list or property objects.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class FpmResultReader {
    /** Enforce non-instantiability with a private constructor. */
    private FpmResultReader() {}

    /**
     * @param xml The FPM tool result XML string
     * @return The properties contained in the result file
     */
    public static List<Prop> properties(final String xml) {
        SAXBuilder builder = new SAXBuilder();
        List<Prop> result = new ArrayList<Prop>();
        try {
            Element root = builder.build(new StringReader(xml))
                    .getRootElement();
            for (Object object : root.getChild("format")
                    .getChildren("property")) {
                Element pElem = (Element) object;
                Element metricsElem = (Element) pElem.getChild("metrics");
                List<Prop> mProps = new ArrayList<Prop>();
                for (Object mObjects : metricsElem.getChildren("m")) {
                    Element mElem = (Element) mObjects;
                    mProps.add(Prop.name(mElem.getChildTextTrim("mName")).type(
                            mElem.getChildTextTrim("mId")).description(
                            mElem.getChildTextTrim("mDescription")).values(
                            mElem.getChildTextTrim("mType")).build());
                }
                Prop p = Prop.name(pElem.getChildTextTrim("name")).type(
                        pElem.getChildTextTrim("id")).description(
                        pElem.getChildTextTrim("description")).unit(
                        pElem.getChildTextTrim("unit")).values(
                        pElem.getChildTextTrim("type").trim()).values(
                        mProps.toArray(new Prop[] {})).build();
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
