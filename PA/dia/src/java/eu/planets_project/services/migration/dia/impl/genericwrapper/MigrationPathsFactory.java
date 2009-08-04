package eu.planets_project.services.migration.dia.impl.genericwrapper;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameter.Builder;
import eu.planets_project.services.migration.dia.impl.genericwrapper.exceptions.MigrationPathConfigException;
import eu.planets_project.services.utils.PlanetsLogger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Factory for construction and initialisation of <code>CliMigrationPaths</code>
 * objects.
 *
 * TODO: The documentation of this class still needs some tender love.
 *
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class MigrationPathsFactory {

    private PlanetsLogger log = PlanetsLogger
            .getLogger(MigrationPathsFactory.class);

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
    public MigrationPaths getMigrationPaths(Document pathConfiguration)
            throws MigrationPathConfigException {

        MigrationPaths migrationPaths = new MigrationPaths();

        log.info("Entering getInstance with configuration");

        // TODO: I realise that the current way of parsing the configuration is
        // not optimal. This factory should be refactored to apply a SAX parser
        // or the like. Also, this factory should apply a separate
        // CliMigrationPaths builder (implementing an interface), so it is
        // possible to support older versions of the configuration document.
        // That is, the factory should also check the version number of the
        // configuration.

        try {
            NodeList topLevelNodes = pathConfiguration
                    .getElementsByTagName("paths")
                    .item(0)
                    .getChildNodes();

            for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
                final Node currentNode = topLevelNodes.item(nodeIndex);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE
                    && "path".equals(currentNode.getNodeName().toLowerCase())) {
                    List<MigrationPath> pathlist =
                            createCliMigrationPathList(currentNode);
                    migrationPaths.addAll(pathlist);
                }
            }
            log.info("Leaving getInstance");
            return migrationPaths;
        } catch (Exception e) {
            throw new MigrationPathConfigException(
                    "Failed parsing migration path configuration document.", e);
        }


    }

    /**
     * Create a <code>List</code> of <code>CliMigrationPath</code> instances,
     * based on the migration path configurations in the path element specified
     * by <code>pathElement</code>.
     *
     * @param pathElement
     *            a path element from a configuration document.
     * @return a <code>List</code> of <code>CliMigrationPath</code> instances.
     * @throws URISyntaxException
     *             if the configuration contains a malformed URI.
     * @throws MigrationPathConfigException
     *             if the format of the path configuration document, specified
     *             by <code>pathElement</code>, was incorrect.
     */
    private List<MigrationPath> createCliMigrationPathList(Node pathElement)
            throws URISyntaxException, MigrationPathConfigException {

        log.info("Entering createCliMigrationPathList");
        final NodeList subNodes = pathElement.getChildNodes();
        MigrationPath pathTemplate = new MigrationPath();

        URI destinationFormatURI = null;
        List<URI> sourceFormatURIs = new ArrayList<URI>();

        for (int subNodeIndex = 0; subNodeIndex < subNodes.getLength(); subNodeIndex++) {

            Node currentSubNode = subNodes.item(subNodeIndex);

            final String currentSubNodeName =
                    currentSubNode.getNodeName().toLowerCase();

            if (currentSubNode.getNodeType() == Node.ELEMENT_NODE) {
                if (Constants.SOURCE_FORMATS_ELEMENT.equals(currentSubNodeName)) {
                    sourceFormatURIs = createURIList(currentSubNode);
                } else
                if (Constants.DESTINATION_FORMAT_ELEMENT.equals(currentSubNodeName)) {
                    destinationFormatURI = createURIList(currentSubNode).get(0);
                } else if (Constants.COMMAND_LINE_ELEMENT.equals(currentSubNodeName)) {
                    pathTemplate.setCommandLine(getCommandLine(currentSubNode));
                } else if (Constants.TEMP_FILES_ELEMENT.equals(currentSubNodeName)) {
                    pathTemplate = configureTempFileDeclarations(
                            currentSubNode, pathTemplate);
                } else if (Constants.TOOL_PARAMETERS_ELEMENT.equals(currentSubNodeName)) {
                    pathTemplate
                            .setToolParameters(createParameterList(currentSubNode));
                } else if (Constants.TOOL_PRESETS_ELEMENT.equals(currentSubNodeName)) {
                    pathTemplate = configureToolPresets(currentSubNode,
                                                        pathTemplate);
                }
            }
        }

        if (destinationFormatURI == null) {
            throw new MigrationPathConfigException(
                    "The path element did not have a destination format URI.");
        }

        if (sourceFormatURIs.isEmpty()) {
            throw new MigrationPathConfigException(
                    "The path element did not have any source format URIs.");
        }

        List<MigrationPath> result = createCliMigrationPathInstances(pathTemplate,
                                                                     sourceFormatURIs,
                                                                     destinationFormatURI);
        log.info("Leaving createCliMigrationPathList");
        return result;
    }

    /**
     * Configure the tool presets of <code>pathToConfigure</code> based on the
     * information carried by <code>toolPresetsNode</code> and its sub-nodes.
     *
     * @param toolPresetsElement
     *            <code>Node</code> containing a <code>toolpresets</code>
     *            document element.
     * @param pathToConfigure
     *            <code>CliMigrationPath</code> instance to add preset
     *            information to.
     * @return <code>pathToConfigure</code> with the configured tool presets.
     */
    private MigrationPath configureToolPresets(Node toolPresetsElement,
                                               MigrationPath pathToConfigure) {

        // Get the name of the default preset, if it has not been set yet.
        if (pathToConfigure.getDefaultPresetCategory() == null) {
            pathToConfigure.setDefaultPresetCategory(getAttributeValue(
                    toolPresetsElement, Constants.DEFAULT_ATTRIBUTE));
        }

        final NodeList presetNodes = toolPresetsElement.getChildNodes();
        for (int presetIndex = 0; presetIndex < presetNodes.getLength(); presetIndex++) {

            final Node currentNode = presetNodes.item(presetIndex);

            final String currentNodeName = currentNode.getNodeName()
                    .toLowerCase();
            if ((currentNode.getNodeType() == Node.ELEMENT_NODE)
                && (Constants.PRESET_ELEMENT.equals(currentNodeName))) {

                pathToConfigure = configurePreset(pathToConfigure, currentNode);
            }
        }

        return pathToConfigure;
    }

    /**
     * Add a new preset to <code>pathToConfigure</code> based on the preset
     * element in <code>Node presetElement</code>.
     *
     * @param pathToConfigure
     *            <code>CliMigrationPath</code> instance to add a new preset to.
     * @param presetElement
     *            <code>Node</code> instance containing the configuration of the
     *            preset to add.
     */
    private MigrationPath configurePreset(MigrationPath pathToConfigure,
                                          Node presetElement) {

        // Get the name of the default preset setting, if it has not been set
        // yet.
        if (pathToConfigure.getDefaultPresetCategoryValue() == null) {
            pathToConfigure.setDefaultPresetCategoryValue(getAttributeValue(
                    presetElement, Constants.DEFAULT_ATTRIBUTE));
        }

        final String presetName = getAttributeValue(presetElement,
                                                    Constants.NAME_ATTRIBUTE);
        final NodeList settingsNodes = presetElement.getChildNodes();
        for (int settingsIndex = 0; settingsIndex < settingsNodes.getLength(); settingsIndex++) {

            final Node currentNode = settingsNodes.item(settingsIndex);
            final String currentNodeName = currentNode.getNodeName()
                    .toLowerCase();

            if ((currentNode.getNodeType() == Node.ELEMENT_NODE)
                && (Constants.SETTINGS_ELEMENT.equals(currentNodeName))) {

                final String settingName = getAttributeValue(currentNode,
                                                             Constants.NAME_ATTRIBUTE);

                final Collection<Parameter> settingParameters = createParameterList(currentNode);
                pathToConfigure.addToolPreset(presetName, settingName,
                                              settingParameters);
                // TODO: Remember the "description" element! it should go with
                // the setting information...
            }

        }
        return pathToConfigure;
    }

    /**
     * Create a collection of <code>Parameter</code> instances based on the
     * parameter (child) elements in <code>nodeWithParameterElements</code>.
     *
     * @param nodeWithParameterElements
     *            <code>Node</code> instance containing parameter child
     *            elements.
     * @return a <code>Collection</code> of <code>Parameter</code> instances
     *         created from the parameter elements in
     *         <code>nodeWithParameterElements</code>
     */
    private Collection<Parameter> createParameterList(
            Node nodeWithParameterElements) {

        Collection<Parameter> parameters = new ArrayList<Parameter>();
        final NodeList nodeList = nodeWithParameterElements.getChildNodes();
        for (int parameterIndex = 0; parameterIndex < nodeList.getLength(); parameterIndex++) {

            final Node currentNode = nodeList.item(parameterIndex);
            final String currentNodeName = currentNode.getNodeName()
                    .toLowerCase();

            if ((currentNode.getNodeType() == Node.ELEMENT_NODE)
                && (Constants.PARAMETER_ELEMENT.equals(currentNodeName))) {

                parameters.add(createParameter(currentNode));
            }

        }
        return parameters;
    }

    /**
     * Create a <code>Parameter</code> instance based on the configuration in
     * the parameter element <code>parameterElement</code>.
     *
     * @param parameterElement
     *            parameter element configuration to use for the creation of the
     *            <code>Parameter</code> instance.
     * @return a <code>Parameter</code> instance created from the configuration
     *         information in <code>parameterElement</code>
     */
    private Parameter createParameter(Node parameterElement) {

        final NodeList parameterSubNodes = parameterElement.getChildNodes();
        String description = "";
        String value = "";

        for (int subNodeIndex = 0; subNodeIndex < parameterSubNodes.getLength(); subNodeIndex++) {

            final Node currentNode = parameterSubNodes.item(subNodeIndex);
            final String currentNodeName = currentNode.getNodeName()
                    .toLowerCase();

            if ((currentNode.getNodeType() == Node.ELEMENT_NODE)
                && (Constants.DESCRIPTION_ELEMENT.equals(currentNodeName))) {
                description = currentNode.getTextContent();
            } else if (currentNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                value = currentNode.getNodeValue();
            }
        }
        final String parameterName = getAttributeValue(parameterElement,
                                                       Constants.NAME_ATTRIBUTE);
        final Builder parameterBuilder = new Builder(parameterName, value);
        parameterBuilder.description(description);

        return parameterBuilder.build();
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
     */
    private String getAttributeValue(Node elementWithAttributes,
                                     String attributeName) {

        final NamedNodeMap attributes = elementWithAttributes.getAttributes();

        final Node attributeNode = attributes.getNamedItem(attributeName);
        final String attributeValue = (attributeNode != null) ? attributeNode
                .getNodeValue() : null;
        return attributeValue;
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
    private MigrationPath configureTempFileDeclarations(Node tempFilesNode,
                                                        MigrationPath pathToConfigure)
            throws MigrationPathConfigException {

        log.info("Entering configureTempFileDeclarations");
        final NodeList tempFileNodes = tempFilesNode.getChildNodes();
        for (int tempFileNodeIndex = 0; tempFileNodeIndex < tempFileNodes
                .getLength(); tempFileNodeIndex++) {
            final Node currentNode = tempFileNodes.item(tempFileNodeIndex);
            final String currentNodeName = currentNode.getNodeName()
                    .toLowerCase();
            if (currentNode.getNodeType() == Node.ELEMENT_NODE){

                log.info("Found temp file declaration");


                NamedNodeMap attrs = currentNode.getAttributes();

                TempFile tempfile = new TempFile(attrs.getNamedItem(
                        "label").getNodeValue());
                log.info("Made new tempfile with code "+tempfile.getCodename());

                Node name = attrs.getNamedItem("name");
                if (name != null){
                    tempfile.setRequestedName(name.getNodeValue());
                }


                if (Constants.TEMP_INPUT_FILE_ELEMENT.equals(currentNodeName)){
                    log.info("This is the input file");
                    pathToConfigure.setTempInputFile(tempfile);

                } else if (Constants.TEMP_OUTPUT_FILE_ELEMENT.equals(currentNodeName)){
                    pathToConfigure.setTempOutputFile(tempfile);
                    log.info("This is the output file");

                } else if (Constants.TEMP_FILE_ELEMENT.equals(currentNodeName)){
                    pathToConfigure.addTempFilesDeclaration(tempfile);
                    log.info("This is just a temp file");
                }
            }
        }

        log.info("Leaving configureTempFileDeclarations");
        return pathToConfigure;
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
    private List<MigrationPath> createCliMigrationPathInstances(
            MigrationPath pathTemplate, List<URI> sourceFomatURIs,
            URI destinationFormatURI) throws MigrationPathConfigException {

        final List<MigrationPath> paths = new ArrayList<MigrationPath>();
        for (URI sourceFormatUri : sourceFomatURIs) {
            MigrationPath newPath;
            try {
                newPath = (MigrationPath) pathTemplate.clone();
            }  catch (CloneNotSupportedException e) {
                throw new MigrationPathConfigException(
                        "Failed copying path template when instantiating CliMigrationPath for migration path: "
                        + sourceFomatURIs
                        + " -> "
                        + destinationFormatURI);
            }

            newPath.setSourceFormat(sourceFormatUri);
            newPath.setDestinationFormat(destinationFormatURI);
            log.debug("Createing CliMigrationPath instance for the path: "
                      + sourceFomatURIs + " -> " + destinationFormatURI);
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
    private MigrationPath copyPath(MigrationPath pathTemplate)
            throws URISyntaxException {

        MigrationPath pathCopy = new MigrationPath();

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
            final Builder parameterBuilder = new Builder(parameter.getName(),
                                                         parameter.getValue());
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
        pathCopy.setDefaultPresetCategory(pathTemplate
                .getDefaultPresetCategory());
        pathCopy.setDefaultPresetCategoryValue(pathTemplate
                .getDefaultPresetCategoryValue());

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
    private List<URI> createURIList(Node uriListNode) throws URISyntaxException {

        final List<URI> uriList = new ArrayList<URI>();
        final NodeList childNodes = uriListNode.getChildNodes();

        for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {
            final Node currentChildNode = childNodes.item(childIndex);
            if (currentChildNode.getNodeType() == Node.ELEMENT_NODE
                && "uri".equals(currentChildNode.getNodeName()
                    .toLowerCase())) {
                uriList.add(new URI(getAttributeValue(currentChildNode,
                                                      Constants.VALUE_ATTRIBUTE)));
            }
        }
        return uriList;
    }
}
