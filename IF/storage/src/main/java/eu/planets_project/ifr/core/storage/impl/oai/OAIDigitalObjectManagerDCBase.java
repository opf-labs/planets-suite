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


/**
 * Implementation of the DigitalObjectManager interface based upon a OAI-DC repository.
 */
public class OAIDigitalObjectManagerDCBase extends DigitalObjectManagerBase {
	/** The logger instance */
    private static Logger log = Logger.getLogger(OAIDigitalObjectManagerDCBase.class.getName());

	/** This is the root directory of this particular Data Registry */
	protected File _root = null;
	
	/** Public statics for the property names used to configure an instance */
	public final static String PATH_KEY = "manager.path";
	
	private static OAIDigitalObjectManagerDCImpl dom = null;
	
	protected static String DEFAULT_BASE_URL = "http://eprints.whiterose.ac.uk/cgi/oai2";
	
	public static String OAI_DC_CHILD_URI = "eprints.whiterose.ac.uk"; 
	
	public static String PREFIX = "oai_dc";
	
	public static String REGISTRY_NAME = "oai-dc";
	

    /**
     * {@inheritDoc}
     * @param config 
     */
    public OAIDigitalObjectManagerDCBase(Configuration config) {
    	super(config);
    	try {
        	String path = config.getString(PATH_KEY);
        	this.checkConstructorArguments(new File(path));
    		log.info("OAIDigitalObjectManagerDCBase() DEFAULT_BASE_URL: " + DEFAULT_BASE_URL);
        	
            dom = new OAIDigitalObjectManagerDCImpl(DEFAULT_BASE_URL);
        	
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
		 * 
		 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
		 */
	@Override
	public List<URI> list(URI pdURI) {
		// The return array of URIs contains the contents for the passed URI
		List<URI> retVal = new ArrayList<URI>(0);
		log.info("OAIDigitalObjectManagerDCBase list() URI " + pdURI);

		try {
			if (pdURI.toString().contains(REGISTRY_NAME)) {
				URI _pdURI = null;
				retVal = dom.list(_pdURI);
			}
		} catch (Exception e) {
			log.info("OAIDigitalObjectManagerDCBase dom.list error: "
					+ e.getMessage());
		}

		return retVal;
	}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#retrieve(java.net.URI)
		 */
		@Override
		public DigitalObject retrieve(URI pdURI)
				throws DigitalObjectNotFoundException {
			DigitalObject retObj = null;
			log.info("OAIDigitalObjectManagerDCBase retrieve() URI " + pdURI);
			
			retObj = dom.retrieve(pdURI);
			
			return retObj;
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
			return new OAIDigitalObjectManagerDCBase(config);
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
