/**
 * 
 */
package eu.planets_project.services.migration.floppyImageHelper.impl.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ZipResult;

/**
 * @author melmsp
 *
 */
public class VfdWrapper {
	
	private static File TOOL_DIR = null;
	private static String VFD_TOOL_NAME = "VFD.EXE";
	private static String FLOPPY_IMAGE_TOOLS_HOME_PATH = System.getenv("FLOPPY_IMAGE_TOOLS_HOME");
	
	private File TEMP_FOLDER = null;
	private String TEMP_FOLDER_NAME = "VFD_WRAPPER";
	private String DEFAULT_FLOPPY_IMAGE_NAME = "floppy144.ima";
	private File EXTRACTED_FILES_DIR = null;
	private String EXTRACTION_OUT_FOLDER_NAME = "EXTRACTED_FILES";
	
	private static final long FLOPPY_SIZE = 1474560;
	
	private StringBuffer process_error = new StringBuffer();
	private StringBuffer process_output = new StringBuffer();
	
	
	private static String br = System.getProperty("line.separator");
	
	private PlanetsLogger log = PlanetsLogger.getLogger(this.getClass());
	
	public VfdWrapper() {
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteTempFiles(TEMP_FOLDER);
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		EXTRACTED_FILES_DIR = FileUtils.createFolderInWorkFolder(TEMP_FOLDER, EXTRACTION_OUT_FOLDER_NAME);
	}
	
	public VfdWrapperResult createImageWithVfdAndInjectFiles(List<File> filesToInject) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		if(FileUtils.filesTooLargeForMedium(filesToInject, FLOPPY_SIZE)) {
			process_error.append("Sorry! File compilation too large to be written to a Floppy (1.44 MB).");
			log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB).");
			return this.returnWithError(process_error.toString());
		}
		
		ProcessRunner cmd = new ProcessRunner();
		cmd.setStartingDir(TEMP_FOLDER);
		// Install the Vfd Driver, if it already is installed, nothing will happen...
		// FIXME Maybe this should be removed, if we don't have Admin/Poweruser rights on the server instance
		// where this service is running...?
		
		/*cmd.setCommand(installVfdDriver());
		cmd.run();
		appendProcessOutAndError(cmd);*/
		
		// Start the Vfd Driver (again, if it is already running, nothing will happen.
		// FIXME Maybe this should be removed, if we don't have Admin/Poweruser rights on the server instance
		// where this service is running...?
		
		// TODO
		/*cmd.setCommand(startVfdDriver());
		cmd.run();
		appendProcessOutAndError(cmd);*/
		
		// Create a new, empty 1.44 large floppy image
		cmd.setCommand(createNewImageWithVfd());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// Format the virtual floppy drive
		cmd.setCommand(formatVfdDrive());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// Unlink the drive letter from virtual floppy drive
		cmd.setCommand(uLinkDriveLetterFromVfdDrive());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// Look up a free, unused drive letter on this system and assign a new one that we know for sure, 
		// to be able to write to this drive later...
		String driveLetter = getDriveLetter();
		if(driveLetter==null) {
			process_error.append("Could not assign drive letter to virtual floppy drive, all letters are in use!");
			return this.returnWithError(process_error.toString());
		}
		cmd.setCommand(linkDriveLetterToVfdDrive(driveLetter));
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// Our freshly created floppy image as a file, ready to write to
		File floppy = new File(driveLetter);
		
		// Copy the files in the ZIP file to the floppy drive
		for (File currentFile : filesToInject) {
			File target = new File(floppy, currentFile.getName());
			FileUtils.copyFileTo(currentFile, target);
		}
		
		// save the content of the floppy drive to an image file
		cmd.setCommand(saveImageInVfdDriveTo(DEFAULT_FLOPPY_IMAGE_NAME));
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// close the current virtual floppy drive 
		cmd.setCommand(closeImageInVfd());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// unlink the drive letter again to make it available for further calls
		cmd.setCommand(uLinkDriveLetterFromVfdDrive());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// Our created floppy image file, containing the files in the zip file...
		File newfloppyImage = new File(TEMP_FOLDER, DEFAULT_FLOPPY_IMAGE_NAME);
		
		return this.returnWithSuccess(process_output.toString(), newfloppyImage, null);
	}

	// Open an existing floppy image file and mount it with vfd.exe to
	// get the files on it. Then return them as a ZIP.
	public VfdWrapperResult openImageWithVfdAndGetFiles(File imageFile) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		ProcessRunner cmd = new ProcessRunner();
		cmd.setStartingDir(TEMP_FOLDER);
		
		// Install and start the driver
		// FIXME Maybe this should be removed, if we don't have Admin/Poweruser rights on the server instance
		// where this service is running...?
		// TODO
		/*cmd.setCommand(installVfdDriver());
		cmd.run();
		appendProcessOutAndError(cmd);
		
		cmd.setCommand(startVfdDriver());
		cmd.run();
		appendProcessOutAndError(cmd);*/
		
		// unlink the drive letter it may has, but which we don't know and assign one we know ;-)
		cmd.setCommand(uLinkDriveLetterFromVfdDrive());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		String driveLetter = getDriveLetter();
		if(driveLetter==null) {
			process_error.append("Could not assign drive letter to virtual floppy drive, all letters are in use!");
			return this.returnWithError(process_error.toString());
		}
		cmd.setCommand(linkDriveLetterToVfdDrive(driveLetter));
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// Open the passed image file in Vfd.exe
		cmd.setCommand(openImageInVfd(imageFile));
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		File floppy = new File(driveLetter);
		System.out.println("Floppy Path name: " + floppy.getName());
		
		File[] filesOnFloppy = floppy.listFiles();
		
		// FIXME Workaound due to some strange behaviour in the createZip method in FileUtils...
		// Copy files to our working directory...
		for (File file : filesOnFloppy) {
			File dest = new File(EXTRACTED_FILES_DIR, file.getName());
			FileUtils.copyFileTo(file, dest);
		}
		
		// create a ZIP containing the files on the floppy disk...
		ZipResult zip = FileUtils.createZipFileWithChecksum(EXTRACTED_FILES_DIR, TEMP_FOLDER, "extracedFiles.zip");
		cmd.setCommand(closeImageInVfd());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// unlink the drive letter again...
		cmd.setCommand(uLinkDriveLetterFromVfdDrive());
		cmd.run();
		process_error.append(cmd.getProcessErrorAsString());
		process_output.append(cmd.getProcessOutputAsString());
		
		// return the result
		return this.returnWithSuccess(process_output.toString(), null, zip);
	}
	
	public VfdWrapperResult addFilesToFloppyImage(File floppyImage, List<File> filesToAdd) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		ProcessRunner cmd = new ProcessRunner();
		cmd.setStartingDir(TEMP_FOLDER);
		
		cmd.setCommand(startVfdDriver());
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		cmd.setCommand(uLinkDriveLetterFromVfdDrive());
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		String driveLetter = getDriveLetter();
		if(driveLetter==null) {
			process_error.append("Could not assign drive letter to virtual floppy drive, all letters are in use!");
			return this.returnWithError(process_error.toString());
		}
		cmd.setCommand(linkDriveLetterToVfdDrive(driveLetter));
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		cmd.setCommand(openImageInVfd(floppyImage));
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		File floppy = new File(driveLetter);
		
		List<File> allFiles = new ArrayList<File>();
		List<File> filesOnFloppy = Arrays.asList(floppy.listFiles());
		
		allFiles.addAll(filesOnFloppy);
		allFiles.addAll(filesToAdd);
		
		boolean filesTooLarge = FileUtils.filesTooLargeForMedium(allFiles, FLOPPY_SIZE);
		
		if(!filesTooLarge) {
			for (File currentfile : filesToAdd) {
				File dest = new File(floppy, currentfile.getName());
				FileUtils.copyFileTo(currentfile, dest);
			}
			List<File> afterCopy = Arrays.asList(floppy.listFiles());
			log.info("Files on floppy: " + br);
			for (File file : afterCopy) {
				log.info(file.getAbsoluteFile() + br);
			}
		}
		else {
			process_error.append("ERROR: Not enough space! Files could not be added to floppy disk!");
			return this.returnWithError(process_error.toString());
		}
		
		cmd.setCommand(saveImageInVfdDriveTo(floppyImage.getAbsolutePath()));
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		cmd.setCommand(uLinkDriveLetterFromVfdDrive());
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		cmd.setCommand(closeImageInVfd());
		cmd.run();
		process_output.append(cmd.getProcessOutputAsString());
		process_error.append(cmd.getProcessErrorAsString());
		
		return this.returnWithSuccess(process_output.toString(), floppyImage, null);
	}
	

	/**
	 * Get the first free drive letter...
	 * @return the first unused drive letter on your system to map it to the floppy drive
	 */
	private String getDriveLetter() {
		List<String> freeDriveLetters = FileUtils.listAvailableDriveLetters();
		String driveLetter = null;
		if(freeDriveLetters!=null) {
			driveLetter = freeDriveLetters.get(0);
		}
		else { 
			process_error.append("Virtual floppy drive could not be mapped to a drive letter; all drive letters are in use!!!" +
			   				br + "Returning NULL");
			return null;
		}
		return driveLetter;
	}




	private ArrayList<String> startVfdDriver() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("START");
		return commands;
	}

	private ArrayList<String> stopVfdDriver() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("STOP");
		commands.add("/FORCE");
		return commands;
	}

	private ArrayList<String> installVfdDriver() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("INSTALL");
		commands.add("/AUTO");
		return commands;
	}

	private ArrayList<String> removeVfdDriver() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("REMOVE");
		commands.add("/FORCE");
		return commands;
	}

	private ArrayList<String> openImageInVfd(File floppyImage) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("OPEN");
		commands.add(floppyImage.getAbsolutePath());
		commands.add("/W");
		commands.add("/FORCE");
		return commands;
	}

	private ArrayList<String> createNewImageWithVfd() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("OPEN");
		commands.add("0:");
		commands.add("/NEW");
		commands.add("/W");
		commands.add("/1.44");
		commands.add("/FORCE");
		return commands;
	}

	private ArrayList<String> closeImageInVfd() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("CLOSE");
		commands.add("0:");
		commands.add("/FORCE");
		return commands;
	}

	private ArrayList<String> uLinkDriveLetterFromVfdDrive() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("ULINK");
		return commands;
	}

	private ArrayList<String> linkDriveLetterToVfdDrive(String driveLetter) {
			if(driveLetter.endsWith("\\")) {
				driveLetter = driveLetter.replace("\\", "");
			}
			ArrayList<String> commands = new ArrayList<String>();
			commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
			commands.add("LINK");
			commands.add("0:");
			commands.add(driveLetter);
	//		commands.add("/L");
			return commands;
		}

	private ArrayList<String> saveImageInVfdDriveTo(String destFileName) {
			ArrayList<String> commands = new ArrayList<String>();
			commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
			commands.add("SAVE");
			commands.add("0:");
			commands.add(destFileName);
	//		commands.add("/OVER");
			commands.add("/FORCE");
			return commands;
		}

	private ArrayList<String> statusOfVfdDrive() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("STATUS");
		return commands;
	}

	private ArrayList<String> formatVfdDrive() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(TOOL_DIR + "\\" + VFD_TOOL_NAME);
		commands.add("FORMAT");
		commands.add("0:");
		commands.add("/FORCE");
		return commands;
	}

	private boolean toolInstalledProperly() {
		if(FLOPPY_IMAGE_TOOLS_HOME_PATH == null) {
			log.error("FLOPPY_IMAGE_TOOLS_HOME_PATH = null! " +
					br + "Could not find floppy image tools! " +
					br + "Please install 'extract' and 'fat_imgen' on your system and point a System variable to the installation folder!" +
					br + "Otherwise this service will carry on to refuse to do its work!");
			process_error.append("FLOPPY_IMAGE_TOOLS_HOME_PATH = null! " +
					br + "Could not find floppy image tools! " +
					br + "Please install 'extract' and 'fat_imgen' on your system and point a System variable to the installation folder!" +
					br + "Otherwise this service will carry on to refuse to do its work!");
			return false;
		}
		else {
			TOOL_DIR = new File(FLOPPY_IMAGE_TOOLS_HOME_PATH);
			return true;
		}
	}

	private VfdWrapperResult returnWithError(String message) {
		VfdWrapperResult vfdResult = new VfdWrapperResult();
		vfdResult.setMessage(message);
		vfdResult.setResultFile(null);
		vfdResult.setZipResult(null);
		vfdResult.setState(VfdWrapperResult.ERROR);
		return vfdResult;
	}

	private VfdWrapperResult returnWithSuccess(String message, File resultFile, ZipResult zip) {
		VfdWrapperResult vfdResult = new VfdWrapperResult();
		vfdResult.setMessage(message);
		if(resultFile!=null) {
			vfdResult.setResultFile(resultFile);
		}
		else {
			vfdResult.setZipResult(zip);
		}
		vfdResult.setState(VfdWrapperResult.SUCCESS);
		return vfdResult;
	}

}
