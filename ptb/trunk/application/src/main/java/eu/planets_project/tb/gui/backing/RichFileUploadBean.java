/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RichFileUploadBean {
  
    private static PlanetsLogger log = PlanetsLogger.getLogger(RichFileUploadBean.class);
    
    private DataHandler dh = new DataHandlerImpl();
    
    private boolean richUploadEnabled = false;
    
    private String fileTypes = "*";
    
    private Integer maxFiles = 50;
    
    public RichFileUploadBean() {
        log.info("Constructed.");
    }

    public void listener(UploadEvent event) throws Exception {
        log.info("Got: "+event.getUploadItems());
        UploadItem item = event.getUploadItem();
        log.info("File : '" + item.getFileName() + "' was uploaded");
        URI furi = null;
        if (item.getFile() != null ) {
            File file = item.getFile();
            log.info("Absolute Path : '" + file.getAbsolutePath() + "'!");
            log.info("Adding file to the Data Registry.");
            try {
				furi = dh.storeBytestream(new FileInputStream( file ) , item.getFileName() );
			} catch (FileNotFoundException e) {
                log.error("File, FileNotFoundException: "+e);
				e.printStackTrace();
			} catch (IOException e) {
                log.error("File, IOException: "+e);
				e.printStackTrace();
			}

        } else if ( item.getData() != null ) {
			furi = dh.storeBytearray(item.getData(), item.getFileName() );
        	
        } else {
        	log.error("Upload failed: both Data and File are null!");
        	throw new Exception("Upload failed: both Data and File are null!");
        }
        // If it worked, add to experiment:
        if( furi != null ) {
        	log.info("Adding file to Experiment Bean.");
        	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        	String position = expBean.addExperimentInputData(furi.toString());
        	log.info("File added in position: "+position);
        } else {
            log.error("Upload could not be stored! "+event.isMultiUpload());
        }
    }
    
    public void enableRichUpload() {
        this.richUploadEnabled = true;
    }

    public void disableRichUpload() {
        this.richUploadEnabled = false;
    }
    
    /**
     * @return the fileTypes
     */
    public String getFileTypes() {
        log.info("Getting fileTypes: "+fileTypes);
        return fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(String fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * @return the maxFiles
     */
    public Integer getMaxFiles() {
        log.info("Getting maxFiles: "+maxFiles);
        return maxFiles;
    }

    /**
     * @param maxFiles the maxFiles to set
     */
    public void setMaxFiles(Integer maxFiles) {
        this.maxFiles = maxFiles;
    }

    /**
     * @return the richUploadEnabled
     */
    public boolean isRichUploadEnabled() {
        return richUploadEnabled;
    }

    /**
     * @param richUploadEnabled the richUploadEnabled to set
     */
    public void setRichUploadEnabled(boolean richUploadEnabled) {
        this.richUploadEnabled = richUploadEnabled;
    }

}
