package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Types;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;

/**
 * Droid identification service
 * 
 * @author Fabian Steeg, Carl Wilson
 * 
 */
@WebService(name = Droid.NAME, serviceName = IdentifyOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(IdentifyOneBinary.class)
@Remote(IdentifyOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public class Droid implements IdentifyOneBinary, Serializable {
	private static final String TEMP_FILE = "temp_droid";
	private static final long serialVersionUID = -7116493742376868770L;
	public static final String NAME = "Droid";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			IdentifyOneBinary.NAME);

	/**
	 * Identify a file represented as a byte array using Droid
	 * 
	 * @param byteIn
	 *            The file to identify using Droid (as a byte array)
	 * @return Returns the Pronom IDs found for the file as URIs in a Types
	 *         object
	 */
	@WebMethod(operationName = IdentifyOneBinary.NAME, action = PlanetsServices.NS
			+ "/" + IdentifyOneBinary.NAME)
	@WebResult(name = IdentifyOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + IdentifyOneBinary.NAME, partName = IdentifyOneBinary.NAME
			+ "Result")
	public Types identifyOneBinary(byte[] byteIn) {
		// Determine the working directories:
		String sigFileLocation = FileHelper.configFolder();
		String tempFile = FileHelper.tempFile(TEMP_FILE);
		FileHelper.storeAsTempFile(byteIn, tempFile);
		// Here we start using the Droid API:
		AnalysisController controller = new AnalysisController();
		try {
			controller.readSigFile(sigFileLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		controller.addFile(tempFile);
		controller.setVerbose(false);
		controller.runFileFormatAnalysis();
		Iterator<IdentificationFile> iterator = controller.getFileCollection()
				.getIterator();
		Types retVal = null;
		// We identify one file only:
		if (iterator.hasNext()) {
			IdentificationFile file = iterator.next();
			waitFor(file);
			URI[] uris = new URI[file.getNumHits()];
			// Retrieve the results:
			try {
				for (int hitCounter = 0; hitCounter < file.getNumHits(); hitCounter++) {
					FileFormatHit formatHit = file.getHit(hitCounter);
					uris[hitCounter] = new URI("info:pronom/"
							+ formatHit.getFileFormatPUID());
				}
			} catch (URISyntaxException _excep) {
				_excep.printStackTrace();
			}
			retVal = new Types(uris, file.getClassificationText());
		}
		FileHelper.deleteTempFile(tempFile);
		return retVal;
	}

	/**
	 * Identify a file represented as a file name using Droid. This is a utility
	 * method to enable SOAP-based testing, it converts the specified file into
	 * a byte array and calls the actual identify method with that
	 * 
	 * @param fileName
	 *            The file name of the file to identify
	 * @return Returns a Types object containing an array with the Pronom IDs as
	 *         URIs for the specified file
	 */
	@WebMethod()
	public Types identifyOneFile(String fileName) {
		byte[] array = FileHelper.byteArrayForFile(fileName);
		return identifyOneBinary(array);
	}

	private void waitFor(IdentificationFile file) {
		int slept = 0;
		/*
		 * Droid runs the identification in a Thread, so we have to wait until
		 * it finishes...
		 */
		while (file.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED
				/* ...but we won't wait forever */
				&& slept < 300) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			slept++;
		}
	}
}
