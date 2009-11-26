package eu.planets_project.fedora.connector;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 19, 2009
 * Time: 10:13:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraConnectionException extends FedoraException{
    public FedoraConnectionException() {
    }

    public FedoraConnectionException(String s) {
        super(s);
    }

    public FedoraConnectionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FedoraConnectionException(Throwable throwable) {
        super(throwable);
    }
}
