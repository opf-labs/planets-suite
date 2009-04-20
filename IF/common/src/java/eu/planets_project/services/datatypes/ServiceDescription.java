/**
 * 
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A entity to hold metadata about services. The content of this object was
 * first defined at the IF meeting in September 2008. This is intended to be
 * used primarily as an XML schema, but is defined in Java to make
 * reading/writing easier. See also, DOAP: http://trac.usefulinc.com/doap
 * <p/>
 * This class is immutable in practice; its instances can therefore be shared
 * freely and concurrently. Instances are created using a builder to allow
 * optional named constructor parameters and ensure consistent state during
 * creation. E.g. to create a service description with only the required
 * arguments, you'd use:
 * <p/>
 * {@code ServiceDescription d = new ServiceDescription.Builder(name,
 * type).build();}
 * <p/>
 * You can cascade additional calls for optional arguments:
 * <p/>
 * {@code ServiceDescription d = new ServiceDescription.Builder(name,
 * type).paths(path1,path2).logo(logo).build();}
 * <p/>
 * ServiceDescription instances can be serialized to XML. Given such an XML
 * representation, a service description can be instantiated using a static
 * factory method:
 * <p/>
 * {@code ServiceDescription d = ServiceDescription.of(xml);}
 * <p/>
 * To use a given service description (either as an object or as XML) as a
 * template for your service description, you can give it to the builder and add
 * or override values:
 * <p/>
 * {@code ServiceDescription d = new
 * ServiceDescription.Builder(xml).paths(path1, path2).logo(logo).build();}
 * <p/>
 * A corresponding XML schema can be generated from this class by running this
 * class as a Java application, see {@link #main(String[])}.
 * <p/>
 * For usage examples, see the tests in {@link ServiceDescriptionTest}.
 * @see ServiceDescriptionTest
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>, <a
 *         href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
@XmlRootElement(namespace = PlanetsServices.SERVICES_NS)
@XmlType(namespace = PlanetsServices.SERVICES_NS)
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class ServiceDescription {


    /**
     * A brief name by which this service is known.
     */
    @XmlElement(name = "title", namespace = PlanetsServices.TERMS_NS, required = true)
    String name;

    /**
     * The name of the concrete implementation class.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS, required = true)
    String classname;

    /**
     * The type of the service, which is the fully qualified name of the service
     * interface.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS, required = true)
    String type;

    /**
     * The endpoint of the service.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS)
    URL endpoint;

    /**
     * Declared Parameters: [name, type, value (default)]*n.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS)
    List<Parameter> parameters;

    /**
     * The link to the Tool registry.
     */
    @XmlElement(namespace = PlanetsServices.TOOLS_NS, required = true)
    Tool tool;

    /**
     * Human readable description of the service. Allow to be HTML, using a
     * <![CDATA[ <b>Hi</b> ]]>
     */
    @XmlElement(namespace = PlanetsServices.TERMS_NS)
    String description;

    /**
     * Wrapper version.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS)
    String version;

    /**
     * Identifier - A unique identifier for this service. "We need a unique id
     * for every service; Andrew Lindley is using a MD5 hash to identify a
     * service. This is a brilliant idea. I would say this field summarizes Name
     * of class impl service, Version of service and ID of Tool (URI) or makes
     * them unnecessary."
     */
    @XmlElement(namespace = PlanetsServices.TERMS_NS)
    String identifier;

    /**
     * Who wrote the wrapper. Preferred form would be a URI or a full email
     * address, like: "Full Name <fullname@server.com>".
     */
    @XmlElement(name = "creator", namespace = PlanetsServices.TERMS_NS, required = true)
    String author;

    /**
     * The organisation that is publishing this service endpoint.
     */
    @XmlElement(name = "publisher", namespace = PlanetsServices.TERMS_NS)
    String serviceProvider;

    // FIXME Add service status....???

    /**
     * Installation instructions. Properties to be set, or s/w to be installed.
     * Allow to be HTML, using non-parsed embedding, like this: <![CDATA[
     * <b>Hi</b> ]]>. JAXB should handle this.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS)
    String instructions;

    /**
     * Link to further information about this service wrapper.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS)
    URI furtherInfo;

    /**
     * A link to a web-browsable logo for this service. Used when presenting the
     * service to the user.
     */
    @XmlElement(namespace = PlanetsServices.SERVICES_NS)
    URI logo;

    /**
     * Services may specify what types they can take as inputs. [input]*n This
     * is particularly useful for Validate and Characterise.
     */
    @XmlElement(name = "inputFormat", required = false, namespace = PlanetsServices.SERVICES_NS)
    List<URI> inputFormats;

    /**
     * Name-value pairs for service properties. For characterisation services,
     * this should list all the digital object properties that the service can
     * deal with.
     */
    @XmlElement(name = "property", required = false, namespace = PlanetsServices.SERVICES_NS)
    List<Property> properties;

    /**
     * If this service performs migrations, they can be listed herein: Migration
     * Matrix: [input, output]*n.
     */
    @XmlElement(name = "migrationPath", required = false, namespace = PlanetsServices.SERVICES_NS)
    List<MigrationPath> paths;

    /**
     * @param name The name of the service description to build
     * @param type The type of service description to build
     * @return The builder, to alter the created service description; call
     *         build() on the builder to create the actual service description
     */
    public static ServiceDescription.Builder create(final String name,
            final String type) {
        return new ServiceDescription.Builder(name, type);
    }

    /**
     * @param serviceDescription The service description to copy
     * @return The builder, to alter the created service description; call
     *         build() on the builder to create the actual service description
     */
    public static ServiceDescription.Builder copy(
            final ServiceDescription serviceDescription) {
        return new ServiceDescription.Builder(serviceDescription);
    }

    /**
     * @param builder The builder to construct a service description from
     */
    private ServiceDescription(final Builder builder) {
        name = builder.name;
        type = builder.type;
        endpoint = builder.endpoint;
        paths = builder.paths;
        properties = builder.properties;
        inputFormats = builder.inputFormats;
        logo = builder.logo;
        furtherInfo = builder.furtherInfo;
        instructions = builder.instructions;
        serviceProvider = builder.serviceProvider;
        author = builder.author;
        identifier = builder.identifier;
        version = builder.version;
        description = builder.description;
        tool = builder.tool;
        parameters = builder.parameters;
        classname = builder.classname;
    }

    /**
     * Builder for ServiceDescription instances. Using a builder ensures
     * consistent object state during creation and models optional named
     * constructor parameters while allowing immutable objects.
     * @see eu.planets_project.services.datatypes.ServiceDescriptionTest
     */
    public static final class Builder {

        /** No-arg constructor for JAXB. API clients should not use this. */
        @SuppressWarnings("unused")
        private Builder() {}

        /* Required parameters: */
        private String name;
        private String type;
        /* Optional parameters, initialised to default values: */
        private List<MigrationPath> paths = new ArrayList<MigrationPath>();
        private List<Property> properties = new ArrayList<Property>();
        private List<URI> inputFormats = new ArrayList<URI>();
        private URI logo = null;
        private URL endpoint = null;
        private URI furtherInfo = null;
        private String instructions = null;
        private String serviceProvider = null;
        private String author = null;
        private String identifier = null;
        private String version = null;
        private String description = null;
        private Tool tool = null;
        private List<Parameter> parameters = null;
        private String classname = null;

        /** @return The instance created using this builder. */
        public ServiceDescription build() {
            return new ServiceDescription(this);
        }

        /**
         * @param name The name
         * @param type The type
         */
        public Builder(final String name, final String type) {
            this.name = name;
            this.type = type;
        }

        /**
         * @param xml The XML of a service description to use as a template for
         *        creating a new service description
         */
        public Builder(final String xml) {
            ServiceDescription d = ServiceDescription.of(xml);
            initialize(d);
        }

        /**
         * @param serviceDescription The service description to use as a
         *        template for creating a new service description
         */
        public Builder(final ServiceDescription serviceDescription) {
            initialize(serviceDescription);
        }

        /**
         * @param serviceDescription The description to use as a template for
         *        creating a new description
         */
        private void initialize(final ServiceDescription serviceDescription) {
	    if( serviceDescription == null ) return;
            name = serviceDescription.name;
            type = serviceDescription.type;
            endpoint = serviceDescription.endpoint;
            paths = serviceDescription.paths;
            properties = serviceDescription.properties;
            inputFormats = serviceDescription.inputFormats;
            logo = serviceDescription.logo;
            furtherInfo = serviceDescription.furtherInfo;
            instructions = serviceDescription.instructions;
            serviceProvider = serviceDescription.serviceProvider;
            author = serviceDescription.author;
            identifier = serviceDescription.identifier;
            version = serviceDescription.version;
            description = serviceDescription.description;
            tool = serviceDescription.tool;
            parameters = serviceDescription.parameters;
            classname = serviceDescription.classname;
        }

        /**
         * @param name The service name
         * @return The builder, for cascaded calls
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @param type The service type, i.e. the interface implemented
         * @return The builder, for cascaded calls
         */
        public Builder type(final String type) {
            this.type = type;
            return this;
        }

        /**
         * @param endpoint The endpoint for this service
         * @return The builder, for cascaded calls
         */
        public Builder endpoint(final URL endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * @param paths The migration paths supported by the service
         * @return The builder, for cascaded calls
         */
        public Builder paths(final MigrationPath... paths) {
            this.paths = new ArrayList<MigrationPath>(Arrays.asList(paths));
            return this;
        }

        /**
         * @param properties Properties for the service
         * @return The builder, for cascaded calls
         */
        public Builder properties(final Property... properties) {
            this.properties = new ArrayList<Property>(Arrays.asList(properties));
            return this;
        }

        /**
         * @param inputFormats The input formats supported by the service
         * @return The builder, for cascaded calls
         */
        public Builder inputFormats(final URI... inputFormats) {
            this.inputFormats = new ArrayList<URI>(Arrays.asList(inputFormats));
            return this;
        }

        /**
         * @param logo The logo
         * @return The builder, for cascaded calls
         */
        public Builder logo(final URI logo) {
            this.logo = logo;
            return this;
        }

        /**
         * @param furtherInfo Further infor on the service
         * @return The builder, for cascaded calls
         */
        public Builder furtherInfo(final URI furtherInfo) {
            this.furtherInfo = furtherInfo;
            return this;
        }

        /**
         * @param instructions The service instructions
         * @return The builder, for cascaded calls
         */
        public Builder instructions(final String instructions) {
            this.instructions = instructions;
            return this;
        }

        /**
         * @param serviceProvider The providing organization
         * @return The builder, for cascaded calls
         */
        public Builder serviceProvider(final String serviceProvider) {
            this.serviceProvider = serviceProvider;
            return this;
        }

        /**
         * @param author The service author
         * @return The builder, for cascaded calls
         */
        public Builder author(final String author) {
            this.author = author;
            return this;
        }

        /**
         * @param identifier An identifier for the service
         * @return The builder, for cascaded calls
         */
        public Builder identifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * @param version The service version
         * @return The builder, for cascaded calls
         */
        public Builder version(final String version) {
            this.version = version;
            return this;
        }

        /**
         * @param description A description of the service
         * @return The builder, for cascaded calls
         */
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @param classname The name of the class implementing the service
         * @return The builder, for cascaded calls
         */
        public Builder classname(final String classname) {
            this.classname = classname;
            return this;
        }

        /**
         * @param parameters The service parameters
         * @return The builder, for cascaded calls
         */
        public Builder parameters(final List<Parameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        /**
         * @param tool The tool the service uses
         * @return The builder, for cascaded calls
         */
        public Builder tool(final Tool tool) {
            this.tool = tool;
            return this;
        }
        
    }

    /** For JAXB. */
    private ServiceDescription() {
        super();
    }

    /**
     * @return the name
     */
    @Queryable
    public String getName() {
        return name;
    }

    /**
     * @return the classname
     */
    @Queryable
    public String getClassname() {
        return classname;
    }

    /**
     * @return the type
     */
    @Queryable
    public String getType() {
        return type;
    }

    /**
     * @return the endpoint
     */
    @Queryable
    public URL getEndpoint() {
        return endpoint;
    }

    /**
     * @return a copy of the parameters
     */
    @Queryable
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @return the tool
     */
    @Queryable
    public Tool getTool() {
        return tool;
    }

    /**
     * @return the description
     */
    @Queryable
    public String getDescription() {
        return description;
    }

    /**
     * @return the version
     */
    @Queryable
    public String getVersion() {
        return version;
    }

    /**
     * @return the author
     */
    @Queryable
    public String getAuthor() {
        return author;
    }

    /**
     * @return the serviceProvider
     */
    @Queryable
    public String getServiceProvider() {
        return serviceProvider;
    }

    /**
     * @return the instructions
     */
    @Queryable
    public String getInstructions() {
        return instructions;
    }

    /**
     * @return the furtherInfo
     */
    @Queryable
    public URI getFurtherInfo() {
        return furtherInfo;
    }

    /**
     * @return the logo
     */
    @Queryable
    public URI getLogo() {
        return logo;
    }

    /**
     * @return the identifier
     */
    @Queryable
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the paths (unmodifiable)
     */
    @Queryable
    public List<MigrationPath> getPaths() {
        return paths == null ? new ArrayList<MigrationPath>() : Collections
                .unmodifiableList(paths);
    }

    /**
     * @return the inputFormats (unmodifiable)
     */
    @Queryable
    public List<URI> getInputFormats() {
        return inputFormats == null ? new ArrayList<URI>() : Collections
                .unmodifiableList(inputFormats);
    }

    /**
     * @return the properties (unmodifiable)
     */
    @Queryable
    public List<Property> getProperties() {
        return properties == null ? new ArrayList<Property>() : Collections
                .unmodifiableList(properties);
    }

    /*
     * Proposed hashing and equality methods, giving the identifier preference
     * if it exists, else using the serialized XML form.
     */

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (identifier != null) {
            return identifier.hashCode();
        } else {
            return toXml().hashCode();
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ServiceDescription)) {
            return false;
        }
        ServiceDescription other = (ServiceDescription) obj;
        if (identifier != null) {
            return identifier.equals(other.identifier);
        } else {
            return toXml().equals(other.toXml());
        }
    }

    /**
     * @param xml The XML representation of a service description (as created
     *        from calling toXml)
     * @return A digital object instance created from the given XML
     */
    public static ServiceDescription of(final String xml) {
        try {
            /* Unmarshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(ServiceDescription.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(xml));
            ServiceDescription unmarshalled = (ServiceDescription) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return An XML representation of this service description (can be used to
     *         instantiate an object using the static factory method)
     */
    public String toXml() {
        return toXml(false);
    }

    /**
     * @return A formatted (pretty-printed) XML representation of this service
     *         description
     */
    public String toXmlFormatted() {
        return toXml(true);
    }

    private String toXml(boolean formatted) {
        try {
            /* Marshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(ServiceDescription.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.setProperty("jaxb.formatted.output", formatted);
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
    @Override
    public String toString() {
        return this.name + " : " + this.type + " : " + this.getDescription();
    }

    /***/
    private static java.io.File baseDir = new java.io.File(
            "IF/common/src/resources");
    /***/
    private static String schemaFileName = "service_description.xsd";

    /** Resolver for schema generation. */
    static class Resolver extends SchemaOutputResolver {
        /**
         * {@inheritDoc}
         * @see javax.xml.bind.SchemaOutputResolver#createOutput(java.lang.String,
         *      java.lang.String)
         */
        public Result createOutput(final String namespaceUri,
                final String suggestedFileName) throws IOException {
            return new StreamResult(new java.io.File(baseDir, schemaFileName
                    .split("\\.")[0]
                    + "_" + suggestedFileName));
        }
    }

    /**
     * Generates the XML schema for this class.
     * @param args Ignored
     */
    public static void main(final String[] args) {
        try {
            Class<ServiceDescription> clazz = ServiceDescription.class;
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
