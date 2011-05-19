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

import java.io.ByteArrayOutputStream;

import org.dom4j.io.XMLWriter;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.xml.ProjectExporter;

public class ProjectExporterUnitTest {

    @Test
    public void testPlainExport() throws Exception {
        ProjectExporter exporter = new ProjectExporter();

        String s = exporter.exportToString(TestProjectFactory.createMinimalistTestProject());

        Plan q = TestProjectFactory.createMinimalistTestProject();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        XMLWriter writer = new XMLWriter(buffer, ProjectExporter.prettyFormat);

        assert writer != null;

        writer.write(exporter.exportToXml(q));
        writer.close();
        assert s.equals(buffer.toString());
    }

}
