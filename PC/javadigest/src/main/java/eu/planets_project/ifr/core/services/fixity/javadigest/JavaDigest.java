package eu.planets_project.ifr.core.services.fixity.javadigest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.jboss.util.platform.Java;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.fixity.Fixity;
import eu.planets_project.services.fixity.FixityResult;
/**
 * JavaDigest Fixity service.
 * First pass simply creates an MD5 checksum, will implement other supported algorithms
 * via a parameter
 * 
 * @author  Carl Wilson
 */
@Stateless
@WebService(
		name = JavaDigest.NAME, 
		serviceName = Fixity.NAME, 
		targetNamespace = PlanetsServices.NS, 
		endpointInterface = "eu.planets_project.services.fixity.Fixity")
		@StreamingAttachment(parseEagerly = true)
		@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public final class JavaDigest implements Fixity, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8087686018249395167L;

	private static Logger log = Logger.getLogger(JavaDigest.class.getName());

	private static final String NO_DATA_MESSAGE = "No data associated with Digital Object";
	private static final String NO_ALG_MESSAGE = "The MessageDigest function does not implement the algorithm ";
	private static final String SUCCESS_MESSAGE = "Digest calculated successfully";
	private static final String SERVICE_DESC = "Fixity service based on Java " + 
		MessageDigest.class.getName() + "\n";
		
	private static final String SERVICE_AUTHOR = "Carl Wilson";
	private static final String SERVICE_VERSION = "0.1"; 
	private static final String SERVICE_PROVIDER = "The Planets Consortium.";
	private static final String TOOL_DESC = "This MessageDigest class provides applications " +
											"the functionality of a message digest algorithm" +
											", such as MD5 or SHA. Message digests are " +
											"secure one-way hash functions that take " +
											"arbitrary-sized data and output a fixed-length " +
											"hash value.";
	private static final URI SUPPORT_DOCUMENT_LOC = 
		URI.create("http://java.sun.com/j2se/1.5.0/docs/api/java/security/MessageDigest.html");

	public static final String ALG_PARAM_NAME = "AlgorithmId";
	public static final String ALG_PARAM_TYPE = "URI";
	private static final String ALG_PARAM_DESC = 
		"A Planets digest algorithm URI identifying the " +
		"requested algorithm, supported values: ";

	public static final String PROV_PARAM_NAME = "Provider";
	public static final String PROV_PARAM_TYPE = "String";
	private static final String PROV_PARAM_DESC = 
		"An algorithm provider supported values: ";

	private static final String LIST_SEP = ", ";

	private static final int DEFAULT_CHUNK_SIZE = 1024;
	

	/** The name of the service / class */
	static final String NAME = "JavaDigest";

	private URI requestedAlgorithm = JavaDigestUtils.getDefaultAlgorithmId();
	private String requestedProvider = null;
	
	public FixityResult calculateChecksum(DigitalObject digitalObject,
			List<Parameter> parameters) {
		FixityResult retResult = null;
		ServiceReport retReport = null;
		try {
			// First off, let's check the parameters, this will throw an exception
			// For invalid parameters
			this.checkParameters(parameters);
			
			// OK let's try to get the digest algorithm 
			MessageDigest messDigest = this.getMessageDigest(parameters);
			
			if (this.requestedProvider == null) {
				this.requestedProvider = messDigest.getProvider().getName();
			}

			// Now calc the result, we need the bytes from the object
			// so let's get the stream
			InputStream inStream = digitalObject.getContent().getInputStream();

			// Catch the special case of no data in the file
			if (this.addStreamBytesToDigest(messDigest,
					inStream,
					JavaDigest.DEFAULT_CHUNK_SIZE) < 1) {
				// log it, and create a new service report
				JavaDigest.log.severe(JavaDigest.NO_DATA_MESSAGE);
				retReport = new ServiceReport(ServiceReport.Type.ERROR,
						ServiceReport.Status.SUCCESS,
						JavaDigest.NO_DATA_MESSAGE);

				// And wrap it in the result
				retResult = new FixityResult(this.requestedAlgorithm.toString(),
											 null,
											 retReport);

				// Return the result
				return retResult;
			}
			messDigest.getProvider().getName();
			// OK, success so create the result
			retReport = new ServiceReport(ServiceReport.Type.INFO,
					ServiceReport.Status.SUCCESS,
					JavaDigest.SUCCESS_MESSAGE);

			// And wrap it in the result
			retResult = new FixityResult(this.requestedAlgorithm.toString(),
										 messDigest.digest(),
										 null,
										 retReport);

		} catch (NoSuchAlgorithmException e) {
			// This shouldn't happen at the moment, it supports MD5
			// Create the Error ServiceReport
			retReport = new ServiceReport(ServiceReport.Type.ERROR,
					ServiceReport.Status.TOOL_ERROR,
					e.getMessage() + " for algorithm " + this.requestedAlgorithm +
					((this.requestedProvider != null) ?
							"and provider " + this.requestedProvider  + "." :
							"."));
			// And wrap it in the result
			retResult = new FixityResult(retReport);
		} catch (IOException e) {
			// OK, a problem reading the file
			retReport = new ServiceReport(ServiceReport.Type.ERROR,
					ServiceReport.Status.TOOL_ERROR,
					e.getMessage());
			// And wrap it in the result
			retResult = new FixityResult(retReport);
		} catch (NoSuchProviderException e) {
			// Create the Error ServiceReport
			retReport = new ServiceReport(ServiceReport.Type.ERROR,
					ServiceReport.Status.TOOL_ERROR,
					e.getMessage());
			// And wrap it in the result
			retResult = new FixityResult(retReport);
		} catch (URISyntaxException e) {
			retReport = new ServiceReport(ServiceReport.Type.ERROR,
					ServiceReport.Status.TOOL_ERROR,
					e.getMessage());
			// And wrap it in the result
			retResult = new FixityResult(retReport);
		}
		// Return the result
		return retResult;
	}

	public ServiceDescription describe() {
		// Create a ServiceDescription builder
		ServiceDescription.Builder sd = new ServiceDescription.Builder(
				JavaDigest.NAME, 
				Fixity.class.getCanonicalName());

		// Add the service name, description and author
		sd.classname(this.getClass().getCanonicalName());
		sd.description(JavaDigest.SERVICE_DESC);
		sd.author(JavaDigest.SERVICE_AUTHOR);

		// Add the tool details
		sd.tool(Tool.create(null,
				MessageDigest.class.getName(),
				String.valueOf(Java.getVersion()),
				JavaDigest.TOOL_DESC,
				JavaDigest.SUPPORT_DOCUMENT_LOC.toString()));
		
		// Add doc loc, provider, and the version
		sd.furtherInfo(JavaDigest.SUPPORT_DOCUMENT_LOC);
		sd.serviceProvider(JavaDigest.SERVICE_PROVIDER);
		sd.version(JavaDigest.SERVICE_VERSION);

		// Add an any format URI, cos we can take any data
		sd.inputFormats(FormatRegistryFactory.getFormatRegistry().createAnyFormatUri());
		
		// Last is worst, the parameters
		sd.parameters(JavaDigest.getParameters());

		// Return the description
		return sd.build();
	}

	
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
	
	private static List<Parameter> getParameters() {
		List<Parameter> paramList = new ArrayList<Parameter>();
		
		// Add the algorithm selection parameter from a builder
		// We need the name and the default value
		Parameter.Builder algBuilder = 
			new Parameter.Builder(JavaDigest.ALG_PARAM_NAME,
								  JavaDigestUtils.ALG_URI_PREFIX + 
								  		JavaDigestUtils.getDefaultAlgorithmName());
		
		// We need a description, the prefix is OK
		String algParamDesc = JavaDigest.ALG_PARAM_DESC;

		// But the alg list is a bit of a nightmare, get the algs from the utils
		for (URI uri : JavaDigestUtils.getDigestAlgorithms()) {
			// And add one for each alg plus a list separator
			algParamDesc += uri + JavaDigest.LIST_SEP;
		}
		// We can now add the description 
		// but we'll need to chop off the last list separator
		algBuilder.description(algParamDesc.substring(0, algParamDesc.length() - 
														 JavaDigest.LIST_SEP.length()));
		// Finally the type and deliver parameter goodness to our list
		algBuilder.type(JavaDigest.ALG_PARAM_TYPE);
		paramList.add(algBuilder.build());

		// Now a param for 
		Parameter.Builder provBuilder = new Parameter.Builder(JavaDigest.PROV_PARAM_NAME,
		 "");

		String algProvDesc = JavaDigest.PROV_PARAM_DESC;
		for (String algProv : JavaDigestUtils.getProviders()) {
			algProvDesc += algProv + JavaDigest.LIST_SEP;
		}
		
		provBuilder.description(algProvDesc.substring(0, algProvDesc.length() - JavaDigest.LIST_SEP.length()));
		provBuilder.type(JavaDigest.PROV_PARAM_TYPE);
		
		paramList.add(provBuilder.build());
		
		return Collections.unmodifiableList(paramList);
	}
	
	private MessageDigest getMessageDigest(List<Parameter> params) throws NoSuchAlgorithmException, NoSuchProviderException {

		if (this.requestedProvider != null) {
			return MessageDigest.getInstance(
					JavaDigestUtils.getJavaAlgorithmNameFromURI(this.requestedAlgorithm),
					this.requestedProvider);
		}
		
		return MessageDigest.getInstance(
				JavaDigestUtils.getJavaAlgorithmNameFromURI(this.requestedAlgorithm));
	}

	private void checkParameters(List<Parameter> params)
					throws NoSuchAlgorithmException, URISyntaxException {
		// First check null params, no checking necessary, just use the defaults
		if (params != null) {
			// Now check out that parameter list
			for (Parameter param : params) {
				// It's the algorithm identifier param
				if (param.getName().equals(ALG_PARAM_NAME)) {
					try {
						if (JavaDigestUtils.hasAlgorithmById(URI.create(param.getValue())))
							// If it's an OK algorithm
							this.requestedAlgorithm = URI.create(param.getValue());
						else 
							// It's not a valid algorithm ID so throw
							throw new NoSuchAlgorithmException(NO_ALG_MESSAGE + param.getValue());
					} catch (IllegalArgumentException e) {
						// OK the URI has blown so throw the underlying cause
						throw (URISyntaxException)e.getCause();
					}
				} else if (param.getName().equals(PROV_PARAM_NAME)) {
					this.requestedProvider = param.getValue();
				}
			}
		}
	}
}
