/**
 * 
 */
package eu.planets_project.ifr.core.techreg.impl.formats.droid;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.signatureFile.FFSignatureFile;
import uk.gov.nationalarchives.droid.signatureFile.FileFormat;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.droid.DroidConfig;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DroidFormatRegistry  {
    
    private static Log log = LogFactory.getLog(DroidFormatRegistry.class);

    private static final String INFO_PRONOM = "pronom/";
    
    FFSignatureFile sigFile;
    
    int numFormats = 0;
    
    /**
     * Constructor to set up the look-up tables:
     */
    public DroidFormatRegistry() {
        // Grab a Droid analyser:
        AnalysisController controller = getController();
        
        // Get the list of file formats
        sigFile = controller.getSigFile();
        numFormats = sigFile.getNumFileFormats();

    }
    
    /**
     * 
     * @param PUID
     * @return
     */
    private static URI PUIDtoURI( String PUID ) {
        URI puidURI = null;
        try {
            // Opaque URL constructed using a Scheme Specific Part:
            puidURI = new URI("info", INFO_PRONOM + PUID, null );
        } catch (URISyntaxException e) {
            log.error("Exception while constructing type URI: "+e);
            puidURI = null;
        }
        return puidURI;
    }
    
    /**
     * 
     * @param puri
     * @return
     */
    private static String URItoPUID( URI puri ) {
        if( puri == null ) return null;
        return puri.getSchemeSpecificPart().substring(INFO_PRONOM.length());
    }
    
    /**
     * 
     * @param ff
     * @return
     */
    private Format fillFormatFromPRONOM( FileFormat ff ) {
        Format fmt  = new Format();
        if( ff == null ) return fmt;
        
        // Store the unique id and the description
        fmt.setTypeURI( PUIDtoURI(ff.getPUID()) );
        try {
            fmt.setRegistryURL( new URL("http://www.nationalarchives.gov.uk/PRONOM/Format/proFormatSearch.aspx?status=detailReport&id=" + ff.getID()) );
        } catch (MalformedURLException e) {
            fmt.setRegistryURL(null);
        }
        fmt.setSummary( ff.getName() );
        fmt.setVersion( ff.getVersion() );
        
        // Store mime-type:
        if( ff.getMimeType() != null && ! "".equals(ff.getMimeType()) ) {
            HashSet<String> mimes = new HashSet<String>();
            mimes.add(ff.getMimeType());
            fmt.setMimeTypes(mimes);
        }
        
        // Store extensions:
        HashSet<String> exts = new HashSet<String>();
        for( int j = 0; j < ff.getNumExtensions(); j++ ) 
            exts.add(ff.getExtension(j));
        fmt.setExtensions(exts);
        
        // Return
        return fmt;
    }
    
    /**
     * 
     * @return
     */
    public Set<Format> getFormats() {
       HashSet<Format> fmts = new HashSet<Format>();
       for( int i = 0; i < sigFile.getNumFileFormats(); i++ ) {
           fmts.add( fillFormatFromPRONOM(sigFile.getFileFormat(i)) );
       }
       return fmts;
    }
    
    /**
     * 
     * @return
     */
    public static AnalysisController getController() {
        // Determine the config directory:
        String sigFileLocation = DroidConfig.configFolder();
        // Here we start using the Droid API:
        AnalysisController controller = new AnalysisController();
        try {
            controller.readSigFile(sigFileLocation);
        } catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
        return controller;
    }
}
