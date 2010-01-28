package eu.planets_project.ifr.core.services.fixity.javadigest;

import java.net.URI;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class JavaDigestUtils {
    /** Util classes providing static methods should not be instantiated. */
    private JavaDigestUtils() {/* Enforce non-instantiability */}

	static final String ALG_URI_PREFIX = "planets:digest/java/alg/";
	static final String ALG_ALIAS_URI_PREFIX = "planets:digest/java/alg/alias/";
	static final String MD5 = "MD5";

	static final String DIGEST_PREFIX = "MessageDigest.";
	static final String DIGEST_ALIAS_PREFIX = "Alg.Alias." + DIGEST_PREFIX;

	public static final Map<URI, String> algorithms = 
		new HashMap<URI, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4494715265843908566L;
			{
				for (URI uri : JavaDigestUtils.getDigestAlgorithms()) {
					put(uri, JavaDigestUtils.getJavaAlgNameFromURI(uri));
				}
			}
		};
		
	public static final Map<String, Set<URI>> providerDetails = 
		new HashMap<String, Set<URI>>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1425731176683194334L;
			{
				for (String provider : JavaDigestUtils.getProviders()) {
					put(provider, JavaDigestUtils.getAlgorithmsForProvider(provider));
				}
			}
		};
	
	public static String getDefaultAlgorithm() {return JavaDigestUtils.MD5;}

	static URI[] getDigestAlgorithms() {
		Set<URI> result = new HashSet<URI>();
		
		for (Provider provider : Security.getProviders()) {
			for (URI uri : JavaDigestUtils.getAlgorithmsForProvider(provider)) {
				result.add(uri);
			}
		}
		
		return result.toArray(new URI[result.size()]);
	}
	
	static String[] getProviders() {
		Set<String> result = new HashSet<String>();
		
		for (Provider provider: Security.getProviders()) {
			result.add(provider.getName());
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	static Set<URI> getAlgorithmsForProvider(String name) {
		return JavaDigestUtils.getAlgorithmsForProvider(Security.getProvider(name));
	}
	
	private static Set<URI> getAlgorithmsForProvider(Provider provider) {
		// If provider is null return a null set
		if (provider == null) return null;
		
		Set<URI> result = new HashSet<URI>();

		for (Object key : provider.keySet()) {
			String thisKey = (String) key;
			thisKey = thisKey.split(" ")[0];
			
			if (thisKey.startsWith(DIGEST_PREFIX)) {
				result.add(URI.create(ALG_URI_PREFIX + thisKey.substring(DIGEST_PREFIX.length())));
			} else if (thisKey.startsWith(DIGEST_ALIAS_PREFIX)) {
				result.add(URI.create(ALG_ALIAS_URI_PREFIX + thisKey.substring(DIGEST_PREFIX.length())));
			}
		}
		return result;
	}

	public static String getJavaAlgNameFromURI(URI uri) {
		String name = null;
		
		name = uri.toString().substring(uri.toString().lastIndexOf("/"));
		return name;
	}
}
