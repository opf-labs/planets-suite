package eu.planets_project.fedora;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableDigitalObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.net.URI;

/**
 * TODO abr forgot to document this class
 */
public class DigitalObjectManagerImpl implements DigitalObjectManager {

    private String username;
    private String password;
    private String server;

    public DigitalObjectManagerImpl(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.server = server;
    }

    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {

        //first we check that the object exist
        String objecturl = server+"/objects/"+pdURI.toString();

        //then we discover if it has a transform method


        //then we invoke the transform method
        HttpClient client = new HttpClient();
        HttpMethod getobject = new GetMethod(objecturl);
        // Provide custom retry handler is necessary
        getobject.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        //then we invoke the transform method
        try {
            int statuscode = client.executeMethod(getobject);
            if (statuscode != HttpStatus.SC_OK){
                throw new DigitalObjectNotFoundException("There was a problem retrieving the object from the repository");
            }
            String objectxml = getobject.getResponseBodyAsString();
            DigitalObject retrieved = ImmutableDigitalObject.of(objectxml);
            return retrieved;
        }catch (HttpException e){
            throw new DigitalObjectNotFoundException("There was a problem retrieving the object from the repository");
        } catch (IOException e) {
            throw new DigitalObjectNotFoundException("There was a problem retrieving the object from the repository");
        } finally {
            getobject.releaseConnection();
        }


    }

    public URI[] list(URI pdURI) {
        return new URI[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
