package eu.planets_project.services.migration.floppyImageHelper;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
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
        name = FloppyImageHelperWin.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class FloppyImageHelperWin implements Migrate {
	
	public FloppyImageHelperWin() {
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteTempFiles(TEMP_FOLDER);
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		EXTRACTED_FILES_DIR = FileUtils.createFolderInWorkFolder(TEMP_FOLDER, EXTRACTION_OUT_FOLDER_NAME);
	}
	                 
	public static final String NAME = "FloppyImageHelperWin";
	
//	private static File TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp("FLOPPY_IMAGE_HELPER_TMP");
	private static File TEMP_FOLDER = null;
	private static String TEMP_FOLDER_NAME = "FLOPPY_IMAGE_HELPER";
	
	private static File EXTRACTED_FILES_DIR = null;
	private static String EXTRACTION_OUT_FOLDER_NAME = "EXTRACTED_FILES";
	private static String FLOPPY_IMAGE_TOOLS_HOME = System.getenv("FLOPPY_IMAGE_TOOLS_HOME");
//	private static String FLOPPY_IMAGE_TOOLS_HOME = "PA/floppyImageHelper/src/resources/FLOPPY_IMAGE_TOOLS";
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

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME, Migrate.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.description("This service is a wrapper for the 'Extract' Windows Commandline tool.\n" +
        				"This tools is able to create Floppy disk images (1.44 MB) from scratch, containing files of your choice (up to 1.44 MB!).\n" +
        				"This is the first possible direction. The other one is the Extraction of files from a floppy disk image without mounting that image as a disk drive." +
        				"This service accepts:\n" +
        				"1) ZIP files, containing the files you want to be written on the floppy image. The service will unpack the ZIP file and write the contained files to the floppy image, " +
        				"which is returned, if the files in the ZIP do not exceed the capacity limit of 1.44 MB.\n" +
        				"2) a single file which should be written on the floppy image. This file could be of ANY type/format (except the '.ima/.img' type!)\n" +
        				"3) An '.IMA'/'.IMG' file. In this case, the service will extract all files from that floppy image and return a set of files (as a ZIP)." +
        				"'Extract.exe' is using the WinImage SDK.");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("1.0");

        sd.tool( Tool.create(null, "Extract.exe", "v2.10", null, "http://www.winimage.com/extract.htm"));
        List<MigrationPath> pathways = new ArrayList<MigrationPath>();
        FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
        pathways.add(new MigrationPath(format.createExtensionUri("ZIP"), format.createExtensionUri("IMA"), null));
        pathways.add(new MigrationPath(format.createExtensionUri("ANY"), format.createExtensionUri("IMA"), null));
        pathways.add(new MigrationPath(format.createExtensionUri("IMA"), format.createExtensionUri("ZIP"), null));
        pathways.add(new MigrationPath(format.createExtensionUri("IMG"), format.createExtensionUri("ZIP"), null));
        
        sd.paths(pathways.toArray(new MigrationPath[] {}));
        return sd.build();
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
		
		FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
        String inFormat = formatRegistry.getExtensions(inputFormat).iterator().next().toUpperCase();
		String outFormat = formatRegistry.getExtensions(inputFormat).iterator().next().toUpperCase();
		
		if(parameters!=null && parameters.size()>0) {
			for (Parameter parameter : parameters) {
				if(parameter.name.equalsIgnoreCase("modifyImage")) {
					if(parameter.value.equalsIgnoreCase("true")) {
						MODIFY_IMAGE = true;
					}
				}
			}
		}
		
		List<File> extractedFiles = null;

		String fileName = digitalObject.getTitle();
		
		ImmutableContent content = (ImmutableContent)digitalObject.getContent();
		
		Checksum checksum = content.getChecksum();
		
		long check = 0;
		
		if(checksum!=null) {
			check = Long.parseLong(checksum.getValue());
		}
		else {
			check = -1;
		}
		
		if(fileName==null) {
			INPUT_EXT = formatRegistry.getExtensions(inputFormat).iterator().next();
			fileName = DEFAULT_INPUT_NAME + "." + INPUT_EXT;
		}
		
		File inputFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), TEMP_FOLDER, fileName);
		
		File imageFile = null;
		
		File floppyImage = null;
		
		ZipResult zippedResult = null;
		
		if((inFormat.endsWith("IMA")) || inFormat.endsWith("IMG")) {
			zippedResult = this.extractFilesFromFloppyImage(inputFile);
			
			Content zipContent = ImmutableContent.asStream(
                    zippedResult.getZipFile()).withChecksum(
                    zippedResult.getChecksum());
			
			DigitalObject resultDigObj = new DigitalObject.Builder(zipContent)
			.format(formatRegistry.createExtensionUri("zip"))
			.title(zippedResult.getZipFile().getName())
			.build();

			ServiceReport report = new ServiceReport();
			report.setErrorState(0);
			report.setInfo(PROCESS_OUT);
			log.info("Created Service report...");
			return new MigrateResult(resultDigObj, report);
			
		}
		
		// Check if we have a ZIP file?
		if(inFormat.endsWith("ZIP")) {// x-fmt/263
			if(check!=-1) {
				// if yes, extract files from this ZIP and use these files for floppy image creation
				 extractedFiles = FileUtils.extractFilesFromZipAndCheck(inputFile, TEMP_FOLDER, check);
			}
			else {
				extractedFiles = FileUtils.extractFilesFromZip(inputFile, TEMP_FOLDER);
			}
			imageFile = this.createFloppyImageAndInjectFiles(extractedFiles);
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
		DigitalObject resultDigObj = new DigitalObject.Builder(ImmutableContent.asStream(imageFile))
										.format(outputFormat)
										.title(imageFile.getName())
										.build();
		
		ServiceReport report = new ServiceReport();
        report.setErrorState(0);
        report.setInfo(PROCESS_OUT);
        log.info("Created Service report...");
		return new MigrateResult(resultDigObj, report);
	}
	
	private ZipResult extractFilesFromFloppyImage(File image) {
		ProcessRunner cmd = new ProcessRunner();
		cmd.setCommand(this.getExtractCommandLine(image));
		cmd.setStartingDir(EXTRACTED_FILES_DIR);
		cmd.run();
		PROCESS_OUT = cmd.getProcessOutputAsString();
		log.info("Tool output:\n" + PROCESS_OUT);
		PROCESS_ERROR = cmd.getProcessErrorAsString();
		log.info("Tool errors:\n" + PROCESS_ERROR);
		return FileUtils.createZipFileWithChecksum(EXTRACTED_FILES_DIR, TEMP_FOLDER, "extractedFiles.zip");
	}
	
	private ArrayList<String> getExtractCommandLine(File image) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "EXTRACT.EXE" + "\"");
		commands.add("-oe");
		commands.add("\".." + File.separator + image.getName() + "\"");
//		commands.add(outputFolder.getName() + File.separator);
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
	
//	private File addFilesToFloppyImage(File floppyImage, List<File> filesToAdd) {
//		if(FileUtils.filesTooLargeForMedium(filesToAdd, FLOPPY_SIZE)) {
//			log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB). Returning with error: " + PROCESS_ERROR);
//			return null;
//		}
//		if(floppyImage == null) {
//			return this.createFloppyImageAndInjectFiles(filesToAdd);
//		}
//		else {
//			for (File file : filesToAdd) {
//				log.info("Writing file to image: " + file.getName());
//				ProcessRunner cmd = new ProcessRunner();
//				cmd.setCommand(this.getModifyCommandLine(floppyImage, file));
//				cmd.setStartingDir(TEMP_FOLDER);
//				cmd.run();
//				PROCESS_OUT = cmd.getProcessOutputAsString();
//				log.info("Tool output: \n" + PROCESS_OUT);
//				PROCESS_ERROR = cmd.getProcessErrorAsString();
//				
//				if(!PROCESS_ERROR.equalsIgnoreCase("")) {
//					log.error(PROCESS_ERROR);
//					return null;
//				}
//			}
//			return floppyImage.getAbsoluteFile();
//		}
//	}

//	private ArrayList<String> getModifyCommandLine(File floppyImage, File fileToAdd) {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "FAT_IMGEN.EXE" + "\"");
//		commands.add("modify");
//		commands.add("\"" + floppyImage.getAbsolutePath() + "\"");
//		commands.add("-f");
//		commands.add("\"" + fileToAdd.getAbsolutePath() + "\"");
//		return commands;
//	}
	
	

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
