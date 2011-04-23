/**
 * 
 */
package eu.planets_project.ifr.core.storage.api;

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

//import org.jboss.annotation.ejb.LocalBinding;
//import org.jboss.annotation.ejb.RemoteBinding;
//import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.ifr.core.storage.impl.util.TBContentResolver;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;

/**
 * Package private DataRegistry implementation, to be instantiated using the DataRegistryFactory.
 * @author CFWilson
 *
 */
@Stateless(mappedName="data/DataRegistry")
@Local(DataRegistry.class)
@Remote(DataRegistry.class)
//@LocalBinding(jndiBinding="planets-project.eu/DataRegistry/local")
//@RemoteBinding(jndiBinding="planets-project.eu/DataRegistry/remote")
//@SecurityDomain("PlanetsRealm")
class DataRegistryImpl implements DataRegistry {
	// The logger
	private static Logger log = Logger.getLogger(DataRegistryImpl.class.getName());

	// The keys in the DataRegistry.roperties config file
	private static final String DEFAULT_CONFIG_KEY = "dom.config.default.location";
	private static final String USER_CONFIG_KEY = "dom.config.user.location";

	// The class name key that MUST be in every DigitalObjectManager config file
	private static final String CLASS_NAME_KEY = "manager.class.name";

	// String for the extension filter for properties files
	private static final String PROP_EXT = ".properties";
	
	// The instance to try to keep this a singleton
	private static volatile DataRegistryImpl instance = null;

	// Hash set of DigitalObjectManagers, effectively the DataRegistry
	// Any DigitalObjectManger that wants to be part of this DataRegistry implementation MUST
	// extend the DigitalObjectMangerBase abstract class, not simply implement the interface
	private static volatile HashMap<URI, DigitalObjectManagerBase> domSet = new HashMap<URI, DigitalObjectManagerBase>();

	// The variables required to handle the configuration setup
	// The name of the default DigitalObjectManager config dir
	private static String defaultDomDirName = null;

	// The name of the user DigitalObjectManager config dir
	private static String userDomDirName = null;

	// The URI ID of the default data registry
	private static URI defaultDomUri = null;
	
	/**
	 * Factory method, this is the way to get a DataRegistryImpl instance
	 * 
	 * @return The DataRegistryImpl instance
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

	/**
	 * Constructor of the DataRegistryImpl, protected and is called from the factory 
	 */
	protected DataRegistryImpl() {
		// Set up defaults, test we have some before populating
		if (DataRegistryImpl.setUpDefaultFolders()) {
			// OK lets populate the DigitalObjectManager HashList
			DataRegistryImpl.findDigitalObjectManagers();
		}
	}
	
	public static String getDefaultDomDirName() {
		return DataRegistryImpl.defaultDomDirName;
	}
	
	public static String getUserDomDirName() {
		return DataRegistryImpl.userDomDirName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DataRegistry#getDigitalObjectManager(java.net.URI)
	 */
	public DigitalObjectManager getDigitalObjectManager(URI uri) throws DigitalObjectManagerNotFoundException {
		// The return val
		DigitalObjectManager dom = null;

		// First check that the URI isn's null, if it is throw IllegalArgumentException
		if (uri == null) throw new IllegalArgumentException("URI argument is null");

		// OK, let's try to get the DigitalObjectManager
		try {
			dom = DataRegistryImpl.domSet.get(new PDURI(uri.normalize()).formDataRegistryRootURI());
		// Catch the URI syntax thrown by PDURI if this isn't a valid ID URI, and rethrow as not found
		} catch (URISyntaxException e) {
			throw new DigitalObjectManagerNotFoundException("URI: " + uri + 
					" is not a valid Planets DataRegistry URI ID", e);
		} catch (Exception e) {
			throw new DigitalObjectManagerNotFoundException("No digital object manager found for URI: " + uri, e);
		}
		
		// Final check, if the returned DOM is null then it's not in the HashMap
		if (dom == null) {
			throw new DigitalObjectManagerNotFoundException("No DigitalObjectManager found for URI: " + uri); 
		}

		// Share the goodness of the retrieved DOM
		return dom;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DataRegistry#hasDigitalObjectManager(java.net.URI)
	 */
	public boolean hasDigitalObjectManager(URI uri) {
		// First check that the URI isn's null, if it is throw IllegalArgumentException
		if (uri == null) throw new IllegalArgumentException("URI argument is null");

		// Try to retrieve the DigitalObjectManager for the supplied URI
		try {
			log.info("Looking up DigitalObjectManager for " + uri);
			// Return the hash map lookup, this gives the answer
			return DataRegistryImpl.domSet.containsKey(new PDURI(uri.normalize()).formDataRegistryRootURI());
		// Catch the invalid Planets URI, log and return false
		} catch (URISyntaxException e) {
			log.warning("URI: " + uri + 
					" is not a valid Planets DataRegistry URI ID");
			log.info(e.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		/*
		 * TODO: This is messy as without a URI the multi manager cannot identify which
		 * DigitalObjectManager to use and no sensible exception to throw at the moment.
		 * 
		 * It may be possible to implement an "uber" query type that goes across DOMs but that's
		 * quite a lot of work
		 */
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
	 */
	public boolean isWritable(URI pdURI) {
		try {
			// Attempt to return the isWritable result from the appropriate DigitalObjectManager
			return this.getDigitalObjectManager(pdURI).isWritable(pdURI.normalize());
		// If no DigitalObjectManager found
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
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
			List<URI> allDoms = new ArrayList<URI>(domSet.keySet());
			
			// Return the created list
			return allDoms;
		}
		
		// OK we have a URI, so return the DigitalObjectManager using it
		try {
			log.info("Retrieving list from the DOM " + pdURI);
			return this.getDigitalObjectManager(pdURI).list(pdURI.normalize());
		
		// If no match then return catch the DigitalObjectNotFound excep and return null
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
	 */
	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
		try {
			return this.getDigitalObjectManager(pdURI).list(pdURI.normalize(), q);
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		try {
			return this.getDigitalObjectManager(pdURI).retrieve(pdURI.normalize());
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotFoundException("No DigitalObjectManager found for URI " + pdURI, e);
		}
	}

	/**
	 * This is a bit of a hack required by the test bed, it's NOT in the interface, it's specific
	 * to the implementation.
	 * 
	 * FIXME: This needs to be better
	 * 
	 * @param pdURI
	 * @return A DigitalObject where the content is guaranteed to be a TB reference
	 * @throws DigitalObjectNotFoundException
	 */
	public DigitalObject retrieveAsTbReference(URI pdURI)
			throws DigitalObjectNotFoundException {
		// The return value
		DigitalObject finalObj = null;

		try {
			// let's get the digital object
			DigitalObject digitalObj = this.getDigitalObjectManager(pdURI).retrieve(pdURI.normalize());
			
			// And a content object that's by Refernce
			DigitalObjectContent content = DataRegistryImpl.getObjectContentByRef(pdURI);

			// Buld the new DO, using the reference content
			log.info("The permanent URI of this object will be " + digitalObj.getPermanentUri());
			finalObj = new DigitalObject.Builder(content)
					.title(digitalObj.getTitle())
					.permanentUri(digitalObj.getPermanentUri())
					.manifestationOf(digitalObj.getManifestationOf())
					.format(digitalObj.getFormat())
					.metadata(digitalObj.getMetadata().toArray(new Metadata[0]))
					.events(digitalObj.getEvents().toArray(new Event[0]))
					.build();
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotFoundException("No DigitalObjectManager found for URI " + pdURI, e);
		} catch (MalformedURLException e) {
			log.info("Malformed URL exception resolving " + pdURI);
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
		// OK let's use the default DOM if we can if we
		try {
			return this.getDefaultDigitalObjectManager().storeAsNew(digitalObject);
		} catch (DigitalObjectManagerNotFoundException e) {
			throw new DigitalObjectNotStoredException("Default DigitalObjectManager not found", e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		// Check the passed args to make sure that there are no nulls
		if (pdURI == null)
			throw new IllegalArgumentException("Argument URI pdURI must not be null");
		if (digitalObject == null)
			throw new IllegalArgumentException("Argument DigitalObject digitalObject must not be null");

		// Simply look up the correct Digital Object Manager and call the storeAsNew method.
		try {
			return this.getDigitalObjectManager(pdURI).storeAsNew(pdURI.normalize(), digitalObject);

		// If we can't find the DigitalObjectManger
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotStoredException("No DigitalObjectManager found for URI " + pdURI, e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI updateExisting(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException,
			DigitalObjectNotFoundException {
		// Check the passed args to make sure that there are no nulls
		if (pdURI == null)
			throw new IllegalArgumentException("Argument URI pdURI must not be null");
		if (digitalObject == null)
			throw new IllegalArgumentException("Argument DigitalObject digitalObject must not be null");

		try {
			return this.getDigitalObjectManager(pdURI).updateExisting(pdURI.normalize(), digitalObject);
		} catch (DigitalObjectManagerNotFoundException e) {
			log.info("No DigitalObjectManager found for URI " + pdURI);
			throw new DigitalObjectNotStoredException("No DigitalObjectManager found for URI " + pdURI, e);
		}
	}


	public boolean addDigitalObjectManager(String name, DigitalObjectManagerBase dom) {
		// Check the passed args to make sure that there are no nulls
		if ((name == null) || (name.length() == 0))
			throw new IllegalArgumentException("Argument String name cannot be null or empty");
		if (dom == null)
			throw new IllegalArgumentException("Argument DigitalObjectManagerBase dom must not be null");

		// OK we'll try a put to the set
		try {
			DataRegistryImpl.domSet.put(DataRegistryImpl.createDataRegistryIdFromName(name), dom);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int countDigitalObjectMangers() {
		return DataRegistryImpl.domSet.size();
	}

	public void deleteDigitalObjectManager(URI uri)  throws DigitalObjectManagerNotFoundException {
		try {
			if (DataRegistryImpl.domSet.remove(new PDURI(uri).formDataRegistryRootURI()) == null) {
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
	private static void findDigitalObjectManagers() {
		// OK, we need the config information to find where the DigitalObjectManger
		// config files are held, first we get the default directory
		URI firstDefaultWritable = null;
		URI firstUserWritable = null;
		try {
			if (DataRegistryImpl.defaultDomDirName != null)
				firstDefaultWritable = DataRegistryImpl.getDigitalObjectManagersFromDirectory(DataRegistryImpl.defaultDomDirName,
																			  DataRegistryImpl.DEFAULT_CONFIG_KEY);
		// We can catch no default problem, this should never happen
		} catch (NoSuchElementException e) {
			DataRegistryImpl.log.severe("No default directory for DOM config found, excpecting " +
										 DataRegistryImpl.DEFAULT_CONFIG_KEY + " property in config file.");
		}

		try {
			if (DataRegistryImpl.userDomDirName != null)
				firstUserWritable = DataRegistryImpl.getDigitalObjectManagersFromDirectory(DataRegistryImpl.userDomDirName,
																				DataRegistryImpl.USER_CONFIG_KEY);
		// OK this is not fatal, there may not be a user dir
		} catch (NoSuchElementException e) {
			DataRegistryImpl.log.info("No User Defined default DOM configuration directory found");
		}
		
		// If no default set
		if (DataRegistryImpl.defaultDomUri == null) {
			// Set to default writable if not null
			if (firstDefaultWritable != null) {
				DataRegistryImpl.defaultDomUri = firstDefaultWritable;
			// Else set to user writable
			} else if (firstUserWritable != null) {
				DataRegistryImpl.defaultDomUri = firstDefaultWritable;
			}
		}		
	}
	
	private static URI getDigitalObjectManagersFromDirectory(String dirName, String propName) {
		// We want to record the URI of the first writeable DOM we come to.
		// This will be our fall back for default DOM, it's also the ret val
		URI firstWriteable = null;
		
		// We need the directory
		File propDir = new File(dirName);

		// Check that this is a directory, if not log and return null URI
		// This should never happen as checked and shouldn't be called
		if (!propDir.isDirectory()) {
			log.info("Property " + propName + " is not a directory");
			return firstWriteable;
		}
		
		// Create a filter for properties files
		FilenameFilter sel = new FilenameFilter() {
			public boolean accept(File file, String name) {
				return name.endsWith(PROP_EXT);
			}
		};

		// Iterate through contained files 
		for (File propFile : propDir.listFiles(sel)) {
			// Get the properties set from the file
			Configuration config = ServiceConfig.getConfiguration(propFile);
				
			// Add the registry to the hash map, the Interface class is instantiated
			// from the class name
			DigitalObjectManagerBase dom = getDomBaseInstance(config);
			if (dom != null) {
				log.info("Adding DOM: " + config.getString(DigitalObjectManagerBase.NAME_KEY));
				DataRegistryImpl.domSet.put(dom.getId(), dom);
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
						(DataRegistryImpl.defaultDomUri == null)) {
					DataRegistryImpl.defaultDomUri = dom.getId();
				// No default candidate so we'll look for the first writeable
				} else if ((firstWriteable == null) &&
							dom.isWritable(dom.getId())) {
					firstWriteable = dom.getId();
				}
			}
		}
		return firstWriteable;
	}

	/**
	 * Just a little util method to get the default configuration folder for the data registry
	 * from its config property file
	 * 
	 * @return The String name of the DataRegistry default config dir
	 */
    private static boolean setUpDefaultFolders() {

    	boolean defaultsOK = false;
    	Configuration conf = ServiceConfig.getConfiguration("DataRegistry");

    	try {
    		DataRegistryImpl.defaultDomDirName = conf.getString(DataRegistryImpl.DEFAULT_CONFIG_KEY);

    		// If it's a directory we're OK 
    		if (new File(DataRegistryImpl.defaultDomDirName).isDirectory())
    			defaultsOK = true;
       		// If it's not a directory, once again not terminal, but erase the name
    		else
    			DataRegistryImpl.defaultDomDirName = null;
    	} catch (NoSuchElementException e) {
    		// No default directory found, unusual, so should log as severe but not a show stopper
    		DataRegistryImpl.log.severe("Config file for DataRegistry didn't contain a " + DataRegistryImpl.DEFAULT_CONFIG_KEY + " property.");
    	}

    	try {
    		DataRegistryImpl.userDomDirName = conf.getString(DataRegistryImpl.USER_CONFIG_KEY);

    		// If it's a directory we're OK 
    		if (new File(DataRegistryImpl.userDomDirName).isDirectory())
    			defaultsOK = true;
       		// If it's not a directory, once again not terminal, but erase the name
    		else
    			DataRegistryImpl.userDomDirName = null;
    		
    	} catch (NoSuchElementException e) {
    		// No user directory found, not unusual, so should log as info
    		DataRegistryImpl.log.info("Config file for DataRegistry didn't contain a " + DataRegistryImpl.USER_CONFIG_KEY + " property.");
    	}

    	// Belt & Braces return test
    	return (defaultsOK && 
    			((DataRegistryImpl.defaultDomDirName != null) ||
    			 (DataRegistryImpl.userDomDirName != null)));
    }

    /**
     * public method that instantiates and returns a DigitalObjectManager interface
     * from the implementation class name.  This is slightly evil.
     * @param className
     * @return
     */
    public static DigitalObjectManagerBase getDomBaseInstance(Configuration config) {
		// Empty Class and DigitalObjectManagerBase
		Class<?> domClass = null;
		DigitalObjectManagerBase dom = null;
		try {
			// OK we need a class name or we can't instatiate the instance
			String className = config.getString(DataRegistryImpl.CLASS_NAME_KEY);

			// Get the class name from the config and then the class from the contexts class loader
			log.info("Getting DigitalObjectManager class: " + className);
			domClass = Class.forName(className,	true,
					Thread.currentThread().getContextClassLoader());
			
			// Check that the class inherits from DigitalObjectManagerBase, if not we won't try to 
			// instantiate we just pass back null instead
			log.info("Checking " + className + " extends " + DigitalObjectManagerBase.class.getCanonicalName());
			if 	(domClass.getSuperclass().getName().equals(DigitalObjectManagerBase.class.getName())) {

				// Right we have a class that extends the right base, get the config constructor
				// and create the object
				log.info("Now we need a constructor that takes a: " 
						+ Configuration.class.getName() + " object");
				dom = (DigitalObjectManagerBase) domClass.getConstructor(
						new Class[] {Configuration.class}).newInstance(new Object[] {config});

			// OK so the DOM implementation doesn't extend DigitalObjectManagerBase
			// Log the error and move on
			} else {
				log.info("Class " + " doesn't extendend " + DigitalObjectManagerBase.class.getCanonicalName());
				log.info(DataRegistryImpl.class.getName() + " requires that DigitalObjectManagers extend this base");
			}
		// We don't want to stop if the creation of one DOM goes wrong
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			// The class name element doesn't exist so we can't instantiate
			log.info("No " + DataRegistryImpl.CLASS_NAME_KEY + " key for class name");
			log.info("Check the Digital Object Manager for configuration");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// This means that the underlying constructor threw an exception
			log.info("Exception thrown invoking constructor for DOM class");
			e.printStackTrace();
			log.info("Underlying construction exception follows");
			log.info(e.getCause().getMessage());
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
		log.info("getDescription() for " + uri);
		String desc = "";
		if (uri == null) {
			throw new DigitalObjectManagerNotFoundException("Cannot find DigitalObjectManger for null URI");
		}
		DigitalObjectManagerBase dom;
		try {
			dom = domSet.get(new PDURI(uri).formDataRegistryRootURI());
		} catch (URISyntaxException e) {
			throw new DigitalObjectManagerNotFoundException("Cannot find DigitalObjectManger for " + uri);
		}
		if (dom == null) {
			throw new DigitalObjectManagerNotFoundException("Cannot find DigitalObjectManger for " + uri);
		}
		desc = dom.getDescription();
		log.info("Description is " + desc);
		return desc;
	}

	public String getName(URI uri) throws DigitalObjectManagerNotFoundException {
		return domSet.get(uri).getName();
	}

	public DigitalObjectManager getDefaultDigitalObjectManager()
			throws DigitalObjectManagerNotFoundException {
		DigitalObjectManager dom = null;
		if (defaultDomUri != null) {
			dom = this.getDigitalObjectManager(defaultDomUri);
		}
		return dom;
	}

	/**
	 * Return the default URI
	 * 
	 * @return The default DOM URI
	 */
	public URI getDefaultDigitalObjectManagerId() {
		return defaultDomUri;
	}

	/**
	 * A convenient way of setting the default DOM by URI.  If we have this DOM
	 * then we'll make it the default, if not we retain the existing default.
	 * 
	 * @return The default DOM URI following this call
	 */
	public URI setDefaultDigitalObjectManagerId(URI uri) {
		if (this.hasDigitalObjectManager(uri))
			try {
				defaultDomUri = new PDURI(uri.normalize()).formDataRegistryRootURI();
			} catch (URISyntaxException e) {
				log.info("URI: " + uri + 
						" is not a valid Planets DataRegistry URI ID");
			}
			return defaultDomUri;
	}
		
	public static URI createDataRegistryIdFromName(String registryName) throws URISyntaxException {
		// OK let's check that the name isn't null
		if (registryName == null) 
			throw new IllegalArgumentException("Argument String registryName cannot be null");
		if (registryName.length() < 1) 
			throw new IllegalArgumentException("Argument String registryName cannot be empty");
		
		// Right we need the details for this server
		try {
			return PDURI.formDataRegistryRootURI(PlanetsServerConfig.getHostname(), 
											  String.valueOf(PlanetsServerConfig.getPort()),
											  registryName);
		} catch (NullPointerException e) {
			// Assume we're in test mode and return a test mode default
			return PDURI.formDataRegistryRootURI("testhost", "8080", registryName);
		}
		 
	}

	// Part of the TB hack
	private static DigitalObjectContent getObjectContentByRef(URI uri)
		throws MalformedURLException {
		DigitalObjectContent content = null;
		URI resolverURI = URI.create(TBContentResolver.getResolverPath() + uri).normalize();
		content = Content.byReference(resolverURI.toURL());
		return content;
	}
}

