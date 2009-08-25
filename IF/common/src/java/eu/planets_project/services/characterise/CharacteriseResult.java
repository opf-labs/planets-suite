/**
 * 
 */
package eu.planets_project.services.characterise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * Result type for the {@link Characterise} interface.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a> (recursive structure)
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CharacteriseResult {

    private ServiceReport report;

    private List<Property> props;
    
    private List<CharacteriseResult> results;
    
    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private CharacteriseResult() {}

    /**
     * @param props The characterisation result properties
     * @param report The service report
     */
    public CharacteriseResult(final List<Property> props,
            final ServiceReport report) {
        this.props = props;
        this.report = report;
        this.results = new ArrayList<CharacteriseResult>(); 
    }
    
    /**
     * @param props The characterisation result properties
     * @param report The service report
     * @param results The embedded results
     */
    public CharacteriseResult(final List<Property> props,
            final ServiceReport report, final List<CharacteriseResult> results) {
        this.props = props;
        this.report = report;
        this.results = results; 
    }

    /**
     * @return An unmodifiable view of the result properties
     */
    public List<Property> getProperties() {
        return Collections.unmodifiableList(props);
    }

    /**
     * @return The service report
     */
    public ServiceReport getReport() {
        return report;
    }

    /**
     * @return An unmodifiable view of the embedded results
     */
    public List<CharacteriseResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}
