package eu.planets_project.tb.unittest.services;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.TestbedServiceTemplateImpl;
import junit.framework.TestCase;

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
		evalSer.setXPathToMetricName("./metric/@name");
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
			NodeList nodes = evalSer.getAllEvalResultsRootNodes(document);
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
			List<String> names = new ArrayList<String>();
			List<String> names2 = new ArrayList<String>();
			
			if((nodes!=null)&&(nodes.getLength()>0)){
				for(int i=0;i<nodes.getLength();i++){
					Node node = nodes.item(i);
					
					//Please note: if not found, s = "";
					String s = evalSer.getEvalResultMetricName(node);
					String s2 = evalSer.getEvalResultMetricValue(node);
					if(!s.equals(""))
							names.add(s);
					if(!s2.equals(""))
						names2.add(s2);

				}
				assertEquals(true, names.contains("hammingDistance"));
				assertEquals(true, names2.contains("resolutionUnit"));
				assertEquals(7, names.size());	
				assertEquals(7, names2.size());	
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
