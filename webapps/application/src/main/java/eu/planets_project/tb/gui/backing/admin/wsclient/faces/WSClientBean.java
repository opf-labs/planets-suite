package eu.planets_project.tb.gui.backing.admin.wsclient.faces;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.wsdl.xml.WSDLReaderImpl;

import eu.planets_project.tb.gui.backing.admin.wsclient.util.ComponentBuilder;
import eu.planets_project.tb.gui.backing.admin.wsclient.util.OperationInfo;
import eu.planets_project.tb.gui.backing.admin.wsclient.util.ServiceInfo;
import eu.planets_project.tb.gui.backing.admin.wsclient.util.WSClient;

/**
 * This bean implements the following logic:
 *    a generic SOAP client 
 *    a WSDL conformance check
 *
 * @author Markus Reis, ARC
 */
public class WSClientBean implements ValueChangeListener
{

	//the log of this class
	private Log log = LogFactory.getLog(WSClientBean.class);	

	//the selected WebService (i.e. a SelectItem) in the 
 	//   drop-down list of available services 
 	//   (defined in the selected WSDL contract) 	 
	SelectItem serviceSelectItem = null;
	
	//the selected WebService operation (i.e. a SelectItem) in the 
 	//   drop-down list of available operations 
 	//   (defined in the selected WSDL contract) 	 	
	SelectItem operationSelectItem = null;
	
	//the wsdl uri displayed in the text control
	String wsdlURI = "";
	
	//the SelectItems displayed in the drop-down list of
	//   available services (defined in the selected WSDL contract)
	ArrayList<SelectItem> serviceSelectItems = new ArrayList<SelectItem>();

	//the SelectItems displayed in the drop-down list of
	//   available service operations
	//   (defined in the selected WSDL contract for the selected WebService)
	ArrayList<SelectItem> operationSelectItems = new ArrayList<SelectItem>();

	//the ServiceInfo objects corresponding to the 
	//   WebServices displayed in the drop-down list of
	//   available services (defined in the selected WSDL contract)	
	HashMap<String, ServiceInfo> serviceInfos = new HashMap<String, ServiceInfo>();
	
	//the OperationInfo objects corresponding to the 
	//   WebService operations displayed in the drop-down list of
	//   available service operations
	//   (defined in the selected WSDL contract for the selected WebService)		
	HashMap<String, OperationInfo> operationInfos = new HashMap<String, OperationInfo>();

	//the XML data sent back from the WebService endpoint on a previous request
	String xmlResponse = "";

	//the directory on the local FS where files are stored/served from
	String workDir = "";

	//http URL pointing to a certain context on the application server
	String reportContext = "";
	
	//the URI specifying the location where to download the HTML conformance
	//   check results
	String wsiResultURI = "";

	/**
	 * Constructor
	 */
	public WSClientBean()
	{
	}

	/**
	 * sets the working directory
	 * @param dir - a file path (either relative or absolute) to a 
	 *              directory on the local FS 
	 */
	public void setWorkDir(String dir) {
		workDir = dir;
	}


	/**
	 * gets the working directory
	 * @return a file path (either relative or absolute) to a 
	 *         directory on the local FS
	 */
	public String getWorkDir() {
		return workDir;
	}


	/**
	 * a http URL pointing to a certain context on the application server 
	 * @param context - in the form of a http URL
	 */
	public void setReportContext(String context) {
		reportContext = context;
	}


	/**
	 * the http URL pointing to a certain context on the application server 
	 * @return context in the form of a http URL
	 */
	public String getReportContext() {
		return reportContext;
	}


	/**
	 * Sets the wsdl uri displayed in the text control
	 *
	 * @param uri - The URI of the WSDL to set
	 */
	public void setWsdlURI(String uri)
	{
		log.debug("Setting WSDL uri to: " + uri);
		wsdlURI = uri;
	}


	/**
	 * Gets the wsdl uri displayed in the text control
	 *
	 * @return The URI of the WSDL to set
	 */
	public String getWsdlURI()
	{
		log.debug("Getting WSDL uri ...");
		return wsdlURI;
	}   


	/**
	 * Gets the SelectItems displayed in the drop-down list of
	 * available services (defined in the selected WSDL contract)
	 * 
	 * @return a list of SelectItems
	 */
	public List<SelectItem> getServiceSelectItems()
	{
		log.debug("Getting serviceSelectItems");
		return serviceSelectItems;        
	}


	/**
	 * Sets the SelectItems displayed in the drop-down list of
	 * available services (defined in the selected WSDL contract)
	 * 
	 * @param itemList - a list of SelectItems corresponding to 
	 *                   WebServices
	 */
	public void setServiceSelectItems(List<SelectItem> itemList)
	{
		log.debug("Setting serviceSelectItems");
	}        


	/**
 	 * Gets the selected WebService (i.e. a SelectItem) from the 
 	 * drop-down list of available services 
 	 * (defined in the selected WSDL contract)
     *
	 * @return the selected WebService (i.e. a SelectItem)
	 */
	public SelectItem getServiceSelectItem()
	{
		log.debug("Getting serviceSelectItem");
		if (serviceSelectItem != null) {
			log.debug(serviceSelectItem);
		}
		return serviceSelectItem;
	}
	
	
	/**
 	 * Sets the selected WebService (i.e. a SelectItem) in the 
 	 * drop-down list of available services 
 	 * (defined in the selected WSDL contract)
 	 *  
	 * @param selectItem - the selected WebService
	 */
	public void setServiceSelectItem(SelectItem selectItem)
	{
		log.debug("Setting serviceSelectItem ...");
		if (selectItem != null) {
			boolean hasChanged = false;
			if (this.serviceSelectItem == null)
				hasChanged = true;
			else if (!((String)this.serviceSelectItem.getValue()).equalsIgnoreCase((String)selectItem.getValue()))
				hasChanged = true;
			else ;
			if (hasChanged) {
				serviceSelectItem = selectItem;
				reloadOperations();
			}
			else ;
		}
		else ;
		log.debug("Setting serviceSelectItem DONE");
	}        	


	/**
 	 * Gets the selected WebService name (i.e. a String) from the 
 	 * drop-down list of available services 
 	 * (defined in the selected WSDL contract)
 	 * 
 	 * @return the selected WebService name (i.e. a String)
	 */
	public String getServiceSelectItemValue()
	{
		log.debug("Getting serviceSelectItemValue");
		if (serviceSelectItem != null) {
			if (serviceSelectItem.getValue() != null)
				return serviceSelectItem.getValue().toString();    		
		}
		return "";
	}


	/**
 	 * Sets the selected WebService name (i.e. a String) in the 
 	 * drop-down list of available services 
 	 * (defined in the selected WSDL contract)
 	 * 
 	 * @param value - the selected WebService name (i.e. a String)
	 */	
	public void setServiceSelectItemValue(String value)
	{
		log.debug("Setting serviceSelectItemValue");
		setServiceSelectItem(new SelectItem(value));
	}            


	/**
	 * reloads the list of available operations given a selected 
	 * WebService
	 */
	private void reloadOperations() {
		operationSelectItems.clear();
		operationInfos.clear();
		operationSelectItem = null;
		if (serviceSelectItem != null) {
			Iterator operationIterator = ((ServiceInfo)serviceInfos.get(serviceSelectItem.getValue())).getOperations();
			boolean firstLoop = true;    		
			while (operationIterator.hasNext()) {
				OperationInfo operationInfo = (OperationInfo)operationIterator.next();
				SelectItem operationSelectItem = new SelectItem(operationInfo.getTargetMethodName());
				Iterator operationSelectItemsIterator = operationSelectItems.iterator();
				boolean alreadyInserted = false;
				while (operationSelectItemsIterator.hasNext()) {
					if (operationSelectItem.getValue().equals(((SelectItem)operationSelectItemsIterator.next()).getValue())) {
						alreadyInserted = true;
						break;
					}	
					else;
				}
				if (!alreadyInserted)
					operationSelectItems.add(operationSelectItem);
				if (!operationInfos.containsKey(operationInfo.getTargetMethodName()))
					operationInfos.put(operationInfo.getTargetMethodName(), operationInfo);
				else ;
				if (firstLoop) {
					this.operationSelectItem = operationSelectItem;
					firstLoop = false;
				}
			}
		}
		else ;
	}
		

	/**
	 * Gets the SelectItems displayed in the drop-down list of
	 * available service operations 
	 * (defined in the selected WSDL contract for the selected WebService)
	 * 
	 * @return a list of SelectItems (corresponding to WebService operations)
	 */	
	public List<SelectItem> getOperationSelectItems() {
		return operationSelectItems;
	}
	

	/**
	 * Sets the SelectItems displayed in the drop-down list of
	 * available service operations 
	 * (defined in the selected WSDL contract for the selected WebService)
	 * 
	 * @param itemList - a list of SelectItems (corresponding to WebService operations)
	 */		
	public void setOperationSelectItems(List<SelectItem> itemList)
	{
		log.debug("Setting operationSelectItems");
	}            


	/**
 	 * Gets the selected WebService operation (i.e. a SelectItem) from the 
 	 * drop-down list of available service operations 
 	 * (defined in the selected WSDL contract for the selected WebService)
     *
	 * @return the selected WebService operation (i.e. a SelectItem)
	 */	
	public SelectItem getOperationSelectItem()
	{
		log.debug("Getting operationSelectItem - BR0");
		return operationSelectItem;
	}    

	
	/**
 	 * Sets the selected WebService operation (i.e. a SelectItem) in the 
 	 * drop-down list of available service operations 
 	 * (defined in the selected WSDL contract for the selected WebService)
     *
	 * @param selectItem - the selected WebService operation (i.e. a SelectItem)
	 */		
	public void setOperationSelectItem(SelectItem selectItem)
	{
		log.debug("Setting operationSelectItems ...");
		
		if (selectItem != null) {
			boolean hasChanged = false;
			if (this.operationSelectItem == null)
				hasChanged = true;
			else if (!((String)this.operationSelectItem.getValue()).equalsIgnoreCase((String)selectItem.getValue()))
				hasChanged = true;
			else ;
			if (hasChanged) {
				operationSelectItem = selectItem;
				//setXMLRequest();
			}
			else ;
		}
		else ;		
		
		
		operationSelectItem = selectItem;
		log.debug("Setting operationSelectItems DONE");
	}        	
	

	/**
 	 * Gets the selected WebService operation name (i.e. a String) from the 
 	 * drop-down list of available service operations
 	 * (defined in the selected WSDL contract)
 	 * 
 	 * @return the selected WebService operation name (i.e. a String)
	 */	
	public String getOperationSelectItemValue()
	{
		log.debug("Getting operationSelectItemValue");
		getOperationSelectItem();
		if (operationSelectItem != null) {
			return operationSelectItem.getValue().toString();
		}
		return "";
	}            


	/**
 	 * Sets the selected WebService operation name (i.e. a String) in the 
 	 * drop-down list of available service operations
 	 * (defined in the selected WSDL contract)
 	 * 
 	 * @param value - the selected WebService operation name (i.e. a String)
	 */		
	public void setOperationSelectItemValue(String value)
	{
		log.debug("Setting operationSelectItemValue");
		setOperationSelectItem(new SelectItem(value));
	}                


	/**
	 * gets the XML request data to be sent to the WebService endpoint
	 * 
	 * @return the XML request
	 */
	public String getXmlRequest()
	{
		log.debug("Getting xmlRequest");
		String ret = "<xml>No operation defined yet!</xml>";
		if (operationSelectItem != null) {
			if (operationInfos != null) {
				if (operationInfos.containsKey(operationSelectItem.getValue())) {
					log.debug("getXMLRequest.operationSelectItem.getValue() = " + operationSelectItem.getValue());
					log.debug("operationInfos.size = "+operationInfos.size());
					OperationInfo o = (OperationInfo)operationInfos.get(operationSelectItem.getValue());
					log.debug("TargetMethod Name = "+o.getTargetMethodName());
					ret = o.getInputMessageText();
					//ret = ((OperationInfo)operationInfos.get(operationSelectItem.getValue())).getInputMessageText();
				}	
				else 
					ret = "<xml>No operationInfos key matched!</xml>";
			}
			else 
				ret = "<xml>No operationInfos object or null!</xml>";    		
		}
		else ;
		log.debug("Getting xmlRequest - ret = " + ret + " - DONE");    		
		return ret;

	}    


	/**
	 * sets the XML request data to be sent to the WebService endpoint
	 * 
	 * @param xmlRequest - the XML request to be sent
	 */	
	public void setXmlRequest(String xmlRequest)
	{
		log.debug("Setting XML Request to: " + xmlRequest);    	
		if (operationSelectItem != null) {
			if (operationInfos != null) {
				if (operationInfos.containsKey(operationSelectItem.getValue()))
					((OperationInfo)operationInfos.get(operationSelectItem.getValue())).setInputMessageText(xmlRequest);
				else ;
			}
			else ;    		
		}
		else ;    	
		log.debug("XML Request set to: " + xmlRequest);
	}        


	/**
	 * sets the XML response data received from the WebService endpoint
	 * 
	 * @param response - the XML response
	 */	
	public void setXmlReponse(String response) {
		this.xmlResponse = response;
	}
	
	
	/** 
	 * gets the xml input template message (i.e. including "?") from the OperationInfo Bean
	 * 
	 * @return the xml request template
	 */
	public String getOperationRequestTemplate(){
		if (operationSelectItem != null) {
			if (operationInfos != null) {
				if (operationInfos.containsKey(operationSelectItem.getValue()))
					return ((OperationInfo)operationInfos.get(operationSelectItem.getValue())).getInputMessageText();
			}  		
		}
		return null;
	}
	
	
	/** 
	 * gets the xml output template message (i.e. including "?") from the OperationInfo Bean
	 * 
	 * @return the xml responds template
	 */
	public String getOperationRespondsTemplate(){
		if (operationSelectItem != null) {
			if (operationInfos != null) {
				if (operationInfos.containsKey(operationSelectItem.getValue()))
					return ((OperationInfo)operationInfos.get(operationSelectItem.getValue())).getOutputMessageText();
			}  		
		}
		return null;
	}


	/**
	 * gets the XML response data received from the WebService endpoint
	 * 
	 * @return the XML response
	 */		
	public String getXmlResponse() {
		return this.xmlResponse;
	}


	/**
	 * sets the URI pointing to the WS-I conformance check
	 * result file on the application server
	 * 
	 * @param result - the URI to be set
	 */
	public void setWsiResultURI(String result) {
		this.wsiResultURI = result;
	}


	/**
	 * gets the URI pointing to the WS-I conformance check
	 * result file on the application server
	 * 
	 * @return the URI
	 */	
	public String getWsiResultURI() {
		log.debug("Getting WSIResultURI = " + this.wsiResultURI);
		return this.wsiResultURI;
	}    


	/**
	 * tells whether the request to be sent to the WebService
	 * endpoint is not ready
	 * 
	 * @return a boolean
	 */
	public boolean isRequestNotReady()
	{
		if (getServiceSelectItemValue() != null) {    		
			if (getServiceSelectItemValue().length() > 0) {
				if (getOperationSelectItemValue() != null) {
					if (getOperationSelectItemValue().length() > 0)
						return false;
					else
						return true;
				}
				else 
					return true;
			}
			else 
				return true;
		}
		else 
			return false;
	}    


	/**
	 * Reads the WSDL and creates Service and Service Operation
	 * information.
	 * 
	 * @return a String saying either "error" or "success"
	 */
	public String analyzeWsdl()
	{
		try
		{
			log.debug("Clean up");
			this.serviceInfos.clear();
			this.operationInfos.clear();
			this.operationSelectItem = null;
			this.serviceSelectItem = null;
			this.operationSelectItems.clear();
			this.serviceSelectItems.clear();
			this.xmlResponse = "";

			// Create the in memory model of services and operations
			// defined in the current WSDL
			log.debug("WsdlURI = " + wsdlURI);
			ComponentBuilder builder = new ComponentBuilder();
			List services = builder.buildComponents(wsdlURI.toString());

			// List all the services defined in the current WSDL
			Iterator iter = services.iterator();

			boolean firstLoop = true;

			while(iter.hasNext())
			{
				// Load each service into the services combobox model
				ServiceInfo serviceInfo = (ServiceInfo)iter.next();
				SelectItem serviceSelectItem = new SelectItem(serviceInfo.getName());                       
				this.serviceSelectItems.add(serviceSelectItem);
				serviceInfos.put(serviceInfo.getName(), serviceInfo);
				if (firstLoop) {
					setServiceSelectItem(serviceSelectItem);
					//this.serviceSelectItem = serviceSelectItem;
					firstLoop = false;
				}
			}
			return "success-analyze";
		}

		catch(Exception e)
		{
			// Report the error to the user
			log.debug("NOT successfully analyzed");
			log.error(e.getMessage());
			e.printStackTrace();       
			return "error-analyze";
		}
	}


	/**
	 * Checks the WSDL whether it conforms to the latest WS-I profile or not
	 * 
	 * @return a String saying either "error" or "success"
	 */
	public String checkWsdl()
	{
		try
		{
			log.debug("Checking WSDL for WS-I compliance");

			//create working dir
			File wsiDir = new File(workDir + "/wsi");
			wsiDir.mkdirs();    	 
			log.debug("File created at: " + wsiDir.getCanonicalPath());

			//create analyzer config file
			log.debug("location of template file is: " + this.getClass().getClassLoader().getResource("resources/wsi/analyzerConfigTemplate.xml"));
			File analyzerConfigTemplate = new File(this.getClass().getClassLoader().getResource("resources/wsi/analyzerConfigTemplate.xml").toURI());
			String analyzerConfigTemplateString = readFileAsString(analyzerConfigTemplate);
			File reportFile = new File (wsiDir, "report.xml");
			analyzerConfigTemplateString = analyzerConfigTemplateString.replaceAll("@report_file_location@", reportFile.getCanonicalPath().replace('\\', '/'));
			File _checkFile = new File(this.getClass().getClassLoader().getResource("resources/wsi/profiles/SSBP10_BP11_TAD.xml").toURI());
			File checkFile = new File (wsiDir, "checkFile.xml");
			copy(_checkFile, checkFile);
			analyzerConfigTemplateString = analyzerConfigTemplateString.replaceAll("@check_file_location@", checkFile.getCanonicalPath().replace('\\', '/'));
			WSDLReaderImpl wsdlReader = new WSDLReaderImpl();
			Definition wsdlDefinition = wsdlReader.readWSDL(getWsdlURI());
			Iterator bindingsIterator = wsdlDefinition.getBindings().values().iterator();
			if (bindingsIterator.hasNext()) {
				Binding binding = (Binding)bindingsIterator.next();
				analyzerConfigTemplateString = analyzerConfigTemplateString.replaceAll("@binding_name@", binding.getQName().getLocalPart());
				analyzerConfigTemplateString = analyzerConfigTemplateString.replaceAll("@target_namespace@", binding.getQName().getNamespaceURI());
			}
			analyzerConfigTemplateString = analyzerConfigTemplateString.replaceAll("@wsdl_uri@", getWsdlURI());
			log.debug("analyzerConfigTemplateString = \n" + analyzerConfigTemplateString);
			File analyzerConfigFile = new File (wsiDir, "analyzerConfig.xml");
			BufferedWriter out = new BufferedWriter(new FileWriter(analyzerConfigFile));
			out.write(analyzerConfigTemplateString);
			out.close();
			log.debug("analyzerConfigFile created at: " + analyzerConfigFile.getCanonicalPath().replace('\\', '/'));

			//start analyze
			runWSICheck(analyzerConfigFile.getCanonicalPath().replace('\\', '/'));


			log.debug("Analyze COMPLETED - report at: " + reportFile.getCanonicalPath().replace('\\', '/'));

			File transformedReport = transformReport(reportFile);
			log.debug( "WSI Report temporarily created at [" + transformedReport.getCanonicalPath() + "]" );
			this.wsiResultURI = reportContext + transformedReport.getName();

			log.debug("Transform COMPLETED - transformed report at: " + this.wsiResultURI); 		 		

			return "success-check";
		}

		catch(Exception e)
		{
			// Report the error to the user
			log.debug("NOT successfully analyzed");
			log.error(e.getMessage());
			e.printStackTrace();       
			return "error-check";
		}
	}   


	/**
	 * Sends the built XML request to the WebService endpoint
	 * 
	 * @return a String saying either "error" or "success"
	 */
	public String sendRequest()
	{
		try {
			log.debug("Sending Request ....");
			if (operationSelectItem != null) {
				if (operationInfos != null) {
					if (operationInfos.containsKey(operationSelectItem.getValue())) {
						log.debug("Invoking WS-Operation ...");
						this.xmlResponse = WSClient.invokeOperation((OperationInfo)operationInfos.get(operationSelectItem.getValue()));
						log.debug("Invoking WS-Operation DONE");
					}	   
					else {
						log.debug("<xml>No operationInfos key matched!</xml>");
						return "error-send";
					}   
				}
				else {
					log.debug("<xml>No operationInfos object or null!</xml>");
					return "error-send";
				}   
			}
			else {
				log.debug("<xml>No operation defined yet!</xml>");
				return "error-send";
			}    
		} catch (Exception e) { 
			log.debug("excpetion during send", e);
                        log.debug(e);
                        log.debug(e.getMessage());
                        //e.printStackTrace();
			return "error-send";
		}

		return "success-send";
	}

	/** 
	 * reads the contents of a file into a String
	 * 
	 * @param filePath the name of the file to open.  
	 */ 
	private static String readFileAsString(File file)
	throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(file));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}   


	/**
	 * copies a file from one location to another
	 * @param src - the source file
	 * @param dst - the destinationfile
	 * @throws IOException
	 */
	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}    


	/**
	 * runs the WSI Check using the official WS-I Testing Tool (and runs
	 * this tool via Runtime.exec(..))
	 * @param configFile - the path to the confguration file used by the 
	 *                     official WS-I Testing Tool
	 */
	private void runWSICheck(String configFile) {
		String[] args;
		log.debug("WSI_HOME = " + System.getenv("WSI_HOME"));
		log.debug("os.name = " + System.getProperty("os.name"));
		if( System.getProperty("os.name").contains("indows"))
		{
			args = new String[5];
			args[0] = "cmd.exe";
			args[1] = "/C";
			args[2] = "%WSI_HOME%/java/bin/Analyzer.bat";
			args[3] = "-config";
			args[4] = configFile;
		}
		else
		{
			args = new String[4];
			args[0] = "sh";
			//args[1] = "-c";
			//args[1] = "$WSI_HOME/java/bin/Analyzer.sh";
			args[1] = System.getenv("WSI_HOME")+"/java/bin/Analyzer.sh";
			args[2] = "-config";
			args[3] = configFile;
		}


		log.debug("Execution stmt: " );
		for (int i= 0; i< args.length; i++) {
			log.debug(args[i] + " ");
		}
		

		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(args);
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();	

			try {
				if (proc.waitFor() != 0) {
					log.error("exit value = " + proc.exitValue());
				}
				else
					log.debug("Terminated gracefully");
			}
			catch (InterruptedException e) {
				log.error(e);
			}			  
		} catch (Exception e) {e.printStackTrace();}

	}

	
	/**
	 * For testing purposes ONLY!
	 * @param args
	 */
	public static void main (String[] args) {
		/*File f = new File("samples/log.xml");
    	try {//f.createNewFile();
    	log.debug(f.getCanonicalPath());} catch (Exception e) {e.printStackTrace();}
    	WSClientBean wscb = new WSClientBean();
        log.debug("Starting analyze ...");
        String inputArg = "-config"; 
   	    String[] args2 = {inputArg, "C:/DATA/projects/PLANETS/wsi-test-tools/java/samples/analyzerConfig.xml"};
   	    //BasicProfileAnalyzer.main(args);
   	    wscb.runWSICheck(args2);
   	    log.debug("Analyze COMPLETED");*/
		/*log.debug("Start ...");
    	String uri = "C:/DATA/projects/PLANETS/ifr_server/server/default/data/planets/pcfr/wsi/checkFile.xml";

    	org.xml.sax.InputSource inputSource = new org.xml.sax.InputSource(uri);
    	try {
    	XMLReader reader = XMLUtils.getXMLReader();

        // Set content handler to inner class
        //reader.setContentHandler(new ProfileAssertionsReaderImpl.ProfileAssertionsHandler());
    	reader.parse(inputSource);
    	} catch (Exception e) { e.printStackTrace();}*/
		System.out.println("Success");

	}


	/**
	 * transforms a WS-I XML report to html using the corresponding stylesheet  
	 * @param reportFile - the XML report file
	 * @return the transformed HTML report file
	 * @throws Exception
	 */
	private static File transformReport( File reportFile ) throws Exception
	{

		String dir = System.getenv("WSI_HOME");
		File xsltFile = new File( dir + File.separatorChar + "common" + File.separatorChar + "xsl" + 
				File.separatorChar + "report.xsl" );

		Source xmlSource = new StreamSource(reportFile);
		Source xsltSource = new StreamSource(xsltFile);

		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer trans = transFact.newTransformer(xsltSource);

		String outputFolder = reportFile.getParent();
		File output = outputFolder == null || outputFolder.trim().length() == 0 ? null : new File( outputFolder );
		File tempFile = File.createTempFile( "wsi-report", ".html", output );
		trans.transform(xmlSource, new StreamResult( tempFile ));		

		return tempFile;
	}    


	/**
	 * This class handles the runtime output of the WS-I Testing Tool
	 *  
	 * @author Markus Reis, ARC
	 */
	class StreamGobbler extends Thread
	{
		InputStream is;
		String type;
		OutputStream os;

		StreamGobbler(InputStream is, String type)
		{
			this(is, type, null);
		}
		StreamGobbler(InputStream is, String type, OutputStream redirect)
		{
			this.is = is;
			this.type = type;
			this.os = redirect;
		}

		public void run()
		{
			try
			{
				PrintWriter pw = null;
				if (os != null)
					pw = new PrintWriter(os);

				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while ( (line = br.readLine()) != null)
				{
					if (pw != null)
						pw.println(line);
					log.debug(line);	
				}
				if (pw != null)
					pw.flush();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();  
			}
		}
	}
	

	/**
	 * handles generic events
	 */
	public void processValueChange(ValueChangeEvent vce)
	{
		log.debug("Processing generic value change: " + vce.getNewValue().toString());
	}
	
	/**
	 * handles generic events occurring due to changes on the operation drop-down box
	 */	
	public void processOperationChange(ValueChangeEvent vce)
	{
		log.debug("Processing operation value change: " + vce.getNewValue().toString());
		setOperationSelectItemValue((String)vce.getNewValue());
	}

	/**
	 * handles generic events occurring due to changes on the service drop-down box
	 */		
	public void processServiceChange(ValueChangeEvent vce)
	{
		log.debug("Processing service value change: " + vce.getNewValue().toString());
		setServiceSelectItemValue((String)vce.getNewValue());
	}		

}
