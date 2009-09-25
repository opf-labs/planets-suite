/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.activation.MimetypesFileTypeMap;

import org.jfree.util.Log;


import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.serialization.ExperimentFileCache;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DownloadManager {
    private static PlanetsLogger log = PlanetsLogger.getLogger(DownloadManager.class);

    /**
     * 
     */
    private static ExperimentFileCache expCache = new ExperimentFileCache();
    
    /**
     * 
     * @return
     */
    public String downloadExperiment( Long expID ) {
        if( expID == null ) return "experimentNotFound";
        TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
        Experiment exp = testbedMan.getExperiment(expID);
        return downloadExperiment( (ExperimentImpl) exp );
    }
    
    /**
     * 
     * @param exp
     * @return
     */
    public String downloadExperiment( ExperimentImpl exp ) {
        String expExportID = expCache.createExperimentExport(exp);
        return downloadExportedExperiment(expExportID, exp.getExperimentSetup().getBasicProperties().getExperimentName() );
    }


    /**
     * 
     * @return
     * @throws IOException
     */
    public String downloadExportedExperiment( String expExportID, String downloadName ) {
        FacesContext ctx = FacesContext.getCurrentInstance();

        // Decode the file name (might contain spaces and on) and prepare file object.
        try {
            expExportID = URLDecoder.decode(expExportID, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        File file = expCache.getExportedFile(expExportID);

        HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
        
        // Check if file exists and can be read:
        if ( !file.exists() || !file.isFile() || !file.canRead() ) {
            return "fileNotFound";
        }

        // Get content type by filename.
        String contentType = new MimetypesFileTypeMap().getContentType(file);

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open the file:
            input = new BufferedInputStream(new FileInputStream(file));
            int contentLength = input.available();

            // Initialise the servlet response:
            response.reset();
            response.setContentType(contentType);
            response.setContentLength(contentLength);
            response.setHeader(
                "Content-disposition", "attachment; filename=\"" + downloadName + ".xml\"");
            output = new BufferedOutputStream(response.getOutputStream());

            // Write file out:
            for (int data; (data = input.read()) != -1;) {
                output.write(data);
            }

            // Flush the stream:
            output.flush();
            
            // Tell Faces that we're finished:
            ctx.responseComplete();
            
        } catch (IOException e) {
            // Something went wrong?
            e.printStackTrace();
            
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }
        return "success";
    }
    
    /**
     * Exports all exps as one big file.
     *
     * @param allExps
     * @return
     */
	public String downloadAllExperiments(Collection<Experiment> allExps) {
        String expExportID = expCache.createExperimentsExport(allExps);
        log.info("Cached all experiments in: " + expExportID );
        Calendar date = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return downloadExportedExperiment(expExportID, "all-experiments-"+df.format(date.getTime()));
    }
  
    
    // Helpers (can be refactored to public utility class) ----------------------------------------

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it.
                e.printStackTrace();
            }
        }
    }

}