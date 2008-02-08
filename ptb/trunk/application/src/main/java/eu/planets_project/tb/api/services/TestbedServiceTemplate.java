package eu.planets_project.tb.api.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Andrew Lindley, ARC
 * 
 * This interface describes an TestbedService i.e. a Service which has been registered
 * through the Testbed's service registration interface (i.e. building a xmlrequest 
 *  template, invoking it with data and building an Xpath Statement) by using the wsclient classes
 * for actual service execution. The Testbed's Service Registry Impl registeres instances
 * of objects implementing this interface.
 * 
 * Important: A class implementing this interface contains all Information that's required 
 * for being able to invoke an experiment, execute the underlying service, and knows how to
 * extract the resulting data.
 */
public interface TestbedServiceTemplate {

	public String getDescription();
	public void setDescription(String sDescription);
	
	public String getEndpoint();
	public void setEndpoint(String sURL);
	/**
	 * Takes the Service's Endpoint and if extract = true, fetches and stores the WSDL's content.
	 * @param sURL
	 * @param extract true: fetches the WSDL content and stores it in .setWSDLContent(String content)
	 */
	public void setEndpoint(String sURL, boolean extract);
	
	public String getName();
	public void setName(String sName);

	/**
	 * Returns a list of all registered service operations.
	 * @return
	 */
	public List<ServiceOperation> getAllServiceOperations();
	public List<String> getAllServiceOperationNames();
	
	/**
	 * @param sName
	 * @return null if operation is not found
	 */
	public ServiceOperation getServiceOperation(String sName);
	
	/**
	 * @param sOperationName: null not allowed
	 * @param xmlRequestTemplate: null not allowed
	 * @param xPathToOutput: null not allowed
	 */
	public void addServiceOperation(String sOperationName, String xmlRequestTemplate, String xpathToOutput);
	public void addServiceOperation(ServiceOperation operation);
	public void removeServiceOperation(String sOperationName);
	public void setServiceOperations(List<ServiceOperation> operations);
	
	public boolean isEndpointWSICompliant();

	public void setEndpointWSICompliant(boolean compliant);
	
	/**
	 * Uses MD5 UUID Generation out of WSDL Content. This has the advantage, services
	 * can be recogized directly by their source. To getUUID either Endpoint or WSDL content need to be already set.
	 * The first time getUUID is called
	 * @return
	 */
	public String getUUID();
	
	public String getWSDLContent();
	public void setWSDLContent(String content);
	/**
	 * Fetches a given http service endpoint URL and fetches its content which
	 * is then stored.
	 * @param sURL
	 */
	public void extractWSDLContent(String sURL)throws FileNotFoundException, IOException;
	
	public boolean isOperationRegistered(String sOpName);
	
	
	/**
	 * @author Andrew Lindley, ARC
	 *
	 */
	public interface ServiceOperation{
		public String getName();
		public void setName(String sName);
		
		public String getDescription();
		public void setDescription(String sDescr);
		
		public String getXMLRequestTemplate();
		public void setXMLRequestTemplate(String template);
		
		public String getXPathToOutput();
		public void setXPathToOutput(String xpath);
		
		/**
		 * This method is checked when a TestbedServiceOperation is registered at the ServiceRegistry
		 * this information contains: XMLRequestTemplate, XPathOutputProcessing, etc.
		 * which are needed for service execution
		 * @return
		 */
		//public boolean isExecutionInformationComplete();
		
		/**
		 * Returns the restriction of maximum Input Elements 
		 * @return
		 */
		public int getMaxSupportedInputFiles();
		public void setMaxSupportedInputFiles(int i);
		
		/**
		 * Returns the restriction of minimum required Input Elements 
		 * @return
		 */
		public int getMinRequiredInputFiles();
		public void setMinRequiredInputFiles(int i);
		
		
		public final String SERVICE_OPERATION_TYPE_MIGRATION = "PA";
		public final String SERVICE_OPERATION_TYPE_CHARACTERISATION = "PC";
		/**
		 * A service operation can be classified by a certain type 
		 * e.g. PA or PC
		 * @return
		 */
		public String getServiceOperationType();
		public void setServiceOperationType(String sType);
		
		
		public final String OUTPUT_OBJECT_TYPE_FILE = "File";
		public final String OUTPUT_OBJECT_TYPE_String = "String";
		/**
		 * Returns the output object's type. e.g. File or String
		 * @param type
		 */
		public void setOutputObjectType(String type);
		public String getOutputObjectType();
	}

}
