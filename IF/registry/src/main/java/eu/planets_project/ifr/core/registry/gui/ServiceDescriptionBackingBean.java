package eu.planets_project.ifr.core.registry.gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class ServiceDescriptionBackingBean {
	// keeping the logger just in case
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ServiceDescriptionBackingBean.class);
	
	// The original Service Description
	private ServiceDescription _serviceDescription = null;
	
	// The edit fields
	private String _name = null;
	private String _desc = null;
	private String _author = null;
	private String _serviceProvider;
	/**
	 * No arg constructor
	 */
	public ServiceDescriptionBackingBean() {
		// create an empty description
		this._serviceDescription = new ServiceDescription.Builder(null, null).build();
	}

	/**
	 * @param _description the _description to set
	 */
	public void setServiceDescription(ServiceDescription _description) {
		this._serviceDescription = _description;
		this._name = this._serviceDescription.getName();
		this._desc = this._serviceDescription.getDescription();
		this._author = this._serviceDescription.getAuthor();
		this._serviceProvider = this._serviceDescription.getServiceProvider();
	}

	/**
	 * @return the _description
	 */
	public ServiceDescription getServiceDescription() {
		return _serviceDescription;
	}

	/**
	 * @param _name the _name to set
	 */
	public void setName(String _name) {
		this._name = _name;
	}

	/**
	 * @return the _name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param _desc the _desc to set
	 */
	public void setDescription(String _desc) {
		this._desc = _desc;
	}

	/**
	 * @return the _desc
	 */
	public String getDescription() {
		return _desc;
	}

	/**
	 * @param _author the _author to set
	 */
	public void setAuthor(String _author) {
		this._author = _author;
	}

	/**
	 * @return the _author
	 */
	public String getAuthor() {
		return _author;
	}

	/**
	 * @param _serviceProvider the _serviceProvider to set
	 */
	public void setServiceProvider(String _serviceProvider) {
		this._serviceProvider = _serviceProvider;
	}

	/**
	 * @return the _serviceProvider
	 */
	public String getServiceProvider() {
		return _serviceProvider;
	}
	/**
	 * 
	 */
    public void updateDescription() {
    	ServiceDescription.Builder sb = new ServiceDescription.Builder(this._serviceDescription);
    	if ((this._name.length() > 0) | (null != this._serviceDescription.getName()))
    		sb.name(this._name);
    	if ((this._author.length() > 0) | (null != this._serviceDescription.getAuthor()))
    		sb.author(this._author);
    	if ((this._serviceProvider.length() > 0) | (null != this._serviceDescription.getServiceProvider()))
    		sb.serviceProvider(this._serviceProvider);
    	if ((this._desc.length() > 0) | (null != this._serviceDescription.getDescription()))
    		sb.description(this._desc);
    	this._serviceDescription = sb.build();
    }
}
