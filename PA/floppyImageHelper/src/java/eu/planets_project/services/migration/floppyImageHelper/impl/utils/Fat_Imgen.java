/**
 * 
 */
package eu.planets_project.services.migration.floppyImageHelper.impl.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ZipResult;
import eu.planets_project.services.utils.ZipUtils;

/**
 * @author melmsp
 *
 */
public class Fat_Imgen {
	
	private static File TOOL_DIR = null;
	private static String FAT_IMGEN_NAME = "fat_imgen";
	private static String FLOPPY_IMAGE_TOOLS_HOME_PATH = System.getenv("FLOPPY_IMAGE_TOOLS_HOME");
	
	private File TEMP_FOLDER = null;
	private String TEMP_FOLDER_NAME = "FAT_IMGEN_TMP";
	private String DEFAULT_FLOPPY_IMAGE_NAME = "floppy144.ima";
	private File EXTRACTED_FILES_DIR = null;
	private String EXTRACTED_FILES_FOLDER_NAME = "EXTRACTED_FILES";
	private String ZIP_NAME = "zipped_floppy_content.zip";
	
	private static final long FLOPPY_SIZE = 1474560;
	
	private StringBuffer process_error = new StringBuffer();
	private StringBuffer process_output = new StringBuffer();
	
	
	private static String br = System.getProperty("line.separator");
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public Fat_Imgen() {
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		if(TEMP_FOLDER.exists()) {
			FileUtils.deleteAllFilesInFolder(TEMP_FOLDER);
		}
	}
	
	
	public FloppyHelperResult createImageAndInjectFiles(List<File> filesToInject) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		if(FileUtils.filesTooLargeForMedium(filesToInject, FLOPPY_SIZE)) {
			process_error.append("Sorry! File compilation too large to be written to a Floppy (1.44 MB).");
			log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB).");
			return this.returnWithError(process_error.toString());
		}
		
		File floppyImage = new File(TEMP_FOLDER, FileUtils.randomizeFileName(DEFAULT_FLOPPY_IMAGE_NAME));
		
		ProcessRunner cmd = new ProcessRunner(createEmptyImage(floppyImage));
		cmd.run();

		int returnCode = cmd.getReturnCode();
		appendOut(cmd.getProcessOutputAsString());
		appendErr(cmd.getProcessErrorAsString());
		
		for (File file : filesToInject) {
			cmd.setCommand(injectFileToImage(floppyImage, file));
			cmd.run();
			returnCode = cmd.getReturnCode();
			appendOut(cmd.getProcessOutputAsString());
			appendErr(cmd.getProcessErrorAsString());
		}
		
		if(returnCode != 0) {
			return this.returnWithError(getAllError());
		} 
		else {
			return this.returnWithSuccess(getAllOutput(), floppyImage, null);
		}
	}
	
	
	
	
	// Open an existing floppy image file to
	// get the files on it. Then return them as a ZIP.
	public FloppyHelperResult openImageAndGetFiles(File imageFile) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		EXTRACTED_FILES_DIR = FileUtils.createFolderInWorkFolder(TEMP_FOLDER, FileUtils.randomizeFileName(EXTRACTED_FILES_FOLDER_NAME));
		
		if(EXTRACTED_FILES_DIR.list().length > 0) {
			FileUtils.deleteAllFilesInFolder(EXTRACTED_FILES_DIR);
		}
		
		ProcessRunner cmd = new ProcessRunner(extractFiles(imageFile));
		cmd.setStartingDir(EXTRACTED_FILES_DIR);
		cmd.run();
		
		int returnCode = cmd.getReturnCode();
		
		appendOut(cmd.getProcessOutputAsString());
		appendErr(cmd.getProcessErrorAsString());
		
		ZipResult zipResult = 
			
			ZipUtils.createZipAndCheck(EXTRACTED_FILES_DIR, TEMP_FOLDER, FileUtils.randomizeFileName(ZIP_NAME), true);
		
		if(returnCode != 0) {
			return this.returnWithError(getAllError());
		}
		else {			
			return this.returnWithSuccess(getAllOutput(), null, zipResult);
		}
	}
	
	
	
	public FloppyHelperResult addFilesToFloppyImage(File floppyImage, List<File> filesToAdd) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		ProcessRunner cmd = new ProcessRunner();
		int returnCode = -1;
		
		for (File file : filesToAdd) {
			cmd.setCommand(injectFileToImage(floppyImage, file));
			cmd.run();
			returnCode = cmd.getReturnCode();
			appendOut(cmd.getProcessOutputAsString());
			appendErr(cmd.getProcessErrorAsString());
		}
		
		if(returnCode != 0) {
			return this.returnWithError(getAllError());
		}
		else {			
			return this.returnWithSuccess(getAllOutput(), floppyImage, null);
		}
	}
	
	private List<String> extractFiles(File floppyImage) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(TOOL_DIR.getAbsolutePath() + File.separator + "fat_imgen");
		cmd.add("-e");
		cmd.add("-f");
		cmd.add(floppyImage.getAbsolutePath());
		cmd.add("-F");
		return cmd;
	}
	
	
	private List<String> createImageAndInjectFile (File newFloppyImage, File fileToInject) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(TOOL_DIR.getAbsolutePath() + File.separator + "fat_imgen");
		cmd.add("-c");
		cmd.add("-f");
		cmd.add(newFloppyImage.getAbsolutePath());
		cmd.add("-i");
		cmd.add(fileToInject.getAbsolutePath());
		cmd.add("-F");
		return cmd;
	}
	
	private List<String> createEmptyImage (File newFloppyImage) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(TOOL_DIR.getAbsolutePath() + File.separator + "fat_imgen");
		cmd.add("-c");
		cmd.add("-f");
		cmd.add(newFloppyImage.getAbsolutePath());
		cmd.add("-F");
		return cmd;
	}
	
	private List<String> injectFileToImage (File targetFloppyImage, File fileToInject) {
		fileToInject = FileUtils.truncateNameAndRenameFile(fileToInject);
		List<String> cmd = new ArrayList<String>();
		cmd.add(TOOL_DIR.getAbsolutePath() + File.separator + "fat_imgen");
		cmd.add("-m");
		cmd.add("-f");
		cmd.add(targetFloppyImage.getAbsolutePath());
		cmd.add("-i");
		cmd.add(fileToInject.getAbsolutePath());
		cmd.add("-F");
		return cmd;
	}
	
	

	private boolean toolInstalledProperly() {
		if(FLOPPY_IMAGE_TOOLS_HOME_PATH == null) {
			log.error("FLOPPY_IMAGE_TOOLS_HOME = null! " +
					br + "Could not find floppy image tools! " +
					br + "Please install the tool 'Fat_Imgen' on your system and point a System variable to the installation folder!" +
					br + "Otherwise this service will carry on to refuse to do its work! (you can find the tool here: http://www.ohloh.net/p/fat_imgen/download)");
			process_error.append("FLOPPY_IMAGE_TOOLS_HOME = null! " +
					br + "Could not find floppy image tools! " +
					br + "Please install the tool 'Fat_Imgen' on your system and point a System variable to the installation folder!" +
					br + "Otherwise this service will carry on to refuse to do its work! (you can find the tool here: http://www.ohloh.net/p/fat_imgen/download)");
			return false;
		}
		else {
			TOOL_DIR = new File(FLOPPY_IMAGE_TOOLS_HOME_PATH);
			return true;
		}
	}

	private String getAllError() {
		return process_error.toString();
	}


	private String getAllOutput() {
		return process_output.toString();
	}


	private void appendErr (String err) {
		if(err!=null || !err.equalsIgnoreCase("")) {
			process_error.append(err);
			process_error.append(System.getProperty("line.separator"));
		}
	}


	private void appendOut(String out) {
		if(out!=null || !out.equalsIgnoreCase("")) {
			process_output.append(out);
			process_output.append(System.getProperty("line.separator"));
		}
	}


	private FloppyHelperResult returnWithError(String message) {
		FloppyHelperResult fhResult = new FloppyHelperResult();
		fhResult.setMessage(message);
		fhResult.setResultFile(null);
		fhResult.setZipResult(null);
		fhResult.setState(FloppyHelperResult.ERROR);
		return fhResult;
	}

	private FloppyHelperResult returnWithSuccess(String message, File resultFile, ZipResult zip) {
		FloppyHelperResult fhResult = new FloppyHelperResult();
		fhResult.setMessage(message);
		if(resultFile!=null) {
			fhResult.setResultFile(resultFile);
		}
		else {
			fhResult.setZipResult(zip);
		}
		fhResult.setState(FloppyHelperResult.SUCCESS);
		return fhResult;
	}

}
