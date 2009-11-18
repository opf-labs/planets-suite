package eu.planets_project.services.migration.floppyImageHelper.impl.utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.formats.api.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migration.floppyImageHelper.api.FloppyImageHelper;
import eu.planets_project.services.migration.floppyImageHelper.impl.FloppyImageHelperService;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.services.utils.ZipResult;
import eu.planets_project.services.utils.ZipUtils;

/**
 * @author Peter Melms
 *
 */
//@Stateless()
//@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
//@WebService(
//        name = FloppyImageHelperWin.NAME, 
//        serviceName = Migrate.NAME,
//        targetNamespace = PlanetsServices.NS,
//        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class UniversalFloppyImageHelper implements Migrate, FloppyImageHelper {
	
	private File TEMP_FOLDER = null;
	private String TEMP_FOLDER_NAME = "UFIH_TMP";
	
	private File EXTRACTED_FILES = null;
	private String sessionID = FileUtils.randomizeFileName("");
	private String EXTRACTED_FILES_DIR = "EXTRACTED_FILES" + sessionID;
	
	private String DEFAULT_INPUT_NAME = "inputFile" + sessionID;
	
	private String PROCESS_ERROR = "";
	private String PROCESS_OUT = "";
	
	private String br = System.getProperty("line.separator");
	
	private static FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
	
    private Log log = LogFactory.getLog(this.getClass());
    
    private Fat_Imgen fat_imgen = new Fat_Imgen();
    
    public UniversalFloppyImageHelper() {
    	// clean the temp folder for this app at startup...
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteAllFilesInFolder(TEMP_FOLDER);
		EXTRACTED_FILES = FileUtils.createFolderInWorkFolder(TEMP_FOLDER, EXTRACTED_FILES_DIR);
	}
    
    
    

    /* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#describe()
	 */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(FloppyImageHelperService.NAME, Migrate.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.description("This service is a wrapper for the 'Fat_Imgen' Commandline tool." + br +
        				"This tools is able to create Floppy disk images - 1.44 MB - from scratch, containing files of your choice." + br +
        				"This is the first possible direction. The other one is the Extraction of files from a floppy disk image." +
        				"This service accepts:" + br +
        				"1) ZIP files, containing the files you want to be written on the floppy image. The service will unpack the ZIP file and write the contained files to the floppy image, " +
        				"which is returned, if the files in the ZIP do not exceed the capacity limit of 1.44 MB." + br +
        				"2) a single file which should be written on the floppy image. This file could be of ANY type/format (except the '.ima/.img' type!)" + br +
        				"3) An '.IMA'/'.IMG' file. In this case, the service will extract all files from that floppy image and return a set of files (as a ZIP).");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("1.0");
        sd.tool( Tool.create(null, "Fat_Imgen (fat_imgen.exe)", "v2.1.1", null, "http://www.ohloh.net/p/fat_imgen"));
        sd.logo(URI.create("http://bits.ohloh.net/attachments/18083/floppy_64x64_med.png"));
        List<MigrationPath> pathways = new ArrayList<MigrationPath>();
        pathways.add(new MigrationPath(format.createExtensionUri("ZIP"), format.createExtensionUri("IMA"), null));
        pathways.add(new MigrationPath(format.createExtensionUri("ANY"), format.createExtensionUri("IMA"), null));
        pathways.add(new MigrationPath(format.createExtensionUri("IMA"), format.createExtensionUri("ZIP"), null));
//        pathways.add(new MigrationPath(format.createExtensionUri("IMG"), format.createExtensionUri("ZIP"), null));
        
        sd.paths(pathways.toArray(new MigrationPath[] {}));
        return sd.build();
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, List<Parameter> parameters) {
		
		FloppyHelperResult fat_imgen_result = null;
        String inFormat = format.getFirstExtension(inputFormat).toUpperCase();
		
		List<File> extractedFiles = null;

		String fileName = digitalObject.getTitle();
		
		DigitalObjectContent content = digitalObject.getContent();
		
		Checksum checksum = content.getChecksum();
		
//		long check = 0;
//		
//		if(checksum!=null) {
//			check = Long.parseLong(checksum.getValue());
//		}
//		else {
//			check = -1;
//		}
		
		if(fileName==null) {
			fileName = DEFAULT_INPUT_NAME + "." + inFormat;
		}
		
		File inputFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), TEMP_FOLDER, fileName);
		
		File imageFile = null;
		
		ZipResult zippedResult = null;
		
		if((inFormat.endsWith("IMA")) || inFormat.endsWith("IMG")) {
			fat_imgen_result = fat_imgen.openImageAndGetFiles(inputFile);
			if(fat_imgen_result.resultIsZip) {
				zippedResult = fat_imgen_result.getZipResult();
			}
			else {
				return this.returnWithErrorMessage(fat_imgen_result.getMessage(), null);
			}
			
//			DigitalObjectContent zipContent = Content.byReference(zippedResult.getZipFile())
//												 .withChecksum(zippedResult.getChecksum());
			
//			DigitalObject resultDigObj = new DigitalObject.Builder(zipContent)
//											.format(format.createExtensionUri("zip"))
//											.title(zippedResult.getZipFile().getName())
//											.build();
			
			DigitalObject resultDigObj = DigitalObjectUtils.createZipTypeDigitalObject(zippedResult.getZipFile(), zippedResult.getZipFile().getName(), true, true, false);


			ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, PROCESS_OUT);
			log.info("Created Service report...");
			return new MigrateResult(resultDigObj, report);
		}
		
		// Check if we have a ZIP file?
		if(inFormat.endsWith("ZIP")) {// x-fmt/263
			if(checksum!=null) {
				// if yes, extract files from this ZIP and use these files for floppy image creation
				 extractedFiles = ZipUtils.checkAndUnzipTo(inputFile, EXTRACTED_FILES, checksum);
			}
			else {
				extractedFiles = ZipUtils.unzipTo(inputFile, EXTRACTED_FILES);
			}
			fat_imgen_result = fat_imgen.createImageAndInjectFiles(extractedFiles);
			if(!fat_imgen_result.resultIsZip) {
				imageFile = fat_imgen_result.getResultFile();
//				log.info(vfdResult.getMessage());
			}
			else {
				return this.returnWithErrorMessage(fat_imgen_result.getError(), null);
			}
			
		}
		// the file in the digitalObject is NOT a Zip, so we have just one file to write ;-)
		// Put that in a List and pass it to the creation-method as usual...
		else {
			List<File> tmpList = new ArrayList<File>();
			tmpList.add(inputFile);
			
			fat_imgen_result = fat_imgen.createImageAndInjectFiles(tmpList);
			imageFile = fat_imgen_result.getResultFile();
			
			if(imageFile==null) {
				 return this.returnWithErrorMessage(PROCESS_ERROR, null);
			}
		}
		
		// If we have reached this line, we should have an image file created, so wrap a DigObj around that and return 
		// a MigrateResult... 
		DigitalObject resultDigObj = new DigitalObject.Builder(Content.byReference(imageFile))
										.format(outputFormat)
										.title(imageFile.getName())
										.build();
		
		ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, PROCESS_OUT);
        log.info("Created Service report...");
		return new MigrateResult(resultDigObj, report);
	}

	/**
	 * @param message an optional message on what happened to the service
	 * @param e the Exception e which causes the problem
	 * @return CharacteriseResult containing a Error-Report
	 */
	private MigrateResult returnWithErrorMessage(final String message,
	        final Exception e) {
	    if (e == null) {
	        return new MigrateResult(null, ServiceUtils
	                .createErrorReport(message));
	    } else {
	        return new MigrateResult(null, ServiceUtils
	                .createExceptionErrorReport(message, e));
	    }
	}
}
