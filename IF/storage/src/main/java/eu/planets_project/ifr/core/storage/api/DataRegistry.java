/**
 * 
 */
package eu.planets_project.ifr.core.storage.api;

import java.net.URI;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public interface DataRegistry extends DigitalObjectManager {
	public final static String DEFAULT_KEY = "manager.default";

	/**
	 * @param uri - the identifier of the DigitalObjectManger to be retrieved
	 * @return The DigitalObjectManager with identifier matching the uri param
	 * @throws DigitalObjectManagerNotFoundException
	 */
	public DigitalObjectManager getDigitalObjectManager(URI uri) throws DigitalObjectManagerNotFoundException;
	
	/**
	 * @return
	 * @throws DigitalObjectManagerNotFoundException
	 */
	public DigitalObjectManager getDefaultDigitalObjectManager() throws DigitalObjectManagerNotFoundException;

	/**
	 * @return
	 */
	public URI getDefaultDigitalObjectManagerId();
	
	/**
	 * @param uri
	 * @return
	 * @throws DigitalObjectManagerNotFoundException
	 */
	public String getName(URI uri) throws DigitalObjectManagerNotFoundException;

	/**
	 * @param uri
	 * @return
	 * @throws DigitalObjectManagerNotFoundException
	 */
	public String getDescription(URI uri) throws DigitalObjectManagerNotFoundException;
	/**
	 * @param uri - the identifier of the DigitalObjectManager to be queried
	 * @return true if the DataRegistry knows of DigitalObjectManger with identifier
	 * equal to uri, false otherwise
	 */
	public boolean hasDigitalObjectManager(URI uri);
	
	/**
	 * @return the number of DigitalObjectMangers that this DataRegistry knows about
	 */
	public int countDigitalObjectMangers();
	
	/**
	 * @param uri - the identifier of the DigitalObjectManager to be added
	 * @param dom - the DigitalObjectManager to be added
	 * @return true if added successfully, otherwise false
	 */
	public boolean addDigitalObjectManager(URI uri, DigitalObjectManagerBase dom);
	
	/**
	 * @param uri - the identifier of the DigitalObjectManager to be removed
	 * @throws DigitalObjectManagerNotFoundException 
	 */
	public void deleteDigitalObjectManager(URI uri) throws DigitalObjectManagerNotFoundException;

	/**
	 * Exception thrown when a DigitalObject requested by URI cannot be found in the Data Registry.
	 *
	 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
	 */
	public class DigitalObjectManagerNotFoundException extends Exception {
		
		/**
		 * UID for serialization 
		 */
		private static final long serialVersionUID = -7464160216555968213L;

		/**
		 * @param message
		 * 		The message for the exception
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectManagerNotFoundException(String message, Throwable excep) {
			super(message, excep);
		}
		
		/**
		 * @param message
		 * 		The message for the exception
		 */
		public DigitalObjectManagerNotFoundException(String message) {
			super(message);
		}
		
		/**
		 * @param excep
		 * 		The exception that prompted the creation of this exception
		 */
		public DigitalObjectManagerNotFoundException(Throwable excep) {
			super(excep);
		}
	}
}
