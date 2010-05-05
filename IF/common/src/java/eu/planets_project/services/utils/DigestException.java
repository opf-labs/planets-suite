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

/**
 * A {@link RuntimeException} thrown when there is unexpected errors
 * computing a digest.
 *
 * <p>For example when you checksum a fixed {@link String} with
 * a well known digest such as MD5, errors a re not expected.
 *
 * <p>It is also used to hide explicit code to handle {@link java.security.NoSuchAlgorithmException}s
 * in cases where you request algorithms required by the
 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/CryptoSpec.html">Java CryptoSpec</a>. Fx
 * {@code MD5} and {@code SHA-1}.
 */
public class DigestException extends RuntimeException {

    /** Generated ID. */
    private static final long serialVersionUID = 1921532045016030988L;

    /**
     * @param message The message
     */
    public DigestException (String message) {
        super (message);
    }

    /**
     * @param message The message
     * @param t The throwable
     */
    public DigestException (String message, Throwable t) {
        super (message, t);
    }

}
