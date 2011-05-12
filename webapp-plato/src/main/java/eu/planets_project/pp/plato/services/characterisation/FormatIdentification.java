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
import java.util.ArrayList;
import java.util.List;

public class FormatIdentification implements Serializable {
    public enum FormatIdentificationResult {
        POSITIVE,
        TENTATIVE,
        NOHIT,
        ERROR
    }
    /**
     * 
     */
    private static final long serialVersionUID = 87417659933280364L;
    
    private List<FormatHit> formatHits = new ArrayList<FormatHit>();
    
    private FormatIdentificationResult result;
    
    private String info;

    public List<FormatHit> getFormatHits() {
        return formatHits;
    }

    public void setFormatHits(List<FormatHit> formatHits) {
        this.formatHits = formatHits;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public FormatIdentificationResult getResult() {
        return result;
    }

    public void setResult(FormatIdentificationResult result) {
        this.result = result;
    }

    
}
