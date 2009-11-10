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
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;

/**
 * 
 * A request-scope bean that handles inspection of a service.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the service and makes it available to the page.
 * 
 * Also the experiment specific ExpTypeBeans are initialized with experiment data.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 *
 */
public class ExpBeanReqManager {
    /** */
    private static final Log log = LogFactory.getLog(ExpBeanReqManager.class);
    
    /** */
    static final String EXP_BEAN_IN_SESSION = "ExperimentBeanInSession";
    static final String EXP_BEAN_IN_REQUEST = "ExperimentBean";

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
        // FIXME This overrides the experimental behaviour and returns to the default logic.
        ctx.getExternalContext().getSessionMap().put(EXP_BEAN_IN_REQUEST, expBean);
        updateExpTypeBeanForExperimentInSession();
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
        //finally update the experiment-type specific bean for this expBean
        updateExpTypeBeanForExperimentInSession();
        return expBean;
    }
    
    
    /**
     * This method grabs the ExperimentBean from the session and initializes the required
     * expType specific beans' 'fill' methods for this expBean
     * @param exp
     */
    private static void updateExpTypeBeanForExperimentInSession(){
         ExpTypeBackingBean exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeExecutablePP");
         //a kind of fill method for the expTypeBean for the current expBean
         exptype.initExpTypeBeanForExistingExperiment();

    }

}
