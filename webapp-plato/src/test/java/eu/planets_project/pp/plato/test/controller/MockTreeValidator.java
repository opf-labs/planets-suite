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
package eu.planets_project.pp.plato.test.controller;

import java.util.List;

import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.validators.INodeValidator;
import eu.planets_project.pp.plato.validators.ITreeValidator;

public class MockTreeValidator implements ITreeValidator {

    public boolean validate(TreeNode node,
            INodeValidator validator, List<TreeNode> nodes,
            boolean showValidationErrors) {
        return true;
    }

}
