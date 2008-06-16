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
 *                  FilePreviewCollection - table model representation of file collection 
 *                                          used to populate the JFreeReport object
 *                 
 *
 * Version      Date        Author      Short Description
 *
 * V1.R0.M0     23-Mar-2005 S.Malik     Created
 * $History: PrintPreview.java $
 * 
 * *****************  Version 15  *****************
 * User: Mals         Date: 20/06/05   Time: 14:42
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Ordering of files same as screen display
 * 
 * *****************  Version 14  *****************
 * User: Mals         Date: 10/05/05   Time: 15:21
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Print Preview icon displayed in titlebar 
 * 
 * *****************  Version 13  *****************
 * User: Mals         Date: 9/05/05    Time: 14:00
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * File identification status boxes increased in size for larger status
 * icons
 * 
 * *****************  Version 12  *****************
 * User: Mals         Date: 3/05/05    Time: 11:36
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Tessella Ref: NPD/4305/PR/IM/2005MAY03/08:51:16
 * 5: I think genuinely unidentified files (i.e. not as a result of an
 * error) should be displayed as an ID result ... The same should be true
 * in the print preview.
 * 
 * *****************  Version 11  *****************
 * User: Mals         Date: 28/04/05   Time: 14:52
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Changed format to match file list and identification results on
 * FileIdentification pane
 * 
 * *****************  Version 10  *****************
 * User: Mals         Date: 20/04/05   Time: 12:18
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Date created written in default format
 * 
 * *****************  Version 9  *****************
 * User: Mals         Date: 18/04/05   Time: 16:53
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Tessella Ref: NPD/4305/PR/IM/2005APR18/09:51:03
 * 39.The Help menu item and information toolbar icon should be removed. I
 * assume we will need to include the JGoodies copyright statement in a
 * Readme.
 * 
 * *****************  Version 8  *****************
 * User: Mals         Date: 13/04/05   Time: 10:20
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Ref:Email from A.Brown NPD/4305/CL/CSC/2005APR12/13:11  File ID GUI
 * +Name of application DROID (Digital Record Object Identification) 
 * 
 * *****************  Version 7  *****************
 * User: Mals         Date: 8/04/05    Time: 9:22
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Disabled log print out messages
 * 
 * *****************  Version 6  *****************
 * User: Mals         Date: 7/04/05    Time: 16:33
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Add application version , signature file version and date to the header
 * of the report
 * 
 * *****************  Version 5  *****************
 * User: Mals         Date: 4/04/05    Time: 9:18
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * No Change - checked in for code sharing
 * 
 * *****************  Version 4  *****************
 * User: Mals         Date: 31/03/05   Time: 15:14
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * +Uses file classification/status images which FileIdentificationPane
 * uses. 
 * 
 * *****************  Version 3  *****************
 * User: Mals         Date: 29/03/05   Time: 17:43
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * No Changes. Check in for code sharing. 
 * 
 * *****************  Version 2  *****************
 * User: Mals         Date: 24/03/05   Time: 16:50
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * +Print can be called without displaying print preview
 * 
 * *****************  Version 1  *****************
 * User: Mals         Date: 24/03/05   Time: 13:18
 * Created in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Creates a print preview display of a uk.FileCollection
 * 
 */
package uk.gov.nationalarchives.droid.GUI.Printing;

import java.awt.geom.Rectangle2D;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.table.AbstractTableModel;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.ImageElement;
import org.jfree.report.JFreeReport;
import org.jfree.report.ShapeElement;
import org.jfree.report.TextElement;
import org.jfree.report.elementfactory.ImageFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewFrame;
import org.jfree.report.modules.gui.base.ReportPane;
import org.jfree.report.style.FontDefinition;
import org.jfree.report.util.Log;
import org.jfree.report.util.ReportConfiguration;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileCollection;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;

import java.util.Iterator;

/**
 * Creates a print preview display of a uk.FileCollection<br>
 * or can directly print without previewing<br>
 * Uses JFreeReport (see http://www.jfree.org/jfreereport) to create print preview <br>
 * <p/>
 * Example of use:
 * <code>
 * //Launches print preview window
 * uk.GUI.Printing.PrintPreview.launchPrintPreview(aFileCollection,...) ;
 * <p/>
 * //or
 * uk.GUI.Printing.PrintPreview.printPrinterFriendly(aFileCollection,...);
 * </code>
 *
 * @author Shahzad Malik
 * @version V1.R0.M.0, 23-Mar-2005
 */
public class PrintPreview {

    /**
     * The file collection to be displayed
     */
    private FileCollection fileList;

    /**
     * Application version number
     */
    private String droidVersion;
    /**
     * Signature file version number
     */
    private String SigFileVersion;

    /**
     * The TableModel object which represents the file collection
     */
    private javax.swing.table.TableModel fileListModel;

    /**
     * List of images used in report
     */
    private java.util.ArrayList statusImages;

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

    //Columns in the table model used to populate the JFreeReport object
    static final int COL_FILE_STATUS = 0;
    static final int COL_FILE_PATH = 1;
    static final int COL_FILE_WARNING = 2;
    static final int COL_HIT_PUID = 3;
    static final int COL_HIT_MIME = 4;
    static final int COL_HIT_FORMAT = 5;
    static final int COL_HIT_VERSION = 6;
    static final int COL_HIT_STATUS = 7;
    static final int COL_HIT_WARNING = 8;

    //Layout column width and positions used create a JFreeReport definition


    /**
     * Spacing between fields in report header
     */
    final int LAYOUT_FIELD_SPACING_REPORT_HEADER = 15;
    final int LAYOUT_VALUE_SPACING_REPORT_HEADER = 5;


    final double LAYOUT_HEADER_HEIGHT_COLUMNS_NAMES = 10.00;
    //Report header layout values
    final int LAYOUT_HEADER_POS_LABEL_FFIT = 0;
    final int LAYOUT_HEADER_WIDTH_LABEL_FFIT = 50;
    final int LAYOUT_HEADER_POS_VALUE_FFIT = LAYOUT_HEADER_POS_LABEL_FFIT +
            LAYOUT_HEADER_WIDTH_LABEL_FFIT +
            LAYOUT_VALUE_SPACING_REPORT_HEADER;
    final int LAYOUT_HEADER_WIDTH_VALUE_FFIT = 50;

    final int LAYOUT_HEADER_POS_LABEL_SIG_FILE = LAYOUT_HEADER_POS_VALUE_FFIT +
            LAYOUT_HEADER_WIDTH_VALUE_FFIT +
            LAYOUT_FIELD_SPACING_REPORT_HEADER;

    final int LAYOUT_HEADER_WIDTH_LABEL_SIG_FILE = 50;
    final int LAYOUT_HEADER_POS_VALUE_SIG_FILE = LAYOUT_HEADER_POS_LABEL_SIG_FILE +
            LAYOUT_HEADER_WIDTH_LABEL_SIG_FILE +
            LAYOUT_VALUE_SPACING_REPORT_HEADER;
    final int LAYOUT_HEADER_WIDTH_VALUE_SIG_FILE = 50;

    final int LAYOUT_HEADER_POS_LABEL_DATE = LAYOUT_HEADER_POS_VALUE_SIG_FILE +
            LAYOUT_HEADER_WIDTH_VALUE_SIG_FILE +
            LAYOUT_FIELD_SPACING_REPORT_HEADER;

    final int LAYOUT_HEADER_WIDTH_LABEL_DATE = 50;
    final int LAYOUT_HEADER_POS_VALUE_DATE = LAYOUT_HEADER_POS_LABEL_DATE +
            LAYOUT_HEADER_WIDTH_LABEL_DATE +
            LAYOUT_VALUE_SPACING_REPORT_HEADER;
    final int LAYOUT_HEADER_WIDTH_VALUE_DATE = 50;


    /**
     * Spacing between columns in page header
     */
    final int LAYOUT_COL_SPACING_PAGE_HEADER = 20;

    final int LAYOUT_COL_POS_STATUS = 0;
    final int LAYOUT_COL_WIDTH_STATUS = 40;

    final int LAYOUT_COL_POS_FILENAME = LAYOUT_COL_POS_STATUS + LAYOUT_COL_WIDTH_STATUS + LAYOUT_COL_SPACING_PAGE_HEADER;
    final int LAYOUT_COL_WIDTH_FILENAME = 350;


    /**
     * Spacing between columns in File format hits header
     */
    private final int LAYOUT_COL_SPACING_HITS = 8;
    /**
     * Row height for file format hits
     */
    private final double LAYOUT_ROW_HEIGHT_HITS = 25;
    private final double LAYOUT_HIT_HEADER_HEIGHT_HITS = 10;
    private final double LAYOUT_ROW_SPACING_HITS = 2;

    private final int LAYOUT_COL_POS_PUID = 10;
    private final int LAYOUT_COL_WIDTH_PUID = 35;
    
    private final int LAYOUT_COL_POS_MIME = LAYOUT_COL_POS_PUID + LAYOUT_COL_WIDTH_PUID + LAYOUT_COL_SPACING_HITS;
    private final int LAYOUT_COL_WIDTH_MIME = 42;

    private final int LAYOUT_COL_POS_HITNAME = LAYOUT_COL_POS_MIME + LAYOUT_COL_WIDTH_MIME + LAYOUT_COL_SPACING_HITS;
    private final int LAYOUT_COL_WIDTH_HITNAME = 150;

    private final int LAYOUT_COL_POS_HITVERSION = LAYOUT_COL_POS_HITNAME + LAYOUT_COL_WIDTH_HITNAME + LAYOUT_COL_SPACING_HITS;
    private final int LAYOUT_COL_WIDTH_HITVERSION = 40;

    private final int LAYOUT_COL_POS_HITSTATUS = LAYOUT_COL_POS_HITVERSION + LAYOUT_COL_WIDTH_HITVERSION + LAYOUT_COL_SPACING_HITS;
    private final int LAYOUT_COL_WIDTH_HITSTATUS = 80;

    private final int LAYOUT_COL_POS_HITWARNING = LAYOUT_COL_POS_HITSTATUS + LAYOUT_COL_WIDTH_HITSTATUS + LAYOUT_COL_SPACING_HITS;
    private final int LAYOUT_COL_WIDTH_HITWARNING = 100;

    //Font Definitions used in report
    /**
     * Font for page header column names
     */
    private FontDefinition fontPageHeader;
    /**
     * Font for file detail field values
     */
    private FontDefinition fontFileFields;
    /**
     * Font for file hit column names
     */
    private FontDefinition fontHitHeader;
    /**
     * Font for file hit field values
     */
    private FontDefinition fontHitFields;
    /**
     * Font for page footer labels
     */
    private FontDefinition fontPageFooterLabel;
    /**
     * Font for page footer values
     */
    private FontDefinition fontPageFooterField;

    /**
     * Font for report header labels
     */
    private FontDefinition fontHeaderLabel;
    /**
     * Font for report header field values
     */
    private FontDefinition fontHeaderField;


    //Page colours
    /**
     * Color for page header column names
     */
    private java.awt.Color colorPageHeaderText;
    /**
     * Color for file format hit row background
     */
    private java.awt.Color colorHitRowBackground;
    /**
     * Color for file field text
     */
    private java.awt.Color colorFileFieldText;
    /**
     * Color for hit column name text
     */
    private java.awt.Color colorHitColumnHeaderText;
    /**
     * Color for hit value text
     */
    private java.awt.Color colorHitFieldText;

    /**
     * Color for footer labels
     */
    private java.awt.Color colorFooterLabel;
    /**
     * Color for footer values
     */
    private java.awt.Color colorFooterValues;

    /**
     * Color for report header labels
     */
    private java.awt.Color colorHeaderLabel;
    /**
     * Color for report header values
     */
    private java.awt.Color colorHeaderValues;


    /**
     * Column names for table model used to populate the JFreeReport object
     * Positions map to COL_FILE_<COL_ID> constants
     */
    private String[] columnNames;

    /**
     * Creates a new instance of PrintPreview
     *
     * @param afileList           Collection of files to populate report
     * @param aorderedFileIndexes Collection of Integer objects which correspond to which position file is in the list
     * @param droidVersion         Application version number
     * @param SigFileVersion      Signature File Version number
     */
    private PrintPreview(FileCollection afileList, List aorderedFileIndexes, String droidVersion, String SigFileVersion) {


        columnNames = new String[]{
                "File Name", "File Status", "File Warning", "Hit PUID", "Hit MIME type", "Hit Format", "Hit Version", "HIT Status", "Hit Warning"};

        this.droidVersion = droidVersion;
        this.SigFileVersion = SigFileVersion;
        fileListModel = new PrintPreview.FilePreviewCollection();
        setStatusImages();

        //Create a copy of the collections, so print preview doesn't change if 
        //files are added/removed/changed 
        //outside of this class 
        this.fileList = new FileCollection();
        
        Iterator<IdentificationFile> files = afileList.getIterator();
        while (files.hasNext()){
            this.fileList.addIdentificationFile(files.next());
        }


    }

    /*
    * Initialise and adds images to the status image list
    */
    private void setStatusImages() {
        statusImages = new java.util.ArrayList();

        try {

            //Create the images
            java.awt.Image imgPositive = ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/positive.GIF"));
            java.awt.Image imgTentitiveHit = ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/tentitive.GIF"));
            java.awt.Image imgNoHit = ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/noHit.GIF"));
            java.awt.Image imgError = ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/error.GIF"));

            //Add the images to the list 
            statusImages.add(imgPositive);
            statusImages.add(imgTentitiveHit);
            statusImages.add(imgNoHit);
            statusImages.add(imgError);


        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.toString());
        }

    }

    /**
     * Get the image for a given file classification status
     * Classifications listed as constants in uk.AnalysisController
     *
     * @param status file identication classification e.g. AnalysisController.FILE_CLASSFICATION_[X]
     * @return image corresponding to given file classification
     */
    private java.awt.Image getStatusImage(int status) {

        java.awt.Image imgReturn = null;
        int statusImagePosition;

        switch (status) {
            case AnalysisController.FILE_CLASSIFICATION_ERROR:
                statusImagePosition = 3;
                break;
            case AnalysisController.FILE_CLASSIFICATION_NOHIT:
                statusImagePosition = 2;
                break;
            case AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED:
                return null;
                //  break ;
            case AnalysisController.FILE_CLASSIFICATION_POSITIVE:
                statusImagePosition = 0;
                break;
            case AnalysisController.FILE_CLASSIFICATION_TENTATIVE:
                statusImagePosition = 1;
                break;
            default:
                return null;
                // break ;

        }

        imgReturn = (java.awt.Image) statusImages.get(statusImagePosition);

        return imgReturn;
    }

    /**
     * Initialises the fonts for the report
     */
    private void setReportStyles() {

        final int FONT_SIZE_FILE_ELEMENT = 9;
        final int FONT_SIZE_HIT_ELEMENT = 8;
        final int FONT_SIZE_PAGE_FOOTER = 6;
        final int FONT_SIZE_REPORT_HEADER = 5;

        fontPageHeader = new FontDefinition("", FONT_SIZE_FILE_ELEMENT, true, false, false, false);
        fontFileFields = new FontDefinition("", FONT_SIZE_FILE_ELEMENT, false, true, false, false);
        fontHitHeader = new FontDefinition("", FONT_SIZE_HIT_ELEMENT, true, false, false, false);
        fontHitFields = new FontDefinition("", FONT_SIZE_HIT_ELEMENT, false, true, false, false);
        fontPageFooterLabel = new FontDefinition("", FONT_SIZE_PAGE_FOOTER, true, false, false, false);
        fontPageFooterField = new FontDefinition("", FONT_SIZE_PAGE_FOOTER, false, true, false, false);

        fontHeaderLabel = new FontDefinition("", FONT_SIZE_REPORT_HEADER, true, false, false, false);
        fontHeaderField = new FontDefinition("", FONT_SIZE_REPORT_HEADER, false, true, false, false);


        float[] RGBtoHSB = new float[3];
        java.awt.Color.RGBtoHSB(203, 13, 7, RGBtoHSB);

        colorPageHeaderText = java.awt.Color.getHSBColor(RGBtoHSB[0], RGBtoHSB[1], RGBtoHSB[2]);

        java.awt.Color.RGBtoHSB(243, 240, 221, RGBtoHSB);
        colorHitRowBackground = java.awt.Color.getHSBColor(RGBtoHSB[0], RGBtoHSB[1], RGBtoHSB[2]);

        colorFileFieldText = java.awt.Color.BLACK;

        colorHitColumnHeaderText = colorPageHeaderText;

        colorHitFieldText = java.awt.Color.BLACK;

        colorFooterLabel = colorPageHeaderText;

        colorFooterValues = java.awt.Color.BLACK;

        colorHeaderLabel = java.awt.Color.BLACK;
        colorHeaderValues = java.awt.Color.BLACK;

    }

    /**
     * Adds info to the page header
     * The header contains the uk application version , the Signature file version and the current date
     */
    private void addVersionInfoHeader(org.jfree.report.PageHeader reportHeader) {


        TextElement applicationVersionLabel = LabelElementFactory.createLabelElement(
                "applicationVersionLabel",
                new Rectangle2D.Double(LAYOUT_HEADER_POS_LABEL_FFIT, 0.0, LAYOUT_HEADER_WIDTH_LABEL_FFIT, 10.0),
                colorHeaderLabel,
                ElementAlignment.MIDDLE,
                fontHeaderLabel,
                "DROID Version"
        );

        TextElement applicationVersion = LabelElementFactory.createLabelElement(
                "applicationVersion",
                new Rectangle2D.Double(LAYOUT_HEADER_POS_VALUE_FFIT, 0.0, LAYOUT_HEADER_WIDTH_VALUE_FFIT, 10.0),
                colorHeaderValues,
                ElementAlignment.MIDDLE,
                fontHeaderField,
                droidVersion
        );

        TextElement sigFileVersionLabel = LabelElementFactory.createLabelElement(
                "sigFileVersionLabel",
                new Rectangle2D.Double(LAYOUT_HEADER_POS_LABEL_SIG_FILE, 0.0, LAYOUT_HEADER_WIDTH_LABEL_SIG_FILE, 10.0),
                colorHeaderLabel,
                ElementAlignment.MIDDLE,
                fontHeaderLabel,
                "Signature File"
        );

        TextElement sigFileVersion = LabelElementFactory.createLabelElement(
                "sigFileVersion",
                new Rectangle2D.Double(LAYOUT_HEADER_POS_VALUE_SIG_FILE, 0.0, LAYOUT_HEADER_WIDTH_VALUE_SIG_FILE, 10.0),
                colorHeaderValues,
                ElementAlignment.MIDDLE,
                fontHeaderField,
                SigFileVersion
        );

        TextElement dateCreatedLabel = LabelElementFactory.createLabelElement(
                "dateCreatedLabel",
                new Rectangle2D.Double(LAYOUT_HEADER_POS_LABEL_DATE, 0.0, LAYOUT_HEADER_WIDTH_LABEL_DATE, 10.0),
                colorHeaderLabel,
                ElementAlignment.MIDDLE,
                fontHeaderLabel,
                "Date Created"
        );

        TextElement dateCreated = LabelElementFactory.createLabelElement(
                "dateCreated",
                new Rectangle2D.Double(LAYOUT_HEADER_POS_VALUE_DATE, 0.0, LAYOUT_HEADER_WIDTH_VALUE_DATE, 10.0),
                colorHeaderValues,
                ElementAlignment.MIDDLE,
                fontHeaderField,
                AnalysisController.writeDisplayDate(new java.util.Date())
        );

        reportHeader.addElement(applicationVersionLabel);
        reportHeader.addElement(applicationVersion);
        reportHeader.addElement(sigFileVersionLabel);
        reportHeader.addElement(sigFileVersion);
        reportHeader.addElement(dateCreatedLabel);
        reportHeader.addElement(dateCreated);


    }

    /**
     * Creates the page header for a JFreeReport object
     * The header contains the column headings
     * File Identification Status , File Path and File Warning
     *
     * @return populated PageHeader object
     */
    private org.jfree.report.PageHeader getPageHeader() {

        org.jfree.report.PageHeader pageHeader = new org.jfree.report.PageHeader();


        TextElement fileStatusLabel = LabelElementFactory.createLabelElement(
                "fileStatusLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_STATUS, 15.0, LAYOUT_COL_WIDTH_STATUS, LAYOUT_HEADER_HEIGHT_COLUMNS_NAMES),
                colorPageHeaderText,
                ElementAlignment.MIDDLE,
                fontPageHeader,
                "Status"
        );

        TextElement fileNameLabel = LabelElementFactory.createLabelElement(
                "fileNameLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_FILENAME, 15.0, LAYOUT_COL_WIDTH_FILENAME, LAYOUT_HEADER_HEIGHT_COLUMNS_NAMES),
                colorPageHeaderText,
                ElementAlignment.MIDDLE,
                fontPageHeader,
                "File"
        );


        pageHeader.addElement(fileStatusLabel);
        pageHeader.addElement(fileNameLabel);


        addVersionInfoHeader(pageHeader);

        return pageHeader;
    }

    /**
     * Creates the page footer
     * Contains the page number
     *
     * @return page footer object for JFreeReport
     */
    private org.jfree.report.PageFooter getPageFooter() {
        org.jfree.report.PageFooter pageFooter = new org.jfree.report.PageFooter();

        TextElement pageLabel = LabelElementFactory.createLabelElement(
                "fileWarningLabel",
                new Rectangle2D.Double(0.0, 0.0, 30, 20.0),
                colorFooterLabel,
                ElementAlignment.MIDDLE,
                fontPageFooterLabel,
                "Page"
        );

        TextElement pageNumberLabel = TextFieldElementFactory.createStringElement(
                "HitStatus",
                new Rectangle2D.Double(50, 0.0, 50, 20.0),
                colorFooterValues,
                ElementAlignment.MIDDLE,
                fontPageFooterField, // font
                "-", // null string
                "page_number"
        );

        pageFooter.addElement(pageLabel);
        pageFooter.addElement(pageNumberLabel);
        return pageFooter;
    }

    /**
     * Creates a Group for the report, with Group header and footer filled in
     * The report is grouped by file path
     * The file path, file identification status and file warning fields
     * are displayed in the group header
     *
     * @return populated Group where grouping is on file path
     */
    private org.jfree.report.Group getGroup() {

        /**********************************************
         *Set Grouping on File
         ************************************************/

        //Group rows by File Name 
        Group fileGroup = new Group();
        fileGroup.addField(columnNames[COL_FILE_PATH]);

        /**********************************************
         *Create Group Header
         ************************************************/

        //File  Elements used in Group Header 

        ImageElement fileStatusImageElement = ImageFieldElementFactory.createImageDataRowElement(
                "Status image element",
                new Rectangle2D.Double(LAYOUT_COL_POS_STATUS, 10.0, 21.0, 24.0),
                columnNames[COL_FILE_STATUS],
                false
        );


        TextElement fileStatusElement = TextFieldElementFactory.createStringElement(
                "fileStatusElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_STATUS, 10.0, LAYOUT_COL_WIDTH_STATUS, 20.0),
                colorFileFieldText,
                ElementAlignment.MIDDLE,
                fontFileFields, // font
                "-", // null string
                columnNames[COL_FILE_STATUS]
        );

        TextElement fileNameElement = TextFieldElementFactory.createStringElement(
                "fileNameElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_FILENAME, 10.0, LAYOUT_COL_WIDTH_FILENAME, 20.0),
                colorFileFieldText,
                ElementAlignment.MIDDLE,
                fontFileFields, // font
                "-", // null string
                columnNames[COL_FILE_PATH]
        );

        //Labeling elements used in Header
        //Column headers for File Hits 

        //Label for when file is a Error status
        TextElement FileErrorWarningLabel = LabelElementFactory.createLabelElement(
                "FileError",
                new Rectangle2D.Double(LAYOUT_COL_POS_PUID, 30, LAYOUT_COL_WIDTH_PUID + 50, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                TITLE_WARNING_BOX_ERROR
        );

        //Label for when file is a not Identified(No hits) status 
        TextElement FileNotIdentifiedWarningLabel = LabelElementFactory.createLabelElement(
                "FileNotIdentified",
                new Rectangle2D.Double(LAYOUT_COL_POS_PUID, 30, LAYOUT_COL_WIDTH_PUID + 50, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                TITLE_WARNING_BOX_NOT_IDENTIFIED
        );

        TextElement PUIDLabel = LabelElementFactory.createLabelElement(
                "PUIDLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_PUID, 30, LAYOUT_COL_WIDTH_PUID, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                "PUID"
        );
        
        TextElement MIMELabel = LabelElementFactory.createLabelElement(
                "MIMELabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_MIME, 30, LAYOUT_COL_WIDTH_MIME, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                "MIME Type"
        );

        TextElement HitNameLabel = LabelElementFactory.createLabelElement(
                "NameLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITNAME, 30, LAYOUT_COL_WIDTH_HITNAME, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                "Format"
        );

        TextElement HitStatusLabel = LabelElementFactory.createLabelElement(
                "StatusLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITSTATUS, 30, LAYOUT_COL_WIDTH_HITSTATUS, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                "Status"
        );

        TextElement HitVersionLabel = LabelElementFactory.createLabelElement(
                "VersionLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITVERSION, 30, LAYOUT_COL_WIDTH_HITVERSION, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                "Version"
        );

        TextElement HitWarningLabel = LabelElementFactory.createLabelElement(
                "WarningLabel",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITWARNING, 30, LAYOUT_COL_WIDTH_HITWARNING, LAYOUT_HIT_HEADER_HEIGHT_HITS),
                colorHitColumnHeaderText,
                ElementAlignment.MIDDLE,
                fontHitHeader,
                "Warning"
        );

        //Add Elements to group header
        // fileGroup.getHeader().addElement(fileStatusElement) ;
        fileGroup.getHeader().addElement(fileStatusImageElement);
        fileGroup.getHeader().addElement(fileNameElement);


        fileGroup.getHeader().addElement(PUIDLabel);
        fileGroup.getHeader().addElement(MIMELabel);
        fileGroup.getHeader().addElement(HitNameLabel);
        fileGroup.getHeader().addElement(HitStatusLabel);
        fileGroup.getHeader().addElement(HitVersionLabel);
        fileGroup.getHeader().addElement(HitWarningLabel);

        fileGroup.getHeader().addElement(FileErrorWarningLabel);
        fileGroup.getHeader().addElement(FileNotIdentifiedWarningLabel);

        //Group header repeats on new pages
        fileGroup.getHeader().setRepeat(false);

        /**********************************************
         *Create Group Footer
         ************************************************/
        ShapeElement groupFooterLine = StaticShapeElementFactory.createHorizontalLine(
                "GroupFootetLine",
                java.awt.Color.BLACK,
                new java.awt.BasicStroke(),
                10.00

        );

        //fileGroup.getFooter().addElement(groupFooterLine) ; 

        //return Group
        return fileGroup;

    }

    /**
     * This represents one row from the data for the report
     * Each itemband row will display Hit Fields - PUID, Format Name , Version , Status and Warning
     *
     * @return intialised ItemBand object
     */
    private org.jfree.report.ItemBand getItemBand() {
        org.jfree.report.ItemBand itemBand = new org.jfree.report.ItemBand();


        ShapeElement shpItemRow = StaticShapeElementFactory.createRectangleShapeElement(
                "rowBackground",
                colorHitRowBackground,
                new java.awt.BasicStroke(),
                new Rectangle2D.Double(0, 0.0, LAYOUT_COL_POS_HITWARNING + LAYOUT_COL_WIDTH_HITWARNING, LAYOUT_ROW_HEIGHT_HITS - 1),
                false, //- a flag controlling whether or not the shape outline is drawn.
                true //- a flag controlling whether or not the shape interior is filled.
        );

        TextElement FileWarningElement = TextFieldElementFactory.createStringElement(
                "FileWarning",
                new Rectangle2D.Double(LAYOUT_COL_POS_PUID, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_PUID + 100, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_FILE_WARNING]
        );


        TextElement HitPUIDFieldElement = TextFieldElementFactory.createStringElement(
                "HitPUIDFieldElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_PUID, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_PUID, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_HIT_PUID]
        );
        
        TextElement HitMIMEFieldElement = TextFieldElementFactory.createStringElement(
                "HitMIMEFieldElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_MIME, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_MIME, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_HIT_MIME]
        );


        TextElement HitNameFieldElement = TextFieldElementFactory.createStringElement(
                "HitNameFieldElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITNAME, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_HITNAME, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_HIT_FORMAT]
        );

        TextElement HitStatusFieldElement = TextFieldElementFactory.createStringElement(
                "HitStatusFieldElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITSTATUS, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_HITSTATUS, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_HIT_STATUS]
        );

        TextElement HitVersionFieldElement = TextFieldElementFactory.createStringElement(
                "HitVersionFieldElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITVERSION, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_HITVERSION, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_HIT_VERSION]
        );

        TextElement HitWarningFieldElement = TextFieldElementFactory.createStringElement(
                "HitWarningFieldElement",
                new Rectangle2D.Double(LAYOUT_COL_POS_HITWARNING, LAYOUT_ROW_SPACING_HITS, LAYOUT_COL_WIDTH_HITWARNING, LAYOUT_ROW_HEIGHT_HITS),
                colorHitFieldText,
                ElementAlignment.MIDDLE,
                fontHitFields, // font
                "-", // null string
                columnNames[COL_HIT_WARNING]
        );

        itemBand.addElement(shpItemRow);
        itemBand.addElement(HitPUIDFieldElement);
        itemBand.addElement(HitMIMEFieldElement);
        itemBand.addElement(HitNameFieldElement);
        itemBand.addElement(HitStatusFieldElement);
        itemBand.addElement(HitVersionFieldElement);
        itemBand.addElement(HitWarningFieldElement);

        itemBand.addElement(FileWarningElement);

        return itemBand;
    }


    /**
     * Creates the report definition to display in the print preview frame
     *
     * @return defined JFreeReport object
     */
    private JFreeReport createReportDefinition() {

        JFreeReport report = new JFreeReport();

        report.getReportConfiguration().setDisableLogging(true);

        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.NoDefaultDebug", "true");

        //Set report configuration
        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.modules.gui.xls.Enable", "false");
        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.modules.gui.csv.Enable", "false");
        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.modules.gui.html.Enable", "false");
        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.modules.gui.plaintext.Enable", "false");
        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.modules.gui.rtf.Enable", "false");
        //Remove help and about box 
        ReportConfiguration.getGlobalConfig().setConfigProperty("org.jfree.report.modules.gui.base.About", "disable");
        //Set Report Properties
        report.setName("Print Preview");
        org.jfree.report.function.PageFunction pf = new org.jfree.report.function.PageFunction();
        pf.setName("page_number");
        report.addExpression(pf);

        //Add the function to show/hide format hits
        IdResultsFunction irf = new IdResultsFunction();
        report.addExpression(irf);

        //Set and intialise report fonts
        setReportStyles();

        //Set Page Header
        report.setPageHeader(getPageHeader());

        //Set Page Footer
        report.setPageFooter(getPageFooter());

        //Set Group and GroupHeader
        report.addGroup(getGroup());

        //Set ItemBand 
        report.setItemBand(getItemBand());

        return report;
    }

    /**
     * Launch a print preivew frame displaying the file list
     *
     * @param fc                   file collection to display
     * @param orderedFiles         Collection of Integer objects which correspond to which position file is in the list
     * @param ApplicationVersion   Version number of uk application
     * @param SignatureFileVersion Version number of Signature file
     */
    public static void launchPrintPreview(FileCollection fc, List orderedFiles, String ApplicationVersion, String SignatureFileVersion) {
        PrintPreview p = new PrintPreview(fc, orderedFiles, ApplicationVersion, SignatureFileVersion);
        p.showPrintPreview();
    }

    /**
     * Print a given file list
     *
     * @param fc                   file collection to print
     * @param orderedFiles         Collection of Integer objects which correspond to which position file is in the list
     * @param ApplicationVersion   Version number of uk application
     * @param SignatureFileVersion Version number of Signature file
     */
    public static void printPrinterFriendly(FileCollection fc, List orderedFiles, String ApplicationVersion, String SignatureFileVersion) {
        PrintPreview p = new PrintPreview(fc, orderedFiles, ApplicationVersion, SignatureFileVersion);
        p.printDirecty();
    }

    /**
     * Print this objects file collection
     */
    private void printDirecty() {
        ReportPane reportPane;
        PrinterJob printerJob;

        //Create the report definition and set its data
        JFreeReport report = createReportDefinition();
        report.setData(this.fileListModel);

        //Create the report pane used for printing
        try {
            reportPane = new ReportPane(report);


        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error setting report: " + e.toString());
            return;
        }

        //Setup print job 
        printerJob = PrinterJob.getPrinterJob();
        printerJob.setPageable(reportPane);

        //Show Print Dialog 
        if (printerJob.printDialog()) {
            //user has selected print  
            try {
                Log.debug("about to print...");
                //Set print job name
                printerJob.setJobName("DROID analysis");
                //print file list
                printerJob.print();
            }
            catch (PrinterException exception) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error printing: " + exception.toString());
                Log.error("Printing error", exception);
            }

        }


    }


    /**
     * shows the print preview frame
     */
    private void showPrintPreview() {

        try {

            //Create report 
            org.jfree.report.JFreeReport report = createReportDefinition();
            //Set the reports data
            report.setData(this.fileListModel);
            //Create frame to preview report
            PreviewFrame frame = new PreviewFrame(report);
            //Pack , display and give focus 
            frame.pack();
            //Set the icon for the print preview window
            frame.setIconImage(javax.imageio.ImageIO.read(getClass().getResource("/uk/gov/nationalarchives/droid/GUI/Icons/Print Preview 16 h g.gif")));
            frame.setVisible(true);
            frame.requestFocus();
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }


    /**
     * Represents a uk.FileCollection as a TableModel to populate a JFreeReportObject
     * Each row represents a hit for a file in the collection
     * if the item in the collection does not contain a hit , it still appears in the table
     * but the hit fieilds(format name, puid and etc..) are empty.
     */
    private class FilePreviewCollection extends AbstractTableModel {

        /**
         * Calculates number of columns in the table
         *
         * @return number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Finds the column name for a specified column position
         *
         * @param column column position of column name to find
         * @return found column name
         */
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * Calculates number of rows in the table
         *
         * @return number of rows
         */
        public int getRowCount() {

            int rowCount = 0;
            IdentificationFile file = null;
            FileFormatHit hit = null;
            
            java.util.Iterator<IdentificationFile> files = fileList.getIterator();
            while (files.hasNext()){
                file = files.next();

                if (file.getNumHits() > 0) {
                    for (int hitCount = 0; hitCount < file.getNumHits(); hitCount++) {
                        rowCount++;
                    }
                } else {
                    rowCount++;
                }
            }

            return rowCount;


        }

        /**
         * Finds object which should be displayed in the table
         *
         * @param rowIndex    row position of object to find
         * @param columnIndex column position of object to find
         * @return object to display
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            int rowCount = -1;
            Integer filePosition;
            IdentificationFile file;
            FileFormatHit hit = null;
            
            Iterator<IdentificationFile> fileIterator = fileList.getIterator();
            while (fileIterator.hasNext()){
                
                file = fileIterator.next();

                if (file.getNumHits() > 0) {
                    for (int hitCount = 0; hitCount < file.getNumHits(); hitCount++) {
                        hit = file.getHit(hitCount);
                        rowCount++;

                        if (rowCount == rowIndex) {
                            switch (columnIndex) {
                                case COL_FILE_STATUS:
                                    return getStatusImage(file.getClassification());
                                case COL_FILE_PATH:
                                    return file.getFilePath();
                                case COL_FILE_WARNING:
                                    return replaceEmpty(file.getWarning(), "");
                                case COL_HIT_FORMAT:
                                    return hit.getFileFormatName();
                                case COL_HIT_STATUS:
                                    return hit.getHitTypeVerbose();
                                case COL_HIT_VERSION:
                                    return hit.getFileFormatVersion();
                                case COL_HIT_PUID:
                                    return hit.getFileFormatPUID();
                                case COL_HIT_WARNING:
                                    return hit.getHitWarning();
                                case COL_HIT_MIME:
                                	return hit.getMimeType();
                                default:
                                    return "";
                            }
                        }

                    }

                } else {
                    rowCount++;
                    if (rowCount == rowIndex) {
                        switch (columnIndex) {
                            case COL_FILE_STATUS:

                                return getStatusImage(file.getClassification());
                            case COL_FILE_PATH:
                                return file.getFilePath();
                            case COL_FILE_WARNING:
                                //If the format has no hits , show this message in the warning
                                if (file.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOHIT) {
                                    return MSG_UNIDENTIFIED;
                                }
                                return replaceEmpty(file.getWarning(), "");
                            case COL_HIT_FORMAT:
                                return file.getClassificationText();
                            default:
                                return "";
                        }
                    }
                }


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
            if (col == 0) {
                return java.awt.Image.class;

            }

            return String.class;
        }

        /**
         * Returns given string with some other text if String is null or empty i.e. ""
         *
         * @param toReplace   String to query
         * @param replaceText String to return if other string is empty or null
         * @return given string with some other text if String is null or empty i.e. ""
         */
        private String replaceEmpty(String toReplace, String replaceText) {


            if (toReplace == null) {
                return replaceText;
            }

            if (toReplace.trim() == "") {
                return replaceText;
            }

            return toReplace;
        }


    }


}
