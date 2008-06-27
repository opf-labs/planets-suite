package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
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
 * PP comparator service (work in progress)
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
	public static final String NAME = "Comparator";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicCompareTwoXCDLStrings.NAME);
	/**
	 * TODO Here, we use a hard-coded string for the working directory (where
	 * the Comparator executable and the output folder are found) on the test
	 * server at UzK (planetarium.hki.uni-koeln.de); the output folder should be
	 * given to the comparator as a parameter; then, we could get rid of this.
	 */
	private static final String WORKING_DIR = "/home/vh/Comparator/";
	/** The locations of the result and log files: */
	private static final String LOG_TXT = WORKING_DIR + "output/log.txt";
	private static final String RESULT = WORKING_DIR + "output/CPR.xml";
	/** The comparator executable */
	private static final String COMPARATOR = WORKING_DIR + "Comparator";

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
		String tempFile1 = tempFile("XCDL1");
		String tempFile2 = tempFile("XCDL2");
		/* Store the given content in the temp files: */
		save(tempFile1, xcdl1);
		save(tempFile2, xcdl2);
		/* Compare the temp files: */
		ProcessRunner pr = new ProcessRunner(Arrays.asList(COMPARATOR,
				tempFile1, tempFile2));
		pr.run();
		/* Print some debugging info on the call: */
		log.info("Comparator call output: " + pr.getProcessOutputAsString());
		log.info("Comparator call error: " + pr.getProcessErrorAsString());
		/* read the resulting files: */
		String result = read(RESULT);
		String logged = read(LOG_TXT);
		/* Print some debugging info on the results: */
		log.info("Comparator result: " + result);
		log.info("Comparator log: " + logged);
		return result;
	}

	/**
	 * Helper/Mock method for testing, using file locations instead of the
	 * actual data. Calls the actual comparison method above.
	 */
	@WebMethod()
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
	public static String tempFile(String name) {
		File input;
		try {
			input = File.createTempFile(name, null);
			input.deleteOnExit();
			return input.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
