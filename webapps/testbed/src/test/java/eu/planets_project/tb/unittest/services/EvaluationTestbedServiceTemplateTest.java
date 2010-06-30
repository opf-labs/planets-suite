/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.unittest.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;

public class EvaluationTestbedServiceTemplateTest extends TestCase{

	private Document document;
	EvaluationTestbedServiceTemplateImpl evalSer;
	
	public void setUp(){
		DocumentBuilderFactory factory =   DocumentBuilderFactory.newInstance();  
		factory.setNamespaceAware(false);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Reader reader = new StringReader(xmlResponds);
			InputSource inputSource = new InputSource(reader);
			document = builder.parse(inputSource);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		evalSer = new EvaluationTestbedServiceTemplateImpl();
		evalSer.setXPathForBMGoalRootNodes("/*//property");
		evalSer.setXPathForName("@name");
		evalSer.setXPathToCompStatus("@compStatus");
		evalSer.setXPathForSrcValue("./values/src");
		evalSer.setXPathForTarValue("./values/tar");
		evalSer.setXPathToMetricNode("./metric");
		evalSer.setXPathToMetricName("@name");
		evalSer.setXPathToMetricValue("@result");
	}
	
	public void testXPathToRootNodes(){
		try {
			NodeList nodes = evalSer.getAllEvalResultsRootNodes(document);
			assertEquals(7, nodes.getLength());	
		} catch (XPathExpressionException e) {
		}
	}
	
	public void testXPathToRootNodesWrongXPath(){
		try {
			evalSer.setXPathForBMGoalRootNodes("/*//xyz");
			evalSer.getAllEvalResultsRootNodes(document);
		} catch (XPathExpressionException e) {
			assertFalse(false);
		}
	}
	
	public void testXPathToName(){
		try {
			NodeList nodes = evalSer.getAllEvalResultsRootNodes(document);
			List<String> names = new ArrayList<String>();
			
			if((nodes!=null)&&(nodes.getLength()>0)){
				for(int i=0;i<nodes.getLength();i++){
					Node node = nodes.item(i);
					String s = evalSer.getEvalResultName(node);
					names.add(s);

				}
				assertEquals(true, names.contains("imageHeight"));
				assertEquals(true, names.contains("imageType"));
				assertEquals(7, names.size());	
			}
		} catch (XPathExpressionException e) {
		}
	}
	
	public void testXPathToCompStatus(){
		try {
			NodeList nodes = evalSer.getAllEvalResultsRootNodes(document);
			List<String> names = new ArrayList<String>();
			
			if((nodes!=null)&&(nodes.getLength()>0)){
				for(int i=0;i<nodes.getLength();i++){
					Node node = nodes.item(i);
					String s = evalSer.getEvalResultCompStatus(node);
					names.add(s);

				}
				assertEquals(true, names.contains("complete"));
				assertEquals(true, names.contains("failed"));
				assertEquals(7, names.size());	
			}
		} catch (XPathExpressionException e) {
		}
	}
	
	public void testXPathToSrcAndTarValue(){
		try {
			NodeList nodes = evalSer.getAllEvalResultsRootNodes(document);
			List<String> names = new ArrayList<String>();
			List<String> names2 = new ArrayList<String>();
			
			if((nodes!=null)&&(nodes.getLength()>0)){
				for(int i=0;i<nodes.getLength();i++){
					Node node = nodes.item(i);
					
					//Please note: if not found, s = "";
					String s = evalSer.getEvalResultSrcValue(node);
					String s2 = evalSer.getEvalResultTarValue(node);
					if(!s.equals(""))
							names.add(s);
					if(!s2.equals(""))
						names2.add(s2);

				}
				assertEquals(true, names.contains("32"));
				assertEquals(true, names2.contains("32"));
				assertEquals(3, names.size());	
				assertEquals(3, names2.size());	
			}
		} catch (XPathExpressionException e) {
		}
	}
	
	public void testXPathToMetricNameAndValue(){
		try {
			NodeList nodes = evalSer.getAllEvalResultsRootNodes(document);
			
			boolean bFound = false;
			
			if((nodes!=null)&&(nodes.getLength()>0)){
				for(int i=0;i<nodes.getLength();i++){
					Node node = nodes.item(i);
					
					//Please note: if not found, s = "";
					Map<String,String> r = evalSer.getEvalResultMetricNamesAndValues(node);
					if(r.containsKey("hammingDistance")){
						bFound = true;
						assertEquals(r.get("hammingDistance"),"0");
					}
				}
				if(!bFound)
					assertEquals(true,false);
			}
		} catch (XPathExpressionException e) {
		}
	}
	
	
	
	protected void tearDown(){
	}
	
	private String xmlResponds = "<cpResponse>"+
"<compSet id=\"1\" source=\"XCDL145215.tmp\" target=\"XCDL245216.tmp\">"+
	"<property id=\"2\" name=\"imageHeight\" unit=\"pixel\" compStatus=\"complete\">"+
		"<values type=\"int\">"+
			"<src>32</src>"+
			"<tar>32</tar>"+
		"</values>"+
		"<metric id=\"200\" name=\"equal\" result=\"true\"/>"+
		"<metric id=\"201\" name=\"intDiff\" result=\"0\"/>"+
		"<metric id=\"210\" name=\"percDev\" result=\"0.000000\"/>"+
	"</property>"+
	"<property id=\"30\" name=\"imageWidth\" unit=\"pixel\" compStatus=\"complete\">"+
		"<values type=\"int\">"+
			"<src>32</src>"+
			"<tar>32</tar>"+
		"</values>"+
		"<metric id=\"200\" name=\"equal\" result=\"true\"/>"+
		"<metric id=\"201\" name=\"intDiff\" result=\"0\"/>"+
		"<metric id=\"210\" name=\"percDev\" result=\"0.000000\"/>"+
	"</property>"+
	"<property id=\"300\" name=\"normData\" compStatus=\"complete\">"+
		"<values type=\"int\">"+
			"<src>00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa </src>"+
			"<tar>00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55  aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff  00 00 00 00 55 55 55 55 ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00  55 55 55 55 aa aa aa aa ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa  ff ff ff ff 00 00 00 00 55 55 55 55 aa aa aa aa </tar>"+
		"</values>"+
		"<metric id=\"10\" name=\"hammingDistance\" result=\"0\"/>"+
		"<metric id=\"50\" name=\"RMSE\" result=\"0.000000\"/>"+
	"</property>"+
	"<property id=\"18\" name=\"compression\" compStatus=\"failed\"/>"+
	"<property id=\"20\" name=\"imageType\" compStatus=\"failed\"/>"+
	"<property id=\"11\" name=\"bitDepth\" compStatus=\"failed\"/>"+
	"<property id=\"12\" name=\"resolutionUnit\" compStatus=\"failed\"/>"+
"</compSet>"+
"</cpResponse>";



}
