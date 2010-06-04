/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * A richer tool description, so that the system can work in the absence of a
 * tool registry.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlType(namespace = PlanetsServices.TOOLS_NS)
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class Tool {

    /** An identifier for this tool, should be resolvable via a tool registry. */
    @XmlElement(namespace = PlanetsServices.TOOLS_NS)
    URI identifier;

    /** The tool name. */
    @XmlElement(namespace = PlanetsServices.TOOLS_NS)
    String name;

    /** The tool version. */
    @XmlElement(namespace = PlanetsServices.TOOLS_NS)
    String version;

    /** A tool description. */
    @XmlElement(namespace = PlanetsServices.TOOLS_NS)
    String description;

    /** A link to the tool homepage. */
    @XmlElement(namespace = PlanetsServices.TOOLS_NS)
    URL homepage;

    /** For JAXB. */
    @SuppressWarnings("unused")
    private Tool() {}

    /* -------------------------------------------- */

    /**
     * @param identifier An identifier that resolves this tool via a tool
     *        registry. Set to NULL if not in a registry.
     * @param name The name of this tool.
     * @param version The version number of this tool. NULL if unknown.
     * @param description A tool description
     * @param homepage A link to the tool homepage.
     */
    public Tool(URI identifier, String name, String version,
            String description, URL homepage) {
        this.identifier = identifier;
        this.name = name;
        this.version = version;
        this.description = description;
        this.homepage = homepage;
    }

    /* -------------------------------------------- */

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the homepage
     */
    public URL getHomepage() {
        return homepage;
    }

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
     * If you specify a tool registry URI, then any information stored in the
     * registry will be used to fill in the gaps. If there is no registry URI,
     * then you should specify at least a name and version. All params can be
     * null if unknown.
     * @param identifier An identifier resolvable via a tool registry.
     * @param name The tool name.
     * @param version The tool version.
     * @param description An optional description of the tool.
     * @param homepageUrl An optional pointer to the homepage of the tool.
     * @return A Tool instance for the given values
     */
    public static Tool create(URI identifier, String name, String version,
            String description, String homepageUrl) {
        URL homepage = null;
        try {
            homepage = new URL(homepageUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new Tool(identifier, name, version, description, homepage);
    }
}
