package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.Serializable;
import java.util.Arrays;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.CompareMultipleXcdlValues;

/**
 * PP comparator service, comparing multiple XCDL strings, using the given
 * configuration string and returning a result string for the comparison.
 * @author Fabian Steeg
 * @deprecated Use {@link XcdlCompare} instead
 */
@WebService(name = ComparatorCompareMultipleXcdlValues.NAME, serviceName = CompareMultipleXcdlValues.NAME, 
        targetNamespace = PlanetsServices.NS, endpointInterface="eu.planets_project.services.compare.CompareMultipleXcdlValues")
@Local(CompareMultipleXcdlValues.class)
@Remote(CompareMultipleXcdlValues.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class ComparatorCompareMultipleXcdlValues implements
        CompareMultipleXcdlValues, Serializable {
    /***/
    private static final long serialVersionUID = -7694062288388666720L;
    /***/
    static final String NAME = "ComparatorCompareMultipleXcdlValues";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareMultipleXcdlValues#compareMultipleXcdlValues(java.lang.String[],
     *      java.lang.String)
     */
    @WebMethod(operationName = CompareMultipleXcdlValues.NAME, action = PlanetsServices.NS
            + "/" + CompareMultipleXcdlValues.NAME)
    @WebResult(name = CompareMultipleXcdlValues.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CompareMultipleXcdlValues.NAME, partName = CompareMultipleXcdlValues.NAME
            + "Result")
    public String compareMultipleXcdlValues(
            @WebParam(name = "xcdls", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlValues.NAME) final String[] xcdls,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlValues.NAME) final String config) {
        return ComparatorWrapper.compare(xcdls[0], Arrays.asList(xcdls)
                .subList(1, xcdls.length), config);
    }
}
