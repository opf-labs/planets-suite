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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * Tests for digital objects.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * @see DigitalObject
 */
public final class DigitalObjectTests {

    /**
     * Simple sample usage (only required values).
     * @throws MalformedURLException
     */
    @Test
    public void usage1() throws MalformedURLException {
        /* A simple example with only required values: */
        URI id = URI.create("http://id");
        /* Either by reference: */
        DigitalObject o = new DigitalObject.Builder(Content.byReference(new URL("http://planets-project.eu")))
                .permanentUri(id).build();
        assertEquals(o, new DigitalObject.Builder(o.toXml()).build());
        /* Or use the factory method to create...: */
        o = new DigitalObject.Builder(Content.byValue(new File("build.xml"))).permanentUri(id).build();
        assertEquals(o, new DigitalObject.Builder(o.toXml()).build());
        /* Or to copy a digital object: */
        o = new DigitalObject.Builder(o).build();
        assertEquals(o, new DigitalObject.Builder(o.toXml()).build());
        /* Or by value: */
        o = new DigitalObject.Builder(Content.byValue(new File("build.xml"))).permanentUri(id).build();
        /*
         * These objects can be serialized to XML and instantiated from that form:
         */
        assertEquals(o, new DigitalObject.Builder(o.toXml()).build());
    }

    /**
     * More complex sample usage (some optional parameters). See further below for more examples.
     * @throws MalformedURLException
     */
    @Test
    public void usage2() throws MalformedURLException {
        /* For a more complex sample, we set up a few things we need: */
        URI purl = URI.create("http://id");
        URL data1 = new URL("http://some.reference");
        /* Create an optional checksum: */
        String algorithm = "MD5";
        String value = "the checksum data";
        Checksum checksum = new Checksum(algorithm, value);
        // byte[] data2 = new byte[] {};// see ContentTests for a real sample
        /* Create the content: */
        DigitalObjectContent c1 = Content.byReference(data1).withChecksum(checksum);
        /* Create some optional metadata: */
        URI type = URI.create("meta:/data.type");
        String metaContent = "the meta data";
        Metadata meta = new Metadata(type, metaContent);
        Assert.assertNotNull(c1.getChecksum());
        Assert.assertEquals(algorithm, c1.getChecksum().getAlgorithm());
        Assert.assertEquals(value, c1.getChecksum().getValue());
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUri(purl).metadata(meta).build();
        System.out.println("Created: " + object);

    }

    private static final String SOME_URL_1 = "http://url1";
    private static final String SOME_URL_2 = "http://url2";
    private static final Checksum CHECKSUM = new Checksum("algo", "checksum");
    private static final Event EVENT = new Event(null, null, 0d, null, null);
    private static final String FRAGMENT = "ID";
    private static final Metadata META = new Metadata(URI.create(SOME_URL_1), "meta");
    private static final String TITLE = "title";
    private DigitalObject digitalObject1;
    private DigitalObject digitalObject2;

    /**
     * The ensure consistent state during creation and support named optional constructor parameters, digital objects
     * are created using a builder.
     */
    @Before
    public void createInstances() {
        try {
            URI permanentUrl = URI.create(SOME_URL_1);
            URI manifestationOf = URI.create(SOME_URL_1);
            URI planetsFormatUri = URI.create(SOME_URL_1);
            /* Creation with only required arguments: */
            digitalObject2 = new DigitalObject.Builder(Content.byReference(new URL(SOME_URL_2))).permanentUri(
                    URI.create(SOME_URL_2)).build();
            /* Creation with all optional arguments: */
            DigitalObjectContent content = Content.byReference(permanentUrl.toURL()).withChecksum(CHECKSUM);
            digitalObject1 = new DigitalObject.Builder(content).permanentUri(permanentUrl).events(EVENT).fragments(
                    FRAGMENT).manifestationOf(manifestationOf).format(planetsFormatUri).metadata(META).title(TITLE)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * DigitalObject instances also work with Java's set collections, which use equals for comparison, not comparable as
     * the static sorting method in Collections.
     * @throws MalformedURLException
     */
    @Test
    public void equality() throws MalformedURLException {
        Set<DigitalObject> set = new HashSet<DigitalObject>();
        set.add(digitalObject1);
        set.add(digitalObject1);
        /* The permanent URL is optional: */
        DigitalObject anon = new DigitalObject.Builder(Content.byReference(new URL(SOME_URL_1))).build();
        set.add(anon);
        set.add(anon);
        assertTrue("Set of digital objects contains duplicate entries;", set.size() == 2);
    }

    /**
     * Test XML serialization for a digital object using a URL reference.
     * @throws MalformedURLException
     */
    @Test
    public void toXmlWithUrl() throws MalformedURLException {
        DigitalObject digitalObject = new DigitalObject.Builder(Content.byReference(new File("build.xml").toURI()
                .toURL())).build();
        Assert.assertTrue("XML representation of DigitalObject must not be null", digitalObject.toXml() != null);
    }

    /**
     * Test XML serialization for a digital object using a data handler for content streaming.
     * @throws MalformedURLException
     */
    @Test
    public void toXmlWithDataHandler() throws MalformedURLException {
        DigitalObject digitalObject = new DigitalObject.Builder(Content.byReference(new File("build.xml"))).build();
        Assert.assertTrue("XML representation of DigitalObject must not be null", digitalObject.toXml() != null);
    }

    /**
     * Digital objects offer XML serialization vie API.
     */
    @Test
    public void xmlSerializationRoundtrip() {
        System.out.println("Original: " + digitalObject1);
        String xml = digitalObject1.toXml();
        System.out.println("XML: " + xml);
        DigitalObject deserialized = new DigitalObject.Builder(xml).build();
        System.out.println("Unmarshalled: " + digitalObject1);
        compare(digitalObject1, deserialized);
    }

    /**
     * As digital objects should be used as parameters and return values of web service calls, they need to be
     * serializable using JAXB.
     */
    @Test
    public void jaxbSerialization() {
        System.out.println("Original: " + digitalObject1);
        DigitalObject roundtrip = roundtrip(digitalObject1);
        System.out.println("Unmarshalled: " + digitalObject1);
        compare(digitalObject1, roundtrip);
    }

    /**
     * As a helper method for counting the size of the bytestream content has been added, this should be tested.
     * <p/>
     * TODO: Should go into a DigitalObjectUtilsTests class
     */
    @Test
    public void contentSizeCalculation() {
        int size = 23823;
        DigitalObject bytes1 = new DigitalObject.Builder(Content.byValue(new byte[size])).build();
        long bytes = DigitalObjectUtils.getContentSize(bytes1);
        assertEquals("Counted, shallow byte[] size is not correct.", size, bytes);
    }

    /**
     * @param one The first digital object
     * @param two The second digital object
     */
    private void compare(final DigitalObject one, final DigitalObject two) {
        assertEquals("Original and unmarshalled object are not equal;", one, two);
        assertEquals("Original and unmarshalled toString representations are not equal;", one.toString(), two
                .toString());
    }

    /**
     * @param dObject The digital object to marshall and unmarschall.
     * @return the digital object unmarshalled from the marshalled form of the given digital object
     */
    private DigitalObject roundtrip(final DigitalObject dObject) {
        try {
            JAXBContext context = JAXBContext.newInstance(ImmutableDigitalObject.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(dObject, writer);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(writer.toString()));
            DigitalObject unmarshalled = (DigitalObject) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("JAXB marshalling of digital object failed;");
        }
        return null;
    }

    @Test
    public void schemaGeneration() {
        ImmutableDigitalObject.main(new String[] {});
    }

}
