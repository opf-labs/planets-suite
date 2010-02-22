/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.memory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author CFWilson
 *
 */
public class InMemoryDigitalObjectManagerImpl extends DigitalObjectManagerBase {
	private static Logger log = Logger.getLogger(InMemoryDigitalObjectManagerImpl.class.getName());

	/**
	 * OK a two HashMaps here, they hold the same info but are supposed to make lookup easy:
	 * 		childMap holds the links between "folders" and a list of their children
	 * 		objectMap holds the URI object map for store and retrieve
	 */
	// private HashMap<URI, URI> parentMap = new HashMap<URI, URI>();
	private HashMap<URI, ArrayList<URI>> childMap = new HashMap<URI, ArrayList<URI>>();
	private HashMap<URI, DigitalObject> objectMap = new HashMap<URI, DigitalObject>();

	/**
	 * @param config
	 */
	public InMemoryDigitalObjectManagerImpl(Configuration config) {
		// Nothing to do but call the "super" constructor
		super(config);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#isWritable(java.net.URI)
	 */
	@Override
	public boolean isWritable(URI pdURI) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
	 */
	@Override
	public List<URI> list(URI pdURI) {
		// The return array of URIs contains the contents for the passed URI
		ArrayList<URI> retrievedChildren = null;

		// If pdURI null then we need to return the root 
		if (pdURI == null)
		{
			log.info("URI is null so return root URI only");
			retrievedChildren = new ArrayList<URI>();
			retrievedChildren.add( this.id ); 
			return retrievedChildren; 
		}

		// pdURI is not null so return the child set
		return this.childMap.get(pdURI.normalize());
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	@Override
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		// OK, let's look it up in the HashMap
		DigitalObject retrievedObject = this.objectMap.get(pdURI.normalize());
		
		// We need to check the null
		if (retrievedObject == null) 
			throw new DigitalObjectNotFoundException("The Digital Object not found by " + this.getName());
		
		// OK not null so let's return it
		return retrievedObject;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(eu.planets_project.services.datatypes.DigitalObject)
	 */
	@Override
	public URI storeAsNew(DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        URI pdURI = null;
        System.out.println("Putting URI together");
        System.out.println("ID = " + this.id);
		pdURI = this.id.resolve(this.name + "/" + UUID.randomUUID().toString());
		System.out.println("STORE AS NEW resolved URI = " + pdURI);
        return this.store(pdURI, digitalObject);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	@Override
	public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
        return this.store(pdURI, digitalObject);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#updateExisting(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	 */
	@Override
	public URI updateExisting(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException,
			DigitalObjectNotFoundException {
		// OK let's see if this exists, if it doesn't throw not found
		if (!this.objectMap.containsKey(pdURI)) 
			throw new DigitalObjectNotFoundException("DigitalObject " + pdURI + " not found");

		// As it exists then delete it and return the store result
		this.objectMap.remove(pdURI);
        return this.store(pdURI, digitalObject);
	}

	private URI store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
		// Test that the passed params aren't null
		// OK we check that URI is not null
		if (pdURI == null)
			throw new DigitalObjectNotStoredException("The URI passed was null");
		// OK we check that the passed DO is not null
		if (digitalObject == null)
			throw new DigitalObjectNotStoredException("The digitalObject passed was null");
		
		// If the object exists we throw an error
		if ((this.objectMap.containsKey(pdURI.normalize())) || 
				(this.childMap.containsKey((pdURI.normalize())))) {
					throw new DigitalObjectNotStoredException("The digitalObject at " + pdURI +
							"already exists");
		}

		// This used to return the stored location
		URI storeLocation = null;
		
		// OK, if this has a folder part we need the path
		// get the path array from the URI to store at
		log.info("Getting new PDURI from " + pdURI);
		PDURI _parsedURI;
		try {
			// URI used to build folder ids as we go
			_parsedURI = new PDURI(pdURI);
			
			// Get the path parts
			String[] decodedPath =_parsedURI.getPathParts(); 

			// Save the URI context from "root", and a variable for the last element
			URI contextURI = this.id.normalize();
			
			// Iterate through the path elements
			for (String pathElement : decodedPath) {
				// OK Save the parent URI
				URI parentLocation = contextURI.normalize();
				
				// Set up the child URI
				URI childLocation = new URI(parentLocation.toString() + "/" + pathElement).normalize();
			
				// Let's see if there's parent location, if it doesn't create it in the map
				// It's new so it has no children but the next item
				if (!this.childMap.containsKey(parentLocation)) {
					ArrayList<URI> oneChild = new ArrayList<URI>();
					oneChild.add(childLocation);
					this.childMap.put(parentLocation, oneChild);
				} else {
					// OK the parent is in here, let's add the child URI
					this.childMap.get(parentLocation).add(childLocation);
				}
				// Set the context to the childLocation
				contextURI = childLocation.normalize();
			}
			
			// OK let's add the object
			this.objectMap.put(contextURI, digitalObject);
			storeLocation = contextURI;

		} catch (URISyntaxException e) {
			throw new DigitalObjectNotStoredException("The URI " + pdURI + " doesn't appear to be valid", e);
		}

		return storeLocation;
	}
}
