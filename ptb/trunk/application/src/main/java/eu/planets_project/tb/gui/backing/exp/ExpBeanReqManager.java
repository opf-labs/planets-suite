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
import eu.planets_project.tb.impl.model.ExperimentImpl;

/**
 * 
 * A request-scope bean that handles inspection of a service.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the service and makes it available to the page.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExpBeanReqManager {
    /** */
    private static final Log log = LogFactory.getLog(ExpBeanReqManager.class);
    
    /** */
    private static final String EXP_BEAN_IN_SESSION = "ExperimentBeanInSession";
    private static final String EXP_BEAN_IN_REQUEST = "ExperimentBean";

    /** */
    private String eid = null;
    
    /**
     * 
     * @param eid
     */
    public void setEid( String eid ) { 
        this.eid = eid; 
        
        // Initialise the exp var:
        Experiment exp = null;
        
        // Parse to a number:
        if( eid != null ) {
            long eidl = Long.parseLong(eid);
            log.info("Looking for experiment id: "+eidl);
            // Look up this experiment, if there is one:
            if( eidl != -1 ) {
                TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
                exp = testbedMan.getExperiment(eidl);
            }
        }
        
        // Also look for an experiment bean in the session:
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExperimentBean sessExpBean = (ExperimentBean) ctx.getExternalContext().getSessionMap().get(EXP_BEAN_IN_SESSION);
        
        // Experiment exp should work:
        if( exp != null ) {
            log.info("Experiment found: "+exp.getExperimentSetup().getBasicProperties().getExperimentName());
            putExperimentIntoRequestExperimentBean(exp);
        }
        // If not, use the one in the session:
        else if( sessExpBean != null ) {
            log.info("Experiment found in session: "+sessExpBean.getEname());
            ctx.getExternalContext().getRequestMap().put(EXP_BEAN_IN_REQUEST, sessExpBean);
        }
        // Otherwise, treat as a new experiment:
        else {
            log.info("No experiment found: make a new one.");
            exp = new ExperimentImpl();
            putExperimentIntoRequestExperimentBean(exp);
        }
    }

    /**
     * @return the current experiment identifier.
     */
    public String getEid() { return eid; }

    /**
     * @param exp
     * @return
     */
    public static ExperimentBean putExperimentIntoSessionExperimentBean( Experiment exp ) {
        ExperimentBean expBean = new ExperimentBean();
        if( exp != null ) expBean.fill(exp);
        //Store selected Experiment Row accessible later as #{Experiment} 
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put(EXP_BEAN_IN_SESSION, expBean);
        return expBean;
    }

    /**
     * @param exp
     * @return
     */
    public static ExperimentBean putExperimentIntoRequestExperimentBean( Experiment exp ) {
        ExperimentBean expBean = new ExperimentBean();
        if( exp != null ) expBean.fill(exp);
        //Store selected Experiment Row accessible later as #{ExperimentBean}
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getRequestMap().put(EXP_BEAN_IN_REQUEST, expBean);
        // Also overwrite the bean stored in the session.
        ctx.getExternalContext().getSessionMap().put(EXP_BEAN_IN_SESSION, expBean);
        return expBean;
    }

}
