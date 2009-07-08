package eu.planets_project.services.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Checksum;

/**
 * Utilities for reading and writing data.
 * @author Thomas Kraemer (thomas.kraemer@uni-koeln.de), Peter Melms
 *         (peter.melms@uni-koeln.de)
 */
/**
 * @author melmsp
 *
 */
public final class FileUtils {
    private static final int BUFFER = 32768;

    private static Log log = LogFactory.getLog(FileUtils.class);

    private static final String SYSTEM_TEMP = System
            .getProperty("java.io.tmpdir");

    private static final String TEMP_STORE_DIR = "planets-if-temp-store".toUpperCase();

    /** We enforce non-instantiability with a private constructor. */
    private FileUtils() {}

    /**
     * @return The system temp dir
     */
    public static File getSystemTempFolder() {
        return new File(SYSTEM_TEMP);
    }
    
    /**
     * @return the central PLANETS temp store folder
     */
    public static File getPlanetsTmpStoreFolder() {
    	File tmpStore = new File(getSystemTempFolder(), TEMP_STORE_DIR);
    	if(tmpStore.exists()) {
    		return tmpStore;
    	}
    	else {
    		tmpStore.mkdir();
    		return tmpStore;
    	}
    }
    
    public static boolean clearPlanetsTmpStoreFolder() {
    	File tmpStore = getPlanetsTmpStoreFolder();
    	if(tmpStore.exists()) {
    		return deleteAllFilesInFolder(tmpStore);
    	}
    	else {
    		return true;
    	}
    }

    /**
     * @param name The name to use when generating the temp file
     * @param suffix The suffix for the temp file to be created
     * @return Returns a temp file created in the System-Temp folder
     */
    public static File getTempFile(final String name, final String suffix) {
        String suffixToUse = suffix == null ? ".tmp" : suffix;
        // Add a dot if missing:
        if (!suffixToUse.startsWith(".")) {
            suffixToUse = "." + suffix;
        }
        String nameToUse = name == null ? "planetsTmp" : name;
        File input = null;
        try {
            File folder = new File(SYSTEM_TEMP, FileUtils.TEMP_STORE_DIR);
            if (!folder.exists()) {
                boolean mkdirs = folder.mkdirs();
                checkCreation(folder, mkdirs);
            }
            input = File.createTempFile(nameToUse, suffixToUse, folder);
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
    public static File getTempFile(final byte[] data, final String name,
            final String suffix) {
        String suffixCopy = suffix;
        if (!suffixCopy.startsWith(".")) {
            suffixCopy = "." + suffix;
        }
        File input = getTempFile(name, suffixCopy);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(input);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fos);
        }
        return input;
    }

    /**
     * @param src The source file
     * @return An input stream created from the file
     */
    public static InputStream getInputStreamFromFile(File src) {
        BufferedInputStream fileIn = null;

        try {
            fileIn = new BufferedInputStream(new FileInputStream(src));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileIn;
    }

    /**
     * @param dest The destination file
     * @return A stream to the given file
     */
    public static OutputStream getOutputStreamToFile(File dest) {
        BufferedOutputStream fileOut = null;
        try {
            fileOut = new BufferedOutputStream(new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileOut;
    }

    
    /**
     * Convenience method that creates a URL from a file in a proper (i.e. not deprecated) way, using the toURI().toURL() way. 
     * Hiding the Exception, so you don't have to put it in a try-catch block.
     * @param file
     * @return
     */
    public static URL getUrlFromFile(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    public static String randomizeFileName(String name) {
    	Random random = new Random();
    	String prefix = null;
    	String postfix = null;
    	String randomName = null;
    	if(name==null) {
    		name = "";
    	}
    	if(name.contains(".")) {
    		prefix = name.substring(0, name.lastIndexOf(".")) + "_";
    		postfix = name.substring(name.lastIndexOf("."));
    		randomName = prefix + random.nextInt(Integer.MAX_VALUE) + postfix;
    	}
    	else {
    		randomName = name + "_" + random.nextInt(Integer.MAX_VALUE);
    	}
    	return randomName;
    }
    
    public static String getOutputFileNameFor(String inputFileName, String outFileExtension) {
    	String fileName = null;
    	if(outFileExtension==null) {
    		outFileExtension = "bin";
    	}
		if(inputFileName.contains(" ")) {
			inputFileName = inputFileName.replaceAll(" ", "_");
		}
		
		if(inputFileName.contains(".")) {
			fileName = inputFileName.substring(0, inputFileName.lastIndexOf(".")) + "." + outFileExtension;
		}
		else {
			fileName = inputFileName + "." + outFileExtension;
		}
		return fileName;
    }

	/**
     * @param src The source file
     * @param dest The destination file
     * @return True, if successful
     */
    public static boolean copyFileTo(File src, File dest) {
        InputStream in = getInputStreamFromFile(src);
        OutputStream out = getOutputStreamToFile(dest);
        long destSize = writeInputStreamToOutputStream(in, out);
        if (destSize == src.length()) {
            close(in);
            flush(out);
            close(out);
            return true;
        } else {
            close(in);
            flush(out);
            close(out);
            return false;
        }
    }

    /**
     * @param folderName The name of the folder to be created in the System-Temp
     *        folder. The name could contain '/' or '\'. In this case, all
     *        nested folders will be created.
     * @return Returns the created folder as a File object, or the deepest
     *         nested folder
     */
    public static File createWorkFolderInSysTemp(final String folderName) {
        File folder = null;
        folder = new File(SYSTEM_TEMP, folderName);
        if (folderName.contains("/") || folderName.contains(File.separator)) {
            boolean mkdirs = folder.mkdirs();
            checkCreation(folder, mkdirs);
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
    public static File createFolderInWorkFolder(final File parentFolder,
            final String folderName) {
        File folder = null;
        folder = new File(parentFolder, folderName);
        boolean mkdirs = folder.mkdirs();
        checkCreation(folder, mkdirs);
        return folder;
    }

    /**
     * @param folder The file we tried to create
     * @param mkdirs The result of creating the file
     */
    private static void checkCreation(final File folder, final boolean mkdirs) {
        log.info(String.format("Created folder '%s': %s", folder, mkdirs));
        if (!folder.exists()) {
            throw new IllegalArgumentException(String.format(
                    "Could not create '%s'", folder));
        }
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
     * @param bytes The data to write
     * @param dest The destination file
     * @return The file containing the given data
     */
    public static boolean writeByteArrayToFile(final byte[] bytes, File dest) {
    	return writeByteArrayToFile(bytes, dest.getAbsolutePath()).exists();
    }

    /**
     * Writes the contents of a byte array into a temporary file.
     * @param bytes The bytes to write into a temporary file
     * @return Returns the temporary file into which the bytes have been written
     */
    public static File writeByteArrayToTempFile(final byte[] bytes) {
        File file = null;
        try {
            file = getTempFile("planets", null);

            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file), BUFFER);
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
     * @param textFile The file to read from
     * @return The file contents as a string
     */
    public static String readTxtFileIntoString(final File textFile) {
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * @param content The content to write to destination
     * @param destination The destination to write content to
     * @return file A file at destination with the given content
     */
    public static File writeStringToFile(final String content,
            final String destination) {
        File result = new File(destination);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(result),
                    BUFFER);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param content The content to write to destination
     * @param target The destination to write content to
     * @return file A file at destination with the given content
     */
    public static File writeStringToFile(final String content, final File target) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(target),
                    BUFFER);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target;
    }

    /**
     * @param inputStream The stream to write to a byte array
     * @return The byte array created from the stream
     */
    public static byte[] writeInputStreamToBinary(final InputStream inputStream) {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream();
        long size = writeInputStreamToOutputStream(inputStream, boStream);
        try {
            boStream.flush();
            boStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (size > 0) {
            return boStream.toByteArray();
        }
        return null;
    }

    /**
     * Writes an input stream to the specified file.
     * @param stream The stream to write to the file
     * @param target The file to write the stream to
     */
    public static void writeInputStreamToFile(final InputStream stream,
            final File target) {
        BufferedOutputStream bos = null;
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(target);
            bos = new BufferedOutputStream(fileOut);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        long size = writeInputStreamToOutputStream(stream, bos);
        try {
            if (bos != null) {
                bos.flush();
                bos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (log.isInfoEnabled()) {
            log.info("Wrote " + size + " bytes to " + target.getAbsolutePath());
        } else {
            System.out.println("Wrote " + size + " bytes to "
                    + target.getAbsolutePath());
        }
    }

    /**
     * This method writes an InputStream to a file. If a file with the same name
     * and path exists already, a random number is appended to the filename and
     * the "renamed" file is returned.
     * @param in the input stream to write to a file
     * @param parentFolder the folder the file should be created in
     * @param fileName the name of the file to be created
     * @return a File object with the given name and path
     */
    public static File writeInputStreamToFile(final InputStream in,
            final File parentFolder, final String fileName) {
        String name = fileName;
        File target = new File(parentFolder, name);
        if (target.exists()) {
            long randonNr = (long) (Math.random() * 1000000);
            if (fileName.contains(".")) {
                name = name.substring(0, name.lastIndexOf(".")) + "_"
                        + randonNr + name.substring(name.lastIndexOf("."));
            } else {
                name = name + randonNr;
            }
            target = new File(parentFolder, name);
        }
        writeInputStreamToFile(in, target);
        return target;
    }

    /**
     * @param inputStream The stream to write
     * @param fileName The file name to write the stream to
     * @param suffix The suffix to use
     * @return A temp file containing the stream contents
     */
    public static File writeInputStreamToTmpFile(final InputStream inputStream,
            final String fileName, final String suffix) {
        String suffixToUse = suffix;
        if (suffixToUse != null) {
            if (!suffixToUse.startsWith(".")) {
                suffixToUse = "." + suffixToUse;
            }
        }
        File file = getTempFile(fileName, suffixToUse);
        writeInputStreamToFile(inputStream, file);
        return file;
    }

    /**
     * Writes an input stream to an output stream, using a sane buffer size.
     * @param in The input stream
     * @param out The output stream
     * @return The number of bytes written
     */
    public static long writeInputStreamToOutputStream(final InputStream in,
            final OutputStream out) {
        long size = 0;
        try {
            int dataBit;
            byte[] buf = new byte[BUFFER];
            while ((dataBit = in.read(buf)) != -1) {
                out.write(buf, 0, dataBit);
                size += dataBit;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    // Commented the following line out, because it caused a
                    // crash with the zip utility methods,
                    // when the ZipWriter tried to close the current ZipEntry.
                    // After a short look at the referencing methods, it seems
                    // that all Outputstreams are closed outside this
                    // this method! So it should do no harm to remove it here?!
                    // out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        log.info("Wrote " + size + " bytes.");
        return size;
    }

	/**
     * @param srcFolder the resulting zip file will contain all files in this
     *        folder
     * @param destFolder The folder where the created Zip file should live
     * @param zipFileName the name, the returned zip file should have
     * @return a zip file containing all files in 'srcFolder' or null, if the
     *         folder does not contain a file.
     */
    public static File createSimpleZipFile(final File srcFolder,
            final File destFolder, final String zipFileName) {
       /* // The target zip file
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
            
//        	ArrayList<String> normalizedPaths = new ArrayList<String>();
//            for (int i = 0; i < resultFileList.size(); i++) {
//                String currentPath = resultFileList.get(i);
//                // Strip the beginning of the String, except the "[FOLDER-NAME]
//                // itself\"....
//                int index = currentPath.indexOf(srcFolder.getName());
//                currentPath = currentPath.substring(index);
//                // Delete the [FOLDER-NAME] part of the paths
//                currentPath = currentPath.replace(srcFolder.getName()
//                        + File.separator, "");
//                // add the normalized path to the list
//                normalizedPaths.add(currentPath);
//            }
        	
        	List<String> normalizedPaths = normalizePaths(srcFolder);
            
            // Write the output ZIP
            ZipOutputStream zipWriter;
            try {
                zipWriter = new ZipOutputStream(new FileOutputStream(resultZIP));
                // zipWriter.setMethod(ZipOutputStream.DEFLATED);
                zipWriter.setLevel(ZIP_COMPRESSION_LEVEL);
                // writing the resultFiles to the ZIP
                for (int i = 0; i < normalizedPaths.size(); i++) {
                    // Creating the ZipEntries using the normalizedList
                    ZipEntry zipEntry = new ZipEntry(normalizedPaths.get(i));
                    if (zipEntry.isDirectory()) {
                        zipWriter.putNextEntry(zipEntry);
                    } else {
                        zipWriter.putNextEntry(zipEntry);
                        // And getting the files to write for the ZipEntry from
                        // the
                        // resultFileList
                        File currentFile = new File(resultFileList.get(i));
                        if (currentFile.isFile()) {
                            long size = writeInputStreamToOutputStream(
                                    new FileInputStream(currentFile), zipWriter);
                            log.info(String.format(
                                    "Wrote %s bytes from %s to %s", size,
                                    currentFile, zipWriter));
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
        return resultZIP;*/
    	
    	return ZipUtils.createZip(srcFolder, destFolder, zipFileName, false);
    }
    
    /**
     * @param srcFolder the resulting zip file will contain all files in this
     *        folder
     * @param destFolder The folder where the created Zip file should live
     * @param zipFileName the name, the returned zip file should have
     * @return a ZipResult including:<br/>
     *         1) the zip file, which is containing all files in 'srcFolder'<br/>
     *         or null, if the folder does not contain a file.<br/>
     *         2) the checksum value of that zip file
     * @throws IOException
     */
    public static ZipResult createZipFileWithChecksum(final File srcFolder,
            final File destFolder, final String zipFileName) {
    	
        return ZipUtils.createZipAndCheck(srcFolder, destFolder, zipFileName, false);
    }
    
//    public static File insertFileInZip(File zip, File toInsert, String targetLocationInZip) {
//		return zip;
//    }
//    
//    public static File deleteFileFromZip(File zip, String deleteFileLocationInZip) {
//    	return zip;
//    }

    /**
     * Extracts all files from a given Zip file.
     * NOTE: method is left here to keep the API unchanged, but is now redirected to ZipUtils.unzipTo(..).
     * 
     * @param zipFile the zip file to extract files from
     * @param destDir the folder where the extracted files should be placed in
     * @return All extracted files
     */
    public static List<File> extractFilesFromZip(final File zipFile,
            final File destDir) {
    	return ZipUtils.unzipTo(zipFile, destDir);
    }

    /**
     * Extracts all files from a given Zip file.
     * NOTE: method is left here to keep the API unchanged, but is now redirected to ZipUtils.checkAndUnzipTo(..).
     * 
     * @param zipFile the zip file to extract files from
     * @param destDir the folder where the extracted files should be placed in
     * @param checksumValue The checksum value
     * @return All extracted files if the actual checksum is equal to the passed
     *         checksumValue<br/>
     *         - <strong>null</strong> if not!
     */
    public static List<File> extractFilesFromZipAndCheck(final File zipFile,
            final File destDir, final long checksumValue) {
    	Checksum checksum = new Checksum("MD5", String.valueOf(checksumValue));
        return ZipUtils.checkAndUnzipTo(zipFile, destDir, checksum);
    }

    /**
     * Extracts all files from a given Zip file. Convenience method that takes a
     * Planets-IF Checksum instead of a long.
     * NOTE: method is left here to keep the API unchanged, but is now redirected to ZipUtils.checkAndUnzipTo(..).
     * 
     * @param zipFile the zip file to extract files from
     * @param destDir the folder where the extracted files should be placed in
     * @param planetsChecksum a Checksum object
     * @return All extracted files if the actual checksum is equal to the passed
     *         planetsChecksum<br/>
     *         - <strong>null</strong> if not!
     */
    public static List<File> extractFilesFromZipAndCheck(final File zipFile,
            final File destDir, final Checksum planetsChecksum) {
    	
    	return ZipUtils.checkAndUnzipTo(zipFile, destDir, planetsChecksum);
    }


    /**
     * @param file The file to rename to an 8-digit file name
     * @return The renamed file
     */
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
        }
        newName = newName + ext;
        File renamedFile = new File(new File(parent), newName);
        boolean renamed = file.renameTo(renamedFile);
        if (!renamed) {
            throw new IllegalArgumentException("Could not rename: " + file);
        }
        return renamedFile;
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
            log.error("Attention: files size (" + size
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
     * @param dir The dir to list
     * @param list The list to add the contents of dir to
     * @return The given list, with the contents of dir added, including files and folders
     */
    public static List<File> listAllFilesAndFolders(final File dir,
            final List<File> list) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File currentFile = files[i];
                boolean currentFileIsDir = currentFile.isDirectory();
                if (currentFileIsDir) {
                    // Ignore hidden folders
                    if (currentFile.isHidden()) {
                        continue;
                    }
                    if (currentFile.getName().equalsIgnoreCase("CVS")) {
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
    

    /**
     * @param dir The dir to list
     * @param list The list to add the contents of dir to
     * @return The given list, with the contents of dir added, only including files NOT folders
     */
    public static List<File> listAllFiles(final File dir, final List<File> list) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File currentFile = files[i];
                boolean currentFileIsDir = currentFile.isDirectory();
                if (currentFileIsDir) {
                    // Ignore hidden folders
                    if (currentFile.isHidden()) {
                        continue;
                    }
                    if (currentFile.getName().equalsIgnoreCase("CVS")) {
                        continue;
                    }
                    // list.add(currentFile);
                    listAllFiles(currentFile, list);
                } else {
                    list.add(currentFile);
                }
            }
        }
        return list;
    }

    /**
     * This method returns all the free/available Drive letters on your System,
     * based on File.listRoots().
     * @return a List of String containing all free drive letters on a windows
     *         system OR null, if all letters are beeing used. NOTE: On
     *         Unix/Linux based systems "/" the root is returned, as we don't
     *         have drive letters.
     */
    public static List<String> listAvailableDriveLetters() {
        ArrayList<String> freeLetters = new ArrayList<String>();
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            freeLetters.add("/");
            log
                    .info("Running on Non-Windows OS, so we don't have DriveLetters ;-)");
            return freeLetters;
        }
        File[] roots = File.listRoots();

        ArrayList<String> usedLetters = new ArrayList<String>(roots.length);

        for (int i = 0; i < roots.length; i++) {
            usedLetters.add(roots[i].getAbsolutePath());
        }

        // Fill the template Arraylist
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            freeLetters.add(ch + ":\\");
        }

        // Remove the used letters from our template list of ALL possible
        // letters
        for (String currentLetter : usedLetters) {
            if (freeLetters.contains(currentLetter)) {
                freeLetters.remove(currentLetter);
            }
        }
        // return the un-used letters for further use ;-)
        if (freeLetters.size() > 0) {
            return freeLetters;
        } else {
            return null;
        }
    }

    /**
     * @param file The file to determine the extension from
     * @return The extension or null
     */
    public static String getExtensionFromFile(final File file) {
        String name = file.getName();
        String extension = null;
        if (name.contains(".")) {
            int index = name.indexOf(".");
            extension = name.substring(index + 1);
            return extension;
        } else {
            return null;
        }
    }

    /**
     * @param out The closeable (Writer, Stream, etc.) to close
     */
    public static void close(final Closeable out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param out The closeable (Writer, Stream, etc.) to close
     */
    public static void flush(final OutputStream out) {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param file The file to call mkdir on and check for the result
     * @return The result of calling mkdirs on the given file
     * @throws IllegalArgumentException if the creation was not successful and
     *         the file does not already exist
     */
    public static boolean mkdir(final File file) {
        boolean mkdir = file.mkdir();
        handle(mkdir, file);
        return mkdir;
    }

    /**
     * @param file The file to call mkdirs on and check for the result
     * @return The result of calling mkdirs on the given file
     * @throws IllegalArgumentException if the creation was not successful and
     *         the file does not already exist
     */
    public static boolean mkdirs(final File file) {
        boolean mkdirs = file.mkdirs();
        handle(mkdirs, file);
        return mkdirs;
    }

    private static void handle(boolean mkdir, File file) {
        if (!mkdir && !file.exists()) {
            throw new IllegalArgumentException("Could not create " + file);
        }

    }
    
    /**
	 * This method deletes all the content in a folder, without the need of
	 * passing it a PlanetsLogger instance!
	 * @param workFolder the folder you wish to delete. All contained folders
	 *        will be deleted recursively
	 * @return true, if all folders were deleted and false, if not.
	 */
	public static boolean deleteTempFiles(final File workFolder) {
	    return deleteTempFiles(workFolder, true);
	}
	
	
	/**
	 * This method deletes all the content in a folder, without the need of
	 * passing it a PlanetsLogger instance!
	 * @param workFolder the folder you wish to delete. All contained folders
	 *        will be deleted recursively
	 * @return true, if all folders were deleted and false, if not.
	 */
	private static boolean deleteTempFiles(final File workFolder, boolean deleteFolder) {
	    if (workFolder.isDirectory()) {
	        File[] entries = workFolder.listFiles();
	        for (int i = 0; i < entries.length; i++) {
	            File current = entries[i];
	            boolean deleteTempFiles = deleteTempFiles(current);
	            if (!deleteTempFiles) {
	                return false;
	            } else {
	                log.info("Deleted: " + current);
	            }
	        }
	        if(deleteFolder) {
	        	return workFolder.delete() ? true : false;
	        }
	        else {
	        	return true;
	        }
	    }
	    if(deleteFolder) {
	    	return workFolder.delete() ? true : false;
	    } 
	    else {
	    	return true;
	    }
	}
	
	
	
	/**
	 * Deletes all files in 'folder' without deleting 'folder' itself.
	 * 
	 * @param folder the folder to delete all files from
	 * @return true, if all files has been deleted.
	 */
	public static boolean deleteAllFilesInFolder(final File folder) {
		return deleteTempFiles(folder, false);
	}
	

	/**
     * @param file The file to delete
     * @throws IllegalArgumentException if the deletion was not successful and
     *         the file still exist
     */
    public static void delete(final File file) {
        boolean ok = file.delete();
        if (!ok && file.exists()) {
            throw new IllegalArgumentException("Could not delete: " + file);
        }

    }

}
