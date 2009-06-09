package eu.planets_project.ifr.core.storage.impl.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.PDURI;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Implements the DigitalObjectManager interface for the NASA Blue Marble
 * image collection. This DigitalObjectManager is read-only and not
 * queryable.
 * 
 * A word of warning: the Blue Marble collection does not provide a well-defined
 * interface. It is only available through a Web portal and various Web mirrors.
 * This interface class therefore scrapes the HTML code from the mirror site at
 * the Arctic Region Supercomputing Center (http://mirrors.arsc.edu/nasa/). Any change
 * in the HTML at the mirror site is likely to cause this interface class to break!
 * 
 * The directory structure of the Blue Marble collection is as follows:
 * 
 * - root - world_2km
 *        - world_500m
 *        - world_8km
 * 
 * This directory structure (including the contents of the world_XXXm folders)
 * is retrieved once and then buffered in memory to minimize HTTP accesses.
 * 
 * This implementation does not perform lazy loading.
 * 
 * @author SimonR
 * 
 */
public class BlueMarbleDigitalObjectManagerImpl implements DigitalObjectManager {
	
    /**
     * Logger.
     */
    private static Log _log = LogFactory.getLog(BlueMarbleDigitalObjectManagerImpl.class);
	
    /**
     * HttpClient timeout in ms.
     */
    private static final int TIMEOUT = 10000;

    /**
     * Mirror site base URL.
     */   
    private static final String MIRROR_BASE_URL = "http://mirrors.arsc.edu/nasa/";
    
    /**
     * Mirror site base URI.
     */
    private URI rootURI = null; 
    
    /**
     * The HTTP client.
     */
    private HttpClient httpClient = new HttpClient();
    
    /**
     * Local buffer for the remote directory structure to increase responsiveness of the GUI.
     */
    private HashMap<URI, List<URI>> directoryCache = new HashMap<URI, List<URI>>();
	
    /**
     * Create a blue marble digital object manager.
     */
    public BlueMarbleDigitalObjectManagerImpl() {
    	try {
    		this.rootURI = new URI(MIRROR_BASE_URL);
    	} catch (URISyntaxException e) {
    		_log.error("This should never happen: " + e.getMessage());
    	}
    	
    	// Set up HTTP client
    	String host = System.getProperty("http.proxyHost");
        String port = System.getProperty("http.proxyPort");
        if( host != null && port != null ) {
            httpClient.getHostConfiguration().setProxy(host, Integer.parseInt(port)); 
        }
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
		httpClient.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(TIMEOUT));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT);
    }
	
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");
	}

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable( URI pdURI ) {
    	return false;
    }
    
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {    	
    	// 'null' - return mirror base URL
    	if (pdURI == null) {
        	ArrayList<URI> children = new ArrayList<URI>();
    		children.add(rootURI);
			return children;
    	}
    	
    	// Otherwise: Try the dir cache before downloading from remote mirror
    	List<URI> bufferedList = directoryCache.get(pdURI);
    
    	if (bufferedList == null) {
    		// Not in cache - remote download (and cache result if any)
    		if (pdURI.equals(rootURI)) {
       	    	try {
       				// Top level directory
       				GetMethod dirRequest = new GetMethod(MIRROR_BASE_URL);
    				dirRequest.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(TIMEOUT));
       				httpClient.executeMethod(dirRequest);
       				
       				// Scrape directory names
       				List<URI> topLvlDirs = scrapeTopLvlDirs(dirRequest.getResponseBodyAsString());
       				directoryCache.put(pdURI, topLvlDirs);
       				return topLvlDirs;
       	    	} catch (IOException e) {
       	    		_log.error("Error scraping top-level directory from Blue Marble mirror");
       	    		return new ArrayList<URI>();
       	    	}
       		} else if (!(pdURI.toString().endsWith("png") || pdURI.toString().endsWith("jpg"))) {
       			try {
       	   			// Sub-directory    				
    				GetMethod dirRequest = new GetMethod(pdURI.toString());
    				dirRequest.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(TIMEOUT));
    				httpClient.executeMethod(dirRequest);
    				
    				// Scrape file names
    				List<URI> subDirs = scrapeSubDir(dirRequest.getResponseBodyAsString(), pdURI.toString());
    				directoryCache.put(pdURI, subDirs);
    				return subDirs;
       			} catch (IOException e) {
       				_log.error("Error scraping subdirectory '" + pdURI + "' from Blue Marble mirror");
       				return new ArrayList<URI>();
       			}
       		} else {
       			// Leaf node
       			return null;
       		}
    	} else {
    		return bufferedList;
    	}
    }

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		try {
			// Will simply attempt to download the object at the provided URI,
			// no matter whether located at the NASA mirror or not
            DigitalObject.Builder dob = new DigitalObject.Builder(Content.byReference(pdURI.toURL()));
            dob.title( PDURI.extractLeafname( pdURI.getPath() ) );
            return dob.build();
		} catch (Exception e) {
			throw new DigitalObjectNotFoundException("Error retrieving object from " + pdURI.toString() + " (" + e.getMessage() + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		return null;
	}

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        throw new QueryValidationException("This implementation does not support queries.");
    }
	
	private List<URI> scrapeTopLvlDirs(String html) {
		Pattern worldDirPattern = Pattern.compile("<a href=\"world_((.|\n)*?)</a>");
		Matcher m = worldDirPattern.matcher(html);
		
		ArrayList<URI> dirs = new ArrayList<URI>();
		String match;
		while (m.find()) {
			m.find(); // Skip every odd match
			match = m.group(1);
			try {
				if (match.indexOf("_big") < 0) {
					dirs.add(new URI(MIRROR_BASE_URL + match.substring(match.indexOf('>') + 1)));
				}
			} catch (URISyntaxException e) {
				_log.error("Error parsing Blue Marble dir: " + match);
			}
		}
		return dirs;
	}
	
	private List<URI> scrapeSubDir(String html, String baseURL) {
		Pattern worldDirPattern = Pattern.compile(" <a href=\"world((.|\n)*?)\">");
		Matcher m = worldDirPattern.matcher(html);
		ArrayList<URI> images = new ArrayList<URI>();

		String match;
		while (m.find()) {
			match = "world" + m.group(1);
			if (match.endsWith("jpg") || match.endsWith("png") ) {
				try {
					images.add(new URI(baseURL + match));
				} catch (URISyntaxException e) {
					_log.error("Error parsing Blue Marble image dir: " + match);
				}
			}
		}
		return images;
	}

}
