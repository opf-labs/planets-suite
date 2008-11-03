/**
 * 
 */
package eu.planets_project.services.utils.test;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

/**
 * Service creation utilities for use when using testing.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceCreator {
    
    /**
     * This code handles the grotty details when instanciating a Planets service class for testing.
     * 
     * It reads the environment variables and sets up the test accordingly.
     * 
     * @param <T> The type of the class to be tested.
     * @param qname The QName of the service you want to invoke.
     * @param so An instance of your service that you wish to test.
     * @param wsdlLoc The location of the WSDL, relative to the root server context. e.g. "/pserv-if-simple/SimpleIdentifyService?wsdl"
     * @return A new instance of a class that implements the givne interface.
     * @throws MalformedURLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static <T> T createTestService(  QName qname, Class<T> so, String wsdlLoc ) 
            throws MalformedURLException, InstantiationException, IllegalAccessException {
        URL url = null;
        T ids = null;
        
        // Set up the remote version, if applicable:
        if ("standalone".equals(System.getenv("pserv.test.context"))
                || "server".equals(System.getenv("pserv.test.context"))) {

            /* In the standalone case, start up the test endpoint. */
            if (System.getenv("pserv.test.context").equals("standalone")) {

                System.out.println("INIT: Setting up temporary test server.");

                // Set up a temporary service with the code deployed at the
                // specified address:
                Endpoint testEndpoint = Endpoint
                        .create(so.newInstance());
                url = new URL(
                        "http://localhost:18367" + wsdlLoc );
                testEndpoint.publish(url.toString());

            }
            // In the server case, pick the server config up:
            else {
                String host = System.getenv("pserv.test.host")+":"+System.getenv("pserv.test.port");
                System.out
                        .println("INIT: Configuring against server at " + host );
                url = new URL( "http://" + host + wsdlLoc );

            }

            System.out.println("INIT: Creating the proxied service class.");
            Service service = Service.create( url, qname );
            ids = (T) service.getPort(so.getInterfaces()[0]);
            System.out.println("INIT: Created proxy class for service " + service.getServiceName() );
        }
        // If no remote context is configured, invoke locally:
        else {
            System.out.println("INIT: Creating a local instance.");
            ids = so.newInstance();
        }
        return ids;
    }

}
