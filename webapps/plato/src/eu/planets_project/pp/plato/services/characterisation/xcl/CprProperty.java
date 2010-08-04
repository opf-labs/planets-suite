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
package eu.planets_project.pp.plato.services.characterisation.xcl;

import java.io.Serializable;
import java.util.HashMap;

import eu.planets_project.pp.plato.model.measurement.Metric;

public class CprProperty implements Serializable {

    private static final long serialVersionUID = 4508943314044410855L;
    
    private int id;
    private String name; 
    private String compStatus;
    
    private String source;
    private String target;
    
    /**
     * Maps names of {@link Metric} to corresponding result values
     */
    private HashMap<String, CprMetricResult> resultMetrics = new HashMap<String, CprMetricResult>();
    

    public Object addMetric(Object key, Object value) {
        if (value != null)
            return resultMetrics.put((String)key, (CprMetricResult)value);
        else
            return null;
    }
    public boolean hasResults(){
        return "complete".equals(compStatus);
    }

    public String getCompStatus() {
        return compStatus;
    }

    public void setCompStatus(String compStatus) {
        this.compStatus = compStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, CprMetricResult> getResultMetrics() {
        return resultMetrics;
    }

    public void setResultMetrics(HashMap<String, CprMetricResult> resultMetrics) {
        this.resultMetrics = resultMetrics;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
    
    

}
