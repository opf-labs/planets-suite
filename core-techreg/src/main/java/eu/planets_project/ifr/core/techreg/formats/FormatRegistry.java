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
package eu.planets_project.ifr.core.techreg.formats;

import java.net.URI;
import java.util.List;
import java.util.Set;

import eu.planets_project.ifr.core.techreg.formats.Format.UriType;


/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
public interface FormatRegistry {
    
    /* Query methods: */

    /**
     * @param query The query
     * @return the List of URIs matching query
     */
    List<URI> search(String query);

    /**
     * @param extension The extension
     * @return the Set of URIs for the passed extension
     */
    Set<URI> getUrisForExtension(String extension);

    /**
     * @param mime The mime type
     * @return the Set of URIs for the passed mimetype
     */
    Set<URI> getUrisForMimeType(String mime);

    /**
     * This class looks up the different Format URIs consistent with the given
     * URI.
     * @param typeUri The URI
     * @return a List of format URIs consistent with the passed URI
     */
    List<URI> getFormatUriAliases(URI typeUri);

    /**
     * @param typeURI The type URI
     * @return All aliases (in other format types) for the given URI
     */
    List<Format> getFormatAliases(URI typeURI);

    /**
     * @param puri The Planets URI (see FormatRegistry)
     * @return A format instance for the given URI
     */
    Format getFormatForUri(URI puri);
    
    /**
     * @param uri The URI to find extensions for
     * @return Extensions corresponding to the given URI
     */
    Set<String> getExtensions(URI uri);

    /**
     * @param uri The URI to find an extension for
     * @return The first extension found corresponding to the given URI
     */
    String getFirstExtension(URI uri);

    /* Planets URI factory methods: */

    /**
     * @param extension The simple file extension
     * @return A URI representing the extension
     */
    URI createExtensionUri(String extension);
    
    /**
     * @param action the action a modify service can perform (e.g.: "repair", "rotate", "crop" ...)
     * @return a URI representing this action
     */
    URI createActionUri(String action);

    /**
     * @param pronom The pronom ID to create URI for, e.g. "fmt/101"
     * @return A URI representing the given pronom ID
     */
    URI createPronomUri(String pronom);

    /**
     * @param mime The mime type to create a URI for
     * @return A URI representing the given mime type
     */
    URI createMimeUri(String mime);

    /**
     * @return A URI representing any format.
     */
    URI createAnyFormatUri();
    
    /**
     * @return a URI representing a folder.
     */
    URI createFolderTypeUri();

    /**
     * @return A URI representing an unknown format.
     */
    URI createUnknownFormatUri();

    /* Planets URI info method: */

    /**
     * @param uri The URI to test
     * @param type The URI type to test for
     * @return True, if the given URI is of the given type
     */
    Boolean isUriOfType(URI uri, UriType type);

    /* Conversion back from Planets URIs: */

    /**
     * @param uri The URI
     * @return The raw value the URI was created with or null
     */
    String getValueFromUri(URI uri);
}
