package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.planets_project.ifr.core.services.comparison.comparator.config.ComparatorConfigParser;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompareProperties;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Prop;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the comparator property comparison service.
 * @author Fabian Steeg
 */
public final class XcdlComparePropertiesTests {

    private static final String WSDL = "/pserv-xcl/XcdlCompareProperties?wsdl";

    /**
     * Tests PP comparator comparison using the XCDL comparator.
     */
    @Test
    public void testService() {
        byte[] data1 = ByteArrayHelper.read(new File(
                ComparatorWrapperTests.XCDL1));
        byte[] data2 = ByteArrayHelper.read(new File(
                ComparatorWrapperTests.XCDL2));
        byte[] configData = ByteArrayHelper.read(new File(
                ComparatorWrapperTests.PCR_SINGLE));
        testServices(data1, data2, configData);
    }

    /**
     * Tests the services that use the actual value strings.
     * @param data1 The XCDL1 data
     * @param data2 The XCDL2 data
     * @param configData The config data
     */
    protected void testServices(final byte[] data1, final byte[] data2,
            final byte[] configData) {
        Compare<List<Prop<Object>>> c = ServiceCreator.createTestService(
                Compare.QNAME, XcdlCompareProperties.class, WSDL);
        /* The actual XCDL files: */
        DigitalObject[] objects = new DigitalObject[] {
                new DigitalObject.Builder(Content.byValue(data1)).build(),
                new DigitalObject.Builder(Content.byValue(data2)).build() };
        /* The actual config file: */
        DigitalObject configFile = new DigitalObject.Builder(Content
                .byValue(configData)).build();
        /* We now convert both into properties using our service: */
        List<List<Prop<Object>>> inputProps = new ArrayList<List<Prop<Object>>>();
        for (DigitalObject digitalObject : objects) {
            inputProps.add(c.convertInput(digitalObject));
        }
        List<Prop<Object>> configProps = new ArrayList<Prop<Object>>( 
                new ComparatorConfigParser(ByteArrayHelper.write(FileUtils
                        .writeInputStreamToBinary(configFile.getContent()
                                .read()))).getProperties());
        List<Prop> properties = new ArrayList<Prop>(c.compare(inputProps,
                configProps).getProperties());
        ComparatorWrapperTests.check(properties);
    }
}
