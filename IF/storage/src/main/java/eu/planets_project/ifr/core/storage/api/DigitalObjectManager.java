package eu.planets_project.ifr.core.storage.api;

import java.net.URI;

import eu.planets_project.services.datatypes.DigitalObject;

/**
 * Interface for storage and retrieval of Digital Objects in an IF Data Registry
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 * 
 */
public interface DigitalObjectManager {
	/**
	 * Persist a DigitalObject to the Data Registry
	 * 
	 * @param pdURI
	 *            The URI which uniquely identifies the persisted DigitalObject
	 * @param digitalObject
	 * @throws DigitalObjectNotStoredException 
	 */
	public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException;

	/**
	 * Retrieve a DigitalObject from the DataRegistry
	 * 
	 * @param pdURI
	 *            URI that uniquely identifies the DigitalObject
	 * @return the DigitalObject retrieved from the registry
	 * @throws DigitalObjectNotFoundException 
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException;

	/**
	 * 
	 * @param pdURI
	 *            URI that identifies an Digital Object or folder
	 * @return an array of all child URIs
	 */
	public URI[] list(URI pdURI);

	/**
	 * Exception thrown when a DigitalObject requested by URI cannot be found in the Data Registry
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
	 * Exception thrown when a DigitalObject cannot be stored in the Data Registry 
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
