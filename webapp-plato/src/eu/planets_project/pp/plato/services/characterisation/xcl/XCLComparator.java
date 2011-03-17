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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.evaluation.MeasurementsDescriptor;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.util.CommandExecutor;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * XCL comparator, for XCLEvaluator
 * 
 * The comparator is used as command line tool and not invoked through the Planets Service interface.
 * This way we also get the values, and the compare results for all metrics.  
 * 
 * 
 * @author Michael Kraxner
 *
 */
public class XCLComparator {
    
    private Log log = PlatoLogger.getLogger(this.getClass());

    private String configParam;
    private String comparatorHome;
    private String extractorHome;

    private String name;
    
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
    private String comparatorCommand = "comparator %XCDL1% %XCDL2%  -outdir %OUTDIR% -makelog -outdata PROPVAL";
    
    MeasurementsDescriptor descriptor;
    
    public XCLComparator(MeasurementsDescriptor descriptor) {
        this.descriptor = descriptor;
        setConfigParam(System.getenv("XCLTOOLS_HOME"));
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
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
        return OS.completePathWithSeparator(OS.completePathWithSeparator(tempDir)+"xcl"+time);
    }
    
    /**
     * generates a XCDL description for <param>filename</param>
     * and stores the description in a temp file relative to the given file
     * silent call: if describing with XCDL fails, <code>null</null> is returned.
     * 
     * @param filename
     * @return filename of XCDL description: <param>filename</param> + .xcdl
     */
    public String extractXCDL(String filename) {
        String outXCDL = filename+".xcdl";
        CommandExecutor cmdExecutor = new CommandExecutor();     
        try {
            cmdExecutor.setWorkingDirectory(extractorHome);
            /* now we have to expand the command with the working directory! */
            String cmd = extractorHome + extractorCommand.replace("%FILE%", filename)
                                         .replace("%XCDL%", outXCDL); 
            
            int exitStatus = cmdExecutor.runCommand(cmd);
            if (exitStatus == 0) {
               // WHAT IS THE OUTPUT FILE??
            } else {
                log.error("Problem calling Extractor on file "+filename+" : "+cmdExecutor.getCommandError());
                outXCDL = null;
            }
        } catch (Exception e) {
          log.error("Problem calling Extractor on file "+filename,e);
          outXCDL = null;
        }        
        return outXCDL;
    }
    
    
    /**
     * compares two XCDL files using the given comparator configuration and writes creates a result file
     * in the given working directory.
     *  
     * @param in
     * @param out
     * @param pcr
     * @param workingDir
     * @return filename of the comparator result (including the path), or an empty string if the call failed
     */
    private String callComparator(String in, String out, String pcr, String workingDir) {
        CommandExecutor cmdExecutor = new CommandExecutor();     
        try {
            cmdExecutor.setWorkingDirectory(comparatorHome);
            /* now we have to expand the command with the working directory! */
            String cmd = comparatorHome + comparatorCommand.replace("%XCDL1%", in)
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
       return workingDir+"copra.xml";
    }
    
    /**
     * evaluates to XCDL files
     * 
     * @param tempDir
     * @param inFile
     * @param outFile
     * @return
     */
    public HashMap<MeasurementInfoUri, Value> compare(String tempDir, String inXCDL,String outXCDL, List<MeasurementInfoUri> measurementInfoUris) {
        String pcrFile = ""; // use predefined path(s) configured in the tool-config?
        String cprFile = "";
        long time = System.nanoTime();
        String wDir = makeWorkingDirectoryName(tempDir,time);
        File workingDir = new File(wDir);
        workingDir.mkdir();
        
        try {
            try {
                // extract both XCDLs
                if ((inXCDL == null)||(outXCDL == null)) {
                    log.error("Cannot start evaluation, XCDL files are missing.");
                } else {
                    // generate the configuration for the comparator
                    pcrFile = makePCR(measurementInfoUris);
                    cprFile = callComparator(inXCDL,outXCDL,pcrFile,wDir);   
    
                    if (!"".equals(cprFile)) {
                        // digest output (cf. plato integration)
                        return extractMeasurements(cprFile, measurementInfoUris);
                    }
                }
            } catch (Exception e) {
                log.error("XCLEvaluator failed: "+e.getMessage(), e);
            }
            return null;
        } finally {
            // We have to do the cleanup here, at least atm.
            // When using the working dir we might be able to do it in cleanup(), no ?
            //new File(pcrFile).delete();
            OS.deleteDirectory(workingDir);
        }
    }

    private HashMap<MeasurementInfoUri, Value> extractMeasurements(String cprFile, List<MeasurementInfoUri> measurementInfoUris) {
        HashMap<MeasurementInfoUri, Value> result = new HashMap<MeasurementInfoUri, Value>();
        try {
            ComparatorUtils compUtils = new ComparatorUtils();
            List<CompareResult> compResult = compUtils.parseResponse(cprFile);
            if (compResult.size() == 1) {
                /* we compare two xcdl files, which correspond to one compSet */
                for(MeasurementInfoUri info : measurementInfoUris) {
                    // collect results for the given measurement-info-uris
                    String propertyId = info.getPath().replace("object/xcl/", "");
                    
                    CprProperty resultProperty = compResult.get(0).getProperties().get(propertyId);
                    if (resultProperty != null) {
                        // Note:
                        // 1. there are only derived measurements in XCL
                        // 2. the fragment of an xcl - measurable property always corresponds to a xcl metric 
                        String metricId = info.getFragment();
                        
                        Value v = null;
                        Scale s = descriptor.getMeasurementScale(info);
                        if (s != null) {
                            v = s.createValue();
                            v.setScale(null);
                        }
                        if ((metricId != null)&&(v != null)) {
                            CprMetricResult mResult = resultProperty.getResultMetrics().get(metricId);
                            if (mResult != null) {
                                if ("ok".equals(mResult.getState())) {
                                    v.parse(mResult.getResult());
                                    result.put(info, v);
                                    v.setComment("xcdl values(sample::result)=(" + resultProperty.getSource()+"::" + resultProperty.getTarget()+")");
                                } else {
                                    log.debug("evaluation failed: " + mResult.getResult());
                                }
                            }
                        }
                    }
                }
            } else {
                log.error("Comparator should return exactly one compResult, but there were: " + compResult.size());
            }
        } catch (PlatoServiceException e) {
            log.error("Problem extracting cpr values of:"+cprFile,e);
        }
        
        
        
        return result;
    }

    /**
     * generates a comparator configuration file and returns the full path.
     * 
     * @param measurementInfoUris
     * @return
     */
    private String makePCR(List<MeasurementInfoUri> measurementInfoUris) {
        // TODO: improve performance by generating a config out of all used measurementInfos (Plato 3.1)        
        // - at the moment we use the default config
        return comparatorHome + "config.xml";
    }
    
    public String getConfigParam() {
        return configParam;
    }

    public void setConfigParam(String param) {
        configParam = param;
        if (configParam != null) {
            configParam = OS.completePathWithSeparator(configParam);
            comparatorHome = OS.completePathWithSeparator(configParam + "comparator");
            extractorHome = OS.completePathWithSeparator(configParam + "extractor");
        } else {
            log.error("XCLTOOLS - HOME not defined.");
            comparatorHome = "";
            extractorHome = "";
        }
    }
}

