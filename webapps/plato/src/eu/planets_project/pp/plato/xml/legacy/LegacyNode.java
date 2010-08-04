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

package eu.planets_project.pp.plato.xml.legacy;

import java.util.ArrayList;
import java.util.List;

public class LegacyNode implements ILegacyElement {
    private String name;
    private List<ILegacyElement> children = new ArrayList<ILegacyElement>();
    private ILegacyElement parent = null;
    
    public ILegacyElement getParent() {
        return parent;
    }

    public void setParent(ILegacyElement parent) {
        this.parent = parent;
    }

    public void addChild(ILegacyElement e) {
        children.add(e);
        e.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<LegacyResultLeaf> getAllLeaves() {
        List<LegacyResultLeaf> list = new ArrayList<LegacyResultLeaf>();
        for (ILegacyElement n : children) {
            if (n instanceof LegacyResultLeaf) {
                LegacyResultLeaf leaf = (LegacyResultLeaf) n;
                list.add(leaf);
            } else {
                list.addAll(n.getAllLeaves());
            }
        }
        return list;
    }
    
}
