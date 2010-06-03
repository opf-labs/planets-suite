package eu.planets_project.tb.impl.serialization;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 31.10.2009
 * 
 * A utility class with static methods for marshalling and unmarshalling objects via Jaxb
 *
 */
public class JaxbUtil {
	
	 private static final Log log = LogFactory.getLog(JaxbUtil.class);
	
	/**
	 * Uses a StringWriter to capture the marshalled object
	 * @param result
	 * @return
	 */
	/*public static String marshallWorkflowResultToXML(WorkflowResult result) throws Exception{
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(WorkflowResult.class);
			Marshaller m = context.createMarshaller(); 
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			StringWriter sw = new StringWriter();
			//now call the actual marshalling job
			m.marshal(result, sw);
			return sw.toString();
		} catch (Exception e) {
			log.error("marshalWorkflowResult failed "+e);
			throw e;
		}
	}*/
	
	 /**
	 * Marshalling via Jaxb: Creates a String Serialization of the requested class for
	 * the content of Object objectToSerialize. The provided object and the requested class need to be of the same Type
	 * @param <T>
	 * @param objectClass
	 * @param objectToSerialize
	 * @return
	 * @throws Exception
	 */
	public static <T> String marshallObjectwithJAXB( Class<T> objectClass, T objectToSerialize) throws Exception{
		 JAXBContext context;
			try {
				context = JAXBContext.newInstance(objectClass);
				Marshaller m = context.createMarshaller(); 
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
				StringWriter sw = new StringWriter();
				//now call the actual marshalling job
				m.marshal(objectToSerialize, sw);
				return sw.toString();
			} catch (Exception e) {
				log.error("marshalWorkflowResult failed for objectClass: "+objectClass+" with "+e);
				throw e;
			}
	 }
	 
	public static <T> T unmarshallObjectViaJaxb(Class<T> objectClass, String serializedObject) throws Exception{
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(objectClass);
				Unmarshaller um = context.createUnmarshaller(); 
				return (T) um.unmarshal(new StringReader(serializedObject)); 
			} catch (JAXBException e) {
				log.error("unmarshalWorkflowResult failed for objectClass"+objectClass+" with "+e);
				throw e;
			} 
	}
	 
	/*public static WorkflowResult unmarshallWorkflowResult(String wfResultasXML) throws Exception{
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(WorkflowResult.class);
				Unmarshaller um = context.createUnmarshaller(); 
				WorkflowResult wfc = (WorkflowResult) um.unmarshal(new StringReader(wfResultasXML)); 
				return wfc;
			} catch (JAXBException e) {
				log.error("unmarshalWorkflowResult failed" +e);
				throw e;
			} 
	}*/

}
