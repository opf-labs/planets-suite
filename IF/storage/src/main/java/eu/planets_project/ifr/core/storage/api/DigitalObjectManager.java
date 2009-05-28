package eu.planets_project.ifr.core.storage.api;

import java.net.URI;
import java.util.List;

import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Interface for storage and retrieval of Digital Objects in an IF Data Registry.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 * 
 */
public interface DigitalObjectManager {
	/**
	 * Persist a DigitalObject to the Data Registry.
	 * 
	 * @param pdURI
	 *            The URI which uniquely identifies the persisted DigitalObject
	 * @param digitalObject
	 * @throws DigitalObjectNotStoredException 
	 */
	public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException;

	/**
	 * Test if Digital Objects can be persisted.
	 * 
	 * @param pdURI The URI that we wish to write to, or NULL to test if the whole repository is read-only.
	 * @return false if the 'store' method should work for this URI. If 
	 * the whole DOM is a read-only system, then return false when pdURI == NULL.
	 * If some parts are writable, return true when pdURI == NULL.
	 */
    public boolean isWritable( URI pdURI );
	
    /**
     * Returns the URIs of Digital Objects matching the given parent pdURI.
     * 
     * If the pdURI points to a 'file' (A Digital Object)
     * then this should return null.
     * 
     * If the pdURI points to a 'directory', then this should return a valid List object, with zero or more entries.
     * 
     * If the Query has been set, only return matching Digital Objects.
     * 
     * @param pdURI
     *            URI that identifies an Digital Object or folder
     * @return an array of all child URIs.  Empty folders return an empty list, and files return null.
     */
    public List<URI> list(URI pdURI);

	/**
	 * Retrieve a DigitalObject from the DataRegistry.
	 * 
	 * @param pdURI
	 *            URI that uniquely identifies the DigitalObject
	 * @return the DigitalObject retrieved from the registry
	 * @throws DigitalObjectNotFoundException 
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException;

	/**
	 * If your interface does not support queries, please return null.
	 * @return An array of the types of query that are supported.
	 */
	public List<Class<? extends Query>> getQueryTypes();

	/**
	 * Execute a more complex query, at some point in the URI tree.
	 * If the query does not make sense, throw an exception.
	 * Ideally, include a message in said exception that can be shown to the 
	 * user so that they might improve their query.
	 * 
	 * @param pdURI The URI in the repository at which the query should be executed.  Can be null, meaning at the top-level.
	 * @param q The Query to be executed.
	 */
	public List<URI> list( URI pdURI, Query q ) throws QueryValidationException;
	
	/**
	 * Exception thrown when a DigitalObject requested by URI cannot be found in the Data Registry.
	 *
	 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
	 */
	public class DigitalObjectNotFoundException extends Exception {
		static final long serialVersionUID = 3120789461926213247L;
		
		/**
		 * @param message
		 * 		The message for the exception
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectNotFoundException(String message, Throwable excep) {
			super(message, excep);
		}
		
		/**
		 * @param message
		 * 		The message for the exception
		 */
		public DigitalObjectNotFoundException(String message) {
			super(message);
		}
		
		/**
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectNotFoundException(Throwable excep) {
			super(excep);
		}
	}
	
	/**
	 * Exception thrown when a DigitalObject cannot be stored in the Data Registry.
	 *
	 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
	 */
	public class DigitalObjectNotStoredException extends Exception {
		static final long serialVersionUID = 1469131144643980203L;

		/**
		 * @param message
		 * 		The message for the exception
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectNotStoredException(String message, Throwable excep) {
			super(message, excep);
		}
		
		/**
		 * @param message
		 * 		The message for the exception
		 */
		public DigitalObjectNotStoredException(String message) {
			super(message);
		}
		
		/**
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectNotStoredException(Throwable excep) {
			super(excep);
		}
	}
}
