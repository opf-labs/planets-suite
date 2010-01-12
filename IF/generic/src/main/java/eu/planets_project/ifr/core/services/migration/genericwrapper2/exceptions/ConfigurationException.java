package eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: pko
 * Date: Aug 4, 2009
 * Time: 2:04:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1123233695684015000L;

	public ConfigurationException() {
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
