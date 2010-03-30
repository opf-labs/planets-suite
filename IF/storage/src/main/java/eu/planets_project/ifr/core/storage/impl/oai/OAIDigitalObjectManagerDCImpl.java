package eu.planets_project.ifr.core.storage.impl.oai;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import se.kb.oai.pmh.Record;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.RecordsList;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
//import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.DigitalObject.Builder;

//import java.util.Calendar;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 * DC implementation of the OAI digital object manager.
 */
public class OAIDigitalObjectManagerDCImpl extends AbstractOAIDigitalObjectManagerImpl {
	
	/**
	 * The cache map binds URI with the digital object.
	 */
	private static Map<URI, DigitalObject> leafMap = new HashMap<URI, DigitalObject>();
	
    /**
     * The manager control thread.
     */
    private static ManagerControl mc;

	/**
	 * This is a cache for list method.
	 */
	private static List<URI> uriList = new ArrayList<URI>();

    
	/**
	 * @param baseURL The base URL
	 */
	public OAIDigitalObjectManagerDCImpl(String baseURL) {
		super(baseURL, OAIDigitalObjectManagerDCBase.PREFIX);
        mc = new ManagerControl(this);
        mc.start();
        log.info("Manager control thread started.");
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
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
    	long starttime = System.currentTimeMillis();
    	log.info("OAIDigitalObjectManagerDCImpl retrieve() starttime: " + starttime);
		try {
			if (pdURI != null && leafMap.containsKey(pdURI)) {
		    	log.info("OAIDigitalObjectManagerDCImpl retrieve() already exist in map uri: " + pdURI);
		    	long endtime = System.currentTimeMillis();
		    	log.info("OAIDigitalObjectManagerDCImpl retrieve() timediff: " + (endtime - starttime));
				return leafMap.get(pdURI);
			}
	    	long endtime = System.currentTimeMillis();
	    	log.info("OAIDigitalObjectManagerDCImpl retrieve() error1: NoHTTP URL available." + " timediff: " + (endtime - starttime));
			throw new DigitalObjectNotFoundException("No HTTP URL available for this record");
		} catch (Exception e) {
	    	long endtime = System.currentTimeMillis();
	    	log.info("OAIDigitalObjectManagerDCImpl retrieve() error2: " + e.getMessage() + " timediff: " + (endtime - starttime));
			throw new DigitalObjectNotFoundException(e.getMessage());
		}
	}

	
	/**
	 * This method retrieves titles, URIs and metadata from server.
	 * @param pdURI The parent URI
	 * @param server The PMH server
	 * @return The list of the URIs
	 */
	private ArrayList<URI> retrieveAll(URI pdURI, OaiPmhServer server) {
    	ArrayList<URI> resultList = new ArrayList<URI>();

    	long starttime = System.currentTimeMillis();
    	log.info("OAIDigitalObjectManagerDCImpl retrieveAll() starttime: " + starttime + ", pdURI: " + pdURI);
    	try {
	    	RecordsList recordsList;
	    	recordsList = server.listRecords(metaDataPrefix);	
	    	for (Record record : recordsList.asList()) {
	    		Element metadata = record.getMetadata();
				if (metadata != null) {
					// Namespace URI
					URI namespaceURI = null;
					try {
						namespaceURI = new URI(metadata.getNamespaceURI());
				    	log.info("OAIDigitalObjectManagerDCImpl retrieveAll() found namespaceURI: " + namespaceURI);
					} catch (URISyntaxException e) {
						log.warning("Error parsing namespace URI: " + metadata.getNamespaceURI() + " (should never happen...)");
					}
	
					// DigitalObject title
					String title = null;
					// DigitalObject URL (required)
					String url = null;
					
					for (Object child : metadata.content()) {
						if (child instanceof Element) {
							Element c = (Element) child;
							
							// Title
							if (c.getName().equalsIgnoreCase("title")) {
								title = c.getData().toString();
						    	log.info("OAIDigitalObjectManagerDCImpl retrieveAll() found title: " + title);
							}
							
							// URL (if provided)
							if (c.getName().equalsIgnoreCase("identifier")) {
								String id = c.getData().toString();
								if (id.startsWith("http://"))
									url = id;
							}										
						}
					}
			    	log.info("OAIDigitalObjectManagerDCImpl retrieveAll() found idurl: " + url);
					if (url != null) {
		    	    	resultList.add(URI.create(url));
						Builder builder = new DigitalObject.Builder(Content.byReference(new URL(url)));
						builder.title(title);
						builder.metadata(new Metadata(namespaceURI, record.getMetadataAsString()));
				    	long endtime = System.currentTimeMillis();
				    	log.info("OAIDigitalObjectManagerDCImpl retrieve() timediff: " + (endtime - starttime));
						DigitalObject o = builder.build();
						if (url != null && !leafMap.containsKey(URI.create(url))) {
					    	log.info("OAIDigitalObjectManagerDCImpl retrieve() add to map uri: " + URI.create(url));
							leafMap.put(URI.create(url), o);
						}
					}
				}
	    	}
    	} catch (Exception e) {
			log.info("OAIDigitalObjectManagerDCImpl retrieveAll() pdURI: " + pdURI + ", error: " + e.getMessage());    		
    	}
    	long endtime = System.currentTimeMillis();
    	log.info("****** OAIDigitalObjectManagerDCImpl retrieveAll() timediff: " + (endtime - starttime));
    	
    	return resultList;		
	}
	
	
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
		log.info("OAIDigitalObjectManagerDCImpl list() URI " + pdURI);
		
    	if (pdURI == null) {
    		if (uriList != null && uriList.size() > 0) {
    			return uriList;
    		} else {
	    		// OAI hierarchy is flat (no sub-directories) - only allow 'null' as pdURI!
		    	List<URI> resultList = new ArrayList<URI>();
		    	
				log.info("OAIDigitalObjectManagerDCImpl pdURI: " + pdURI + ", baseURL: "+ baseURL);
		    	OaiPmhServer server = new OaiPmhServer(baseURL);
		    	try {
		    		if (q == null) {
		    			log.info("OAIDigitalObjectManagerDCImpl pdURI: " + pdURI + ", metaDataPrefix: "+ metaDataPrefix);
			    		
			    		// read titles and metadata
		    			resultList = retrieveAll(pdURI, server);
		    		} else {
		    			log.info("OAIDigitalObjectManagerDCImpl pdURI: " + pdURI + ", q != null");
		    			if (!(q instanceof QueryDateRange))
		    				throw new QueryValidationException("Unsupported query type");
		    			resultList = super.list(pdURI, q);
		    		}		    		
		    	} catch (Exception e) {
		    		log.severe(e.getMessage());
	    			log.info("OAIDigitalObjectManagerDCImpl pdURI: " + pdURI + ", error: " + e.getMessage());
		    	}
		    	uriList = resultList;
		        return resultList;
			}
    	} else {
    		return new ArrayList<URI>();
    	}		
    }


    /*
	public static void main(String[] args) {
		OAIDigitalObjectManagerDCImpl oaiImpl = new OAIDigitalObjectManagerDCImpl(OAIDigitalObjectManagerDCBase.DEFAULT_BASE_URL);		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MONTH, -24);
		Calendar now = Calendar.getInstance();
		
		// ListIdentifiers
		System.out.println("starting query.");
		try {
//			List<URI> identifiers = oaiImpl.list(null, new QueryDateRange(start, now));	
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
		} catch (QueryValidationException e) {
			System.out.println("QueryValidationException: " + e.getMessage());
		}	
		System.out.println("done.");
	}
	*/
    
    public URI storeAsNew(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
        throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");
    }
    
    public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
        throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");
    }

    public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
        throw new DigitalObjectNotStoredException("Storing not supported by this implementation.");
    }

    
    /**
     * This class manages the cache of retrieved digital objects.
     * @author GrafR
     *
     */
    private class ManagerControl extends Thread {
        
        /**
         * Repository implementation
         */
        OAIDigitalObjectManagerDCImpl impl;
        
        /**
         * The time between consistency check
         */
        long sleeptime = 1200000;

        /**
         * @param impl The repository implementation
         */
        public ManagerControl(OAIDigitalObjectManagerDCImpl _impl) {
            this.impl = _impl;
        }

        public void run() {
            while (true) {
                try {
                    log.info("ManagerControl run().");
                    Thread.sleep(sleeptime);
                    // check cache entries. Update digital object if necessary. 
                    Iterator<URI> leafIterator = leafMap.keySet().iterator();
                    while(leafIterator.hasNext()) {
                        URI currentUri = leafIterator.next();
                        log.info("ManagerControl check uri: " + currentUri);    
                        try {
                        	DigitalObject presentObj = impl.retrieve(currentUri);
                        	if (!presentObj.equals(leafMap.get(currentUri))) {
                        		leafMap.put(currentUri, presentObj);
                        	}
                        } catch (DigitalObjectNotFoundException e) {
                            log.info("ManagerControl digital object not found for uri: " + currentUri);    
                            // remove it if it is not more present in the repository.
                            leafMap.remove(currentUri);
                        }
                    }
                } catch (InterruptedException e) {
                    log.info("ManagerControl error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
