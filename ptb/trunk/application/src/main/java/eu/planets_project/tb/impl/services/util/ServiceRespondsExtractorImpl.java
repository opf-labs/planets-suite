/**
 * 
 */
package eu.planets_project.tb.impl.services.util;


import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.planets_project.tb.api.services.util.ServiceRespondsExtractor;

/**
 * @author Andrew Lindley, ARC
 * This util class takes a XMLServiceResponds, a list of inputFileRefs and 
 * the XPathStatement on how to query the responds output. Additionally for base64 service
 * types or similar, where values instead of references are passed, it downloads/decodes them and
 * provides a local file reference for them.
 * Please note: This class works with localFile references.
 */
public class ServiceRespondsExtractorImpl implements ServiceRespondsExtractor{

	private String sXMLServiceResponds="";
	//Map<Position,Value>
	private Map<String,String> hmAllOutputs = new HashMap<String,String>();
	private String sXPathToOutput ="";
	//A Log for this - transient: it's not persisted with this entity
    private Log log = LogFactory.getLog(ServiceRespondsExtractorImpl.class);
	
    
	public ServiceRespondsExtractorImpl(String xmlServiceResponds, String XPathToOutput){
		this.sXMLServiceResponds = xmlServiceResponds;
		this.sXPathToOutput = XPathToOutput;
		parseRespondsAndBuildOutput();
	}
	

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRespondsExtractor#getAllOutputs()
	 */
	public Map<String,String> getAllOutputs(){
		return this.hmAllOutputs;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRespondsExtractor#getOutput(int)
	 */
	public String getOutput(int position){
		if(this.hmAllOutputs.keySet().contains(position+"")){
			return this.hmAllOutputs.get(position+"");
		}
		return null;
	}
	//extract output from xml and store to experiment executable
	
	
	/**
	 * Takes the XML Responds, applies the query and iterates over the output
	 * The result is stored within a Map<InputFile,Output> If could not be found it's null.
	 */
	private void parseRespondsAndBuildOutput(){

		String sQuery = this.sXPathToOutput;
		String xmlResponds = this.sXMLServiceResponds;

		try {
			//query the DOM with the entered XPath statement
			DocumentBuilderFactory factory =   DocumentBuilderFactory.newInstance();  
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Reader reader = new StringReader(xmlResponds);
			InputSource inputSource = new InputSource(reader);
			Document document = builder.parse(inputSource);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate(sQuery, document, XPathConstants.NODESET);
			
			//store the output data
			this.storeNodeListOutputs(nodes);
		}
		catch(Exception e){
			log.error("Exception applying the XPath to the DOM");
		}
	}
	
	/**
	 * Takes a given w3c.dom.NodeList, trys to extract its information according
	 * to fit into the expected output schema.
	 * i.e. expects the nodes to be element nodes and try to extract the information
	 * from their text-nodes.
	 * @param node
	 */
	private void storeNodeListOutputs(NodeList nodes){
		if(nodes!=null){
			for(int j=0;j<nodes.getLength();j++){
				Node item = nodes.item(j);
				if(item.getNodeType() == Node.ELEMENT_NODE){
					this.hmAllOutputs.put(j+"", item.getTextContent());
				}
			}
		}
	}
	
		
}
