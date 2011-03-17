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

import java.util.HashMap;
import java.util.List;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;

/**
 * This is the interface for all evaluation plugins that are providing
 * measurements that require looking at a specific object or the experiment
 * produced by applying an action to a specific object.
 * These will usually belong to the categories <ul>
 * <li>outcome object and</li>
 * <li>action runtime.</li>
 * </ul>
 * The interface defines constant URIs for core properties that are implemented 
 * and integrated in Plato.
 * @author cb
 */
public interface IObjectEvaluator extends IEvaluator {
    /**
     * relative filesize  (measure, positive number)
     */
    String OBJECT_FORMAT_RELATIVEFILESIZE   = "outcome://object/relativeFileSize";
    /**
     * is the format wellformed, valid, conforms ? (measure, boolean)
     */
    String OBJECT_FORMAT_CORRECT_WELLFORMED = "outcome://object/format/correct/wellformed";
    String OBJECT_FORMAT_CORRECT_VALID      = "outcome://object/format/correct/valid";
    String OBJECT_FORMAT_CORRECT_CONFORMS   = "outcome://object/format/correct/conforms";

    /**
     * compression scheme (measure, free text)
     * compression scheme#equal (derived measure, boolean) 
     * more derived measures with XCL  
     */
    String OBJECT_COMPRESSION_SCHEME   = "outcome://object/compression/scheme";
    /**
     * is this sample stored lossless, or with a lossy compression? (measure, boolean)
     */
    String OBJECT_COMPRESSION_LOSSLESS = "outcome://object/compression/lossless";
    String OBJECT_COMPRESSION_LOSSY    = "outcome://object/compression/lossy";
    
    /**
     * width and height of the image (derived measure: #equal, boolean)
     *  - more derived with XCL 
     */
    String OBJECT_IMAGE_DIMENSION_WIDTH       = "outcome://object/image/dimension/width";
    String OBJECT_IMAGE_DIMENSION_HEIGHT      = "outcome://object/image/dimension/height";
    /**
     * aspect ratio of image  height/ width  (derived measure: #equal, boolean)
     */
    String OBJECT_IMAGE_DIMENSION_ASPECTRATIO = "outcome://object/image/dimension/aspectRatio";
    
    /**
     * bits per sample (derived measure: #equal, boolean)
     * - more derived with XCL
     */
    String OBJECT_IMAGE_COLORENCODING_BITSPERSAMPLE   = "outcome://object/image/colorEncoding/bitsPerSample";
    /**
     * samplesPerPixel (derived measure: #equal, boolean)
     * - more derived with XCL
     */
    String OBJECT_IMAGE_COLORENCODING_SAMPLESPERPIXEL = "outcome://object/image/colorEncoding/samplesPerPixel";
    String OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORSPACE =  "outcome://object/image/photometricInterpretation/colorSpace";
    String OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORPROFILE_ICCPROFILE =  "outcome://object/image/photometricInterpretation/colorProfile/iccProfile";

    String OBJECT_IMAGE_SPATIALMETRICS_SAMPLINGFREQUENCYUNIT   = "outcome://object/image/spatialMetrics/samplingFrequencyUnit";
    String OBJECT_IMAGE_SPATIALMETRICS_XSAMPLINGFREQUENCY   = "outcome://object/image/spatialMetrics/xSamplingFrequency";
    String OBJECT_IMAGE_SPATIALMETRICS_YSAMPLINGFREQUENCY   = "outcome://object/image/spatialMetrics/ySamplingFrequency";

    String OBJECT_IMAGE_METADATA = "outcome://object/image/metadata";
    // exif: artist
    String OBJECT_IMAGE_METADATA_PRODUCER = "outcome://object/image/metadata/producer";
    String OBJECT_IMAGE_METADATA_SOFTWARE = "outcome://object/image/metadata/software";
    // exif: DateTime
    String OBJECT_IMAGE_METADATA_CREATIONDATE = "outcome://object/image/metadata/creationDate";
    // exif: FileModifyDate/ModifyDate
    String OBJECT_IMAGE_METADATA_LASTMODIFIED = "outcome://object/image/metadata/lastModified";
    String OBJECT_IMAGE_METADATA_DESCRIPTION = "outcome://object/image/metadata/description";
    String OBJECT_IMAGE_METADATA_ORIENTATION = "outcome://object/image/metadata/orientation";
        
    String OBJECT_IMAGE_SIMILARITY = "outcome://object/image/similarity";

    /**
     * 
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_TIME_PERSAMPLE = "action://runtime/performance/time/perSample";
    
    /**
     * time per MB is defined msec/MB (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_TIME_PERMB =  "action://runtime/performance/time/perMB";
    
    /**
     * throughput is defined in MB per second (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_THROUGHPUT =  "action://runtime/performance/throughput";

    /**
     * Memory per MB (of the sample object's size) is defined (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERMB =  "action://runtime/performance/memory/perMB";
    
    /**
     * Memory per Sample (measure, positive number)
     */
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PERSAMPLE =  "action://runtime/performance/memory/perSample";

    /**
     * Memory peak (measure, positive number)
     */
    // TODO: not evaluated yet
    String OBJECT_ACTION_RUNTIME_PERFORMANCE_MEMORY_PEAK =  "action://runtime/performance/memory/peak";
    
    
    String OBJECT_ACTION_ACTIVITYLOGGING_FORMAT =  "action://runtime/activityLogging/format";
    String OBJECT_ACTION_ACTIVITYLOGGING_AMOUNT =  "action://runtime/activityLogging/amount";
    
    
    /**
     * evaluates result and sample object with regard to the given critera defined in leaves
     * returns a list of values, one per leaf
     * 
     * It is not nice that leaves are passed to the evaluator, and a map of leaves to values is returned
     * 
     * This information is really needed:
     *  - how this criterion is measured (MeasurementInfo)
     *  - what is type of the evaluated value (Scale)
     *  
     * @param alternative
     * @param sample
     * @param result
     * @param measurementInfoUris
     * @param listener
     * @return
     * @throws EvaluatorException
     */
    public HashMap<MeasurementInfoUri, Value> evaluate(
            Alternative alternative,
            SampleObject sample,
            DigitalObject result,
            List<MeasurementInfoUri> measurementInfoUris, IStatusListener listener) throws EvaluatorException;
    
}
