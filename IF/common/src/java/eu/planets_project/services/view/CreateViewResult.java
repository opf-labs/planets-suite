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
 * A service that creates a view should return a URL where the user can view the
 * session. Optionally, the service should return a session handle so the
 * invoking agent can query the sessions status.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CreateViewResult {

    private URL viewURL;
    private String sessionIdentifier;
    private ServiceReport report;

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private CreateViewResult() {}

    /**
     * @param viewURL The URL
     * @param sessionIdentifier The session ID
     * @param report The report
     */
    public CreateViewResult(final URL viewURL, final String sessionIdentifier,
            final ServiceReport report) {
        this.viewURL = viewURL;
        this.sessionIdentifier = sessionIdentifier;
        this.report = report;
    }

    /**
     * @return the viewURL
     */
    public URL getViewURL() {
        return viewURL;
    }

    /**
     * @return the sessionIdentifier
     */
    public String getSessionIdentifier() {
        return sessionIdentifier;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }

}
