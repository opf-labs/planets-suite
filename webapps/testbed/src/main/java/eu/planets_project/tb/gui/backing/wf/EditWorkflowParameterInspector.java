/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.gui.backing.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean;
import eu.planets_project.tb.gui.backing.exp.ExperimentInspector;
import eu.planets_project.tb.gui.backing.exp.NewExpWizardController;


/**
 * The backing bean for the request based add/edit workflow parameter GUI used in
 * step: 'design experiment' allows to edit/add service parameters for ONE service at a time
 * 
 * Please note: all the checks for if(this.sessBean==null) are requires, as this page is
 * called as modal panel in stage 'design experiment' therefore is already loaded when stage2 renders
 * and only gets active when also and forServiceURL is provided
 * 
 * please note: as the redirect causes the oncomplete="Richfaces.showModalPanel('configEditParamsPanel'); not 
 * to take place, we need to provide the forServiceURL within the SessionMap and not as Managed-Property
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 15.01.2010
 *
 */
public class EditWorkflowParameterInspector {
	
	private Log log = LogFactory.getLog(EditWorkflowParameterInspector.class);
	//the service this information belongs to
	private String forServiceURL;
	private String forServiceID;
	//the experiment this information belongs to
	private String experimentId;
	private EditWorkflowParameterSessionBean sessBean = null;

	public static final String EDIT_WORKFLOW_PARAM_SERURL_MAP = "edit_param_serurl_map";
	public static final String EDIT_WORKFLOW_PARAM_SERID_MAP = "edit_param_serid_map";
	private static final String EDIT_WORKFLOW_PARAM_SESSION_BEAN_MAP = "edit_wf_param_sess_beans_map";
    
    
	
	/**
	 * This constructor is called on every request for the request scoped
	 * managed bean
	 */
	public EditWorkflowParameterInspector(){
	}
	
	//<------------- start of filled by managed bean's property --------------- >
	/**
	 * The experiment id in which the edit/add param gui is taking place
	 * @return
	 */
	public String getExperimentId() {
		return experimentId;
	}

	/**
	 * The experiment id in which the edit/add param gui is taking place
	 * This element is filled as a managed-bean's property 
	 * @param experimentId
	 */
	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
		
		//set the service URL the parameters are about
		this.setForServiceURL(this.getServiceURLFromSessionMap(experimentId));
		this.setForServiceID(this.getServiceIDFromSessionMap(experimentId));
		
	    //finally init this bean
	    initBean(this.experimentId,this.forServiceURL);
	}

	/**
	 * The service's ID we're adding parameters for
	 * @return
	 */
	public String getForServiceURL() {
		return forServiceURL;
	}
	

	/**
	 * The service's Endpoint we're adding parameters for
	 * This element is filled as a managed-bean's proeprty
	 * @param forServiceID
	 */
	public void setForServiceURL(String forServiceURL) {
		this.forServiceURL = forServiceURL;
		//initBean(this.experimentId,this.forServiceURL);
	}
	
	/**
	 * The service's ID we're adding parameters for
	 * e.g. migrate1
	 * @param serviceID
	 */
	public void setForServiceID(String serviceID){
		this.forServiceID = serviceID;
	}
	
	public String getForServiceID(){
		return this.forServiceID;
	}
	
	//<------------- end of filled by managed bean's property --------------- >
	
	/**
	 * The service's name we're adding parameters for
	 * @return
	 */
	public String getSerName() {
		if((this.sessBean!=null)&&(this.sessBean.serviceDescr!=null)){
			return this.sessBean.serviceDescr.getName();
		}
		return "";
	}
	
	//I NEED TO HAND OVER ALL components that are bound by reference!
	HtmlDataTable tempTableBinding;
	
	//removed reference binding for component as this causes problems when 
	//syncing with the session scoped var
	/*public HtmlDataTable getParameterTable() {
		if(this.sessBean==null){
			return tempTableBinding;
		}
		else{
			return sessBean.parameterTable;
		}
	}
	
	public void setParameterTable(HtmlDataTable parameterTable) {
		if(this.sessBean==null){
			this.tempTableBinding = parameterTable;
			return;
		}
		this.sessBean.parameterTable = parameterTable;
	}*/

	public List<ServiceParameter> getServiceParametersToEdit() {
		if(this.sessBean!=null){
			return this.sessBean.serviceParams;
		}
		else{
			return new ArrayList<ServiceParameter>();
		}
	}
	
	/**
	 * The NEW value for the parameter that's currently being edited.
	 * @return
	 */
	public String getNewValue() {
		if(this.sessBean==null){
			return "";
		}
		return sessBean.newValue;
	}
	
	/**
	 * The NEW value for the parameter that's currently being edited.
	 * @param value
	 */
	public void setNewValue(String value) {
        if( this.sessBean == null ) return;
		this.sessBean.newValue = value;
	}
	
	/**
	 * The name of the parameter that's currently being edited.
	 * @return
	 */
	public String getParameterName() {
		if(this.sessBean==null){
			return "";
		}
		return this.sessBean.parameterName;
	}

	/**
	 * The existing value for the parameter that's currently being edited.
	 * @return
	 */
	public String getParameterValue() {
		if(this.sessBean==null){
			return "";
		}
		return this.sessBean.parameterValue;
	}
	
	/**
	 * The name of the parameter that's currently being edited.
	 * @param name
	 */
	public void setParameterName(String name) {
        if( this.sessBean == null ) return;
		this.sessBean.parameterName = name;
	}

	/**
	 * The existing value for the parameter that's currently being edited.
	 * @param value
	 */
	public void setParameterValue(String value) {
	    if( this.sessBean == null ) return;
		this.sessBean.parameterValue = value;
	}
	
	/**
	 * GUI command that's called to update the value which have been already submitted to
	 * 'newValue' for the one selected Parameter that's being edited
	 */
	public void updateParameter(ActionEvent e) {
		if (sessBean.newValue == null) {
			//TODO AL: add errorMessage for GUI component?
			log.debug("New value is null - cannot update!");
			return;
		}
		if (sessBean.newValue.equals("")) {
			//TODO AL: add errorMessage for GUI component?
			log.debug("Invalid new value - cannot update!");
			return;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("rowKey");
		if(o1==null){
			return;
		}
		int dataRow = Integer.valueOf((String)o1);
		String pn = sessBean.serviceParams.get(dataRow).getName();
		sessBean.serviceParams.remove(dataRow);
		sessBean.serviceParams.add(dataRow,
				new ServiceParameter(pn, sessBean.newValue));
	}
	
	/**
	 * GUI command that's called to remove the currently selected parameter
	 * 'newValue' for the one selected Parameter that's being edited
	 */
	public void removeParameter(ActionEvent e) {
		FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("rowKey");
		if(o1==null){
			return;
		}
		int dataRow = Integer.valueOf((String)o1);
		sessBean.serviceParams.remove(dataRow);
	}
	
	public void clearParameters() {
		this.sessBean.defaultParams.clear();
		this.sessBean.serviceParams.clear();
	}
	
	
	/**
	 * Contains the default parameters as provided through the service registry
	 * This field may not be populated for every item
	 * @return
	 */
	public List<ServiceParameter> getDefaultServiceParameters() {
		if(this.sessBean==null){
			return new ArrayList<ServiceParameter>();
		}
		return this.sessBean.getDefaultServiceParameters();
	}

	
	public void addParameter(ServiceParameter par) {
		//ServiceParameters names need to be used uniquely - decide update or new
		ServiceParameter spUpdate = this.getServiceParamContained(par.getName());
		if(spUpdate!=null){
			//delete
			this.removeParameter(spUpdate);
		}
		//finally add add
		this.sessBean.serviceParams.add(par);
	}
	
	public void addParameter(ActionEvent event) {
		if (sessBean.parameterName.equals("")) {
			//TODO AL: create ErrorMessage for GUI Component
			log.debug("Unable to create new parameter: name undefined!");
			return;
		}
		if (sessBean.parameterValue.equals("")) {
			//TODO AL: create ErrorMessage for GUI Component
			log.debug("Unable to create new parameter: value undefined!");
			return;
		}
		this.addParameter(new ServiceParameter(sessBean.parameterName, sessBean.parameterValue));
		this.sessBean.parameterName ="";
		this.sessBean.parameterValue ="";
	}

	public void removeParameter(ServiceParameter par) {
		this.sessBean.serviceParams.remove(par);
	}
	
	/**
	 * Triggers the update parameter information process from the edit service parameters screen to update
	 * the underlying experiment type specific backing bean with the new values.
	 */
	public void commandUpdateServiceBeanFromEditParamScreen(ActionEvent evt){
		//writes values back - finishes this bean
		this.commandUpdateWorkflowParameters();
		this.removeEditWFParamBeanInSession();
		//finally redirect to exp_stage2?eid without the serURL param - rerenders the entire page
		NewExpWizardController.redirectToExpStage(Long.parseLong(this.getExperimentId()), 2);
	}
	
	/**
	  * Provides information for the auto-complete form on the param screen.
	  * @param query
	  * @return
	 */
	public List<ServiceParameter> suggestMatchingDefaultParamsByName(Object query) {
       if( query == null) return null;
       // look for matching default params
       String q = (String) query;
       // Filter this into a list of matching parameters:
       ArrayList<ServiceParameter> matches = new ArrayList<ServiceParameter>();
       for(ServiceParameter sp :  this.getDefaultServiceParameters()){
           if( sp.getName().startsWith(q) ||
           	sp.getName().contains(q)) {
                 matches.add(sp);
           }
       }
       return matches;
   }
	
	
    
	
	/**
	 * Checks if a given ServiceParameterName is already contained in the list
	 * off added ServiceParameters
	 * @param paramName
	 * @return
	 */
	private ServiceParameter getServiceParamContained(String paramName){
		for(ServiceParameter sp : this.sessBean.serviceParams){
			if(sp.getName().equals(paramName)){
				return sp;
			}
		}
		return null;
	}
	
	/**
	 * Fetches the requested experiment and pulls out the already existing
	 * service parameters for the specified serviceID from the underlying
	 * experiment type specific backing bean
	 * @param experimentID
	 */
	private void initBean(String experimentID, String serviceID){
		if((experimentID != null)&&(!experimentID.equals(""))&&(serviceID !=null )&&(!serviceID.equals(""))){
			
			//check if this was called the first time
			if(this.getEditWFParamBeanInSession()==null){
				EditWorkflowParameterSessionBean editParamSessBean = new EditWorkflowParameterSessionBean(this);
				this.updateEditWFParamBeanInSession(editParamSessBean);
				this.sessBean = editParamSessBean;
			}
			//or if the editParam is already in progress
			else{
				this.sessBean = this.getEditWFParamBeanInSession();
			}
		}
		//else: not all required parameters have been passed along yet
	}
	
	
	
	/**
	 * stores/updates the corresponding session bean
	 * @param editParamSessBean
	 */
	private void updateEditWFParamBeanInSession(EditWorkflowParameterSessionBean editParamSessBean){
		getWFParamBeansSessionMap().put(this.generateSessionBeanKey(), editParamSessBean);
	}
	
	/**
	 * removes the corresponding session bean
	 */
	public void removeEditWFParamBeanInSession(){
		//removes the backing bean
		getWFParamBeansSessionMap().remove(this.generateSessionBeanKey());
		//removes the reference to the serviceURL we're using
		this.removeServiceInformationFromSessionMap();
	}
	
	/**
	 * Fetches the corresponding session bean 
	 * @return
	 */
	private EditWorkflowParameterSessionBean getEditWFParamBeanInSession(){
		return getWFParamBeansSessionMap().get(this.generateSessionBeanKey());
	}
	
	/**
	 * Fetches the map that stores all EditWorkflowParametersSessionBeans from the session
	 * and create one if it does not already exist.
	 * @return
	 */
	private HashMap<String,EditWorkflowParameterSessionBean> getWFParamBeansSessionMap(){
		FacesContext ctx = FacesContext.getCurrentInstance();
        Object o = ctx.getExternalContext().getSessionMap().get(EDIT_WORKFLOW_PARAM_SESSION_BEAN_MAP);
        HashMap<String,EditWorkflowParameterSessionBean> editWFSessionBeanMap;
        
        if(o==null){
        	// no map available in the session
        	editWFSessionBeanMap = new HashMap<String,EditWorkflowParameterSessionBean>();
        	ctx.getExternalContext().getSessionMap().put(EDIT_WORKFLOW_PARAM_SESSION_BEAN_MAP, editWFSessionBeanMap);
        }else{
        	editWFSessionBeanMap = (HashMap<String,EditWorkflowParameterSessionBean>)o;
        }
        return editWFSessionBeanMap;
	}
	
	/**
	 * the bean's input parameter that's passed within the SessionMap
	 * @param expID
	 * @return
	 */
	private String getServiceURLFromSessionMap(String expID){
		//fetch the serviceURL - due to the modal panel we can't use a managed-property and irect
		FacesContext ctx = FacesContext.getCurrentInstance();
		Object o = ctx.getExternalContext().getSessionMap().get(EDIT_WORKFLOW_PARAM_SERURL_MAP);
	    if(o!=null){
	    	HashMap<String,String> editParamSerURLMap = (HashMap<String,String>)o;
	    	return editParamSerURLMap.get(experimentId);
	    }else{
	    	return null;
	    }
	}
	
	/**
	 * the bean's input parameter that's passed within the SessionMap
	 * @param expID
	 * @return
	 */
	private String getServiceIDFromSessionMap(String expID){
		//fetch the serviceURL - due to the modal panel we can't use a managed-property and irect
		FacesContext ctx = FacesContext.getCurrentInstance();
		Object o = ctx.getExternalContext().getSessionMap().get(EDIT_WORKFLOW_PARAM_SERID_MAP);
	    if(o!=null){
	    	HashMap<String,String> editParamSerIDMap = (HashMap<String,String>)o;
	    	return editParamSerIDMap.get(experimentId);
	    }else{
	    	return null;
	    }
	}
	
	private void removeServiceInformationFromSessionMap(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		Object o = ctx.getExternalContext().getSessionMap().get(EDIT_WORKFLOW_PARAM_SERURL_MAP);
	    if(o!=null){
	    	HashMap<String,String> editParamSerURLMap = (HashMap<String,String>)o;
	    	editParamSerURLMap.remove(this.experimentId);
	    }
		Object o2 = ctx.getExternalContext().getSessionMap().get(EDIT_WORKFLOW_PARAM_SERID_MAP);
	    if(o2!=null){
	    	HashMap<String,String> editParamSerIDMap = (HashMap<String,String>)o;
	    	editParamSerIDMap.remove(this.experimentId);
	    }
	}
	
	/**
	 * A key for identifying this information in the map of EditWorkflowParameterSessionBeans
	 * @return
	 */
	private String generateSessionBeanKey(){
		//key is assembled with expID and serviceURL
		return "expID:"+this.getExperimentId()+"servURL:"+this.getForServiceURL()+"servID:"+this.forServiceID;
	}
	
	
	/**
	 * calls the update parameter method for the underlying experiment type specific bean
	 */
	public void commandUpdateWorkflowParameters(){
		ExperimentInspector expInspector = new ExperimentInspector();
		expInspector.setExperimentId(this.getExperimentId());
		ExperimentBean expBean = expInspector.getExperimentBean();
		ExpTypeBackingBean exptypeBean = ExpTypeBackingBean.getExpTypeBean(expBean.getEtype());
		
		Map<String,List<Parameter>> ret = new HashMap<String,List<Parameter>>();
		if(this.forServiceID!=null){
			//using a different serviceID than the service's url
			ret.put(this.forServiceID, sessBean.convertServiceParameterList(this.sessBean.serviceParams));
		}
		else{
			//just using the serviceURL as identifier
			ret.put(this.forServiceURL, sessBean.convertServiceParameterList(this.sessBean.serviceParams));
		}
		
		exptypeBean.setWorkflowParameters(ret);
	}
	
	
	public class ServiceParameter implements Cloneable{
		private String name;
		private String value;
		private String description;

		public ServiceParameter() {
		}

		public ServiceParameter(String n, String v) {
			this.name = n;
			this.value = v;
		}
		
		public ServiceParameter(String n, String v, String d) {
			this.name = n;
			this.value = v;
			this.description = d;
		}

		public String getName() {
			return this.name;
		}

		public String getValue() {
			return this.value;
		}
		
		public String getDescription() {
			return this.description;
		}

		public void setDescription(String d) {
			this.description = d;
		}

		public void setName(String n) {
			this.name = n;
		}

		public void setValue(String v) {
			this.value = v;
		}
		
		public ServiceParameter clone(){
			try {
				return (ServiceParameter) super.clone();
			} catch (CloneNotSupportedException e) {
				log.error("Clone not supported for ServiceParameter "+e);
				return null;
			}
		}
	}

}


