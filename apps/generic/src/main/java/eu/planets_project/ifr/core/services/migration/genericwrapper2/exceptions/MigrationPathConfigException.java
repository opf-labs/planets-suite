/**
 * 
 */
package eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 *
 */
public class MigrationPathConfigException extends ConfigurationException {


    /**
     * 
     */
    private static final long serialVersionUID = -8556800799890244012L;

    /**
     * TODO: Documentation!!! 
     */
    public MigrationPathConfigException() {
    }

    /**
     * @param message
     */
    public MigrationPathConfigException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public MigrationPathConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public MigrationPathConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
