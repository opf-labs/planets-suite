package eu.planets_project.ifr.core.services.validation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.validate.BasicValidateOneBinary;

/**
 * PngCheck validation service
 * 
 * @author Fabian Steeg
 * 
 */
@WebService(name = PngCheck.NAME, serviceName = BasicValidateOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicValidateOneBinary.class)
@Remote(BasicValidateOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public class PngCheck implements BasicValidateOneBinary, Serializable {
	private static final long serialVersionUID = -596706737946485163L;
	public static final String NAME = "PngCheck";
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicValidateOneBinary.NAME);
	private PlanetsLogger log;
	private byte[] bytes;

	/**
	 * Validates that a file (represented as a byte array) is a PNG using
	 * PngCheck
	 * 
	 * @param binary The file to verify being a PNG using PngCheck (as a byte
	 *        array)
	 * @param uri Ignored in this service (as it only identifies PNG files), so
	 *        can be null
	 * @return Returns true if the given file is a valid PNG file, else false
	 */
	@WebMethod(operationName = BasicValidateOneBinary.NAME, action = PlanetsServices.NS
			+ "/" + BasicValidateOneBinary.NAME)
	@WebResult(name = BasicValidateOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + BasicValidateOneBinary.NAME, partName = BasicValidateOneBinary.NAME
			+ "Result")
	public boolean basicValidateOneBinary(
			@WebParam(name = "binary", targetNamespace = PlanetsServices.NS
					+ "/" + BasicValidateOneBinary.NAME, partName = "binary")
			byte[] binary,
			@WebParam(name = "fmt", targetNamespace = PlanetsServices.NS + "/"
					+ BasicValidateOneBinary.NAME, partName = "fmt")
			URI fmt) throws PlanetsException {
		log = PlanetsLogger.getLogger(this.getClass());
		/* We create a temporary file and write the bytes to that file: */
		File tempFile = tempFile("toPngCheck");
		log.debug("Temp file: " + tempFile);
		write(binary, tempFile);
		/* Then we call pngcheck with that temporary file: */
		List<String> commands = Arrays.asList("pngcheck", tempFile
				.getAbsolutePath());
		ProcessRunner pr = new ProcessRunner(commands);
		log.debug("Executing: " + commands);
		pr.run();
		/* Print some debugging info on the call: */
		String output = pr.getProcessOutputAsString();
		log.debug("PngCheck call output: " + output);
		log.debug("PngCheck call error: " + pr.getProcessErrorAsString());
		return output.startsWith("OK");
	}

	/**
	 * @param binary The bytes to write to the file
	 * @param file The file to write the bytes to
	 */
	private void write(byte[] binary, File file) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(binary);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for testing purpose: takes a file name as the only parameter,
	 * converts the file into a byte array and calls the actual identification
	 * method with that array
	 * 
	 * @param fileName The local (where the service is running) location of the
	 *        PNG file to validate
	 * @return Returns true if the file with the given name is a PNG file, else
	 *         false
	 * @throws PlanetsException
	 * @throws IOException
	 */
	@WebMethod
	public boolean basicValidateOneBinary(String fileName)
			throws PlanetsException, IOException {
		bytes = PngCheck.bytes(fileName);
		return basicValidateOneBinary(bytes, null);
	}

	/**
	 * @param name The name to use when generating the temporary file
	 * @return Returns a temporary file created using File.createTempFile or
	 *         null if something went wrong
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

	/**
	 * @param location The location of the file to read into a byte array
	 * @return Returns a byte array containing the contents of the file at
	 *         location
	 * @throws IOException
	 */
	public static byte[] bytes(String location) throws IOException {
		File f = new File(location);
		if (f.length() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("File " + location
					+ " is to large to be read into a byte array!");
		}
		byte[] out = new byte[(int) f.length()];
		InputStream is = new FileInputStream(f);
		is.read(out);
		return out;
	}

}
