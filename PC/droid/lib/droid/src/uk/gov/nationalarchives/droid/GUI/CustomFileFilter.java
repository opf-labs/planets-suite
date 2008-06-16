/*
 * © The National Archives 2005-2006.  All rights reserved.
 * See Licence.txt for full licence details.
 *
 * Developed by:
 * Tessella Support Services plc
 * 3 Vineyard Chambers
 * Abingdon, OX14 3PX
 * United Kingdom
 * email: info@tessella.com
 * web:   www.tessella.com
 *
 * Project Number:  Tessella/NPD/4305
 *                  
 *
 * Project Title:   File Format Identification Tool
 * Project Identifier: uk
 *
 * Version      Date        Author      Short Description
 * V1.R0.M0     31-Mar-2005 S.Malik     Created
 *
 * $History: CustomFileFilter.java $   
 * 
 * *****************  Version 1  *****************
 * User: Mals         Date: 31/03/05   Time: 10:32
 * Created in $/PRONOM4/FFIT_SOURCE/GUI
 * File Filter to restrict a javax.swing.JFileChooser to an accepted file
 * type 
 * Based on extracted nested class in FileIdentificationPane.java 
 * 
 */

package uk.gov.nationalarchives.droid.GUI;

/**
 * File Filter to restrict a javax.swing.JFileChooser to an accepted file type
 * Example of use:
 * <CODE>
 * fc.addChoosableFileFilter(new uk.GUI.CustomFileFilter("xml","XML document(*.xml)"));
 * </CODE>
 *
 * @author Shahzad Malik
 * @version V1.R0.M.0, 31-Mar-2005
 */
public class CustomFileFilter extends javax.swing.filechooser.FileFilter {

    /**
     * File extension for accepted files
     */
    private String fileExtension;
    /**
     * The description associated with accepted files
     */
    private String description;

    /**
     * Constructor
     *
     * @param fileExtension File extension for accepted files
     * @param description   The description associated with accepted files
     */
    public CustomFileFilter(String fileExtension, String description) {
        this.fileExtension = fileExtension;
        this.description = description;
    }

    /**
     * Determines whether a file or folder is accepted by the filter
     *
     * @param f File object in question
     * @return true if file is of given file extension or a folder , false otherwise
     */
    public boolean accept(java.io.File f) {
        if (f.isDirectory()) {
            return true;
        } else {
            String extension = getExtension(f);

            if (extension.equalsIgnoreCase(fileExtension)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * The description associated with accepted files
     *
     * @return description associated with accepted files
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the selected file extension for this filter
     */
    public String getFileExtension(){
       return this.fileExtension; 
    }

    /*
    * Get the extension of a file.
     *@param f file object to determine extension
     *@return the file extension of the file if one exists, returns empty string if no extension exists
    */
    private String getExtension(java.io.File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
