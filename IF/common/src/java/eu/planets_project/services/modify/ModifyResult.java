/**
 * 
 */
package eu.planets_project.services.modify;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Peter.Melms@uni-koeln.de">Peter Melms</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ModifyResult {
	
	private DigitalObject digitalObject;    

	private ServiceReport report;
	 
	 
	 /**
	 * for JAXB
	 */
	public ModifyResult () {};
	
	
	/**
     * @param digitalObject
     * @param report
     */
    public ModifyResult(DigitalObject digitalObject, ServiceReport report) {
        super();
        this.digitalObject = digitalObject;
        this.report = report;
    }

    /**
     * @return the digitalObject
     */
    public DigitalObject getDigitalObject() {
        return digitalObject;
    }

    /**
     * @return the event
     */
    public ServiceReport getReport() {
        return report;
    }


}
