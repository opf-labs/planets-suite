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
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.sensitivity.ISensitivityAnalysisResult;
import eu.planets_project.pp.plato.sensitivity.IWeightModifier;

/**
 * This is a JUnit test for the sensitivity analysis.
 * 
 * It is similar to the AnalyseSensitivitySimpleTest except that in this test the instabilities are
 * deeper in the tree (not the first layer of children).
 * 
 * The tree has the following structure:
 * 
 * root
 *   -child1 [0.6] 
 *     -leaf11 [x] (1.5, 1.0)
 *     -leaf12 [y] (1.0, 1.5)
 *   -child2 [0.4]
 *     -leaf21 [0.3] (2, 2.3)
 *     -leaf21 [0.7] (1.0, 1.0)
 * 
 * The numbers in [] are the weights, the numbers in () are values for the two alternatives.
 *     
 * The child1, child2, leaf21 and leaf22 have fixed values and fixed weighs. We modify the weights x and y
 * of leaf11 and leaf12 to create instability (or not) and then run the sensitivity analysis.
 *
 * The tipping point is x=0.56, y=0.44. If x gets bigger (and y smaller) the alternative a1 wins. If x gets
 * smaller alternative a2 wins. 
 * 
 * @author Jan Zarnikov
 *
 */
public class AnalyseSensitivityDeepTest  {
    
    private Node root = new Node();
    private Node child1 = new Node();
    private Node child2 = new Node();
    
    public SimpleLeaf leaf11 = new SimpleLeaf();
    public SimpleLeaf leaf12 = new SimpleLeaf();
    public SimpleLeaf leaf21 = new SimpleLeaf();
    public SimpleLeaf leaf22 = new SimpleLeaf();
    
    public List<Alternative> alternatives = new Vector<Alternative>();
    
    private IWeightModifier weightModifier = SensitivityAnalysisTestFactory.getWeightModifier();
    
    Alternative a1 = new Alternative("Alternative 1", "Alternative 1");
    Alternative a2 = new Alternative("Alternative 2", "Alternative 2");
    
    public AnalyseSensitivityDeepTest() {
        alternatives.add(a1);
        alternatives.add(a2);
        root.addChild(child1);
        root.addChild(child2);
        
        child1.setWeight(0.6);
        child2.setWeight(0.4);
        
        child1.addChild(leaf11);
        child1.addChild(leaf12);
        child2.addChild(leaf21);
        child2.addChild(leaf22);
        
        leaf21.setWeight(0.3);
        leaf22.setWeight(0.7);
        
        leaf21.setResult(a1, 2.0);
        leaf21.setResult(a2, 2.3);
        leaf22.setResult(a1, 1.0);
        leaf22.setResult(a2, 1.0);
        
        leaf11.setResult(a1, 1.5);
        leaf11.setResult(a2, 1.0);
        leaf12.setResult(a1, 1.0);
        leaf12.setResult(a2, 1.5);
    }
    
    public void testUnstableWeights() {
        leaf11.setWeight(0.55);
        leaf12.setWeight(0.45);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(getResult(resultRoot, child1).isSensitive());
        assert(resultRoot.isAnyChildSensitive());
        assert(resultRoot.isSensitive(true));
    }
    
    public void testYetUnstableWeights() {
        leaf11.setWeight(0.59);
        leaf12.setWeight(0.41);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        //assert(resultRoot.isSensitive());
        assert(getResult(resultRoot, child1).isSensitive());
    }    
    
    public void testStableWeights() {
        leaf11.setWeight(0.81);
        leaf12.setWeight(0.29);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!getResult(resultRoot, child1).isSensitive());
    }
    
    
    public void testYetStableWeights() {
        leaf11.setWeight(0.69);
        leaf12.setWeight(0.31);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(!getResult(resultRoot, child1).isSensitive());
    }
    
    /**
     * Search the result tree for the result of the senstitivity analysis of a specific tree node. 
     * @param root
     * @param node
     * @return
     */
    private ISensitivityAnalysisResult getResult(ResultNode root, TreeNode node) {
        ISensitivityAnalysisResult result = null;
        if(root.getTreeNode().equals(node)) {
            return root.getSensitivityAnalysisResult();
        }
        for(ResultNode child : root.getChildren()) {
            result = getResult(child, node);
            if(result != null) {
                return result;
            }
        }
        return result;
    }
    
    
}
