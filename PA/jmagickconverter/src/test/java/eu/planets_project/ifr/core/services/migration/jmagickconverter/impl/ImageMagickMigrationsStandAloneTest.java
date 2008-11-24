package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;

import org.junit.BeforeClass;

import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.utils.test.ServiceCreator;

public class ImageMagickMigrationsStandAloneTest extends
		ImageMagickMigrationsLocalTest {
	

	@BeforeClass
    public static void setup() {
		System.out.println("Running ImageMagickMigrations STANDALONE tests...");
		System.out.println("*************************************************");
		
		TEST_OUT = ImageMagickMigrationsTestHelper.STANDALONE_TEST_OUT;
		
    	System.setProperty("pserv.test.context", "Standalone");

    	imageMagick = ServiceCreator.createTestService(Migrate.QNAME, ImageMagickMigrations.class, wsdlLocation);
    	compressionTypes[0] = "Undefined Compression";
		compressionTypes[1] = "No Compression";
		compressionTypes[2] = "BZip Compression";
		compressionTypes[3] = "Fax Compression";
		compressionTypes[4] = "Group4 Compression";
		compressionTypes[5] = "JPEG Compression";
		compressionTypes[6] = "JPEG2000 Compression";
		compressionTypes[7] = "LosslessJPEG Compression";
		compressionTypes[8] = "LZW Compression";
		compressionTypes[9] = "RLE Compression";
		compressionTypes[10] = "Zip Compression";
    }
	
}
