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
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilities for reading and writing data.
 * @author Thomas Kraemer thomas.kraemer@uni-koeln.de, Peter Melms
 *         (peter.melms@uni-koeln.de), Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FileUtils {
    private static Log log = LogFactory.getLog(FileUtils.class);

    private static final String SYSTEM_TEMP = System
            .getProperty("java.io.tmpdir");
    
    private static final String TEMP_STORE_DIR = "planets-if-temp-store";

    /** We enforce non-instantiability with a private constructor. */
    private FileUtils() {
    }
    
    /**
	 * @return system temp dir
	 */
	public static File getSystemTempFolder() {
	    return new File(SYSTEM_TEMP);
	}

	public static File getIfTempStoreDir() {
    	File tempStoreDir = FileUtils.createWorkFolderInSysTemp(TEMP_STORE_DIR);
    	return tempStoreDir;
    }
	
	/**
	 * @param name The name to use when generating the temp file
	 * @param suffix The suffix for the temp file to be created
	 * @return Returns a temp file created in the System-Temp folder
	 */
	public static File getTempFile(String name, String suffix) {
	    if( suffix == null ) suffix = ".tmp";
	    if( name == null ) name = "planetsTmp";
	    // Add a dot if missing:
	    if (!suffix.startsWith(".")) {
	        suffix = "." + suffix;
	    }
	    File input = null;
	    try {
	        File folder = new File(SYSTEM_TEMP, FileUtils.TEMP_STORE_DIR );
	        if( ! folder.exists() ) {
	            folder.mkdirs();
	        }
	        input = File.createTempFile(name, suffix, folder );
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

	public static File createFolderInIfTempStore (String folderName) {
		return FileUtils.createFolderInWorkFolder(getIfTempStoreDir(), folderName);
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
	        if (!madeFolder && !folder.exists()) {
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
     * Reads the contents of a file into a byte array.
     * @param file The file to read into a byte array
     * @return Returns the contents of the given file as a byte array
     */
    public static byte[] readFileIntoByteArray(final File file) {
        byte[] array = null;
        try {
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(file));
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
            out = new BufferedOutputStream(new FileOutputStream(fileName));
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
	 * Writes the contents of a byte array into a temporary file.
	 * @param bytes The bytes to write into a temporary file
	 * @return Returns the temporary file into which the bytes have been written
	 */
	public static File writeByteArrayToTempFile(final byte[] bytes) {
	    File file = null;
	    try {
	        file = getTempFile("planets",null);
	        
	        BufferedOutputStream out = new BufferedOutputStream(
	                new FileOutputStream(file), 32768);
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
	 * This method deletes all the content in a folder, without the need of
	 * passing it a PlanetsLogger instance!
	 * @param workFolder the folder you wish to delete. All contained folders
	 *        will be deleted recursively
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
     * @param workFolder the folder you wish to delete. All contained folders
     *        will be deleted recursively
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
     * @param inputStream
     * @return byte array from stream
     */
    public static byte[] writeInputStreamToBinary(InputStream inputStream) {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream();
        
        long size = writeInputStreamToOutputStream(inputStream,boStream);
        try {
            boStream.flush();
            boStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if( size > 0 ) {
            return boStream.toByteArray();
        }
        
        return null;
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
	        bos = new BufferedOutputStream(fileOut);
	    } catch (FileNotFoundException e1) {
	        e1.printStackTrace();
	    }
	    
	    long size = writeInputStreamToOutputStream( in, bos );
	    
	    try {
	        bos.flush();
	        bos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    if( log.isInfoEnabled() ) {
	        log.info("Wrote "+size+" bytes to "+target.getAbsolutePath());
	    } else {
	        System.out.println("Wrote "+size+" bytes to "+target.getAbsolutePath());
	    }
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
     * Writes an input stream to an output stream, using a sane buffer size:
     * @param in The input stream
     * @param out The output stream
     * @return
     */
    public static long writeInputStreamToOutputStream( InputStream in, OutputStream out ) {
        int BUFFER_SIZE = 65536;
        long size = 0;

        try {
            int dataBit;

            byte[] buf = new byte[BUFFER_SIZE];

            while ((dataBit = in.read(buf)) != -1) {
                out.write(buf, 0, dataBit);
                size+=dataBit;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return 0;

        } finally { 
            try {
                if( out != null ) {
                    out.flush();
// Commented the following line out, because it caused a crash with the zip utility methods, 
// when the ZipWriter tried to close the current ZipEntry.
// After a short look at the referencing methods, it seems that all Outputstreams are closed outside this
// this method! So it should do no harm to remove it here?!                    
//                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        log.info("Wrote "+size+" bytes.");
        return size;
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
//	            zipWriter.setMethod(ZipOutputStream.DEFLATED);
	            zipWriter.setLevel(9); //Best compression
	
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
			                long size = writeInputStreamToOutputStream( new FileInputStream( currentFile) , zipWriter );
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
		 * @param srcFolder the resulting zip file will contain all files in this
		 *        folder
		 * @param destFolder The folder where the created Zip file should live
		 * @param zipFileName the name, the returned zip file should have
		 * @return a ZipResult including:<br/>
		 * 			1) the zip file, which is containing all files in 'srcFolder'<br/>
		 * 			or null, if the folder does not contain a file.<br/>
		 * 			2) the checksum value of that zip file
		 * @throws IOException
		 */
			public static ZipResult createZipFileWithChecksum(File srcFolder, File destFolder, String zipFileName) {
			// The target zip file
			File resultZIP = new File(destFolder, zipFileName);
			CheckedOutputStream checksum = null;
		    
			// Creating an empty ArrayList for calling the listAllFiles method with
		    // "resultFolder" as root.
		    ArrayList<String> listOfFiles = new ArrayList<String>();
		    
	//	    listOfFiles.add(srcFolder + "/");
		
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
		        	checksum = new CheckedOutputStream(new FileOutputStream(resultZIP), new Adler32());
		            zipWriter = new ZipOutputStream(new BufferedOutputStream(checksum));
		            log.info("Checksum algorithm: Adler32");
	//	            zipWriter.setMethod(ZipOutputStream.DEFLATED);
		            zipWriter.setLevel(9); //Best compression
		            
		            // writing the resultFiles to the ZIP
		            for (int i = 0; i < normalizedPaths.size(); i++) {
		                // Creating the ZipEntries using the normalizedList
		            	ZipEntry zipEntry = new ZipEntry(normalizedPaths.get(i));
		            	if(zipEntry.isDirectory()) {
		            		zipWriter.putNextEntry(new ZipEntry(normalizedPaths.get(i)));
		            		zipWriter.closeEntry();
		            	}
		            	else {
			                // And getting the files to write for the ZipEntry from the
			                // resultFileList
			                File currentFile = new File(resultFileList.get(i));
			                // getting the byte[] to write
		                	zipWriter.putNextEntry(new ZipEntry(normalizedPaths.get(i)));
		                	
			                byte[] current = writeInputStreamToBinary(new FileInputStream(currentFile));
			                zipWriter.write(current);
			                zipWriter.flush();
			                zipWriter.closeEntry();
		            	}
		            }
		            zipWriter.flush();
		            zipWriter.finish();
		            zipWriter.close();
		            System.out.println("[createSimpleZipFile()] Checksum: " + checksum.getChecksum().getValue());
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		
		    // Getting a reference to the new ZIP file
		    ZipResult zipResult = new ZipResult(resultZIP, checksum.getChecksum().getValue());
		    return zipResult;
		}

	//
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
				    	boolean createdFolder = outFile.mkdir();
//				    	extractedFiles.add(outFile);
				    }
				    else {
						// Open the output file
					    outFile = new File(destDir, entry.getName());
//					    System.out.println("Create file: " + outFile.getAbsolutePath());
					    outFile.createNewFile();
					    out = new FileOutputStream(outFile);
					    // Transfer bytes from the ZIP file to the output file
					    writeInputStreamToFile(in, outFile);
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
	
	/**
	 * Extracts all files from a given Zip file.
	 * 
	 * @param zipFile the zip file to extract files from
	 * @param destDir the folder where the extracted files should be placed in
	 * @return a List<File> with all extracted files if the actual checksum is 
	 * equal to the passed checksumValue<br/>
	 *                      - <strong>null</strong> if not!
	 */ 
	public static List<File> extractFilesFromZipAndCheck(File zipFile, File destDir, long checksumValue) {
    	
    	List<File> extractedFiles = new ArrayList<File>();
    	CheckedInputStream checksum = null;
    	
    		// Open the ZIP file
            try {
            	checksum = 
            		new CheckedInputStream(new FileInputStream(zipFile), new Adler32());
            	
            	ZipInputStream in = new ZipInputStream(new BufferedInputStream(checksum));
            	log.info("Checksum algorithm: Adler32");
            	OutputStream out = null;
            	
            	ZipEntry entry = null;
            	
				while((entry = in.getNextEntry())!=null) {
				    // Get the first entry

					File outFile = null;
				    if(entry.isDirectory()) {
				    	outFile = new File(destDir, entry.getName());
				    	boolean createdFolder = outFile.mkdir();
//				    	extractedFiles.add(outFile);
				    }
				    else {
						// Open the output file
					    outFile = new File(destDir, entry.getName());
//						    System.out.println("Create file: " + outFile.getAbsolutePath());
//					    outFile.createNewFile();
//					    out = new BufferedOutputStream(new FileOutputStream(outFile));
//					    // Transfer bytes from the ZIP file to the output file
//					    byte[] buf = new byte[1024];
//					    int len;
//					    
//					    while ((len = in.read(buf)) > 0) {
//					        out.write(buf, 0, len);
//					        out.flush();
//					    }
					    writeInputStreamToFile(in, outFile);
					 // Add extracted Files to List
					    extractedFiles.add(outFile);

				    }
				}
				// Close the streams
//			    out.close();
			    in.close();
			    log.info("[extractFilesFromZip()] Checksum: " + checksum.getChecksum().getValue());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(checksumValue != checksum.getChecksum().getValue()) {
        		log.error("Wrong checksum. Zip file might has been damaged!");
        	}
		return extractedFiles;
    }	
	
	
	
	/**
	 * Calculates the overall size of a given list of files
	 * 
	 * @param files the list of files for which the size should be calculated
	 * @return the size in bytes as <strong>long</strong>
	 */
	public static long calculateSize(List<File> files) {
		long size = 0; 
		for (File file : files) {
			size = size + file.length();
		}
		log.info("Calculated file size: " + size + " bytes.");
		return size;
	}	
	
	
	/**
	 * Calculates the overall size of a given list of files and checks, whether these files will fit on a medium
	 * with a specific space (--> targetMediaSizeInBytes).
	 * 
	 * @param files a list of files which size should be calculated
	 * @param targetMediaSizeInBytes the size of the target medium (e.g. Floppy 1.44 MB = 1474560 bytes)
	 * @return true if the file compilation will fit on the target medium, false if not.
	 */
	public static boolean filesTooLargeForMedium(List<File> files, long targetMediaSizeInBytes) {
		long size = calculateSize(files);
		boolean filesToLarge = size > targetMediaSizeInBytes; 
		if(filesToLarge) {
			log.error("Attention: files size (" + size + " bytes) too big for target medium (" + targetMediaSizeInBytes + " bytes) !");
		}
		else {
			log.info("Summed up file size: " + size + " bytes. All files will fit on target medium with " + targetMediaSizeInBytes + " bytes capacity.");
		}
		return filesToLarge;
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
                if (currentFileIsDir) {
                	// Ignore .svn folders
                	if(currentFile.getAbsolutePath().contains(".svn")) {
                		continue;
                	}
                	if(currentFile.getName().equalsIgnoreCase("CVS")) {
                		continue;
                	}
                	list.add(currentFile.getPath() + "/"); // the closing "/" has to be there to tell the ZipOutputStream that this is a folder...
                	listAllFilesAndFolders(currentFile, list);
                } else {
                    list.add(currentFile.getPath());
                }
            }
        }
        return list;
    }
    
    public static List<File> listAllFilesAndFolders(File dir,
            List<File> list) {
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
                if (currentFileIsDir) {
                	// Ignore .svn folders
                	if(currentFile.getAbsolutePath().contains(".svn")) {
                		continue;
                	}
                	if(currentFile.getName().equalsIgnoreCase("CVS")) {
                		continue;
                	}
                	list.add(currentFile);
                	listAllFilesAndFolders(currentFile, list);
                } else {
                    list.add(currentFile);
                }
            }
        }
        return list;
    }

	public static String getExtensionFromFile(File file) {
		String name = file.getName();
		String extension = null;
		if(name.contains(".")) {
			int index = name.indexOf(".");
			extension = name.substring(index+1);
			return extension;
		}
		else {
			return null;
		}
	}

}
