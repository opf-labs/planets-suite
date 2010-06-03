package eu.planets_project.tb.gui.backing.exp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.wee.api.utils.WorkflowConfigUtil;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
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
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.api.model.ExperimentExecutable;
import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.exp.utils.ExpTypeWeeBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.services.util.wee.WeeRemoteUtil;

/**
 * 
 * A session-scope bean that's backing the executable preservation plan experiment type
 * 
 * @author <a href="mailto:Andrew.Lindley@ait.ac.at">Andrew Lindley</a>
 *
 */
public class ExpTypeExecutablePP extends ExpTypeBackingBean implements Serializable,ExpTypeWeeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3669443792175630629L;
	private Log log = LogFactory.getLog(ExpTypeExecutablePP.class);
	private HashMap<String, String> serviceTypes;
	private ArrayList<ServiceBean> serviceBeans;
	//mapping of service IDs in the workflow XML to ServiceBeans
	private HashMap<String, ServiceBean> serviceLookup;
	// This hash maps service endpoints to service names
	private HashMap<String, String> serviceNameMap;
	private WorkflowConfigUtil wfConfigUtil;
	private WorkflowConf wfConf;
	private boolean bValidXMLConfig,bXMLConfigUploaded,bWfTemplateAvailableInRegistry;
    private String xmlConfigFileRef;
    private String wfDescription;
    //for caching purposes when exposing the current xml configuration
    private int currXMLConfigHashCode;
    private String currXMLConfigTempFileURI;
	//Current experiment ID for checking if the reinit must be called.
	//private String currExpId="";
    public static final String EXP_TYPE_SESSION_MAP = "exp_type_executable_pp_sessionMap";
    
	
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
		
		//check and try to load from session
		boolean b = initFromCurrentSessionData();
		if(b){
			//we've inited from the session
			return;
		}else{
			//try to load from experiment
			initFromPersistedExperimentData();
		}	
	}
	
	
	private boolean initFromCurrentSessionData(){
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		if(expBean.getApproved()){
			//if approved we don't init from current session data
			return false;
		}
		long expID = expBean.getID();
		FacesContext ctx = FacesContext.getCurrentInstance();
		HashMap<String,ExpTypeExecutablePP> expTypeSessMap;
        Object o = ctx.getExternalContext().getSessionMap().get(ExpTypeExecutablePP.EXP_TYPE_SESSION_MAP);
        if(o!=null){
        	expTypeSessMap = (HashMap<String,ExpTypeExecutablePP>)o;
        	ExpTypeExecutablePP sessBean = expTypeSessMap.get(expID+"");
        	if(sessBean!=null){
        		//now exchange the session variable with the one we've stored
        		ctx.getExternalContext().getSessionMap().put("ExpTypeExecutablePP",sessBean);
        		return true;
        	}
        	else
        		return false;
        }
        return false;
	}
	
	
	private void initFromPersistedExperimentData(){
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
        //store information in the db entities
		ExperimentExecutable expExecutable = expBean.getExperiment().getExperimentExecutable();
        expExecutable.setWEEWorkflowConfig(this.buildWorkflowConfFromCurrentConfiguration());
        //specify which batch processing system WEE or TB/Local we want to use for this experiment
        expExecutable.setBatchSystemIdentifier(BatchProcessor.BATCH_QUEUE_TESTBED_WEE_LOCAL);
        expBean.updateExperiment();
	}
	
	
	/* 
	 * TODO AL: version 1.0 uses this structure to check for a valid workflow (exp-type specific) configuration.
	 * (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#checkExpTypeBean_Step2_WorkflowConfigurationOK()
	 */
	@Override
	public void checkExpTypeBean_Step2_WorkflowConfigurationOK() throws Exception{
		
		//1. check valid configuration file provided
		if(!this.isValidXMLConfig()){
			throw new Exception("The provided workflow configuration file is not valid");
		}	
		//2 check workflow available on system
		if(!isTemplateAvailableInWftRegistry()){
			throw new Exception("The selected workflow is not available on the execution engine - please contact Testbed helpdesk");
		}
		//3 check all selected services available
		if(!helperCheckAllSelectedServicesAvailable()){
			throw new Exception("One or more selected services are not available within the Testbed - please contact Testbed helpdesk");
		}
	}
	
	
	/**
	 * Checks if all specified/selected services are registered within the
	 * service registry.
	 * @return
	 */
	private boolean helperCheckAllSelectedServicesAvailable(){
		boolean bOK = true;
		for(ServiceBean sb : this.getAllCurrentlyUsedServices()){
			if(!sb.isServiceAvailable()){
				bOK = false;
			}
		}
		return bOK;
	}
	
	/**
	 * Returns a List of ServiceBean objects containing information for
	 * all services that are currently used within the worfklow configuration
	 * @return
	 */
	public List<ServiceBean> getAllCurrentlyUsedServices(){
		return this.serviceBeans;
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
    	DataRegistry digoManager = DataRegistryFactory.getDataRegistry();
    	URI uriRef = new URI(fileRef);
    	this.log.info("Retrieving Digital Object at " + uriRef);
		DigitalObject xmlTemplateDigo = digoManager.retrieve(new URI(fileRef));
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(xmlTemplateDigo.getContent().getInputStream()));
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
			return WorkflowConfigUtil.unmarshalWorkflowConfig(wfConfigXML);
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
    	String token1 = "public String describe() {";
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
			} else {
				log.info("Service: " + sdesc.getName() +" has no default parameters.");
			}

			//FIXME: dirty solution. Offer the migrate_from and migrate_to parameters for type 'Migrate' service 
			//TODO offer a pull down list for mime-types
			if(serType.endsWith(Migrate.NAME)){
				if((!sb.getDefaultServiceParameters().contains(WorkflowTemplate.SER_PARAM_MIGRATE_FROM))){
					ServiceParameter spar = new ServiceParameter(WorkflowTemplate.SER_PARAM_MIGRATE_FROM,"planets:fmt/ext/png");
					sb.addDefaultParameter(spar);
				}
				if((addDefaultParamsAsStandardParams)&&(!sb.getServiceParameters().contains(WorkflowTemplate.SER_PARAM_MIGRATE_FROM))){
					ServiceParameter spar = new ServiceParameter(WorkflowTemplate.SER_PARAM_MIGRATE_FROM,"planets:fmt/ext/png");
					sb.addParameter(spar);
				}
				if((!sb.getDefaultServiceParameters().contains(WorkflowTemplate.SER_PARAM_MIGRATE_TO))){
					ServiceParameter spar = new ServiceParameter(WorkflowTemplate.SER_PARAM_MIGRATE_TO,"planets:fmt/ext/png");
					sb.addDefaultParameter(spar);
				}
				if((addDefaultParamsAsStandardParams)&&(!sb.getServiceParameters().contains(WorkflowTemplate.SER_PARAM_MIGRATE_TO))){
					ServiceParameter spar = new ServiceParameter(WorkflowTemplate.SER_PARAM_MIGRATE_TO,"planets:fmt/ext/png");
					sb.addParameter(spar);
				}
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
	

	/**
	 * A backing bean for handling services contained in the surrounding workflow
	 * 
	 */
	public class ServiceBean implements Cloneable,Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5558546200237525470L;
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
		
		/**
		 * Checks if a given ServiceParameterName is already contained in the list
		 * off added ServiceParameters
		 * @param paramName
		 * @return
		 */
		private ServiceParameter getServiceParamContained(String paramName){
			for(ServiceParameter sp : this.getServiceParameters()){
				if(sp.getName().equals(paramName)){
					return sp;
				}
			}
			return null;
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
			//ServiceParameters names need to be used uniquely - decide update or new
			ServiceParameter spUpdate = this.getServiceParamContained(par.getName());
			if(spUpdate!=null){
				//delete
				this.removeParameter(spUpdate);
			}
			//finally add add
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
			this.defaultParameters.clear();
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

	public class ServiceParameter implements Cloneable,Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2362976530899676720L;
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
	
    /* ------------------------------------------- */
    
    /**
     * A Bean to hold the results on each digital object.
     */
    public class ExecutablePPResultsForDO  extends ResultsForDigitalObjectBean implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 8115319442858925163L;

		public ExecutablePPResultsForDO(String input) {
			super(input);
		}
		
		public List<ExecutablePPResultBean> getMigrations() {
            List<ExecutablePPResultBean> migs = new Vector<ExecutablePPResultBean>();
            int i = 1;
            for( ExecutionRecordImpl exerec : this.getExecutionRecords() ) {
                migs.add(new ExecutablePPResultBean( i, exerec ) );
                i++;
            }
            return migs;
        }
    }
    
    public class ExecutablePPResultBean implements Serializable{

        /**
		 * 
		 */
		private static final long serialVersionUID = -9171518810708157936L;
		private int batchId;
        @SuppressWarnings("unused")
		private ExecutionRecordImpl exerec;
        private Properties props;

        /**
         * @param exerec
         */
        public ExecutablePPResultBean(int batchId, ExecutionRecordImpl exerec) {
            this.batchId = batchId;
            this.exerec = exerec;
            try {
				props =  exerec.getPropertiesListResult();
			} catch (IOException e) {
				log.debug("No Properties recorded for the batch: "+batchId);
				props = new Properties();
			}
        }
        
        
        /**
         * get the DigoResult object if there's one
         * @return
         */
        public String getDigitalObjectResult() {
			Object tbDigoURI = props.get(ExecutionRecordImpl.RESULT_PROPERTY_URI);
            String summary = "Run "+batchId+": ";
            if( tbDigoURI != null ) {
                DataHandler dh = new DataHandlerImpl();
                DigitalObjectRefBean dobr;
                try {
                    dobr = dh.get((String)tbDigoURI);
                } catch (FileNotFoundException e) {
                    log.error("Could not find file. "+e);
                    return "";
                }
                summary += dobr.getName();
                long size = dobr.getSize();
                if( size >= 0 ) {
                    summary += " ("+size+" bytes)";
                }
                return summary;
            }
            summary += "No Result.";
            return summary;
        }
        
        /**
         * Gets a String represenation of all information that was logged for the workflow
         * except pointers to migration_results or migration_interim_results
         * @return
         */
        public List<String> getResultProperties() {
			List<String> ret = new ArrayList<String>();
			Enumeration<?> enumeration = props.keys();
			while(enumeration.hasMoreElements()){
				String key = (String)enumeration.nextElement();
				String value = props.getProperty(key);
				if(!key.startsWith(ExecutionRecordImpl.RESULT_PROPERTY_URI)){
					ret.add("["+key+"= "+value+"]");
				}
			}
			// Sort list in Case-insensitive sort
            Collections.sort(ret, String.CASE_INSENSITIVE_ORDER);
			return ret;	
        }
        
        /**
         * If any migration took place: link the interim digos in the GUI
         * @return
         */
        public List<ResultsForDigitalObjectBean> getInterimResults() {
        	//List<String> ret = new ArrayList<String>();
        	List<ResultsForDigitalObjectBean> ret = new ArrayList<ResultsForDigitalObjectBean>();
			Enumeration<?> enumeration = props.keys();
			while(enumeration.hasMoreElements()){
				String key = (String)enumeration.nextElement();
				props.getProperty(key);
				//keys start with the 
				if(key.startsWith(ExecutionRecordImpl.RESULT_PROPERTY_INTERIM_RESULT_URI)){
					ResultsForDigitalObjectBean r = new ResultsForDigitalObjectBean(props.getProperty(key));
					ret.add(r);
				}
			}
			// Sort list in Case-insensitive sort
            //Collections.sort(ret, String.CASE_INSENSITIVE_ORDER);
			return ret;	
        }
        
        /**
         * @return
         */
        public String getDigitalObjectDownloadURL() {
        	Object tbDigoURI = props.get(ExecutionRecordImpl.RESULT_PROPERTY_URI);
        	return getDigitalObjectDownloadURL(tbDigoURI);
        }
        
        public String getDigitalObjectURI() {
        	Object tbDigoURI = props.get(ExecutionRecordImpl.RESULT_PROPERTY_URI);
        	if(tbDigoURI!=null){
        		return (String)tbDigoURI;
        	}
        	return null;
        }
        
        /**
         * Creates an external http:// object ref for any TB datamanager ref.
         * e.g. https://localhost:8443/testbed/reader/download.jsp?fid=file%253A%252FD%253A%252FImplementation%252Fifr_server%252Fplanets-ftp%252Fusa_bundesstaaten_png.png
         * @return
         */
        private String getDigitalObjectDownloadURL(Object tbDigoURI) {
            if( tbDigoURI != null ) {
                DataHandler dh = new DataHandlerImpl();
                try {
                    DigitalObjectRefBean dobr = dh.get((String)tbDigoURI);
                    return dobr.getDownloadUri().toString();
                } catch (FileNotFoundException e) {
                    log.error("Failed to generate download URL. " + e);
                    return "";
                }
            }
            return "";
        }
        
    }
    
    
    /**
     * @return
     */
    public List<ExecutablePPResultsForDO> getExecutablePPResults() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        List<ExecutablePPResultsForDO> results = new Vector<ExecutablePPResultsForDO>();
        // Populate using the results:
        for( String file : expBean.getExperimentInputData().values() ) {
        	ExecutablePPResultsForDO res = new ExecutablePPResultsForDO(file);
            results.add(res);
        }

        // Now return the results:
        return results;
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#isExperimentBeanType()
	 */
	@Override
	public boolean isExperimentBeanType() {
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean == null ) return false;
		if( AdminManagerImpl.EXECUTABLEPP.equals(expBean.getEtype()) ) return true;
		return false;
	}

	/** {@inheritDoc} 
	 * This is the callback method for the edit-param bean for passing out existing parameter values
	 * */
	@Override
	public Map<String, List<Parameter>> getWorkflowParameters() {
		Map<String, List<Parameter>> ret = new HashMap<String, List<Parameter>>();
		// populate the data for the add/edit params (a separate managed bean)
		for(ServiceBean sb : this.getServiceBeans()){
			List<Parameter> lParam = new ArrayList<Parameter>();
    		String serID = sb.getServiceId();
    		
    		//3. iterate over all parameters that have been created/altered
    		for(ServiceParameter param : sb.getServiceParameters()){
    			Parameter parameter = new Parameter(param.getName(),param.getValue());
    			lParam.add(parameter);
    		}
    		ret.put(serID, lParam);
    	}
		return ret;
	}

	/** {@inheritDoc} 
	 * This is the callback method for the edit-param bean for writing back modified parameter values
	 * */
	@Override
	public void setWorkflowParameters(Map<String,List<Parameter>> params) {
		//adapted when moving to the add/edit params in a separate managed bean
		if(params!=null){
			for(String serviceID : params.keySet()){
				ServiceBean sb = serviceLookup.get(serviceID);
				
				//store old migrate_to and migrate_from and use if they were deleted
				ServiceParameter pFromOld = sb.getServiceParamContained(WorkflowTemplate.SER_PARAM_MIGRATE_FROM);
				ServiceParameter pToOld = sb.getServiceParamContained(WorkflowTemplate.SER_PARAM_MIGRATE_TO);
				
				//clear old parameters and add the ones that are handed over here
				sb.clearParameters();
				for(Parameter p : params.get(serviceID)){
					sb.addParameter(new ServiceParameter(p.getName(),p.getValue()));
				}
				
				//check if we haven't lost the migrate_to and _from parameters
				if(sb.getServiceParamContained(WorkflowTemplate.SER_PARAM_MIGRATE_FROM)==null){
					sb.addDefaultParameter(pFromOld);
				}
				if(sb.getServiceParamContained(WorkflowTemplate.SER_PARAM_MIGRATE_TO)==null){
					sb.addDefaultParameter(pToOld);
				}
			}
		}
	}
	

	/** {@inheritDoc} */
	public WorkflowConf getWeeWorkflowConf() {
		ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		
		if(!expBean.getApproved()){
			//that's the one used when building this in 'design experiment'
			return this.buildWorkflowConfFromCurrentConfiguration();
        } 
		if(expBean.getApproved()){
			//that's the one after 'design experiment' has been saved
			ExperimentExecutable expExecutable = expBean.getExperiment().getExperimentExecutable();
			return expExecutable.getWEEWorkflowConfig();
        }
		return null;
	}

	/** {@inheritDoc} */
	public boolean isValidCurrentConfiguration() {
		return isValidXMLConfig();
	}
  

}
