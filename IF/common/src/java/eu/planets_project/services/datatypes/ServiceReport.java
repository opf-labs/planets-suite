/**
 * 
 */
package eu.planets_project.services.datatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
    @XmlElement
    public String info;
    
    /**
     * Warnings returned by a Service.  Things the user should be aware of, but
     * are not fatal and do not imply significant data loss.
     */
    @XmlElement
    public String warn;
    
    /**
     * Errors returned by a Service.  Serious problems invoking the service,
     * implying that no valid output will exist and the workflow should not
     * continue.<br>
     *
     * This field should only be used, if the error_state is != SUCCESS. If
     * this field is used, the info and warn fields should not be used
     *
     * @see #error_state
     * @see #SUCCESS
     * @see #TOOL_ERROR
     * @see #INSTALLATION_ERROR
     * @see #info
     * @see #warn
     */
    @XmlElement
    public String error;


    /**
     * The service was invoced succesfully. This does not guarantee
     * that the service did what it was supposed to do, just that
     * the service wrapping registred no errors.
     *
     * For futher detail, examine the info stringm and the warn string
     *
     * @see #info
     * @see #warn
     * @see #error_state
     */
    public static final int SUCCESS = 0;


    /**
     * The service failed. For further details, examine the error string
     *
     * @see #error_state
     * @see #error
     */
    public static final int TOOL_ERROR = 1;


    /**
     * The service failed in such a way, that further invocations of this
     * service will also likely fail. Do not invoke the service again<br>
     * This is the correct error state, if the service has unfulfilled
     * dependencies from the enviroment, or suffers a catastrophic error
     * like OutOfMemory
     *
     * For futher detail on the error, examine the error string
     *
     * @see #error
     * @see #error_state
     */
    public static final int INSTALLATION_ERROR = 2;


    /**
     * The error state for the service invocation. Restricted to
     * three values
     * <ul>
     * <li> SUCCESS
     * <li> TOOL_ERROR
     * <li> INSTALLATION_ERROR
     * <ul>
     *
     * @see #SUCCESS
     * @see #TOOL_ERROR
     * @see #INSTALLATION_ERROR
     */
    public int error_state = SUCCESS;


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
        return rep.toString();
    }
}
