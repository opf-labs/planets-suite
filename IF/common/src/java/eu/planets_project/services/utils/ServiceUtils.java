/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import com.sun.xml.ws.developer.JAXWSProperties;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;

/**
 * A class to hold some utility functions for Planets Service developers.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>, <a href="mailto:fabian.steeg@uni-koeln.de">Fabian
 *         Steeg</a>
 */
public class ServiceUtils {

    private ServiceUtils() { /* Enforce non-instantiability for this util class */}

    /** */
    private static Logger log = Logger.getLogger(ServiceUtils.class.getName());

    /**
     * @param message A message that described the error.
     * @param e The Exception that caused the error - can be NULL.
     * @param errorType The kind of error, ServiceReport.TOOL_ERROR, ServiceReport.INSTALLATION_ERROR, ...
     * @return The service report
     */
    public static ServiceReport createExceptionErrorReport(String message, Exception e, int errorType) {
        String error = message;
        if (e != null)
            error += "\n" + e.toString();
        /*
         * This weird usage of the enum is temporary (the int param and probably this whole method should probably be
         * replaced with enum usage)
         */
        ServiceReport sr = new ServiceReport(Type.ERROR, Status.values()[errorType], error);
        return sr;
    }

    /**
     * @param message The message
     * @param e The exception
     * @return service report from exception and message
     */
    public static ServiceReport createExceptionErrorReport(String message, Exception e) {
        return new ServiceReport(Type.ERROR, Status.TOOL_ERROR, message + "\n" + e.toString());
    }

    /**
     * @param message The message
     * @return service report from message
     */
    public static ServiceReport createErrorReport(String message) {
        return new ServiceReport(Type.ERROR, Status.TOOL_ERROR, message);
    }

    /**
     * Convenience method to get the current System date and time as a formatted String. Example:
     * "29/01/2009 - 16:39:26,937"
     * @return the current System time and date as String
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
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        if (second.length() == 1) {
            second = "0" + second;
        }
        return day + "/" + month + "/" + year + " - " + hour + ":" + minute + ":" + second + "," + millisecond;
    }

    /**
     * @param startTime the start time
     * @param endTime The end time
     * @return The duration
     */
    public static double calculateDuration(long startTime, long endTime) {
        double duration = endTime - startTime;
        return duration;
    }

    /**
     * Creates a simple MigrationPath Matrix needed by most Migrate services. It's not possible to add Parameters per
     * Path...
     * @param inputFormats List of URIs with possible Inputformats
     * @param outputFormats List of URIs with possible Outputformats
     * @return a MigrationPath[] containing all possible combinations of formats
     */
    public static MigrationPath[] createMigrationPathways(List<URI> inputFormats, List<URI> outputFormats) {
        List<MigrationPath> paths = new ArrayList<MigrationPath>();

        for (URI currentInputFormat : inputFormats) {
            for (URI currentOutputFormat : outputFormats) {
                MigrationPath path = new MigrationPath(currentInputFormat, currentOutputFormat, null);
                paths.add(path);
            }
        }

        return paths.toArray(new MigrationPath[] {});
    }

    /**
     * @param uris The URIs
     * @return The uris as a set
     */
    public static Set<URI> asSet(URI... uris) {
        return new HashSet<URI>(Arrays.asList(uris));
    }

    /**
     * @param <T> The type of the implementation class to instantiate
     * @param interfaceName The QName of the service interface, e.g. Migrate.QNAME
     * @param implementationClass The class of the instance to create, e.g. JTidy.class
     * @param wsdlLocation The full URL of the WSDL for the service to create
     * @return An instance of T, representing the service running at the given URL
     */
    public static <T> T createService(QName interfaceName, Class<T> implementationClass, URL wsdlLocation) {
        log.info("INIT: Creating the proxied service class.");
        Service service = Service.create(wsdlLocation, interfaceName);
        /* Enable streaming, if supported by the service: */
        @SuppressWarnings("unchecked") T ids = (T) service.getPort(implementationClass.getInterfaces()[0],
                new MTOMFeature());
        ((BindingProvider) ids).getRequestContext().put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8096);
        SOAPBinding binding = (SOAPBinding) ((BindingProvider) ids).getBinding();
        binding.setMTOMEnabled(true);
        log.info("INIT: Created proxy class for service " + service.getServiceName());
        log.info("INIT: MTOM enabled for Service: " + binding.isMTOMEnabled());
        return ids;
    }

    /**
     * @param <T> The type of the service to create
     * @param description The description to instantiate a service for. Needs endpoint, type and class name set
     * @return A service proxy for a service running at the descriptions endpoint
     */
    public static <T> T createService(ServiceDescription description) {
        if (description.getEndpoint() == null || description.getType() == null || description.getClassname() == null) {
            throw new IllegalArgumentException(
                    "Service description needs endpoint, type and class name to be instantiated.");
        }
        try {
            String type = description.getType();
            @SuppressWarnings("unchecked")/* We have to assume the class name is correct */
            Class<T> clazz = (Class<T>) Class.forName(description.getClassname());
            /*
             * This assumes the type to be the full qualified interface name (which is what we do for our services).
             * Would getting the interface name from the class object created above be any better? (problem: order)
             */
            QName name = new QName(PlanetsServices.NS, type.substring(type.lastIndexOf('.') + 1));
            URL endpoint = description.getEndpoint();
            T service = createService(name, clazz, endpoint);
            return service;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Could not instantiate service description: " + e.getMessage(), e);
        }
    }

}
