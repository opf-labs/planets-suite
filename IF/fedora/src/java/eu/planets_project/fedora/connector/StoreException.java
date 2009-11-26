package eu.planets_project.fedora.connector;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 25, 2009
 * Time: 11:36:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class StoreException extends FedoraException{

    public StoreException() {
    }

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreException(Throwable cause) {
        super(cause);
    }
}
