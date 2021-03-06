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

package eu.planets_project.pp.plato.model.aggregators;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;

/**
 * This {@link Aggregator} class performs multiplication,
 * i.e. the result value for a node is the result of multiplying
 * the values of the child nodes. 
 * Does not consider weightings!
 * @author cbu
 *
 */
public class Multiplication extends Aggregator {

    /**
     * 
     */
    private static final long serialVersionUID = 4826097775805428124L;

    /**
     * returns <ul>
     * <li>for a {@link Leaf}: the value of the Alternative provided as parameter</li>
     * <li>for a {@link Node}: the result of multiplying the values of all children.</li>
     * </ul>
     * @param n the node for which the aggregated value shall be cmputed
     * @param alternative the Alternative for which the aggregated value shall be computed 
     */
    public double getAggregatedValue(TreeNode n, Alternative alternative) {
            
            if (n instanceof Leaf) {
                return ((Leaf)n).getResult(alternative);
            } else {
                // multiply the values of all children
                double d = 1.0;
                for (TreeNode child: n.getChildren()) {
                        d *= getAggregatedValue(child,alternative);
                }
                return d;
            }
	}

}
