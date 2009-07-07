/**
 * 
 */
package eu.planets_project.services.migration.dia.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameter.Builder;
import eu.planets_project.services.utils.PlanetsLogger;

/**
 * Factory for construction and initialisation of <code>CliMigrationPaths</code>
 * objects.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class CliMigrationPathsFactory {

    /**
     * 
     */
    private static final String LABEL_ATTRIBUTE = "label";
    /**
     * 
     */
    private static final String NAME_ATTRIBUTE = "name";
    /**
     * Code will break if the *_ELEMENT constant values are not in lowercase!
     */
    private static final String TOOL_PRESETS_ELEMENT = "toolpresets";
    /**
     * 
     */
    private static final String TOOL_PARAMETERS_ELEMENT = "toolparameters";
    /**
     * 
     */
    private static final String TEMP_FILES_ELEMENT = "tempfiles";
    /**
     * 
     */
    private static final String COMMAND_LINE_ELEMENT = "commandline";
    /**
     * 
     */
    private static final String DESTINATION_FORMAT_ELEMENT = "destinationformat";
    /**
     * 
     */
    private static final String SOURCE_FORMATS_ELEMENT = "sourceformats";
    /*
     * Temp file element names.
     */
    private static final String TEMP_INPUT_FILE = "inputfile";
    private static final String TEMP_OUTPUT_FILE = "outputfile";
    private static final String TEMP_FILE = "outputfile";

    @SuppressWarnings("unused")
    private PlanetsLogger log = PlanetsLogger
	    .getLogger(CliMigrationPathsFactory.class);

    // TODO: We should create a schema for the configuration file and refer to
    // it in this javadoc. Also, this factory should check the specified config
    // file against the schema. The config file is currently not validated.
    /**
     * Create a <code>CliMigrationPaths</code> object containing the migration
     * paths described by the <code>pathConfiguration</code> document.
     * 
     * @return A <code>CliMigrationPaths</code> object containing all the paths
     *         configured in the configuration document specified.
     * @throws MigrationPathConfigException
     *             if the contents of <code>pathConfiguration</code> is invalid.
     */
    public CliMigrationPaths getInstance(Document pathConfiguration)
	    throws MigrationPathConfigException {

	CliMigrationPaths migrationPaths = new CliMigrationPaths();

	try {
	    NodeList topLevelNodes = pathConfiguration.getChildNodes().item(0)
		    .getChildNodes();

	    for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
		final Node currentNode = topLevelNodes.item(nodeIndex);
		if (currentNode.getNodeType() == Node.ELEMENT_NODE
			&& "path".equals(currentNode.getNodeName())) {
		    for (CliMigrationPath cliMigrationPath : createCliMigrationPathList(currentNode))
			migrationPaths.addPath(cliMigrationPath);
		}
	    }

	    return migrationPaths;
	} catch (Exception e) {
	    throw new MigrationPathConfigException(
		    "Failed parsing migration path configuration document.", e);
	}
    }

    private List<CliMigrationPath> createCliMigrationPathList(Node pathNode)
	    throws URISyntaxException, MigrationPathConfigException {

	final NodeList subNodes = pathNode.getChildNodes();
	CliMigrationPath pathTemplate = new CliMigrationPath();
	URI destinationFormatURI = null; // FIXME! FOOO!
	List<URI> sourceFomatURIs = new ArrayList<URI>();
	for (int subNodeIndex = 0; subNodeIndex < subNodes.getLength(); subNodeIndex++) {
	    Node currentSubNode = subNodes.item(subNodeIndex);
	    if (currentSubNode.getNodeType() == Node.ELEMENT_NODE) {
		if (SOURCE_FORMATS_ELEMENT.equals(currentSubNode.getNodeName())) {
		    sourceFomatURIs = getURIList(currentSubNode);
		} else if (DESTINATION_FORMAT_ELEMENT.equals(currentSubNode
			.getNodeName().toLowerCase())) {
		    destinationFormatURI = getURIList(currentSubNode).get(0);
		} else if (COMMAND_LINE_ELEMENT.equals(currentSubNode
			.getNodeName().toLowerCase())) {
		    pathTemplate.setCommandLine(getCommandLine(currentSubNode));
		} else if (TEMP_FILES_ELEMENT.equals(currentSubNode
			.getNodeName().toLowerCase())) {
		    pathTemplate = configureTempFileDeclarations(
			    currentSubNode, pathTemplate);
		} else if (TOOL_PARAMETERS_ELEMENT.equals(currentSubNode
			.getNodeName().toLowerCase())) {
		    pathTemplate
			    .setToolParameters(getToolParameters(currentSubNode));
		} else if (TOOL_PRESETS_ELEMENT.equals(currentSubNode
			.getNodeName().toLowerCase())) {
		    pathTemplate = configureToolPresets(currentSubNode,
			    pathTemplate);
		}

	    }
	}
	return createCliMigrationPathInstances(pathTemplate, sourceFomatURIs,
		destinationFormatURI);
    }

    /**
     * @param toolPresetsNode
     * @param pathTemplate
     * @return
     */
    private CliMigrationPath configureToolPresets(Node toolPresetsNode,
	    CliMigrationPath pathTemplate) {

	// for all categories
	//     - create category
	//     for all category presets
	// 		for all presets
	//                   - add preset value and parameters to category

	
//	final NodeList toolPresetNodes = toolPresetsNode.getChildNodes();
//	for (int toolPresetNodeIndex = 0; toolPresetNodeIndex < toolPresetNodes.getLength(); toolPresetNodeIndex++) {
//	    final Node currentPresetNode = toolPresetNodes.item(toolPresetNodeIndex);
//	    final String currentNodeName = currentPresetNode.getNodeName();
//	    if ((currentPresetNode.getNodeType() == Node.ELEMENT_NODE)
//		    && (TEMP_INPUT_FILE.equals(currentNodeName)
//			    || TEMP_OUTPUT_FILE.equals(currentNodeName) || TEMP_FILE
//			    .equals(currentNodeName))) {
//		final Map<String, String> fileLabelAndName = getLabelAndNameAttribute(currentPresetNode);
//
//		if (TEMP_INPUT_FILE.equals(currentNodeName)) {
//		    pathToConfigure.setTempInputFile(fileLabelAndName);
//		} else if (TEMP_OUTPUT_FILE.equals(currentNodeName)) {
//		    pathToConfigure.setTempOutputFile(fileLabelAndName);
//		} else {
//		    // It's a temp. file declaration
//		    pathToConfigure.addTempFilesDeclarations(fileLabelAndName);
//		}
//	    }
//	}

	
	// TODO Auto-generated method stub
	System.out.println("TODO: configure tool presets.");
	return pathTemplate;
    }

    /**
     * @param currentSubNode
     * @return
     */
    private Collection<Parameter> getToolParameters(Node currentSubNode) {
	// TODO Auto-generated method stub
	System.out.println("TODO: configure tool parameters.");
//	    final Builder parameterBuilder = new Builder(parameter.getName(), parameter
//		    .getValue());
//	    parameterBuilder.description(parameter.getDescription());
//	    parameterBuilder.type(parameter.getType());

	return new ArrayList<Parameter>();
    }

    /**
     * Parse all declarations of temporary files from <code>tempFilesNode</code>
     * and add the information to <code>pathToConfigure</code>.
     * 
     * @param tempFilesNode
     *            <code>Node</code> containing declarations of temporary files.
     * @param pathToConfigure
     *            <code>CliMigrationPath</code> instance to add temp. file
     *            declarations to.
     * @return <code>pathToConfigure</code> with any found temp. file
     *         declarations added.
     * @throws MigrationPathConfigException
     *             if any errors were encountered in the
     *             <code>tempFilesNode</code> node.
     */
    private CliMigrationPath configureTempFileDeclarations(Node tempFilesNode,
	    CliMigrationPath pathToConfigure)
	    throws MigrationPathConfigException {

	final NodeList tempFileNodes = tempFilesNode.getChildNodes();
	for (int tempFileNodeIndex = 0; tempFileNodeIndex < tempFileNodes
		.getLength(); tempFileNodeIndex++) {
	    final Node currentNode = tempFileNodes.item(tempFileNodeIndex);
	    final String currentNodeName = currentNode.getNodeName();
	    if ((currentNode.getNodeType() == Node.ELEMENT_NODE)
		    && (TEMP_INPUT_FILE.equals(currentNodeName)
			    || TEMP_OUTPUT_FILE.equals(currentNodeName) || TEMP_FILE
			    .equals(currentNodeName))) {
		final Map<String, String> fileLabelAndName = getLabelAndNameAttribute(currentNode);

		if (TEMP_INPUT_FILE.equals(currentNodeName)) {
		    pathToConfigure.setTempInputFile(fileLabelAndName);
		} else if (TEMP_OUTPUT_FILE.equals(currentNodeName)) {
		    pathToConfigure.setTempOutputFile(fileLabelAndName);
		} else {
		    // It's a temp. file declaration
		    pathToConfigure.addTempFilesDeclarations(fileLabelAndName);
		}
	    }
	}
	return pathToConfigure;
    }

    /**
     * Get the value of the mandatory <code>LABEL_ATTRIBUTE</code> and the
     * optional <code>NAME_ATTRIBUTE</code> of the
     * <code>elementWithAttributes</code> element. The element may have several
     * other attributes, however, this method expects that at least the
     * <code>LABEL_ATTRIBUTE</code> is declared.
     * 
     * @param elementWithAttributes
     *            <code>Node</code> having at least a
     *            <code>LABEL_ATTRIBUTE</code> and optionally a
     *            <code>NAME_ATTRIBUTE</code>.
     * @return a map containing the value of the <code>LABEL_ATTRIBUTE</code>
     *         mapped with the <code>NAME_ATTRIBUTE</code> value or null if it
     *         is not declared.
     * @throws MigrationPathConfigException
     *             if no <code>LABEL_ATTRIBUTE</code> is declared in
     *             <code>elementWithAttributes</code>.
     */
    private Map<String, String> getLabelAndNameAttribute(
	    Node elementWithAttributes) throws MigrationPathConfigException {

	final Map<String, String> labelNameMap = new HashMap<String, String>();
	NamedNodeMap attributes = elementWithAttributes.getAttributes();

	final Node nameNode = attributes.getNamedItem(NAME_ATTRIBUTE);
	final String nameValue = (nameNode != null) ? nameNode.getNodeValue()
		: null;
	try {
	    final String labelValue = attributes.getNamedItem(LABEL_ATTRIBUTE)
		    .getNodeValue();
	    labelNameMap.put(labelValue, nameValue);
	} catch (NullPointerException npe) {
	    throw new MigrationPathConfigException(
		    "No \"label\" attribute declared in node: "
			    + elementWithAttributes.getNodeName(), npe);
	}
	return labelNameMap;
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
    private List<CliMigrationPath> createCliMigrationPathInstances(
	    CliMigrationPath pathTemplate, List<URI> sourceFomatURIs,
	    URI destinationFormatURI) throws MigrationPathConfigException {

	final List<CliMigrationPath> paths = new ArrayList<CliMigrationPath>();
	for (URI sourceFormatUri : sourceFomatURIs) {
	    CliMigrationPath newPath;
	    try {
		newPath = copyPath(pathTemplate);
	    } catch (URISyntaxException use) {
		throw new MigrationPathConfigException(
			"Failed copying path template when instantiating CliMigrationPath for migration path: "
				+ sourceFomatURIs
				+ " -> "
				+ destinationFormatURI);
	    }

	    newPath.setSourceFormat(sourceFormatUri);
	    newPath.setDestinationFormat(destinationFormatURI);
	    newPath.setCommandLine(pathTemplate.getCommandLine());
	    System.out
		    .println("Createing CliMigrationPath instance for the path: "
			    + sourceFomatURIs + " -> " + destinationFormatURI);// TODO:
	    // remove
	    // sysout
	    paths.add(newPath);
	}
	return paths;
    }

    /**
     * Make a safe (deep) copy of the contents of <code>pathTemplate</code>.
     * 
     * @param pathTemplate
     *            <code>CliMigrationPath</code> instance to copy.
     * @return a copy of <code>pathTemplate</code> that shares no data with the
     *         original.
     * @throws URISyntaxException
     *             in the unlikely event that a <code>URI</code> could not be
     *             copied.
     */
    private CliMigrationPath copyPath(CliMigrationPath pathTemplate)
	    throws URISyntaxException {

	CliMigrationPath pathCopy = new CliMigrationPath();

	pathCopy.setCommandLine(pathTemplate.getCommandLine());

	// Make a safe copy of the format URIs
	URI formatURI = pathTemplate.getDestinationFormat();
	formatURI = (formatURI != null) ? new URI(formatURI.toString())
		: formatURI;
	pathCopy.setDestinationFormat(formatURI);

	formatURI = pathTemplate.getSourceFormat();
	formatURI = (formatURI != null) ? new URI(formatURI.toString())
		: formatURI;
	pathCopy.setSourceFormat(formatURI);

	// The below method already makes a safe copy.
	if (pathTemplate.getTempFileDeclarations() != null) {
	    pathCopy.addTempFilesDeclarations(pathTemplate
		    .getTempFileDeclarations());
	}

	final ArrayList<Parameter> parametersCopy = new ArrayList<Parameter>();
	for (Parameter parameter : pathTemplate.getToolParameters()) {
	    final Builder parameterBuilder = new Builder(parameter.getName(), parameter
		    .getValue());
	    parameterBuilder.description(parameter.getDescription());
	    parameterBuilder.type(parameter.getType());
	    parametersCopy.add(parameterBuilder.build());
	}
	pathCopy.setToolParameters(parametersCopy);

	// Make a safe copy of the presets.
	for (String presetCategory : pathTemplate.getToolPresetCategories()) {
	    for (String presetValue : pathTemplate
		    .getToolPresetValues(presetCategory)) {
		final ArrayList<Parameter> presetParametersCopy = new ArrayList<Parameter>();
		for (Parameter presetValueParameter : pathTemplate
			.getToolPresetParameters(presetCategory, presetValue)) {
		    presetParametersCopy.add(new Parameter(presetValueParameter
			    .getName(), presetValueParameter.getValue()));
		}
		pathCopy.addToolPreset(presetCategory, presetValue,
			presetParametersCopy);
	    }
	}

	return pathCopy;
    }

    /**
     * Get a command line string from the document node
     * <code>commandLineNode</code>.
     * 
     * @param commandLineNode
     *            <code>Node</code> containing a command line string.
     * @return <code>String</code> containing the command line data from
     *         <code>commandLineNode</code>
     * @throws MigrationPathConfigException
     *             if not <code>TEXT_NODE</code> element was not found in
     *             <code>commandLineNode</code>
     */
    private String getCommandLine(Node commandLineNode)
	    throws MigrationPathConfigException {
	final NodeList childNodes = commandLineNode.getChildNodes();
	for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {
	    final Node currentNode = childNodes.item(childIndex);
	    if (currentNode.getNodeType() == Node.CDATA_SECTION_NODE) {
		// Avoid accidental new-lines and carriage returns
		return currentNode.getNodeValue().replaceAll("[\n\r]", "");
	    }
	}

	throw new MigrationPathConfigException(
		"This supposed command line node contains no CDATA element. NodeName = "
			+ commandLineNode.getNodeName());
    }

    /**
     * Create <code>URI</code> objects for all URIs in <code>uriListNode</code>.
     * 
     * @param uriListNode
     *            Node expected to contain a list of URI elements.
     * @return List containing an <code>URI</code> instance for each URI element
     *         found in <code>uriListNode</code>.
     * @throws URISyntaxException
     *             if a URI in the node is invalid.
     */
    private List<URI> getURIList(Node uriListNode) throws URISyntaxException {

	final List<URI> uriList = new ArrayList<URI>();
	final NodeList childNodes = uriListNode.getChildNodes();

	for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {
	    final Node currentChildNode = childNodes.item(childIndex);
	    if (currentChildNode.getNodeType() == Node.ELEMENT_NODE
		    && "uri".equals(currentChildNode.getNodeName()
			    .toLowerCase())) {
		final NamedNodeMap attributes = currentChildNode
			.getAttributes();
		final Node valueAttributeNode = attributes
			.getNamedItem("value");
		uriList.add(new URI(valueAttributeNode.getNodeValue()));
	    }
	}
	return uriList;
    }

    // Element fileformats = pathConfiguration.getDocumentElement();
    // if (fileformats != null){
    // NodeList children = fileformats.getChildNodes();
    // for (int i = 0; i<children.getLength();i++){
    // Node child = children.item(i);
    // if (child.getNodeType() == Node.ELEMENT_NODE){
    // if (child.getNodeName().equals("path")){
    // CliMigrationPath pathdef = decodePathNode(child);
    // paths.add(pathdef);
    // }
    // }
    // }
    // }

    // private static CliMigrationPath decodePathNode(Node path)
    // throws URISyntaxException {
    // NodeList children = path.getChildNodes();
    // Set<URI> froms = null;
    // Set<URI> tos = null;
    // String command = null;
    // for (int i = 0; i < children.getLength(); i++) {
    // Node child = children.item(i);
    // if (child.getNodeType() == Node.ELEMENT_NODE) {
    //
    // if (child.getNodeName().equals("from")) {
    // froms = decodeFromOrToNode(child);
    // }
    // if (child.getNodeName().equals("to")) {
    // tos = decodeFromOrToNode(child);
    // }
    // if (child.getNodeName().equals("command")) {
    // command = decodeCommandNode(child);
    // }
    //
    // }
    // }
    // return new CliMigrationPath(froms, tos, command);
    //
    // }

    //TODO: Remember removing the below methods.
    @SuppressWarnings("unused")
    private static Set<URI> decodeFromOrToNode(Node urilist)
	    throws URISyntaxException {
	NodeList children = urilist.getChildNodes();
	Set<URI> uris = new HashSet<URI>();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (child.getNodeType() == Node.ELEMENT_NODE) {
		if (child.getNodeName().equals("uri")) {
		    URI uri = decodeURI(child);
		    uris.add(uri);
		}
	    }
	}
	return uris;
    }

    private static URI decodeURI(Node uri) throws URISyntaxException {
	NamedNodeMap attrs = uri.getAttributes();

	Node item = attrs.getNamedItem("value");
	String urivalue = item.getNodeValue();
	return new URI(urivalue);
    }

    @SuppressWarnings("unused")
    private static String decodeCommandNode(Node command) {
	Node commandtext = command.getFirstChild();
	if (commandtext.getNodeType() == Node.TEXT_NODE) {
	    return commandtext.getNodeValue();
	}
	return "";
    }

}
