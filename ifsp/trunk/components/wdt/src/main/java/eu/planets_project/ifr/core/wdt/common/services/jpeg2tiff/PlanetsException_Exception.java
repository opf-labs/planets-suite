
package eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff;

import javax.xml.ws.WebFault;


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
@WebFault(name = "PlanetsException", targetNamespace = "http://planets-project.eu/ifr/migration")
public class PlanetsException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private PlanetsException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public PlanetsException_Exception(String message, PlanetsException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public PlanetsException_Exception(String message, PlanetsException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff.PlanetsException
     */
    public PlanetsException getFaultInfo() {
        return faultInfo;
    }

}
