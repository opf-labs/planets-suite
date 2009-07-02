/**
 * 
 */
package eu.planets_project.services.migration.dia.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.services.utils.FileUtils;

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
     */
    public CliMigrationPaths getInstance(Document pathConfiguration) {

        CliMigrationPaths paths = new CliMigrationPaths();

//        Element fileformats = pathConfiguration.getDocumentElement();
//        if (fileformats != null){
//            NodeList children = fileformats.getChildNodes();
//            for (int i = 0; i<children.getLength();i++){
//                Node child = children.item(i);
//                if (child.getNodeType() == Node.ELEMENT_NODE){
//                    if (child.getNodeName().equals("path")){
//                        CliMigrationPath pathdef = decodePathNode(child);
//                        paths.add(pathdef);
//                    }                
//                }
//            }
//        }
 
        
        return paths;
    }
}
