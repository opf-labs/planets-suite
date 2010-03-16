package eu.planets_project.tb.impl.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.Log;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.impl.services.tags.ServiceTagImpl;

/**
 * @author alindley
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="DiscrCol")
//@Table(name="TBServiceTemplate")
@XmlAccessorType(XmlAccessType.FIELD) 
public class TestbedServiceTemplateImpl implements TestbedServiceTemplate, java.io.Serializable, Cloneable{
//    private static final long serialVersionUID = 19584521347834L -4546334927373617048L;
    
    private String sServiceDescription, sServiceEndpoint, sServiceName;
    @XmlTransient
	private String sWSDLContent;
	private boolean bURIisWSICompliant;
	//not only the registered Service Operations - note: non registered ones cannot be invoked
	// FIXME: Should this be in the XML? Only can be if a real class (not inner).
    @XmlTransient
    @Lob
	private Vector<ServiceOperationImpl> lAllRegisteredServiceOperations;
	//all Operation Names within the WSDL not only the registered ones that can be executed via the TB
	//Note: to persist this object it's impl and not its interface is required here
    @Lob
	private Vector<String> lAllOperationNamesFromWSDL;
    //all tag names and values that have been registered for this service
    @Lob
	private Vector<ServiceTagImpl> lTags;
	//records the service's first deployment data
	private Calendar deploymentDate = new GregorianCalendar();

	@Transient
    @XmlTransient
	public String DISCR_TEMPLATE = "template";
	@Transient
    @XmlTransient
	public String DISCR_EXPERIMENT = "experiment";
	//discriminator can either be "template" or "experiment". later one indicates TBServiceTemplates being used within an experiment
	@Column(name="discr")
	private String sdiscr = DISCR_TEMPLATE;
	
	// This annotation specifies that the property or field is not persistent.
	@Transient
    @XmlTransient
	private static Log log;
	//The ServiceTemplate's UUID is used as discriminator
	@Column(name="hashUUID")
	private String sServiceID;
	
	@Id
	@GeneratedValue
    @XmlTransient
	private long lEntityID;
	
	public TestbedServiceTemplateImpl(){
		log = LogFactory.getLog(this.getClass());
		sServiceDescription = "";
		sServiceEndpoint = "";
		sServiceName = "";
		sServiceID = "";
		sWSDLContent = "";
		bURIisWSICompliant = false;
		lAllOperationNamesFromWSDL = new Vector<String>();
		lAllRegisteredServiceOperations = new Vector<ServiceOperationImpl>();
		lTags = new Vector<ServiceTagImpl>();

	}
	
	public long getEntityID(){
		return this.lEntityID;
	}
	
	public void setEntityID(long entityID){
		this.lEntityID = entityID;
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
	 * @see eu.planets.test.api.model.Service#getServiceEndpoint()
	 */
	public String getEndpoint(){
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
		//return this.lAllOperationNamesFromWSDL;
		//TODO: not yet implemented
		return new Vector<String>();
	}
	
	/**
	 * Used in the context of: If you're missing a registered operation, use this method
	 * to see which operations are offered by the service to then possibly ask ask the TB
	 * admin to register this operation for you
	 */
	public void setAllWSDLOperationNames(List<String> operationNames){
		/*if(operationNames!=null){
			this.lAllOperationNamesFromWSDL = (Vector<String>)operationNames;
		}*/
		//TODO: not yet implemented
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
			this.lAllRegisteredServiceOperations.add((ServiceOperationImpl)operation);
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#removeServiceOperation(java.lang.String)
	 */
	public void removeServiceOperation(String sOperationName){
		if(sOperationName!=null){
			if(this.getAllServiceOperationNames().contains(sOperationName)){
				Iterator<ServiceOperation> it = this.getAllServiceOperations().iterator();
				boolean bFound = false;
				ServiceOperation opRet = null;
				while(it.hasNext()){
					ServiceOperation op = it.next();
					if(op.getName().equals(sOperationName)){
						opRet = op;
						bFound=true;
					}
				}
				if(bFound){
					this.lAllRegisteredServiceOperations.remove(opRet);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#setServiceOperations(java.util.List)
	 */
	public void setServiceOperations(List<ServiceOperation> operations){
		if(operations!=null){
			this.lAllRegisteredServiceOperations = new Vector<ServiceOperationImpl>();
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
	    Vector<ServiceOperation> sos = new Vector<ServiceOperation>();
	    for( ServiceOperationImpl soi : this.lAllRegisteredServiceOperations )
	        sos.add(soi);
		return sos;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#getAllServiceOperationsByType(java.lang.String)
	 */
	public List<ServiceOperation> getAllServiceOperationsByType(String serviceOperationType){
		List<ServiceOperation> ret = new Vector<ServiceOperation>();
		Iterator<ServiceOperation> operations = getAllServiceOperations().iterator();
		while(operations.hasNext()){
			ServiceOperation operation = operations.next();
			if(operation.getServiceOperationType().equals(serviceOperationType))
				ret.add(operation);
		}
		return ret;
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
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#addTag(eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceTag)
	 */
	public void addTag(ServiceTag tag) {
		if(tag!=null){
			
			//try to remove a previous tag with the same name
			this.removeTag(tag.getName());
			
			//add the new item
			this.lTags.add((ServiceTagImpl)tag);
		}
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#getAllTags()
	 */
	public List<ServiceTag> getAllTags() {
	    List<ServiceTag> sts = new Vector<ServiceTag>();
	    for( ServiceTagImpl tag : this.lTags )
	        sts.add(tag);
		return sts;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#getTag(java.lang.String)
	 */
	public ServiceTag getTag(String sTagName) {
		if(sTagName!=null){
			Iterator<ServiceTagImpl> tags = this.lTags.iterator();

			while(tags.hasNext()){
				ServiceTag tagit = tags.next();
				//Tag was found, as TagName only may exist once
				if(tagit.getName().equals(sTagName)){
					return tagit;
				}
			}
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#removeTag(java.lang.String)
	 */
	public void removeTag(String sTagName) {
		if(sTagName!=null){
			Iterator<ServiceTagImpl> tags = this.lTags.iterator();
			boolean bFound = false;
			ServiceTag bFoundTag = null;
			while(tags.hasNext()){
				ServiceTag tagit = tags.next();
				//Tag to replace was found
				if(tagit.getName().equals(sTagName)){
					bFound = true;
					bFoundTag = tagit;
				}
			}
			//remove the old item
			if(bFound){
				this.lTags.remove(bFoundTag);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#removeTags()
	 */
	public void removeTags() {
		this.lTags = new Vector<ServiceTagImpl>();
	}

	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.TestbedService#extractWSDLContent(java.lang.String)
	 */
	public void extractWSDLContent(String sURL)throws FileNotFoundException, IOException, NullPointerException{

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
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#setDeploymentDate(long)
	 */
	public void setDeploymentDate(long timeInMillis) {
		this.deploymentDate.setTimeInMillis(timeInMillis);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate#getDeploymentDate()
	 */
	public Calendar getDeploymentDate() {
		return this.deploymentDate;
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
			if((this.getEndpoint()!=null)&&(this.getEndpoint().length()!=0)){
				this.extractWSDLContent(this.getEndpoint());
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
	
	
	/* (non-Javadoc)
	 * This method is required to separate between ServiceTemplates that 
	 * 1.are stored as part of an experiment an may not be deleted and templates
	 * 2.that are registered by the admin. So if 2. gets deleted 1. must be still available
	 * @see java.lang.Object#clone()
	 */
	public TestbedServiceTemplateImpl clone(){
		TestbedServiceTemplateImpl template = new TestbedServiceTemplateImpl();
		try{
			template = (TestbedServiceTemplateImpl) super.clone();
		}catch(CloneNotSupportedException e){
			log.error("Error cloning TestbedServiceTemplateImpl Object"+e.toString());
		}
		
		return template;
	}
	
	
	/**
	 * Sets a discriminator to distinguish between
	 * a) templates that are used within an experiment and therefore cannot be deleted anymore
	 * b) templates that are displayed in the list of available TBServiceTemplates - these can also be deleted
	 * @param discr
	 */
	public void setDiscriminator(String discr){
		if((discr!=null)&&(discr.equals(this.DISCR_TEMPLATE))){
			this.sdiscr = discr;
		}
		if((discr!=null)&&(discr.equals(this.DISCR_EXPERIMENT))){
			this.sdiscr = discr;
		}
	}
	
	public String getDiscriminator(){
		return this.sdiscr;
	}
	
	
	
	/**
	 * @author Andrew Lindley, ARC
	 *
	 */
	@Embeddable
	public class ServiceOperationImpl implements TestbedServiceTemplate.ServiceOperation, java.io.Serializable{
		
		private String sName ="";
		private String sDescription ="";
		//Template containing a valid XML Request for this operation - with placeholders
		private String sXMLRequestTemplate="";
		private String sXPathToOutput="";
		private int iMaxSupportedInputFiles = 100;
		private int iMinRequiredInputFiles = 1;
		private String sServiceType="";
		private String sOutputOjbectType="";
		private boolean bInputTypeIsCallByValue=true;
		private String sOutputFileType="";
		
		
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

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#getDescription()
		 */
		public String getDescription() {
			return this.sDescription;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#setDescription(java.lang.String)
		 */
		public void setDescription(String sDescr) {
			if(sDescr!=null){
				this.sDescription = sDescr;
			}
			
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#isInputTypeCallByValue()
		 */
		public boolean isInputTypeCallByValue() {
			return this.bInputTypeIsCallByValue;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#setInputTypeIsCallByReference(boolean)
		 */
		public void setInputTypeIsCallByReference(boolean b) {
			this.bInputTypeIsCallByValue = !b;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#setInputTypeIsCallByValue(boolean)
		 */
		public void setInputTypeIsCallByValue(boolean b) {
			this.bInputTypeIsCallByValue = b;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#getOutputFileType()
		 */
		public String getOutputFileType() {
			return this.sOutputFileType;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation#setOutputFileType(java.lang.String)
		 */
		public void setOutputFileType(String s) {
			this.sOutputFileType = s;
		}

	}

}
