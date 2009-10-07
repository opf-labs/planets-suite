package eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions;

public class MigrationException extends Exception {

    /** Generated ID. */
    private static final long serialVersionUID = 1308316855907791901L;

    public MigrationException() {
        super();
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(Throwable cause) {
        super(cause);
    }
}
