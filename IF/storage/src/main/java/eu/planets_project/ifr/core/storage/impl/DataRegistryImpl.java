/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.ifr.core.storage.impl.util.TBContentResolver;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;

/**
 * @author CFWilson
 *
 */
@Stateless(mappedName="data/DataRegistry")
@Local(DataRegistry.class)
@Remote(DataRegistry.class)
@LocalBinding(jndiBinding="planets-project.eu/DataRegistry/local")
@RemoteBinding(jndiBinding="planets-project.eu/DataRegistry/remote")
@SecurityDomain("PlanetsRealm")
public class DataRegistryImpl implements DataRegistry {
	// The logger
	private static Logger _log = Logger.getLogger(DataRegistryImpl.class.getName());

	private static final String CLASS_NAME_KEY = "manager.class.name";

	// String for the extension filter for properties files
	private static final String PROP_EXT = ".properties";
	
	// The instance to try to keep this a singleton
	private static volatile DataRegistryImpl instance = null;

	// Hash set of ObjectManagers
	private HashMap<URI, DigitalObjectManagerBase> _objManagerSet = new HashMap<URI, DigitalObjectManagerBase>();

	// The location of the DigitalObjectManager property files
	private String _domPropFileDirName = null;
	
	private URI defaultDomUri = null;
	
	/**
	 * Constructor of the DataRegistryImpl
	 */
	protected DataRegistryImpl() {
		// OK lets populate the DigitalObjectManager HashList
		this.findDigitalObjectManagers();
	}
	
	/**
	 * Factory method
	 * @return The DataRegistryImpl instance
	 * 
	 */
	public static DataRegistryImpl getInstance() {
		if(instance == null)
		{
			synchronized(DataRegistryImpl.class)
			{
				if(instance == null)
				{
				   instance = new DataRegistryImpl();
				}
			}
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DataRegistry#getDigitalObjectManager(java.net.URI)
	 */
	public DigitalObjectManager getDigitalObjectManager(URI uri) throws DigitalObjectManagerNotFoundException {
		DigitalObjectManager dom = null;
		try {
			_log.info("Looking up DigitalObjectManager for " + uri);
			dom = this._objManagerSet.get(new PDURI(uri).formDataRegistryRootURI());
		} catch (URISyntaxException e) {
			_log.info("URI Syntax Exception");
			throw new DigitalObjectManagerNotFoundException("No DigitalObjectManager found for URI:" + uri.toASCIIString(), e);
		}
		if (dom == null) {
			_log.info("Returned DigitalObjectManager is NULL");
			throw new DigitalObjectManagerNotFoundException("No DigitalObjectManager found for URI:" + uri.toASCIIString()); 
		}
		return dom;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DataRegistry#hasDigitalObjectManager(java.net.URI)
	 */
	public boolean hasDigitalObjectManager(URI uri) {
		// Try to retrieve the DigitalObjectManager for the supplied URI
		try {
			_log.info("Looking up DigitalObjectManager for " + uri);
			return this._objManagerSet.containsKey(new PDURI(uri).formDataRegistryRootURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_log.warning("URI Syntax Exception, we don't have this DOM");
			_log.info(e.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		/*
		 * FIXME: This is messy as without a URI the multi manager cannot identify which
		 * DigitalObjectManager to use and no sensible exception to throw at the moment
		 */
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
	 */
	public boolean isWritable(URI pdURI) {
		try {
			// Attempt to return the isWritable result from the appropriate DigitalObjectManager
			return this.getDigitalObjectManager(pdURI).isWritable(pdURI);
		// If no DigitalObjectManager found
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("DataRegistry.isWritable()");
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			// Return false, can't write to a manager that doesn't exist 
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
	 */
	public List<URI> list(URI pdURI) {
		// If a null URI passed then return a list of all DigitalObjectManagers
		if (pdURI == null) {
			
			// Create a new ArrayList to return
			List<URI> allDoms = new ArrayList<URI>(this._objManagerSet.size());
			
			// Iterate through the keys and add to the ArrayList
			for (URI uri : this._objManagerSet.keySet()) {
				allDoms.add(uri);
			}
			
			// Return the created list
			return allDoms;
		}
		
		// OK we have a URI, so return the DigitalObjectManager using it
		try {
			_log.info("Retrieving list from the DOM " + pdURI);
			return this.getDigitalObjectManager(pdURI).list(pdURI);
		
		// If no match then return catch the DigitalObjectNotFound excep and return null
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("DataRegistry.list(URI pdURI, Query q)");
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
	 */
	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
		try {
			return this.getDigitalObjectManager(pdURI).list(pdURI, q);
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("DataRegistry.list(URI pdURI, Query q)");
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		try {
			return this.getDigitalObjectManager(pdURI).retrieve(pdURI);
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotFoundException("No DigitalObjectManager found for URI " + pdURI, e);
		}
	}

	/**
	 * @param pdURI
	 * @return
	 * @throws DigitalObjectNotFoundException
	 */
	public DigitalObject retrieveAsTBReference(URI pdURI)
			throws DigitalObjectNotFoundException {
			DigitalObject finalObj = null;
		try {
			DigitalObject digitalObj = this.getDigitalObjectManager(pdURI).retrieve(pdURI);
			DigitalObjectContent content = DataRegistryImpl.getObjectContentByRef(pdURI);
			_log.info("The permanent URI of this object will be " + digitalObj.getPermanentUri());
			finalObj = new DigitalObject.Builder(content)
					.title(digitalObj.getTitle())
					.permanentUri(digitalObj.getPermanentUri())
					.manifestationOf(digitalObj.getManifestationOf())
					.format(digitalObj.getFormat())
					.metadata(digitalObj.getMetadata().toArray(new Metadata[0]))
					.events(digitalObj.getEvents().toArray(new Event[0]))
					.build();
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotFoundException("No DigitalObjectManager found for URI " + pdURI, e);
		} catch (MalformedURLException e) {
			_log.info("Malformed URL exception resolving " + pdURI);
			// TODO Auto-generated catch block
			throw new DigitalObjectNotFoundException("Malformed URL exception resolving " + pdURI, e);
		}
		return finalObj;
	}
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI storeAsNew(DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		/*
		 * FIXME: This is messy as without a URI the multi manager cannot identify which
		 * DigitalObjectManager to use
		 */
		throw new DigitalObjectNotStoredException("Could not identify DigitalObjectManager to store to");
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		
		// Simply look up the correct Digital Object Manager and call the storeAsNew method.
		try {
			return this.getDigitalObjectManager(pdURI).storeAsNew(pdURI, digitalObject);
		// If we can't find the DigitalObjectManger
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("DataRegistry.storeAsNew(URI pdURI, DigitalObject digitalObject)");
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotStoredException(e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI updateExisting(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException,
			DigitalObjectNotFoundException {

		try {
			return this.getDigitalObjectManager(pdURI).updateExisting(pdURI, digitalObject);
		} catch (DigitalObjectManagerNotFoundException e) {
			_log.info("DataRegistry.updateExisting(URI pdURI, DigitalObject digitalObject)");
			_log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotFoundException(e);
		}
	}


	public boolean addDigitalObjectManager(URI uri, DigitalObjectManagerBase dom) {
		try {
			this._objManagerSet.put(new PDURI(uri).formDataRegistryRootURI(), dom);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int countDigitalObjectMangers() {
		return this._objManagerSet.size();
	}

	public void deleteDigitalObjectManager(URI uri)  throws DigitalObjectManagerNotFoundException {
		try {
			if (this._objManagerSet.remove(new PDURI(uri).formDataRegistryRootURI()) == null) {
				throw new DigitalObjectManagerNotFoundException("DigitalObjectManager " +
						new PDURI(uri).formDataRegistryRootURI() + " not found");
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Private method to populate the internal HashMap of Digital Object Managers
	 */
	private void findDigitalObjectManagers() {
		// OK, we need the config information to find where the DigitalObjectManger
		// config files are held
		// TODO: Just using hard coded value during development, replace with config code
		this._domPropFileDirName = domConfigFolder();
		System.out.println("Property file Config folder is " + this._domPropFileDirName);
		File domPropFileDir = new File(this._domPropFileDirName);
		
		// Check that this is a directory
		if (domPropFileDir.isDirectory()) {
			// Create a filter for properties files
			FilenameFilter sel = new FilenameFilter() {
				public boolean accept(File file, String name) {
					return name.endsWith(PROP_EXT);
				}
			};

			// We want to record the URI of the first writeable DOM we come to.
			// This will be our fall back for default DOM
			URI firstWriteable = null;

			// Iterate through contained files 
			for (File propFile : domPropFileDir.listFiles(sel)) {
				// Get the properties set from the file
				_log.info("Found DOM property file: " + propFile.getName());
				Configuration config = ServiceConfig.getConfiguration(propFile);
				
				// Add the registry to the hash map, the Interface class is instantiated
				// from the class name
				try {
					_log.info("Adding DOM: " + config.getURI(DigitalObjectManagerBase.ID_KEY));
					DigitalObjectManagerBase dom = getDomFromConfig(config);
					if (dom != null) {
						this._objManagerSet.put(new PDURI(config.getURI(DigitalObjectManagerBase.ID_KEY)).formDataRegistryRootURI(), 
								dom);
						// Now check if the default property set true and
						// we haven't got a default yet (this.defaultDomUri not null)
						// First default wins
						String isDefault = "";
						try {
							isDefault = config.getString(DataRegistry.DEFAULT_KEY);
						// No default key but not a problem
						} catch (NoSuchElementException e) {
							isDefault = "";
						}
						if (isDefault.equals("true") && 
								(this.defaultDomUri == null)) {
							this.defaultDomUri = dom.getId();
						// No default candidate so we'll look for the first writeable
						} else if ((firstWriteable == null) &&
									dom.isWritable(dom.getId())) {
							firstWriteable = dom.getId();
						}
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		// The config dir is in fact a file so we bail....
		// TODO: alternatively we could treat this as a single 
		// instance property file and treat it as such
		} else {
			_log.severe("findDigitalObjectManagers");
		}
	}

    private static String domConfigFolder() {
        String domCongigDir = null;
    	Configuration conf = ServiceConfig.getConfiguration("DataRegistry");
    	domCongigDir = conf.getString("dom.config.dir.location");
    	return domCongigDir;
    }

    /**
     * Private method that instantiates and returns a DigitalObjectManager interface
     * from the implementation class name.  This is slightly evil.
     * @param className
     * @return
     */
    private static DigitalObjectManagerBase getDomFromConfig(Configuration config) {
		// Empty Class and DigitalObjectManagerBase
		Class<?> domClass = null;
		DigitalObjectManagerBase dom = null;
		try {
			String className = config.getString(DataRegistryImpl.CLASS_NAME_KEY);
			// Get the class name from the config and then the class from the contexts class loader
			_log.info("Getting DigitalObjectManager class: " + className);
			domClass = Class.forName(className,	true,
					Thread.currentThread().getContextClassLoader());
			
			// Check that the class inherits from DigitalObjectManagerBase, if not we won't try to 
			// instantiate we just pass back null instead
			_log.info("Checking " + className + " extends " + DigitalObjectManagerBase.class.getCanonicalName());
			if 	(domClass.getSuperclass().getName().equals(DigitalObjectManagerBase.class.getName())) {

				// Right we have a class that extends the right base, get the config constructor
				// and create the object
				_log.info("Now we need a constructor that takes a: " 
						+ Configuration.class.getName() + " object");
				dom = (DigitalObjectManagerBase) domClass.getConstructor(
						new Class[] {Configuration.class}).newInstance(new Object[] {config});

			// OK so the DOM implementation doesn't extend DigitalObjectManagerBase
			// Log the error and move on
			} else {
				_log.info("Class " + " doesn't extendend " + DigitalObjectManagerBase.class.getCanonicalName());
				_log.info(DataRegistryImpl.class.getName() + " requires that DigitalObjectManagers extend this base");
			}
		// We don't want to stop if the creation of one DOM goes wrong
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			// The class name element doesn't exist so we can't instantiate
			_log.info("No " + DataRegistryImpl.CLASS_NAME_KEY + " key for class name");
			_log.info("Check the Digital Object Manager for configuration");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// This means that the underlying constructor threw an exception
			_log.info("Exception thrown invoking constructor for DOM class");
			e.printStackTrace();
			_log.info("Underlying construction exception follows");
			_log.info(e.getCause().getMessage());
			e.getCause().printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dom;
    }

	public String getDescription(URI uri) throws DigitalObjectManagerNotFoundException {
		_log.info("getDescription() for " + uri);
		String desc = "";
		if (uri == null) {
			throw new DigitalObjectManagerNotFoundException("Cannot find DigitalObjectManger for null URI");
		}
		DigitalObjectManagerBase dom;
		try {
			dom = this._objManagerSet.get(new PDURI(uri).formDataRegistryRootURI());
		} catch (URISyntaxException e) {
			throw new DigitalObjectManagerNotFoundException("Cannot find DigitalObjectManger for " + uri);
		}
		if (dom == null) {
			throw new DigitalObjectManagerNotFoundException("Cannot find DigitalObjectManger for " + uri);
		}
		desc = dom.getDescription();
		_log.info("Description is " + desc);
		return desc;
	}

	public String getName(URI uri) throws DigitalObjectManagerNotFoundException {
		return this._objManagerSet.get(uri).getName();
	}

	public DigitalObjectManager getDefaultDigitalObjectManager()
			throws DigitalObjectManagerNotFoundException {
		DigitalObjectManager dom = null;
		if (this.defaultDomUri != null) {
			dom = this.getDigitalObjectManager(this.defaultDomUri);
		}
		return dom;
	}

	public URI getDefaultDigitalObjectManagerId() {
		// TODO Auto-generated method stub
		return this.defaultDomUri;
	}

	private static DigitalObjectContent getObjectContentByRef(URI uri)
		throws MalformedURLException {
		DigitalObjectContent content = null;
		URI resolverURI = URI.create(TBContentResolver.getResolverPath() + uri);
		content = Content.byReference(resolverURI.toURL());
		return content;
	}
}

