
package eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg;

import javax.xml.ws.WebFault;


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
@WebFault(name = "IOException", targetNamespace = "http://tiff2jpg.planets.bl.uk/")
public class IOException_Exception
    extends Exception
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4425425275948280022L;
	/**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private IOException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public IOException_Exception(String message, IOException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public IOException_Exception(String message, IOException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: uk.bl.planets.tiff2jpg.IOException
     */
    public IOException getFaultInfo() {
        return faultInfo;
    }

}
