/**
 * 
 */
package eu.planets_project.ifr.core.registry.gui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.utils.DiscoveryUtils;
import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class ExternalEndpointBackingBean {
	/** Static logger for External bean class */
	private static Log _log = LogFactory.getLog(ExternalEndpointBackingBean.class);
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
		ExternalEndpointBackingBean._log.info("ExternalURL->" + this._externalUrl);
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
			ExternalEndpointBackingBean._log.error("Invalid External URI->" + this._externalUrl);
			ExternalEndpointBackingBean._log.error(e.getStackTrace());
			return "invalidURI";
		} catch (MalformedURLException e) {
			ExternalEndpointBackingBean._log.error("Malformed External URL->" + this._externalUrl);
			ExternalEndpointBackingBean._log.error(e.getStackTrace());
			return "invalidURI";
		}
		return "success";
	}
}
