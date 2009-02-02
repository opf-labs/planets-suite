package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreator;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlParser;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigCreator;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.CompareProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Prop;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;

/**
 * XCDL Comparator service. Compares lists of properties.
 * <p/>
 * Not: this is work in progress.
 * @see XcdlComparePropertiesTests
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = XcdlCompareProperties.NAME, serviceName = CompareProperties.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.compare.CompareProperties")
@Stateless
public final class XcdlCompareProperties implements CompareProperties {
    /***/
    static final String NAME = "XcdlCompareProperties";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareProperties#compare(java.util.List,
     *      java.util.List)
     */
    public CompareResult compare(final List<ArrayList<Prop>> lists,
            final List<Prop> config) {
        List<ArrayList<Prop>> first = new ArrayList<ArrayList<Prop>>();
        first.add(lists.get(0));
        String xcdl = read(first).get(0);
        List<String> xcdls = read(lists.subList(1, lists.size()));
        String pcr = new ComparatorConfigCreator(config)
                .getComparatorConfigXml();
        String result = ComparatorWrapper.compare(xcdl, xcdls, pcr);
        List<Prop> props = propertiesFrom(result);
        return new CompareResult(props, new ServiceReport());
    }

    /**
     * @param result The comparator result
     * @return The properties found in the result XML
     */
    private List<Prop> propertiesFrom(final String result) {
        File file = ByteArrayHelper.write(result.getBytes());
        return new ResultPropertiesReader(file).getProperties();
    }

    /**
     * @param list The list of digital objects
     * @return A list of strings representing the content of the digital objects
     */
    private List<String> read(final List<ArrayList<Prop>> list) {
        List<String> result = new ArrayList<String>();
        for (List<Prop> xcdlProps : list) {
            String content = new XcdlCreator(xcdlProps).getXcdlXml();
            result.add(content);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareProperties#describe()
     */
    public ServiceDescription describe() {
        return new ServiceDescription.Builder("XCL property comparator", super
                .getClass().getName()).classname(super.getClass().getName())
                .author("Fabian Steeg").description(
                        "XCDL Comparison via Properties Service")
                .serviceProvider("The Planets Consortium").build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareProperties#convertInput(eu.planets_project.services.datatypes.DigitalObject)
     */
    public ArrayList<Prop> convertInput(final DigitalObject inputFile) {
        File file = ByteArrayHelper.write(FileUtils
                .writeInputStreamToBinary(inputFile.getContent().read()));
        return new ArrayList<Prop>(new XcdlParser(file).getProps());
    }
}
