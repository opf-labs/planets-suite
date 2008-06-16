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
 * Nested Classes:
 *                  FileListTableModel      - Used to display file list in JTable 
 *                  HitListTableModel       - Used to dislay file format hits in a JTable
 *                  FileListHeaderRenderer  - Renders the column headers in the file list JTable
 *                  CellRenderer            - Renders text cells in the filelist JTable
 *                  IconCellRenderer        - Renders cells with icons in the filelist JTable
 *
 * Version      Date        Author      Short Description
 *
 * V1.R0.M0     08-Mar-2005 S.Malik     Created
 *$History: FileIdentificationPane.java $   
 * 
 * *****************  Version 65  *****************
 * User: Walm         Date: 20/10/05   Time: 15:15
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * When using the PRONOM web service, check whether the connection failed,
 * and if so provide a helpful message
 * 
 * *****************  Version 64  *****************
 * User: Mals         Date: 20/06/05   Time: 14:43
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Calling parameters for Print Preview changed, so needed updating
 * 
 * *****************  Version 63  *****************
 * User: Walm         Date: 6/06/05    Time: 16:55
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Change appearance of form:
 * - resize frames
 * - frame around toolbar
 * - appearance of "Save as" button when hovering over it
 * - remove vertical line in toolbar
 * 
 * *****************  Version 62  *****************
 * User: Mals         Date: 12/05/05   Time: 13:03
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Added tooltip to open list button on toolbar
 * 
 * *****************  Version 61  *****************
 * User: Mals         Date: 10/05/05   Time: 13:47
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Changed path to image for Down arrow which shows image on file list
 * header
 * 
 * *****************  Version 60  *****************
 * User: Mals         Date: 10/05/05   Time: 11:47
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * + Frame icon set to image supplied by TNA
 * + Help window icon set to same as help icon on menubar
 * 
 * *****************  Version 59  *****************
 * User: Mals         Date: 6/05/05    Time: 16:27
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Toolbar and menubar icons changed to set supplied by TNA (
 * Tessella Ref: NPD/4305/CL/CSC/2005MAY06/11:35:51 )
 * 
 * +Fixed bug discovered in tests 7.7,7.8( STS V1.R1.M1)  -  4 May 2005
 * 
 * *****************  Version 58  *****************
 * User: Mals         Date: 3/05/05    Time: 15:44
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/CL/CSC/2005MAY03/09:01:53
 * 7: I  think we needï¿½one more minor adjustment to the spacing in the
 * layout: can  we increase the space between the file list and id results
 * tables and the  borders at the sides to match that at top and bottom.
 * 
 * *****************  Version 57  *****************
 * User: Mals         Date: 3/05/05    Time: 11:54
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005MAY03/08:51:16
 * 
 * 6: In Identification results, the width of the Status column should be
 * wide enough to display the longest status text completely.
 * 
 * *****************  Version 56  *****************
 * User: Mals         Date: 3/05/05    Time: 11:40
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005MAY03/08:51:16
 * 
 * 1: Can we have the application window open not-maximised?
 * 
 * *****************  Version 55  *****************
 * User: Mals         Date: 3/05/05    Time: 11:35
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005MAY03/08:51:16
 * 5: I think genuinely unidentified files (i.e. not as a result of an
 * error) should be displayed as an ID result with a status of
 * "Unidentified", and  a warning to elaborate - "The format could not be
 * identified". 
 * 
 * *****************  Version 54  *****************
 * User: Mals         Date: 3/05/05    Time: 9:59
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005MAY03/08:51:16
 * 3: Agreed - Error is better. - When selecting an ERROR status file from
 * the file list, the "Identification results" pane shows a text box with
 * a "warnings" header - this should really be "Error" 
 * 
 * *****************  Version 53  *****************
 * User: Mals         Date: 28/04/05   Time: 15:28
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Changed to Plastic Look and Feels from PlasticXP Look and feel, as
 * buttons and toolbars were not displayed correctly on Max OS X. 
 * 
 * *****************  Version 52  *****************
 * User: Mals         Date: 28/04/05   Time: 15:12
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Only allows printing and print previewing if there is 1 or more files
 * in the file list
 * 
 * *****************  Version 51  *****************
 * User: Mals         Date: 28/04/05   Time: 10:03
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR28/09:57:44
 * Update the DateLastDownload in the configuration file after new sig
 * file is checked for- this is so that the user is not asked to check
 * signature file until another "DownloadFrequency" days have elapsed
 * 
 * *****************  Version 50  *****************
 * User: Mals         Date: 28/04/05   Time: 9:52
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Warning column removed from file list and identification results grid
 * turns into text box if file is identified as error.
 * 
 * Tessella Ref: NPD/4305/CL/CSC/2005APR21/16:33:49
 * 
 * *****************  Version 49  *****************
 * User: Mals         Date: 20/04/05   Time: 17:32
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Fixed bug : Resize cursor wasn't shown when resizing columns on
 * identification results
 * 
 * *****************  Version 48  *****************
 * User: Mals         Date: 20/04/05   Time: 16:35
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Help window centred when opened
 * 
 * *****************  Version 47  *****************
 * User: Mals         Date: 19/04/05   Time: 16:50
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tool tips on File List cells
 * 
 * *****************  Version 46  *****************
 * User: Mals         Date: 19/04/05   Time: 11:16
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR18/09:51:03
 * Issue 32 - Change background of file list and identification results to
 * white
 * 
 * *****************  Version 45  *****************
 * User: Mals         Date: 18/04/05   Time: 16:51
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR18/09:51:03
 * 1.The main application window should open centralised on screen, as the
 * splash screen does.
 * 5.The rightmost toolbar divider is no longer required.
 * 6.Toolbar tips text should have all initial letters capitalised, and
 * should be consistent with menu items text.
 * 7.All command buttons should have keyboard shortcuts indicated by
 * underlined text.
 * 8.Command button tips text should have all initial letters capitalised,
 * and should be consistent with menu items text.
 * 9.The progress bar text should be of the form ï¿½File x of y analysedï¿½.
 * 10.Menu bar changes as in document.
 * 11.All dialog boxes should have the same title as the menu item or
 * button which opens them: Open List, Save List, Export to CSV, Add
 * Files, Options, DROID Help, About DROID.
 * 23.The infill square at top right above the vertical scroll bar appears
 * a different shade of grey.
 * 24.Add files should be 'Add Files'.
 * 25.Remove should be 'Remove Files'.
 * 26.'Remove all' should be 'Remove All'.
 * 27.I think the icons should be removed from these buttons ï¿½ this will
 * help to make the Identify and Cancel buttons stand out more.
 * 28.The Remove and Remove All buttons should be disabled unless the file
 * list contains at least one file.
 * 30.The infill square at top right above the vertical scroll bar should
 * be grey to match (this is only visible with multiple identifications).
 * 31.The file name text box is not editable and should have a grey
 * background, with a dark grey border. The scroll bar can be removed. The
 * label should be centralised with the text box.
 * 35.The default column widths could be adjusted a little ï¿½ PUID and
 * Version could be a little narrower, and Warning a little wider.
 * 
 * 
 * *****************  Version 44  *****************
 * User: Mals         Date: 15/04/05   Time: 10:29
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Hour glasses on print preview and help launch
 * 
 * *****************  Version 43  *****************
 * User: Mals         Date: 14/04/05   Time: 17:13
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Splash Image URL corrected
 * 
 * *****************  Version 42  *****************
 * User: Mals         Date: 14/04/05   Time: 15:54
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR13/17:56:51
 * 4.Key press on BackSpace calls remove all
 * 
 * *****************  Version 41  *****************
 * User: Mals         Date: 14/04/05   Time: 14:34
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR13/17:56:51
 * 8. Some of the dialog text needs to be corrected. 
 * 
 * *****************  Version 40  *****************
 * User: Mals         Date: 14/04/05   Time: 11:09
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Tessella Ref: NPD/4305/PR/IM/2005APR13/17:56:51
 * GUI changes
 * 1.Make the screen 800x600 pixles by default.
 * 2. Can we size the columns Identification Results columns intelligently
 * (in particular, so most Formats can be seen).
 * 
 * 
 * *****************  Version 39  *****************
 * User: Mals         Date: 13/04/05   Time: 12:33
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Ref:Email from A.Brown NPD/4305/CL/CSC/2005APR12/13:11  
 * File ID GUI comments
 * -----------------------------------
 * 
 * Changes made in light of comments
 * 
 * +name for the tool - DROID (Digital Record Object Identification)
 * +Add/Removeactions as command buttons on the form, rather than on the
 * tool bar
 * +"Remove All" instead of "new list"
 * +The Add files button should be the default button.
 * +"File details" renamed "Identification results"
 * 
 * 
 * *****************  Version 38  *****************
 * User: Mals         Date: 13/04/05   Time: 10:06
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Ref:Email from A.Brown NPD/4305/CL/CSC/2005APR12/13:11  File ID GUI
 * comments
 * -Only have one save function, which saves results if they exist
 * 
 * *****************  Version 37  *****************
 * User: Mals         Date: 7/04/05    Time: 16:31
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Add application and signature version numbers as parameters to print
 * and print preview functions
 * 
 * *****************  Version 36  *****************
 * User: Mals         Date: 7/04/05    Time: 14:03
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Generate mnemonics code in NetBeans 3.6 turned off , so openide
 * library not needed
 * +Mnemonics(keyboard shortcuts) set on all menu items
 * 
 * *****************  Version 35  *****************
 * User: Walm         Date: 7/04/05    Time: 11:43
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * investigate mouse display in column resizing
 * 
 * *****************  Version 34  *****************
 * User: Walm         Date: 5/04/05    Time: 11:36
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Correct bug in removeFiles method
 * 
 * *****************  Version 33  *****************
 * User: Walm         Date: 4/04/05    Time: 17:45
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * modify startup procedure
 * 
 * *****************  Version 32  *****************
 * User: Mals         Date: 4/04/05    Time: 9:18
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * No Change - checked in for code sharing
 * 
 * *****************  Version 31  *****************
 * User: Mals         Date: 31/03/05   Time: 15:13
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Displays Error file classification/status icon
 * 
 * *****************  Version 30  *****************
 * User: Mals         Date: 31/03/05   Time: 12:20
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Added parameters to the method to show the about box
 * 
 * *****************  Version 29  *****************
 * User: Mals         Date: 31/03/05   Time: 10:43
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * -Removed nested class XMLFileFilter
 * +Uses CustomFileFilter instead
 * 
 * *****************  Version 28  *****************
 * User: Mals         Date: 30/03/05   Time: 16:41
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Saving file list and saving results are now seperate methods 
 * 
 * *****************  Version 27  *****************
 * User: Mals         Date: 30/03/05   Time: 15:43
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Export to CSV
 * +Passes analysis controller object to OptionsDialog
 * 
 * *****************  Version 26  *****************
 * User: Mals         Date: 30/03/05   Time: 11:23
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Checks for is signature file download is due on startup 
 * +Asks user whether they would like to download new file and then
 * downloads if yes
 * 
 * *****************  Version 25  *****************
 * User: Mals         Date: 29/03/05   Time: 17:42
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Open list on menu item click now works
 * 
 * *****************  Version 24  *****************
 * User: Mals         Date: 29/03/05   Time: 17:37
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Direct print runs in its own thread as this may take some time
 * 
 * *****************  Version 23  *****************
 * User: Walm         Date: 29/03/05   Time: 16:55
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Initialise the main pane used for displaying warning messages
 * 
 * *****************  Version 22  *****************
 * User: Mals         Date: 29/03/05   Time: 12:01
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * REF: Email from A.Brown NPD/4305/CL/CSC/2005MAR21/12:25:13 
 * Menu Bar
 * ----------------
 * +All menu items have keyboard shortcuts indicated
 * + ... added to menu items that open dialogs
 * + Save menu item goes directly to dialog
 * 
 * Tool bar
 * -------------
 * +Simply toolbar text 
 * 
 * File List
 * -----------
 * +Horizontal scrollbar when file names exceed width of box
 * +white space in top right hand corner between scrollbar and column head
 * is infilled
 * 
 * File details
 * ----------------
 * +Title is now "File details" instead of "File Details"
 * 
 * *****************  Version 21  *****************
 * User: Mals         Date: 29/03/05   Time: 9:22
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Vertical gridlines shown on file formats hits table 
 * REF: Email from A.Brown NPD/4305/CL/CSC/2005MAR21/12:25:13 
 * 
 * *****************  Version 20  *****************
 * User: Mals         Date: 24/03/05   Time: 16:52
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Print menu item and print button on toolbar call print function
 * 
 * *****************  Version 19  *****************
 * User: Mals         Date: 24/03/05   Time: 13:23
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * +Added print previewing 
 * +Changed look and feel to Jgoodies Plastic XP L&F with the Sky Bluer
 * theme 
 * 
 * *****************  Version 18  *****************
 * User: Mals         Date: 21/03/05   Time: 14:04
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Launches help window
 * 
 * *****************  Version 17  *****************
 * User: Mals         Date: 21/03/05   Time: 9:57
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Correctected copyright statement
 * Sorting by filename now ignores case.
 * 
 * *****************  Version 16  *****************
 * User: Mals         Date: 16/03/05   Time: 17:13
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Check file save on window closing 
 * 
 * *****************  Version 15  *****************
 * User: Mals         Date: 16/03/05   Time: 16:32
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * -Opens and saves file lists without identification and format hits
 * -Validates list is saved before: new , open or close 
 * 
 * *****************  Version 14  *****************
 * User: Mals         Date: 16/03/05   Time: 12:29
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * -File list sort by column is highlighted
 * -Cancel button now cancels run , but cannot restart identification at
 * the moment
 * -Identify and cancel butons enable/disable depending on run and/or file
 * list
 * -Add files doesn't open when identification process is running 
 * -Menu bar item now call functions(i.e Identify , Cancel , Remove  , Add
 * Files) 
 * 
 * *****************  Version 13  *****************
 * User: Mals         Date: 16/03/05   Time: 10:27
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * File list sort by column gets highlighted
 * 
 * *****************  Version 12  *****************
 * User: Mals         Date: 15/03/05   Time: 15:15
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Remove files functionality added - but can also remove identified files
 * at this point and files while identification is running
 * 
 * Changes made to acces to IdentificationFile objects as classification
 * is now an int instead of string 
 * 
 * *****************  Version 11  *****************
 * User: Mals         Date: 15/03/05   Time: 10:54
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Implemented sorting by status or filename on file list 
 * 
 * *****************  Version 10  *****************
 * User: Mals         Date: 14/03/05   Time: 18:12
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Progress bar working on run
 * 
 * *****************  Version 9  *****************
 * User: Mals         Date: 14/03/05   Time: 9:42
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * launch method added so GUI can be run from another class
 * 
 * *****************  Version 8  *****************
 * User: Mals         Date: 11/03/05   Time: 17:12
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI
 * Displays files when added
 * 
 */

package uk.gov.nationalarchives.droid.GUI;

import java.net.URL;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileCollection;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.StatsThread;
import uk.gov.nationalarchives.droid.IdentificationFile;
import uk.gov.nationalarchives.droid.messageDisplay;
import uk.gov.nationalarchives.droid.GUI.FileSelection.FileSelectDialog;
import uk.gov.nationalarchives.droid.GUI.FileSelection.FileSelectReturnParameter;
import uk.gov.nationalarchives.droid.GUI.Printing.PrintPreview;
import uk.gov.nationalarchives.droid.xmlReader.PronomWebService;
import uk.gov.nationalarchives.droid.stats.StatsLogger;
import uk.gov.nationalarchives.droid.stats.GUI.*;


/**
 * Entry form for the GUI front end
 * This is the entry form for the GUI
 * This form :
 * displays  identication file list ,
 * initiates identification process,
 * displays progress of identification process,
 * displays format hits after identification process
 * Based on fiMain.java from the FileIDPrototype
 * <p/>
 * Created in netBeans IDE 3.6
 * Related file FileIdentificationPane.Form (for use in netBeans)
 *
 * @author Shahzad Malik
 * @version V1.R0.M.0, 08-Mar-2005
 */
public class FileIdentificationPane extends javax.swing.JFrame {

    /**
     * Object to peform File Identification analysis
     */
    private AnalysisController analysisControl;

    /**
     * Used for status text delay
     */
    private javax.swing.Timer timer;

    /**
     * Used to poll controller when identification run is in progess
     */
    private javax.swing.Timer identifyTimer;

    /**
     * Has the fileList been saved
     */
    private boolean fileListSaved;

    /**
     * Column to sort file list by
     */
    private int FileListSortByColumn;

    /**
     * Flags when identification process is running
     */
    private boolean identificationRunning;

    /**
     * List of IdentificationFile indexes
     */
    private java.util.List fileList;

    /**
     * List of ImageIcons used to display file identification status
     */
    private java.util.List statusIcons;

    /**
     * Position of file identification status column in file list
     */
    private final int FILELIST_COL_STATUS = 0;
    /**
     * Position of file name column in file list
     */
    private final int FILELIST_COL_FILENAME = 1;
    /**
     * Position of file identification warning column in file list
     */
    private final int FILELIST_COL_WARNING = 2;

    /**
     * Name of tool to be displayed in title bar *
     */
    private final String APPLICATION_NAME = "DROID (Digital Record Object Identification)";

    /**
     * File extension to save and open file lists
     */
    private final String FILE_COLLECTION_FILE_EXTENSTION = "xml";
    /**
     * File descriptions for open and save dialogs when opening and saving file lists
     */
    private final String FILE_COLLECTION_FILE_DESCRIPTION = "DROID file collection(*.xml)";

    /**
     * File extension for Comma separated value file (CSV) files)
     */
    private final String CSV_FILE_EXTENSION = "csv";
    /**
     * File description for Comma separated value file (CSV) files)
     */
    private final String CSV_FILE_DESCRIPTION = "Comma separated value file (*.csv)";

    //Query and cofirm dialog messages

    /**
     * Message when opening a saved file list
     */
    private final String MSG_SAVE_FILE_LIST = "Opening a new file list will cause the current list to be lost.  Do you wish to save this first?";

    /**
     * Message when attempting to remove all files from list
     */
    private final String MSG_REMOVE_ALL = "Are you sure you would like to remove ALL the files in the current list?";

    /**
     * Message when attempting to remove one or more files from list
     */
    private final String MSG_REMOVE_FILE = "Are you sure you would like to remove the selected file(s)?";

    /**
     * Message when no files are selected when user attempts to use remove
     */
    private final String MSG_NOT_REMOVED_FILE = "One or more files must be selected";

    /**
     * Message when user is selects an exisiting file when saving or exporting
     */
    private final String MSG_OVERWRITE = "The specified file exists, overwrite?";

    /**
     * Message when list not saved on exit
     */
    private final String MSG_EXIT_SAVE_CHECK = "Do you want to save the current file list before exiting?";

    /**
     * Message to check if a signature file is available for download
     */
    private final String MSG_CHECK_SIG_FILE_UPDATE = "Would you like to check the web for a newer signature file?";

    /**
     * Message to download signature file
     */
    private final String MSG_DOWNLOAD_SIG_FILE = "A newer signature file is available.  Would you like to download it?";

    /**
     * Message when file list is empty and trying to print
     */
    private final String MSG_PRINT_FILE_LIST_EMPTY = "One or more files must be in the file list";

    /**
     * Message when file list is empty and trying to print preview
     */
    private final String MSG_PRINT_PREVIEW_FILE_LIST_EMPTY = "One or more files must be in the file list";

    /**
     * Warning box title when file has an Error status
     */
    private final String TITLE_WARNING_BOX_ERROR = "Error";
    /**
     * Warning box title when file is has an uidentified status
     */
    private final String TITLE_WARNING_BOX_NOT_IDENTIFIED = "Unidentified";
    /**
     * Detailed message for warning box when file is not identified
     */
    private final String MSG_UNIDENTIFIED = "The format could not be identified";

    /**
     * Creates new form FileIdentificationPane
     *
     * @param ac Analysis controller object that provided functionality
     */
    private FileIdentificationPane(AnalysisController ac) {

        fileListSaved = false;             //Default as false
        identificationRunning = false;     //Default as false
        fileList = new java.util.ArrayList();
        analysisControl = ac;              //Initialise the controller object
        FileListSortByColumn = FILELIST_COL_FILENAME;
        fileListSaved = true;

        setStatusIconList();

        setCustomLookAndFeel();
        initComponents();

        //Set the icon for the window 
        try {
            this.setIconImage(javax.imageio.ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/DROID16.gif")));
        } catch (java.io.IOException e) {
            //Silently ignore exception
        }

        //Set "Add files" as the default button
        jPanelFileIdentification.getRootPane().setDefaultButton(jButtonAdd);

        setMnemonics();
        this.setTitle(APPLICATION_NAME);

        pack();    //display form

        refreshFileList();

        //Set the column width for the jTables in the form
        setTableColumnWidths();

        //Set file list event listeners
        setFileListListener();
        setFileListHeaderListner();
        //Set the fil list header renderer 
        setFileListHeaderRenderer();
        setFileListCellRenderers();

        messageDisplay.initialiseMainPane(this);

        //draw a splash window
        java.awt.Frame f = javax.swing.JOptionPane.getFrameForComponent(this);
        SplashWindow mySplish = new SplashWindow("/uk/gov/nationalarchives/droid/GUI/Icons/splash_image.gif", f);

        //Initialise Config file 
        try {
            analysisControl.readConfiguration();
            String theSigFileName = analysisControl.getSignatureFileName();
            analysisControl.readSigFile(theSigFileName, true, true);
            analysisControl.checkSignatureFile();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.toString());
        }

        setFocusable(true);
        requestFocus();

        //Centre on screen 
        this.setLocationRelativeTo(null);

        //Show the form
        this.setVisible(true);

        // set config parameters on the results table
        // for linking to pronom web site
        jTableHitList.setbaseURL(ac.getPuidResolutionURL());
        jTableHitList.setBrowserPath(ac.getBrowserPath());

        setKeyListeners();

        //hide splash window
        mySplish.endSplash();

        //Check if signature file update is due
        sigFileDownloadDue();
    }

    /**
     * Sets the look and feel for the form
     * Must be called before initComponents() in the constructor
     * <p/>
     * Jgoodies Plastic L&F with the Sky Bluer theme
     * (Changed from PlasticXP as this didn't work on Mac OS X)
     * All options as default except General options: Popup Shadow: On
     * REF: Email from A.Brown NPD/4305/CL/CSC/2005MAR21/12:25:13
     */
    private void setCustomLookAndFeel() {
        try {

            com.jgoodies.plaf.plastic.PlasticLookAndFeel lf = new com.jgoodies.plaf.plastic.PlasticLookAndFeel();
            lf.setMyCurrentTheme(new com.jgoodies.plaf.plastic.theme.SkyBluer());

            javax.swing.UIManager.setLookAndFeel(lf);
            javax.swing.UIManager.put("jgoodies.popupDropShadowEnabled", Boolean.TRUE);


        } catch (Exception e) {
            //Silently ignore exception
        }
    }


    /**
     * Add key listener to form
     */
    private void setKeyListeners() {

        this.addKeyListener(
                new java.awt.event.KeyListener() {

                    public void keyPressed(java.awt.event.KeyEvent e) {
                        actionOnKeyPress(e);
                    }

                    public void keyReleased(java.awt.event.KeyEvent e) {
                        //Do nothing
                    }

                    public void keyTyped(java.awt.event.KeyEvent e) {
                        //Do nothing
                    }

                }
        );
    }

    /**
     * Perform commands on keypress
     * BACKSPACE --> Calls "Remove All" files function
     *
     * @param e KeyEvent
     */
    private void actionOnKeyPress(java.awt.event.KeyEvent e) {
        //BACKSPACE KeyCode = 8 
        if (e.getKeyCode() == 8) {
            newFileList();
        }
    }


    /**
     * Sets the JTable list model listener to recognise row selections
     */
    private void setFileListListener() {
        ListSelectionModel rowSM = jTableFileList.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //System.out.println("No rows are selected.");
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    //System.out.println("Row " + selectedRow
                    // + " is now selected.");

                    showFileHits(selectedRow);
                }
            }
        });
    }

    /**
     * Sets a mouse listener on the table header.
     * When a column is clicked , file list is sorted by that column
     */
    private void setFileListHeaderListner() {

        java.awt.event.MouseAdapter listMouseListener = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                javax.swing.table.TableColumnModel columnModel = jTableFileList.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = jTableFileList.convertColumnIndexToModel(viewColumn);
                if (e.getClickCount() == 1 && column != -1) {
                    //System.out.println("Sorting ..."); 
                    int shiftPressed = e.getModifiers() & java.awt.event.InputEvent.SHIFT_MASK;
                    boolean ascending = (shiftPressed == 0);
                    FileListSortByColumn = column;
                    refreshFileList();
                }
            }
        };
        JTableHeader th = jTableFileList.getTableHeader();
        th.addMouseListener(listMouseListener);


    }

    /**
     * Sets a uk.GUI.FileIdentificationPane.FileListHeaderRenderer()  object
     * as the Renderer for each column header
     */
    private void setFileListHeaderRenderer() {
        TableCellRenderer fileListRenderer = new FileIdentificationPane.FileListHeaderRenderer();
        jTableFileList.getColumnModel().getColumn(0).setHeaderRenderer(fileListRenderer);
        jTableFileList.getColumnModel().getColumn(1).setHeaderRenderer(fileListRenderer);


    }

    /**
     * Set the cell renderers for the File List jTable
     */
    private void setFileListCellRenderers() {
        jTableFileList.getColumnModel().getColumn(0).setCellRenderer(new FileIdentificationPane.IconCellRenderer());
        jTableFileList.getColumnModel().getColumn(1).setCellRenderer(new FileIdentificationPane.CellRenderer());


    }

    /**
     * Set the mnemonics (Keyboard shortcuts) for menu items on this form
     * Can only be called after initComponents()
     */
    private void setMnemonics() {

        //Menu mnemonics
        jMenuFile.setMnemonic('F');
        jMenuEdit.setMnemonic('E');
        jMenuIdentify.setMnemonic('I');
        jMenuTools.setMnemonic('T');
        jMenuHelp.setMnemonic('H');

        //File Menu mnemonics
        jMenuItemOpenList.setMnemonic('O');

        jMenuItemSaveResults.setMnemonic('S');
        jMenuItemPrintPreview.setMnemonic('v');
        jMenuItemPrint.setMnemonic('P');
        jMenuItemExportCSV.setMnemonic('C');
        jMenuItemExit.setMnemonic('x');

        //Edit Menu mnemonics
        jMenuItemAdd.setMnemonic('A');
        jMenuItemRemove.setMnemonic('R');
        jMenuItemRemoveAll.setMnemonic('l');

        //Identify Menu mnemonics
        jMenuItemIdentify.setMnemonic('I');
        jMenuItemCancelidentify.setMnemonic('C');

        //Tools Menu mnemonics
        jMenuItemOptions.setMnemonic('O');
        jCheckBoxShowFilePaths.setMnemonic('S');

        //Help Menu mnemonics
        jMenuItemHelpContents.setMnemonic('H');
        jMenuItemHelpAbout.setMnemonic('A');

        //Command button mnemonics
        jButtonAdd.setMnemonic('A');
        jButtonRemove.setMnemonic('R');
        jButtonRemoveAll.setMnemonic('l');

        jButtonIdentify.setMnemonic('I');
        jButtonCancel.setMnemonic('C');


    }


    /**
     * Populates the icon list with ImageIcon objects
     */
    private void setStatusIconList() {
        statusIcons = new java.util.ArrayList();
        statusIcons.add(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/noHit.GIF")));
        statusIcons.add(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/tentitive.GIF")));
        statusIcons.add(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/positive.GIF")));
        statusIcons.add(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/error.GIF")));
    }

    /**
     * Gets the status icon(for the file list) to display for a given file classification
     *
     * @param fileClassfication File Classification (see uk.AnalysisController.FILE_CLASSIFICATION_[X], where X is a classsification type)
     * @return icon corresponding to given file classification
     */
    private javax.swing.ImageIcon getStatusIcon(int fileClassfication) {

        switch (fileClassfication) {
            case AnalysisController.FILE_CLASSIFICATION_ERROR:
                return (javax.swing.ImageIcon) statusIcons.get(3);
            case AnalysisController.FILE_CLASSIFICATION_NOHIT:
                return (javax.swing.ImageIcon) statusIcons.get(0);
            case AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED:
                return null;
            case AnalysisController.FILE_CLASSIFICATION_TENTATIVE:
                return (javax.swing.ImageIcon) statusIcons.get(1);
            case AnalysisController.FILE_CLASSIFICATION_POSITIVE:
                return (javax.swing.ImageIcon) statusIcons.get(2);
            default:
                return null;
        }

    }

    /**
     * Checks whether a new signature file download is due
     */
    private void sigFileDownloadDue() {

        //Message for confirmm dialog
        final String confirmMessage = MSG_CHECK_SIG_FILE_UPDATE;

        //Check if a signature download file is due and that a
        if (analysisControl.isSigFileDownloadDue()) {
            //if download is due ask user if they would like to check for a new signature file
            int confirmValue = JOptionPane.showConfirmDialog(this, confirmMessage, "Signature file update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirmValue == JOptionPane.YES_OPTION) {
                //if they confirm they would like to then 
                //check if a newer file exists and download
                checkSigFileAndDownload();
            }
        }

    }

    /**
     * Checks whether a new signature file download is due
     */
    private void checkSigFileAndDownload() {
        //Message for confirm dialog
        final String confirmMessage = MSG_DOWNLOAD_SIG_FILE;

        //Set cursor to wait
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        //Show in status bar that sig file update search is taking place
        setStatusText("Checking for signature file update...");

        //Check if newer sig file available
        if (analysisControl.isNewerSigFileAvailable()) {

            //Reset status bar text and mouse cursor
            setStatusText("");
            this.setCursor(null);

            //Confirm with user whether they would like to download new signature file
            int confirmValue = JOptionPane.showConfirmDialog(this, confirmMessage, "Signature file update available", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirmValue == JOptionPane.YES_OPTION) {
                //if they confirm they would like to then 
                //download
                //Set cursor to wait
                this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
                //Show in status bar that sig file update search is taking place
                setStatusText("Downloading signature file update...");
                analysisControl.downloadwwwSigFile();
                this.setCursor(null);

            }
        } else if (PronomWebService.isCommSuccess) {
            //Newer sig file not found
            //Reset status text and cursor
            setStatusText("");
            setStatusText("No updates found for signature file", 2000);
            this.setCursor(null);
            //Update the DateLastDownload in the configuration file - this is so that the user is not asked to check signature file until another "DownloadFrequency" days have elapsed
            analysisControl.updateDateLastDownload();
            //Tell user newer sig file not found 
            JOptionPane.showMessageDialog(this, "Your signature file is up to date.", "No update available", JOptionPane.INFORMATION_MESSAGE);
        } else {
            //failed to connect to web service

            //Reset status text and cursor
            setStatusText("");
            setStatusText("Error connecting to web service", 2000);
            this.setCursor(null);
            //Message to warn user
            String failureMessage = "Unable to connect to the PRONOM web service. Make sure that the following settings in your configuration file (DROID_config.xml) are correct:\n";
            failureMessage += "    1- <SigFileURL> is the URL of the PRONOM web service.  This should be '" + AnalysisController.PRONOM_WEB_SERVICE_URL + "'\n";
            failureMessage += "    2- <ProxyHost> is the IP address of the proxy server if one is required\n";
            failureMessage += "    3- <ProxyPort> is the port to use on the proxy server if one is required";
            //Warn the user that the connection failed
            javax.swing.JOptionPane.showMessageDialog(this, failureMessage, "Web service connection error", javax.swing.JOptionPane.WARNING_MESSAGE);

        }
    }

    private void openStatsWindow(){
        final StatsReturnParameter returnObj = StatsDialog.showDialog(this);

        //If add was selected then add files to analysis object 
        if (returnObj.getAction() == FileSelectDialog.ACTION_ADD) {
           
            StatsThread thread = analysisControl.runStatsGathering("", "", returnObj.getPaths(), returnObj.isRecursive());
            /*
            //Run in worker thread as this may a considerable amount of time
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    // Perform stats on each file
                    for (int n = 0; n < returnObj.getPaths().length; n++) {
                        
                        setStatusText("");
                    }
                    
                    return "";
                }

            };

            //Start the thread
            worker.start();
            */
            StatsResultDialog.showDialog(this,thread,this.analysisControl);
        } else {
            this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            ; //Set the cursor to default
        }
    }

    /**
     * Show open dialog and populate file collection
     */
    private void openFileList() {

        //Check if file list is saved first 
        if (!fileListSaved) {
            //If not, ask user whether they would like to save current file list
            int returnval = javax.swing.JOptionPane.showConfirmDialog(this,
                    MSG_SAVE_FILE_LIST,
                    "File list not saved",
                    javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);


            switch (returnval) {
                case javax.swing.JOptionPane.YES_OPTION:
                    //if user wants to save list, then do so and exit from openining a file
                    saveFileList();
                    return;
                case JOptionPane.CANCEL_OPTION:
                    //if user selected cancel, do nothing
                    return;

            }

        }

        //User must have clicked cancel to be at this point , so show open file dialog

        //Intialise file chooser dialog
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        //Add file filter so only shows XML files
        fc.addChoosableFileFilter(new CustomFileFilter(FILE_COLLECTION_FILE_EXTENSTION, FILE_COLLECTION_FILE_DESCRIPTION));
        fc.setAcceptAllFileFilterUsed(false);

        //Sets the dialog title to same as menu item text
        fc.setDialogTitle(jMenuItemOpenList.getText());

        //Show the dialog
        int returnVal = fc.showOpenDialog(this);

        //Decide if user chose OK or Cancel
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            //User chose ok so open file list

            //Get path selected 
            final String selectedFilePath = fc.getSelectedFile().getPath();
            //Set cursor to wait
            this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            //Show in status bar that file is opening
            setStatusText("Loading " + selectedFilePath);

            //Run reading files in a thread , as this may take some time
            final SwingWorker worker =
                    new SwingWorker() {
                        public Object construct() {
                            try {
                                analysisControl.readFileCollection(selectedFilePath);
                            } catch (Exception e) {
                                javax.swing.JOptionPane.showMessageDialog(null, e.toString());
                            }
                            //Set status text to number of files in collection
                            setStatusText(selectedFilePath + " contains " + analysisControl.getNumFiles() + " files");
                            //update the file list jTable
                            refreshFileList();
                            //The file list is saved
                            fileListSaved = true;
                            //Cursor is set back to default
                            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
                            //File list location appended to titlebar
                            setTitle(APPLICATION_NAME + " [" + selectedFilePath + "]");
                            return "";

                        }
                    };

            //Start the thread
            worker.start();

        }
    }

    /**
     * Clears the open file list but checks if saved before
     */
    private void newFileList() {

        //Ask user to confirm they would like to remove all files
        int returnval = javax.swing.JOptionPane.showConfirmDialog(this, MSG_REMOVE_ALL, "Remove all", javax.swing.JOptionPane.YES_NO_OPTION);

        //If yes
        if (returnval == javax.swing.JOptionPane.YES_OPTION) {
            //Reset file list , progress bar, identification results and application title
            analysisControl.resetFileList();
            resetProgressBar();
            refreshFileList();
            showFileHits(-1);
            fileListSaved = true;
            this.setTitle(APPLICATION_NAME);
        }


    }


    /**
     * Run the identification process on the files in the list
     * only runs when a run is not taking place
     */
    private void identifyFiles() {
        if (!identificationRunning) {
            analysisControl.runFileFormatAnalysis();
            pollController();
        }

    }

    /**
     * Cancels the identification process
     * Should only cancel if a run is actually taking place
     */
    private void cancelIdentifyFiles() {
        analysisControl.cancelAnalysis();
    }

    /**
     * Opens File Selection dialog when user chooses to select files in modal view
     */
    private void addFiles() {

        //Doesn't allow add files dialog to open if identification process is running
        if (identificationRunning) {
            JOptionPane.showMessageDialog(this, "Cannot add files while identication process is running", "Cannot Add files", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        //Set cursor to wait (Egg timer)
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        //Get the number of files currently in the collection 
        final int fileCountBefore = analysisControl.getNumFiles();

        //Show the Add files selction dialog 
        final FileSelectReturnParameter returnObj = FileSelectDialog.showDialog(this);

        //If add was selected then add files to analysis object 
        if (returnObj.getAction() == FileSelectDialog.ACTION_ADD) {

            this.setStatusText("Adding files");

            //Run in worker thread as this may a considerable amount of time
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    // DEBUG System.out.print("DEBUG Paths selected:" + returnObj.getPaths().length ) ;
                    // Add files to collection for each path selected
                    for (int n = 0; n < returnObj.getPaths().length; n++) {
                        analysisControl.addFile(returnObj.getPaths()[n], returnObj.isRecursive());
                    }
                    //Set the status to the number of files that were added
                    setStatusText("");
                    setStatusText((analysisControl.getNumFiles() - fileCountBefore) + " files added", 3000);
                    //refresh the file list
                    refreshFileList();
                    //Set the cursor back to default
                    setCursor(null);
                    //File list has been changed so file list needs to be saved
                    fileListSaved = false;
                    return "";
                }

            };

            //Start the thread
            worker.start();

        } else {
            this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            ; //Set the cursor to default
        }


    }

    /**
     * Remove selected files from the file list
     * User has to confirm before files are removed
     */
    private void removeFiles() {
        //Get selected rows
        int[] selectedRows = jTableFileList.getSelectedRows();

        //Check if 1 or more files are selected
        if (selectedRows.length >= 1) {

            //Confirm with user 
            int confirm = JOptionPane.showConfirmDialog(this,
                    MSG_REMOVE_FILE,
                    "Remove files", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                //Build list of files to remove 
                java.util.List theFilesToRemove = new java.util.ArrayList();
                for (int n = 0; n < selectedRows.length; n++) {
                    Integer iRow = (Integer) fileList.get(selectedRows[n]);
                    theFilesToRemove.add(iRow);
                }
                //sort the list of file indexes
                java.util.Collections.sort(theFilesToRemove);
                //remove files in order from the largest index to the smallest
                for (int n = theFilesToRemove.size() - 1; n >= 0; n--) {
                    analysisControl.removeFile(((Integer) theFilesToRemove.get(n)).intValue());
                    fileList.remove(theFilesToRemove.get(n));
                }
                //Refresh file list after files removed
                refreshFileList();

                //Flag that file list is not saved
                fileListSaved = false;

            }


        } else {
            //Show message dialog if no files selected
            JOptionPane.showMessageDialog(this,
                    MSG_NOT_REMOVED_FILE,
                    "No files selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Displays Options dialog in modal view
     */
    private void showOptions() {
        boolean value = OptionsDialog.showDialog(this, analysisControl);
    }

    /**
     * Displays About box
     */
    private void showAboutBox() {
        AboutDialog.showDialog(this, APPLICATION_NAME, AnalysisController.getDROIDVersion(), analysisControl.getSigFileVersion() + "");
    }

    /**
     * Resets progress bar value and text
     * used when new run is started or new file list is displayed
     */
    private void resetProgressBar() {
        jProgressIdentification.setString("");
        jProgressIdentification.setValue(0);

    }

    /**
     * refreshes data the File list table and shows file hits
     */
    private void refreshFileList() {

        int currentlySelectedRow = jTableFileList.getSelectedRow();


        if (fileList.size() != analysisControl.getNumFiles()) {
            fileList.clear();
            java.util.Enumeration<Integer> it = analysisControl.getFileCollection().getIndexKeys();
            while (it.hasMoreElements()){
                fileList.add(it.nextElement());
            }

        }

        sortFileList(FileListSortByColumn);
        jTableFileList.setModel(new FileIdentificationPane.FileListTableModel());

        if (jTableFileList.getModel().getRowCount() > currentlySelectedRow && currentlySelectedRow >= 0) {
            jTableFileList.setRowSelectionInterval(currentlySelectedRow, currentlySelectedRow);
        }


        enableIdentifyActions();
        enableRemoveActions();
    }

    /**
     * Remove and Remove all buttons and menu items enabled
     * if the file list is not empty.If it is empty buttons are disabled
     */
    private void enableRemoveActions() {

        //Flag to allow remove buttons and menu item
        boolean allowRemove = (analysisControl.getNumFiles() > 0);

        //Set menu items
        jMenuItemRemove.setEnabled(allowRemove);
        jMenuItemRemoveAll.setEnabled(allowRemove);

        //Set buttons
        jButtonRemove.setEnabled(allowRemove);
        jButtonRemoveAll.setEnabled(allowRemove);
    }

    /**
     * Show the file hits for a selected file
     */
    private void showFileHits(int selectedRow) {

        //Get the file selected
        boolean foundDetails = false;

        //Get the card layout for the identification results panel
        java.awt.CardLayout cl = (java.awt.CardLayout) (jPanelIdentificationResults.getLayout());

        IdentificationFile idFile = new IdentificationFile("");
        jTextPaneNoIDMessage.setText("");
        if (selectedRow >= 0) {
            //Get the file object corresponding to selection
            Integer i = (Integer) fileList.get(selectedRow);
            idFile = analysisControl.getFile(i.intValue());
            foundDetails = true;
            //Set the text in the warning box
            jTextPaneNoIDMessage.setText(idFile.getWarning());
        }

        //If file selected has an Error status , hide the results and show the warning box
        if (idFile.getClassification() == AnalysisController.FILE_CLASSIFICATION_ERROR) {
            //Set the title of the group as "Warning"
            jPanelWarnings.setBorder(new javax.swing.border.TitledBorder(TITLE_WARNING_BOX_ERROR));
            //Show the text box panel 
            cl.show(jPanelIdentificationResults, "cardWarnings");
        } else if (idFile.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOHIT) {
            //Set the title of the group as "Errors"
            jPanelWarnings.setBorder(new javax.swing.border.TitledBorder(TITLE_WARNING_BOX_NOT_IDENTIFIED));
            //Show the text box panel 
            cl.show(jPanelIdentificationResults, "cardWarnings");
            String message = MSG_UNIDENTIFIED;
            //If warning exists, append it 
            if (!idFile.getWarning().equals("")) {
                message = message + " (" + idFile.getWarning() + ")";
            }
            jTextPaneNoIDMessage.setText(message);
        } else {
            //Otherwise show the results
            cl.show(jPanelIdentificationResults, "cardResults");
        }

        jTableHitList.setModel(new FileIdentificationPane.HitListTableModel(idFile));
        jTableHitList.getColumnModel().getColumn(0).setCellRenderer(jTableHitList.getCellRenderer());
        //Show the file path only if it has changed
        if (!jTextFieldSelectedFile.getText().equals(idFile.getFilePath()))
            jTextFieldSelectedFile.setText(idFile.getFilePath());


    }

    /**
     * Sort the displayed file list by a given column
     *
     * @param col index of column to sort by
     */
    private void sortFileList(int col) {
        
        java.util.Comparator c;

        //Declare comparator to sort by file name
        java.util.Comparator fileCompare = new java.util.Comparator() {
            public int compare(Object o1, Object o2) {

                Integer i = (Integer) o1;
                int i1 = i.intValue();

                i = (Integer) o2;
                int i2 = i.intValue();
                               
                String path1 = analysisControl.getFile(i1).getFilePath();
                String path2 = analysisControl.getFile(i2).getFilePath();

                //Get file names if showing paths is switched off
                if (!jCheckBoxShowFilePaths.isSelected()) {
                    path1 = analysisControl.getFile(i1).getFileName();
                    path2 = analysisControl.getFile(i2).getFileName();
                }
                //Compare the strings ignoring case 
                return path1.compareToIgnoreCase(path2);

            }
        };

        //Declare comparotor to compare by status
        java.util.Comparator statusCompare = new java.util.Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i = (Integer) o1;
                int i1 = i.intValue();

                i = (Integer) o2;
                int i2 = i.intValue();

                //Get the status values for both objects
                int status1 = analysisControl.getFile(i1).getClassification();
                int status2 = analysisControl.getFile(i2).getClassification();

                return status1 - status2;
            }
        };

        //Declare comparator to sort by warnings
        java.util.Comparator warningCompare = new java.util.Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i = (Integer) o1;
                int i1 = i.intValue();

                i = (Integer) o2;
                int i2 = i.intValue();


                String warn1 = analysisControl.getFile(i1).getWarning();
                String warn2 = analysisControl.getFile(i2).getWarning();

                return warn2.compareTo(warn1);
            }
        };

        //Decide which comparator to use depending on whihc column selected
        switch (col) {
            case FILELIST_COL_STATUS:
                c = statusCompare;
                break;
            case FILELIST_COL_FILENAME:
                c = fileCompare;
                break;
            case FILELIST_COL_WARNING:
                c = warningCompare;
                break;
            default:
                c = statusCompare;
                break;

        }

        //Sort the filelist by chosen comparator
        java.util.Collections.sort(fileList, c);
        
        //Refresh the table header , so sort by column is highlighted 
        jTableFileList.getTableHeader().resizeAndRepaint();
        
    }

    /**
     * Sets the preferred , min and max column widths for the FileList
     * and HitList tables
     */
    private void setTableColumnWidths() {
        //Set the status column max and min widths 
        jTableFileList.getColumnModel().getColumn(0).setPreferredWidth(65);
        jTableFileList.getColumnModel().getColumn(0).setMaxWidth(70);
        //jTableFileList.getColumnModel().getColumn(0).setMinWidth(50) ;

        //Set File name column width
        double remainingColWidth = jTableFileList.getWidth() - jTableFileList.getColumnModel().getColumn(0).getWidth();
        double warningColWidth = (double) remainingColWidth * 0.3;

        jTableFileList.getColumnModel().getColumn(1).setMinWidth(250);

        setIdentificationResultsColumnWidths();


    }

    /**
     * Sets the preferred ,min and max column widhts for the hitlist(identification results) table
     */
    private void setIdentificationResultsColumnWidths() {
        //Set Identification results column widths 

        //Format name column
        jTableHitList.getColumnModel().getColumn(2).setMinWidth(200);
        jTableHitList.getColumnModel().getColumn(2).setPreferredWidth(250);
        //Warning column
        jTableHitList.getColumnModel().getColumn(5).setMinWidth(100);
        jTableHitList.getColumnModel().getColumn(5).setPreferredWidth(250);
        //Status Column 
        jTableHitList.getColumnModel().getColumn(4).setMinWidth(100);
        jTableHitList.getColumnModel().getColumn(4).setPreferredWidth(200);
        //Version column
        jTableHitList.getColumnModel().getColumn(3).setMinWidth(50);
        jTableHitList.getColumnModel().getColumn(3).setPreferredWidth(50);
        
    }


    /**
     * Save file list without saving file format hits
     */
    private void saveFileList() {
        saveFileCollection(false);
    }

    /**
     * Save file list with file format hits if they exist
     */
    private void saveResults() {
        saveFileCollection(true);
    }

    /**
     * Shows a file dialog to save file list
     * Queries user if file already exists
     * saves file collection in chosen format to chosen destination
     *
     * @param saveResults saves the file format hits aswell if true
     */
    private void saveFileCollection(boolean saveResults) {

        java.io.File path = null;

        String dialogTitle = " file list";

        if (saveResults) {
            dialogTitle = " results";
        }

        //Setup save file dialog
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.addChoosableFileFilter(new CustomFileFilter(FILE_COLLECTION_FILE_EXTENSTION, FILE_COLLECTION_FILE_DESCRIPTION));
        fc.setAcceptAllFileFilterUsed(false);
        //Set dialog title to same as dialog boxess 
        fc.setDialogTitle(jMenuItemSaveResults.getText());

        //show file dialog
        int returnVal = fc.showSaveDialog(this);

        //Save file if user has chosen a file
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {

            //Get file user selected
            path = fc.getSelectedFile();

            //if no extension was specified add one
            if (!(path.getName().endsWith("." + FILE_COLLECTION_FILE_EXTENSTION))) {
                path = new java.io.File(path.getParentFile(), path.getName() + "." + FILE_COLLECTION_FILE_EXTENSTION);
            }

            //if path exists check confirm with user if they want to overwrite
            if (path.exists()) {
                int option = javax.swing.JOptionPane.showConfirmDialog(this, MSG_OVERWRITE);
                if (option != javax.swing.JOptionPane.YES_OPTION) return;
            }

            //Get filepath of selected file
            final String selectedFilePath = path.getPath();

            //Show in status bar that saving is taking place
            setStatusText("Saving " + selectedFilePath);

            //Save file
            analysisControl.saveFileList(selectedFilePath, saveResults);
            //Show in status file has been saved
            setStatusText("List saved to " + selectedFilePath);
            //Append saved file name to title bar
            this.setTitle(APPLICATION_NAME + " [" + selectedFilePath + "]");

            //Saved flag set to true if saving results

            if (saveResults) {
                fileListSaved = true;
            }


        }//End Save file if user has chosen a file


    }

    /**
     * Export the file list to a CSV file
     */
    private void exportFileListAsCSV() {
        java.io.File path = null;

        //Setup save file dialog
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.addChoosableFileFilter(new CustomFileFilter(CSV_FILE_EXTENSION, CSV_FILE_DESCRIPTION));
        fc.setAcceptAllFileFilterUsed(false);

        //Set dialog title to same as menu item text
        fc.setDialogTitle(jMenuItemExportCSV.getText());

        //show file dialog
        int returnVal = fc.showSaveDialog(this);

        //Save file if user has chosen a file
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {

            //Get file user selected
            path = fc.getSelectedFile();

            //if no extension was specified add one
            if (!(path.getName().endsWith("." + CSV_FILE_EXTENSION))) {
                path = new java.io.File(path.getParentFile(), path.getName() + "." + CSV_FILE_EXTENSION);
            }

            //if path exists check confirm with user if they want to overwrite
            if (path.exists()) {
                int option = javax.swing.JOptionPane.showConfirmDialog(this, MSG_OVERWRITE);
                if (option != javax.swing.JOptionPane.YES_OPTION) return;
            }

            //Get filepath of selected file
            final String selectedFilePath = path.getPath();

            //Show in status bar that saving is taking place
            setStatusText("Exporting as CSV... " + selectedFilePath);

            //Save file
            analysisControl.exportFileCollectionAsCSV(selectedFilePath);
            //Show in status file has been saved
            setStatusText("Exported As CSV to " + selectedFilePath);

        }


    }

    /**
     * Specify the text displayed on the status bar
     *
     * @param statusText Text to display
     */
    public void setStatusText(String statusText) {
        jStatus.setText(statusText);
    }


    /**
     * Specify the text displayed on the status bar , for a given amount of time
     *
     * @param statusText    Text to display
     * @param timeToDisplay Time in ms
     */
    private void setStatusText(String statusText, int timeToDisplay) {
        final String previous = getStatusText();
        setStatusText(statusText);
        timer = new javax.swing.Timer(timeToDisplay, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setStatusText(previous);
                timer.stop();
            }
        });

        timer.start();
    }

    /**
     * Gets the text displayed in the status bar
     *
     * @return text in status bar
     */
    private String getStatusText() {
        return jStatus.getText();
    }


    /**
     * Enables/Disables Cancel button and file menu item depending on whether analysis is running
     */
    private void enableCancelIdentify() {
        boolean enableCancel = identificationRunning;

        jButtonCancel.setEnabled(enableCancel);
        jMenuItemCancelidentify.setEnabled(enableCancel);
    }

    /**
     * Enables/Disables identify button and file menu item depending whether there are files to identify
     */
    private void enableIdentifyActions() {
        boolean enableIdentify = (analysisControl.getNumFiles() > analysisControl.getNumCompletedFiles()) && !identificationRunning;

        jButtonIdentify.setEnabled(enableIdentify);
        jMenuItemIdentify.setEnabled(enableIdentify);
    }

    /**
     * Polls the controller to refresh file list when identification process is running
     */
    private void pollController() {
        //Reset progrss bar
        resetProgressBar();
        //Progress bar set to maximum 
        jProgressIdentification.setMaximum(analysisControl.getNumFiles());

        //Set flag the identification process is running 
        identificationRunning = true;

        //Enable cancel identifcation buttons
        enableCancelIdentify();

        //Every n milliseconds refresh file list and progress bar
        identifyTimer = new javax.swing.Timer(200, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {

                fileListSaved = false;

                int numFiles = analysisControl.getNumFiles();
                int numFilesCompleted = analysisControl.getNumCompletedFiles();

                //Update progress bar
                jProgressIdentification.setValue(numFilesCompleted);
                jProgressIdentification.setString("File " + numFilesCompleted + " of " + numFiles + " analysed");

                //Check if analyis identification process has finished or been cancelled
                if (analysisControl.isAnalysisComplete()) {
                    //Stop the timer
                    identifyTimer.stop();
                    identificationRunning = false;
                    //Update progress bar
                    jProgressIdentification.setValue(numFilesCompleted);
                    jProgressIdentification.setString("File " + numFilesCompleted + " of " + numFiles + " analysed");
                    //refresh file list
                    refreshFileList();
                    //Disable cancel identify buttons
                    enableCancelIdentify();

                    //Alert user that process has completed or has been cancelled
                    String cancelledOrComplete = "Identification complete";
                    if (analysisControl.isAnalysisCancelled()) {
                        cancelledOrComplete = "Identification cancelled";
                    }

                    analysisFinishedMessage(numFilesCompleted + " files analysed", cancelledOrComplete);

                    //Set the first file in the list to be selected and scroll to the top
                    jTableFileList.setRowSelectionInterval(0, 0);
                    jScrollPaneFileList.getVerticalScrollBar().setValue(0);
                }

                //Refresh file list
                refreshFileList();

            }
        });

        identifyTimer.start();

    }

    /**
     * Alerts user that analysis has finished
     *
     * @param message message to tell user
     * @param title   title of message
     */
    private void analysisFinishedMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a print preview frame for current file list
     */
    private void launchPrintPreview() {

        //Only continue if there are files in the file list 
        if (analysisControl.getNumFiles() < 1) {
            JOptionPane.showMessageDialog(this, MSG_PRINT_PREVIEW_FILE_LIST_EMPTY, "File list is empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Set cursor to wait (Egg timer) and put message in status bar
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        setStatusText("Preparing for print preview...");

        //Launch print preiview pane 
        PrintPreview.launchPrintPreview((FileCollection) analysisControl.getFileCollection(),
                (java.util.List) fileList,
                AnalysisController.getDROIDVersion(),
                analysisControl.getSigFileVersion() + "");

        //Reset cursor and status bar text
        setCursor(null);
        setStatusText("");

    }

    /**
     * Prints the file list (without previewing)
     * (Runs in a worker thread) as this can take some time
     */
    private void printFileList() {

        //Only continue if there are files in the file list 
        if (analysisControl.getNumFiles() < 1) {
            JOptionPane.showMessageDialog(this, MSG_PRINT_FILE_LIST_EMPTY, "File list is empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Set cursor to wait (Egg timer)
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        setStatusText("Preparing for printing...");

        //Run in worker thread as this may a considerable amount of time
        final SwingWorker worker = new SwingWorker() {

            public Object construct() {

                //Call print method
                PrintPreview.printPrinterFriendly(analysisControl.getFileCollection(), fileList,
                        AnalysisController.getDROIDVersion(), analysisControl.getSigFileVersion() + "");

                //Reset cursor and status bar text 
                setCursor(null);
                setStatusText("");
                return "";
            }
        };

        //Start the thread
        worker.start();
    }

    /**
     * Launch the help window
     */
    private void launchHelp() {
        JHelp helpViewer = null;
        boolean foundHelpSet = false;

        //Set cursor to wait (Egg timer)
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        setStatusText("Loading help set...");

        // Get the classloader of this class.
        ClassLoader cl = FileIdentificationPane.class.getClassLoader();
        URL url = getClass().getResource("Help/jhelpset.hs");

        try {

            // Create a new JHelp object with a new HelpSet.
            helpViewer = new JHelp(new HelpSet(cl, url));
            // Set the initial entry point in the table of contents.
            helpViewer.setCurrentID("Simple.Introduction");
            foundHelpSet = true;
        }
        catch (Exception e) {
            System.err.println("API Help Set not found");
            System.err.println(e.toString());
            foundHelpSet = false;
            setCursor(null);
            setStatusText("");
            JOptionPane.showMessageDialog(this, "DROID Help set not found at " + url.getPath());
        }

        //If the help set has been found and intialised then show window
        if (foundHelpSet) {
            setStatusText("Lanching help window...");
            // Create a new frame.
            javax.swing.JFrame frame = new javax.swing.JFrame();

            //Set title same as menu item 
            frame.setTitle(jMenuItemHelpContents.getText());

            // Set it's size.
            frame.setSize(500, 500);
            // Add the created helpViewer to it.
            frame.getContentPane().add(helpViewer);
            // Set a default close operation.
            frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            //Set location of help window relative to application window
            frame.setLocationRelativeTo(this);

            try {
                frame.setIconImage(javax.imageio.ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Help Green 16 h g.gif")));
            } catch (java.io.IOException e) {
                //silently ignore exception
            }
            // Make the frame visible.
            frame.setVisible(true);

        }

        setStatusText("");
        setCursor(null);
    }

    /**
     * Exits form but throws up dialog if file list hasn't been saved
     */
    private void exitAndCheckSave() {
        //Check if file list not saved before exiting
        if (!fileListSaved) {
            //If not saved ask user if they would like to save
            int returnval = javax.swing.JOptionPane.showConfirmDialog(this, MSG_EXIT_SAVE_CHECK, "File list not saved", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);
            switch (returnval) {
                case javax.swing.JOptionPane.YES_OPTION:
                    //If YES save results but don't exit
                    saveResults();
                    break;
                case javax.swing.JOptionPane.NO_OPTION:
                    //If no , just exit
                    System.exit(0);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    //if cancel , don't save and don't exit
                    break;


            }

        } else {
            //Exit if file list is saved
            System.exit(0);
        }
    }


    /**
     * For a selected row in file list find the index for the file in
     * reference to its position in the AnalysisController object.
     *
     * @param selectedRow Selected row in jTable
     * @return index for the file
     */
    private int selectedRowToFileIndex(int selectedRow) {
        Integer i = (Integer) fileList.get(selectedRow);
        return i.intValue();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jToolBar1 = new javax.swing.JToolBar();
        jButtonOpenList = new javax.swing.JButton();
        jButtonSaveResults = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();
        jButtonPrintPreview = new javax.swing.JButton();
        jPanelFileIdentification = new javax.swing.JPanel();
        jPanelFileList = new javax.swing.JPanel();
        jPanelAddRemoveButtons = new javax.swing.JPanel();
        jButtonAdd = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonRemoveAll = new javax.swing.JButton();
        jScrollPaneFileList = new javax.swing.JScrollPane();
        jTableFileList = new javax.swing.JTable();
        jPanelActionsAndHits = new javax.swing.JPanel();
        jPanelButtonsAndProgress = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonIdentify = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanelProgress = new javax.swing.JPanel();
        jProgressIdentification = new javax.swing.JProgressBar();
        jPanelFileDetails = new javax.swing.JPanel();
        jPanelHitFileDetails = new javax.swing.JPanel();
        jLabelFileName = new javax.swing.JLabel();
        jTextFieldSelectedFile = new javax.swing.JTextField();
        jPanelIdentificationResults = new javax.swing.JPanel();
        jScrollPaneHitList = new javax.swing.JScrollPane();
        jTableHitList = new HyperLinkTable(analysisControl.getPuidResolutionURL());
        jPanelWarnings = new javax.swing.JPanel();
        jTextPaneNoIDMessage = new javax.swing.JTextPane();
        jPanelStatusBar = new javax.swing.JPanel();
        jStatus = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemStats = new javax.swing.JMenuItem();
        jMenuItemOpenList = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemSaveResults = new javax.swing.JMenuItem();
        jMenuItemExportCSV = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItemPrintPreview = new javax.swing.JMenuItem();
        jMenuItemPrint = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemAdd = new javax.swing.JMenuItem();
        jMenuItemRemove = new javax.swing.JMenuItem();
        jMenuItemRemoveAll = new javax.swing.JMenuItem();
        jMenuIdentify = new javax.swing.JMenu();
        jMenuItemIdentify = new javax.swing.JMenuItem();
        jMenuItemCancelidentify = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemOptions = new javax.swing.JMenuItem();
        jCheckBoxShowFilePaths = new javax.swing.JCheckBoxMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelpContents = new javax.swing.JMenuItem();
        jMenuItemHelpAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("File Format Identification Tool");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jToolBar1.setBorder(new javax.swing.border.EtchedBorder());
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jButtonOpenList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Open File or Folder 24 n g.gif")));
        jButtonOpenList.setToolTipText("Open List...");
        jButtonOpenList.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Open File or Folder 24 d g.gif")));
        jButtonOpenList.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Open File or Folder 24 h g.gif")));
        jButtonOpenList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenListActionPerformed(evt);
            }
        });
        jButtonOpenList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonOpenListMouseEntered(evt);
            }
        });

        jToolBar1.add(jButtonOpenList);

        jButtonSaveResults.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Save Blue 24 n g.gif")));
        jButtonSaveResults.setToolTipText("Save List...");
        jButtonSaveResults.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Save Blue 24 d g.gif")));
        jButtonSaveResults.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Save Blue 24 h g.gif")));
        jButtonSaveResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveResultsActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonSaveResults);

        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Printer 24 n g.gif")));
        jButtonPrint.setToolTipText("Print");
        jButtonPrint.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Printer 16 d g.gif")));
        jButtonPrint.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Printer 24 h g.gif")));
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonPrint);

        jButtonPrintPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 24 n g.gif")));
        jButtonPrintPreview.setToolTipText("Print Preview");
        jButtonPrintPreview.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 24 d g.gif")));
        jButtonPrintPreview.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 24 h g.gif")));
        jButtonPrintPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintPreviewActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonPrintPreview);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanelFileIdentification.setLayout(new java.awt.BorderLayout());

        jPanelFileIdentification.setMinimumSize(new java.awt.Dimension(600, 450));
        jPanelFileIdentification.setPreferredSize(new java.awt.Dimension(750, 400));
        jPanelFileIdentification.setRequestFocusEnabled(false);
        jPanelFileList.setLayout(new java.awt.BorderLayout());

        jPanelFileList.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "File list"), new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 6, 1, 6))));
        jPanelFileList.setMinimumSize(new java.awt.Dimension(500, 40));
        jPanelFileList.setPreferredSize(new java.awt.Dimension(572, 307));
        jButtonAdd.setText("Add Files");
        jButtonAdd.setToolTipText("Add Files");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jPanelAddRemoveButtons.add(jButtonAdd);

        jButtonRemove.setText("Remove Files");
        jButtonRemove.setToolTipText("Remove Files");
        jButtonRemove.setEnabled(false);
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        jPanelAddRemoveButtons.add(jButtonRemove);

        jButtonRemoveAll.setText("Remove All");
        jButtonRemoveAll.setToolTipText("Remove All");
        jButtonRemoveAll.setEnabled(false);
        jButtonRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveAllActionPerformed(evt);
            }
        });

        jPanelAddRemoveButtons.add(jButtonRemoveAll);

        jPanelFileList.add(jPanelAddRemoveButtons, java.awt.BorderLayout.SOUTH);

        jScrollPaneFileList.setBorder(null);
        jScrollPaneFileList.setMaximumSize(new java.awt.Dimension(550, 300));
        jScrollPaneFileList.setPreferredSize(new java.awt.Dimension(550, 235));
        //Set background to white
        jScrollPaneFileList.getViewport().setBackground(java.awt.Color.WHITE);
        jTableFileList.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Status", "File"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTableFileList.setAutoCreateColumnsFromModel(false);
        jTableFileList.getTableHeader().setReorderingAllowed(false);
        jTableFileList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableFileListKeyPressed(evt);
            }
        });

        jScrollPaneFileList.setViewportView(jTableFileList);

        jPanelFileList.add(jScrollPaneFileList, java.awt.BorderLayout.CENTER);

        jPanelFileIdentification.add(jPanelFileList, java.awt.BorderLayout.CENTER);

        jPanelActionsAndHits.setLayout(new java.awt.BorderLayout());

        jPanelButtonsAndProgress.setLayout(new java.awt.BorderLayout());

        jButtonIdentify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Forward or Next 16 n g.gif")));
        jButtonIdentify.setText("Identify");
        jButtonIdentify.setToolTipText("Identify");
        jButtonIdentify.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Forward or Next 16 d g.gif")));
        jButtonIdentify.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Forward or Next 16 h g.gif")));
        jButtonIdentify.setEnabled(false);
        jButtonIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIdentifyActionPerformed(evt);
            }
        });

        jPanelButtons.add(jButtonIdentify);

        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Stop Play 16 n g.gif")));
        jButtonCancel.setText("Cancel");
        jButtonCancel.setToolTipText("Cancel");
        jButtonCancel.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Stop Play 16 d g.gif")));
        jButtonCancel.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Stop Play 16 h g.gif")));
        jButtonCancel.setEnabled(false);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jPanelButtons.add(jButtonCancel);

        jPanelButtonsAndProgress.add(jPanelButtons, java.awt.BorderLayout.NORTH);

        jProgressIdentification.setMinimumSize(new java.awt.Dimension(50, 18));
        jProgressIdentification.setPreferredSize(new java.awt.Dimension(300, 18));
        jProgressIdentification.setString("");
        jProgressIdentification.setStringPainted(true);
        jPanelProgress.add(jProgressIdentification);

        jPanelButtonsAndProgress.add(jPanelProgress, java.awt.BorderLayout.CENTER);

        jPanelActionsAndHits.add(jPanelButtonsAndProgress, java.awt.BorderLayout.NORTH);

        jPanelFileDetails.setLayout(new java.awt.BorderLayout());

        jPanelFileDetails.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Identification results"), new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 6, 6, 6))));
        jPanelFileDetails.setName("File details");
        jPanelFileDetails.setPreferredSize(new java.awt.Dimension(10, 150));
        jPanelFileDetails.setRequestFocusEnabled(false);
        jPanelHitFileDetails.setLayout(new javax.swing.BoxLayout(jPanelHitFileDetails, javax.swing.BoxLayout.X_AXIS));

        jLabelFileName.setText("  File  ");
        jLabelFileName.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelHitFileDetails.add(jLabelFileName);

        jTextFieldSelectedFile.setEditable(false);
        jTextFieldSelectedFile.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldSelectedFile.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(3, 3, 3, 3)), new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102))));
        jPanelHitFileDetails.add(jTextFieldSelectedFile);

        jPanelFileDetails.add(jPanelHitFileDetails, java.awt.BorderLayout.NORTH);

        jPanelIdentificationResults.setLayout(new java.awt.CardLayout());

        jPanelIdentificationResults.setPreferredSize(new java.awt.Dimension(550, 85));
        jScrollPaneHitList.setMaximumSize(new java.awt.Dimension(32767, 15000));
        jScrollPaneHitList.setPreferredSize(new java.awt.Dimension(550, 85));
        //Set background to white
        jScrollPaneHitList.getViewport().setBackground(java.awt.Color.WHITE);
        jTableHitList.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "PUID", "MIME",  "Format", "Version", "Status", "Warning"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTableHitList.setAutoCreateColumnsFromModel(false);
        jTableHitList.getTableHeader().setReorderingAllowed(false);
        jScrollPaneHitList.setViewportView(jTableHitList);

        jPanelIdentificationResults.add(jScrollPaneHitList, "cardResults");

        jPanelWarnings.setLayout(new java.awt.GridLayout(1, 0));

        jPanelWarnings.setBorder(new javax.swing.border.TitledBorder("Error"));
        jTextPaneNoIDMessage.setBorder(null);
        jTextPaneNoIDMessage.setEditable(false);
        jPanelWarnings.add(jTextPaneNoIDMessage);

        jPanelIdentificationResults.add(jPanelWarnings, "cardWarnings");

        jPanelFileDetails.add(jPanelIdentificationResults, java.awt.BorderLayout.SOUTH);

        jPanelActionsAndHits.add(jPanelFileDetails, java.awt.BorderLayout.CENTER);

        jPanelFileIdentification.add(jPanelActionsAndHits, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanelFileIdentification, java.awt.BorderLayout.CENTER);

        jPanelStatusBar.setLayout(new java.awt.GridLayout(1, 0));

        jStatus.setMaximumSize(new java.awt.Dimension(100, 20));
        jStatus.setMinimumSize(new java.awt.Dimension(0, 10));
        jStatus.setPreferredSize(new java.awt.Dimension(100, 20));
        jStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jPanelStatusBar.add(jStatus);

        getContentPane().add(jPanelStatusBar, java.awt.BorderLayout.SOUTH);

        jMenuBar1.setBorder(null);
        jMenuFile.setText("File");
        jMenuItemOpenList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Open File or Folder 16 n g.gif")));
        jMenuItemOpenList.setText("Open List...");
        jMenuItemOpenList.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Open File or Folder 16 d g.gif")));
        jMenuItemOpenList.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Open File or Folder 16 h g.gif")));
        jMenuItemOpenList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenListActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemOpenList);

        jMenuFile.add(jSeparator1);

	  jMenuItemStats.setText("Generate profile");
	  jMenuFile.add(jMenuItemStats);
        jMenuItemStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStatsActionPerformed(evt);
            }
        });

        jMenuFile.add(new javax.swing.JSeparator());

        jMenuItemSaveResults.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Save Blue 16 n g.gif")));
        jMenuItemSaveResults.setText("Save List...");
        jMenuItemSaveResults.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Save Blue 16 d g.gif")));
        jMenuItemSaveResults.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Save Blue 16 h g.gif")));
        jMenuItemSaveResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveResultsActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemSaveResults);

        jMenuItemExportCSV.setText("Export to CSV...");
        jMenuItemExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportCSVActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemExportCSV);

        jMenuFile.add(jSeparator2);

        jMenuItemPrintPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 16 n g.gif")));
        jMenuItemPrintPreview.setText("Print Preview");
        jMenuItemPrintPreview.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 16 d g.gif")));
        jMenuItemPrintPreview.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 16 h g.gif")));
        jMenuItemPrintPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrintPreviewActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemPrintPreview);

        jMenuItemPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Printer 16 n g.gif")));
        jMenuItemPrint.setText("Print...");
        jMenuItemPrint.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Printer 16 d g.gif")));
        jMenuItemPrint.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Printer 16 h g.gif")));
        jMenuItemPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrintActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemPrint);

        jMenuFile.add(jSeparator3);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });

        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuEdit.setText("Edit");
        jMenuItemAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Add Document 2 16 n g.gif")));
        jMenuItemAdd.setText("Add Files\u2026");
        jMenuItemAdd.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Add Document 2 16 d g.gif")));
        jMenuItemAdd.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Add Document 2 16 h g.gif")));
        jMenuItemAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddActionPerformed(evt);
            }
        });

        jMenuEdit.add(jMenuItemAdd);

        jMenuItemRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Delete Document 2 16 n g.gif")));
        jMenuItemRemove.setText("Remove Files");
        jMenuItemRemove.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Delete Document 2 16 d g.gif")));
        jMenuItemRemove.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Delete Document 2 16 h g.gif")));
        jMenuItemRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveActionPerformed(evt);
            }
        });

        jMenuEdit.add(jMenuItemRemove);

        jMenuItemRemoveAll.setText("Remove All");
        jMenuItemRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveAllActionPerformed(evt);
            }
        });

        jMenuEdit.add(jMenuItemRemoveAll);

        jMenuBar1.add(jMenuEdit);

        jMenuIdentify.setText("Identify");
        jMenuItemIdentify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Forward or Next 16 n g.gif")));
        jMenuItemIdentify.setText("Identify");
        jMenuItemIdentify.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Forward or Next 16 d g.gif")));
        jMenuItemIdentify.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Forward or Next 16 h g.gif")));
        jMenuItemIdentify.setEnabled(false);
        jMenuItemIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIdentifyActionPerformed(evt);
            }
        });

        jMenuIdentify.add(jMenuItemIdentify);

        jMenuItemCancelidentify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Stop Play 16 n g.gif")));
        jMenuItemCancelidentify.setText("Cancel");
        jMenuItemCancelidentify.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Stop Play 16 d g.gif")));
        jMenuItemCancelidentify.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Stop Play 16 h g.gif")));
        jMenuItemCancelidentify.setEnabled(false);
        jMenuItemCancelidentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCancelidentifyActionPerformed(evt);
            }
        });

        jMenuIdentify.add(jMenuItemCancelidentify);

        jMenuBar1.add(jMenuIdentify);

        jMenuTools.setText("Tools");
        jMenuItemOptions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Cog 2 16 n g.gif")));
        jMenuItemOptions.setText("Options");
        jMenuItemOptions.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Cog 2 16 d g.gif")));
        jMenuItemOptions.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Cog 2 16 h g.gif")));
        jMenuItemOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOptionsActionPerformed(evt);
            }
        });

        jMenuTools.add(jMenuItemOptions);

        jCheckBoxShowFilePaths.setSelected(true);
        jCheckBoxShowFilePaths.setText("Show File Paths");
        jCheckBoxShowFilePaths.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowFilePathsActionPerformed(evt);
            }
        });

        jMenuTools.add(jCheckBoxShowFilePaths);

        jMenuBar1.add(jMenuTools);

        jMenuHelp.setText("Help");
        jMenuItemHelpContents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Help Green 16 n g.gif")));
        jMenuItemHelpContents.setText("DROID Help");
        jMenuItemHelpContents.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Help Green 16 d g.gif")));
        jMenuItemHelpContents.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Help Green 16 h g.gif")));
        jMenuItemHelpContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpContentsActionPerformed(evt);
            }
        });

        jMenuHelp.add(jMenuItemHelpContents);

        jMenuItemHelpAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Info Round Blue 16 n g.gif")));
        jMenuItemHelpAbout.setText("About DROID");
        jMenuItemHelpAbout.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Info Round Blue 16 d g.gif")));
        jMenuItemHelpAbout.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Info Round Blue 16 h g.gif")));
        jMenuItemHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpAboutActionPerformed(evt);
            }
        });

        jMenuHelp.add(jMenuItemHelpAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

    }//GEN-END:initComponents

    private void jButtonOpenListMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOpenListMouseEntered
        //nothing
    }//GEN-LAST:event_jButtonOpenListMouseEntered

    /**
     * Save file list with results on button click
     *
     * @param evt Action event object
     */
    private void jButtonSaveResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveResultsActionPerformed
        saveResults();
    }//GEN-LAST:event_jButtonSaveResultsActionPerformed

    //Button/Menu Click and key events
    //================================

    /**
     * Open file list on menu item click
     *
     * @param evt Action event object
     */
    private void jButtonOpenListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenListActionPerformed
        openFileList();
    }//GEN-LAST:event_jButtonOpenListActionPerformed

    /**
     * Fires action on any key press on the File list jTable
     *
     * @param evt Key event
     */
    private void jTableFileListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableFileListKeyPressed
        actionOnKeyPress(evt);
    }//GEN-LAST:event_jTableFileListKeyPressed


    /**
     * Removes all files from file list on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveAllActionPerformed
        newFileList();
    }//GEN-LAST:event_jMenuItemRemoveAllActionPerformed

    /**
     *Save file list with results on button click
     *@param evt Action event object
     */
    /**
     * Save file list with results on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemSaveResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveResultsActionPerformed
        saveResults();
    }//GEN-LAST:event_jMenuItemSaveResultsActionPerformed

    /**
     * Export file list as CSV on menu item event
     *
     * @param evt Action event object
     */
    private void jMenuItemExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportCSVActionPerformed
        exportFileListAsCSV();
    }//GEN-LAST:event_jMenuItemExportCSVActionPerformed

    /**
     * Open list on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemOpenListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenListActionPerformed
        openFileList();
    }//GEN-LAST:event_jMenuItemOpenListActionPerformed

    /**
     * Generate statistics on menu item click
     *
     * @param evt Action event object
     */    
    private void jMenuItemStatsActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        openStatsWindow();
    }

    /**
     * Print on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPrintActionPerformed
        printFileList();
    }//GEN-LAST:event_jMenuItemPrintActionPerformed

    /**
     * Print on button click
     *
     * @param evt Action event object
     */
    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        printFileList();
    }//GEN-LAST:event_jButtonPrintActionPerformed

    /**
     * Print preview on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPrintPreviewActionPerformed
        launchPrintPreview();
    }//GEN-LAST:event_jMenuItemPrintPreviewActionPerformed

    /**
     * Print preview on button click
     *
     * @param evt Action event object
     */
    private void jButtonPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintPreviewActionPerformed
        launchPrintPreview();
    }//GEN-LAST:event_jButtonPrintPreviewActionPerformed

    /**
     * Opens help on menu item selected
     *
     * @param evt Action event object
     */
    private void jMenuItemHelpContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpContentsActionPerformed
        launchHelp();
    }//GEN-LAST:event_jMenuItemHelpContentsActionPerformed

    /**
     * Exit form on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        exitAndCheckSave();
    }//GEN-LAST:event_jMenuItemExitActionPerformed


    /**
     * Show about box on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemHelpAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpAboutActionPerformed
        showAboutBox();
    }//GEN-LAST:event_jMenuItemHelpAboutActionPerformed

    /**
     * Remove files on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveActionPerformed
        removeFiles();
    }//GEN-LAST:event_jMenuItemRemoveActionPerformed

    /**
     * Cancel the identification process, if process is running on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemCancelidentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCancelidentifyActionPerformed
        cancelIdentifyFiles();
    }//GEN-LAST:event_jMenuItemCancelidentifyActionPerformed

    /**
     * Intiatiates a file identification run on menu item click
     *
     * @param evt Action event object
     */
    private void jMenuItemIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemIdentifyActionPerformed
        identifyFiles();
    }//GEN-LAST:event_jMenuItemIdentifyActionPerformed

    /**
     * Cancel the identification process, if process is running on button click
     *
     * @param evt Action event object
     */
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        cancelIdentifyFiles();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * Remove files on button click
     *
     * @param evt Action event object
     */
    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        removeFiles();
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    /**
     * Clears the file list on button click
     *
     * @param evt Action event object
     */
    private void jButtonRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveAllActionPerformed
        newFileList();
    }//GEN-LAST:event_jButtonRemoveAllActionPerformed

    /**
     *Opens a file dialog to open a file list on button click 
     *@param evt Action event object
     */
    /**
     * Intiatiates a file identification run on button click
     *
     * @param evt Action event object
     */
    private void jButtonIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIdentifyActionPerformed
        this.identifyFiles();
    }//GEN-LAST:event_jButtonIdentifyActionPerformed

    /**
     * Toggle show file paths on menu click
     *
     * @param evt Action event object
     */
    private void jCheckBoxShowFilePathsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxShowFilePathsActionPerformed
        this.refreshFileList();
    }//GEN-LAST:event_jCheckBoxShowFilePathsActionPerformed

    /**
     * Opens the Options dialog when selected on menu bar under Tools.
     *
     * @param evt Action event object
     */
    private void jMenuItemOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOptionsActionPerformed
        showOptions();
    }//GEN-LAST:event_jMenuItemOptionsActionPerformed

    /**
     * Opens file selection dialog when Add Files button pressed on Toolbar
     *
     * @param evt Action event object
     */
    private void jMenuItemAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddActionPerformed
        addFiles();
    }//GEN-LAST:event_jMenuItemAddActionPerformed

    /**
     * Opens file selection dialog when Add Files button pressed on Toolbar
     *
     * @param evt Action event object
     */
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        addFiles();
    }//GEN-LAST:event_jButtonAddActionPerformed

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        exitAndCheckSave();

    }//GEN-LAST:event_exitForm

    /**
     * Displays the GUI
     *
     * @param ac Object to perform all application functions
     */
    public static void launch(AnalysisController ac) {
    	ac.setVerbose(false);
        new FileIdentificationPane(ac);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonIdentify;
    private javax.swing.JButton jButtonOpenList;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonPrintPreview;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonRemoveAll;
    private javax.swing.JButton jButtonSaveResults;
    private javax.swing.JCheckBoxMenuItem jCheckBoxShowFilePaths;
    private javax.swing.JLabel jLabelFileName;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenu jMenuIdentify;
    private javax.swing.JMenuItem jMenuItemAdd;
    private javax.swing.JMenuItem jMenuItemStats;
    private javax.swing.JMenuItem jMenuItemCancelidentify;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemExportCSV;
    private javax.swing.JMenuItem jMenuItemHelpAbout;
    private javax.swing.JMenuItem jMenuItemHelpContents;
    private javax.swing.JMenuItem jMenuItemIdentify;
    private javax.swing.JMenuItem jMenuItemOpenList;
    private javax.swing.JMenuItem jMenuItemOptions;
    private javax.swing.JMenuItem jMenuItemPrint;
    private javax.swing.JMenuItem jMenuItemPrintPreview;
    private javax.swing.JMenuItem jMenuItemRemove;
    private javax.swing.JMenuItem jMenuItemRemoveAll;
    private javax.swing.JMenuItem jMenuItemSaveResults;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanelActionsAndHits;
    private javax.swing.JPanel jPanelAddRemoveButtons;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelButtonsAndProgress;
    private javax.swing.JPanel jPanelFileDetails;
    private javax.swing.JPanel jPanelFileIdentification;
    private javax.swing.JPanel jPanelFileList;
    private javax.swing.JPanel jPanelHitFileDetails;
    private javax.swing.JPanel jPanelIdentificationResults;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelStatusBar;
    private javax.swing.JPanel jPanelWarnings;
    private javax.swing.JProgressBar jProgressIdentification;
    private javax.swing.JScrollPane jScrollPaneFileList;
    private javax.swing.JScrollPane jScrollPaneHitList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel jStatus;
    private javax.swing.JTable jTableFileList;
    private HyperLinkTable jTableHitList;
    private javax.swing.JTextField jTextFieldSelectedFile;
    private javax.swing.JTextPane jTextPaneNoIDMessage;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    /**********************************************************************
     *NESTED CLASSES
     **********************************************************************
     */

    /**
     * Table model to hold hold the file list data
     */
    private class FileListTableModel extends AbstractTableModel {

        /**
         * Returns number of columns in the table
         * Only two columns required Status and Filename
         *
         * @return number of columns in table
         */
        public int getColumnCount() {
            return 2;
        }

        /**
         * Gets the number of rows displayed in the table
         *
         * @return Number of files held by the filelist
         */
        public int getRowCount() {
            return fileList.size();
        }

        /**
         * Gets the object for a specified cell
         *
         * @param row row id
         * @param col column id
         * @return object to display in cell
         */
        public Object getValueAt(int row, int col) {
            Integer controlIndex = (Integer) fileList.get(row);

            IdentificationFile idFile = analysisControl.getFile(controlIndex.intValue());

            switch (col) {
                case 0:
                    return getStatusIcon(idFile.getClassification());
                case 1:
                    if (jCheckBoxShowFilePaths.isSelected()) {
                        return idFile.getFilePath();
                    } else {

                        return idFile.getFileName();

                    }


            }
            return "Some Value row: " + row + " col: " + col;
        }

        public IdentificationFile getIdentFileAt(int row){
            return analysisControl.getFile((Integer)fileList.get(row));
        }
        
        // public String getColumnName(int column) {return columnNames[column];}

        /**
         * Get the Class for a column
         *
         * @param col column to find class
         * @return Class type found
         */
        public Class getColumnClass(int col) {
            if (col == 0) {
                return javax.swing.ImageIcon.class;
            }

            return String.class;
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
            //DOES NOTHING IN THIS IMPLEMENTATION

        }
    }

    /**
     * Table model to hold hold the File Hits
     */
    private class HitListTableModel extends AbstractTableModel {


        private IdentificationFile idFile;
        private String[] columnNames;

        public HitListTableModel(IdentificationFile idFile) {
            this.idFile = idFile;
            columnNames = new String[]{
                    "PUID", "MIME", "Format", "Version", "Status", "Warning"};
        }

        public String getColumnName(int column) {
            return columnNames[column];
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
            return idFile.getNumHits();
        }

        /**
         * Gets the object for a specified cell
         *
         * @param row row id
         * @param col column id
         * @return object to display in cell
         */
        public Object getValueAt(int row, int col) {

            FileFormatHit hit = idFile.getHit(row);

            switch (col) {
                case 0:
                    return hit.getFileFormatPUID();
                case 1:
                    return hit.getMimeType();
                case 2:
                    return hit.getFileFormatName();
                case 3:
                    return hit.getFileFormatVersion();
                case 4:
                    return hit.getHitTypeVerbose();
                case 5:
                    return hit.getHitWarning();


            }
            return "Some Value row: " + row + " col: " + col;
        }

        // public String getColumnName(int column) {return columnNames[column];}

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
     * Renderer for the file list header to highlight sort by column
     */
    private class FileListHeaderRenderer extends DefaultTableCellRenderer {

        /**
         * Overrides method in DefaultTableCellRender
         * sets the column text to red if that column is used to sort list
         *
         * @param table      table to apply renderer to
         * @param value      object stored in the cell
         * @param isSelected Is cell selected?
         * @param hasFocus   Does cell have focus
         * @param row        cell row in table
         * @param column     cell column in table
         * @return DefaultTableCellRenderer object
         */
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus, int row, int column) {
            // Inherit the colors and font from the header component
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setIcon(null);
                    //Check if column selected 
                    if (column == FileListSortByColumn) {
                        setForeground(java.awt.Color.RED);
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Down16.gif")));

                    }
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                    setText(header.getColumnModel().getColumn(column).getHeaderValue().toString());
                    setBorder(javax.swing.UIManager.getBorder("TableHeader.cellBorder"));
                    setHorizontalAlignment(javax.swing.JLabel.CENTER);
                }
            }


            return this;
        }
    }

    /**
     * Renderer for the file list header to highlight sort by column
     */
    private class CellRenderer extends javax.swing.JLabel implements TableCellRenderer {

        /**
         * This method is called each time a cell in a column
         * using this renderer needs to be rendered.
         *
         * @param table      Table whos cells are to be rendered
         * @param value      object in cell to be rendered
         * @param isSelected Is the cell selected
         * @param hasFocus   Does the cell have focus
         * @param rowIndex   Cells Row position
         * @param vColIndex  Cells column position
         */
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at
            // (rowIndex, vColIndex)

            if (isSelected) {
                this.setOpaque(true);
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());

            } else {
                super.setForeground(table.getForeground());
                super.setBackground(table.getBackground());
            }

            if (hasFocus) {

            }

            //if (value instanceof  javax.swing.ImageIcon){
            //  setIcon((javax.swing.ImageIcon)value) ;
            // }
            //else if (value instanceof  String){ 
            setText((String) value);
            //}


            IdentificationFile idFile = analysisControl.getFile(selectedRowToFileIndex(rowIndex));

            String toolTipText = idFile.getClassificationText();
            if (!idFile.getWarning().equals("")) {
                toolTipText += " (" + idFile.getWarning() + ")";
            }

            toolTipText += " " + idFile.getFilePath();

            // Set tool tip if desired
            setToolTipText(toolTipText);

            // Since the renderer is a component, return itself
            return this;
        }

        // The following methods override the defaults for performance reasons
        public void validate() {
        }

        public void revalidate() {
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }

    /**
     * Renderer for the file list header to highlight sort by column
     */
    private class IconCellRenderer extends javax.swing.JLabel implements TableCellRenderer {

        /**
         * This method is called each time a cell in a column
         * using this renderer needs to be rendered.
         *
         * @param table      Table whos cells are to be rendered
         * @param value      object in cell to be rendered
         * @param isSelected Is the cell selected
         * @param hasFocus   Does the cell have focus
         * @param rowIndex   Cells Row position
         * @param vColIndex  Cells column position
         */
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at
            // (rowIndex, vColIndex)


            if (isSelected) {
                this.setOpaque(true);
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());

            } else {
                super.setForeground(table.getForeground());
                super.setBackground(table.getBackground());
            }

            if (hasFocus) {
                // this cell is the anchor and the table has the focus
            }


            setIcon((javax.swing.ImageIcon) value);
            this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);


            IdentificationFile idFile = analysisControl.getFile(selectedRowToFileIndex(rowIndex));

            String toolTipText = idFile.getClassificationText();
            if (!idFile.getWarning().equals("")) {
                toolTipText += " (" + idFile.getWarning() + ")";
            }

            toolTipText += " " + idFile.getFilePath();

            // Set tool tip if desired
            setToolTipText(toolTipText);

            // Since the renderer is a component, return itself
            return this;
        }

        // The following methods override the defaults for performance reasons
        public void validate() {
        }

        public void revalidate() {
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
    }

    
    
   
}
