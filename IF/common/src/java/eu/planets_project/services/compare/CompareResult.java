/**
 * 
 */
package eu.planets_project.services.compare;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * Result type for the Compare Interface.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CompareResult {
    private DigitalObject digitalObject;
    private ServiceReport report;

    /** For JAXB. */
    @SuppressWarnings("unused")
    private CompareResult() {}

    /**
     * @param digitalObject The digital object
     * @param report The report
     */
    public CompareResult(final DigitalObject digitalObject,
            final ServiceReport report) {
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
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }

}
