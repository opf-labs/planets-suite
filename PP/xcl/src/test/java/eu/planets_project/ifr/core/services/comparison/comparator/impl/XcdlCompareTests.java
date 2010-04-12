package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the comparator XCDL comparison service.
 * @author Fabian Steeg
 */
public final class XcdlCompareTests {
    
    private static final String WSDL = "/pserv-xcl/XcdlCompare?wsdl";

    @Test
    public void testDescribe() {
        Compare c = ServiceCreator.createTestService(XcdlCompare.QNAME, XcdlCompare.class, WSDL);
        ServiceDescription sd = c.describe();
        assertTrue("The service description should not be null", sd != null);
    }

    @Test
    public void xcdlComparison(){
        testWith(ComparatorWrapperTests.XCDL1, ComparatorWrapperTests.XCDL2, ComparatorWrapperTests.COCO_IMAGE);
    }
    
    @Test
    public void imageComparison(){
    	System.out.println("Testing direct image comparison.");
    	testWith(ComparatorWrapperTests.PNG, ComparatorWrapperTests.TIFF, ComparatorWrapperTests.COCO_IMAGE);
    	testWith(ComparatorWrapperTests.JPG, ComparatorWrapperTests.TIFF, null );
    }
    
    //@Test Not yet supported
    public void textComparison(){
    	System.out.println("Testing direct text/document comparison.");
        testWith(ComparatorWrapperTests.DOCX, ComparatorWrapperTests.PDF, ComparatorWrapperTests.COCO_TEXT);
    }

    private void testWith(String file1, String file2, String config) {
        testServices(new File(file1), new File(file2), config == null ? null : new File(config));
    }

    /**
     * Tests the services that use the actual value strings.
     * @param file1 The XCDL1 data
     * @param file2 The XCDL2 data
     * @param file3 The config data
     */
    protected void testServices(final File file1, final File file2, final File file3) {
        Compare c = ServiceCreator.createTestService(XcdlCompare.QNAME, XcdlCompare.class, WSDL);
        DigitalObject first = new DigitalObject.Builder(Content.byValue(file1)).build();
        DigitalObject second = new DigitalObject.Builder(Content.byValue(file2)).build();
        DigitalObject configFile = null;
        if( file3 != null ) configFile = new DigitalObject.Builder(Content.byValue(file3)).build();
        List<Property> properties = c.compare(first, second, configFile == null ? null : c.convert(configFile)).getProperties();
        ComparatorWrapperTests.check(properties);
    }
}
