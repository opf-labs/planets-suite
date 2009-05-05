/**
 * 
 */
package eu.planets_project.services.characterise;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * Result type for the {@link Characterise} interface.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CharacteriseResult {

    private ServiceReport report;

    private List<Property> props;

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
        super();
        this.props = props;
        this.report = report;
    }

    /**
     * @return The result properties
     */
    public List<Property> getProperties() {
        return props;
    }

    /**
     * @return The service report
     */
    public ServiceReport getReport() {
        return report;
    }
}
