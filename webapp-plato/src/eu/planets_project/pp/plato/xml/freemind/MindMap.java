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

package eu.planets_project.pp.plato.xml.freemind;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.model.PolicyNode;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class MindMap {
    private Node root;

    private static final Log log = PlatoLogger.getLogger(MindMap.class);

    public void addChild(Node root) {
        if (this.root != null) {
            log.warn("root added twice on temp MindMap!");
        }
        this.root = root;
    }


    /**
     * This gets the complete ObjectiveTree out of the mindmap structure
     * @param hasUnits
     * @param hasLeaves deontes if the original mindmap contained leaves, or just nodes
     * @return {@link eu.planets_project.pp.plato.model.tree.ObjectiveTree}
     */
    public TreeNode getObjectiveTreeRoot(boolean hasUnits, boolean hasLeaves) {
        return root.createNode(hasUnits, hasLeaves);
    }

    public PolicyNode getPolicyTreeRoot() {
        return root.createPolicyNode();
    }


    public Node getRoot() {
        return root;
    }


    public void setRoot(Node root) {
        this.root = root;
    }
}
