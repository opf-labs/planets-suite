package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXcdlReferences;
import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;

/**
 * PP comparator service, comparing two XCDLs given as references into the IF
 * data registry, using the default configuration and returning a reference to
 * the result of the comparison.
 * @author Fabian Steeg
 */
@WebService(name = ComparatorBasicCompareTwoXcdlReferences.NAME, serviceName = BasicCompareTwoXcdlReferences.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicCompareTwoXcdlReferences.class)
@Remote(BasicCompareTwoXcdlReferences.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class ComparatorBasicCompareTwoXcdlReferences implements
        BasicCompareTwoXcdlReferences, Serializable {
    /***/
    private static final long serialVersionUID = 913872475853395544L;
    /***/
    static final String NAME = "ComparatorBasicCompareTwoXcdlReferences";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXcdlReferences#basicCompareTwoXcdlReferences(java.net.URI,
     *      java.net.URI)
     */
    @WebMethod(operationName = BasicCompareTwoXcdlReferences.NAME, action = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlReferences.NAME)
    @WebResult(name = BasicCompareTwoXcdlReferences.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlReferences.NAME, partName = BasicCompareTwoXcdlReferences.NAME
            + "Result")
    public URI basicCompareTwoXcdlReferences(
            @WebParam(name = "xcdl1", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlReferences.NAME) final URI xcdl1,
            @WebParam(name = "xcdl2", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlReferences.NAME) final URI xcdl2) {
        DataRegistryAccessHelper helper = new DataRegistryAccessHelper();
        String xcdl1Value = new String(helper.read(xcdl1.toASCIIString()));
        String xcdl2Value = new String(helper.read(xcdl2.toASCIIString()));
        String compare = ComparatorWrapper.compare(xcdl1Value, Arrays
                .asList(xcdl2Value), null);
        return helper.write(compare.getBytes(), System.currentTimeMillis()
                + "CPR.CPR", "Planets-PP-Comparator");
    }
}
