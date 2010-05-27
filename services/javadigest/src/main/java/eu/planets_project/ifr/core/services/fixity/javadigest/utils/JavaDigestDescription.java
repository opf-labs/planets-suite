/**
 * 
 */
package eu.planets_project.ifr.core.services.fixity.javadigest.utils;

import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.util.platform.Java;

import eu.planets_project.ifr.core.services.fixity.javadigest.JavaDigest;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.fixity.Fixity;

/**
 * A utility class that generates the ServiceDescription object
 * for the PC Java Fixity service.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public final class JavaDigestDescription {
	private static final String SERVICE_DESC = "Fixity service based on Java " + 
		MessageDigest.class.getName() + "\n";
	
	private static final String SERVICE_AUTHOR = "Carl Wilson";
	private static final String SERVICE_VERSION = "0.1"; 
	private static final String SERVICE_PROVIDER = "The Planets Consortium.";
	private static final String TOOL_DESC = "This MessageDigest class provides applications the " +
											"functionality of the MD5 message digest algorithm." +
											" Message digests are secure one-way hash functions that " +
											"take arbitrary-sized data and output a fixed-length " +
											"hash value.";
	private static final URI SUPPORT_DOCUMENT_LOC = 
		URI.create("http://java.sun.com/j2se/1.5.0/docs/api/java/security/MessageDigest.html");

	/** The name of the parameter for the algorithm ID */
	public static final String ALG_PARAM_NAME = "AlgorithmId";
	/** The type of the parameter for the algorithm ID */
	public static final String ALG_PARAM_TYPE = "URI";
	/** The description of the parameter for the algorithm ID */
	private static final String ALG_PARAM_DESC = 
		"A Planets digest algorithm URI identifying the " +
		"requested algorithm, supported values: ";
	private static final String LIST_SEP = ", ";

	/**
	 * @return the populated ServiceDescription for the Java Fixity service 
	 */
	public static final ServiceDescription getDescription() {
		// Create a ServiceDescription builder
		ServiceDescription.Builder sd = new ServiceDescription.Builder(
				JavaDigest.NAME, 
				Fixity.class.getCanonicalName());

		// Add the service name, description and author
		sd.classname(JavaDigest.class.getCanonicalName());
		sd.description(JavaDigestDescription.SERVICE_DESC);
		sd.author(JavaDigestDescription.SERVICE_AUTHOR);

		// Add the tool details
		sd.tool(Tool.create(null,
				MessageDigest.class.getName(),
				String.valueOf(Java.getVersion()),
				JavaDigestDescription.TOOL_DESC,
				JavaDigestDescription.SUPPORT_DOCUMENT_LOC.toString()));
		
		// Add doc loc, provider, and the version
		sd.furtherInfo(JavaDigestDescription.SUPPORT_DOCUMENT_LOC);
		sd.serviceProvider(JavaDigestDescription.SERVICE_PROVIDER);
		sd.version(JavaDigestDescription.SERVICE_VERSION);

		// Add an any format URI, cos we can take any data
		sd.inputFormats(FormatRegistryFactory.getFormatRegistry().createAnyFormatUri());

		// Last is worst, the parameters
		sd.parameters(JavaDigestDescription.getParameters());

		// Return the description
		return sd.build();
	}

	/**
	 * A private helper method that puts together the java.util.List of 
	 * eu.planets_project.services.datatypes.Parameter objects for the
	 * eu.planets_project.services.datatypes.ServiceDescription. 
	 * 
	 * These are the parameters taken by the fixity service 
	 * 
	 * @return the java.util.List of parameters
	 */
	private static List<Parameter> getParameters() {
		List<Parameter> paramList = new ArrayList<Parameter>();
		
		// Add the algorithm selection parameter from a builder
		// We need the name and the default value
		Parameter.Builder algBuilder = 
			new Parameter.Builder(JavaDigestDescription.ALG_PARAM_NAME,
								  JavaDigestUtils.getDefaultAlgorithmId().toString());
		
		// We need a description, the prefix is OK
		String algParamDesc = JavaDigestDescription.ALG_PARAM_DESC;

		// But the alg list is a bit of a nightmare, get the algs from the utils
		for (URI uri : JavaDigestUtils.getDigestAlgorithmIds()) {
			// And add one for each alg plus a list separator
			algParamDesc += uri + JavaDigestDescription.LIST_SEP;
		}
		// We can now add the description 
		// but we'll need to chop off the last list separator
		algBuilder.description(algParamDesc.substring(0, algParamDesc.length() - 
				JavaDigestDescription.LIST_SEP.length()));
		// Finally the type and deliver parameter goodness to our list
		algBuilder.type(JavaDigestDescription.ALG_PARAM_TYPE);
		paramList.add(algBuilder.build());

		// Return an unmodifiable list of parameters
		return Collections.unmodifiableList(paramList);
	}
}
