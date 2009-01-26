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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

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
        URL id = new URL("http://id");
        /* Either by reference: */
        DigitalObject o = new DigitalObject.Builder(Content
                .byReference(new URL("http://some.reference")))
                .permanentUrl(id).build();
        assertEquals(o, DigitalObject.of(o.toXml()));
        /* Or use the factory method to create...: */
        o = DigitalObject.create(Content.byValue(new File("build.xml")))
                .build();
        assertEquals(o, DigitalObject.of(o.toXml()));
        /* Or to copy a digital object: */
        o = DigitalObject.copy(o).build();
        assertEquals(o, DigitalObject.of(o.toXml()));
        /* Or by value: */
        o = new DigitalObject.Builder(Content.byValue(new File("build.xml")))
                .permanentUrl(id).build();
        /*
         * These objects can be serialized to XML and instantiated from that
         * form:
         */
        assertEquals(o, DigitalObject.of(o.toXml()));
    }

    /**
     * More complex sample usage (some optional parameters). See further below
     * for more examples.
     * @throws MalformedURLException
     */
    @Test
    public void usage2() throws MalformedURLException {
        /* For a more complex sample, we set up a few things we need: */
        URL purl = new URL("http://id");
        URL data1 = new URL("http://some.reference");
        // byte[] data2 = new byte[] {};// see ContentTests for a real sample
        /* Create the content: */
        Content c1 = Content.byReference(data1);
        // Content c2 = Content.byValue(data2);
        /* Create some optional metadata: */
        URI type = URI.create("meta:/data.type");
        String metaContent = "the meta data";
        Metadata meta = new Metadata(type, metaContent);
        /* Create an optional checksum: */
        String algorithm = "MD5";
        String value = "the checksum data";
        Checksum checksum = new Checksum(algorithm, value);
        /* Given these, we can instantiate our object: */
        DigitalObject object = new DigitalObject.Builder(c1).permanentUrl(purl)
                .metadata(meta).checksum(checksum).build();
        System.out.println("Created: " + object);

    }

    private static final String SOME_URL_1 = "http://url1";
    private static final String SOME_URL_2 = "http://url2";
    private static final Checksum CHECKSUM = new Checksum("algo", "checksum");
    private static final Event EVENT = new Event();
    private static final DigitalObject.Fragment FRAGMENT = new DigitalObject.Fragment(
            "ID");
    private static final Metadata META = new Metadata(URI.create(SOME_URL_1),
            "meta");
    private static final String TITLE = "title";
    private DigitalObject digitalObject1;
    private DigitalObject digitalObject2;

    /**
     * The ensure consistent state during creation and support named optional
     * constructor parameters, digital objects are created using a builder.
     */
    @Before
    public void createInstances() {
        try {
            URL permanentUrl = new URL(SOME_URL_1);
            URI manifestationOf = URI.create(SOME_URL_1);
            URI planetsFormatUri = URI.create(SOME_URL_1);
            /* Creation with only required arguments: */
            digitalObject2 = new DigitalObject.Builder(Content
                    .byReference(new URL(SOME_URL_2))).permanentUrl(
                    new URL(SOME_URL_2)).build();
            /* Creation with all optional arguments: */
            digitalObject1 = new DigitalObject.Builder(Content
                    .byReference(permanentUrl)).permanentUrl(permanentUrl)
                    .checksum(CHECKSUM).events(EVENT).fragments(FRAGMENT)
                    .manifestationOf(manifestationOf).format(planetsFormatUri)
                    .metadata(META).title(TITLE).contains(digitalObject2)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * DigitalObject instances are comparable and can thus be used with Java's
     * sorting mechanisms.
     */
    @Test
    public void sortDigitalObjects() {
        List<DigitalObject> objects = Arrays.asList(digitalObject2,
                digitalObject1);
        Collections.sort(objects);
        assertEquals("Sorting a collection of digital objects did not work;",
                digitalObject1, objects.get(0));
    }

    /**
     * DigitalObject instances also work with Java's set collections, which use
     * equals for comparison, not comparable as the static sorting method in
     * Collections.
     * @throws MalformedURLException
     */
    @Test
    public void equality() throws MalformedURLException {
        Set<DigitalObject> set = new HashSet<DigitalObject>();
        set.add(digitalObject1);
        set.add(digitalObject1);
        /* The permanent URL is optional: */
        DigitalObject anon = new DigitalObject.Builder(Content
                .byReference(new URL(SOME_URL_1))).build();
        set.add(anon);
        set.add(anon);
        assertTrue("Set of digital objects contains duplicate entries;", set
                .size() == 2);
    }

    /**
     * Digital objects offer XML serialization vie API.
     */
    @Test
    public void xmlSerialization() {
        System.out.println("Original: " + digitalObject1);
        String xml = digitalObject1.toXml();
        System.out.println("XML: " + xml);
        DigitalObject deserialized = DigitalObject.of(xml);
        System.out.println("Unmarshalled: " + digitalObject1);
        compare(digitalObject1, deserialized);
    }

    /**
     * As digital objects should be used as parameters and return values of web
     * service calls, they need to be serializable using JAXB.
     */
    @Test
    public void jaxbSerialization() {
        System.out.println("Original: " + digitalObject1);
        DigitalObject roundtrip = roundtrip(digitalObject1);
        System.out.println("Unmarshalled: " + digitalObject1);
        compare(digitalObject1, roundtrip);
    }

    /**
     * @param one The first digital object
     * @param two The second digital object
     */
    private void compare(final DigitalObject one, final DigitalObject two) {
        assertEquals("Original and unmarshalled object are not equal;", one,
                two);
        assertEquals(
                "Original and unmarshalled toString representations are not equal;",
                one.toString(), two.toString());
    }

    /**
     * @param dObject The digital object to marshall and unmarschall.
     * @return the digital object unmarshalled from the marshalled form of the
     *         given digital object
     */
    private DigitalObject roundtrip(final DigitalObject dObject) {
        try {
            JAXBContext context = JAXBContext.newInstance(DigitalObject.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(dObject, writer);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(writer
                    .toString()));
            DigitalObject unmarshalled = (DigitalObject) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("JAXB marshalling of digital object failed;");
        }
        return null;
    }

}
