package eu.planets_project.tb.gui.backing.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;

import org.apache.myfaces.custom.fileupload.HtmlInputFileUploadTag;

import eu.planets_project.tb.api.services.ServiceRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.gui.backing.FileUploadBean;
import eu.planets_project.tb.gui.backing.Manager;
import eu.planets_project.tb.gui.backing.admin.wsclient.faces.WSClientBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.services.ServiceRegistryImpl;


/**
 * This bean implements the following logic:
 *    - renderer for all registered TestbedServices and their metadata
 *    - queries Testbed's service registry
 *
 * @author Andrew Lindley, ARC
 */

public class TBServiceRenderer implements ValueChangeListener {
	
	private List<SelectItem> lServiceSelectItems = new Vector<SelectItem>();
	private List<SelectItem> lOperationSelectItems = new Vector<SelectItem>();
	private SelectItem serviceSelectItem;
	private SelectItem operationSelectItem;

	//the selectChoices
	private Map<String,Boolean> cboxes = new HashMap<String,Boolean>();
	private boolean allCboxesSelected = false;
	
	public TBServiceRenderer(){
		
		//fill the cboxes list:
		cboxes.put("endpoint",true);
		cboxes.put("uri",false);
		
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
		ServiceRegistry registry = ServiceRegistryImpl.getInstance();
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
		ServiceRegistry registry = ServiceRegistryImpl.getInstance();
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
		ServiceRegistry registry = ServiceRegistryImpl.getInstance();
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
	public Map<String,Boolean> getAllCheckboxes(){
		//return this.cboxes;
		return this.cboxes;
	}
	
	public boolean getAllCboxesSelected(){
		return this.allCboxesSelected;
	}
	
	public void setAllCBoxesSelected(boolean b){
		this.allCboxesSelected = b;
	}
	
	/**
	 * Select or deselect all checkboxes
	 */
	public void processSelAllBoxesChange(ValueChangeEvent vce){
		
		this.allCboxesSelected = (Boolean)vce.getNewValue();

		Iterator<String> itValues = this.cboxes.keySet().iterator();
		while(itValues.hasNext()){
			this.cboxes.put(itValues.next(), this.allCboxesSelected);
		}
	}

}
