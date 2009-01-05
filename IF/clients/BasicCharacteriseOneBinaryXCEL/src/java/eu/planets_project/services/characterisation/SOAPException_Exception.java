
package eu.planets_project.services.characterisation;

import javax.xml.ws.WebFault;


/**
 * JBossWS Generated Source
 * 
 * Generation Date: Fri Jul 04 13:08:18 CEST 2008
 * 
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 * 
 * JAX-WS Version: 2.0
 * 
 */
@WebFault(name = "SOAPException", targetNamespace = "http://planets-project.eu/ifr/core/storage/data")
public class SOAPException_Exception
    extends Exception
{
	private static final long serialVersionUID = 3256808525800060185L;
	/**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private SOAPException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public SOAPException_Exception(String message, SOAPException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public SOAPException_Exception(String message, SOAPException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: eu.planets_project.services.characterisation.SOAPException
     */
    public SOAPException getFaultInfo() {
        return faultInfo;
    }

}
