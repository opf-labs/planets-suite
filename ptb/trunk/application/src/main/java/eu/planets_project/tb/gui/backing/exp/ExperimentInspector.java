/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

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
        Long eid = null;
        Experiment exp = null;

        // Attempt to parse the experiment ID:
        if( this.experimentId != null ) {
            // Do the parseLong and check it first.
            try {
                eid = Long.parseLong(experimentId);
            } catch( NumberFormatException e ) {
                log.error("Could not parse experiment id "+experimentId);
            }
        }
        
        // Check if there is an experiment bean already, and persist changes.
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean != null ) { 
            log.info("Experiment Bean found: "+expBean.getEname());
        }
        
        // Load it:
        ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
        if( edao != null && eid != null ) {
            exp = edao.findExperiment(eid);
        }

        // Also look for an experiment bean in the session:
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExperimentBean sessExpBean = (ExperimentBean) ctx.getExternalContext().getSessionMap().get(ExperimentInspector.EXP_BEAN_IN_SESSION_DEPRECATED);
        
        // Experiment exp should work:
        if( exp != null ) {
            log.info("Experiment found: "+exp.getExperimentSetup().getBasicProperties().getExperimentName());
            experimentBean = new ExperimentBean();
            experimentBean.fill(exp);
            ExperimentInspector.putExperimentIntoRequestExperimentBean(exp);
        }
        // If not, use the one in the session:
        else if( sessExpBean != null ) {
            log.info("Experiment found in session: "+sessExpBean.getEname());
            if( sessExpBean.getExperiment() != null ) {
                this.experimentId = ""+sessExpBean.getExperiment().getEntityID();
            }
            this.experimentBean = sessExpBean;
            ctx.getExternalContext().getRequestMap().put(ExperimentInspector.EXP_BEAN_IN_REQUEST, sessExpBean);
        }
        // Otherwise, treat as a new experiment:
        else {
            log.info("No experiment found: make a new one.");
            exp = new ExperimentImpl();
            this.experimentId = null;
            experimentBean = new ExperimentBean();
            experimentBean.fill(exp);
            ExperimentInspector.putExperimentIntoRequestExperimentBean(exp);
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
    
     /* -------------------------------  */
    
    /** */
    static final String EXP_BEAN_IN_SESSION_DEPRECATED = "ExperimentBeanInSession";
    static final String EXP_BEAN_IN_REQUEST = "ExperimentBean";

    /**
     * @param exp
     * @return
     */
    @Deprecated
    public static ExperimentBean putExperimentIntoSessionExperimentBean( Experiment exp ) {
        ExperimentBean expBean = new ExperimentBean();
        if( exp != null ) expBean.fill(exp);
        //Store selected Experiment Row accessible later as #{Experiment} 
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put(EXP_BEAN_IN_SESSION_DEPRECATED, expBean);
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
