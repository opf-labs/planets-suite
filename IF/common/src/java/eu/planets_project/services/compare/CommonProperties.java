package eu.planets_project.services.compare;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;

/**
 * Determine common properties of different file formats. Implementing services
 * provide a list of common file format properties given identifiers of file
 * formats.
 * @author Thomas Kraemer thomas.kraemer@uni-koeln.de, Fabian Steeg
 *         (fabian.steeg@uni-koeln.de)
 */
@WebService(name = CommonProperties.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface CommonProperties extends PlanetsService {
    /** The interface name. */
    String NAME = "CommonProperties";
    /** The qualified name. */
    QName QNAME = new QName(PlanetsServices.NS, CommonProperties.NAME);

    /**
     * @param formatIds File format IDs (PRONOM)
     * @return Returns the set of common properties of the specified file
     *         formats (in a compare result object)
     */
    @WebMethod(operationName = CommonProperties.NAME, action = PlanetsServices.NS
            + "/" + CommonProperties.NAME)
    @WebResult(name = CommonProperties.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CommonProperties.NAME, partName = CommonProperties.NAME
            + "Result")
    CompareResult of(@WebParam(targetNamespace = PlanetsServices.NS + "/"
            + CommonProperties.NAME) List<URI> formatIds);

}
