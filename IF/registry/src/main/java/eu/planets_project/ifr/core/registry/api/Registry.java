package eu.planets_project.ifr.core.registry.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Registry interface based on the new ServiceDescription objects, supporting
 * query by example.
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = Registry.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface Registry {

    String NAME = "Registry";
    QName QNAME = new QName(PlanetsServices.NS, Registry.NAME);

    /**
     * @param serviceDescription The service description to register
     * @return A response message
     */
    @WebMethod
    @WebResult
    Response register(@WebParam ServiceDescription serviceDescription);

    /**
     * Query by example registry lookup.
     * @param example The sample service description
     * @return The services for which all non-null values correspond to the
     *         values of the given sample object
     */
    @WebMethod
    @WebResult
    List<ServiceDescription> query(@WebParam ServiceDescription example);

    /**
     * Clears the registry of all entries.
     * @return A response message
     */
    @WebMethod
    @WebResult
    Response clear();

    // **********************************************************************

    // TODO Do XML-based methods make sense at all here?
    // /**
    // * @param xmlServiceDescription The service description XML to register
    // * @return A status message
    // */
    // ServiceRegistryMessage register(String xmlServiceDescription);

    // TODO Do XML-based methods make sense at all here?
    // /**
    // * Query by example registry lookup.
    // * @param sampleXmlServiceDescription The sample service description XML
    // * @return The services for which all non-null values correspond to the
    // * values of the given sample object
    // */
    // List<ServiceDescription> query(String sampleXmlServiceDescription);

    // **********************************************************************
}
