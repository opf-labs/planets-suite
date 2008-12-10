/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExpTypeIdentify {
    private PlanetsLogger log = PlanetsLogger.getLogger(ExpTypeIdentify.class, "testbed-log4j.xml");
    
    /**
     * @return the identifyService
     */
    public String getIdentifyService() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        log.info("Got params: "+expBean.getExperiment().getExperimentExecutable().getParameters() );
        log.info("Got param: "+expBean.getExperiment().getExperimentExecutable().getParameters().get(IdentifyWorkflow.PARAM_SERVICE) );
        return expBean.getExperiment().getExperimentExecutable().getParameters().get(IdentifyWorkflow.PARAM_SERVICE);
    }

    /**
     * @param identifyService the identifyService to set
     */
    public void setIdentifyService(String identifyService) {
        log.info("Setting the Identify service to: "+identifyService);
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().getParameters().put(IdentifyWorkflow.PARAM_SERVICE, identifyService);
    }

    /**
     * 
     * @return
     */
    public Collection<MeasurementImpl> getObservables() {
        return this.getIdentifyWorkflow().getObservables();
    }

    /**
     * 
     * @return
     */
    public ExperimentWorkflow getIdentifyWorkflow() {
        return new IdentifyWorkflow();
    }
    
    
    // Get the format registry:
    public FormatRegistry fr = FormatRegistryFactory.getFormatRegistry();
    public Format unknown = fr.getFormatForURI( Format.extensionToURI("unknown") );

}
