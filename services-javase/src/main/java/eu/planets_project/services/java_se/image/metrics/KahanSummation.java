package eu.planets_project.services.java_se.image.metrics;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Utility class to perform Kahan summation of a series of numbers.
 * 
 * See http://en.wikipedia.org/wiki/Kahan_summation_algorithm
 * 
 * @author AnJackson
 */
public class KahanSummation {
    private static Logger log = Logger.getLogger(KahanSummation.class.getName());

    private double sum = 0;
    private BigDecimal deciSum = new BigDecimal("0.0");

    /** A running compensation for lost low-order bits. */
    private double c = 0;

    /** temporary variables */
    private double y, t;

    public synchronized void add( double in ) {
        y = in - c;
        t = sum + y;
        c = (t - sum) - y;
        sum = t;
        deciSum = deciSum.add( new BigDecimal(in) );
    }

    public double getSum() {
        log.info("Got BigDecimal "+deciSum+" v. double " + sum );
        BigDecimal diff = deciSum.subtract(new BigDecimal(sum));
        if( diff.doubleValue() != 0.0 ) {
            log.severe("Kahan summation disagrees with BigDecimal by "+diff);
        }
        return sum;
    }
}