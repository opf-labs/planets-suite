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

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.Plan;


public class NewProjectTest extends SeamTest {

    @Test
    public void testNewProject () throws Exception {

        String id;
        id = new FacesRequest("/project/newproject.xhtml") {

            @Override
            protected void applyRequestValues()throws Exception {
            }

            @Override
            protected void invokeApplication() throws Exception {

                invokeMethod("#{newProject.createProject}");
            }

            @Override
            protected void renderResponse() throws Exception {
                Plan p = (Plan)getValue("#{selectedPlan}");

                assert p == null;
            }

        }.run();
    }
}
