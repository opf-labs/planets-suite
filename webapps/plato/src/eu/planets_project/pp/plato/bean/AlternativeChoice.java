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

package eu.planets_project.pp.plato.bean;

import java.io.Serializable;

import eu.planets_project.pp.plato.model.Alternative;

public class AlternativeChoice implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1830200393415765764L;
    private boolean selected = false;
    private Alternative a;

    public AlternativeChoice(){
        
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public AlternativeChoice(Alternative a) {
        this.a = a;
    }

    public String getName() {
        return a.getName();
    }

    public Alternative getAlternative() {
        return a;
    }
}
