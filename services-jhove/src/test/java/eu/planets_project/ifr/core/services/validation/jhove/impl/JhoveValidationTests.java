package eu.planets_project.ifr.core.services.validation.jhove.impl;

import static eu.planets_project.services.utils.test.TestFile.testValidation;

import java.io.File;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.test.TestFile;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * Tests of the JHOVE validation functionality.
 * @author Fabian Steeg
 */
public class JhoveValidationTests {
    static Validate jhove;

    /**
     * Tests JHOVE validation using a local JhoveValidation instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Local:");
        jhove = new JhoveValidation();
    }

    /**
     * Test the describe() method.
     */
    @Test
    public void testServiceDescription() {
        ServiceDescription description = new JhoveValidation().describe();
        Assert.assertTrue("We have no supported pronom IDs", description
                .getInputFormats().size() > 0);
        System.out.println(description.toXmlFormatted());
    }
    
    /*
     * To get more informative test reports, we wrap every enum element into its
     * own test. We could iterate over the enum elements instead (see below).
     */
    @Test public void testXml() { test(TestFile.XML, jhove); }
    @Test public void testPdf() { test(TestFile.PDF, jhove); }
    @Test public void testGif() { test(TestFile.GIF, jhove); }
    @Test public void testJpg() { test(TestFile.JPG, jhove); }
    //@Test public void testTif() { test(TestFile.TIF, jhove); }
    //@Test public void testWav() { test(TestFile.WAV, jhove); }
    @Test public void testTxt() { test(TestFile.TXT, jhove); }
    @Test public void testHtml(){ test(TestFile.HTML, jhove);}
    //@Test public void testAiff(){ test(TestFile.AIFF, jhove);}

    private void test(TestFile f, Validate validate) {
        boolean b = testValidation(f, validate);
        Assert.assertTrue("Validation failed for: " + f, b);
    }
    // TODO Improve tests
//    @Test
//    public void testUnsupported() throws MalformedURLException {
//        ValidateResult vr = jhove.validate(new DigitalObject.Builder(Content
//                .byReference(new File(TestFile.BMP.getLocation()).toURI()
//                        .toURL())).build(), TestFile.BMP.getTypes().iterator()
//                .next(), null);
//        ServiceReport report = vr.getReport();
//        /*
//         * If validation was attempted for an unsupported format, the report
//         * will be of type ERROR:
//         */
//        Assert.assertEquals(ServiceReport.Type.ERROR, report.getType());
//        /* More info is available in the report message: */
//        System.err.println("Report message: " + report.getMessage());
//    }
//    
    @Test
    public void testInvalid() throws MalformedURLException {
        ValidateResult vr = jhove.validate(new DigitalObject.Builder(Content
                .byReference(new File(TestFile.XML.getLocation()).toURI()
                        .toURL())).build(), TestFile.PDF.getTypes().iterator()
                .next(), null);
        Assert.assertFalse("Invalid should be invalidated; ", vr
                .isValidInRegardToThisFormat()
                && vr.isOfThisFormat());
    }

}
