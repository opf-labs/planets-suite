/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.List;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ResultsForDigitalObjectBean extends DigitalObjectBean {
    private Log log = LogFactory.getLog(ResultsForDigitalObjectBean.class);

    private List<ExecutionRecordImpl> executionRecords = new Vector<ExecutionRecordImpl>();

    /**
     * @param input
     */
    public ResultsForDigitalObjectBean( String input ) {
    	super(input);
    	this.init(input);
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
    
    
    private void init(String file){
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
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
