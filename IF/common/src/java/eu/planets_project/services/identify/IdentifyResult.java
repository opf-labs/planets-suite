/**
 * 
 */
package eu.planets_project.services.identify;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.ServiceReport;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class IdentifyResult {

    URI type;
    
    ServiceReport report;

    /**
     * No-args constructor required by JAXB
     */
    public IdentifyResult() { }

    /**
     * @param types
     * @param report
     */
    public IdentifyResult(URI type, ServiceReport report) {
        super();
        this.type = type;
        this.report = report;
    }

    /**
     * @return the type
     */
    public URI getType() {
        return type;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }


    
}
