/**
 *  @author : Thomas Krämer thomas.kraemer@uni-koeln.de
 *  created : 14.07.2008
 *  
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  @author: Thomas Kraemer thomas.kraemer@uni-koeln.de
 *  created: 14.07.2008
 */
public class FileUtils {
	
	static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	
	
	/**
	 * @param folderName The name of the folder to be created in the System-Temp folder.
	 * 			   The name could contain '/' or '\'. In this case, all nested folders will be created.
	 * @return Returns the created folder as a File object, or the deepest nested folder
	 */
	public static File createWorkFolderInSysTemp(String folderName) {
		File folder = null;
		folder = new File(SYSTEM_TEMP, folderName);
		if(folderName.contains("/") | folderName.contains(File.separator)) {
			boolean madeFolder = folder.mkdirs();
		}
		else {
			boolean madeFolder = folder.mkdir();
			if(!madeFolder & !folder.exists()) {
				System.err.println("ERROR: Could not create Folder!");
			}
		}
		return folder;
	}
	
	
	/**
	 * @param parentFolder the folder to create the new folder with "folderName" in
	 * @param folderName the folder to create in the parentFolder
	 * @return a File object of the created folder
	 */
	public static File createFolderInWorkFolder(File parentFolder, String folderName) {
		File folder = null;
		folder  = new File(parentFolder, folderName);
		folder.mkdirs();
		return folder;
	}
	
	
	
    /**
	 * @param name
	 *            The name to use when generating the temp file
	 * @param suffix
	 * 			  The suffix for the temp file to be created	           
	 * @return Returns a temp file created in the System-Temp folder
	 */
	public static File getTempFile(String name, String suffix) {
		File input;
		if(suffix.startsWith(".")) {
			input = new File(SYSTEM_TEMP, name + suffix);
		}
		else {
			input = new File(SYSTEM_TEMP, name + "." + suffix);
		}
		return input;
	}
	
	
	/**
	 * @param name
	 *            The name to use when generating the temp file
	 * @param suffix
	 * 			  The suffix for the temp file           
	 * @return Returns a temp file created in the System-Temp folder
	 */
	public static File getTempFile(byte[] data ,String  name,String suffix) {
		File input;
			if(suffix.startsWith(".")) {
				input = new File(SYSTEM_TEMP, name + suffix);
			}
			else {
				input = new File(SYSTEM_TEMP, name + "." + suffix);
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(input);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return input;
	}
	
	/**
	 * @param workFolder the folder you wish to delete. All contained folders will be deleted recursive
	 * @param plogger a PlanetsLogger instance to log the deletion of that folders
	 * @return true, if all folders were deleted and false, if not.
	 */
	public static boolean deleteTempFiles(File workFolder, PlanetsLogger plogger) {
		String workFolderName = workFolder.getPath();
		if (workFolder.isDirectory()){
			File[] entries = workFolder.listFiles();
				for (int i=0;i<entries.length;i++){
					File current = entries[i];
					deleteTempFiles(current, plogger);
				}
			if (workFolder.delete()) {
				plogger.info("Deleted: " + workFolderName);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (workFolder.delete()) {
				plogger.info("Deleted: " + workFolderName);
				return true;
			}
			else {
				return false;
			}
		}
	}

}
