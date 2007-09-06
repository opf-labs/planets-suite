/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.services.mockups.Service;

/**
 * @author alindley
 *
 */
//@Entity
public class ServiceImpl implements Service, java.io.Serializable {

	//@Id
	//@GeneratedValue
	private long id;
	private Vector<String> vInputMimeTypes;
	private Vector<String> vOutputMimeTypes;
	private String sDescription, sEndpointAddress, sName, sWSDL, sAuthor, sEndpointID;
	
	public ServiceImpl(){
		vInputMimeTypes = new Vector<String>();
		vOutputMimeTypes = new Vector<String>();
		sDescription = new String();
		sEndpointAddress = new String();
		sName = new String();
		sWSDL = new String();
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#addInputMIMEType(java.lang.String)
	 */
	public void addInputMIMEType(String mimeType) {
		if(isValidMimeType(mimeType)&&!vInputMimeTypes.contains(mimeType)){
			this.vInputMimeTypes.add(mimeType);
		}

	}
	
	private boolean isValidMimeType(String mimeType){
		boolean bRet = false;
		//parse input string if it is in the format String/String
		StringTokenizer tokenizer = new StringTokenizer(mimeType,"/",true);
		if (tokenizer.countTokens()==3){
			bRet = true;
		}
		return bRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#getDescription()
	 */
	public String getDescription() {
		return this.sDescription;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#getEndpoint()
	 */
	public String getEndpointAddress() {
		return this.sEndpointAddress;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#getInputMIMETypes(java.util.List)
	 */
	public List<String> getInputMIMETypes() {
		return this.vInputMimeTypes;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#getServiceName()
	 */
	public String getServiceName() {
		return this.sName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#getWSDL()
	 */
	public String getWSDL() {
		return this.sWSDL;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#removeInputMIMEType(java.lang.String)
	 */
	public void removeInputMIMEType(String mimeType) {
		if(this.vInputMimeTypes.contains(mimeType)){
			this.vInputMimeTypes.remove(mimeType);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.sDescription = description;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#setEndpoint(java.lang.String)
	 */
	public void setEndpointAddress(String sURI) {
		this.sEndpointAddress = sURI;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#setInputMIMETypes(java.util.List)
	 */
	public void setInputMIMETypes(List<String> mimeTypes) {
		this.vInputMimeTypes = new Vector<String>();
		for(int i=0;i<mimeTypes.size();i++){
			this.addInputMIMEType(mimeTypes.get(i));
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#setServiceName(java.lang.String)
	 */
	public void setServiceName(String name) {
		this.sName = name;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#setWSDL(java.lang.String)
	 */
	public void setWSDL(String sWSDL) {
		this.sWSDL = sWSDL;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.mockups.Service#setWSDL(java.io.File)
	 */
	public void setWSDL(File fileWSDL) {

		try {
			FileInputStream fis = new FileInputStream(fileWSDL);

		    // Here BufferedInputStream is added for fast reading.
		    BufferedInputStream bis = new BufferedInputStream(fis);
		    DataInputStream dis = new DataInputStream(bis);

		    // dis.available() returns 0 if the file does not have more lines.
		    while(dis.available()!=0) {
		    	this.sWSDL += dis.readLine();
		    }

		    //dispose all the resources after using them.
		    fis.close();
		    bis.close();
		    dis.close();
		    
		  }catch(FileNotFoundException e) {
		      //TODO add logging statement
		  }catch(IOException e) {
		      //TODO add logging statement
		  }
	}

	public void addOutputMIMEType(String mimeType) {
		if(isValidMimeType(mimeType)&&!vOutputMimeTypes.contains(mimeType)){
			this.vOutputMimeTypes.add(mimeType);
		}
	}

	public List<String> getOutputMIMETypes() {
		return this.vOutputMimeTypes;
	}

	public void removeOutputMIMEType(String mimeType) {
		if(this.vOutputMimeTypes.contains(mimeType)){
			this.vOutputMimeTypes.remove(mimeType);
		}
	}

	public void setOutputMIMETypes(List<String> mimeTypes) {
		this.vOutputMimeTypes = new Vector<String>();
		for(int i=0;i<mimeTypes.size();i++){
			this.addOutputMIMEType(mimeTypes.get(i));
		}
	}

	public String getAuthorName() {
		return this.sAuthor;
	}

	public String getEndpointID() {
		return this.sEndpointID;
	}

	public void setAuthorName(String name) {
		this.sAuthor = name;
	}

	public void setEndpointID(String name) {
		this.sEndpointID = name;
	}

}
