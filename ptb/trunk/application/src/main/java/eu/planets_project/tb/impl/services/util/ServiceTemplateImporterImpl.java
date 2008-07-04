package eu.planets_project.tb.impl.services.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.api.services.util.ServiceTemplateImporter;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;
import eu.planets_project.tb.impl.services.TestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.tags.ServiceTagImpl;

/**
 * @author alindley
 * Responsible for handling the import of a single TestbedServiceTemplate.xml file which is
 * the format the Testbed serializes a ServiceTemplate. The file is validated
 * and afterwards converted and registered within the TBServiceTemplate registry
 * 1 service; 1..n operations
 *
 */
public class ServiceTemplateImporterImpl implements ServiceTemplateImporter,ErrorHandler{
	
	//The standard schema for migration/characterisation service templates
	private String xmlTemplateSchema;
	//The extended schema for evaluation service templates
	private String xmlEvaluationTemplateSchema;
	private Document document;
	private Element root;
	private boolean bEndpointReachable = false;
	//the serviceTemplate
	private TestbedServiceTemplate tbService;
	
	private boolean bIsServiceTemplate = false;
	private boolean bIsEvaluationServiceTemplate = false;
	//A helper as we need to have an Impl to call .new ServiceOperationimpl();
	TestbedServiceTemplateImpl t = new TestbedServiceTemplateImpl();
	
	/**
	 * Takes an input stream of an uploaded config-file as input and validates it against the provided XML template schema
	 * @param xmlTemplate
	 * @throws Exception if the import is not schema compliant
	 */
	public ServiceTemplateImporterImpl(InputStream xmlTemplate) throws Exception{
		//load serviceTemplate xsd schema - all imports have to be valid against it
		xmlTemplateSchema = getClass().getClassLoader().getResource("eu/planets_project/tb/impl/TBServiceTemplateSchema.xsd").toExternalForm();
		xmlEvaluationTemplateSchema = getClass().getClassLoader().getResource("eu/planets_project/tb/impl/TBEvaluationServiceTemplateSchema.xsd").toExternalForm();
		//File xmlSchema = new File("C:/DATA/Implementation/SVN_Planets/ptb/trunk/application/src/main/resources/eu/planets_project/tb/impl/TBServiceTemplateSchema.xsd");
		//xmlTemplateSchema = xmlSchema.getAbsolutePath();
		//validate xmlTemplate import file against schema and loads the Document
		this.parseAndValidateImportFile(xmlTemplate);
		root = document.getDocumentElement();
	}
	
	
	/**
	 * Takes the provided importTemplate.xml file, validates it against the schema
	 * and finally populates the Document object.
	 * @throws Exception if not a valid schema conform document
	 */
	private void parseAndValidateImportFile(InputStream xmlTemplate) throws Exception{
			Exception e;
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			factory.setAttribute(
		             "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
		             "http://www.w3.org/2001/XMLSchema");
			try{
				//1) Test if it's a valid TestbedEvaluationServiceSchema import
				factory.setAttribute(
					      "http://java.sun.com/xml/jaxp/properties/schemaSource",
					      xmlEvaluationTemplateSchema);
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				builder.setErrorHandler(this);
				this.document = builder.parse(xmlTemplate);
				this.bIsEvaluationServiceTemplate = true;
				return;
			}catch(Exception e1){
				e=e1;
			}
			try{
				//2) Test if it's a valid TestbedServiceSchema import
				//first reset the stream
				xmlTemplate.reset();
				factory.setAttribute(
					      "http://java.sun.com/xml/jaxp/properties/schemaSource",
					      xmlTemplateSchema);
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				builder.setErrorHandler(this);
				this.document = builder.parse(xmlTemplate);
				this.bIsServiceTemplate = true;
				return;
			}catch(Exception e1){
				e=e1;
			}
			
			throw e;
			
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceTemplateImporter#createTemplate()
	 */
	public TestbedServiceTemplate createTemplate() throws Exception{
		return helperCreateAndRegisterTemplate(false);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceTemplateImporter#createAndRegisterTemplate()
	 */
	public TestbedServiceTemplate createAndRegisterTemplate() throws Exception{
		return helperCreateAndRegisterTemplate(true);
	}
	
	/**
	 * @param register: indicates if the template is also registered within the service tempalte registry
	 * @return
	 */
	private TestbedServiceTemplate helperCreateAndRegisterTemplate(boolean register) throws Exception{
		
		if(this.isImportedServiceEndpointReachable()!=true)
			throw new Exception("Service endpoint "+this.getEndpointURI()+" not reachable");
		
		if(this.checkIsTBVersionIDcompliant()!=true)
			throw new Exception("Imported template with version "+this.getTBVersionID()+" not compliant with the currently deployed one");
		
		
		TestbedServiceTemplate.ServiceOperation o = t.new ServiceOperationImpl();
		
		boolean bOK = false;
		if(this.isAValidServiceTemplate()){
			tbService = new TestbedServiceTemplateImpl();
			bOK = true;
		}
		if(this.isAValidEvaluationServiceTemplate()){
			tbService = new EvaluationTestbedServiceTemplateImpl();
			bOK = true;
		}
		if(!bOK)
			throw new Exception("No template properly instantiated");
		
		
		TestbedServiceTemplate.ServiceOperation operation = t.new ServiceOperationImpl();
		ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
		
		//relevant information to build the wsclient bean: wsdlURI, servicename, opname
		//relevant information to build the TB parts: XMLRequest template, XPath query
		
		//endpoint + extract WSDLcontent
		tbService.setEndpoint(this.getEndpointURI(),true);
		//serviceName
		tbService.setName(this.getServiceName());
		
		//when registerin, check if this operation is added to an existing service
		if(register){
			//check if this is a new service or just a new operation for it
			TestbedServiceTemplate s = registry.getServiceByWSDLContent(tbService.getWSDLContent());
			if(s!=null){
				tbService = (TestbedServiceTemplateImpl)s;
			}
		}
		
		//description
		tbService.setDescription(this.getServiceDescription());
		//deployment date
		tbService.setDeploymentDate(this.getServiceDeploymentDate().getTimeInMillis());
		
		//add the service operations
		helperRegisterOperations();
		
		//extract the ServiceEvaluationData if it's an import from this schema
		if(this.isAValidEvaluationServiceTemplate()){
			helperAddEvaluationTBServiceTemplateData();
		}
		
		try {
			//register additional metadata for this service
			helperRegisterTags();
			
			if(!register){
				//don't register - just return the template object
				return tbService;
			}
			
			//now register the template within the registry
			try{
				String serviceID = tbService.getUUID();
				if(serviceID==null){
					//this leads to the error-page
					throw new Exception("Service did not obtain a proper UUID");
				}
				
				//register the service within the registry
				registry.registerService(tbService);
				return registry.getServiceByID(tbService.getUUID());

			}catch(Exception e){
				e.printStackTrace();
				//this leads to the error-page
				throw new Exception("Service did not obtain a proper UUID");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Build serviceOperation object, parse data from the xml and add the object
	 * to the TBServiceTemplate
	 */
	private void helperRegisterOperations(){
		for(int j=0;j<this.countOpNodes();j++){
			//create an empty serviceOperation object
			TestbedServiceTemplate.ServiceOperation operation = t.new ServiceOperationImpl();
			//take the current serviceOperation
			NodeList opSettings = this.getServiceOperationNode(j).getChildNodes();
			
			//extract the operation's properties
			for(int k=0;k<opSettings.getLength();k++){
				Node opNode = opSettings.item(k);
				
				//operationName
				if(opNode.getNodeName().equals("operationName"))
					operation.setName(opNode.getTextContent()); 
				
				//description
				if(opNode.getNodeName().equals("operationDescription"))
					operation.setDescription(opNode.getTextContent());
				
				//xmlRequestTemplate
				if(opNode.getNodeName().equals("xmlRequestTemplate"))
					operation.setXMLRequestTemplate(opNode.getTextContent());
				
				//xpathQueryToOutput
				if(opNode.getNodeName().equals("xPathToOutput"))
					operation.setXPathToOutput(opNode.getTextContent());
				
				//max. supported input files
				if(opNode.getNodeName().equals("getMaxAllowedNrOfInputFiles"))
					operation.setMaxSupportedInputFiles(Integer.valueOf(opNode.getTextContent()));
			
				//min. required input files
				if(opNode.getNodeName().equals("getMinRequiredNrOfInputFiles"))
					operation.setMinRequiredInputFiles(Integer.valueOf(opNode.getTextContent()));
			
				//set service Operation type: e.g. PA or PC or EV
				if(opNode.getNodeName().equals("serviceOperationType"))
					operation.setServiceOperationType(opNode.getTextContent());
			
				//set service output type
				if(opNode.getNodeName().equals("selectedOutputType"))
					operation.setOutputObjectType(opNode.getTextContent());
			
				//input Type: call by value or reference
				if(opNode.getNodeName().equals("callByValue")){
					boolean byValue = Boolean.valueOf(opNode.getTextContent());
					if(byValue){
						operation.setInputTypeIsCallByValue(byValue);
					}
					else{
						operation.setInputTypeIsCallByValue(!byValue);
					}	
				}
			
				if(opNode.getNodeName().equals("outputFileType")){
					if(operation.isInputTypeCallByValue()){
						operation.setOutputFileType(opNode.getTextContent());
					}
				}
				
			}
			
			//add the service operation
			this.tbService.addServiceOperation(operation);
		}	
	}
	
	/**
	 * If the imported operation is a "evaluationServiceTemplate" which extends the ServiceTemplate class
	 * this methods parses the additional provided information.
	 * i.e. XPathToMetricName, etc. as well as BMGoal2Property mapping
	 * 
	 * @param operation
	 */
	public void helperAddEvaluationTBServiceTemplateData(){
		Node nodeEvalProps = this.getEvalPropertiesNode();
		NodeList nodes = nodeEvalProps.getChildNodes();
		
		
		//extract the operation's properties
		for(int k=0;k<nodes.getLength();k++){
			Node n = nodes.item(k);
			
			//XPathForBMGoalRootNodes
			if(n.getNodeName().equals("XPathForBMGoalRootNodes")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathForBMGoalRootNodes(n.getTextContent());
			}
			//XPathForBMGoalRootNodes
			if(n.getNodeName().equals("XPathForBMGoalName")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathForName(n.getTextContent());
			}
			//SrcXpath
			if(n.getNodeName().equals("SrcXpath")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathForSrcValue(n.getTextContent());
			}
			//TarXpath
			if(n.getNodeName().equals("TarXpath")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathForTarValue(n.getTextContent());
			}
			//MetricName
			if(n.getNodeName().equals("MetricName")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathToMetricName(n.getTextContent());
			}
			//MetricResult
			if(n.getNodeName().equals("MetricResult")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathToMetricValue(n.getTextContent());
			}
			//CompStatusSuccess
			if(n.getNodeName().equals("CompStatusSuccess")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setStringForCompStatusSuccess(n.getTextContent());
			}
			//CompStatusXpath
			if(n.getNodeName().equals("CompStatusXpath")){
				((EvaluationTestbedServiceTemplateImpl) tbService).setXPathToCompStatus(n.getTextContent());
			}
			//CompStatusXpath
			if(n.getNodeName().equals("mappings")){
				NodeList mappings = n.getChildNodes();
				for (int p=0;p<mappings.getLength();p++){
					String BMGoalName ="";
					String BMGoalID = "";
					String PropertyName="";
					//Iterate over the <mapping> elements
					Node mapping = mappings.item(p);
					for(int l=0;l<mapping.getChildNodes().getLength();l++){
						//BMGoalName
						if(mapping.getChildNodes().item(l).getNodeName().equals("BMGoalName")){
							BMGoalName = mapping.getChildNodes().item(l).getTextContent();
						}
						//BMGoalID
						if(mapping.getChildNodes().item(l).getNodeName().equals("BMGoalID")){
							BMGoalID = mapping.getChildNodes().item(l).getTextContent();
						}
						//PropertyName
						if(mapping.getChildNodes().item(l).getNodeName().equals("PropertyName")){
							PropertyName = mapping.getChildNodes().item(l).getTextContent();
						}
					}
					if((!BMGoalName.equals(""))&&(!BMGoalID.equals(""))&&(!PropertyName.equals(""))){
						((EvaluationTestbedServiceTemplateImpl) tbService).setBMGoalPropertyNameMapping(BMGoalName, BMGoalID, PropertyName);
					}
				}
			}
		}
	}
	
	
	/**
	 * Takes the map of parsed Tag/Value pairs and adds them to the serviceTemplate
	 * @throws Exception
	 */
	private void helperRegisterTags() throws Exception{
		//register all Tag names + values
		Iterator<String> itTags = this.getMapTagNamesValues().keySet().iterator();
		int count =0;
		while(itTags.hasNext()){
			String sTag = itTags.next();
			String sValue = this.getMapTagNamesValues().get(sTag);
			if((sValue!=null)&&(!sValue.equals(""))){
				
				//add this tag to the service template
				ServiceTag tag = new ServiceTagImpl();
				tag.setTag(sTag,sValue);
				
				//remove all tags because this wizard contains and adds all of them
				if(count==0)
					tbService.removeTags();
				tbService.addTag(tag);
				count++;
			}
		}
	}
	
	/* (non-Javadoc)
	 * Please note: very time consuming operation
	 * @see eu.planets_project.tb.api.services.util.ServiceTemplateImporter#isImportedServiceEndpointReachable()
	 */
	private int helper = 0;
	public boolean isImportedServiceEndpointReachable() {
		if(helper==0){
			this.checkEndpointIsReachable();
		}
		return this.bEndpointReachable;
	}
	
	private void checkEndpointIsReachable(){
		TestbedServiceTemplateImpl tbService = new TestbedServiceTemplateImpl();
		try {
			tbService.extractWSDLContent(this.getEndpointURI());
		} catch (FileNotFoundException e) {
			bEndpointReachable= false;
		} catch (IOException e) {
			bEndpointReachable= false;
		}
		bEndpointReachable= true;
	}
	
	private boolean checkIsTBVersionIDcompliant(){
		TestbedManager manager = TestbedManagerImpl.getInstance(true);
		if(manager.getCurrentTBVersionNumber().equals(this.getTBVersionID()))
				return true;
		return false;
	}
	
	
	
	//START PARSING THE XML of the imported file
	
	private Map<String,String> getMapTagNamesValues() {
		Map<String,String> ret = new HashMap<String,String>();
		NodeList tags = root.getElementsByTagName("tag");
		for(int k=0;k<tags.getLength();k++){
			 Node tag = tags.item(k);
			 String name = tag.getAttributes().item(0).getTextContent();
			 String value = tag.getChildNodes().item(1).getTextContent();
			 ret.put(name,value);
		}
		return ret;
	}
	
	private Node getServiceOperationNode(int index){
		return root.getElementsByTagName("serviceOperation").item(index);
	}
	
	private int countOpNodes(){
		return root.getElementsByTagName("serviceOperation").getLength();
	}

	private Calendar getServiceDeploymentDate() {
		return new GregorianCalendar();
	}

	private String getServiceDescription() {
		return root.getElementsByTagName("serviceDescription").item(0).getTextContent();
	}

	private String getServiceName() {
		return root.getElementsByTagName("serviceName").item(0).getTextContent();
	}

	private String getEndpointURI(){
		return root.getElementsByTagName("endpoint").item(0).getTextContent();
	}
	
	private Node getEvalPropertiesNode(){
		return root.getElementsByTagName("evalProperties").item(0);
	}
	
	/**
	 * Contains the MD5 id of a service which is created from the wsdl content
	 * @return
	 */
	private String getServiceTemplateMD5ID(){
		return root.getAttribute("serviceMD5id");
	}
	
	/**
	 * version number of the TB application this xml was exported from
	 * @return
	 */
	private String getTBVersionID(){
		return root.getAttribute("TBversionID");
	}
	
	/**
	 * Returns true if the imported config file validates against the ServiceTemplate schema
	 * @return
	 */
	public boolean isAValidServiceTemplate(){
		return this.bIsServiceTemplate;
	}
	
	/**
	 * Returns true if the imported config file validates against the ServiceTemplate schema
	 * @return
	 */
	public boolean isAValidEvaluationServiceTemplate(){
		return this.bIsEvaluationServiceTemplate;
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException exception) throws SAXException {
	}

}
