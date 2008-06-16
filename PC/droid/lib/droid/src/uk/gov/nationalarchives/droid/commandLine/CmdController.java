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
 * $Id: CmdController.java,v 1.7 2006/03/13 15:15:26 linb Exp $
 * 
 * $Log: CmdController.java,v $
 * Revision 1.7  2006/03/13 15:15:26  linb
 * Changed copyright holder from Crown Copyright to The National Archives.
 * Added reference to licence.txt
 * Changed dates to 2005-2006
 *
 * Revision 1.6  2006/02/13 11:27:29  linb
 * - Correct spelling in error message
 *
 * Revision 1.5  2006/02/08 12:03:37  linb
 * - add more comments
 *
 * Revision 1.4  2006/02/08 11:45:48  linb
 * - add support for streams
 *
 * Revision 1.3  2006/02/08 08:56:35  linb
 * - Added header comments
 *
 *
 * $History: CmdController.java $
 * 
 * *****************  Version 8  *****************
 * User: Walm         Date: 20/10/05   Time: 15:31
 * Updated in $/PRONOM4/FFIT_SOURCE/commandLine
 * When using the web service, check whether or not connection was
 * successful, and if not, display a helpful message to user
 * 
 * *****************  Version 7  *****************
 * User: Walm         Date: 12/05/05   Time: 12:12
 * Updated in $/PRONOM4/FFIT_SOURCE/commandLine
 * read configuration file when running -C and -D options
 * 
 * *****************  Version 6  *****************
 * User: Walm         Date: 5/05/05    Time: 10:29
 * Updated in $/PRONOM4/FFIT_SOURCE/commandLine
 * Save to file at end of run has been moved to setAnalysisComplete() in
 * the AnalysisController
 * 
 * *****************  Version 5  *****************
 * User: Walm         Date: 26/04/05   Time: 17:29
 * Updated in $/PRONOM4/FFIT_SOURCE/commandLine
 * change name from uk to DROID
 * 
 * *****************  Version 4  *****************
 * User: Walm         Date: 5/04/05    Time: 18:08
 * Updated in $/PRONOM4/FFIT_SOURCE/commandLine
 * review headers
 * 
 * *****************  Version 3  *****************
 * User: Walm         Date: 31/03/05   Time: 15:26
 * Updated in $/PRONOM4/FFIT_SOURCE/commandLine
 * Download a signature file
 * + some changes of output for other options
 *
 * Created on 15 March 2005, 14:43
 */

package uk.gov.nationalarchives.droid.commandLine;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.messageDisplay;
import uk.gov.nationalarchives.droid.binFileReader.UrlByteReader;
import uk.gov.nationalarchives.droid.xmlReader.PronomWebService;
import uk.gov.nationalarchives.droid.StatsThread;

/**
 * @author walm
 */
public class CmdController {    
    static final String myArgumentHelp =
            "The arguments should be \n" +
                    "    Display the command line options\n" +
                    "        -H\n" +
                    "    Display the analysis code version\n" +
                    "        -V\n" +
                    "    Display the signature file version\n" +
                    "        -Vxxx  where xxx is the signature file name\n" +
                    "    Check whether the PRONOM website has a newer signature file\n" +
                    "        -Cxxx  where xxx is the local signature file\n" +
                    "    Download the latest signature file from PRONOM website\n" +
                    "        -Dxxx  where xxx is the name of the file to download to\n" +
                    "    Quiet mode: only print errors to the command line\n" +
                    "        -Q\n" +
                    "    Run the analysis on a comma separated list of files\n" +
                    "        -Lxxx  where xxx is a comma-separated list of files\n" +
                    "        -Syyy  where yyy is the signature file\n" +
                    "        -Ozzz  where zzz is the base name for the output files\n" +
                    "        -Faaa  where aaa is a comma-separated list of output formats from XML,CSV\n" +
                    "    Run the analysis on the files in a file list file\n" +
                    "        -Axxx  where xxx is the file list file\n" +
                    "        -Syyy  where yyy is the signature file\n" +
                    "        -Ozzz  where zzz is the base name for the output files\n" +
                    "        -Faaa  where aaa is a comma-separated list of output formats from XML,CSV\n" + 
                    "    Generate statistics profile on a comma separated list of files\n" +
                    "        -Mxxx  where xxx is a comma-separated list of files\n" +
                    "        -Syyy  where yyy is the signature file\n" +
                    "        -Ozzz  where zzz is the base name for the output files\n" +
                    "        -Faaa  where aaa is a comma-separated list of output formats from XML,CSV\n" +
                    "    Generate statistics profile on the files in a file list file\n" +
                    "        -Nxxx  where xxx is the file list file\n" +
                    "        -Syyy  where yyy is the signature file\n" +
                    "        -Ozzz  where zzz is the base name for the output files\n" +
                    "        -Faaa  where aaa is a comma-separated list of output formats from XML,CSV";

    //These are the possible actions requested
    boolean isHelpDisplay = false;  //Display argument options (args must be -H)
    boolean isAnalysisVersionDisplay = false;  //Display system version (args must be -V)
    boolean isSigFileVersionDisplay = false;  //Display signature file version (args must be -S)
    boolean isSigFileCheck = false;  //Checks whether a more recent signature file is available (args must be -C -S)
    boolean isDownload = false;  //Download a signature file (args must be -D)
    boolean isAnalysis = false;  //Run the analysis (args must be -A -S -O -F or -L -S -O -F)
    boolean isStats = true;
    
    AnalysisController myAnalysis;
    
    String[] nonCollectionFiles; //Array of selected filenames not stored in a FileCollection (for stats generation)
    
    /**
     * Creates a new instance of CmdController
     */
    public CmdController(String[] theArgs) {

        myAnalysis = new AnalysisController();
        this.parseArgs(theArgs);
    }

    private void parseArgs(String[] theArgs) {
        boolean isWellFormed = true;
        String theSignatureFile = "";
        String theListFileName = "";
        String theOutputFile = "DROID_out";
        String theOutputFormats = "XML";
        boolean isXMLOutput = false;
        boolean isCSVOutput = false;
        String proxyHost = null;
        String proxyPort = null;

        //parse arguments
        for (int i = 0; i < theArgs.length; i++) {
            if (!theArgs[i].substring(0, 1).equals("-")) {
                isWellFormed = false;
                break;
            }
            if (theArgs[i].substring(1, 2).equals("V")) {
                theSignatureFile = theArgs[i].substring(2);
                if (theSignatureFile.length() == 0) {
                    isAnalysisVersionDisplay = true;
                } else {
                    isSigFileVersionDisplay = true;
                }
            } else if (theArgs[i].substring(1, 2).equals("C")) {
                theSignatureFile = theArgs[i].substring(2);
                isSigFileCheck = true;
            } else if (theArgs[i].substring(1, 2).equals("D")) {
                theSignatureFile = theArgs[i].substring(2);
                isDownload = true;
            } else if (theArgs[i].substring(1, 2).equals("L")) {
                String[] files = theArgs[i].substring(2).split(",");
                for (int j = 0; j < files.length; j++) {
                    String fileName = files[j];
                    if (UrlByteReader.isURL(fileName) && proxyHost == null) {
                        /* Get proxy settings from configuration file, if not already set */
                        try {
                            myAnalysis.readConfiguration();
                            proxyHost = myAnalysis.getProxyHost();
                            proxyPort = Integer.toString(myAnalysis.getProxyPort());
                            if (!"".equals(proxyHost)) {
                                System.setProperty("http.proxyHost", proxyHost);
                                System.setProperty("http.proxyPort", proxyPort);
                            }
                        } catch (Exception e) {
                            messageDisplay.generalInformation("Error reading the congifuration file: " + AnalysisController.CONFIG_FILE_NAME + "\n" + e.getMessage());
                        }
                    }

                    myAnalysis.addFile(fileName);
                }
                isAnalysis = true;
            } else if (theArgs[i].substring(1, 2).equals("M")) {
                // Comma-separated list of files for stats
                String[] files = theArgs[i].substring(2).split(",");
                this.nonCollectionFiles = files;
                
                // If any of the filenames are URLs, import proxy settings
                for (int j = 0; j < nonCollectionFiles.length; j++) {
                    String fileName = files[j];                    
                    if (UrlByteReader.isURL(fileName) && proxyHost == null) {
                        // Get proxy settings from configuration file, if not already set
                        try {
                            myAnalysis.readConfiguration();
                            proxyHost = myAnalysis.getProxyHost();
                            proxyPort = Integer.toString(myAnalysis.getProxyPort());
                            if (!"".equals(proxyHost)) {
                                System.setProperty("http.proxyHost", proxyHost);
                                System.setProperty("http.proxyPort", proxyPort);
                            }
                        } catch (Exception e) {
                            messageDisplay.generalInformation("Error reading the congifuration file: " + AnalysisController.CONFIG_FILE_NAME + "\n" + e.getMessage());
                        }
                    }
                }
                
                isStats = true;                
            } else if (theArgs[i].substring(1, 2).equals("A")) {
                theListFileName = theArgs[i].substring(2);
                isAnalysis = true;
            } else if (theArgs[i].substring(1, 2).equals("N")) {
                theListFileName = theArgs[i].substring(2);
                isStats = true;                
            } else if (theArgs[i].substring(1, 2).equals("S")) {
                theSignatureFile = theArgs[i].substring(2);
            } else if (theArgs[i].substring(1, 2).equals("O")) {
                theOutputFile = theArgs[i].substring(2);
            } else if (theArgs[i].substring(1, 2).equals("F")) {
                theOutputFormats = theArgs[i].substring(2);
            } else if (theArgs[i].substring(1, 2).equals("H")) {
                isHelpDisplay = true;
            } else if (theArgs[i].substring(1, 2).equalsIgnoreCase("Q")) {
            	myAnalysis.setVerbose(false);
            } else {
                isWellFormed = false;
                break;
            }
        }

        if (!isWellFormed) {
            messageDisplay.fatalError("The command line arguments were incorrectly formed\n" + myArgumentHelp);
        }

        //check command is complete and consistent and run it
        if (isAnalysisVersionDisplay && !isSigFileVersionDisplay && !isSigFileCheck && !isDownload && !isAnalysis && !isHelpDisplay) {
            //display analysis version
            System.out.println("DROID " + AnalysisController.getDROIDVersion());

        } else
        if (!isAnalysisVersionDisplay && isSigFileVersionDisplay && !isSigFileCheck && !isDownload && !isAnalysis && !isHelpDisplay) {
            //read signature file this automatically displays its version once it has read it in
            try {
                myAnalysis.readSigFile(theSignatureFile, false);
            } catch (Exception e) {
                messageDisplay.fatalError("Error reading the signature file: " + theSignatureFile);
            }

        } else
        if (!isAnalysisVersionDisplay && !isSigFileVersionDisplay && isSigFileCheck && !isDownload && !isAnalysis && !isHelpDisplay) {
            //check whether a later signature file exists on PRONOM website
            //first read in the signature file
            try {
                myAnalysis.readSigFile(theSignatureFile, false);
            } catch (Exception e) {
                messageDisplay.fatalError("Error reading the signature file: " + theSignatureFile);
            }
            //then read in configuration file to get URL for PRONOM web services
            try {
                myAnalysis.readConfiguration();
            } catch (Exception e) {
                messageDisplay.fatalError("Error reading the congifuration file: " + AnalysisController.CONFIG_FILE_NAME + "\n" + e.getMessage());
            }
            if (myAnalysis.isNewerSigFileAvailable()) {
                System.out.println("Signature file " + theSignatureFile + " is out of date.  Please download a new one from the PRONOM web service");
            } else if (PronomWebService.isCommSuccess) {
                System.out.println("Signature file " + theSignatureFile + " is up to date");
            } else {
                String failureMessage = "Unable to connect to the PRONOM web service. Make sure that the following settings in your configuration file (DROID_config.xml) are correct:\n";
                failureMessage += "    1- <SigFileURL> is the URL of the PRONOM web service.  This should be '" + AnalysisController.PRONOM_WEB_SERVICE_URL + "'\n";
                failureMessage += "    2- <ProxyHost> is the IP address of the proxy server if one is required\n";
                failureMessage += "    3- <ProxyPort> is the port to use on the proxy server if one is required";
                messageDisplay.fatalError(failureMessage);
            }

        } else
        if (!isAnalysisVersionDisplay && !isSigFileVersionDisplay && !isSigFileCheck && isDownload && !isAnalysis && !isHelpDisplay) {
            if (theSignatureFile.length() == 0) {
                messageDisplay.fatalError("No signature file name was provided.\n" + myArgumentHelp);
            }
            //Read in configuration file to get URL for PRONOM web services
            try {
                myAnalysis.readConfiguration();
            } catch (Exception e) {
                messageDisplay.fatalError("Error reading the congifuration file: " + AnalysisController.CONFIG_FILE_NAME + "\n" + e.getMessage());
            }
            //download latest signature file from PRONOM website
            System.out.println("Downloading latest signature file ...");
            myAnalysis.downloadwwwSigFile(theSignatureFile, false);
            if (!PronomWebService.isCommSuccess) {
                String failureMessage = "Unable to connect to the PRONOM web service. Make sure that the following settings in your configuration file (DROID_config.xml) are correct:\n";
                failureMessage += "    1- <SigFileURL> is the URL of the PRONOM web service.  This should be '" + AnalysisController.PRONOM_WEB_SERVICE_URL + "'\n";
                failureMessage += "    2- <ProxyHost> is the IP address of the proxy server if one is required\n";
                failureMessage += "    3- <ProxyPort> is the port to use on the proxy server if one is required";
                messageDisplay.fatalError(failureMessage);
            } else if (myAnalysis.isFileFound(theSignatureFile)) {
                System.out.println("A new signature file has been downloaded to " + theSignatureFile);
            } else {
                System.out.println("Signature file download has failed");
            }

        } else
        if (!isAnalysisVersionDisplay && !isSigFileVersionDisplay && !isSigFileCheck && !isDownload && isAnalysis && !isHelpDisplay) {           
            if (theSignatureFile.length() == 0) {
                messageDisplay.fatalError("No signature file name was provided.\n" + myArgumentHelp);
            } else if (theOutputFile.length() == 0) {
                messageDisplay.fatalError("No output file name was provided.\n" + myArgumentHelp);
            } else if (theOutputFormats.length() == 0) {
                messageDisplay.fatalError("No output format was provided.\n" + myArgumentHelp);
            }

            //get list of files
            if (theListFileName.length() > 0) {
                try {
                    myAnalysis.readFileCollection(theListFileName);
                } catch (Exception e) {
                    messageDisplay.fatalError("Error reading the file collection file: " + theListFileName);
                }
            }
            if (myAnalysis.getNumFiles() == 0) {
                messageDisplay.fatalError("No file was provided for identification.\n" + myArgumentHelp);
            }
            //read signature file
            try {
                myAnalysis.readSigFile(theSignatureFile, false);
            } catch (Exception e) {
                messageDisplay.fatalError("Error reading the signature file: " + theSignatureFile);
            }
            myAnalysis.checkSignatureFile();

            //run analysis
            if( myAnalysis.isVerbose() )
            	System.out.println("Run analysis");
            myAnalysis.setAnalysisStart();
            myAnalysis.runFileFormatAnalysis(theOutputFormats, theOutputFile);

        } else
        if (isStats && !isAnalysisVersionDisplay && !isSigFileVersionDisplay && !isSigFileCheck && !isDownload && !isAnalysis && !isHelpDisplay) {           
            // Generate statistics
            if (theSignatureFile.length() == 0) {
                messageDisplay.fatalError("No signature file name was provided.\n" + myArgumentHelp);
            } else if (theOutputFile.length() == 0) {
                messageDisplay.fatalError("No output file name was provided.\n" + myArgumentHelp);
            } else if (theOutputFormats.length() == 0) {
                messageDisplay.fatalError("No output format was provided.\n" + myArgumentHelp);
            }

            // Get list of files if FileCollection is provided
            if (theListFileName.length() > 0) {
                try {
                    myAnalysis.readFileCollection(theListFileName);
                } catch (Exception e) {
                    messageDisplay.fatalError("Error reading the file collection file: " + theListFileName);
                }
            }
            if ( (this.nonCollectionFiles == null || this.nonCollectionFiles.length == 0) && myAnalysis.getNumFiles() == 0) {
                messageDisplay.fatalError("No file was provided for identification.\n" + myArgumentHelp);
            }
            
            // Read signature file
            try {
                myAnalysis.readSigFile(theSignatureFile, false);
            } catch (Exception e) {
                messageDisplay.fatalError("Error reading the signature file: " + theSignatureFile);
            }
            myAnalysis.checkSignatureFile();

            //run stats
            if( myAnalysis.isVerbose() )
            	System.out.println("Run statistics");
                                    
            myAnalysis.setAnalysisStart();                        
            if (this.nonCollectionFiles != null && this.nonCollectionFiles.length > 0){
                myAnalysis.runStatsGathering(theOutputFormats, theOutputFile, this.nonCollectionFiles, true);
            }else{
                myAnalysis.runStatsGathering(theOutputFormats, theOutputFile);
            }
            
        } else if (isHelpDisplay) {
            messageDisplay.generalInformation(myArgumentHelp);
        } else {
            messageDisplay.fatalError("Too many command line options were provided\n" + myArgumentHelp);
        }

    }
}
