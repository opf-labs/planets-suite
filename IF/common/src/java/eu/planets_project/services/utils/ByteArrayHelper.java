package eu.planets_project.services.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper class for writing byte arrays into temporary files and reading files
 * into byte arrays, supplied via static methods. This functionality is used by
 * different services in the pserv project (e.g. droid, pngcheck, jhove).
 * 
 * @author Fabian Steeg
 */
public final class ByteArrayHelper {

    /** We enforce non-instantiability with a private constructor. */
    private ByteArrayHelper() {

    }

    /**
     * Reads the contents of a file into a byte array.
     * 
     * @param file The file to read into a byte array
     * @return Returns the contents of the given file as a byte array
     */
    public static byte[] read(final File file) {
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
     * 
     * @param bytes The bytes to write into a temporary file
     * @return Returns the temporary file into which the bytes have been written
     */
    public static File write(final byte[] bytes) {
        File file = null;
        try {
            file = File.createTempFile("planets", null);
            file.deleteOnExit();
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
     * @param inputBlob
     * @param inputFilePath
     * @return the file written to
     */
    public static File writeToDestFile(final byte[] inputBlob, final String inputFilePath) {
		File file = new File(inputFilePath);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(inputFilePath), 65536);
			out.write(inputBlob);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
    }
}
