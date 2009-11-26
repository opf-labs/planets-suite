package eu.planets_project.ifr.core.storage.impl.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryString;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;

/**
 * 
 * SRU is a standard XML-focused search protocol for Internet search queries,
 * utilizing CQL (Contextual Query Language), a standard syntax for representing
 * queries.
 * 
 * @see http://www.loc.gov/standards/sru/
 * 
 * 
 */
public class SimpleSRUDigitalObjectManagerImpl implements DigitalObjectManager {
	
    /**
     * Logger.
     */
    private static Log _log = LogFactory.getLog(SimpleSRUDigitalObjectManagerImpl.class);
    
	/**
     * SRU endpoint base URL.
     */
    private String baseURL;
    
    /**
     * HttpClient timeout in ms.
     */
    private static final int TIMEOUT = 10000;
    
    /**
     * The HTTP client.
     */
    private HttpClient httpClient = new HttpClient();

    /**
     * @param baseURL The base URL
     */
    public SimpleSRUDigitalObjectManagerImpl(String baseURL) {
    	this.baseURL = baseURL;
    	
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
    	return new ArrayList<URI>();
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
	public List<Class<? extends Query>> getQueryTypes() {
		ArrayList<Class<? extends Query>> qTypes = new ArrayList<Class<? extends Query>>();
		qTypes.add(Query.STRING);
		return qTypes;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
	 */
	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
    	if (q == null) 
    		throw new QueryValidationException("null query not allowed");
    	
    	if (!(q instanceof QueryString))
    		throw new QueryValidationException("Unsupported query type");
		
    	if (pdURI == null) {
    		// SRU hierarchy is flat (no sub-directories) - only allow 'null' as pdURI!
    		ArrayList<URI> resultList = new ArrayList<URI>();
    		
    		try {
				String url = baseURL + "&query=" + URLEncoder.encode(((QueryString) q).getQuery(), "UTF-8") +
				 			 "&maximumRecords=" + "10" /* limit!!! */ + "&recordSchema=dc";
				
				GetMethod sruRequest = new GetMethod(url);
				sruRequest.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, Integer.valueOf(TIMEOUT));
				httpClient.executeMethod(sruRequest);
	    		
				// Parse metadata records
				SAXBuilder builder = new SAXBuilder();
				List<SRUResult> results = parseDom(builder.build(sruRequest.getResponseBodyAsStream()));
				
				// Extract URIs from metadata and return
				for (SRUResult aResult : results) {
					resultList.add(aResult.uri);
				}
    		} catch (UnsupportedEncodingException e) {
    			_log.error(e.getMessage() + " (this should never happen");
    		} catch (IOException e) {
    			throw new QueryValidationException("Error connecting to SRU endpoint: " + e.getMessage());
    		} catch (JDOMException e) {
    			throw new QueryValidationException("Invalid SRU respons: " + e.getMessage());
    		}
			
    		return resultList;
    	} else {
    		return new ArrayList<URI>();
    	}
	}
	
    private ArrayList<SRUResult> parseDom(Document dom) {
    	ArrayList<SRUResult> results = new ArrayList<SRUResult>();
    	
    	try {
    		XPath x = XPath.newInstance("/zs:searchRetrieveResponse//zs:records//zs:record//zs:recordData//srw_dc:dc");
    		x.addNamespace(Namespace.getNamespace("srw_dc", "info:srw/schema/1/dc-schema"));
    		Namespace dc = Namespace.getNamespace("http://purl.org/dc/elements/1.1/");
     		
    		for (Object element : x.selectNodes(dom)) {
    			SRUResult aResult = new SRUResult();
    			
    			// Title
    			aResult.title = ((Element) element).getChildText("title", dc);
    			
    			// URI
    			try {
    				aResult.uri = new URI("http://");
    			} catch (URISyntaxException e) {
    				// Do nothing
    			}
    			
    			if ((aResult.title != null) && (aResult.uri != null))
    				results.add(aResult);
    		}
    	} catch (JDOMException e) {
    		_log.error(e.getMessage());
    	}

    	return results;
    }
	
	private class SRUResult {
		String title = null;
		URI uri = null;
	}
    
	/*
	public static void main(String[] args) {
		System.out.println("starting query.");
		GenericSRUDigitalObjectManagerImpl sruImpl = new GenericSRUDigitalObjectManagerImpl("http://z3950.loc.gov:7090/voyager?version=1.1&operation=searchRetrieve");
		try {
			sruImpl.list(null, new QueryString("dinosaurs"));
		} catch (QueryValidationException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("done.");
	}
	*/
	
}
