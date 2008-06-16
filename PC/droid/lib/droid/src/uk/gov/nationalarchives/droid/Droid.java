/*
 * © The National Archives 2005-2006.  All rights reserved.
 * See Licence.txt for full licence details.
 *
 * Developed by:
 * Tessella Support Services plc
 * 3 Vineyard Chambers
 * Abingdon, OX14 3PX
 * United Kingdom
 * http://www.tessella.com
 *
 */

package uk.gov.nationalarchives.droid;

import static uk.gov.nationalarchives.droid.binFileReader.AbstractByteReader.newByteReader;

import java.net.URL;

import uk.gov.nationalarchives.droid.binFileReader.ByteReader;


/**
 * Public interface for droid programming API
 */
public class Droid {

    private AnalysisController analysisControl = null;
    private String version = null;

    /**
     * Create the AnalysisController
     * and set the config and signature file.
     *
     * @param configFile
     */
    public Droid(URL configFile) throws Exception {
        analysisControl = new AnalysisController();
        analysisControl.readConfiguration(configFile);
    }
    
    /**
     * No-args constructor. To be used when no config file is required.
     * 
     * @throws Exception
     */
    public Droid() throws Exception {
    	analysisControl = new AnalysisController();
    }
    
    /**
     * Create the AnalysisController
     * and set the config and signature file.
     *
     * @param configFile
     */
    public Droid(URL configFile, URL sigFileURL) throws Exception {
        analysisControl = new AnalysisController();
        analysisControl.readConfiguration(configFile);
    }

    /**
     * Downloads a new signature file using the setting in the DROID config file but does not load it into DROID.
     * 
     * @param fileName
     */
    public void downloadSigFile(String fileName) {
    	analysisControl.downloadwwwSigFile(fileName, false);
    }
    
    /**
     * Read the signature file
     *
     * @param signatureFile
     */
    public void readSignatureFile(URL signatureFile) throws Exception {
        version = analysisControl.readSigFile(signatureFile);
    }

    /**
     * Read the signature file
     *
     * @param signatureFile
     */
    public void readSignatureFile(String signatureFile) throws Exception {
        version = analysisControl.readSigFile(signatureFile);
    }
    
    /**
     * get the signature file version
     *
     * @return
     */
    public String getSignatureFileVersion() {
        return version;
    }

    /**
     * Sets the URL of the signature file webservices
     * 
     * @param sigFileURL
     */
    public void setSigFileURL(String sigFileURL) {
    	analysisControl.setSigFileURL(sigFileURL);
    }
    
    /**
     * identify files using droid
     *
     * @param file   full path to a disk file
     * @return IdentificationFile
     */
    public IdentificationFile identify(String file) {

        IdentificationFile identificationFile = new IdentificationFile(file);
        ByteReader byteReader = null;
        byteReader = newByteReader(identificationFile);
        analysisControl.getSigFile().runFileIdentification(byteReader);
    
        return identificationFile;
    }

    /**
     * Determines whether Pronom has a newer signature file available. 
     * 
     * @param currentVersion
     * @return
     */
    public boolean isNewerSigFileAvailable(int currentVersion) {
    	return analysisControl.isNewerSigFileAvailable(currentVersion);
    }
}
