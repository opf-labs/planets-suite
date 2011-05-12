package eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions;

/**
 * TODO abr forgot to document this class
 */
public class NoSuchPathException extends MigrationException {

    private static final long serialVersionUID = 3049215431244780805L;

    public NoSuchPathException() {
    }

    public NoSuchPathException(String message, Throwable cause) {
	super(message, cause);
    }

    public NoSuchPathException(String message) {
	super(message);
    }

    public NoSuchPathException(Throwable cause) {
	super(cause);
    }
}
