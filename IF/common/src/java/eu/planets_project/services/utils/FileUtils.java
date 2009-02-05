package eu.planets_project.services.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 *  @author Thomas Kraemer thomas.kraemer@uni-koeln.de
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
			folder.mkdirs();
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
		if(!suffix.startsWith(".")) {
			suffix = "." + suffix;
		}
		File input = null;
		try {
			input = File.createTempFile(name, suffix, new File(SYSTEM_TEMP));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	
	
	/**
	 * @param data - the data to write to that file
	 * @param name - the file name of the file to be created
	 * @param suffix - the suffx of that file (e.g. ".tmp", ".bin", ...)
	 * @return - a new File with the given content (--> data), name and extension.
	 */
	public static File getTempFile(byte[] data ,String  name,String suffix) {
		if(!suffix.startsWith(".")) {
			suffix = "." + suffix;
		}
		File input = getTempFile(name, suffix);
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
	 * @param textFile
	 * @return file contents as string
	 */
	public static String readTxtFileIntoString(File textFile) {
		String resultString = null;
		StringBuffer buffer = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			
			while((line = reader.readLine())!=null) {
				buffer.append(line);
				buffer.append("\n");
			}
			reader.close();
			resultString = buffer.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultString;
	}
	
	/**
	 * @param toWriteToFile
	 * @param destinationFilePath
	 * @return file 
	 */
	public static File writeStringToFile (String toWriteToFile, String destinationFilePath) {
		File result = new File(destinationFilePath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(result), 32768);
			writer.write(toWriteToFile);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
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
	
	/**
	 * This method deletes all the content in a folder, without the need of passing it a PlanetsLogger instance!
	 * @param workFolder the folder you wish to delete. All contained folders will be deleted recursive
	 * @return true, if all folders were deleted and false, if not.
	 */
	public static boolean deleteTempFiles(File workFolder) {
		if (workFolder.isDirectory()){
			File[] entries = workFolder.listFiles();
				for (int i=0;i<entries.length;i++){
					File current = entries[i];
					deleteTempFiles(current);
				}
			if (workFolder.delete()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (workFolder.delete()) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * @param inputStream
	 * @return byte array from stream
	 */
	public static byte[] writeInputStreamToBinary(InputStream inputStream) {
		ByteArrayOutputStream boStream = new ByteArrayOutputStream();
		int in;
		try {
			while((in = inputStream.read())!= -1) {
				boStream.write(in);
			}
			
			boStream.flush();
			boStream.close();
			
			byte[] data = boStream.toByteArray();
			
			return data;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param inputStream
	 * @param fileName
	 * @param suffix
	 * @return tmp file from stream
	 */
	public static File writeInputStreamToTmpFile(InputStream inputStream, String fileName, String suffix) {
		if(suffix!=null) {
			if(!suffix.startsWith(".")) {
				suffix = "." + suffix;
			}
		}
		File file = getTempFile(fileName, suffix);
		BufferedOutputStream bos = null;
		FileOutputStream fileOutStream = null;
		try {
			fileOutStream = new FileOutputStream(file);
			bos = new BufferedOutputStream(fileOutStream, 32768);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			int dataBit;
			
			while((dataBit = inputStream.read())!=-1) {
				bos.write(dataBit);
			}
			bos.flush();
			bos.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * This method writes an InputStream to a file. If a file with the same name and path exists already, 
	 * a random number is appended to the filename and the "renamed" file is returned.
	 * 
	 * @param in the inputstream to write to a file
	 * @param parentFolder the folder the file should be created in
	 * @param fileName the name of the file to be created
	 * @return a File object with the given name and path
	 */
	public static File writeInputStreamToFile(InputStream in, File parentFolder, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fileOut = null;
		File target = new File(parentFolder, fileName);
		if(target.exists()) {
			long randonNr = (long) (Math.random()* 1000000);
			if(fileName.contains(".")) {
				fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + randonNr + fileName.substring(fileName.lastIndexOf("."));
			}
			else {
				fileName = fileName + randonNr;
			}
			//target.delete();
			target = new File(parentFolder, fileName);
		}
		try {
			fileOut = new FileOutputStream(target);
			bos = new BufferedOutputStream(fileOut, 32768);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			int dataBit;
			
			while((dataBit = in.read())!=-1) {
				bos.write(dataBit);
			}
			bos.flush();
			bos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return target;
	}
	
	/**
	 * @return system temp dir
	 */
	public static File getSystemTempFolder() {
		return new File(SYSTEM_TEMP);
	}
	
	/** 
	 * 
	 * @param folder the resulting zip file will contain all files in this folder
	 * @param zipFileName the name, the returned zip file should have
	 * @return a zip file containing all files in 'folder' or null, if the folder does not contain a file.
	 * @throws IOException 
	 */
	
	public static File createZipFile(File folder, String zipFileName) {
		// Creating an empty ArrayList for calling the listAllFiles method with
		// "resultFolder" as root.
		ArrayList<String> listOfFiles = new ArrayList<String>();
		
		// Calling the recursive method listAllFiles, which lists all files in
		// all folders in the resultFolder.
		ArrayList<String> resultFileList;
//		try {
		resultFileList = listAllFiles(folder, listOfFiles);
		
		if(resultFileList.size()==0) {
			return null;
		}
		else {		
			// "Normalize" the paths in resultFileList for creation of ZipEntries
			ArrayList<String> normalizedPaths = new ArrayList<String>();
			
			for (int i = 0; i < resultFileList.size(); i++) {
				String currentPath = resultFileList.get(i);
				// Strip the beginning of the String, except the "[FOLDER-NAME] itself\"....
				int index = currentPath.indexOf(folder.getName().toUpperCase());
				currentPath = currentPath.substring(index);
				// Delete the [FOLDER-NAME] part of the paths
				currentPath = currentPath.replace(folder.getName().toUpperCase() + File.separator, "");
				// add the normalized path to the list
				normalizedPaths.add(currentPath);
			}
			
			// Write the output ZIP
			ZipOutputStream zipWriter;
			try {
				zipWriter = new ZipOutputStream(new FileOutputStream(new File(folder, zipFileName)));
				zipWriter.setLevel(9);
				
				// writing the resultFiles to the ZIP
				for (int i = 0; i < normalizedPaths.size(); i++) {
					// Creating the ZipEntries using the normalizedList
					zipWriter.putNextEntry(new ZipEntry(normalizedPaths.get(i)));
					// And getting the files to write for the ZipEntry from the resultFileList
					File currentFile = new File(resultFileList.get(i));
					// getting the byte[] to write
					byte[] current = ByteArrayHelper.read(currentFile);
					zipWriter.write(current);
					zipWriter.flush();
					zipWriter.closeEntry();
				}
		
				zipWriter.flush();
				zipWriter.finish();
				zipWriter.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Getting a reference to the new ZIP file
		File resultZIP = new File(folder, zipFileName);
		return resultZIP;
	}

	
	private static ArrayList<String> listAllFiles(File dir, ArrayList<String> list) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					listAllFiles(files[i], list); 
	            }
				else {
					list.add(files[i].getPath());
				}
			}
		}
		return list;
	}

}
