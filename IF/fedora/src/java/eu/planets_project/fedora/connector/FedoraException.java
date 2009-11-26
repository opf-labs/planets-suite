package eu.planets_project.fedora.connector;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 23, 2009
 * Time: 5:25:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraException extends Exception{

    public FedoraException() {
    }

    public FedoraException(String message) {
        super(message);
    }

    public FedoraException(String message, Throwable cause) {
        super(message, cause);
    }

    public FedoraException(Throwable cause) {
        super(cause);
    }
}
