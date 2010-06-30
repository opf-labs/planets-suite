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

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean;
import eu.planets_project.tb.gui.backing.wf.EditWorkflowParameterInspector.ServiceParameter;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * A session bean that's backing the request scoped EditWorkflowParameterInspector
 * as not all temp information that hasn't been submitted to the ExpTypeBackingBean 
 * can be dragged alon as request parameters.
 * a) The bean is created when starting the edit procedure and dropped when either cancel or submit are called
 * b) if null then information is reinited from the ExpTypeBackingBean
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 20.01.2010
 *
 */
public class EditWorkflowParameterSessionBean {
	
	private Log log = LogFactory.getLog(EditWorkflowParameterSessionBean.class);
	private EditWorkflowParameterInspector reqParamInspector;
	//for experimentID
	@SuppressWarnings("unused")
	private String sExpID = null;
	private String forServiceURL = null;
	private String forServiceID = null;
	//holds values for
	public ServiceDescription serviceDescr;
	protected ServiceRegistry registry = ServiceRegistryFactory.getServiceRegistry();
	//service's parameters and default parameters
    public List<ServiceParameter> serviceParams = new ArrayList<ServiceParameter>();
    public List<ServiceParameter> defaultParams = new ArrayList<ServiceParameter>();
	//only use this for the creation of (inner class) ServiceParameters
    
    public String newValue = "";
	public String parameterName = "";
	public String parameterValue = "";
    
	public EditWorkflowParameterSessionBean(EditWorkflowParameterInspector reqParamInspector){
		this.reqParamInspector= reqParamInspector;
		sExpID = reqParamInspector.getExperimentId(); 
		forServiceURL = reqParamInspector.getForServiceURL();
		forServiceID = reqParamInspector.getForServiceID();
		
		//1.init default parameters for service
		this.initDefaultParametersFromSerRegistry();
		
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		ExpTypeBackingBean exptypeBean = ExpTypeBackingBean.getExpTypeBean(expBean.getEtype());
		
		//2. fill in existing parameters - handed over by the calling expType
		Map<String, List<Parameter>> existingServiceParamsByService = exptypeBean.getWorkflowParameters();
		if(existingServiceParamsByService!=null){
			//add existing parameters to this bean
			if(this.forServiceID!=null){
				//using a different serviceID than the service's url
				this.serviceParams = convertParameterList(existingServiceParamsByService.get(forServiceID));
			}
			else{
				//just using the serviceURL as identifier
				this.serviceParams = convertParameterList(existingServiceParamsByService.get(forServiceURL));
			}
		}
	}
	
	
	private void initDefaultParametersFromSerRegistry(){
    	try{
    		this.serviceDescr = getServiceDescirptionFromServiceRegistry(this.forServiceURL);
    		String serType = serviceDescr.getType();
			//get default parameters for Service
			List<Parameter> pList = serviceDescr.getParameters();
			if (pList != null) {
				Iterator<Parameter> it = pList.iterator();
				while (it.hasNext()) {
					Parameter par = it.next();
					ServiceParameter spar = reqParamInspector.new ServiceParameter(par
							.getName(), par.getValue(), par.getDescription());
					
					//add the default params to the bean
					this.addDefaultParameter(spar);
				}
			} else {
				log.info("Service: " + serviceDescr.getName() +" has no default parameters.");
			}

			//FIXME: dirty solution. Offer the migrate_from and migrate_to parameters for type 'Migrate' service 
			//TODO offer a pull down list for mime-types
			if(serType.endsWith(Migrate.NAME)){
				if((!this.getDefaultServiceParameters().contains(WorkflowTemplate.SER_PARAM_MIGRATE_FROM))){
					ServiceParameter spar = reqParamInspector.new ServiceParameter(WorkflowTemplate.SER_PARAM_MIGRATE_FROM,"planets:fmt/ext/png");
					this.addDefaultParameter(spar);
				}
				if((!this.getDefaultServiceParameters().contains(WorkflowTemplate.SER_PARAM_MIGRATE_TO))){
					ServiceParameter spar = reqParamInspector.new ServiceParameter(WorkflowTemplate.SER_PARAM_MIGRATE_TO,"planets:fmt/ext/png");
					this.addDefaultParameter(spar);
				}
			}

    	}catch(Exception e){
			log.debug("unable to load default parameters for "+this.forServiceURL+" "+e);
    	}
    }
	
	private void addDefaultParameter(ServiceParameter param){
		this.getDefaultServiceParameters().add(param);
	}
	
	public List<ServiceParameter> getDefaultServiceParameters() {
		return this.defaultParams;
	}
	
	 /**
     * retrieves a service description from the service registry if there's only exact one endpoint matching the query
     * @param sEndpoint
     * @return
     * @throws Exception
     */
    private ServiceDescription getServiceDescirptionFromServiceRegistry(String sEndpoint) throws Exception{
    	URL sendsURL = new URL(sEndpoint);
		List<ServiceDescription> regSer = registry
				.query(new ServiceDescription.Builder(null,
						null).endpoint(sendsURL).build());
		if ((regSer.size() < 1)||(regSer.size() > 1)) {
			String err = "Unable to find service corresponding to endpoint or uniquly identifying it: "+ sendsURL;
			log.debug(err);
			throw new Exception(err);
		}
		else{
			
			ServiceDescription sdesc = regSer.get(0);
			return sdesc;
		}
    }
	
	/**
	 * @param paramList
	 * @return
	 */
	protected List<Parameter> convertServiceParameterList(List<ServiceParameter> paramList){
		List<Parameter> retList = new ArrayList<Parameter>();
		if(paramList==null)
			return retList;
		Iterator<ServiceParameter> iSerParam = paramList.iterator();
		while(iSerParam.hasNext()){
			ServiceParameter serParam = iSerParam.next();
			Parameter p = new Parameter.Builder(
					serParam.getName(), serParam.getValue()).build();
			retList.add(p);
		}
		return retList;
	}
	
	protected List<ServiceParameter> convertParameterList(List<Parameter> paramList){
		List<ServiceParameter> retList = new ArrayList<ServiceParameter>();
		if(paramList==null)
			return retList;
		
		Iterator<Parameter> iParam = paramList.iterator();
		while(iParam.hasNext()){
			Parameter serParam = iParam.next();
			ServiceParameter serP = reqParamInspector.new ServiceParameter(serParam.getName(), serParam.getValue(), serParam.getDescription());
			retList.add(serP);
		}
		return retList;
	}

}
