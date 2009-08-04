package eu.planets_project.services.migration.dia.impl.genericwrapper.exceptions;

public class MigrationException extends Exception {

    /** Generated ID. */
    private static final long serialVersionUID = -3608851152624902005L;

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
