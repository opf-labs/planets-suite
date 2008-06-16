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
 *
 * V1.R0.M0     16-Mar-2005 S.Malik     Created
 *$History: AboutDialog.java $   
 * 
 * *****************  Version 12  *****************
 * User: Walm         Date: 6/06/05    Time: 16:51
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Make sure hand cursor comes up when mouse hovers over link (fix for
 * JIRA bug PRON-9)
 * 
 * *****************  Version 11  *****************
 * User: Mals         Date: 3/05/05    Time: 15:00
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/CL/CSC/2005MAY03/13:00:06
 * Changed to layout described in above email
 * 
 * *****************  Version 10  *****************
 * User: Mals         Date: 20/04/05   Time: 15:45
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Opens in central position to application window.
 * 
 * *****************  Version 9  *****************
 * User: Mals         Date: 18/04/05   Time: 16:46
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR18/09:51:03
 * 10.Menu bar changes as in document.  (All dialog boxes should have the
 * same title as the menu item or button which opens them) 
 * +Title changed to About DROID
 * 
 * *****************  Version 8  *****************
 * User: Mals         Date: 15/04/05   Time: 17:09
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * More white space around National Archives logo
 * 
 * *****************  Version 7  *****************
 * User: Mals         Date: 15/04/05   Time: 16:57
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Changed About Box background to white
 * +Logos double click to website
 * 
 * *****************  Version 6  *****************
 * User: Mals         Date: 15/04/05   Time: 10:31
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Resized window to fit text
 * 
 * *****************  Version 5  *****************
 * User: Mals         Date: 14/04/05   Time: 12:19
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR13/17:33:48
 * Based on format detailed in above email
 * 
 * *****************  Version 4  *****************
 * User: Mals         Date: 13/04/05   Time: 14:32
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Changed from netbeans absolute layout to Null Layout
 * 
 * *****************  Version 3  *****************
 * User: Mals         Date: 7/04/05    Time: 14:02
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Generate mnemonics code in NetBeans 3.6 turned off , so openide library
 * not needed
 * 
 * *****************  Version 2  *****************
 * User: Mals         Date: 31/03/05   Time: 12:19
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Added more information to the dialog:
 * +Application title, version , signature file version , developed by ,
 * more info url 
 * 
 * *****************  Version 1  *****************
 * User: Mals         Date: 16/03/05   Time: 12:24
 * Created in $/PRONOM4/FFIT_SOURCE/GUI
 * Created About Box Dialog. Needs more information. Just Displays
 * Tessella Logo and OK button. 
 */

package uk.gov.nationalarchives.droid.GUI;

/**
 * Dialog to show about box
 *
 * @author Shahzad Malik
 * @version V2.R1.M0, 02-Apr-2007
 */
public class AboutDialog extends javax.swing.JDialog {

    /**
     * Dialog object to show *
     */
    private static AboutDialog dialog;

    /**
     * National Archive PRONOM website address
     */
    private final String URL_TNA_PRONOM = "www.nationalarchives.gov.uk/pronom/";
    /**
     * Tessella's website address
     */
    private final String URL_TESSELLA = "http://www.tessella.com";

    /**
     * Creates new form AboutDialog
     *
     * @param applicationTitle     Title of the application
     * @param applicationVersion   The application's version number
     * @param signatureFileVersion The signature file version number
     */
    private AboutDialog(java.awt.Frame parent, boolean modal, String applicationTitle,
                        String applicationVersion,
                        String signatureFileVersion) {
        super(parent, modal);
        initComponents();
        setAboutValues(applicationTitle, applicationVersion, signatureFileVersion);
        this.setLocationRelativeTo(parent);

        pack();
    }

    /**
     * Set the values on the form
     *
     * @param applicationTitle     Title of the application
     * @param applicationVersion   The application's version number
     * @param signatureFileVersion The signature file version number
     */
    private void setAboutValues(String applicationTitle,
                                String applicationVersion,
                                String signatureFileVersion) {


        jTextFieldMoreInfo.setText(URL_TNA_PRONOM);
        jTextFieldVersion.setText(applicationVersion);
        jTextFieldSigFileVersion.setText(signatureFileVersion);
    }

    /**
     * Creates and shows a options dialog
     *
     * @param frameComp            Component dialog is owned by
     * @param applicationTitle     Title of the application
     * @param applicationVersion   The application's version number
     * @param signatureFileVersion The signature file version number
     */
    public static void showDialog(java.awt.Component frameComp, String applicationTitle,
                                  String applicationVersion,
                                  String signatureFileVersion) {

        java.awt.Frame f = javax.swing.JOptionPane.getFrameForComponent(frameComp);

        dialog = new AboutDialog(f, true, applicationTitle, applicationVersion, signatureFileVersion);

        dialog.setVisible(true);


    }

    /**
     * Launches the PRONOM website in the systems default web browser
     */
    private void launchTNAWebsite() {
        try {
            Browser.openUrl(URL_TNA_PRONOM);
        } catch (java.io.IOException ioe) {
            javax.swing.JOptionPane.showMessageDialog(this, "Cannot launch default browser", "Default browser not found", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Launches Tessella's website in the systems default web browser
     */
    private void launchTessellaWebsite() {
        try {
            Browser.openUrl(URL_TESSELLA);
        } catch (java.io.IOException ioe) {
            javax.swing.JOptionPane.showMessageDialog(this, "Cannot launch default browser", "Default browser not found", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanelAbout = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabelVersion = new javax.swing.JLabel();
        jTextFieldVersion = new javax.swing.JTextField();
        jLabelSigFileVersion = new javax.swing.JLabel();
        jTextFieldSigFileVersion = new javax.swing.JTextField();
        jLabelMoreInfo = new javax.swing.JLabel();
        jTextFieldMoreInfo = new javax.swing.JTextField();
        jLabelTNALogo = new javax.swing.JLabel();
        jLabelCopyright = new javax.swing.JLabel();
        jButtonOK = new javax.swing.JButton();
        jLabelSubTitle = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.CardLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About DROID");
        setResizable(false);
        jPanelAbout.setLayout(null);

        jPanelAbout.setBackground(new java.awt.Color(255, 255, 255));
        jPanelAbout.setPreferredSize(new java.awt.Dimension(510, 285));
        jLabelTitle.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 24));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("DROID");
        jPanelAbout.add(jLabelTitle);
        jLabelTitle.setBounds(150, 20, 190, 24);

        jLabelVersion.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabelVersion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelVersion.setLabelFor(jTextFieldVersion);
        jLabelVersion.setText("Version");
        jPanelAbout.add(jLabelVersion);
        jLabelVersion.setBounds(200, 80, 44, 15);

        jTextFieldVersion.setEditable(false);
        jTextFieldVersion.setBorder(null);
        jPanelAbout.add(jTextFieldVersion);
        jTextFieldVersion.setBounds(250, 80, 100, 14);

        jLabelSigFileVersion.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabelSigFileVersion.setLabelFor(jTextFieldSigFileVersion);
        jLabelSigFileVersion.setText("Signature File Version");
        jPanelAbout.add(jLabelSigFileVersion);
        jLabelSigFileVersion.setBounds(160, 110, 122, 15);

        jTextFieldSigFileVersion.setEditable(false);
        jTextFieldSigFileVersion.setBorder(null);
        jPanelAbout.add(jTextFieldSigFileVersion);
        jTextFieldSigFileVersion.setBounds(290, 110, 70, 14);

        jLabelMoreInfo.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabelMoreInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMoreInfo.setText("Developed by The National Archives as part of the PRONOM Technical Registry service");
        jPanelAbout.add(jLabelMoreInfo);
        jLabelMoreInfo.setBounds(10, 140, 500, 20);

        jTextFieldMoreInfo.setEditable(false);
        jTextFieldMoreInfo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldMoreInfo.setText("www.nationalarchives.gov.uk/pronom/");
        jTextFieldMoreInfo.setBorder(null);
        jTextFieldMoreInfo.setPreferredSize(new java.awt.Dimension(240, 20));
        jTextFieldMoreInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldMoreInfoMouseClicked(evt);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTextFieldMoreInfoMouseEntered(evt);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextFieldMoreInfoMouseExited(evt);
            }
        });

        jPanelAbout.add(jTextFieldMoreInfo);
        jTextFieldMoreInfo.setBounds(130, 170, 240, 20);

        jLabelTNALogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTNALogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/tna_logo.gif")));
        jLabelTNALogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelTNALogoMouseClicked(evt);
            }
        });

        jPanelAbout.add(jLabelTNALogo);
        jLabelTNALogo.setBounds(10, 10, 120, 60);

        jLabelCopyright.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabelCopyright.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCopyright.setText("\u00a9 The National Archives 2005-2008");
        jPanelAbout.add(jLabelCopyright);
        jLabelCopyright.setBounds(100, 200, 300, 15);

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        jButtonOK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonOKMouseEntered(evt);
            }
        });

        jPanelAbout.add(jButtonOK);
        jButtonOK.setBounds(220, 230, 60, 23);

        jLabelSubTitle.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabelSubTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSubTitle.setText("Digital Record Object Identification");
        jPanelAbout.add(jLabelSubTitle);
        jLabelSubTitle.setBounds(150, 50, 200, 15);

        getContentPane().add(jPanelAbout, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOKMouseEntered
        jTextFieldMoreInfo.setForeground(java.awt.Color.BLACK);
        this.setCursor(null);
    }//GEN-LAST:event_jButtonOKMouseEntered

    //EVENT HANDLERS
    //==============

    /**
     * Launches Pronom website on double click
     *
     * @param evt mouse event
     */
    private void jLabelTNALogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelTNALogoMouseClicked
        if (evt.getClickCount() == 2) {
            launchTNAWebsite();
        }
    }//GEN-LAST:event_jLabelTNALogoMouseClicked

    /**
     * Launches Tessella's website on double click
     * @param evt mouse event 
     */
    /**
     * Return label text to black on mouse away
     *
     * @param evt mouse event
     */
    private void jTextFieldMoreInfoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldMoreInfoMouseExited
        jTextFieldMoreInfo.setForeground(java.awt.Color.BLACK);
        jTextFieldMoreInfo.setCursor(null);
    }//GEN-LAST:event_jTextFieldMoreInfoMouseExited

    /**
     * Label turned to blue on mouse over
     *
     * @param evt mouse event
     */
    private void jTextFieldMoreInfoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldMoreInfoMouseEntered
        jTextFieldMoreInfo.setForeground(java.awt.Color.BLUE);
        //java.awt.Cursor handCursor = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
        jTextFieldMoreInfo.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jTextFieldMoreInfoMouseEntered

    /**
     * Opens pronom website in default browser on mouse click
     *
     * @param evt mouse event Object
     */
    private void jTextFieldMoreInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldMoreInfoMouseClicked
        launchTNAWebsite();
    }//GEN-LAST:event_jTextFieldMoreInfoMouseClicked

    /**
     * Closes about box on button click
     *
     * @param evt ActionEvent Object
     */
    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        AboutDialog.dialog.setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabelCopyright;
    private javax.swing.JLabel jLabelMoreInfo;
    private javax.swing.JLabel jLabelSigFileVersion;
    private javax.swing.JLabel jLabelSubTitle;
    private javax.swing.JLabel jLabelTNALogo;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelVersion;
    private javax.swing.JPanel jPanelAbout;
    private javax.swing.JTextField jTextFieldMoreInfo;
    private javax.swing.JTextField jTextFieldSigFileVersion;
    private javax.swing.JTextField jTextFieldVersion;
    // End of variables declaration//GEN-END:variables
    
}
