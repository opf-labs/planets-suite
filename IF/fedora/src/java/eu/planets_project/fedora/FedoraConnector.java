package eu.planets_project.fedora;

import org.w3c.dom.Document;

import java.net.URI;
import java.util.Set;
import java.io.InputStream;

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

    public boolean isPlanetsModel(URI cm) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public Document getDatastream(URI planetsModel, String dsid) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean modifyDatastream(URI pdURI, String filedatastream, InputStream inputStream) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
