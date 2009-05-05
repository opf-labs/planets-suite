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
 * Result type for migration services.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class MigrateResult {

    private DigitalObject digitalObject;
    private ServiceReport report;

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private MigrateResult() {}

    /**
     * @param digitalObject The resulting digital object
     * @param report The report for this migration
     */
    public MigrateResult(final DigitalObject digitalObject,
            final ServiceReport report) {
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
