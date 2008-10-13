package eu.planets_project.services.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
 * API side while remaining JAXB-serializable. <p/>
 * 
 * This class is immutable in practice; its instances can therefore be shared
 * freely and concurrently. Instances are created using a builder to allow
 * optional named constructor parameters and ensure consistent state during
 * creation. E.g. to create a digital object with only the required arguments,
 * you'd use:<p/>
 * 
 * {@code DigitalObject o = new DigitalObject.Builder(id, content).build();}<p/>
 * 
 * You can cascade additional calls for optional arguments:<p/>
 * 
 * {@code DigitalObject o = new DigitalObject.Builder(id,
 * content).manifestationOf(abstraction).title(title).build();}<p/>
 * 
 * DigitalObject instances can be serialized to XML. Given such an XML
 * representation, a digital object can be instantiated using a static factory
 * method:<p/>
 * 
 * {@code DigitalObject o = DigitalObject.of(xml);} <p/>
 * 
 * For usage examples, see the tests in {@link DigitalObjectTest} and web
 * service sample usage in {@link PassThruMigrationService} (pserv/IF/simple).<p/>
 * 
 * A corresponding XML schema can be generated from this class by running this
 * class as a Java application, see {@link #main(String[])}.
 * 
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 * @see DigitalObjectTest
 * @see Migrate
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class DigitalObject implements Comparable<DigitalObject>,
        Serializable {
    
    /** Generated UID. */
    private static final long serialVersionUID = -893249048201058999L;
    
    /** @See {@link #getTitle()} */
    @XmlAttribute
    private String title;
    
    /** @See {@link #getPlanetsFormatUri()} */
    @XmlAttribute
    private URI planetsFormatUri;
    
    /** @See {@link #getPermanentUrl()} */
    @XmlAttribute(required = true)
    private URL permanentUrl;
    
    /** @See {@link #getManifestationOf()} */
    @XmlAttribute
    private URI manifestationOf;
    
    /** @See {@link #getChecksum()} */
    @XmlAttribute
    private String checksum;
    
    /** @See {@link #getTaggedMetadata()} */
    @XmlAttribute
    private String taggedMetadata;
    
    /** @See {@link #getContained()} */
    @XmlElement
    private List<DigitalObject> contained;
    
    /** @See {@link #getContent()} */
    @XmlElement(required = true)
    private Content content;
    
    /** @See {@link #getEvents()} */
    @XmlElement
    private List<String> events;
    
    /** @See {@link #getFragmentIds()} */
    @XmlElement
    private List<String> fragmentIds;

    /**
     * Builder for DigitalObject instances. Using a builder ensures consistent
     * object state during creation and models optional named constructor
     * parameters.
     * 
     * @see eu.planets_project.ifr.core.common.services.datatypes.DigitalObjectTest
     */
    public static final class Builder {
        /* Required parameters: */
        private URL permanentUrl;
        private Content content;
        /* Optional parameters, initialized to default values: */
        private List<String> events = new ArrayList<String>();
        private List<String> fragmentIds = new ArrayList<String>();
        private List<DigitalObject> contained = new ArrayList<DigitalObject>();
        private URI manifestationOf = null;
        private String checksum = null;
        private String taggedMetadata = null;
        private URI planetsFormatUri = null;
        private String title = null;

        /** @return The instance created using this builder. */
        public DigitalObject build() {
            return new DigitalObject(this);
        }

        /**
         * @param permanentUrl The unique ID for the digital object
         * @param content The content of the digital object
         */
        public Builder(final URL permanentUrl, final Content content) {
            this.permanentUrl = permanentUrl;
            this.content = content;
        }

        /** No-arg constructor for JAXB. API clients should not use this. */
        public Builder() {}

        /**
         * @param events The events of the digital object
         * @return The builder, for cascaded calls
         */
        public Builder events(final List<String> events) {
            this.events = new ArrayList<String>(events);
            return this;
        }

        /**
         * @param fragmentIds The fragments the digital object is made of
         * @return The builder, for cascaded calls
         */
        public Builder fragmentIds(final List<String> fragmentIds) {
            this.fragmentIds = new ArrayList<String>(fragmentIds);
            return this;
        }

        /**
         * @param contained The contained digital objects
         * @return The builder, for cascaded calls
         */
        public Builder contains(final List<DigitalObject> contained) {
            this.contained = new ArrayList<DigitalObject>(contained);
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
        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        /**
         * @param taggedMetadata Additional metadata for the digital object
         * @return The builder, for cascaded calls
         */
        public Builder taggedMetadata(String taggedMetadata) {
            this.taggedMetadata = taggedMetadata;
            return this;
        }

        /**
         * @param planetsFormatUri The type of the digital object
         * @return The builder, for cascaded calls
         */
        public Builder planetsFormatUri(URI planetsFormatUri) {
            this.planetsFormatUri = planetsFormatUri;
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
        fragmentIds = builder.fragmentIds;
        manifestationOf = builder.manifestationOf;
        title = builder.title;
        checksum = builder.checksum;
        taggedMetadata = builder.taggedMetadata;
        planetsFormatUri = builder.planetsFormatUri;
    }

    /**
     * No-args constructor for JAXB serialization. Should not be called by an
     * API client. Clients should use: <p/>
     * 
     * {@code new DigitalObject.Builder(required args...)optional
     * args...build();}
     */
    public DigitalObject() {}

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
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String
                .format(
                        "DigitalObject: id '%s', title '%s'; %s content elements, "
                                + "%s contained objects, %s events, %s fragments; "
                                + "type '%s', manifestation of '%s', checksum '%s', metadata '%s'",
                        permanentUrl, title, content == null ? 0 : content
                                .isBinary(), contained == null ? 0 : contained
                                .size(), events == null ? 0 : events.size(),
                        fragmentIds == null ? 0 : fragmentIds.size(),
                        planetsFormatUri, manifestationOf, checksum,
                        taggedMetadata);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final DigitalObject o) {
        return this.permanentUrl.toString()
                .compareTo(o.permanentUrl.toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DigitalObject
                && (this.compareTo((DigitalObject) obj) == 0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return permanentUrl.toString().hashCode();
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
    public URI getPlanetsFormatUri() {
        return planetsFormatUri;
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
    public String getChecksum() {
        return checksum;
    }

    /**
     * @return Additional repository-specific metadata.
     */
    public String getTaggedMetadata() {
        return taggedMetadata;
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
     * @return The actual content references. Required. 
     */
    public Content getContent() {
        return content;
    }

    /**
     * @return The 0..n events that happened to this digital object. Returns a
     *         defensive copy, changes to the obtained list won't affect this
     *         digital object.
     */
    public List<String> getEvents() {
        return new ArrayList<String>(events);
    }

    /**
     * @return The 0..n fragments this digital object consists of. Returns a
     *         defensive copy, changes to the obtained list won't affect this
     *         digital object.
     */
    public List<String> getFragmentIds() {
        return new ArrayList<String>(fragmentIds);
    }

    /* Schema generation: */

    /***/
    private static java.io.File baseDir = new java.io.File(
            "components/common/src/main/resources");
    /***/
    private static String schemaFileName = "digital_object.xsd";

    /** Resolver for schema generation. */
    static class Resolver extends SchemaOutputResolver {
        /**
         * {@inheritDoc}
         * 
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
     * 
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
