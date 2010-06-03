/**
 * This is a class that wraps a Planets Digital Object.
 * It is usually associated with a Planets URI, but the data may be embedded.
 * It may be a single file, or a directory or other collection.
 */
package eu.planets_project.ifr.core.wdt.api.data;

import java.net.URI;

import java.util.logging.Logger;

/**
 * @author AnJackson
 * deprecated
 */
public class DigitalObject {
    // A logger for this:
    @SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(DigitalObject.class.getName());
    
    // The Planets URI to which this description refers.
    private URI puri = null;
    
    // The nature of this item, directory or file:
    private boolean directory = false;
    
    // Constructor from URI:
    public DigitalObject( URI puri ) {
        this.puri = puri;
    }

    /**
     * @return the puri
     */
    public URI getUri() {
        return puri;
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
        String path = puri.getPath();
        if( path == null ) return "";
        // Trim any trailing slash:
        if( path.lastIndexOf("/") == path.length()-1 ) {
            path = path.substring(0, path.length()-1 );
        }
        // Return the portion up to the last slash:
        return path.substring( path.lastIndexOf('/') + 1 );
    }

}
