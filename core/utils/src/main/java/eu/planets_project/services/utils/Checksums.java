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

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Checksums util class.
 */
public class Checksums {

    private static final int bufferSize = 2048;

    /**
     * Calculate the checksum of a given {@link InputStream}.
     * The stream is guaranteed to be closed after ended operation.
     * @param algorithm the algorithm to use to compute the digest. Possbile values can be found in the <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/CryptoSpec.html">Java CryptoSpec</a>.
     * @param in the stream to digest
     * @return the computed digest in a byte array
     * @throws IOException if there is an error reading the input stream
     * @throws NoSuchAlgorithmException if the algorithm requested in {@code algorithm} isn't known to the jvm.
     */
    public static byte[] digest (String algorithm, InputStream in)
                            throws IOException, NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            byte[] buffer = new byte[bufferSize];
            int count = 0;
            while ((count = in.read(buffer)) > 0) {
                md.update(buffer, 0, count);
            }

            return md.digest();
        } finally {
            in.close();
        }
    }

    /**
     * Calculate the checksum of a byte array. The array will be read in one chunk.
     * @param algorithm the algorithm to use to compute the digest. Possbile values can be found in the <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/CryptoSpec.html">Java CryptoSpec</a>.
     * @param in array the byte array to compute the digest of
     * @return the computed digest in a byte array
     * @throws NoSuchAlgorithmException if the algorithm requested in {@code algorithm} isn't known to the jvm.
     */
    public static byte[] digest (String algorithm, byte[] in)
                            throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(in);
            return md.digest();
    }

    /**
     * Calculate the checksum of a given {@link String}.
     * @see #digest(String, java.io.InputStream)
     */
    public static byte[] digest (String algorithm, String in) throws NoSuchAlgorithmException {
        return digest (algorithm, in.getBytes());
    }

    /**
     * Calculate the checksum of a given {@link File}.
     * @see #digest(String, java.io.InputStream)
     */
    public static byte[] digest (String algorithm, File in) throws NoSuchAlgorithmException, IOException {
        return digest (algorithm, new BufferedInputStream(new FileInputStream(in)));
    }

    /**
     * Calculate the {@code SHA-1} checksum of a given stream.
     * @throws IOException if there is an error reading the stream
     */
    public static byte[] sha1 (InputStream in) throws IOException {
        try {
            return digest ("SHA-1", in);
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException("Unknown algorithm: SHA-1");
        }
    }

    /**
     * Calculate the {@code SHA-1} checksum of a string.
     * @throws DigestException if the JVM doesn't know the {@code SHA-1} algorithm
     */
    public static byte[] sha1 (String in) {
        try {
            return digest ("SHA-1", in);
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException("Unknown algorithm: SHA-1");
        }
    }

    /**
     * Calculate the {@code SHA-1} checksum of a {@link File}.
     * @throws IOException if there is an error reading the file
     * @throws DigestException if the JVM doesn't know the {@code SHA-1} algorithm
     */
    public static byte[] sha1 (File in) throws IOException {
        try {
            return digest ("SHA-1", in);
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException("Unknown algorithm: SHA-1");
        }
    }

    /**
     * Calculate the {@code MD5} checksum of a given stream.
     * @throws IOException if there is an error reading the stream
     * @throws DigestException if the JVM doesn't know the {@code MD5} algorithm
     */
    public static byte[] md5 (InputStream in) throws IOException {
        try {
            return digest ("MD5", in);
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException("Unknown algorithm: MD5");
        }
    }

    /**
     * Calculate the {@code MD5} checksum of a string.
     * @throws DigestException if the JVM doesn't know the {@code MD5} algorithm
     */
    public static byte[] md5 (String in) {
        try {
            return digest ("MD5", in);
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException("Unknown algorithm: MD5");
        }
    }

    /**
     * Calculate the {@code MD5} checksum of a {@link File}.
     * @throws IOException if there is an error reading the file
     * @throws DigestException if the JVM doesn't know the {@code MD5} algorithm
     */
    public static byte[] md5 (File in) throws IOException {
        try {
            return digest ("MD5", in);
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException("Unknown algorithm: MD5");
        }
    }


}
