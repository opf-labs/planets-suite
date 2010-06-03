package eu.planets_project.ifr.core.wee.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;


public class WFResultUtil implements Serializable{
	
	private static Log log = LogFactory.getLog(WFResultUtil.class);
	
	/**
	 * Uses the JAXB to provide a java api for the xml wfResult marshalling/unmarshalling of workflow configurations
	 * @return
	 * @throws JAXBException 
	 */
	public static WorkflowResult unmarshalWorkflowResult(String xmlWFResult) throws JAXBException{

			JAXBContext context;
			try {
				context = JAXBContext.newInstance(WorkflowResult.class);
				Unmarshaller um = context.createUnmarshaller(); 
				WorkflowResult wfc = (WorkflowResult) um.unmarshal(new StringReader(xmlWFResult)); 
				return wfc;
			} catch (JAXBException e) {
				log.error("unmarshalWFResult failed",e);
				throw e;
			} 
	}
	
	/**
	 * Uses the JAXB to provide a java api for the xml wfResult marshalling/unmarshalling of workflow configurations
	 * @param wfConfig
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static File marshalWorkflowResultToXMLFile(WorkflowResult wfResult,String wfID) throws JAXBException, IOException, SAXException{

		JAXBContext context;
		try {
			 //Create temp file.
	        File temp = File.createTempFile("wfResult-id-"+wfID, ".xml");
	        log.info("created wfResult file: "+temp.getCanonicalPath()+" starting marshalling");
	        //Delete temp file when program exits.
	        //temp.deleteOnExit();
	        FileOutputStream fos = new FileOutputStream(temp);
	        
			context = JAXBContext.newInstance(WorkflowResult.class);
			Marshaller m = context.createMarshaller(); 
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			//now call the actual marshalling job
			m.marshal(wfResult,fos);
	        log.info("created wfResult file: "+temp.getCanonicalPath()+" completed marshalling");
			//String ret = readXMLConfigFileToString(temp);
			return temp;
			
		} catch (JAXBException e) {
			log.error("marshalWorkflowConfigToXML failed",e);
			throw e;
		}catch (IOException e2) {
			log.error("marshalWorkflowConfigToXML failed due to properly reading inputFile",e2);
			throw e2;
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

}
