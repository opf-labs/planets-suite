package eu.planets_project.services.utils.cli;

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
 */
public class CliMigrationPaths {

    private List<CliMigrationPath> paths;

    protected final static File defaultPath = new File(".");

    /**
     * @param resourceName The XML file containing the path configuration
     * @return The paths defined in the XML file
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws URISyntaxException
     */
    // TODO: Clean up this exception-mess. Something like "InitialisationException" or "ConfigurationErrorException" should cover all these exceptions.
    public static CliMigrationPaths initialiseFromFile(String resourceName) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {


        InputStream instream;


        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resourceURL = loader.getResource(resourceName);
        if (new File(defaultPath, resourceName).isFile()) {
            instream = new FileInputStream(new File(defaultPath, resourceName));
        } else if (new File(resourceName).isFile()) {
            instream = new FileInputStream(new File(resourceName));
        } else if (resourceURL != null) {
            instream = resourceURL.openStream();
        } else {
            String msg = String.format("Could not locate resource %s",
                    resourceName);
            throw new FileNotFoundException(msg);
        }


        CliMigrationPaths paths = new CliMigrationPaths();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(instream);


        Element fileformats = doc.getDocumentElement();
        if (fileformats != null){
            NodeList children = fileformats.getChildNodes();
            for (int i = 0; i<children.getLength();i++){
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE){
                    if (child.getNodeName().equals("path")){
                        CliMigrationPath pathdef = decodePathNode(child);
                        paths.add(pathdef);
                    }                
                }
            }
        }
        FileUtils.close(instream);
        return paths;
    }

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

    
    private CliMigrationPaths() {
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
