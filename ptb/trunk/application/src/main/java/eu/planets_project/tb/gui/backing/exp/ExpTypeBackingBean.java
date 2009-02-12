/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.HashMap;
import java.util.List;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;
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

    /** Allow the workflow to be cached during editing. */
    private ExperimentWorkflow ewfCache = null;

    /** A Service Registry instance for look-ups. */
    protected Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());

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
    public final ExperimentWorkflow getWorkflow( String etype ) {
        
        // Workflow, depending on the experiment type:
        if( AdminManagerImpl.IDENTIFY.equals(etype)) {
            log.info("Running an Identify experiment.");
            if( ewfCache == null || ( ! (ewfCache instanceof IdentifyWorkflow) ) )
                ewfCache = new IdentifyWorkflow();
            
        } else if( AdminManagerImpl.MIGRATE.equals(etype)) {
            log.info("Running a Migrate experiment.");
            if( ewfCache == null || ( ! (ewfCache instanceof MigrateWorkflow) ) )
                ewfCache = new MigrateWorkflow();
            
        } else {
            log.error("Unknown experiment type: "+etype);
            
        }

        // Ensure the parameters are stored/remembered:
        if( ewfCache != null ) {
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
            try {
                ewfCache.setParameters(expBean.getExperiment().getExperimentExecutable().getParameters());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ewfCache;
    }
    
    /**
     * @param type
     * @return
     */
    protected List<ServiceDescription> lookupServicesByType( String type ) {
        ServiceDescription sdQuery = new ServiceDescription.Builder(null, type ).build();
        return registry.query(sdQuery);
    }

}