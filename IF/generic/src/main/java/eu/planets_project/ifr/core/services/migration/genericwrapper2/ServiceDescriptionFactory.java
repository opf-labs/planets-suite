package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.ConfigurationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationPathConfigException;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;

/**
 * @author Pelle Kofod &lt;pko@statsbiblioteket.dk&gt;
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class ServiceDescriptionFactory {
    private Logger log = Logger.getLogger(ServiceDescriptionFactory.class
	    .getName());

    private final Document configuration;
    private final String canonicalServiceName;

    ServiceDescriptionFactory(String canonicalServiceName,
	    Document wrapperConfiguration) {
	configuration = wrapperConfiguration;
	this.canonicalServiceName = canonicalServiceName;
    }

    ServiceDescription getServiceDescription() throws ConfigurationException,
	    MigrationPathConfigException {

	NodeList topLevelNodes = configuration.getElementsByTagName(
		Constants.SERVICE_DESCRIPTION).item(0).getChildNodes();

	String title = null, description = null, version = null, creator = null, publisher = null, identifier = null, instructions = null, furtherinfo = null, logo = null;

	Tool tool = null;

	for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
	    final Node currentNode = topLevelNodes.item(nodeIndex);
	    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
		if (currentNode.getNodeName().equals(Constants.TITLE)) {
		    title = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(
			Constants.DESCRIPTION)) {
		    description = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(Constants.TOOL)) {
		    tool = parseTool(currentNode);
		} else if (currentNode.getNodeName().equals(Constants.VERSION)) {
		    version = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(Constants.CREATOR)) {
		    creator = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName()
			.equals(Constants.PUBLISHER)) {
		    publisher = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(
			Constants.IDENTIFIER)) {
		    identifier = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(
			Constants.INSTRUCTIONS)) {
		    instructions = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(
			Constants.FURTHERINFO)) {
		    furtherinfo = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(Constants.LOGO)) {
		    logo = currentNode.getTextContent().trim();
		}
	    }
	}
	if (title == null) {
	    throw new ConfigurationException("title not set in configfile");
	}
	if (creator == null) {
	    throw new ConfigurationException("creator not set in configfile");
	}
	
	//FIXME! Is that the correct type passed on to the builder? Shouldn't it be the type of the concrete service?
	ServiceDescription.Builder builder = new ServiceDescription.Builder(
		title, "eu.planets_project.ifr.services.migrate.Migrate");

	try {
	    builder.author(creator);
	    builder.classname(canonicalServiceName);
	    builder
		    .endpoint(new URL("http://FIXME! put the correct URL here!"));
	    builder.description(description);
	    builder.identifier(identifier);
	    builder.instructions(instructions);
	    builder.version(version);
	    builder.tool(tool);
	    builder.serviceProvider(publisher);
	    eu.planets_project.services.datatypes.MigrationPath planetsPaths[] = getPlanetsMigrationPaths(configuration);
	    builder.paths(planetsPaths);
	    builder.inputFormats(getInputFormats(planetsPaths));
	    
	    //FIXME! Collect all unique parameters from all the paths defined in the configuration.
	    final List<Parameter> parameters = planetsPaths[0].getParameters();
	    builder.parameters(parameters);
	} catch (MalformedURLException mue) {
	    throw new ConfigurationException(
		    "Failed adding end-point information to the service "
			    + "description.", mue);
	}
	if (furtherinfo != null) {
	    try {
		builder.furtherInfo(new URI(furtherinfo));
	    } catch (URISyntaxException e) {
		throw new ConfigurationException(
			"furtherInfo not set to valid value", e);
	    }
	}
	if (logo != null) {
	    try {
		builder.logo(new URI(logo));
	    } catch (URISyntaxException e) {
		throw new ConfigurationException("logo not set to valid value",
			e);
	    }
	}

	return builder.build();

    }

    /**
     * Extract the unique input format <code>URI</code>s from the migration
     * paths specified by <code>planetsPaths</code>.
     * 
     * @param planetsPaths
     *            A list of planets <code>MigrationPath</code> instances to
     *            extract input format <code>URI</code>s from.
     * @return An array containing the unique input format <code>URI</code>s
     *         from the migration paths.
     */
    private URI[] getInputFormats(
	    eu.planets_project.services.datatypes.MigrationPath[] planetsPaths) {
	final Set<URI> inputFormats = new HashSet<URI>();
	for (eu.planets_project.services.datatypes.MigrationPath migrationPath : planetsPaths) {
	    inputFormats.add(migrationPath.getInputFormat());
	}
	return inputFormats.toArray(new URI[inputFormats.size()]);
    }

    private eu.planets_project.services.datatypes.MigrationPath[] getPlanetsMigrationPaths(
	    Document wrapperConfiguration) throws MigrationPathConfigException {

	final DBMigrationPathFactory migrationPathFactory = new DBMigrationPathFactory(
		wrapperConfiguration);
	
	final MigrationPaths migrationPaths = migrationPathFactory
		.getAllMigrationPaths();

	final Collection<MigrationPath> pathCollection = migrationPaths
		.getAllMigrationPaths();

	return convertToPlanetsPaths(pathCollection)
		.toArray(
			new eu.planets_project.services.datatypes.MigrationPath[pathCollection
				.size()]);
    }

    private Tool parseTool(Node tool) throws ConfigurationException {
	NodeList topLevelNodes = tool.getChildNodes();
	String description = null, version = null, identifier = null, name = null, homepage = null;

	for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
	    final Node currentNode = topLevelNodes.item(nodeIndex);
	    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
		if (currentNode.getNodeName().equals(Constants.DESCRIPTION)) {
		    description = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(Constants.VERSION)) {
		    version = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(
			Constants.IDENTIFIER)) {
		    identifier = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(Constants.NAME)) {
		    name = currentNode.getTextContent().trim();
		} else if (currentNode.getNodeName().equals(Constants.HOMEPAGE)) {
		    homepage = currentNode.getTextContent().trim();
		}

	    }

	}
	URL homepageURL = null;
	URI identifierURI = null;
	if (homepage != null) {
	    try {
		homepageURL = new URL(homepage);
	    } catch (MalformedURLException e) {
		throw new ConfigurationException(
			"Homepage not set to valid value", e);
	    }
	}
	if (identifier != null) {
	    try {
		identifierURI = new URI(identifier);
	    } catch (URISyntaxException e) {
		throw new ConfigurationException(
			"identifier not set to valid value", e);
	    }
	}

	Tool t = new Tool(identifierURI, name, version, description,
		homepageURL);
	return t;
    }

    /**
     * TODO: This should go into a utility class.
     * 
     * Convert a collection of generic wrapper migration paths to a PLANETS
     * <code>MigrationPath</code> instances. During this conversion any presets
     * of the generic wrapper migration paths will be converted to a PLANETS
     * parameters and a list of valid values and their descriptions will be
     * appended to the description of the (preset) parameter.
     * 
     * @param genericWrapperMigrationPaths
     *            A collection of generic wrapper <code>MigrationPath</code>
     *            instances to convert.
     * @return a <code>List</code> of
     *         <code>eu.planets_project.services.datatypes.MigrationPath</code>
     *         created from the generic wrapper migration paths.
     */
    private List<eu.planets_project.services.datatypes.MigrationPath> convertToPlanetsPaths(
	    Collection<MigrationPath> genericWrapperMigrationPaths) {

	final ArrayList<eu.planets_project.services.datatypes.MigrationPath> planetsPaths = new ArrayList<eu.planets_project.services.datatypes.MigrationPath>();
	for (MigrationPath migrationPath : genericWrapperMigrationPaths) {

	    final List<Parameter> planetsParameters = new ArrayList<Parameter>();
	    planetsParameters.addAll(migrationPath.getToolParameters());

	    // Add a parameter for each preset (category)
	    final ToolPresets toolPresets = migrationPath.getToolPresets();
	    final Collection<Preset> presets = toolPresets.getAllToolPresets();
	    for (Preset preset : presets) {

		Parameter.Builder parameterBuilder = new Parameter.Builder(
			preset.getName(), null);

		// Append a description of the valid values for the preset
		// parameter.
		String usageDescription = "\n\nValid values : Description\n";

		for (PresetSetting presetSetting : preset.getAllSettings()) {

		    usageDescription += "\n" + presetSetting.getName() + " : "
			    + presetSetting.getDescription();
		}

		parameterBuilder.description(preset.getDescription()
			+ usageDescription);

		planetsParameters.add(parameterBuilder.build());
	    }
	    planetsPaths
		    .add(new eu.planets_project.services.datatypes.MigrationPath(
			    migrationPath.getSourceFormat(), migrationPath
				    .getDestinationFormat(), planetsParameters));
	}

	return planetsPaths;
    }

}
