/**
 * 
 */
package eu.planets_project.services.compare;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result type for the {@link Compare} interface.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CompareResult {
    private List<Property> properties;
    private ServiceReport report;
    private String fragmentID;
    private List<CompareResult> results;
    
    /** For JAXB. */
    @SuppressWarnings("unused")
    private CompareResult() {}

    /**
     * @param properties The result properties
     * @param report The report
     */
    public CompareResult(final List<Property> properties, final ServiceReport report) {
        if( properties != null ) {
            this.properties = new ArrayList<Property>(properties);
        } else {
            this.properties = new ArrayList<Property>();
        }
        this.report = report;
        this.results = Collections.unmodifiableList(new ArrayList<CompareResult>());
        this.fragmentID = null;
    }
    
    /**
     * @param properties The result properties
     * @param report The report
     * @param fragmentID the id of the fragment the result refers to
     */
    public CompareResult(final List<Property> properties, 
    		final ServiceReport report, final String fragmentID) {
        this.properties = new ArrayList<Property>(properties);
        this.report = report;
        this.results = Collections.unmodifiableList(new ArrayList<CompareResult>());
        this.fragmentID = fragmentID;
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
        this.fragmentID = null;
    }

    /**
     * @param properties The result properties
     * @param report The report
     * @param results The embedded results
     * @param fragmentID the id of the fragment the result refers to
     */
    public CompareResult(final List<Property> properties,
    		final ServiceReport report, final List<CompareResult> results,
    		final String fragmentID) {
        this.properties = new ArrayList<Property>(properties);
        this.report = report;
        this.results = new ArrayList<CompareResult>(results);
        this.fragmentID = fragmentID;
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
    
    /**
     * @return The fragment ID
     */
    public String getFragmentID() {
    	return this.fragmentID;
    }
}
