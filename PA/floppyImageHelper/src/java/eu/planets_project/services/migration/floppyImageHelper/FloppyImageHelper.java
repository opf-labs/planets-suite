package eu.planets_project.services.migration.floppyImageHelper;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.services.utils.ZipResult;

/**
 * @author Peter Melms
 *
 */
@Stateless()
@Local(Migrate.class)
@Remote(Migrate.class)

@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = FloppyImageHelper.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class FloppyImageHelper implements Migrate {
	
	public FloppyImageHelper() {
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteTempFiles(TEMP_FOLDER, log);
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		EXTRACTED_FILES_DIR = FileUtils.createFolderInWorkFolder(TEMP_FOLDER, EXTRACTION_OUT_FOLDER_NAME);
	}
	                 
	public static final String NAME = "FloppyImageHelper";
	
//	private static File TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp("FLOPPY_IMAGE_HELPER_TMP");
	private static File TEMP_FOLDER = null;
	private static String TEMP_FOLDER_NAME = "FLOPPY_IMAGE_HELPER";
	
	private static File EXTRACTED_FILES_DIR = null;
	private static String EXTRACTION_OUT_FOLDER_NAME = "EXTRACTED_FILES";
	private static String FLOPPY_IMAGE_TOOLS_HOME = System.getenv("FLOPPY_IMAGE_TOOLS_HOME");
	private static String DEFAULT_INPUT_NAME = "inputFile";
	private static String INPUT_EXT = null;
	private static String OUTPUT_EXT = ".ima";
	
	private static String DEFAULT_FLOPPY_IMAGE_NAME = "floppy144.ima";
	private static File TOOL_DIR = null;
	private static final long FLOPPY_SIZE = 1474560;
	
	private static boolean MODIFY_IMAGE = false;
	private static boolean ENABLE_EXTRACTION_MODE = false;
	
	private static String PROCESS_ERROR = null;
	private static String PROCESS_OUT = null;
	
    private PlanetsLogger log = PlanetsLogger.getLogger(this.getClass());

	/* (non-Javadoc)
	 * @see eu.planets_project.services.PlanetsService#describe()
	 */
	public ServiceDescription describe() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
	 */
	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, List<Parameter> parameters) {
		
		if(FLOPPY_IMAGE_TOOLS_HOME == null) {
			log.error("FLOPPY_IMAGE_TOOLS_HOME = null! " +
					"\nCould not find floppy image tools! " +
					"\nPlease install 'extract' and 'fat_imgen' on your system and point a System variable to the installation folder!" +
					"\nOtherwise this service will carry on to refuse to do its work!");
			return this.returnWithErrorMessage("FLOPPY_IMAGE_TOOLS_HOME is NOT set! Nothing done, sorry!", null);
		}
		else {
			TOOL_DIR = new File(FLOPPY_IMAGE_TOOLS_HOME);
		}
		
		String inFormat = getFormatExtension(inputFormat).toUpperCase();
		String outFormat = getFormatExtension(outputFormat).toUpperCase();
		
		for (Parameter parameter : parameters) {
			if(parameter.name.equalsIgnoreCase("modifyImage")) {
				if(parameter.value.equalsIgnoreCase("true")) {
					MODIFY_IMAGE = true;
				}
			}
		}
		
		List<File> extractedFiles = null;

		String fileName = digitalObject.getTitle();
		
		Content content = (Content)digitalObject.getContent();
		
		Checksum checksum = content.getChecksum();
		
		long check = Long.parseLong(checksum.getValue());
		
		if(fileName==null) {
			INPUT_EXT = getFormatExtension(inputFormat);
			fileName = DEFAULT_INPUT_NAME + "." + INPUT_EXT;
		}
		
		File inputFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), TEMP_FOLDER, fileName);
		
		File imageFile = null;
		
		File floppyImage = null;
		
		ZipResult zippedResult = null;
		
		if(inFormat.endsWith(".IMA")) {
			zippedResult = this.extractFilesFromFloppyImage(inputFile, EXTRACTED_FILES_DIR);
			
			Content zipContent = Content.asStream(zippedResult.getZipFile());
			zipContent.setChecksum(new Checksum("Adler32", Long.toString(zippedResult.getChecksum())));
			
			DigitalObject resultDigObj = new DigitalObject.Builder(zipContent)
			.format(Format.extensionToURI("zip"))
			.title(zippedResult.getZipFile().getName())
			.build();

			ServiceReport report = new ServiceReport();
			report.setErrorState(0);
			log.info("Created Service report...");
			return new MigrateResult(resultDigObj, report);
			
		}
		
		// Check if we have a ZIP file?
		if(inFormat.endsWith("ZIP")) {// x-fmt/263
			// if yes, extract files from this ZIP and use these files for floppy image creation
			 extractedFiles = FileUtils.extractFilesFromZipAndCheck(inputFile, TEMP_FOLDER, check);
			 
			 // If the modifyImage parameter has been passed, look for an ".ima" file and make that
			 // the image that will be modified.
			 if(MODIFY_IMAGE) {
				 for (File file : extractedFiles) {
					 String name = file.getName();
					 name = name.toUpperCase();
					 // Success! We have found an "ima" file
					 if(name.endsWith(".IMA") || name.endsWith(".IMG")) {
						 floppyImage = file;
						 extractedFiles.remove(file);
						 break;
					 }
					 // otherwise we pass NULL to the addFilesToFloppyImage method which will then
					 // create a new image from scratch and write all extracted files to it!
				}
				imageFile = this.addFilesToFloppyImage(floppyImage, extractedFiles);
				if(imageFile==null) {
					return this.returnWithErrorMessage(PROCESS_ERROR, null);
				}
			 }
			 // If no MODIFY parameter is passed, we will create a floppy image from scratch...
			 else {
				 imageFile = this.createFloppyImageAndInjectFiles(extractedFiles);
				 if(imageFile==null) {
					 return this.returnWithErrorMessage(PROCESS_ERROR, null);
				 }
			 }
			 
		}
		// the file in the digitalObject is NOT a Zip, so we have just one file to write ;-)
		// Put that in a List and pass it to the creation-method as usual...
		else {
			List<File> tmpList = new ArrayList<File>();
			tmpList.add(inputFile);
			imageFile = this.createFloppyImageAndInjectFiles(tmpList);
			if(imageFile==null) {
				 return this.returnWithErrorMessage(PROCESS_ERROR, null);
			}
		}
		
		// If we have reached this line, we should have an image file created, so wrap a DigObj around that and return 
		// a MigrateResult...
		DigitalObject resultDigObj = new DigitalObject.Builder(Content.asStream(imageFile))
										.format(outputFormat)
										.title(imageFile.getName())
										.build();
		
		ServiceReport report = new ServiceReport();
        report.setErrorState(0);
        log.info("Created Service report...");
		return new MigrateResult(resultDigObj, report);
	}
	
	private ZipResult extractFilesFromFloppyImage(File image, File outputFolder) {
		ProcessRunner cmd = new ProcessRunner();
		cmd.setCommand(this.getExtractCommandLine(image, EXTRACTED_FILES_DIR));
		cmd.setStartingDir(TEMP_FOLDER);
		cmd.run();
		PROCESS_OUT = cmd.getProcessOutputAsString();
		log.info("Tool output:\n" + PROCESS_OUT);
		PROCESS_ERROR = cmd.getProcessErrorAsString();
		log.info("Tool errors:\n" + PROCESS_ERROR);
		return FileUtils.createZipFileWithChecksum(EXTRACTED_FILES_DIR, TEMP_FOLDER, "extractedFiles.zip");
	}
	
	private ArrayList<String> getExtractCommandLine(File image, File outputFolder) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "EXTRACT.EXE" + "\"");
		commands.add("-oe");
		commands.add("\"" + image.getName() + "\"");
		commands.add("\"" + outputFolder.getName() + "\"");
		return commands;
	}
	
	private File createFloppyImageAndInjectFiles(List<File> filesToInject) {
			if(FileUtils.filesTooLargeForMedium(filesToInject, FLOPPY_SIZE)) {
				log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB). Returning with error: " + PROCESS_ERROR);
				return null;
			}
			
			ProcessRunner cmd = new ProcessRunner();
			cmd.setCommand(this.getCreationAndInjectCommandLine(filesToInject));
			cmd.setStartingDir(TEMP_FOLDER);
			cmd.run();
			PROCESS_OUT = cmd.getProcessOutputAsString();
			log.info("Tool output:\n" + PROCESS_OUT);
			PROCESS_ERROR = cmd.getProcessErrorAsString();
			log.info("Tool errors:\n" + PROCESS_ERROR);
			
			File resultImage = new File(TEMP_FOLDER, DEFAULT_FLOPPY_IMAGE_NAME);
			if(!resultImage.exists()) {
				log.error("No floppy image created! Returning with error: " + PROCESS_ERROR);
				return null;
			}
			return resultImage;
		}

	private ArrayList<String> getCreationAndInjectCommandLine(List<File> files) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "EXTRACT.EXE" + "\"");
		commands.add("-i");
		commands.add(DEFAULT_FLOPPY_IMAGE_NAME);
		for (File file : files) {
			commands.add("\"" + file.getName() + "\"");
		}
		commands.add("-F144");
		return commands;
	}
	
	private File addFilesToFloppyImage(File floppyImage, List<File> filesToAdd) {
		if(FileUtils.filesTooLargeForMedium(filesToAdd, FLOPPY_SIZE)) {
			log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB). Returning with error: " + PROCESS_ERROR);
			return null;
		}
		if(floppyImage == null) {
			return this.createFloppyImageAndInjectFiles(filesToAdd);
		}
		else {
			for (File file : filesToAdd) {
				log.info("Writing file to image: " + file.getName());
				ProcessRunner cmd = new ProcessRunner();
				cmd.setCommand(this.getModifyCommandLine(floppyImage, file));
				cmd.setStartingDir(TEMP_FOLDER);
				cmd.run();
				PROCESS_OUT = cmd.getProcessOutputAsString();
				log.info("Tool output: \n" + PROCESS_OUT);
				PROCESS_ERROR = cmd.getProcessErrorAsString();
				
				if(!PROCESS_ERROR.equalsIgnoreCase("")) {
					log.error(PROCESS_ERROR);
					return null;
				}
			}
			return floppyImage.getAbsoluteFile();
		}
	}

	private ArrayList<String> getModifyCommandLine(File floppyImage, File fileToAdd) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "FAT_IMGEN.EXE" + "\"");
		commands.add("modify");
		commands.add("\"" + floppyImage.getAbsolutePath() + "\"");
		commands.add("-f");
		commands.add("\"" + fileToAdd.getAbsolutePath() + "\"");
		return commands;
	}
	
	
	private String getFormatExtension (URI formatURI) {
        log.info("Getting extension for given format URI: " + formatURI.toASCIIString());
        Format f = new Format(formatURI);
        String extension = null;
        if(Format.isThisAnExtensionURI(formatURI)) {
            log.info("URI is an Extension-URI.");
            extension = f.getExtensions().iterator().next(); 
            log.info("Got Extension for format URI: " + formatURI.toASCIIString() + "--> " + extension );
        }
        else {
            log.info("URI is of another supported type.");
            FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
            Format fileFormat = formatRegistry.getFormatForURI(formatURI);
            Set <String> extensions = fileFormat.getExtensions();
            if(extensions != null){
                Iterator <String> iterator = extensions.iterator();
                extension = iterator.next();
                log.info("Got Extension for format URI: " + formatURI.toASCIIString() + "--> " + extension );
            }
        }
        return extension;
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
