/**
 * 
 */
package eu.planets_project.services.migration.floppyImageHelper;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migration.floppyImageHelper.impl.FloppyImageHelperService;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ZipResult;
import eu.planets_project.services.utils.ZipUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * @author melmsp
 *
 */
public class FloppyImageHelperServiceTest {
	
	static Migrate FLOPPY_IMAGE_HELPER;
	
	static String WSDL = "/pserv-pa-floppy-image-helper/FloppyImageHelperService?wsdl";
	
	static String OUT_DIR_NAME = "FLOPPY_IMAGE_HELPER_TEST_OUT";
	
	static File OUT_DIR = null;
	
	static File FILES_TO_INJECT = new File("PA/floppyImageHelper/src/test/resources/input_files/for_injection");
	
	static File FILES_FOR_MODIFICATION = new File("PA/floppyImageHelper/src/test/resources/input_files/for_modification");
	
	static File FILES_FOR_MODIFICATION_WITH_ERROR = new File("PA/floppyImageHelper/src/test/resources/input_files/for_modification_with_error");
	
	static File FLOPPY_IMAGE = new File("PA/floppyImageHelper/src/test/resources/input_files/for_modification/FLOPPY144.IMA");
	
	static File RESULT_FILE = null;

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
        Logger.getRootLogger().setLevel( Level.INFO );
        OUT_DIR = FileUtils.createWorkFolderInSysTemp(OUT_DIR_NAME); 
        FLOPPY_IMAGE_HELPER = ServiceCreator.createTestService(Migrate.QNAME, FloppyImageHelperService.class, WSDL);
        
	}
	
	@Test
	public void testListAvailableDriveLetters() {
		List<String> letters = FileUtils.listAvailableDriveLetters();
		for (String string : letters) {
			System.out.println(string);
		}
	}

	/**
	 * Test method for {@link eu.planets_project.services.migration.floppyImageHelper.impl.utils.FloppyImageHelperWin#describe()}.
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
	 * Test method for {@link eu.planets_project.services.migration.floppyImageHelper.impl.utils.FloppyImageHelperWin#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)}.
	 */
	@Test
	public void testMigrateAndCreateImage() {
		System.out.println("****************************************************");
		System.out.println("* Testing: Create Floppy Image and Inject Files... *");
		System.out.println("****************************************************");
		FileUtils.deleteAllFilesInFolder(OUT_DIR);
		ZipResult zipResult = ZipUtils.createZipAndCheck(FILES_TO_INJECT, OUT_DIR, "test.zip", false); 
		File zipFile = zipResult.getZipFile();
//		DigitalObjectContent content = Content.byReference(zipFile).withChecksum(zipResult.getChecksum());
        DigitalObject input = DigitalObjectUtils.createZipTypeDigitalObject(zipFile, zipFile.getName(), true, true, true);
		MigrateResult migrateResult = FLOPPY_IMAGE_HELPER.migrate(input, format.createExtensionUri("zip"), format.createExtensionUri("ima"), null);
		ServiceReport report = migrateResult.getReport();
		System.out.println(report);
		assertTrue("Resulting DigitalObject should NOT be NULL!!!", migrateResult.getDigitalObject()!=null);
		DigitalObject resultDigObj = migrateResult.getDigitalObject();
		RESULT_FILE = new File(OUT_DIR, resultDigObj.getTitle());
		FileUtils.writeInputStreamToFile(resultDigObj.getContent().read(), RESULT_FILE);
	}
	
	
	@Test
	public void testMigrateExtractFilesFromFloppy() {
		System.out.println("********************************************");
		System.out.println("* Testing: Extract Files from Floppy Image *");
		System.out.println("********************************************");
		DigitalObjectContent content = Content.byReference(RESULT_FILE);
		DigitalObject input = new DigitalObject.Builder(content).format(format.createExtensionUri("ima")).title(RESULT_FILE.getName()).build();
		MigrateResult migrateResult = FLOPPY_IMAGE_HELPER.migrate(input, format.createExtensionUri("ima"), format.createExtensionUri("zip"), null);
		ServiceReport report = migrateResult.getReport();
		System.out.println(report);
		assertTrue("Resulting DigitalObject should NOT be NULL!!!", migrateResult.getDigitalObject()!=null);
		DigitalObject resultDigObj = migrateResult.getDigitalObject();
		File resultFile = new File(OUT_DIR, resultDigObj.getTitle());
		FileUtils.writeInputStreamToFile(resultDigObj.getContent().read(), resultFile);
		DigitalObjectContent resultContent = resultDigObj.getContent();
//		long resultChecksum = Long.parseLong(resultContent.getChecksum().getValue());
		ZipUtils.checkAndUnzipTo(resultFile, OUT_DIR, resultContent.getChecksum());
	}
}
