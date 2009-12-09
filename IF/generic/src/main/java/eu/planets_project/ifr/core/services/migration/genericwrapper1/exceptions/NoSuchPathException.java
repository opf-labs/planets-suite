package eu.planets_project.ifr.core.services.migration.genericwrapper1.exceptions;

/**
 * TODO abr forgot to document this class
 */
public class NoSuchPathException extends MigrationException{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4331884817418311800L;

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
