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

package eu.planets_project.pp.plato.validators;

import java.util.List;

import eu.planets_project.pp.plato.model.tree.TreeNode;


/**
 */
public interface ITreeValidator {

    /**
     * This method validates the whole tree (branch) provided according to the INodeValidator that
     * is given to it. It traverses through the model
     * 
     * @param validator The Validator that calls the right methods in every TreeNode
     * @param nodes List of TreeNodes that fail validation.
     * @param showValidationErrors true if validation error shall be displayed
     * @return TRUE if tree validates, FALSE if not
     */
    boolean validate(TreeNode node, INodeValidator validator,
            List<TreeNode> nodes,
            boolean showValidationErrors);

}
