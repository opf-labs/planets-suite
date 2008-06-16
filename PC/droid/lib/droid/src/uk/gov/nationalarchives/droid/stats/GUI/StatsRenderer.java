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
 * V1.R0.M0     08-Mar-2005 S.Malik     Created
 *
 *$History: StatsRenderer.java $ 
 * 
 * *****************  Version 7  *****************
 * User: Mals         Date: 9/05/05    Time: 13:23
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * +Altered method of how display names found so "My Computer" and "My
 * Network Places" are displayed correctly
 * 
 * *****************  Version 6  *****************
 * User: Mals         Date: 18/04/05   Time: 12:42
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * Fixed showing floppy and cd rom icons
 * 
 * *****************  Version 5  *****************
 * User: Mals         Date: 18/04/05   Time: 12:10
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * Tessella Ref: NPD/4305/PR/IM/2005APR18/09:51:03
 * 22.The drive letters appear to inconsistently display a drive icon . Is
 * it possible for them all to display them and the drive name (e.g. Local
 * Disk (C:))? 
 * 
 * *****************  Version 4  *****************
 * User: Walm         Date: 6/04/05    Time: 17:49
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * provide icons for floppies and CD drives
 * 
 * *****************  Version 3  *****************
 * User: Walm         Date: 6/04/05    Time: 16:08
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * debug error messages when opening file selector on some XP boxes
 * 
 * *****************  Version 2  *****************
 * User: Walm         Date: 5/04/05    Time: 16:50
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * review headers
 */
package uk.gov.nationalarchives.droid.stats.GUI;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A Tree Cell renderer to display file icon images and file names
 * <p/>
 * Example of use:
 * <p/>
 * jFileTree.setCellRenderer(new StatsRenderer());
 * 
 * 
 * @author Shahzad Malik
 * @version V1.R0.M0 , 08-Mar-2005
 */
class StatsRenderer extends DefaultTreeCellRenderer {

    javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();


    /**
     * Default construct
     */
    public StatsRenderer() {

    }

    /**
     * Returns Tree Cell Renderer Component with File Icon set
     * returns Renderer component
     */
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        Icon fileIcon = getDisplayIcon(value, leaf);
        if (null != fileIcon) {
            setIcon(fileIcon);
        }

        return this;
    }

    /**
     * Returns the display icon for a node in the tree and sets the display name
     * param value   java.io.File object repersenting node in tree
     * param leaf    is the Node a leaf?
     * return Icon object for given node
     */
    protected Icon getDisplayIcon(Object value, boolean leaf) {

        Icon returnIcon = null;
        ;

        java.io.File f = (java.io.File) (value);

        try {

            if (fsv.isFloppyDrive(f)) {
                returnIcon = new ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/floppy_drive.gif"));
            } else if (f.exists()) {

                returnIcon = fsv.getSystemIcon(f);

            } else if (fsv.isDrive(f)) {
                returnIcon = new ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/cd_drive.gif"));
            }


            String displayText = null;  //Holds display text if found


            displayText = fsv.getSystemDisplayName(f);
            //Only set the display text if it exists
            if (displayText.trim().length() > 0) {

                this.setText(displayText);
            }


        } catch (Exception e) {
            //System.out.println(e.toString());
            System.out.println("e");

        }
        return returnIcon;
    }


}
