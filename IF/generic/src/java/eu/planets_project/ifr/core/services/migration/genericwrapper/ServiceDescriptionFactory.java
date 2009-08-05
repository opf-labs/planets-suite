package eu.planets_project.ifr.core.services.migration.genericwrapper;

import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.ConfigurationException;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.utils.PlanetsLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: pko
 * Date: Aug 4, 2009
 * Time: 1:30:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDescriptionFactory {
    private PlanetsLogger log = PlanetsLogger
            .getLogger(ServiceDescriptionFactory.class);



    public ServiceDescription getServiceDescription(
            Document configuration,
            List<eu.planets_project.services.datatypes.MigrationPath> paths)
            throws ConfigurationException {


        NodeList topLevelNodes = configuration
                .getElementsByTagName("serviceDescription")
                .item(0)
                .getChildNodes();

        String title = null, description = null, version = null, creator = null,
                publisher = null, identifier = null,
                instructions = null, furtherinfo = null, logo = null,
                classname = null;
        Tool tool = null;

        for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
            final Node currentNode = topLevelNodes.item(nodeIndex);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE){
                if (currentNode.getNodeName().equals("title")){
                    title = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("description")){
                    description = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("tool")){
                    tool = parseTool(currentNode);
                }else if(currentNode.getNodeName().equals("version")){
                    version = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("creator")){
                    creator = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("publisher")){
                    publisher = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("identifier")){
                    identifier = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("instructions")){
                    instructions = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("furtherinfo")){
                    furtherinfo = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("logo")){
                    logo = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("classname")){
                    classname = currentNode.getTextContent();
                }
            }
        }
        if(title == null){
            throw new ConfigurationException("title not set in configfile");
        }
        if(classname == null){
            throw new ConfigurationException("classname not set in configfile");
        }
        if(creator == null){
            throw new ConfigurationException("creator not set in configfile");
        }
        ServiceDescription.Builder builder =
                new ServiceDescription.Builder(title,
                                               "eu.planets_project.ifr.services.migrate.Migrate");
        builder.author(creator);
        builder.classname(classname);
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
                if(currentNode.getNodeName().equals("description")){
                    description = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("version")){
                    version = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("identifier")){
                    identifier = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("name")){
                    name = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("homepage")){
                    homepage = currentNode.getTextContent();
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
