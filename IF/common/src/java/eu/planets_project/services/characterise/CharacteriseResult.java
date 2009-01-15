/**
 * 
 */
package eu.planets_project.services.characterise;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CharacteriseResult {

    private ServiceReport report;

    private DigitalObject digitalObject;

    private List<Property> props;

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private CharacteriseResult() {}

    /**
     * @param props
     * @param report
     */
    public CharacteriseResult(List<Property> props, ServiceReport report) {
        super();
        this.props = props;
        this.report = report;
    }

    /**
     * @deprecated Use the constructor with a list of properties instead.
     * @param digitalObject
     * @param report
     */
    public CharacteriseResult(DigitalObject digitalObject, ServiceReport report) {
        super();
        this.digitalObject = digitalObject;
        this.report = report;
    }

    /**
     * @return the result properties
     */
    public List<Property> getProperties() {
        return props;
    }

    /**
     * @deprecated As the corresponding constructor, this will be replaced by
     *             the list of properties.
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
