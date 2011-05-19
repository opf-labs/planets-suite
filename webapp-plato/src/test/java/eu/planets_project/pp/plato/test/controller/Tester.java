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

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.testng.annotations.Test;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Decision;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.Decision.GoDecision;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.TreeLoader;


/**
 * First trial of the TestNG framework that is embraced within Seam.
 * <b>We have to use this framework throughout the system.</b>
 * Tests and assertions need to be done a lot more thoroughly
 * with the 'real' tests.
 * @author Christoph Becker
 */
public class Tester {

    private static final Log log = PlatoLogger.getLogger(Tester.class);

    private EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Test
    public void testFoo2() {

    }

    @Test
    public void testBar() {
        assert true;
    }

    @Test
    public void testImportXML()  {
        ObjectiveTree t = new TreeLoader().load("data/trees/pdfa.xml");
        TreeNode n = t.getRoot();
        assert "PDF/A".equals(n.getName());
        assert n instanceof Node;
        Node node = (Node) n;
        assert node.getChildren().size() == 4;
        assert node.getChildren().get(1).getWeight()==0.15;
    }

    @Test
    public void testProjectPersistence() {
        ObjectiveTree t = new TreeLoader().load("data/trees/pdfa.xml");
        Plan p = new Plan();
        p.getPlanProperties().setName("Plato-Testproject number");
        p.getPlanProperties().setAuthor("Christoph Becker");

        Decision d = new Decision();
        d.setDecision(GoDecision.GO);
        d.setActionNeeded("nothing");
        d.setReason("This is just SOO great, oh man!");

        p.setDecision(d);
        p.getPlanProperties().setDescription("This is the best and best and even better test project one can imagine.");
        p.getProjectBasis().setDocumentTypes("This is a test documentation string.");
        p.getPlanProperties().setOrganization("ICW - Icebears for Climate warming");
        p.getSampleRecordsDefinition().setSamplesDescription("yes yes i know");
        p.getState().setValue(PlanState.ANALYSED);
        p.setTree(t);
        Alternative alt = new Alternative();
        alt.setName("Alternative Name");
        p.getAlternativesDefinition().getAlternatives().add(alt);
        p.getAlternativesDefinition().getAlternatives().add(alt);

        em.persist(p);

        int i = p.getId();

    }
}
