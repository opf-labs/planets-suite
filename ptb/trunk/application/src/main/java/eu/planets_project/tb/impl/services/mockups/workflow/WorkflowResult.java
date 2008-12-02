/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.util.Collection;

import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class WorkflowResult {
    
    Collection<MeasurementImpl> measurements;
    
    String resultType;
    public static final String RESULT_URI = "uri";
    public static final String RESULT_DIGITAL_OBJECT = "digital_object";
    
    Object result;
    
    ServiceReport report;
    
    /** */
    protected WorkflowResult() {}

    /**
     * @param measurements
     * @param resultType
     * @param result
     * @param report
     */
    public WorkflowResult(Collection<MeasurementImpl> measurements,
            String resultType, Object result, ServiceReport report) {
        super();
        this.measurements = measurements;
        this.resultType = resultType;
        this.result = result;
        this.report = report;
    }

    /**
     * @return the measurements
     */
    public Collection<MeasurementImpl> getMeasurements() {
        return measurements;
    }

    /**
     * @return the resultType
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }
    
}
