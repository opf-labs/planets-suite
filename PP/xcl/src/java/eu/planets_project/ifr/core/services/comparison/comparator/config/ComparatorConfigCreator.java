package eu.planets_project.ifr.core.services.comparison.comparator.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Coco;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.CompSet;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.MeasureType;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Metric;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Property;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Conversion of an XCDL Comparator config object model to a valid config file
 * to be handed to the XCDL Comparator.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ComparatorConfigCreator {

    private String cocoXml;

    /**
     * @param config The config elements
     */
    public ComparatorConfigCreator(final List<Parameter> config) {
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services."
                            + "comparison.comparator.config.generated");
            Marshaller marshaller = jc.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            Coco pcrObject = createCocoObject(config);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("jaxb.schemaLocation",
                    "http://www.planets-project.eu/xcl/schemas/xcl /res/schemas/coco.xsd");
            marshaller.marshal(pcrObject, stringWriter);
            this.cocoXml = stringWriter.toString();
            System.out.println(String.format("Marshalled %s to:\n%s",
                    pcrObject, cocoXml));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param config The config elements
     * @return Returns a {@link PcRequest} corresponding to the properties
     */
    private Coco createCocoObject(final List<Parameter> config) {
        Coco result = new Coco();
        CompSet set = new CompSet();
        result.getCompSets().add(set);
        for (Parameter prop : config) {
            addPropertyElement(set, prop);
        }
        return result;
    }

    /**
     * @param set The comp set to add the prop to
     * @param prop The prop to add as a property element to the comp set
     */
    private void addPropertyElement(final CompSet set, final Parameter prop) {
        Property p = new Property();
        p.setId(Integer.parseInt(prop.getType()));
        p.setName(prop.getName());
        if (prop.getValue() != null && prop.getValue().contains("unit")) {
            String[] tokens = clean(prop.getValue().split(","));
            for (String string : tokens) {
                String[] lowerTokens = clean(string.split(" "));
                if (lowerTokens[0].equals("unit") && lowerTokens.length == 2) {
                    MeasureType[] values = MeasureType.values();
                    for (MeasureType type : values) {
                        if (type.name().toLowerCase().equals(lowerTokens[1])) {
                            break;
                        }
                    }
                }
            }
        }
        if (prop.getValue() != null) {
            String[] metrics = clean(prop.getValue().split(","));
            for (String metric : metrics) {
                String[] mToks = metric.split(" ");
                if (mToks.length != 3) {
                    throw new IllegalArgumentException(
                            "Cannot work with metric string: " + metric);
                }
                if (mToks[0].equals("metric")) {
                    Metric m = new Metric();
                    m.setName(mToks[1]);
                    String type = mToks[2];
                    if (type.trim().length() == 0) {
                        throw new IllegalStateException(String.format(
                                "%s has an empty type", metric));
                    }
                    m.setId(Integer.parseInt(type));
                    p.getMetrics().add(m);
                }
            }
        }
        set.getProperties().add(p);
    }

    /**
     * @return Returns the PCR XML config file content, which can be given to
     *         the comparator.
     */
    public String getComparatorConfigXml() {
        return cocoXml;
    }

    private String[] clean(final String[] split) {
        List<String> result = new ArrayList<String>();
        for (String string : split) {
            String clean = string.trim();
            if (clean.length() > 0) {
                result.add(clean);
            }
        }
        return result.toArray(new String[] {});
    }

}
