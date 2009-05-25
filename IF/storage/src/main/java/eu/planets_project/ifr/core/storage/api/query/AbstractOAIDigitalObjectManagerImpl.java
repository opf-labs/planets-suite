package eu.planets_project.ifr.core.storage.api.query;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

import eu.planets_project.services.datatypes.DigitalObject;

public abstract class AbstractOAIDigitalObjectManagerImpl implements DigitalObjectManager {
	
	/**
	 * Logger
	 */
    protected static PlanetsLogger log = PlanetsLogger.getLogger(AbstractOAIDigitalObjectManagerImpl.class);
	
    /**
     * OAI-style date format
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
     * OAI endpoint base URL
     */
    protected String baseURL;
    
    /**
     * OAI metadata prefix
     */
    protected String metaDataPrefix;
    
    /**
     * OAI set (may be null)
     */
    protected String set = null;
	
    public AbstractOAIDigitalObjectManagerImpl(String baseURL, String metaDataPrefix) {
    	this.baseURL = baseURL;
    	this.metaDataPrefix = metaDataPrefix;
    }
    
    public AbstractOAIDigitalObjectManagerImpl(String baseURL, String metaDataPrefix, String set) {
    	this.baseURL = baseURL;
    	this.metaDataPrefix = metaDataPrefix;
    	this.set = set;
    }
	
    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }
    
    public boolean isWritable(URI pdURI) {
        return false;
    }
    
    public List<URI> list(URI pdURI) {
        // Perform OAI-PMH request without time range ('from' and 'until' are optional in OAI-PMH!)
        try {
            return list(pdURI, null);
        } catch (QueryValidationException e) {
            // Since query is null, this can never happen
            return new ArrayList<URI>();
        }
    }

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
    
	public List<Class<? extends Query>> getQueryTypes() {
		ArrayList<Class<? extends Query>> qTypes = new ArrayList<Class<? extends Query>>();
		qTypes.add(Query.DATE_RANGE);
		return qTypes;
	}

}
