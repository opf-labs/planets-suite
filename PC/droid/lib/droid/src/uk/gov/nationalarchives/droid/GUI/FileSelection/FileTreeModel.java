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
 * Project Title:   File Format Identification Tool
 * Project Identifier: uk
 *
 *
 * Version      Date        Author      Short Description
 *
 * V1.R0.M0     08-Mar-2005 S.Malik     Created
 *
 *
 * Based on example from  of a simple static TreeModel. It contains a
    (java.io.File) directory structure.
    (C) 2001 Christian Kaufhold (ch-kaufhold@gmx.de)
 *
 * File history. 
 * Version  Date         Author      Short Description
 * V1.R0.M0 08-Mar-2005  S.Malik     Created
 *
 *$History: FileTreeModel.java $
 * 
 * *****************  Version 7  *****************
 * User: Mals         Date: 14/07/05   Time: 13:57
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * Sorts files alphabetically by name
 * 
 * *****************  Version 6  *****************
 * User: Mals         Date: 9/05/05    Time: 13:27
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * +The file system roots are found by a FileSystemView object, instead of
 * a list of roots in the constructors parameters
 * +On Windows, root is "Desktop" and on Max/Unix/Linux root is "/" 
 * +Fixed bug: on Windows 2000  - No disk error message appears 
 * 
 * *****************  Version 5  *****************
 * User: Walm         Date: 6/04/05    Time: 17:49
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * deal with missing floppies or CD drives
 * 
 * *****************  Version 4  *****************
 * User: Walm         Date: 6/04/05    Time: 16:37
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * Speed up file selection dialog by not sorting file list alphabetically
 * (the list already comes in this order, so this is not required).
 * However, still lists directories before files.
 * 
 * *****************  Version 3  *****************
 * User: Mals         Date: 21/03/05   Time: 11:10
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/FileSelection
 * Sort files and folders, first by folders then by file names
 * Corrected Copyright header
 */

package uk.gov.nationalarchives.droid.GUI.FileSelection;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * A TreeModel implementation to populate a JTree component with the system file structure
 * This object requirs a java.util.List of java.io.File objects which represent file system
 * roots or drives. The model will display these roots at the top level of the tree.
 * <p/>
 * Example of use:
 * //Where rootFolders is a java.util.List of java.IO.File objects
 * jFileTree = new javax.swing.JTree(new FileTreeModel(rootFolders));
 * <p/>
 * Based on code from FileTreeModel1.java in the FileIDPrototype
 *
 * @author Shahzad Malik
 * @version V1.R0.M0 , 08-Mar-2005
 */
class FileTreeModel
        implements TreeModel, Serializable, Cloneable {
    protected EventListenerList listeners;
    private static final Object LEAF = new Serializable() {
    };
    private Map map;
    private File root;
    private java.util.List roots;

    /**
     * Comparator object to use to sort file list by folderes then files
     */
    private java.util.Comparator fileCompare;


    /**
     * Constructor
     */
    public FileTreeModel() {

        javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();

        java.io.File f = new java.io.File("File System");
        fileCompare = new FileTreeModel.fileComparator();

        this.root = f;

        //Get the file system roots
        java.util.List altRoots = java.util.Arrays.asList(fsv.getRoots());
        this.roots = altRoots;

        this.map = new HashMap();


        this.listeners = new EventListenerList();


    }


    /**
     * Gets the root Element of the Tree
     *
     * @return root Element (java.io.File object in practice)
     */
    public Object getRoot() {
        return root;
    }

    /**
     * Determines if specified object in tree is a Leaf
     * If object is a drive , returns false always
     *
     * @return true - when object is leaf in tree , false - otherwise
     */
    public boolean isLeaf(Object node) {
        File f = (File) node;
        javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();

        if (fsv.isDrive(f)) {
            return false;
        }

        return map.get(node) == LEAF;
    }

    /**
     * Calculates number of child nodes under a specified node
     *
     * @param node the node to be interogated
     * @return child count of node
     */
    public int getChildCount(Object node) {


        List children = children(node);

        if (children == null)
            return 0;

        return children.size();
    }

    /**
     * Finds the child object of a given node in the tree
     *
     * @param parent Node to find child
     * @param index  index position of node
     * @return child object required
     */
    public Object getChild(Object parent, int index) {
        return children(parent).get(index);
    }

    /**
     * Finds the Index of child of a given parent
     *
     * @param parent Node to find child index
     * @return index of child element for given parent
     * @index index of node
     */
    public int getIndexOfChild(Object parent, Object child) {
        return children(parent).indexOf(child);
    }

    /*
    *Builds a list of children nodes for a given node
    *@param     node   node to find children
    *@return   List of children for given node
    */
    protected List children(Object node) {
        File f = (File) node;


        Object value = map.get(f);

        if (value == LEAF) {
            return null;
        }

        List children = (List) value;


        if (children == null) {
            File[] c = null;

            javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();

            c = f.listFiles();


            if (f.getPath() == "File System") {
                c = new File[roots.size()];
                for (int counter = 0; counter < roots.size(); counter++) {
                    c[counter] = (java.io.File) roots.get(counter);
                    //System.out.println(c[counter].getPath()) ; 
                }

            }


            if (c != null) {
                children = new ArrayList(c.length);

                //add directories to file list
                for (int len = c.length, i = 0; i < len; i++) {
                    if ((!fsv.isFloppyDrive(c[i])) || (fsv.isTraversable(c[i]).booleanValue())) {
                        if (c[i].isDirectory()) {
                            children.add(c[i]);
                        } else if (fsv.isFloppyDrive(c[i])) {
                            children.add(c[i]);
                            map.put(c[i], LEAF);
                        } else if (fsv.isDrive(c[i])) {
                            children.add(c[i]);
                            map.put(c[i], LEAF);
                        }
                    }
                }
                //add folders to file list
                for (int len = c.length, i = 0; i < len; i++) {
                    if ((!fsv.isDrive(c[i]))) {
                        if (!c[i].isDirectory()) {
                            children.add(c[i]);
                            map.put(c[i], LEAF);
                        }
                    }
                }
            } else
                children = new ArrayList(0);

            map.put(f, children);
        }

        sortFileList(children);
        return children;
    }

    /**
     * Method needed to implement TreeModel interface
     * Does nothing
     */
    public void valueForPathChanged(TreePath path, Object value) {
    }

    /**
     * Adds Tree Model Listener
     *
     * @param l TreeModelListner object
     */
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(TreeModelListener.class, l);
    }

    /**
     * Removes Tree model listner
     *
     * @param l TreeModelListner object
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(TreeModelListener.class, l);
    }

    /**
     * Creates a clone of this object
     *
     * @return a clone of this object
     */
    public Object clone() {
        try {
            FileTreeModel clone = (FileTreeModel) super.clone();

            clone.listeners = new EventListenerList();

            clone.map = new HashMap(map);

            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Sorts a java.util.List of java.io.File objects by folders then by files
     *
     * @param files list of files to sort
     */
    private void sortFileList(java.util.List files) {

        //Sort the list with comparator object 
        java.util.Collections.sort(files, fileCompare);


    }

    /**
     * Implementation of a java.util.Comparator to sort a list of
     * java.io.File objects by folders then by files.
     */
    private class fileComparator implements java.util.Comparator {

        /**
         * Decides which object should precede the other , out of two given objects
         *
         * @param o1 object1 (java.io.File)
         * @param o2 object2 (jav.io.File)
         * @returns -1 if object1 precedes object 2 , 1 if object2 precedes object1 , or 0 if objects are equal
         */
        public int compare(Object o1, Object o2) {

            java.io.File f1 = (java.io.File) o1;
            java.io.File f2 = (java.io.File) o2;

            //Find out if objects are folders , or files 
            boolean isf1Dir = f1.isDirectory();
            boolean isf2Dir = f2.isDirectory();

            if (isf1Dir && !isf2Dir) {
                //If file1 is a folder and file 2 isn't
                //file 1 is before file 2
                return -1;
            } else if (!isf1Dir && isf2Dir) {
                //If file1 isn't a folder and file2 is
                //file 2 is before file 1
                return 1;
            } else {
                //otherwise compare filenames ignoring case
                return f1.getPath().compareToIgnoreCase(f2.getPath());
            }


        }
    }


}