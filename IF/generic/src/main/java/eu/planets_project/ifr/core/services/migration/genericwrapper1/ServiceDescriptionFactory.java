package eu.planets_project.ifr.core.services.migration.genericwrapper1;

import eu.planets_project.ifr.core.services.migration.genericwrapper1.exceptions.ConfigurationException;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger; 


/**
 * Created by IntelliJ IDEA.
 * User: pko
 * Date: Aug 4, 2009
 * Time: 1:30:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDescriptionFactory {
    @SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ServiceDescriptionFactory.class.getName());


    /**
     * @param configuration
     * @param paths
     * @param canonicalName
     * @return the service description
     * @throws ConfigurationException
     */
    public ServiceDescription getServiceDescription(
            Document configuration,
            List<eu.planets_project.services.datatypes.MigrationPath> paths, String canonicalName)
            throws ConfigurationException {


        NodeList topLevelNodes = configuration
                .getElementsByTagName(Constants.SERVICE_DESCRIPTION)
                .item(0)
                .getChildNodes();

        String title = null, description = null, version = null, creator = null,
                publisher = null, identifier = null,
                instructions = null, furtherinfo = null, logo = null;

        Tool tool = null;

        for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
            final Node currentNode = topLevelNodes.item(nodeIndex);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE){
                if (currentNode.getNodeName().equals(Constants.TITLE)){
                    title = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.DESCRIPTION)){
                    description = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.TOOL)){
                    tool = parseTool(currentNode);
                }else if(currentNode.getNodeName().equals(Constants.VERSION)){
                    version = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.CREATOR)){
                    creator = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.PUBLISHER)){
                    publisher = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.IDENTIFIER)){
                    identifier = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.INSTRUCTIONS)){
                    instructions = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.FURTHERINFO)){
                    furtherinfo = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.LOGO)){
                    logo = currentNode.getTextContent().trim();
                }
            }
        }
        if(title == null){
            throw new ConfigurationException("title not set in configfile");
        }
        if(creator == null){
            throw new ConfigurationException("creator not set in configfile");
        }
        ServiceDescription.Builder builder =
                new ServiceDescription.Builder(title,
                                               "eu.planets_project.ifr.services.migrate.Migrate");
        builder.author(creator);
        builder.classname(canonicalName);
        builder.description(description);
        builder.identifier(identifier);
        builder.instructions(instructions);
        builder.version(version);
        builder.tool(tool);
        builder.serviceProvider(publisher);
        builder.paths(paths.toArray(new eu.planets_project.services.datatypes.MigrationPath[paths.size()]));

        if(furtherinfo != null){
            try {
                builder.furtherInfo(new URI(furtherinfo));
            } catch (URISyntaxException e) {
                throw new ConfigurationException("furtherInfo not set to valid value", e);
            }
        }
        if(logo != null){
            try {
                builder.logo(new URI(logo));
            } catch (URISyntaxException e) {
                throw new ConfigurationException("logo not set to valid value", e);
            }
        }

        return builder.build();

    }

    private Tool parseTool (Node tool) throws ConfigurationException {
        NodeList topLevelNodes = tool.getChildNodes();
        String description = null, version = null, identifier = null, name = null, homepage = null;

        for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
            final Node currentNode = topLevelNodes.item(nodeIndex);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE){
                if(currentNode.getNodeName().equals(Constants.DESCRIPTION)){
                    description = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.VERSION)){
                    version = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.IDENTIFIER)){
                    identifier = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.NAME)){
                    name = currentNode.getTextContent().trim();
                }else if(currentNode.getNodeName().equals(Constants.HOMEPAGE)){
                    homepage = currentNode.getTextContent().trim();
                }

            }


        }
        URL homepageURL = null;
        URI identifierURI = null;
        if(homepage != null){
            try {
                homepageURL = new URL(homepage);
            } catch (MalformedURLException e) {
                throw new ConfigurationException("Homepage not set to valid value", e);
            }
        }
        if(identifier != null){
            try {
                identifierURI = new URI(identifier);
            } catch (URISyntaxException e) {
                throw new ConfigurationException("identifier not set to valid value", e);
            }
        }

        Tool t = new Tool(identifierURI, name, version, description, homepageURL);
        return t;
    }
}
