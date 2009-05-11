package eu.planets_project.fedora;

/**
 * TODO abr forgot to document this class
 */
public class RepositoryException extends RuntimeException{
    /** Generated ID. */
    private static final long serialVersionUID = -2490422682585880403L;

    public RepositoryException() {
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
