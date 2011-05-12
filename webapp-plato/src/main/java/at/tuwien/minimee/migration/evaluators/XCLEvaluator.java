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
package at.tuwien.minimee.migration.evaluators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.measurement.Measurements;
import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.scales.FloatRangeScale;
import eu.planets_project.pp.plato.model.scales.IntegerScale;
import eu.planets_project.pp.plato.model.scales.PositiveIntegerScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.characterisation.xcl.ComparatorUtils;
import eu.planets_project.pp.plato.services.characterisation.xcl.CompareResult;
import eu.planets_project.pp.plato.services.characterisation.xcl.CprMetricResult;
import eu.planets_project.pp.plato.services.characterisation.xcl.CprProperty;
import eu.planets_project.pp.plato.services.characterisation.xcl.MetricToScaleMapping;
import eu.planets_project.pp.plato.util.CommandExecutor;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class XCLEvaluator implements IMinimeeEvaluator {
    
    private Log log = PlatoLogger.getLogger(this.getClass());
    
    private String configParam;
    private String name;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<MeasurableProperty> getMeasurableProperties() {
        List<MeasurableProperty> list = new ArrayList<MeasurableProperty>();
        list.add(new MeasurableProperty(new BooleanScale(),"xcl:imageWidth:equal"));
        list.add(new MeasurableProperty(new BooleanScale(),"xcl:imageHeight:equal"));
        list.add(new MeasurableProperty(new BooleanScale(),"xcl:bitsPerSample:equal"));
        list.add(new MeasurableProperty(new IntegerScale(),"xcl:bitsPerSample:intDiff"));
        list.add(new MeasurableProperty(new BooleanScale(),"xcl:interlace:equal"));
        list.add(new MeasurableProperty(new BooleanScale(),"xcl:transparency:equal"));
        list.add(new MeasurableProperty(new BooleanScale(),"xcl:backgroundColour:equal"));
        list.add(new MeasurableProperty(new PositiveIntegerScale(),"xcl:rgbPalette:hammingDistance"));
        list.add(new MeasurableProperty(new FloatRangeScale(),"xcl:gamma:percDeviation"));
        return list;
    }
    /**
     * file = input file to extract
     * NOT NEEDED ANYMORE xcel = absolute path to XCEL
     * xcdl = absolute path to outputFILE
     * 
     * It is not sufficient to set the working directory and call via "extract infile ...", 
     * as linux requires a preceding "./", and windows does not accept this.
     * therefore we need to expand this command before execution.
     * 
     */
    private String extractorCommand = "extractor -o %XCDL% %FILE%";
    
    /**
     * XCDL1 = xcdl of input object
     * XCDL2 = xcdl of result object
     * PCR = pcr config file
     * OUTDIR = output DIRECTORY, not path(!)
     * 
     * It is not sufficient to set the working directory and call via "comparator infile ...", 
     * as linux requires a preceding "./", and windows does not accept this.
     * therefore we need to expand this command before execution.
     *
     */
    private String comparatorCommand = "comparator %XCDL1% %XCDL2% -c %PCR% -o %OUTDIR%";
    
    protected String prepareWorkingDirectory(String tempDir,long time) throws Exception {
        String workingDir = makeWorkingDirectoryName(tempDir,time);
        new File(workingDir).mkdir();
        return workingDir;
    }

    /**
     * @param time
     * @return
     */
    private String makeWorkingDirectoryName(String tempDir,long time) {
        return tempDir+"xcl"+time+"/";
    }
    
    private String extract(String file) {
        String outXCDL = file+".xcdl";
        CommandExecutor cmdExecutor = new CommandExecutor();     
        try {
            cmdExecutor.setWorkingDirectory(getConfigParam());
            /* now we have to expand the command with the working directory! */
            String cmd = getConfigParam() + extractorCommand.replace("%FILE%", file)
                                         .replace("%XCDL%", outXCDL); 
            
            int exitStatus = cmdExecutor.runCommand(cmd);
            if (exitStatus == 0) {
               // WHAT IS THE OUTPUT FILE??
            } else {
                log.error("Problem calling Extractor on file "+file+" : "+cmdExecutor.getCommandError());
                outXCDL = null;
            }
        } catch (Exception e) {
          log.error("Problem calling Extractor on file "+file,e);
          outXCDL = null;
        }        
        return outXCDL;
    }
    
    
    private String compare(String in, String out, String pcr, String workingDir) {
        CommandExecutor cmdExecutor = new CommandExecutor();     
        try {
            cmdExecutor.setWorkingDirectory(getConfigParam());
            /* now we have to expand the command with the working directory! */
            String cmd = getConfigParam() + comparatorCommand.replace("%XCDL1%", in)
                                          .replace("%XCDL2%", out)
                                          .replace("%PCR%", pcr)
                                          .replace("%OUTDIR%", workingDir);
            
            int exitStatus = cmdExecutor.runCommand(cmd);
            if (exitStatus != 0) {
                log.error("Problem calling Comparator on files "+in+", "+out+", "+pcr+" : "+cmdExecutor.getCommandError());
                return "";
            }
        } catch (Exception e) {
          log.error("Problem calling Comparator on files "+in+", "+out+", "+pcr,e);
          return "";
        }        
       return workingDir+"cpr.cpr";
    }
    
    public List<Measurement> evaluate(String tempDir, String inFile,String outFile) {
        String inXCDL = "";
        String outXCDL = "";
        String pcrFile = ""; // use predefined path(s) configured in the tool-config?
        String cprFile = "";
        long time = System.nanoTime();
        String wDir = makeWorkingDirectoryName(tempDir,time);
        new File(wDir).mkdir();
        List<Measurement> list = new ArrayList<Measurement>();
        
        
        try {
            try {
                // extract both XCDLs
                inXCDL = extract(inFile);
                outXCDL = extract(outFile);
                if ((inXCDL == null)||(outXCDL == null)) {
                    log.error("Cannot start evaluation, XCDL files are missing. in = " + inXCDL + ", out = " + outXCDL);
                } else {
                    pcrFile = makePCR(time);
                    cprFile = compare(inXCDL,outXCDL,pcrFile,wDir);   
    
                    if (!"".equals(cprFile)) {
                        // digest output (cf. plato integration)
                        Measurements measurements =
                            extractMeasurements(cprFile, getMeasurableProperties());
                        // write stats (only stats for the moment) to MigrationResult
                        for (Measurement m : measurements.getList()) {
                            list.add(m);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("XCLEvaluator failed: "+e.getMessage(), e);
            }
            return list;
        } finally {
            // We have to do the cleanup here, at least atm.
            // When using the working dir we might be able to do it in cleanup(), no ?
            new File(inXCDL).delete();
            new File(outXCDL).delete();
            //new File(pcrFile).delete();
            new File(cprFile).delete();
        }
    }

    private Measurements extractMeasurements(String cprFile, List<MeasurableProperty> properties) {
        Measurements result = new Measurements();
        try {
            ComparatorUtils compUtils = new ComparatorUtils();
            List<CompareResult> compResult = compUtils.parseResponse(cprFile);
            if (compResult.size() == 1) {
                /* we compare two xcdl files, which correspond to one compSet */
                for(MeasurableProperty p : properties) {
                    /* 
                     * get all values for measureable properties that correspond to xcl properties:
                     * they have the pattern: xcl:<propertyName>:<metricName>
                     */
                    String[] keyParts =  p.getName().split(":");
                    if (keyParts.length == 3) {
                        if ("xcl".equals(keyParts[0])) {
                            CprProperty cprP = 
                               compResult.get(0).getProperties().get(keyParts[1]);
                            if (cprP != null) {
                                String id = MetricToScaleMapping.getMetricId(keyParts[2]);
                                if (id != null) {
                                    Value v = null;                                    
                                    Scale s = MetricToScaleMapping.getScale(id);
                                    if (s == null) {
                                        // There is a new XCLMetric we have not registered in MetricToScaleMapping yet...
                                        log.debug("CPR: unkown metricId: " +  id);
                                    } else {
                                        // scale found, so we can create a value object
                                        v =  s.createValue();
                                        v.setScale(null);
                                        
                                        CprMetricResult mResult = cprP.getResultMetrics().get(id);
                                        if (mResult != null) {
                                            if ("ok".equals(mResult.getState())) {
                                                v.parse(mResult.getResult());
                                                v.setComment("xcdl values(sample::result)=(" + cprP.getSource()+"::" + cprP.getTarget()+")");
                                            } else {
                                                v.setComment(mResult.getResult());
                                            }
                                        }                                        
                                    }
                                    if (v != null) {
                                        Measurement m = new Measurement();
                                        m.setProperty(p);
                                        // TODO: validate type of value with respect to property(scale)
                                        m.setValue(v);
                                        result.addMeasurement(m);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (PlatoServiceException e) {
            log.error("Problem extracting cpr values of:"+cprFile,e);
        }
        
        
        
        return result;
    }
/* Extractor uses DROID for identification!
 * 
    private String getXCEL(String ending) {
        if ("PNG".equals(ending.toUpperCase())) {
            return getConfigParam()+"res/xcl/xcel/xcel_docs/xcel_png.xml";
        }
        if ("TIFF".equals(ending.toUpperCase()) || "TIF".equals(ending.toUpperCase())) {
            return getConfigParam()+"res/xcl/xcel/xcel_docs/xcel_tiff.xml";
        }
        return getConfigParam()+"res/xcl/xcel/xcel_docs/xcel_imageMagick.xml";
    }
*/
    private String makePCR(long time) {
        // construct or choose or use-the-same PCR (?)
        
        return getConfigParam() + "PCRMultiImage.xml";
    }
    
    public String getConfigParam() {
        return configParam;
    }

    public void setConfigParam(String configParam) {
        this.configParam = configParam;
    }
}
