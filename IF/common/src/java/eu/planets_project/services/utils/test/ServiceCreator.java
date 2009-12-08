/**
 * 
 */
package eu.planets_project.services.utils.test;

import static eu.planets_project.services.utils.ServiceUtils.createService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

/**
 * Service creation utilities for use when using testing.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>, <a
 *         href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
public final class ServiceCreator {

    /**
     * We enforce non-instantiability with a private constructor (this is a
     * utility class).
     */
    private ServiceCreator() {
    }

    private static Logger log = Logger.getLogger(ServiceCreator.class.getName());

    /**
     * Modes for creating a service implementation instance.
     * @author Fabian Steeg (fabian.steeg@netcologne.de)
     */
    public enum Mode {
        /** Use a local instance. */
        LOCAL {
            /**
             * {@inheritDoc}
             * @see eu.planets_project.services.utils.test.ServiceCreator.Creator#create(javax.xml.namespace.QName,
             *      java.lang.Class, java.lang.String)
             */
            public <T> T create(QName qname, Class<T> impl, String wsdl) {
                log.info("INIT: Creating a local instance.");
                try {
                    return impl.newInstance();
                } catch (InstantiationError e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            };
        },
        /** Use a standalone server. */
        STANDALONE {
            /**
             * {@inheritDoc}
             * @see eu.planets_project.services.utils.test.ServiceCreator.Creator#create(javax.xml.namespace.QName,
             *      java.lang.Class, java.lang.String)
             */
            public <T> T create(QName qname, Class<T> impl, String wsdl) {
                URL url = null;
                try {
                    url = setupTempTestServer(impl, wsdl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return createService(qname, impl, url);
            }
        },
        /** Use a running server. */
        SERVER {
            /**
             * {@inheritDoc}
             * @see eu.planets_project.services.utils.test.ServiceCreator.Creator#create(javax.xml.namespace.QName,
             *      java.lang.Class, java.lang.String)
             */
            public <T> T create(QName qname, Class<T> impl, String wsdl) {
                URL url = null;
                try {
                    url = setupServer(wsdl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return createService(qname, impl, url);
            }
        };
        /**
         * @param <T> The interface type
         * @param qname The qualified name
         * @param impl The service impl class
         * @param wsdl The wsdl
         * @return An instance of the given class
         */
        abstract <T> T create(QName qname, Class<T> impl, String wsdl);
        
        
    }

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
    public static <T> T createTestService(QName qname,
            Class<T> serviceImplementation, String wsdlLoc) {
        try {
            // In the standalone case, start up the test endpoint.
            if ( testInStandaloneMode() ) {
                return Mode.STANDALONE.create(qname, serviceImplementation,
                        wsdlLoc);
            }

            // In the server case, pick the server config up:
            else if (testInServerMode()  ) 
            {
                /*
                System.setProperty("proxySet","true");
                System.setProperty("http.proxyHost","loncache.bl.uk");
                System.setProperty("http.proxyPort","8080");
                System.setProperty("http.nonProxyHosts","localhost|127.0.0.1|*.ad.bl.uk");
                */
                System.out.println("INFO: Proxy is set to "+System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort"));
                
                return Mode.SERVER.create(qname, serviceImplementation, wsdlLoc);
            }

            // If no remote context is configured, invoke locally:
            else {
                return Mode.LOCAL.create(qname, serviceImplementation, wsdlLoc);
            }
        } catch( Exception e ) {
            log.severe("Instanciation of test service failed! "+e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * This code handles the grotty details when instanciating a Planets service
     * class for testing.
     * @param <T> The type of the class to be tested.
     * @param qname The QName of the service interface you want to invoke.
     * @param serviceImplementation The class of your service implementation
     *        that you wish to test.
     * @param wsdlLoc The location of the WSDL, relative to the root server
     *        context. e.g. "/pserv-if-simple/SimpleIdentifyService?wsdl"
     * @param mode The testing mode to use, e.g. Mode.SERVER
     * @return A new instance of a class that implements the given interface,
     *         should be assigned using the interface, not an implementation.
     */
    public static <T> T createTestService(QName qname,
            Class<T> serviceImplementation, String wsdlLoc, Mode mode) {
        return mode.create(qname, serviceImplementation, wsdlLoc);
    }

    static final String TEST_MODE_SERVER = "server";
    static final String TEST_MODE_STANDALONE = "standalone";
    static final String TEST_MODE_CONTEXT_FLAG = "pserv.test.context";
    
    static boolean testInServerMode() {
        if( TEST_MODE_SERVER.equals( System.getProperty( TEST_MODE_CONTEXT_FLAG ))) return true;
        return false;
    }

    static boolean testInStandaloneMode() {
        if( TEST_MODE_STANDALONE.equals( System.getProperty( TEST_MODE_CONTEXT_FLAG ))) return true;
        return false;
    }

    private static URL setupServer(String wsdlLoc) throws MalformedURLException {
        URL url;
        String hostAdress = System.getProperty("pserv.test.host");
        String host = hostAdress + ":" + System.getProperty("pserv.test.port");
        /* If we have the props, use them, else we take the default: */
        if (hostAdress == null) {
            System.err
                    .println("WARNING: No system properties set, falling back to default.");
            host = "localhost:8080";
        }
        url = new URL("http://" + host + wsdlLoc);
        System.out.println("INIT: Configuring against server at " + url );
        return url;
    }

    private static <T> URL setupTempTestServer(Class<T> serviceImplementation,
            String wsdlLoc) throws InstantiationException,
            IllegalAccessException, MalformedURLException {
        URL url;
        log.info("INIT: Setting up temporary test server.");
        // Set up a temporary service with the code deployed at the
        // specified address:
        Endpoint testEndpoint = Endpoint.create(serviceImplementation
                .newInstance());
        url = new URL("http://localhost:18367" + wsdlLoc);
        System.out.println("INIT: Configuring standalone server at " + url);
        testEndpoint.publish(url.toString());
        return url;
    }

}
