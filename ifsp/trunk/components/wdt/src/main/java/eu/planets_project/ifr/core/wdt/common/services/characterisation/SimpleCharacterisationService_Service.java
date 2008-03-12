
package eu.planets_project.ifr.core.wdt.common.services.characterisation;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 09:40:05 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "SimpleCharacterisationService", targetNamespace = "http://services.planets-project.eu/ifr/characterisation", wsdlLocation = "http://dme023:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl")
public class SimpleCharacterisationService_Service
    extends Service
{

    private final static URL SIMPLECHARACTERISATIONSERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/sample-ifr-sample-ejb/SimpleCharacterisationService?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SIMPLECHARACTERISATIONSERVICE_WSDL_LOCATION = url;
    }

    public SimpleCharacterisationService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SimpleCharacterisationService_Service() {
        super(SIMPLECHARACTERISATIONSERVICE_WSDL_LOCATION, new QName("http://services.planets-project.eu/ifr/characterisation", "SimpleCharacterisationService"));
    }

    /**
     * 
     * @return
     *     returns SimpleCharacterisationService
     */
    @WebEndpoint(name = "SimpleCharacterisationServicePort")
    public SimpleCharacterisationService getSimpleCharacterisationServicePort() {
        return (SimpleCharacterisationService)super.getPort(new QName("http://services.planets-project.eu/ifr/characterisation", "SimpleCharacterisationServicePort"), SimpleCharacterisationService.class);
    }

}
