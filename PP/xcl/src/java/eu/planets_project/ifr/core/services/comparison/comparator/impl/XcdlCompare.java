package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigCreator;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigParser;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;

/**
 * XCDL Comparator service. Compares XCDL files wrapped as digital objects.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = XcdlCompare.NAME, serviceName = Compare.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.compare.Compare")
@Stateless
public final class XcdlCompare implements Compare {
    /***/
    static final String NAME = "XcdlCompare";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#compare(eu.planets_project.services.datatypes.DigitalObject[],
     *      eu.planets_project.services.datatypes.DigitalObject)
     */
    public CompareResult compare(final DigitalObject first, final DigitalObject second, final List<Parameter> config) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Digital objects to compare must not be null");
        }
        // TODO Hm, I guess this is not OK? (fsteeg)
        // List<URI> supported = ComparatorWrapper.getSupportedInputFormats();
        // if (!supported.contains(first.getFormat()) || !supported.contains(second.getFormat())) {
        // throw new IllegalArgumentException("Unsupported format!");
        // }
        String pcr = new ComparatorConfigCreator(config).getComparatorConfigXml();
        String result = ComparatorWrapper.compare(read(first), Arrays.asList(read(second)), pcr);
        List<List<Property>> props = propertiesFrom(result);
        return compareResult(props);
    }

    /**
     * @param props The 1-level nested result properties
     * @return A compare result object with either top-level properties only or without top-level properties but
     *         embedded results
     */
    static CompareResult compareResult(final List<List<Property>> props) {
        if (props.size() == 1) {
            return new CompareResult(props.get(0), new ServiceReport(Type.INFO, Status.SUCCESS,
                    "Top-level comparison result without embedded results"));
        } else {
            List<CompareResult> embedded = new ArrayList<CompareResult>();
            for (List<Property> list : props) {
                embedded.add(new CompareResult(list, new ServiceReport(Type.INFO, Status.SUCCESS,
                        "Embedded comparison result")));
            }
            return new CompareResult(new ArrayList<Property>(), new ServiceReport(Type.INFO, Status.SUCCESS,
                    "Top-level comparison result with embedded results"), embedded);
        }
    }

    /**
     * @param result The comparator result
     * @return The properties found in the result XML
     */
    private List<List<Property>> propertiesFrom(final String result) {
        File file = FileUtils.writeByteArrayToTempFile(result.getBytes());
        return new ResultPropertiesReader(file).getProperties();
    }

    /**
     * @param digitalObject The digital objects
     * @return A string representing the content of the digital objects
     */
    private String read(final DigitalObject digitalObject) {
        if (digitalObject == null) {
            throw new IllegalArgumentException("Digital object is null!");
        }
        InputStream stream = digitalObject.getContent().read();
        String content = new String(FileUtils.writeInputStreamToBinary(stream));
        System.out.println("XCDL: " + content);
        return content;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#describe()
     */
    public ServiceDescription describe() {
        /* TODO: Set input formats to XCDL PUID when there is one. */
        return new ServiceDescription.Builder(NAME, Compare.class.getCanonicalName())
                .classname(this.getClass().getCanonicalName())
                .author("Fabian Steeg")
                .description(
                        "XCDL Comparison Service, which compares two Xcdl files generated by the Extractor tool."
                                + "This services is a wrapper for the Comparator command line tool developed at the UzK."
                                + "The Comparator allows to check how much information has been lost during a migration process."
                                + "To use the Comparator with a list of properties instead of .xcdl files, please use the XcdlCompareProperties service!")
                .serviceProvider("The Planets Consortium").inputFormats(
                        ComparatorWrapper.getSupportedInputFormats().toArray(new URI[] {})).build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#convert(eu.planets_project.services.datatypes.DigitalObject)
     */
    public List<Parameter> convert(final DigitalObject configFile) {
        InputStream inputStream = configFile.getContent().read();
        return new ComparatorConfigParser(inputStream).getProperties();
    }
}
