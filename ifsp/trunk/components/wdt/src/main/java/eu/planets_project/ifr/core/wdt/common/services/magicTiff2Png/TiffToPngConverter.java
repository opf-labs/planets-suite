
package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Png;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Thu Jun 05 15:31:41 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "TiffToPngConverter", targetNamespace = "http://planets-project.eu/ifr/migration")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface TiffToPngConverter {


    /**
     * 
     * @param arg0
     * @return
     *     returns byte[]
     * @throws PlanetsException_Exception
     */
    @WebMethod
    @WebResult(name = "convertedImageDataByteArray", partName = "convertedImageDataByteArray")
    public byte[] basicMigrateBinary(
        @WebParam(name = "arg0", partName = "arg0")
        byte[] arg0)
        throws PlanetsException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns eu.planets_project.ifr.core.wdt.common.services.magicTiff2Png.Base64BinaryArray
     * @throws PlanetsException_Exception
     */
    @WebMethod
    @WebResult(name = "convertedImageDataByteArrays", partName = "convertedImageDataByteArrays")
    public Base64BinaryArray basicMigrateBinarys(
        @WebParam(name = "arg0", partName = "arg0")
        Base64BinaryArray arg0)
        throws PlanetsException_Exception
    ;

}
