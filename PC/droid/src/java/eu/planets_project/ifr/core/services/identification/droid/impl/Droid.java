package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

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

	/**
	 * Identify a file represented as a byte array using Droid
	 * 
	 * @param byteIn
	 *            The file to identify using Droid (as a byte array)
	 * @return Returns the Pronom IDs found for the file
	 */
	// operationName = Droid.NAME,
	@WebMethod(action = PlanetsServices.NS + "/" + Droid.NAME)
	// name = Droid.NAME + "Result",
	@WebResult(targetNamespace = PlanetsServices.NS + "/" + Droid.NAME, partName = Droid.NAME
			+ "Result")
	public Types identifyBytes(byte[] byteIn) {
		// Determine the working directories:
		String sigFileLocation = FileHelper.configFolder();
		String tempFile = FileHelper.tempFolder() + "temp_droid";
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
					uris[hitCounter] = new URI("info:pronom/" + formatHit.getFileFormatPUID());
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
	 * Identify a file represented as a file name using Droid
	 * 
	 * @param fileName
	 *            The file name of the file to identify
	 * @return Returns an array with the Pronom IDs for the specified file
	 */
	@WebMethod()
	public Types identifyFile(String fileName) {
		byte[] array = FileHelper.byteArrayForFile(fileName);
		return identifyBytes(array);
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
