package eu.planets_project.fedora.connector;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 23, 2009
 * Time: 5:28:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParseException extends FedoraException{

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
