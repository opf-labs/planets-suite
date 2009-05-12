/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;


/**
 * 
 * A request-scope bean that handles inspection of a view.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the service and makes it available to the page.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class EmulationInspector {
    /** */
    private static final Log log = LogFactory.getLog(EmulationInspector.class);

    private String experimentId;
    
    private String sessionId;
    
    private Experiment experiment;
    
    private ViewResultBean vrb;
    
    /**
     * @return the experimentId
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * @param experimentId the experimentId to set
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
        log.info("Setting eid = "+experimentId);
        // Lookup experiment and add
        ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
        this.experiment = edao.findExperiment( Long.parseLong( this.experimentId ) );
        this.vrb = null;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        log.info("Setting sid = "+sessionId);
        this.sessionId = sessionId;
        this.vrb = null;
    }
    
    /**
     * @return
     */
    public boolean isValid() {
        // Check the session for a view URL:
        if( this.getViewResultBean() == null ) {
            return false;
        }
        return true;
    }
    
    /**
     * @return
     */
    public ViewResultBean getViewResultBean() {
        if( this.vrb == null ) this.initViewResultBean();
        return this.vrb;
    }
    
    /** */
    private void initViewResultBean() {
        if( this.experiment == null ) return;
        if( this.sessionId == null || "".equals(this.sessionId)) {
            return;
        }
        // Look up this session id in the experiment:
        List<ExecutionRecordImpl> executionRecords = new ArrayList<ExecutionRecordImpl>();
        for( BatchExecutionRecordImpl batch : experiment.getExperimentExecutable().getBatchExecutionRecords() ) {
            for( ExecutionRecordImpl run : batch.getRuns() ) {
                    executionRecords.add(run);
            }
        }
        List<ViewResultBean> vrbs = ViewResultBean.createResultsFromExecutionRecords(executionRecords);
        // Look for a match:
        for( ViewResultBean vrb : vrbs ) {
            if( this.sessionId.equals(vrb.getSessionId())) {
                this.vrb = vrb;
            }
        }
    }
    

}
