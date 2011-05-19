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

/**
 * This is a JUnit test for the sensitivity analysis.
 * 
 * It defines a Node with two Leafs as children and two Alternatives.
 * In first Leaf sf1, the Alternative a2 is better, in the second Leaf sf2 the first Alternative a1 is better.
 * 
 * This means that the winning Alternative depends on the weights of the Leafs. If the Leaf sf1 has bigger weight 
 * (compared to sf2) than the Alternative a2 wins and vice versa.
 * 
 * The test cases use different weights - some unstable (0.49 -0.51) and some stable (0.19 - 0.81).
 * 
 * @author Jan Zarnikov
 *
 */
public class AnalyseSensitivitySimpleTest {
    
    private Node root;
    
    private IWeightModifier weightModifier = SensitivityAnalysisTestFactory.getWeightModifier();
    
    
    private List<Alternative> alternatives = new Vector<Alternative>();
    
    SimpleLeaf sf1 = new SimpleLeaf();
    SimpleLeaf sf2 = new SimpleLeaf();
    
    Alternative a1 = new Alternative("Alternative 1", "Alternative 1");
    Alternative a2 = new Alternative("Alternative 2", "Alternative 2");
    
    @BeforeClass
    public void setUp() {
        alternatives.add(a1);
        alternatives.add(a2);
        root = new Node();
        root.addChild(sf1);
        root.addChild(sf2);
        
    }
    
    @Test
    public void testUnstableWeights() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.49 and 0.51
        // now the alternative a1 is better
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // in other words the current weighting is unstable
        sf1.setWeight(0.49);
        sf2.setWeight(0.51);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        assert(resultRoot.isSensitive());
    }
    
        
    public void testYetunstableWeights() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.47 and 0.53
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights to change the outcome of the alternatives
        // (0.47->0.51 and 0.53->0.49)
        // a change by 0.04 is not much which means this weighting is unstable 
        sf1.setWeight(0.47);
        sf2.setWeight(0.53);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(resultRoot.isSensitive());
    }
    
    public void testYetunstableWeights2() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.47 and 0.53
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights to change the outcome of the alternatives
        // (0.47->0.51 and 0.53->0.49)
        // a change by 0.04 is not much which means this weighting is unstable 
        sf1.setWeight(0.46);
        sf2.setWeight(0.54);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(resultRoot.isSensitive());
    }
    
    public void testYetunstableWeights3() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.47 and 0.53
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights to change the outcome of the alternatives
        // (0.47->0.51 and 0.53->0.49)
        // a change by 0.04 is not much which means this weighting is unstable 
        sf1.setWeight(0.45);
        sf2.setWeight(0.55);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        assert(resultRoot.isSensitive());
    }
    
    public void testStableWeights() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.19 and 0.81
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights a lot to change the outcome of the alternatives
        // (0.19->0.51 and 0.81->0.49), this means the weighting is stable 
        sf1.setWeight(0.19);
        sf2.setWeight(0.81);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!resultRoot.isSensitive());
    }
    
    public void testYetStableWeights() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.4 and 0.6
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights to change the outcome of the alternatives
        // (0.4->0.51 and 0.6->0.49)
        // a change by 0.11 is considered a lot, therefor this weighting is stable
        sf1.setWeight(0.4);
        sf2.setWeight(0.6);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!resultRoot.isSensitive());
    }
    
    public void testYetStableWeights2() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.4 and 0.6
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights to change the outcome of the alternatives
        // (0.4->0.51 and 0.6->0.49)
        // a change by 0.11 is considered a lot, therefor this weighting is stable
        sf1.setWeight(0.44);
        sf2.setWeight(0.56);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!resultRoot.isSensitive());
    }
    
    public void testYetStableWeights3() {
        sf1.setResult(a1, 1.0);
        sf1.setResult(a2, 1.1);
        sf2.setResult(a1, 1.1);
        sf2.setResult(a2, 1.0);
        
        // we set the weight of the leafs to 0.4 and 0.6
        // now the alternative a1 is better one
        // at 0.5 and 0.5 the order of alternatives flips
        // that means that if we set the weights to sf1=0.51 and sf2=0.51
        // the alternative a2 becomes the new winner
        // you have to move the weights to change the outcome of the alternatives
        // (0.4->0.51 and 0.6->0.49)
        // a change by 0.11 is considered a lot, therefor this weighting is stable
        sf1.setWeight(0.43);
        sf2.setWeight(0.57);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!resultRoot.isSensitive());
    }
    

}
