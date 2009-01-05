package eu.planets_project.ifr.core.registry.gui;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

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

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class EndpointBackingBean {
	private static Log log = LogFactory.getLog(EndpointBackingBean.class);

	private Endpoint _justRegistered = null;
	private List<Endpoint> _endpoints = new ArrayList<Endpoint>();
	private SelectItemGroup categoryMenu;
	private List<SelectItem> serviceCategories = new ArrayList<SelectItem>();
    private HtmlDataTable endpointsDataTable;
	private Endpoint currentEndpoint;
	private String searchStr = "";
	private String selectedCategory = "all";
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
				if ((this.selectedCategory.equals("all")) | (this.selectedCategory.equals(endpoint.getCategory())))
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
	 * @return the number of registered planets service endpoints found
	 */
	public int getRegisteredEndpointCount() {
		int _count = 0;
		for (Endpoint endpoint : this._endpoints) {
			if (endpoint.isRegistered()) _count++;
		}
		return _count;
	}

	/**
	 * @return the number of registered planets service endpoints found
	 */
	public int getUnregisteredEndpointCount() {
		int _count = 0;
		for (Endpoint endpoint : this._endpoints) {
			if (! endpoint.isRegistered()) _count++;
		}
		return _count;
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

	/**
	 * @param selectedCategory the selectedCategory to set
	 */
	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	/**
	 * @return the selectedCategory
	 */
	public String getSelectedCategory() {
		return selectedCategory;
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
		return serviceCategories;
	}

	/**
	 * @param categoryMenu the categoryMenu to set
	 */
	public void setCategoryMenu(SelectItemGroup categoryMenu) {
		this.categoryMenu = categoryMenu;
	}

	/**
	 * @return the categoryMenu
	 */
	public SelectItemGroup getCategoryMenu() {
		return categoryMenu;
	}

	/**
	 * @return the _justRegistered
	 */
	public boolean getJustRegistered() {
		if ((null != this._justRegistered) && (this.currentEndpoint.equals(this._justRegistered))) {
			this._justRegistered = null;
			return true;
		}
		return false;
	}

	/* ----------------- Actions ---------------------- */
	/**
     * Select the current format from the table.
	 * @return success status code
     */
    public String selectAnEndpoint() {
    	// get the ServiceDescription backing bean
    	log.info("Getting context");
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	ELResolver res = ctx.getApplication().getELResolver();
    	ServiceDescriptionBackingBean descBean = (ServiceDescriptionBackingBean) res.getValue(ctx.getELContext(), null, "DescriptionBean");
    	// get the selected endpoint
    	log.info("Getting Row data");
        currentEndpoint = (Endpoint) this.endpointsDataTable.getRowData();
    	Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
    	ServiceDescription example = new ServiceDescription.Builder(null, null).endpoint(currentEndpoint.getLocation()).build();
    	List<ServiceDescription> _matches = registry.query(example);  
        // If it's registered then get the description from the registry
    	if (_matches.size() > 0) {
        	descBean.setServiceDescription(_matches.get(0));
        // If it's a new style endpoint then we can get the description and set the bean
        } else if (! currentEndpoint.isDepracated()) {
        	log.info("");
        	// Get the service description and add the endpoint
        	ServiceDescription servDev = DiscoveryUtils.getServiceDescription(currentEndpoint.getLocation());
        	servDev = new ServiceDescription.Builder(servDev).endpoint(currentEndpoint.getLocation()).build();
        	descBean.setServiceDescription(servDev);
        // It's an old deprecated interface so we'll cobble together a service description
        // as best we can :)
        // TODO: This is horrible, we can get rid when we de-commission the old interfaces
        } else {
        	ServiceDescription.Builder sb = 
        		new ServiceDescription.Builder(currentEndpoint.getName(), currentEndpoint.getType());
        	descBean.setServiceDescription(
        			sb.endpoint(currentEndpoint.getLocation()).classname(
        					currentEndpoint.getFullName()).build());
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
     * 
     */
    public void recordRegistration() {
    	this.currentEndpoint.setRegistered(true);
    	this._justRegistered = this.currentEndpoint;
    	for (Endpoint endpoint : this._endpoints)  {
    		if(endpoint.getLocation().toString().equals(currentEndpoint.getLocation().toString())) {
    			endpoint.setRegistered(true);
    		}
    	}
    }

    /* Private methods */
    
    private void findEndpoints() {
    	HashMap<String, String> _cats = new HashMap<String, String>();
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
					// Check to see if we're already registered
					Registry registry = PersistentRegistry.getInstance(CoreRegistry.getInstance());
					List<ServiceDescription> matches = registry.query(new ServiceDescription.Builder(null, null).endpoint(location.toURL()).build());
					// Create a new endpoint
					Endpoint _endpoint;
					if (matches.size() > 0)
						_endpoint = new Endpoint(matches.get(0));
					else
						_endpoint = new Endpoint(pse);
						
					this._endpoints.add(_endpoint);
					if (! _cats.containsKey(_endpoint.getCategory())) {
						_cats.put(_endpoint.getCategory(), _endpoint.getCategory());
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				log.error("Endpoint " + location.toASCIIString() + " is a malformed URL");
				e.printStackTrace();
			}
			this.serviceCategories.clear();
			this.serviceCategories.add(new SelectItem("all", "all"));
			for (String value : _cats.values()) {
				this.serviceCategories.add(new SelectItem(value, value));
			}
    	}
    }
}
