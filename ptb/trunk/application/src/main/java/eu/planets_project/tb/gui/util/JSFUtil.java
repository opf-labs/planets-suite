package eu.planets_project.tb.gui.util;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


/**
 * Utility class with useful methods for JSF/Web apps
 */


public class JSFUtil 
{
  
  
  public static ValueBinding getValueBinding(String expression)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    return context.getApplication().createValueBinding(expression);
  }
  
  public static String getValueBindingString(String expression)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    ValueBinding currentBinding =  context.getApplication().createValueBinding(expression);
    return (String) currentBinding.getValue(context);
    
  }
  
  
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
  
}