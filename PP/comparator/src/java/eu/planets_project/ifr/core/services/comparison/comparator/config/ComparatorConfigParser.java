package eu.planets_project.ifr.core.services.comparison.comparator.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.CompSet;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Metric;
import eu.planets_project.ifr.core.services.comparison.comparator.config.generated.PcRequest;
import eu.planets_project.services.datatypes.Prop;

/**
 * Access to a complete XCDL comparator config file (PCR), via JAXB-generated
 * classes.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ComparatorConfigParser {

    private File pcrFile;
    private PcRequest pcr;

    /**
     * @param xcdl The comparator config file
     */
    public ComparatorConfigParser(final File xcdl) {
        this.pcrFile = xcdl;
        this.pcr = loadPcr();
    }

    /**
     * @return The XCDL root object
     */
    private PcRequest loadPcr() {
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services.comparison.comparator.config.generated");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            java.lang.Object object = unmarshaller.unmarshal(pcrFile);
            System.out.println(object.getClass());
            return (PcRequest) object;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return The properties of the comparator config file
     */
    public List<Prop> getProperties() {
        List<Prop> result = new ArrayList<Prop>();
        CompSet compSet = pcr.getCompSets().get(0);
        // TODO what about multiple files in the PCR?
        result.add(Prop.name("source").values(compSet.getSource().getName())
                .build());
        result.add(Prop.name("target").values(compSet.getTarget().getName())
                .build());
        List<eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Property> list = compSet
                .getProperties();
        for (eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Property property : list) {
            String name = property.getName();
            List<Prop> metrics = new ArrayList<Prop>();
            List<Metric> pcrMetrics = property.getMetrics();
            for (Metric metric : pcrMetrics) {
                int metricId = metric.getId();
                metrics.add(Prop.name("metric").type(String.valueOf(metricId))
                        .description(metric.getName()).build());
            }
            String propId = String.valueOf(property.getId());
            Prop prop = Prop.name(name).props(metrics.toArray(new Prop[] {}))
                    .type(propId).build();
            result.add(prop);
        }
        return result;
    }

    /**
     * @return the full comparator config object tree
     */
    public PcRequest getPcRequest() {
        return pcr;
    }
}
