package eu.planets_project.tb.gui.backing.exp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.xml.bind.JAXBException;

import org.richfaces.component.html.HtmlInplaceSelect;


import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.security.api.model.User;
import eu.planets_project.ifr.core.security.api.services.UserManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.wee.api.utils.WorkflowConfigUtil;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Template;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services.Service;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services.Service.Parameters;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services.Service.Parameters.Param;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.data.DigitalObjectMultiManager;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.MigrateWorkflow;
import eu.planets_project.tb.impl.services.util.wee.WeeRemoteUtil;

/**
 * 
 * A session-scope bean that's backing the executable preservation plan experiment type
 * 
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 *
 */
public class ExpTypeExecutablePP extends ExpTypeBackingBean {

	private PlanetsLogger log = PlanetsLogger.getLogger(ExpTypeExecutablePP.class, "testbed-log4j.xml");
	private HashMap<String, String> serviceTypes;
	private ArrayList<ServiceBean> serviceBeans;
	//mapping of service IDs in the workflow XML to ServiceBeans
	private HashMap<String, ServiceBean> serviceLookup;
	// This hash maps service endpoints to service names
	private HashMap<String, String> serviceNameMap;
	private WorkflowConfigUtil wfConfigUtil;
	private HtmlDataTable parameterTable;
	private WorkflowConf wfConf;
	//The service bean used on the editParameter screen
	private ServiceBean sbiq;
	private String newValue = "";
	private String parameterName = "";
	private String parameterValue = "";
	private boolean bValidXMLConfig,bXMLConfigUploaded,bWfTemplateAvailableInRegistry;
    private String xmlConfigFileRef;
    private String wfDescription;
    //for caching purposes when exposing the current xml configuration
    private int currXMLConfigHashCode;
    private String currXMLConfigTempFileURI;
	//Current experiment ID for checking if the reinit must be called.
	//private String currExpId="";
    
	
	public ExpTypeExecutablePP(){
		initBean();
	}
	
	private void initBean(){
		wfConf = null;
		serviceBeans = new ArrayList<ServiceBean>();
		serviceLookup = new HashMap<String, ServiceBean>();
		serviceNameMap = new HashMap<String, String>();
		// build service types map
		serviceTypes = new HashMap<String, String>();
		serviceTypes.put("Characterise",
				"eu.planets_project.services.characterise.Characterise");
		serviceTypes.put("Compare",
				"eu.planets_project.services.compare.Compare");
		serviceTypes.put("Identify",
				"eu.planets_project.services.identify.Identify");
		serviceTypes.put("Migrate",
				"eu.planets_project.services.migrate.Migrate");
		serviceTypes.put("Validate",
				"eu.planets_project.services.validate.Validate");
		serviceTypes.put("Modify", "eu.planets_project.services.modify.Modify");
		serviceTypes.put("CreateView",
				"eu.planets_project.services.view.CreateView");
		serviceTypes.put("ViewAction",
				"eu.planets_project.services.view.ViewAction");
		wfConfigUtil = new WorkflowConfigUtil();
		bValidXMLConfig = false;
		bXMLConfigUploaded = false;
		xmlConfigFileRef = null;
	    wfDescription = null;
	    bWfTemplateAvailableInRegistry = false;
	    currXMLConfigHashCode=0;
	    currXMLConfigTempFileURI = null;
	}
	
	/**
	 * This method is used to initialize this bean from a given experiment
	 * (i.e. WorkflowConf object as it is persisted in the db.)
	 * @param wfConf
	 */
	@Override
	public void initExpTypeBeanForExistingExperiment(){
		//1) reinit the bean
		initBean();
		
		//fetch the data to populate from
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		if(expBean.getExperiment()==null){
			//this is the case when the 'new experiment' hasn't been persisted
			return;
		}
		WorkflowConf storedConf = expBean.getExperiment().getExperimentExecutable().getWEEWorkflowConfig();
	
		//2) possibly populate bean from stored information 
		if(storedConf!=null){	
			this.setWeeXMLConfig(storedConf);
			//and call the populate from wfConfig method
	        populateServiceInformationFromWorkflowConfig();
	        this.setXMLConfigFileProvided(true);
	        //fill additional required gui information
	        this.setWeeXMLConfigFileRef("empty");
	        this.setXMLConfigFileProvided(true);
	        this.setXMLConfigValid(true);
	        
	        //check if the template is still available on the wee system
	        WftRegistryService wftRegistryService = WeeRemoteUtil.getInstance().getWeeRegistryService();
			if (wftRegistryService.getAllSupportedQNames().contains(storedConf.getTemplate().getClazz())){
				this.setTemplateAvailableInWftRegistry(true);
			}else{
				this.setTemplateAvailableInWftRegistry(false);
			}
		}
	}
	
	
	/**
	 * Takes the bean's information and persist it into the testbed's db model
	 * i.e. the experiment's executable.setWorkflowConfig method
	 */
	@Override
	public void saveExpTypeBean_Step2_WorkflowConfiguration_ToDBModel(){
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().setWEEWorkflowConfig(this.buildWorkflowConfFromCurrentConfiguration());
	}
	
	
	/**
	 * reinits the bean for the step where an template gets uploaded 
	 */
	public void reInitBeanForWFXMLConfigUploaded(){
		this.initBean();
	}
	
	
	HashMap<String,List<MeasurementImpl>> manualObsCache;
    long cacheExperimentID;
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getManualObservables()
	 */
	@Override
	public HashMap<String, List<MeasurementImpl>> getManualObservables() {
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	if(manualObsCache==null||(cacheExperimentID != expBean.getExperiment().getEntityID())){
    		cacheExperimentID = expBean.getExperiment().getEntityID();
    		
        	//query for properties that have been added from the Ontology
        	HashMap<String,Vector<String>> ontoPropIDs = new HashMap<String, Vector<String>>();
        	for(ExperimentStageBean stage : expBean.getStages()){
        		ontoPropIDs.put(stage.getName(),expBean.getExperiment().getExperimentExecutable().getManualProperties(stage.getName()));
        	}
        	
        	//this is the static list of manual properties - normally empty
        	HashMap<String,List<MeasurementImpl>> staticWFobs = getWorkflow(AdminManagerImpl.EXECUTABLEPP).getManualObservables();
        	
        	//FIXME AL: staticWFobs returns wrong items - where are they added - exclude staticWFobs for now
        	//manualObsCache = mergeManualObservables(staticWFobs, ontoPropIDs);
        	manualObsCache = mergeManualObservables(null, ontoPropIDs);
    	}
    	return manualObsCache;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getObservables()
	 */
	@Override
	public HashMap<String, List<MeasurementImpl>> getObservables() {
		return getWorkflow(AdminManagerImpl.EXECUTABLEPP).getObservables();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getStageBeans()
	 */
	@Override
	public List<ExperimentStageBean> getStageBeans() {
		 return getWorkflow(AdminManagerImpl.EXECUTABLEPP).getStages();
	}
	
	// ---------- INFORMATION FOR STAGE2 --------------
	public boolean isValidXMLConfig(){
		return bValidXMLConfig;
	}
	
	public void setXMLConfigValid(boolean valid){
		this.bValidXMLConfig = valid;
	}
    
	public boolean isXMLConfigFileProvided(){
		return bXMLConfigUploaded;
	}
	
	public void setXMLConfigFileProvided(boolean provided){
		this.bXMLConfigUploaded = provided;
	}
	
    /**
     * A pointer to the uploaded xml config file that was submitted to the system 
     * @param fileRef
     */
    public void setWeeXMLConfigFileRef(String fileRef){
    	this.xmlConfigFileRef = fileRef;
    }
    
    public String getWeeXMLConfigFileRef(){
    	if(xmlConfigFileRef == null)
    		return "";
    	return xmlConfigFileRef;
    }
    
    public void setWeeXMLConfig(WorkflowConf wfConfig){
    	this.wfConf = wfConfig;
    }
    
    /**
     * The original xmlConfig file that was submitted by the user - it does not contain any changes made during
     * step2 editParameters
     * @return
     */
    public WorkflowConf getWeeXMLConfig(){
    	return wfConf;
    }
    
    
    public void checkAndParseWorkflowConfigObject(String fileRef) throws Exception{
    	try {
    		String xmlConfigContent = helperReadDigoToString(fileRef);
	        //check validity against schema
	        wfConfigUtil.checkValidXMLConfig(xmlConfigContent);
	        //unmarshall the object with JAXB
	        WorkflowConf config = WorkflowConfigUtil.unmarshalWorkflowConfig(xmlConfigContent);
	        
	        //fill the with information
	        this.setWeeXMLConfigFileRef(fileRef);
	        this.setXMLConfigFileProvided(true);
	        this.setWeeXMLConfig(config);
	        this.setXMLConfigValid(true);
	        log.debug("Workflow XML config file valid and properly parsed - configuration written to bean for workflow: "+config.getTemplate().getClazz());
		
		} catch (DigitalObjectNotFoundException e) {
			this.setXMLConfigFileProvided(false);
			throw e;
		} catch (URISyntaxException e) {
			this.setXMLConfigFileProvided(false);
			throw e;
		} catch (IOException e) {
			this.setXMLConfigFileProvided(false);
			throw e;
		} catch (JAXBException e) {
			this.setXMLConfigValid(false);
			throw e;
		}
    }
    
    /**
     * A helper to read the content of a xmlConfigFile from a given ref location into a String
     * @param fileRef
     * @return
     * @throws IOException
     * @throws DigitalObjectNotFoundException
     * @throws URISyntaxException
     */
    private String helperReadDigoToString(String fileRef) throws IOException, DigitalObjectNotFoundException, URISyntaxException{
    	DigitalObjectMultiManager digoManager = new DigitalObjectMultiManager();
		DigitalObject xmlTemplateDigo = digoManager.retrieve(new URI(fileRef));
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(xmlTemplateDigo.getContent().read()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line + "\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
    
    /**
     * Takes the current configuration (which may consist of 
     * a) an uploaded xml configuration,
     * b) the altered parameter and service selection in step2 and 
     * marshalls all this into an WorkflowConf object
     * wich may be offered to the client for downloading (by serealizing it to XML) or for submitting it to the wee (WorkflowFactory).
     * @return
     */
    public WorkflowConf buildWorkflowConfFromCurrentConfiguration(){
    	String wfConfigXML = this.buildXMLConfigFromCurrentConfiguration();
    	try {
			return wfConfigUtil.unmarshalWorkflowConfig(wfConfigXML);
		} catch (JAXBException e) {
			log.debug("Unable to retrieve the WorkflowConfiguration",e);
			return null;
		}
    }
    
    /**
     * Is the same as buildWorkflowConfFromCurrentConfiguration but returns the xml representation
     * instead of the WorkflowConf object. This may be offered for downloading.
     * @see buildWorkflowConfFromCurrentConfiguration
     * @return
     */
    public String buildXMLConfigFromCurrentConfiguration(){
    	WorkflowConf conf = new WorkflowConf();
    	
    	//1.add the template retrieved from the uploaded wfconfig
    	Template serTempl = this.getWeeXMLConfig().getTemplate();
    	
    	Services services = new Services();
    	//2.browse through the servicebeans and build the Services object
    	for(ServiceBean sb : this.getServiceBeans()){
    		Service service = new Service();
    		service.setId(sb.getServiceId());
    		service.setEndpoint(sb.getServiceEndpoint());
    		
    		Parameters parameters = new Parameters();
    		//3. iterate over all parameters that have been created/altered
    		for(ServiceParameter param : sb.getServiceParameters()){
    			Param parameter = new Param();
    			parameter.setName(param.getName());
    			parameter.setValue(param.getValue());
    			parameters.getParam().add(parameter);
    		}
    		if(parameters.getParam().size()>0){
    			//there needs to be a Parameter element only if there's a param for being xsd compliant
    			service.setParameters(parameters);
    		}
    		
    		services.getService().add(service);
    	}
    	
    	conf.setServices(services);
    	conf.setTemplate(serTempl);
    	
    	try {
			return wfConfigUtil.marshalWorkflowConfigToXMLTemplate(conf);
		} catch (Exception e) {
			log.debug("Unable to retrieve the XMLWorkflowConfiguration",e);
			return null;
		}
    }
    
    /**
     * Builds the currentXMLConfig from the given service/param configuration
     * and writes it to a temporary file that's accessible via an external url ref.
     * This can be used within the browser to download the currentXMLConfig
     * @return
     */
    public String getTempFileDownloadLinkForCurrentXMLConfig(){
		DataHandler dh = new DataHandlerImpl();
		String currXMLConfig = buildXMLConfigFromCurrentConfiguration();
		if(currXMLConfig==null){
			return null;
		}
		//save it's hashcode - for caching purposes
		if((this.currXMLConfigHashCode!=-1)&&(this.currXMLConfigHashCode!=currXMLConfig.hashCode())){
			this.currXMLConfigHashCode =  currXMLConfig.hashCode();
			
			try {
				//get a temporary file
				File f = dh.createTempFileInExternallyAccessableDir();
				Writer out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(f), "UTF-8" ) );
				out.write(currXMLConfig);
				out.close();
				currXMLConfigTempFileURI = ""+dh.getHttpFileRef(f);
				return currXMLConfigTempFileURI;
			} catch (Exception e) {
				log.debug("Error getting TempFileDownloadLinkForCurrentXMLConfig "+e);
				return null;
			}
		}
		else{
			//FIXME: still missing to check if this temp file ref still exists
			//returned the cached object
			return this.currXMLConfigTempFileURI;
		}
		
    }
   
    
    public boolean isTemplateAvailableInWftRegistry(){
    	return bWfTemplateAvailableInRegistry;
    }
    
    public void setTemplateAvailableInWftRegistry(boolean available){
    	bWfTemplateAvailableInRegistry = available;
    }
    
    public void setWFDescription(String description){
    	wfDescription = description;
    }
    
    public String getWFDescription(){
    	return this.wfDescription;
    }
    
    /**
     * Looks for the "public String describe()" method, creates a substring until the next appearance of "}"
     * and finally runs a regexp to remove special chars.
     * @param javafile
     * @return
     */
    public String helperParseDescription(String content){
    	String token1 = "public String describe(){";
    	String token2 = "}";
    	String regexp = "[a-z A-Z 0-9 () . \\\\s]+";
    	
    	int itoken1 = content.indexOf(token1);
    	if(content.indexOf(token1)!=-1){
    		int itoken2 = content.indexOf(token2,itoken1);
    		if(itoken2!=-1){
    			String text = content.substring(itoken1+token1.length(), itoken2);
    			Pattern urlpattern = Pattern.compile(regexp);
    			Matcher m = urlpattern.matcher(text);
    			StringBuffer buff = new StringBuffer();
    			buff = m.appendTail(buff);
    			return buff.toString();
    		}
    	}
    	return null;
    }
    
    
    /**
     * Extracts Service Endpoint and Parameter information from a given xml configuration
     * and queries the service registry to retrieve Service type, etc. and checks if it's registered
     */
    public void populateServiceInformationFromWorkflowConfig(){
    	WorkflowConf wfConfig = this.getWeeXMLConfig();
    	Services services = wfConfig.getServices();
    	if(services==null)
    		return;
    	List<Service> lServices = services.getService();
    	//now iterate over all declared services from the config
    	for(Service service : lServices){
    		if(service.getId()!=null){
    			ServiceBean sb = new ServiceBean(service.getId());
    			//check if we've got a valid endpoint
    			if(service.getEndpoint()!=null){
    				sb.setServiceEndpoint(service.getEndpoint());
    				//now query the service registry for this endpoint..
    				try {
    					//fetch the metadata from the service registry - but no default params
    					helperGetMetadataFromSerRegistry(sb,service.getEndpoint(),false);
    					
    				} catch (Exception ex) {
    					log.debug("Unable to lookup service endpoint: "+ service.getEndpoint()+ex);
    				}
    			}
    			else{
    				//no service endpoint was provided
    				log.debug("Found a service element with no endpoint: ");
    			}
    			//now Iterate over all params that were passed along
    			if(service.getParameters()!=null){
	    			for(Param param : service.getParameters().getParam()){
	    				String pName = param.getName();
	    				String pValue = param.getValue();
	    				if((pName!=null)&&(pValue!=null)){
	    					//add them to the WfServiceBean
	    					ServiceParameter sp = new ServiceParameter(
									pName, pValue);
							sb.addParameter(sp);
	    				}
	    			}
    			}
    			serviceBeans.add(sb);
				serviceLookup.put(service.getId(), sb);
    		}	
    		else{
    			log.debug("Found a service element with no Id");
    		}
    	}
    }
    
    /**
     * Retrieves all available metadata (service description, name, type, etc.) from the service registry
     * and updates this information on the given ServiceBean.
     * invokes getServiceDescirptionFromServiceRegistry()
     * @param sb
     * @param sEndpoint
     * @param addDefaultParams allows to trigger if the default parameters shall be added to the service bean as standard parameters
     * @return
     * @throws Exception 
     * @throws MalformedURLException 
     */
    private ServiceDescription helperGetMetadataFromSerRegistry(ServiceBean sb, String sEndpoint, boolean addDefaultParamsAsStandardParams) throws Exception{
    	try{
    		ServiceDescription sdesc = getServiceDescirptionFromServiceRegistry(sEndpoint);
    		sb.setServiceName(sdesc.getName());
			String serType = sdesc.getType();
			sb.setServiceDescription(sdesc.getDescription());
			sb.setServiceType(serType.substring(serType
					.lastIndexOf('.') + 1));
			
			//get default parameters for Service
			List<Parameter> pList = sdesc.getParameters();
			if (pList != null) {
				Iterator<Parameter> it = pList.iterator();
				while (it.hasNext()) {
					Parameter par = it.next();
					ServiceParameter spar = new ServiceParameter(par
							.getName(), par.getValue());
					//decide if the default params are added as standard params
					if(addDefaultParamsAsStandardParams){
						sb.addParameter(spar);
					}
					//add the default params to the bean
					sb.addDefaultParameter(spar);
				}
				//also pupulate the default parameters in the cache object
				//defaulParamsFromRegistryForServiceID.put(sb.getServiceId(), pList);
			} else {
				log.info("Service: " + sdesc.getName() +" has no default parameters.");
			}

			sb.setServiceAvailable(true);
			return sdesc;
    	}catch(Exception e){
			sb.setServiceAvailable(false);
			sb.setRequestedServiceEndpoint(sEndpoint);
			throw e;
    	}
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

    
    public void serviceSelectionChanged(ValueChangeEvent event) {
    	//HtmlInplaceSelect sel = (HtmlInplaceSelect) event.getComponent();
    	HtmlSelectOneMenu sel = (HtmlSelectOneMenu) event.getComponent();
    	String selServiceEndpoint = (String)event.getNewValue();
    	
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selServiceID");
		String sForServiceID;
		if(o1!=null){
			sForServiceID = (String)o1; 
		}
		else{return;}
		ServiceBean serviceBean = serviceLookup.get(sForServiceID);
		if (serviceBean == null) {
			log.debug("Unable to lookup service bean with ID: "+ sForServiceID);
			return;
		}
		if (selServiceEndpoint.equals("None")) {
			serviceBean.setServiceName("None");
			serviceBean.setServiceEndpoint("");
			serviceBean.setServiceDescription("");
			serviceBean.clearParameters();
			return;
		}
		String selServiceName = serviceNameMap.get(selServiceEndpoint);
		if (selServiceName == null) {
			log.debug("Unable to lookup service name for endpoint: "+ selServiceEndpoint);
			return;
		}
		serviceBean.clearParameters();
		try {
			//query the registry and update the serviceBean with all the information found
			this.helperGetMetadataFromSerRegistry(serviceBean,selServiceEndpoint,true);
			
		}catch(Exception e){
			log.debug("Unable to lookup service with endpoint: "+selServiceEndpoint);
		}

		serviceBean.setServiceName(selServiceName);
		serviceBean.setServiceEndpoint(selServiceEndpoint);
    }
    
    
	public List<ServiceBean> getServiceBeans() {
		return serviceBeans;
	}   
	
	// INFORMATION REQUIRED FOR THE EDIT PARAMETER WF SCREEN
	public HtmlDataTable getParameterTable() {
		return parameterTable;
	}
	
	public void setParameterTable(HtmlDataTable parameterTable) {
		this.parameterTable = parameterTable;
	}
	
	public List<ServiceParameter> getServiceParametersToEdit() {
		List<ServiceParameter> sps = new ArrayList<ServiceParameter>();
		if (sbiq != null) {
			sps = sbiq.getServiceParameters();
		}
		return sps;
	}
	
	public void updateParameter() {
		if (newValue == null) {
			//TODO AL: add errorMessage for GUI component?
			log.debug("New value is null - cannot update!");
			return;
		}
		if (newValue.equals("")) {
			//TODO AL: add errorMessage for GUI component?
			log.debug("Invalid new value - cannot update!");
			return;
		}
		int dataRow = parameterTable.getRowIndex();
		String pn = sbiq.getServiceParameters().get(dataRow).getName();
		sbiq.getServiceParameters().remove(dataRow);
		sbiq.getServiceParameters().add(dataRow,
				new ServiceParameter(pn, newValue));
	}
	
	public void removeParameter() {
		int dataRow = parameterTable.getRowIndex();
		sbiq.getServiceParameters().remove(dataRow);
	}

	/**
	 * Add a parameter in the editParam screen
	 * @param event
	 */
	public void addParameter(ActionEvent event) {
		if (sbiq == null) {
			log.debug("No ServiceBean selected!");
			return;
		}
		if (parameterName.equals("")) {
			//TODO AL: create ErrorMessage for GUI Component
			log.debug("Unable to create new parameter: name undefined!");
			return;
		}
		if (parameterValue.equals("")) {
			//TODO AL: create ErrorMessage for GUI Component
			log.debug("Unable to create new parameter: value undefined!");
			return;
		}
		sbiq.addParameter(new ServiceParameter(parameterName, parameterValue));
		this.parameterName ="";
		this.parameterValue ="";
	}
	
	public String getNewValue() {
		return newValue;
	}
	
	public void setNewValue(String value) {
		this.newValue = value;
	}
	
	public String getParameterName() {
		return this.parameterName;
	}

	public String getParameterValue() {
		return this.parameterValue;
	}
	
	public void setParameterName(String name) {
		this.parameterName = name;
	}

	public void setParameterValue(String value) {
		this.parameterValue = value;
	}
	
	/**
	 * Sets a sb record for the edit process
	 * @param sb
	 */
	public void setSBForEdit(ServiceBean sb){
		this.sbiq = sb;
	}
	
	
	/**
	 * Returns the bean that's used on the editParam screen
	 * @return
	 */
	public ServiceBean getEditedSB(){
		if(sbiq!=null){
			return sbiq;
		}
		return null;
	}
	
	private String getParamValueFromRequest(String s) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String value = facesContext.getExternalContext()
				.getRequestParameterMap().get(s);
		return value;
	}
	
	
	
	public void commandSetServiceBeanForEditParam(ActionEvent evt){
		FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("selServiceID");
		if(o1==null){
			return;
		}
		String sServiceBeanIdForEditParam = (String)o1; 
		this.newValue="";
		this.parameterName="";
		this.parameterValue="";

		//set the bean that's used for the edit mode - but clone it (allows cancel and update button)
		ServiceBean sb = this.serviceLookup.get(sServiceBeanIdForEditParam);
		ServiceBean sbForEdit = sb.clone();
		this.setSBForEdit(sbForEdit);
	}
	
	/**
	 * Triggers the update parameter information process from the edit service parameters screen to update
	 * the service bean record with the new parameters
	 */
	public void commandUpdateServiceBeanFromEditParamScreen(ActionEvent evt){
		
		//update the service bean's parameter information from the edit-bean
		ServiceBean sb = this.serviceLookup.get(this.sbiq.getServiceId());
		sb.clearParameters();
		
		for(ServiceParameter param : this.getEditedSB().getServiceParameters()){
			sb.addParameter(param);
		}
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
        for(ServiceParameter sp :  this.getEditedSB().getDefaultServiceParameters()){
            if( sp.getName().startsWith(q) ||
            	sp.getName().contains(q)) {
                  matches.add(sp);
            }
        }
        return matches;
    }
	
	//END INFORMATION REQUIRED FOR THE EDIT PARAMETER WF SCREEN

	/**
	 * A backing bean for handling services contained in the surrounding workflow
	 * 
	 */
	public class ServiceBean implements Cloneable{

		private String serviceId;
		private String serviceType;
		private String serviceName;
		private String serviceEndpoint;
		private String serviceDescription="";
		private ArrayList<ServiceParameter> serviceParameters;
		private ArrayList<SelectItem> serviceNames;
		private boolean bServiceAvailable = false;
		private ArrayList<ServiceParameter> defaultParameters;

		public ServiceBean() {
			this.serviceName = "None";
			this.serviceParameters = new ArrayList<ServiceParameter>();
			this.defaultParameters = new ArrayList<ServiceParameter>(); 
		}

		public ServiceBean(String id) {
			this.serviceId = id;
			this.serviceName = "None";
			this.serviceParameters = new ArrayList<ServiceParameter>();
			this.defaultParameters = new ArrayList<ServiceParameter>(); 
		}
		
		public boolean isServiceAvailable(){
			return bServiceAvailable;
		}

		/**
		 * Indicates if a service is available to the system the wftemplate is triggered from
		 * i.e. from the local service registry
		 * @param available
		 */
		public void setServiceAvailable(boolean available){
			bServiceAvailable = available;
		}	
		
		private String sRequestedServiceEndpoint="";
		/**
		 * Even if a endpoint is not registered in the service registry we're storing
		 * it here to tell a user to register if for using it within this workflow
		 * @return
		 */
		public String getRequestedServiceEndpoint(){
			return sRequestedServiceEndpoint;
		}
		
		public void setRequestedServiceEndpoint(String s){
			if(s!=null){
				sRequestedServiceEndpoint = s;
			}
		}

		public String getServiceId() {
			return serviceId;
		}

		public String getServiceType() {
			return serviceType;
		}

		public String getServiceName() {
			return serviceName;
		}
		
		public String getServiceDescription(){
			return serviceDescription;
		}
		
		public void setServiceDescription(String s){
			if(s!=null){
				serviceDescription =s;
			}
		}

		public String getServiceEndpoint() {
			return serviceEndpoint;
		}

		public List<ServiceParameter> getServiceParameters() {
			return serviceParameters;
		}

		public void setServiceId(String id) {
			this.serviceId = id;
		}

		public void setServiceType(String type) {
			this.serviceType = type;
			serviceNames = new ArrayList<SelectItem>();
			serviceNames.add(new SelectItem("None", "Select an Endpoint..."));
			serviceNames.add(new SelectItem("None", "None"));
			if (serviceType != null) {
				String serviceClass = serviceTypes.get(serviceType);
				List<ServiceDescription> services = registry
						.query(new ServiceDescription.Builder(null,
								serviceClass).build());
				Iterator<ServiceDescription> it = services.iterator();
				while (it.hasNext()) {
					ServiceDescription sd = it.next();
					serviceNames.add(new SelectItem(
							sd.getEndpoint().toString(), sd.getName()));
					serviceNameMap.put(sd.getEndpoint().toString(), sd
							.getName());
				}
			}
		}

		public void setServiceName(String name) {
			this.serviceName = name;
		}

		public void setServiceEndpoint(String endpoint) {
			this.serviceEndpoint = endpoint;
		}

		public void addParameter(ServiceParameter par) {
			this.serviceParameters.add(par);
		}

		public void removeParameter(ServiceParameter par) {
			this.serviceParameters.remove(par);
		}

		public List<SelectItem> getEndpointOptions() {
			if (serviceNames == null) {
				serviceNames = new ArrayList<SelectItem>();
				serviceNames
						.add(new SelectItem("None", "Select an Endpoint..."));
				serviceNames.add(new SelectItem("None", "None"));
			}
			return serviceNames;
		}

		public void clearParameters() {
			this.serviceParameters.clear();
		}
		
		public void addDefaultParameter(ServiceParameter param){
			this.getDefaultServiceParameters().add(param);
		}
		
		/**
		 * Contains the default parameters as provided through the service registry
		 * This field may not be populated for every item
		 * @return
		 */
		public List<ServiceParameter> getDefaultServiceParameters() {
			return this.defaultParameters;
		}
		
		
		
		/* 
		 * clone the object and create a deep copy for the serviceParameter to decouple them from the original object
		 * this allows its usage on the edit Screen
		 */
		public ServiceBean clone(){
			try{
				ServiceBean clonedSB = (ServiceBean)super.clone();
				clonedSB.serviceParameters = new ArrayList<ServiceParameter>();
				
				for(ServiceParameter param : this.getServiceParameters()){
					clonedSB.serviceParameters.add(param.clone());
				}
				return clonedSB;
			}catch(Exception e){
				log.error("Clone ServiceBean not supported"+e);
				return null;
			}
		}
	}

	public class ServiceParameter implements Cloneable{
		private String name;
		private String value;

		public ServiceParameter() {
		}

		public ServiceParameter(String n, String v) {
			this.name = n;
			this.value = v;
		}

		public String getName() {
			return this.name;
		}

		public String getValue() {
			return this.value;
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
