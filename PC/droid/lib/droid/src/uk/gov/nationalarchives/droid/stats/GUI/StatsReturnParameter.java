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
 *
 * Version      Date        Author      Short Description
 *
 * V1.R0.M0     11-Mar-2005 S.Malik     Created

 *$History: StatsReturnParameter.java $ 
 * 
 * *****************  Version 2  *****************
 * User: Walm         Date: 5/04/05    Time: 16:50
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * review headers
 * 
 * *****************  Version 1  *****************
 * User: Mals         Date: 11/03/05   Time: 15:16
 * Created in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * FileSelectDialog returns this object to class that called it
 */

package uk.gov.nationalarchives.droid.stats.GUI;

/**
 * FileSelectDialog returns this object to class that called it
 * Contains a collection of folder and file paths selected by user
 * and whether the user chose to select folders recursively.
 *
 * @author Shahzad Malik
 * @version V1.R0.M0, 08-Mar-2005
 */
public class StatsReturnParameter {

    /**
     * Collection of strings which are file or folder paths
     */
    private String[] paths;
    /**
     * Specifies whether all files and folders recursively under path should be added
     */
    private boolean recursive;
    /**
     * Which button was pressed Add (FileSelectDialog.ACTION_ADD)
     * or Cancel (FileSelectDialog.ACTION_CANCEL)
     */
    private int action;


    /**
     * Creates a new instance of StatsReturnParameter
     * 
     * 
     * @param paths     Array of file and/or folderpaths
     * @param recursive whether all files and folders recursively under path should be added
     * @param action    Which button was pressed (FileSelectDialog.ACTION_ADD) or Cancel (FileSelectDialog.ACTION_CANCEL)
     */
    public StatsReturnParameter(String[] paths,
                                     boolean recursive, int action) {

        this.paths = paths;
        this.recursive = recursive;
        this.action = action;
    }

    /**
     * Collection of strings which are file or folder paths
     *
     * @return strings which are file or folder paths
     */
    public String[] getPaths() {
        return paths;
    }

    /**
     * Specifies whether all files and folders recursively under path should be added
     *
     * @return if true - files and folders recursively under path should be added
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * Which button was pressed Add (FileSelectDialog.ACTION_ADD)
     * or Cancel (FileSelectDialog.ACTION_CANCEL)
     *
     * @return FileSelectDialog.ACTION_ADD or FileSelectDialog.ACTION_CANCEL
     */
    public int getAction() {
        return action;
    }
}
