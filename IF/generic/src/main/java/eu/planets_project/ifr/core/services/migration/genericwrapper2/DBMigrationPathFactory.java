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
 * <code>MigrationPaths</code> objects containing <code>MigrationPath</code>
 * instances created from a generic wrapper configuration <code>Document<code>.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class DBMigrationPathFactory implements MigrationPathFactory {

    /**
     * <code>XPathFactory</code> instance used through out the code of this
     * factory.
     */
    private final XPathFactory xPathFactory;

    /**
     * <code>Document</code> containing the XML configuration file for the
     * migration tool service using the generic wrapper framework.
     */
    private Document genericWrapperConfiguration;

    /**
     * Create a <code>DBMigrationPathFactory</code> which can produce
     * <code>MigrationPath</code> objects based on the migration path
     * descriptions in the <code>Document</code> specified by
     * <code>genericWrapperConfiguration</code>.
     * 
     * @param genericWrapperConfiguration
     *            a generic wrapper configuration <code>Document</code> which
     *            (among other things) contains descriptions of all the
     *            migration paths to produce.
     */
    DBMigrationPathFactory(Document genericWrapperConfiguration) {
	xPathFactory = XPathFactory.newInstance();
	this.genericWrapperConfiguration = genericWrapperConfiguration;
    }

    // TODO: We should create a schema for the configuration file and refer to
    // it in this javadoc. Also, this factory should check the specified config
    // file against the schema. The config file is currently not validated.
    /**
     * Create a <code>MigrationPaths</code> instance containing all the
     * migration paths described by the generic wrapper configuration document
     * provided at construction time of this factory.
     * 
     * @return A <code>MigrationPaths</code> instance containing all the
     *         migration paths produced by this factory.
     * @throws MigrationPathConfigException
     *             if any problems are encountered while parsing the generic
     *             wrapper configuration and producing
     *             <code>MigrationPath</code> instances.
     */
    public MigrationPaths getAllMigrationPaths()
	    throws MigrationPathConfigException {

	// First, make sure that the version of the configuration document is
	// compatible with this factory.
	verifyConfigurationVersion(genericWrapperConfiguration);

	final MigrationPaths migrationPaths = new MigrationPaths();

	try {
	    // Get the XML element containing the migration path configurations.
	    final XPath pathsXPath = xPathFactory.newXPath();
	    final NodeList pathNodes = (NodeList) pathsXPath.evaluate(
		    ConfigurationFileTagsV1.PATH_ELEMENT_XPATH,
		    genericWrapperConfiguration, XPathConstants.NODESET);

	    // Process each of the migration path configurations.
	    for (int nodeIndex = 0; nodeIndex < pathNodes.getLength(); nodeIndex++) {
		final Node currentPathNode = pathNodes.item(nodeIndex);

		// Collect all the migration paths constructed from the
		// configuration - one migration path configuration may describe
		// multiple migration paths.
		migrationPaths
			.addAll(createMigrationPathInstances(currentPathNode));
	    }
	    return migrationPaths;
	} catch (Exception exception) {
	    throw new MigrationPathConfigException(
		    "Failed parsing the migration path configurations in the "
			    + "configuration document.", exception);
	}
    }

    /**
     * Create a list containing a <code>MigrationPath</code> instance for each
     * format <code>URI</code> entry in the <code>&lt;inputformats&gt;</code>
     * element of the path configuration provided by <code>pathNode</code>.
     * 
     * @param pathNode
     *            a <code>Document Node</code> containing a
     *            <code>&lt;path&gt;</code> element from the configuration
     *            document.
     * @return a list of <code>MigrationPath</code> instances created from the
     *         path configuration.
     * @throw MigrationPathConfigException if any problems are encountered while
     *        parsing the configuration and creating the
     *        <code>MigrationPath</code> instances.
     */
    private List<MigrationPath> createMigrationPathInstances(Node pathNode)
	    throws MigrationPathConfigException {

	// Get the input formats
	final List<URI> inputFormatURIs = getURIList(pathNode,
		ConfigurationFileTagsV1.INPUT_FORMATS_ELEMENT);

	// Get the output format
	final List<URI> outputFormatURIs = getURIList(pathNode,
		ConfigurationFileTagsV1.OUTPUT_FORMAT_ELEMENT);

	if (outputFormatURIs.size() != 1) {
	    throw new MigrationPathConfigException(String.format(
		    "The '%s' element of a '%s' element must contain exactly "
			    + "one format URI element and not "
			    + outputFormatURIs.size() + " elements.",
		    ConfigurationFileTagsV1.OUTPUT_FORMAT_ELEMENT, pathNode
			    .getNodeName()));
	}

	final URI destinationFormatURI = outputFormatURIs.get(0);

	// Get command line and command line parameters
	final CommandLine commandLine = getCommandLine(pathNode,
		ConfigurationFileTagsV1.COMMAND_LINE_ELEMENT);

	// Get a map of temporary file names and their associated label name.
	final Map<String, String> tempFileDeclarations = getTempFileDeclarations(
		pathNode, ConfigurationFileTagsV1.TEMPFILES_ELEMENT);

	// Get tool input information
	final ToolIOProfile toolInputProfile = getToolIOProfile(pathNode,
		ConfigurationFileTagsV1.TOOLINPUT_ELEMENT);

	// Get tool output information
	final ToolIOProfile toolOutputProfile = getToolIOProfile(pathNode,
		ConfigurationFileTagsV1.TOOLOUTPUT_ELEMENT);

	// Get tool parameters
	final Collection<Parameter> toolParameters = getToolParameters(
		pathNode, ConfigurationFileTagsV1.TOOLPARAMETERS_ELEMENT);

	// Get tool presets
	ToolPresets toolPresets = getToolPresets(pathNode,
		ConfigurationFileTagsV1.TOOL_PRESETS_ELEMENT);

	// Create a MigrationPath instance for each input format URI.
	final List<MigrationPath> paths = new ArrayList<MigrationPath>();
	for (URI inputFormatURI : inputFormatURIs) {
	    MigrationPathImpl newPath = new MigrationPathImpl();

	    newPath.setInputFormat(inputFormatURI);
	    newPath.setOutputFormat(destinationFormatURI);
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

    /**
     * Create a <code>ToolIOProfile</code> instance from the information in the
     * XML element with the name specified by
     * <code>toolIOProfileElementName</code> in the <code>pathNode</code>
     * element.
     * 
     * @param pathNode
     *            an XML <code>Document Node</code> containing an element with
     *            the name specified by <code>toolIOProfileElementName</code>.
     * @param toolIOProfileElementName
     *            the name of the element to get the tool IO profile information
     *            from.
     * @return a <code>ToolIOProfile</code> instance created from the
     *         information of the XML element identified by
     *         <code>toolIOProfileElementName</code>.
     * @throws MigrationPathConfigException
     *             if any problems are encountered while parsing the XML element
     *             and creating the <code>ToolIOProfile</code> instance.
     */
    private ToolIOProfile getToolIOProfile(Node pathNode,
	    String toolIOProfileElementName)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	final ToolIOProfileImpl toolIOProfile = new ToolIOProfileImpl();

	try {
	    final Node pipedNode = (Node) pathsXPath.evaluate(
		    toolIOProfileElementName + "/"
			    + ConfigurationFileTagsV1.PIPED_ELEMENT, pathNode,
		    XPathConstants.NODE);

	    if (pipedNode != null) {
		toolIOProfile.setUsePipedIO(true);
	    }

	    final Map<String, String> tempFileMapping = getTempFileDeclarations(
		    pathNode, toolIOProfileElementName);

	    if (!tempFileMapping.isEmpty() && toolIOProfile.usePipedIO()) {
		throw new MigrationPathConfigException(
			"Both piped IO and temporary file is specified in the "
				+ "tool IO profile. Only one type may be "
				+ "specified in the '"
				+ toolIOProfileElementName
				+ "' element of the '" + pathNode.getNodeName()
				+ "' element.");
	    }

	    if (tempFileMapping.isEmpty() && !toolIOProfile.usePipedIO()) {
		throw new MigrationPathConfigException(
			"Either piped IO or a temporary file must be specified"
				+ " in the '" + toolIOProfileElementName
				+ "' element of the '" + pathNode.getNodeName()
				+ "' element.");
	    }

	    if (tempFileMapping.size() > 1) {
		throw new MigrationPathConfigException(
			"Only one temporary file may be specified in the tool"
				+ " IO profile. " + tempFileMapping.size()
				+ " were specified in the '"
				+ toolIOProfileElementName
				+ "' element of the '" + pathNode.getNodeName()
				+ "' element.");
	    }

	    if (!toolIOProfile.usePipedIO()) {
		// Based on the above code, we are sure that the temp. file map
		// contains exactly one element, thus this is OK.
		final String tempFileLabel = tempFileMapping.keySet()
			.iterator().next();
		toolIOProfile.setCommandLineFileLabel(tempFileLabel);
		toolIOProfile.setDesiredTempFileName(tempFileMapping
			.get(tempFileLabel));
	    }

	    return toolIOProfile;
	} catch (XPathExpressionException xPathExpressionException) {
	    throw new MigrationPathConfigException(
		    "Failed reading tool IO profile information from the '"
			    + toolIOProfileElementName + "' element in the '"
			    + pathNode.getNodeName() + "' element.",
		    xPathExpressionException);
	}
    }

    // TODO: Doc - MARK
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
		    elementWithDefaultAttribute,
		    ConfigurationFileTagsV1.DEFAULT_ATTRIBUTE);

	    if (defaultAttributeValue.length() == 0) {
		throw new MigrationPathConfigException(String.format(
			"Empty '%s' attribute declared in node: ",
			ConfigurationFileTagsV1.DEFAULT_ATTRIBUTE)
			+ elementWithDefaultAttribute.getNodeName());
	    }

	    return defaultAttributeValue;
	} catch (XPathExpressionException xPathExpressionException) {
	    throw new MigrationPathConfigException(String.format(
		    "Failed reading the '%s' attribute of the '",
		    ConfigurationFileTagsV1.DEFAULT_ATTRIBUTE)
		    + nameOfElementWithDefaultAttribute
		    + "' element in the '"
		    + pathNode.getNodeName() + "' element.",
		    xPathExpressionException);
	}
    }

    // TODO: Javadoc!
    private ToolPresets getToolPresets(Node pathNode, String presetsElementName)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();

	try {
	    final NodeList presetNodes = (NodeList) pathsXPath.evaluate(
		    presetsElementName + "/"
			    + ConfigurationFileTagsV1.PRESET_ELEMENT, pathNode,
		    XPathConstants.NODESET);

	    ToolPresets toolPresets = null;
	    final Collection<Preset> presets = new ArrayList<Preset>();
	    for (int presetIndex = 0; presetIndex < presetNodes.getLength(); presetIndex++) {

		final Node presetNode = presetNodes.item(presetIndex);

		final String presetName = getAttributeValue(presetNode,
			ConfigurationFileTagsV1.NAME_ATTRIBUTE);
		if (presetName.length() == 0) {
		    throw new MigrationPathConfigException(
			    "Empty \"name\" attribute declared in node: "
				    + presetNode.getNodeName());
		}

		final String defaultPresetName = getAttributeValue(presetNode,
			ConfigurationFileTagsV1.DEFAULT_ATTRIBUTE);

		final Node descriptionNode = (Node) pathsXPath.evaluate(
			ConfigurationFileTagsV1.DESCRIPTION_ELEMENT,
			presetNode, XPathConstants.NODE);

		final String description = descriptionNode.getTextContent();

		final Collection<PresetSetting> presetSettings = getPresetSettings(presetNode);

		final Preset newPreset = new Preset(presetName, presetSettings,
			defaultPresetName);
		newPreset.setDescription(description);

		presets.add(newPreset);
	    }

	    toolPresets = new ToolPresets();
	    toolPresets.setToolPresets(presets);

	    String defaultPresetName = null;
	    if (presets.size() > 0) {
		defaultPresetName = getDefaultAttributeValue(pathNode,
			presetsElementName);
	    }
	    toolPresets.setDefaultPresetName(defaultPresetName);
	    return toolPresets;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading tool preset elements from the '"
			    + presetsElementName + "' element in the '"
			    + pathNode.getNodeName() + "' element.", xpee);
	}
    }

    // TODO: Javadoc!
    private Collection<PresetSetting> getPresetSettings(Node presetNode)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	final Collection<PresetSetting> presetSettings = new ArrayList<PresetSetting>();

	try {
	    final NodeList presetSettingNodes = (NodeList) pathsXPath.evaluate(
		    ConfigurationFileTagsV1.SETTINGS_ELEMENT, presetNode,
		    XPathConstants.NODESET);

	    for (int settingsIndex = 0; settingsIndex < presetSettingNodes
		    .getLength(); settingsIndex++) {

		final Node settingsNode = presetSettingNodes
			.item(settingsIndex);

		final String settingsName = getAttributeValue(settingsNode,
			ConfigurationFileTagsV1.NAME_ATTRIBUTE);

		if (settingsName.length() == 0) {
		    throw new MigrationPathConfigException(
			    "Empty \"name\" attribute declared in node: "
				    + settingsNode.getNodeName());
		}

		Collection<Parameter> parameters = getSettingsParameters(settingsNode);

		final Node descriptionNode = (Node) pathsXPath.evaluate(
			ConfigurationFileTagsV1.DESCRIPTION_ELEMENT,
			settingsNode, XPathConstants.NODE);

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

    // TODO: Javadoc!
    private Collection<Parameter> getToolParameters(Node pathNode,
	    String nameOfElementContainingParameters)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	final Collection<Parameter> toolParameters = new ArrayList<Parameter>();

	try {
	    final NodeList parameterNodes = (NodeList) pathsXPath.evaluate(
		    nameOfElementContainingParameters + "/"
			    + ConfigurationFileTagsV1.PARAMETER_ELEMENT,
		    pathNode, XPathConstants.NODESET);

	    for (int parameterIndex = 0; parameterIndex < parameterNodes
		    .getLength(); parameterIndex++) {

		final Node parameterNode = parameterNodes.item(parameterIndex);

		final String parameterName = getAttributeValue(parameterNode,
			ConfigurationFileTagsV1.NAME_ATTRIBUTE);
		if (parameterName == null) {
		    throw new MigrationPathConfigException(
			    "No \"name\" attribute declared in node: "
				    + parameterNode.getNodeName());
		}

		final Node descriptionNode = (Node) pathsXPath.evaluate(
			ConfigurationFileTagsV1.DESCRIPTION_ELEMENT,
			parameterNode, XPathConstants.NODE);

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

    // TODO: Javadoc!
    private Collection<Parameter> getSettingsParameters(Node settingsNode)
	    throws MigrationPathConfigException {

	final XPath pathsXPath = xPathFactory.newXPath();
	final Collection<Parameter> settingsParameters = new ArrayList<Parameter>();

	try {
	    final NodeList parameterNodes = (NodeList) pathsXPath.evaluate(
		    ConfigurationFileTagsV1.PARAMETER_ELEMENT, settingsNode,
		    XPathConstants.NODESET);

	    for (int parameterIndex = 0; parameterIndex < parameterNodes
		    .getLength(); parameterIndex++) {

		final Node parameterNode = parameterNodes.item(parameterIndex);

		final String parameterName = getAttributeValue(parameterNode,
			ConfigurationFileTagsV1.NAME_ATTRIBUTE);
		if (parameterName == null) {
		    throw new MigrationPathConfigException(
			    "No \"name\" attribute declared in node: "
				    + parameterNode.getNodeName());
		}

		final String parameterValue = parameterNode.getTextContent()
			.trim();
		final Builder parameterBuilder = new Builder(parameterName,
			parameterValue);

		settingsParameters.add(parameterBuilder.build());
	    }

	    return settingsParameters;
	} catch (XPathExpressionException xpee) {
	    throw new MigrationPathConfigException(
		    "Failed reading settings parameter elements from the '"
			    + settingsNode.getNodeName() + "' element.", xpee);
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
		    tempFilesElementName + "/"
			    + ConfigurationFileTagsV1.TEMPFILE_ELEMENT,
		    pathNode, XPathConstants.NODESET);

	    for (int tempfileIndex = 0; tempfileIndex < tempFileNodes
		    .getLength(); tempfileIndex++) {

		final Node tempfileNode = tempFileNodes.item(tempfileIndex);

		final String tempFileLabel = getAttributeValue(tempfileNode,
			ConfigurationFileTagsV1.LABEL_ATTRIBUTE);
		if (tempFileLabel == null) {
		    throw new MigrationPathConfigException(
			    "The \"label\" attribute has no value. Declared in node: "
				    + tempfileNode.getNodeName());

		}

		String tempFileName = null;
		try {
		    tempFileName = getAttributeValue(tempfileNode,
			    ConfigurationFileTagsV1.NAME_ATTRIBUTE);
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
		    commandLineElementName + "/"
			    + ConfigurationFileTagsV1.COMMAND_ELEMENT,
		    pathNode, XPathConstants.NODE);

	    final String command = commandNode.getTextContent().trim();

	    if (command.length() == 0) {
		throw new MigrationPathConfigException(
			"No command was specified in the '"
				+ commandLineElementName + "' element in the '"
				+ pathNode.getNodeName() + "' element.");
	    }

	    final NodeList parameterNodes = (NodeList) pathsXPath
		    .evaluate(
			    commandLineElementName
				    + "/"
				    + ConfigurationFileTagsV1.COMMANDPARAMETER_ELEMENT_XPATH,
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
		    uriListElementName + "/"
			    + ConfigurationFileTagsV1.URI_ELEMENT, pathNode,
		    XPathConstants.NODESET);

	    final List<URI> uriList = new ArrayList<URI>();

	    for (int uriIndex = 0; uriIndex < uriNodes.getLength(); uriIndex++) {

		final Node uriNode = uriNodes.item(uriIndex);
		uriList.add(new URI(getAttributeValue(uriNode,
			ConfigurationFileTagsV1.VALUE_ATTRIBUTE)));
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
     * TODO: Revisit doc....
     * Verify that the version number of the configuration document
     * <code>pathConfiguration</code> is compatible with this factory. The
     * validation is a case-insensitive comparison between the constant
     * <code>{@link ConfigurationFileTagsV1.CONFIGURATION_FORMAT_VERSION}</code>
     * and the value of the <code>version</code> attribute of the
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

	    final Node serviceWrappingNode = (Node) pathsXPath.evaluate(
		    ConfigurationFileTagsV1.CONFIGURATION_ROOT_ELEMENT_XPATH,
		    pathConfiguration, XPathConstants.NODE);
	    final Node versionNode = serviceWrappingNode.getAttributes()
		    .getNamedItem(ConfigurationFileTagsV1.VERSION_ATTRIBUTE);

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

	if (!ConfigurationFileTagsV1.CONFIGURATION_FORMAT_VERSION.toLowerCase()
		.equals(versionNumber.toLowerCase())) {
	    throw new MigrationPathConfigException(
		    "Invalid version number in configuration document '"
			    + versionNumber
			    + "'. Expected version '"
			    + ConfigurationFileTagsV1.CONFIGURATION_FORMAT_VERSION);
	}
    }
}
