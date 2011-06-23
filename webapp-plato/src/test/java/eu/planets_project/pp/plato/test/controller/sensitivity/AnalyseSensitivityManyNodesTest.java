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

import java.util.List;
import java.util.Vector;

import eu.planets_project.pp.plato.bean.ResultNode;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.aggregators.WeightedSum;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.sensitivity.IWeightModifier;

public class AnalyseSensitivityManyNodesTest  {
    
    private Node root = new Node();

    private List<SimpleLeaf> children = new Vector<SimpleLeaf>();
    
    private SimpleLeaf l1, l2;
    
    private List<Alternative> alternatives = new Vector<Alternative>();
    
    private Alternative a1 = new Alternative("Alternative 1", "Alternative 1");
    private Alternative a2 = new Alternative("Alternative 2", "Alternative 2");
    
    private IWeightModifier weightModifier = SensitivityAnalysisTestFactory.getWeightModifier();
    
    public AnalyseSensitivityManyNodesTest() {
        alternatives.add(a1);
        alternatives.add(a2);
        for(int i = 0; i < 8; i++) {
            SimpleLeaf x = new SimpleLeaf();
            x.setResult(a1, 1.0);
            x.setResult(a2, 0.75);
            root.addChild(x);
            children.add(x);
        }
        l1 = new SimpleLeaf();
        l1.setResult(a1, 0.5);
        l1.setResult(a2, 1.5);
        root.addChild(l1);
        l2 = new SimpleLeaf();
        l2.setResult(a1, 0.5);
        l2.setResult(a2, 1.5);
        root.addChild(l2);
        
    }
    
    public void testUnstableWeights() {
        for(SimpleLeaf sl : children) {
            sl.setWeight(0.1);
        }
        l1.setWeight(0.1);
        l2.setWeight(0.1);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(resultRoot.isSensitive());
    }
    
    public void testStableWeights() {
        for(SimpleLeaf sl : children) {
            sl.setWeight(0.05);
        }
        l1.setWeight(0.3);
        l2.setWeight(0.3);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!resultRoot.isSensitive());
    }
    
    public void testYetUnstableWeights() {
        for(SimpleLeaf sl : children) {
            sl.setWeight(0.11);
        }
        l1.setWeight(0.06);
        l2.setWeight(0.06);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(resultRoot.isSensitive());
    }
    
    public void testYetStableWeights() {
        for(SimpleLeaf sl : children) {
            sl.setWeight(0.084);
        }
        
        l1.setWeight(0.164);
        l2.setWeight(0.164);
        
        
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!resultRoot.isSensitive());
    }
    
    public void testYetUnstableWeights2() {
        for(SimpleLeaf sl : children) {
            sl.setWeight(0.086);
        }
        
        l1.setWeight(0.156);
        l2.setWeight(0.156);
        
        
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(resultRoot.isSensitive());
    }

    
}
