package eu.planets_project.ifr.core.storage.impl.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.storage.impl.DataManager;

/**
 * 
 * @author CFwilson
 *
 */
public class PDURI {
	/**
	 * The name of the planets URI scheme
	 */
	private static final String PLANETS_SCHEME = "planets";

	/**
	 * The path identifier for a Data Registry PLANETS URI
	 */
	private static final String DATA_REG_PART = "dr";
	
	private String _scheme = "";
	private String _host = ""; 
	private int _port = 0;
	private String _registryTag = "";
	private String _registryName = "";
	private String _path;
	private String[] _decodedPath = null; 
    private static Logger log = Logger.getLogger(DataManager.class.getName());

	/**
	 * 
	 * @param pdURI
	 * @throws URISyntaxException
	 */
	public PDURI(URI pdURI) throws URISyntaxException{
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
		_registryName = URLDecoder.decode(pathParts[1]);
		log.fine("reg name :" + _registryName);
		log.fine("path parts");
		_decodedPath = new String[pathParts.length - 2];
		log.fine("_decodedPath is an array " + _decodedPath.length + " long");
		for (int _loop = 0; _loop < _decodedPath.length; _loop++) {
			log.fine("Adding path part :" + pathParts[_loop]);
			_decodedPath[_loop] = URLDecoder.decode(pathParts[_loop + 2]);
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
		this.log.fine("isPlanetsURI()");
		if ((_host == null) | (_scheme == null)) {
			return false;
		}
		this.log.fine("isPlanetsURI() Checks");
		// Now perform the checks and return the result
		return ((_port > -1) && (_host.length() > 0) && _scheme.equals(PLANETS_SCHEME));
	}
	
	/**
	 * Checks to see if a URI is a PLANETS Data Registry URI
	 * The URI argument must be of the following form:
	 * <p/>
	 * <code>planets://<i>server:port</i>/dr/</code>
	 * 
	 * @return true if the URI is a PLANETS data URI
	 */
	public boolean isDataRegistryURI() {
		boolean _retVal = false;
		this.log.fine("isDataRegistryURI()");
		if (this.isPlanetsURI()) {
			this.log.fine("isDataRegistryURI() checks");
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
	 * @throws URISyntaxException
	 */
	public URI formDataRegistryRootURI() throws URISyntaxException {
		URI _retVal = null;
		String _pathPart = "/" + _registryTag + "/" + URLEncoder.encode(_registryName);
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, _host, _port, _pathPart, null, null);
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
		String _rootURIPath = "/" + PDURI.DATA_REG_PART + "/" + registryName;
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, host, Integer.parseInt(port.trim()), _rootURIPath, null, null);
		return _retVal;
	}

	/**
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	public URI getURI() throws URISyntaxException {
		log.fine("PDURI.getURI()");
		URI _retVal = null;
		String _pathPart = "/" + _registryTag + "/" + URLEncoder.encode(_registryName);
		log.fine("PDURI.getURI() _pathPart initialised:" + _pathPart);
		
		for (String _string : this._decodedPath) {
			log.fine("In concat loop, adding:" + _string);
			_pathPart = _pathPart.concat("/").concat(URLEncoder.encode(_string));
			log.fine("_pathPart:" + _pathPart);
		}
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, _host, _port, _pathPart, null, null);
		log.fine("New URI is:" + _retVal.toString());
		return _retVal;
	}
	
	/**
	 * 
	 * @param path
	 * @return
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
}
