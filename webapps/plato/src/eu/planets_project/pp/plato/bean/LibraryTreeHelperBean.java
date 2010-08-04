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
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.UITree;

import eu.planets_project.pp.plato.model.tree.LibraryRequirement;
import eu.planets_project.pp.plato.model.tree.TreeNode;

/**
 * Class used to change the default operations of the rich:tree
 * - decides which nodes should be expanded
 *  
 * 
 */
@Scope(ScopeType.SESSION)
@Name("libraryTreeHelper")
public class LibraryTreeHelperBean implements Serializable {
    private static final long serialVersionUID = 7243664711182931065L;

    private final static Logger log = Logger.getLogger(LibraryTreeHelperBean.class);

    private Set<Object> expandedNodes = new HashSet<Object>();
    


    public Boolean adviseNodeOpened(UITree tree) {
        if (expandedNodes.contains(tree.getRowData())) {
            return true;
        }
        return null;
    }
    

    public void collapseAll() {
        expandedNodes.clear();
    }

    public void expandAll(LibraryRequirement n) {
        // add all nodes from the currently focused downwards(!) to the set
        collapseAll();
        expand(n);
    }
    
    public void expand(TreeNode n) {
        expandedNodes.add(n);
        for (TreeNode node: n.getChildren()) {
            expand(node);
        }
    }
    
    public void closeNode(Object node) {
        expandedNodes.remove(node);
    }
    
    


}