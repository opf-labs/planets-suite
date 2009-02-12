/**
 * 
 */
package eu.planets_project.services.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
     * @return service report from exception and message
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
     * @return service report from message
     */
    public static ServiceReport createErrorReport(String message) {
        ServiceReport sr = new ServiceReport();
        sr.setErrorState(ServiceReport.ERROR);
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
}
