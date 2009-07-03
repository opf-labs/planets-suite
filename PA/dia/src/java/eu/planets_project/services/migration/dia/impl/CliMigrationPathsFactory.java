/**
 * 
 */
package eu.planets_project.services.migration.dia.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Factory for construction and initialisation of <code>CliMigrationPaths</code>
 * objects.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class CliMigrationPathsFactory {

    // TODO: We should create a schema for the configuration file and refer to
    // it in this javadoc.
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

        NodeList topLevelNodes = pathConfiguration.getChildNodes().item(0)
                .getChildNodes();
        for (int nodeIndex = 0; nodeIndex < topLevelNodes.getLength(); nodeIndex++) {
            System.out.println(nodeIndex + " : Node name: "
                    + topLevelNodes.item(nodeIndex).getNodeName());
            // for all paths in pathConfiguration
            // create a CliMigrationPath
            // add the path to migrationPaths

        }

        return migrationPaths;
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

    private static String decodeCommandNode(Node command) {
        Node commandtext = command.getFirstChild();
        if (commandtext.getNodeType() == Node.TEXT_NODE) {
            return commandtext.getNodeValue();
        }
        return "";
    }

}
