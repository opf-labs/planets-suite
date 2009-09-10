/**
 * 
 */
package eu.planets_project.services.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * Result type for the {@link Compare} interface.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CompareResult {
    private List<Property> properties;
    private ServiceReport report;
    private List<CompareResult> results;
    
    /** For JAXB. */
    @SuppressWarnings("unused")
    private CompareResult() {}

    /**
     * @param properties The result properties
     * @param report The report
     */
    public CompareResult(final List<Property> properties, final ServiceReport report) {
        this.properties = new ArrayList<Property>(properties);
        this.report = report;
        this.results = Collections.unmodifiableList(new ArrayList<CompareResult>());
    }
    
    /**
     * @param properties The result properties
     * @param report The report
     * @param results The embedded results
     */
    public CompareResult(final List<Property> properties, final ServiceReport report, final List<CompareResult> results) {
        this.properties = new ArrayList<Property>(properties);
        this.report = report;
        this.results = new ArrayList<CompareResult>(results);
    }

    /**
     * @return An unmodifiable copy of the result properties
     */
    public List<Property> getProperties() {
        return properties == null ? Collections.unmodifiableList(new ArrayList<Property>()) : properties;
    }

    /**
     * @return The report
     */
    public ServiceReport getReport() {
        return report;
    }

    /**
     * @return An unmodifiable copy of the embedded results
     */
    public List<CompareResult> getResults() {
        return results == null ? Collections.unmodifiableList(new ArrayList<CompareResult>()) : results;
    }

}
