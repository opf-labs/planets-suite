package eu.planets_project.ifr.core.services.migration.genericwrapper;

import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.MigrationPathConfigException;
import eu.planets_project.ifr.core.services.migration.genericwrapper.exceptions.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

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



    public ServiceDescription getServiceDescription(Document configuration) throws ConfigurationException {


        NodeList topLevelNodes = configuration
                .getElementsByTagName("serviceDescription")
                .item(0)
                .getChildNodes();

        String title = null, description = null, version = null, creator = null, publisher = null, identifier = null,
                instructions = null, furtherinfo = null, logo = null, classname = null;

        for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
            final Node currentNode = topLevelNodes.item(nodeIndex);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE){
                if (currentNode.getNodeName().equals("title")){
                    title = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("description")){
                    description = currentNode.getTextContent();
                }else if(currentNode.getNodeName().equals("tool")){
                                       //TODO: handle tool
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
        ServiceDescription.Builder builder = new ServiceDescription.Builder(title, "Migrate");
        builder.author(creator);
        builder.classname(classname);
        builder.description(description);
        builder.identifier(identifier);
        builder.instructions(instructions);
        builder.version(version);
        builder.serviceProvider(publisher);

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

}