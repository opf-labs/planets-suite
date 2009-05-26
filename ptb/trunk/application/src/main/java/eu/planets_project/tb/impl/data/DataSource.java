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
package eu.planets_project.tb.impl.data;

import java.net.URI;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;

public class DataSource {
    private URI uri = null;
    private DigitalObjectManager dom = null;
    

    /**
     * @param uri
     * @param dom
     */
    public DataSource(URI uri, DigitalObjectManager dom) {
        super();
        this.uri = uri;
        this.dom = dom;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return the dom
     */
    public DigitalObjectManager getDom() {
        return dom;
    }
    
    /**
     * @return true if this is a DigitalObjectManager data source.
     */
    public boolean isDigitalObjectManager() {
        return dom != null;
    }
    
    /**
     * @param resourceUri URI of the resource of interest.
     * @return true if this source owns that URI.
     */
    public boolean matchesURI( URI puri ) {
        if( puri == null ) return false;
        puri = puri.normalize();
        if( puri.toString().startsWith(this.getUri().toString())) return true;
        return false;
    }
    
}