package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreator;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlParser;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigCreator;
import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigParser;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.CompareProperties;
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
 * XCDL Comparator service. Compares CharacteriseResult objects.
 * <p/>
 * Note: this is work in progress.
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
     * @see eu.planets_project.services.compare.CompareCharacteriseResults#compare(eu.planets_project.services.characterise.CharacteriseResult,
     *      eu.planets_project.services.characterise.CharacteriseResult, java.util.List)
     */
    public CompareResult compare(final CharacteriseResult first, final CharacteriseResult second,
            final List<Parameter> config) {
        return compare(propertyLists(first), propertyLists(second), config);
    }

    /**
     * @param result The characterization result
     * @return A list of property lists, corresponding to a list of objects, each descriped by the properties
     */
    private List<List<Property>> propertyLists(final CharacteriseResult result) {
        ArrayList<List<Property>> list = new ArrayList<List<Property>>();
        if (result.getProperties().size() > 0) {
            list.add(result.getProperties());
            return list;
        } else if (result.getProperties().size() > 0) {
            for (CharacteriseResult embedded : result.getResults()) {
                list.addAll(propertyLists(embedded));
            }
        }
        return list;
    }

    /**
     * @param first The first one-level nested properties
     * @param second The second one-level nested properties
     * @param config The comparison configuration
     * @return The result of the comparison
     */
    private CompareResult compare(final List<List<Property>> first, final List<List<Property>> second,
            final List<Parameter> config) {
        String firstXcdl = new XcdlCreator(first).getXcdlXml();
        String secondXcdl = new XcdlCreator(second).getXcdlXml();
        String comparatorConfig = new ComparatorConfigCreator(config).getComparatorConfigXml();
        String result = ComparatorWrapper.compare(firstXcdl, Arrays.asList(secondXcdl), comparatorConfig);
        List<Property> props = propertiesFrom(result);
        return new CompareResult(props, new ServiceReport(Type.INFO, Status.SUCCESS, "OK"));
    }

    /**
     * @param result The comparator result
     * @return The properties found in the result XML
     */
    private List<Property> propertiesFrom(final String result) {
        File file = FileUtils.writeByteArrayToTempFile(result.getBytes());
        return new ResultPropertiesReader(file).getProperties();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareProperties#describe()
     */
    public ServiceDescription describe() {
        return new ServiceDescription.Builder(NAME, CompareProperties.class.getCanonicalName()).classname(
                this.getClass().getCanonicalName()).author("Fabian Steeg").description(
                "This services is a wrapper for the Comparator command line tool developed at the UzK."
                        + "It compares Property lists instead of .xcdl files. This way, it is possible"
                        + "to use the Comparator to compare the output of any Characterisation tool that could be"
                        + "converted to (or that is delivered as) a list of properties."
                        + "To enable this, a xcdl file is created on the fly for each list of"
                        + "properties and is used as input for the Comparator command line tool."
                        + "IMPORTANT NOTE: To use .xcdl files directly, please use the XcdlCompare service!")
                .serviceProvider("The Planets Consortium").build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareCharacteriseResults#convertInput(eu.planets_project.services.datatypes.DigitalObject)
     */
    public CharacteriseResult convertInput(final DigitalObject inputFile) {
        File file = FileUtils.writeByteArrayToTempFile(FileUtils
                .writeInputStreamToBinary(inputFile.getContent().read()));
        //TODO: pass InputStream to the parser instead (or the digital object?) 
        CharacteriseResult props = new XcdlParser(file).getProperties();
        if (props.getProperties().size() == 0) {
            throw new IllegalStateException("Could not parse any properties from: " + file.getAbsolutePath());
        }
        return props;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#convert(eu.planets_project.services.datatypes.DigitalObject)
     */
    public List<Parameter> convertConfig(final DigitalObject configFile) {
        return new ComparatorConfigParser(configFile.getContent().read()).getProperties();
    }

}
