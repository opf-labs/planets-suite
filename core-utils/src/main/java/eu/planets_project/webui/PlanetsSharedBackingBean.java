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
/**
 * 
 */
package eu.planets_project.webui;

import java.io.File;
import java.net.MalformedURLException;

import javax.faces.context.FacesContext;

/**
 * Utility backing bean for Planets web components.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PlanetsSharedBackingBean {

	// TODO This shouldn't be hard coded, review role info and setup before V4
	private final static String adminRole = "admin";
    /**
     * A helper method to look-up the file path the to shared resources, e.g. facelets templates.
     * 
     * @return String containing the absolute file URI.
     */
	public String getSharedFileBasePath() {
        File homedir = new File(System.getProperty("jboss.server.home.dir"));
        File shareddir = new File( homedir, "deploy/jboss-web.deployer/ROOT.war");
        try {
            return shareddir.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            return "";
        }
    }
    

    /**
     * A helper to find the context that the shared resources are available from, e.g. css or images. 
     * 
     * @return String containing the shared resoure web context.
     */
    public String getSharedFileContext() {
        return "/";
    }

    /**
     * Boolean getter for the nav bar to check whether the current user is in the admin role.
     * 
     * @return true if the user is in the admin role, false otherwise
     */
    public boolean getIsAdmin() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(adminRole);
    }
}
