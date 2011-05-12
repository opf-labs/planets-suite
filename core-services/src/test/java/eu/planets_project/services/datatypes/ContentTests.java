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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Content objects. Reads the same data using Content objects both by value and by
 * reference, checking for equality of the results.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ContentTests {
    private static final String LOCATION = "test/data/content/sample_content.txt";

    private File file;
    private URL url;
    private InputStream stream;
    private byte[] byteArray;
    private String content;

    @Before
    public void init() throws IOException, URISyntaxException {
    	this.url=ClassLoader.getSystemResource(LOCATION);
        this.file = new File(this.url.toURI());
        this.stream = this.url.openStream();
        this.byteArray = IOUtils.toByteArray(new FileInputStream(this.file));
        this.content = read(new FileInputStream(this.file));
    }

    @Test
    public void byReferenceToFile() {
        test(Content.byReference(this.file));
    }
    
    @Test
    public void byReferenceToInputStream() {
        test(Content.byReference(this.stream));
    }
    
    @Test
    public void byReferenceToUrl() {
        test(Content.byReference(this.url));
    }

    @Test
    public void byValueOfFile() {
        test(Content.byValue(this.file));
    }
    
    @Test
    public void byValueOfInputStream() {
        test(Content.byValue(this.stream));
    }
    
    @Test
    public void byValueOfByteArray() {
        test(Content.byValue(this.byteArray));
    }

    private void test(DigitalObjectContent object) {
    	System.out.println(this.content);
    	System.out.println(read(object.getInputStream()));
        Assert.assertTrue("Original content and wrapped content should be equal", this.content.equals(read(object.getInputStream())));
    }

    @Test
    public void equals() {
        DigitalObjectContent c1 = Content.byReference(this.url);
        DigitalObjectContent c2 = Content.byReference(this.url);
        assertEquals("Equal object don't equal;", c1, c2);
        assertEquals("Equal objects have different string representations;", c1.toString(), c2
                .toString());

    }

    @Test
    public void hashcode() {
        Set<DigitalObjectContent> set = new HashSet<DigitalObjectContent>(Arrays.asList(Content
                .byReference(this.url), Content.byReference(this.url), Content.byReference(this.url)));
        assertEquals("Set contains duplicates;", 1, set.size());
    }

    /**
     * @param source The source to read from
     * @return Returns the content of the source
     */
    private String read(final InputStream source) {
        StringBuilder builder = new StringBuilder();
        Scanner s = new Scanner(source);
        while (s.hasNextLine()) {
            builder.append(s.nextLine()).append("\n");
        }
        return builder.toString().trim();
    }

}
