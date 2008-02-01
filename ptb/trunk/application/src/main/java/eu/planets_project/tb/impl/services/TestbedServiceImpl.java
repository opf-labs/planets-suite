package eu.planets_project.tb.impl.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import eu.planets_project.tb.api.services.TestbedService;
import eu.planets_project.tb.api.services.TestbedService.ServiceOperation;

/**
 * @author alindley
 *
 */
public class TestbedServiceImpl implements TestbedService{
	
	private String sServiceDescription, sServiceEndpoint, sServiceName, sServiceID, sWSDLContent;
	private boolean bURIisWSICompliant;
	//not only the registered Service Operations - note: non registered ones cannot be invoked
	private List<ServiceOperation> lAllRegisteredServiceOperations;
	//all Operation Names within the WSDL not only the registered ones that can be executed via the TB
	private List<String> lAllOperationNamesFromWSDL;

	
	
	public TestbedServiceImpl(){
		sServiceDescription = "";
		sServiceEndpoint = "";
		sServiceName = "";
		sServiceID = "";
		sWSDLContent = "";
		bURIisWSICompliant = false;
		lAllOperationNamesFromWSDL = new Vector<String>();
		lAllRegisteredServiceOperations = new Vector<ServiceOperation>();

	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.api.model.Service#getServiceDescription()
	 */
	public String getDescription(){
		return this.sServiceDescription;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setDescription(java.lang.String)
	 */
	public void setDescription(String sDescription){
		this.sServiceDescription = sDescription;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.api.model.Service#getServiceEntpoint()
	 */
	public String getEntpoint(){
		return this.sServiceEndpoint;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setEndpoint(java.lang.String)
	 */
	public void setEndpoint(String sURL){
		this.sServiceEndpoint = sURL;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#getName()
	 */
	public String getName(){
		return this.sServiceName;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setName(java.lang.String)
	 */
	public void setName(String sName){
		this.sServiceName = sName;
	}
	
	
	/**
	 * Used in the context of: If you're missing a registered operation, use this method
	 * to see which operations are offered by the service to then possibly ask ask the TB
	 * admin to register this operation for you
	 */
	public List<String> getAllWSDLOperationNames(){
		return this.lAllOperationNamesFromWSDL;
	}
	
	/**
	 * Used in the context of: If you're missing a registered operation, use this method
	 * to see which operations are offered by the service to then possibly ask ask the TB
	 * admin to register this operation for you
	 */
	public void setAllWSDLOperationNames(List<String> operationNames){
		if(operationNames!=null){
			this.lAllOperationNamesFromWSDL = operationNames;
		}
	}
	
	
	public void addServiceOperation(String sOperationName, String xmlRequestTemplate, String xpathtoOutput){
		if((sOperationName!=null)&&(xmlRequestTemplate!=null)&&(xpathtoOutput!=null)){
			ServiceOperation op = new ServiceOperationImpl(xmlRequestTemplate,xpathtoOutput);
			op.setName(sOperationName);
			
			//now add this service operation
			addServiceOperation(op);
			
		}
	}
	
	public void addServiceOperation(ServiceOperation operation){
		if(operation!=null){
			this.lAllRegisteredServiceOperations.add(operation);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#removeServiceOperation(java.lang.String)
	 */
	public void removeServiceOperation(String sOperationName){
		if(sOperationName!=null){
			if(this.getAllServiceOperationNames().contains(sOperationName)){
				Iterator<ServiceOperation> it = this.getAllServiceOperations().iterator();
				while(it.hasNext()){
					ServiceOperation op = it.next();
					if(op.getName().equals(sOperationName)){
						this.lAllRegisteredServiceOperations.remove(op);
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setServiceOperations(java.util.List)
	 */
	public void setServiceOperations(List<ServiceOperation> operations){
		if(operations!=null){
			this.lAllRegisteredServiceOperations = new Vector<ServiceOperation>();
			Iterator<ServiceOperation> it = operations.iterator();
			while(it.hasNext()){
				addServiceOperation(it.next());
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#getServiceOperation(java.lang.String)
	 */
	public ServiceOperation getServiceOperation(String sName){
		if(sName!=null){
			if(this.getAllServiceOperationNames().contains(sName)){
				Iterator<ServiceOperation> it = this.getAllServiceOperations().iterator();
				while(it.hasNext()){
					ServiceOperation op = it.next();
					if(op.getName().equals(sName)){
						return op;
					}
				}
			}
		}
		//else
		return null;
	}


	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#isEndpointWSICompliant()
	 */
	public boolean isEndpointWSICompliant() {
		return this.bURIisWSICompliant;
	}

	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setEndpointWSICompliant(boolean)
	 */
	public void setEndpointWSICompliant(boolean compliant) {	
		this.bURIisWSICompliant = compliant;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#getUUID()
	 */
	public String getUUID(){
		//test if an UUID has already been generated
		if((this.sServiceID==null)||(this.sServiceID.length()==0)){
			try{
				this.sServiceID = this.generateUUID();
			}catch(Exception e){
				//TODO log;
			}
		}
		//cannot be null except an error occured
		return this.sServiceID;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#getWSDLContent()
	 */
	public String getWSDLContent(){
		return this.sWSDLContent;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#extractWSDLContent(java.lang.String)
	 */
	public void setWSDLContent(String content){
		if(content!=null){
			this.sWSDLContent = content; 
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#getAllServiceOperations()
	 */
	public List<ServiceOperation> getAllServiceOperations(){
		return this.lAllRegisteredServiceOperations;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#getAllServiceOperationNames()
	 */
	public List<String> getAllServiceOperationNames(){
		List<String> lRet = new Vector<String>();
		Iterator<ServiceOperation> it = this.getAllServiceOperations().iterator();
		while(it.hasNext()){
			lRet.add(it.next().getName());
		}
		return lRet;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#isOperationRegistered(java.lang.String)
	 */
	public boolean isOperationRegistered(String opName){
		if(opName!=null){
			return this.getAllServiceOperationNames().contains(opName);
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setEndpoint(java.lang.String, boolean)
	 */
	public void setEndpoint(String sURL, boolean extract) {
		if(sURL!=null){
			this.setEndpoint(sURL);
			if(extract){
				try{
					this.extractWSDLContent(sURL);
				} catch (FileNotFoundException e) {
					// TODO LOG statement
				} catch (IOException e) {
					//TODO LOG statement
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#extractWSDLContent(java.lang.String)
	 */
	public void extractWSDLContent(String sURL)throws FileNotFoundException, IOException{

		InputStream in = null;
		try{
			in = new URL(sURL).openStream();
			boolean eof = false;
			String content = "";
			StringBuffer sb = new StringBuffer();
			while(!eof){
				int byteValue = in.read();
				if(byteValue != -1){
					char b = (char)byteValue;
					sb.append(b);
				}
				else{
					eof = true;
				}
			}
			content = sb.toString();
			if(content!=null){
				//now store the services WSDL content
				this.setWSDLContent(content);
			}
		}
		finally{
			in.close();
		}
	}
	
	
	/**
	 * UniqueIDs are created by using an MD5 hashing on the service's WSDL content. This allows to distinguish
	 * different services with the same name and on the other hand to classify a service just on behalf of its 
	 * WSDL contract fingerprint.
	 * @throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException 
	 */
	private String generateUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException{
		
		if((this.getWSDLContent()==null)||(this.getWSDLContent().length()==0)){
			//last chance: download information from service Endpoint
			if((this.getEntpoint()!=null)&&(this.getEntpoint().length()!=0)){
				this.extractWSDLContent(this.getEntpoint());
			}
			else{
				//no chance to get the information
				throw new IOException("WSDL Content could not be loaded");
			}
		}

		//now do the hasing
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		//use the WSDL content for extracting the MD5 Hash
		byte[] result =  md5.digest( this.getWSDLContent().getBytes("UTF-8") );

		//transform and return the result
		return hexEncode(result);
	}

	/**
	* The byte[] returned by MessageDigest does not have a nice
	* textual representation, so some form of encoding is usually performed.
	*
	* This implementation follows the example of David Flanagan's book
	* "Java In A Nutshell", and converts a byte array into a String
	* of hex characters.
	*
	* Another popular alternative is to use a "Base64" encoding.
	*/
	private String hexEncode( byte[] aInput){
		  StringBuffer result = new StringBuffer();
		  char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
		  for ( int idx = 0; idx < aInput.length; ++idx) {
		     byte b = aInput[idx];
		     result.append( digits[ (b&0xf0) >> 4 ] );
		     result.append( digits[ b&0x0f] );
		  }
		  return result.toString();
	}
	
	
	public class ServiceOperationImpl implements TestbedService.ServiceOperation{
		
		private String sName ="";
		//Template containing a valid XML Request for this operation - with placeholders
		private String sXMLRequestTemplate="";
		private String sXPathToOutput="";
		private int iMaxSupportedInputFiles = 100;
		private int iMinRequiredInputFiles = 1;
		private String sServiceType="";
		private String sOutputOjbectType="";
		
		
		public ServiceOperationImpl(String sXMLRequestTemplate, String sXPathToOutput){
			if((sXMLRequestTemplate!=null)&&(sXPathToOutput!=null)){
				this.setXMLRequestTemplate(sXMLRequestTemplate);
				this.setXPathToOutput(sXPathToOutput);
			}
		}
		
		public ServiceOperationImpl(){
			
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getName()
		 */
		public String getName(){
			return this.sName;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setName(java.lang.String)
		 */
		public void setName(String name){
			if(name!=null)
				this.sName = name;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getXMLRequestTemplate()
		 */
		public String getXMLRequestTemplate(){
			return this.sXMLRequestTemplate;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setXMLRequestTemplate(java.lang.String)
		 */
		public void setXMLRequestTemplate(String template){
			if(template!=null)
				this.sXMLRequestTemplate = template;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getXPathToOutput()
		 */
		public String getXPathToOutput(){
			return this.sXPathToOutput;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setXPathToOutput(java.lang.String)
		 */
		public void setXPathToOutput(String xpath){
			if(xpath!=null)
				this.sXPathToOutput = xpath;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets.test.backend.api.model.mockup.TestbedService.ServiceOperation#isExecutionInformationComplete()
		 */
		public boolean isExecutionInformationComplete() {
			if((sName.length()>0)&&(sXMLRequestTemplate.length()>0)
					&&(sXPathToOutput.length()>0))
				return true;
			return false;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setMaxSupportedInputFiles(int)
		 */
		public void setMaxSupportedInputFiles(int i){
			if(i>=1)
				this.iMaxSupportedInputFiles = i;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getMaxSupportedInputFiles()
		 */
		public int getMaxSupportedInputFiles(){
			return this.iMaxSupportedInputFiles;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setMinRequiredInputFiles(int)
		 */
		public void setMinRequiredInputFiles(int i){
			if(i>=1)
				this.iMinRequiredInputFiles = i;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getMinRequiredInputFiles()
		 */
		public int getMinRequiredInputFiles(){
			return this.iMinRequiredInputFiles;
		}
		

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getServiceOperationType()
		 */
		public String getServiceOperationType() {
			return this.sServiceType;
		}


		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setServiceOperationType(java.lang.String)
		 */
		public void setServiceOperationType(String sType) {
			if(sType!=null){
				this.sServiceType = sType;
			}
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#getOutputObjectType()
		 */
		public String getOutputObjectType() {
			return this.sOutputOjbectType;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedService.ServiceOperation#setOutputObjectType(java.lang.String)
		 */
		public void setOutputObjectType(String type) {
			if(type!=null){
				this.sOutputOjbectType = type;
			}
		}

	}

}
