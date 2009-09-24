package eu.planets_project.ifr.core.services.migration.genericwrapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.MigrationPathConfigException;
import eu.planets_project.services.utils.PlanetsLogger;

/**
 * Expermiental factory for construction of <code>MigrationPaths</code> objects
 * <b>NOT TO BE USED!</c>. If the experiment turns out to be successful then
 * this implementation should replace the current
 * <code>MigrationPahtsFactory</code> implementation.
 * 
 * @author Thomas Skou Hansen <tsh@statsbiblioteket.dk>
 */
public class MurkyFactory {

    private PlanetsLogger log = PlanetsLogger.getLogger(MurkyFactory.class);

    private static final String VALUE_ATTRIBUTE = "value";

    private static final String COMPATIBLE_CONFIGURATION_VERSION = "1.0";

    private final XPathFactory xPathFactory;

    public MurkyFactory() {
        xPathFactory = XPathFactory.newInstance();

    }

    /**
     * Create a <code>CliMigrationPaths</code> object containing the migration
     * paths described by the <code>pathConfiguration</code> document.
     * 
     * @return A <code>CliMigrationPaths</code> object containing all the paths
     *         configured in the configuration document specified.
     * @throws MigrationPathConfigException
     *             if the contents of <code>pathConfiguration</code> is invalid.
     */
    public MigrationPaths getMigrationPaths(Document pathConfiguration)
            throws MigrationPathConfigException {

        // First, make sure that the version of the configuration document is
        // compatible with this factory.
        verifyConfigurationVersion(pathConfiguration);

        MigrationPaths migrationPaths = new MigrationPaths();

        try {
            final XPath pathsXPath = xPathFactory.newXPath();
            final NodeList pathsNode = (NodeList) pathsXPath.evaluate(
                    "//serviceWrapping/paths/path", pathConfiguration,
                    XPathConstants.NODESET);

            for (int nodeIndex = 0; nodeIndex < pathsNode.getLength(); nodeIndex++) {
                final Node currentPathNode = pathsNode.item(nodeIndex);

                migrationPaths
                        .addAll(createCliMigrationPathInstances(currentPathNode));
            }
            return migrationPaths;
        } catch (Exception exception) {
            throw new MigrationPathConfigException(
                    "Failed parsing migration path configuration document.",
                    exception);
        }
    }

    /**
     * Create a list containing a <code>CliMigrationPath</code> instance for
     * each entry in the <code>sourceFormatURI</code> list
     * 
     * @param pathTemplate
     *            <code>CliMigrationPath</code> instance to use as template for
     *            the created paths.
     * @param sourceFomatURIs
     *            List of <code>URI</code>s to use as source <code>URI</code>
     *            for the created <code>CliMigrationPath</code> instances.
     * @param destinationFormatURI
     *            <code>URI</code> to use as the destination <code>URI</code>
     *            for the created <code>CliMigrationPath</code> instances.
     * @return a list of <code>CliMigrationPath</code> instances containing an
     *         instance per entry in the <code>sourceFormatURI</code> list.
     * @throw MigrationPathConfigException if the migration paths could not be
     *        instantiated.
     */
    private List<MigrationPath> createCliMigrationPathInstances(Node pathNode)
            throws MigrationPathConfigException {

        // Get input formats
        final List<URI> sourceFormatURIs = getURIList(pathNode, "inputformats");

        // Get output format
        final URI destinationFormatURI = getURIList(pathNode, "outputformat")
                .get(0);

        // Get command line
        // Get temp files
        // Get tool parameters
        // Get tool presets

        // for each input format {create a path element}.

        // <path>
        //
        // <commandline>
        // <cmd>
        // /bin/sh
        // </cmd>
        // <cmd>
        // -c
        // </cmd>
        // <cmd>
        // ps2pdf12 #param1 #tempSource #tempDestination
        // </cmd>
        // </commandline>
        //
        //
        // <tempfiles>
        // <inputfile label="tempSource">
        // </inputfile>
        // <outputfile label="tempDestination">
        // </outputfile>
        // </tempfiles>
        //
        // <toolparameters>
        // <parameter name="param1">
        // <description>Command line parameters for the 'ps2pdf'
        // </description>
        // </parameter>
        //
        //
        // </toolparameters>
        // <toolpresets default="mode">
        // <preset name="mode" default="Normal">
        // <description>
        // </description>
        // <settings name="Silent">
        // <parameter name="param1"><![CDATA['-q']]>
        // </parameter>
        // <description>Silent Running</description>
        // </settings>
        // <settings name="Normal">
        // <parameter name="param1"><![CDATA[]]>
        // </parameter>
        // <description>Normal</description>
        // </settings>
        // </preset>
        // </toolpresets>
        // </path>

        final List<MigrationPath> paths = new ArrayList<MigrationPath>();
        for (URI sourceFormatURI : sourceFormatURIs) {
            MigrationPath newPath = new MigrationPath();

            newPath.setSourceFormat(sourceFormatURI);
            newPath.setDestinationFormat(destinationFormatURI);
            log.debug("Createing CliMigrationPath instance for the path: "
                    + sourceFormatURI + " -> " + destinationFormatURI);
            paths.add(newPath);
        }
        return paths;
    }

    /**
     * Get a list of <code>URI</code> instances from the list element specified
     * by <code>uriListElementName</code> in <code>pathNode</code>.
     * 
     * TODO: finish doc.
     * 
     * OLD DOC:
     * 
     * Create <code>URI</code> objects for all <code>&lt;uri&gt;</code> elements
     * in <code>uriListNode</code>.
     * 
     * @param uriListNode
     *            Node expected to contain a list of URI elements.
     * @return List containing an <code>URI</code> instance for each URI element
     *         found in <code>uriListNode</code>.
     * @throws MigrationPathConfigException
     *             if an error occurred while parsing the configuration
     *             described by <code>uriListNode</code>
     * 
     * 
     * 
     * 
     * @param pathNode
     * @param string
     * @return
     * @throws stuff
     */
    private List<URI> getURIList(Node pathNode, String uriListElementName)
            throws MigrationPathConfigException {

        final XPath pathsXPath = xPathFactory.newXPath();
        try {
            final NodeList uriNodes = (NodeList) pathsXPath.evaluate(
                    uriListElementName + "/uri", pathNode,
                    XPathConstants.NODESET);

            final List<URI> uriList = new ArrayList<URI>();

            for (int uriIndex = 0; uriIndex < uriNodes.getLength(); uriIndex++) {

                final Node uriNode = uriNodes.item(uriIndex);
                uriList
                        .add(new URI(
                                getAttributeValue(uriNode, VALUE_ATTRIBUTE)));
            }

            if (uriList.isEmpty()) {
                throw new MigrationPathConfigException("The element '"
                        + pathNode.getNodeName()
                        + "' has no URI elements in the '" + uriListElementName
                        + "' list.");
            }
            return uriList;
        } catch (Exception exception) {
            throw new MigrationPathConfigException(
                    "Failed reading URIs from the '" + uriListElementName
                            + "' URI list element in the element '"
                            + pathNode.getNodeName() + "'", exception);
        }
    }

    /**
     * Get the value of the attribute <code>attributeName</code> of the
     * <code>elementWithAttributes Node</code>.
     * 
     * @param elementWithAttributes
     *            a <code>Node</code> with attributes.
     * @return the value of the attribute <code>attributeName</code> as a
     *         <code>String</code> if it is defined and otherwise
     *         <code>null</code>
     * @throws MigrationPathConfigException
     *             if the attribute specified by <code>attributeName</code> was
     *             not found in <code>elementWithAttributes</code>.
     */
    private String getAttributeValue(Node elementWithAttributes,
            String attributeName) throws MigrationPathConfigException {

        final NamedNodeMap attributes = elementWithAttributes.getAttributes();

        final Node attributeNode = attributes.getNamedItem(attributeName);
        if (attributeNode == null) {
            throw new MigrationPathConfigException("The attribute '"
                    + attributeName + "' is not defined in element '"
                    + elementWithAttributes.getNodeName() + "'");
        }
        final String attributeValue = attributeNode.getNodeValue();
        return attributeValue;
    }

    /**
     * Verify that the version number of the configuration document
     * <code>pathConfiguration</code> is compatible with this factory. The
     * validation is a case-insensitive comparison between the constant
     * <code>COMPATIBLE_CONFIGURATION_VERSION</code>, of this factory, and the
     * value of the <code>version</code> attribute of the
     * <code>&lt;serviceWrapping&gt;</code> root element of the configuration
     * document.
     * 
     * @param pathConfiguration
     *            The configuration document to verify the version number of.
     * @throws MigrationPathConfigException
     *             if the version number of the configuration document is
     *             incompatible with this factory.
     */
    public void verifyConfigurationVersion(Document pathConfiguration)
            throws MigrationPathConfigException {

        String versionNumber = "";

        try {
            final XPath pathsXPath = xPathFactory.newXPath();

            final Node serviceWrappingNode = (Node) pathsXPath
                    .evaluate("//serviceWrapping", pathConfiguration,
                            XPathConstants.NODE);
            final Node versionNode = serviceWrappingNode.getAttributes()
                    .getNamedItem("version");

            versionNumber = versionNode.getNodeValue();

        } catch (NullPointerException npe) {
            throw new MigrationPathConfigException(
                    "Could not access the 'version' attribute of the 'serviceWrapping' element in the configuration document.",
                    npe);

        } catch (Exception exception) {
            throw new MigrationPathConfigException(
                    "Validation of the configuration file version number failed.",
                    exception);
        }

        if (!COMPATIBLE_CONFIGURATION_VERSION.toLowerCase().equals(
                versionNumber.toLowerCase())) {
            throw new MigrationPathConfigException(
                    "Invalid version number in configuration document '"
                            + versionNumber + "'. Expected version '"
                            + COMPATIBLE_CONFIGURATION_VERSION);
        }
    }
}
