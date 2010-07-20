/**
 * 
 */
package eu.planets_project.services.migration.floppyImageHelper.impl.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ZipResult;
import eu.planets_project.services.utils.ZipUtils;

/**
 * @author melmsp
 *
 */
public class Fat_Imgen {
	
	private static File TOOL_DIR = null;
	private static String FLOPPY_IMAGE_TOOLS_HOME_PATH = System.getenv("FLOPPY_IMAGE_TOOLS_HOME");
	
	public static final File SYSTEM_TEMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));
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
	
	private static Logger log = Logger.getLogger(Fat_Imgen.class.getName());
	
	public Fat_Imgen() {
		TEMP_FOLDER = new File(SYSTEM_TEMP_FOLDER, TEMP_FOLDER_NAME);
		try {
            FileUtils.forceMkdir(TEMP_FOLDER);
            if(TEMP_FOLDER.exists()) {
            	FileUtils.cleanDirectory(TEMP_FOLDER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	public FloppyHelperResult createImageAndInjectFiles(List<File> filesToInject) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		if(filesTooLargeForMedium(filesToInject, FLOPPY_SIZE)) {
			process_error.append("Sorry! File compilation too large to be written to a Floppy (1.44 MB).");
			log.severe("Sorry! File compilation too large to be written to a Floppy (1.44 MB).");
			return this.returnWithError(process_error.toString());
		}
		
		File floppyImage = new File(TEMP_FOLDER, randomize(DEFAULT_FLOPPY_IMAGE_NAME));
		
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
	
	/**
     * Calculates the overall size of a given list of files and checks, whether
     * these files will fit on a medium with a specific space (-->
     * targetMediaSizeInBytes).
     * @param files a list of files which size should be calculated
     * @param targetMediaSizeInBytes the size of the target medium (e.g. Floppy
     *        1.44 MB = 1474560 bytes)
     * @return true if the file compilation will fit on the target medium, false
     *         if not.
     */
    public static boolean filesTooLargeForMedium(final List<File> files,
            final long targetMediaSizeInBytes) {
        long size = calculateSize(files);
        boolean filesToLarge = size > targetMediaSizeInBytes;
        if (filesToLarge) {
            log.warning("Attention: files size (" + size
                    + " bytes) too big for target medium ("
                    + targetMediaSizeInBytes + " bytes) !");
        } else {
            log.info("Summed up file size: " + size
                    + " bytes. All files will fit on target medium with "
                    + targetMediaSizeInBytes + " bytes capacity.");
        }
        return filesToLarge;
    }
    
    /**
     * Calculates the overall size of a given list of files.
     * @param files the list of files for which the size should be calculated
     * @return the size in bytes as <strong>long</strong>
     */
    public static long calculateSize(final List<File> files) {
        long size = 0;
        for (File file : files) {
            size = size + file.length();
        }
        log.info("Calculated file size: " + size + " bytes.");
        return size;
    }
	
	
	// Open an existing floppy image file to
	// get the files on it. Then return them as a ZIP.
	public FloppyHelperResult openImageAndGetFiles(File imageFile) {
		if(!toolInstalledProperly()) {
			return this.returnWithError(process_error.toString());
		}
		
		EXTRACTED_FILES_DIR = new File(TEMP_FOLDER, randomize(EXTRACTED_FILES_FOLDER_NAME));
		try {
            FileUtils.forceMkdir(EXTRACTED_FILES_DIR);
            
            if(EXTRACTED_FILES_DIR.list().length > 0) {
            	FileUtils.cleanDirectory(EXTRACTED_FILES_DIR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		ProcessRunner cmd = new ProcessRunner(extractFiles(imageFile));
		cmd.setStartingDir(EXTRACTED_FILES_DIR);
		cmd.run();
		
		int returnCode = cmd.getReturnCode();
		
		appendOut(cmd.getProcessOutputAsString());
		appendErr(cmd.getProcessErrorAsString());
		
		ZipResult zipResult = 
			
			ZipUtils.createZipAndCheck(EXTRACTED_FILES_DIR, TEMP_FOLDER, randomize(ZIP_NAME), true);
		
		if(returnCode != 0) {
			return this.returnWithError(getAllError());
		}
		else {			
			return this.returnWithSuccess(getAllOutput(), null, zipResult);
		}
	}
	
	public static String randomize(String name) {
        File f = null;
        try {
            f = File.createTempFile(name, "." + FilenameUtils.getExtension(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = f != null ? f.getName() : null;
        FileUtils.deleteQuietly(f);
        return result;
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
		fileToInject = truncateNameAndRenameFile(fileToInject);
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
	
	public static File truncateNameAndRenameFile(final File file) {
        String newName = file.getName();
        String parent = file.getParent();
        String ext = "";
        if (newName.contains(".")) {
            ext = newName.substring(newName.lastIndexOf("."));
            newName = newName.substring(0, newName.lastIndexOf("."));
        }
        if (newName.length() > 8) {
            newName = newName.substring(0, 8);
            log.info("File name longer than 8 chars. Truncated file name to: "
                    + newName
                    + " to avoid problems with long file names in DOS!");
            
            newName = newName + ext;
            File renamedFile = new File(new File(parent), newName);
            boolean renamed = file.renameTo(renamedFile);
            if (!renamed) {
//                throw new IllegalArgumentException("Could not rename: " + file);
            }
            return renamedFile;
        }
        else {
            return file;
        }
        
    }
	
	

	private boolean toolInstalledProperly() {
		if(FLOPPY_IMAGE_TOOLS_HOME_PATH == null) {
			log.severe("FLOPPY_IMAGE_TOOLS_HOME = null! " +
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
		if(err!=null && !err.equalsIgnoreCase("")) {
			process_error.append(err);
			process_error.append(System.getProperty("line.separator"));
		}
	}


	private void appendOut(String out) {
		if(out!=null && !out.equalsIgnoreCase("")) {
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
