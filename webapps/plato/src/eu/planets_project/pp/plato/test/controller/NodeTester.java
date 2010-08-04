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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;


public class NodeTester {
    
    @Test
    public void isCompletelySpecified(){
        Node node = new Node();
        Leaf leaf = new Leaf();
        leaf.setName("Name");
        node.addChild(leaf);
        node.addChild(leaf);
        Leaf leaf2 = new Leaf();
        leaf2.setName("Name2");
        node.addChild(leaf2);
        node.addChild(leaf2);
        List<String> nodelist = new ArrayList<String>();
        System.out.println(node.getChildren().size());
        node.isCompletelySpecified(nodelist);
        System.out.println(nodelist.size());
        for (String string : nodelist) {
            System.out.println(string);
        }
        
        assert(nodelist.size() == 2);
    }

}
