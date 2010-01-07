package eu.planets_project.tb.gui.backing.exp.utils;

import java.util.List;

import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Template;
import eu.planets_project.tb.gui.backing.exp.ExpTypeExecutablePP.ServiceBean;

public interface ExpTypeWeeBean {
    
    /**
     * The object containing the workflow's configuration - that's the one being submitted to the WEE
     * @param wfConfig
     */
    public void setWeeWorkflowConf(WorkflowConf wfConfig);
    
    public WorkflowConf getWeeWorkflowConf();
    
    public boolean isValidCurrentConfiguration();
    
    /**
     * Builds the currentXMLConfig from the given service/param configuration
     * and writes it to a temporary file that's accessible via an external url ref.
     * This can be used within the browser to download the currentXMLConfig
     * @return
     */
    public String getTempFileDownloadLinkForCurrentXMLConfig();
    
    public boolean isTemplateAvailableInWftRegistry();
    
    /**
	 *  Takes the current bean's configuration (e.g. service, input, output format) and
	 *  creates a WorkflowConf object.
	 */
	public WorkflowConf buildWorkflowConfFromCurrentConfiguration()  throws Exception;
    
}
