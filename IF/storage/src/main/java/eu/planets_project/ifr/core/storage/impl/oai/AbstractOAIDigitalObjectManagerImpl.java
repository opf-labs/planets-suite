package eu.planets_project.ifr.core.storage.impl.oai;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.Log;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;

import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Abstract superclass for OAI digital object managers.
 */
public abstract class AbstractOAIDigitalObjectManagerImpl implements DigitalObjectManager {
	
    protected static Log log = LogFactory.getLog(AbstractOAIDigitalObjectManagerImpl.class);
	
    /**
     * OAI-style date format.
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
     * OAI endpoint base URL.
     */
    protected String baseURL;
    
    /**
     * OAI metadata prefix.
     */
    protected String metaDataPrefix;
    
    /**
     * OAI set (may be null).
     */
    protected String set = null;
	
    /**
     * @param baseURL The base URL
     * @param metaDataPrefix The meta data prefix
     */
    public AbstractOAIDigitalObjectManagerImpl(String baseURL, String metaDataPrefix) {
    	this.baseURL = baseURL;
    	this.metaDataPrefix = metaDataPrefix;
    }
    
    /**
     * @param baseURL The base URL
     * @param metaDataPrefix The meta data prefix
     * @param set The set
     */
    public AbstractOAIDigitalObjectManagerImpl(String baseURL, String metaDataPrefix, String set) {
    	this.baseURL = baseURL;
    	this.metaDataPrefix = metaDataPrefix;
    	this.set = set;
    }
	
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }
    
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable(URI pdURI) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {
        // Perform OAI-PMH request without time range ('from' and 'until' are optional in OAI-PMH!)
        try {
            return list(pdURI, null);
        } catch (QueryValidationException e) {
            // Since query is null, this can never happen
            return new ArrayList<URI>();
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
    	if (pdURI == null) {
    		// OAI hierarchy is flat (no sub-directories) - only allow 'null' as pdURI!
	    	ArrayList<URI> resultList = new ArrayList<URI>();
	    	
	    	OaiPmhServer server = new OaiPmhServer(baseURL);
	    	try {
	    		IdentifiersList list;
	    		if (q == null) {
		    		list = server.listIdentifiers(metaDataPrefix);	    			
	    		} else {
	    			if (!(q instanceof QueryDateRange))
	    				throw new QueryValidationException("Unsupported query type");
	    			
	    			list = server.listIdentifiers(metaDataPrefix, dateFormat.format(((QueryDateRange) q).getStartDate().getTime()), dateFormat.format(((QueryDateRange) q).getEndDate().getTime()), set);	
	    		}
	    		
	    		for (Header header : list.asList()) {
	    			try {
	    				resultList.add(new URI(header.getIdentifier()));
	    			} catch (URISyntaxException e) {
	    				log.warn("Illegal identifier returned from " + baseURL + ": " + header.getIdentifier());
	    			}
	    		}
	    	} catch (OAIException e) {
	    		log.error(e.getMessage());
	    	}
	        return resultList;
    	} else {
    		return new ArrayList<URI>();
    	}
	}
    
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		ArrayList<Class<? extends Query>> qTypes = new ArrayList<Class<? extends Query>>();
		qTypes.add(Query.DATE_RANGE);
		return qTypes;
	}

}
