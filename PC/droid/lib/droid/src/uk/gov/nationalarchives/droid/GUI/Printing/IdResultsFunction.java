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
 * $History: IdResultsFunction.java $
 * 
 * *****************  Version 2  *****************
 * User: Mals         Date: 3/05/05    Time: 11:36
 * Updated in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * Tessella Ref: NPD/4305/PR/IM/2005MAY03/08:51:16
 * 5: I think genuinely unidentified files (i.e. not as a result of an
 * error) should be displayed as an ID result ... The same should be true
 * in the print preview.
 * 
 * *****************  Version 1  *****************
 * User: Mals         Date: 28/04/05   Time: 14:50
 * Created in $/PRONOM4/FFIT_SOURCE/GUI/Printing
 * JFreeReport function to show Identification Hits when it is a positive
 * hit and shows file warning if it is an error or not identified 
 * 
 */
package uk.gov.nationalarchives.droid.GUI.Printing;

import org.jfree.report.Group;
import org.jfree.report.ReportDefinition;
import org.jfree.report.event.ReportEvent;
import org.jfree.report.function.AbstractFunction;

import uk.gov.nationalarchives.droid.AnalysisController;

/**
 * JFreeReport function to show Identification Hits when it is a positive hit
 * and shows file warning if it is an error or not identified.
 * <p/>
 * <p/>
 * <h1> Example of use </h1>
 * <pre>
 *     //Add the function to show/hide format hits
 *      uk.GUI.Printing.IdResultsFunction irf = new uk.GUI.Printing.IdResultsFunction() ;
 *      report.addExpression(irf);
 *  </pre>
 *
 * @author Shahzad Malik
 * @version V1.R0.M.0, 23-Mar-2005
 */
public class IdResultsFunction extends AbstractFunction {


    /**
     * Show PUID,Format Name,Status, etc column headings if file format hits exist,
     * otherwise show file warning in that space
     */
    public void groupStarted(ReportEvent event) {
        ReportDefinition rdef = event.getReport();
        Group currentGroup = event.getReport().getGroup(1);

        //Get the format name 
        String formatName = (String) event.getDataRow().get(PrintPreview.COL_HIT_FORMAT);

        //Decide whether the headings should be shown
        //Only show if it is not an Error status or No hits status
        boolean showGroupHeader = !((formatName.equals(AnalysisController.FILE_CLASSIFICATION_ERROR_TEXT))
                || (formatName.equals(AnalysisController.FILE_CLASSIFICATION_NOHIT_TEXT)));

        //Show/Hide headings
        currentGroup.getHeader().getElement("PUIDLabel").setVisible(showGroupHeader);
        currentGroup.getHeader().getElement("MIMELabel").setVisible(showGroupHeader);
        currentGroup.getHeader().getElement("NameLabel").setVisible(showGroupHeader);
        currentGroup.getHeader().getElement("StatusLabel").setVisible(showGroupHeader);
        currentGroup.getHeader().getElement("VersionLabel").setVisible(showGroupHeader);
        currentGroup.getHeader().getElement("WarningLabel").setVisible(showGroupHeader);

        //Show this label only if file is an error status
        currentGroup.getHeader().getElement("FileError").setVisible(formatName.equals(AnalysisController.FILE_CLASSIFICATION_ERROR_TEXT));

        //Show this label only if file is not identified
        currentGroup.getHeader().getElement("FileNotIdentified").setVisible(formatName.equals(AnalysisController.FILE_CLASSIFICATION_NOHIT_TEXT));

        //Show/Hide fields
        event.getReport().getItemBand().getElement("HitPUIDFieldElement").setVisible(showGroupHeader);
        event.getReport().getItemBand().getElement("HitMIMEFieldElement").setVisible(showGroupHeader);
        event.getReport().getItemBand().getElement("HitStatusFieldElement").setVisible(showGroupHeader);
        event.getReport().getItemBand().getElement("HitVersionFieldElement").setVisible(showGroupHeader);
        event.getReport().getItemBand().getElement("HitWarningFieldElement").setVisible(showGroupHeader);

        //Show this only when it is not an Error or No Hits status
        event.getReport().getItemBand().getElement("HitNameFieldElement").setVisible(!formatName.equals(AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED_TEXT) && showGroupHeader);

        //Show this field only if others hidden, othewise hide
        event.getReport().getItemBand().getElement("FileWarning").setVisible(!showGroupHeader);
    }


    /**
     * Implemented as this is an absract method in <Code>AbstractFunction</Code>
     */
    public Object getValue() {
        return new Object();
    }


}