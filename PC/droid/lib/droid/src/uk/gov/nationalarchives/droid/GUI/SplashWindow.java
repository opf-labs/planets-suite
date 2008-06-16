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
 * $History: SplashWindow.java $
 * 
 * *****************  Version 2  *****************
 * User: Mals         Date: 14/04/05   Time: 16:20
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Fixed getting splash image by URL
 * 
 * *****************  Version 1  *****************
 * User: Walm         Date: 6/04/05    Time: 16:38
 * Created in $/PRONOM4/FFIT_SOURCE/GUI
 * Add a spash screen to the application
 *
 * Created on 06 April 2005, 11:32
 */

package uk.gov.nationalarchives.droid.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * Class containing the splash window to be launched at start of application
 *
 * @author walm
 */
public class SplashWindow extends JWindow {


    /**
     * Creates a new instance of SplashWindow
     *
     * @param theImageFile URL Address for the splash screen image
     * @param f            Frame within which to draw splash screen
     */
    public SplashWindow(String theImageFile, Frame f) {

        super(f);

        //set cursor to hourglass
        java.awt.Cursor hourglassCursor = new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);

        //Define the splash screen
        JLabel l = new JLabel(new ImageIcon(getClass().getResource(theImageFile)));

        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width / 2 - (labelSize.width / 2),
                screenSize.height / 2 - (labelSize.height / 2));

        //add a mouse listener to allow for the splash screen to disappear when clicked on
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                endSplash();
            }
        });

        //display splash screen
        setVisible(true);

    }

    /**
     * Hide the splash screen
     */
    public void endSplash() {
        setVisible(false);
        dispose();
    }

}


