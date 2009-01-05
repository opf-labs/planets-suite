/**
 * 
 */
package eu.planets_project.ifr.core.sample.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;

/**
 * 
 * This is not yet a real test, but I want to see if I can invoke services remotely. Seems not to work!
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class SimpleRemoteCallTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // http://testbed.planets-project.eu/pserv-pc-droid/Droid?wsdl
            // http://www.thebiscuit.co.uk/redirect.php
//            URL endpointUrl = new URL("http://testbed.planets-project.eu/pserv-pc-droid/Droid?wsdl");
            // This endpont issues a HTTP 307 redirect to the above TB endpoint, thus showing that this redirection works:
            URL endpointUrl = new URL("http://www.thebiscuit.co.uk/redirect.php");
            
            //-DproxySet=true -Dhttp.proxyHost=bspcache.bl.uk -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts="localhost|127.0.0.1|*.ad.bl.uk" 
            System.setProperty("proxySet","true");
            System.setProperty("http.proxyHost", "bspcache.bl.uk");
            System.setProperty("http.proxyPort", "8080");
            System.setProperty("http.nonProxyHosts","localhost|127.0.0.1|*.ad.bl.uk");
            
            Service service = Service.create( endpointUrl, Identify.QNAME);
            System.out.println("Using WSDL from: "+service.getWSDLDocumentLocation());
            Iterator<QName> ports = service.getPorts();
            while( ports.hasNext() ) {
                System.out.println("Got : "+ports.next());
            }
            Identify droid = (Identify) service.getPort( Identify.class );
            IdentifyResult result = droid.identify(new DigitalObject.Builder(
                    Content.byValue(new File("PC/droid/src/resources/Licence.rtf"))).build());
            System.out.println("Result: "+result.getTypes());
            System.exit(1);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
    }

}
