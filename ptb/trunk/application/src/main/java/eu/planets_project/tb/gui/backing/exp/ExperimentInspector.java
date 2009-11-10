/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * 
 * A request-scope bean that handles inspection of a experiment.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the experiment and makes it available to the page.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentInspector {
    /** */
    private static final Log log = LogFactory.getLog(ExperimentInspector.class);

    private String experimentId;
    
    private ExperimentBean experimentBean = null;
    
    /**
     * @param serviceName
     */
    public void setExperimentId(String experimentId) { 
        log.info("Got experimentId: "+ experimentId);
        this.experimentId = experimentId;
        // Do the parseLong and check it first.
        Long eid = null;
        try {
            eid = Long.parseLong(experimentId);
        } catch( NumberFormatException e ) {
        	log.error("Could not parse experiment id "+experimentId);
        	return;
        }
        // Load it:
        TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");
        Experiment exp = testbedMan.getExperiment(eid);

        experimentBean = new ExperimentBean();
        if( exp != null ) {
        	experimentBean.fill(exp);
            log.info("exp name: "+ exp.getExperimentSetup().getBasicProperties().getExperimentName());
            ExpBeanReqManager.putExperimentIntoRequestExperimentBean(exp);
        }
    }

    /**
     * @return
     */
    public String getExperimentId() { 
        return experimentId; 
    }

	/**
	 * @return the experimentBean
	 */
	public ExperimentBean getExperimentBean() {
		return experimentBean;
	}

	/**
	 * @param experimentBean the experimentBean to set
	 */
	public void setExperimentBean(ExperimentBean experimentBean) {
		this.experimentBean = experimentBean;
	}
    
}
