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
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author CFWilson
 */
public abstract class DigitalObjectManagerBase implements DigitalObjectManager {
	/** The logger instance */
    private static Logger log = Logger.getLogger(DigitalObjectManagerBase.class.getName());

	/** Public statics for the property names used to configure an instance */
	public final static String NAME_KEY = "manager.name";
	public final static String DESC_KEY = "manager.description";
	public final static String ID_KEY = "manager.identifier.uri";

	protected URI id = null;
	protected String name = null; 
	protected String description = "";

	@SuppressWarnings("unused")
	private DigitalObjectManagerBase(){/** */};

	protected DigitalObjectManagerBase(Configuration config) {
		try {
			URI id = config.getURI(ID_KEY);
			String name = config.getString(NAME_KEY);

			// We don't care if there's no description
			try {
				this.description = config.getString(DESC_KEY);
			} catch (NoSuchElementException e) {
				this.description = "";
			}
			this.checkConstructorArguments(id, name);
			this.id = id;
			this.name = name;
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException("Every DOM properties file must have a " +
					ID_KEY + " property for the URI id and a " +
					NAME_KEY + " property for a String name", e);
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
		throw new DigitalObjectNotFoundException("The Digital Object not found by " + DigitalObjectManagerBase.class.getName());
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
	
	protected void checkConstructorArguments(URI id, String name) throws IllegalArgumentException {
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
		
		// URI should be a valid PDURI
		try {
			if (id == null) {
				String message = "The supplied uri id should not be null";
				log.severe(message);
				throw new IllegalArgumentException(message);
			}
			PDURI pduri = new PDURI(id);
			if (!pduri.isDataRegistryURI()) {
				String message = "The supplied id should not be a valid Planets Data Registry id";
				log.severe(message);
				throw new IllegalArgumentException(message);
			}
		} catch (URISyntaxException e) {
			String message = "The supplied id should be a valid URI and a valid Planets Data Registry id";
			log.severe(message);
			throw new IllegalArgumentException(message);
		}
	}
}
