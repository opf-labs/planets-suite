
package eu.planets_project.services.characterisation;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Jul 04 13:08:18 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "DataManager", targetNamespace = "http://planets-project.eu/ifr/core/storage/data", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/storage-ifr-storage-ejb/DataManager?wsdl")
public class DataManager_Service
    extends Service
{

    private final static URL DATAMANAGER_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/storage-ifr-storage-ejb/DataManager?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DATAMANAGER_WSDL_LOCATION = url;
    }

    public DataManager_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DataManager_Service() {
        super(DATAMANAGER_WSDL_LOCATION, new QName("http://planets-project.eu/ifr/core/storage/data", "DataManager"));
    }

    /**
     * 
     * @return
     *     returns DataManager
     */
    @WebEndpoint(name = "DataManagerPort")
    public DataManager getDataManagerPort() {
        return (DataManager)super.getPort(new QName("http://planets-project.eu/ifr/core/storage/data", "DataManagerPort"), DataManager.class);
    }

}
