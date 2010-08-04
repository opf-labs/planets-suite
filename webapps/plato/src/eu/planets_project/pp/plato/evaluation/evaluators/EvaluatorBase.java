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
/**
 * 
 */
package eu.planets_project.pp.plato.evaluation.evaluators;

import java.io.File;
import java.io.StringReader;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.evaluation.IEvaluator;
import eu.planets_project.pp.plato.evaluation.MeasurementsDescriptor;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * @author kraxner
 *
 */
public class EvaluatorBase implements IEvaluator {
    private static final Log log = PlatoLogger.getLogger(EvaluatorBase.class);
    
    protected MeasurementsDescriptor descriptor;
    protected String descriptorStr;
    
    /**
     * @see eu.planets_project.pp.plato.evaluation.IEvaluator#getPossibleMeasurements()
     */
    public String getPossibleMeasurements() {
        return descriptorStr;
    }
    
    /**
     * loads measurements description from the given file.
     * populates descriptor and descriptor String
     * 
     * @param filename
     * @return
     */
    protected boolean loadMeasurementsDescription(String filename) {
        try {
            File descr = FileUtils.getResourceFile(filename);
            if (descr != null) {
                descriptorStr = new String(FileUtils.getBytesFromFile(descr), "UTF-8");
                descriptor= new MeasurementsDescriptor();
                descriptor.addMeasurementInfos(new StringReader(descriptorStr));
                return true;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

}
