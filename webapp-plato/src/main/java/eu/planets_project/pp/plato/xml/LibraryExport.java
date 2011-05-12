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
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.commons.digester.Digester;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.LibraryRequirement;
import eu.planets_project.pp.plato.model.tree.LibraryTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.util.MeasurementInfoUri;
import eu.planets_project.pp.plato.xml.plato.CriterionCategoryFactory;

public class LibraryExport implements Serializable {

    private static final long serialVersionUID = -8034062583889766942L;
    
    public static OutputFormat prettyFormat = new OutputFormat(" ", true,"ISO-8859-1"); //OutputFormat.createPrettyPrint();
    public static OutputFormat compactFormat = new OutputFormat(null, false,"ISO-8859-1"); //OutputFormat.createPrettyPrint();

    public static Namespace xsi;
    public static Namespace platoLibNS;
    
    private LibraryTree lib;
    
    static {
        xsi = new Namespace("xsi",  "http://www.w3.org/2001/XMLSchema-instance");
        platoLibNS = new Namespace("", "http://www.planets-project.eu/plato/library");
    }
    
    
    public void exportToStream(LibraryTree lib, OutputStream out) throws IOException{
        XMLWriter writer = new XMLWriter(out, prettyFormat);
        writer.write(exportToDocument(lib));
        writer.close();
    }
    
    public Document exportToDocument(LibraryTree lib) {
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement("library");
        
        root.add(xsi);
        root.add(platoLibNS);
//        root.addAttribute(xsi.getPrefix()+":schemaLocation", "http://www.planets-project.eu/plato plato-2.1.xsd");
//        root.addAttribute(xsi.getPrefix()+":noNamespaceSchemaLocation", "http://www.ifs.tuwien.ac.at/dp/plato/schemas/plato-2.1.xsd");
        root.addAttribute("name", lib.getName());
        
        Element rootReq = root.addElement(new QName("requirement", platoLibNS));
        addRequirementProperties(rootReq, lib.getRoot());

        return doc;
        
    }
    private void addRequirementProperties(Element node, LibraryRequirement req) {
        addNodeAttributes(node, req);

        LibraryRequirement r = (LibraryRequirement) req;
        if (r.isPredefined()) {
            node.addAttribute("predefined", ""+r.isPredefined());
        }
        if (r.getCategory() != null) {
            node.addElement("category")
            .addAttribute("name", r.getCategory().name());
        }
        
        Element children = node.addElement("refinedBy");
        for(TreeNode n : r.getChildren()) {
            addTreeFragment(children, n);
        }
    }
    private void addTreeFragment(Element parent, TreeNode node) {
        
        if (node instanceof LibraryRequirement) {
            Element req = parent.addElement("requirement");
            addRequirementProperties(req, (LibraryRequirement)node);
        } else {
            Leaf l = (Leaf) node;
            Element leaf = parent.addElement("criterion");
            addNodeAttributes(leaf, l);
            
            if (l.getMeasurementInfo().getUri() != null) {
                Element meas = leaf.addElement("measurementInfo");
                meas.addAttribute("uri", l.getMeasurementInfo().getUri());
            }
        }
        
    }
    
    private void addNodeAttributes(Element element, TreeNode node) {
        element.addAttribute("name", node.getName());
        addStringElement(element, "description", node.getDescription());
    }
    
    /**
     * TODO: move this to a dom4j utility library, it is copied from
     * {@link ProjectExporter}
     * 
     * Long strings are stored as XML-elements, not as attributes.
     * It is not possible to add an element with value <code>null</code>,
     * therefore this has to be handled here:
     * A new element is only added if there is a value at all.
     *
     * @param parent
     * @param name
     * @param value
     */
    private  Element addStringElement(Element parent, String name, String value){
        Element e = null;
        // &&(!"".equals(value)
        if (value != null) {
           e = parent.addElement(name);
           if (!"".equals(value)) {
               e.addText(value);
           }
        }
        return e;
    }
    public LibraryTree getLib() {
        return lib;
    }

    public void setLib(LibraryTree lib) {
        this.lib = lib;
    }
    
    public LibraryTree importFromStream(InputStream in)  throws IOException, SAXException {
        lib = new LibraryTree();
        Digester d = new Digester();
        
        d.push(lib);
        d.addSetProperties("library");

        d.addObjectCreate("library/requirement", LibraryRequirement.class);
        d.addSetProperties("library/requirement");
        d.addSetNext("library/requirement", "setRoot");

        // some general rules
        d.addCallMethod("*/description", "setDescription", 0);

        // create category according to its name
        d.addFactoryCreate("*/category", CriterionCategoryFactory.class);
        d.addSetNext("*/category", "setCategory");
        
        // create requirements below root node
        d.addObjectCreate("*/refinedBy/requirement", LibraryRequirement.class);
        d.addSetProperties("*/refinedBy/requirement");
        d.addSetNext("*/refinedBy/requirement", "addChild");

        // create criteria
        d.addObjectCreate("*/criterion", Leaf.class);
        d.addSetProperties("*/criterion");
        d.addSetNext("*/criterion", "addChild");
        
        // and measurement infos if existent
        d.addObjectCreate("*/measurementInfo",  MeasurementInfoUri.class);
        d.addSetProperties("*/measurementInfo", "uri", "asURI");
        d.addSetNext("*/measurementInfo", "setMeasurementInfo");
        
        d.parse(in);
        
        return lib;
    }

}
