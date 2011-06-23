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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import org.junit.Test;

import eu.planets_project.pp.plato.model.measurement.MeasurementInfo;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.LibraryRequirement;
import eu.planets_project.pp.plato.model.tree.LibraryTree;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.xml.LibraryExport;

public class LibraryExportTest {

    @Test
    public void testExportImport() throws Exception{
        LibraryTree lib = new LibraryTree();
        lib.addMainRequirements();
        lib.getRoot().setDescription("This is a description");
        Leaf c = new Leaf();
        c.setName("ImageWidth");
        MeasurementInfo mInfo = new MeasurementInfo();
        mInfo.fromUri("xcl://imageWidth#equal");
        c.setMeasurementInfo(mInfo);
        ((LibraryRequirement)lib.getRoot().getChildren().get(0).getChildren().get(0)).addChild(c);
        
        LibraryExport export = new LibraryExport();
        File exported = new File ("test-data/lib_export.xml");
        export.exportToStream(lib, new FileOutputStream(exported));
        
        lib = export.importFromStream(new FileInputStream(exported));
        File reExported = new File(exported + "_out.xml");
        export.exportToStream(lib, new FileOutputStream(reExported));
        
        if (Arrays.equals(FileUtils.getBytesFromFile(exported), FileUtils.getBytesFromFile(reExported))) {
            // they are equal, remove the exported plan
            exported.delete();
            reExported.delete();
        } else {
            fail("imported and exported files are not equal!");
        }
        
        
    }


}
