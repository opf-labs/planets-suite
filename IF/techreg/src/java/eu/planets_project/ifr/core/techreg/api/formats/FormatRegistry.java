/**
 * 
 */
package eu.planets_project.ifr.core.techreg.api.formats;

import java.net.URI;
import java.util.Set;
import java.util.List;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public interface FormatRegistry {

    /**
     * @param puri
     * @return a format object for the passed URI
     */
    Format getFormatForURI(URI puri);

    /**
     * @param ext
     * @return the Set of URIs for the passed extension
     */
    Set<URI> getURIsForExtension(String ext);

    /**
     * @param mimetype
     * @return the Set of URIs for the passed mimetype
     */
    Set<URI> getURIsForMimeType(String mimetype);

    /**
     * @param query
     * @return the List of URIs matching query
     */
    List<URI> search(String query);

    /**
     * This class looks up the different Format URIs consistent with the given
     * URI.
     * @param typeURI
     * @return a List of format URIs consistent with the passed URI
     */
    List<URI> getFormatURIAliases(URI typeURI);

    /**
     * @param typeURI
     * @return a List of Format objects consistent with the passed URI
     */
    List<Format> getFormatAliases(URI typeURI);

    /**
     * @param puid The Pronom ID, e.g. "fmt/101"
     * @return A URI representing the given Pronom ID
     */
    URI puidToUri(String puid);

    /**
     * @param uri A URI representing a Pronom ID
     * @return The Pronom ID, e.g. "fmt/101"
     */
    String uriToPuid(URI uri);

}