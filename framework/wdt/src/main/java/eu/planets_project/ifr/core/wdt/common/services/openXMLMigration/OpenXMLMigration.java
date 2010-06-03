
package eu.planets_project.ifr.core.wdt.common.services.openXMLMigration;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 10:17:05 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "OpenXMLMigration", targetNamespace = "http://planets-project.eu/ifr/core/services/migration")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface OpenXMLMigration {


    /**
     * 
     * @param fileRef
     * @return
     *     returns java.lang.String
     * @throws PlanetsServiceException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public String convertFileRef(
        @WebParam(name = "fileRef", partName = "fileRef")
        String fileRef)
        throws PlanetsServiceException_Exception
    ;

    /**
     * 
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(partName = "return")
    public boolean isConfigValid();

}
