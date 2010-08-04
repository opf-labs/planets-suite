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
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.BooleanValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.characterisation.fits.FitsNamespaceContext;
import eu.planets_project.pp.plato.util.FloatFormatter;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class FITSEvaluator extends EvaluatorBase implements IObjectEvaluator {
    private static final String FITS_COMPRESSIONSCHEME_UNCOMPRESSED = "Uncompressed";
    private static final String NAME = "FITS/Jhove/Exiftool";
    private static final String SOURCE = "\n- extracted by " + NAME;

    private static final Log log = PlatoLogger.getLogger(FITSEvaluator.class);

    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsFITS.xml";
    
    public FITSEvaluator(){
        // load information about measurements
        loadMeasurementsDescription(DESCRIPTOR_FILE);
    }

    public HashMap<MeasurementInfoUri, Value> evaluate(Alternative alternative,
            SampleObject sample, DigitalObject result, List<MeasurementInfoUri> measurementInfoUris,
            IStatusListener listener) throws EvaluatorException {
        
        FloatFormatter formatter = new FloatFormatter();

        HashMap<MeasurementInfoUri, Value> results = new HashMap<MeasurementInfoUri, Value>();
        
        String fitsXMLResult = result.getFitsXMLString();
        String fitsXMLSample = sample.getFitsXMLString();
        
        XmlExtractor extractor = new XmlExtractor();
        extractor.setNamespaceContext(new FitsNamespaceContext());
        if ((fitsXMLResult != null) && (fitsXMLSample != null)) {
             // so we have a fits xml, lets analyse it:
             try {
                 StringReader reader = new StringReader(fitsXMLResult);
                 Document fitsDocResult = extractor.getDocument(new InputSource(reader));
                 reader = new StringReader(fitsXMLSample);
                 Document fitsDocSample = extractor.getDocument(new InputSource(reader));
                 
                 String sampleImageCompressionScheme = extractor.extractText(fitsDocSample, "//fits:compressionScheme/text()");
                 String resultImageCompressionScheme = extractor.extractText(fitsDocResult, "//fits:compressionScheme/text()");
                 
                 for (MeasurementInfoUri measurementInfoUri : measurementInfoUris) {
                     Value v = null;
                     String propertyURI = measurementInfoUri.getAsURI();
                     Scale scale = descriptor.getMeasurementScale(measurementInfoUri);
                     if (scale == null)  {
                         // This means that I am not entitled to evaluate this measurementInfo and therefore supposed to skip it:
                         continue;
                     }
                     if(OBJECT_FORMAT_CORRECT_WELLFORMED.equals(propertyURI)) {
                         v = extractor.extractValue(fitsDocResult, scale,
                             "//fits:well-formed[@status='SINGLE_RESULT']/text()",
                             "//fits:filestatus/fits:message/text()");
                     } else if(OBJECT_FORMAT_CORRECT_VALID.equals(propertyURI)) {
                         v = extractor.extractValue(fitsDocResult, scale, 
                                 "//fits:filestatus/fits:valid[@status='SINGLE_RESULT']/text()",
                                 "//fits:filestatus/fits:message/text()");
                     } if(OBJECT_COMPRESSION_SCHEME.equals(propertyURI)) {
                         v = extractor.extractValue(fitsDocResult, scale, 
                                 "//fits:compressionScheme/text()",
                                 null);
                     }
                     
                     if ((v!= null) && (v.getComment() == null || "".equals(v.getComment()))) {
                         v.setComment(SOURCE);
                         results.put(measurementInfoUri, v);
                         listener.updateStatus(String.format("%s: measurement: %s = %s", NAME, measurementInfoUri.getAsURI(), v.toString())); 
                         // this leaf has been processed
                         continue;
                     }                              

                     if(OBJECT_FORMAT_CORRECT_CONFORMS.equals(propertyURI)) {
                         if (alternative.getAction() != null) {
                             String puid = "UNDEFINED";
                             FormatInfo info = alternative.getAction().getTargetFormatInfo();
                             if (info != null) {
                                 puid = info.getPuid();
                             }
                             String fitsText = extractor.extractText(fitsDocResult,"//fits:externalIdentifier[@type='puid']/text()");
                             v = identicalValues(puid, fitsText, scale);
                         }
                     }  else if((OBJECT_IMAGE_DIMENSION_WIDTH + "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample, "//fits:imageWidth/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:imageWidth/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     }  else if((OBJECT_IMAGE_DIMENSION_HEIGHT + "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:imageHeight/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:imageHeight/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     }  else if((OBJECT_IMAGE_DIMENSION_ASPECTRATIO + "#equal").equals(propertyURI)) {
                         try {
                            int sampleHeight = Integer.parseInt(extractor.extractText(fitsDocSample,"//fits:imageHeight/text()"));
                            int resultHeight =  Integer.parseInt(extractor.extractText(fitsDocResult,"//fits:imageHeight/text()"));
                            int sampleWidth = Integer.parseInt(extractor.extractText(fitsDocSample,"//fits:imageWidth/text()"));
                            int resultWidth =  Integer.parseInt(extractor.extractText(fitsDocResult,"//fits:imageWidth/text()"));
                            
                            double sampleRatio = ((double)sampleWidth) / sampleHeight;
                            double resultRatio = ((double)resultWidth) / resultHeight;
                            v = scale.createValue();
                            ((BooleanValue)v).bool(0 == Double.compare(sampleRatio, resultRatio));
                            v.setComment(String.format("Reference value: %s\nActual value: %s", 
                                    formatter.formatFloat(sampleRatio),formatter.formatFloat(resultRatio)));
                        } catch (NumberFormatException e) {
                            // not all values are available - aspectRatio cannot be calculated 
                            v = scale.createValue();
                            v.setComment("Image width and/or height are not available - aspectRatio cannot be calculated");
                        }
                     }  else if((OBJECT_COMPRESSION_SCHEME + "#equal").equals(propertyURI)) {
                         v = identicalValues(sampleImageCompressionScheme, resultImageCompressionScheme, scale);
                     }  else if(OBJECT_COMPRESSION_LOSSLESS.equals(propertyURI)) {
                         // At the moment we only handle compression schemes of images
                         if ((resultImageCompressionScheme != null) && (!"".equals(resultImageCompressionScheme))) {
                             v = scale.createValue();
                             ((BooleanValue)v).bool(FITS_COMPRESSIONSCHEME_UNCOMPRESSED.equals(resultImageCompressionScheme));
                         }
                     }  else if(OBJECT_COMPRESSION_LOSSY.equals(propertyURI)) {
                         // At the moment we only handle compression schemes of images
                         if ((resultImageCompressionScheme != null) && (!"".equals(resultImageCompressionScheme))) {
                             v = scale.createValue();
                             ((BooleanValue)v).bool(! FITS_COMPRESSIONSCHEME_UNCOMPRESSED.equals(resultImageCompressionScheme));
                         }
                     }  else if((OBJECT_IMAGE_COLORENCODING_BITSPERSAMPLE + "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:bitsPerSample/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:bitsPerSample/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     }  else if((OBJECT_IMAGE_COLORENCODING_SAMPLESPERPIXEL + "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:samplesPerPixel/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:samplesPerPixel/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     }  else if((OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORSPACE + "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:colorSpace/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:colorSpace/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORPROFILE_ICCPROFILE+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:iccProfileName/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:iccProfileName/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_SPATIALMETRICS_SAMPLINGFREQUENCYUNIT+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:samplingFrequencyUnit/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:samplingFrequencyUnit/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_SPATIALMETRICS_XSAMPLINGFREQUENCY+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:xSamplingFrequency/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:xSamplingFrequency/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_SPATIALMETRICS_YSAMPLINGFREQUENCY+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:ySamplingFrequency/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:ySamplingFrequency/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                         
                     } else if ((OBJECT_IMAGE_METADATA+ "#equal").equals(propertyURI)) {
                         // we use the equal metric. reserve PRESERVED metric for later and get it right.
                         HashMap<String, String> sampleMetadata = extractor.extractValues(fitsDocSample, "//fits:exiftool/*[local-name() != 'rawdata']"); 
                         HashMap<String, String> resultMetadata = extractor.extractValues(fitsDocResult, "//fits:exiftool/*[local-name() != 'rawdata']");
                         v = preservedValues(sampleMetadata, resultMetadata, scale);
                     } else if ((OBJECT_IMAGE_METADATA_PRODUCER+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:ImageCreation/ImageProducer/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:ImageCreation/ImageProducer/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_METADATA_SOFTWARE+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:creatingApplicationName/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:creatingApplicationName/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_METADATA_CREATIONDATE+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:ImageCreation/DateTimeCreated/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:ImageCreation/DateTimeCreated/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_METADATA_LASTMODIFIED+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:fileinfo/lastmodified/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:fileinfo/lastmodified/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_METADATA_DESCRIPTION+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:exiftool/ImageDescription/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:exiftool/ImageDescription/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     } else if ((OBJECT_IMAGE_METADATA_ORIENTATION+ "#equal").equals(propertyURI)) {
                         String sampleValue = extractor.extractText(fitsDocSample,"//fits:exiftool/Orientation/text()");
                         String resultValue = extractor.extractText(fitsDocResult,"//fits:exiftool/Orientation/text()");
                         v = identicalValues(sampleValue, resultValue, scale);
                     }
                    

                     if (v!= null) {
                         v.setComment(v.getComment() + SOURCE);
                         results.put(measurementInfoUri, v);
                         listener.updateStatus(String.format("%s: evaluated measurement: %s = %s", NAME, measurementInfoUri.getAsURI(), v.toString()));                             
                     } else {
                         listener.updateStatus(String.format("%s: no evaluator found for measurement: %s", NAME, measurementInfoUri.getAsURI()));                             
                     }
                 }
            } catch (IOException e) {
                listener.updateStatus(" - could not read FITS xml");
            } catch (SAXException e) {
                listener.updateStatus(" - invalid FITS xml found");
            } catch (ParserConfigurationException e) {
                listener.updateStatus(" - invalid FITS xml found");
            }
        } else {
             listener.updateStatus(" - no FITS xml found");
        }
        return results;
    }

    private Value preservedValues(HashMap<String, String> sampleMetadata,
            HashMap<String, String> resultMetadata, Scale scale) {
        int numMissing = 0;
        int numChanged = 0;
        BooleanValue v = (BooleanValue)scale.createValue();
        StringBuilder comment = new StringBuilder();
        for (String key : sampleMetadata.keySet()) {
            String sampleValue = sampleMetadata.get(key);
            String resultValue = resultMetadata.get(key);
            if (resultValue == null) {
                numMissing ++;
                comment.append(" - " + key + "\n");
            } else if (!resultValue.equals(sampleValue)) {
                numChanged++;
                comment.append(" ~ " + key + ": sample="+sampleValue+", result="+resultValue+"\n");
            }
        }
        if ((numChanged ==  0)&&(numMissing == 0)) {
            v.bool(true);
            v.setComment("result contains complete metadata of sample");
        } else {
            v.bool(false);
            comment.insert(0, "following differences found: (- .. missing, ~ .. altered):\n");
            v.setComment(comment.toString());
        }
        return v;
    }

    
    private Value identicalValues(String v1, String v2, Scale s) {
        BooleanValue bv = (BooleanValue) s.createValue();
        String s1 = (v1 == null || "".equals(v1))? "UNDEFINED" : v1;
        String s2 = (v2 == null || "".equals(v2))? "UNDEFINED" : v2;
        
        if (!"UNDEFINED".equals(s1) && ! "UNDEFINED".equals(s2)) {
            // both values are defined:
            if (s1.equals(s2)) {
                bv.bool(true);
                bv.setComment("Both have value " + s1);
            } else {
                bv.bool(false);
                bv.setComment("Reference value: " + s1 + "\nActual value: " + s2);
            }
        } else if (s1.equals(s2)) {
            // both are undefined :
            bv.setComment("Both values are UNDEFINED");
            //bv.setValue("");
        } else {
            // one value is undefined:
            bv.setComment("Reference value: " + s1 + "\nActual value: " + s2);
        }
        return bv;
    }
}


