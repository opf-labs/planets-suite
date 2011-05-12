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

import java.util.HashMap;
import java.util.Map;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.tree.Leaf;

/**
 * A simplified Leaf. It doesn't use any value transformers. The results are stored directly.
 * This class should be used ONLY IN TESTS.
 *
 */
public class SimpleLeaf extends Leaf {
    private static final long serialVersionUID = -485869756218822752L;
    private Map<Alternative, Double> results = new HashMap<Alternative, Double>();
    
    public double getResult(Alternative a) {
        Double result = results.get(a);
        if(result != null) {
            return result;
        } else {
            return 0;
        }
    }
    
    public void setResult(Alternative a, Double result) {
        results.put(a, result);
    }
}

