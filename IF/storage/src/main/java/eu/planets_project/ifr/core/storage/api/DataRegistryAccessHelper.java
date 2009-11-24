package eu.planets_project.ifr.core.storage.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Service;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

/**
 * @author Peter Melms (original code)
 */
public class DataRegistryAccessHelper {

    private String host;
    private static int MONTH;
    private static int DAY;
    private static int YEAR;
    private static int HOUR;
    private static int MINUTE;
    private static int SECOND;
    private static Calendar myCALENDAR;

    public DataRegistryAccessHelper(String host) {
        this.host = host;
        this.createCalendar();
    }

    public DataRegistryAccessHelper() {
        this.host = "http://localhost:8080";
        this.createCalendar();
    }

    private final static PlanetsLogger plogger = PlanetsLogger
            .getLogger(DataRegistryAccessHelper.class);

    private void createCalendar() {
        // Creating a Calendar instance for the timestamp used in the
        // write(...) method.
        myCALENDAR = Calendar.getInstance();
        DAY = myCALENDAR.get(Calendar.DAY_OF_MONTH);
        MONTH = myCALENDAR.get(Calendar.MONTH) + 1;
        YEAR = myCALENDAR.get(Calendar.YEAR);
        HOUR = myCALENDAR.get(Calendar.HOUR_OF_DAY);
        MINUTE = myCALENDAR.get(Calendar.MINUTE);
        SECOND = myCALENDAR.get(Calendar.SECOND);
    }

    /**
     * get the src file from the DataRegistry using the file reference contained
     * in the XML-PDM String. The file is returned as byte[].
     * @param fileReference reference to the src-file in the DataRegistry
     * @return src file as byte[] for conversion
     */
    public byte[] read(String fileReference) {
        plogger.debug("Starting to get File from DataRegistry...");

        URI fileURI = null;
        try {
            fileURI = new URI(fileReference);
        } catch (URISyntaxException e1) {
            plogger.warn("Exception: " + e1.getLocalizedMessage());
            e1.printStackTrace();
        }

        DataManagerLocal dataRegistry = null;

        // Binding the DataManagerLocal-Interface to the local
        // DataManager-Instance via JNDI.
        plogger.debug("Trying to get InitialContext for JNDI-Lookup...");
        dataRegistry = createDataRegistry();

        byte[] srcFileArray = null;
        if (fileURI != null) {
            try {
                plogger.debug("Retrieving file from DataRegistry: "
                        + fileURI.toASCIIString());
                srcFileArray = dataRegistry.retrieveBinary(fileURI);
                plogger.debug("Successfully retrieved file!");
            } catch (SOAPException e) {
                plogger.error("Exception: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return srcFileArray;
    }

    /* FIXME write access currently not possible (security issues), therefore private */
    @SuppressWarnings("unused")
    private URI write(byte[] binary, String fileName, String outputDir) {
        plogger.info("Starting to store File in DataRegistry...");
        DataManagerLocal dataRegistry = null;
        URI fileURI = null;
        URI registryRoot = null;
        String dataRegistryPath = null;

        // Binding the DataManagerLocal-Interface to the local
        // DataManager-Instance via JNDI.
        dataRegistry = createDataRegistry();
        try {
            // Get the root path of the DataRegistry...using an undocumented
            // "hidden" feature of the DataManager,
            // which is to return the root path of the DR, when "null" is
            // passed to the list() method.
            URI[] storagePaths = dataRegistry.list(null);
            registryRoot = storagePaths[0];
            dataRegistryPath = registryRoot.toASCIIString();
            plogger.info("Registry root: " + dataRegistryPath);

        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            plogger.info("Creating File URI...");
            plogger.info("URI will be: " + dataRegistryPath + "/" + outputDir
                    + "/" + fileName);

            // Create the new URI for storing the file to the DataRegistry.
            fileURI = new URI(dataRegistryPath + "/" + outputDir + "/"
                    + fileName);

            plogger.info("Created File URI: " + fileURI.toASCIIString());
        } catch (URISyntaxException e) {
            plogger.error("Malformed URI...! ");
            e.printStackTrace();
        }

        try {
            plogger.info("Starting to write binary to DataRegistry...");
            // URI of the default OUTPUT_FOLDER of this Service, used as search
            // root when testing
            // if a file already exists.
            URI outputFolderURI = new URI(dataRegistryPath + "/" + outputDir);
            plogger.info("Outputfolder: " + outputFolderURI.toASCIIString());
            plogger.info("Searching for duplicated files...");

            URI[] searchResults = null;
            try {
                searchResults = dataRegistry.findFilesWithNameContaining(
                        registryRoot, fileName);
            } catch (SOAPException e) {
                e.printStackTrace();
            }

            // debug output
            StringBuffer sb = new StringBuffer();
            if (searchResults != null) {
                for (int i = 0; i < searchResults.length; i++) {
                    sb = sb.append(searchResults[i].toASCIIString() + "\n");
                }
            }
            // end debug output

            plogger.info("Found the following hits: " + sb.toString());

            String searchPattern = outputDir + "/" + fileName;

            boolean hitFound = testSearchResultsForHits(searchResults,
                    searchPattern);

            String renamedFileName = null;

            if (hitFound) {
                renamedFileName = addTimestampToFileName(fileName);
                URI newURI = createNewURI(renamedFileName, outputFolderURI);
                plogger.info("Storing file with new name: " + renamedFileName
                        + " in DataRegistry...");
                // store it in the DataRegistry, using the new
                // filename
                dataRegistry.storeBinary(newURI, binary);
                plogger.info("Successfully stored binary in DataRegistry: "
                        + renamedFileName);
                fileURI = newURI;
            } else {
                dataRegistry.storeBinary(fileURI, binary);
            }

        } catch (URISyntaxException e) {
            plogger.error("URISyntaxException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fileURI;
    }

    private URI createNewURI(String renamedFileName, URI outputFolderURI) {
        // create a new URI for the renamed file and...
        URI renamedFileURI = null;
        try {
            renamedFileURI = new URI(outputFolderURI.toASCIIString() + "/"
                    + renamedFileName);

            plogger.info("New file URI: " + renamedFileURI.toASCIIString());

            plogger.info("Storing file with new name: " + renamedFileName
                    + " in DataRegistry...");
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return renamedFileURI;
    }

    private String addTimestampToFileName(String fileName) {
        if (!fileName
                .matches(".*_[0-9][0-9]-[0-9][0-9]-[0-9][0-9][0-9][0-9]_[0-9][0-9]-[0-9][0-9]-[0-9][0-9]_[0-9][0-9][0-9]ms.*")) {
            plogger.info("File already exists: " + fileName
                    + ". File will be renamed...");

            // ...get a timestamp
            String timestamp = getTimeStamp();
            // ;

            // ...split the initial filename in a prefix and...
            String fileNamePrefix = fileName.substring(0, fileName
                    .lastIndexOf("."));
            plogger.info("fileNamePrefix: " + fileNamePrefix);

            // // ...and the postfix
            String fileNamePostfix = fileName.substring(fileName
                    .lastIndexOf("."));
            plogger.info("fileNamePostfix: " + fileNamePostfix);

            // // and add the "_[timestamp]" to the filename
            plogger.info("Adding timestamp to filename: " + timestamp);
            String renamedFileName = fileNamePrefix + "_" + timestamp
                    + fileNamePostfix;

            plogger.info("New file Name: " + renamedFileName);
            return renamedFileName;
        } else {
            String timestamp = getTimeStamp();
            plogger.info("Filename already contains timestamp!");
            plogger.info("Replacing timestamp...");
            fileName = fileName
                    .replaceFirst(
                            "[0-9][0-9]-[0-9][0-9]-[0-9][0-9][0-9][0-9]_[0-9][0-9]-[0-9][0-9]-[0-9][0-9]_[0-9][0-9][0-9]ms",
                            timestamp);
            plogger.info("New file name: " + fileName);
            return fileName;
        }

    }

    private boolean testSearchResultsForHits(URI[] searchResults,
            String searchPattern) {
        boolean hitFound = false;
        if (searchResults != null) {
            // there have been some hits, e.g. files with the same filename,
            // but maybe in a different path...
            if (searchResults.length > 0) {
                for (int i = 0; i < searchResults.length; i++) {
                    String currentURIString = searchResults[i].toASCIIString();
                    // Check if there have been hits inside the
                    // OUTPUT_FOLDER
                    if (currentURIString.indexOf(searchPattern) != -1) {
                        // There is (at least) a file with the same name
                        // inside the "outputDir" so...
                        hitFound = true;
                    }
                }
            } else {
                hitFound = false;
            }
        } else {
            hitFound = false;
        }

        return hitFound;
    }

    /**
     * @param dataRegistry
     * @return
     * @throws NamingException
     */
    private DataManagerLocal createDataRegistry() {
        Context ctx;
        try {
            plogger.info("Trying to get InitialContext for JNDI-Lookup...");
            ctx = new InitialContext();
            DataManagerLocal dataRegistry = null;
            try {
                dataRegistry = (DataManagerLocal) ctx
                        .lookup("planets-project.eu/DataManager/local");
            } catch (NoInitialContextException x) {
                plogger.warn("No initial context, trying via service...");
                URL url = null;
                try {
                    url = new URL(this.host
                            + "/storage-ifr-storage-ejb/DataManager?wsdl");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Service service = Service.create(url, new QName(
                        "http://planets-project.eu/ifr/core/storage/data",
                        "DataManager"));
                dataRegistry = service.getPort(DataManagerLocal.class);
                // x.printStackTrace();
            }
            plogger.info("Created dataRegistry-Object...");

            return dataRegistry;
        } catch (NamingException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private String getTimeStamp() {
        String day, month, year, hour, minute, second, millisecond = null;
        if (DAY > 9) {
            day = "" + DAY;
        } else {
            day = "0" + DAY;
        }
        if (MONTH > 9) {
            month = "" + MONTH;
        } else {
            month = "0" + MONTH;
        }

        year = "" + YEAR;

        if (HOUR > 9) {
            hour = "" + HOUR;
        } else {
            hour = "0" + HOUR;
        }
        if (MINUTE > 9) {
            minute = "" + MINUTE;
        } else {
            minute = "0" + MINUTE;
        }
        if (SECOND > 9) {
            second = "" + SECOND;
        } else {
            second = "0" + SECOND;
        }
        Calendar now = Calendar.getInstance();
        millisecond = "" + now.get(Calendar.MILLISECOND) + "ms";

        String timestamp = day + "-" + month + "-" + year + "_" + hour + "-"
                + minute + "-" + second + "_" + millisecond;
        return timestamp;
    }
}
