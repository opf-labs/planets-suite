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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import at.tuwien.minimee.migration.evaluators.ImageCompareEvaluator;
import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.BooleanValue;
import eu.planets_project.pp.plato.model.values.FloatValue;
import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class ImageComparisonEvaluator extends EvaluatorBase implements IObjectEvaluator {
    private static final String NAME = "imagecompare (imagemagick)";
//    private static final String SOURCE = " - evaluated by " + NAME;
    
    private File tempDir = null;
    private Map<DigitalObject, String> tempFiles = new HashMap<DigitalObject, String>();

    private static final Log log = PlatoLogger.getLogger(ImageComparisonEvaluator.class);

    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsImageComp.xml";
    
    public ImageComparisonEvaluator(){
        // load information about measurements
        loadMeasurementsDescription(DESCRIPTOR_FILE);
    }

    
    public HashMap<MeasurementInfoUri, Value> evaluate(Alternative alternative,
            SampleObject sample, DigitalObject result, List<MeasurementInfoUri> measurementInfoUris,
            IStatusListener listener) throws EvaluatorException {

        //listener.updateStatus(NAME + ": Start evaluation"); //" for alternative: %s, sample: %s", NAME, alternative.getName(), sample.getFullname()));
        setUp();
        try {
            HashMap<MeasurementInfoUri, Value> results = new HashMap<MeasurementInfoUri, Value>();
    
            saveTempFile(sample);
            saveTempFile(result);
            
            // NOTE: imageEvaluator is still called once per leaf !
            // -> could be optimized, but the used minimee evaluator will do separate calls anyway 
            ImageCompareEvaluator imageEvaluator = new ImageCompareEvaluator();
            
            for(MeasurementInfoUri measurementInfoUri: measurementInfoUris) {
                String propertyURI = measurementInfoUri.getAsURI();
                String fragment = measurementInfoUri.getFragment();
                Scale scale = descriptor.getMeasurementScale(measurementInfoUri);
                if (scale == null)  {
                    // This means that I am not entitled to evaluate this measurementInfo and therefore supposed to skip it:
                    continue;
                }
                if ((propertyURI != null) && propertyURI.startsWith(OBJECT_IMAGE_SIMILARITY +"#")) {
                    Value v = null;
                    if (fragment.equals("equal")) {
                        Double d= imageEvaluator.evaluate(tempDir.getAbsolutePath(), 
                                tempFiles.get(sample), 
                                tempFiles.get(result),
                                "AE");
                        
                        if (d.compareTo(Scale.MAX_VALUE) == 0) {
                            // No: only evaluation results are returned, no error messages
                            // v.setComment("ImageMagick compare failed or could not be called");
                        } else {
                            v = scale.createValue();
                            ((BooleanValue)v).bool(d.compareTo(0.0) == 0);
                            v.setComment("ImageMagick compare returned "+Double.toString(d)+" different pixels");
                        }
        //                log.debug("difference" + Double.toString(Scale.MAX_VALUE-d));
                    } else {
                        Double d= imageEvaluator.evaluate(tempDir.getAbsolutePath(), 
                                         tempFiles.get(sample), 
                                         tempFiles.get(result),
                                         fragment);
                        if (d == null) {
                            // No: only evaluation results are returned, no error messages
                            // v = leaf.getScale().createValue();
                            // v.setComment("ImageMagick comparison failed");
                        } else {
                            v = scale.createValue();
                            if (v instanceof FloatValue) {
                                ((FloatValue)v).setValue(d);
                                v.setComment("computed by ImageMagick compare");                            
                            } else if (v instanceof PositiveFloatValue) {
                                ((PositiveFloatValue)v).setValue(d);
                                v.setComment("computed by ImageMagick compare");                            
                            } else {
                                v.setComment("ImageMagick comparison failed - wrong Scale defined.");
                            }
                        }
                    }
                    if (v != null) {
                        // add the value to the result set
                        results.put(measurementInfoUri, v);
                    }
                }
            }
            return results;
        }finally {
            tearDown();
        }
    }
    

    protected void doClearEm() {
        OS.deleteDirectory(tempDir);
        tempFiles.clear();
    }

    /**
     * 
     * @param migratedObject the object that shall be used as KEY for storing the result bytestream
     * @param resultObject the object that contains the actual bytestream to be stored
     * @return the size of the bytestream
     */
    private void saveTempFile(DigitalObject object) {
        String tempFileName = tempDir.getAbsolutePath()+"/"+System.nanoTime();
        OutputStream fileStream;
        try {
            fileStream = new  BufferedOutputStream (new FileOutputStream(tempFileName));
            byte[] data = object.getData().getData();
            fileStream.write(data);
            fileStream.close();
            tempFiles.put(object, tempFileName);
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    private void setUp(){
        if (tempDir != null) {
            tearDown();
        }
        tempDir = new File(OS.getTmpPath() + "imagecompare" + System.nanoTime());
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempFiles.clear();
        log.debug("using temp directory " + tempDir.getAbsolutePath());
        
    }

    private void tearDown() {
        if (tempDir != null) {
            OS.deleteDirectory(tempDir);
            tempFiles.clear();
            tempDir = null;
        }
    }
    
}
