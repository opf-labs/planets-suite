package eu.planets_project.ifr.core.wee.api.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;


public class WorkflowConfigUtil {
	
	private static Log log = LogFactory.getLog(WorkflowConfigUtil.class);
	
	/**
	 * Uses the JAXB to provide a java api for the xml config marshalling/unmarshalling
	 * @return
	 * @throws JAXBException 
	 */
	public static WorkflowConf unmarshalWorkflowConfig(String xmlWFConfig) throws JAXBException{

			JAXBContext context;
			try {
				context = JAXBContext.newInstance(WorkflowConf.class);
				Unmarshaller um = context.createUnmarshaller(); 
				WorkflowConf wfc = (WorkflowConf) um.unmarshal(new StringReader(xmlWFConfig)); 
				return wfc;
			} catch (JAXBException e) {
				log.error("unmarshalWorkflowConfig failed",e);
				throw e;
			} 


	}
	
	private void checkValidXMLConfig(InputStream xmlWFConfig) throws Exception{
		InputStream bis = getClass().getClassLoader().getResourceAsStream(
				"planets_wdt.xsd");
		try {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(bis));
			Validator validator = schema.newValidator();
			// Validate file against schema
			XMLOutputter outputter = new XMLOutputter();
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(xmlWFConfig);
			validator.validate(new StreamSource(new StringReader(outputter
					.outputString(doc.getRootElement()))));
		} catch (Exception e) {
			String err = "The provided xmlWFConfig is not valid against the currently used planets_wdt_xsd schema";
			log.debug(err,e);
			throw new Exception (err,e);
		}
		finally{
			bis.close();
		}
	}
	
	/**
	 * Check if the provided wfXMLConfiguration is valid against the currently used schema
	 * @param xmlWFConfig
	 * @return
	 */
	public boolean isValidXMLConfig(String xmlWFConfig){
		InputStream ins = new ByteArrayInputStream(xmlWFConfig.getBytes());
		try{
			checkValidXMLConfig(ins);
			ins.close();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Check if the provided wfXMLConfiguration is valid against the currently used schema
	 * and throw an exception if not.
	 * @param xmlWFConfig
	 * @throws Exception
	 */
	public void checkValidXMLConfig(String xmlWFConfig) throws Exception{
		InputStream ins = new ByteArrayInputStream(xmlWFConfig.getBytes());
		try{
			checkValidXMLConfig(ins);
		}catch(Exception e){
			throw e;
		}finally{
			ins.close();
		}
	}

}
