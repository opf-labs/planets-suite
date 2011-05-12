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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * This class entails functions for analysing original and transformed
 * objects, ranging from a simple comparison of file sizes to general
 * format-concerned issues such as well-formedness and validity to 
 * specific issues wrt image quality, where it delegates to @link {@link ImageComparisonEvaluator}
 * @author cb
 *
 */
public class ObjectEvaluator extends EvaluatorBase implements IObjectEvaluator {
    private static final Log log = PlatoLogger.getLogger(ObjectEvaluator.class);
    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsObject.xml";
    
    public ObjectEvaluator() {
        // load information about measurements
        loadMeasurementsDescription(DESCRIPTOR_FILE);
    }

    
    public HashMap<MeasurementInfoUri, Value> evaluate(Alternative alternative,
            SampleObject sample, DigitalObject result, List<MeasurementInfoUri> measurementInfoUris,
            IStatusListener listener) throws EvaluatorException {

        listener.updateStatus("Objectevaluator: Start evaluation"); //" for alternative: %s, samle: %s", NAME, alternative.getName(), sample.getFullname()));
        
        HashMap<MeasurementInfoUri, Value> results = new HashMap<MeasurementInfoUri, Value>();
        
        for(MeasurementInfoUri measurementInfoUri: measurementInfoUris) {
            String propertyURI = measurementInfoUri.getAsURI();
            Scale scale = descriptor.getMeasurementScale(measurementInfoUri);
            if (scale == null)  {
                // This means that I am not entitled to evaluate this measurementInfo and therefore supposed to skip it:
                continue;
            }
            if (OBJECT_FORMAT_RELATIVEFILESIZE.equals(propertyURI)) {
                // evaluate here
                PositiveFloatValue v = (PositiveFloatValue) scale.createValue();
                double d = ((double)result.getData().getSize())/sample.getData().getSize()*100;
                long l = Math.round(d);
                d = ((double)l)/100;
                v.setValue(d);
                results.put(measurementInfoUri, v);
                listener.updateStatus(String.format("Objectevaluator: evaluated measurement: %s = %s", measurementInfoUri.getAsURI(), v.toString())); 
            }
        }
        measurementInfoUris.removeAll(results.keySet());
        FITSEvaluator fitsEval = new FITSEvaluator();
        HashMap<MeasurementInfoUri, Value> fitsResults = fitsEval.evaluate(alternative, sample, result, measurementInfoUris, listener);
        fitsResults.putAll(results);
        
        return fitsResults;
    }
    
}
