package eu.planets_project.tb.impl.services;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import eu.planets_project.tb.api.persistency.TestbedServiceTemplatePersistencyRemote;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.api.services.TestbedServiceTemplate.ServiceOperation;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.impl.persistency.TestbedServiceTemplatePersistencyImpl;

/**
 * @author alindley
 * This class does only return objects of the type TestbedServiceTemplate with the descriminator
 * field "template" and not "experiment" (which indicates that this template is part of an experiment)
 */
public class ServiceTemplateRegistryImpl implements ServiceTemplateRegistry, java.io.Serializable{

	//Map<ServiceUUID, Service>
	private Map<String,TestbedServiceTemplate> hm_AllServices;
	//Map<ServiceUUID, List<ServiceTag>
	private Map<String, List<ServiceTag>> hm_ServiceTags;

	TestbedServiceTemplatePersistencyRemote dao_r;
	//this object is implemented following the java singleton pattern
	private static ServiceTemplateRegistryImpl instance; 
	
	
	/**
	 * This Class implements the Java singleton pattern and therefore the constructor should be private
	 * However due to requirements of JSF managed beans it is set public (at the moment).
	 */
	public ServiceTemplateRegistryImpl(){
		dao_r = TestbedServiceTemplatePersistencyImpl.getInstance();
		loadExistingTemplateData();
	}
	
	
	/**
	 * Uses the remote session object to fetch all existing (=registered) 
	 * service templates
	 */
	private void loadExistingTemplateData(){
		//HashMap<ServiceUUID, TestbedService>
		this.hm_AllServices = dao_r.getAllTBServiceIDAndTemplates();
		this.hm_ServiceTags = dao_r.getAllTBServiceIDAndTags();
	}
	
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized ServiceTemplateRegistryImpl getInstance(){
		if (instance == null){
			instance = new ServiceTemplateRegistryImpl();
		}
		return instance;
	}

	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getAllServiceNames()
	 */
	public List<String> getAllServiceNames(){
		List<String> sRet = new Vector<String>();
		Iterator<TestbedServiceTemplate> it =this.getAllServices().iterator();
		while(it.hasNext()){
			//get the service's name and add it
			sRet.add(it.next().getName());
		}
		return sRet;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getAllServicNamesAndServiceEntrySets()
	 */
	public Set<Entry<String,TestbedServiceTemplate>> getAllServicNamesAndServiceEntrySets(){
		//Set contains Entry<UUID,TBservice>
		Set<Entry<String,TestbedServiceTemplate>> setUUIDAndService = this.hm_AllServices.entrySet();
		//Return Set contains: Entry<Name, TBService>
		Set<Entry<String,TestbedServiceTemplate>> setRet = new HashSet<Entry<String,TestbedServiceTemplate>>();
		
		Iterator<Entry<String,TestbedServiceTemplate>> it = setUUIDAndService.iterator();
		while(it.hasNext()){
			Entry<String,TestbedServiceTemplate> entry = it.next();
			TestbedServiceTemplate service = entry.getValue();
			Map<String,TestbedServiceTemplate> mapHelper = new HashMap<String,TestbedServiceTemplate>();
			//now put ServiceName and TestbedService as return value
			mapHelper.put(service.getName(), service);
			Iterator<Entry<String,TestbedServiceTemplate>> entryret = mapHelper.entrySet().iterator();
			setRet.add(entryret.next());
		}
		
		return setRet;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getAllServices()
	 */
	public Collection<TestbedServiceTemplate> getAllServices(){
		return this.hm_AllServices.values();
	}
	
	/* (non-Javadoc)
	 * Please note the serviceTemplate may also contain other operations than the ones
	 * implementing the selecte type. This needs to be filtered out sparately
	 * @see eu.planets_project.tb.api.services.ServiceTemplateRegistry#getAllServicesByType(java.lang.String)
	 */
	public Collection<TestbedServiceTemplate> getAllServicesWithType(String serviceOperationType){
		Collection<TestbedServiceTemplate> ret = new Vector<TestbedServiceTemplate>();
		Iterator<String> itIDs = this.hm_AllServices.keySet().iterator();
		while(itIDs.hasNext()){
			String id = itIDs.next();
			TestbedServiceTemplate template = this.hm_AllServices.get(id);
			Iterator<ServiceOperation> operations = template.getAllServiceOperations().iterator();
			boolean bFound = false;
			while(operations.hasNext()){
				ServiceOperation operation = operations.next();
				if(operation.getServiceOperationType().equals(serviceOperationType))
					bFound = true;
			}
			if(bFound){
				ret.add(template);
			}
		}
		return ret;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getAllServiceUUIDs()
	 */
	public Collection<String> getAllServiceUUIDs(){
		return this.hm_AllServices.keySet();
	}
	

	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getAllEndpoints()
	 */
	public List<String> getAllEndpoints() {
		List<String> sRet = new Vector<String>();
		Iterator<TestbedServiceTemplate> it = this.getAllServices().iterator();
		while(it.hasNext()){
			sRet.add(it.next().getEndpoint());
		}
		return sRet;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getServiceByID(java.lang.String)
	 */
	public TestbedServiceTemplate getServiceByID(String sServiceUUID) {
		if(sServiceUUID!=null){
			if(this.getAllServiceUUIDs().contains(sServiceUUID)){
				//now return the object
				return this.hm_AllServices.get(sServiceUUID);
			}
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#getServiceByWSDLContent(java.lang.String)
	 */
	public TestbedServiceTemplate getServiceByWSDLContent(String sWSDLContent){
		if(sWSDLContent!=null){
			Iterator<TestbedServiceTemplate> it = this.getAllServices().iterator();
			while(it.hasNext()){
				TestbedServiceTemplate tbService = it.next();
				String content = tbService.getWSDLContent();
				if((content!=null)&&(sWSDLContent.equals(content))){
					return tbService;
				}
			}
		}
		return null;
	}

	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.ServiceTemplateRegistry#registerService(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void registerService(TestbedServiceTemplate service) throws Exception{
		if((service!=null)&&(isExecutionInformationComplete(service))){

			//this.hm_AllServices.put(service.getUUID(), service);
			
			//check if we're updating/extending an existing object
			if(isUpdateService(service.getUUID())){
				//update the already registered persistent object
				dao_r.updateTBServiceTemplate(service);
			}
			else{
				//add to persistency layer
				dao_r.persistTBServiceTemplate(service);
				
			}
			
			//reload the registry
			this.loadExistingTemplateData();
			
		}
	}
	
	/**
	 * A private helper method that checks if the service has already been persisted
	 * (in this case don't call "persist" but rather "update"
	 * @param UUID
	 * @return
	 */
	private boolean isUpdateService(String UUID){
		return dao_r.isServiceTemplateIDRegistered(UUID);
	}
	
	/**
	 * Required data for a servicetemplate to be registered are:
	 * service: name, uuid, endpoint
	 * for all operation: name, xmlrequest template, xpath
	 * @param template
	 * @return
	 */
	private boolean isExecutionInformationComplete(TestbedServiceTemplate template){
		boolean bret = true;
		try{
			if((template!=null)&&(template.getAllServiceOperations()!=null)){
				//service specific:
				if((template.getName()==null)||(template.getName().equals("")))
					bret = false;
				if((template.getUUID()==null)||(template.getUUID().equals("")))
					bret = false;
				if((template.getEndpoint()==null)||(template.getEndpoint().equals("")))
					bret = false;
				
				//for all operations:
				Iterator<ServiceOperation> ops = template.getAllServiceOperations().iterator();
				while(ops.hasNext()){
					ServiceOperation operation = ops.next();
					if((operation.getName()==null)||(operation.getName().equals("")))
						bret = false;
					if((operation.getXMLRequestTemplate()==null)||(operation.getXMLRequestTemplate().equals("")))
						bret = false;
					if((operation.getXPathToOutput()==null)||(operation.getXPathToOutput().equals("")))
						bret = false;
				}
				
			}
			else{
				bret = false;
			}
		}catch(Exception e){
			bret = false;
		}
		
		return bret;
	}


	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#isServiceRegistered(java.lang.String)
	 */
	public boolean isServiceRegistered(String WSDLContent) {
		Iterator<TestbedServiceTemplate> it = this.getAllServices().iterator();
		while(it.hasNext()){
			//this can never be null;
			String wsdlcontent = it.next().getWSDLContent();
			if(wsdlcontent.equals(WSDLContent))
				return true;
		}
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets.test.backend.api.model.mockup.ServiceRegistry#isServiceEndpointRegistered(java.lang.String)
	 */
	public boolean isServiceEndpointRegistered(String sEndpointRef){
		Iterator<TestbedServiceTemplate> it = this.getAllServices().iterator();
		while(it.hasNext()){
			//this can never be null;
			String endpoint = it.next().getEndpoint();
			if(endpoint.equals(sEndpointRef))
				return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.ServiceRegistry#removeService(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void removeService(TestbedServiceTemplate service) {
		if((service!=null)&&(service.getUUID()!=null)){
			this.removeService(service.getUUID());
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.ServiceRegistry#removeService(java.lang.String)
	 */
	public void removeService(String UUID) {
		// TODO Auto-generated method stub
		if(UUID!=null){
			
			//remove the service from the registry
			boolean b = this.hm_AllServices.containsKey(UUID);
			if (b){
				this.hm_AllServices.remove(UUID);
				
				//remove from persistency layer
				dao_r.deleteTBServiceTemplate(UUID);
				
				//reload the registry
				this.loadExistingTemplateData();
			}	
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.ServiceRegistry#getServicesByTagName(java.lang.String)
	 */
	public List<TestbedServiceTemplate> getServicesByTagName(String sTagName) {
		return this.getServicesByTagNameAndValue(sTagName, null);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.ServiceRegistry#getServicesByTagNameAndValue(java.lang.String, java.lang.String)
	 */
	public List<TestbedServiceTemplate> getServicesByTagNameAndValue(String sTagName, String sValue) {
		
		boolean bValueSearch = sValue==null ? false : true;
		
		List<TestbedServiceTemplate> ret = new Vector<TestbedServiceTemplate>();
		if(sTagName !=null){
			//iterate over all key items
			Iterator<String> skeys = this.hm_ServiceTags.keySet().iterator();
			
			while(skeys.hasNext()){
				String UUID = skeys.next();
				Iterator<ServiceTag> ittag = this.hm_ServiceTags.get(UUID).iterator();
			
				//iterate over all tags
				while(ittag.hasNext()){
					ServiceTag tag =ittag.next();

					if(bValueSearch){
						//also looking for the value
						if((tag.getName().equals(sTagName))&&(tag.getValue().equals(sValue))){
							//fetch the object and add it to the list
							ret.add(dao_r.getTBServiceTemplate(UUID));
						}
					}
					else{
						//value is not being searched 
						if(tag.getName().equals(sTagName)){
							//fetch the object and add it to the list
							ret.add(dao_r.getTBServiceTemplate(UUID));
						}
					}
				}
			}
		}
		return ret;
	}
	
}
