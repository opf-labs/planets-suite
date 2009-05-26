/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.techreg.formats.Format.UriType;

/**
 * This is the Planets Format Registry and Resolver.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@Stateless(mappedName = "planets-project.eu/FormatRegistry")
// @Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
@Local(FormatRegistry.class)
@LocalBinding(jndiBinding = "planets-project.eu/FormatRegistry/local")
@Remote(FormatRegistry.class)
@RemoteBinding(jndiBinding = "planets-project.eu/FormatRegistry/remote")
class FormatRegistryImpl implements FormatRegistry {
    private static Log log = LogFactory.getLog(FormatRegistryImpl.class);

    /**
     * Main index, 1-2-1 mapping the type URIs to the FileFormat objects.
     */
    private Map<URI, Format> uriMap = new HashMap<URI, Format>();

    /**
     * Map a file extension onto one or more type URIs.
     */
    private Map<String, Set<URI>> extMap = new HashMap<String, Set<URI>>();

    /**
     * Map a mime type onto one or more type URIs.
     */
    private Map<String, Set<URI>> mimeMap = new HashMap<String, Set<URI>>();

    /**
     * Constructor loads the format data in and builds the look-up tables.
     */
    public FormatRegistryImpl() {
        // Build up a set of Format objects from known information sources.
        // Currently, this is just DROID.
        DroidFormatRegistry dfr = new DroidFormatRegistry();
        Set<Format> ffs = dfr.getFormats();
        log.info("File format data loaded.");

        // Set up the hash tables that index this set of formats:
        for (Format ff : ffs) {
            // log.debug("--------------------------\nGot format "+
            // ff.getSummary());

            // Store the format in a PUID map:
            uriMap.put(ff.getTypeURI(), ff);
            // log.debug("Stored under PUID: "+ff.getTypeURI());

            // Store the mime mapping:
            if (ff.getMimeTypes() != null) {
                for (String mimeType : ff.getMimeTypes()) {
                    Set<URI> mimeSet = mimeMap.get(mimeType);
                    if (mimeSet == null) {
                        mimeSet = new HashSet<URI>();
                    }
                    mimeSet.add(ff.getTypeURI());
                    if (ff.getAliases() != null) {
                        for (URI furi : ff.getAliases()) {
                            mimeSet.add(furi);
                        }
                    }
                    mimeMap.put(mimeType, mimeSet);
                    // log.debug("Referenced under MIME: "+mimeType);
                }
            }
            // Store the extension mapping:
            if (ff.getExtensions() != null) {
                for (String ext : ff.getExtensions()) {
                    Set<URI> extSet = extMap.get(ext);
                    if (extSet == null) {
                        extSet = new HashSet<URI>();
                    }
                    extSet.add(ff.getTypeURI());
                    if (ff.getAliases() != null) {
                        for (URI furi : ff.getAliases()) {
                            extSet.add(furi);
                        }
                    }
                    extMap.put(ext, extSet);
                    // log.debug("Referenced under extension: "+ext);
                }
            }
        }
        log.info("File format look-up tables complete.");
    }

    /**
     * @param puri The Planets URI (see FormatRegistry)
     * @return A format instance for the given URI
     */
    public Format getFormatForURI(URI puri) {
        if (isMimeUri(puri) || isExtensionUri(puri)) {
            return new Format(puri);
        } else {
            return uriMap.get(puri);
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getUrisForExtension(java.lang.String)
     */
    public Set<URI> getUrisForExtension(String ext) {
        ext = ext.toLowerCase();
        return extMap.get(ext);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getUrisForMimeType(java.lang.String)
     */
    public Set<URI> getUrisForMimeType(String mimetype) {
        return mimeMap.get(mimetype);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#search(java.lang.String)
     */
    public List<URI> search(String query) {
        ArrayList<URI> found = new ArrayList<URI>(this
                .getUrisForExtension(query));
        Collections.sort(found);
        return found;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getFormatUriAliases(java.net.URI)
     */
    public List<URI> getFormatUriAliases(URI typeURI) {
        Set<URI> turis = new HashSet<URI>();
        turis.add(typeURI);

        if (isMimeUri(typeURI)) {
            Format mime = new Format(typeURI);
            Set<URI> furis = getUrisForMimeType(mime.getMimeTypes().iterator()
                    .next());
            turis.addAll(furis);
        } else if (isExtensionUri(typeURI)) {
            Format ext = new Format(typeURI);
            Set<URI> furis = getUrisForExtension(ext.getExtensions().iterator()
                    .next());
            turis.addAll(furis);
        } else {
            // This is a known format, ID, so add it, any aliases, and the ext
            // and mime forms:
            Format f = uriMap.get(typeURI);
            // Aliases:
            for (URI uri : f.getAliases()) {
                turis.add(uri);
            }
            // Ext:
            for (String ext : f.getExtensions()) {
                turis.add(createExtensionUri(ext));
            }
            // Mime:
            for (String mime : f.getMimeTypes()) {
                turis.add(createMimeUri(mime));
            }
        }
        return new ArrayList<URI>(turis);
    }

    /**
     * @param typeURI The type URI
     * @return All aliases (in other format types) for the given URI
     */
    public List<Format> getFormatAliases(URI typeURI) {
        List<Format> fmts = new ArrayList<Format>();
        for (URI furi : getFormatUriAliases(typeURI)) {
            fmts.add(getFormatForURI(furi));
        }
        return fmts;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#puidToUri(java.lang.String)
     */
    public URI puidToUri(final String puid) {
        return DroidFormatRegistry.PUIDtoURI(puid);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#convertUriToPronom(java.net.URI)
     */
    public String convertUriToPronom(final URI uri) {
        return DroidFormatRegistry.URItoPUID(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createExtensionUri(java.lang.String)
     */
    public URI createExtensionUri(String extensionFromFile) {
        return FormatUtils.createExtensionUri(extensionFromFile);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createActionUri(java.lang.String)
     */
    public URI createActionUri(String action) {
        return FormatUtils.createActionUri(action);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isPronomUri(java.net.URI)
     */
    public Boolean isPronomUri(URI uri) {
        return FormatUtils.isPronomUri(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isExtensionUri(java.net.URI)
     */
    public Boolean isExtensionUri(URI uri) {
        return FormatUtils.isExtensionUri(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createMimeUri(java.lang.String)
     */
    public URI createMimeUri(String type) {
        return FormatUtils.createMimeUri(type);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createPronomUri(java.lang.String)
     */
    public URI createPronomUri(String string) {
        return FormatUtils.createPronomUri(string);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getExtensions(java.net.URI)
     */
    public Set<String> getExtensions(URI puidToUri) {
        return getFormatForURI(puidToUri).getExtensions();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getFirstExtension(java.net.URI)
     */
    public String getFirstExtension(URI uri) {
        return FormatUtils.getFirstExtension(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isMimeUri(java.net.URI)
     */
    public Boolean isMimeUri(URI typeURI) {
        return FormatUtils.isMimeUri(typeURI);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isUriOfType(java.net.URI,
     *      eu.planets_project.ifr.core.techreg.formats.Format.UriType)
     */
    public Boolean isUriOfType(URI uri, UriType type) {
        switch (type) {
        case MIME:
            return isMimeUri(uri);
        case PRONOM:
            return isPronomUri(uri);
        case EXTENSION:
            return isExtensionUri(uri);
        case ANY:
            return uri.toString().equals(Format.ANY.toString());
        case UNKNOWN:
            return uri.toString().equals(Format.UNKNOWN.toString());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createAnyFormatUri()
     */
    public URI createAnyFormatUri() {
        return Format.ANY;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createUnknownFormatUri()
     */
    public URI createUnknownFormatUri() {
        return Format.UNKNOWN;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getValueFromUri(java.net.URI)
     */
    public String getValueFromUri(URI uri) {
        String marker = null;
        if (isUriOfType(uri, UriType.PRONOM)) {
            marker = "pronom/";
        } else if (isUriOfType(uri, UriType.EXTENSION)) {
            marker = "ext/";
        } else if (isUriOfType(uri, UriType.MIME)) {
            marker = "mime/";
        }
        if (marker != null) {
            return uri.toString().substring(
                    uri.toString().lastIndexOf(marker) + marker.length());
        }
        return null;
    }

}
