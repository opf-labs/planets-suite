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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;

import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.xml.StrictErrorHandler;

public class ComparatorUtils implements Serializable {
    private static final long serialVersionUID = -2949938310717474560L;

    private String error;
    private List<CompareResult> compResult;

    
    public ComparatorUtils() {
    }
    
    public boolean addResult(CompareResult result) {
        return compResult.add(result);
    }

    /**
     * parses a cpResponse and generates a list of {@link CompareResult compare results}, one per
     * sent target XCDL.
     */
    public List<CompareResult> parseResponse(String response) throws PlatoServiceException {
        compResult = new ArrayList<CompareResult>();
        error = null;

        File validResult = new File(response);
        if (! validResult.exists()) {
            return compResult;            
        }
        Digester d = new Digester();
        d.setValidating(false);

        StrictErrorHandler errorHandler =  new StrictErrorHandler();
        d.setErrorHandler(errorHandler);

        d.setUseContextClassLoader(true);
        
        d.push(this);

        d.addCallMethod("copra/error", "setError", 0);
        
        d.addObjectCreate("*/set", CompareResult.class);
        d.addSetNext("*/set", "addResult");
        
        d.addObjectCreate("*/set/property", CprProperty.class);
        d.addSetProperties("*/set/property");
        d.addBeanPropertySetter("*/set/property/data/src/value", "source");
        d.addBeanPropertySetter("*/set/property/data/tar/value", "target");
        
        d.addObjectCreate("*/metrics/metric", CprMetricResult.class);
        d.addSetProperties("*/metrics/metric/result");
        d.addBeanPropertySetter("*/metrics/metric/state");
        
        CallMethodRule metricRule = new CallMethodRule(1, "addMetric", 2);
        d.addRule("*/metrics/metric", metricRule);
        d.addCallParam("*/metrics/metric", 0 , "name");
        d.addCallParam("*/metrics/metric", 1, true);   
        
        
//            <metric id="121" name="valueSetMatch_1">
//            <result state="ok">true</result>
//            </metric>

        
        CallMethodRule r = new CallMethodRule(1, "addProperty", 2);
        d.addRule("*/set/property", r);
        d.addCallParam("*/set/property", 0 , "name");
        d.addCallParam("*/set/property",1,true);

        
        try {
            d.parse(validResult);
            if (error != null) {
                throw new PlatoServiceException("XCL tool:comparator failed: " + error);
            }
            return compResult;
        } catch (PlatoServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new PlatoServiceException("The response of the XCL tool:comparator is invalid.", e);
        } catch (Error e) {
            throw new PlatoServiceException("The response of the XCL tool:comparator is invalid.", e);
        }
    }
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    
    
}
