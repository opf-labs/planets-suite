
package eu.planets_project.ifr.core.wdt.common.services.openXMLMigration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 10:17:05 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "OpenXMLMigration", targetNamespace = "http://planets-project.eu/ifr/core/services/migration", wsdlLocation = "http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl")
public class OpenXMLMigration_Service
    extends Service
{

    private final static URL OPENXMLMIGRATION_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/ifr-openXML-ejb/OpenXMLMigration?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        OPENXMLMIGRATION_WSDL_LOCATION = url;
    }

    public OpenXMLMigration_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public OpenXMLMigration_Service() {
        super(OPENXMLMIGRATION_WSDL_LOCATION, new QName("http://planets-project.eu/ifr/core/services/migration", "OpenXMLMigration"));
    }

    /**
     * 
     * @return
     *     returns OpenXMLMigration
     */
    @WebEndpoint(name = "OpenXMLMigrationPort")
    public OpenXMLMigration getOpenXMLMigrationPort() {
        return (OpenXMLMigration)super.getPort(new QName("http://planets-project.eu/ifr/core/services/migration", "OpenXMLMigrationPort"), OpenXMLMigration.class);
    }

}
