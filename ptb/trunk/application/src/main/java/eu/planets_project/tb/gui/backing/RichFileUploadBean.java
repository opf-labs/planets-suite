/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;


import org.ajax4jsf.component.html.HtmlActionParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.backing.wf.EditWorkflowParameterInspector;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RichFileUploadBean {
  
    private static Log log = LogFactory.getLog(RichFileUploadBean.class);
    
    private DataHandler dh = new DataHandlerImpl();
    
    private boolean richUploadEnabled = true;
    
    private String fileTypes = "*";
    
    private Integer maxFiles = 50;
    
    public RichFileUploadBean() {
        log.info("Constructed.");
    }

    public void listener(UploadEvent event) throws Exception {
        log.info("Got: "+event.getUploadItems());
        UploadItem item = event.getUploadItem();
        String filename = FileUploadBean.sanitizeFilename(item.getFileName());
        log.info("File : '" + filename + "' was uploaded");
        URI furi = null;
        if (item.getFile() != null ) {
            File file = item.getFile();
            log.info("Absolute Path : '" + file.getAbsolutePath() + "'!");
            log.info("Adding file to the Data Registry.");
            try {
				furi = dh.storeBytestream(new FileInputStream( file ) , filename );
			} catch (FileNotFoundException e) {
                log.error("File, FileNotFoundException: "+e);
				e.printStackTrace();
			} catch (IOException e) {
                log.error("File, IOException: "+e);
				e.printStackTrace();
			}

        } else if ( item.getData() != null ) {
			furi = dh.storeBytearray(item.getData(), filename );
        	
        } else {
        	log.error("Upload failed: both Data and File are null!");
        	throw new Exception("Upload failed: both Data and File are null!");
        }
        // If it worked, add to experiment:
        if( furi != null ) {
        	addDataToAppropriateExperimentStage(event, furi);
        } else {
            log.error("Upload could not be stored! "+event.isMultiUpload());
        }
    }
    
    private void addDataToAppropriateExperimentStage(UploadEvent evt, URI furi){
    	FacesContext context = FacesContext.getCurrentInstance();
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		String forStage = null;
	
		for(UIComponent child : evt.getComponent().getChildren()){
        	if(child instanceof HtmlActionParameter){
        		HtmlActionParameter param = (HtmlActionParameter)child;
        		if(param.getName().equals("stageName")){
        			forStage = (String)param.getValue();
        		}
        	}
		}

		if(forStage==null)
			return;
		
		//decide if data shall be added to 'design experiment' or 'evaluate experiment' stage
		if(forStage.equals("design experiment")){
			log.info("Adding file to Experiment Bean, stage: design experiment");
			String position = expBean.addExperimentInputData(furi.toString());
			log.info("File added in position: "+position);
			return;
		}
		if(forStage.equals("evaluate experiment")){
			log.info("Adding file to Experiment Bean, stage: evaluate experiment");
			expBean.addEvaluationExternalDigoRef(furi.toString());
			return;
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
