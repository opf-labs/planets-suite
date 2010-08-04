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
package eu.planets_project.pp.plato.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class XMLCompare {
    
    private StringBuffer errorMessages = new StringBuffer();
    
    private List<String> excludedNodes;
    
    public static void main (String[] args) {
        
        List<String> nodesToExclude = new ArrayList<String>();
        nodesToExclude.add("//*[name()='jhoveXML']");

        XMLCompare xmlCompare = new XMLCompare();
        xmlCompare.setExcludedNodes(nodesToExclude);
        boolean equal = xmlCompare.compareXml(args[0], args[1], true, true);
        
        System.out.println (equal? "The documents are equal" : xmlCompare.getErrorMessage());
    }
    
    public XMLCompare() {
    }

    /**
     * @param exclude set of XPath expressions, matching nodes will be detached.
     */
    public void setExcludedNodes(List<String> exclude) {
        excludedNodes = exclude;
    }
    
    public boolean compareXml(File file1, File file2, boolean ignoreEmptyNodes, boolean ignoreEmptyAttributes) {
        errorMessages.setLength(0);
        errorMessages.append("File #1: ").append(file1.getAbsolutePath()).append(System.getProperty("line.separator"));
        errorMessages.append("File #2: ").append(file2.getAbsolutePath()).append(System.getProperty("line.separator"));
        
        SAXReader reader = new SAXReader();
        
        try {
            Document xml1Doc = reader.read(file1);
            Document xml2Doc = reader.read(file2);
            
            excludeNodes(xml1Doc);
            excludeNodes(xml2Doc);

            Element root1 = xml1Doc.getRootElement();
            Element root2 = xml2Doc.getRootElement();
            
            boolean equal = compareTrees(root1, xml2Doc, ignoreEmptyNodes, ignoreEmptyAttributes);
            
            equal = equal && compareTrees(root2, xml1Doc, ignoreEmptyNodes, ignoreEmptyAttributes);

            return equal;

        } catch (DocumentException e1) {
            e1.printStackTrace();
        }

        return false;
    }
    
    /**
     * 
     * @param file1
     * @param file2
     * @param ignoreEmptyNodes
     * @param ignoreEmptyAttributes
     * @return
     */
    public boolean compareXml(String file1, String file2, boolean ignoreEmptyNodes, boolean ignoreEmptyAttributes) {
        
        File f1 = new File(file1);
        File f2 = new File(file2);
        
        return compareXml(f1, f2, ignoreEmptyNodes, ignoreEmptyAttributes);
    }    
    
    private void excludeNodes(Document doc) {

        Element root = doc.getRootElement();
        
        for (String xpathString : excludedNodes) {

            XPath xpath = doc.createXPath(xpathString);
    
            List<Node> l = xpath.selectNodes(root);

            for (Node n : l) {
                n.detach();
            }
        }
        
    }
    
    private boolean compareTrees(Element node1, Document doc2, boolean ignoreEmptyNodes, boolean ignoreEmptyAttributes) {
        boolean isValid = true;

        Element root2 = doc2.getRootElement();
        
        String path = node1.getUniquePath();
        
        Element elementDoc2 = (Element) doc2.selectSingleNode(path);

        // the node doesn't exist in doc2
        if (elementDoc2 == null && node1.elements().size() == 0 && "".equals(node1.getTextTrim())) {
            if (ignoreEmptyNodes == false) {
                errorMessages.append("The following empty node doesnt exist in xml file #2: " + node1.getUniquePath()).append(System.getProperty("line.separator"));
                isValid= false;
            }
        } else if (elementDoc2 == null && (node1.elements().size() > 0 || !"".equals(node1.getTextTrim()))) {
            errorMessages.append("The following node (which is not empty) doesn't exist in xml file #2: " + node1.getUniquePath() + System.getProperty("line.separator"));
            errorMessages.append("File #1: " + node1.getTextTrim() + System.getProperty("line.separator"));
            errorMessages.append("File #2: " + ((elementDoc2 == null)? "<not existent>" : elementDoc2.getTextTrim()) + System.getProperty("line.separator"));

            isValid= false;
        } else if (elementDoc2 == null || !elementDoc2.getTextTrim().equals(node1.getTextTrim())) {
            errorMessages.append("Text of following nodes not equal in both xmls: " + node1.getUniquePath() + System.getProperty("line.separator"));
            errorMessages.append("File #1: " + node1.getTextTrim() + System.getProperty("line.separator"));
            errorMessages.append("File #2: " + ((elementDoc2 == null)? "<not existent>" : elementDoc2.getTextTrim()) + System.getProperty("line.separator"));

            isValid= false;
        } 

        //
        // compare attributes
        //
        List<Attribute> attributes = node1.attributes();

        for (Attribute a : attributes) {

            String attributePath = a.getUniquePath();

            org.dom4j.Node node = root2.selectSingleNode(attributePath);

            if (node == null && "".equals(a.getValue().trim())) {
                // document 1 has an empty attribute (sting length = 0) which
                // does not exist in document 2

                if (ignoreEmptyAttributes == false) {
                    errorMessages.append("Attribute " + a.getName() + " doesn't exist in xml file #2, node " + a.getUniquePath()).append(System.getProperty("line.separator"));
                    isValid = false;
                }
                
                continue;
            }

            if (node == null || !a.getValue().equals(node.getText())) {
                
                errorMessages.append("Value of following attribute not equal in both xmls: " + a.getUniquePath() + System.getProperty("line.separator"));
                errorMessages.append("File #1: " + a.getValue() + System.getProperty("line.separator"));
                errorMessages.append("File #2: " + ((node != null) ? node.getText() : "<not existent>") + System.getProperty("line.separator")); 

                isValid = false;
            }
        }

        List<Element> elements = node1.elements();

        for (Element e : elements) {
            if (!compareTrees(e, doc2, ignoreEmptyNodes, ignoreEmptyAttributes)) {
                isValid = false;
            }
        }

        return isValid;
    }
    
    public String getErrorMessage() {
        return errorMessages.toString();
    }
}
