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

package eu.planets_project.pp.plato.test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.XMLCompare;
import eu.planets_project.pp.plato.xml.ProjectExporter;
import eu.planets_project.pp.plato.xml.ProjectImporter;

public class ProjectExporterImporterUnitTest {

    
    private long testRunId;
    private String currentVersionPath;
    
    private Logger log = Logger.getLogger(ProjectExporterImporterUnitTest.class);
    
    @BeforeSuite
    public void init() {
        URL url = Loader.getResource("testng-log.properties");
        
        if (url != null) {
            PropertyConfigurator.configure(url);
        }

        testRunId = System.currentTimeMillis();
        currentVersionPath = "test-data/plans/currentversion/";// + testRunId;
        makeCurrentVersionPlans();
    }
    
//    @Test
//    public void testExportImportTestProject() throws Exception{
//        File testDir = new File("test-output/");
// 
//        Plan testProject = TestProjectFactory.createMinimalistTestProject();
//        
//        ProjectExporter exporter = new ProjectExporter();
//        File minimalistFile = File.createTempFile("minimalist", ".xml", testDir);
//        exporter.exportToFile(testProject, minimalistFile);
//
//        ProjectImporter importer = new ProjectImporter();
//        List<Plan> plans =  importer.importProjects(minimalistFile.getAbsolutePath()); 
//        assert (plans.size() == 1);
//
//        System.out.println("before, after:");
//        System.out.println(testProject.getProjectBasis().getDocumentTypes());
//        System.out.println(plans.get(0).getProjectBasis().getDocumentTypes());
//        
//        File impexpFile = new File(minimalistFile.getAbsoluteFile() + ".impexp.xml");
//        exporter.exportToFile(plans.get(0), impexpFile);
//        
//        assert Arrays.equals(FileUtils.getBytesFromFile(minimalistFile), FileUtils.getBytesFromFile(impexpFile));
//        // remove files if successful
//        minimalistFile.delete();
//        impexpFile.delete();
//    }
//
//    @Test
//    public void testImportInvalidFile(){
//        ProjectImporter importer = new ProjectImporter();
//        List<Plan> plans = null;
//        
//        System.out.println("InvalidProjectElement:");
//
//        try {
//            plans = importer.importProjects("data/projects/InvalidProjectElement.xml");
//            assert false;
//        } catch (Exception e) {
//            System.out.println("true, this plan is not valid.");
//        }
//        
//        
//        System.out.println("Invalidfile:");
//        try {
//            plans = importer.importProjects("data/projects/InvalidFile.xml"); 
//            assert false;
//        } catch (Exception e) {
//            System.out.println("true, this plan is not valid.");
//        }
//
//        System.out.println("InvalidProjectState:");
//        try {
//            plans = importer.importProjects("data/projects/InvalidProjectState.xml");
//            assert false;
//        } catch (Exception e) {
//            System.out.println("true, this plan is not valid.");
//        }
//
//        System.out.println("Two Projects per file:");
//        try {
//            plans = importer.importProjects("data/projects/TwoProjects.xml");
//            assert false;
//        } catch (Exception e) {
//            System.out.println("true, this plan is not valid.");
//        }
//      
//    }
    
    private void testPlansInDir(String planPath, String outputPath) {
        File[] demoFiles = new File(planPath).listFiles(
                new FilenameFilter() {
                    public boolean accept(File dir, String file) {
                        return (file.toLowerCase().endsWith(".xml"));
                    }
                }
                );
        boolean success = true;
        for (File demoFile : demoFiles) {
            ProjectImporter importer = new ProjectImporter();
            ProjectExporter exporter = new ProjectExporter();

            //
            // for xml compare, we leave the jhoveXML out. we don't compare it.
            // it changes after import, because we call jhove when importing a plan
            List<String> nodesToExclude = new ArrayList<String>();
            nodesToExclude.add("//*[name()='jhoveXML']");
            nodesToExclude.add("//*[name()='changelog']");
            
            String logText = "";
            
            try {
                
                logText = "Test plan: " + demoFile.getAbsolutePath();
                System.out.println(logText);
                log.info(logText);
                
                List<Plan> importedPlans = importer.importProjects(demoFile.getAbsolutePath());
                
                assert importedPlans.size() == 1;
                
                logText = " - organisation: "+ importedPlans.get(0).getPlanProperties().getOrganization();
                
                System.out.println(logText);
                log.info(logText);
                
                File exportedPlanFile = new File(outputPath + demoFile.getName() + ".impexp.xml");

                exporter.exportToFile( importedPlans.get(0), exportedPlanFile);
                
                String sorted1 = createSortedXml(demoFile);
                String sorted2 = createSortedXml(exportedPlanFile);
                
                logText = demoFile +" -> " + sorted1;
                System.out.println(logText);
                log.info(logText);
                
                logText = exportedPlanFile +" -> " + sorted2;
                System.out.println(logText);
                log.info(logText);
                
                XMLCompare xmlCompare = new XMLCompare();
                xmlCompare.setExcludedNodes(nodesToExclude);
                
                if (xmlCompare.compareXml(sorted1, sorted2, true, true)) {
                    // they are equal, remove the exported plan
                    exportedPlanFile.delete();
                    log.info("SUCCESS");
                    
                } else {
                    logText = " - failed: " + System.getProperty("line.separator") + xmlCompare.getErrorMessage();
                    
                    System.out.println(logText);
                    log.info(logText);
                    
                    success = false;
                }
                
            } catch (Exception e) {
                logText = " - failed: " + System.getProperty("line.separator") + e.getMessage();
                
                System.out.println(logText);
                log.info(logText, e);
                
                success = false;
                e.printStackTrace();
            }
        }
        Assert.assertTrue(success, "At least some plans are not the same after export/import, check dir: " + outputPath); 
    }
    
    private String createSortedXml(File original) {
        
        ProjectImporter importer = new ProjectImporter();
        
        String file = System.getProperties().getProperty("java.io.tmpdir") + "/sorted" + System.nanoTime() + ".xml";
        
        try {
            //File xslt = new File("data/xslt/sort.xsl");
            if (!importer.transformXmlData(original.getAbsolutePath(), file, "data/xslt/compareFilter.xsl")) {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        
        return file;
    }
    
//    @Test
//    public void testImportExportDemoPlans() {
//        String outputPath = "test-output/plans/demo_" + testRunId + "/";
//        new File(outputPath).mkdirs();
//        testPlansInDir("data/projects/demos/", outputPath);
//    }
    
    @Test
    public void testImportExportCurrentVersionPlans() {
        String outputPath = "test-output/plans/failed/";
        new File(outputPath).mkdirs();
        testPlansInDir(currentVersionPath, outputPath);
    }
    
    private void makeCurrentVersionPlans(){
        File originalDir = new File("test-data/plans/original/");
        File currentVersionDir = new File(currentVersionPath);
        // remove all old files
        OS.deleteDirectory(currentVersionDir);
        currentVersionDir.mkdirs();
        
        File[] originalFiles = originalDir.listFiles(
                new FilenameFilter() {
                    public boolean accept(File dir, String file) {
                        return (file.toLowerCase().endsWith(".xml"));
                    }
                }
                );
        String tempPath = currentVersionPath + "migrations/";
        File tempDir = new File(tempPath);
        ProjectImporter importer = new ProjectImporter();
        for (int i = 0; i < originalFiles.length; i++) {
            tempDir.mkdirs();
            File file = originalFiles[i];
            try {
                System.out.println("migrating plan: " + file.getAbsolutePath());
                String currentFile = importer.getCurrentVersionData(
                        new FileInputStream(file), tempPath);
                if (currentFile == null) {
                    System.out.println(" - failed");
                } else {
                    File current = new File(currentFile);
                    current.renameTo(new File(currentVersionPath +  file.getName()));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            OS.deleteDirectory(tempDir);
        }
    }

}
