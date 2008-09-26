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

import eu.planets_project.ifr.core.techreg.impl.formats.droid.DroidFormatRegistry;

/**
 * This is the Planets Format Registry and Resolver.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Stateful(mappedName="planets-project.eu/FormatRegistry")
@Local(FormatRegistry.class)
@LocalBinding(jndiBinding = "planets-project.eu/FormatRegistry/local")
@Remote(FormatRegistry.class)
@RemoteBinding(jndiBinding = "planets-project.eu/FormatRegistry/remote")
public class FormatRegistryImpl implements FormatRegistry {
    private static Log log = LogFactory.getLog(FormatRegistryImpl.class);
    
    /**
     * Main index, 1-2-1 mapping the type URIs to the FileFormat objects:
     */
    Map<URI,Format> uriMap = new HashMap<URI,Format>();
    
    /**
     * Map a file extension onto one or more type URIs.
     */
    Map<String,Set<URI>> extMap = new HashMap<String,Set<URI>>();
    
    /**
     * Map a mime type onto one or more type URIs.
     */
    Map<String,Set<URI>> mimeMap = new HashMap<String,Set<URI>>();
    
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
        for( Format ff : ffs ){
            //log.debug("--------------------------\nGot format "+ ff.getSummary());
            
            // Store the format in a PUID map:
            uriMap.put(ff.getTypeURI(), ff);
            //log.debug("Stored under PUID: "+ff.getTypeURI());
            
            // Store the mime mapping:
            if( ff.getMimeTypes() != null ) {
                for( String mimeType : ff.getMimeTypes() ) {
                    Set<URI> mimeSet = mimeMap.get(mimeType);
                    if( mimeSet == null ) mimeSet = new HashSet<URI>();
                    mimeSet.add(ff.getTypeURI());
                    mimeMap.put(mimeType, mimeSet);
                    //log.debug("Referenced under MIME: "+mimeType);
                }
            }
            // Store the extension mapping:
            if( ff.getExtensions() != null ) {
                for( String ext : ff.getExtensions() ) {
                    Set<URI> extSet = extMap.get(ext);
                    if( extSet == null ) extSet = new HashSet<URI>();
                    extSet.add(ff.getTypeURI());
                    extMap.put(ext, extSet);
                    //log.debug("Referenced under extension: "+ext);
                }
            }            
        }
        log.info("File format look-up tables complete.");
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getFormatForURI(java.net.URI)
     */
    public Format getFormatForURI(URI puri) {
        return uriMap.get(puri);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getURIsForExtension(java.lang.String)
     */
    public Set<URI> getURIsForExtension(String ext) {
        return extMap.get(ext);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#getURIsForMimeType(java.lang.String)
     */
    public Set<URI> getURIsForMimeType(String mimetype) {
        return mimeMap.get(mimetype);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry#search(java.lang.String)
     */
    public List<URI> search( String query ) {
        ArrayList<URI> found = new ArrayList<URI>(this.getURIsForExtension(query));
        Collections.sort(found);
        return found;
    }
    
    

}
