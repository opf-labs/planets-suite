package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXCDLStrings;

/**
 * PP comparator service
 * 
 * @author Fabian Steeg
 * 
 */
@WebService(name = Comparator.NAME, serviceName = BasicCompareTwoXCDLStrings.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicCompareTwoXCDLStrings.class)
@Remote(BasicCompareTwoXCDLStrings.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public class Comparator implements BasicCompareTwoXCDLStrings, Serializable {
	private static final long serialVersionUID = 1238447797051780267L;
	private static final PlanetsLogger log = PlanetsLogger
			.getLogger(Comparator.class);
	public static String COMPARATOR_HOME = System.getenv("COMPARATOR_HOME")
			+ File.separator;
	public static final String NAME = "Comparator";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicCompareTwoXCDLStrings.NAME);
	/** The file names of the result and log files: */
	private static final String LOG_TXT = "log.txt";
	private static final String RESULT_ENDING = ".cpr";
	/** The comparator executable, has to be on the path on the server */
	private static final String COMPARATOR = "comparator";

	/**
	 * @param xcdl1
	 *            The first XCDL
	 * @param xcdl2
	 *            The second XCDL
	 * @return Returns the result of comparing the first and the second XCDL
	 */
	@WebMethod(operationName = BasicCompareTwoXCDLStrings.NAME, action = PlanetsServices.NS
			+ "/" + BasicCompareTwoXCDLStrings.NAME)
	@WebResult(name = BasicCompareTwoXCDLStrings.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + BasicCompareTwoXCDLStrings.NAME, partName = BasicCompareTwoXCDLStrings.NAME
			+ "Result")
	public String basicCompareTwoXCDLStrings(String xcdl1, String xcdl2) {
		/* Create temp files for the XCDLs to be compared: */
		File tempFile1 = tempFile("XCDL1");
		log.debug("Temp file 1: " + tempFile1);
		File tempFile2 = tempFile("XCDL2");
		log.debug("Temp file 2: " + tempFile2);
		/* For storing the result, we use the temp folder: */
		String tempFolder = tempFile1.getParent();
		/* If we can't read or write to the temp folder, cancel: */
		File f = new File(tempFolder);
		if (!f.canRead() || !f.canWrite()) {
			throw new IllegalStateException("Can't read from or write to: "
					+ f.getAbsolutePath());
		}
		/* Store the given content in the temp files: */
		save(tempFile1.getAbsolutePath(), xcdl1);
		save(tempFile2.getAbsolutePath(), xcdl2);
		/* Compare the temp files: */
		List<String> commands = Arrays.asList(COMPARATOR, tempFile1
				.getAbsolutePath(), tempFile2.getAbsolutePath(), tempFolder);
		ProcessRunner pr = new ProcessRunner(commands);
		/* We change into the comparator home directory: */
		File home = new File(COMPARATOR_HOME);
		if (!home.exists()) {
			throw new IllegalStateException("COMPARATOR_HOME does not exist: "
					+ COMPARATOR_HOME);
		}
		pr.setStartingDir(home);
		log.debug("Executing: " + commands);
		pr.run();
		/* Print some debugging info on the call: */
		log.debug("Comparator call output: " + pr.getProcessOutputAsString());
		log.debug("Comparator call error: " + pr.getProcessErrorAsString());
		/* Read the resulting files: */
		String result = read(tempFolder + File.separator
				+ tempFile1.getName().split("\\.")[0] + "-"
				+ tempFile2.getName().split("\\.")[0] + RESULT_ENDING);
		String logged = read(tempFolder + File.separator + LOG_TXT);
		/* Print some debugging info on the results: */
		log.info("Comparator result: " + result);
		log.debug("Comparator log: " + logged);
		return result;
	}

	/**
	 * Helper/mock method for testing, using file locations instead of the
	 * actual data. Calls the actual comparison method above.
	 */
	@WebMethod
	public String basicCompareTwoXCDLFiles(String xcdl1Name, String xcdl2Name) {
		String content1 = read(xcdl1Name);
		String content2 = read(xcdl2Name);
		return basicCompareTwoXCDLStrings(content1, content2);
	}

	/**
	 * @param location
	 *            The location of the text file to read
	 * @return Return the content of the file at the specified location,
	 *         replacing line breaks with blanks
	 */
	public static String read(String location) {
		StringBuilder builder = new StringBuilder();
		Scanner s;
		try {
			s = new Scanner(new File(location));
			while (s.hasNextLine()) {
				builder.append(s.nextLine()).append(" ");
			}
			return builder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param fileName
	 *            The file name to write the specified content to
	 * @param content
	 *            The content to write to a file with the specified name
	 */
	private void save(String fileName, String content) {
		try {
			FileWriter out = new FileWriter(fileName);
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param name
	 *            The name to use when generating the temp file
	 * @return Returns a temp file created using File.createTempFile
	 */
	public static File tempFile(String name) {
		File input;
		try {
			input = File.createTempFile(name, null);
			input.deleteOnExit();
			return input;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
