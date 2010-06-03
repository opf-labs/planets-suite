
package eu.planets_project.ifr.core.wdt.common.services.openXMLMigration;

import javax.xml.ws.WebFault;


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
@WebFault(name = "PlanetsServiceException", targetNamespace = "http://planets-project.eu/ifr/core/services/migration")
public class PlanetsServiceException_Exception
    extends Exception
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 546775730940639194L;
	/**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private PlanetsServiceException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public PlanetsServiceException_Exception(String message, PlanetsServiceException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public PlanetsServiceException_Exception(String message, PlanetsServiceException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: eu.planets_project.ifr.core.services.migration.PlanetsServiceException
     */
    public PlanetsServiceException getFaultInfo() {
        return faultInfo;
    }

}
