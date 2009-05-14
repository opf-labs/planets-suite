package eu.planets_project.tb.impl.data.demo.queryable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
// import java.util.Calendar;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
// import org.dom4j.io.OutputFormat;
// import org.dom4j.io.XMLWriter;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.IdentifiersList;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.DigitalObject.Builder;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

/**
 * Implements the DigitalObjectManager interface for a 'generic' OAI-PMH source. 
 * (In fact, 'generic' is not quite true - since the source must adhere to certain
 * internal criteria, i.e. deliver URLs to actual digital files in the 'identifier'
 * field).
 * 
 * This implementation is read-only and queryable with a QueryDateRange.
 * 
 * It is implemented based on the open source OAI client library OAI4J created by the
 * National Library of Sweden, licenced under the Apache License V2.0 licence.
 * 
 * @author SimonR
 *
 */
public class GenericOAIDigitalObjectManagerImpl implements DigitalObjectManager {
	
	/**
	 * Logger
	 */
    private static PlanetsLogger log = PlanetsLogger.getLogger(GenericOAIDigitalObjectManagerImpl.class);
    
    /**
     * OAI-style date format
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
     * OAI endpoint base URL
     */
    private String baseURL;
    
    private String metaDataPrefix = "oai_dc";

    public GenericOAIDigitalObjectManagerImpl(String baseURL) {
    	this.baseURL = baseURL;
    }
    
    public GenericOAIDigitalObjectManagerImpl(String baseURL, String metaDataPrefix) {
    	this.baseURL = baseURL;
    	this.metaDataPrefix = metaDataPrefix;
    }
	
	public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");
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
	    			
	    			list = server.listIdentifiers(metaDataPrefix, dateFormat.format(((QueryDateRange) q).getStartDate().getTime()), dateFormat.format(((QueryDateRange) q).getEndDate().getTime()), null);	
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
    
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
    	if (metaDataPrefix.equals("de2aleph")) {
    		return retrieveDe2Aleph(pdURI);
    	} else {
    		// Assume default 'oai_dc'
    		return retrieveDC(pdURI);
    	}
    }

	private DigitalObject retrieveDC(URI pdURI) throws DigitalObjectNotFoundException {
		try {
			OaiPmhServer server = new OaiPmhServer(baseURL);
			Record record = server.getRecord(pdURI.toString(), metaDataPrefix);
			Element metadata = record.getMetadata();
			
			/*
            OutputFormat screenOutFormat = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(System.out, screenOutFormat);
			writer.setIndentLevel(2);
			writer.write(record.getResponse());
			*/
			
			if (metadata != null) {
				// Namespace URI
				URI namespaceURI = null;
				try {
					namespaceURI = new URI(metadata.getNamespaceURI());
				} catch (URISyntaxException e) {
					log.warn("Error parsing namespace URI: " + metadata.getNamespaceURI() + " (should never happen...)");
				}
				
				// DigitalObject title
				String title = null;
				
				// DigitalObject URL (required)
				String url = null;
				
				for (Object child : metadata.content()) {
					if (child instanceof Element) {
						Element c = (Element) child;
						
						// Title
						if (c.getName().equalsIgnoreCase("title"))
							title = c.getData().toString();
						
						// URL (if provided)
						if (c.getName().equalsIgnoreCase("identifier")) {
							String id = c.getData().toString();
							if (id.startsWith("http://"))
								url = id;
						}
					}
				}
				
				if (url != null) {
					Builder builder = new DigitalObject.Builder(ImmutableContent.byReference(new URL(url)));
					builder.title(title);
					builder.metadata(new Metadata(namespaceURI, record.getMetadataAsString()));
					return builder.build();
				}
			}
			
			throw new DigitalObjectNotFoundException("No HTTP URL available for this record");
		} catch (OAIException e) {
			throw new DigitalObjectNotFoundException(e.getMessage());
		} catch (IOException e) {
			throw new DigitalObjectNotFoundException(e.getMessage());
		}
	}
	
    private DigitalObject retrieveDe2Aleph(URI pdURI) throws DigitalObjectNotFoundException {
        try {
        	OaiPmhServer server = new OaiPmhServer(baseURL);
            Record record = server.getRecord(pdURI.toString(), metaDataPrefix);
            Element metadata = record.getMetadata();
            
            /*
            OutputFormat screenOutFormat = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(System.out, screenOutFormat);
			writer.setIndentLevel(2);
			writer.write(metadata);
			*/
			            
            if (metadata != null) {
				// Namespace URI
				URI namespaceURI = null;
				try {
					namespaceURI = new URI(metadata.getNamespaceURI());
				} catch (URISyntaxException e) {
					log.warn("Error parsing namespace URI: " + metadata.getNamespaceURI() + " (should never happen...)");
				}
				
                HashMap<String, String> map = new HashMap<String, String> ();
                map.put("pmh", "http://www.openarchives.org/OAI/2.0/");
                map.put("xb", "http://com/exlibris/digitool/repository/api/xmlbeans");

                Document doc = metadata.getDocument();

                XPath xpathURL = new Dom4jXPath("/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:urls/pmh:url[@type='stream']");
                xpathURL.setNamespaceContext(new SimpleNamespaceContext(map));

                XPath xpathLabel = new Dom4jXPath("/pmh:OAI-PMH/pmh:GetRecord/pmh:record/pmh:metadata/xb:digital_entity/pmh:control/pmh:label");
                xpathLabel.setNamespaceContext(new SimpleNamespaceContext(map));

                Node urlNode = (Node) xpathURL.selectSingleNode(doc);
                Node dcNode = (Node) xpathLabel.selectSingleNode(doc);

                if (dcNode != null && urlNode != null) {
                    String title = dcNode.getText();
                    String url = urlNode.getText();
                    
					Builder builder = new DigitalObject.Builder(ImmutableContent.byReference(new URL(url)));
					builder.title(title);
					builder.metadata(new Metadata(namespaceURI, record.getMetadataAsString()));
					return builder.build();
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return null;
    }

	public List<Class<? extends Query>> getQueryTypes() {
		ArrayList<Class<? extends Query>> qTypes = new ArrayList<Class<? extends Query>>();
		qTypes.add(Query.DATE_RANGE);
		return qTypes;
	}
	
	/*
	public static void main(String[] args) {
		GenericOAIDigitalObjectManagerImpl oaiImpl = new GenericOAIDigitalObjectManagerImpl("http://www.bibliovault.org/perl/oai2");
		// GenericOAIDigitalObjectManagerImpl oaiImpl = new GenericOAIDigitalObjectManagerImpl("http://archiv-test.onb.ac.at:8881/OAI-PUB", "de2aleph");
		// GenericOAIDigitalObjectManagerImpl oaiImpl = new GenericOAIDigitalObjectManagerImpl("http://localhost:8881/OAI-PUB", "de2aleph");
		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MONTH, -24);
		Calendar now = Calendar.getInstance();
		
		// ListIdentifiers
		System.out.println("starting query.");
		try {
			List<URI> identifiers = oaiImpl.list(null, new QueryDateRange(start, now));
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
		} catch (QueryValidationException e) {
			System.out.println("QueryValidationException: " + e.getMessage());
		}
		
		System.out.println("done.");
	}
	*/
	
}
