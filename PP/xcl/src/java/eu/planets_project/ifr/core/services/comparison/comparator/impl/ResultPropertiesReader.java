package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.compare.PropertyComparison.Equivalence;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.utils.FileUtils;

/**
 * Access to CPR (the XCDL comparator result format) properties.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ResultPropertiesReader {

    private static final Namespace NS = Namespace.getNamespace("http://www.planets-project.eu/xcl/schemas/xcl");

    private String cprString = "";

    /**
     * @param cprFile The CPR comparator result file
     */
    public ResultPropertiesReader(final File cprFile) {
        this.cprString = FileUtils.readTxtFileIntoString(cprFile);
    }               

    /**
     * @return The properties in the given CPR file
     */
    public List<List<PropertyComparison>> getProperties() {
        List<List<PropertyComparison>> all = new ArrayList<List<PropertyComparison>>();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(new StringReader(cprString));
            if (doc.getRootElement().getChild("set", NS) == null) {
                String childText = doc.getRootElement().getChildText("error", NS);
                throw new IllegalArgumentException("Can't process document: " + childText);
            }
            @SuppressWarnings("unchecked") // JDOM API
            List<Element> sets = doc.getRootElement().getChildren("set", NS);
            for (Element set : sets) {
                List<PropertyComparison> properties = new ArrayList<PropertyComparison>();
                List<?> propElems = set.getChildren("property", NS);
                for (Object object : propElems) {
                    Element e = (Element) object;
                    String state = e.getAttributeValue("state");
                    Map<String,String> metrics = processMetrics(e);
                    String value = "";
                    for( String key : metrics.keySet() ) {
                        if( !"equal".equals(key)) {
                            value += key+"="+metrics.get(key)+" ";
                        }
                    }
                    String name = e.getAttributeValue("name");
                    Property result = new Property.Builder(XcdlProperties.makePropertyURI(name)).name(name).value(value.trim())
                            .description(state+" property "+name).build();
                    // Work out equivalence:
                    Equivalence eq = Equivalence.UNKNOWN;
                    if( "complete".equals(state) || "partial".equals(state) ) {
                        if( "true".equals( metrics.get("equal") ) ) {
                            eq = Equivalence.EQUAL;
                        } else if( "false".equals( metrics.get("equal") ) ) {
                            eq = Equivalence.DIFFERENT;
                        }
                    }
                    if( "missing".equals(state) ) eq = Equivalence.MISSING;
                    properties.add( new PropertyComparison( result, eq ) );
                }
                all.add(properties);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return all;
    }

    private Map<String,String> processMetrics(final Element propertyElement) {
        Map<String,String> mmap = new HashMap<String,String>();
        Element metricsElem = propertyElement.getChild("metrics", NS);
        int count = 0;
        if (metricsElem != null) {
            List<?> metrics = metricsElem.getChildren();
            for (Object mObject : metrics) {
                Element m = (Element) mObject;
                String metricName = m.getAttributeValue("name");
                String resultString = m.getChildText("result", NS);
                mmap.put(metricName.trim(), resultString.trim());
                count += 1;
            }
        }
        return mmap;
    }
}
