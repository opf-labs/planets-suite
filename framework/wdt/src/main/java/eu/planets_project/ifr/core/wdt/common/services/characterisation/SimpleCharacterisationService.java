
package eu.planets_project.ifr.core.wdt.common.services.characterisation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 09:40:05 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "SimpleCharacterisationService", targetNamespace = "http://services.planets-project.eu/ifr/characterisation")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SimpleCharacterisationService {


    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String characteriseFile(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String characteriseFileDH(
        @WebParam(name = "arg0", partName = "arg0")
        byte[] arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns net.java.dev.jaxb.array.StringArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray characteriseFileDHs(
        @WebParam(name = "arg0", partName = "arg0")
        Base64BinaryArray arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    public String characteriseFileURL(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns net.java.dev.jaxb.array.StringArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray characteriseFileURLs(
        @WebParam(name = "arg0", partName = "arg0")
        AnyURIArray arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns net.java.dev.jaxb.array.StringArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public StringArray characteriseFiles(
        @WebParam(name = "arg0", partName = "arg0")
        StringArray arg0);

}
