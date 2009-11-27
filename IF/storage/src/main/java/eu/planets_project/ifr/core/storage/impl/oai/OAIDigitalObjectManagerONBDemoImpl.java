package eu.planets_project.ifr.core.storage.impl.oai;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.RecordsList;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * ONB implementation of the OAI digital object manager.
 *
 */
public class OAIDigitalObjectManagerONBDemoImpl extends AbstractOAIDigitalObjectManagerImpl {
	
	private URI root = null;
	
	/**
	 * Create ONB connector with default settings
	 */
	public OAIDigitalObjectManagerONBDemoImpl() {
		super("http://archiv-test.onb.ac.at:8881/OAI-PUB", "de2aleph", "dtl2aleph");
		try {
			this.root = new URI("http://archiv-test.onb.ac.at:8881/ONB-OAI");
		} catch (URISyntaxException e) {
			// Can never happen
		}
	}
	
	/**
	 * Create ONB connector with alternative endpoint
	 * (useful for testing via SSH tunnel and localhost address) 
	 * @param endpoint
	 */
	public OAIDigitalObjectManagerONBDemoImpl(String endpoint) {
		super(endpoint, "de2aleph", "dtl2aleph");
		try {
			this.root = new URI("http://archiv-test.onb.ac.at:8881/ONB-OAI");
		} catch (URISyntaxException e) {
			// Can never happen
		}
	}
	
	@Override
	public List<URI> list(URI pdURI) {
        // Perform OAI-PMH request without time range ('from' and 'until' are optional in OAI-PMH!)
        try {
            return list(pdURI, null);
        } catch (QueryValidationException e) {
            // Since query is null, this can never happen
            return new ArrayList<URI>();
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
    	if ((pdURI == null) || pdURI.equals(root)) {
    		// OAI hierarchy is flat (no sub-directories) - only allow 'null' as pdURI!
	    	ArrayList<URI> resultList = new ArrayList<URI>();
	    	
	    	OaiPmhServer server = new OaiPmhServer(baseURL);
	    	try {
	    		RecordsList list;
	    		if (q == null) {
		    		list = server.listRecords(metaDataPrefix, "2009-07-12T10:50:20Z", "2009-07-14T16:38:20Z", set);	    			
	    		} else {
	    			if (!(q instanceof QueryDateRange))
	    				throw new QueryValidationException("Unsupported query type");
	    			
	    			list = server.listRecords(metaDataPrefix, dateFormat.format(((QueryDateRange) q).getStartDate().getTime()), dateFormat.format(((QueryDateRange) q).getEndDate().getTime()), set);	
	    		}
	    		
	    		// Prepare XPath expression
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("pmh", "http://www.openarchives.org/OAI/2.0/");	
                map.put("xb", "http://com/exlibris/digitool/repository/api/xmlbeans");
                
                try {
	                XPath xpathURL = new Dom4jXPath("/pmh:OAI-PMH/pmh:ListRecords/pmh:record/pmh:metadata/xb:digital_entity/pmh:urls/pmh:url[@type='stream']");
	                xpathURL.setNamespaceContext(new SimpleNamespaceContext(map));
		    		
		    		List urlList = xpathURL.selectNodes(list.getResponse());
		    		for (Object aNode : urlList) {
		    			try {
		    				if (aNode instanceof Node) {
		    					resultList.add(new URI(((Node) aNode).getText()));
		    				}
		    			} catch (URISyntaxException ue) {
		    				log.warning("Error parsing record from " + baseURL + ": " + aNode.toString());
		    			}
		    		}
	    		} catch (JaxenException je) {
    				log.warning("Error creating XPath expression (should not happen).");
    			}
	    	} catch (OAIException e) {
	    		log.severe(e.getMessage());
	    	}
	        return resultList;
    	} else {
    		return new ArrayList<URI>();
    	}
	}
	
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		try {
			// Will simply attempt to download the object at the provided URI,
			// no matter whether located at ONB or not
            DigitalObject.Builder dob = new DigitalObject.Builder(Content.byReference(pdURI.toURL()));
            String uriStr = pdURI.toString();
            System.out.println("Building: " + uriStr);
            System.out.println("DigitalObject: " + dob.toString());
            dob.title(uriStr.substring(uriStr.lastIndexOf('?') + 5) + ".tif");
            System.out.println("Title: " + dob.getTitle());
            return dob.build();
		} catch (Exception e) {
			throw new DigitalObjectNotFoundException("Error retrieving object from " + pdURI.toString() + " (" + e.getMessage() + ")");
		}
    }
    
    public URI getRootURI() {
    	return root;
    }
    
	/**
	 * Basic tests.
	 */
	public static void main(String[] args) {
		OAIDigitalObjectManagerONBDemoImpl oaiImpl = new OAIDigitalObjectManagerONBDemoImpl("http://archiv-test.onb.ac.at:8881/OAI-PUB");
		
		// ListIdentifiers
		System.out.println("starting query.");

		List<URI> identifiers = oaiImpl.list(null);
		System.out.println(identifiers.size() + " found.");
		
		// GetRecord for each identifier
		for (URI id : identifiers) {
			try {
				DigitalObject dob = oaiImpl.retrieve(id);
				System.out.println("retrieved file: " + dob.getTitle());
			} catch (DigitalObjectNotFoundException e) {
				System.out.println("couldn't retrieve file: " + e.getMessage());
			}
		}
		
		System.out.println("done.");
	}
    public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
}
