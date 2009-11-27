package eu.planets_project.ifr.core.storage.api;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;

/**
 * 
 * @author CFwilson
 *
 */
public class PDURI {
	/** The logger instance */
    private static Logger log = Logger.getLogger(PDURI.class.getName());
	/** The name of the planets URI scheme */
	private static final String PLANETS_SCHEME = "planets";
	/** The path identifier for a Data Registry PLANETS URI */
	private static final String DATA_REG_PART = "dr";
	
	private String _scheme = "";
	private String _host = ""; 
	private int _port = 0;
	private String _registryTag = "";
	private String _registryName = "";
	private String _path;
	private String[] _decodedPath = null; 

	/**
	 * 
	 * @param pdURI the URI
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException 
	 */
	public PDURI(URI pdURI) throws URISyntaxException, UnsupportedEncodingException{
		// Get the port, host and scheme from the supplied
		log.fine("parsing URI");
		_port = pdURI.getPort();
		 _host = pdURI.getHost();
		_scheme = pdURI.getScheme();
		_path = PDURI.stripSeparators(pdURI.normalize().getPath());
		
		log.fine("splitting path :" + _path);
		String[] pathParts = _path.split("/");
		for (String _string : pathParts) {
			log.fine("pathpart :" + _string);
		}
		_registryTag = pathParts[0];
		log.fine("reg tag :" + _registryTag);
		_registryName = URLDecoder.decode(pathParts[1], "UTF-8");
		log.fine("reg name :" + _registryName);
		log.fine("path parts");
		_decodedPath = new String[pathParts.length - 2];
		log.fine("_decodedPath is an array " + _decodedPath.length + " long");
		for (int _loop = 0; _loop < _decodedPath.length; _loop++) {
			log.fine("Adding path part :" + pathParts[_loop]);
			_decodedPath[_loop] = URLDecoder.decode(pathParts[_loop + 2],  "UTF-8");
		}
		log.fine("Checking is a DR URI");
		if (!this.isDataRegistryURI())
			throw new URISyntaxException(pdURI.toString(), "Invalid PLANETS URI");

	}

	private static String stripSeparators(String path) {
		while ((path.lastIndexOf("/") == (path.length() - 1))) {
			path = path.substring(0, path.length()-1);
		}
		
		while (path.indexOf("/") == 0) {
			path = path.substring(1, path.length());
		}
		return path;
	}

	/**
	 * Checks a URI against the PLANETS URI syntax rules.
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i></code>
	 * 
	 * @return true if the parameter is a well formed PLANETS URI
	 */
	public boolean isPlanetsURI() {
		// Check for nulls
		log.fine("isPlanetsURI()");
		if ((_host == null) | (_scheme == null)) {
			return false;
		}
		log.fine("isPlanetsURI() Checks");
		// Now perform the checks and return the result
		return ((_port > -1) && (_host.length() > 0) && _scheme.equals(PLANETS_SCHEME));
	}
	
	/**
	 * Checks to see if a URI is a PLANETS Data Registry URI
	 * The URI argument must be of the following form.
	 * <p/>
	 * <code>planets://<i>server:port</i>/dr/</code>
	 * 
	 * @return
	 * 		true if the URI is a PLANETS data URI, false otherwise
	 */
	public boolean isDataRegistryURI() {
		boolean _retVal = false;
		log.fine("isDataRegistryURI()");
		if (this.isPlanetsURI()) {
			log.fine("isDataRegistryURI() checks");
			_retVal = (this._registryTag.toLowerCase().equals(DATA_REG_PART));
		}
		return _retVal;
	}
	
	/**
	 * For a given PLANETS Data Registry URI returns the data registry identifier.
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i>/dr/<i>data registry identifier</i></code>
	 * 
	 * @return A data registry identifier or <code>null</code> if the URI is not a genuine PLANETS URI
	 */
	public String getDataRegistryIdentifier() {
		return _registryName;
	}

	/**
	 * 
	 * @return
	 * 		A Planets Data Registry format URI
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException 
	 */
	public URI formDataRegistryRootURI() throws URISyntaxException, UnsupportedEncodingException {
		URI _retVal = null;
		String _pathPart = "/" + _registryTag + "/" + URLEncoder.encode(_registryName, "UTF-8");
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, _host, _port, _pathPart, null, null);
		return _retVal;
	}

	/**
	 * 
	 * @param host the host
	 * @param port the port
	 * @param registryName the registry name
	 * @return
	 * 		A Planets Data Registry format URI
	 * @throws URISyntaxException
	 */
	public static URI formDataRegistryRootURI(String host, String port, String registryName) throws URISyntaxException {
		URI _retVal = null;
		String _rootURIPath = "/" + PDURI.DATA_REG_PART + "/" + registryName;
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, host, Integer.parseInt(port.trim()), _rootURIPath, null, null);
		return _retVal;
	}

	/**
	 * 
	 * @return
	 * 		A Planets Data Registry format URI
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException 
	 */
	public URI getURI() throws URISyntaxException, UnsupportedEncodingException {
		log.fine("PDURI.getURI()");
		URI _retVal = null;
		String _pathPart = "/" + _registryTag + "/" + URLEncoder.encode(_registryName,  "UTF-8");
		log.fine("PDURI.getURI() _pathPart initialised:" + _pathPart);
		
		for (String _string : this._decodedPath) {
			log.fine("In concat loop, adding:" + _string);
			_pathPart = _pathPart.concat("/").concat(URLEncoder.encode(_string,  "UTF-8"));
			log.fine("_pathPart:" + _pathPart);
		}
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, _host, _port, _pathPart, null, null);
		log.fine("New URI is:" + _retVal.toString());
		return _retVal;
	}
	
	/**
	 * 
	 * @param path the path
	 * @throws URISyntaxException
	 */
	public void replaceDecodedPath(String path) throws URISyntaxException {
		log.fine("PDURI.replaceDecodedPath():" + path);
		path = PDURI.stripSeparators(path);
		log.fine("PDURI.replaceDecodedPath() striped path:" + path);
		String[] _parsedPath = null;
		
		if (path.indexOf("/") >= 0) {
			_parsedPath = path.split("/");
			log.fine("separators present array is:");
		}
		else {
			log.fine("no spearator present");
			_parsedPath = new String[1];
			_parsedPath[0] = path;
		}
		this._decodedPath = _parsedPath;
		for (String _string : _parsedPath) {
			log.fine("ArrayItem:" + _string);
		}
	}
	
	/**
	 * 
	 * @return
	 * 		The path part of the Data Registry URI as a java.lang.String
	 */
	public String getDataRegistryPath() {
		String _retVal = "";
		log.fine("_decodedPath has " + _decodedPath.length + " elements");
		for (String _string : this._decodedPath) {
			log.fine("Adding another element :" + _string);
			_retVal = _retVal.concat("/").concat(_string);
		}
		log.fine("returning :" + _retVal);
		return _retVal;
	}

	/**
	 * @param fullpath
	 * @return
	 */
	public static String extractLeafname( String fullpath ) {
	    if( fullpath == null ) return null;
        int lastSlash = fullpath.lastIndexOf("/");
        if( lastSlash != -1 ) {
            return fullpath.substring( lastSlash + 1, fullpath.length() );
        }
        return fullpath;
	}
	
}
