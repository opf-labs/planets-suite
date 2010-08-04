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
package eu.planets_project.pp.plato.evaluation;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.PlatoLogger;


public class MiniRED implements MiniREDRemote {
    private Map<String, String> evaluatorClasses = new HashMap<String, String>();
    
    private static MiniRED me = null;
    
    private MeasurementsDescriptor descriptor;
   
    private MiniRED() {
        descriptor = new MeasurementsDescriptor();
        reloadEvaluators();
    }
    
    public void reloadEvaluators(){
        descriptor.clearMeasurementInfos();

        // These are deactivated for now since they are experimental. The plan is to reintroduce them in Plato 3.1
//        register("metadata",    "eu.planets_project.pp.plato.evaluation.evaluators.ImageMetadataEvaluator");
//        register("imagecompjava", "eu.planets_project.pp.plato.evaluation.evaluators.imagecomparison.java.ImageComparisonEvaluator");
        register("pcdl",    "eu.planets_project.pp.plato.evaluation.evaluators.PCDLEvaluator");
        register("experiment", "eu.planets_project.pp.plato.evaluation.evaluators.ExperimentEvaluator");
        register("xcl", "eu.planets_project.pp.plato.evaluation.evaluators.XCLEvaluator");
        register("object", "eu.planets_project.pp.plato.evaluation.evaluators.ObjectEvaluator");
        register("minireef", "eu.planets_project.pp.plato.evaluation.evaluators.MiniREEFEvaluator");
        register("imagecomp", "eu.planets_project.pp.plato.evaluation.evaluators.ImageComparisonEvaluator");
    }
    
    public static synchronized MiniRED getInstance() {
   //     me = null;
        if (me == null) {
            me = new MiniRED();
        }
        return me;
    }
    
    public IEvaluator createEvaluator(String schema) {
        String className = evaluatorClasses.get(schema);
        
        try {
            IEvaluator eval = (IEvaluator) Class.forName(className).newInstance();
            return eval;
        } catch (Exception e) {
            PlatoLogger.getLogger(MiniRED.class).error(
                    "Could not create an IEvaluator for schema:"+schema, e);
            return null;
        } 
    }
    
    public List<IObjectEvaluator> getObjectEvaluationSequence() {
        LinkedList<IObjectEvaluator> evaluators = new LinkedList<IObjectEvaluator>();
        // "metadata", 
        String[] keys = new String[]{"xcl", "imagecompjava",  "experiment","object","imagecomp"};
        for (String s: keys) {
            if (evaluatorClasses.containsKey(s)) {
                evaluators.add((IObjectEvaluator)createEvaluator(s));
            }
        }
        return evaluators;
    }
    
    public List<IActionEvaluator> getActionEvaluationSequence() {
        LinkedList<IActionEvaluator> evaluators = new LinkedList<IActionEvaluator>();

        String[] keys = new String[]{"pcdl","minireef"};
        for (String s: keys) {
            if (evaluatorClasses.containsKey(s)) {
                evaluators.add((IActionEvaluator)createEvaluator(s));
            }
        }
        return evaluators;
    }
    
    public String echo(String s) {
        return s;
    }
    /** 
     * temporarily this is a double entry point
     * @see #createEvaluator(String)
     */
    public IEvaluator discover(String name) {
        return createEvaluator(name);
    }

    public void register(String name, String classname) {
        evaluatorClasses.put(name,classname);
        
        IEvaluator e = createEvaluator(name);
        String description = e.getPossibleMeasurements();
        if (description != null) {
            descriptor.addMeasurementInfos(new StringReader(description));
        }
    }

    /**
     * returns all measurements, which can be evaluated with the currently registered evaluators
     * 
     * @return uri of measurement, see  {@link MeasurementInfoUri}
     */
    public Collection<MeasurableProperty> getPossibleMeasurements() {
        
        return descriptor.getPossibleMeasurements();
    }
    
    public MeasurementsDescriptor getMeasurementsDescriptor() {
        return descriptor;
    }
}
