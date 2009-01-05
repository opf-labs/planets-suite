/**
 * 
 */
package eu.planets_project.ifr.core.techreg.api.formats;

import java.net.URI;
import java.util.Set;
import java.util.List;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface FormatRegistry {

    /**
     * 
     * @param puri
     * @return a format object for the passed URI
     */
    public abstract Format getFormatForURI(URI puri);
    
    /**
     * 
     * @param ext
     * @return the Set of URIs for the passed extension
     */
    public abstract Set<URI> getURIsForExtension(String ext);

    /**
     * 
     * @param mimetype
     * @return the Set of URIs for the passed mimetype
     */
    public abstract Set<URI> getURIsForMimeType(String mimetype);
    
    /**
     * 
     * @param query
     * @return the List of URIs matching query 
     */
    public abstract List<URI> search( String query );

    /**
     * This class looks up the different Format URIs consistent with the given URI.
     * 
     * @param typeURI
     * @return a List of format URIs consistent with the passed URI
     */
    public abstract List<URI> getFormatURIAliases( URI typeURI );

    /**
     * 
     * @param typeURI
     * @return a List of Format objects consistent with the passed URI
     */
    public abstract List<Format> getFormatAliases( URI typeURI );
    
}