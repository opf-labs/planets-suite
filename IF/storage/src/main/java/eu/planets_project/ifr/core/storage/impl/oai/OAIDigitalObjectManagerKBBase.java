package eu.planets_project.ifr.core.storage.impl.oai;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;


/**
 * Implementation of the DigitalObjectManager interface based upon a OAI-KB repository.
 */
public class OAIDigitalObjectManagerKBBase extends DigitalObjectManagerBase {
	/** The logger instance */
    private static Logger log = Logger.getLogger(OAIDigitalObjectManagerKBBase.class.getName());

	/** This is the root directory of this particular Data Registry */
	protected File _root = null;
	
	/** Public statics for the property names used to configure an instance */
	public final static String PATH_KEY = "manager.path";
	
	private static OAIDigitalObjectManagerKBImpl dom = null;
	
	public static final String DEFAULT_BASE_URL = 
		"http://jsru.kb.nl/sru?operation=searchRetrieve&version=1.1&x-collection=eDepot&recordSchema=dcx&query=nbn-number%20all";
	
	public static final String PREFIX = "oai_kb";
	
	public static final String REGISTRY_NAME = "oai-kb";
	
	
    /**
     * {@inheritDoc}
     * @param config 
     */
    public OAIDigitalObjectManagerKBBase(Configuration config) {
    	super(config);
    	try {
        	String path = config.getString(PATH_KEY);
        	this.checkConstructorArguments(new File(path));
        	
    		log.info("OAIDigitalObjectManagerKBBase() DEFAULT_BASE_URL: " + DEFAULT_BASE_URL);
            dom = new OAIDigitalObjectManagerKBImpl(DEFAULT_BASE_URL);
        	
        	this._root = new File(path);
    	} catch (NoSuchElementException e) {
	    		throw new IllegalArgumentException("Path property with key " + PATH_KEY + " not found in config");
	    	}
	    }

		//=============================================================================================
		// BASE CLASS METHODS
		//=============================================================================================
		/**
		 * {@inheritDoc}
		 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
		 */
		@Override
		public List<URI> list(URI pdURI) {
			// The return array of URIs contains the contents for the passed URI
			List<URI> retVal = null;
			log.info("OAIDigitalObjectManagerKBBase list() URI " + pdURI);

			try {
				// First lets look at the passed URI, if it's null then we need to return the root 
				log.info("OAIDigitalObjectManagerKBBase list() Testing for null URI");
				if (pdURI == null)
				{
					log.info("OAIDigitalObjectManagerKBBase list() URI is empty so return root URI only");
					retVal = new ArrayList<URI>();
					retVal.add( this.id ); 
					return retVal; 
				}
				log.info("OAIDigitalObjectManagerKBBase list() URI is NOT NULL");
				try {
			        log.info("Replace HTTP path by registry path: " + pdURI);   
					URI baseUri = new PDURI(pdURI.normalize()).formDataRegistryRootURI();
					log.info("OAIDigitalObjectManagerKBBase list() base URI " + baseUri);	
					if (dom != null && baseUri != null) {
						dom.setBaseRegistryURI(baseUri);
					}

					if (pdURI.equals(baseUri)) {
						retVal = this.listFileLocation(pdURI);
					}

					if (retVal != null) {
						log.info("Listing URIs");
						for (URI uri : retVal) {
							log.info("URI in list: " + uri);
						}
					} else {
						log.info("RetVal is NULL");
					}
					
				} catch (URISyntaxException e) {
					log.severe("URI Syntax exception");
					log.severe(e.getMessage());
					log.severe(e.getStackTrace().toString());
					e.printStackTrace();
					return null;
				}
			} catch (Exception e) {
				log.info("OAIDigitalObjectManagerKBBase dom.list error: "
						+ e.getMessage());
			}

			return retVal;
		}

		
		/**
		 * This code take the actual file location and turns it into a listing.
		 * 
		 * @param pdURI
		 * @param fullPath
		 * @return
		 * @throws URISyntaxException
		 */
		protected List<URI> listFileLocation(URI pdURI) throws URISyntaxException {
	        List<URI> retVal = new ArrayList<URI>();
	        List<URI> tmpRetVal = new ArrayList<URI>();
	        tmpRetVal = dom.list(pdURI);
	        log.info("OAIDigitalObjectManagerKBBase listFileLocation() pdURI " + pdURI);
	        log.info("OAIDigitalObjectManagerKBBase listFileLocation() Contents URI array has " + tmpRetVal.size() + " elements");
	        for (URI uri : tmpRetVal) {
	        	String leafname = "";
	            if(uri != null) {
	                leafname = uri.getPath();
	                log.info("OAIDigitalObjectManagerKBBase listFileLocation() uri.getPath(): " + leafname);
	                if(leafname != null) {
	                    String[] parts = leafname.split("/");
	                    if( parts != null && parts.length > 0 )
	                        leafname = parts[parts.length-1];
	                }
	            }
	            log.info("OAIDigitalObjectManagerKBBase listFileLocation() leafname: " + leafname +
	            		", pdURI.toString(): " + pdURI.toString() + ", uri.toString(): " + uri.toString());

	           	URI resUri = URI.create(pdURI.toString() +"/"+ leafname).normalize();            
	        	log.info("OAIDigitalObjectManagerKBBase listFileLocation() Adding URI " + resUri + " to list");
	            retVal.add( resUri );
	            }
	        return retVal;
		}


		/**
		 * This method evaluates original HTTP URI from registry URI
		 * @param uri The registry URI
		 * @return The original HTTP URI
		 */
		public static URI getOriginalUri(URI uri) {
			URI res = uri;
			try {
				URI baseUri = new PDURI(uri.normalize()).formDataRegistryRootURI();
				log.info("OAIDigitalObjectManagerKBBase getOriginalUri() base URI " + baseUri);				
				URI keyUri = URI.create(uri.toString().replaceAll(baseUri.toString(), "").substring(1));
				log.info("OAIDigitalObjectManagerKBBase getOriginalUri() keyURI: " + keyUri);
				res = dom.getOriginalUri(keyUri);
			} catch (Exception e) {
				log.info("OAIDigitalObjectManagerKBBase getOriginalUri() error: " + e.getMessage());				
			}

			return res;		
		}
		
				
		/**
		 * {@inheritDoc}
		 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#retrieve(java.net.URI)
		 */
		@Override
		public DigitalObject retrieve(URI pdURI)
				throws DigitalObjectNotFoundException {
			DigitalObject retObj = null;
			
			try {
				URI httpURI = getOriginalUri(pdURI);
				log.info("OAIDigitalObjectManagerKBBase retrieve() httpURI: " + httpURI);	
				retObj = dom.retrieve(httpURI);
			} catch (Exception e) {
				log.info("OAIDigitalObjectManagerKBBase retrieve() error: " + e.getMessage());				
			}
			
			return retObj;
		}

	    public String retrieveTitle(URI pdURI) {
	    	return null;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getQueryTypes()
	     */
	    @Override
		public List<Class<? extends Query>> getQueryTypes() {
	        return null;
	    }

	    
	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerbASE#storeAsNew()
	     */
	    @Override
		public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
			log.fine("FileSysDOM storeAsNew");
	        URI pdURI;
	        pdURI = dom.storeAsNew(digitalObject);
	        return pdURI;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	     */
	    @Override
		public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
	            throws DigitalObjectNotStoredException {
	        this.store(pdURI, digitalObject);
	        return pdURI;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#updateExisting()
	     */
	    @Override
		public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
	    	// First lets call retrieve to see if this exists
	    	this.retrieve(pdURI);
	        this.store(pdURI, digitalObject);
	        return pdURI;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#isWritable(java.net.URI)
	     */
	    @Override
		public boolean isWritable(URI pdURI) {
	        return true;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
	     */
	    @Override
		public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
	        return null;
	    }

	    //=============================================================================================
		// FACTORY METHODS
		//=============================================================================================
		/**
		 * @param config
		 * 		A config object with the DOM details 
		 * @return
		 * 		A new FilesystemDigitalObjectManagerImpl instance based upon a root directory
		 * @throws IllegalArgumentException 
		 */
		public static DigitalObjectManager getInstance(Configuration config) throws IllegalArgumentException {
			return new OAIDigitalObjectManagerKBBase(config);
		}

	    //=============================================================================================
		// PRIVATE & PROTECTED METHODS
		//=============================================================================================
		private void checkConstructorArguments(File root) throws IllegalArgumentException {
			// Ensure root is not null
			if (root == null) {
				String message = "Supplied root dir is null";
				log.severe(message);
				throw new IllegalArgumentException(message);
			// first make sure it exists and is a directory
			} else if (root.exists()) {
				// OK root exists but it MUST be a directory
				if (!root.isDirectory()) {
					String message = root.getPath() + " is not a directory";
					log.severe(message);
					throw new IllegalArgumentException(message);
				}
			// It doesn't exist so lets create the directory
			} else {
				String message = "Directory " + root.getPath() + " doesn't exist";
				log.info(message);
				root.mkdirs();
			}
		}


		/**
		 * @param pdURI the URI
		 * @param digitalObject the object
		 * @throws DigitalObjectNotStoredException
		 */
		private void store(URI pdURI, DigitalObject digitalObject)
				throws DigitalObjectNotStoredException {

			log.fine("testing title");
			       if( digitalObject.getTitle() == null ) {
			          throw new DigitalObjectNotStoredException(
			        		"The DigitalObject titel was not found!");
			       }

			dom.store(pdURI, digitalObject);
		}

		/**
		 * This is just a helper to create a new URI reliably, using an existing one as a template.
		 * 
		 * @param oldPathUri
		 * @param newPath
		 * @return
		 * @throws URISyntaxException
		 */
	    static protected URI createNewPathUri(URI oldPathUri, String newPath)
	            throws URISyntaxException {
	        return new URI(oldPathUri.getScheme(), oldPathUri.getUserInfo(),
	                oldPathUri.getHost(), oldPathUri.getPort(), newPath, null, null);
	    }
	}
