package eu.planets_project.ifr.core.registry.gui;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.component.html.HtmlDataTable;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.Endpoint;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.ifr.core.registry.utils.DiscoveryUtils;
import eu.planets_project.ifr.core.registry.utils.EndpointUtils;
import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceDescription.Builder;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class EndpointBackingBean {
	private static Log log = LogFactory.getLog(RegistryBackingBean.class);

	private List<Endpoint> _endpoints = new ArrayList<Endpoint>();
    private HtmlDataTable endpointsDataTable;
	private Endpoint currentEndpoint;
	private ServiceDescription currentDescription = null;
	private String searchStr = "";
	/**
	 * Default constructor
	 */
	public EndpointBackingBean() {
		// populate the list of endpoints deployed on this server
		this.findEndpoints();
	}

	/**
	 * @return the endpoints
	 */
	public synchronized List<Endpoint> getEndpoints() {
		ArrayList<Endpoint>endpoints = new ArrayList<Endpoint>();
		for (Endpoint endpoint : _endpoints) {
			if ((endpoint.getCategory().toLowerCase().indexOf(this.searchStr.toLowerCase()) > -1) ||
					(endpoint.getName().toLowerCase().indexOf(this.searchStr.toLowerCase()) > -1)){
				endpoints.add(endpoint);
			}
		}
		return endpoints;
	}

	/**
	 * @param endpointsDataTable the endpointsDataTable to set
	 */
	public void setEndpointsDataTable(HtmlDataTable endpointsDataTable) {
		this.endpointsDataTable = endpointsDataTable;
	}
	/**
	 * @return the endpointsDataTable
	 */
	public HtmlDataTable getEndpointsDataTable() {
		return endpointsDataTable;
	}
	/**
	 * @return the number of planets service endpoints found
	 */
	public int getEndpointCount() {
		return this._endpoints.size();
	}

	/**
     * @return the current endpoint
     */
    public Endpoint getCurrentEndpoint() {
		return currentEndpoint;
	}

	/**
	 * @param currentEndpoint
	 */
	public void setCurrentEndpoint(Endpoint currentEndpoint) {
		this.currentEndpoint = currentEndpoint;
	}

	/**
	 * @return the currentDesription
	 */
	public ServiceDescription getCurrentDescription() {
		return this.currentDescription;
	}

	/**
	 * @return the search string
	 */
    public String getSearchStr() {
		return searchStr;
	}

    /**
     * @param searchStr
     */
	public void setSearchStr(String searchStr) {
		this.searchStr = searchStr;
	}

	/* ----------------- Actions ---------------------- */
	/**
     * Select the current format from the table.
	 * @return success status code
     */
    public String selectAnEndpoint() {
        currentEndpoint = (Endpoint) this.endpointsDataTable.getRowData();
        if (currentEndpoint.isDescribed()) {
			this.currentDescription = DiscoveryUtils.getServiceDescription(currentEndpoint.getLocation());
        }
        return "success";
    }

    /**
     * @return string status for faces
     */
    public String editADescription() {
    	return "editDescription";
    }
    
    /**
     * @return string status for faces
     */
    public String registerService() {
    	// Get the registry bean
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	ELResolver res = ctx.getApplication().getELResolver();
    	RegistryBackingBean regBean = (RegistryBackingBean) res.getValue(ctx.getELContext(), null, "RegistryBean");
    	// Build a new Service Description with the endpoint
    	ServiceDescription desc = 
    		new ServiceDescription.Builder(currentDescription).endpoint(currentEndpoint.getLocation()).build();
    	// Now register it
    	regBean.registerService(desc);
    	return "gotoServices";
    }

    /* Private methods */
    
    private void findEndpoints() {
    	log.info("looking for deployed endpoints");
    	// get the endpoints from ServiceLookup
    	this._endpoints = new ArrayList<Endpoint>();
    	List<URI> serviceEndpoints = EndpointUtils.listAvailableEndpoints();
    	log.info("endpoints found->" + serviceEndpoints.size());
    	// for each URI location create a new Endpoint
    	for (URI location : serviceEndpoints) {
    		try {
    			PlanetsServiceExplorer pse = new PlanetsServiceExplorer(location.toURL());
    			// we only want the planets services
				if ((null != pse.getServiceClass()) && (null != pse.getQName())) {
					this._endpoints.add(new Endpoint(pse));
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				log.error("Endpoint " + location.toASCIIString() + " is a malformed URL");
				e.printStackTrace();
			}
    	}
    }

}
