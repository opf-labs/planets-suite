package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * Droid identification service
 * 
 * @author Fabian Steeg, Carl Wilson
 * 
 */
@WebService(name = Droid.NAME, serviceName = Droid.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@Stateless()
public class Droid {
	public static final String NAME = "Droid";
	public static final QName QNAME = new QName(PlanetsServices.NS, Droid.NAME);
	private static String SIG = "DROID_SignatureFile_Planets.xml";
	private static final String CONF = "/server/default/conf/";
	private static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";
	private static final String LOCAL = "PC/droid/";
	private static final String LOCAL_TEMP = LOCAL + "temp";

	/**
	 * @param byteIn
	 *            The file to identify using Droid (as a byte array)
	 * @return Returns the Pronom IDs found for the file
	 * @throws Exception
	 */
	@WebMethod(operationName = Droid.NAME, action = PlanetsServices.NS + "/"
			+ Droid.NAME)
	@WebResult(name = Droid.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + Droid.NAME, partName = Droid.NAME + "Result")
	public String[] identify(byte[] byteIn) throws Exception {
		// Determine the working directories:
		String sigFileLocation = configFolder();
		String tempFolder = tempFolder();
		storeAsTempFile(byteIn, tempFolder);
		// Here we start using the Droid API:
		AnalysisController controller = new AnalysisController();
		controller.readSigFile(sigFileLocation);
		controller.addFile(tempFolder);
		controller.setVerbose(false);
		controller.runFileFormatAnalysis();
		Iterator<IdentificationFile> iterator = controller.getFileCollection()
				.getIterator();
		String[] result = null;
		// We identify one file only:
		if (iterator.hasNext()) {
			IdentificationFile file = iterator.next();
			waitFor(file);
			result = new String[file.getNumHits()];
			// Retrieve the results:
			for (int hitCounter = 0; hitCounter < file.getNumHits(); hitCounter++) {
				FileFormatHit formatHit = file.getHit(hitCounter);
				result[hitCounter] = formatHit.getFileFormatPUID();
			}
		}
		deleteTempFile(tempFolder);
		return result;
	}

	private void waitFor(IdentificationFile file) throws InterruptedException {
		int slept = 0;
		/*
		 * Droid runs the identification in a Thread, so we have to wait until
		 * it finishes...
		 */
		while (file.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED
				/* ...but we won't wait forever */
				&& slept < 300) {
			Thread.sleep(100);
			slept++;
		}
	}

	private void deleteTempFile(String location) {
		File tempFile = new File(location);
		boolean delete = tempFile.delete();
		if (!delete) {
			System.err.println("Could not delete: " + tempFile);
		}
	}

	private void storeAsTempFile(byte[] byteIn, String tempFolder)
			throws FileNotFoundException, IOException {
		FileOutputStream o = new FileOutputStream(tempFolder);
		o.write(byteIn);
		o.close();
	}

	/**
	 * @param fileName
	 *            The file name of the file to identify
	 * @return Returns an array with the Pronom IDs for the specified file
	 */
	@WebMethod()
	public String[] identifyMock(String fileName) {
		File file = new File(fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			byte[] array = new byte[(int) in.available()];
			in.read(array);
			return identify(array);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return If running in JBoss, returns the deployment directory, else (like
	 *         when running a unit test) returns the project directory to
	 *         retrieve the concepts file
	 */
	private String configFolder() {
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
	String tempFolder() {
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
		return LOCAL_TEMP;
	}

}
