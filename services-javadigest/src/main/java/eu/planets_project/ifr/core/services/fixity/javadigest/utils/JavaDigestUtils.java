package eu.planets_project.ifr.core.services.fixity.javadigest.utils;

import java.net.URI;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public final class JavaDigestUtils {
    /** Util classes providing static methods should not be instantiated. */
    private JavaDigestUtils() {/* Enforce non-instantiability */}

	private static final String ALG_URI_PREFIX = "planets:digest/alg/";
	private static final String MD5 = "MD5";
	private static final String DIGEST_SERVICE_NAME = "MessageDigest";
	
	private static Map<URI, String> algorithms;
	
	static {
		// Get the message digest algorithm names and create the hash map
		Set<String> algNames = Security.getAlgorithms(DIGEST_SERVICE_NAME);
		algorithms = new HashMap<URI, String>(algNames.size());
		
		// Now add the URIs & names
		for (String name : algNames) {
			algorithms.put(URI.create(ALG_URI_PREFIX + name), name);
		}
	}
	
	/**
	 * @return the URI identifier for the default digest algorithm (MD5)
	 */
	public static final URI getDefaultAlgorithmId() {
		// Create a URI to return
		return URI.create(ALG_URI_PREFIX + MD5);
	}
	
	/**
	 * @return the String name of the default digest algorithm (MD5)
	 */
	public static final String getDefaultAlgorithmName() {
		return MD5;
	}
	
	/**
	 * @return a List of all URI digest algorithm ids
	 */
	public static List<URI> getDigestAlgorithmIds() {
		return new ArrayList<URI>(algorithms.keySet());
	}
	
	/**
	 * @param algId
	 *        The URI key / id of a java digest algorithm  
	 * @return true if the algorithm map contains the URI passed 
	 */
	public static boolean hasAlgorithmById(URI algId) {
		return algorithms.containsKey(algId);
	}
	
	/**
	 * @param algId
	 *        The URI key / id of a java digest algorithm  
	 * @return the String name of the algorithm
	 */
	public static String getJavaAlgorithmName(URI algId) {
		return algorithms.get(algId);
	}
}
