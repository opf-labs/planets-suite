package eu.planets_project.fedora;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableDigitalObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URI;



/**
 * DigitalObjectManager implementation for Fedora.
 * <br>
 * This class enables you to import and export Digital Objects from
 * a Fedora Repository.
 * <br>
 * Certain standards are enforced.
 * TODO
 *
 */
public class FedoraDigitalObjectManagerImpl implements DigitalObjectManager {

    private String username;
    private String password;
    private String server;

    public static final String transformMethod = "transform";

    public FedoraDigitalObjectManagerImpl(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.server = server;
    }


    /**
     * Dumps the object back into Fedora. Not implemented
     *
     * <br>
     * First examines whether or not the object exist in the repository already
     * If it does, update the object.
     * If not, create a new object.
     * <br>
     * TODO
     * This method has several major issues, that must be resolved beforehand
     *
     * <ul>
     * <li>How to handle contained objects
     * <li>How to handle Metadata. Should the type-URI be the datastream name?
     * <li>Content ref, the export makes a link to Fedora. This cannot be stored back...
     * <li>Which datastream to use for content
     * <li>Should the object be associated with the content model.
     * <li>How to figure out references, or relations
     * <li>And there is probably more....
     * </ul>
     *
     * @param pdURI
     *            The URI which uniquely identifies the persisted DigitalObject
     * @param digitalObject
     * @throws DigitalObjectNotStoredException
     */
    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new DigitalObjectNotStoredException("Not implemented yet");
    }

    private void createNewObject(){

    }

    private void updateExistingObject(){
        
    }

    /**
     * Retrieves a digital object from a fedora repository.
     * <br>
     * The fedora repository is queried through it's REST interface.
     * <br>
     *
     * This method draws upon a custom Fedora disseminator, that
     * transform the object to planets xml format.
     * <br>
     * <ul>
     * <li>The object title is set to the fedora object title
     * <li>The DC datastream is stored as a Metadata object
     * <li>The Content datastream is stored as a reference in the content object
     * <li>The permanentURL is set the the url of the object in the repository
     *</ul>

     *
     * @param pdURI
     *            URI with the fedora pid of the object
     * @return a new immutable digital object with the information from the repository
     * @throws DigitalObjectNotFoundException If the object is not found in the repository
     * @throws RepositoryException If there is a problem communication with the repository
     */
    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException, RepositoryException {

        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        Credentials creds = new UsernamePasswordCredentials(username,password);
        client.getState().setCredentials(AuthScope.ANY,creds);
        //above sets the credentials to use, for ANY host. This is
        // a security problem, but then, we do not use this client
        // outside this method

        //first we check that the object exist
        String methodurl = server+"/objects/"+pdURI.toString()+"/methods.xml";
        HttpMethod getmethods = new GetMethod(methodurl);

        try {
            int methodstatus = client.executeMethod(getmethods);
            if (methodstatus != HttpStatus.SC_OK){
                throw new DigitalObjectNotFoundException("The given URI "+pdURI+" does not refer to a object in the repository");
            }
            //then we discover if it has a transform method


            XPathFactory xpathfac = XPathFactory.newInstance();
            XPath xpath = xpathfac.newXPath();

            String sdefpid = null;
            try {
                sdefpid = xpath.compile("//method[@name='"+transformMethod+"']/../@pid").
                    evaluate(new InputSource(getmethods.getResponseBodyAsStream()));
            } catch (XPathExpressionException e) {
                throw new DigitalObjectNotFoundException("The given URI "+pdURI+" does not refer to a object in the repository");
            } finally {
                getmethods.releaseConnection();
            }

            if (sdefpid.equals("")){
                throw new DigitalObjectNotFoundException("Object found, but is not exportable to planets");
            }

            //then we invoke the transform method
            String transformurl = server + "/get/" + pdURI.toString() + "/" + sdefpid + "/" + transformMethod;

            HttpMethod getobject = new GetMethod(transformurl);
            // Provide custom retry handler is necessary
            getobject.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));

            int statuscode = client.executeMethod(getobject);
            if (statuscode != HttpStatus.SC_OK){
                throw new DigitalObjectNotFoundException("There was a problem retrieving the object from the repository");
            }

            //This cause a warning, but we need the object as string, for
            //the deserilization, so tough
            String objectxml = getobject.getResponseBodyAsString();

            getobject.releaseConnection();
            if (objectxml.startsWith("<?xml")){
                DigitalObject retrieved = ImmutableDigitalObject.of(objectxml);
                return retrieved;
            } else{
                throw new DigitalObjectNotFoundException("The object could not be ported to planets");
            }

        } catch (IOException e) {
            throw new RepositoryException("Problem communication with the repository",e);
        }

    }

    /**
     * This method makes little sense for Fedora. There is no
     * logical hierachy for fedora digital objects. You should subclass
     * and reimplement this method for your own repository, depending
     * on the datamodel you use.
     * @param pdURI
     *            URI that identifies an Digital Object or folder
     * @return
     */
    public URI[] list(URI pdURI) {
        return new URI[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
