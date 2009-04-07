/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.services.datatypes;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A richer tool description, so that the system can work in the absence of a tool registry.
 *
 * @author  <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement(name = "tool")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Tool {
    // A namespace for the tool elements:
    private static final String TOOLS_NS = "http://www.planets-project.eu/services/tools";

    /** An identifier for this tool, should be resolvable via a tool registry. */
    @XmlElement(namespace = TOOLS_NS)
    URI identifier;

    /** The tool name */
    @XmlElement(namespace = TOOLS_NS)
    String name;
    
    /** The tool version */
    @XmlElement(namespace = TOOLS_NS)
    String version;
    
    /** A tool description */
    @XmlElement(namespace = TOOLS_NS)
    String description;
    
    /** A link to the tool homepage. */
    @XmlElement(namespace = TOOLS_NS)
    URL homepage;

    /** For JAXB */
    protected Tool() { }

    /* -------------------------------------------- */

    /**
     * @param identifier An identifier that resolves this tool via a tool registry.  Set to NULL if not in a registry.
     * @param name The name of this tool.
     * @param version The version number of this tool.  NULL if unknown.
     */
    public Tool(URI identifier, String name, String version) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.version = version;
    }
    
    /* -------------------------------------------- */

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /* -------------------------------------------- */

    /**
     * @return the homepage
     */
    public URL getHomepage() {
        return homepage;
    }

    /**
     * @param homepage the homepage to set
     */
    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }

    /* -------------------------------------------- */
    
    /**
     * @return the identifier
     */
    public URI getIdentifier() {
        return identifier;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }    

    /* -------------------------------------------- */
    
    /**
     * If you specify a tool registry URI, then any information stored in the registry will be used to fill in the gaps.
     * 
     * If there is no registry URI, then you should specify at least a name and version.
     * 
     * All params can be null if unknown. 
     * 
     * @param identifier An identifier resolvable via a tool registry.
     * @param name The tool name.
     * @param version The tool version.
     * @param description An optional description of the tool.
     * @param homepageUrl An optional pointer to the homepage of the tool.
     * @return
     */
    public static Tool create( URI identifier, String name, String version, String description, String homepageUrl ) {
        Tool tool = new Tool( identifier, name, version );
        tool.setDescription(description);
        try {
            tool.setHomepage( new URL( homepageUrl ) );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            tool.setHomepage(null);
        }
        return tool;
    }
}
