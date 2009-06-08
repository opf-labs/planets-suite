package eu.planets_project.ifr.core.registry.gui;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.ifr.core.registry.impl.PlanetsServiceEndpoint;
import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class RegistryBackingBean {
	// keeping the logger just in case
	private static Log log = LogFactory.getLog(RegistryBackingBean.class);

    public static Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
    

	// TODO This shouldn't be hard coded, review role info and setup before V4
	private final static String adminRole = "admin";
	private final static String providerRole = "provider";

	/**
	 * Default constructor
	 */
	public RegistryBackingBean() {
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
        // First get services from the service registry, get a registry instance
        List<PlanetsServiceEndpoint> endpoints = new ArrayList<PlanetsServiceEndpoint>();
        // Iterate over the descriptions and add a new endpoint for each
        for (ServiceDescription desc : registry.query(null)) {
            PlanetsServiceEndpoint _endpoint = null;
            try {
                _endpoint = new PlanetsServiceEndpoint(desc);
            } catch (IllegalArgumentException e) {
                log.error("Null or bad service description used to construct endpoint");
                log.error(e.getStackTrace());
                continue;
            }
            // Check if the current service description is up to date:
            _endpoint.checkUpToDate();
            // Add to the list:
            endpoints.add(_endpoint);
            /*
            try {
                uris.add(desc.getEndpoint().toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            // Add the category as well
            cats.add(_endpoint.getCategory());
            */
        }
        return endpoints;
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
		
		registry.register(desc);

    	// Update the Endpoint List
		log.info("recording registration");
		endBean.recordRegistration();
	}
}
