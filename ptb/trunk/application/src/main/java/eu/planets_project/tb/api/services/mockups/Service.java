package eu.planets_project.tb.api.services.mockups;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Service {
	
	public void setEndpointAddress(String sURI);
	public String getEndpointAddress();
	
	public void setEndpointID(String sName);
	public String getEndpointID();
	
	public void setWSDL(String sWSDL);
	public void setWSDL(File fileWSDL);
	public String getWSDL();
	
	/**
	 * Returns the expected MIME Type for a given Parameter
	 * @param sParameterName
	 * @return
	 */
	public List<String> getInputMIMETypes();
	public void setInputMIMETypes(List<String> sMimeTypes);
	public void addInputMIMEType(String sMimeType);
	public void removeInputMIMEType(String sMimeType);
	
	public List<String> getOutputMIMETypes();
	public void setOutputMIMETypes(List<String> sMimeTypes);
	public void addOutputMIMEType(String sMimeType);
	public void removeOutputMIMEType(String sMimeType);
	
	//service metadata
	public void setDescription(String sDescription);
	public String getDescription();
	
	public void setServiceName(String sName);
	public String getServiceName();
	
	public void setAuthorName(String sName);
	public String getAuthorName();

}
