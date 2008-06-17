package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * Helper class for accessing folders (in JBoss and local), storing temp files
 * and conversion of files to byte arrays, supplied via static methods
 * 
 * @author Fabian Steeg
 */
public class FileHelper {
	private static String SIG = "DROID_SignatureFile_Planets.xml";
	private static final String CONF = "/server/default/conf/";
	private static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";
	public static final String LOCAL = "PC/droid/src/resources/";

	/**
	 * @param location
	 *            The location of the file
	 * @return Returns the file as a byte array or null if something went wrong
	 */
	public static byte[] byteArrayForFile(String location) {
		File file = new File(location);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			if (file.length() > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("The file at " + location
						+ " is too large to be represented as a byte array!");
			}
			byte[] array = new byte[(int) file.length()];
			in.read(array);
			return array;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param location
	 *            the location of the file to delete
	 */
	public static void deleteTempFile(String location) {
		File tempFile = new File(location);
		boolean delete = tempFile.delete();
		if (!delete) {
			System.err.println("Could not delete: " + tempFile);
		}
	}

	/**
	 * @param byteIn
	 *            The data to save to a file
	 * @param tempFolder
	 *            The location to store the file
	 */
	public static void storeAsTempFile(byte[] byteIn, String tempFolder) {
		FileOutputStream o;
		try {
			o = new FileOutputStream(tempFolder);
			o.write(byteIn);
			o.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return If running in JBoss, returns the deployment directory, else (like
	 *         when running a unit test) returns the project directory to
	 *         retrieve the concepts file
	 */
	public static String configFolder() {
		String deployedJBossHome = System.getProperty(JBOSS_HOME_DIR_KEY);
		String sigFileFolder = (deployedJBossHome != null ? deployedJBossHome
				+ CONF : LOCAL);
		String sigFileLocation = sigFileFolder + SIG;
		return sigFileLocation;
	}

	/**
	 * @return If we are running in JBoss, returns a sand box from the data
	 *         registry, else a local temp folder in the project (like for unit
	 *         testing)
	 */
	public static String tempFolder() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			DataManagerLocal dml = (DataManagerLocal) ctx
					.lookup("planets-project.eu/DataManager/local");
			URI uri = dml.createLocalSandbox();
			System.err.println("URI: " + uri.toString());
			File _tempDir = new File(uri.toString().substring(6));
			return _tempDir.getAbsolutePath();
		} catch (NamingException e) {
			System.err.println(e.getMessage());
		} catch (URISyntaxException e) {
			System.err.println(e.getMessage());
		}
		// If that didn't work, use a local directory
		return LOCAL;
	}
}
