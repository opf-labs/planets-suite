package eu.planets_project.ifr.core.storage.impl.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryString;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;

/**
 * Implements the DigitalObjectManager interface for the Yahoo Image API. This
 * DigitalObjectManager is read-only and queryable with a QueryString.
 * 
 * This implementation is lazy-loading:
 * 
 * - When list is first called, a batch of 50 query results (which is the
 *   maximum allowed size for an API call) is retrieved. The total number
 *   of available results (which typically exceeds 50 by far) is extracted
 *   from the XML response.
 *   
 * - All results are buffered in memory
 * 
 * - Additional results are retrieved via HTTP only if needed when the
 *   get() Method of the YahooResultList implementation is called.
 *   
 * - Room for improvement: additional query results are always added to the
 *   in-memory buffer. The buffer is never flushed.
 *   
 * - Room for improvement: results are always loaded subsequently - no gaps
 *   allowed. I.e. if index 10.000 is retrieved, 1-9.999 will be downloaded
 *   before. 
 *
 * @author SimonR
 *
 */
public class YahooImageAPIDigitalObjectManagerImpl implements DigitalObjectManager {
	
    /**
     * Logger.
     */
    private static Log _log = LogFactory.getLog(eu.planets_project.ifr.core.storage.impl.web.SimpleSRUDigitalObjectManagerImpl.class);

	/**
     * API base URL (incl. Yahoo App ID).
     */
    private static String API_BASE_URL = "http://search.yahooapis.com/ImageSearchService/V1/imageSearch";
    private static String Y_APP_ID = "Oc5vBjrV34EdL30ngS_5VnW9PVk0jRSkwyzQO0IDDNXCsBJE4OSq5NE1NF4FToohppPX";
    private static String BASE_URL = API_BASE_URL + "?appid=" + Y_APP_ID + "&";
    
    /**
     * HttpClient timeout in ms.
     */
    private static final int TIMEOUT = 10000;
	
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");		
	}

    public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    	// list() without query not supported - empty result list 
    	return new YahooResultList(null);
    }

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		try {
			// Will simply attempt to download the object at the provided URI,
			// no matter whether it was part of the query result or not
			return new DigitalObject.Builder(Content.byReference(pdURI.toURL())).title(pdURI.getPath().substring( pdURI.getPath().lastIndexOf('/')+1)).build();
		} catch (Exception e) {
			throw new DigitalObjectNotFoundException("Error retrieving object from " + pdURI.toString() + " (" + e.getMessage() + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes(){
		ArrayList<Class<? extends Query>> qTypes = new ArrayList<Class<? extends Query>>();
		qTypes.add(Query.STRING);
		return qTypes;
	}

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
    	if (q == null) {
    		// list() without query not supported - empty result list 
    		throw new QueryValidationException("null query not allowed");
    	}
    	
    	if (pdURI == null) {
    		// Hierarchy is flat (no sub-directories) - only allow 'null' as pdURI!
       		if (q instanceof QueryString) {
       			return new YahooResultList((QueryString) q);
       		} else {
       			throw new QueryValidationException("Unsupported query type");
       		}
    	} else {
    		return new YahooResultList(null);
    	}
	}
	
	/**
	 * Yahoo result list representation.
	 *
	 */
	public class YahooResultList extends AbstractList<URI> {

	    /**
	     * Query string.
	     */
		private String queryString = null;
		
	    /**
	     * The HTTP client.
	     */
	    private HttpClient httpClient = new HttpClient();
	    
	    /**
	     * ArrayList storing all retrieved URIs so far.
	     */
	    private ArrayList<YahooResult> bufferedQueryResults = new ArrayList<YahooResult>();
	    
	    /**
	     * Total number of results.
	     */
	    private int size = 0;
		
		/**
		 * @param query The query
		 */
		public YahooResultList(QueryString query) {
			// Set query string
			if (query != null)
				this.queryString = query.getQuery();
			
	    	// Set up HTTP client
	    	String host = System.getProperty("http.proxyHost");
	        String port = System.getProperty("http.proxyPort");
	        if( host != null && port != null ) {
	            httpClient.getHostConfiguration().setProxy(host, Integer.parseInt(port)); 
	        }
			httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
			httpClient.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, Integer.valueOf(TIMEOUT));
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT);
			
			// Download first 50 results (50 = max. result size for Yahoo API)
			if (queryString != null)
				this.size = nextFiftyResults(0);
		}
		
		/**
		 * {@inheritDoc}
		 * @see java.util.AbstractList#get(int)
		 */
		public URI get(int index) {
			if (index >= bufferedQueryResults.size()) {
				// Not yet in cache - lazy load
				System.out.println("getting index " + index);
				int retrievals = (index - bufferedQueryResults.size()) / 50 + 1;
				System.out.println("lazy loading - # of retrievals: " + retrievals);
				for (int i=0; i<retrievals; i++) {
					System.out.println("downloading results " + bufferedQueryResults.size() + " to " + (bufferedQueryResults.size() + 50));
					nextFiftyResults(bufferedQueryResults.size());
				}
			}

			return bufferedQueryResults.get(index).uri;
		}
		
		/**
		 * {@inheritDoc}
		 * @see java.util.AbstractCollection#size()
		 */
		public int size() {
			return this.size;
		}
		
		private int nextFiftyResults(int offset) {
	    	try {   		
	    		// Fire GET request to Yahoo image search API
	    		String url = BASE_URL + "query=" + URLEncoder.encode(queryString, "UTF-8") + "&results=50&start=" + offset;   		
				GetMethod yahooRequest = new GetMethod(url);
				yahooRequest.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, Integer.valueOf(TIMEOUT));
				httpClient.executeMethod(yahooRequest);
				
				// Create XML DOM
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder docBuilder = factory.newDocumentBuilder();
		        Document dom = docBuilder.parse(yahooRequest.getResponseBodyAsStream());
		        
				// Parse DOM
		        return parseDom(dom);
	    	} catch (UnsupportedEncodingException e) {
	    		_log.error(e.getClass() + ": " + e.getMessage());
	    	} catch (IOException e) {
	    		_log.error(e.getClass() + ": " + e.getMessage());
	    	} catch (SAXException e) {
	    		_log.error(e.getClass() + ": " + e.getMessage());
	    	} catch (ParserConfigurationException e) {
	    		_log.error(e.getClass() + ": " + e.getMessage());
	    	}
	    	return 0;
		}
		
	    private int parseDom(Document dom) {	    	
	    	// Create a node iterator
	        Node root = dom.getDocumentElement();
	     
	        // Parse number of available results
	        int totalResultsAvailable = 0;
	        if (root.getNodeType() == Node.ELEMENT_NODE) {
	        	try {
	        		totalResultsAvailable = Integer.parseInt(((Element) root).getAttribute("totalResultsAvailable"));
	        	} catch (Exception e) {
	        		_log.error("Number of total results available not found");
	        	}
	        }
	        
	        if (totalResultsAvailable == 0)
	        	return 0;
	        
	        NodeList results = root.getChildNodes();
	        
	        // Walk the DOM for all <result> elements
	        Element aResult;
	        NodeList resultFragment;
	        Element aResultFragmentChild; 

	        for (int i=0; i<results.getLength(); i++) {
	        	if (results.item(i).getNodeType() == Node.ELEMENT_NODE) {
	        		aResult = (Element) results.item(i);
	        		if (aResult.getNodeName().equalsIgnoreCase("result")) {
	        			// Node is a <result> element node
	        			YahooResult result = new YahooResult();
	        			
	        			resultFragment = aResult.getChildNodes();
	        			for (int j=0; j<resultFragment.getLength(); j++) {
	        				if (resultFragment.item(j).getNodeType() == Node.ELEMENT_NODE) {
	        					aResultFragmentChild = (Element) resultFragment.item(j);
	        					
	        					// URL
	        					if (aResultFragmentChild.getNodeName().equalsIgnoreCase("url")) {
	        						try {
	        							result.uri = new URI(aResultFragmentChild.getFirstChild().getNodeValue().trim());
	        						} catch (URISyntaxException e) {
	        							// Do nothing
	        						}
	        						
	        					// Title
	        					} else if (aResultFragmentChild.getNodeName().equalsIgnoreCase("title")) {
	        						result.title = aResultFragmentChild.getFirstChild().getNodeValue().trim();
	        						
	        					// Filesize
	        					} else if (aResultFragmentChild.getNodeName().equalsIgnoreCase("filesize")) {
	        						try {
	        							double size = Integer.parseInt(aResultFragmentChild.getFirstChild().getNodeValue().trim());
	        							size = (int) (size / 10.24);
	        							result.fileSize = (int) (size /= 100);
	        						} catch (Exception e) {
	        							// Do nothing
	        						}
	        						
	        					// Fileformat
	        					} else if (aResultFragmentChild.getNodeName().equalsIgnoreCase("fileformat")) {
	        						result.format = aResultFragmentChild.getFirstChild().getNodeValue().trim();
	        					}
	        				}
	        			}
	        			
	        			if ((result.uri != null) && (result.title != null))
	        				bufferedQueryResults.add(result);

	        		}
	        	}
	        }
			
	    	return totalResultsAvailable;
	    }
		
	}
	
	// A simple class to wrap a query result URL 
	// with a little bit of metadata. (Might be
	// extended in thefuture?)
	private class YahooResult {
		URI uri = null;       // URI
		String title = null;  // Title
		int fileSize;         // File size in kByte
		String format = null; // File format by extension
	}
	
	/*
	public static void main(String args[]) {
		System.out.println("starting...");
		YahooImageAPIDigitalObjectManagerImpl impl = new YahooImageAPIDigitalObjectManagerImpl();
		impl.setQuery(new QueryString("planets"));
		List<URI> results = impl.list(null);
		System.out.println(results.size() + " results");

		List<URI> sublist = results.subList(298, 310);
		for (URI uri : sublist)
			System.out.println(uri);
		
		System.out.println("done.");
	}
	*/

}
