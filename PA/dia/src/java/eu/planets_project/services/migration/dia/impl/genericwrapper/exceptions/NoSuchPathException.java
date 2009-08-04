package eu.planets_project.services.migration.dia.impl.genericwrapper.exceptions;

/**
 * TODO abr forgot to document this class
 */
public class NoSuchPathException extends MigrationException{

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
