/**
 * 
 */
package eu.planets_project.services.compare;

import eu.planets_project.services.compare.PropertyComparison.Equivalence;
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
    private List<PropertyComparison> properties;
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
    public CompareResult(final List<PropertyComparison> properties, final ServiceReport report) {
        if( properties != null ) {
            this.properties = new ArrayList<PropertyComparison>(properties);
        } else {
            this.properties = new ArrayList<PropertyComparison>();
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
    public CompareResult(final List<PropertyComparison> properties, 
    		final ServiceReport report, final String fragmentID) {
        this.properties = new ArrayList<PropertyComparison>(properties);
        this.report = report;
        this.results = Collections.unmodifiableList(new ArrayList<CompareResult>());
        this.fragmentID = fragmentID;
    }

    /**
     * @param properties The result properties
     * @param report The report
     * @param results The embedded results
     */
    public CompareResult(final List<PropertyComparison> properties, final ServiceReport report, final List<CompareResult> results) {
        this.properties = new ArrayList<PropertyComparison>(properties);
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
    public CompareResult(final List<PropertyComparison> properties,
    		final ServiceReport report, final List<CompareResult> results,
    		final String fragmentID) {
        this.properties = new ArrayList<PropertyComparison>(properties);
        this.report = report;
        this.results = new ArrayList<CompareResult>(results);
        this.fragmentID = fragmentID;
    }
    /**
     * @param result
     * @param report2
     */
    @Deprecated
    public CompareResult(List<Property> result, String fragmentID, ServiceReport report) {
        this.report = report;
        this.fragmentID = fragmentID;
        this.results = new ArrayList<CompareResult>();
        this.importProperties(result);
    }

    /**
     * @param arrayList
     * @param object
     * @param serviceReport
     * @param embedded
     */
    @Deprecated
    public CompareResult(ArrayList<Property> props, String fragmentID,
            ServiceReport report, List<CompareResult> embedded) {
        this.report = report;
        this.fragmentID = fragmentID;
        this.results = new ArrayList<CompareResult>(embedded);
        this.importProperties(props);
    }

    /*
     * 
     */
    private void importProperties( List<Property> props ) {
        // Construct a list of PropertyComparison objects:
        this.properties = new ArrayList<PropertyComparison>();
        for( Property p : props ) {
            properties.add( new PropertyComparison(p, null, null, Equivalence.UNKNOWN ) );
        }
    }

    /**
     * @return An unmodifiable copy of the result comparisons
     */
    public List<PropertyComparison> getComparisons() {
        return properties == null ? Collections.unmodifiableList(new ArrayList<PropertyComparison>()) : properties;
    }

    /**
     * @return An unmodifiable copy of the result properties
     */
    public List<Property> getProperties() {
        List<Property> props = new ArrayList<Property>();
        if( this.properties == null ) return props;
        for( PropertyComparison pc : this.properties ) {
            props.add( pc.getComparison() );
        }
        return props;
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
