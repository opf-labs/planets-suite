/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A Report From A Preservation Service 
 * 
 * Defined based on the need identified in the 1st Service Developers Meeting.
 * 
 * A non-zero error_state should be used to indicate that a service failed, and 
 * that any further processing of the outputs should be halted.
 * 
 * The 'warn' string should be used to report detailed warnings.
 * 
 * Where possible, information concerning the quality of the outputs 
 * should be placed in Events associated with DigitalObjects. Further information
 * e.g. for debugging, 
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceReport {
    
    /**
     * Information returned by a Service, roughly corresponding to Standard Out.
     */
    public String info;
    
    /**
     * Warnings returned by a Service.  Things the user should be aware of, but are not fatal and do not imply significant data loss.
     */
    public String warn;
    
    /**
     * Errors returned by a Service.  Serious problems invoking the service, implying that no valid output will exist and the workflow should not continue.
     */
    public String error;
    
    /**
     * An integer indicating the output error state.
     * 
     * In the manner of the process exit status on UNIX, or errorlevel in DOS.
     * 
     * A value of 0 indicates success.
     * A non-zero value (usually 1-255) indicates failure, and may perhaps be used as an error code.
     * 
     * {@link http://en.wikipedia.org/wiki/Exit_status}
     */
    public int error_state;
    
    /**
     * Also allow properties to be returned, to permit extensible behaviour.
     */
    public List<Property> properties;

    /**
     * 
     */
    public ServiceReport() {
    }    
    
}
