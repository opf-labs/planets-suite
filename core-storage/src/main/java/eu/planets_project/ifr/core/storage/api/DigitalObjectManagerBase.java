/**
 * 
 */
package eu.planets_project.ifr.core.storage.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author CFWilson
 */
public abstract class DigitalObjectManagerBase implements DigitalObjectManager {
	/** The logger instance */
    private static Logger log = Logger.getLogger(DigitalObjectManagerBase.class.getName());

	/** Public static for the property name used to name an instance */
	public final static String NAME_KEY = "manager.name";
	/** Public static for the property name used to describe an instance */
	public final static String DESC_KEY = "manager.description";

	protected URI id = null;
	protected String name = null; 
	protected String description = "";

	@SuppressWarnings("unused")
	private DigitalObjectManagerBase(){/** We don't want no arg constructors*/}

	protected DigitalObjectManagerBase(Configuration config) {
		try {
			String nam = config.getString(NAME_KEY);

			// We don't care if there's no description
			try {
				this.description = config.getString(DESC_KEY);
			} catch (NoSuchElementException e) {
				this.description = "";
			}
			this.checkConstructorArguments(nam);
			this.id = DataRegistryFactory.createDataRegistryIdFromName(nam).normalize();
			this.name = nam;
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException("Every DOM properties file must have a " +
					NAME_KEY + " property for a String name", e);
		} catch (URISyntaxException e) {
			String message = "The supplied name should be valid as part of a URI Planets Data Registry ID";
			log.severe(message);
			throw new IllegalArgumentException(message, e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
	 */
	public List<Class<? extends Query>> getQueryTypes() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
	 */
	public boolean isWritable(URI pdURI) {
		// This defaults to false for non-writable DOMs
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
	 */
	abstract public List<URI> list(URI pdURI);

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
	 */
	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        throw new QueryValidationException("This implementation does not support queries.");
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		throw new DigitalObjectNotFoundException("The Digital Object not found by " + DigitalObjectManagerBase.class.getName());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI storeAsNew(DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("The Digital Object not stored by " + DigitalObjectManagerBase.class.getName());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		throw new DigitalObjectNotStoredException("The Digital Object not stored by " + DigitalObjectManagerBase.class.getName());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	public URI updateExisting(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException,
			DigitalObjectNotFoundException {
		// This will throw not found if it can't be found
		this.retrieve(pdURI);
		// Throw not stored as it's not implemented
		throw new DigitalObjectNotStoredException("The Digital Object not stored by " + DigitalObjectManagerBase.class.getName());
	}

	/**
	 * Getter for the String name
	 * @return the name of the DOM
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter for the URI identifier
	 * @return the URI identifier
	 */
	public URI getId() {
		return this.id;
	}

	/**
	 * @return the String description
	 */
	public String getDescription() {
		return this.description;
	}
	
	protected void checkConstructorArguments(String name) throws IllegalArgumentException {
		// Name should not be null or empty
		if (name == null) {
			String message = "The supplied name should not be null";
			log.severe(message);
			throw new IllegalArgumentException(message);
		} else if (name.length() < 1) {
			String message = "The supplied name should not be empty";
			log.severe(message);
			throw new IllegalArgumentException(message);
		}
		
		// Name should also be a part of a valid URI
		try {
			DataRegistryImpl.createDataRegistryIdFromName(name);
		} catch (URISyntaxException e) {
			String message = "The supplied name should be valid as part of a URI Planets Data Registry ID";
			log.severe(message);
			throw new IllegalArgumentException(message, e);
		}
	}
}
