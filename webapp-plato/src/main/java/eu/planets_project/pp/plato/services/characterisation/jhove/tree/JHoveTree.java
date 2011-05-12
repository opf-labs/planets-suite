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

import java.io.Serializable;

/**
 * Simple Tree used to be displayed from the Richfaces
 * 
 * @author riccardo
 * 
 */
public class JHoveTree implements Serializable {

    private static final long serialVersionUID = 5873765450869688894L;

    private JHoveTreeNode root;

    public JHoveTree() {
        initRoot("Tree not initialized");
    }

    public void initRoot(String name) {
        root = new JHoveTreeNode(name, "node");
    }

    public JHoveTreeNode getRoot() {
        return root;
    }

    public void setRoot(JHoveTreeNode root) {
        this.root = root;
    }

    public void destroy() {

    }

}