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
import eu.planets_project.services.datatypes.Parameter;

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
    public List<Parameter> getProperties() {
        List<Parameter> result = new ArrayList<Parameter>();
        result.addAll(getBasicProperties());
        CompSet compSet = pcr.getCompSets().get(0);
        // TODO what about multiple files in the PCR?
        List<eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Property> list = compSet
                .getProperties();
        for (eu.planets_project.ifr.core.services.comparison.comparator.config.generated.Property property : list) {
            String name = property.getName();
            StringBuilder metrics = new StringBuilder();
            List<Metric> pcrMetrics = property.getMetrics();
            for (Metric metric : pcrMetrics) {
                int metricId = metric.getId();
                metrics.append("metric").append(" ").append(metric.getName())
                        .append(" ").append(String.valueOf(metricId)).append(
                                ",");
            }
            String mString = metrics.toString();
            if (mString.endsWith(",")) {
                mString = mString.substring(0, mString.length() - 1);
            }
            String propId = String.valueOf(property.getId());
            Parameter prop = new Parameter(name, mString, propId);
            result.add(prop);
        }
        return result;
    }

    /**
     * @return The basic string properties
     */
    public List<Parameter> getBasicProperties() {
        List<Parameter> result = new ArrayList<Parameter>();
        CompSet compSet = pcr.getCompSets().get(0);
        Parameter p1 = new Parameter("source", compSet.getSource().getName());
        Parameter p2 = new Parameter("target", compSet.getTarget().getName());
        result.add(p1);
        result.add(p2);
        return result;
    }

    /**
     * @return the full comparator config object tree
     */
    public PcRequest getPcRequest() {
        return pcr;
    }
}
