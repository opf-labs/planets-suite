package eu.planets_project.fedora;

/**
 * Repository exception type.
 */
public class RepositoryException extends RuntimeException{
    /** Generated ID. */
    private static final long serialVersionUID = -2490422682585880403L;

    /**
     * Empty exception.
     */
    public RepositoryException() {
    }

    /**
     * @param message The exception message
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * @param message The exception message
     * @param cause The exception cause
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause The exception cause
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
