/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ResultsForDigitalObjectBean {
    private PlanetsLogger log = PlanetsLogger.getLogger(ResultsForDigitalObjectBean.class, "testbed-log4j.xml");
    
    private String digitalObject;
    
    private String downloadURL;
    
    private String digitalObjectName;
    
    private List<ExecutionRecordImpl> executionRecords = new Vector<ExecutionRecordImpl>();

    /**
     * @param input
     */
    public ResultsForDigitalObjectBean( String input ) {
        this.init(input);
    }
    
    /**
     * @return the digitalObject
     */
    public String getDigitalObject() {
        return digitalObject;
    }

    /**
     * @param digitalObject the digitalObject to set
     */
    public void setDigitalObject(String digitalObject) {
        this.digitalObject = digitalObject;
    }

    /**
     * @return the downloadURL
     */
    public String getDownloadURL() {
        return downloadURL;
    }

    /**
     * @param downloadURL the downloadURL to set
     */
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    /**
     * @return the digitalObjectName
     */
    public String getDigitalObjectName() {
        return digitalObjectName;
    }

    /**
     * @param digitalObjectName the digitalObjectName to set
     */
    public void setDigitalObjectName(String digitalObjectName) {
        this.digitalObjectName = digitalObjectName;
    }

    /**
     * @return the executionRecords
     */
    public List<ExecutionRecordImpl> getExecutionRecords() {
        return executionRecords;
    }

    /**
     * @param executionRecords the executionRecords to set
     */
    public void setExecutionRecords(List<ExecutionRecordImpl> executionRecords) {
        this.executionRecords = executionRecords;
    }
    
    
    /**
     * 
     */
    private void init( String file ) {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        // Populate using the results:
        DataHandler dh = new DataHandlerImpl();
        // Set up the DO name, etc
        setDigitalObject(file);
        try {
            setDownloadURL(dh.getDownloadURI(file).toString());
        } catch (FileNotFoundException e) {
            setDownloadURL("");
        }
        try {
            setDigitalObjectName(DataHandlerImpl.createShortDOName(dh.getName(file)));
        } catch (FileNotFoundException e) {
            setDigitalObjectName(DataHandlerImpl.createShortDOName(file));
        }
        // Loop over results and patch them in:
        for( BatchExecutionRecordImpl batch : expBean.getExperiment().getExperimentExecutable().getBatchExecutionRecords() ) {
            for( ExecutionRecordImpl run : batch.getRuns() ) {
                if( file.equals( run.getDigitalObjectReferenceCopy() ) ) {
                    getExecutionRecords().add(run);
                }
            }
        }
        log.info("Result object initialised.");
    }

}
