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

import org.apache.commons.codec.binary.Base64;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.BasicCompareTwoXcdlValues;

/**
 * PP comparator service, comparing two XCDL strings, using the default
 * configuration and returning a result string for the comparison.
 * @author Fabian Steeg
 * @deprecated Use {@link XcdlCompare} instead
 */
@WebService(name = ComparatorBasicCompareTwoXcdlValues.NAME, serviceName = BasicCompareTwoXcdlValues.NAME, 
        targetNamespace = PlanetsServices.NS, endpointInterface="eu.planets_project.services.compare.BasicCompareTwoXcdlValues")
@Local(BasicCompareTwoXcdlValues.class)
@Remote(BasicCompareTwoXcdlValues.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class ComparatorBasicCompareTwoXcdlValues implements
        BasicCompareTwoXcdlValues, Serializable {
    /***/
    private static final long serialVersionUID = 1198444561594720727L;
    /***/
    static final String NAME = "ComparatorBasicCompareTwoXcdlValues";

    /*
     * First, we have the interface implementation method, including full web
     * service interop annotations:
     */

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.BasicCompareTwoXcdlValues#basicCompareTwoXcdlValues(java.lang.String,
     *      java.lang.String)
     */
    @WebMethod(operationName = BasicCompareTwoXcdlValues.NAME, action = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlValues.NAME)
    @WebResult(name = BasicCompareTwoXcdlValues.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareTwoXcdlValues.NAME, partName = BasicCompareTwoXcdlValues.NAME
            + "Result")
    public String basicCompareTwoXcdlValues(
            @WebParam(name = "xcdl1", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlValues.NAME) final String xcdl1,
            @WebParam(name = "xcdl2", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCompareTwoXcdlValues.NAME) final String xcdl2) {
        return ComparatorWrapper.compare(xcdl1, Arrays.asList(xcdl2), null);
    }

    /* Then we have a few non-interface utility and convenience methods: */

    /**
     * Fully configurable method: specify the files, the config, and an ID.
     * @param xcdl1 The first XCDL
     * @param xcdl2 The second XCDL
     * @param config The PCR config file specifying the metrics for comparison
     * @return Returns the result of comparing the first and the second XCDL
     */
    @WebMethod
    @WebResult
    public String compareTwoXcdlValues(
            @WebParam(name = "xcdl1") final String xcdl1,
            @WebParam(name = "xcdl2") final String xcdl2,
            @WebParam(name = "config") final String config) {
        return ComparatorWrapper.compare(xcdl1, Arrays.asList(xcdl2), config);
    }

    /**
     * @param xcdl1Base64 The first XCDL, Base64 encoded
     * @param xcdl2Base64 The second XCDL, Base64 encoded
     * @param configBase64 The config file, Base64 encoded
     * @return Returns the result of comparing the first and the second XCDL,
     *         Base64 encoded
     */
    @WebMethod
    @WebResult
    public String compareTwoXcdlValuesBase64(
            @WebParam(name = "xcdl1Base64") final String xcdl1Base64,
            @WebParam(name = "xcdl2Base64") final String xcdl2Base64,
            @WebParam(name = "configBase64") final String configBase64) {
        String xcdl1 = new String(Base64.decodeBase64(xcdl1Base64.getBytes()));
        String xcdl2 = new String(Base64.decodeBase64(xcdl2Base64.getBytes()));
        String config = new String(Base64.decodeBase64(configBase64.getBytes()));
        String result = compareTwoXcdlValues(xcdl1, xcdl2, config);
        String resultBase64 = new String( Base64.encodeBase64(result.getBytes()) );
        return resultBase64;
    }

    /**
     * @param xcdl1Base64 The first XCDL, Base64 encoded
     * @param xcdl2Base64 The second XCDL, Base64 encoded
     * @return Returns the result of comparing the first and the second XCDL,
     *         Base64 encoded
     */
    @WebMethod
    @WebResult
    public String basicCompareTwoXcdlValuesBase64(
            @WebParam(name = "xcdl1Base64") final String xcdl1Base64,
            @WebParam(name = "xcdl2Base64") final String xcdl2Base64) {
        String xcdl1 = new String(Base64.decodeBase64(xcdl1Base64.getBytes()));
        String xcdl2 = new String(Base64.decodeBase64(xcdl2Base64.getBytes()));
        String result = basicCompareTwoXcdlValues(xcdl1, xcdl2);
        String resultBase64 = new String( Base64.encodeBase64(result.getBytes()) );
        return resultBase64;
    }

    /**
     * Helper/mock method for testing, using file locations instead of the
     * actual data. Calls the actual comparison method above.
     * @param xcdl1Name The location of the first XCDL
     * @param xcdl2Name The location of the second XCDL
     * @return Returns the result of comparing the first and the second XCDL
     */
    @WebMethod
    @WebResult
    public String basicCompareTwoXcdlFiles(
            @WebParam(name = "xcdl1Name") final String xcdl1Name,
            @WebParam(name = "xcdl2Name") final String xcdl2Name) {
        String content1 = ComparatorWrapper.read(xcdl1Name);
        String content2 = ComparatorWrapper.read(xcdl2Name);
        return basicCompareTwoXcdlValues(content1, content2);
    }
}
