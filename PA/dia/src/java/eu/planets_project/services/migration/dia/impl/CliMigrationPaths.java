package eu.planets_project.services.migration.dia.impl;

import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.utils.FileUtils;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Create migration paths from an XML config file.
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class CliMigrationPaths {

    private List<CliMigrationPath> paths;

    /**
     * @param in The input format
     * @param out The output format
     * @return The tool of the path corresponding to the given input and output
     */
    public String findMigrationCommand(URI in, URI out){
        for (CliMigrationPath path: paths){
            if (path.getIn().contains(in) && path.getOut().contains(out)){
                return path.getTool();
            }
        }
        return null; // FIXME! throw exception! Do not return null.
    }

    /**
     * @param sourceFormat
     * @param destinationFormat
     * @return
     */
    public CliMigrationPath findMigrationPath(URI sourceFormat,
	    URI destinationFormat) {
        for (CliMigrationPath path: paths){
            if (path.getIn().contains(sourceFormat) && path.getOut().contains(destinationFormat)){
                return path;
            }
        }
        return null; // FIXME! throw exception! Do not return null.
    }

    /**
     * @return The migration paths as planets paths
     */
    public MigrationPath[] getAsPlanetsPaths(){


        List<MigrationPath> planetspaths = new ArrayList<MigrationPath>();
        for (CliMigrationPath mypath : paths) {
            planetspaths.addAll(MigrationPath.constructPaths(mypath.getIn(),
                    mypath.getOut()));
        }
        return planetspaths.toArray(new MigrationPath[0]);

    }

    
    protected CliMigrationPaths() {
        paths = new ArrayList<CliMigrationPath>();
    }

    private boolean add(CliMigrationPath cliMigrationPath) {
        return paths.add(cliMigrationPath);
    }

    private static CliMigrationPath decodePathNode(Node path) throws URISyntaxException {
        NodeList children = path.getChildNodes();
        Set<URI> froms = null;
        Set<URI> tos = null;
        String command = null;
        for (int i = 0; i<children.getLength();i++){
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE){

                if (child.getNodeName().equals("from")){
                    froms = decodeFromOrToNode(child);
                }
                if (child.getNodeName().equals("to")){
                    tos = decodeFromOrToNode(child);
                }
                if (child.getNodeName().equals("command")){
                    command = decodeCommandNode(child);
                }

            }
        }
        return new CliMigrationPath(froms,tos,command);

    }

    private static Set<URI> decodeFromOrToNode(Node urilist) throws URISyntaxException {
        NodeList children = urilist.getChildNodes();
        Set<URI> uris = new HashSet<URI>();
        for (int i = 0; i<children.getLength();i++){
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE){
                if (child.getNodeName().equals("uri")){
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


    private static String decodeCommandNode(Node command){
        Node commandtext = command.getFirstChild();
        if (commandtext.getNodeType() == Node.TEXT_NODE){
            return commandtext.getNodeValue();
        }
        return "";
    }


    


}
