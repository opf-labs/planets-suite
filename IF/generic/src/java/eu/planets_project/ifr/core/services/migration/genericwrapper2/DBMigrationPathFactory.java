package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationPathConfigException;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameter.Builder;

/**
 * Document based factory for construction and initialisation of
 * <code>MigrationPaths</code> objects based on a configuration described in a
 * <code>Document<code>.
 *
 * TODO: The documentation of this class still needs some tender love.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class DBMigrationPathFactory implements MigrationPathFactory {

    private static final String VALUE_ATTRIBUTE = "value";

    private static final String COMPATIBLE_CONFIGURATION_VERSION = "1.0";

    private final XPathFactory xPathFactory;

    private Document currentPathConfiguration;

    /**
     * Create a <code>DBMigrationPathFactory</code> which can produce
     * <code>MigrationPath</code> objects based on the <code>Document</code>
     * specified by <code>pathConfiguration</code> containing the configuration
     * of the migration paths that can be produced.
     * 
     * @param pathConfiguration
     *            <code>Document</code> containing the description of all the
     *            migration paths that can be produced.
     */
    public DBMigrationPathFactory(Document pathConfiguration) {
	xPathFactory = XPathFactory.newInstance();
	currentPathConfiguration = pathConfiguration;
    }

    // TODO: We should create a schema for the configuration file and refer to
    // it in this javadoc. Also, this factory should check the specified config
    // file against the schema. The config file is currently not validated.
    /**
     * Create a <code>CliMigrationPaths</code> object containing all the
     * migration paths described by the <code>pathConfiguration</code> document.
     * 
     * @return A <code>CliMigrationPaths</code> object containing all the paths
     *         configured in the configuration document specified.
     * @throws MigrationPathConfigException
     *             if the contents of <code>pathConfiguration</code> is invalid.
     */
    public MigrationPaths getAllMigrationPaths()
	    throws MigrationPathConfigException {

	// First, make sure that the version of the configuration document is
	// compatible with this factory.
	verifyConfigurationVersion(currentPathConfiguration);

	MigrationPaths migrationPaths = new MigrationPaths();

	try {
	    final XPath pathsXPath = xPathFactory.newXPath();
	    final NodeList pathsNode = (NodeList) pathsXPath.evaluate(
		    "//serviceWrapping/paths/path", currentPathConfiguration,
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
	final List<URI> destinationFormatURIs = getURIList(pathNode,
		"outputformat");

	if (destinationFormatURIs.size() > 1) {
	    throw new MigrationPathConfigException(
		    "The 'outputformat' element of a 'path' element must contain exactly one 'URI' element and not "
			    + destinationFormatURIs.size() + " 'URI' elements.");
	}

	final URI destinationFormatURI = destinationFormatURIs.get(0);

	// Get command line and command line parameters
	final CommandLine commandLine = getCommandLine(pathNode, "commandline");

	// Get temp files
	final Map<String, String> tempFileDeclarations = getTempFileDeclarations(
		pathNode, "tempfiles");

	// Get tool input information
	final ToolIOProfile toolInputProfile = getToolIOProfile(pathNode,
		"toolinput");

	// Get tool output information
	final ToolIOProfile toolOutputProfile = getToolIOProfile(pathNode,
		"tooloutput");

	// Get tool parameters
	final Collection<Parameter> toolParameters = getToolParameters(
		pathNode, "toolparameters");

	// Get tool presets
	ToolPresets toolPresets = getToolPresets(pathNode, "toolpresets");

	// for each input format {create a path element}.

	final List<MigrationPath> paths = new ArrayList<MigrationPath>();
	for (URI sourceFormatURI : sourceFormatURIs) {
	    MigrationPathImpl newPath = new MigrationPathImpl();

	    newPath.setSourceFormat(sourceFormatURI);
	    newPath.setDestinationFormat(destinationFormatURI);
	    newPath.setCommandLine(commandLine);
	    newPath.setTempFilesDeclarations(tempFileDeclarations);
	    newPath.setToolParameters(toolParameters);
	    newPath.setToolPresets(toolPresets);
	    newPath.setToolInputProfile(toolInputProfile);
	    newPath.setToolOutputProfile(toolOutputProfile);

	    paths.add(newPath);
	}
	return paths;
    }

    private ToolIOProfile getToolIOProfile(Node pathNode,
	    String toolIOProfileElementName)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	final ToolIOProfileImpl toolIOProfile = new ToolIOProfileImpl();

	try {
	    final Node pipedNode = (Node) pathsXPath.evaluate(
		    toolIOProfileElementName + "/" + "piped", pathNode,
		    XPathConstants.NODE);

	    if (pipedNode != null) {
		toolIOProfile.setUsePipedIO(true);
	    }

	    Map<String, String> tempFileMapping = getTempFileDeclarations(
		    pathNode, toolIOProfileElementName);

	    if (!tempFileMapping.isEmpty() && toolIOProfile.usePipedIO()) {
		throw new MigrationPathConfigException(
			"Both piped IO and temporary file is specified in the tool IO profile. Only one type may be specified in the '"
				+ toolIOProfileElementName
				+ "' element of the '"
				+ pathNode.getNodeName()
				+ "' element.");
	    }

	    if (tempFileMapping.isEmpty() && !toolIOProfile.usePipedIO()) {
		throw new MigrationPathConfigException(
			"Either piped IO or a temporary file must be specified in the '"
				+ toolIOProfileElementName
				+ "' element of the '" + pathNode.getNodeName()
				+ "' element.");
	    }

	    if (tempFileMapping.size() > 1) {
		throw new MigrationPathConfigException(
			"Only one temporary file may be specified in the tool IO profile. "
				+ tempFileMapping.size()
				+ " were specified in the '"
				+ toolIOProfileElementName
				+ "' element of the '" + pathNode.getNodeName()
				+ "' element.");
	    }

	    if (!toolIOProfile.usePipedIO()) {
		final String tempFileLabel = tempFileMapping.keySet()
			.iterator().next();
		toolIOProfile.setCommandLineFileLabel(tempFileLabel);
		toolIOProfile.setDesiredTempFileName(tempFileMapping
			.get(tempFileLabel));
	    }

	    return toolIOProfile;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading tool IO profile information from the '"
			    + toolIOProfileElementName + "' element in the '"
			    + pathNode.getNodeName() + "' element.", xpee);
	}
    }

    private String getDefaultAttributeValue(Node pathNode,
	    String nameOfElementWithDefaultAttribute)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();

	try {
	    final Node elementWithDefaultAttribute = (Node) pathsXPath
		    .evaluate(nameOfElementWithDefaultAttribute, pathNode,
			    XPathConstants.NODE);

	    if (elementWithDefaultAttribute == null) {
		throw new MigrationPathConfigException("No '"
			+ nameOfElementWithDefaultAttribute
			+ "' element declared in node: "
			+ pathNode.getNodeName());
	    }

	    final String defaultAttributeValue = getAttributeValue(
		    elementWithDefaultAttribute, "default");

	    if (defaultAttributeValue.length() == 0) {
		throw new MigrationPathConfigException(
			"Empty \"default\" attribute declared in node: "
				+ elementWithDefaultAttribute.getNodeName());
	    }

	    return defaultAttributeValue;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading the 'default' attribute of the '"
			    + nameOfElementWithDefaultAttribute
			    + "' element in the '" + pathNode.getNodeName()
			    + "' element.", xpee);
	}
    }

    private ToolPresets getToolPresets(Node pathNode, String presetsElementName)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();

	try {
	    final NodeList presetNodes = (NodeList) pathsXPath.evaluate(
		    presetsElementName + "/" + "preset", pathNode,
		    XPathConstants.NODESET);

	    ToolPresets toolPresets = null;
	    if (presetNodes.getLength() > 0) {
		final Collection<Preset> presets = new ArrayList<Preset>();
		for (int presetIndex = 0; presetIndex < presetNodes.getLength(); presetIndex++) {

		    final Node presetNode = presetNodes.item(presetIndex);

		    final String presetName = getAttributeValue(presetNode,
			    "name");
		    if (presetName.length() == 0) {
			throw new MigrationPathConfigException(
				"Empty \"name\" attribute declared in node: "
					+ presetNode.getNodeName());
		    }

		    final String defaultPresetName = getAttributeValue(
			    presetNode, "default");

		    final Node descriptionNode = (Node) pathsXPath.evaluate(
			    "description", presetNode, XPathConstants.NODE);

		    final String description = descriptionNode.getTextContent();

		    final Collection<PresetSetting> presetSettings = getPresetSettings(presetNode);

		    final Preset newPreset = new Preset(presetName,
			    presetSettings, defaultPresetName);
		    newPreset.setDescription(description);

		    presets.add(newPreset);
		}

		toolPresets = new ToolPresets();
		toolPresets.setToolPresets(presets);

		final String defaultPresetName = getDefaultAttributeValue(
			pathNode, presetsElementName);
		toolPresets.setDefaultPresetName(defaultPresetName);
	    }
	    return toolPresets;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading tool preset elements from the '"
			    + presetsElementName + "' element in the '"
			    + pathNode.getNodeName() + "' element.", xpee);
	}
    }

    private Collection<PresetSetting> getPresetSettings(Node presetNode)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	Collection<PresetSetting> presetSettings = new ArrayList<PresetSetting>();

	try {
	    final NodeList presetSettingNodes = (NodeList) pathsXPath.evaluate(
		    "settings", presetNode, XPathConstants.NODESET);

	    for (int settingsIndex = 0; settingsIndex < presetSettingNodes
		    .getLength(); settingsIndex++) {

		final Node settingsNode = presetSettingNodes
			.item(settingsIndex);

		final String settingsName = getAttributeValue(settingsNode,
			"name");

		if (settingsName.length() == 0) {
		    throw new MigrationPathConfigException(
			    "Empty \"name\" attribute declared in node: "
				    + settingsNode.getNodeName());
		}

		Collection<Parameter> parameters = getToolParameters(
			presetNode, "settings");

		final Node descriptionNode = (Node) pathsXPath.evaluate(
			"description", settingsNode, XPathConstants.NODE);

		final String description = descriptionNode.getTextContent();

		final PresetSetting presetSetting = new PresetSetting(
			settingsName, parameters);
		presetSetting.setDescription(description);
		presetSettings.add(presetSetting);
	    }

	    return presetSettings;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading preset settings elements from the '"
			    + presetNode.getNodeName() + "' element.", xpee);
	}

    }

    private Collection<Parameter> getToolParameters(Node pathNode,
	    String nameOfElementContainingParameters)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	Collection<Parameter> toolParameters = new ArrayList<Parameter>();

	try {
	    final NodeList parameterNodes = (NodeList) pathsXPath.evaluate(
		    nameOfElementContainingParameters + "/" + "parameter",
		    pathNode, XPathConstants.NODESET);

	    for (int parameterIndex = 0; parameterIndex < parameterNodes
		    .getLength(); parameterIndex++) {

		final Node parameterNode = parameterNodes.item(parameterIndex);

		final String parameterName = getAttributeValue(parameterNode,
			"name");
		if (parameterName == null) {
		    throw new MigrationPathConfigException(
			    "No \"name\" attribute declared in node: "
				    + parameterNode.getNodeName());
		}

		final Node descriptionNode = (Node) pathsXPath.evaluate(
			"description", parameterNode, XPathConstants.NODE);

		String description = null;
		if (descriptionNode != null) {
		    description = descriptionNode.getTextContent();
		}

		final Builder parameterBuilder = new Builder(parameterName,
			null);
		parameterBuilder.description(description);

		toolParameters.add(parameterBuilder.build());
	    }

	    return toolParameters;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading tool parameter elements from the '"
			    + nameOfElementContainingParameters
			    + "' element in the '" + pathNode.getNodeName()
			    + "' element.", xpee);
	}
    }

    /**
     * Parse all declarations of temporary files from <code>tempFilesNode</code>
     * and add the information to <code>pathToConfigure</code>.
     * 
     * @param pathNode
     *            <code>Node</code> containing the current path node being
     *            processed.
     * @param tempFilesElementName
     *            Name of the element containing the temp. file elements.
     * 
     * @return a <code>Map</code> containing the labels of the declared temp.
     *         files and associated desired filename (if defined).
     * 
     * @throws MigrationPathConfigException
     *             if any errors were encountered while parsing the node
     *             specified <code>tempFilesElementName</code> in the
     *             <code>pathNode</code>.
     */
    private Map<String, String> getTempFileDeclarations(Node pathNode,
	    String tempFilesElementName) throws MigrationPathConfigException {

	final HashMap<String, String> tempFileMappings = new HashMap<String, String>();
	final XPath pathsXPath = xPathFactory.newXPath();

	try {
	    final NodeList tempFileNodes = (NodeList) pathsXPath.evaluate(
		    tempFilesElementName + "/tempfile", pathNode,
		    XPathConstants.NODESET);

	    for (int tempfileIndex = 0; tempfileIndex < tempFileNodes
		    .getLength(); tempfileIndex++) {

		final Node tempfileNode = tempFileNodes.item(tempfileIndex);

		final String tempFileLabel = getAttributeValue(tempfileNode,
			"label");
		if (tempFileLabel == null) {
		    throw new MigrationPathConfigException(
			    "The \"label\" attribute has no value. Declared in node: "
				    + tempfileNode.getNodeName());

		}

		String tempFileName = null;
		try {
		    tempFileName = getAttributeValue(tempfileNode, "name");
		} catch (MigrationPathConfigException mpce) {
		    // This can safely be to ignored. The name attribute is
		    // optional.
		}

		tempFileMappings.put(tempFileLabel, tempFileName);
	    }

	    return tempFileMappings;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading temp. file elements from the '"
			    + tempFilesElementName + "' element in the '"
			    + pathNode.getNodeName() + "' element.", xpee);
	}
    }

    /**
     * FIXME! Revisit documentation....
     * 
     * Get the command line fragments from the element named
     * <code>commandLineElementName</code>. The fragments are returned in a list
     * containing the command as the first element, followed by any command
     * parameter strings.
     * 
     * @param pathNode
     *            Document node containing a <code>&lt;path&gt;</code> element.
     * @param commandLineElementName
     *            XML tag name of the command line element.
     * @return <code>List</code> containing all command line fragments from the
     *         element specified by <code>commandLineElementName</code>.
     * @throws MigrationPathConfigException
     *             if the command and its parameters could not be extracted from
     *             the document node.
     */
    private CommandLine getCommandLine(Node pathNode,
	    String commandLineElementName) throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();

	try {
	    final Node commandNode = (Node) pathsXPath.evaluate(
		    commandLineElementName + "/command", pathNode,
		    XPathConstants.NODE);

	    final String command = commandNode.getTextContent().trim();

	    if (command.length() == 0) {
		throw new MigrationPathConfigException(
			"No command was specified in the '"
				+ commandLineElementName + "' element in the '"
				+ pathNode.getNodeName() + "' element.");
	    }

	    final NodeList parameterNodes = (NodeList) pathsXPath.evaluate(
		    commandLineElementName + "/commandparameters/parameter",
		    pathNode, XPathConstants.NODESET);

	    final ArrayList<String> commandLineParameters = new ArrayList<String>();
	    for (int parameterIndex = 0; parameterIndex < parameterNodes
		    .getLength(); parameterIndex++) {

		final Node parameterNode = parameterNodes.item(parameterIndex);
		commandLineParameters
			.add(parameterNode.getTextContent().trim());
	    }
	    
	    return new CommandLine(command, commandLineParameters);
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading command and parameters from the '"
			    + commandLineElementName + "' element in the '"
			    + pathNode.getNodeName() + "' element.", xpee);
	}
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
    private void verifyConfigurationVersion(Document pathConfiguration)
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
