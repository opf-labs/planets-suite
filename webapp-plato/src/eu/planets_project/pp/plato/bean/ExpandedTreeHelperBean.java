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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.UITree;

/**
 * Class used to change the default operations of the rich:tree
 * - always expands nodes
 *  
 */
@Scope(ScopeType.SESSION)
@Name("expandedTreeHelper")
public class ExpandedTreeHelperBean implements Serializable  {
    
    /**
     * 
     */
    private static final long serialVersionUID = -2829259843787079128L;

    public Boolean adviseNodeOpened(UITree tree) {
        return true;
    }
    
}