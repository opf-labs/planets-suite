package eu.planets_project.ifr.core.services.migration.genericwrapper1.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: pko
 * Date: Aug 4, 2009
 * Time: 2:04:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationException extends MigrationException{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3713439986495998064L;

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
