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
/***/
package eu.planets_project.services.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for Checksum objects.
 * 
 * @author Fabian Steeg
 * 
 */
public final class ChecksumTests {
    private static final String VALUE = "value";
    private static final String ALGO = "algo";

    /** Test checksum object creation. */
    @Test
    public void creation() {
        Checksum c = instantiate(VALUE, ALGO);
        String message = "Value incorrect;";
        assertEquals(message, ALGO, c.getAlgorithm());
        assertEquals(message, VALUE, c.getValue());
    }

    /** Test checksum object sorting. */
    @Test
    public void sorting() {
        /* We sort checksums by algorithm first, next by the value: */
        Checksum c1 = instantiate(VALUE + "1", ALGO + "1");
        Checksum c2 = instantiate(VALUE + "2", ALGO + "1");
        Checksum c3 = instantiate(VALUE + "2", ALGO + "2");
        List<Checksum> sums = Arrays.asList(c3, c2, c1);
        System.out.println("Unsorted: " + sums);
        Collections.sort(sums);
        System.out.println("Sorted: " + sums);
        String message = "Wrong sorting!";
        assertEquals(message, c1, sums.get(0));
        assertEquals(message, c2, sums.get(1));
        assertEquals(message, c3, sums.get(2));
    }

    /** Test checksum object equality. */
    @Test
    public void equality() {
        Checksum checksum1 = new Checksum(VALUE, ALGO);
        Checksum checksum2 = new Checksum(VALUE, ALGO);
        assertEquals("Equality test fails;", checksum1, checksum2);
        Set<Checksum> sums = new HashSet<Checksum>(Arrays.asList(checksum1,
                checksum2));
        /* A set should contain no duplicates: */
        assertTrue("Not working with set; ", sums.size() == 1);
    }

    /**
     * @param v The value
     * @param a The algorithm
     * @return The instantiated and tested checksum object.
     */
    private Checksum instantiate(final String v, final String a) {
        Checksum c = new Checksum(a, v);
        assertTrue("Could not instantiate object;", c != null);
        System.out.println("Created: " + c);
        return c;
    }
}
