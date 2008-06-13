
package eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri May 30 14:32:57 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "JpgToTiffConverter", targetNamespace = "http://planets-project.eu/ifr/migration")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface JpgToTiffConverter {


    /**
     * 
     * @param srcFileByteArray
     * @return
     *     returns byte[]
     * @throws PlanetsException_Exception
     */
    @WebMethod
    @WebResult(name = "convertedImageDataByteArray", partName = "convertedImageDataByteArray")
    public byte[] basicMigrateBinary(
        @WebParam(name = "srcFileByteArray", partName = "srcFileByteArray")
        byte[] srcFileByteArray)
        throws PlanetsException_Exception
    ;

}
