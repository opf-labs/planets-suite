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
package eu.planets_project.services.datatypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Static factory methods for content creation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Content {

    private Content() {/* enforce non-instantiability */};

    /*
     * We use static factory methods to provide named constructors for the
     * different kinds of content instances:
     */

    /**
     * Create content by reference.
     * @param reference The URL reference to the actual content
     * @return A content instance referencing the given location
     */
    public static DigitalObjectContent byReference(final URL reference) {
    	URI uriReference = null;
    	/* we do not really expect a URI syntax exception on conversion from URL ... */
    	try { uriReference = reference.toURI(); }
    	catch(URISyntaxException use) { System.out.println(use.getClass().getName()+": "+use.getMessage()); }
        return new ImmutableContent(uriReference);
    }

    /**
     * Create content by reference.
     * @param reference The URI reference to the content
     * @return A content instance referencing the given location
     */
    public static DigitalObjectContent byReference(final URI reference) {
        return new ImmutableContent(reference);
    }

    /**
     * Create (streamed) content by reference, from a file. Note that the file
     * must be left in place long enough for the web service client to complete
     * the access.
     * @param reference The reference to the actual content value, using a File
     *        whose content will be streamed over the connection.
     * @return A content instance referencing the given location.
     */
    public static DigitalObjectContent byReference(final File reference) {
        if (!reference.exists()) {
            throw new IllegalArgumentException("Given file does not exist: " + reference);
        }
        return new ImmutableContent(reference);
    }

    /**
     * Create (streamed) content by reference, from an input stream.
     * @param inputStream The InputStream containing the value for the content.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byReference(final InputStream inputStream) {
        try {
            File tempFile = File.createTempFile("tempContent", "tmp");
            tempFile.deleteOnExit(); // TODO do we really want this here? 
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copyLarge(inputStream, out);
            out.close();
            return new ImmutableContent(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not create content for input stream: " + inputStream);
    }

    /**
     * Create content by value (actually embedded in the request).
     * @param value The value bytes for the content
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final byte[] value) {
        return new ImmutableContent(value);
    }

    /**
     * Create content by value (actually embedded in the request).
     * @param value The value file for the content.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final File value) {
        if (!value.exists()) {
            throw new IllegalArgumentException("Given file does not exist: " + value);
        }
        try {
            return new ImmutableContent(FileUtils.readFileToByteArray(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not create content for file: " + value);
    }

    /**
     * Create content by value (actually embedded in the request).
     * @param inputStream The InputStream containing the value for the content.
     * @return A content instance with the specified value
     */
    public static DigitalObjectContent byValue(final InputStream inputStream) {
        try {
            return new ImmutableContent(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not create content for input stream: " + inputStream);
    }
}
