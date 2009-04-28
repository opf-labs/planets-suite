/**
 * 
 */
package eu.planets_project.services.utils;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class to hold some utility functions for Planets Service developers.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceUtils {
    /** */
    private static Log log = LogFactory.getLog(ServiceUtils.class);

    /**
     * @param message A message that described the error.
     * @param e The Exception that caused the error - can be NULL.
     * @param errorType The kind of error, ServiceReport.TOOL_ERROR, ServiceReport.INSTALLATION_ERROR, ...
     * @return
     */
    public static ServiceReport createExceptionErrorReport(String message, Exception e, int errorType ) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState( errorType );
        String error = message;
        if( e != null ) message += "\n" + e.toString();
        sr.setError(error);
        return sr;
    }
    
    /**
     * 
     * @param message
     * @param e
     * @return service report from exception and message
     */
    public static ServiceReport createExceptionErrorReport(String message, Exception e) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.TOOL_ERROR);
        sr.setError(message + "\n" + e.toString());
        return sr;
    }
    
    /**
     * 
     * @param message
     * @return service report from message
     */
    public static ServiceReport createErrorReport(String message) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.TOOL_ERROR);
        sr.setError(message);
        return sr;
    }
    
    
    /**
     * 
     * Convenience method to get the current System date and time as 
     * a formatted String. Example: 
     * 
     *     "29/01/2009 - 16:39:26,937"
     * 
     * @return the current System time and date as String 
     * 
     */
    public static String getSystemDateAndTimeFormatted() {
		Calendar calendar = new GregorianCalendar();
		String day, month, year, hour, minute, second, millisecond;
		day = Integer.toString(calendar.get(Calendar.DATE));
		month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		year = Integer.toString(calendar.get(Calendar.YEAR));
		hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		minute = Integer.toString(calendar.get(Calendar.MINUTE));
		second = Integer.toString(calendar.get(Calendar.SECOND));
		millisecond = Integer.toString(calendar.get(Calendar.MILLISECOND)); 
		if(day.length()==1) {
			day = "0" + day;
		}
		if(month.length()==1) {
			month = "0" + month;
		}
		if(hour.length()==1) {
			hour = "0" + hour;
		}
		if(minute.length()==1) {
			minute = "0" + minute;
		}
		if(second.length()==1) {
			second = "0" + second;
		}
		return day + "/" + month + "/" + year + " - " + hour + ":" + minute + ":" + second + "," + millisecond;
    }
    
    public static double calculateDuration(long startTime, long endTime) {
    	double duration = endTime - startTime;
    	return duration;
    }
    
    public static Set<URI> asSet(URI... uris){
        return new HashSet<URI>(Arrays.asList(uris));
    }

    // Some standard property identifiers:
    // FIXME !!! Move to a standard place?
    
    /** A standard ENVIRONMENT identifier for the Java System.getProperties. */
    public static final URI ENV_JAVA_SYS_PROP = URI.create("planets:if/srv/java-system-properties");
    
    /**
     * Add or Update automatically generated list of JVM/OS properties.
     * Embeds information about the service environment inside the service description 
     * as a property called 'planets:if/srv/java-system-properties'
     * 
     * TODO Upgrade this idea to some standardised form for platform/environment/software stacks.
     */
    public static Property createServerDescriptionProperty() {
        java.util.Properties p = System.getProperties();
        
        ByteArrayOutputStream byos = new ByteArrayOutputStream();
        try {
            p.storeToXML(byos, "Automatically generated server description.", "UTF-8");
            Property jspp = new Property(ENV_JAVA_SYS_PROP,"Java JVM System Properties", byos.toString("UTF-8") );
            return jspp;
        } catch ( IOException e ) {
            // Fail silently.
            log.debug("IOException when storing server properties to XML. "+e);
        }
        
        return null;
    }

}
