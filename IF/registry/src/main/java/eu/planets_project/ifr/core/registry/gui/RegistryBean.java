package eu.planets_project.ifr.core.registry.gui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.jaxr.JaxrServiceRegistry;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryObjectFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceTaxonomy;
import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.JAXRClassificationScheme;
import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.JAXRConcept;
import eu.planets_project.ifr.core.registry.api.jaxr.model.OrganizationList;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsBinding;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsOrganization;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsService;
import eu.planets_project.ifr.core.registry.api.jaxr.model.ServiceList;
import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.ifr.core.registry.utils.ServiceLookup;

/**
 * This is the main backing bean for the rewritten Service Registry GUI.
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class RegistryBean {
	private static Log log = LogFactory.getLog(RegistryBean.class);
	private static final String USERNAME = "provider";
	private static final String PASSWORD = "provider";

	private String name = "RegistryBean";
	private List<EndpointInfo> deployedEndpoints;
	private List<PsService> registeredServices;
	private List<SelectItem> serviceCategories = new ArrayList<SelectItem>();
	private List<SelectItem> organizations = new ArrayList<SelectItem>();
	private OrganizationList registeredOrganizations;
	private PsOrganization organization = new PsOrganization();
	private PsService service = new PsService();
	private PsBinding binding = new PsBinding();
	private String selectedOrg = "";
	private String selectedCat = "";

	/**
	 * No arg constructor
	 */
	public RegistryBean() {
		log.info("RegistryBean() no arg constructor");
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param organizationList the organizationList to set
	 */
	public void setRegisteredOrganizations(OrganizationList organizationList) {
		this.registeredOrganizations = organizationList;
	}
	/**
	 * @return the organizationList
	 */
	public OrganizationList getRegisteredOrganizations() {
		log.info("RegistryBean.getRegisteredOrganizations");
		log.info("Getting registry instance");
		ServiceRegistry reg = JaxrServiceRegistry.getInstance(USERNAME, PASSWORD);
		log.info("getting registered orgainisations");
		this.registeredOrganizations = reg.findOrganizations(USERNAME, PASSWORD, "%");
		log.info("There are " + registeredOrganizations.organizations.size() + " organisations");
		return registeredOrganizations;
	}
	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(PsOrganization organization) {
		this.organization = organization;
	}

	/**
	 * @return the organization
	 */
	public PsOrganization getOrganization() {
		return organization;
	}

	/**
	 * 
	 * @return the String status for web navigation
	 */
	public String addOrganization() {
		/* First, we create a registry instance: */
		ServiceRegistry registry = JaxrServiceRegistry.getInstance(USERNAME, PASSWORD);
		/* Then create an object factory for the registry: */
		ServiceRegistryObjectFactory factory = new ServiceRegistryObjectFactory(
				USERNAME, PASSWORD, registry);
		/* With that, we create an organization: */
		organization = factory.createOrganization(organization.getName(), organization.getDescription(), organization.getContactName(), organization.getContactMail());
		this.registeredOrganizations = registry.findOrganizations(USERNAME, PASSWORD, "%");
		return "addedorg";
	}
	/**
	 * @param service the service to set
	 */
	public void setService(PsService service) {
		this.service = service;
	}
	/**
	 * @return the service
	 */
	public PsService getService() {
		return service;
	}
	/**
	 * @param serviceCategories the serviceCategories to set
	 */
	public void setServiceCategories(List<SelectItem> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}
	/**
	 * @return the serviceCategories
	 */
	public List<SelectItem> getServiceCategories() {
		ServiceRegistry reg = JaxrServiceRegistry.getInstance(USERNAME, PASSWORD);
		ServiceTaxonomy st = reg.findTaxonomy(USERNAME, PASSWORD);
		List<JAXRClassificationScheme> schemes = st.getPC().getJAXRClassificationScheme();
		int loopIter = 0;
		for (JAXRConcept concept : schemes.get(0).getJAXRConcept()) {
			serviceCategories.add(new SelectItem(loopIter++, concept.getName(), "description", false));
		}
		return serviceCategories;
	}
	/**
	 * @param organizations the organizations to set
	 */
	public void setOrganizations(List<SelectItem> organizations) {
		this.organizations = organizations;
	}
	
	/**
	 * @return the organizations
	 */
	public List<SelectItem> getOrganizations() {
		log.info("RegistryBean.getOrganizations");
		log.info("clearing org list");
		this.organizations.clear();
		log.info("retrieving organizations");
		this.getRegisteredOrganizations();
		log.info("Looping through orgs to create select list for " + this.registeredOrganizations.organizations.size() + " orgs.");
		int iter = 0;
		for (PsOrganization org : this.registeredOrganizations.organizations) {
			this.organizations.add(new SelectItem(iter++, org.getName(), org.getDescription(), false));
		}
		log.info("returning organisations");
		return organizations;
	}
	/**
	 * @param binding the binding to set
	 */
	public void setBinding(PsBinding binding) {
		this.binding = binding;
	}
	/**
	 * @return the binding
	 */
	public PsBinding getBinding() {
		return binding;
	}
	/**
	 * @param registeredServices the registeredServices to set
	 */
	public void setRegisteredServices(List<PsService> registeredServices) {
		this.registeredServices = registeredServices;
	}
	/**
	 * @return the registeredServices
	 */
	public List<PsService> getRegisteredServices() {
		log.info("Getting registered services, first we need a registry");
		try {
	        ServiceRegistry registry = ServiceRegistryFactory.getInstance();
	        //log.info("Clearing registry");
	        //registry.clear(USERNAME, PASSWORD);
			log.info("now find the services");
	        registeredServices = registry.findServices(USERNAME,
	                PASSWORD, "%", "").services;
	        log.info("We found " + registeredServices.size() + " services");
	        // Now add the bindings
	        for (PsService service : registeredServices) {
	        	service.setBindings(registry.findBindings(USERNAME, PASSWORD, service.getKey()).bindings);
	        }
		} catch (Exception e) {
			log.error("Exception in RegistryBean.getRegisteredServices()");
			e.printStackTrace();
		}
		return registeredServices;
	}
	public String addService() {
		log.info("RegistryBean:addService()");
        /* First, we create a registry instance: */
        ServiceRegistry registry = ServiceRegistryFactory.getInstance();
        /* Then create an object factory for the registry: */
        ServiceRegistryObjectFactory factory = new ServiceRegistryObjectFactory(
                USERNAME, PASSWORD, registry);
        int orgIndex = Integer.parseInt(selectedOrg);
        log.info("selected org = " + orgIndex);
        int catIndex = Integer.parseInt(selectedCat);
        log.info("selected category = " + catIndex);
        log.info("Creating service");
        log.info("name = " + this.service.getName());
        log.info("description = " + this.service.getDescription());
        log.info("org name = " + this.registeredOrganizations.organizations.get(orgIndex).getName());
        log.info("org description = " + this.registeredOrganizations.organizations.get(orgIndex).getDescription());
        log.info("category = " + this.serviceCategories.get(catIndex).getLabel());
        log.info("binding description = " + this.binding.getDescription());
        log.info("binding URI = " + this.binding.getAccessURI());
        PsService service = factory.createService(this.service.getName(),
                this.service.getDescription(),
                this.registeredOrganizations.organizations.get(orgIndex), 
                this.serviceCategories.get(catIndex).getLabel());
        PsBinding binding = factory.createBinding(this.binding.getDescription(),
                this.binding.getAccessURI(), service);
		return "addservice";
	}
	/**
	 * @param selectedOrg the selectedOrg to set
	 */
	public void setSelectedOrg(String selectedOrg) {
		this.selectedOrg = selectedOrg;
		log.info("Selected org = " + selectedOrg);
	}
	/**
	 * @return the selectedOrg
	 */
	public String getSelectedOrg() {
		return selectedOrg;
	}
	/**
	 * @param selectedCat the selectedCat to set
	 */
	public void setSelectedCat(String selectedCat) {
		this.selectedCat = selectedCat;
	}
	/**
	 * @return the selectedCat
	 */
	public String getSelectedCat() {
		return selectedCat;
	}
	
	public String viewService() {
		FacesContext context = FacesContext.getCurrentInstance();
		log.info("checking for serviceToView parameter from parameterMap");
		Object value = context.getExternalContext().getRequestParameterMap().get("serviceToView");
		log.info("Retrieved value");
		String serviceToView = value.toString();
        ServiceRegistry registry = ServiceRegistryFactory.getInstance();
        ServiceList services = registry.findServices(USERNAME, PASSWORD, serviceToView, "");
        log.info("Retrieved services we have " + services.services.size() + " services");
        this.service = services.services.get(0);
        log.info("retrieved service description is " + this.service.getDescription());
        log.info("Service has " + this.service.getBindings().size() + " bindings");
        this.service.setBindings(registry.findBindings(USERNAME, PASSWORD,
                this.service.getKey()).bindings);
		return "viewservice";
	}
	/**
	 * @param deployedEndpoints the deployedEndpoints to set
	 */
	public void setDeployedEndpoints(List<EndpointInfo> deployedEndpoints) {
		this.deployedEndpoints = deployedEndpoints;
	}
	/**
	 * @return the deployedEndpoints
	 */
	public List<EndpointInfo> getDeployedEndpoints() {
		deployedEndpoints = new ArrayList<EndpointInfo>();
		int index = 0;
		for (URI wsEndpoint : ServiceLookup.listAvailableEndpoints()) {
			try {
				log.info("processing endpoint " + wsEndpoint.toASCIIString());
				PlanetsServiceExplorer pse = new PlanetsServiceExplorer(wsEndpoint.toURL());
				if ((null != pse.getServiceClass()) && (null != pse.getQName())) {
					deployedEndpoints.add(new EndpointInfo(pse.getWsdlLocation(),
							pse.getServiceClass().getCanonicalName()));
				}
				log.info("enpoint gave qname " + deployedEndpoints.get(deployedEndpoints.size() - 1).getCategory().toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				log.error("malformedURLException for URI: " + wsEndpoint.toASCIIString());
				e.printStackTrace();
			} catch (RuntimeException e) {
				log.error("Error parsing WSDL for " + wsEndpoint.toASCIIString());
				log.error(e.getClass().getName());
				e.printStackTrace();
				continue;
			}
		}
		// Now remove the registered endpoints
		this.getRegisteredServices();
		List<URL> regEndpoints = new ArrayList<URL>();
		log.info("Comparing to registered endpoints");
		for (PsService service : this.registeredServices) {
			log.info("Service " + service.getName());
			for (PsBinding binding : service.getBindings()) {
				try {
					log.info("Adding URL " + binding.getAccessURI());
					regEndpoints.add(new URL(binding.getAccessURI()));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					log.info("URL malformed for " + binding.getAccessURI());
					e.printStackTrace();
					continue;
				}
			}
		}
		try {
			List<EndpointInfo> registeredInfo = new ArrayList<EndpointInfo>();
			for (EndpointInfo endpoint : deployedEndpoints) {
				if (regEndpoints.contains(endpoint.getEndpoint())) {
					registeredInfo.add(endpoint);
				}
			}
			for (EndpointInfo endpoint : registeredInfo) {
				log.info("comparing endpoint " + endpoint.getEndpoint().toString());
				if (deployedEndpoints.contains(endpoint)) {
					log.info("Removing endpoint " + endpoint.getEndpoint().toString());
					deployedEndpoints.remove(endpoint);
				}
			}
		} catch (Exception e) {
			log.info("Caught exception");
			e.printStackTrace();
		}
		log.info("returning deployed endpoints");
		return deployedEndpoints;
	}
	
	public String registerEndpoint() {
		this.getOrganizations();
		FacesContext context = FacesContext.getCurrentInstance();
		log.info("checking for serviceToView parameter from parameterMap");
		Object value = context.getExternalContext().getRequestParameterMap().get("endpointToRegister");
		log.info("Retrieved value");
		int index = Integer.parseInt(value.toString());
		log.info("registering endpoint " + index);
		value = context.getExternalContext().getRequestParameterMap().get("category");
		String category = value.toString();
		EndpointInfo selectedEndpoint = deployedEndpoints.get(index); 
		this.service = new PsService(selectedEndpoint.getEndpoint().getPath(), "");
		this.getServiceCategories();
		for (SelectItem item : this.serviceCategories) {
			log.info("Checking item " + item.getLabel());
			log.info("Against category " + category);
			if (category.equals(item.getLabel())) {
				this.selectedCat = item.getValue().toString();
				break;
			}
		}
		this.binding = new PsBinding();
		this.binding.setAccessURI(selectedEndpoint.endpoint.toString());
		return "registerendpoint";
	}
	
	public String deleteService() {
        ServiceRegistry registry = ServiceRegistryFactory.getInstance();
        registry.deleteService(USERNAME, PASSWORD, this.service);
		return "deleteservice";
	}
	
	public class EndpointInfo {
		private URL endpoint;
		private String category;
		
		public EndpointInfo(URL endpoint, String category) {
			this.setEndpoint(endpoint);
			if (category.indexOf('.') >= 0) {
				this.setCategory(category.substring(category.lastIndexOf('.') + 1));
			}
			else {
				this.setCategory(category);
			}
		}

		/**
		 * @param endpoint the endpoint to set
		 */
		public void setEndpoint(URL endpoint) {
			this.endpoint = endpoint;
		}

		/**
		 * @return the endpoint
		 */
		public URL getEndpoint() {
			return endpoint;
		}

		/**
		 * @param category the category to set
		 */
		public void setCategory(String category) {
			this.category = category;
		}

		/**
		 * @return the category
		 */
		public String getCategory() {
			return category;
		}
	}
}
