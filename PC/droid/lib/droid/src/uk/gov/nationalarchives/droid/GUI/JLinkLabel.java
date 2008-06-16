/*
 * The National Archives 2005-2006.  All rights reserved.
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
 */

package uk.gov.nationalarchives.droid.GUI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;


/**
 * Class which extends JLabel
 * to mimic a HTTP hyperlink
 *
 *
 */
public class JLinkLabel extends JLabel {

    final Color COLOR_NORMAL    = Color.BLUE;
    final Color COLOR_HOVER     = COLOR_NORMAL;
    final Color COLOR_ACTIVE    = COLOR_NORMAL;
    final Color COLOR_BG_NORMAL = Color.WHITE;
    final Color COLOR_BG_ACTIVE = Color.WHITE;
    Color mouseOutDefault;


    /**
    *
    * Default constructor
    *
    */
    public JLinkLabel() {
        setForeground(COLOR_NORMAL);
        setBackground(COLOR_BG_NORMAL);
        mouseOutDefault = COLOR_NORMAL;
        this.setSize((int)this.getPreferredSize().getWidth(),(int)this.getPreferredSize().getHeight());
        this.setOpaque(true);

    }

    /**
     * Set text
     *
     * @param text
     */
    public void setText(String text) {
        super.setText(text);
    }

    /**
     * Draw line under text
     *
     * @param g
     */
    public void paint(Graphics g) {
        super.paint(g);
        g.drawLine(2,  getHeight()-1,   (int)getPreferredSize().getWidth()-2, getHeight()-1);
    }
    

}
