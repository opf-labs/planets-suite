/*
 * 
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
import javax.swing.Box;
import uk.gov.nationalarchives.droid.stats.StatsLogger;
import uk.gov.nationalarchives.droid.stats.StatsExporterFactory;
import uk.gov.nationalarchives.droid.stats.StatsExportFormat;
import uk.gov.nationalarchives.droid.StatsThread;
import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.GUI.CustomFileFilter;
import java.lang.StringBuffer;
import javax.swing.BorderFactory;
import javax.swing.border.*;

import javax.swing.table.AbstractTableModel;
import java.util.Iterator;

/**
 * Dialog that watches a given StatsThread, permits the user to abort,
 * displays results and permits export to CSV and XML
 *
 * @author zeip
 */
public class StatsResultDialog extends javax.swing.JDialog {

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
    private static StatsResultDialog dialog;

    /**
     * Timer to regularly update stats display during generation 
     */
    private javax.swing.Timer timer;
    
    /**
     * Thread in which StatsLogger is already being used
     */
    private StatsThread statisticsThread;
    
    /**
     * Reference to parent controller
     */
    private AnalysisController analysisControl;
    
    /**
     * File extension for Comma separated value file (CSV) files)
     */
    private static final String CSV_FILE_EXTENSION = "csv";
    /**
     * File description for Comma separated value file (CSV) files)
     */
    private static final String CSV_FILE_DESCRIPTION = "Comma separated value file (*.csv)";
    /**
     * File extension for Comma separated value file (CSV) files)
     */
    private static final String XML_FILE_EXTENSION = "xml";
    /**
     * File description for Comma separated value file (CSV) files)
     */
    private static final String XML_FILE_DESCRIPTION = "XML file (*.xml)";
    /**
     * Message when user is selects an exisiting file when saving or exporting
     */
    private static final String MSG_OVERWRITE = "The specified file exists, overwrite?";
    
    /**
     * Creates new form StatsResultDialog
     * @param parent parent GUI frame
     * @param modal whether this should block interaction with the main window
     * @param thread the calling thread
     * @param analysisControl main application controller
     */
    private StatsResultDialog(java.awt.Frame parent, boolean modal, StatsThread thread, AnalysisController analysisControl) {
        super(parent, modal);
       
        this.statisticsThread = thread;
        this.analysisControl = analysisControl;
        
        setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        loadComponents(); //Intialise the form components
        makeDisposeOnEscapeKey(this);
        setMnemonics();

        //Set "Stop" as the default button
        this.getRootPane().setDefaultButton(jButtonCancel);

        //This window should open centralised on the main application window.
        this.setLocationRelativeTo(parent);
        
        // Begin querying StatsThread for new results
        startTimer();
    }

    /**
     * Update the summary statistics with values from the StatsLogger
     */
    private void updateSummaryFields(){
        jTextFieldResults[0].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getTotalFiles()));
        jTextFieldResults[1].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getGlobalTotalBytes()));
        jTextFieldResults[2].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getNumBadFolders()));
        jTextFieldResults[3].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getNumBadFiles()));        
        jTextFieldResults[4].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getSmallestFileSize()));
        jTextFieldResults[5].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getLargestFileSize()));
        jTextFieldResults[6].setText(" "+String.valueOf(this.analysisControl.getStatsLogger().getMeanFileSize()));
    }
    
    /**
     * Begin timer to update GUI from the model
     */
    private void startTimer(){
        timer = new javax.swing.Timer(500, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                // Update summary fields
                updateSummaryFields();
                
                // Update ByYear and ByFormat tables
                ((AbstractTableModel)jTableByYear.getModel()).fireTableDataChanged();
                ((AbstractTableModel)jTableByFormat.getModel()).fireTableDataChanged();
                
                if (analysisControl.isAnalysisComplete() || analysisControl.isAnalysisCancelled()){
                    timer.stop();
                    jButtonCancel.setEnabled(false);
                    jButtonExport.setEnabled(true);

                    // Check whether any files were found
                    if (analysisControl.getStatsLogger().getTotalFiles() == 0){
                        javax.swing.JOptionPane.showMessageDialog(null,
                        "No files were found in the location(s) you selected",
                        "No files found",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        jButtonExport.setEnabled(false);
                    }
                }                
            }
        });
        timer.start();
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
     * @return Collection of File paths as strings (Contains no duplicates)
     */
    public static void showDialog(java.awt.Component frameComp,StatsThread thread, AnalysisController ac) {

        java.awt.Frame f = javax.swing.JOptionPane.getFrameForComponent(frameComp);

        dialog = new StatsResultDialog(f, true, thread, ac);

        dialog.setVisible(true);

    }

    /**
     * Set the mnemonics (Keyboard shortcuts) for menu items on this form
     * Can only be called after initComponents()
     */
    private void setMnemonics() {
        jButtonExport.setMnemonic('E');
        jButtonCancel.setMnemonic('S');
    }
    
    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void loadComponents() {
                
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Profile");
        setName("StatsResultDialog");
        
        jPanelMaster = new javax.swing.JPanel();
        jPanelMaster.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),"Report"),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        jPanelMaster.setLayout(new javax.swing.BoxLayout(jPanelMaster,javax.swing.BoxLayout.PAGE_AXIS));
        
        // Create results summary fields
        jPanelSummary = new javax.swing.JPanel();
        jLabelSummaryLabels = new javax.swing.JLabel[7];
        jTextFieldResults = new javax.swing.JTextField[7];
        
        jPanelSummary.setLayout(new java.awt.GridLayout(jTextFieldResults.length, 2, 5, 5));
        for (int i = 0; i < jLabelSummaryLabels.length; i++){
            jLabelSummaryLabels[i] = new javax.swing.JLabel();
            jTextFieldResults[i] = new javax.swing.JTextField();
            jTextFieldResults[i].setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
            jTextFieldResults[i].setEditable(false);
            jTextFieldResults[i].setBackground(java.awt.Color.white);
            jPanelSummary.add(jLabelSummaryLabels[i]);
            jPanelSummary.add(jTextFieldResults[i]);
        }
        
        jLabelSummaryLabels[0].setText("Total readable files: ");
        jLabelSummaryLabels[1].setText("Total file size (bytes): ");
        jLabelSummaryLabels[2].setText("Total unreadable folders: ");
        jLabelSummaryLabels[3].setText("Total unreadable files: ");
        jLabelSummaryLabels[4].setText("Smallest file's size (bytes): ");
        jLabelSummaryLabels[5].setText("Largest file's size (bytes): ");
        jLabelSummaryLabels[6].setText("Mean file size (bytes): ");
        
        jPanelMaster.add(jPanelSummary);
        
        // Create spacer
        jPanelMaster.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
                
        // Create by year table        
        javax.swing.JPanel byYearPanel = new javax.swing.JPanel();
        byYearPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Files by modified date",TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION),
                BorderFactory.createEmptyBorder(5,0,5,0)
        ));        
        jTableByYear = new javax.swing.JTable(new ByYearTableModel(analysisControl.getStatsLogger()));
        jTableByYear.setPreferredScrollableViewportSize(jTableByYear.getPreferredSize());
        jScrollPaneByYear = new javax.swing.JScrollPane(jTableByYear);
        jScrollPaneByYear.setPreferredSize(new java.awt.Dimension(460,200) );
        byYearPanel.add(jScrollPaneByYear);

        // Create by format table
        javax.swing.JPanel byFormatPanel = new javax.swing.JPanel();
        byFormatPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Files by format"),
                BorderFactory.createEmptyBorder(5,0,5,0)
        ));        
        jTableByFormat = new javax.swing.JTable(new ByFormatTableModel(analysisControl.getStatsLogger()));
        jTableByFormat.setPreferredScrollableViewportSize(jTableByFormat.getPreferredSize());
        jScrollPaneByFormat = new javax.swing.JScrollPane(jTableByFormat);        
        jScrollPaneByFormat.setPreferredSize(new java.awt.Dimension(460,200) );
        byFormatPanel.add(jScrollPaneByFormat);

        // Add sub-panels to master, and add master to JFrame
        jPanelMaster.add(byYearPanel);
        jPanelMaster.add(byFormatPanel);
        getContentPane().add(jPanelMaster, java.awt.BorderLayout.CENTER);
        
        jPanelActions = new javax.swing.JPanel();
        jButtonExport = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        
        // Add CSV export button
        jButtonExport.setText("Export to ");
        jButtonExport.setEnabled(false);
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });
        jPanelActions.add(jButtonExport);

        // Add cancel button
        jButtonCancel.setText("Stop");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelActions.add(jButtonCancel);

        // Add buttons panel to JFrame
        getContentPane().add(jPanelActions, java.awt.BorderLayout.SOUTH);

        pack();
    }
   
   /**
    * Export results. Display file dialog then call performExport
    */
    private void exportResults() {
        
        String fileExtension;
        String format;
        
        //Setup save file dialog
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.addChoosableFileFilter(new CustomFileFilter(XML_FILE_EXTENSION, XML_FILE_DESCRIPTION));
        fc.addChoosableFileFilter(new CustomFileFilter(CSV_FILE_EXTENSION, CSV_FILE_DESCRIPTION));
        fc.setAcceptAllFileFilterUsed(false);
        
        //Set dialog title to same as menu item text
        fc.setDialogTitle("Export statistics results");
        
        //show file dialog
        int returnVal = fc.showSaveDialog(this);
        
        java.io.File path = null;
        
        //Save file if user has chosen a file
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            
            // Get file the user selected
            path = fc.getSelectedFile();
            
            // Get the file extension
            if (fc.getFileFilter() instanceof CustomFileFilter != true){
                System.err.println("Unknown type of filter filter used in Export dialog");
                return;
            }
            fileExtension = ((CustomFileFilter)fc.getFileFilter()).getFileExtension();
            
            // If no extension was specified add one
            if (!(path.getName().endsWith("." + fileExtension))) {
                path = new java.io.File(path.getParentFile(), path.getName() + "." + fileExtension);
            }
            
            // If path exists check confirm with user if they want to overwrite
            if (path.exists()) {
                int option = javax.swing.JOptionPane.showConfirmDialog(this, MSG_OVERWRITE);
                if (option != javax.swing.JOptionPane.YES_OPTION) return;
            }
            
            // Get filepath of selected file
            final String selectedFilePath = path.getPath();
            
            // Export stats to file
            performExport(selectedFilePath, fileExtension);
        
        }
    }

    /**
     * Export statistics to the given filename using the appropriate StatsExportFormat
     * 
     * @param filePath the location of the file to write
     * @param fileExtension the three letter file extension, with no dot
     */
    private void performExport(String filePath, String fileExtension){

            // Flag for whether the stats have been exported successfully
            boolean exportSuccess = false;
            
            try {
                // Create create exporter for the format
                StatsExportFormat exporter = StatsExporterFactory.createExportFormat(fileExtension);
                                
                // Perform export
                exportSuccess = exporter.export(analysisControl.getStatsLogger(),analysisControl.getStartTime(),
                        analysisControl.getCompletedTime(),filePath);
                
                // Report success or failure of compile and write
                if (exportSuccess == true){
                    // Show popup saying the stats has been saved
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Results exported as "+fileExtension.toUpperCase()+" to " + filePath,
                            "Statistics exported",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }else {
                    // Show popup saying there was a compile error (XML only)
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Unable to export statistics\n ("+ exporter.getCompilationError() +")",
                            "Export failed",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (StatsExporterFactory.FormatUnknownException e){
                // Show popup saying unknown format
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Unable to export - unknown file format "+fileExtension+". Please try again.",
                        "Export failed",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }           
       
    }
    
    /**
     * Hides the dialog
     *
     * @param evt Event object
     */
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.analysisControl.cancelAnalysis();
        jButtonCancel.setEnabled(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * Adds file paths  to return list on button click
     *
     * @param evt Event object
     */
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed

        
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt){
        this.exportResults();
    }
    
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelMaster;
    private javax.swing.JPanel jPanelSummary;
    private javax.swing.JTextPane jTextPaneProcessed;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JCheckBox jCheckBoxRecursive;
    private javax.swing.JPanel jPanelActions;
    private javax.swing.JPanel jPanelFilesFolders;
    private javax.swing.JPanel jPanelTables;
    
    // Report controls
    private javax.swing.JLabel[] jLabelSummaryLabels;
    private javax.swing.JTextField[] jTextFieldResults;
    private javax.swing.JTable jTableByYear;
    private javax.swing.JTable jTableByFormat;
    private javax.swing.JScrollPane jScrollPaneByYear;
    private javax.swing.JScrollPane jScrollPaneByFormat;

   /**
    * Table model to show stats by year
    */
   private class ByYearTableModel extends AbstractTableModel {

        private String[] columnNames;
        private StatsLogger statsLogger;
        private String[] years;
        
        public ByYearTableModel(StatsLogger statsLogger) {
            this.columnNames = new String[]{
                    "Last Modified Year", "Number of Files", "Total Bytes"};
            this.statsLogger = statsLogger;
            this.years = getSortedYears();
        }

        public String getColumnName(int column) {
            return this.columnNames[column];
        }

        /**
         * Returns number of columns in the table
         * PUID,Format,Status,Version,Warnings
         *
         * @return number of columns in table
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Gets the number of rows displayed in the table
         *
         * @return Number of files held by the filelist
         */
        public int getRowCount() {
             return (int)this.years.length;
        }

        /**
         * Notify table that data has changed
         */
        public void fireTableDataChanged(){
           this.years = getSortedYears();
           super.fireTableDataChanged();
        }
        
        /**
         * Gets the object for a specified cell
         *
         * @param row row id
         * @param col column id
         * @return object to display in cell
         */
        
        public Object getValueAt(int row, int col) {
            //String[] years = getSortedYears();
            if (years.length == 0) return "";
            switch (col) {
                case 0:
                    return years[row];
                case 1:
                    return this.statsLogger.getNumFilesByYear(years[row]);
                case 2:
                    return this.statsLogger.getTotalVolumeByYear(years[row]);
            }
            return "";
        }
           
        /**
         * Get the files' years logged, sorted from old to new
         *
         * @return Array of year names, e.g. {"2004","2005"}
         */        
        private String[] getSortedYears(){
            
            String[] yearStrings = new String[statsLogger.getTotalYears()];
            int counter = 0;
            
            if (yearStrings.length > 0){
                Iterator years = statsLogger.getYearKeys();    
                while (years.hasNext()){
                    yearStrings[counter] = (String)years.next();
                    counter++;
                }
            }
            java.util.Arrays.sort(yearStrings);
            return yearStrings;
        }
        
        
        /**
         * Get the Class for a column
         *
         * @param col column to find class
         * @return Class type found
         */
        
        public Class getColumnClass(int col) {
            try {
                return getValueAt(0, col).getClass();
            } catch (NullPointerException e) {
                return "".getClass();
            }
        }

        /**
         * Determines wether table cell is editable
         * ALWAYS returns false
         *
         * @param row Row of cell
         * @param col Column of cell
         * @return true if cell editable , false otherwise
         */
        public boolean isCellEditable(int row, int col) {

            return false;
        }

        /**
         * Sets the value for a specific cell
         * DOES NOTHING IN THIS IMPLEMENTATION
         *
         * @param aValue Object to set in cell
         * @param row    Row of cell to enter object
         * @param column Column of cell to enter object
         */
        
        public void setValueAt(Object aValue, int row, int column) {
            //data[row][column] = aValue;

        }
    }    
   
   /**
    * Table model to show stats by format
    */
   private class ByFormatTableModel extends AbstractTableModel {

        private String[] columnNames;
        private StatsLogger statsLogger;
        private String[] formats;

        public ByFormatTableModel(StatsLogger statsLogger) {
            this.columnNames = new String[]{
                    "PUID", "MIME", "Format", "Version", "Number of Files", "Total Bytes"};
            this.statsLogger = statsLogger;
            this.formats = getFormats();

        }

        public String getColumnName(int column) {
            return this.columnNames[column];
        }

        /**
         * Returns number of columns in the table
         *
         * @return number of columns in table
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Gets the number of rows displayed in the table
         *
         * @return Number of files held by the filelist
         */
        public int getRowCount() {
             return (int)this.formats.length;
        }

        /**
         * Notify table that data has changed
         */
        public void fireTableDataChanged(){
           this.formats = getFormats();
           super.fireTableDataChanged();
        }
                
        /**
         * Gets the object for a specified cell
         *
         * @param row row id
         * @param col column id
         * @return object to display in cell
         */
        
        public Object getValueAt(int row, int col) {
            if (this.formats.length == 0) return "";
            switch (col) {
                case 0:
                    try {
                        return this.formats[row];
                    }catch (Exception e){
                        System.err.println(this.formats.length+","+row+","+this.statsLogger.getTotalFormats());
                        System.err.println(this.statsLogger.getTotalFiles()+","+this.statsLogger.getTotalFormats());
                        System.exit(1);
                    }
                    break;
                case 1:
                    return this.statsLogger.getMIMEByFormat(formats[row]);
                case 2:
                    return this.statsLogger.getFormatNameByFormat(formats[row]);
                case 3:
                    return this.statsLogger.getVersionByFormat(formats[row]);
                case 4:
                    return this.statsLogger.getNumFilesByFormat(formats[row]);
                case 5:
                    return this.statsLogger.getTotalVolumeByFormat(formats[row]);
            }
            return "";
        }
           
        /**
         * Get the Class for a column
         *
         * @param col column to find class
         * @return Class type found
         */        
        public Class getColumnClass(int col) {
            try {
                return getValueAt(0, col).getClass();
            } catch (NullPointerException e) {
                return "".getClass();
            }
        }

        /**
         * Get the formats logged
         *
         * @return Array of format PUIDs
         */
        private String[] getFormats(){
            
            String[] formatStrings = new String[statsLogger.getTotalFormats()];
            int counter = 0;
            
            if (formatStrings.length > 0){
                Iterator formats = statsLogger.getFormatKeys();    
                while (formats.hasNext()){
                    formatStrings[counter] = (String)formats.next();
                    counter++;
                }
            }
            
            return formatStrings;
        }
        
        /**
         * Determines whether table cell is editable
         * ALWAYS returns false
         *
         * @param row Row of cell
         * @param col Column of cell
         * @return true if cell editable , false otherwise
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        /**
         * Sets the value for a specific cell
         * DOES NOTHING IN THIS IMPLEMENTATION
         *
         * @param aValue Object to set in cell
         * @param row    Row of cell to enter object
         * @param column Column of cell to enter object
         */
        
        public void setValueAt(Object aValue, int row, int column) {
            //data[row][column] = aValue;

        }
    }       
}
