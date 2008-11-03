/**
 * 
 */
package eu.planets_project.services.migrate;

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
public class MigrateResult {

    private DigitalObject digitalObject;    

    private ServiceReport report;
    
    
    /**
     *  For JAXB.
     */
    public MigrateResult() { }

    /**
     * @param digitalObject
     * @param report
     */
    public MigrateResult(DigitalObject digitalObject, ServiceReport report) {
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
