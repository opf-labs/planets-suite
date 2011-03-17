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
import java.net.URL;
import java.util.Set;

/**
 * This is the Planets 'Preservation Object' 'Format' description entity. It is
 * based on the DROID FileFormat entity, but simplified. Other format registries
 * could be supported too. 
 * <p/>
 * Note that DROID also provides: 'has priority over'
 * (good for doing PP) and 'signatures' (good for doing PC) but as this is meant
 * to be a simple, interoperable type identifier system, this extra information
 * (which is more oriented to specific use contexts) is not included here.
 * <p/>
 * Types are identified by URIs. As we are currently based on PRONOM, these URIs
 * will be PRONOM info:pronom/fmt/XX URIs. By using these URIs, we retain the
 * ability to add further registries in the future without changing any of this
 * code.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 */
public interface Format {
    /**
     * Different types of format URIs supported by Planets services.
     * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
     */
    public static enum UriType { MIME, PRONOM, EXTENSION, ANY, UNKNOWN }
    
    /** @return The file extensions corresponding to this format. */
    Set<String> getExtensions();
    /** @return The alias URIs this format. */
    Set<URI> getAliases();
    /** @return The MIME types corresponding to this format. */
    Set<String> getMimeTypes();
    /** @return The actual URI representation of this format. */
    URI getUri();
    /** @return A URL providing further information about this format in the context of its original registry. */
    URL getRegistryUrl();
    /** @return A version string for this format. */
    String getVersion();
    /** @return A textual summary of this format. */
    String getSummary();
}
