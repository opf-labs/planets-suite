/**
 * 
 */
package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * TODO The return object of a workflow execution still needs to be defined. It
 * should contain a log of operations that took place registry pointers to
 * produced data of the individual steps etc.
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 15.12.2008
 */
/**
 * First draft of an actual WorkflowResult implementation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowResult implements Serializable {

    /** Generated. */
    private static final long serialVersionUID = -7804803563573452403L;

    @SuppressWarnings("unused")
    // For JAXB
    private WorkflowResult() {}

    // TODO Needs to be defined
    // digital objects with result data or registry pointers
    // metadata on execution time, etc.
    // events

    private List<URL> results;
    private URL log;
    private URL report;

    /**
     * @param report The location of the report
     * @param log The location of the log
     * @param results The location of the results
     */
    public WorkflowResult(URL report, URL log, List<URL> results) {
        this.report = report;
        this.log = log;
        this.results = results;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s, report: %s, log: %s, results: %s", this
                .getClass().getSimpleName(), report, log, results);
    }

    /**
     * @return the objects
     */
    public List<URL> getResults() {
        return results;
    }

    /**
     * @return the log
     */
    public URL getLog() {
        return log;
    }

    /**
     * @return the report
     */
    public URL getReport() {
        return report;
    }

}
