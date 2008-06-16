
package eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Jun 13 17:25:47 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "JpgToTiffConverter", targetNamespace = "http://planets-project.eu/ifr/migration", wsdlLocation = "http://planetarium.hki.uni-koeln.de:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl")
public class JpgToTiffConverter_Service
    extends Service
{

    private final static URL JPGTOTIFFCONVERTER_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://planetarium.hki.uni-koeln.de:8080/ifr-jmagickconverter-ejb/JpgToTiffConverter?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JPGTOTIFFCONVERTER_WSDL_LOCATION = url;
    }

    public JpgToTiffConverter_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public JpgToTiffConverter_Service() {
        super(JPGTOTIFFCONVERTER_WSDL_LOCATION, new QName("http://planets-project.eu/ifr/migration", "JpgToTiffConverter"));
    }

    /**
     * 
     * @return
     *     returns JpgToTiffConverter
     */
    @WebEndpoint(name = "JpgToTiffConverterPort")
    public JpgToTiffConverter getJpgToTiffConverterPort() {
        return (JpgToTiffConverter)super.getPort(new QName("http://planets-project.eu/ifr/migration", "JpgToTiffConverterPort"), JpgToTiffConverter.class);
    }

}
