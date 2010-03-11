/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.ifr.core.techreg.properties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author AnJackson
 *
 */
public class ServiceProperties {
    /** */
    private static Logger log = Logger.getLogger(ServiceProperties.class.getName());
    
    /** A standard environment identifier for the Java System.getProperties. */
    public static final URI ENV_JAVA_SYS_PROP = URI.create("planets:service/sys/java-system-properties");
    /** */
    public static final URI JAVA_MEM_TOTAL_PROP = URI.create("planets:service/jvm/mem/total");    
    
    /** A standard Planets identifier for the wall-clock time a process takes to execute. */
    public static final URI SERVICE_WALLCLOCK_PROP = URI.create("planets:service/exec/wallclock");
    /** */
    public static final URI SERVICE_WALLCLOCK_TRANSFER_PROP = URI.create("planets:service/exec/wallclock/transfer");
    /** */
    public static final URI SERVICE_WALLCLOCK_LOAD_PROP = URI.create("planets:service/exec/wallclock/load");
    /** */
    public static final URI SERVICE_CPU_TIME_PROP = URI.create("planets:service/exec/cpu");
    /** */
    public static final URI SERVICE_USER_TIME_PROP = URI.create("planets:service/exec/user");
    /** */
    public static final URI SERVICE_COMPILE_TIME_PROP = URI.create("planets:service/exec/compile");
    /** */
    public static final URI SERVICE_PEAK_HEAP_PROP = URI.create("planets:service/mem/heap/peak");
    /** */
    public static final URI SERVICE_PEAK_NONHEAP_PROP = URI.create("planets:service/mem/non-heap/peak");
    /** */
    public static final URI SERVICE_TOOL_RUNNER_TIME_PROP = URI.create("planets:service/exec/tool");
    
    /**
     * @return A list of all known service properties, including some values.
     */
    public static List<Property> listAllProperties() {
        List<Property> ps = new ArrayList<Property>();
        // Go through the properties here:
        ps.add( ServiceProperties.createClassesLoadedProperty(0) );
        ps.add( ServiceProperties.createCompilationTimeProperty(0) );
        ps.add( ServiceProperties.createCpuTimeProperty(0) );
        ps.add( ServiceProperties.createPeakHeapMemoryProperty(0) );
        ps.add( ServiceProperties.createPeakNonHeapMemoryProperty(0) );
        ps.add( ServiceProperties.createTotalJavaMemoryProperty(0) );
        ps.add( ServiceProperties.createUserTimeProperty(0) );
        ps.add( ServiceProperties.createWallclockLoadTimeProperty(0) );
        ps.add( ServiceProperties.createWallclockTimeProperty(0) );
        ps.add( ServiceProperties.createWallclockTransferTimeProperty(0) );
        ps.addAll( ServiceProperties.collectSystemProperties() );
        return ps;
    }
    
    /**
     * @param argv
     */
    public static void main( String[] argv ) {
        PropertyDefinitionFile pdf = new PropertyDefinitionFile(listAllProperties());
        System.out.println(pdf.toXmlFormatted());
    }
     
    /**
     * @param elapsed time in milliseconds
     * @return A Property for this value.
     */
    public static Property createWallclockTimeProperty(double elapsedMillis ) {
        Property.Builder p = new Property.Builder( SERVICE_WALLCLOCK_PROP );
        p.name("Wall-clock time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time, as would be measured by a clock on the wall.");
        return p.build();
    }

    /**
     * @param elapsedMillis
     * @return
     */
    public static Property createWallclockTransferTimeProperty(double elapsedMillis ) {
        Property.Builder p = new Property.Builder( SERVICE_WALLCLOCK_TRANSFER_PROP );
        p.name("Wall-clock transfer time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time taken to transfer the content to the service, as would be measured by a clock on the wall.");
        return p.build();
    }

    /**
     * @param elapsedMillis
     * @return
     */
    public static Property createWallclockLoadTimeProperty(double elapsedMillis ) {
        Property.Builder p = new Property.Builder( SERVICE_WALLCLOCK_LOAD_PROP );
        p.name("Wall-clock load time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time taken to transfer the content to the service and load it into memory, as would be measured by a clock on the wall.");
        return p.build();
    }

    /**
     * @param elapsedMillis
     */
    public static Property createCpuTimeProperty(double elapsedMillis) {
        Property.Builder p = new Property.Builder( SERVICE_CPU_TIME_PROP );
        p.name("CPU time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed CPU time.");
        return p.build();
    }
    /**
     * @param elapsedMillis
     */
    public static Property createUserTimeProperty(double elapsedMillis) {
        Property.Builder p = new Property.Builder( SERVICE_USER_TIME_PROP );
        p.name("User time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time spent executing user code, as opposed to system or IO operations.");
        return p.build();
    }

    /**
     * @param elapsedMillis
     */
    public static Property createCompilationTimeProperty(double elapsedMillis) {
        Property.Builder p = new Property.Builder( SERVICE_COMPILE_TIME_PROP );
        p.name("Compilation time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time spent in the JIT, compiling code for execution.");
        return p.build();
    }
    
    public static Property createClassesLoadedProperty( long loaded ) {
        Property.Builder p = new Property.Builder( SERVICE_COMPILE_TIME_PROP );
        p.name("Classes loaded");
        p.value(""+loaded);
        p.unit(null);
        p.description("The number of classes loaded into the JVM during execution.");
        return p.build();
    }
    
    public static Property createTotalJavaMemoryProperty( long bytes ) {
        Property.Builder p = new Property.Builder( JAVA_MEM_TOTAL_PROP );
        p.name("Java VM Total Memory");
        p.value(""+bytes);
        p.unit("bytes");
        p.description("The total memory allocated to the Java virtual machine.");
        return p.build();
    }
    
    public static Property createToolRunnerTimeProperty( double elapsedMillis) {
    	Property.Builder p = new Property.Builder( SERVICE_TOOL_RUNNER_TIME_PROP );
        p.name("Tool Runner Execution Time");
        p.value(""+((elapsedMillis)/1000.0));
        p.unit("s");
        p.description("The elapsed time spent in the command line runner, executing the tool.");
        return p.build();
    }

    /**
     * @param bytes
     */
    public static Property createPeakHeapMemoryProperty( long bytes ) {
        Property.Builder p = new Property.Builder( SERVICE_PEAK_HEAP_PROP );
        p.name("Peak Heap Memory");
        p.value(""+bytes);
        p.unit("bytes");
        p.description("The peak heap memory of the JVM during execution.");
        return p.build();
    }

    /**
     * @param bytes
     */
    public static Property createPeakNonHeapMemoryProperty( long bytes ) {
        Property.Builder p = new Property.Builder( SERVICE_PEAK_NONHEAP_PROP );
        p.name("Peak Non-Heap Memory");
        p.value(""+bytes);
        p.unit("bytes");
        p.description("The peak non-heap memory of the JVM during execution.");
        return p.build();
    }

    /**
     * Add or Update automatically generated list of JVM/OS properties.
     * Embeds information about the service environment inside the service description 
     * as a property.
     * 
     * TODO Upgrade this idea to some standardised form for platform/environment/software stacks.
     */
    private static Property createServerDescriptionProperty() {
        java.util.Properties p = System.getProperties();
        
        ByteArrayOutputStream byos = new ByteArrayOutputStream();
        try {
            p.storeToXML(byos, "Automatically generated server description.", "UTF-8");
            Property.Builder jspp = new Property.Builder(ENV_JAVA_SYS_PROP);
            jspp.name( "Java JVM System Properties");
            jspp.value( byos.toString("UTF-8") );
            jspp.description("The JVM System Propertes, as enumerated by System.getProperties(), encoded in UTF-8 as XML via Properties.storeToXML. Contains a list of the JVM system properties, including platform and VM details etc.");
            return jspp.build();
        } catch ( IOException e ) {
            // Fail silently.
            log.fine("IOException when storing server properties to XML. "+e);
        }
        
        return null;
    }
    
    /**
     * TODO Decide which of these to store.
     * @return A list of useful system properties, stored in standard property definitions.
     */
    public static List<Property> collectSystemProperties() {
        List<Property> sysprops = new ArrayList<Property>();
        
        // Look OS Props:
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        osBean.getArch();
        osBean.getAvailableProcessors();
        osBean.getName();
        osBean.getVersion();
        // On Sun Java, we can get more info:
        if ( (osBean instanceof
                com.sun.management.OperatingSystemMXBean) ) {
            com.sun.management.OperatingSystemMXBean sb = 
                (com.sun.management.OperatingSystemMXBean) osBean;
            log.info("com.sun.management.OperatingSystemMXBean is supported.");
            sb.getCommittedVirtualMemorySize();
            sb.getFreePhysicalMemorySize();
            sb.getFreeSwapSpaceSize();
            sb.getProcessCpuTime();
            sb.getTotalPhysicalMemorySize();
            sb.getTotalSwapSpaceSize();
        }

        
        // Look for Runtime Props:
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        runtimeBean.getSpecVersion();
        Runtime runtime = Runtime.getRuntime();
        sysprops.add( ServiceProperties.createTotalJavaMemoryProperty( runtime.totalMemory() ) );
        
        // All System/Environment Properties in a bundle:
        sysprops.add( ServiceProperties.createServerDescriptionProperty() );
        
        return sysprops;
    }
    
    /**
     * Util to print out a list properties.
     * @param ps
     * @param props
     */
    public static void printProperties( PrintStream ps, List<Property> props ) {
        int max = 100;
        for( Property p : props ) {
            String propString = p.toString();
            if( propString.length() > max ) propString = propString.substring(0, max);
            ps.println("| " + propString );
        }
    }

}
