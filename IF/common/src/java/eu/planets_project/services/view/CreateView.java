/**
 * 
 */
package eu.planets_project.services.view;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.ResponseWrapper;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * The purpose of the CreateView operation is to take a Digital Object and to wrap it up so
 * that the user can examine it more easily.  The service returns a URL pointing to the web site or 
 * downloadable package that will provide the rendering experience to the user.
 * 
 * It is envisaged that this URL will be passed back to the user as a new link to open in a new browser window.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(name = CreateView.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public interface CreateView extends PlanetsService {

    public static final String NAME = "CreateView";
    
    public static final QName QNAME = new QName(PlanetsServices.NS,
            CreateView.NAME);

    /**
     * @param digitalObject 
     *            The Digital Object to be identified.
     * @return Returns a Types object containing the identification result
     */
    @WebMethod(operationName = CreateView.NAME, action = PlanetsServices.NS
            + "/" + CreateView.NAME)
    @WebResult(name = CreateView.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CreateView.NAME, partName = CreateView.NAME
            + "Result")
    public CreateViewResult createView(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + CreateView.NAME, partName = "digitalObject") 
            DigitalObject digitalObject);
    
    /**
     * A method that can be used to recover a rich service description, and thus populate a service registry.
     * @return An ServiceDescription object that describes this service, to aid service discovery.
     */
    @WebMethod(operationName = CreateView.NAME + "_describe", action = PlanetsServices.NS
            + "/" + CreateView.NAME + "/describe")
    @WebResult(name = CreateView.NAME + "Description", targetNamespace = PlanetsServices.NS
            + "/" + CreateView.NAME, partName = CreateView.NAME
            + "Description")
    @ResponseWrapper(className="eu.planets_project.services.view."+CreateView.NAME+"DescribeResponse")
    public ServiceDescription describe();
}
