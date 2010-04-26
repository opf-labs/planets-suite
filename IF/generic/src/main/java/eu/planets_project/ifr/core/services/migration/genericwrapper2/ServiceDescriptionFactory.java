package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.ConfigurationException;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.Property.Builder;
import eu.planets_project.services.migrate.Migrate;

/**
 * Factory for construction of
 * <code>{@link eu.planets_project.services.datatypes.ServiceDescription}</code>
 * instances from a generic wrapper configuration document.
 * 
 * @author Pelle Kofod &lt;pko@statsbiblioteket.dk&gt;
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class ServiceDescriptionFactory {

    @SuppressWarnings("unused")
	private Logger log = Logger.getLogger(ServiceDescriptionFactory.class
            .getName());

    /**
     * <code>XPathFactory</code> instance used through out the code of this
     * factory.
     */
    private final XPathFactory xPathFactory;

    /**
     * <code>Document</code> containing the XML configuration file for the
     * migration tool service using the generic wrapper framework.
     */
    private final Document configuration;

    /**
     * Canonical class name of the migration tool service class using the
     * generic wrapper framework.
     */
    private final String canonicalServiceName;

    /**
     * Identification of the organisation providing the service wrapped with the
     * generic wrapper framework.
     */
    private final String serviceProvider;

    /**
     * Create a factory which creates
     * <code>{@link eu.planets_project.services.datatypes.ServiceDescription}</code>
     * instances based on the in configuration formation provided by
     * <code>wrapperConfiguration</code>. The dynamic information
     * <code>canonicalServiceName</code> and <code>serviceProvider</code> is
     * also used for the construction of the <code>ServiceDescription</code>
     * instances, however, they must be provided by the concrete service which
     * applies the generic wrapper framework.
     * 
     * @param canonicalServiceName
     *            Canonical class name of the service utilising this factory
     *            instance.
     * @param serviceProvider
     *            Identifier for the organisation providing the service, i.e.
     *            the organisation that hosts the running service instance.
     * @param wrapperConfiguration
     *            A generic wrapper configuration document containing a service
     *            description element with the necessary information for
     *            creation of <code>ServiceDescription</code> instances.
     */
    ServiceDescriptionFactory(String canonicalServiceName,
            String serviceProvider, Document wrapperConfiguration) {

        this.xPathFactory = XPathFactory.newInstance();
        this.configuration = wrapperConfiguration;
        this.serviceProvider = serviceProvider;
        this.canonicalServiceName = canonicalServiceName;
    }

    /**
     * Get a new
     * <code>{@link eu.planets_project.services.datatypes.ServiceDescription}</code>
     * instance, created from the information provided at the construction time
     * of this factory.
     * 
     * @return The created <code>ServiceDescription</code> instance.
     * @throws ConfigurationException
     *             if any errors are encountered in the configuration file while
     *             parsing it.
     */
    ServiceDescription getServiceDescription() throws ConfigurationException {

        final XPath pathsXPath = this.xPathFactory.newXPath();
        try {
            final Node serviceDescriptionNode = (Node) pathsXPath.evaluate(
                    ConfigurationFileTagsV1.SERVICE_DESCRIPTION_ELEMENT_XPATH,
                    this.configuration, XPathConstants.NODE);

            final String title = getMandatoryElementText(
                    serviceDescriptionNode,
                    ConfigurationFileTagsV1.TITLE_ELEMENT);

            // Start the creation of a service description for a migration
            // service.
            ServiceDescription.Builder builder = new ServiceDescription.Builder(
                    title, Migrate.class.getCanonicalName());

            Property[] serviceProperties = getServiceProperties(serviceDescriptionNode);

            builder.author(getMandatoryElementText(serviceDescriptionNode,
                    ConfigurationFileTagsV1.CREATOR_ELEMENT));

            builder.classname(this.canonicalServiceName);

            builder.description(getOptionalElementText(serviceDescriptionNode,
                    ConfigurationFileTagsV1.DESCRIPTION_ELEMENT));

            final String serviceVersion = getOptionalElementText(
                    serviceDescriptionNode,
                    ConfigurationFileTagsV1.VERSION_ELEMENT);

            final Tool toolDescription = getToolDescriptionElement(serviceDescriptionNode);

            // Get the migration service identifier or create an identifier if
            // it has not been specified in the configuration document.
            String identifier = getOptionalElementText(serviceDescriptionNode,
                    ConfigurationFileTagsV1.IDENTIFIER_ELEMENT);

            if (identifier == null || "".equals(identifier)) {
                
                // Construct an identifier in the form of a MD5 digest of
                // the tool ID, the canonical class name of the migration
                // service and its version number.
                try {
                    final MessageDigest identDigest = MessageDigest
                            .getInstance("MD5");
                    identDigest.update(this.canonicalServiceName.getBytes());

                    final String versionInfo = (serviceVersion != null) ? serviceVersion
                            : "";
                    identDigest.update(versionInfo.getBytes());

                    final URI toolIDURI = toolDescription.getIdentifier();
                    final String toolIdentifier = toolIDURI == null ? ""
                            : toolIDURI.toString();
                    identDigest.update(toolIdentifier.getBytes());

                    final BigInteger md5hash = new BigInteger(identDigest.digest());
                    identifier = md5hash.toString(16);

                } catch (NoSuchAlgorithmException nsae) {
                    // There is nothing we can do...
                    throw new RuntimeException(nsae);
                }
            }

            builder.identifier(identifier);
            builder.version(serviceVersion);
            builder.tool(toolDescription);

            builder.instructions(getOptionalElementText(serviceDescriptionNode,
                    ConfigurationFileTagsV1.INSTRUCTIONS_ELEMENT));

            builder.furtherInfo(getOptionalURIElement(serviceDescriptionNode,
                    ConfigurationFileTagsV1.FURTHER_INFO_ELEMENT));

            builder.logo(getOptionalURIElement(serviceDescriptionNode,
                    ConfigurationFileTagsV1.LOGO_ELEMENT));

            builder.serviceProvider(this.serviceProvider);

            final DBMigrationPathFactory migrationPathFactory = new DBMigrationPathFactory(
                    this.configuration);

            final MigrationPaths migrationPaths = migrationPathFactory
                    .getAllMigrationPaths();

            builder
                    .paths(MigrationPathConverter
                            .toPlanetsPaths(migrationPaths));

            builder.inputFormats(migrationPaths.getInputFormatURIs().toArray(
                    new URI[0]));

            builder.parameters(getUniqueParameters(migrationPaths));

            builder.properties(serviceProperties);

            return builder.build();

        } catch (XPathExpressionException xPathExpressionException) {
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element in the '%s' element.",
                    ConfigurationFileTagsV1.SERVICE_DESCRIPTION_ELEMENT_XPATH,
                    this.configuration.getNodeName()), xPathExpressionException);
        } catch (NullPointerException nullPointerException) {
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element in the '%s' element.",
                    ConfigurationFileTagsV1.SERVICE_DESCRIPTION_ELEMENT_XPATH,
                    this.configuration.getNodeName()), nullPointerException);
        }
    }

    /**
     * Return the text content of the optional element with the name specified
     * by <code>elementName</code> in <code>nodeWithOptionalElement</code> or
     * <code>null</code> if it is missing.
     * 
     * @param nodeWithOptionalElement
     *            <code>Node</code> that may contain an optional (child) element
     *            with the name specified by <code>elementName</code>.
     * 
     * @param elementName
     *            name of the optional element to parse.
     * 
     * @return a <code>String</code> instance containing the text content of the
     *         optional element or <code>null</code> if it does not exist.
     */
    private String getOptionalElementText(Node nodeWithOptionalElement,
            String elementName) {

        final XPath pathsXPath = this.xPathFactory.newXPath();
        try {

            final Node elementNode = (Node) pathsXPath.evaluate(elementName,
                    nodeWithOptionalElement, XPathConstants.NODE);

            return elementNode.getTextContent().trim();

        } catch (NullPointerException npe) {
            // The version is optional, thus ignore exceptions.
        } catch (XPathExpressionException xpee) {
            // The version is optional, thus ignore exceptions.
        }

        return null;
    }

    /**
     * Return the text content of the mandatory element with the name specified
     * by <code>elementName</code> element of
     * <code>nodeWithMandatoryElement</code>. An exception will be throw if the
     * element is missing or if any other problems are encountered while parsing
     * the element. or <code>null</code> if it is missing.
     * 
     * @param nodeWithMandatoryElement
     *            <code>Node</code> that must contain a mandatory (child)
     *            element with the name specified by <code>elementName</code>.
     * 
     * @param elementName
     *            name of the mandatory element to parse.
     * 
     * @return a <code>String</code> instance containing the text content of the
     *         mandatory element.
     * @throws ConfigurationException
     *             if any problems are encountered while retriving the text
     *             contents of the mandatory element.
     */
    private String getMandatoryElementText(Node nodeWithMandatoryElement,
            String elementName) throws ConfigurationException {

        final XPath pathsXPath = this.xPathFactory.newXPath();
        try {

            final Node elementNode = (Node) pathsXPath.evaluate(elementName,
                    nodeWithMandatoryElement, XPathConstants.NODE);

            return elementNode.getTextContent().trim();

        } catch (Exception exception) {
            // This is a mandatory element, thus no exceptions are tolerated.
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element in the '%s' element.",
                    elementName, nodeWithMandatoryElement.getNodeName()),
                    exception);
        }
    }

    /**
     * Return a <code>URI</code> created from the text content of the optional
     * element with the name specified by <code>elementName</code> in the
     * <code>nodeWithOptionalElement</code> or <code>null</code> if it is not
     * found.
     * 
     * @param nodeWithOptionalURIElement
     *            <code>Node</code> that may contain a (child) element with the
     *            name specified by <code>elementName</code> .
     * @param elementName
     *            name of the optional element to parse.
     * @return a <code>URI</code> instance created from the text content of the
     *         optional element or <code>null</code> if the element was not
     *         found.
     * @throws ConfigurationException
     *             if there are problems parsing and creating a the
     *             <code>URI</code> instance.
     */
    private URI getOptionalURIElement(Node nodeWithOptionalURIElement,
            String elementName) throws ConfigurationException {

        final XPath pathsXPath = this.xPathFactory.newXPath();

        try {
            final Node uriElementNode = (Node) pathsXPath.evaluate(elementName,
                    nodeWithOptionalURIElement, XPathConstants.NODE);

            final String uriString = uriElementNode.getTextContent().trim();
            if (uriString != null) {
                return new URI(uriString);
            }
        } catch (XPathExpressionException xpee) {
            // The URI element is optional, thus ignore exceptions.
        } catch (NullPointerException npe) {
            // The URI element is optional, thus ignore exceptions.
        } catch (URISyntaxException uriSyntalException) {
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element and creating a URI, in "
                            + "the '%s' element.", elementName,
                    nodeWithOptionalURIElement.getNodeName()),
                    uriSyntalException);
        }

        return null;
    }

    /**
     * Return a <code>URL</code> created from the text content of the optional
     * element with the name specified by <code>elementName</code> in the
     * <code>nodeWithOptionalElement</code> or <code>null</code> if it is not
     * found.
     * 
     * @param nodeWithOptionalURLElement
     *            <code>Node</code> that may contain a (child) element with the
     *            name specified by <code>elementName</code> .
     * @param elementName
     *            name of the optional element to parse.
     * @return a <code>URL</code> instance created from the text content of the
     *         optional element or <code>null</code> if the element was not
     *         found.
     * @throws ConfigurationException
     *             if there are problems parsing and creating a the
     *             <code>URL</code> instance.
     */
    private URL getOptionalURLElement(Node nodeWithOptionalURLElement,
            String elementName) throws ConfigurationException {

        final XPath pathsXPath = this.xPathFactory.newXPath();

        try {
            final Node urlElementNode = (Node) pathsXPath.evaluate(elementName,
                    nodeWithOptionalURLElement, XPathConstants.NODE);

            final String urlString = urlElementNode.getTextContent().trim();
            if (urlString != null) {
                return new URL(urlString);
            }
        } catch (XPathExpressionException xpee) {
            // The URI element is optional, thus ignore exceptions.
        } catch (NullPointerException npe) {
            // The URI element is optional, thus ignore exceptions.
        } catch (MalformedURLException malformedURLException) {
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element and creating a URL, in "
                            + "the '%s' element.", elementName,
                    nodeWithOptionalURLElement.getNodeName()),
                    malformedURLException);
        }

        return null;
    }

    /**
     * Get all the properties declared in the service description element of the
     * configuration document.
     * 
     * @param serviceDescriptionNode
     *            The XML node containing the
     *            <code>&lt;serviceDescription&gt;</code> element.
     * @return an array of <code>Property</code> instances, one for each
     *         declaration in the configuration.
     * @throws ConfigurationException
     *             in case of parser errors or if mandatory information is
     *             missing.
     */
    private Property[] getServiceProperties(Node serviceDescriptionNode)
            throws ConfigurationException {

        Node propertyNode = null;
        try {
            final XPath pathsXPath = this.xPathFactory.newXPath();
            final NodeList propertyNodes = (NodeList) pathsXPath.evaluate(
                    ConfigurationFileTagsV1.PROPERTIES_PROPERTY_XPATH,
                    serviceDescriptionNode, XPathConstants.NODESET);

            ArrayList<Property> properties = new ArrayList<Property>();
            for (int propertyIdx = 0; propertyIdx < propertyNodes.getLength(); propertyIdx++) {
                propertyNode = propertyNodes.item(propertyIdx);

                final NamedNodeMap propertyAttributes = propertyNode
                        .getAttributes();

                // Get the values for the mandatory attributes and sub-nodes.
                final URI propertyID = new URI(propertyAttributes.getNamedItem(
                        ConfigurationFileTagsV1.ID_ATTRIBUTE).getNodeValue());
                Property.Builder propertyBuilder = new Property.Builder(
                        propertyID);
                propertyBuilder.name(propertyAttributes.getNamedItem(
                        ConfigurationFileTagsV1.NAME_ATTRIBUTE).getNodeValue());

                propertyBuilder = addValue(propertyBuilder, propertyNode);

                // Add values from optional attributes and sub-nodes.
                try {
                    propertyBuilder.type(propertyAttributes.getNamedItem(
                            ConfigurationFileTagsV1.TYPE_ATTRIBUTE)
                            .getNodeValue());
                } catch (NullPointerException nullPointerException) {
                    // Ignore. This attribute is optional.
                }

                propertyBuilder.description(getOptionalElementText(
                        propertyNode,
                        ConfigurationFileTagsV1.DESCRIPTION_ELEMENT));

                properties.add(propertyBuilder.build());
            }
            return properties.toArray(new Property[properties.size()]);
        } catch (Exception exception) {
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element of the configuration"
                            + " document.", serviceDescriptionNode
                            .getNodeName()), exception);
        }
    }

    /**
     * Add the text content of <code>&lt;value&gt;</code> in
     * <code>nodeWithValueElement</code> and the value of its optional
     * <code>&quot;unit&quot;</code> attribute to the
     * <code>propertyBuilder</code>.
     * 
     * @param propertyBuilder
     *            <code>Property.Builder</code> instance to store the retrieved
     *            data in.
     * @param nodeWithValueElement
     *            <code>Node</code> instance having a <code>&lt;value&gt;</code>
     *            sub-node.
     * @return the <code>Property.Builder</code> specified by
     *         <code>propertyBuilder</code> with the added data retrieved from
     *         <code>nodeWithValueElement</code>.
     * @throws XPathExpressionException
     *             if no <code>&lt;value&gt;</code> element is found in
     *             <code>nodeWithValueElement</code>.
     */
    private Builder addValue(Property.Builder propertyBuilder,
            Node nodeWithValueElement) throws XPathExpressionException {

        final XPath pathsXPath = this.xPathFactory.newXPath();
        final Node valueNode = (Node) pathsXPath.evaluate(
                ConfigurationFileTagsV1.VALUE_ELEMENT, nodeWithValueElement,
                XPathConstants.NODE);

        propertyBuilder.value(valueNode.getTextContent().trim());

        NamedNodeMap valueAttributes = valueNode.getAttributes();
        propertyBuilder.unit(valueAttributes.getNamedItem(
                ConfigurationFileTagsV1.UNIT_ATTRIBUTE).getNodeValue());

        return propertyBuilder;
    }

    /**
     * Collect all the unique parameters (unique by name) defined for the
     * migration paths specified by <code>migrationPaths</code> and return them
     * in a list.
     * <p/>
     * <p/>
     * The same parameter is likely to be defined for multiple paths, however,
     * if the configuration file for the generic wrapper has been properly
     * written, then all occurrences should have the same meaning, thus, only
     * the first occurrence of a parameter will be collected. For the same
     * reason, the returned parameters will not have any value to avoid
     * confusion, as the default value of a parameter may differ among the
     * various migration paths.
     * 
     * @param migrationPaths
     *            A <code>MigrationPaths</code> instances containing
     *            <code>MigrationPath</code> instances to collect parameters
     *            from.
     * @return <code>List</code> of unique parameters declared for the paths
     *         specified by <code>migrationPaths</code>. The value of all the
     *         parameters will be <code>null</code>.
     */
    private List<Parameter> getUniqueParameters(MigrationPaths migrationPaths) {

        final HashMap<String, Parameter> parameterMap = new HashMap<String, Parameter>();

        for (MigrationPath migrationPath : migrationPaths
                .getAllMigrationPaths()) {

            for (Parameter parameter : migrationPath.getToolParameters()) {

                // Collect the parameter and ignore multiple occurrences.
                // Parameters having a value will have the value stripped.
                final String parameterName = parameter.getName();
                if (!parameterMap.containsKey(parameterName)) {

                    Parameter.Builder parameterBuilder = new Parameter.Builder(
                            parameterName, null);
                    parameterMap.put(parameterName, parameterBuilder.build());
                }
            }

        }

        return new ArrayList<Parameter>(parameterMap.values());
    }

    /**
     * Initialise a <code>Tool</code> instance with the tool description
     * information found in &quot;tool&quot; element in the
     * <code>nodeWithToolDescription</code> node.
     * 
     * @param nodeWithToolDescription
     *            <code>Node</code> containing a tool description element.
     * @return A <code>Tool</code> instance created from the tool description
     *         (child) element provided by <code>nodeWithToolDescription</code>.
     * @throws ConfigurationException
     *             if any problems are encountered while parsing the tool
     *             description element.
     */
    private Tool getToolDescriptionElement(Node nodeWithToolDescription)
            throws ConfigurationException {

        final XPath pathsXPath = this.xPathFactory.newXPath();
        Node toolDescriptionNode;
        try {
            toolDescriptionNode = (Node) pathsXPath.evaluate(
                    ConfigurationFileTagsV1.TOOL_ELEMENT,
                    nodeWithToolDescription, XPathConstants.NODE);

            final String description = getOptionalElementText(
                    toolDescriptionNode,
                    ConfigurationFileTagsV1.DESCRIPTION_ELEMENT);

            final String version = getOptionalElementText(toolDescriptionNode,
                    ConfigurationFileTagsV1.VERSION_ELEMENT);

            final URI identifierURI = getOptionalURIElement(
                    toolDescriptionNode,
                    ConfigurationFileTagsV1.IDENTIFIER_ELEMENT);

            final String name = getOptionalElementText(toolDescriptionNode,
                    ConfigurationFileTagsV1.NAME_ELEMENT);

            final URL homePageURL = getOptionalURLElement(toolDescriptionNode,
                    ConfigurationFileTagsV1.HOME_PAGE_ELEMENT);

            final Tool toolDescription = new Tool(identifierURI, name, version,
                    description, homePageURL);

            return toolDescription;
        } catch (XPathExpressionException xPathExpressionException) {
            throw new ConfigurationException(String.format(
                    "Failed parsing the '%s' element in the element: %s",
                    ConfigurationFileTagsV1.TOOL_ELEMENT,
                    nodeWithToolDescription.getNodeName()),
                    xPathExpressionException);
        }
    }
}
