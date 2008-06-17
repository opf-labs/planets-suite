
package eu.planets_project.ifr.core.wdt.common.services.droid;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Tue Jun 17 17:21:05 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "Droid", targetNamespace = "http://planets-project.eu/services", wsdlLocation = "http://dme023:8080/pserv-pc-droid/Droid?wsdl")
public class Droid_Service
    extends Service
{

    private final static URL DROID_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://dme023:8080/pserv-pc-droid/Droid?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DROID_WSDL_LOCATION = url;
    }

    public Droid_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Droid_Service() {
        super(DROID_WSDL_LOCATION, new QName("http://planets-project.eu/services", "Droid"));
    }

    /**
     * 
     * @return
     *     returns Droid
     */
    @WebEndpoint(name = "DroidPort")
    public Droid getDroidPort() {
        return (Droid)super.getPort(new QName("http://planets-project.eu/services", "DroidPort"), Droid.class);
    }

}
