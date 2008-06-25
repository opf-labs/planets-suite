
package eu.planets_project.ifr.core.wdt.common.services.serviceRegistry;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Mon Jun 23 17:00:32 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "ServiceRegistryManager", targetNamespace = "http://planets-project.eu/ifr/core/registry", wsdlLocation = "http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl")
public class ServiceRegistryManager_Service
    extends Service
{

    private final static URL SERVICEREGISTRYMANAGER_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://dme023:8080/registry-ifr-registry-ejb/ServiceRegistryManager?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SERVICEREGISTRYMANAGER_WSDL_LOCATION = url;
    }

    public ServiceRegistryManager_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ServiceRegistryManager_Service() {
        super(SERVICEREGISTRYMANAGER_WSDL_LOCATION, new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManager"));
    }

    /**
     * 
     * @return
     *     returns ServiceRegistryManager
     */
    @WebEndpoint(name = "ServiceRegistryManagerPort")
    public ServiceRegistryManager getServiceRegistryManagerPort() {
        return (ServiceRegistryManager)super.getPort(new QName("http://planets-project.eu/ifr/core/registry", "ServiceRegistryManagerPort"), ServiceRegistryManager.class);
    }

}
