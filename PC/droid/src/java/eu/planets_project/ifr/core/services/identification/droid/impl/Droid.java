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

@WebService(name = Droid.NAME, serviceName = Droid.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@Stateless()
public class Droid {

	public static final String NAME = "Droid";
	public static final QName QNAME = new QName(PlanetsServices.NS, Droid.NAME);

	private static String SIG = "DROID_SignatureFile_Planets.xml";
	private static final String TEMP = "/Users/fsteeg/Documents/eclipsestuff/workspace/if_sp/components/droid/temp";

	private static final String CONF = "/server/default/conf/";
	private static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";
	private static final String LOCAL = "appserver";

	@WebMethod(operationName = Droid.NAME, action = PlanetsServices.NS + "/"
			+ Droid.NAME)
	@WebResult(name = Droid.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + Droid.NAME, partName = Droid.NAME + "Result")
	public String[] identify(byte[] byteIn) throws Exception {

		/*
		 * If running in JBoss we use the deployment directory, else (like when
		 * running a unit test) we use the project directory to retrieve the
		 * concepts file:
		 */
		String deployed = System.getProperty(JBOSS_HOME_DIR_KEY);
		String configFolder = (deployed != null ? deployed : LOCAL) + CONF;
		String sigFile = configFolder + SIG;

		String tempFolder = tempFolder();
		tempFolder = tempFolder == null ? TEMP : tempFolder;
		// Store the byte array as a temp file:
		FileOutputStream o = new FileOutputStream(TEMP);
		o.write(byteIn);
		o.close();
		AnalysisController controller = new AnalysisController();
		controller.readSigFile(sigFile);
		// Use the temp file:
		controller.addFile(TEMP);
		System.out.println(controller.getFileCollection().getIterator()
				.hasNext());
		controller.setVerbose(false);
		controller.runFileFormatAnalysis();
		Iterator<IdentificationFile> iterator = controller.getFileCollection()
				.getIterator();
		String[] result = null;
		while (iterator.hasNext()) {
			IdentificationFile next = iterator.next();
			int slept = 0;
			while (next.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED
					&& slept < 300) {
				Thread.sleep(100);
				slept++;
			}
			result = new String[next.getNumHits()];
			for (int hitCounter = 0; hitCounter < next.getNumHits(); hitCounter++) {
				FileFormatHit formatHit = next.getHit(hitCounter);
				result[hitCounter] = formatHit.getFileFormatPUID();
			}
		}
		// In the end, delete the temp file:
		File tempFile = new File(TEMP);
		boolean delete = tempFile.delete();
		if (!delete) {
			System.err.println("Could not delete: " + tempFile);
		}
		return result;
	}
	@WebMethod()
	public String[] identifyMock(String fileName){
		File file = new File(fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			byte[] array = new byte[(int)in.available()];
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
		return null;
	}

}
