
package eu.planets_project.ifr.core.wdt.common.services.serviceRegistry;

import javax.xml.ws.WebFault;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Tue Apr 22 14:45:59 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebFault(name = "JAXRException", targetNamespace = "http://planets-project.eu/ifr/core/registry")
public class JAXRException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private JAXRException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public JAXRException_Exception(String message, JAXRException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public JAXRException_Exception(String message, JAXRException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: eu.planets_project.ifr.core.registry.JAXRException
     */
    public JAXRException getFaultInfo() {
        return faultInfo;
    }

}
