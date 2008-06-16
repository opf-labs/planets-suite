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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * Allow table rows to open PUID resolution web page
 */
public class HyperLinkTable extends javax.swing.JTable {

    private String baseURL;
    private String browserPath;
    private TableCellRenderer cellEditor = null;

    private static String WIN_ID = "Windows";

    /**
     * Create the Hyper link Table
     *
     * @param baseURL The URL for PUID resolution
     */
    public HyperLinkTable(String baseURL) {
        this.setSize((int) this.getPreferredSize().getWidth(), (int) this.getPreferredSize().getHeight());
        this.setOpaque(true);
        this.baseURL = baseURL;
        addMouseMotionListener(new CellMouseMotionListner());
        addMouseListener(new CellMouseListener());
    }


    /**
     * Set the base url;
     *
     * @param baseURL
     */
    public void setbaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Set the path to the web browser
     */
    public void setBrowserPath(String path) {
        this.browserPath = path;
    }


    public TableCellRenderer getCellRenderer() {
        if (cellEditor == null) {
            cellEditor = new CellRenderer();
        }
        return cellEditor;
    }


    /**
     * open  URL on windows
     *
     * @param url
     */
    public void displayWindowsURL(String url) {

        String WIN_PATH = "rundll32";
        String WIN_FLAG = "url.dll,FileProtocolHandler";
        String cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
        }
    }

    /**
     * Are we running on windows
     *
     * @return boolean
     */
    public boolean isWindowsPlatform() {
        String os = System.getProperty("os.name");
        return os != null && os.startsWith(WIN_ID);
    }


    /**
     * open URL on UNIX
     * Only tested under LINUX
     *
     * @param url
     */
    public void displayUnixURL(String url) {
        try {
            Process p = Runtime.getRuntime().exec(browserPath + " " + url);
        } catch (IOException e) {
        }
    }


    private class CellMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            Component c = getComponentAt(e.getPoint());
            if (c instanceof HyperLinkTable) {
                HyperLinkTable table = (HyperLinkTable) c;
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 0) {
                    String url = baseURL + getModel().getValueAt(row, col).toString();
                    if (isWindowsPlatform()) {
                        displayWindowsURL(url);
                    } else {
                        displayUnixURL(url);
                    }
                }
            }


        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    private class CellMouseMotionListner implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            Component c = getComponentAt(e.getPoint());
            if (c instanceof HyperLinkTable) {
                HyperLinkTable table = (HyperLinkTable) c;
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 0) {
                    setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }


    private class CellRenderer extends JLinkLabel implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setText(table.getModel().getValueAt(row, column).toString());
            this.setToolTipText(baseURL + getText());
            return this;
        }

    }


}
