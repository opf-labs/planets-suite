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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.characterisation.xcl.XCLComparator;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class XCLEvaluator extends EvaluatorBase implements IObjectEvaluator{

    private File tempDir = null;
    private static final Log log = PlatoLogger.getLogger(XCLEvaluator.class);
    
    
    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsXCL.xml";
    
    public XCLEvaluator() {
        // load information about measurements
        loadMeasurementsDescription(DESCRIPTOR_FILE);
    }
    
    
    
    

    public HashMap<MeasurementInfoUri, Value>  evaluate(Alternative alternative, SampleObject sample,
            DigitalObject result, List<MeasurementInfoUri> measurementInfoUris, IStatusListener listener)
            throws EvaluatorException {
        HashMap<MeasurementInfoUri, Value> results = new HashMap<MeasurementInfoUri, Value>();

        // maybe we should characterise objects where xcdl descriptions are missing
        if ((sample.getXcdlDescription() == null)|| !sample.getXcdlDescription().isDataExistent()) {
            listener.updateStatus("XCDL description of sample object " + sample.getFullname() + " is missing. Please generate!");
            return results;
        }
        if ((result.getXcdlDescription() == null) || !result.getXcdlDescription().isDataExistent()) {
            listener.updateStatus("XCDL description of result of action " + alternative.getName() + " for sample " + sample.getFullname() + " is missing.  Please generate!");
            return results;
        }
        
        setUp();

        try {
            // dump XCDL descriptions to temp files
            String tempPath = OS.completePathWithSeparator(tempDir.getAbsolutePath());
            String sampleXCDLFile = tempPath + "sample.xcdl";
            String resultXCDLFile = tempPath + "result.xcdl";
            
            FileUtils.writeToFile(new ByteArrayInputStream(sample.getXcdlDescription().getData().getData()),
                    new FileOutputStream(sampleXCDLFile));
            FileUtils.writeToFile(new ByteArrayInputStream(result.getXcdlDescription().getData().getData()),
                    new FileOutputStream(resultXCDLFile));
            
            // only pass xcl measurement uris to the comparator
            List<MeasurementInfoUri> xclMeasurements = new LinkedList<MeasurementInfoUri>();
            for (MeasurementInfoUri infoUri : measurementInfoUris) {
                String path = infoUri.getPath();
                if ((path != null) && (path.startsWith("object/xcl/"))) {
                    xclMeasurements.add(infoUri);
                    
                }
            }
            
            XCLComparator comp = new XCLComparator(descriptor);
            HashMap<MeasurementInfoUri, Value> compResult = comp.compare(tempDir.getAbsolutePath(), sampleXCDLFile, resultXCDLFile, xclMeasurements);
            
            // compResult can be null when the comparator for instance can't handle a specific file format
            if (compResult == null) {
                return results;
            }
            
            // add comments to results
            for (MeasurementInfoUri info : compResult.keySet()) {
                Value v = compResult.get(info);
                if (v != null) {
                    v.setComment(v.getComment() + "\n - evaluated with XCL tools");
                    results.put(info, v);
                }
            }
            return results;
        } catch (Exception e) {
            throw new EvaluatorException(e);
        } finally {
            // clean up
            tearDown();
        }
    }
    
    private void setUp() {
        if (tempDir != null) {
            tearDown();
        }
        tempDir = new File(OS.getTmpPath() + "xclevaluate" + System.nanoTime());
        tempDir.mkdir();
        tempDir.deleteOnExit();
        
        log.debug("using temp directory " + tempDir.getAbsolutePath());
    }
    
    private void tearDown() {
        if (tempDir != null) {
            OS.deleteDirectory(tempDir);
            tempDir = null;
        }
    }

}
