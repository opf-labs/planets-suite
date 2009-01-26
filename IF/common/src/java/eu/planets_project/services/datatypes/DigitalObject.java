package eu.planets_project.services.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Representation of a concrete digital object, to be passed through web
 * services. As the other planets data types, it uses XmlAccessType.FIELD
 * instead of getters and setters. This allows for proper encapsulation on the
 * API side while remaining JAXB-serializable.
 * <p/>
 * This class is immutable in practice; its instances can therefore be shared
 * freely and concurrently. Instances are created using a builder to allow
 * optional named constructor parameters and ensure consistent state during
 * creation. E.g. to create a digital object with only the required arguments,
 * you'd use:
 * <p/>
 * {@code DigitalObject o = new DigitalObject.Builder(content).build();}
 * <p/>
 * You can cascade additional calls for optional arguments:
 * <p/>
 * {@code DigitalObject o = new
 * DigitalObject.Builder(content).manifestationOf(abstraction
 * ).title(title).build();}
 * <p/>
 * DigitalObject instances can be serialized to XML. Given such an XML
 * representation, a digital object can be instantiated using a static factory
 * method:
 * <p/>
 * {@code DigitalObject o = DigitalObject.of(xml);}
 * <p/>
 * For usage examples, see the tests in {@link DigitalObjectTests} and web
 * service sample usage in
 * {@link eu.planets_project.ifr.core.simple.impl.PassThruMigrationService#migrate}
 * (pserv/IF/simple).
 * <p/>
 * A corresponding XML schema can be generated from this class by running this
 * class as a Java application, see {@link #main(String[])}.
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 * @see DigitalObjectTests
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class DigitalObject implements Comparable<DigitalObject>,
        Serializable {
    /** Generated UID. */
    private static final long serialVersionUID = -893249048201058999L;

    /** @see {@link #getTitle()} */
    @XmlAttribute
    private String title;

    /** @see {@link #getFormat()} */
    @XmlAttribute
    private URI format;

    /** @see {@link #getPermanentUrl()} */
    private URL permanentUrl;

    /** @see {@link #getManifestationOf()} */
    @XmlAttribute
    private URI manifestationOf;

    /** @see {@link #getChecksum()} */
    @XmlElement
    private Checksum checksum;

    /** @see {@link #getMetadata()} */
    @XmlElement
    private List<Metadata> metadata;

    /** @see {@link #getContained()} */
    @XmlElement
    private List<DigitalObject> contained;

    /** @see {@link #getContent()} */
    @XmlElement(required = true)
    private Content content;

    /** @see {@link #getEvents()} */
    @XmlElement
    private List<Event> events;

    /** @see {@link #getFragments()} */
    @XmlElement
    private List<Fragment> fragments;

    /**
     * @param content The content for the digital object to build
     * @return The builder, to alter the created digital object; call build() on
     *         the builder to create the actual digital object
     */
    public static DigitalObject.Builder create(final Content content) {
        return new DigitalObject.Builder(content);
    }

    /**
     * @param digitalObject The digital object to copy
     * @return The builder, to alter the copied digital object; call build() on
     *         the builder to create the actual digital object
     */
    public static DigitalObject.Builder copy(final DigitalObject digitalObject) {
        return new DigitalObject.Builder(digitalObject);
    }

    /**
     * A digital object fragment.
     */
    public static final class Fragment {
        /** The fragment ID. */
        @XmlAttribute
        private String id;

        /** No-arg constructor for JAXB. Client should not use this. */
        @SuppressWarnings("unused")
        private Fragment() {}

        /**
         * @param id The ID
         */
        public Fragment(final String id) {
            this.id = id;
        }

        /**
         * @return The ID
         */
        public String getId() {
            return id;
        }
    }

    /**
     * Builder for DigitalObject instances. Using a builder ensures consistent
     * object state during creation and models optional named constructor
     * parameters.
     * @see eu.planets_project.services.datatypes.DigitalObjectTests
     */
    public static final class Builder {
        /* Required parameter: */
        private Content content;
        /* Optional parameters, initialized to default values: */
        private URL permanentUrl = null;
        private List<Event> events = new ArrayList<Event>();
        private List<Fragment> fragments = new ArrayList<Fragment>();
        private List<DigitalObject> contained = new ArrayList<DigitalObject>();
        private URI manifestationOf = null;
        private Checksum checksum = null;
        private List<Metadata> metadata = null;
        private URI format = null;
        private String title = null;

        /** @return The instance created using this builder. */
        public DigitalObject build() {
            return new DigitalObject(this);
        }

        /**
         * Constructs an anonymous (permanentUrl == null) digital object.
         * @param content The content of the digital object.
         */
        public Builder(final Content content) {
            this.content = content;
        }

        /**
         * @param digitalObject An existing digital object to copy into an new
         *        anonymous (permanentUrl == null) digital object.
         */
        public Builder(final DigitalObject digitalObject) {
            content = digitalObject.content;
            contained = digitalObject.contained;
            events = digitalObject.events;
            fragments = digitalObject.fragments;
            manifestationOf = digitalObject.manifestationOf;
            title = digitalObject.title;
            checksum = digitalObject.checksum;
            metadata = digitalObject.metadata;
            format = digitalObject.format;
        }

        /** No-arg constructor for JAXB. API clients should not use this. */
        @SuppressWarnings("unused")
        private Builder() {}

        /**
         * @param content The new content for the digital object to be created
         * @return The builder, for cascaded calls
         */
        public Builder content(final Content content) {
            this.content = content;
            return this;
        }

        /**
         * @param permanentUrl The globally unique locator and identifier for
         *        this digital object.
         * @return The builder, for cascaded calls
         */
        public Builder permanentUrl(final URL permanentUrl) {
            this.permanentUrl = permanentUrl;
            return this;
        }

        /**
         * @param events The events of the digital object
         * @return The builder, for cascaded calls
         */
        public Builder events(final Event... events) {
            this.events = new ArrayList<Event>(Arrays.asList(events));
            return this;
        }

        /**
         * @param fragments The fragments the digital object is made of
         * @return The builder, for cascaded calls
         */
        public Builder fragments(final Fragment... fragments) {
            this.fragments = new ArrayList<Fragment>(Arrays.asList(fragments));
            return this;
        }

        /**
         * @param contained The contained digital objects
         * @return The builder, for cascaded calls
         */
        public Builder contains(final DigitalObject... contained) {
            this.contained = new ArrayList<DigitalObject>(Arrays
                    .asList(contained));
            return this;
        }

        /**
         * @param manifestationOf What the digital object is a manifestation of
         * @return The builder, for cascaded calls
         */
        public Builder manifestationOf(final URI manifestationOf) {
            this.manifestationOf = manifestationOf;
            return this;
        }

        /**
         * @param title The title of the digital object
         * @return The builder, for cascaded calls
         */
        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        /**
         * @param checksum The digital object's checksum
         * @return The builder, for cascaded calls
         */
        public Builder checksum(Checksum checksum) {
            this.checksum = checksum;
            return this;
        }

        /**
         * @param metadata Additional metadata for the digital object
         * @return The builder, for cascaded calls
         */
        public Builder metadata(Metadata... metadata) {
            this.metadata = new ArrayList<Metadata>(Arrays.asList(metadata));
            return this;
        }

        /**
         * @param format The type of the digital object
         * @return The builder, for cascaded calls
         */
        public Builder format(URI format) {
            this.format = format;
            return this;
        }
    }

    /**
     * @param builder The builder to construct a digital object from
     */
    private DigitalObject(final Builder builder) {
        permanentUrl = builder.permanentUrl;
        content = builder.content;
        contained = builder.contained;
        events = builder.events;
        fragments = builder.fragments;
        manifestationOf = builder.manifestationOf;
        title = builder.title;
        checksum = builder.checksum;
        metadata = builder.metadata;
        format = builder.format;
    }

    /**
     * No-args constructor for JAXB serialization. Should not be called by an
     * API client. Clients should use:
     * <p/>
     * {@code new DigitalObject.Builder(required args...)optional
     * args...build();}
     */
    private DigitalObject() {}

    /**
     * @param xml The XML representation of a digital object (as created from
     *        calling toXml)
     * @return A digital object instance created from the given XML
     */
    public static DigitalObject of(final String xml) {
        try {
            /* Unmarshall with JAXB: */
            JAXBContext context = JAXBContext.newInstance(DigitalObject.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(xml));
            DigitalObject unmarshalled = (DigitalObject) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return An XML representation of this digital object (can be used to
     *         instantiate an object using the static factory method)
     */
    public String toXml() {
        try {
            /* Marshall with JAXB: */
            JAXBContext context = JAXBContext.newInstance(DigitalObject.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() {
        int contentSize = content == null ? 0 : 1;
        int containedSize = contained == null ? 0 : contained.size();
        int eventsSize = events == null ? 0 : events.size();
        int fragmentsSize = fragments == null ? 0 : fragments.size();
        int metaSize = metadata == null ? 0 : metadata.size();
        return String
                .format(
                        "DigitalObject: id '%s', title '%s'; %s content elements, "
                                + "%s contained objects, %s events, %s fragments; "
                                + "type '%s', manifestation of '%s', checksum '%s', metadata '%s'",
                        permanentUrl, title, contentSize, containedSize,
                        eventsSize, fragmentsSize, format, manifestationOf,
                        checksum, metaSize);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final DigitalObject o) {
        if (this.permanentUrl != null && o.permanentUrl != null) {
            /* The ID is optional, so if we have one we use it: */
            return this.permanentUrl.toString().compareTo(
                    o.permanentUrl.toString());
        } else if (this.permanentUrl != null || o.permanentUrl != null) {
            /* If only one of them is defined, they are not equal: */
            return -1;
        } else {
            /* But if none is, we use the XML serialization: */
            return this.toXml().compareTo(o.toXml());
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DigitalObject
                && this.compareTo((DigitalObject) obj) == 0;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (permanentUrl != null) {
            return permanentUrl.toString().hashCode();
        } else {
            return this.toXml().hashCode();
        }
    }

    /**
     * @return The title of this digital object.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The type of this digital object.
     */
    public URI getFormat() {
        return format;
    }

    /**
     * @return The unique identifier. Required.
     */
    public URL getPermanentUrl() {
        return permanentUrl;
    }

    /**
     * @return The URI that this digital object is a manifestation of.
     */
    public URI getManifestationOf() {
        return manifestationOf;
    }

    /**
     * @return The checksum for this digital object.
     */
    public Checksum getChecksum() {
        return checksum;
    }

    /**
     * @return Additional repository-specific metadata. Returns a defensive
     *         copy, changes to the obtained list won't affect this digital
     *         object.
     */
    public List<Metadata> getMetadata() {
        return new ArrayList<Metadata>(metadata);
    }

    /**
     * @return The 0..n digital objects contained in this digital object.
     *         Returns a defensive copy, changes to the obtained list won't
     *         affect this digital object.
     */
    public List<DigitalObject> getContained() {
        return new ArrayList<DigitalObject>(contained);
    }

    /**
     * @return The actual content references. Required. Returns a defensive
     *         copy, changes to the obtained list won't affect this digital
     *         object.
     */
    public Content getContent() {
        return content;
    }

    /**
     * @return The 0..n events that happened to this digital object. Returns a
     *         defensive copy, changes to the obtained list won't affect this
     *         digital object.
     */
    public List<Event> getEvents() {
        return new ArrayList<Event>(events);
    }

    /**
     * @return The 0..n fragments this digital object consists of. Returns a
     *         defensive copy, changes to the obtained list won't affect this
     *         digital object.
     */
    public List<Fragment> getFragments() {
        return new ArrayList<Fragment>(fragments);
    }

    /* Schema generation: */

    /***/
    private static java.io.File baseDir = new java.io.File(
            "IF/common/src/resources");
    /***/
    private static String schemaFileName = "digital_object.xsd";

    /** Resolver for schema generation. */
    static class Resolver extends SchemaOutputResolver {
        /**
         * {@inheritDoc}
         * @see javax.xml.bind.SchemaOutputResolver#createOutput(java.lang.String,
         *      java.lang.String)
         */
        public Result createOutput(final String namespaceUri,
                final String suggestedFileName) throws IOException {
            return new StreamResult(new java.io.File(baseDir, schemaFileName));
        }
    }

    /**
     * Generates the XML schema for this class.
     * @param args Ignored
     */
    public static void main(final String[] args) {
        try {
            Class<DigitalObject> clazz = DigitalObject.class;
            JAXBContext context = JAXBContext.newInstance(clazz);
            context.generateSchema(new Resolver());
            System.out.println("Generated XML schema for "
                    + clazz.getSimpleName()
                    + " at "
                    + new java.io.File(baseDir, schemaFileName)
                            .getAbsolutePath());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
