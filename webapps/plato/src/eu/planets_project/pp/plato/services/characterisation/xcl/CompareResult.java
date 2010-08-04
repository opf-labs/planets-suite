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
import java.util.List;

import at.tuwien.minimee.migration.evaluators.XCLEvaluator;

import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.services.datatypes.Property;

public class CompareResult implements Serializable {

    private static final long serialVersionUID = -146256886130041021L;
    
    @Deprecated
    private List<Property> pservProperties;

    private HashMap<String, CprProperty> properties = new HashMap<String, CprProperty>();
    

    @Deprecated
    public List<Property> getPservProperties() {
        return pservProperties;
    }

    @Deprecated
    public void setPservProperties(List<Property> properties) {
        this.pservProperties = properties;
    }
    private Property lookUpPservProperty(String propertyName){
        if (pservProperties == null)
            return null;
        for (Property p : pservProperties) {
            if (propertyName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * returns the evaluation result for the given propertyName and metricName
     * a Value is created according to the metricName.
     * 
     * Note: this is only used by miniMee's {@link XCLEvaluator}
     * 
     * @param propertyName
     * @param metricName
     * @return
     */
    @Deprecated
    public Value getEvaluationValue(String propertyName, String metricName) {
       if (pservProperties != null) {
           try {
               String strValue = getEvaluationResult(propertyName, metricName);
               if (strValue != null) {
                   Scale s = MetricToScaleMapping.getScale(MetricToScaleMapping.getMetricId(metricName));
                   Value result = s.createValue();
                   result.setScale(null);
                   MetricToScaleMapping.setValueFromCprString(strValue, result);
                   return result;
               }
           }catch (Exception e) {
               PlatoLogger.getLogger(CompareResult.class).error("could not extract evaluation value: ", e);
           }
       } 
       return null; 
    }
    

    /**
     * extracts the evaluation result for given propertyName and metricName 
     * from a pservProperty string.
     * 
     * @param propertyName
     * @param metricName
     * @return string representation of the evaluation result
     */
    @Deprecated
    public String getEvaluationResult(String propertyName, String metricName) {
        if (pservProperties != null) {
            Property p = lookUpPservProperty(propertyName);
            if (p != null) {
                // eu.planets_project.ifr.core.services.comparison.comparator.impl.ResultPropertiesReader:
                // descriptionBuilder.append(String.format(" %s=%s", metricName, resultString));
                String descr = p.getDescription().replaceAll(" ", "]").replaceAll("\\[", "]");
                String metricKey = "]"+metricName+"=";
                int startIdx = descr.indexOf(metricKey);
                if (startIdx >-1) {
                    startIdx += metricKey.length();
                }
                
                int endIdx = descr.indexOf("]", startIdx);
                String result = null;
                if ((startIdx >-1) && (endIdx >= startIdx)) {
                    result = descr.substring(startIdx, endIdx);
                }
                return result;
            } 
        }
        return null;
    }
    
    
    public Object addProperty(Object name, Object property) {
        return properties.put((String)name, (CprProperty)property);
    }

    public HashMap<String, CprProperty> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, CprProperty> compSet) {
        this.properties = compSet;
    }
}
