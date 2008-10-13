package eu.planets_project.ifr.core.services.validation.jhove.impl;

import java.io.Serializable;
import java.net.URI;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Types;
import eu.planets_project.services.validate.BasicValidateOneBinary;

/**
 * JHOVE validation service.
 * 
 * @author Fabian Steeg
 */
@WebService(name = JhoveValidation.NAME, serviceName = BasicValidateOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicValidateOneBinary.class)
@Remote(BasicValidateOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class JhoveValidation implements BasicValidateOneBinary,
        Serializable {
    /***/
    private static final long serialVersionUID = 2127494848765937613L;
    /***/
    static final String NAME = "JhoveValidation";

    /**
     * @param binary The binary file to validate
     * @param fmt The pronom URI the binary should be validated against
     * @return Returns true if the given pronom URI describes the given binary
     *         file, else false
     * @see eu.planets_project.services.validate.BasicValidateOneBinary#basicValidateOneBinary(byte[],
     *      java.net.URI)
     */
    @WebMethod(operationName = BasicValidateOneBinary.NAME, action = PlanetsServices.NS
            + "/" + BasicValidateOneBinary.NAME)
    @WebResult(name = BasicValidateOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicValidateOneBinary.NAME, partName = BasicValidateOneBinary.NAME
            + "Result")
    public boolean basicValidateOneBinary(
            @WebParam(name = "binary", targetNamespace = PlanetsServices.NS
                    + "/" + BasicValidateOneBinary.NAME, partName = "binary")
            final byte[] binary,
            @WebParam(name = "fmt", targetNamespace = PlanetsServices.NS + "/"
                    + BasicValidateOneBinary.NAME, partName = "fmt")
            final URI fmt) {
        /* Identify the binary: */
        JhoveIdentification identification = new JhoveIdentification();
        Types result = identification.identifyOneBinary(binary);
        /* And check it it is what we expected: */
        for (URI uri : result.types) {
            if (uri.equals(fmt)) {
                /* One of the identified types is the one we expected: */
                return true;
            }
        }
        return false;
    }
}
