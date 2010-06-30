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
package eu.planets_project.tb.gui.tags;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;

public final class GetUser extends TagHandler {

	private static Log log = LogFactory.getLog(GetUser.class);
  

   /**
   * @param config
   */
  public GetUser(TagConfig config) {
      super(config);
  }

  /**
   */
  public void apply(FaceletContext ctx, UIComponent parent)
          throws IOException, FacesException, ELException {
     		//log.debug("in Tag: GetUser!");		
     		FacesContext context = FacesContext.getCurrentInstance();
  			//Object session = context.getExternalContext().getSession(false);
  			//if (session != null) {
  				UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
  				//UserBean user = (UserBean)(((HttpSession)session).getAttribute("userBean"));
	     		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	     		if (user.getUserid() == null) {
	     			String userId = request.getRemoteUser();
	     			// JAAS Login before?
	     			if (userId != null) {        		
	     				//user = new UserBean();
	     				user.setUserid(userId);
	     				//((HttpSession)session).setAttribute("userBean",user);
	     			}
	     		}
	     		log.debug("user is: '" + user.getUserid() + "'");
  			//}
  	}
}
