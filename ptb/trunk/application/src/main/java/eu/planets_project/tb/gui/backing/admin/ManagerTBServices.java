package eu.planets_project.tb.gui.backing.admin;

import java.io.ByteArrayOutputStream;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFileDefaultMemoryImpl;

import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.api.services.util.ServiceTemplateExporter;
import eu.planets_project.tb.api.services.util.ServiceTemplateImporter;
import eu.planets_project.tb.gui.backing.ServiceTemplateBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;
import eu.planets_project.tb.impl.services.util.ServiceTemplateExporterImpl;
import eu.planets_project.tb.impl.services.util.ServiceTemplateImporterImpl;


/**
 * This bean implements the following logic: controller + JSF backing
 *   (backing bean is used for render and update mode)
 *   a)  
 *    - renderer for all registered TestbedServices and their metadata
 *    - queries Testbed's service registry
 *    - deletes existing entries from the TBServiceRegistry
 *	 b)
 *    - import and export service templates from/to XML from/into the application
 *
 * @author Andrew Lindley, ARC
 *
 */

public class ManagerTBServices implements ValueChangeListener{
	
	private Log log = LogFactory.getLog(ManagerTBServices.class);
	private List<SelectItem> lServiceSelectItems = new Vector<SelectItem>();
	private List<SelectItem> lOperationSelectItems = new Vector<SelectItem>();
	private SelectItem serviceSelectItem;
	private SelectItem operationSelectItem;

	//the selected rendering choices
	private Map<String,HtmlSelectBooleanCheckbox> cboxes = new HashMap<String,HtmlSelectBooleanCheckbox>();
	private HtmlSelectBooleanCheckbox cbx_endpoint = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_uri = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_wsdlcontent = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_description = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_tags = new HtmlSelectBooleanCheckbox();
	
	private HtmlSelectBooleanCheckbox cbx_serOpType = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_xmlRequtemplate = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_xPath = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_RequFilesNr = new HtmlSelectBooleanCheckbox();
	private HtmlSelectBooleanCheckbox cbx_outputType = new HtmlSelectBooleanCheckbox();
	private boolean allCboxesSelected = false;
	
	
	public ManagerTBServices(){
			
		//set all service rendered checkboxes
		cbx_endpoint.setSelected(true);
		cbx_uri.setSelected(true);
		cbx_wsdlcontent.setSelected(false);
		cbx_description.setSelected(false);
		cbx_tags.setSelected(false);
		
		//set all operation rendered checkboxes
		cbx_serOpType.setSelected(true);
		cbx_xmlRequtemplate.setSelected(false);
		cbx_xPath.setSelected(false);
		cbx_RequFilesNr.setSelected(false);
		cbx_outputType.setSelected(false);
		
		//add rendered checkboxes
		cboxes.put("endpoint", cbx_endpoint);
		cboxes.put("uri",cbx_uri);
		cboxes.put("wsdlcontent", cbx_wsdlcontent);
		cboxes.put("description",  cbx_description);
		cboxes.put("tags", cbx_tags);
		
		cboxes.put("xmltemplate",cbx_xmlRequtemplate);
		cboxes.put("xpath",cbx_xPath);
		cboxes.put("NrOfFiles", cbx_RequFilesNr);
		cboxes.put("operationType", cbx_serOpType);
		cboxes.put("outputType", cbx_outputType);
		
		//query the service registry and load all available services
		this.loadServices();
		if(this.serviceSelectItem!=null)
			this.loadOperations(this.serviceSelectItem.getValue().toString());
	}
	
	/**
 	 * Gets the selected WebService operation (i.e. a SelectItem) from the 
 	 * drop-down list of available testbed service operations 
 	 * (registered within the TB Service Registry by using the admin interface)
     *
	 * @return the selected WebService operation (i.e. a SelectItem)
	 */	
	public SelectItem getOperationSelectItem()
	{
		return operationSelectItem;
	}    

	
	/**
 	 * Sets the selected WebService operation (i.e. a SelectItem) in the 
 	 * drop-down list of available service operations 
 	 * (registered within the TB Service Registry by using the admin interface)
     *
	 * @param selectItem - the selected WebService operation (i.e. a SelectItem)
	 */		
	public void setOperationSelectItem(SelectItem selectItem)
	{
		if (selectItem != null) {
			boolean hasChanged = false;
			if (this.operationSelectItem == null)
				hasChanged = true;
			else if (!((String)this.operationSelectItem.getValue()).equalsIgnoreCase((String)selectItem.getValue()))
				hasChanged = true;
			else ;
			if (hasChanged) {
				operationSelectItem = selectItem;
			}
			else ;
		}
		else ;		
		operationSelectItem = selectItem;
	}        	
	

	/**
 	 * Gets the selected WebService operation name (i.e. a String) from the 
 	 * drop-down list of available service operations
 	 * 
 	 * @return the selected WebService operation name (i.e. a String)
	 */	
	public String getOperationSelectItemValue()
	{
		if (getOperationSelectItem() != null) {
			return getOperationSelectItem().getValue().toString();
		}
		return "";
	}            


	/**
 	 * Sets the selected WebService operation name (i.e. a String) in the 
 	 * drop-down list of available service operations
 	 * 
 	 * @param value - the selected WebService operation name (i.e. a String)
	 */		
	public void setOperationSelectItemValue(String value)
	{
		setOperationSelectItem(new SelectItem(value));
	} 
	
	/**
	 * Gets a List of SelectItemss to be displayed in the drop-down list of
	 * available service operations 
	 * 
	 * @return a list of SelectItems of operations
	 */	
	public List<SelectItem> getOperationSelectItems() {
		return lOperationSelectItems;
	}
	
	
	public void setOperationSelectItems(List<SelectItem> itemList)
	{
		//Just required for syntax reasons
	}  
	
	
	/**
 	 * Gets the selected WebService label:name+value:uid (i.e. a SelectItem) from the 
 	 * drop-down list of available and registered testbed services 
 	 * (registered within the TB Service Registry by using the admin interface)
     *
	 * @return the selected WebService operation (i.e. a SelectItem)
	 */	
	public SelectItem getServiceSelectItem()
	{
		return serviceSelectItem;
	}    

	
	/**
 	 * Sets the selected WebService ID (i.e. a SelectItem) from the 
 	 * drop-down list of available and registered testbed service names+values
 	 * (registered within the TB Service Registry by using the admin interface)
     *
	 * @return the selected WebService operation (i.e. a SelectItem)
	 */		
	public void setServiceSelectItem(SelectItem selectItem)
	{
		if (selectItem != null) {
			boolean hasChanged = false;
			if (this.serviceSelectItem == null)
				hasChanged = true;
			else if (!((String)this.serviceSelectItem.getValue()).equalsIgnoreCase((String)selectItem.getValue()))
				hasChanged = true;
			else ;
			if (hasChanged) {
				this.serviceSelectItem = selectItem;
				reloadOperations();
			}
			else ;
		}
		else ;
	}        	
	

	/**
 	 * Gets the selected WebService value (i.e. its ServiceID) from the 
 	 * drop-down list of available services
 	 * 
 	 * @return the selected WebService ID (i.e. a String)
	 */	
	public String getServiceSelectItemValue()
	{
		if (getServiceSelectItem() != null) {
			return getServiceSelectItem().getValue().toString();
		}
		return "";
	}      


	/**
 	 * Sets the selected WebService value (i.e. its ServiceID) in the 
 	 * drop-down list of available services
 	 * 
 	 * @param value - the selected WebService name (i.e. a String)
	 */		
	public void setServiceSelectItemValue(String value)
	{
		if((this.lServiceSelectItems!=null)&&(this.lServiceSelectItems.size()>0)){
			//iterate over the list of available Items and pick the one to set
			Iterator<SelectItem> it = this.lServiceSelectItems.iterator();
			while(it.hasNext()){
				SelectItem item = it.next();
				if(item.getValue().equals(value)){
					//we've found our item - note only one: value is the ID!
					setServiceSelectItem(item);
				}
			}
		}
		//setServiceSelectItem(new SelectItem(value));
	} 
	
	/**
	 * Gets a List of SelectItemss to be displayed in the drop-down list of
	 * available service names 
	 * 
	 * @return a list of SelectItems of service names
	 */	
	public List<SelectItem> getServiceSelectItems() {
		return this.lServiceSelectItems;
	}
	
	
	public void setServiceSelectItems(List<SelectItem> itemList)
	{
		//Just required for syntax reasons
	}  
	
	
	/**
	 * reloads the list of available operations given a selected service
	 */
	private void reloadOperations() {
		loadOperations(this.serviceSelectItem.getValue().toString());
	}
	
	/**
	 * Event is triggered when a service selection is changed within the gui and its 
	 * operations shall be loaded and displayed.
	 * @return
	 */
	public void processServiceChange(ValueChangeEvent vce){
		//fetches the new value (i.e. the ID) and queries for all operations
		this.loadOperations(((String)vce.getNewValue()));
	}
	
	/**
	 * Event is triggered when selected operationname is changed within the gui
	 * @return
	 */
	public void processOperationChange(ValueChangeEvent vce){
		//processOperationChange
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.event.ValueChangeListener#processValueChange(javax.faces.event.ValueChangeEvent)
	 */
	public void processValueChange(ValueChangeEvent vce){
		//handles generic events and must be implemented
	}
	
	/**
	 * query the testbed's service registry for all available service names
	 * annd fill the gui's rendering elements with this information
	 */
	private void loadServices(){
		this.lServiceSelectItems = new Vector<SelectItem>();
		ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
		if(registry.getAllServices()!=null){
			Iterator<TestbedServiceTemplate> itServices = registry.getAllServices().iterator();
			
			int count = 0;
			while(itServices.hasNext()){
				TestbedServiceTemplate service = itServices.next();
				//adds a SelectItem with value=serviceID label=serviceName
				SelectItem item = new SelectItem(service.getUUID(),service.getName());
				this.lServiceSelectItems.add(item);
				if(count==0)
					this.serviceSelectItem = item;
			}
		
		}
	}
	
	/**
	 * Query the selected service for its registered operation names and fill the
	 * gui's rendering elements with this information
	 * @param serviceID
	 */
	private void loadOperations(String serviceID){
		this.lOperationSelectItems = new Vector<SelectItem>();
		ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
		if((registry.getAllServiceUUIDs()!=null)&&(registry.getAllServiceUUIDs().contains(serviceID))){
			TestbedServiceTemplate s1 = registry.getServiceByID(serviceID);
			Iterator<String> itOps = s1.getAllServiceOperationNames().iterator();
			
			int count = 0;
			while(itOps.hasNext()){
				//adds a SelectItem with value=operationName
				SelectItem item = new SelectItem(itOps.next());
				this.lOperationSelectItems.add(item);
				if(count==0)
					this.operationSelectItem = item;
			}
		}
	}
	
	/**
	 * Just needs to be there for JSF reasons
	 * @param s
	 */
	public void setTBService(TestbedServiceTemplate s){
		//just needs to be there
	}
	
	/**
	 * Used to hand the selected TBService over to the GUI for extracting and displaying its metadata
	 * Could also be used to hand over the selected TestbedService object to further components
	 * @return
	 */
	public TestbedServiceTemplate getTBService(){
		//query serviceRegistry for the selected TestbedService Object
		ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
		if(this.serviceSelectItem!=null){
			//value stores the service's UUID; service may also be null
			TestbedServiceTemplate service = registry.getServiceByID(this.serviceSelectItem.getValue().toString());
		return service;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns true if the TestbedService has already been selected. Used to decide
	 * if certain gui elements are rendered or not.
	 * @return
	 */
	public boolean isTBServiceSelected(){
		if(this.getTBService()!=null){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the TestbedServiceOperation has already been selected. Used to decide
	 * if certain gui elements are rendered or not.
	 * @return
	 */
	public boolean isTBServiceOperationSelected(){
		if(this.getServiceOperation()!=null){
			return true;
		}
		return false;
	}
	
	/**
	 * Used to hand the selected TBService operation over to the GUI for extracting and displaying its metadata
	 * @return
	 */
	public ServiceOperation getServiceOperation(){
		if(this.getTBService()!=null){
			TestbedServiceTemplate tbService = this.getTBService();
			return tbService.getServiceOperation(this.getOperationSelectItemValue());
		}
		else{
			return null;
		}
	}
	
	/**
	 * Just needs to be there for JSF reasons
	 * @param s
	 */
	public void setServiceOperation(ServiceOperation op){
		//just needs to be there
	}

	
	/**
	 * Returns a list of all checkboxes in the stage of browsing service+operation
	 * metadata. The Boolean attributes are directly modified by the GUI
	 * @return
	 */
	public Map<String,HtmlSelectBooleanCheckbox> getAllCheckboxes(){
		return this.cboxes;
	}
	

	public boolean getAllCboxesSelected(){
		return this.allCboxesSelected;
	}
	
	public void setAllCBoxesSelected(boolean b){
		this.allCboxesSelected = b;
	}
	
	/**
	 * Select all or select none of the checkboxes
	 * @return
	 */
	public void processSelAllBoxesChange(ValueChangeEvent vce){
		
		this.setAllCBoxesSelected((Boolean)vce.getNewValue());

		Iterator<String> itValues = this.cboxes.keySet().iterator();
		while(itValues.hasNext()){
			String key = itValues.next();
			HtmlSelectBooleanCheckbox cbx = this.cboxes.get(key);
			cbx.setSelected(this.allCboxesSelected);
		}
	}
	
	/**
	 * Returns a list to get a printable form of all tags and values for rendering
	 * @return
	 */
	public List<String> getTagsForSelectedService(){
		
		if(this.getTBService()!=null){
			TestbedServiceTemplate template = this.getTBService();
			//get all tags that have been registered for this service
			Iterator<ServiceTag> itTags= template.getAllTags().iterator();
			
			List<String> ret = new Vector<String>();
			while(itTags.hasNext()){
				ServiceTag tag = itTags.next();
				ret.add(tag.getName()+"="+tag.getValue());
			}
			return ret;
		}
		else{
			return new Vector<String>();
		}
	}
	
	
	/**
	 * commands for the "delete" mode of this bean. Takes the selected service
	 * and removes it (as well as all of its operations) from the TBServiceRegistry
	 * @return
	 */
	public String command_deleteSelectedService(){
		ServiceTemplateRegistry registry = ServiceTemplateRegistryImpl.getInstance();
		if(this.getTBService()!=null){
			//delete the service
			registry.removeService(this.getTBService());
			
			//reload this bean's entries
			this.loadServices();
			if(this.serviceSelectItem!=null)
				this.loadOperations(this.serviceSelectItem.getValue().toString());
		}
		
		return "reload-page";
	}
	
	/**
	 * commands for the "delete" mode of this bean. Takes the selected operation
	 * and removes it from the selected TestbedService
	 * @return
	 */
	public String command_deleteSelectedOperation(){
		if((this.getTBService()!=null)&&(this.getServiceOperation()!=null)){
			
			//check if this is the last operations that's left, if yes also delete the Service.
			boolean bLast = (this.getTBService().getAllServiceOperationNames().size()>1)?false:true;
			
			if(bLast){
				//delete service as well
				this.command_deleteSelectedService();
			}
			else{
				//delte operation
				this.getTBService().removeServiceOperation(
					this.getServiceOperation().getName()
				);
			}
			
			//reload this bean's entries
			if(this.serviceSelectItem!=null)
				this.loadOperations(this.serviceSelectItem.getValue().toString());
		}
		
		return "reload-page";
	}
	
	/**
	 * actionListener for the "browse" mode of this bean. Takes the selected service template,
	 * creates a downloadable config file and offers it a file-download within the browser.
	 * @return
	 */
	public void exportConfigToFile(ActionEvent event){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    //Listener is creating a service template config 
	    HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();   
	    
	    try {
	    	
	    	//loading the template to export
		    ServiceTemplateExporter templateExporter = new ServiceTemplateExporterImpl();
		    baos = templateExporter.getExportAsStream(this.getTBService());
	
		    String format = "yyyy-MM-dd_HH-mm-ss";
			String date = doFormat(format, new GregorianCalendar());
		    
		    response.setContentType("application/xml");     
		    response.setHeader("Content-Disposition", "attachment; filename=testbed_service_template-" + getTBService().getName()+ date + ".xml");
		    response.setHeader("Cache-Control", "no-cache");  
		    response.setContentLength(baos.size());
		    
		    ServletOutputStream sos = response.getOutputStream();
	    	
	    	
	    	baos.writeTo(sos);
	    	sos.flush();
	    	
		} catch (Exception e) {
			// TODO: add exception to log
		}
	    
		FacesContext faces = FacesContext.getCurrentInstance();
	    faces.responseComplete();
	}
	
	
	   /**
     * Formats a gregorian calendar according to a specified format input schema
     * @param format
     * @param gc
     * @return
     */
    private String doFormat(String format, Calendar gc){
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	FieldPosition fpos = new FieldPosition(0);

    	StringBuffer b = new StringBuffer();
    	StringBuffer sb = sdf.format(gc.getTime(), b, fpos);

    	return sb.toString();
    }
    
    //controller for handling import of config files
    
    private UploadedFileDefaultMemoryImpl configFile;
    public void setUploadedConfigFile(UploadedFileDefaultMemoryImpl file){
    	log.debug("uploading service config template xml...");
    	this.configFile = file;
    }
    
    public UploadedFileDefaultMemoryImpl getUploadedConfigFile(){
    	return this.configFile;
    }
    
    private ServiceTemplateImporter importer = null;
    public String command_ParseUploadedConfigFile()
    {
       try {
			 java.io.InputStream confFile = this.configFile.getInputStream();
			 importer = new ServiceTemplateImporterImpl(confFile);
			 TestbedServiceTemplate template = importer.createTemplate();

			 ServiceTemplateBrowser serTempBrowser = (ServiceTemplateBrowser)JSFUtil.getManagedObject("ServiceTemplateBrowser");
			 serTempBrowser.loadTreeDataFromImportedServiceTemplate(template);
			 
		} catch (Exception e) {
			this.configFile = null;
			importer = null;
	        FacesMessage fmsg = new FacesMessage();
	        fmsg.setDetail("The provided file is not compliant with this version");
	        fmsg.setSummary("The provided file is not compliant with this version");
	        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ctx.addMessage("formUploadServiceTemplate:configfileupload",fmsg); 
	        log.debug("The provided file is not compliant with this version");
		}
        return "reload-page";
    }
    
    /**
     * Checks if a service configuration file has been uploaded and should therefore
     * be displayed.
     * @return not boolean as this is passed as ui:param
     */
    public String getIsServiceConfigRendered(){
    	if ((this.configFile!=null)&&(this.configFile.getSize()>0))
    		return "true";
    	return "false";
    }
    
    public void setIsServiceConfigRendered(String value){
    	//
    }
    
    /**
     * Finally imports the provided configuration as service template and 
     * registers it within the registry
     * @return
     */
    public String command_registerUploadedTemplate(){
    	if(importer!=null){
    		try {
    			//register the template
				importer.createAndRegisterTemplate();
				
				FacesMessage fmsg = new FacesMessage();
		        fmsg.setDetail("service configuration imported successfully!");
		        fmsg.setSummary("service configuration imported successfully!");
		        fmsg.setSeverity(FacesMessage.SEVERITY_INFO);
		        FacesContext ctx = FacesContext.getCurrentInstance();
		        ctx.addMessage("formUploadServiceTemplate:configfileupload",fmsg); 
				
			} catch (Exception e) {
				this.configFile = null;
				importer = null;
		        FacesMessage fmsg = new FacesMessage();
		        fmsg.setDetail("The provided file is not compliant for this version");
		        fmsg.setSummary("The provided file is not compliant for this version");
		        fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
		        FacesContext ctx = FacesContext.getCurrentInstance();
		        ctx.addMessage("formUploadServiceTemplate:configfileupload",fmsg); 
		        log.error(e.toString());
			}
    	}
    	return "reload-page";
    }
    
}
