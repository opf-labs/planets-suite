
package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Feb 29 11:30:07 CET 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebService(name = "Tiff2Jpeg", targetNamespace = "http://tiff2jpg.planets.bl.uk/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Tiff2Jpeg {


    /**
     * 
     * @param arg0
     * @return
     *     returns uk.bl.planets.tiff2jpg.ImageData
     */
    @WebMethod
    @WebResult(partName = "return")
    public ImageData convert(
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
    public String convertFile(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws IOException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public String createBatcher(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1)
        throws IOException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public String getDetailedState(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    public void halt(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public String identify(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public boolean isFinished(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    public void rollback(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    public void start(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws UnknownTokenException_Exception
     */
    @WebMethod
    public void suspend(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws UnknownTokenException_Exception
    ;

}
