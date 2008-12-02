/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RichFileUploadBean {
  
    private static PlanetsLogger log = PlanetsLogger.getLogger(RichFileUploadBean.class, "testbed-log4j.xml");
    
    private String fileTypes = "*";
    
    private Integer maxFiles = 5;

    public void listener(UploadEvent event){
        UploadItem item = event.getUploadItem();
        System.out.println("File : '" + item.getFileName() + "' was uploaded");
        if (item.isTempFile()) {
            File file = item.getFile();
            log.info("Absolute Path : '" + file.getAbsolutePath() + "'!");
            log.info("Adding file to Experiment Bean.");
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
            String position = expBean.addExperimentInputData(file.getAbsolutePath());
            log.info("File added in position: "+position);
            file.deleteOnExit();
        } else {
            try {
                log.error("Cannot cope with this mode of upload!");
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                b.write(item.getData());
                System.out.println(b.toString());
            } catch (Exception e) {
                // TODO: handle exception
            }
        
        }
    }

    /**
     * @return the fileTypes
     */
    public String getFileTypes() {
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
        return maxFiles;
    }

    /**
     * @param maxFiles the maxFiles to set
     */
    public void setMaxFiles(Integer maxFiles) {
        this.maxFiles = maxFiles;
    }

}
