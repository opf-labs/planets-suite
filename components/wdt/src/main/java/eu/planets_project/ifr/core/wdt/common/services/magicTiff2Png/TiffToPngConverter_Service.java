
package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Png;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Thu Jun 05 15:31:41 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "TiffToPngConverter", targetNamespace = "http://planets-project.eu/ifr/migration", wsdlLocation = "http://dme023:8080/ifr-jmagickconverter-ejb/TiffToPngConverter?wsdl")
public class TiffToPngConverter_Service
    extends Service
{

    private final static URL TIFFTOPNGCONVERTER_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://dme023:8080/ifr-jmagickconverter-ejb/TiffToPngConverter?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        TIFFTOPNGCONVERTER_WSDL_LOCATION = url;
    }

    public TiffToPngConverter_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TiffToPngConverter_Service() {
        super(TIFFTOPNGCONVERTER_WSDL_LOCATION, new QName("http://planets-project.eu/ifr/migration", "TiffToPngConverter"));
    }

    /**
     * 
     * @return
     *     returns TiffToPngConverter
     */
    @WebEndpoint(name = "TiffToPngConverterPort")
    public TiffToPngConverter getTiffToPngConverterPort() {
        return (TiffToPngConverter)super.getPort(new QName("http://planets-project.eu/ifr/migration", "TiffToPngConverterPort"), TiffToPngConverter.class);
    }

}
