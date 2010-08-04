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

package eu.planets_project.pp.plato.services.characterisation.jhove;

import java.io.StringReader;
import java.util.Vector;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.NodeCreateRule;
import org.apache.commons.logging.Log;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.services.characterisation.jhove.tree.JHoveTree;
import eu.planets_project.pp.plato.services.characterisation.jhove.tree.JHoveTreeNode;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * JHove adaptor, produces a tree renderable with the rich:tree module.
 * 
 */
public class JHoveAdaptor {

    private static final Log log = PlatoLogger.getLogger(JHoveAdaptor.class);

    private JHove jHove = new JHove();

    public String describe(DigitalObject object) {
        try {
                if (object.isDataExistent()) {
                    return jHove.getJHoveInfoAsString(object.getId()
                            + System.nanoTime() + "", object.getData().getData());
                } else {
                    return "";
                }
        } catch (Exception ex) {
            log.error("Cannot initialize jhove XML String"+ex.getMessage(),ex);
            return null;
        }
    }

    /**
     * Returns a JHoveTreeFrom the string: writes as root node name the sampleName String
     * 
     * @param xmlString
     * @return
     */
    public JHoveTree digestString(String sampleName, String xmlString) {
        return initFromJHoveProperty(sampleName, jHove.digestString(xmlString));
    }

    /**
     * Creates a {@link JHoveTree} from a {@link JHoveFileProperty} file
     * 
     * @param prop
     * @return
     */
    private JHoveTree initFromJHoveProperty(String sampleName,
            JHoveFileProperty prop) {
        if (prop == null)
            return null;
        JHoveTree mTree = new JHoveTree();
        mTree.initRoot(sampleName);
        if (prop.getExtractionDate() != null)
            mTree.getRoot().addChild(
                    new JHoveTreeNode("Extraction date: "
                            + prop.getExtractionDate(), "leaf"));
        if (prop.getStatus().compareTo("Not well-formed") == 0) {
            mTree.getRoot().addChild(
                    new JHoveTreeNode("Status: " + prop.getStatus(), "leaf"));
            return mTree;
        }
        mTree.getRoot().addChild(
                new JHoveTreeNode("Size: " + prop.getFileSize(), "leaf"));
        mTree.getRoot().addChild(
                new JHoveTreeNode("Mime-Type: " + prop.getMimetype(), "leaf"));
        mTree.getRoot().addChild(
                new JHoveTreeNode("Format: " + prop.getFormat(), "leaf"));
        if (prop.getVersion() != null)
            mTree.getRoot().addChild(
                    new JHoveTreeNode("Version: " + prop.getVersion(), "leaf"));
        mTree.getRoot().addChild(
                new JHoveTreeNode("Status: " + prop.getStatus(), "leaf"));

        // if not a bytestream, display also the properties
        if (prop.getFormat().compareTo("bytestream") != 0) {
            // Add Module to the tree
            JHoveTreeNode tmpMaterialTreeNode = new JHoveTreeNode("Module",
                    "node");
            tmpMaterialTreeNode.addChild(new JHoveTreeNode("Name: "
                    + prop.getModule().getName(), "leaf"));
            tmpMaterialTreeNode.addChild(new JHoveTreeNode("Release: "
                    + prop.getModule().getRelease(), "leaf"));
            tmpMaterialTreeNode.addChild(new JHoveTreeNode("Date: "
                    + prop.getModule().getDate(), "leaf"));
            mTree.getRoot().addChild(tmpMaterialTreeNode);

            // Add Profiles
            tmpMaterialTreeNode = new JHoveTreeNode("Profiles", "node");
            Vector<String> profiles = prop.getProfiles();
            if (profiles != null) {
                for (String profile : profiles) {
                    tmpMaterialTreeNode.addChild(new JHoveTreeNode(profile,
                            "leaf"));
                }
            }

            mTree.getRoot().addChild(tmpMaterialTreeNode);

            JHoveTreeNode tmpProp = new JHoveTreeNode("Properties", "node");

            // Add Properties
            for (Property property : prop.getProperties()) {
                tmpProp.addChild(getPropertyTreeNode(property));
            }
            mTree.getRoot().addChild(tmpProp);

        }
        log.debug("JHoveTree created from Samplerecord " + sampleName);
        return mTree;
    }

    /**
     * Recursive function that returns all subproperties of a
     * {@link jHoveProperty} as children of a {@link JHoveTreeNode}. If a
     * Property child is an XMP property, formats the XMP String file in a
     * branch tree, containing the XML structure.
     * 
     * @param property
     * @return
     */
    private JHoveTreeNode getPropertyTreeNode(Property property) {
        if (property.getName().compareTo("") == 0)
            return null;

        JHoveTreeNode tmp = new JHoveTreeNode(property.getName(), "node");
        // can use the type for setting the different node
        for (Object tmpObject : property.getValues()) {
            if (((Property) tmpObject).getType().compareTo("Property") == 0) {
                tmp.addChild(getPropertyTreeNode((Property) tmpObject));
            } else {
                if (((Property) tmpObject).getName().compareTo("XMP") == 0)
                    tmp.addChild(getJHoveTreeNodeFromXML(((Property) tmpObject)
                            .getValues().toString(), "XMP Charachteristics"));
                else
                    tmp
                            .addChild(new JHoveTreeNode(tmpObject.toString(),
                                    "leaf"));
            }
        }
        return tmp;
    }

    /**
     * Process any kind of XML String and returns a {@link JHoveTreeNode}
     * this seemingly absurd method stems from the fact that jhove might include ENCODED xml in a response,
     * so that within the xml tag we have another encoded, embedded, XML string.
     * For example, XMP info is embedded in that way.
     * (see data/testfiles-characterisation/jhove-output-with-xmpinfo.xml and sample-pdf-including-xmp.pdf)
     * So we create another xml root node, copy the encoded stuff in and send it to an XML extraction method.
     * 
     * @param XMPasString
     * @param name
     * @return
     * @see #getJHoveTreeNodeFromXPathNode(Node)
     */
    private JHoveTreeNode getJHoveTreeNodeFromXML(String XMPasString,
            String name) {
        try {
            //tested with digester: XML displayed as a jhoveTreeNode 
            XMPasString = XMPasString.substring(1, XMPasString.length() - 1);
            
            Digester digester=new Digester();
            digester.addRule("XMPInfo", new NodeCreateRule());
            
            Node rootNode = (Node)digester.parse(
                    new StringReader("<XMPInfo>" + XMPasString + "</XMPInfo>"));
            
            return getJHoveTreeNodeFromXPathNode(rootNode);
        } catch (Exception e) {
            log.warn("Error in JHove identification: " + e.getMessage(), e);
            return new JHoveTreeNode("Error in Processing XMP", "leaf");
        }
    }

    /**
     * Creates the complete {@link JHoveTreeNode} from the given {@link Node}.
     * To display the informations property they have to be stored in a tree: the {@link JHoveTreeNode}
     * in this case
     * 
     * @param node
     * @return
     */
    private JHoveTreeNode getJHoveTreeNodeFromXPathNode(Node node) {
        JHoveTreeNode tmpNode;
        if (node.hasChildNodes()) {
            tmpNode = new JHoveTreeNode(node.getNodeName(), "node");
            NodeList nodelist = node.getChildNodes();
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                tmpNode
                        .addChild(getJHoveTreeNodeFromXPathNode(nodelist
                                .item(i)));
            }
        } else {
            String tmpNodeContext = node.getNodeValue();
            
            // node value can be null. if it is null we change it to an empty string
            if (tmpNodeContext == null) {
                tmpNodeContext = "";
            }
            tmpNodeContext = tmpNodeContext.replaceAll("\n", "");
            tmpNodeContext = tmpNodeContext.replaceAll(" ", "");
            tmpNode = new JHoveTreeNode(tmpNodeContext, "leaf");
        }
        return tmpNode;
    }

    //Change required, because the data of the uploaded are now saved in a tmpDirectory
    public String describe(String dataTmpFile) {
        return jHove.getJHoveInfoAsString(dataTmpFile);
    }

}
