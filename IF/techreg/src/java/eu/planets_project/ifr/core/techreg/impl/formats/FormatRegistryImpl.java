/**
 * 
 */
package eu.planets_project.ifr.core.techreg.impl.formats;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;


/**
 * This is the Planets Format Registry and Resolver.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@Stateful(mappedName = "planets-project.eu/FormatRegistry")
@Local(FormatRegistry.class)
@LocalBinding(jndiBinding = "planets-project.eu/FormatRegistry/local")
@Remote(FormatRegistry.class)
@RemoteBinding(jndiBinding = "planets-project.eu/FormatRegistry/remote")
public class FormatRegistryImpl implements FormatRegistry {
    private static Log log = LogFactory.getLog(FormatRegistryImpl.class);

    /**
     * Main index, 1-2-1 mapping the type URIs to the FileFormat objects:
     */
    Map<URI, Format> uriMap = new HashMap<URI, Format>();

    /**
     * Map a file extension onto one or more type URIs.
     */
    Map<String, Set<URI>> extMap = new HashMap<String, Set<URI>>();

    /**
     * Map a mime type onto one or more type URIs.
     */
    Map<String, Set<URI>> mimeMap = new HashMap<String, Set<URI>>();

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
                    if (mimeSet == null)
                        mimeSet = new HashSet<URI>();
                    mimeSet.add(ff.getTypeURI());
                    if (ff.getAliases() != null) {
                        for (URI furi : ff.getAliases())
                            mimeSet.add(furi);
                    }
                    mimeMap.put(mimeType, mimeSet);
                    // log.debug("Referenced under MIME: "+mimeType);
                }
            }
            // Store the extension mapping:
            if (ff.getExtensions() != null) {
                for (String ext : ff.getExtensions()) {
                    Set<URI> extSet = extMap.get(ext);
                    if (extSet == null)
                        extSet = new HashSet<URI>();
                    extSet.add(ff.getTypeURI());
                    if (ff.getAliases() != null) {
                        for (URI furi : ff.getAliases())
                            extSet.add(furi);
                    }
                    extMap.put(ext, extSet);
                    // log.debug("Referenced under extension: "+ext);
                }
            }
        }
        log.info("File format look-up tables complete.");
    }

    /**
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getFormatForURI(java.net.URI)
     */
    public Format getFormatForURI(URI puri) {
        if (Format.isThisAMimeURI(puri) || Format.isThisAnExtensionURI(puri)) {
            return new Format(puri);
        } else {
            return uriMap.get(puri);
        }
    }

    /**
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getURIsForExtension(java.lang.String)
     */
    public Set<URI> getURIsForExtension(String ext) {
        ext = ext.toLowerCase();
        return extMap.get(ext);
    }

    /**
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getURIsForMimeType(java.lang.String)
     */
    public Set<URI> getURIsForMimeType(String mimetype) {
        return mimeMap.get(mimetype);
    }

    /**
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#search(java.lang.String)
     */
    public List<URI> search(String query) {
        ArrayList<URI> found = new ArrayList<URI>(this
                .getURIsForExtension(query));
        Collections.sort(found);
        return found;
    }

    /**
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getFormatURIAliases(java.net.URI)
     */
    public List<URI> getFormatURIAliases(URI typeURI) {
        Set<URI> turis = new HashSet<URI>();
        turis.add(typeURI);

        if (Format.isThisAMimeURI(typeURI)) {
            Format mime = new Format(typeURI);
            Set<URI> furis = getURIsForMimeType(mime.getMimeTypes().iterator().next());
            turis.addAll(furis);
        } else if( Format.isThisAnExtensionURI(typeURI)) {
            Format ext = new Format(typeURI);
            Set<URI> furis = getURIsForExtension(ext.getExtensions().iterator()
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
                turis.add(Format.extensionToURI(ext));
            }
            // Mime:
            for (String mime : f.getMimeTypes()) {
                turis.add(Format.mimeToURI(mime));
            }
        }
        return new ArrayList<URI>(turis);
    }

    /**
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getFormatAliases(java.net.URI)
     */
    public List<Format> getFormatAliases(URI typeURI) {
        List<Format> fmts = new ArrayList<Format>();
        for (URI furi : getFormatURIAliases(typeURI)) {
            fmts.add(getFormatForURI(furi));
        }
        return fmts;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#puidToUri(java.lang.String)
     */
    public URI puidToUri(final String puid) {
        return DroidFormatRegistry.PUIDtoURI(puid);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#uriToPuid(java.net.URI)
     */
    public String uriToPuid(final URI uri) {
        return DroidFormatRegistry.URItoPUID(uri);
    }

}
