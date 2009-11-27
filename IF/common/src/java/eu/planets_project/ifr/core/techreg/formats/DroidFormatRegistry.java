/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.signatureFile.FFSignatureFile;
import uk.gov.nationalarchives.droid.signatureFile.FileFormat;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
class DroidFormatRegistry  {
    
    private static Logger log = Logger.getLogger(DroidFormatRegistry.class.getName());

    private static final String INFO_PRONOM = "pronom/";
    
    FFSignatureFile sigFile;
    
    int numFormats = 0;
    
    /**
     * Constructor to set up the look-up tables.
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
     * @param PUID The PUID string
     * @return A URI representation of the given PUID
     */
    static URI PUIDtoURI( String PUID ) {
        URI puidURI = null;
        try {
            // Opaque URL constructed using a Scheme Specific Part:
            puidURI = new URI("info", INFO_PRONOM + PUID, null );
        } catch (URISyntaxException e) {
            log.severe("Exception while constructing type URI: "+e);
            puidURI = null;
        }
        return puidURI;
    }
    
    /**
     * 
     * @param puri A planets URI
     * @return The raw PUID
     */
    static String URItoPUID( URI puri ) {
        if( puri == null ) return null;
        return puri.getSchemeSpecificPart().substring(INFO_PRONOM.length());
    }
    
    /**
     * 
     * @param ff The droid file format
     * @return A Planets format created from the given droid format
     */
    private MutableFormat fillFormatFromPRONOM( FileFormat ff ) {
        if( ff == null ) return null;
        
        // Store the unique id and the description
        MutableFormat fmt  = new MutableFormat( PUIDtoURI(ff.getPUID()) );
        try {
            fmt.setRegistryUrl( new URL("http://www.nationalarchives.gov.uk/PRONOM/Format/proFormatSearch.aspx?status=detailReport&id=" + ff.getID()) );
        } catch (MalformedURLException e) {
            fmt.setRegistryUrl(null);
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
     * @return the formats in a Set
     */
    public Set<MutableFormat> getFormats() {
       HashSet<MutableFormat> fmts = new HashSet<MutableFormat>();
       for( int i = 0; i < sigFile.getNumFileFormats(); i++ ) {
           fmts.add( fillFormatFromPRONOM(sigFile.getFileFormat(i)) );
       }
       return fmts;
    }
    
    /**
     * 
     * @return the DROID analysis controller
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
