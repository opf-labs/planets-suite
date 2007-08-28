package eu.planets_project.tb.api.services.mockups;

import java.util.List;
import java.util.Map;

public interface Service {
	
	public long getServiceID();
	public void setServiceEndpoint(String sURI);
	public String getServiceEndpoint();
	
	/**
	 * Returns the expected MIME Type for a given Parameter
	 * @param sParameterName
	 * @return
	 */
	public String getInputType(String sParameterName);
	public List<String> getInputTypes(List<String> sParameterNames);
	
	public void setInputParameter(String sParameterName, String sInputValue);
	public void setInputParameters(String[] sParameterNames, String[] sInputValues);
	
	public String getAllInputParameters();
	/**
	 * @return Hashtable<ParamName,ParamType>
	 */
	public Map<String,String>getAllInputParameterAndTypes();
	
	public String getParameterValue(String sParameterName);
	/**
	 * @return Hashtable<ParamName,ParamValue>
	 */
	public Map<String,String>getAllInputParameterValues();
	
	public void setServiceWSDL(String sWSDL);
	public String getServiceWSDL();
	
	//service metadata
	public void setServiceDescription(String sDescription);
	public String getServiceDescription();
	
	//Note: Add ServiceTypeID {migration, characterisation, emulation} to finals
	public void setServiceType(int iTypeID);
	public int getServiceType();


}
