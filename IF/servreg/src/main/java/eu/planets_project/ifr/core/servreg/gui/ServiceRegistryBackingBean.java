package eu.planets_project.ifr.core.servreg.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class ServiceRegistryBackingBean {
	// keeping the logger just in case
	private static Logger log = Logger.getLogger(ServiceRegistryBackingBean.class.getName());

    public static final ServiceRegistry registry = ServiceRegistryFactory.getServiceRegistry();
    
    List<PlanetsServiceEndpoint> services = null;

	// TODO This shouldn't be hard coded, review role info and setup before V4
	private final static String adminRole = "admin";
	private final static String providerRole = "provider";

	/**
	 * Default constructor
	 */
	public ServiceRegistryBackingBean() {
		// this clear is useful for dev purposes, we don't really want to clear the registry
		//registry.clear();
	}
	/**
	 * 
	 * @return
	 *     The number of services registered with this IF server
	 */
	public int getRegisteredCount() {
		return registry.query(null).size();
	}

	/**
	 * TODO: This currently relies on the two hardcoded role names defined
	 * as class statics.  Not really the best.
	 * 
	 * @return
	 *     A boolean indicating if the user can edit the service registry
	 */
    public boolean getCanRegisterServices() {
    	return (FacesContext.getCurrentInstance().getExternalContext().isUserInRole(adminRole) ||
    			FacesContext.getCurrentInstance().getExternalContext().isUserInRole(providerRole));
    }
    
    /**
     * @return
     */
    public  List<PlanetsServiceEndpoint> getRegisteredServices() {
        if( this.services == null ) {
            this.updateServiceList();
        }
        return this.services;
    }
    
    /**
     * @param objectName
     * @return
     */
    public static Object getManagedObject(String objectName)
    {
      FacesContext context = FacesContext.getCurrentInstance();
      if( context == null ) return null;
      ELResolver resolver = context.getApplication().getELResolver();
      Object requestedObject =  resolver.getValue(context.getELContext(), null, objectName);
      return  requestedObject;
    }


    //====================================================================================
    // -----Action Methods ---
    //====================================================================================

    /**
     * @return JSF status
     */
    public String registerAutomatically() {
    	this.registerService(false);
    	return "success";
    }

    /**
     * @return JSF status
     */
    public String registerManually() {
    	this.registerService(true);
    	return "gotoAddExternal";
    }
    
    /**
     * @return JSF status
     */
    public String registerExternalService(){
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	ELResolver res = ctx.getApplication().getELResolver();
    	EndpointBackingBean endBean = (EndpointBackingBean) res.getValue(ctx.getELContext(), null, "EndpointBean");
    	ServiceDescriptionBackingBean descBean = (ServiceDescriptionBackingBean) res.getValue(ctx.getELContext(), null, "DescriptionBean");
    	ExternalEndpointBackingBean extBean = (ExternalEndpointBackingBean) res.getValue(ctx.getELContext(), null, "ExternalBean");

    	// Set the current endpoint
    	endBean.addEndpoint(extBean.getEndpoint());
    	// And the service description
    	descBean.setServiceDescription(extBean.getServiceDescription());
    	
		registry.register(extBean.getServiceDescription());

    	// Update the Endpoint List
		log.info("recording registration");
		endBean.recordRegistration();
    	return "gotoEndpoints";
    }
    
    /**
     * @return JSF status
     */
    public String checkAllServiceDescriptions() {
        for( PlanetsServiceEndpoint srv : this.getRegisteredServices() ) {
          // Check if the current service description is up to date:
          srv.setDescriptionStatus( PlanetsServiceEndpoint.DescriptionStatus.UNKNOWN );
          srv.checkUpToDate();
        }
        return "success";
    }

    /**
     * @return JSF status
     */
    public String updateAllServiceDescriptions() {
        for( PlanetsServiceEndpoint srv : this.getRegisteredServices() ) {
          // Attempt to update every description:
          srv.updateDescription();
        }
        return "success";
    }
    
    /**
     * @return JSF status
     */
    public String refreshServiceListCache() {
        this.services = null;
        return "success";
    }

    /* -----Private Methods --- */
    
    private void updateServiceList() {
        // First get services from the service registry, get a registry instance
        services = new ArrayList<PlanetsServiceEndpoint>();
        // Iterate over the descriptions and add a new endpoint for each
        for (ServiceDescription desc : registry.query(null)) {
            PlanetsServiceEndpoint serv = null;
            try {
                serv = new PlanetsServiceEndpoint(desc);
            } catch (IllegalArgumentException e) {
                log.severe("Null or bad service description used to construct endpoint");
                log.severe(e.getStackTrace().toString());
                continue;
            }
            // Add to the list:
            services.add(serv);
        }
    }
    
	private void registerService(boolean updateDesc) {
    	// Get the other beans
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	ELResolver res = ctx.getApplication().getELResolver();
    	EndpointBackingBean endBean = (EndpointBackingBean) res.getValue(ctx.getELContext(), null, "EndpointBean");
    	ServiceDescriptionBackingBean descBean = (ServiceDescriptionBackingBean) res.getValue(ctx.getELContext(), null, "DescriptionBean");
    	
    	// Get the current Description
		if (updateDesc) {
			log.info("Updating description");
			descBean.updateDescription();
		}
		
		log.info("getting the description to be registered");
    	ServiceDescription desc = descBean.getServiceDescription();
		log.info("description->"+desc.toString());
		log.info("endpoint->"+desc.getEndpoint());

    	// Register the service
		log.info("registering description");
		
		registry.register(desc);

    	// Update the Endpoint List
		log.info("recording registration");
		endBean.recordRegistration();
	}
}
