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
 * Tessella/NPD/4305
 * PRONOM 4
 *
 * $Id: FileFormatHit.java,v 1.4 2006/03/13 15:15:25 linb Exp $
 * 
 * $Log: FileFormatHit.java,v $
 * Revision 1.4  2006/03/13 15:15:25  linb
 * Changed copyright holder from Crown Copyright to The National Archives.
 * Added reference to licence.txt
 * Changed dates to 2005-2006
 *
 * Revision 1.3  2006/02/08 08:56:35  linb
 * - Added header comments
 *
 *
 * *$History: FileFormatHit.java $
 *
 * *****************  Version 4  *****************
 * User: Walm         Date: 5/04/05    Time: 18:08
 * Updated in $/PRONOM4/FFIT_SOURCE
 * review headers
 *
 */

package uk.gov.nationalarchives.droid;

import uk.gov.nationalarchives.droid.signatureFile.FileFormat;
import uk.gov.nationalarchives.droid.xmlReader.SimpleElement;

/**
 * holds the description of a hit (format identification) on a file
 *
 * @author Martin Waller
 * @version 4.0.0
 */
public class FileFormatHit extends SimpleElement {
    String myHitWarning = "";
    int myHitType;
    FileFormat myHitFileFormat;

    /**
     * Creates a new blank instance of fileFormatHit
     *
     * @param theFileFormat  The file format which has been identified
     * @param theType        The type of hit i.e. Positive/tentative
     * @param theSpecificity Flag is set to true for Positive specific hits
     * @param theWarning     A warning associated with the hit
     */
    public FileFormatHit(FileFormat theFileFormat, int theType, boolean theSpecificity, String theWarning) {
        myHitFileFormat = theFileFormat;
        if (theType == AnalysisController.HIT_TYPE_POSITIVE_GENERIC_OR_SPECIFIC) {
            if (theSpecificity) {
                myHitType = AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC;
            } else {
                myHitType = AnalysisController.HIT_TYPE_POSITIVE_GENERIC;
            }
        } else {
            myHitType = theType;
        }
        this.setIdentificationWarning(theWarning);
    }

    public FileFormatHit() {
    }

    /**
     * Updates the warning message for a hit
     * <p/>
     * Used by XML reader for IdentificationFile/FileFormatHit/IdentificationWarning element
     *
     * @param theWarning A warning associated with the hit
     */
    public void setIdentificationWarning(String theWarning) {
        myHitWarning = theWarning;
    }


    /**
     * get the fileFormat for the hit
     */
    public FileFormat getFileFormat() {
        return myHitFileFormat;
    }

    /**
     * get the name of the fileFormat of this hit
     */
    public String getFileFormatName() {
        return myHitFileFormat.getName();
    }

    /**
     * get the version of the fileFormat of this hit
     */
    public String getFileFormatVersion() {
        return myHitFileFormat.getVersion();
    }

    /**
     * Get the mime type
     *
     * @return
     */
    public String getMimeType() {
        return myHitFileFormat.getMimeType();
    }

    /**
     * get the PUID of the fileFormat of this hit
     */
    public String getFileFormatPUID() {
        return myHitFileFormat.getPUID();
    }

    /**
     * get the code of the hit type
     */
    public int getHitType() {
        return myHitType;
    }

    /**
     * get the name of the hit type
     */
    public String getHitTypeVerbose() {
        String theHitType = "";
        if (myHitType == AnalysisController.HIT_TYPE_POSITIVE_GENERIC) {
            theHitType = AnalysisController.HIT_TYPE_POSITIVE_GENERIC_TEXT;
        } else if (myHitType == AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC) {
            theHitType = AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC_TEXT;
        } else if (myHitType == AnalysisController.HIT_TYPE_TENTATIVE) {
            theHitType = AnalysisController.HIT_TYPE_TENTATIVE_TEXT;
        } else if (myHitType == AnalysisController.HIT_TYPE_POSITIVE_GENERIC_OR_SPECIFIC) {
            theHitType = AnalysisController.HIT_TYPE_POSITIVE_GENERIC_OR_SPECIFIC_TEXT;
        }
        return theHitType;
    }

    /**
     * get any warning associated with the hit
     */
    public String getHitWarning() {
        return myHitWarning;
    }

    /**
     * For positive hits, this returns true if hit is Specific
     * or returns false if hit is Generic.
     * Meaningless for Tentative hits. (though returns false)
     */
    public boolean isSpecific() {
        if (myHitType == AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Populates the details of the IdentificationFile when it is read in from XML file
     *
     * @param theName  Name of the attribute read in
     * @param theValue Value of the attribute read in
     */
    public void setAttributeValue(String theName, String theValue) {
        if (theName.equals("HitStatus")) {
            this.setStatus(theValue);
        } else if (theName.equals("FormatName")) {
            this.setName(theValue);
        } else if (theName.equals("FormatVersion")) {
            this.setVersion(theValue);
        } else if (theName.equals("FormatPUID")) {
            this.setPUID(theValue);
        } else if (theName.equals("HitWarning")) {
            this.setIdentificationWarning(theValue);
        } else {
            messageDisplay.unknownAttributeWarning(theName, this.getElementName());
        }
    }

    /**
     * Set hit status.  Used by XML reader for IdentificationFile/FileFormatHit/Status element
     */
    public void setStatus(String value) {
        //String value = element.getText();
        if (value.equals(AnalysisController.HIT_TYPE_POSITIVE_GENERIC_TEXT)) {
            myHitType = AnalysisController.HIT_TYPE_POSITIVE_GENERIC;
        } else if (value.equals(AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC_TEXT)) {
            myHitType = AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC;
        } else if (value.equals(AnalysisController.HIT_TYPE_TENTATIVE_TEXT)) {
            myHitType = AnalysisController.HIT_TYPE_TENTATIVE;
        } else if (value.equals(AnalysisController.HIT_TYPE_POSITIVE_GENERIC_OR_SPECIFIC_TEXT)) {
            myHitType = AnalysisController.HIT_TYPE_POSITIVE_GENERIC_OR_SPECIFIC;
        } else {
            messageDisplay.generalWarning("Unknown hit status listed: " + value);
        }
    }

    /**
     * Set hit format name.  Used by XML reader for IdentificationFile/FileFormatHit/Name element
     */
    public void setName(String value) {
        //if necessary, this creates a new dummy File format
        if (myHitFileFormat == null) {
            myHitFileFormat = new FileFormat();
        }
        myHitFileFormat.setAttributeValue("Name", value);
    }

    /**
     * Set hit format version.  Used by XML reader for IdentificationFile/FileFormatHit/Version element
     */
    public void setVersion(String value) {
        if (myHitFileFormat == null) {
            myHitFileFormat = new FileFormat();
        }
        myHitFileFormat.setAttributeValue("Version", value);
    }

    /**
     * Set hit format PUID.  Used by XML reader for IdentificationFile/FileFormatHit/PUID element
     */
    public void setPUID(String value) {
        if (myHitFileFormat == null) {
            myHitFileFormat = new FileFormat();
        }
        myHitFileFormat.setAttributeValue("PUID", value);
    }
    
    /**
     * Set hit format MIME type.  Used by XML reader for IdentificationFile/FileFormatHit/PUID element
     */
    public void setMimeType(String value) {
    	 if (myHitFileFormat == null) {
             myHitFileFormat = new FileFormat();
         }
         myHitFileFormat.setAttributeValue("MIMEType", value);
    }


}
