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
package eu.planets_project.pp.plato.massmigration;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.model.DetailedExperimentInfo;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.ToolExperience;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.scales.FreeStringScale;
import eu.planets_project.pp.plato.model.values.BooleanValue;
import eu.planets_project.pp.plato.model.values.FreeStringValue;
import eu.planets_project.pp.plato.model.values.INumericValue;
import eu.planets_project.pp.plato.services.action.IMigrationAction;
import eu.planets_project.pp.plato.services.action.MigrationResult;
import eu.planets_project.pp.plato.services.action.PreservationActionServiceFactory;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.MeasurementStatistics;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class MassMigrationRunner extends Thread implements Serializable {
    private static final long serialVersionUID = -2687062223716715348L;
    private static final String CSV_SEPARATOR =  ";";
    
    private Log log = PlatoLogger.getLogger(MassMigrationRunner.class);
    
    private File sourceDir;
    private File resultDir;
    
    private String[] inputFiles;
    
    MassMigrationSetup setup;
    
    public MassMigrationRunner(MassMigrationSetup setup){
        this.setup = setup;
    }
    
    private class FileDirectoryFilter implements FileFilter {
        public boolean accept(java.io.File f) {
            return (f.isFile() && !f.isHidden());
        }
    }

    public void run() {
        sourceDir = new File(setup.getSourcePath());
        resultDir = new File(setup.getResultPath());
        
        if (!sourceDir.exists() || !sourceDir.isDirectory() || !sourceDir.canRead()) {
            setup.getStatus().setStatus(MassMigrationStatus.FAILED);
            throw new IllegalArgumentException("Cannot read from source directory: " + setup.getSourcePath());
        }


        File currentResultdir = new File(resultDir, setup.getName()+ "-" + System.nanoTime());
        setup.setLastResultPath(currentResultdir.getAbsolutePath());
        
        if (!currentResultdir.mkdirs()) {
            setup.getStatus().setStatus(MassMigrationStatus.FAILED);
            throw new IllegalArgumentException("Cannot write to result directory: " + setup.getSourcePath());
        }
        try {
            File[] list = sourceDir.listFiles(new FileDirectoryFilter());
            List<String> array = new ArrayList<String>();
            for (int i = 0; i < list.length; i++) {
                array.add(list[i].getName());
            }
            inputFiles = array.toArray(new String[]{});
            java.util.Arrays.sort(inputFiles,String.CASE_INSENSITIVE_ORDER);
            
            Map<String, String> alternativesPaths = new HashMap<String, String>();
            // create directories for each alternative
            for (MassMigrationExperiment exp : setup.getExperiments()) {
                String altPath = FileUtils.makeFilename(makeUniqueActionName(exp.getAction()));
                File dir = new File(currentResultdir, altPath);
                if (dir.mkdir()) {
                    // add this path only if it can be created
                    alternativesPaths.put(exp.getAction().getShortname(), dir.getAbsolutePath());
                } 
                
            }
            
            setup.getStatus().setNumOfSamples(inputFiles.length);
            setup.getStatus().setNumOfTools(setup.getExperiments().size());
            int current = 0;

            for (String filename: inputFiles) {
                File file = new File(sourceDir+File.separator+filename);
                current++;
                int currentTool = 0;
                setup.getStatus().setCurrentSample(current);
                try {
                    byte[] data = FileUtils.getBytesFromFile(file);
                    /*
                     * migrate this file with every migration service, so we have to load it only once
                     */
                    for (MassMigrationExperiment exp : setup.getExperiments()) {
                        currentTool++;
                        setup.getStatus().setCurrentTool(currentTool);
                        
                        MigrationResult result = runExperiment(exp, file.getName(), data);
                        if ((result != null) && (result.isSuccessful())) {
                            // store migration result
                            String altPath = alternativesPaths.get(exp.getAction().getShortname());
                            if (altPath != null) {
                                File mResult = new File(altPath , file.getName() +"."+ result.getTargetFormat().getDefaultExtension());
                                OutputStream out = new BufferedOutputStream(new FileOutputStream(mResult));
                                out.write(result.getMigratedObject().getData().getData());
                                out.close();
                            }
                        }
                    }
                } catch (IOException ioe) {
                    log.error("Could not load file: " + file.getAbsolutePath(),ioe);
                } catch (Exception e) {
                    log.error("Exception while running experiment on file "+file.getAbsolutePath()+e.getMessage(),e);
                }
            }
            /* 
             * Calculation finished - collect result values and export them for further calculations
             */
            Locale locale = Locale.getDefault (  ) ; 
            NumberFormat format = NumberFormat.getNumberInstance( locale ) ;
            //new DecimalFormat("##########.##"); 
                                  
            
            ToolExperience toolExp = null;
            List<String> allProperties = new ArrayList<String>();
            Map<String, DetailedExperimentInfo> accumulatedAvg = new HashMap<String,DetailedExperimentInfo>();
            for (MassMigrationExperiment e : setup.getExperiments()) {
                
                /* calculate average per experiment */
                /* put measurements of sample files to toolExp */
                toolExp = MeasurementStatistics.generateToolExperience(e.getResult());
                /* get calculated average per property */
                DetailedExperimentInfo average = MeasurementStatistics.getAverage(toolExp); 
                accumulatedAvg.put(e.getAction().getShortname(), average);
                e.getAverages().clear();
                e.getAverages().put(average);

                /* a list of all properties to iterate over the values */
                allProperties.clear();
                allProperties.addAll(toolExp.getMeasurements().keySet());
                Collections.sort(allProperties);
                /*
                 * write all measurements of this experiment to a file 
                 */
                String statistics = FileUtils.makeFilename(makeUniqueActionName(e.getAction()));
                File statisticsFile = new File(currentResultdir, statistics+".csv");
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(statisticsFile));
                    StringBuffer header = new StringBuffer();
                    header.append("sample");
                    for (String key : allProperties) {
                       header.append(CSV_SEPARATOR).append(key);
                    }
                    /* write header */
                    out.append(header);                    
                    out.newLine();
                    List<String> keySet = new ArrayList<String>(e.getResult().keySet());
                    String[] toSort = keySet.toArray(new String[]{});
                    java.util.Arrays.sort(toSort,String.CASE_INSENSITIVE_ORDER);
                    
                    /* write measured values for all samples */
                    for (int i = 0; i<toSort.length; i++){
                       String sample = toSort[i];
                       /* 1. column: sample name */
                       out.append(sample);
                       /* followed by all properties */
                       DetailedExperimentInfo info = e.getResult().get(sample);
                       for(String prop: allProperties){
                          out.append(CSV_SEPARATOR);
                          Measurement m = info.getMeasurements().get(prop);
                          if (m != null) {
                             if (m.getValue() instanceof INumericValue) {
                                /* */
                                double value = ((INumericValue)m.getValue()).value();
                                out.append(format.format(value));
                             } else 
                                out.append(m.getValue().toString());
                          }
                       }
                       out.newLine();
                    }
                    /* write header again */
                    out.append(header);                    
                    out.newLine();
                    /* and write calculated average */
                    out.append("average");
                    for (String key : allProperties) {
                       out.append(CSV_SEPARATOR);
                       Measurement m = e.getAverages().getMeasurements().get(key);
                       if (m != null) {
                            if (m.getValue() instanceof INumericValue) {
                                double value = ((INumericValue)m.getValue()).value();
                                out.append(format.format(value));
                            } else 
                                out.append(m.getValue().toString());
                       }
                    }
                    out.newLine();
                    out.append("startupTime");
                    out.append(CSV_SEPARATOR);

                    try {
                        out.append(Double.toString(toolExp.getStartupTime()));
                    } catch (Exception ex) {
                        log.error("Error in calculating the startup time (linear regression): " + ex.getMessage());
                        out.append("Err");
                    }
                    
                    out.newLine();
                    out.close();
                } catch (IOException e1) {
                    log.error("Could not write statistics for: " + statistics, e1);
                }
            }
            /*
             * and write accumulated values 
             */
            File statisticsFile = new File(currentResultdir, "accumulated.csv");
            allProperties.clear();
            allProperties.add(MigrationResult.MIGRES_ELAPSED_TIME_PER_MB);
            allProperties.add(MigrationResult.MIGRES_USED_TIME_PER_MB);
            allProperties.add(MigrationResult.MIGRES_ELAPSED_TIME);
            allProperties.add(MigrationResult.MIGRES_USED_TIME);
            //...
            
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(statisticsFile));
                /* write machine info */
                if (toolExp != null) {
                    // use machine info of last experiment!
                    for (String prop: toolExp.getMeasurements().keySet()) {
                        if (prop.startsWith("machine:")){
                            out.append(prop)
                            .append(CSV_SEPARATOR)
                            .append(toolExp.getMeasurements().get(prop).getList().get(0).getValue().getFormattedValue());
                            out.newLine();
                        }
                    }
                    out.newLine();
                }
                /* write header */
                out.append("tool");
                for (String key : allProperties) {
                   out.append(CSV_SEPARATOR).append(key);
                }
                out.newLine();
                /* write averaged values for all actions */
                for (String action: accumulatedAvg.keySet()){
                   /* 1. column: action name */
                   out.append(action);
                   /* followed by all properties */
                   DetailedExperimentInfo average = accumulatedAvg.get(action);
                   for(String prop: allProperties){
                      out.append(CSV_SEPARATOR);
                      Measurement m = average.getMeasurements().get(prop);
                      if (m != null) {
                         if (m.getValue() instanceof INumericValue) {
                            /* */
                            double value = ((INumericValue)m.getValue()).value();
                            out.append(format.format(value));
                         } else 
                            out.append(m.getValue().toString());
                      }
                   }
                   out.newLine();
                }
                out.newLine();
                out.close();
            } catch (IOException e1) {
                log.error("Could not write accumulated statistics.", e1);
            }            
            
            setup.getStatus().setStatus(MassMigrationStatus.FINISHED);
        } catch (RuntimeException e) {
            setup.getStatus().setStatus(MassMigrationStatus.FAILED);
            log.error("Massmigration failed.", e);
        }
    }
    
    /**
     * Migrates data with service defined in experiment <param>e</param>s action, and stores the result
     * in experiment's results with key <param>filename</param>
     *  
     * @param e
     * @param filename
     * @param data
     */
    private MigrationResult runExperiment(MassMigrationExperiment e, String filename, byte[] data) {
        IMigrationAction service = getService(e.getAction());
        DetailedExperimentInfo eInfo = e.getResult().get(filename);
        if (eInfo == null) {
            eInfo = new DetailedExperimentInfo();
            e.getResult().put(filename, eInfo);
        }
        
        // remove old results
        eInfo.getMeasurements().clear();
        
        
        // why does this expect an instance of SampleObject ??!!
        SampleObject r = new SampleObject();
        r.getData().setData(data);
        r.setFullname(filename);
        
        Measurement success = new Measurement();
        success.setProperty(new MeasurableProperty(new BooleanScale(), MigrationResult.MIGRES_SUCCESS));
        success.setValue(success.getProperty().getScale().createValue());
        
        Measurement report = new Measurement();
        report.setProperty(new MeasurableProperty(new FreeStringScale(), MigrationResult.MIGRES_REPORT));
        report.setValue(report.getProperty().getScale().createValue());
        
        try {
            MigrationResult result = service.migrate(e.getAction(), r);
            if (result.isSuccessful()) {
                /* put all info to toolExperience */
                eInfo.getMeasurements().putAll(result.getMeasurements());

                ((BooleanValue)success.getValue()).setValue("true");
                ((FreeStringValue)report.getValue()).setValue(result.getReport());
            } else {
                ((BooleanValue)success.getValue()).setValue("false");
                ((FreeStringValue)report.getValue()).setValue(result.getReport());
            }
            // and return result, (including the migrated object)
            return result;
            
        } catch (Exception e1) {
            ((BooleanValue)success.getValue()).setValue("false");
            ((FreeStringValue)report.getValue()).setValue(e1.getMessage());
            log.error("Migration with service "+ e.getAction().getShortname()+" failed.");
        }
        return null;
    }

    /**
     * returns a matching service stub
     * could be use to cache already used stubs... 
     */
    private IMigrationAction getService(PreservationActionDefinition a){
        return (IMigrationAction)PreservationActionServiceFactory.getPreservationAction(a);
    }
    
    private static String makeUniqueActionName(PreservationActionDefinition action) {
        String key = "minimee/";
        String actionIdentifier = action.getUrl().substring(action.getUrl().indexOf(key)
                + key.length());
        return action.getShortname() + "_" + actionIdentifier; 
    }
}
