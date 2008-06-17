
package eu.planets_project.ifr.core.wdt.common.services.droid;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Tue Jun 17 17:21:05 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "Droid", targetNamespace = "http://planets-project.eu/services")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface Droid {


    /**
     * 
     * @param identifyBytes
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.droid.Types
     */
    @WebMethod(action = "http://planets-project.eu/services/Droid")
    @WebResult(name = "identifyBytesResponse", targetNamespace = "http://planets-project.eu/services/Droid", partName = "DroidResult")
    public Types identifyBytes(
        @WebParam(name = "identifyBytes", targetNamespace = "http://planets-project.eu/services", partName = "identifyBytes")
        byte[] identifyBytes);

    /**
     * 
     * @param identifyFile
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.droid.Types
     */
    @WebMethod
    @WebResult(name = "identifyFileResponse", targetNamespace = "http://planets-project.eu/services", partName = "identifyFileResponse")
    public Types identifyFile(
        @WebParam(name = "identifyFile", targetNamespace = "http://planets-project.eu/services", partName = "identifyFile")
        String identifyFile);

}
