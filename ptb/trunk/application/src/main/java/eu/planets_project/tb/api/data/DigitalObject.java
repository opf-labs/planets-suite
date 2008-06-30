/**
 * This is a class that wraps a Planets Digital Object.
 * It is usually associated with a Planets URI, but the data may be embedded.
 * It may be a single file, or a directory or other collection.
 */
package eu.planets_project.tb.api.data;

import java.net.URI;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.gui.backing.FileBrowser;

/**
 * @author AnJackson
 *
 */
public class DigitalObject {
    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(DigitalObject.class, "testbed-log4j.xml");
    
    // The Planets URI to which this description refers.
    private URI puri = null;
    
    // The nature of this item, directory or file:
    private boolean directory = false;
    
    // One or more binary blobs that hold the file information instead of referencing PURLs:
    private byte[][] binary = null;
    
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
        final DigitalObject other = (DigitalObject) obj;
        if (puri == null) {
            if (other.puri != null)
                return false;
        } else if (!puri.equals(other.puri))
            return false;
        return true;
    }

    
    
}
