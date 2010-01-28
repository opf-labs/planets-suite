/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.Map;


import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RemoveDigitalObjectActionListener implements ActionListener {
    private Log log = LogFactory.getLog(RemoveDigitalObjectActionListener.class);

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
          
          FacesContext context = FacesContext.getCurrentInstance();
  		  Object o1 = context.getExternalContext().getRequestParameterMap().get("stageName");
  		  String sInExperimentStage = null;
  		  if(o1!=null){
  			sInExperimentStage = (String)o1; 
  		  }

          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          if((sInExperimentStage==null)||(sInExperimentStage.equals("design experiment"))){
	          expBean.removeExperimentInputData(targetBean.get("inputID"));
	          log.info("Removed: "+targetBean.get("inputID")+"in design experiment stage");
          }
          if((sInExperimentStage!=null)||(sInExperimentStage.equals("evaluate experiment"))){
        	  expBean.removeEvaluationExternalDigoRef(targetBean.get("inputID"));
        	  log.info("Removed: "+targetBean.get("inputID")+"in evaluate experiment stage");
          }
        }
      }

      //TODO Exception Handling if UIData not found or tmpRowBean of wrong type

    }
    
}