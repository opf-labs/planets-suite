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

import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.scales.IntRangeScale;
import eu.planets_project.pp.plato.model.scales.OrdinalScale;
import eu.planets_project.pp.plato.model.scales.PositiveFloatScale;
import eu.planets_project.pp.plato.model.scales.PositiveIntegerScale;
import eu.planets_project.pp.plato.model.scales.RestrictedScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.scales.YanScale;
import eu.planets_project.pp.plato.model.tree.Leaf;


/**
 * NGTest for the Leaf
 * (the scale of the Leaf!)
 * 
 * @author michael kraxner
 *
 */
public class LeafTester {
    private BooleanScale booleanScale = new BooleanScale();
    private IntRangeScale intRangeScale = new IntRangeScale();
    private YanScale yanScale = new YanScale();
    private PositiveIntegerScale posInt = new PositiveIntegerScale();
    private PositiveFloatScale posFloat = new PositiveFloatScale();
    
    @Test
    public void testScaleBooleanValue(){
        Leaf leaf = new Leaf();
        leaf.changeScale(booleanScale);
        // test as parent Scale - this is the way it is used within the application
        Scale scale = leaf.getScale();
        assert("Yes/No".equals(((BooleanScale)scale).getRestriction()));
        ((RestrictedScale)scale).setRestriction("meine/eigene");
        assert("Yes/No".equals(((RestrictedScale)scale).getRestriction()));
        Scale cloned = scale.clone();
        assert(cloned instanceof BooleanScale);
        assert("Yes/No".equals(((RestrictedScale)cloned).getRestriction()));
    }
    
    @Test
    public void testScaleIntRangeValue(){
        Leaf leaf = new Leaf();
        leaf.changeScale(intRangeScale);
        Scale scale = leaf.getScale();
        assert("0/5".equals(((RestrictedScale)scale).getRestriction()));
        ((RestrictedScale)scale).setRestriction("-5/-1");
        assert("-5/-1".equals(((RestrictedScale)scale).getRestriction()));
        ((RestrictedScale)scale).setRestriction("aa/bbb");
        assert("-5/-1".equals(((RestrictedScale)scale).getRestriction()));
        scale.setUnit(null);
        assert(scale.getUnit() == null);
        scale.setUnit("mm");
        assert("mm".equals(scale.getUnit()));
        
        Scale cloned = scale.clone();
        assert(cloned instanceof IntRangeScale);
        assert("-5/-1".equals(((RestrictedScale)cloned).getRestriction()));
        assert("mm".equals(cloned.getUnit()));
    }
    
    @Test
    public void testChangeScaleType(){
        Leaf leaf = new Leaf();
        leaf.changeScale(booleanScale);
        leaf.changeScale(intRangeScale);
        Scale scale = leaf.getScale();
        
        assert(scale instanceof IntRangeScale);
        assert("0/5".equals(((RestrictedScale)scale).getRestriction()));
        
        leaf.changeScale(yanScale);
        scale = leaf.getScale();
        assert(scale instanceof YanScale);
        assert("Yes/Acceptable/No".equals(((RestrictedScale)scale).getRestriction()));
        assert("Yes/Acceptable/No".equals(((OrdinalScale)scale).getRestriction()));
        assert("Yes/Acceptable/No".equals(((YanScale)scale).getRestriction()));        
        
//        leaf.setScaleType(scaleType)
    }
    
    

}
