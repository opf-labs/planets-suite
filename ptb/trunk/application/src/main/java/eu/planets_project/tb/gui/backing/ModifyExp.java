package eu.planets_project.tb.gui.backing;


import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.gui.util.JSFUtil;

import eu.planets_project.tb.api.model.*;

public class ModifyExp {
    
   
    
    
    public ModifyExp() {
    }
    
     
    public String saveModificationAction() {
            
            Experiment editExp = (Experiment)JSFUtil.getManagedObject("EditExperiment");
                  
            TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
            
            // add/change attributes
            // ...
            
            testbedMan.updateExperiment(editExp);
            
            return null;
    	
 
    }
    
    
    
    
    
    
    
    
}
