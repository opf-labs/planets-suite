/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RemoveDigitalObjectActionListener implements ActionListener {
    private PlanetsLogger log = PlanetsLogger.getLogger(RemoveDigitalObjectActionListener.class, "testbed-log4j.xml");

    @SuppressWarnings("unchecked")
    public void processAction(ActionEvent anEvent) throws AbortProcessingException {
        log.info("Processing event in RemoveDigitalObjectActionListener.");

      Map<String,String> targetBean = null;

      UIComponent tmpComponent = anEvent.getComponent();

      while (null != tmpComponent && !(tmpComponent instanceof UIData)) {
        tmpComponent = tmpComponent.getParent();
      }

      if (tmpComponent != null && (tmpComponent instanceof UIData)) {
        Object tmpRowData = ((UIData) tmpComponent).getRowData();
        if (tmpRowData instanceof Map ) {
          targetBean = (Map<String,String>) tmpRowData;

          // Look through the row data:
          for( String key: targetBean.keySet() )
              log.info("Got ['"+key+"']="+targetBean.get(key));

          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          expBean.removeExperimentInputData(targetBean.get("inputID"));
          log.info("Removed: "+targetBean.get("inputID"));
          
        }
      }

      //TODO Exception Handling if UIData not found or tmpRowBean of wrong type

    }
    
}