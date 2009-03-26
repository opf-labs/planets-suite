package eu.planets_project.ifr.core.services.comparison.comparator.impl;

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

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.services.datatypes.Prop;
import eu.planets_project.services.datatypes.Property;

/**
 * Access to CPR (the XCDL comparator result format) properties.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ResultPropertiesReader {

    private static final Namespace NS = Namespace
            .getNamespace("http://www.planets-project.eu/xcl/schemas/xcl");

    private File cprFile;

    /**
     * @param cprFile The CPR comparator result file
     */
    public ResultPropertiesReader(final File cprFile) {
        this.cprFile = cprFile;
    }

    /**
     * @return The properties in the given CPR file
     */
    public List<Property> getProperties() {
        List<Property> properties = new ArrayList<Property>();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(cprFile);
            Element obj = doc.getRootElement().getChild("compSet", NS);
            if (obj == null) {
                throw new IllegalArgumentException("Can't process document: "
                        + doc.getRootElement().getChildText("error", NS));
            }
            List<?> propElems = obj.getChildren("property", NS);
            for (Object object : propElems) {
                Element e = (Element) object;
                String name = e.getAttributeValue("name");
                String unit = e.getAttributeValue("unit");
                String status = e.getAttributeValue("compStatus");
                Element valuesElement = e.getChild("values", NS);
                String valuesSrc = null;
                String valuesTar = null;
                String valuesType = null;
                if (valuesElement != null) {
                    valuesType = valuesElement.getAttributeValue("type");
                    valuesSrc = valuesElement.getChildText("src", NS);
                    valuesTar = valuesElement.getChildText("tar", NS);
                }
                Property result;
                result = new Property.Builder(XcdlProperties.makePropertyURI(e
                        .getAttributeValue("id"), name)).type(valuesType).name(
                        name).value(valuesSrc + "," + valuesTar).description(
                        status).unit(unit).build();
                properties.add(result);
                // Prop.Builder pBuilder = Prop.name(name).values(valuesSrc,
                // valuesTar).description(status).unit(unit).type(
                // valuesType);
                // List<Prop> subProperties = metrics(e);
                // pBuilder.values(subProperties.toArray(new Prop[] {}));
                // properties.add(pBuilder.build());
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * @param e The parent "property" element
     * @return A list of properties created from the metrics children
     */
    private List<Prop> metrics(final Element e) {
        /* For each metric, we create a sub-property: */
        List<?> metrics = e.getChildren("metric", NS);
        List<Prop> subProperties = new ArrayList<Prop>();
        for (Object o : metrics) {
            Element metricElement = (Element) o;
            String metricName = metricElement.getAttributeValue("name");
            String metricResult = metricElement.getAttributeValue("result");
            subProperties.add(Prop.name(metricName).values(metricResult)
                    .build());
        }
        return subProperties;
    }
}
