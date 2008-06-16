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
 * Project Number:  Tessella/NPD/4950
 *                  
 */

package uk.gov.nationalarchives.droid.stats.GUI;

import uk.gov.nationalarchives.droid.AnalysisController;

/**
 * Dialog to select files and/or folders , for the File Identification tool to analayse
 * Features:
 * Multiple file and folder selections are permitted
 * If the recursive check box is selected then all files under the folder are added
 * <p/>
 * @author zeip
 */
public class StatsDialog extends javax.swing.JDialog {

    /**
     * Add files button pressed
     */
    public static final int ACTION_ADD = 1;
    /**
     * Cancel button pressed
     */
    public static final int ACTION_CANCEL = 0;

    /**
     * Dialog object to show
     */
    private static StatsDialog dialog;

    /**
     * Collection of string paths to return
     */
    private static java.util.Set filePaths;

    /**
     * Object containing filepaths selected and whether recursive was selected
     */
    private static StatsReturnParameter returnValues;
    
    
    /**
     * Creates new form StatsDialog
     * @param parent parent GUI frame
     * @param modal whether this should block interaction with the main window
     * @return StatsDialog
     */
    private StatsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        filePaths = new java.util.HashSet();

        returnValues = new StatsReturnParameter(null, false, StatsDialog.ACTION_CANCEL);

        loadComponents(); //Intialise the form components
        makeDisposeOnEscapeKey(this);
        setMnemonics();

        //Set "Add files" as the default button
        this.getRootPane().setDefaultButton(jButtonAdd);

        //Set the file tree renderer
        jFileTree.setCellRenderer(new StatsRenderer());

        //Expand the root node (E.G "Desktop" on windows, or "/" on Mac/Unix 
        jFileTree.expandRow(0);

        //This window should open centralised on the main application window.
        this.setLocationRelativeTo(parent);
        
    }

    /**
     * Utility method to cancel dialog when escape button pressed
     *
     * @param rootPane dialog to close on escape key press
     */
    private static void makeDisposeOnEscapeKey(final javax.swing.RootPaneContainer rootPane) {
        //Create action to dispose
        javax.swing.Action action = new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent arg0) {
                ((java.awt.Window) rootPane).dispose();

            }
        };

        //Get keystroke for escape key
        javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        //Add escape key to action map for dialog
        rootPane.getRootPane().getActionMap().put(action, action);
        rootPane.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, action);
    }

    /**
     * Creates and shows a File select dialog
     *
     * @param frameComp Component dialog is owned by
     * @param ac Pointer to the AnalysisController in use
     * @return Collection of File paths as strings (Contains no duplicates)
     */
    public static StatsReturnParameter showDialog(java.awt.Component frameComp) {

        java.awt.Frame f = javax.swing.JOptionPane.getFrameForComponent(frameComp);

        dialog = new StatsDialog(f, true);

        dialog.setVisible(true);

        return returnValues;

    }

    /**
     * Set the mnemonics (Keyboard shortcuts) for menu items on this form
     * Can only be called after initComponents()
     */
    private void setMnemonics() {
        jCheckBoxRecursive.setMnemonic('I');
        jButtonAdd.setMnemonic('A');
        jButtonCancel.setMnemonic('C');
    }


    /**
     * Adds selected files to return list for dialog
     */
    private void addFiles() {

        //If no file paths selected just exit 
        if (jFileTree.getSelectionPaths() == null) {
            return;
        }

        //Set the cursor to waiting
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        //Get the paths selected in the jTree object
        javax.swing.tree.TreePath[] paths = jFileTree.getSelectionPaths();

        //Intialise a new array the size of selected paths
        String[] filePaths = new String[paths.length];

        //For every path selected in the jTree add a file path to the new array
        for (int n = 0; n < paths.length; n++) {
            filePaths[n] = paths[n].getLastPathComponent().toString();
        }

        //Intialise the return object
        returnValues = new StatsReturnParameter(filePaths, jCheckBoxRecursive.isSelected(), StatsDialog.ACTION_ADD);

        //Set cursor to default 
        this.setCursor(null);
    }

    /**
     * Adds selected files to return list for dialog
     *
     * @param filePath  Path of file or folder to add to the return file list
     * @param fileList  Set which the file or folder contents should be added to
     * @param recursive Should all recursive sub files and folders under folder be added
     */
    private void addFiles(String filepath, java.util.Set fileList, boolean recursive) {
        //Create a file object from the given file path
        java.io.File f = new java.io.File(filepath);

        //Decide whether path is for a folder or file 
        if (f.isDirectory()) {
            //if a folder add files in the folder

            //List files and folders in folder
            java.io.File[] folderFiles = f.listFiles();

            //Iterate through list
            for (int m = 0; m < folderFiles.length; m++) {
                //Deccide whats a file and whats a folder
                if (folderFiles[m].isFile()) {
                    //If a file add the file
                    fileList.add(folderFiles[m].getPath());

                } else if (folderFiles[m].isDirectory() && recursive) {
                    //if a folder AND we are recursivley adding folders add this folder 
                    addFiles(folderFiles[m].getPath(), fileList, recursive);
                }
            }

        } else {
            //if a file , then add it
            fileList.add(f.getPath());

        }
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void loadComponents() {
        jPanelFilesFolders = new javax.swing.JPanel();
        jScrollPaneFilesFolders = new javax.swing.JScrollPane();

        jFileTree = new javax.swing.JTree();
        jPanelActions = new javax.swing.JPanel();
        jCheckBoxRecursive = new javax.swing.JCheckBox();
        jButtonAdd = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate profile");
        setName("StatsDialog");
        jPanelFilesFolders.setLayout(new java.awt.GridLayout(1, 0));

        jPanelFilesFolders.setPreferredSize(new java.awt.Dimension(400, 322));
        jScrollPaneFilesFolders.setBorder(null);
        jScrollPaneFilesFolders.setViewportBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)), new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102))));

        jFileTree = new javax.swing.JTree(new FileTreeModel());
        //Don't show the root node
        jFileTree.setRootVisible(false);
        jScrollPaneFilesFolders.setViewportView(jFileTree);

        jPanelFilesFolders.add(jScrollPaneFilesFolders);

        getContentPane().add(jPanelFilesFolders, java.awt.BorderLayout.CENTER);

        jPanelActions.setPreferredSize(new java.awt.Dimension(250, 40));
        jCheckBoxRecursive.setText("Include sub-folders");
        jPanelActions.add(jCheckBoxRecursive);

        jButtonAdd.setText("Analyse Files");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jPanelActions.add(jButtonAdd);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jPanelActions.add(jButtonCancel);

        getContentPane().add(jPanelActions, java.awt.BorderLayout.SOUTH);

        pack();
    }


    /**
     * Hides the dialog
     *
     * @param evt Event object
     */
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed

        StatsDialog.dialog.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * Adds file paths  to return list on button click
     *
     * @param evt Event object
     */
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        addFiles();

        StatsDialog.dialog.setVisible(false);
    }//GEN-LAST:event_jButtonAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JCheckBox jCheckBoxRecursive;
    private javax.swing.JTree jFileTree;
    private javax.swing.JPanel jPanelActions;
    private javax.swing.JPanel jPanelFilesFolders;
    private javax.swing.JScrollPane jScrollPaneFilesFolders;
    // End of variables declaration//GEN-END:variables

}
