package eu.planets_project.services.migration.openjpeg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the digital object migration functionality.
 *
 * @author Sven Schlarb <shsschlarb-planets@yahoo.de>
 */
public final class OpenJpegMigrationTest extends TestCase {

    /* The location of this service when deployed. */
    String wsdlLoc = "/pserv-pa-openjpeg/OpenJpegMigration?wsdl";

    /* A holder for the object to be tested */
    Migrate dom = null;

    List<String> formats = null;
    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        formats = new ArrayList<String>();
        formats.add("tif");
        formats.add("jp2");
        dom = ServiceCreator.createTestService(Migrate.QNAME, OpenJpegMigration.class, wsdlLoc);
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test the Description method.
     */
    @Test
    public void testDescribe() {
        ServiceDescription desc = dom.describe();
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
        System.out.println("Recieved service description: \n\n" + desc.toXmlFormatted());
    }

    /**
     * test for all migrations
     * @throws IOException
     */
    @Test
    public void testMigrateAll() throws IOException {
        String origExt = null;
        String destExt = null;
        // Tests will be executed for 1 set of test files of the formats
        // that the openjpeg service wrapper supports:
        // demonstration.jpg, demonstration.jp2
        for (Iterator<String> itr1 = formats.iterator(); itr1.hasNext();) {
            origExt = (String) itr1.next();
            for (Iterator<String> itr2 = formats.iterator(); itr2.hasNext();)
            {
                destExt = (String) itr2.next();
                // do the migration only if original file extension differs
                // from destination file extension
                if( !origExt.equalsIgnoreCase(destExt) )
                {
                    System.out.println("Do migration test from "+origExt+" to "+destExt);
                    doMigration(origExt,destExt, null);
                }
            }
        }
    }

    private void doMigration(String origExt, String destExt, List<Parameter> params) throws IOException
    {
        // Test file name
        String inTestFileName = "PA/openjpeg/test/testfiles/demonstration." + origExt.toLowerCase();
        File inTestFile = new File(inTestFileName);
        assertTrue("Test file "+inTestFile+" does not exist!", inTestFile.exists());
        // Output file name
        //String outTestFileName = "PA/openjpeg/test/testfiles/generatedfiles/planetsMigrate"+origExt+"to"+destExt+String.valueOf(cycle)+"."+destExt.toLowerCase();
        String resFileDir = "PA/openjpeg/test/testfiles/generatedfiles/";
        String resFileName = "planetsMigrate"+origExt.toUpperCase()+"to"+destExt.toUpperCase()+"."+destExt.toLowerCase();
        byte[] binary = FileUtils.readFileToByteArray(new File(inTestFileName));
        DigitalObject input = new DigitalObject.Builder(Content.byValue(binary)).build();
        FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
        MigrateResult mr = dom.migrate(input, format.createExtensionUri(origExt), format.createExtensionUri(destExt), params);
        DigitalObject doOut = mr.getDigitalObject();
        assertTrue("Resulting digital object is null for planetsMigrate"+origExt+"to"+destExt+".", doOut != null);
        File resultFile = new File(resFileDir+resFileName);
        DigitalObjectUtils.toFile(doOut, resultFile);
        assertTrue("Result file was not created successfully!", resultFile.exists());
    }
}
