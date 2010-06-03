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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.ontology.OntologyHandlerImpl;
import eu.planets_project.tb.impl.model.ontology.util.OntoPropertyUtil;

/**
 * @author AnJackson
 *
 */
public class XcdlCorpusDigitalObjectManagerImpl extends
        FilesystemDigitalObjectManagerImpl {
    /** The Log instance */
    private static Log _log = LogFactory.getLog(XcdlCorpusDigitalObjectManagerImpl.class);
    
    /** Sub-folder name where the binaries will be stored. */
    private static String BIN_DIRNAME = "files";
    
    /** Sub-folder name where the XCDL files will be stored alongside the files directory, if any. */
    private static String XCDL_DIRNAME = "docs";

    /**
     * @param config
     * @throws IllegalArgumentException
     */
    public XcdlCorpusDigitalObjectManagerImpl(Configuration config)
            throws IllegalArgumentException {
        super(config);
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#getQueryTypes()
     */
    @Override
    public List<Class<? extends Query>> getQueryTypes() {
        return super.getQueryTypes();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#isWritable(java.net.URI)
     */
    @Override
    public boolean isWritable(URI pdURI) {
        // The Corpora should not be modified directly at present.
        return false;
//      return super.isWritable(pdURI);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    @Override
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        return super.list(pdURI, q);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#list(java.net.URI)
     */
    @Override
    public List<URI> list(URI pdURI) {
        // Allow the super-class to handle basic listing and validity checking.
        // Actual implementation is modified by overriding the listFileLocation method.
        return super.list(pdURI);
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#listFileLocation(java.net.URI, java.io.File)
     */
    @Override
    protected ArrayList<URI> listFileLocation(URI pdURI, File searchRoot)
            throws URISyntaxException {
        _log.debug("Examining file(s) at "+searchRoot);
        if( searchRoot.listFiles() == null ) {
            return null;
        }
        boolean objects = false;
        for ( File item : searchRoot.listFiles() ) {
            if( item.isDirectory() && BIN_DIRNAME.equals(item.getName()) )
                objects = true;
        }
        // If no 'files' sub-directory, just pass to the superclass.
        if( objects == false )
            return super.listFileLocation(pdURI, searchRoot);
        
        // Look through 'files' and return each
        ArrayList<URI> retVal = new ArrayList<URI>();
        File binDir = new File( searchRoot, BIN_DIRNAME );
        for ( File item : binDir.listFiles() ) {
            retVal.add( FilesystemDigitalObjectManagerImpl.createNewPathUri(pdURI, pdURI.getPath() + item.getName() ));
        }
        
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#retrieve(java.net.URI)
     */
    @Override
    public DigitalObject retrieve(URI pdURI)
            throws DigitalObjectNotFoundException {
        
        _log.debug("Retrieving DigitalObject: "+pdURI );
//        new Exception().printStackTrace();
        
        DigitalObject.Builder dob;
        try {
            PDURI parsedURI = new PDURI(pdURI);
            String leafname = parsedURI.getDataRegistryPath().substring( parsedURI.getDataRegistryPath().lastIndexOf("/")+1);
            String prefix = parsedURI.getDataRegistryPath().substring( 0, parsedURI.getDataRegistryPath().lastIndexOf("/"));
            
            // Handle the binary:
            String binPath = this._root.getCanonicalPath() + File.separator + prefix + 
                File.separator + BIN_DIRNAME + File.separator + leafname;            
            File binf = new File(binPath);
            if( ! binf.exists() || ! binf.canRead() || ! binf.isFile() ) {
                throw new DigitalObjectNotFoundException("The DigitalObject was not found!");
            }
            DigitalObjectContent c = Content.byReference( binf );
            dob = new DigitalObject.Builder( c );
            dob.title( binf.getName() );

            
            // Patch in properties from 'docs' if present.
            String xcdlPath = this._root.getCanonicalPath() + File.separator + prefix + 
                File.separator + XCDL_DIRNAME + File.separator + leafname.substring( 0, leafname.lastIndexOf(".") ) + ".xcdl";
            File xcdlf = new File(xcdlPath);
            _log.debug("Looking for "+xcdlf.getCanonicalPath());
            /* Attach XCDL as metadata, with a identifying URI */
            if( xcdlf.exists() && xcdlf.canRead() && xcdlf.isFile() ) {
                    String xcdls = FileUtils.readFileToString(xcdlf);
                    dob.metadata( new Metadata( XCDL_MD_URI, xcdls ) );
            }
            
            // Then look for 'source' and patch it in?

            // And return:
            return dob.build();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
     */
    @Override
    public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
            throws DigitalObjectNotStoredException {
        // The Corpora should not be modified directly at present.
//        super.store(pdURI, digitalObject);
        throw new DigitalObjectNotStoredException("Storing objects to this corpora is not currently supported!");
    }
    

    /************************************************************************************************/
    /************************************************************************************************/
    /************************************************************************************************/
    
    /** The metadata type uri for XCDL. */
    public static URI XCDL_MD_URI = URI.create("planets:dob/md/xcdl");

    /**
     * Helper function to test if a DOB has an XCDL MD packet.
     * @param dob
     * @return
     */
    public static boolean hasXcdlPropertied( DigitalObject dob ) {
        if( dob != null && dob.getMetadata() != null ) {
            for( Metadata md : dob.getMetadata() ) {
                if( XCDL_MD_URI.equals( md.getType() ) ) return true;
            }
        }
        return false;
    }

    /**
     * Helper function to parse the XCDL attached to a DOB.
     * @param dob
     * @return The extracted List of Property items.
     */
    public static List<Property> getXcdlProperties( DigitalObject dob ) {
        List<Property> properties = new ArrayList<Property>();
        if( dob.getMetadata() != null ) {
            for( Metadata md : dob.getMetadata() ) {
                if( XCDL_MD_URI.equals( md.getType() ) ) {
                	md.getContent();
                    //String xcdls = md.getContent();
                    // Read properties from the XCDL:
                    try {
                        // TODO Make this work again!
                        // Old form:
                        // properties = new XcdlParser(xcdls).getProperties();
                        // New form:
                        // TODO Make this cope with recursive properties?
                        // properties = new XcdlParser( new StringReader(xcdls) ).getCharacteriseResult().getProperties();
                    } catch (Exception e ) {
                        _log.error("Failed to read in XCDL properties!");
                        _log.error("Exception: "+e);
                        e.printStackTrace();
                    }
                }
            }
        }
        
        OntologyHandlerImpl onto = OntologyHandlerImpl.getInstance();
        for( Property p : properties ) {
            _log.info( "Property: " + p );
            OntologyProperty op = onto.getProperty(p.getUri().toString());
            if( op != null ) {
                _log.info("P: "+p.getUri()+" OP: "+op.getURI());
                _log.info("P: "+p.getName()+" OP: "+op.getName());
                _log.info("P: "+p.getDescription()+" OP: "+op.getComment());
                _log.info("P: "+p.getType()+" OP: "+op.getType());
                _log.info("P: "+p.getUnit()+" OP: "+op.getUnit());
                _log.info("P= "+p.getValue()+" OP: "+op.getDataType());
                try {
                    MeasurementImpl measurementinfo = OntoPropertyUtil.createMeasurementFromOntologyProperty(op);
                    _log.info("Got MeasurementImpl: " + measurementinfo.getDescription() );
                } catch (Exception e) {
                    _log.error("Could not turn the OntologyProperty into a MeasurementImpl.");
                    e.printStackTrace();
                }
            }
        }
        
        if( properties != null ) {
            _log.info("Got "+properties.size()+" properties...");
        }
        return properties;
    }

}
