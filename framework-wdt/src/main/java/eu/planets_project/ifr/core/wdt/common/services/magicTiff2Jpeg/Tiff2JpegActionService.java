
package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 11:30:07 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "Tiff2JpegActionService", targetNamespace = "http://tiff2jpg.planets.bl.uk/", wsdlLocation = "http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl")
public class Tiff2JpegActionService
    extends Service
{

    private final static URL TIFF2JPEGACTIONSERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/ImageMagicWS/Tiff2JpegAction?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        TIFF2JPEGACTIONSERVICE_WSDL_LOCATION = url;
    }

    public Tiff2JpegActionService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Tiff2JpegActionService() {
        super(TIFF2JPEGACTIONSERVICE_WSDL_LOCATION, new QName("http://tiff2jpg.planets.bl.uk/", "Tiff2JpegActionService"));
    }

    /**
     * 
     * @return
     *     returns Tiff2Jpeg
     */
    @WebEndpoint(name = "Tiff2JpegActionPort")
    public Tiff2Jpeg getTiff2JpegActionPort() {
        return (Tiff2Jpeg)super.getPort(new QName("http://tiff2jpg.planets.bl.uk/", "Tiff2JpegActionPort"), Tiff2Jpeg.class);
    }

}
