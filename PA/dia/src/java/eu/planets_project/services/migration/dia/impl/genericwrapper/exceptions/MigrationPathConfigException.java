/**
 * 
 */
package eu.planets_project.services.migration.dia.impl.genericwrapper.exceptions;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 *
 */
public class MigrationPathConfigException extends Exception {

    
    /**
     * 
     */
    private static final long serialVersionUID = 5539653164116154839L;

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
