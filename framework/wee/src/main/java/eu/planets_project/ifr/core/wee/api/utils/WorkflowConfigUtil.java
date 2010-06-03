package eu.planets_project.ifr.core.wee.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;


public class WorkflowConfigUtil implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1427261014786526266L;
	private static Log log = LogFactory.getLog(WorkflowConfigUtil.class);
	
	/**
	 * Uses the JAXB to provide a java api for the xml config marshalling/unmarshalling of workflow configurations
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
	
	/**
	 * Uses the JAXB to provide a java api for the xml config marshalling/unmarshalling of workflow configurations
	 * @param wfConfig
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws SAXException
	 */
	public String marshalWorkflowConfigToXMLTemplate(WorkflowConf wfConfig) throws JAXBException, IOException, SAXException{

		JAXBContext context;
		InputStream bis = getClass().getClassLoader().getResourceAsStream(
		"planets_wdt.xsd");
		try {
			 //Create temp file.
	        File temp = File.createTempFile("wfconfig", ".xml");
	        //Delete temp file when program exits.
	        temp.deleteOnExit();
	        FileOutputStream fos = new FileOutputStream(temp);
	        
			context = JAXBContext.newInstance(WorkflowConf.class);
			Marshaller m = context.createMarshaller(); 
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "planets_wdt.xsd");

			// create a SchemaFactory
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// load a schema, represented by a Schema instance
			Schema schema = factory.newSchema(new StreamSource(bis));
			m.setSchema(schema);
			
			//now call the actual marshalling job
			m.marshal(wfConfig,fos);
			
			
			String ret = readXMLConfigFileToString(temp);
			return ret;
		} catch (JAXBException e) {
			log.error("marshalWorkflowConfigToXML failed",e);
			throw e;
		}catch (IOException e2) {
			log.error("marshalWorkflowConfigToXML failed due to properly reading inputFile",e2);
			throw e2;
		} catch (SAXException e3) {
			log.error("marshalWorkflowConfigToXML failed",e3);
			throw e3;
		} 
	}
	
	private static String readXMLConfigFileToString(File file) throws IOException{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line + "\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
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
