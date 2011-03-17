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

import java.lang.reflect.Constructor;
import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.action.workflow.IdentifyRequirementsAction;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanProperties;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.tree.Leaf;

public class LoadPlanTest extends SeamTest {

    @Test
    public void testProjects() throws Exception {

        /*
        new FacesRequest("/project/loadPlan.xhtml") {

            @Override
            protected void invokeApplication() {

                System.out.println("===== before createEntityManagerFactory");
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("platoDatabase");

                System.out.println("===== after createEntityManagerFactory");
                EntityManager em = emf.createEntityManager();
            }

        }.run();
*/
        new FacesRequest("/project/loadPlan.xhtml") {

            @Override
             protected void applyRequestValues() {

                UIViewRoot root = getFacesContext().getViewRoot();

                System.out.println ("ViewID=" + root.getViewId());

                System.out.println ("Child count=" + root.getChildCount());

                invokeMethod("#{loadPlan.listProjects}");
            }

            @Override
            protected void invokeApplication() {

                invokeMethod("#{loadPlan.listProjects}");
            }

            @Override
            protected void renderResponse() {

                DataModel projects = (DataModel)Contexts.getSessionContext().get("projectList");

                assert projects.getRowCount() > 1;

                List<PlanProperties> p = (List<PlanProperties>)projects.getWrappedData();

                System.out.println( "---No. of projects: " + projects.getRowCount() + " ---PROJECT NAME: " + p.get(2).getName());
            }

        }.run();

        new FacesRequest("/project/loadPlan.xhtml") {

            protected void applyRequestValues() throws Exception {

            }

            @Override
            protected void updateModelValues() throws Exception {

            }

            @Override
            protected void invokeApplication() {

                ListDataModel projects = (ListDataModel)getInstance("projectList");

                projects.setRowIndex(2);

                /*
                DataModel projects = (DataModel)Contexts.getSessionContext().get("projectList");

                assert projects != null;

                List<PlanProperties> pp = (List<PlanProperties>)projects.getWrappedData();

                System.out.println ("Plan naem: " + pp.get(0).getName());

                Contexts.getSessionContext().set("selection", pp.get(0));
                */


                // Contexts.getSessionContext().set("selection", pp.get(2)); // we load the ONB project

                invokeMethod("#{loadPlan.load}");

                Plan p = (Plan)getValue("#{selectedPlan}");

                assert p != null;

                System.out.println("Selected project: " + p.getPlanProperties().getName());

                invokeMethod("#{defineSampleRecords.enter}");
            }

            @Override
            protected void renderResponse() {

                System.out.println ("Number of objects: " + getValue("#{selectedPlan.projectBasis.numberOfObjects}"));
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
            public void updateModelValues() throws Exception {

            }

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

                System.out.println ("View ID to render: " +getViewId() );
                assert false == isValidationFailure();
            }

        }.run();

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

            @Override
            protected void renderResponse() throws Exception {

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


                // assert rowKeySet != null;

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

//                try{
                invokeMethod("#{runexperiments.proceed}");
/*                } catch(Exception e) {

                    String name = "nothing";


                    EJBException ex = (EJBException)e.getCause();
                    if (ex != null) {

                        EntityExistsException ex2 = (EntityExistsException)ex.getCause();

                        System.out.println ("EntityExistsException: " + ex2.getMessage());

                        ConstraintViolationException ex3 = (ConstraintViolationException)ex2.getCause();

                        System.out.println("Constrain name = " + ex3.getConstraintName());
                        System.out.println("SQL: " + ex3.getSQL());

                        name = ex3.getCause().getClass().getName();
                    }

                    //name = e.getClass().getName();
                    System.out.println("** CONSTRAINT VIOLATION EXCEPTION: " + name);

                    //System.out.println("constraint name: " + e.getConstraintName());

                    //System.out.println("SQL: " + e.getSQL());
                }*/

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
