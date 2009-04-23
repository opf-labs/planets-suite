package eu.planets_project.fedora;

/**
 * TODO abr forgot to document this class
 */
public class RepositoryException extends RuntimeException{
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
