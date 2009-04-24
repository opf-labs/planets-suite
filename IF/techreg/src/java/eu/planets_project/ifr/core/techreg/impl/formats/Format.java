/**
 * 
 */
package eu.planets_project.ifr.core.techreg.impl.formats;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This is the Planets 'Preservation Object' 'Format' description entity. It is
 * based on the DROID FileFormat entity, but simplified. Other format registries
 * could be supported too. Note that DROID also provides: - 'has priority over'
 * (good for doing PP) - 'signatures' (good for doing PC) but as this is meant
 * to be a simple, interoperable type identifier system, this extra information
 * (which is more oriented to specific use contexts) is not (yet) included here.
 * Types are identified by URIs. As we are currently based on PRONOM, these URIs
 * will be PRONOM info:pronom/fmt/XX URIs. By using these URIs, we retain the
 * ability to add further registries in the future without changing any of this
 * code.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public class Format implements Serializable {

    private static final long serialVersionUID = -4713590391811379383L;

    private URI typeURI;

    private Set<URI> aliases = new HashSet<URI>();

    private URL registryURL;

    private String summary;

    private String version;

    private Set<String> mimeTypes = new HashSet<String>();

    private Set<String> extensions = new HashSet<String>();

    /**
     * @param typeURI
     */
    public Format(URI typeURI) {
        this.typeURI = typeURI;
        if (isThisAMimeURI(typeURI)) {
            String mime = typeURI.toString().replace(MIME_URI_PREFIX, "");
            this.mimeTypes = new HashSet<String>();
            this.mimeTypes.add(mime);
            this.summary = mime.toLowerCase();
        } else if (Format.isThisAnExtensionURI(typeURI)) {
            String ext = typeURI.toString().replace(EXT_URI_PREFIX, "");
            this.extensions = new HashSet<String>();
            this.extensions.add(ext);
            this.summary = ext.toUpperCase();
        }
    }

    /**
     * @return the typeURI
     */
    public URI getTypeURI() {
        return typeURI;
    }

    /**
     * @param typeURI the typeURI to set
     */
    public void setTypeURI(URI typeURI) {
        this.typeURI = typeURI;
    }

    /**
     * @return the aliases
     */
    public Set<URI> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public void setAliases(Set<URI> aliases) {
        this.aliases = aliases;
    }

    /**
     * @return the registryURL
     */
    public URL getRegistryURL() {
        if (registryURL == null) {
            try {
                return typeURI.toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return registryURL;
    }

    /**
     * @param registryURL the registryURL to set
     */
    public void setRegistryURL(URL registryURL) {
        this.registryURL = registryURL;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return the summary with the version number appended.
     */
    public String getSummaryAndVersion() {
        if (version != null)
            return summary + " " + version;
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the mimeTypes
     */
    public Set<String> getMimeTypes() {
        return mimeTypes;
    }

    /**
     * @param mimeTypes the mimeTypes to set
     */
    public void setMimeTypes(Set<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    /**
     * @return the extensions
     */
    public Set<String> getExtensions() {
        return extensions;
    }

    /**
     * @param extensions the extensions to set
     */
    public void setExtensions(Set<String> extensions) {
        this.extensions = extensions;
    }

    /** The prefix for MIME format URIs */
    static final String MIME_URI_PREFIX = "planets:fmt/mime/";
    /** The prefix for extension format URIs */
    static final String EXT_URI_PREFIX = "planets:fmt/ext/";
    /** The prefix for PRONOM format URIs */
    static final String PRONOM_URI_PREFIX = "info:pronom/";
    
    static String getFirstMatchingFormatExtension(URI formatURI) {
        if (formatURI == null) {
            return null;
        }
        Set<String> set = new Format(formatURI).getExtensions();
        if (set != null) {
            return set.iterator().next();
        }
        return null;
    }

    /**
     * Static helper to construct appropriate URIs for file-extensions format
     * specifiers.
     * @param ext
     * @return the extension as a format URI
     */
    static URI extensionToURI(String ext) {
        try {
            return new URI(EXT_URI_PREFIX + ext.toLowerCase());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Static helper to construct appropriate URIs for mime-type format
     * specifiers.
     * @param mime
     * @return the MIME type as a format URI
     */
    static URI mimeToURI(String mime) {
        try {
            return new URI(MIME_URI_PREFIX + mime.toLowerCase());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Static helper to convert a short-form PRONOM ID into a PRONOM URI.
     * @param pronomID Short-form PUID, e.g. 'fmt/95'
     * @return A full PRONOM Format URI, e.g. 'info:pronom/fmt/95'
     */
     static URI pronomIdToURI(String pronomID) {
        try {
            return new URI(PRONOM_URI_PREFIX + pronomID.toLowerCase());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * @param typeURI
     * @return true if a MIME URI
     */
    static boolean isThisAMimeURI(URI typeURI) {
        return typeURI.toString().startsWith(MIME_URI_PREFIX);
    }

    /**
     * @param typeURI
     * @return true if an extension URI
     */
    static boolean isThisAnExtensionURI(URI typeURI) {
        return typeURI.toString().startsWith(EXT_URI_PREFIX);
    }

    /**
     * @param typeURI
     * @return true if a PRONOM URI
     */
    static boolean isThisAPronomURI(URI typeURI) {
        return typeURI.toString().startsWith(PRONOM_URI_PREFIX);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Format) {
            Format f = (Format) obj;
            /*
             * Format objects MUST be uniquely identified by the assigned URI.
             */
            return this.typeURI.equals(f.typeURI);
        } else {
            return super.equals(obj);
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.typeURI.hashCode();
    }

}
