package eu.planets_project.ifr.core.registry.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.ifr.core.registry.impl.MatchingMode;
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

    /** The interface name. */
    String NAME = "Registry";
    /** The qualified name. */
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
     * Query by example registry lookup with a specified lookup strategy.
     * @param example The sample service description
     * @param mode The matching strategy to use when matching against the given
     *        sample
     * @return The services for which all non-null values correspond to the
     *         values of the given sample object, based on the supplied matching
     *         strategy
     */
    @WebMethod
    @WebResult
    /*
     * This method does not overload query(...) as overloading is not supported
     * in recent versions of the WSDL (1.2, 2.0)
     */
    List<ServiceDescription> queryWithMode(
            @WebParam ServiceDescription example, @WebParam MatchingMode mode);

    /**
     * Clears the registry of all entries.
     * @return A response message
     */
    @WebMethod
    @WebResult
    Response clear();

    /**
     * @param example The sample of the service descriptions to delete
     * @return A response message
     */
    @WebMethod
    @WebResult
    Response delete(@WebParam ServiceDescription example);
}
