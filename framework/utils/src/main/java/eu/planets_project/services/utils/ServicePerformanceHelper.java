/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.utils;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.techreg.properties.ServiceProperties;
import eu.planets_project.services.datatypes.Property;

/**
 * A standardised timing and performance data collection class, used to monitor 
 * performance consistently across services.
 * <p/>
 * To use it, first instantiate this class when your preservation action method starts.
 * The helper starts timing on construction. This is to prevent accidental re-use 
 * of each instance of the object, in order to avoid copies of the object being 
 * used in an non-thread-safe manner.
 * <p/>
 * When the service has finished it's work, and before creating the final ServiceReport, 
 * call the .stop() method to halt the timer. Then use getPerformanceProperties() to get
 * the results, which should be added to the ServiceReport (not the return object specific
 * to that preservation action.
 * <p/>
 * As well as measuring how long the process took (wall-clock time), these methods also measure 
 * how much CPU time this current thread required (e.g. half the wall-clock time if this process 
 * is only getting 50% of the CPU time). Note that this will only give meaningful
 * results if your service runs in a single thread. Also, while the thread is running, 
 * this helper measures the peak memory usage of the JVM. While this does not explicitly
 * identify the resource usage associated with your service, it should be indicative in 
 * the main 'interesting' cases, e.g. when your service is passed a large file. A 
 * number of standard system properties are also measured, in order to understand the 
 * context of the execution process.
 * <p/>
 * Additionally, this helper provides two methods by which the caller can record when it 
 * has finished transferring the input arguments (e.g. one or more DigitalObjects) from 
 * the caller (.transferred()), and/or record when the input arguments have been 
 * loaded into memory (.loaded()), if that is relevant. Note that the .loaded() 
 * time should include the .transferred() time. If the input streams are loaded 
 * directly into memory, the calling code should only record the .loaded() time.
 * <p/>
 * For examples of how these calls should be used see JavaImageIOCompare, JavaImageIOMigrate 
 * and JavaImageIOIdentify in the PA/java-se component.
 * <p/>
 * Note that we use System.nanoTime() rather than System.currentTimeMills() because 
 * that call generally more accurate. In particular, on Windows, the currentTimeMillis 
 * is often rounded rather coarsely. See below for details.
 * <p/>
 * @see <a href="http://savvyduck.blogspot.com/2008/06/java-getting-thread-time-with.html">http://savvyduck.blogspot.com/2008/06/java-getting-thread-time-with.html</a>
 * @see <a href="http://java.sun.com/javase/6/docs/api/java/lang/System.html#currentTimeMillis%28%29">http://java.sun.com/javase/6/docs/api/java/lang/System.html#currentTimeMillis%28%29</a>
 * @see <a href="http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking">http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking</a>
 * @see <a href="http://stackoverflow.com/questions/351565/system-currenttimemillis-vs-system-nanotime">http://stackoverflow.com/questions/351565/system-currenttimemillis-vs-system-nanotime</a>
 * @see <a href="http://blogs.sun.com/dholmes/entry/inside_the_hotspot_vm_clocks">http://blogs.sun.com/dholmes/entry/inside_the_hotspot_vm_clocks</a>
 * @see <a href="http://stackoverflow.com/questions/47177/how-to-monitor-the-computers-cpu-memory-and-disk-usage-in-java">http://stackoverflow.com/questions/47177/how-to-monitor-the-computers-cpu-memory-and-disk-usage-in-java</a>
 * <p/>
 * @author Andrew.Jackson@bl.uk
 *
 */
public class ServicePerformanceHelper {
    /** */
    private static Logger log = Logger.getLogger(ServicePerformanceHelper.class.getName());
    
    private static final double NANO_MILLI = 1000000.0;

    private ThreadMXBean threadmxbean;
    private MemoryPoolMXBean heapMemoryBean;
    private MemoryPoolMXBean nonHeapMemoryBean;
    private MemoryMXBean memoryBean;
    private CompilationMXBean compilationBean;
    private ClassLoadingMXBean classLoaderBean;

    private boolean stopped = false;

    private long startSystemNanoTime;
    private long stopSystemNanoTime;
    private long stopTransferNanoTime = -1;
    private long stopLoadedNanoTime = -1;
    
    private long startCpuNs;
    private long stopCpuNs;
    
    private long startUserNs;
    private long stopUserNs;

    private long startCompTimeMillis;
    private long stopCompTimeMillis;
    
    private long startClassTotal;
    private long stopClassTotal;

    private MemoryUsage peakHeapUsage;
    private MemoryUsage peakNonHeapUsage;
    
    @SuppressWarnings("unused")
	private MemoryUsage startHeapUsage;
    @SuppressWarnings("unused")
	private MemoryUsage startNonHeapUsage;
    
    @SuppressWarnings("unused")
	private MemoryUsage stopHeapUsage;
    @SuppressWarnings("unused")
	private MemoryUsage stopNonHeapUsage;

    public ServicePerformanceHelper() {
        // Look for thread management bean:
        ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
        if( tmb.isCurrentThreadCpuTimeSupported() ) {
            this.threadmxbean = tmb;
        }
        // Look-up memory managment beans:
        for( MemoryPoolMXBean mpb : 
            ManagementFactory.getMemoryPoolMXBeans() ) {
            if( MemoryType.HEAP.equals(  mpb.getType() )  ) {
                this.heapMemoryBean = mpb;
            } else if( MemoryType.NON_HEAP.equals(mpb.getType()) ) {
                this.nonHeapMemoryBean = mpb;
            } else {
                log.warning( "Unknown memory type found: " + mpb.getType() );
            }
        }
        // Other useful beans:
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        CompilationMXBean cb = ManagementFactory.getCompilationMXBean();
        if( cb.isCompilationTimeMonitoringSupported() ) {
            this.compilationBean = cb;
        }
        this.classLoaderBean = ManagementFactory.getClassLoadingMXBean();
        // Automatically start the timing:
        this.start();
    }

    /**
     * This starts the timing.
     */
    private void start() {
        // Record some info:
        this.startClassTotal = this.classLoaderBean.getTotalLoadedClassCount();
        // Record basic memory status.
        this.startHeapUsage = this.memoryBean.getHeapMemoryUsage();
        this.startNonHeapUsage = this.memoryBean.getNonHeapMemoryUsage();
        // Reset peak memory usage monitors:
        this.heapMemoryBean.resetPeakUsage();
        this.nonHeapMemoryBean.resetPeakUsage();

        // Look at compilation time:
        if( this.compilationBean != null ) {
            this.startCompTimeMillis = this.compilationBean.getTotalCompilationTime();
        }
        // Initialise timers:
        this.startSystemNanoTime = System.nanoTime();
        if( threadmxbean != null ) {
            this.startCpuNs = this.threadmxbean.getCurrentThreadCpuTime();
            this.startUserNs = this.threadmxbean.getCurrentThreadCpuTime();
        }
    }
    
    /**
     * Allows developers to specify when the service has finished retrieving data, and 
     * is now only going to process the inputs and compose the response.
     * Should only be used when it is really clear that the load/retrieval time can be 
     * clearly distinguished from the processing time.
     */
    public void transferred() {
        this.stopTransferNanoTime = System.nanoTime();
    }

    /**
     * Allows developers to specify when the service has retrieved the data and 
     * loaded it into memory, ready for processing.
     * Should NOT be used if the data is not accessed in this manner.
     */
    public void loaded() {
        this.stopLoadedNanoTime = System.nanoTime();
    }

    /**
     * Stop all timers, as all work has been done apart from returning from the service call method.
     */
    public void stop() {
        // Stop timers:
        this.stopSystemNanoTime = System.nanoTime();
        if( threadmxbean != null ) {
            this.stopCpuNs = this.threadmxbean.getCurrentThreadCpuTime();
            this.stopUserNs = this.threadmxbean.getCurrentThreadCpuTime();
        }
        // Compilation time:
        if( this.compilationBean != null ) {
            this.stopCompTimeMillis = this.compilationBean.getTotalCompilationTime();
        }
        // Memory:
        this.peakHeapUsage = this.heapMemoryBean.getPeakUsage();
        this.peakNonHeapUsage = this.nonHeapMemoryBean.getPeakUsage();
        this.stopHeapUsage = this.memoryBean.getHeapMemoryUsage();
        this.stopNonHeapUsage = this.memoryBean.getNonHeapMemoryUsage();
        // Classloader:
        this.stopClassTotal = this.classLoaderBean.getTotalLoadedClassCount();
        // And done:
        this.stopped = true;
    }

    public List<Property> getPerformanceProperties() {
        // Stop if not already stopped:
        if( stopped == false ) this.stop();
        // Collect sys props
        List<Property> prps = ServiceProperties.collectSystemProperties();
        // Merge in other properties.
        // Timing properties:
        prps.add( ServiceProperties.createWallclockTimeProperty( (stopSystemNanoTime - startSystemNanoTime)/NANO_MILLI ) );
        if( stopTransferNanoTime >= 0 ) {
            prps.add( ServiceProperties.createWallclockTransferTimeProperty( (stopTransferNanoTime - startSystemNanoTime)/NANO_MILLI ) );
        }
        if( stopLoadedNanoTime >= 0 ) {
            prps.add( ServiceProperties.createWallclockLoadTimeProperty( (stopLoadedNanoTime - startSystemNanoTime)/NANO_MILLI ) );
        }
        // Thread timings:
        if( threadmxbean != null ) {
            prps.add( ServiceProperties.createCpuTimeProperty((stopCpuNs - startCpuNs)/NANO_MILLI ) );
            prps.add( ServiceProperties.createUserTimeProperty((stopUserNs - startUserNs)/NANO_MILLI ) );
        }
        // Compilation timing:
        if( this.compilationBean != null ) {
            prps.add( ServiceProperties.createCompilationTimeProperty(stopCompTimeMillis - startCompTimeMillis ) );
        }
        // Memory usage:
        prps.add( ServiceProperties.createPeakHeapMemoryProperty( this.peakHeapUsage.getUsed() ) );
        prps.add( ServiceProperties.createPeakNonHeapMemoryProperty( this.peakNonHeapUsage.getUsed() ) );
        // TODO Also store the start and stop memory use? Probably not as interesting as peak.
        // Classes loader:
        prps.add( ServiceProperties.createClassesLoadedProperty( this.stopClassTotal - this.startClassTotal ) );
        // and return:
        return prps;
    }



}
