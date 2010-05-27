package eu.planets_project.services.java_se.image.metrics;

/**
 * Utility class to perform Kahan summation of a series of numbers.
 * 
 * See http://en.wikipedia.org/wiki/Kahan_summation_algorithm
 * 
 * @author AnJackson
 */
public class KahanSummation {

    private double sum = 0;

    /** A running compensation for lost low-order bits. */
    private double c = 0;

    /** temporary variables */
    private double y, t;

    public synchronized void add( double in ) {
        y = in - c;
        t = sum + y;
        c = (t - sum) - y;
        sum = t;
    }

    public double getSum() {
        return sum;
    }
}