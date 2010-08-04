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
 * Validation of nodes in the objective tree, following the visitor pattern.
 * 
 * The tree has to be traversed in everyone of these classes,
 * so what we do is put the algorithm for Traversing into another class. This
 * one then gets an INodeValidator object and calls its validateNode() Method. This
 * Method then callse the iscompletelyspecified, iscompletelyevaluated or
 * iscompletelytransformed Method for a certain Node and returns if the node
 * validates.
 * 
 * @author florian Motlik
 * 
 */
public interface INodeValidator {

    /**
     * checks if the Node is valid
     * 
     * @param node
     *            The node to validate
     * @param errorMessages
     *            Nodes can put error messages into this list, which will be
     *            displayed later
     * @param nodes
     *            List contains TreeNodes that do not validate (Leaf and Node
     *            implementation decide if they are added) - invalid nodes should be added hereto
     * @return if the Node validates in the certain circumstances (specified,
     *         evaluated, transformed)
     *  @see TreeNode#isCompletelyEvaluated(List, List, List)
     *  @see TreeNode#isCompletelySpecified(List)
     *  @see TreeNode#isCompletelyTransformed(List, List)
     *  @see TreeNode#isCorrectlyWeighted(List)
     */
    public boolean validateNode(TreeNode node, List<String> errorMessages,
            List<TreeNode> nodes);
}
