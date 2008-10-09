/**
 * 
 */
package eu.planets_project.tb.impl.services.util;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.api.services.util.ServiceTemplateExporter;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;

/**
 * @author alindley
 *
 */
public class ServiceTemplateExporterImpl implements ServiceTemplateExporter {
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceTemplateExporter#getExportAsStream(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public ByteArrayOutputStream getExportAsStream(TestbedServiceTemplate template) throws ParserConfigurationException, TransformerException{
			Document xmlDoc = buildXMLExport(template);
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    trans.setOutputProperty(OutputKeys.INDENT, "yes");
		 
		    DOMSource source = new DOMSource(xmlDoc);
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    StreamResult result = new StreamResult(out);
		    trans.transform(source, result);
		    
		    return out;

	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceTemplateExporter#getExportAsString(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public String getExportAsString(TestbedServiceTemplate template) throws TransformerConfigurationException, ParserConfigurationException{
		Document xmlDoc = buildXMLExport(template);
		//set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(xmlDoc);
        try {
			trans.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String xmlString = sw.toString();
        return xmlString;

	}
	
	/**
	 * Contains the logical structure (compliant to the xml-schema) for serializing
	 * the template to an xml structure
	 * @param template
	 * @return
	 * @throws ParserConfigurationException
	 */
	private Document buildXMLExport(TestbedServiceTemplate template) throws ParserConfigurationException{
	
	    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	    Document doc = docBuilder.newDocument();

	    //build the XML tree
	    //create the root element and add it to the document
	    Element elServiceTemplate = doc.createElement("ServiceTemplate");
	    
	    //add the MD5 attribute
	    elServiceTemplate.setAttribute("serviceMD5id", template.getUUID());
	    
	    //add the TB versionID
	    TestbedManager manager = TestbedManagerImpl.getInstance(true);
	    elServiceTemplate.setAttribute("TBversionID", manager.getCurrentTBVersionNumber());
	    
	    //add the endpoint
	    Element elEndpoint = doc.createElement("endpoint");
	    elEndpoint.setTextContent(template.getEndpoint());
	    elServiceTemplate.appendChild(elEndpoint);
	    
	    //add the servicename
	    Element elSerName = doc.createElement("serviceName");
	    elSerName.setTextContent(template.getName());
	    elServiceTemplate.appendChild(elSerName);
	    
	    //add the serviceDescription
	    Element elSerDescr = doc.createElement("serviceDescription");
	    elSerDescr.setTextContent(template.getDescription());
	    elServiceTemplate.appendChild(elSerDescr);
	    
	    //add the serviceOperations and their child elements
	    for (ServiceOperation serviceOperation : template.getAllServiceOperations()) {
		   	Element elSerOp = doc.createElement("serviceOperation");
	    	
	    	//create and add the child elements
	    	Element elOpName = doc.createElement("operationName");
	    	elOpName.setTextContent(serviceOperation.getName());
	    	elSerOp.appendChild(elOpName);
	    	
	    	Element elOpDescr = doc.createElement("operationDescription");
	    	elOpDescr.setTextContent(serviceOperation.getDescription());
	    	elSerOp.appendChild(elOpDescr);
	    	
	    	Element elOpXMLRequtempl= doc.createElement("xmlRequestTemplate");
	    	elOpXMLRequtempl.setTextContent(serviceOperation.getXMLRequestTemplate());
	    	elSerOp.appendChild(elOpXMLRequtempl);
	    	
	    	Element elOpXPath= doc.createElement("xPathToOutput");
	    	elOpXPath.setTextContent(serviceOperation.getXPathToOutput());
	    	elSerOp.appendChild(elOpXPath);
	    	
	    	Element elOpMaxFiles =doc.createElement("getMaxAllowedNrOfInputFiles");
	    	elOpMaxFiles.setTextContent(serviceOperation.getMaxSupportedInputFiles()+"");
	    	elSerOp.appendChild(elOpMaxFiles);
	    	
	    	Element elOpMinFiles =doc.createElement("getMinRequiredNrOfInputFiles");
	    	elOpMinFiles.setTextContent(serviceOperation.getMinRequiredInputFiles()+"");
	    	elSerOp.appendChild(elOpMinFiles);
	    	
	    	Element elOpSerOpType =doc.createElement("serviceOperationType");
	    	elOpSerOpType.setTextContent(serviceOperation.getServiceOperationType());
	    	elSerOp.appendChild(elOpSerOpType);
	    	
	    	Element elOpSelOutputType =doc.createElement("selectedOutputType");
	    	elOpSelOutputType.setTextContent(serviceOperation.getOutputObjectType());
	    	elSerOp.appendChild(elOpSelOutputType);
	    	
	    	Element elOpCallByValue =doc.createElement("callByValue");
	    	elOpCallByValue.setTextContent(serviceOperation.isInputTypeCallByValue()+"");
	    	elSerOp.appendChild(elOpCallByValue);
	    	
	    	if(serviceOperation.isInputTypeCallByValue()){
	    		Element elOpFileType =doc.createElement("outputFileType");
	    		elOpFileType.setTextContent(serviceOperation.getOutputFileType());
	    		elSerOp.appendChild(elOpFileType);
	    	}
	    	
	    	elServiceTemplate.appendChild(elSerOp);
	    	
	    }
	    
	    //add the tags - if there are any
	    List<ServiceTag> lTags = template.getAllTags(); 
	    if(lTags.size()>0){
	    	Element elTags =doc.createElement("tags");
	   
	    	for(int j=0; j<lTags.size();j++){
	    		ServiceTag tag = lTags.get(j);
	    		Element elTag =doc.createElement("tag");
	    		elTag.setAttribute("name", tag.getName());
	    		Element elValue =doc.createElement("value");
	    		elValue.setTextContent(tag.getValue());
	    		elTag.appendChild(elValue);
	    	
	    		if(!tag.getDescription().equals("")){
	    			Element elDescr =doc.createElement("description");
	    			elDescr.setTextContent(tag.getDescription());
	    			elTag.appendChild(elDescr);
	    		}
	    		//append the tag to tags
	    		elTags.appendChild(elTag);
	    	}
	    
	    	//append the tags to the elServiceTemplate
	    	elServiceTemplate.appendChild(elTags);
	    }
	      
	    //now the extended evaluation properties
	    
	    //check if we are dealing with an evaluation template
	    boolean isEvaluationTestbedServiceTemplate = true;
	    EvaluationTestbedServiceTemplateImpl evalTemplate = null;
	    try {	    	
	    	evalTemplate = (EvaluationTestbedServiceTemplateImpl) template;	    	
	    }
	    catch (Exception e){
	    	isEvaluationTestbedServiceTemplate = false;
	    }
	        
	    if (isEvaluationTestbedServiceTemplate) {
	    	//this is an evaluation template
	    	Element elEvalProperties =doc.createElement("evalProperties");
	    	
	    	Element elXPathForBMGoalRootNodes = doc.createElement("XPathForBMGoalRootNodes");
	    	elXPathForBMGoalRootNodes.setTextContent(evalTemplate.getXPathForBMGoalRootNodes());
	    	elEvalProperties.appendChild(elXPathForBMGoalRootNodes);
	    	
	    	Element elXPathForBMGoalName = doc.createElement("XPathForBMGoalName");
	    	elXPathForBMGoalName.setTextContent(evalTemplate.getXPathForNameConfig());
	    	elEvalProperties.appendChild(elXPathForBMGoalName);
	    	
	    	Element elSrcXpath = doc.createElement("SrcXpath");
	    	elSrcXpath.setTextContent(evalTemplate.getXPathForSrcConfig());
	    	elEvalProperties.appendChild(elSrcXpath);
	    	
	    	Element elTarXpath = doc.createElement("TarXpath");
	    	elTarXpath.setTextContent(evalTemplate.getXPathForTarConfig());
	    	elEvalProperties.appendChild(elTarXpath);
	    	
	    	Element elMetricNode = doc.createElement("XPathForMetricNode");
	    	elMetricNode.setTextContent(evalTemplate.getXPathToMetricNodeConfig());
	    	elEvalProperties.appendChild(elMetricNode);
	    	
	    	Element elMetricName = doc.createElement("MetricName");
	    	elMetricName.setTextContent(evalTemplate.getXPathToMetricNameConfig());
	    	elEvalProperties.appendChild(elMetricName);
	    	
	    	Element elMetricResult = doc.createElement("MetricResult");
	    	elMetricResult.setTextContent(evalTemplate.getXPathToMetricResultConfig());
	    	elEvalProperties.appendChild(elMetricResult);
	    	
	    	Element elCompStatusSuccess = doc.createElement("CompStatusSuccess");
	    	elCompStatusSuccess.setTextContent(evalTemplate.sCompStatusSuccess);
	    	elEvalProperties.appendChild(elCompStatusSuccess);
	    	
	    	Element elCompStatusXpath = doc.createElement("CompStatusXpath");
	    	elCompStatusXpath.setTextContent(evalTemplate.getXPathToCompStatus());
	    	elEvalProperties.appendChild(elCompStatusXpath);
	    	
	    	//add mappings
	    	Collection<String> mappedIDs = evalTemplate.getAllMappedBenchmarkGoalIDs();
	    	Element elMappings = doc.createElement("mappings");
	    	Iterator<String> iMappedIDs = mappedIDs.iterator();
	    	BenchmarkGoalsHandlerImpl bgHandler = BenchmarkGoalsHandlerImpl.getInstance();
	    	while (iMappedIDs.hasNext()) {
	    		String mappedID = iMappedIDs.next();
	    		
	    		Element elMapping = doc.createElement("mapping");
	    		
	    		Element elBMGoalName = doc.createElement("BMGoalName");   		
	    		elBMGoalName.setTextContent(bgHandler.getBenchmarkGoal(mappedID).getName());
	    		elMapping.appendChild(elBMGoalName);
	    		
	    		Element elBMGoalID = doc.createElement("BMGoalID");
	    		elBMGoalID.setTextContent(mappedID);
	    		elMapping.appendChild(elBMGoalID);
	    		
	    		Element elPropertyName = doc.createElement("PropertyName");	
	    		elPropertyName.setTextContent(evalTemplate.getMappedPropertyName(mappedID));
	    		elMapping.appendChild(elPropertyName);
	    		
	    		elMappings.appendChild(elMapping);
	    	}	
	    	elEvalProperties.appendChild(elMappings);
	    	elServiceTemplate.appendChild(elEvalProperties);
	    }
	    doc.appendChild(elServiceTemplate);
	    return doc;
	}


}
