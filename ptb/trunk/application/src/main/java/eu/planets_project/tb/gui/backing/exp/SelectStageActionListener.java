/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
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
public class SelectStageActionListener implements ActionListener {
    private Log log = LogFactory.getLog(SelectStageActionListener.class);

    public void processAction(ActionEvent anEvent) throws AbortProcessingException {
        log.info("Processing event.");

      ExperimentStageBean targetBean = null;

      UIComponent tmpComponent = anEvent.getComponent();

      while (null != tmpComponent && !(tmpComponent instanceof UIData)) {
        tmpComponent = tmpComponent.getParent();
      }

      if (tmpComponent != null && (tmpComponent instanceof UIData)) {
        Object tmpRowData = ((UIData) tmpComponent).getRowData();
        
        log.info("Got class: "+tmpRowData.getClass().getCanonicalName());
        
        if (tmpRowData instanceof ExperimentStageBean ) {
          targetBean = (ExperimentStageBean) tmpRowData;

          ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
          expBean.setSelectedStage( targetBean );
        }
        
      }

    }
    
}