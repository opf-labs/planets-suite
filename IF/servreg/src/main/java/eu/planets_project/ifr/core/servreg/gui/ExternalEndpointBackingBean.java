/**
 * 
 */
package eu.planets_project.ifr.core.servreg.gui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.servreg.utils.DiscoveryUtils;
import eu.planets_project.ifr.core.servreg.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class ExternalEndpointBackingBean {
	/** Static logger for External bean class */
	private static Logger log = Logger.getLogger(ExternalEndpointBackingBean.class.getName());
	private static final String WSDL_QUERY = "?wsdl";

	private String _externalUrl = "";
	private PlanetsServiceEndpoint _endpoint = null;
	private ServiceDescription _desc = null;
	
	//===============================================================================
	// Getters and Setters
	//===============================================================================
	
	/**
	 * @return
	 * 		The value of the external URL string used
	 */
	public String getExternalUrl() {
		return this._externalUrl;
	}
	
	/**
	 * @param externalValue
	 * 		New value of external URL to use
	 */
	public void setExternalUrl(String externalValue) {
		this._externalUrl = externalValue;
	}

	/**
	 * @return
	 * 		The current external endpoint
	 */
	public PlanetsServiceEndpoint getEndpoint() {
		return this._endpoint;
	}

	/**
	 * @param endpoint
	 * 		The external endpoint to set
	 */
	public void setEndpoint(PlanetsServiceEndpoint endpoint) {
		this._endpoint = endpoint;
	}
	

	/**
	 * @return
	 * 		The service description for the current service endpoint
	 */
	public ServiceDescription getServiceDescription() {
		return _desc;
	}

	/**
	 * @param desc
	 * 		A ServiceDescription to set the bean value
	 */
	public void setServiceDescription(ServiceDescription desc) {
		this._desc = desc;
	}
	//===================================================================================
	// Action Methods
	//===================================================================================
	/**
	 * @return 
	 * 		Success status code
	 */
	public String parseExternalEndpoint() {
		if (this._externalUrl.toLowerCase().indexOf(ExternalEndpointBackingBean.WSDL_QUERY) == -1)
			this._externalUrl += ExternalEndpointBackingBean.WSDL_QUERY;
		ExternalEndpointBackingBean.log.info("ExternalURL->" + this._externalUrl);
		try {
			URI externalURI = new URI(this._externalUrl);
			PlanetsServiceExplorer pse = new PlanetsServiceExplorer(externalURI.toURL());
			// Lets see if we can get a service description
			this._desc = DiscoveryUtils.getServiceDescription(externalURI.toURL()); 
			if (this._desc == null)
			{
				this._endpoint = new PlanetsServiceEndpoint(pse);
	        	ServiceDescription.Builder sb = 
	        		new ServiceDescription.Builder(_endpoint.getName(), _endpoint.getType());
	        	this._desc = sb.endpoint(_endpoint.getLocation()).classname(_endpoint.getType()).build();
			} else {
	        	ServiceDescription.Builder sb = 
	        		new ServiceDescription.Builder(this._desc);
	        	this._desc = sb.endpoint(externalURI.toURL()).build();
				this._endpoint = new PlanetsServiceEndpoint(this._desc);
			}
		} catch (URISyntaxException e) {
			ExternalEndpointBackingBean.log.severe("Invalid External URI->" + this._externalUrl);
			ExternalEndpointBackingBean.log.severe(e.getStackTrace().toString());
			return "invalidURI";
		} catch (MalformedURLException e) {
			ExternalEndpointBackingBean.log.severe("Malformed External URL->" + this._externalUrl);
			ExternalEndpointBackingBean.log.severe(e.getStackTrace().toString());
			return "invalidURI";
		}
		return "success";
	}
}
