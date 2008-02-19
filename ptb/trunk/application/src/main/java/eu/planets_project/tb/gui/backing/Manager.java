package eu.planets_project.tb.gui.backing;

import javax.faces.context.FacesContext;
import eu.planets_project.tb.gui.backing.admin.RegisterTBServices;
import eu.planets_project.tb.gui.backing.admin.ManagerTBServices;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Andrew Lindley, ARC
 * TestbedManager class
 *  - init all backing beans
 *  - get handle on registered managed beans
 */

public class Manager {
    
	private Log log = LogFactory.getLog(Manager.class);
    
    public Manager() {
    }
    
    public String initExperimentAction() {
		ExperimentBean expBean = new ExperimentBean();
		// Put Bean into Session; accessible later as #{ExperimentBean}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
	    return "success";
    }
    
    
    /**
     * @author alindley
     * (re)Init and register an admin backing bean (scope = session) for the 
     * wizzard: register_TBServices
     */
    public String initRegisterTBService(){
    	RegisterTBServices regTBSer = new RegisterTBServices();
		// Put Bean into Session; accessible later as #{ExperimentBean}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("RegisterTBServiceBean", regTBSer);
	    return "register_TBservices";
    }
    
    /**
     * @author alindley
     * (re)Init and render the content of the TBServiceRegistry for displaying 
     * all registered services and their metadata on the screen
     * overview_registeredTBServices.xhtml
     * @return
     */
    public String initTBServiceDisplayer(){
    	ManagerTBServices renderer = new ManagerTBServices();
		// Put Bean into Session; accessible later as #{TBServiceRenderer}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("TBServiceRenderer", renderer);
	    return "render_TBservices";
    }
    
	/**
	 * @author alindley
	 * Helper to fetch the current WSClientBean from the session
	 * @return
	 */
	public WSClientBean getCurrentWSClientBean(){
		return (WSClientBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("WSClientBean"); 
	}
	
	
    /**
     * @author alindley
     * (re)Init and manage the content of the TBServiceRegistry for removing
     * services and rendering their metadata on the screen
     * remove_registeredTBServices.xhtml
     * @return
     */
    public String initTBServiceManager(){
    	ManagerTBServices manager = new ManagerTBServices();
		// Put Bean into Session; accessible later as #{TBServiceRenderer}
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().put("TBServiceManager", manager);
	    return "manage_TBservices";
    }

}
