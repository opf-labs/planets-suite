package eu.planets_project.ifr.core.wdt.impl.data;

import java.net.URI;
import java.net.URISyntaxException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

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
    private static PlanetsLogger log = PlanetsLogger.getLogger(DigitalObjectReference.class);
    
    // The Planets URI to which this description refers.
    private URI puri = null;
    
    // The nature of this item, directory or file:
    private boolean directory = false;
    
    // Constructor from URI:
    public DigitalObjectReference( URI puri ) {
    	this.puri = puri;
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
        	url = "http://ubuntu.planets-project.arcs.ac.at/" + url.substring(url.indexOf("bl-newspaper"));
        	System.out.println("URL after: " + url);
        	try {
        		return new URI(url);
        	} catch (URISyntaxException e) {
        		System.out.println("SHOULD NEVER HAPPEN!");
        	}
        	return puri;
    	} else {
    		return puri;
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
