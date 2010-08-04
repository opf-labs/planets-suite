/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.services.characterisation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.Droid;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.services.characterisation.FormatIdentification.FormatIdentificationResult;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * defines a wrapper for DROID.
 * 
 * @author Michael Kraxner
 *
 */
public class DROIDIntegration implements FormatIdentificationService{
    /**
     * 
     */
    private static final long serialVersionUID = 1942525396278664042L;

    private Droid droid = null;
    
    private static DROIDIntegration me;
    
    private static final Log log = PlatoLogger
    .getLogger(DROIDIntegration.class);
    
    static {
        try {
            me = new DROIDIntegration();
        } catch (Exception e) {
            me = null;
            log.error("Could not create an instance of DROIDIntegration: ", e);
        }
    }

    public static DROIDIntegration getInstance() {
        return me;
    }
    /**
     * Creates an instance of DROID and initializes it with a signature file.
     * (The file is expected within this package at "data/droid/DROID_SignatureFile.xml")
     * Throws an exception if the file is not found or invalid.
     *  
     * @throws Exception
     */
    private DROIDIntegration() throws Exception{
        if (droid == null) {
           droid = new Droid();
        }
        if ((droid.getSignatureFileVersion() == null) || ("".equals(droid.getSignatureFileVersion()))) {
           URL sigFile = this.getClass().getClassLoader().getResource("data/droid/DROID_SignatureFile.xml");
               droid.readSignatureFile(sigFile);
        }
        
    }

    /**
     * Tries to identify the given <param>data</param> and <param>filename</param>.
     * - throws an exception if it is not possible to create a temporary file.
     * 
     * @param filename
     * @param data
     * @return {@link IdentificationFile}
     * @throws Exception
     */
    public IdentificationFile identify(String filename, byte[] data) throws Exception {
        String filebody = filename;
        String suffix = "";
        int bodyEnd = filename.lastIndexOf(".");
        if (bodyEnd >= 0) {
            filebody = filename.substring(0, bodyEnd);
            suffix = filename.substring(bodyEnd);
        }
        File tempFile = File.createTempFile(filebody + System.nanoTime(), suffix);
        tempFile.deleteOnExit();
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(tempFile));
        out.write(data);
        out.close();
        
        return droid.identify(tempFile.getCanonicalPath());
    }
        
    /**
     * tries to identify the given <param>data</param> and <param>filename</param>.
     * - Returns the first specific hit found by DROID as a FormatInfo-object.
     * - If there are no hits, or only tentative ones, it returns <code>null</code>.
     * 
     * @param filename
     * @param data
     * @return {@link FormatInfo}
     * @throws Exception
     */
    public FormatInfo getMostAppropriateFormat(String filename, byte[] data) throws Exception {
        IdentificationFile ident = identify(filename, data);
        if (ident == null)
            return null;
        if (ident.getNumHits() == 0)
            return null;
        FileFormatHit hit;
        FileFormatHit found = null;

        for (int i = 0; i < ident.getNumHits(); i++) {
            hit = ident.getHit(i);
            log.debug("Hit nr: " + i + " = " + hit.getFileFormatName() + " ," + hit.getFileFormatVersion() + ", hint: " + hit.getHitWarning());
            if ((hit.isSpecific() && found == null))
                found = hit;
        }
        /*
         * there was no specific hit, maybe
         */
        if (found == null)
            return null;
        
        FormatInfo info = new FormatInfo();
        info.setName(found.getFileFormatName());
        info.setPuid(found.getFileFormatPUID());
        info.setVersion(found.getFileFormatVersion());
        info.setMimeType(found.getMimeType());
        /*
         * choose first file extension as default 
         */
        if (found.getFileFormat().getNumExtensions() > 0)
            info.setDefaultExtension(found.getFileFormat().getExtension(0));

        return info;
    }
    
    /**
     * @see #getMostAppropriateFormat(String, byte[])
     */
    public FormatInfo detectFormat(byte[] data, String filename)throws Exception {
        return getMostAppropriateFormat(filename, data);
    }
    
    public FormatIdentification identify(String filepath) {
        
        IdentificationFile ident = droid.identify(filepath);
        
        FormatIdentification result = handelDROIDOutput(ident);
        
        return result;
    }
    
    /**
     * @see FormatIdentificationService#identifyFormat(byte[], String)
     */
    public FormatIdentification identifyFormat(byte[] data, String filename)throws Exception {
        
        IdentificationFile ident = identify(filename, data);
        
        FormatIdentification result = handelDROIDOutput(ident);
        
        return result;
    }
    private FormatIdentification handelDROIDOutput(IdentificationFile ident) {
        
        FormatIdentification result = new FormatIdentification();
        if (ident == null) {
            result.setResult(FormatIdentificationResult.ERROR);
            result.setInfo("Identification failed");
        }
        if (ident.getClassification() == AnalysisController.FILE_CLASSIFICATION_ERROR) {
            result.setResult(FormatIdentificationResult.ERROR);
            result.setInfo(ident.getWarning());
        } else if ((ident.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOHIT) ||
                (ident.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED)){
            result.setResult(FormatIdentificationResult.NOHIT);
            result.setInfo(ident.getWarning());
        } else if (ident.getClassification() == AnalysisController.FILE_CLASSIFICATION_POSITIVE) {
            result.setResult(FormatIdentificationResult.POSITIVE);
            result.setInfo(ident.getWarning());
            addHits(ident, result.getFormatHits());
        } else if (ident.getClassification() == AnalysisController.FILE_CLASSIFICATION_TENTATIVE) {
            result.setResult(FormatIdentificationResult.TENTATIVE);
            result.setInfo(ident.getWarning());
            addHits(ident, result.getFormatHits());
        }
        
        return result;
    }

    private void addHits(IdentificationFile ident, List<FormatHit> allHits) {
        for (int i = 0; i < ident.getNumHits(); i++) {
            FileFormatHit ffHit = ident.getHit(i);
            FormatHit hit = new FormatHit();
            
            hit.setHitWarning(ffHit.getHitWarning());
            hit.setSpecific(ffHit.isSpecific());
            
            
            hit.getFormat().setName(ffHit.getFileFormatName());
            hit.getFormat().setPuid(ffHit.getFileFormatPUID());
            hit.getFormat().setVersion(ffHit.getFileFormatVersion());
            hit.getFormat().setMimeType(ffHit.getMimeType());
            /*
             * choose first file extension as default 
             */
            if (ffHit.getFileFormat().getNumExtensions() > 0)
                hit.getFormat().setDefaultExtension(ffHit.getFileFormat().getExtension(0));
            
            allHits.add(hit);
        }
    }
}

