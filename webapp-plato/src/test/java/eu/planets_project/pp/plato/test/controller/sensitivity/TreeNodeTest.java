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
package eu.planets_project.pp.plato.test.controller.sensitivity;

import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;

public class TreeNodeTest {

    @Test
    public void testNormalizeWeights() {
        Node parent = new Node();
        Node child1 = new Node();
        child1.setWeight(10);
        Node child2 = new Node();
        child2.setWeight(1);
        Node child3 = new Node();
        child3.setWeight(0.1);
        parent.addChild(child1);
        parent.addChild(child2);
        parent.addChild(child3);
        parent.normalizeWeights();
        double sum = 0;
        for(TreeNode t : parent.getChildren()) {
            assert(t.getWeight() >= 0 || t.getWeight() <= 1);
            sum += t.getWeight();
        }
        assert(sum == 1.0);
        
    }
    
    @Test
    public void testNormalizeWeightsRandom() {
        Node parent = new Node();
        for(int i = 0; i < 100; i++) {
            Node child = new Node();
            child.setWeight(Math.random());
            parent.addChild(child);
        }
        parent.normalizeWeights();
        double sum = 0;
        for(TreeNode t : parent.getChildren()) {
            assert(t.getWeight() >= 0 || t.getWeight() <= 1);
            sum += t.getWeight();
        }
        // the following line would fail in most cases 
        // because java's handling of floating point numbers s*cks
        //assertEquals(sum, 1.0);
        
        // let's do some approximation instead:
        assert(Math.abs(sum - 1.0) < 0.0000001);
        
    }

}
