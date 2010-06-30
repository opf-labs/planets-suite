/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.gui.util;

import java.io.IOException;

import java.util.Map;

import javax.el.ELResolver;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Utility class with useful methods for JSF/Web apps
 */


public class JSFUtil 
{
    private static Log log = LogFactory.getLog(JSFUtil.class);
  
    public static Object getManagedObject(String objectName)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    if( context == null ) return null;
    ELResolver resolver = context.getApplication().getELResolver();
    Object requestedObject =  resolver.getValue(context.getELContext(), null, objectName);
    return  requestedObject;
  }
  
 
  public static void storeOnSession(FacesContext ctx, String key, Object object) {
      Map<String, Object> sessionState = ctx.getExternalContext().getSessionMap();
      sessionState.put(key, object);
  }
  
  /**
   * Issues a redirect, e.g. JSFUtil.redirect( "/pages/page.faces" );
   * @param uri The URI in the application context to redirect to.
   */
  public static final void redirect( String uri ) {
      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext externalContext = context.getExternalContext();
      String path = externalContext.getRequestContextPath();
      String url = externalContext.encodeResourceURL( path + uri );
      log.info("Issuing redirect to: "+url);
      try {
          externalContext.redirect( url );
      } catch (IOException ex) {
          throw new FacesException( "Failed to redirect to uri: " + uri + "( " + url + " )", ex );
      }
  }
  
}