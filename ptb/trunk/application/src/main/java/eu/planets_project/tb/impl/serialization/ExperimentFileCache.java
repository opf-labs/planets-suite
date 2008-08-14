/**
 * 
 */
package eu.planets_project.tb.impl.serialization;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.Experiment;

/**
 * This manages the temporary files needed to import and export Experiments.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentFileCache {
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExperimentFileCache.class);
    
    private static File cachedir = null;
    private static String cacheExt = ".cache";
    private static String expExt = ".exp.xml";
    
    /**
     * 
     */
    public ExperimentFileCache() {
        if( cachedir != null ) return;
        // Generate a temporary file and turn it into a directory:
        try {
            UUID dirname = UUID.randomUUID();
            File tmp = File.createTempFile(dirname.toString(), cacheExt);
            tmp.delete();
            tmp.mkdir();
            tmp.deleteOnExit();
            cachedir = tmp;
            log.info("Set up export cache: "+cachedir.getPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param exp
     * @return
     */
    public String createExperimentExport( Experiment exp ) {
        try {
            File tmp = createTempFile();
            ExperimentViaJAXB.writeToFile(exp, tmp);
            log.info("Written to file: "+tmp.getName());
            return tmp.getName();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
    public File createTempFile() throws IOException {
        // Create a temporary file, and ensure it will get cleaned up on exit:
        String exname = UUID.randomUUID().toString();
        File tmp = File.createTempFile(exname.toString(), expExt, cachedir);
        tmp.deleteOnExit();
        log.info("Created temp file: "+tmp);
        return tmp;
    }
    
    /**
     * 
     * @param exportID
     * @return
     */
    public File getExportedFile( String exportID ) {
        File exported = new File( cachedir.getPath() + File.separator + exportID );
        log.info("Getting file: "+exported);
        return exported;
    }

}
