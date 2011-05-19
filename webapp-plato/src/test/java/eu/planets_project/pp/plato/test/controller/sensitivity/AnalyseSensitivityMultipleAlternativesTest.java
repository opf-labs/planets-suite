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
import eu.planets_project.pp.plato.sensitivity.SimpleIterativeWeightModifier;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnalyseSensitivityMultipleAlternativesTest  {
    
    private Node root;
    
    private IWeightModifier weightModifier = new SimpleIterativeWeightModifier();
    
    
    private List<Alternative> alternatives = new Vector<Alternative>();
    
    SimpleLeaf sf1 = new SimpleLeaf();
    SimpleLeaf sf2 = new SimpleLeaf();
    SimpleLeaf sf3 = new SimpleLeaf();
    
    Alternative a1 = new Alternative("Alternative 1", "Alternative 1");
    Alternative a2 = new Alternative("Alternative 2", "Alternative 2");
    Alternative a3 = new Alternative("Alternative 3", "Alternative 3");
    Alternative a4 = new Alternative("Alternative 4", "Alternative 4");
    Alternative a5 = new Alternative("Alternative 5", "Alternative 5");
    
    @BeforeClass
    public void setUp() {
        alternatives.add(a1);
        alternatives.add(a2);
        alternatives.add(a3);
        alternatives.add(a4);
        alternatives.add(a5);
        
        root = new Node();
        root.addChild(sf1);
        root.addChild(sf2);
        root.addChild(sf3);
        
    }
    @Test
    public void testUnstableWeights() {
        double sf1Weight = 0.4;
        double sf2Weight = 0.25;
        double sf3Weight = 0.15;
        
        sf1.setResult(a1, 0.3/sf1Weight);
        sf1.setResult(a2, 0.4/sf1Weight);
        sf1.setResult(a3, 0.3/sf1Weight);
        sf1.setResult(a4, 0.4/sf1Weight);
        sf1.setResult(a5, 0.3/sf1Weight);
        
        sf2.setResult(a1, 1.2/sf2Weight);
        sf2.setResult(a2, 0.9/sf2Weight);
        sf2.setResult(a3, 0.9/sf2Weight);
        sf2.setResult(a4, 1.0/sf2Weight);
        sf2.setResult(a5, 0.8/sf2Weight);
        
        sf3.setResult(a1, 0.8/sf3Weight);
        sf3.setResult(a2, 0.9/sf3Weight);
        sf3.setResult(a3, 0.9/sf3Weight);
        sf3.setResult(a4, 0.7/sf3Weight);
        sf3.setResult(a5, 0.9/sf3Weight);
        
        sf1.setWeight(sf1Weight);
        sf2.setWeight(sf2Weight);
        sf3.setWeight(sf3Weight);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));
        
        assert(resultRoot.isSensitive());
    }

    @Test
    public void testYetUnstableWeights() {
        double sf1Weight = 1.0/3.0;
        double sf2Weight = 1.0/3.0;
        double sf3Weight = 1.0/3.0;
        
        sf1.setResult(a1, 1.54/sf1Weight);
        sf1.setResult(a2, 1.52/sf1Weight);
        sf1.setResult(a3, 1.5/sf1Weight);
        sf1.setResult(a4, 1.5/sf1Weight);
        sf1.setResult(a5, 1.5/sf1Weight);
        
        sf2.setResult(a1, 1.5/sf2Weight);
        sf2.setResult(a2, 1.5/sf2Weight);
        sf2.setResult(a3, 1.54/sf2Weight);
        sf2.setResult(a4, 1.52/sf2Weight);
        sf2.setResult(a5, 1.5/sf2Weight);
        
        sf3.setResult(a1, 1.5/sf3Weight);
        sf3.setResult(a2, 1.5/sf3Weight);
        sf3.setResult(a3, 1.5/sf3Weight);
        sf3.setResult(a4, 1.5/sf3Weight);
        sf3.setResult(a5, 1.54/sf3Weight);
        
        sf1.setWeight(sf1Weight);
        sf2.setWeight(sf2Weight);
        sf3.setWeight(sf3Weight);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));

        assert(resultRoot.isSensitive());
    }
    
    @Test
    public void testYetStableWeights() {
        double sf1Weight = 1.0/3.0;
        double sf2Weight = 1.0/3.0;
        double sf3Weight = 1.0/3.0;
        
        sf1.setResult(a1, 1.65/sf1Weight);
        sf1.setResult(a2, 1.6/sf1Weight);
        sf1.setResult(a3, 1.55/sf1Weight);
        sf1.setResult(a4, 1.5/sf1Weight);
        sf1.setResult(a5, 1.45/sf1Weight);
        
        sf2.setResult(a1, 1.5/sf2Weight);
        sf2.setResult(a2, 1.5/sf2Weight);
        sf2.setResult(a3, 1.5/sf2Weight);
        sf2.setResult(a4, 1.5/sf2Weight);
        sf2.setResult(a5, 1.5/sf2Weight);
        
        sf3.setResult(a1, 1.5/sf3Weight);
        sf3.setResult(a2, 1.5/sf3Weight);
        sf3.setResult(a3, 1.5/sf3Weight);
        sf3.setResult(a4, 1.5/sf3Weight);
        sf3.setResult(a5, 1.5/sf3Weight);
        
        sf1.setWeight(sf1Weight);
        sf2.setWeight(sf2Weight);
        sf3.setWeight(sf3Weight);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));

        assert(!resultRoot.isSensitive());
    }
    
    @Test
    public void testStableWeights() {
        double sf1Weight = 0.9;
        double sf2Weight = 0.05;
        double sf3Weight = 0.05;
        
        sf1.setResult(a1, 2.5/sf1Weight);
        sf1.setResult(a2, 2.0/sf1Weight);
        sf1.setResult(a3, 1.5/sf1Weight);
        sf1.setResult(a4, 1.0/sf1Weight);
        sf1.setResult(a5, 0.5/sf1Weight);
        
        sf2.setResult(a1, 0.1/sf2Weight);
        sf2.setResult(a2, 0.1/sf2Weight);
        sf2.setResult(a3, 0.1/sf2Weight);
        sf2.setResult(a4, 0.1/sf2Weight);
        sf2.setResult(a5, 0.1/sf2Weight);
        
        sf3.setResult(a1, 0.1/sf3Weight);
        sf3.setResult(a2, 0.1/sf3Weight);
        sf3.setResult(a3, 0.1/sf3Weight);
        sf3.setResult(a4, 0.1/sf3Weight);
        sf3.setResult(a5, 0.1/sf3Weight);
        
        sf1.setWeight(sf1Weight);
        sf2.setWeight(sf2Weight);
        sf3.setWeight(sf3Weight);
        
        ResultNode resultRoot = new ResultNode(root, new WeightedSum(), alternatives);
        resultRoot.analyseSensitivity(weightModifier,
                SensitivityAnalysisTestFactory.getSensitivityTest(root, new WeightedSum(), alternatives));

        assert(!resultRoot.isSensitive());
    }
    

}