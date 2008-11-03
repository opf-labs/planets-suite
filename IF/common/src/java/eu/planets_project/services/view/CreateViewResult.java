/**
 * 
 */
package eu.planets_project.services.view;

import java.net.URL;

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
public class CreateViewResult {

    private URL viewURL;
    
    private ServiceReport report;

    /**
     * For JAXB:
     */
    protected CreateViewResult() { }

    /**
     * @param viewURL
     * @param report
     */
    public CreateViewResult(URL viewURL, ServiceReport report) {
        super();
        this.viewURL = viewURL;
        this.report = report;
    }

    /**
     * @return the viewURL
     */
    public URL getViewURL() {
        return viewURL;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }
    
}
