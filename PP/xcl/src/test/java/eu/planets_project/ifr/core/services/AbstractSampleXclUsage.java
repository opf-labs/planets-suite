package eu.planets_project.ifr.core.services;

import java.net.URI;
import java.util.List;

import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacterise;
import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlMigrate;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompare;
import eu.planets_project.ifr.core.services.comparison.comparator.impl.XcdlCompareProperties;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Abstract high level sample usage of the XCL services to compare two files. Which files actually are used is
 * determined by subclasses. For detailed usage of the individual services, see their corresponding tests.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public abstract class AbstractSampleXclUsage {

    private static final URI XCDL_ID = URI.create("planets:/xcdl"); // not used
    protected static final FormatRegistry REGISTRY = FormatRegistryFactory.getFormatRegistry();

    /** @return The configuration file. */
    protected abstract DigitalObject config();

    /** @return The files to use for testing. */
    protected abstract DigitalObject[] files();

    /** @return The IDs corresponding to the files. */
    protected abstract URI[] ids();

    /** @return The extractor parameters */
    protected abstract List<List<Parameter>> parameters();

    /** @param result The compare result to check */
    protected abstract void checkCompareResult(final CompareResult result);

    /** Sample usage via XCDL, using local Java objects. */
    @Test
    public void viaXcdlMigration() {
        /* We migrate both to XCDL (for details see XcdlMigrateTests): */
        Migrate migration = new XcdlMigrate();
        DigitalObject firstXcdl = migration.migrate(files()[0], ids()[0], XCDL_ID, parameters().get(0))
                .getDigitalObject();
        DigitalObject secondXcdl = migration.migrate(files()[1], ids()[1], XCDL_ID, parameters().get(1))
                .getDigitalObject();
        /* We compare both XCDL files (for details see XcdlCompareTests): */
        Compare comparison = new XcdlCompare();
        List<Parameter> configProperties = comparison.convert(config());
        CompareResult result = comparison.compare(firstXcdl, secondXcdl, configProperties);
        /* And print the result: */
        System.out.println("Result: " + result + " " + result.getProperties());
        checkCompareResult(result);
    }

    /** Sample usage via XCDL, using objects retrieved via the Web Services. */
    @Test
    public void viaXcdlMigrationWithServiceCreator() {
        /* Migrate to XCDL: */
        Migrate migration = ServiceCreator.createTestService(Migrate.QNAME, XcdlMigrate.class,
                "/pserv-xcl/XcdlMigrate?wsdl");
        DigitalObject firstXcdl = migration.migrate(files()[0], ids()[0], XCDL_ID, parameters().get(0))
                .getDigitalObject();
        DigitalObject secondXcdl = migration.migrate(files()[1], ids()[1], XCDL_ID, parameters().get(1))
                .getDigitalObject();
        /* Compare the XCDL files: */
        Compare comparison = ServiceCreator.createTestService(Compare.QNAME, XcdlCompare.class,
                "/pserv-xcl/XcdlCompare?wsdl");
        List<Parameter> configProperties = comparison.convert(config());
        CompareResult result = comparison.compare(firstXcdl, secondXcdl, configProperties);
        /* Print the result: */
        System.out.println("Report: " + result.getReport());
        List<Property> properties = result.getProperties();
        System.out.println("Results: " + properties);
        checkCompareResult(result);
    }

    /**
     * Sample usage of the XCL services via the Characterise interface.
     */
    @Test
    public void viaXcdlCharacterisation() {
        /*
         * We perform the characterisation, this time using the actual Characterise interface. The advantage here,
         * besides the less verbose API compared to using the Migrate interface, is that we can exchange the
         * XcdlCharacterise implementation with a different Characterise implementation, and compare their results in
         * the future (currently, only these two Implementation work together, i.e. XcdlCompareProperties will not work
         * with any Characterise implementation yet).
         */
        Characterise characterisation = new XcdlCharacterise();
        CharacteriseResult firstResult = characterisation.characterise(files()[0], null);
        CharacteriseResult secondResult = characterisation.characterise(files()[1], null);
        /*
         * We compare the properties of the files, using the config as parameters:
         */
        CompareProperties comparison = new XcdlCompareProperties();
        List<Parameter> configProperties = comparison.convertConfig(config());
        CompareResult result = comparison.compare(firstResult, secondResult, configProperties);
        System.out.println("Result: " + result + " " + result.getProperties());
        checkCompareResult(result);
    }

    /**
     * Sample usage of the XCL services via the Characterise interface, using remote objects retrieved via web service.
     */
    @Test
    public void viaXcdlCharacterisationWithServiceCreator() {
        /* Characterise: */
        Characterise characterisation = ServiceCreator.createTestService(Characterise.QNAME, XcdlCharacterise.class,
                "/pserv-xcl/XcdlCharacterise?wsdl");
        CharacteriseResult firstResult = characterisation.characterise(files()[0], null);
        CharacteriseResult secondResult = characterisation.characterise(files()[1], null);
        /* CompareProperties: */
        CompareProperties comparison = ServiceCreator.createTestService(CompareProperties.QNAME,
                XcdlCompareProperties.class, "/pserv-xcl/XcdlCompareProperties?wsdl");
        List<Parameter> configProperties = comparison.convertConfig(config());
        CompareResult result = comparison.compare(firstResult, secondResult, configProperties);
        System.out.println("Result: " + result + " " + result.getProperties());
        checkCompareResult(result);
    }
}
