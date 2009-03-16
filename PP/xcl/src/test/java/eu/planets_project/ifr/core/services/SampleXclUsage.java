package eu.planets_project.ifr.core.services;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacterise;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrate;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompare;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompareProperties;
import eu.planets_project.ifr.core.services.comparison.explorer.impl.XcdlCommonProperties;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.CommonProperties;
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
    private static final DigitalObject GIF = DigitalObject.create(
            Content.byReference(new File(ORIGINAL))).build();
    private static final DigitalObject JPG = DigitalObject.create(
            Content.byReference(new File(CONVERTED))).build();
    private static final DigitalObject CONFIG = DigitalObject.create(
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
        CompareResult result = comparison.compare(Arrays.asList(gifXcdl,
                jpgXcdl), configProperties);
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
        CompareResult compareResult = comparison.compare(Arrays.asList(gifXcdl,
                jpgXcdl), configProperties);
        /* Print the result: */
        System.out.println("Report: " + compareResult.getReport());
        List<Prop> properties = compareResult.getProperties();
        System.out.println("Results: " + properties);
    }

    /**
     * This is an API usage sketch of what using the XCL services via the
     * Characterise interface could look like once it's all put together.
     */
    /*
     * The details are not figured out yet, and this is supposed to be a
     * readable API usage sample, hence:
     */
    @SuppressWarnings("unchecked")
    // @Test (To be implemented)
    public void viaXcdlCharacterisation() {
        Assert.fail("Not completely implemented yet!");
        /*
         * We perform the characterisation, this time using the actual
         * Characterise interface. The advantage here, besides the less verbose
         * API compared to using the Migrate interface, is that we can exchange
         * the XcdlCharacterise implementation with a different Characterise
         * implementation, and compare their results.
         */
        Characterise characterisation = new XcdlCharacterise();
        CharacteriseResult gifProps = characterisation.characterise(GIF, null);
        CharacteriseResult jpgProps = characterisation.characterise(JPG, null);
        /* We set up the comparison. First, the values to compare: */
        List<List<Property>> propsToCompare = Arrays.asList(gifProps
                .getProperties(), jpgProps.getProperties());
        /*
         * Next, determine the intersection of common properties to be used to
         * configure the comparison of the values (this won't work yet, because
         * the properties here are different from the ones the comparator
         * currently expects):
         */
        CommonProperties commonProps = new XcdlCommonProperties();
        List<Prop> intersection = commonProps.intersection(
                Arrays.asList(GIF_ID, JPG_ID)).getProperties();
        /*
         * And finally we compare the properties of the files. The different
         * classes don't work together yet, but this is what it should look like
         * (then without the c1 and c2 conversion methods to make it compile
         * here):
         */
        CompareProperties comparison = new XcdlCompareProperties();
        CompareResult result = comparison.compare(c1(propsToCompare),
                c2(intersection));
        System.out.println("Result: " + result + " " + result.getProperties());

    }

    private List<Prop<Object>> c2(List<Prop> intersection) {
        throw new IllegalStateException("Dummy method not to be called!");
    }

    private List<ArrayList<Prop<Object>>> c1(List<List<Property>> propsToCompare) {
        throw new IllegalStateException("Dummy method not to be called!");
    }

}
