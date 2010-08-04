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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.UITree;

import eu.planets_project.pp.plato.model.PolicyNode;
import eu.planets_project.pp.plato.model.Plan;

/**
 * Class used to change the default operations of the rich:tree
 * - decides which nodes should be expanded
 *  
 */
@Scope(ScopeType.SESSION)
@Name("policyTreeHelper")
public class PolicyTreeHelperBean implements Serializable  {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3023151912623250519L;

//    @In
//    Plan selectedPlan;
   
    private final static Logger log = Logger.getLogger(PolicyTreeHelperBean.class);

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

    public void expandAll(PolicyNode n) {
        // add all nodes from the treenode downwards(!) to the set
        collapseAll();
        expand(n); //selectedPlan.getProjectBasis().getPolicyTree().getRoot());
    }
    
    private void expand(PolicyNode n) {
        expandedNodes.add(n);
        for (PolicyNode node: n.getChildren()) {
            expand(node);
        }
    }


}