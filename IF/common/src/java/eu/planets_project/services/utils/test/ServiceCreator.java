/**
 * 
 */
package eu.planets_project.services.utils.test;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service creation utilities for use when using testing.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public class ServiceCreator {

    /**
     * This code handles the grotty details when instanciating a Planets service
     * class for testing. It reads the environment variables and sets up the
     * test accordingly.
     * @param <T> The type of the class to be tested.
     * @param qname The QName of the service interface you want to invoke.
     * @param serviceImplementation The class of your service implementation
     *        that you wish to test.
     * @param wsdlLoc The location of the WSDL, relative to the root server
     *        context. e.g. "/pserv-if-simple/SimpleIdentifyService?wsdl"
     * @return A new instance of a class that implements the given interface,
     *         should be assigned using the interface, not an implementation.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createTestService(QName qname,
            Class<T> serviceImplementation, String wsdlLoc) {
        URL url = null;
        T ids = null;
        Log log = LogFactory.getLog(ServiceCreator.class.getName());

        try {
            // Set up the remote version, if applicable:
            if ("standalone".equals(System.getProperty("pserv.test.context"))
                    || "server".equals(System.getProperty("pserv.test.context"))) {

                /* In the standalone case, start up the test endpoint. */
                if (System.getProperty("pserv.test.context").equals("standalone")) {

                    log.info("INIT: Setting up temporary test server.");

                    // Set up a temporary service with the code deployed at the
                    // specified address:
                    Endpoint testEndpoint = Endpoint
                            .create(serviceImplementation.newInstance());
                    url = new URL("http://localhost:18367" + wsdlLoc);
                    testEndpoint.publish(url.toString());

                }
                // In the server case, pick the server config up:
                else {
                    String host = System.getProperty("pserv.test.host") + ":"
                            + System.getProperty("pserv.test.port");
                    System.out.println("INIT: Configuring against server at "
                            + host);
                    url = new URL("http://" + host + wsdlLoc);

                }

                log.info("INIT: Creating the proxied service class.");
                Service service = Service.create(url, qname);
                ids = (T) service
                        .getPort(serviceImplementation.getInterfaces()[0]);
                log.info("INIT: Created proxy class for service "
                        + service.getServiceName());
            }
            // If no remote context is configured, invoke locally:
            else {
                log.info("INIT: Creating a local instance.");
                ids = serviceImplementation.newInstance();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return ids;
    }

}
