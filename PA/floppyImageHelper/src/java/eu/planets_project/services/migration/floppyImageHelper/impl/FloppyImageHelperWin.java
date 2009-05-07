package eu.planets_project.services.migration.floppyImageHelper.impl;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migration.floppyImageHelper.api.FloppyImageHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.services.utils.ZipResult;

/**
 * @author Peter Melms
 *
 */
@Stateless()
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = FloppyImageHelperWin.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class FloppyImageHelperWin implements Migrate, FloppyImageHelper {
	
	public static final String NAME = "FloppyImageHelperWin";
	
	private File TEMP_FOLDER = null;
	private String TEMP_FOLDER_NAME = "FLOPPY_IMAGE_HELPER_WIN";
	
	private File EXTRACTED_FILES = null;
	private String EXTRACTED_FILES_DIR = "EXTRACTED_FILES";
	
	private String DEFAULT_INPUT_NAME = "inputFile";
	private String INPUT_EXT = null;
	
	private String PROCESS_ERROR = "";
	private String PROCESS_OUT = "";
	
	private String br = System.getProperty("line.separator");
	
	private static FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
	
    private PlanetsLogger log = PlanetsLogger.getLogger(this.getClass());
    
    private VfdCommandWrapper vfd = new VfdCommandWrapper();
    
    public FloppyImageHelperWin() {
    	// clean the temp folder for this app at startup...
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteTempFiles(TEMP_FOLDER);
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		EXTRACTED_FILES = FileUtils.createFolderInWorkFolder(TEMP_FOLDER, EXTRACTED_FILES_DIR);
	}
    
    
    

    /* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#describe()
	 */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME, Migrate.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.description("This service is a wrapper for the 'Virtual Floppy Drive' Commandline tool for Windows." + br +
        				"This tools is able to create Floppy disk images - 1.44 MB - from scratch, containing files of your choice." + br +
        				"This is the first possible direction. The other one is the Extraction of files from a floppy disk image." +
        				"This service accepts:" + br +
        				"1) ZIP files, containing the files you want to be written on the floppy image. The service will unpack the ZIP file and write the contained files to the floppy image, " +
        				"which is returned, if the files in the ZIP do not exceed the capacity limit of 1.44 MB." + br +
        				"2) a single file which should be written on the floppy image. This file could be of ANY type/format (except the '.ima/.img' type!)" + br +
        				"3) An '.IMA'/'.IMG' file. In this case, the service will extract all files from that floppy image and return a set of files (as a ZIP).");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("1.0");
        sd.tool( Tool.create(null, "Virtual Floppy Drive (vfd.exe)", "v2.1.2008.0206", null, "http://chitchat.at.infoseek.co.jp/vmware/vfd.html"));
        List<MigrationPath> pathways = new ArrayList<MigrationPath>();
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
	/* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.migration.floppyImageHelper.FloppyImageHelper#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, List<Parameter> parameters) {
		
		VfdCommandWrapperResult vfdResult = null;
        String inFormat = format.getExtensions(inputFormat).iterator().next().toUpperCase();
		
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
			INPUT_EXT = format.getExtensions(inputFormat).iterator().next();
			fileName = DEFAULT_INPUT_NAME + "." + INPUT_EXT;
		}
		
		File inputFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), TEMP_FOLDER, fileName);
		
		File imageFile = null;
		
		ZipResult zippedResult = null;
		
		if((inFormat.endsWith("IMA")) || inFormat.endsWith("IMG")) {
			vfdResult = vfd.openImageWithVfdAndGetFiles(inputFile);
			if(vfdResult.resultIsZip) {
				zippedResult = vfdResult.getZipResult();
			}
			else {
				return this.returnWithErrorMessage(vfdResult.getMessage(), null);
			}
			
			Content zipContent = ImmutableContent.asStream(zippedResult.getZipFile())
												 .withChecksum(zippedResult.getChecksum());
			
			DigitalObject resultDigObj = new DigitalObject.Builder(zipContent)
											.format(format.createExtensionUri("zip"))
											.title(zippedResult.getZipFile().getName())
											.build();

			ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, PROCESS_OUT);
			log.info("Created Service report...");
			return new MigrateResult(resultDigObj, report);
		}
		
		// Check if we have a ZIP file?
		if(inFormat.endsWith("ZIP")) {// x-fmt/263
			if(check!=-1) {
				// if yes, extract files from this ZIP and use these files for floppy image creation
				 extractedFiles = FileUtils.extractFilesFromZipAndCheck(inputFile, EXTRACTED_FILES, check);
			}
			else {
				extractedFiles = FileUtils.extractFilesFromZip(inputFile, EXTRACTED_FILES);
			}
			vfdResult = vfd.createImageWithVfdAndInjectFiles(extractedFiles);
			if(!vfdResult.resultIsZip) {
				imageFile = vfdResult.getResultFile();
			}
			else {
				return this.returnWithErrorMessage(vfdResult.getMessage(), null);
			}
			
		}
		// the file in the digitalObject is NOT a Zip, so we have just one file to write ;-)
		// Put that in a List and pass it to the creation-method as usual...
		else {
			List<File> tmpList = new ArrayList<File>();
			tmpList.add(inputFile);
			
			vfdResult = vfd.createImageWithVfdAndInjectFiles(tmpList);
			imageFile = vfdResult.getResultFile();
			
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
		
		ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, PROCESS_OUT);
        log.info("Created Service report...");
		return new MigrateResult(resultDigObj, report);
	}
	

	
//	private File createImageWithVfdAndInjectFiles(List<File> filesToInject) {
//		if(FileUtils.filesTooLargeForMedium(filesToInject, FLOPPY_SIZE)) {
//			log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB). Returning with error: " + PROCESS_ERROR);
//			return null;
//		}
//		ProcessRunner cmd = new ProcessRunner();
//		cmd.setStartingDir(TEMP_FOLDER);
//		// Install the Vfd Driver, if it already is installed, nothing will happen...
//		// FIXME Maybe this should be removed, if we don't have Admin/Poweruser rights on the server instance
//		// where this service is running...?
//		
//		/*cmd.setCommand(installVfdDriver());
//		cmd.run();
//		appendProcessOutAndError(cmd);*/
//		
//		// Start the Vfd Driver (again, if it is already running, nothing will happen.
//		// FIXME Maybe this should be removed, if we don't have Admin/Poweruser rights on the server instance
//		// where this service is running...?
//		
//		// TODO
//		/*cmd.setCommand(startVfdDriver());
//		cmd.run();
//		appendProcessOutAndError(cmd);*/
//		
//		// Create a new, empty 1.44 large floppy image
//		cmd.setCommand(vfd.createNewImageWithVfd());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// Format the virtual floppy drive
//		cmd.setCommand(vfd.formatVfdDrive());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// Unlink the drive letter from virtual floppy drive
//		cmd.setCommand(vfd.uLinkDriveLetterFromVfdDrive());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// Look up a free, unused drive letter on this system and assign a new one that we know for sure, 
//		// to be able to write to this drive later...
//		String driveLetter = vfd.getDriveLetter();
//		if(driveLetter==null) {
//			PROCESS_ERROR = "Could not assign drive letter to virtual floppy drive, all letters are in use!";
//			return null;
//		}
//		cmd.setCommand(vfd.linkDriveLetterToVfdDrive(driveLetter));
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// Our freshly created floppy image as a file, ready to write to
//		File floppy = new File(driveLetter);
//		
//		// Copy the files in the ZIP file to the floppy drive
//		for (File currentFile : filesToInject) {
//			File target = new File(floppy, currentFile.getName());
//			FileUtils.copyFileTo(currentFile, target);
//		}
//		
//		// save the content of the floppy drive to an image file
//		cmd.setCommand(vfd.saveImageInVfdDriveTo(DEFAULT_FLOPPY_IMAGE_NAME));
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// close the current virtual floppy drive 
//		cmd.setCommand(vfd.closeImageInVfd());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// unlink the drive letter again to make it available for further calls
//		cmd.setCommand(vfd.uLinkDriveLetterFromVfdDrive());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// Our created floppy image file, containing the files in the zip file...
//		File newfloppyImage = new File(TEMP_FOLDER, DEFAULT_FLOPPY_IMAGE_NAME);
//		
//		return newfloppyImage;
//	}
	
//	// Open an existing floppy image file and mount it with vfd.exe to
//	// get the files on it. Then return them as a ZIP.
//	private ZipResult openImageWithVfdAndGetFiles(File imageFile) {
//		ProcessRunner cmd = new ProcessRunner();
//		cmd.setStartingDir(TEMP_FOLDER);
//		
//		// Install and start the driver
//		// FIXME Maybe this should be removed, if we don't have Admin/Poweruser rights on the server instance
//		// where this service is running...?
//		// TODO
//		/*cmd.setCommand(installVfdDriver());
//		cmd.run();
//		appendProcessOutAndError(cmd);
//		
//		cmd.setCommand(startVfdDriver());
//		cmd.run();
//		appendProcessOutAndError(cmd);*/
//		
//		// unlink the drive letter it may has, but which we don't know and assign one we know ;-)
//		cmd.setCommand(vfd.uLinkDriveLetterFromVfdDrive());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		String driveLetter = vfd.getDriveLetter();
//		if(driveLetter==null) {
//			PROCESS_ERROR = "Could not assign drive letter to virtual floppy drive, all letters are in use!";
//			return null;
//		}
//		cmd.setCommand(vfd.linkDriveLetterToVfdDrive(driveLetter));
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// Open the passed image file in Vfd.exe
//		cmd.setCommand(vfd.openImageInVfd(imageFile));
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		File floppy = new File(driveLetter);
//		System.out.println("Floppy Path name: " + floppy.getName());
//		
//		File[] filesOnFloppy = floppy.listFiles();
//		
//		// FIXME Workaound due to some strange behaviour in the createZip method in FileUtils...
//		// Copy files to our working directory...
//		for (File file : filesOnFloppy) {
//			File dest = new File(EXTRACTED_FILES_DIR, file.getName());
//			FileUtils.copyFileTo(file, dest);
//		}
//		
//		// create a ZIP containing the files on the floppy disk...
//		ZipResult zip = FileUtils.createZipFileWithChecksum(EXTRACTED_FILES_DIR, TEMP_FOLDER, "extracedFiles.zip");
//		cmd.setCommand(vfd.closeImageInVfd());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// unlink the drive letter again...
//		cmd.setCommand(vfd.uLinkDriveLetterFromVfdDrive());
//		cmd.run();
//		vfd.appendProcessOutAndError(cmd);
//		
//		// return the ZIP
//		return zip;
//	}
	
	
//	/**
//	 * Appends the Process error and output to a class variable to put it in the ServiceReport afterwards...
//	 * @param processRunner the processRunner to get the outputstreams from...
//	 */
//	private void appendProcessOutAndError(ProcessRunner processRunner) {
//		PROCESS_OUT = PROCESS_OUT + br + processRunner.getProcessOutputAsString();
//		PROCESS_ERROR = PROCESS_ERROR + br + processRunner.getProcessErrorAsString();
//	}
	
	
//	/**
//	 * Get the first free drive letter...
//	 * @return the first unused drive letter on your system to map it to the floppy drive
//	 */
//	private String getDriveLetter() {
//		List<String> freeDriveLetters = FileUtils.listAvailableDriveLetters();
//		String driveLetter = null;
//		if(freeDriveLetters!=null) {
//			driveLetter = freeDriveLetters.get(0);
//		}
//		else { 
//			PROCESS_ERROR = "Virtual floppy drive could not be mapped to a drive letter; all drive letters are in use!!!" +
//			   				br + "Returning NULL";
//			return null;
//		}
//		return driveLetter;
//	}
//	
//	
//	private ArrayList<String> startVfdDriver() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("START");
//		return commands;
//	}
//	
//	
//	private ArrayList<String> stopVfdDriver() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("STOP");
//		commands.add("/FORCE");
//		return commands;
//	}
//	
//	private ArrayList<String> installVfdDriver() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("INSTALL");
//		commands.add("/AUTO");
//		return commands;
//	}
//	
//	private ArrayList<String> removeVfdDriver() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("REMOVE");
//		commands.add("/FORCE");
//		return commands;
//	}
//	
//	private ArrayList<String> openImageInVfd(File floppyImage) {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("OPEN");
//		commands.add(floppyImage.getName());
//		commands.add("/W");
//		commands.add("/FORCE");
//		return commands;
//	}
//	
//	private ArrayList<String> createNewImageWithVfd() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("OPEN");
//		commands.add("0:");
//		commands.add("/NEW");
//		commands.add("/W");
//		commands.add("/1.44");
//		commands.add("/FORCE");
//		return commands;
//	} 
//	
//	private ArrayList<String> closeImageInVfd() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("CLOSE");
//		commands.add("0:");
//		commands.add("/FORCE");
//		return commands;
//	} 
//	
//	private ArrayList<String> uLinkDriveLetterFromVfdDrive() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("ULINK");
//		return commands;
//	}
//
//	private ArrayList<String> linkDriveLetterToVfdDrive(String driveLetter) {
//		if(driveLetter.endsWith("\\")) {
//			driveLetter = driveLetter.replace("\\", "");
//		}
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("LINK");
//		commands.add("0:");
//		commands.add(driveLetter);
////		commands.add("/L");
//		return commands;
//	}
//	
//	private ArrayList<String> saveImageInVfdDriveTo(String destFileName) {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("SAVE");
//		commands.add("0:");
//		commands.add(destFileName);
////		commands.add("/OVER");
//		commands.add("/FORCE");
//		return commands;
//	}
//	
//	private ArrayList<String> statusOfVfdDrive() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("STATUS");
//		return commands;
//	}
//	
//	private ArrayList<String> formatVfdDrive() {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
//		commands.add("FORMAT");
//		commands.add("0:");
//		commands.add("/FORCE");
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
	
	
	
	
	
	
//	private ZipResult extractFilesFromFloppyImage(File image) {
//	ProcessRunner cmd = new ProcessRunner();
//	cmd.setCommand(this.getExtractCommandLine(image));
//	cmd.setStartingDir(EXTRACTED_FILES_DIR);
//	cmd.setTimeout(2500);
//	cmd.run();
//	PROCESS_OUT = cmd.getProcessOutputAsString();
//	log.info("Tool output:\n" + PROCESS_OUT);
//	PROCESS_ERROR = cmd.getProcessErrorAsString();
//	log.info("Tool errors:\n" + PROCESS_ERROR);
//	return FileUtils.createZipFileWithChecksum(EXTRACTED_FILES_DIR, TEMP_FOLDER, "extractedFiles.zip");
//}

//private ArrayList<String> getExtractCommandLine(File image) {
//	ArrayList<String> commands = new ArrayList<String>();
//	commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "EXTRACT.EXE" + "\"");
//	commands.add("-oe");
//	commands.add("\".." + File.separator + image.getName() + "\"");
////	commands.add(outputFolder.getName() + File.separator);
//	return commands;
//}

//private File createFloppyImageAndInjectFiles(List<File> filesToInject) {
//	if(FileUtils.filesTooLargeForMedium(filesToInject, FLOPPY_SIZE)) {
//		log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB). Returning with error: " + PROCESS_ERROR);
//		return null;
//	}
//	
//	ProcessRunner cmd = new ProcessRunner();
//	cmd.setCommand(this.getCreationAndInjectCommandLine(filesToInject));
//	cmd.setStartingDir(TEMP_FOLDER);
//	cmd.run();
//	PROCESS_OUT = cmd.getProcessOutputAsString();
//	log.info("Tool output:\n" + PROCESS_OUT);
//	PROCESS_ERROR = cmd.getProcessErrorAsString();
//	log.info("Tool errors:\n" + PROCESS_ERROR);
//	
//	File resultImage = new File(TEMP_FOLDER, DEFAULT_FLOPPY_IMAGE_NAME);
//	if(!resultImage.exists()) {
//		log.error("No floppy image created! Returning with error: " + PROCESS_ERROR);
//		return null;
//	}
//	return resultImage;
//}


//private ArrayList<String> getCreationAndInjectCommandLine(List<File> files) {
//	ArrayList<String> commands = new ArrayList<String>();
//	commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "EXTRACT.EXE" + "\"");
//	commands.add("-i");
//	commands.add(DEFAULT_FLOPPY_IMAGE_NAME);
//	for (File file : files) {
//		commands.add("\"" + file.getName() + "\"");
//	}
//	commands.add("-F144");
//	return commands;
//}

}
