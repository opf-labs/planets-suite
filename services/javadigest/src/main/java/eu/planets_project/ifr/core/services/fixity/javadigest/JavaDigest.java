package eu.planets_project.ifr.core.services.fixity.javadigest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.services.fixity.javadigest.utils.JavaDigestDescription;
import eu.planets_project.ifr.core.services.fixity.javadigest.utils.JavaDigestUtils;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.fixity.Fixity;
import eu.planets_project.services.fixity.FixityResult;
import eu.planets_project.services.utils.ServiceUtils;
/**
 * JavaDigest Fixity service.
 * First pass simply creates an MD5 checksum, will implement other supported algorithms
 * via a parameter
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
@Stateless
@WebService(
		name = JavaDigest.NAME, 
		serviceName = Fixity.NAME, 
		targetNamespace = PlanetsServices.NS, 
		endpointInterface = "eu.planets_project.services.fixity.Fixity")
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public final class JavaDigest implements Fixity, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8087686018249395167L;

	private static Logger log = Logger.getLogger(JavaDigest.class.getName());

	private static final String NO_DATA_MESSAGE = "No data associated with Digital Object";
	private static final String NO_ALG_MESSAGE = "The MessageDigest function does not implement the algorithm ";
	private static final String SUCCESS_MESSAGE = "Digest calculated successfully";

	// A private static that sets the size is KB of the data chunks passed to the
	// MessageDigest algorithm
	private static final int DEFAULT_CHUNK_SIZE = 1024;

	/** The name of the service / class */
	public static final String NAME = "JavaDigest";

	/**
	 * @see eu.planets_project.services.fixity.Fixity#calculateChecksum(DigitalObject, List)
	 */
	public FixityResult calculateChecksum(DigitalObject digitalObject,
			List<Parameter> parameters) {

		// The returned FixityResult & ServiceReport
		FixityResult retResult = null;
		ServiceReport retReport = null;
		try {
			// Let's get the requested message digest from the params (or default)
			URI requestedAlgId = this.getDigestIdFromParameters(parameters);

			// OK let's try to get the digest algorithm 
			MessageDigest messDigest = 
				MessageDigest.getInstance(JavaDigestUtils.getJavaAlgorithmName(requestedAlgId));
			
			// Now calc the result, we need the bytes from the object
			// so let's get the stream
			InputStream inStream = digitalObject.getContent().getInputStream();

			// Catch the special case of no data in the file
			if (this.addStreamBytesToDigest(messDigest,
					inStream,
					JavaDigest.DEFAULT_CHUNK_SIZE) < 1) {
				// log it, and create a new service report
				JavaDigest.log.severe(JavaDigest.NO_DATA_MESSAGE);
				retResult = this.createErrorResult(ServiceReport.Status.TOOL_ERROR, JavaDigest.NO_DATA_MESSAGE);

				// Return the result
				return retResult;
			}

			// OK, success so create the result
			retReport = new ServiceReport(ServiceReport.Type.INFO,
					ServiceReport.Status.SUCCESS,
					JavaDigest.SUCCESS_MESSAGE);

			// And wrap it in the result
			retResult = new FixityResult(JavaDigestUtils.getDefaultAlgorithmId().toString(),
					 					 messDigest.getProvider().getName(),
										 messDigest.digest(),
										 null,
										 retReport);

		} catch (NoSuchAlgorithmException e) {
			// This shouldn't happen at the moment, it supports MD5
			// Create the Error ServiceReport
			retResult = this.createErrorResult(ServiceReport.Status.TOOL_ERROR,
					e.getMessage() + " for algorithm " + JavaDigestUtils.getDefaultAlgorithmId() + "."); 
		} catch (IOException e) {
			// OK, a problem reading the file
			retResult = this.createErrorResult(ServiceReport.Status.TOOL_ERROR, e.getMessage()); 
		} catch (URISyntaxException e) {
			// OK, a problem with the URI id value sent
			retResult = this.createErrorResult(ServiceReport.Status.TOOL_ERROR, e.getMessage()); 
		}

		// Return the result
		return retResult;
	}

	/**
	 * @see eu.planets_project.services.PlanetsService#describe()
	 */
	public ServiceDescription describe() {
		// Call the method from the utility class
		return JavaDigestDescription.getDescription();
	}

	/**
	 * Feeds an input stream to the digest algorithm in chunks of the requested size
	 * 
	 * @param messDigest the java.security.MessageDigest checksum algorithm
	 * @param inStream the java.io.InputStream containing the byte sequence to be added to the digest
	 * @param chunkSize the size of the chunks to be fed to the digest algorithm in bytes, i.e. 1024 = 1KB chunks
	 * @return the total number of bytes in the stream
	 * @throws IOException when there's a problem reading from the InputStream inStream
	 */
	private int addStreamBytesToDigest(MessageDigest messDigest, 
			InputStream inStream,
			int chunkSize) throws IOException {
		// Save the total number of bytes added to digest for return
		int totalBytes = 0;

		// byte[] for file reading / digest feeding
		byte[] dataBytes = new byte[chunkSize];

		// First read
		int numRead = inStream.read(dataBytes);

		// Now loop through the rest of the file
		while (numRead > 0) {
			// Feed the chunk to the digest algorithm
			messDigest.update(dataBytes, 0, numRead);
			totalBytes += numRead;

			// Get the next chunk
			numRead = inStream.read(dataBytes);
		}

		// Return total bytes read
		return totalBytes;
	}
	
	/**
	 * Creates an empty FixityResult containing a ServiceReport that contains error information
	 * for a failed invocation
	 * @param status The ServiceReport status to use
	 * @param message The String message for the ServiceReport
	 * @return a FixityResult that wraps the ServiceReport for return
	 */
	private FixityResult createErrorResult(ServiceReport.Status status, String message) {
		ServiceReport retReport = new ServiceReport(ServiceReport.Type.ERROR,
												    status,
													message);
		// And wrap it in the result
		return new FixityResult(retReport);
	}

	private URI getDigestIdFromParameters(List<Parameter> params)
		throws NoSuchAlgorithmException, URISyntaxException {
		URI retVal = JavaDigestUtils.getDefaultAlgorithmId(); 

		// Now check out that parameter list
		if (params != null) {
			for (Parameter param : params) {
				// It's the algorithm identifier param
				if (param.getName().equals(JavaDigestDescription.ALG_PARAM_NAME)) {
					try {
						if (JavaDigestUtils.hasAlgorithmById(URI.create(param.getValue())))
							// If it's an OK algorithm
							return URI.create(param.getValue());
						// It's not a valid algorithm ID so throw
						throw new NoSuchAlgorithmException(NO_ALG_MESSAGE + param.getValue());
					} catch (IllegalArgumentException e) {
						// OK the URI has blown so throw the underlying cause
						throw (URISyntaxException)e.getCause();
					}
				}
			}
		}
		
		return retVal;
	}
}
