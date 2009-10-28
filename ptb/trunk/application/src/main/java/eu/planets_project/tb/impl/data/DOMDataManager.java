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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.soap.SOAPException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author AnJackson
 *
 */
public class DOMDataManager implements DigitalObjectManager {
    private static PlanetsLogger log = PlanetsLogger.getLogger(DOMDataManager.class);

    private DataManagerLocal dataManager = null;
    
    /**
     * @param dataManager
     */
    public DOMDataManager(DataManagerLocal dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#getQueryTypes()
     */
    public List<Class<? extends Query>> getQueryTypes() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#isWritable(java.net.URI)
     */
    public boolean isWritable(URI pdURI) {
        return false;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI)
     */
    public List<URI> list(URI pdURI) {
        
        // Otherwise, list from the appropriate DR:
        if( dataManager == null ) return null;
        
        // return the listing.
        URI[] uris = null;
        try {
            uris = dataManager.list(pdURI);
        } catch (SOAPException e) {
            e.printStackTrace();
            return null;
        }
        if( uris == null ) return null;
        return new ArrayList<URI>( Arrays.asList(uris) );    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
     */
    public DigitalObject retrieve(URI pdURI)
            throws DigitalObjectNotFoundException {
        
        DigitalObject.Builder dob = null;
        try {
            dob = new DigitalObject.Builder( Content.byValue( dataManager.retrieveBinary(pdURI)) );
        } catch (SOAPException e1) {
            e1.printStackTrace();
            log.error("Could not retrieve the binary for " + pdURI);
            throw new DigitalObjectNotFoundException( "Could not retrieve the binary for " + pdURI );
        }
        dob.permanentUri( pdURI );
        dob.title( pdURI.getPath().substring( pdURI.getPath().lastIndexOf('/')+1) );
        return dob.build();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#store(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    public void store(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException {
        throw new DigitalObjectNotStoredException("Could not store the digital object at " + pdURI);
    }

}
