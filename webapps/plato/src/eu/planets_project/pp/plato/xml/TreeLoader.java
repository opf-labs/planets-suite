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

package eu.planets_project.pp.plato.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.model.tree.PolicyTree;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.freemind.MindMap;

/**
 * This class uses the Jakarta Commons Digester to load an objective tree from an xml file.
 * At the moment we support the DELOS Testbed file format and FreeMind mindmaps.
 */
public class TreeLoader {
    private Log log = PlatoLogger.getLogger(this.getClass());

    public TreeLoader() {

    }

    /**
     *
     * @param file absolute file path to proper xml file conforming to
     * the old eu.planets_project.pp.plato.xml.legacy format used in the DELOS Testbed.
     * Note that the xml file structure is slightly adapted from the DELOS testbed, was easier
     * than getting the digester to work properly on the old one. Just the root is changed!
     * samples can be found in data/trees.
     * @return {@link ObjectiveTree} created from the xml file,
     * or <code>null</code> if there was an error
     */
    public ObjectiveTree load(String file) {
        ObjectiveTree tree = new ObjectiveTree();

        Digester digester = new Digester();
        digester.setValidating(false);
        digester.push(tree);

        digester.addObjectCreate("*/root",
        "eu.planets_project.pp.plato.model.Node");
        digester.addSetProperties("*/root");

        digester.addObjectCreate("*/node", "eu.planets_project.pp.plato.model.Node");
        digester.addSetProperties("*/node");

        digester.addObjectCreate("*/leaf","eu.planets_project.pp.plato.model.Leaf");
        digester.addSetProperties("*/leaf");

        digester.addSetNext("*/leaf","addChild");
        digester.addSetNext("*/node","addChild");
        digester.addSetNext("*/root","setRoot");

        try {
            InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            digester.setUseContextClassLoader(true);
            digester.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
            tree = null;
        }

        return tree;
    }

    /**
     * This method imports a FreeMind MindMap defined in an XML file into the
     * objective tree structure defined by Plato.
     * @param in  an InputStream which contains a FreeMind XML mindmap
     * @return {@link ObjectiveTree} created from the xml file,
     * or <code>null</code> if there was an error.
     * @param hasUnits If this is set to <code>true</code>, we assume
     * that every leaf node is a measurement unit, and the objective tree
     * leaves are one level higher. So we stop one level earlier,
     * the units are not imported at the moment.
     */
    public ObjectiveTree loadFreeMindStream(InputStream in, boolean hasUnits, boolean hasLeaves) {
        MindMap map = new MindMap();

        // load content into temporary structure
        Digester digester = new Digester();
        digester.setSchema("/data/schemas/freemind.xsd");
        digester.setValidating(true);
        digester.push(map);

        digester.addObjectCreate("*/node",
        "eu.planets_project.pp.plato.xml.freemind.Node");
        digester.addSetProperties("*/node");
        digester.addCallMethod("*/node/hook/text","setDESCRIPTION",0);
        digester.addSetNext("*/node","addChild");

        try {
            //InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            digester.setUseContextClassLoader(true);
            digester.parse(in);
        } catch (IOException e) {
            log.error("Error loading Freemind file. Cause: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            log.error("Document is not a valid Freemind file. Cause: " + e.getMessage());
            return null;
        }

        // traverse temp structure of map and nodes and create ObjectiveTree
        ObjectiveTree tree = new ObjectiveTree();
        tree.setRoot(map.getObjectiveTreeRoot(hasUnits, hasLeaves));
        if (tree.getRoot().isLeaf()) {
            return null;
        }
        return tree;
    }

    public PolicyTree loadFreeMindPolicyTree(InputStream in) {
        MindMap map = new MindMap();

        // load content into temporary structure
        Digester digester = new Digester();
        digester.setSchema("/data/schemas/freemind.xsd");
        digester.setValidating(true);
        digester.push(map);

        digester.addObjectCreate("*/node",
        "eu.planets_project.pp.plato.xml.freemind.Node");
        digester.addSetProperties("*/node");
        digester.addSetNext("*/node","addChild");

        try {
            //InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            digester.setUseContextClassLoader(true);
            digester.parse(in);
        } catch (IOException e) {
            log.error("Error loading Freemind file. Cause: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            log.error("Document is not a valid Freemind file. Cause: " + e.getMessage());
            return null;
        }

        PolicyTree tree = new PolicyTree();
        tree.setRoot(map.getPolicyTreeRoot());
        return tree;
    }


    public ObjectiveTree loadFreeMind(String file, boolean hasUnits, boolean hasLeaves){
        return loadFreeMindStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(file), hasUnits, hasLeaves);
    }

    public void test(String file) {
        ObjectiveTree t = load(file);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new TreeLoader().test(args[0]);
    }
}
