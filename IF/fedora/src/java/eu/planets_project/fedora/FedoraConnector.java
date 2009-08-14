package eu.planets_project.fedora;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Set;

/**
 * The interface to the fedora repository system. This interface defines the few
 * methods that the planets connector must be able to use. All the more advanced
 * features can be build on top of this.
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

    /**
     * Test if the object is writable. Always test before calling modifyDatastream
     * @param pdUri the id of the object. Must start with "info:fedora/"
     * @return true, if the object can be written
     */
    public boolean isWritable(URI pdUri) {

        return false;
    }

    /**
     * Get the content models of the object
     * @param pdUri the id of the object. Must start with "info:fedora/"
     * @return The uris of the content models of the object
     */
    public Set<URI> getContentModels(URI pdUri) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Check if the object exist in the repository
     * @param pdUri the id of the object. Must start with "info:fedora/"
     * @return true if the object exist in the repository
     */
    public boolean exist(URI pdUri) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Check the given content model, to see if it is a planets conversion
     * content model. 
     * @param cm the id of the content model.
     * @return true if the model can be used to forge the objects to planets objects
     */
    public boolean isPlanetsModel(URI cm) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Get the datastream as a document
     * @param planetsModel
     * @param dsid
     * @return
     */
    public Document getDatastream(URI planetsModel, String dsid) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Get a public url to a datastream
     * @param planetsModel
     * @param dsid
     * @return
     */
    public URL getDatastreamAsUrl(URI planetsModel, String dsid) {
        return null;
    }

    /**
     * Modify a datastream
     * @param pdURI
     * @param filedatastream
     * @param inputStream
     * @return
     */
    public boolean modifyDatastream(URI pdURI, String filedatastream, InputStream inputStream) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
