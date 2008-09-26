/**
 * 
 */
package eu.planets_project.ifr.core.techreg.api.formats;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

/**
 * This is the Planets 'Preservation Object' 'Format' description entity.
 * 
 * It is based on the DROID FileFormat entity, but simplified. Other format
 * registries could be supported too.
 * 
 * Note that DROID also provides:
 * 
 *  - 'has priority over' (good for doing PP)
 *  - 'signatures'        (good for doing PC)
 * 
 * but as this is meant to be a simple, interoperable type identifier system, 
 * this extra information (which is more oriented to specific use contexts) 
 * is not (yet) included here.
 * 
 * Types are identified by URIs.  As we are currently based on PRONOM, 
 * these URIs will be PRONOM info:pronom/fmt/XX URIs.  By using these URIs, 
 * we retain the ability to add further registries in the future without
 * changing any of this code.
 *  
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class Format {

    private URI typeURI;
    
    private URL registryURL;

    private String summary;
    
    private String version;
    
    private Set<String> mimeTypes;
    
    private Set<String> extensions;
    
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
     * @return the registryURL
     */
    public URL getRegistryURL() {
        if( registryURL == null ) {
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
        return summary+" "+version;
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
    
    
}
