package eu.planets_project.ifr.core.techreg.api.formats;

import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
public interface FormatRegistry {

    /* Query methods: */

    /**
     * @param query The query
     * @return the List of URIs matching query
     */
    List<URI> search(String query);

    /**
     * @param extension The extension
     * @return the Set of URIs for the passed extension
     */
    Set<URI> getUrisForExtension(String extension);

    /**
     * @param mime The mime type
     * @return the Set of URIs for the passed mimetype
     */
    Set<URI> getUrisForMimeType(String mime);

    /**
     * This class looks up the different Format URIs consistent with the given
     * URI.
     * @param typeUri The URI
     * @return a List of format URIs consistent with the passed URI
     */
    List<URI> getFormatUriAliases(URI typeUri);

    /**
     * @param uri The URI to find extensions for
     * @return Extensions corresponding to the given URI
     */
    Set<String> getExtensions(URI uri);

    /**
     * @param uri The URI to find an extension for
     * @return The first extension found corresponding to the given URI
     */
    String getFirstExtension(URI uri);

    /* Planets URI factory methods: */

    /**
     * @param extension The simple file extension
     * @return A URI representing the extension
     */
    URI createExtensionUri(String extension);

    /**
     * @param pronom The pronom ID to create URI for, e.g. "fmt/101"
     * @return A URI representing the given pronom ID
     */
    URI createPronomUri(String pronom);

    /**
     * @param mime The mime type to create a URI for
     * @return A URI representing the given mime type
     */
    URI createMimeUri(String mime);

    /* Planets URI info methods: */

    /**
     * @param uri The URI to check
     * @return True, if the given URI is an extension URI (represents a simple
     *         extension, e.g. 'txt')
     */
    Boolean isExtensionUri(URI uri);

    /**
     * @param uri The URI to check
     * @return True, if the given URI is a pronom URI (represents a promon ID,
     *         e.g. 'fmt/10')
     */
    Boolean isPronomUri(URI uri);

    /**
     * @param uri The URI to check
     * @return True, if the given URI is a mime URI (represents a mime type,
     *         e.g. 'text/plain')
     */
    Boolean isMimeUri(URI uri);

    /* Conversion back from Planets URIs: */

    /**
     * @param uri A URI representing a Pronom ID
     * @return The Pronom ID, e.g. "fmt/101"
     */
    String convertUriToPronom(URI uri);
}
