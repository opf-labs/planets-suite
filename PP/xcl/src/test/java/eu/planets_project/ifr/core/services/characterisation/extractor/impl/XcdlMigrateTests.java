package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Test of the extractor (local and remote) using binaries. TODO: clean up both
 * local and in the data registry after the tests
 * @author Peter Melms
 * @author Fabian Steeg
 */
public class XcdlMigrateTests {

    /***/
    static final String WSDL = "/pserv-xcl/XcdlMigrate?wsdl";
    /***/
    // static String xcelString;
    // /***/
    // static File outputXcdl;
    // /***/
    // static byte[] binary;
    /**
     * the service
     */
    static Migrate extractor;

    static String TEST_OUT = null;

    static MigrationPath[] migrationPaths;

    static File testOutFolder = null;

    private static final FormatRegistry format = FormatRegistryFactory
            .getFormatRegistry();

    /**
     * Set up the testing environment: create files and directories for testing.
     */
    @BeforeClass
    public static void setup() {
        TEST_OUT = XcdlMigrateUnitHelper.XCDL_EXTRACTOR_LOCAL_TEST_OUT;

        testOutFolder = FileUtils.createWorkFolderInSysTemp(TEST_OUT);

        extractor = ServiceCreator.createTestService(Migrate.QNAME,
                XcdlMigrate.class, WSDL);

        migrationPaths = extractor.describe().getPaths().toArray(
                new MigrationPath[] {});
    }

    /**
     * test describe method
     */
    @Test
    public void testDescribe() {
        ServiceDescription sd = extractor.describe();
        System.out.println("test: describe()");
        System.out
                .println("--------------------------------------------------------------------");
        System.out.println();
        System.out.println("Received ServiceDescription from: "
                + extractor.getClass().getName());
        assertTrue("The ServiceDescription should not be NULL.", sd != null);
        System.out.println(sd.toXmlFormatted());
        System.out
                .println("--------------------------------------------------------------------");
    }

    @Test
    public void testMigration() throws URISyntaxException {
        testPath(migrationPaths[0]);
    }

    @Test
    public void testRejectInvalidFormat() {
        DigitalObject o = new DigitalObject.Builder(ImmutableContent
                .byValue(new byte[] {})).build();
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        /* And we can't migrate any format (= no format set): */
        MigrateResult result = extractor.migrate(o, null, null, null);
        Assert.assertEquals(Type.ERROR, result.getReport().getType());
        Assert.assertEquals(null, result.getDigitalObject());
        /* And we can't migrate unsupported formats: */
        result = extractor.migrate(o, registry.createExtensionUri("svg"), null,
                null);
        Assert.assertEquals(Type.ERROR, result.getReport().getType());
        Assert.assertEquals(null, result.getDigitalObject());
    }

    protected void testPath(MigrationPath path) throws URISyntaxException {
        URI inputFormat = path.getInputFormat();
        URI outputFormat = path.getOutputFormat();

        System.out.println();
        System.out.println("Testing migrationPath: ["
                + inputFormat.toASCIIString() + " --> "
                + outputFormat.toASCIIString() + "]");
        System.out.println();

        System.out
                .println("PARAMS: disableNormData = FALSE, enableRawData = FALSE, XCEL = YES");

        if (inputFormat.toASCIIString().equalsIgnoreCase("planets:fmt/ext/gif")
                || inputFormat.toASCIIString().equalsIgnoreCase(
                        "planets:fmt/ext/bmp")
                || inputFormat.toASCIIString().equalsIgnoreCase(
                        "planets:fmt/ext/jpg")
                || inputFormat.toASCIIString().equalsIgnoreCase(
                        "planets:fmt/ext/jpeg")) {
            System.err
                    .println("NOTE: NO Xcel will be passed for this input format. "
                            + "Extractor will find the proper one itself!");
        }

        List<Parameter> parameters = createParameters(false, false,
                getTestXCEL(format.getFirstExtension(inputFormat)));
        testMigrate(inputFormat, outputFormat, parameters);
        System.out.println("*******************");
        System.out.println();

        System.out
                .println("PARAMS: disableNormData = FALSE, enableRawData = FALSE, XCEL = NO");
        parameters = createParameters(false, false, null);
        testMigrate(inputFormat, outputFormat, parameters);
        System.out.println("*******************");
        System.out.println();

        System.out
                .println("PARAMS: disableNormData = TRUE, enableRawData = FALSE, XCEL = YES");

        if (inputFormat.toASCIIString().equalsIgnoreCase("planets:fmt/ext/gif")
                || inputFormat.toASCIIString().equalsIgnoreCase(
                        "planets:fmt/ext/bmp")
                || inputFormat.toASCIIString().equalsIgnoreCase(
                        "planets:fmt/ext/jpg")
                || inputFormat.toASCIIString().equalsIgnoreCase(
                        "planets:fmt/ext/jpeg")) {
            System.err
                    .println("NOTE: NO Xcel will be passed for this input format. "
                            + "Extractor will find the proper one itself!");
        }

        parameters = createParameters(true, false, getTestXCEL(format
                .getFirstExtension(inputFormat)));
        testMigrate(inputFormat, outputFormat, parameters);
        System.out.println("*******************");
        System.out.println();

        System.out
                .println("PARAMS: disableNormData = TRUE, enableRawData = FALSE, XCEL = NO");
        parameters = createParameters(true, false, null);
        testMigrate(inputFormat, outputFormat, parameters);
        System.out.println("*******************");
        System.out.println();
    }

    private List<Parameter> createParameters(boolean disableNormDataFlag,
            boolean enableRawDataFlag, String optionalXCELString) {
        List<Parameter> parameterList = new ArrayList<Parameter>();

        if (disableNormDataFlag) {
            Parameter normDataFlag = new Parameter.Builder(
                    "disableNormDataInXCDL", "-n")
                    .description(
                            "Disables NormData output in result XCDL. Reduces file size. Allowed value: '-n'")
                    .build();
            parameterList.add(normDataFlag);
        }

        if (enableRawDataFlag) {
            Parameter enableRawData = new Parameter.Builder(
                    "enableRawDataInXCDL", "-r")
                    .description(
                            "Enables the output of RAW Data in XCDL file. Allowed value: '-r'")
                    .build();
            parameterList.add(enableRawData);
        }

        if (optionalXCELString != null) {
            Parameter xcelStringParam = new Parameter.Builder(
                    "optionalXCELString", optionalXCELString)
                    .description(
                            "Could contain an optional XCEL String which is passed to the Extractor tool.\n\r"
                                    + "If no XCEL String is passed, the Extractor tool will try to  find the corresponding XCEL himself.")
                    .build();
            parameterList.add(xcelStringParam);
        }

        return parameterList;
    }

    private String getTestXCEL(String srcExtension) {
        if (srcExtension.equalsIgnoreCase("TIFF")) {
            return FileUtils
                    .readTxtFileIntoString(XcdlMigrateUnitHelper.TIFF_XCEL);
        }

        if (srcExtension.equalsIgnoreCase("BMP")) {
            return null;
            // return FileUtils
            // .readTxtFileIntoString(XcdlMigrateUnitHelper.BMP_XCEL);
        }

        if (srcExtension.equalsIgnoreCase("GIF")) {
            return null;
            // return FileUtils
            // .readTxtFileIntoString(XcdlMigrateUnitHelper.GIF_XCEL);
        }

        if (srcExtension.equalsIgnoreCase("PDF")) {
            return FileUtils
                    .readTxtFileIntoString(XcdlMigrateUnitHelper.PDF_XCEL);
        }

        if (srcExtension.equalsIgnoreCase("JPEG")
                || srcExtension.equalsIgnoreCase("JPG")) {
            return null;
            // return FileUtils
            // .readTxtFileIntoString(XcdlMigrateUnitHelper.JPEG_XCEL);
        }

        if (srcExtension.equalsIgnoreCase("PNG")) {
            return FileUtils
                    .readTxtFileIntoString(XcdlMigrateUnitHelper.PNG_XCEL);
        }

        // if (srcExtension.equalsIgnoreCase("DOC")) {
        // return XcdlExtractorUnitHelper.DOC_INPUT;
        // }
        return null;
    }

    private File getTestFile(String srcExtension) {

        if (srcExtension.equalsIgnoreCase("TIFF")) {
            return XcdlMigrateUnitHelper.TIFF_INPUT;
        }

        if (srcExtension.equalsIgnoreCase("BMP")) {
            return XcdlMigrateUnitHelper.BMP_INPUT;
        }

        if (srcExtension.equalsIgnoreCase("GIF")) {
            return XcdlMigrateUnitHelper.GIF_INPUT;
        }

        if (srcExtension.equalsIgnoreCase("PDF")) {
            return XcdlMigrateUnitHelper.PDF_INPUT;
        }

        if (srcExtension.equalsIgnoreCase("JPEG")
                || srcExtension.equalsIgnoreCase("JPG")) {
            return XcdlMigrateUnitHelper.JPEG_INPUT;
        }

        if (srcExtension.equalsIgnoreCase("PNG")) {
            return XcdlMigrateUnitHelper.PNG_INPUT;
        }

        // if (srcExtension.equalsIgnoreCase("DOC")) {
        // return XcdlExtractorUnitHelper.DOC_INPUT;
        // }
        return null;
    }

    private DigitalObject createDigitalObject(String srcExtension) {

        File inputFile = getTestFile(srcExtension);

        DigitalObject input = null;

        try {
            input = new DigitalObject.Builder(ImmutableContent
                    .byValue(inputFile)).permanentUrl(
                    new URL("http://xcdlExtractorMigrationTest.eu")).title(
                    inputFile.getName()).build();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return input;
    }

    private void testMigrate(URI inputFormat, URI outputFormat,
            List<Parameter> parameters) {
        String extension = format.getFirstExtension(inputFormat);
        DigitalObject digObj = createDigitalObject(extension);

        MigrateResult mr = extractor.migrate(digObj, inputFormat, outputFormat,
                parameters);

        ServiceReport sr = mr.getReport();

        if (sr.getType() == Type.ERROR) {
            System.err.println("FAILED: " + sr);
        } else {
            System.out.println("Got Report: " + sr);

            DigitalObject doOut = mr.getDigitalObject();

            assertTrue("Resulting digital object is null.", doOut != null);

            File formatFolder = FileUtils.createFolderInWorkFolder(
                    testOutFolder, extension);

            File result = FileUtils.writeInputStreamToFile(doOut.getContent()
                    .read(), formatFolder, "xcdlMigrateTest_" + extension
                    + ".xcdl");

            System.out.println("Resulting file size: " + result.length()
                    + " KB.");
            System.out.println("Resulting file path: "
                    + result.getAbsolutePath());

            System.out.println("Result: " + doOut);
            System.out.println("Result.content: " + doOut.getContent());
        }
    }

    // private URI getUriForFile(File testFile) {
    // String fileName = testFile.getAbsolutePath();
    // String testFileExtension = null;
    // if (fileName.contains(".")) {
    // testFileExtension = fileName
    // .substring(fileName.lastIndexOf(".") + 1);
    // } else {
    // System.err.println("Could not find file extension!!!");
    // return null;
    // }
    // return format.createExtensionUri(testFileExtension);
    // }

    @SuppressWarnings("unused")
    private DigitalObject createDigitalObjectByReference(URL permanentURL,
            URL reference) {
        DigitalObject digObj = new DigitalObject.Builder(ImmutableContent
                .byReference(reference)).build();
        return digObj;
    }
}
