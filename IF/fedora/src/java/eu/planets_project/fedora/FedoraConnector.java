package eu.planets_project.fedora;

import java.net.URI;
import java.util.Set;

/**
 * TODO abr forgot to document this class
 */
public class FedoraConnector {


    private String username;
    private String password;
    private String server;

    public FedoraConnector(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.server = server;
    }

    public boolean isWritable(URI pdURI) {

        return false;
    }

    public Set<URI> getContentModels(URI pdUri) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean exist(URI pdURI) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
