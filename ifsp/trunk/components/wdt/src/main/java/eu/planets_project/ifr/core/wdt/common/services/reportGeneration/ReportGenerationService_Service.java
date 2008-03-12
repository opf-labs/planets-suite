
package eu.planets_project.ifr.core.wdt.common.services.reportGeneration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 12:21:00 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebServiceClient(name = "ReportGenerationService", targetNamespace = "http://services.planets-project.eu/ifr/reporting", wsdlLocation = "http://dme023:8080/ReportGenerationService/ReportGenerationService?wsdl")
public class ReportGenerationService_Service
    extends Service
{

    private final static URL REPORTGENERATIONSERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://dme023:8080/ReportGenerationService/ReportGenerationService?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        REPORTGENERATIONSERVICE_WSDL_LOCATION = url;
    }

    public ReportGenerationService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ReportGenerationService_Service() {
        super(REPORTGENERATIONSERVICE_WSDL_LOCATION, new QName("http://services.planets-project.eu/ifr/reporting", "ReportGenerationService"));
    }

    /**
     * 
     * @return
     *     returns ReportGenerationService
     */
    @WebEndpoint(name = "ReportGenerationServicePort")
    public ReportGenerationService getReportGenerationServicePort() {
        return (ReportGenerationService)super.getPort(new QName("http://services.planets-project.eu/ifr/reporting", "ReportGenerationServicePort"), ReportGenerationService.class);
    }

}
