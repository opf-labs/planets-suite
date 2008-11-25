/**
 * 
 */
package eu.planets_project.services.characterise;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CharacteriseResult {

    private DigitalObject digitalObject;    

    private ServiceReport report;
    
    
    /**
     *  For JAXB.
     */
    public CharacteriseResult() { }

    /**
     * @param digitalObject
     * @param report
     */
    public CharacteriseResult(DigitalObject digitalObject, ServiceReport report) {
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
