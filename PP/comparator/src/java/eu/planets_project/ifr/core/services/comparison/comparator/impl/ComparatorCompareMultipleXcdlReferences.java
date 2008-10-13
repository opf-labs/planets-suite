package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.CompareMultipleXcdlReferences;

/**
 * PP comparator service, comparing multiple XCDLs given as references into the
 * IF data registry, using the referenced configuration and returning a
 * reference to the result of the comparison.
 * @author Fabian Steeg
 */
@WebService(name = ComparatorCompareMultipleXcdlReferences.NAME, serviceName = CompareMultipleXcdlReferences.NAME, targetNamespace = PlanetsServices.NS)
@Local(CompareMultipleXcdlReferences.class)
@Remote(CompareMultipleXcdlReferences.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class ComparatorCompareMultipleXcdlReferences implements
        CompareMultipleXcdlReferences, Serializable {
    /***/
    private static final long serialVersionUID = 2237322311829343232L;
    /***/
    static final String NAME = "ComparatorCompareMultipleXcdlReferences";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.CompareMultipleXcdlReferences#compareMultipleXcdlReferences(java.util.List,
     *      java.lang.String)
     */
    @WebMethod(operationName = CompareMultipleXcdlReferences.NAME, action = PlanetsServices.NS
            + "/" + CompareMultipleXcdlReferences.NAME)
    @WebResult(name = CompareMultipleXcdlReferences.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + CompareMultipleXcdlReferences.NAME, partName = CompareMultipleXcdlReferences.NAME
            + "Result")
    public URI compareMultipleXcdlReferences(
            @WebParam(name = "xcdls", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlReferences.NAME) final URI[] xcdls,
            @WebParam(name = "config", targetNamespace = PlanetsServices.NS
                    + "/" + CompareMultipleXcdlReferences.NAME) final URI config) {
        DataRegistryAccessHelper helper = new DataRegistryAccessHelper();
        String xcdl1Value = new String(helper.read(xcdls[0].toASCIIString()));
        List<String> otherXcdlValues = new ArrayList<String>();
        for (int i = 1; i < xcdls.length; i++) {
            otherXcdlValues.add(new String(helper
                    .read(xcdls[i].toASCIIString())));
        }
        String configValue = new String(helper.read(config.toASCIIString()));
        String compare = ComparatorWrapper.compare(xcdl1Value, otherXcdlValues,
                configValue);
        return helper.write(compare.getBytes(), System.currentTimeMillis()
                + "CPR.CPR", "Planets-PP-Comparator");
    }
}
