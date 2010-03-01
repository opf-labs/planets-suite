package eu.planets_project.ifr.core.wdt.impl.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRConstants;
import eu.planets_project.ifr.core.storage.impl.jcr.DOJCRManager;
import eu.planets_project.services.datatypes.DigitalObject;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;

/**
 * 
 * This actually a reference to a digital object (URI), and does not contain an actual Digital Object.
 * This is used for walking the tree of stored data, and the DigitalObjectManager is used to actually retrieve the thing.
 * 
 * @author AnJackson
 *
 */
public class DigitalObjectReference {
    // A logger for this:
    private static Logger log = Logger.getLogger(DigitalObjectReference.class.getName());
    
    // The Planets URI to which this description refers.
    private URI puri = null;
    
    // The nature of this item, directory or file:
    private boolean directory = false;
    
    // The digital object manager to retrieve additional digital object properties from JCR
    private DataRegistry dom;
    
    // Constructor from URI:
    public DigitalObjectReference( URI puri ) {
    	this.puri = puri;
    }

    /**
     * Constructor with permanent URI and digital object manager
     * 
     * @param puri
     *        This is a permanent URI of the digital object
     * @param _dom
     *        This is a data registry instance used to retrieve additional 
     *        digital object properities
     */
    public DigitalObjectReference( URI puri, DataRegistry _dom ) 
    {
    	this.puri = puri;
    	dom = _dom;
    }
    
    /**
     * @return the puri
     */
    public URI getUri() {
    	return puri;
    }
    
    public URI getScreenUri() {
    	// Special treatment for BL newspapers
    	System.out.println("Returning URI...");
    	if ((puri != null) && (puri.toString().indexOf("jboss-web.deployer/ROOT.war/bl-newspaper/WO1") > -1)) {
    		String url = puri.toString();
        	System.out.println("URL before: " + url);
        	// FIXME
        	url = "http://ubuntu.planets-project.arcs.ac.at/" + url.substring(url.indexOf("bl-newspaper"));
        	System.out.println("URL after: " + url);
        	try {
        		return new URI(url);
        	} catch (URISyntaxException e) {
        		System.out.println("SHOULD NEVER HAPPEN!");
        	}
        	return puri;
    	} else {
        	if ((puri != null) && (puri.toString().indexOf(DOJCRConstants.DOJCR) > -1)) 
        	{
            	try {
            		return new URI(DOJCRManager.getResolverPath() + puri.toString());
            	} catch (URISyntaxException e) {
            		log.log(Level.INFO, "SHOULD NEVER HAPPEN!", e);
            	}
            	return puri;
        	} else {
    		return puri;
        	}
    	}
    }

    /**
     * @param puri the puri to set
     */
    public void setUri(URI puri) {
        this.puri = puri;
    }
    

    /**
     * TODO This should be determined by this class, on demand.
     * @return the directory
     */
    public boolean isDirectory() {
        return directory;
    }


    /**
     * FIXME This should not be necessary - this class should be able to resolve itself and find out.
     * @param directory the directory to set
     */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
    

    /**
     * Helper function returns the leaf name:
     * @return
     */
    public String getLeafname() {
        if( puri == null ) return "";
        
        String path;
        if (puri.toString().indexOf("DeliveryManager?pid=") > -1) {
        	// Special treatment for ONB files!
        	path = puri.toString();
        	path = path.substring(path.indexOf("DeliveryManager?pid=") + 20) + ".tif";
        } else {
        	path = puri.getPath();
        }
        
		log.log(Level.INFO,
				"DigitalObjectReference do perm uri: " + puri + " index: "
						+ puri.toString().indexOf(DOJCRManager.PERMANENT_URI));
        // if it is a digital object from JCR repository
		if (puri.toString().indexOf(DOJCRManager.PERMANENT_URI) > -1
				&& puri.toString().indexOf(DOJCRManager.PERMANENT_URI) == 0) {
			if (puri.toString().equals(DOJCRManager.PERMANENT_URI)) return path;
			// Special treatment for digital object presentation
			if (dom != null) {
				try { 
					DigitalObject obj = dom.getDigitalObjectManager(
							DataRegistryFactory.createDataRegistryIdFromName(DOJCRConstants.REGISTRY_NAME)).retrieve(puri);

					if (obj.getTitle() != null)
					{
						String title = obj.getTitle();
						path = path.concat("_" + title);
					}
				} catch (Exception e) {
					log.log(Level.INFO,
							"DigitalObjectReference title not found. "
									+ e.getMessage(), e);
				}
			}
		} 

        if( path == null ) return "";
        
        // Trim any trailing slash:
        if( path.lastIndexOf("/") == path.length()-1 ) {
            path = path.substring(0, path.length()-1 );
        }
        
        // Return the portion up to the last slash:
        return path.substring( path.lastIndexOf('/') + 1 );
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((puri == null) ? 0 : puri.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DigitalObjectReference other = (DigitalObjectReference) obj;
        if (puri == null) {
            if (other.puri != null)
                return false;
        } else if (!puri.equals(other.puri))
            return false;
        return true;
    }

}
