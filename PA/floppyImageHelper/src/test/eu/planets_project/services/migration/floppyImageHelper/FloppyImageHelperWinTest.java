/**
 * 
 */
package eu.planets_project.services.migration.floppyImageHelper;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ZipResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * @author melmsp
 *
 */
public class FloppyImageHelperWinTest {
	
	static Migrate FLOPPY_IMAGE_HELPER;
	
	static String WSDL = "/pserv-pa-floppy-image-helper/FloppyImageHelperWin?wsdl";
	
	static String OUT_DIR_NAME = "FLOPPY_IMAGE_HELPER_TEST_OUT";
	
	static File OUT_DIR = null;
	
	static File FILES_TO_INJECT = new File("PA/floppyImageHelper/src/test/resources/input_files/for_injection");
	
	static File FILES_FOR_MODIFICATION = new File("PA/floppyImageHelper/src/test/resources/input_files/for_modification");
	
	static File FILES_FOR_MODIFICATION_WITH_ERROR = new File("PA/floppyImageHelper/src/test/resources/input_files/for_modification_with_error");
	
	static File FLOPPY_IMAGE = new File("PA/floppyImageHelper/src/test/resources/input_files/for_modification/FLOPPY144.IMA");

	FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
//		System.setProperty("pserv.test.context", "server");
//        System.setProperty("pserv.test.host", "localhost");
//        System.setProperty("pserv.test.port", "8080");
        
		// Config the logger:
        Logger.getLogger("").setLevel( Level.FINE );
        OUT_DIR = FileUtils.createWorkFolderInSysTemp(OUT_DIR_NAME); 
        FLOPPY_IMAGE_HELPER = ServiceCreator.createTestService(Migrate.QNAME, FloppyImageHelperWin.class, WSDL);
        
	}

	/**
	 * Test method for {@link eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelperWin#describe()}.
	 */
	@Test
	public void testDescribe() {
		ServiceDescription sd = FLOPPY_IMAGE_HELPER.describe();
		assertTrue("The ServiceDescription should not be NULL.", sd != null );
    	System.out.println("test: describe()");
    	System.out.println("--------------------------------------------------------------------");
    	System.out.println();
    	System.out.println("Received ServiceDescription from: " + FLOPPY_IMAGE_HELPER.getClass().getName());
    	System.out.println(sd.toXmlFormatted());
    	System.out.println("--------------------------------------------------------------------");
	}

	/**
	 * Test method for {@link eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelperWin#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)}.
	 */
	@Test
	public void testMigrateAndCreateImage() {
		System.out.println("****************************************************");
		System.out.println("* Testing: Create Floppy Image and Inject Files... *");
		System.out.println("****************************************************");
		ZipResult zipResult = FileUtils.createZipFileWithChecksum(FILES_TO_INJECT, OUT_DIR, "test.zip"); 
		File zipFile = zipResult.getZipFile();
		Content content = ImmutableContent.asStream(zipFile).withChecksum(zipResult.getChecksum());
        DigitalObject input = new DigitalObject.Builder(content).format(format.createExtensionUri("zip")).title("test.zip").build();
		List<Parameter> parameters = new ArrayList<Parameter> ();
		parameters.add(new Parameter("modifyImage", "false"));
		MigrateResult migrateResult = FLOPPY_IMAGE_HELPER.migrate(input, format.createExtensionUri("zip"), format.createExtensionUri("ima"), parameters);
		ServiceReport report = migrateResult.getReport();
		System.out.println(report);
		assertTrue("Resulting DigitalObject should NOT be NULL!!!", migrateResult.getDigitalObject()!=null);
		DigitalObject resultDigObj = migrateResult.getDigitalObject();
		File resultFile = new File(OUT_DIR, resultDigObj.getTitle());
		FileUtils.writeInputStreamToFile(resultDigObj.getContent().read(), resultFile);
	}
	
	
	@Test
	public void testMigrateExtractFilesFromFloppy() {
		System.out.println("********************************************");
		System.out.println("* Testing: Extract Files from Floppy Image *");
		System.out.println("********************************************");
		Content content = ImmutableContent.asStream(FLOPPY_IMAGE);
		DigitalObject input = new DigitalObject.Builder(content).format(format.createExtensionUri("ima")).title(FLOPPY_IMAGE.getName()).build();
		MigrateResult migrateResult = FLOPPY_IMAGE_HELPER.migrate(input, format.createExtensionUri("ima"), format.createExtensionUri("zip"), null);
		ServiceReport report = migrateResult.getReport();
		System.out.println(report);
		assertTrue("Resulting DigitalObject should NOT be NULL!!!", migrateResult.getDigitalObject()!=null);
		DigitalObject resultDigObj = migrateResult.getDigitalObject();
		File resultFile = new File(OUT_DIR, resultDigObj.getTitle());
		FileUtils.writeInputStreamToFile(resultDigObj.getContent().read(), resultFile);
		ImmutableContent resultContent = (ImmutableContent)resultDigObj.getContent();
		long resultChecksum = Long.parseLong(resultContent.getChecksum().getValue());
		FileUtils.extractFilesFromZipAndCheck(resultFile, OUT_DIR, resultChecksum);
	}
}
