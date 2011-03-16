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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import uk.gov.nationalarchives.pronom.FileFormatType;
import uk.gov.nationalarchives.pronom.SignatureFileType;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
class DroidFormatRegistry  {
    
    private static Logger log = Logger.getLogger(DroidFormatRegistry.class.getName());

    private static final String INFO_PRONOM = "pronom/";
    
    SignatureFileType sigFile;
    
    int numFormats = 0;
    
    /**
     * Constructor to set up the look-up tables.
     */
    public DroidFormatRegistry() {
        // Get the list of file formats
        this.sigFile = SigFileUtils.getLatestSigFile().getFFSignatureFile();
        this.numFormats = this.sigFile.getFileFormatCollection().getFileFormat().size();

    }
    
    /**
     * 
     * @param PUID The PUID string
     * @return A URI representation of the given PUID
     */
    static URI PUIDtoURI( String PUID ) {
        URI puidURI = null;
        try {
            // Opaque URL constructed using a Scheme Specific Part:
            puidURI = new URI("info", INFO_PRONOM + PUID, null );
        } catch (URISyntaxException e) {
            log.severe("Exception while constructing type URI: "+e);
            puidURI = null;
        }
        return puidURI;
    }
    
    /**
     * 
     * @param puri A planets URI
     * @return The raw PUID
     */
    static String URItoPUID( URI puri ) {
        if( puri == null ) return null;
        return puri.getSchemeSpecificPart().substring(INFO_PRONOM.length());
    }
    
    /**
     * 
     * @param ff The droid file format
     * @return A Planets format created from the given droid format
     */
    private MutableFormat fillFormatFromPRONOM( FileFormatType ff ) {
        if( ff == null ) return null;
        
        // Store the unique id and the description
        MutableFormat fmt  = new MutableFormat( PUIDtoURI(ff.getPUID()) );
        try {
            fmt.setRegistryUrl( new URL("http://www.nationalarchives.gov.uk/PRONOM/Format/proFormatSearch.aspx?status=detailReport&id=" + ff.getID()) );
        } catch (MalformedURLException e) {
            fmt.setRegistryUrl(null);
        }
        fmt.setSummary( ff.getName() );
        fmt.setVersion( ff.getVersion() );
        
        // Store mime-type:
        if( ff.getMIMEType() != null && ! "".equals(ff.getMIMEType()) ) {
            HashSet<String> mimes = new HashSet<String>();
            mimes.add(ff.getMIMEType());
            fmt.setMimeTypes(mimes);
        }
        
        // Store extensions:
        QName FileFormatTypeExtension_QNAME = new QName("http://www.nationalarchives.gov.uk/pronom/SignatureFile", "Extension");
        HashSet<String> exts = new HashSet<String>();
        for( JAXBElement<? extends Serializable> el :  ff.getInternalSignatureIDOrExtensionOrHasPriorityOverFileFormatID() ) {
        	if( el.getName().equals( FileFormatTypeExtension_QNAME ) ) {
        		String extension = (String)el.getValue();
        		exts.add(extension);
        	}
        }
        //for( int j = 0; j < ff.getNumExtensions(); j++ ) 
        //    exts.add(ff.getExtension(j));
        fmt.setExtensions(exts);
        
        // Return
        return fmt;
    }
    
    /**
     * 
     * @return the formats in a Set
     */
    public Set<MutableFormat> getFormats() {
       HashSet<MutableFormat> fmts = new HashSet<MutableFormat>();
       for( int i = 0; i < this.sigFile.getFileFormatCollection().getFileFormat().size(); i++ ) {
           fmts.add( fillFormatFromPRONOM(this.sigFile.getFileFormatCollection().getFileFormat().get(i)) );
       }
       return fmts;
    }
    
}
