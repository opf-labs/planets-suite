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
     * @return the result properties
     */
    public List<Property> getProperties() {
        return props;
    }

    /**
     * @return the event
     */
    public ServiceReport getReport() {
        return report;
    }

    /**
     * @param digitalObject the digitalObject to set
     * @deprecated In the future, characterise results will only contain the
     *             properties
     */
    public void setDigitalObject(DigitalObject digitalObject) {
        this.digitalObject = digitalObject;
    }

    /**
     * @deprecated Use {@link #getProperties()} to get the list of properties.
     * @return the digitalObject
     */
    public DigitalObject getDigitalObject() {
        return digitalObject;
    }

}
