package eu.planets_project.ifr.core.registry.gui;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class RegistryBackingBean {
	// keeping the logger just in case
	private static Log log = LogFactory.getLog(RegistryBackingBean.class);

	// TODO This shouldn't be hard coded, review role info and setup before V4
	private final static String adminRole = "admin";
	private final static String providerRole = "provider";

	/**
	 * Default constructor
	 */
	public RegistryBackingBean() {
		// this clear is useful for dev purposes, we don't really want to clear the registry
		//Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
		//registry.clear();
	}
	/**
	 * 
	 * @return
	 *     The number of services registered with this IF server
	 */
	public int getRegisteredCount() {
		Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
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
    	return "gotoEndpoints";
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
		Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
		registry.register(extBean.getServiceDescription());

    	// Update the Endpoint List
		log.info("recording registration");
		endBean.recordRegistration();
    	return "gotoEndpoints";
    }

    /* -----Private Methods --- */
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
		Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
		registry.register(desc);

    	// Update the Endpoint List
		log.info("recording registration");
		endBean.recordRegistration();
	}
}
