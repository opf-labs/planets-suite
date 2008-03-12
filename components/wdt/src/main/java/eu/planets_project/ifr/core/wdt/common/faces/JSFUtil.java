package eu.planets_project.ifr.core.wdt.common.faces;

import java.util.Map;

import javax.faces.context.FacesContext;
//import javax.faces.el.ValueBinding;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;



/**
 * Utility class with useful methods for JSF/Web apps
 */


public class JSFUtil 
{
  
  
  /*public static ValueBinding getValueBinding(String expression)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    return context.getApplication().createValueBinding(expression);
  }
  
  public static String getValueBindingString(String expression)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    ValueBinding currentBinding =  context.getApplication().createValueBinding(expression);
    return (String) currentBinding.getValue(context);
    
  }*/
  
  
 	public static Object getManagedObject(String objectName)
  {  		
    FacesContext context = FacesContext.getCurrentInstance();
    Object requestedObject =  context.getApplication().getVariableResolver().resolveVariable(context, objectName);
    return  requestedObject;
  }
  
 
  public static void storeOnSession(FacesContext ctx, String key, Object object) {
      Map sessionState = ctx.getExternalContext().getSessionMap();
      sessionState.put(key, object);
  }
  
  public static void redirectToView(String viewId) {
  		FacesContext facesContext = FacesContext.getCurrentInstance();
			String currentViewId = facesContext.getViewRoot().getViewId();
			
			if (viewId != null && (!viewId.equals(currentViewId))) {
				ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
				UIViewRoot viewRoot = viewHandler.createView(facesContext, viewId);
				facesContext.setViewRoot(viewRoot);
				facesContext.renderResponse();
			}
  }
  
  //access to session scoped bean
  //FacesContext facesContext = FacesContext.getCurrentInstance();
	//ApplicationFactory appFactory = (ApplicationFactory)FactoryFinder.findFactory(FactoryFinder.APPLICATION_FACTORY);
	//Application app = appFactory.getApplication();
	//Object o = (Object)app.getValueBinding("fileBrowser").getValue(facesContext);
	
}