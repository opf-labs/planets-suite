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
     * Persist a DigitalObject to the Data Registry as a new object
     *
     * @param digitalObject The object to store
     * @return the pdURI where the object was stored.
     * @throws DigitalObjectNotStoredException  if the storing somehow failed
     */
    public URI storeAsNew(DigitalObject digitalObject) throws DigitalObjectNotStoredException;
    
    /**
     * Persist a DigitalObject to the Data Registry as a new object, associated the suggested URI if possible.
     * 
     * If a particular implementation does not permit the caller to dictate the store layout, the URI may be overridden.
     * Consequently, the returned URI should be retained as the object reference, not the passed one.
     *
     * @param pdURI The suggested URI to associate with the stored object.
     * @param digitalObject The object to store.
     * @return the pdURI A URI associated with the newly-stored object and which could be used to recover it via the .read(URI) method.
     * @throws DigitalObjectNotStoredException  if the storing somehow failed
     */
    public URI storeAsNew( URI pdURI, DigitalObject digitalObject ) throws DigitalObjectNotStoredException;

    /**
     * Updates an existing object in the repository to contain the given digital object. The repository might create a
     * new object for the new version. This method returns the uri of the updated object.
     * @param pdURI the object to update
     * @param digitalObject the information to update with
     * @return the id of the updated object
     * @throws DigitalObjectNotStoredException if the storing somehow failed
     * @throws DigitalObjectNotFoundException 
     */
    public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException, DigitalObjectNotFoundException;

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
	 * Implementations of this method are currently expected to set the Title of the DigitalObject
	 * to a sensible filename, as this information may be used to write the data to temporary storage systems.
	 * 
	 * TODO Perhaps remove the above, as persisted files should really use the leafname from the URI?
	 * 
	 * @param pdURI
	 *            URI that uniquely identifies the DigitalObject
	 * @return the DigitalObject retrieved from the registry
	 * @throws DigitalObjectNotFoundException If the digital object with this URI is not found in the repository
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException;

	/**
	 * @return An array of the types of query that are supported. If your interface does not support queries, please return null.
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
     * @return the List of Digital Object URIs that matched the query
     * @throws eu.planets_project.ifr.core.storage.api.query.QueryValidationException if the query does not make sense
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
	/**
	 * Exception thrown when a DigitalObject cannot be removed in the Data Registry.
	 */
	public class DigitalObjectNotRemovedException extends Exception {
		static final long serialVersionUID = 1469131144643980203L;

		/**
		 * @param message
		 * 		The message for the exception
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectNotRemovedException(String message, Throwable excep) {
			super(message, excep);
		}
		
		/**
		 * @param message
		 * 		The message for the exception
		 */
		public DigitalObjectNotRemovedException(String message) {
			super(message);
		}
		
		/**
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectNotRemovedException(Throwable excep) {
			super(excep);
		}
	}
	
	/**
	 * Exception thrown when a DigitalObject is too large.
	 */
	public class DigitalObjectTooLargeException extends Exception 
	{
		static final long serialVersionUID = 1469131144643980204L;

		/**
		 * @param message
		 * 		The message for the exception
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectTooLargeException(String message, Throwable excep) {
			super(message, excep);
		}
		
		/**
		 * @param message
		 * 		The message for the exception
		 */
		public DigitalObjectTooLargeException(String message) {
			super(message);
		}
		
		/**
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectTooLargeException(Throwable excep) {
			super(excep);
		}
	}	

	/**
	 * Exception thrown when a DigitalObject cann't be updated in JCR.
	 */
	public class DigitalObjectUpdateException extends Exception 
	{
		static final long serialVersionUID = 1469131144643980205L;

		/**
		 * @param message
		 * 		The message for the exception
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectUpdateException(String message, Throwable excep) {
			super(message, excep);
		}
		
		/**
		 * @param message
		 * 		The message for the exception
		 */
		public DigitalObjectUpdateException(String message) {
			super(message);
		}
		
		/**
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectUpdateException(Throwable excep) {
			super(excep);
		}
	}	
}
