package eu.planets_project.ifr.core.servreg.gui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.component.html.HtmlDataTable;

import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
import eu.planets_project.ifr.core.servreg.utils.DiscoveryUtils;
import eu.planets_project.ifr.core.servreg.utils.EndpointUtils;
import eu.planets_project.ifr.core.servreg.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class EndpointBackingBean {
	private static Log _log = LogFactory.getLog(EndpointBackingBean.class);

	private static final String ALL_CATEGORY = "All";

	private PlanetsServiceEndpoint _justRegistered = null;
	private PlanetsServiceEndpoint _currentEndpoint;
	private List<PlanetsServiceEndpoint> _endpoints = null;
	private List<SelectItem> _serviceCategories = null;
    private HtmlDataTable _endpointsDataTable;
	private String _searchStr = "";
	private String _selectedCategory = EndpointBackingBean.ALL_CATEGORY;

	/**
	 * Create an EndpointBackingBean.
	 */
	public EndpointBackingBean() {
		// Populate the list of endpoints deployed on this server
		this.findEndpoints();
	}

	/**
	 * @return the endpoints
	 */
	public synchronized List<PlanetsServiceEndpoint> getEndpoints() {
		// Create an empty list of endpoints
		ArrayList<PlanetsServiceEndpoint>endpoints = new ArrayList<PlanetsServiceEndpoint>();
		// Now iterate over the internal endpoint list
		for (PlanetsServiceEndpoint endpoint : _endpoints) {
			// Check that either the selected category matches or that the category is all
			if ((this._selectedCategory.equals(EndpointBackingBean.ALL_CATEGORY)) ||
					(this._selectedCategory.equals(endpoint.getCategory()))) {
				// If the name or the category of the endpoint match the search string
				if ((endpoint.getCategory().toLowerCase().indexOf(this._searchStr.toLowerCase()) > -1) ||
						(endpoint.getName().toLowerCase().indexOf(this._searchStr.toLowerCase()) > -1)) {
						endpoints.add(endpoint);
				}
			}
		}
		return endpoints;
	}

	/**
	 * @param endpointsDataTable the endpointsDataTable to set
	 */
	public void setEndpointsDataTable(HtmlDataTable endpointsDataTable) {
		this._endpointsDataTable = endpointsDataTable;
	}
	/**
	 * @return the endpointsDataTable
	 */
	public HtmlDataTable getEndpointsDataTable() {
		return _endpointsDataTable;
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
		for (PlanetsServiceEndpoint endpoint : this._endpoints) {
			if (endpoint.isRegistered()) _count++;
		}
		return _count;
	}

	/**
	 * @return the number of registered planets service endpoints found
	 */
	public int getUnregisteredEndpointCount() {
		int _count = 0;
		for (PlanetsServiceEndpoint endpoint : this._endpoints) {
			if (! endpoint.isRegistered()) _count++;
		}
		return _count;
	}

	/**
     * @return the current endpoint
     */
    public PlanetsServiceEndpoint getCurrentEndpoint() {
		return _currentEndpoint;
	}

	/**
	 * @param currentEndpoint
	 */
	public void setCurrentEndpoint(PlanetsServiceEndpoint currentEndpoint) {
		this._currentEndpoint = currentEndpoint;
	}

	/**
	 * @return the search string
	 */
    public String getSearchStr() {
		return _searchStr;
	}

    /**
     * @param searchStr
     */
	public void setSearchStr(String searchStr) {
		this._searchStr = searchStr;
	}

	/**
	 * @param selectedCategory the selectedCategory to set
	 */
	public void setSelectedCategory(String selectedCategory) {
		this._selectedCategory = selectedCategory;
	}

	/**
	 * @return the selectedCategory
	 */
	public String getSelectedCategory() {
		return _selectedCategory;
	}

	/**
	 * @param serviceCategories the serviceCategories to set
	 */
	public void setServiceCategories(List<SelectItem> serviceCategories) {
		this._serviceCategories = serviceCategories;
	}

	/**
	 * @return the serviceCategories
	 */
	public List<SelectItem> getServiceCategories() {
		return _serviceCategories;
	}

	/**
	 * @return the _justRegistered
	 */
	public boolean getJustRegistered() {
		if ((null != this._justRegistered) && (this._currentEndpoint.equals(this._justRegistered))) {
			this._justRegistered = null;
			return true;
		}
		return false;
	}

	/**
	 * @param endpoint The endpoint to add
	 */
	public void addEndpoint(PlanetsServiceEndpoint endpoint) {
		this._currentEndpoint = endpoint;
		if (!this._endpoints.contains(endpoint)) {
			_log.info("Adding external endpoint to list as ITS NEW");
			this._endpoints.add(endpoint);
		}
	}

	/* ----------------- Actions ---------------------- */
	/**
     * Select the current format from the table.
	 * @return success status code
     */
    public String selectAnEndpoint() {
    	// get the ServiceDescription backing bean
    	ServiceDescriptionBackingBean descBean = (ServiceDescriptionBackingBean) ServiceRegistryBackingBean.getManagedObject("DescriptionBean");
    	// get the selected endpoint
    	_log.info("Getting Row data");
        _currentEndpoint = (PlanetsServiceEndpoint) this._endpointsDataTable.getRowData();
    	ServiceRegistry registry = ServiceRegistryFactory.getRegistry();
    	ServiceDescription example = new ServiceDescription.Builder(null, null).endpoint(_currentEndpoint.getLocation()).build();
    	List<ServiceDescription> _matches = registry.query(example);  
        // If it's registered then get the description from the registry
    	if (_matches.size() > 0) {
        	descBean.setServiceDescription(_matches.get(0));
        // If it's a new style endpoint then we can get the description and set the bean
        } else if (! _currentEndpoint.isDeprecated()) {
        	_log.info("");
        	// Get the service description and add the endpoint
        	ServiceDescription servDev = DiscoveryUtils.getServiceDescription(_currentEndpoint.getLocation());
        	servDev = new ServiceDescription.Builder(servDev).endpoint(_currentEndpoint.getLocation()).build();
        	descBean.setServiceDescription(servDev);
        // It's an old deprecated interface so we'll cobble together a service description
        // as best we can :)
        // TODO: This is horrible, we can get rid when we de-commission the old interfaces
        } else {
        	ServiceDescription.Builder sb = 
        		new ServiceDescription.Builder(_currentEndpoint.getName(), _currentEndpoint.getType());
        	descBean.setServiceDescription(
        			sb.endpoint(_currentEndpoint.getLocation()).classname(
        					_currentEndpoint.getType()).build());
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
     * Sets the endpoints to registered.
     */
    public void recordRegistration() {
    	this._currentEndpoint.setRegistered(true);
    	this._justRegistered = this._currentEndpoint;
    	for (PlanetsServiceEndpoint endpoint : this._endpoints)  {
    		if(endpoint.getLocation().toString().equals(_currentEndpoint.getLocation().toString())) {
    			endpoint.setRegistered(true);
    		}
    	}
    }
    //====================================================================================
    // Private methods
    //====================================================================================
    
    private void findEndpoints() {
    	_log.info("looking for deployed endpoints");
    	// Get the endpoints from ServiceLookup we need to initialise the endpoint list
    	// then a hash set of URIs to keep track of duplicates
    	this._endpoints = new ArrayList<PlanetsServiceEndpoint>();
    	HashSet<URI> uris = new HashSet<URI>();

    	// Create hash map for categories and add all
    	SortedSet<String> _cats = new TreeSet<String>();
    	_cats.add(EndpointBackingBean.ALL_CATEGORY);
    	
    	ServiceRegistryBackingBean reg = (ServiceRegistryBackingBean) ServiceRegistryBackingBean.getManagedObject("RegistryBean");
    	// Iterate over the known descriptions and remember the endpoint for each
    	for (PlanetsServiceEndpoint _endpoint : reg.getRegisteredServices() ) {
    	    this._endpoints.add( _endpoint );
    		try {
                uris.add(_endpoint.getLocation().toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
    		// Add the category as well
    		_cats.add(_endpoint.getCategory());
    	}

    	// Now loop throught the other endpoints
    	List<URI> serviceEndpoints = EndpointUtils.listAvailableEndpoints();
    	_log.info("endpoints found->" + serviceEndpoints.size());
    	// for each URI location create a new Endpoint
    	for (URI location : serviceEndpoints) {
    		try {
	    		// If we've already got this endpoint then push on to the next
				if (uris.contains(location.toURL().toURI())) {
					_log.info("Service registered->" + location);
					continue;
				}

				// Let's see if it's an unregistered Planets service
				PlanetsServiceExplorer pse = new PlanetsServiceExplorer(location.toURL());
				// we only want the planets services
				if ((null != pse.getServiceClass()) && (null != pse.getQName())) {
					// Add the endpoint to the list and the category
					PlanetsServiceEndpoint _endpoint = new PlanetsServiceEndpoint(pse);
					this._endpoints.add(_endpoint);
					_cats.add(_endpoint.getCategory());
				}
			} catch (MalformedURLException e) {
				_log.error("Endpoint " + location.toASCIIString() + " is a malformed URL");
				_log.error(e.getStackTrace());
			} catch (IllegalArgumentException e) {
				_log.error("Null or bad PlanetsServiceExplorer used as constructor for PlanetsServiceEndpoint");
				_log.error(e.getStackTrace());
			} catch (URISyntaxException e) {
                e.printStackTrace();
            }
		}
		// Create a new list for all of the categories
		this ._serviceCategories = new ArrayList<SelectItem>(_cats.size());
		// Then add the values from categories
		for (String value : _cats) {
			this._serviceCategories.add(new SelectItem(value, value));
		}
    }

    //====================================================================================
    // -----Action Methods ---
    //====================================================================================
    
    public String refreshEndpointList() {
        // Refresh the registry
        ServiceRegistryBackingBean reg = (ServiceRegistryBackingBean) ServiceRegistryBackingBean.getManagedObject("RegistryBean");
        reg.refreshServiceListCache();
        // And the endpoint list...
        this.findEndpoints();
        return "gotoEndpoints";
    }

}
