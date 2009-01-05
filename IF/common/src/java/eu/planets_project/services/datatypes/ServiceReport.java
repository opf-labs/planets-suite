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
     * {@link "http://en.wikipedia.org/wiki/Exit_status"}
     */
    public static final int SUCCESS = 0;
    /** Default non zero error value */
    public static final int ERROR = 1;
    /** The error state for the service invocation */
    public int error_state;
    /** Also allow properties to be returned, to permit extensible behaviour. */
    public List<Property> properties;

    /**
     * No arg constructor
     */
    public ServiceReport() {
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the warning
     */
    public String getWarn() {
        return warn;
    }

    /**
     * @param warn the warn to set
     */
    public void setWarn(String warn) {
        this.warn = warn;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return the error_state
     */
    public int getErrorState() {
        return error_state;
    }

    /**
     * @param error_state the error_state to set
     */
    public void setErrorState(int error_state) {
        this.error_state = error_state;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer rep;
        if( this.error_state == 0 ) {
            rep = new StringBuffer("Service Executed Successfully.\n");
        } else {
            rep = new StringBuffer("Service Failed With Error State: "+this.error_state+"\n");
        }
        if( this.error != null ) {
            rep.append("ERROR: "+this.error+"\n");
        }
        if( this.warn != null ) {
            rep.append("WARN: "+this.warn+"\n");
        }
        if( this.info != null ) {
            rep.append("INFO: "+this.info+"\n");
        }
        // Properties?
        if( this.properties != null ) {
            for( Property p : this.properties ) {
                rep.append("PROPERTY: "+p.getName()+" = "+p.getValue()+"\n");
            }
        }
        return rep.toString();
    }
}
