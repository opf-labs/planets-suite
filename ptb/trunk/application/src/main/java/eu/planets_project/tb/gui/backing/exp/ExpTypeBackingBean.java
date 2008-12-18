/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.HashMap;
import java.util.List;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.MigrateWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public abstract class ExpTypeBackingBean {
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExpTypeBackingBean.class);

    /**
     * @return
     */
    public abstract HashMap<String, List<MeasurementImpl>> getObservables();
    
    /**
     * @return
     */
    public abstract List<ExperimentStageBean> getStageBeans();
    
    /**
     * @param etype
     * @return
     */
    public static final ExpTypeBackingBean getExpTypeBean( String etype ) {
        ExpTypeBackingBean exptype;
        if( etype.equals( AdminManagerImpl.IDENTIFY ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeIdentify");
        } else if( etype.equals( AdminManagerImpl.MIGRATE ) ) {
            exptype = (ExpTypeBackingBean)JSFUtil.getManagedObject("ExpTypeMigrate");
        } else {
            // For unrecognised experiment types, set to NULL:
            exptype = null;
        }
        return exptype;
    }
    
    /**
     * @param etype
     * @return
     */
    public static final ExperimentWorkflow getWorkflow( String etype ) {
        ExperimentWorkflow expwf = null;
        
        // Workflow, depending on the experiment type:
        if( AdminManagerImpl.IDENTIFY.equals(etype)) {
            log.info("Running an Identify experiment.");
            expwf = new IdentifyWorkflow();
            
        } else if( AdminManagerImpl.MIGRATE.equals(etype)) {
            log.info("Running a Migrate experiment.");
            expwf = new MigrateWorkflow();

        } else {
            log.error("Unknown experiment type: "+etype);
        }

        // Ensure the parameters are stored/remembered:
        if( expwf != null ) {
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
            try {
                expwf.setParameters(expBean.getExperiment().getExperimentExecutable().getParameters());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return expwf;
    }

}