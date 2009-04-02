package eu.planets_project.services.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utilities for reading and writing data.
 * @author Thomas Kraemer thomas.kraemer@uni-koeln.de, Peter Melms
 *         (peter.melms@uni-koeln.de), Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FileUtils {

    private static final String SYSTEM_TEMP = System
            .getProperty("java.io.tmpdir");

    /** We enforce non-instantiability with a private constructor. */
    private FileUtils() {
    }

    /**
     * Reads the contents of a file into a byte array.
     * @param file The file to read into a byte array
     * @return Returns the contents of the given file as a byte array
     */
    public static byte[] readFileIntoByteArray(final File file) {
        byte[] array = null;
        try {
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(file), 65536);
            if (file.length() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("The file at "
                        + file.getAbsolutePath()
                        + " is too large to be represented as a byte array!");
            }
            array = new byte[(int) file.length()];
            in.read(array);
            in.close();
            return array;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes the contents of a byte array into a temporary file.
     * @param bytes The bytes to write into a temporary file
     * @return Returns the temporary file into which the bytes have been written
     */
    public static File writeByteArrayToTempFile(final byte[] bytes) {
        File file = null;
        try {
            file = getTempFile("planets",null);
            
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file), 65536);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * @param bytes The data to write
     * @param fileName The desired file name
     * @return The file containing the given data
     */
    public static File writeByteArrayToFile(final byte[] bytes,
            final String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BufferedOutputStream out;
        try {
            out = new BufferedOutputStream(new FileOutputStream(fileName),
                    65536);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * @param folderName The name of the folder to be created in the System-Temp
     *        folder. The name could contain '/' or '\'. In this case, all
     *        nested folders will be created.
     * @return Returns the created folder as a File object, or the deepest
     *         nested folder
     */
    public static File createWorkFolderInSysTemp(String folderName) {
        File folder = null;
        folder = new File(SYSTEM_TEMP, folderName);
        if (folderName.contains("/") || folderName.contains(File.separator)) {
            folder.mkdirs();
        } else {
            boolean madeFolder = folder.mkdir();
            if (!madeFolder & !folder.exists()) {
                System.err.println("ERROR: Could not create Folder!");
            }
        }
        return folder;
    }

    /**
     * @param parentFolder the folder to create the new folder with "folderName"
     *        in
     * @param folderName the folder to create in the parentFolder
     * @return a File object of the created folder
     */
    public static File createFolderInWorkFolder(File parentFolder,
            String folderName) {
        File folder = null;
        folder = new File(parentFolder, folderName);
        folder.mkdirs();
        return folder;
    }

    /**
     * @param name The name to use when generating the temp file
     * @param suffix The suffix for the temp file to be created
     * @return Returns a temp file created in the System-Temp folder
     */
    public static File getTempFile(String name, String suffix) {
        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }
        File input = null;
        try {
            input = File.createTempFile(name, suffix, new File(SYSTEM_TEMP));
            input.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * @param data - the data to write to that file
     * @param name - the file name of the file to be created
     * @param suffix - the suffx of that file (e.g. ".tmp", ".bin", ...)
     * @return - a new File with the given content (--> data), name and
     *         extension.
     */
    public static File getTempFile(byte[] data, String name, String suffix) {
        if (!suffix.startsWith(".")) {
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

            while ((line = reader.readLine()) != null) {
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
    public static File writeStringToFile(String toWriteToFile,
            String destinationFilePath) {
        File result = new File(destinationFilePath);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(result),
                    32768);
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
     * @param workFolder the folder you wish to delete. All contained folders
     *        will be deleted recursive
     * @param plogger a PlanetsLogger instance to log the deletion of that
     *        folders
     * @return true, if all folders were deleted and false, if not.
     */
    public static boolean deleteTempFiles(File workFolder, PlanetsLogger plogger) {
        String workFolderName = workFolder.getPath();
        if (workFolder.isDirectory()) {
            File[] entries = workFolder.listFiles();
            for (int i = 0; i < entries.length; i++) {
                File current = entries[i];
                deleteTempFiles(current, plogger);
            }
            if (workFolder.delete()) {
                plogger.info("Deleted: " + workFolderName);
                return true;
            } else {
                return false;
            }
        } else {
            if (workFolder.delete()) {
                plogger.info("Deleted: " + workFolderName);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * This method deletes all the content in a folder, without the need of
     * passing it a PlanetsLogger instance!
     * @param workFolder the folder you wish to delete. All contained folders
     *        will be deleted recursive
     * @return true, if all folders were deleted and false, if not.
     */
    public static boolean deleteTempFiles(File workFolder) {
        if (workFolder.isDirectory()) {
            File[] entries = workFolder.listFiles();
            for (int i = 0; i < entries.length; i++) {
                File current = entries[i];
                deleteTempFiles(current);
            }
            if (workFolder.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (workFolder.delete()) {
                return true;
            } else {
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
            while ((in = inputStream.read()) != -1) {
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
    public static File writeInputStreamToTmpFile(InputStream inputStream,
            String fileName, String suffix) {
        if (suffix != null) {
            if (!suffix.startsWith(".")) {
                suffix = "." + suffix;
            }
        }
        File file = getTempFile(fileName, suffix);

        // Write:
        writeInputStreamToFile(inputStream, file);

        return file;
    }

    /**
     * This method writes an InputStream to a file. If a file with the same name
     * and path exists already, a random number is appended to the filename and
     * the "renamed" file is returned.
     * @param in the inputstream to write to a file
     * @param parentFolder the folder the file should be created in
     * @param fileName the name of the file to be created
     * @return a File object with the given name and path
     */
    public static File writeInputStreamToFile(InputStream in,
            File parentFolder, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fileOut = null;
        File target = new File(parentFolder, fileName);
        if (target.exists()) {
            long randonNr = (long) (Math.random() * 1000000);
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."))
                        + "_" + randonNr
                        + fileName.substring(fileName.lastIndexOf("."));
            } else {
                fileName = fileName + randonNr;
            }
            // target.delete();
            target = new File(parentFolder, fileName);
        }

        // Write:
        writeInputStreamToFile(in, target);

        return target;
    }

    /**
     * Writes an input stream to the specified file:
     * @param in
     * @param target
     * @return
     */
    public static void writeInputStreamToFile(InputStream in, File target) {
        BufferedOutputStream bos = null;
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(target);
            bos = new BufferedOutputStream(fileOut, 32768);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        try {
            int dataBit;

            while ((dataBit = in.read()) != -1) {
                bos.write(dataBit);
            }
            bos.flush();
            bos.close();
            fileOut.flush();
            fileOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return system temp dir
     */
    public static File getSystemTempFolder() {
        return new File(SYSTEM_TEMP);
    }
    
    
    
    /**
	 * @param srcFolder the resulting zip file will contain all files in this
	 *        folder
	 * @param destFolder The folder where the created Zip file should live
	 * @param zipFileName the name, the returned zip file should have
	 * @return a zip file containing all files in 'srcFolder' or null, if the
	 *         folder does not contain a file.
	 * @throws IOException
	 */
		public static File createSimpleZipFile(File srcFolder, File destFolder, String zipFileName) {
		// The target zip file
		File resultZIP = new File(destFolder, zipFileName);
	    
		// Creating an empty ArrayList for calling the listAllFiles method with
	    // "resultFolder" as root.
	    ArrayList<String> listOfFiles = new ArrayList<String>();
	
	    // Calling the recursive method listAllFiles, which lists all files in
	    // all folders in the resultFolder.
	    ArrayList<String> resultFileList;
	    // try {
	    resultFileList = listAllFilesAndFolders(srcFolder, listOfFiles);
	
	    if (resultFileList.size() == 0) {
	        return null;
	    } else {
	        // "Normalize" the paths in resultFileList for creation of
	        // ZipEntries
	        ArrayList<String> normalizedPaths = new ArrayList<String>();
	
	        for (int i = 0; i < resultFileList.size(); i++) {
	            String currentPath = resultFileList.get(i);
	            // Strip the beginning of the String, except the "[FOLDER-NAME]
	            // itself\"....
	            int index = currentPath.indexOf(srcFolder.getName());
	            currentPath = currentPath.substring(index);
	            // Delete the [FOLDER-NAME] part of the paths
	            currentPath = currentPath.replace(srcFolder.getName() + File.separator, "");
	            // add the normalized path to the list
	            normalizedPaths.add(currentPath);
	        }
	
	        // Write the output ZIP
	        ZipOutputStream zipWriter;
	        
	        try {
	            zipWriter = new ZipOutputStream(new FileOutputStream(resultZIP));
	            zipWriter.setLevel(9);
	
	            // writing the resultFiles to the ZIP
	            for (int i = 0; i < normalizedPaths.size(); i++) {
	                // Creating the ZipEntries using the normalizedList
	            	ZipEntry zipEntry = new ZipEntry(normalizedPaths.get(i));
	            	if(zipEntry.isDirectory()) {
	            		zipWriter.putNextEntry(new ZipEntry(normalizedPaths.get(i)));
	            	}
	            	else {
	            		zipWriter.putNextEntry(new ZipEntry(normalizedPaths.get(i)));
		                // And getting the files to write for the ZipEntry from the
		                // resultFileList
		                File currentFile = new File(resultFileList.get(i));
		                if(currentFile.isFile()) {
			                // getting the byte[] to write
			                byte[] current = readFileIntoByteArray(currentFile);
			                zipWriter.write(current);
			                zipWriter.flush();
			                zipWriter.closeEntry();
		                }
	            	}
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
	    return resultZIP;
	}

	/**
	 * Extracts all files from a given Zip file.
	 * 
	 * @param zipFile the zip file to extract files from
	 * @param destDir the folder where the extracted files should be placed in
	 * @return a List<File> with all extracted files.
	 */ 
	public static List<File> extractFilesFromZip(File zipFile, File destDir) {
    	
    	List<File> extractedFiles = new ArrayList<File>();
    	
    		// Open the ZIP file
            try {
            	ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
            	OutputStream out = null;
            	
            	ZipEntry entry = null;
            	
				while((entry = in.getNextEntry())!=null) {
				    // Get the first entry
				    //entry = in.getNextEntry();
//					System.out.println("Entry name: " + entry.getName());
					File outFile = null;
				    if(entry.isDirectory()) {
				    	outFile = new File(destDir, entry.getName());
				    	boolean createdFolder = outFile.mkdirs();
				    	extractedFiles.add(outFile);
				    }
				    else {
						// Open the output file
					    outFile = new File(destDir, entry.getName());
//					    System.out.println("Create file: " + outFile.getAbsolutePath());
					    outFile.createNewFile();
					    out = new FileOutputStream(outFile);
					    // Transfer bytes from the ZIP file to the output file
					    byte[] buf = new byte[1024];
					    int len;
					    
					    while ((len = in.read(buf)) > 0) {
					        out.write(buf, 0, len);
					    }
					 // Add extracted Files to List
					    extractedFiles.add(outFile);

				    }
				}
				// Close the streams
			    out.close();
			    in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return extractedFiles;
    }

    private static ArrayList<String> listAllFilesAndFolders(File dir,
            ArrayList<String> list) {
    	boolean dirIsDir = dir.isDirectory();
//    	if(dirIsDir) {
//    		System.out.println("dir: " + dir.getName() + " is a Directory!");
//    	}
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
            	File currentFile = files[i];
            	boolean currentFileIsDir = currentFile.isDirectory();
//            	if(currentFileIsDir) {
//            		System.out.println("currentFile: " + currentFile.getName() + " is a Directory!");
//            	}
                if (currentFile.isDirectory()) {
                	list.add(currentFile.getPath() + "/");
                	listAllFilesAndFolders(currentFile, list);
                } else {
                    list.add(currentFile.getPath());
                }
            }
        }
        return list;
    }

}
