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
package eu.planets_project.pp.plato.services.characterisation.jhove.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * JHove TreeNode for the Tree {@link JHoveTree}. Each Node contains a name and
 * a type. Nodes can have be of different types. Node type is used to manage the nodes 
 * differently while displaying them in the {@link JHoveTree}
 * 
 * @author riccardo
 * 
 */
public class JHoveTreeNode {
    private String name;

    private String type;

    private List<JHoveTreeNode> children;
/**
 * Creates a Node with the given name and type
 * @param name
 * @param type
 */
    public JHoveTreeNode(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
/**
 * Get all the children of the Node
 * @return
 */
    public List<JHoveTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<JHoveTreeNode> children) {
        this.children = children;
    }

    /**
     * Add child only if the name or the child isn't null. Initialize
     * children if necessary.
     * 
     * The nodes that don't have data (contained in the name variable)
     * or don't have children (aren't container of other properties)
     * shouldn't be added (null nodes haven't to be added)
     * 
     * This control is particular useful to limit the number of nodes created automatically
     * (some of them could have not property been initialized) 
     * 
     * @param child
     */
    public void addChild(JHoveTreeNode child) {
        if ((child.getName() != null && child.getName().compareTo("") != 0)
                || (child.getChildren() != null && child.getChildren().size() > 0)) {
            if (children == null)
                children = new ArrayList<JHoveTreeNode>();
            children.add(child);
        }

    }
   
}