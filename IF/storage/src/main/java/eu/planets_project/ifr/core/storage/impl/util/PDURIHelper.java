package eu.planets_project.ifr.core.storage.impl.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

/**
 * Helper class to validate and parse PLANETS Data URIs.
 * <p>
 * The Data Registry design described a specification for PLANETS Data URIs.  This
 * class is the first implementation of a syntax parser / validator against the specification.
 *  
 * @author CFwilson
 *
 */
public final class PDURIHelper {

	/**
	 * The name of the planets URI scheme
	 */
	private static final String PLANETS_SCHEME = "planets";

	/**
	 * The path identifier for a Data Registry PLANETS URI
	 */
	private static final String DATA_REG_PART = "dr";
	
	/**
	 * Private constructor to prevent instantiation
	 */
	private PDURIHelper() {
		/**
		 * Empty block, need private no arg costructor
		 */
	}
	
	private static String stripTrailingSeparators(String path) {
		String strippedPath = path;
		while ((strippedPath.lastIndexOf("/") == (strippedPath.length() - 1))) {
			strippedPath = strippedPath.substring(0, strippedPath.length()-1);
		}
		return strippedPath;
	}
	/**
	 * Checks a URI against the PLANETS URI syntax rules.
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i></code>
	 * 
	 * @param pdURI - A URI to be checked for conformance to PLANETS URI scheme
	 * @return true if the parameter is a well formed PLANETS URI
	 */
	public static boolean isPlanetsURI(URI pdURI) {
		// Get the port, host and scheme from the supplied 
		int _port = pdURI.getPort();
		String _host = pdURI.getHost();
		String _scheme = pdURI.getScheme();
		
		// Check for nulls
		if ((_host == null) | (_scheme == null)) {
			return false;
		}
		
		// Now perform the checks and return the result
		return ((_port > -1) && (_host.length() > 0) && _scheme.equals(PLANETS_SCHEME));
	}
	
	/**
	 * Checks to see if a URI is a PLANETS Data Registry URI
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i>/dr/</code>
	 * 
	 * @param pdURI - A URI to be checked for conformance as a PLANETS data URI
	 * @return true if the URI is a PLANETS data URI
	 */
	public static boolean isDataRegistryURI(URI pdURI) {
		boolean _retVal = false;
		if (PDURIHelper.isPlanetsURI(pdURI)) {
			// Check the first path component is a data registry identifier
			String _path = pdURI.normalize().getPath(); 
			if (_path.length() > 1) {
				_retVal = pdURI.normalize().getPath().substring(1).split("/")[0].toLowerCase().equals(DATA_REG_PART);
			}
		}
		return _retVal;
	}
	
	/**
	 * For a given PLANETS URI returns the serer identifier.
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i></code>
	 * 
	 * @param pdURI - A URI from which to parse the PLANETS server name
	 * @return the ID of the server as a String
	 */
	public static String getPlanetsServerIdentifier(URI pdURI) {
		String _serverID = null;
		if (PDURIHelper.isPlanetsURI(pdURI)) {
			_serverID = pdURI.getScheme() + "://" + pdURI.getAuthority();
		}
		return _serverID;
	}
	
	/**
	 * For a given PLANETS Data Registry URI returns the data registry identifier.
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i>/dr/<i>data registry identifier</i></code>
	 * 
	 * @param pdURI - A URI from which to parse the data registry identifier
	 * @return A data registry identifier or <code>null</code> if the URI is not a genuine PLANETS URI
	 */
	public static String getDataRegistryIdentifier(URI pdURI) {
		String _dataRegistryID = null;
		
		// If a data registry URI then split of the path part
		if (PDURIHelper.isDataRegistryURI(pdURI)) {
			_dataRegistryID = pdURI.normalize().getPath().substring(1).split("/")[1];
		}
		return _dataRegistryID;
	}
	
	/**
	 * @param pdURI - A URI from which to parse the PLANETS data registry path
	 * @return The data registry path or <code>null</code> if one cannot be found.
	 * @throws URISyntaxException 
	 */
	public static String getDataRegistryPath(URI pdURI) throws URISyntaxException {
		String _dataRegistryPath = null;
		
		if (PDURIHelper.isDataRegistryURI(pdURI)) {
			String[] _pathArray = PDURIHelper.getDecodedPathParts(pdURI);
			_dataRegistryPath = new String("");
			for (int _loop = 3; _loop < _pathArray.length; _loop++) {
				_dataRegistryPath = _dataRegistryPath.concat("/").concat(_pathArray[_loop]);
			}
		}
		else
			throw new URISyntaxException(pdURI.toASCIIString(), "Invalid PLANETS Data Registry URI");
		return _dataRegistryPath;
	}
	
	/**
	 * Method that takes a PLANETS Data Registry URI and replaces the path with
	 * another.
	 * 
	 * @param pdURI - A URI in which to replace the path
	 * @param path - Replacement path for the URI
	 * @return The URI with path replaced
	 * @throws URISyntaxException 
	 */
	public static URI replaceDataRegistryPath(URI pdURI, String path) throws URISyntaxException {
		URI _retVal = null;
		if (PDURIHelper.isDataRegistryURI(pdURI)) {
			String _pdURIString = PDURIHelper.stripTrailingSeparators(pdURI.toString());
			if (PDURIHelper.getDataRegistryPath(pdURI).length() < 1) {
				_pdURIString = _pdURIString.concat(path);
			} else {
				_pdURIString = _pdURIString.replace(PDURIHelper.getDataRegistryPath(pdURI), path);
			}
			_retVal = new URI(_pdURIString);
		}
		return _retVal;
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param registryName
	 * @return
	 * @throws URISyntaxException
	 */
	public static URI formDataRegistryRootURI(String host, String port, String registryName) throws URISyntaxException {
		URI _retVal = null;
		String _path = "/" + PDURIHelper.DATA_REG_PART + "/" + registryName;
		_retVal = new URI(PDURIHelper.PLANETS_SCHEME, null, host, Integer.parseInt(port.trim()), _path, null, null);
		return _retVal;
	}
	
	/**
	 * 
	 * @param pdURI
	 * @return
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("deprecation")
	public static String[] getDecodedPathParts(URI pdURI) throws URISyntaxException {
		String[] _retVal = null;
		if (PDURIHelper.isDataRegistryURI(pdURI)) {
			_retVal = pdURI.normalize().getPath().split("/");
			for (int _loop = 0; _loop < _retVal.length; _loop++) {
				_retVal[_loop] = URLDecoder.decode(_retVal[_loop]);
			}
		}
		else
			throw new URISyntaxException(pdURI.toASCIIString(), "Invalid PLANETS Data Registry URI");
		return _retVal;
	}
}
