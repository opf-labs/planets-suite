package eu.planets_project.tb.gui.util;

import java.util.Map;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;


/**
 * Utility class with useful methods for JSF/Web apps
 */


public class JSFUtil 
{
  
    public static Object getManagedObject(String objectName)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    ELResolver resolver = context.getApplication().getELResolver();
    Object requestedObject =  resolver.getValue(context.getELContext(), null, objectName);
    return  requestedObject;
  }
  
 
  public static void storeOnSession(FacesContext ctx, String key, Object object) {
      Map<String, Object> sessionState = ctx.getExternalContext().getSessionMap();
      sessionState.put(key, object);
  }
  
}