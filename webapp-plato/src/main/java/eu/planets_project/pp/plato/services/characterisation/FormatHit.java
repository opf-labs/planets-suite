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
package eu.planets_project.pp.plato.services.characterisation;

import java.io.Serializable;

import eu.planets_project.pp.plato.model.FormatInfo;

public class FormatHit implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 348413733562487989L;
    
    private FormatInfo format = new FormatInfo();
    private String hitWarning;
    private boolean specific;
    
    public FormatInfo getFormat() {
        return format;
    }
    public void setFormat(FormatInfo format) {
        this.format = format;
    }
    public String getHitWarning() {
        return hitWarning;
    }
    public void setHitWarning(String hitWarning) {
        this.hitWarning = hitWarning;
    }
    public boolean isSpecific() {
        return specific;
    }
    public void setSpecific(boolean specific) {
        this.specific = specific;
    }
    
}
