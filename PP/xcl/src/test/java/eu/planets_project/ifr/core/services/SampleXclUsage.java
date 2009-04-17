package eu.planets_project.ifr.core.services;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacterise;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrate;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompare;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompareProperties;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Prop;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * High level sample usage of the XCL services to compare two files: an original
 * GIF file and a converted JPG version of the file. For detailed usage of the
 * individual services, see their corresponding tests.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class SampleXclUsage {
    /* Given the input file, the output file and a comparator config file: */
    private static final String RESOURCES = "PP/xcl/src/test/resources/";
    private static final String SAMPLES = RESOURCES + "test_samples/";
    private static final String ORIGINAL = SAMPLES + "gif/basketball.gif";
    private static final String CONVERTED = SAMPLES + "jpeg/basketball.jpg";
    private static final String COCO = RESOURCES + "sampleComparatorConfig.xml";
    /* We wrap them as digital objects for later usage: */
    private static final DigitalObject GIF = new DigitalObject.Builder(Content
            .byReference(new File(ORIGINAL))).build();
    private static final DigitalObject JPG = new DigitalObject.Builder(Content
            .byReference(new File(CONVERTED))).build();
    private static final DigitalObject CONFIG = new DigitalObject.Builder(
            Content.byReference(new File(COCO))).build();
    /* We get a PRONOM ID for the original and the converted file: */
    private static final FormatRegistry REGISTRY = FormatRegistryFactory
            .getFormatRegistry();
    private static final URI GIF_ID = REGISTRY.getURIsForExtension("gif")
            .iterator().next();
    private static final URI JPG_ID = REGISTRY.getURIsForExtension("jpg")
            .iterator().next();
    /* We identify the XCDL format somehow: */
    private static final URI XCDL_ID = URI.create("planets:/xcdl"); // not used

    /** Sample usage via XCDL, using local Java objects. */
    @Test
    public void viaXcdlMigration() {
        /* We migrate both to XCDL (for details see XcdlMigrateTests): */
        Migrate migration = new XcdlMigrate();
        DigitalObject gifXcdl = migration.migrate(GIF, GIF_ID, XCDL_ID, null)
                .getDigitalObject();
        DigitalObject jpgXcdl = migration.migrate(JPG, JPG_ID, XCDL_ID, null)
                .getDigitalObject();
        /* We compare both XCDL files (for details see XcdlCompareTests): */
        Compare comparison = new XcdlCompare();
        List<Prop<Object>> configProperties = comparison.convert(CONFIG);
        CompareResult result = comparison.compare(gifXcdl, jpgXcdl,
                configProperties);
        /* And print the result: */
        System.out.println("Result: " + result + " " + result.getProperties());
    }

    /** Sample usage via XCDL, using objects retrieved via the Web Services. */
    @Test
    public void viaXcdlMigrationWithServiceCreator() {
        /* Migrate to XCDL: */
        Migrate migration = ServiceCreator.createTestService(Migrate.QNAME,
                XcdlMigrate.class, "/pserv-xcl/XcdlMigrate?wsdl");
        DigitalObject gifXcdl = migration.migrate(GIF, GIF_ID, XCDL_ID, null)
                .getDigitalObject();
        DigitalObject jpgXcdl = migration.migrate(JPG, JPG_ID, XCDL_ID, null)
                .getDigitalObject();
        /* Compare the XCDL files: */
        Compare comparison = ServiceCreator.createTestService(Compare.QNAME,
                XcdlCompare.class, "/pserv-xcl/XcdlCompare?wsdl");
        List<Prop<Object>> configProperties = comparison.convert(CONFIG);
        CompareResult compareResult = comparison.compare(gifXcdl, jpgXcdl,
                configProperties);
        /* Print the result: */
        System.out.println("Report: " + compareResult.getReport());
        List<Property> properties = compareResult.getProperties();
        System.out.println("Results: " + properties);
    }

    /**
     * Sample usage of the XCL services via the Characterise interface.
     */
    @Test
    public void viaXcdlCharacterisation() {
        /*
         * We perform the characterisation, this time using the actual
         * Characterise interface. The advantage here, besides the less verbose
         * API compared to using the Migrate interface, is that we can exchange
         * the XcdlCharacterise implementation with a different Characterise
         * implementation, and compare their results in the future (currently,
         * only these two Implementation work together, i.e.
         * XcdlCompareProperties will not work with any Characterise
         * implementation yet).
         */
        Characterise characterisation = new XcdlCharacterise();
        CharacteriseResult gifResult = characterisation.characterise(GIF, null);
        CharacteriseResult jpgResult = characterisation.characterise(JPG, null);
        /*
         * We set up the comparison. First, the values to compare:
         */
        List<Property> gifProps = gifResult.getProperties();
        List<Property> jpgProps = jpgResult.getProperties();
        /*
         * Then we compare the properties of the files. This is still
         * preliminary as the interfaces will change (List<Parameter> as the
         * config):
         */
        CompareProperties comparison = new XcdlCompareProperties();
        List<Prop<Object>> configProperties = comparison.convertConfig(CONFIG);
        CompareResult result = comparison.compare(gifProps, jpgProps,
                configProperties);
        System.out.println("Result: " + result + " " + result.getProperties());
    }
}
