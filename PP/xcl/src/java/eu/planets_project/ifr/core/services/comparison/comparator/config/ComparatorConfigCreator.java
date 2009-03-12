package eu.planets_project.ifr.core.services.comparison.comparator.config;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.CompSet;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.MeasureType;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Metric;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.PcRequest;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Property;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Source;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Target;
import eu.planets_project.services.datatypes.Prop;

/**
 * Conversion of an XCDL Comparator config object model to a valid config file
 * to be handed to the XCDL Comparator.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ComparatorConfigCreator {

    private String pcrXml;

    /**
     * @param config The config elements
     */
    public ComparatorConfigCreator(final List<Prop<Object>> config) {
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services."
                            + "comparison.comparator.config.generated");
            Marshaller marshaller = jc.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            PcRequest pcrObject = createPcrObject(config);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("jaxb.schemaLocation",
                    "http://www.planets-project.eu/xcl/schemas/xcl  pcr.xsd");
            marshaller.marshal(pcrObject, stringWriter);
            this.pcrXml = stringWriter.toString();
            System.out.println(String.format("Marshalled %s to:\n%s",
                    pcrObject, pcrXml));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param config The config elements
     * @return Returns a {@link PcRequest} corresponding to the properties
     */
    private PcRequest createPcrObject(final List<Prop<Object>> config) {
        PcRequest result = new PcRequest();
        CompSet set = new CompSet();
        result.getCompSets().add(set);
        for (Prop<Object> prop : config) {
            if (prop.getName().equals("source")) {
                Source s = new Source();
                s.setName(prop.getValues().get(0).toString());
                set.setSource(s);
            } else if (prop.getName().equals("target")) {
                Target t = new Target();
                t.setName(prop.getValues().get(0).toString());
                set.setTarget(t);
            } else {
                addPropertyElement(set, prop);
            }
        }
        return result;
    }

    /**
     * @param set The comp set to add the prop to
     * @param prop The prop to add as a property element to the comp set
     */
    private void addPropertyElement(final CompSet set, final Prop prop) {
        Property p = new Property();
        p.setId(Integer.parseInt(prop.getType()));
        p.setName(prop.getName());
        if (prop.getUnit() != null) {
            MeasureType[] values = MeasureType.values();
            for (MeasureType type : values) {
                if (type.name().toLowerCase().equals(prop.getUnit())) {
                    p.setUnit(type);
                    break;
                }
            }
        }
        List<Prop> metrics = prop.getValues();
        for (Prop metric : metrics) {
            if (metric.getName().equals("metric")) {
                Metric m = new Metric();
                m.setName(metric.getDescription());
                String type = metric.getType();
                if (type.trim().length() == 0) {
                    throw new IllegalStateException(String.format(
                            "%s has an empty type", metric));
                }
                m.setId(Integer.parseInt(type));
                p.getMetrics().add(m);
            }
        }
        set.getProperties().add(p);
    }

    /**
     * @return Returns the PCR XML config file content, which can be given to
     *         the comparator.
     */
    public String getComparatorConfigXml() {
        return pcrXml;
    }

}
