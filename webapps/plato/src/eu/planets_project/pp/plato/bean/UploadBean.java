/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.bean;

import java.io.Serializable;


//@Name("uploadBean")
//@Scope(ScopeType.SESSION)
//@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class UploadBean implements Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = -2336442665952907701L;

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
//    @Destroy
//    @Remove
//    public void destroy() {
//    }
}
