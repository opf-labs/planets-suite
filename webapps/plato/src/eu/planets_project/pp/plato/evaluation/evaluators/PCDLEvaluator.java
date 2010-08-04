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
package eu.planets_project.pp.plato.evaluation.evaluators;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IActionEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * This class extracts values from PCDL descriptors
 * @author cb
 *
 */
public class PCDLEvaluator extends EvaluatorBase implements IActionEvaluator {
    
    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsPCDL.xml";

    private static final Log log = PlatoLogger.getLogger(PCDLEvaluator.class);

    private HashMap<String, String> extractionPaths = new HashMap<String, String>();
    // maybe another hashmap for commentsPaths

    public PCDLEvaluator() {
        // load information about measurements
        loadMeasurementsDescription(DESCRIPTOR_FILE);
        addExtractionPaths();        
    }
    
    public HashMap<MeasurementInfoUri, Value> evaluate(Alternative alternative,
            List<MeasurementInfoUri> measurementInfoUris, IStatusListener listener)
            throws EvaluatorException {
        
        HashMap<MeasurementInfoUri, Value> results = new HashMap<MeasurementInfoUri, Value>();
        if ((alternative.getAction() == null) || (alternative.getAction().getDescriptor() == null)) {
            return results;
        }

        try {
            // yes, this is a hack. It's a demo.
            // If this is a minimee action, we know that there will be 
            // a PCDL for it, so we retrieve it locally:
            if (alternative.getAction().getUrl().contains("minimee/")) {
                String pcdlFile = alternative.getAction().getDescriptor()+".xml";
                InputStream pcdlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        "data/pcdl/"+pcdlFile);
                if (pcdlStream == null) {
                    log.debug("pcdl descriptor not found: " + pcdlFile);
                    return results;
                }
                XmlExtractor xmlExtractor = new XmlExtractor();
                Document doc = xmlExtractor.getDocument(new InputSource(pcdlStream));
                
                for(MeasurementInfoUri measurementInfoUri: measurementInfoUris) {
                    String propertyURI = measurementInfoUri.getAsURI();
                    Scale scale = descriptor.getMeasurementScale(measurementInfoUri);
                    if (scale == null)  {
                        // This means that I am not entitled to evaluate this measurementInfo and therefore supposed to skip it:
                        continue;
                    }
                    if (ACTION_RETAIN_FILENAME.equals(propertyURI)) {
                        // for all wrapped minimee migrators the output filename can be determined by -o <filename> or something similar  
                        Value v = scale.createValue();
                        v.setComment("obtained from PCDL descriptor");
                        v.parse("Yes");
                        results.put(measurementInfoUri, v);
                    }
                    
                    String extractionPath = extractionPaths.get(propertyURI);
                    if (extractionPath != null) {
                        Value v = new XmlExtractor().extractValue(doc, scale, extractionPath, null);
                        if (v != null) {
                            v.setComment("obtained from PCDL descriptor");
                            results.put(measurementInfoUri, v);                        
                        } else {
                            // No: only successfully evaluated values are returned  
                            // v = leaf.getScale().createValue();
                            // v.setComment("failed to obtain value from PCDL descriptor");
                            log.debug("failed to obtain value from PCDL descriptor for path: " + extractionPath);
                        }
                    }
                }
            }
            return results;
            
        } catch (ParserConfigurationException e) {
            throw new EvaluatorException("Could not access PCDL descriptor", e);
        } catch (SAXException e) {
            throw new EvaluatorException("Could not access PCDL descriptor", e);
        } catch (IOException e) {
            throw new EvaluatorException("Could not access PCDL descriptor", e);
        }
    }
    
    private void addExtractionPaths() {
        extractionPaths.put(ACTION_OPENSOURCE, "//OpenSource/text()");
        extractionPaths.put(ACTION_LICENSE, "//License/text()");
    }
}
