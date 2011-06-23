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

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.aggregators.IAggregator;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.sensitivity.ISensitivityTest;
import eu.planets_project.pp.plato.sensitivity.IWeightModifier;
import eu.planets_project.pp.plato.sensitivity.RandomSameOrderModifier;
import eu.planets_project.pp.plato.sensitivity.RangCorrelationTest;
import eu.planets_project.pp.plato.sensitivity.SimpleIterativeWeightModifier;

public class SensitivityAnalysisTestFactory {
    
    public static ISensitivityTest getSensitivityTest(Node root, IAggregator aggregator, List<Alternative> alternatives) {
        return new RangCorrelationTest(root, aggregator, alternatives);
    }
    
    public static IWeightModifier getWeightModifier() {
        return new SimpleIterativeWeightModifier();
    }

}
