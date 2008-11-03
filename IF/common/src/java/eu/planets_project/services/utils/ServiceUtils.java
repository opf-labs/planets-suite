/**
 * 
 */
package eu.planets_project.services.utils;

import eu.planets_project.services.datatypes.ServiceReport;

/**
 * A class to hold some utility functions for Planets Service developers.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceUtils {
    
    /**
     * 
     * @param message
     * @param e
     * @return
     */
    public static ServiceReport createExceptionErrorReport(String message, Exception e) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.ERROR);
        sr.setError(message + "\n" + e.toString());
        return sr;
    }
    
    /**
     * 
     * @param message
     * @return
     */
    public static ServiceReport createErrorReport(String message) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.ERROR);
        sr.setError(message);
        return sr;
    }
    
}
