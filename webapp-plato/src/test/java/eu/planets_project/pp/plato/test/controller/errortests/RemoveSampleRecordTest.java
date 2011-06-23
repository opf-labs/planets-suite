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
package eu.planets_project.pp.plato.test.controller.errortests;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.action.workflow.IdentifyRequirementsAction;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.test.controller.MockTreeValidator;

public class RemoveSampleRecordTest extends SeamTest {

    @Test
    public void testProjects() throws Exception {

        new FacesRequest("/project/loadPlan.xhtml") {

            @Override
            protected void invokeApplication() {

                invokeMethod("#{loadPlan.listProjects}");
            }

            @Override
            protected void renderResponse() {

                DataModel projects = (DataModel)Contexts.getSessionContext().get("projectList");

                assert projects.getRowCount() > 1;
            }

        }.run();

        new FacesRequest("/project/loadPlan.xhtml") {

            @Override
            protected void invokeApplication() {

                // we select the TNA project ...
                ListDataModel projects = (ListDataModel)getInstance("projectList");

                projects.setRowIndex(0);

                // ... and load it
                invokeMethod("#{loadPlan.load}");

                Plan p = (Plan)getValue("#{selectedPlan}");

                assert p != null;

                invokeMethod("#{defineSampleRecords.enter}");
            }

        }.run();

        new FacesRequest("") {

            @Override
            protected void updateModelValues() throws Exception {

                DataModel records = (DataModel)Contexts.getSessionContext().get("records");

                assert records != null;

                List<SampleObject> r = (List<SampleObject>)records.getWrappedData();

                assert r.size() > 0;

                System.out.println("Nr of sample records: " + r.size());

                for (int i = 0; i < records.getRowCount(); i++) {
                    records.setRowIndex(i);
                    System.out.println("Sample Record: " + ((SampleObject)records.getRowData()).getFullname());
                }

                Contexts.getSessionContext().set("record", r.get(0));
            }

            @Override
            protected void invokeApplication() throws Exception {

                ListDataModel records = (ListDataModel)getInstance("records");

                assert records.getRowCount() > 0;

                records.setRowIndex(0);

                invokeMethod("#{defineSampleRecords.askRemoveRecord}");
            }

            @Override
            protected void renderResponse() throws Exception {

                SampleObject toDelete = (SampleObject)Contexts.getSessionContext().get("record");

                int allowRemove = Integer.parseInt(Contexts.getSessionContext().get("allowRemove").toString());

                assert toDelete.getId() == allowRemove;
            }

        }.run();

        new FacesRequest("") {

            @Override
            protected void invokeApplication() throws Exception {

                DataModel records = (DataModel)Contexts.getSessionContext().get("records");

                int nrOfRecords = records.getRowCount();

                invokeMethod("#{defineSampleRecords.removeRecord}");

                records = (DataModel)Contexts.getSessionContext().get("records");

                assert nrOfRecords == records.getRowCount()+1;
            }

            @Override
            protected void renderResponse() throws Exception {

                assert false == isValidationFailure();
            }

        }.run();
        /*
         * save before adding a new record
         *
        new FacesRequest("") {

            @Override
            protected void invokeApplication() throws Exception {

                DataModel records = (DataModel)Contexts.getSessionContext().get("records");

                int sampleRecordCount = records.getRowCount();

                String ret = (String)invokeMethod("#{defineSampleRecords.save}");
                records = (DataModel)Contexts.getSessionContext().get("records");
                assert sampleRecordCount == records.getRowCount();
            }

        }.run();
        
*/
        /*
         * add a new record and proceed to next step
         */
        new FacesRequest("") {

            @Override
            public void updateModelValues() throws Exception {

                String file = "test content";

                Contexts.getSessionContext().set("fileName", "testFile");
                Contexts.getSessionContext().set("contentType", "txt");
                Contexts.getSessionContext().set("file", file.getBytes());

            }

            @Override
            protected void invokeApplication() throws Exception {

                DataModel records = (DataModel)Contexts.getSessionContext().get("records");

                int sampleRecordCount = records.getRowCount();

                String ret = (String)invokeMethod("#{defineSampleRecords.upload}");

                records = (DataModel)Contexts.getSessionContext().get("records");

                records.setRowIndex(1);
                ((SampleObject)records.getRowData()).setShortName("just a short name");

                assert sampleRecordCount+1 == records.getRowCount();

                ret = (String)invokeMethod("#{defineSampleRecords.proceed}");

                System.out.println ("return of proceed: " + ret + " sample record count: " + (sampleRecordCount+1));
            }

        }.run();

        new FacesRequest("workflow/identrequirements.xhtml") {

            @Override
            protected void applyRequestValues() throws Exception {

                ValueBinding vb = getFacesContext().getApplication().createValueBinding("#{identifyRequirements.table}");

                assert vb != null;

                Class clazz = vb.getType(getFacesContext());

                Constructor c = clazz.getConstructor(null);

                assert c != null;

                System.out.println ("Type=" +clazz.getSimpleName());

                IdentifyRequirementsAction ir = (IdentifyRequirementsAction)getInstance(IdentifyRequirementsAction.class);

                assert ir != null;

                ir.setValidator(new MockTreeValidator());

            }

            @Override
            protected void invokeApplication() throws Exception {

                invokeMethod("#{identifyRequirements.proceed}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert false == isValidationFailure();
            }

        }.run();

        new FacesRequest("workflow/definealternatives.xhtml") {

            @Override
            protected void invokeApplication() throws Exception {

                invokeMethod("#{defineAlternatives.proceed}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert false == isValidationFailure();
            }

        }.run();

        new FacesRequest("workflow/gonogo.xhtml") {

            @Override
            protected void invokeApplication() throws Exception {

                invokeMethod("#{gonogo.proceed}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert false == isValidationFailure();
            }

        }.run();

        new FacesRequest("workflow/developexperiment.xhtml") {

            @Override
            protected void invokeApplication() throws Exception {

                invokeMethod("#{devexperiments.proceed}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert false == isValidationFailure();
            }

        }.run();

        new FacesRequest("workflow/runexperiment.xhtml") {

            @Override
            protected void invokeApplication() throws Exception {

                invokeMethod("#{runexperiments.proceed}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert false == isValidationFailure();
            }

        }.run();


        new FacesRequest("workflow/evaluateexperiment.xhtml") {

            @Override
            protected void updateModelValues() throws Exception {
                Plan p = (Plan)getValue("#{selectedPlan}");

                assert p != null;

                List<Leaf> leaves = p.getTree().getRoot().getAllLeaves();

                assert leaves.size() > 0;

                Contexts.getSessionContext().set("leaves", leaves);
            }


            @Override
            protected void invokeApplication() throws Exception {
                invokeMethod("#{evalexperiments.approve}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert false == isValidationFailure();

                List<Leaf> leaves = (List<Leaf>)Contexts.getSessionContext().get("leaves");

                assert leaves != null && leaves.size() > 0;

                Plan p = (Plan)getValue("#{selectedPlan}");

                assert p != null;

                List<Alternative> alternatives = p.getAlternativesDefinition().getConsideredAlternatives();

                for (Leaf leaf : leaves) {

                    for (Alternative alter : alternatives) {

                        if (leaf.isSingle()) {

                            // if (leaf.getScale().getType() == ScaleType.ordinal) {

                                Values vs = leaf.getValueMap().get(alter.getName());

                                assert vs != null;

                                assert vs.getList().get(0) != null;
                            // } else {

                            // }
                        } else {

                            int i = 0;
                            for (SampleObject record : p.getSampleRecordsDefinition().getRecords()) {

                                Values vs = leaf.getValueMap().get(alter.getName());

                                assert vs.getList().get(i++) != null;
                            }
                        }
                    }

                }
            }

        }.run();


    }
}
