package eu.planets_project.tb.gui.backing;


import javax.faces.component.html.HtmlInputText;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;

import eu.planets_project.tb.api.model.*;
import eu.planets_project.tb.impl.model.*;

public class CreateExp {

	  private HtmlInputText ename;

	  public CreateExp() {
	  }
	  
	 public String CreateExpAction(){
	    
	   // Get userid info from managed bean
	   UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");      
		 
	   // Create new Experiment     
	   Experiment newExp = new ExperimentImpl();
	   BasicProperties props = new BasicPropertiesImpl();
	   props.setExperimentName(ename.getValue().toString());
	   // set current User as experimenter
	   props.setExperimenter(currentUser.getUserid());   
	   ExperimentSetup expSetup = new ExperimentSetupImpl();
	   expSetup.setBasicProperties(props);
	   newExp.setExperimentSetup(expSetup);
  
   
	   TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
	   testbedMan.registerExperiment(newExp);
	   	    
	   
	   return "success";  
	 }


	  public void setEname(HtmlInputText ename) {
	    this.ename = ename;
	  }

	  public HtmlInputText getEname() {
	    return ename;
	  }


	
	
}
