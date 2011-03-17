package eu.planets_project.ifr.core.storage.impl.util;

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
	/**
	 * The name of the planets URI scheme
	 */
	private static final String PLANETS_SCHEME = "planets";

	/**
	 * The path identifier for a Data Registry PLANETS URI
	 */
	private static final String DATA_REG_PART = "dr";

	private URI _uri = null;
	private String _scheme = "";
	private String _host = ""; 
	private int _port = 0;
	private String _registryTag = "";
	private String _registryName = "";
	private String _path;
	private String[] _decodedPath = null; 
    private static Logger _log = Logger.getLogger(PDURI.class.getName());

	/**
	 * 
	 * @param pdURI
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException 
	 */
	public PDURI(URI pdURI) throws URISyntaxException {
		// Check that the URL isn't null
		if (pdURI == null) {
			throw new URISyntaxException("null", "Supplied URI cannot be null");
		}
		// Get the port, host and scheme from the supplied
		this._uri = pdURI.normalize();
		this._port = pdURI.getPort();
		this._host = pdURI.getHost();
		this._scheme = pdURI.getScheme();

		try {
			this._path = PDURI.stripSeparators(pdURI.normalize().getPath());
			
			_log.fine("splitting path :" + this._path);
			String[] pathParts = this._path.split("/");
			this._registryTag = pathParts[0];
			_log.fine("reg tag :" + this._registryTag);
			this._registryName = URLDecoder.decode(pathParts[1], "UTF-8");
			_log.fine("reg name :" + this._registryName);
			_log.fine("path parts");
			this._decodedPath = new String[pathParts.length - 2];
			_log.fine("_decodedPath is an array " + this._decodedPath.length + " long");
			for (int _loop = 0; _loop < this._decodedPath.length; _loop++) {
				_log.fine("Adding path part :" + pathParts[_loop]);
				this._decodedPath[_loop] = URLDecoder.decode(pathParts[_loop + 2], "UTF-8");
			}
		// This means that the path array isn't as large as we're expecting
		// (No dr or id is a real possibility)
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new URISyntaxException(pdURI.toString(), "Hasn't enough path parts to be a PLANETS DOM URI");
		} catch (StringIndexOutOfBoundsException e) {
			throw new URISyntaxException(pdURI.toString(), "Hasn't enough path parts to be a PLANETS DOM URI");
		} catch (UnsupportedEncodingException e) {
			throw new URISyntaxException(pdURI.toString(), "There's an unsupported endcoding error UTF-8");
		}
		_log.fine("Checking is a DR URI");
		if (!this.isDataRegistryURI())
			throw new URISyntaxException(pdURI.toString(), "Invalid PLANETS URI");
	}

	private static String stripSeparators(String path) {
		String strippedPath = path;
		while ((strippedPath.lastIndexOf("/") == (strippedPath.length() - 1))) {
			strippedPath = strippedPath.substring(0, strippedPath.length()-1);
		}
		
		while (strippedPath.indexOf("/") == 0) {
			strippedPath = strippedPath.substring(1, strippedPath.length());
		}
		return strippedPath;
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
		PDURI._log.fine("isPlanetsURI()");
		if ((this._host == null) | (this._scheme == null)) {
			return false;
		}
		PDURI._log.fine("isPlanetsURI() Checks");
		// Now perform the checks and return the result
		return ((this._port > -1) && (this._host.length() > 0) && this._scheme.equals(PLANETS_SCHEME));
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
		PDURI._log.fine("isDataRegistryURI()");
		if (this.isPlanetsURI()) {
			PDURI._log.fine("isDataRegistryURI() checks");
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
		return this._registryName;
	}

	/**
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("deprecation")
	public URI formDataRegistryRootURI() throws URISyntaxException {
		URI _retVal = null;
		String _pathPart = "/" + this._registryTag + "/" + URLEncoder.encode(this._registryName);
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, this._host, this._port, _pathPart, null, null);
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
	@SuppressWarnings("deprecation")
	public URI getURI() throws URISyntaxException {
		_log.fine("PDURI.getURI()");
		URI _retVal = null;
		String _pathPart = "/" + this._registryTag + "/" + URLEncoder.encode(this._registryName);
		_log.fine("PDURI.getURI() _pathPart initialised:" + _pathPart);
		
		for (String _string : this._decodedPath) {
			_log.fine("In concat loop, adding:" + _string);
			_pathPart = _pathPart.concat("/").concat(URLEncoder.encode(_string));
			_log.fine("_pathPart:" + _pathPart);
		}
		_retVal = new URI(PDURI.PLANETS_SCHEME, null, this._host, this._port, _pathPart, null, null);
		_log.fine("New URI is:" + _retVal.toString());
		return _retVal;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws URISyntaxException
	 */
	public void replaceDecodedPath(String path) throws URISyntaxException {
		_log.fine("PDURI.replaceDecodedPath():" + path);
		String replacedPath = path;
		replacedPath = PDURI.stripSeparators(replacedPath);
		_log.fine("PDURI.replaceDecodedPath() striped path:" + replacedPath);
		String[] _parsedPath = null;
		
		if (replacedPath.indexOf("/") >= 0) {
			_parsedPath = replacedPath.split("/");
			_log.fine("separators present array is:");
		}
		else {
			_log.fine("no spearator present");
			_parsedPath = new String[1];
			_parsedPath[0] = replacedPath;
		}
		this._decodedPath = _parsedPath;
		for (String _string : _parsedPath) {
			_log.fine("ArrayItem:" + _string);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDataRegistryPath() {
		String _retVal = "";
		_log.fine("_decodedPath has " + this._decodedPath.length + " elements");
		for (String _string : this._decodedPath) {
			_log.fine("Adding another element :" + _string);
			_retVal = _retVal.concat("/").concat(_string);
		}
		_log.fine("returning :" + _retVal);
		return _retVal;
	}
	
	/**
	 * @return
	 */
	public String[] getPathParts() {
		return this._decodedPath;
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

    /**
     * @return The leafname of this entity. So, for /path/to/object.txt, return object.txt.
     */
    public String getLeafname() {
        String leafname = null;
        leafname = this.getDataRegistryPath();
        // Strip any trailing slash:
        if( leafname.endsWith("/") ) {
            leafname = leafname.substring(0, leafname.length()-1);
        }
        // Strip any upper path info:
        if( leafname.contains("/") ) 
        {
            // Strip any directory path information.
            leafname = leafname.substring(leafname.lastIndexOf("/") + 1);
        }
        return leafname;
    }

	/**
	 * Utility method to re-encode a PDURI that has been converted into a string back into a URI, correcting any encoding issues.
	 * @param pdUri the PDURI, in a String.
	 * @return null if not a valid PDURI.
	 */
    public static URI encodePlanetsUriStringAsUri( String pdUri ) {
        if( pdUri == null || pdUri.length() == 0 ) return null;
        try {
            URI nuri = new URI("planets", pdUri.replaceFirst("planets:", ""), null);
            return nuri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
    	System.out.println("HEY Equals called");
        if (!(obj instanceof PDURI)) {
            return false;
        }
    	System.out.println("Object is a PDURI");
        PDURI other = (PDURI) obj;
    	System.out.println("my uri is:" + this._uri.toString());
    	System.out.println("other uri is:" + other._uri.toString());
    	System.out.println("the answer is " + this._uri.toString().equals(other._uri.toString()));
        return this._uri.toString().equals(other._uri.toString());
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	System.out.println("HEY hashCode called");
    	System.out.println("MY uri is: " + this._uri.toString());
    	System.out.println("MY hash is: " + this._uri.hashCode());
    	return this._uri.toString().hashCode();
    }
}
