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
/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.techreg.formats.Format.UriType;

/**
 * This is the Planets Format Registry and Resolver.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
class FormatRegistryImpl implements FormatRegistry {
    private static Logger log = Logger.getLogger(FormatRegistryImpl.class.getName());

    /**
     * Main index, 1-2-1 mapping the type URIs to the FileFormat objects.
     */
    private Map<URI, MutableFormat> uriMap = new HashMap<URI, MutableFormat>();

    /**
     * Map a file extension onto one or more type URIs.
     */
    private Map<String, Set<URI>> extMap = new HashMap<String, Set<URI>>();

    /**
     * Map a mime type onto one or more type URIs.
     */
    private Map<String, Set<URI>> mimeMap = new HashMap<String, Set<URI>>();

    /**
     * Constructor loads the format data in and builds the look-up tables.
     */
    public FormatRegistryImpl() {
        // Build up a set of Format objects from known information sources.
        // Currently, this is just DROID.
        DroidFormatRegistry dfr = new DroidFormatRegistry();
        Set<MutableFormat> ffs = dfr.getFormats();
        log.info("File format data loaded.");

        // Set up the hash tables that index this set of formats:
        for (MutableFormat ff : ffs) {
            // log.debug("--------------------------\nGot format "+
            // ff.getSummary());

            // Store the format in a PUID map:
            this.uriMap.put(ff.getUri(), ff);
            // log.debug("Stored under PUID: "+ff.getTypeURI());

            // Store the mime mapping:
            if (ff.getMimeTypes() != null) {
                for (String mimeType : ff.getMimeTypes()) {
                    Set<URI> mimeSet = this.mimeMap.get(mimeType);
                    if (mimeSet == null) {
                        mimeSet = new HashSet<URI>();
                    }
                    mimeSet.add(ff.getUri());
                    if (ff.getAliases() != null) {
                        for (URI furi : ff.getAliases()) {
                            mimeSet.add(furi);
                        }
                    }
                    this.mimeMap.put(mimeType, mimeSet);
                    // log.debug("Referenced under MIME: "+mimeType);
                }
            }
            // Store the extension mapping:
            if (ff.getExtensions() != null) {
                for (String ext : ff.getExtensions()) {
                    Set<URI> extSet = this.extMap.get(ext);
                    if (extSet == null) {
                        extSet = new HashSet<URI>();
                    }
                    extSet.add(ff.getUri());
                    if (ff.getAliases() != null) {
                        for (URI furi : ff.getAliases()) {
                            extSet.add(furi);
                        }
                    }
                    this.extMap.put(ext, extSet);
                    // log.debug("Referenced under extension: "+ext);
                }
            }
        }
        log.info("File format look-up tables complete.");
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getFormatForUri(java.net.URI)
     */
    public Format getFormatForUri(URI puri) {
        if (isMimeUri(puri) || isExtensionUri(puri)) {
            return new MutableFormat(puri);
        } else {
            if( this.uriMap.containsKey(puri ) ) {
                return this.uriMap.get(puri);
            } else {
                // Unknown format:
                MutableFormat fmt = new MutableFormat(puri);
                fmt.setSummary(""+puri);
                return fmt;
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getUrisForExtension(java.lang.String)
     */
    public Set<URI> getUrisForExtension(String ext) {
        return this.extMap.get(ext.toLowerCase());
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getUrisForMimeType(java.lang.String)
     */
    public Set<URI> getUrisForMimeType(String mimetype) {
        return this.mimeMap.get(mimetype);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#search(java.lang.String)
     */
    public List<URI> search(String query) {
        ArrayList<URI> found = new ArrayList<URI>(this
                .getUrisForExtension(query));
        Collections.sort(found);
        return found;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getFormatUriAliases(java.net.URI)
     */
    public List<URI> getFormatUriAliases(URI typeURI) {
        Set<URI> turis = new HashSet<URI>();
        turis.add(typeURI);

        if (isMimeUri(typeURI)) {
            MutableFormat mime = new MutableFormat(typeURI);
            Set<URI> furis = getUrisForMimeType(mime.getMimeTypes().iterator()
                    .next());
            if( furis != null && furis.size() > 0 ) turis.addAll(furis);
        } else if (isExtensionUri(typeURI)) {
            MutableFormat ext = new MutableFormat(typeURI);
            Set<URI> furis = getUrisForExtension(ext.getExtensions().iterator()
                    .next());
            if( furis != null && furis.size() > 0 ) turis.addAll(furis);
        } else {
            // This is a known format, ID, so add it, any aliases, and the ext
            // and mime forms:
            MutableFormat f = this.uriMap.get(typeURI);
            // Aliases:
            for (URI uri : f.getAliases()) {
                turis.add(uri);
            }
            // Ext:
            for (String ext : f.getExtensions()) {
                turis.add(createExtensionUri(ext));
            }
            // Mime:
            for (String mime : f.getMimeTypes()) {
                turis.add(createMimeUri(mime));
            }
        }
        return new ArrayList<URI>(turis);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getFormatAliases(java.net.URI)
     */
    public List<Format> getFormatAliases(URI typeURI) {
        List<Format> fmts = new ArrayList<Format>();
        for (URI furi : getFormatUriAliases(typeURI)) {
            fmts.add(getFormatForUri(furi));
        }
        return fmts;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#puidToUri(java.lang.String)
     */
    public URI puidToUri(final String puid) {
        return DroidFormatRegistry.PUIDtoURI(puid);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#convertUriToPronom(java.net.URI)
     */
    public String convertUriToPronom(final URI uri) {
        return DroidFormatRegistry.URItoPUID(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createExtensionUri(java.lang.String)
     */
    public URI createExtensionUri(String extensionFromFile) {
        return FormatUtils.createExtensionUri(extensionFromFile);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createActionUri(java.lang.String)
     */
    public URI createActionUri(String action) {
        return FormatUtils.createActionUri(action);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isPronomUri(java.net.URI)
     */
    public Boolean isPronomUri(URI uri) {
        return FormatUtils.isPronomUri(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isExtensionUri(java.net.URI)
     */
    public Boolean isExtensionUri(URI uri) {
        return FormatUtils.isExtensionUri(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createMimeUri(java.lang.String)
     */
    public URI createMimeUri(String type) {
        return FormatUtils.createMimeUri(type);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createPronomUri(java.lang.String)
     */
    public URI createPronomUri(String string) {
        return FormatUtils.createPronomUri(string);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getExtensions(java.net.URI)
     */
    public Set<String> getExtensions(URI puidToUri) {
        return getFormatForUri(puidToUri).getExtensions();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getFirstExtension(java.net.URI)
     */
    public String getFirstExtension(URI uri) {
        return FormatUtils.getFirstExtension(uri);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isMimeUri(java.net.URI)
     */
    public Boolean isMimeUri(URI typeURI) {
        return FormatUtils.isMimeUri(typeURI);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#isUriOfType(java.net.URI,
     *      eu.planets_project.ifr.core.techreg.formats.MutableFormat.UriType)
     */
    public Boolean isUriOfType(URI uri, UriType type) {
        switch (type) {
        case MIME:
            return isMimeUri(uri);
        case PRONOM:
            return isPronomUri(uri);
        case EXTENSION:
            return isExtensionUri(uri);
        case ANY:
            return uri.toString().equals(MutableFormat.ANY.toString());
        case UNKNOWN:
            return uri.toString().equals(MutableFormat.UNKNOWN.toString());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createAnyFormatUri()
     */
    public URI createAnyFormatUri() {
        return MutableFormat.ANY;
    }
    
    
    /** 
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createFolderTypeUri()
     */
    public URI createFolderTypeUri() {
    	return MutableFormat.FOLDER;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#createUnknownFormatUri()
     */
    public URI createUnknownFormatUri() {
        return MutableFormat.UNKNOWN;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.techreg.formats.FormatRegistry#getValueFromUri(java.net.URI)
     */
    public String getValueFromUri(URI uri) {
        String marker = null;
        if (isUriOfType(uri, UriType.PRONOM)) {
            marker = "pronom/";
        } else if (isUriOfType(uri, UriType.EXTENSION)) {
            marker = "ext/";
        } else if (isUriOfType(uri, UriType.MIME)) {
            marker = "mime/";
        }
        if (marker != null) {
            return uri.toString().substring(
                    uri.toString().lastIndexOf(marker) + marker.length());
        }
        return null;
    }

}
