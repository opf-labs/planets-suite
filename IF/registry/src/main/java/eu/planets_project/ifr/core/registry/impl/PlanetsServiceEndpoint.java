/**
 * 
 */
package eu.planets_project.ifr.core.registry.impl;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.utils.PlanetsServiceExplorer;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Class encapsulating details of Planets Service Endpoints.  Used in the service registry
 * GUI to hold details of endpoints on the server and in the Service Registry
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *
 */
public class PlanetsServiceEndpoint {
	/** The logger for this class */
	private static Log _log = LogFactory.getLog(PlanetsServiceEndpoint.class);
	// TODO This is a bodge to work around a bool read problem in JSF
	private static final String notRegGraphic = "/images/notreg.gif"; 
	private static final String isRegGraphic = "/images/reg.gif"; 
	private String regGraphic = notRegGraphic;
	/**
	 * Enumeration for the service status
	 * 
	 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
	 */
	public enum Status {
		/** Service is OK and instantiable */
		OK,
		/** Service is a new style interface but couldn't be instantiated */
		FAILED,
		/** Service is of a depracated type */
		DEPRACATED
	}
	/** The URL for the service endpoint location */
	private URL _location = null;
	/** The class of the service */
	private String _class = null;
	/** The status of the service */
	private Status _status;
	/** Boolean flag for registered services */
	private boolean _registered = false;
	
	//==========================================================================
	// Public Constructors
	//==========================================================================
	/**
	 * @param pse
	 * 		A non null PlanetsServiceExplorer object for the Planets Service Endpoint
	 * @throws IllegalArgumentException
	 */
	public PlanetsServiceEndpoint(PlanetsServiceExplorer pse) throws IllegalArgumentException {
		// Check that the passed argument is not null and the class is not null
		if (pse == null) {
			String message = "The PlanetsServiceExplorer object cannot be null";
			PlanetsServiceEndpoint._log.error(message);
			throw new IllegalArgumentException(message);
		} else if (pse.getServiceClass() == null) {
			String message = "The PlanetsServiceExplorer contained a null service class";
			PlanetsServiceEndpoint._log.error(message);
			throw new IllegalArgumentException(message);
		}
		// Now set the location
		this._location = pse.getWsdlLocation();
		// We need a service class
		this._class = pse.getServiceClass().getCanonicalName();
		// Now see if the service is instantiable
		try {
			if (pse.isServiceInstanciable()) this._status = Status.OK;
			else this._status = Status.FAILED;
		} catch (RuntimeException e) {
			this._status = Status.DEPRACATED;
		}
	}
	
	/**
	 * @param desc
	 * 		A non null ServiceDescription object for the service
	 * @throws IllegalArgumentException 
	 */
	public PlanetsServiceEndpoint(ServiceDescription desc) throws IllegalArgumentException {
		this._location = desc.getEndpoint();
		this._class = desc.getType();
		this._status = Status.OK;
		this.setRegistered(true);
	}

	//==========================================================================
	// Getters and setters
	//==========================================================================
	/**
	 * @return
	 * 		The java.net.URL location for the service endpoint
	 */
	public URL getLocation() {
		return this._location;
	}
	
	/**
	 * @return
	 * 		The name of the service
	 */
	public String getName() {
		// TODO Obtain the service name from somewhere more reliable than the path
		// At the moment we parse the last part of the path after the slash or return the
		// path if there is no slash
		if ( this._location.getPath().lastIndexOf('/') >= 0) {
			return this._location.getPath().substring(this._location.getPath().lastIndexOf('/') + 1);
		} else
			return this._location.getPath();
	}
	
	/**
	 * @return
	 * 		The category name of the service derived from the class name
	 */
	public String getCategory() {
		if (this._class.indexOf('.') >= 0) {
			return (this._class.substring(this._class.lastIndexOf('.') + 1));
		}
		else {
			return (this._class);
		}
	}
	
	/**
	 * @return
	 * 		The type of the service
	 */
	public String getType() {
		return this._class;
	}
	
	/**
	 * @return
	 * 		The status of the service
	 */
	public Status getStatus() {
		return this._status;
	}
	
	/**
	 * @return
	 * 		True is this service implements a depracated interface
	 */
	public boolean isDepracated() {
		return (this._status == Status.DEPRACATED);
	}

	/**
	 * @param registered
	 * 		Boolean denoting registration
	 */
	public void setRegistered(boolean registered) {
		this._registered = registered;
		if (this._registered) this.setRegGraphic(PlanetsServiceEndpoint.isRegGraphic);
		else this.setRegGraphic(PlanetsServiceEndpoint.notRegGraphic);
	}

	/**
	 * @return
	 * 		True if the service is registered
	 */
	public boolean isRegistered() {
		return _registered;
	}

	/**
	 * @param regGraphic the regGraphic to set
	 */
	public void setRegGraphic(String regGraphic) {
		this.regGraphic = regGraphic;
	}

	/**
	 * @return the regGraphic
	 */
	public String getRegGraphic() {
		return regGraphic;
	}
}
