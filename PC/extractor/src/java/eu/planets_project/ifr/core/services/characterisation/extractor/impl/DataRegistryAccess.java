package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

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

import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * @author Peter Melms (original code)
 * 
 */
public class DataRegistryAccess {

    private String host;

    public DataRegistryAccess(String host) {
        this.host = host;
    }

    public DataRegistryAccess() {
        this.host = "http://localhost:8080";
    }

    private final static PlanetsLogger plogger = PlanetsLogger
            .getLogger(DataRegistryAccess.class);
    private static final String EXTRACTOR_DR_OUT = "EXTRACTOR_OUT";

    /**
     * get the src file from the DataRegistry using the file reference contained
     * in the XML-PDM String. The file is returned as byte[].
     * 
     * @param fileReference reference to the src-file in the DataRegistry
     * @return src file as byte[] for conversion
     */
    byte[] read(String fileReference) {
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

        try {
            plogger.debug("Retrieving file from DataRegistry: "
                    + fileURI.toASCIIString());
            srcFileArray = dataRegistry.retrieveBinary(fileURI);
            plogger.debug("Successfully retrieved file!");
        } catch (SOAPException e) {
            plogger.error("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return srcFileArray;
    }

    URI write(byte[] binary, String fileName) {
        plogger.info("Starting to store File in DataRegistry...");
        DataManagerLocal dataRegistry = null;
        URI fileURI = null;
        URI registryRoot = null;
        String dataRegistryPath = null;

        // Binding the DataManagerLocal-Interface to the local
        // DataManager-Instance via JNDI.
        plogger.info("Trying to get InitialContext for JNDI-Lookup...");
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
            plogger.info("URI will be: " + dataRegistryPath + "/"
                    + EXTRACTOR_DR_OUT + "/" + fileName);

            // Create the new URI for storing the file to the DataRegistry.
            fileURI = new URI(dataRegistryPath + "/" + EXTRACTOR_DR_OUT + "/"
                    + fileName);

            plogger.info("Created File URI: " + fileURI.toASCIIString());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            plogger.error("Malformed URI...! " + fileURI.toASCIIString());
            e.printStackTrace();
        }

        try {
            plogger.info("Starting to write binary to DataRegistry...");
            // URI of the default OUTPUT_FOLDER of this Service, used as search
            // root when testing
            // if a file already exists.
            URI outputFolderURI = new URI(dataRegistryPath + "/"
                    + EXTRACTOR_DR_OUT);
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
            for (int i = 0; i < searchResults.length; i++) {
                sb = sb.append(searchResults[i].toASCIIString() + "\n");
            }
            // end debug output

            plogger.info("Found the following hits: " + sb.toString());

            // The returned URI[] searchResults is not NULL and
            if (searchResults != null) {
                // there have been some hits, e.g. files with the same filename,
                // but maybe in a different path...
                if (searchResults.length > 0) {
                    for (int i = 0; i < searchResults.length; i++) {
                        String currentURI = searchResults[i].toASCIIString();
                        // Check if there have been hits inside the
                        // OUTPUT_FOLDER
                        if (currentURI.indexOf(EXTRACTOR_DR_OUT) != -1) {
                            // There is (at least) a file with the same name
                            // inside the OUTPUT_FOLDER so...
                            plogger.info("File already exists: " + fileName
                                    + ". File will be renamed...");

                            // ...get a timestamp
                            String timestamp = getTimeStamp();
                            // ;

                            // ...split the initial filename in a prefix and...
                            String fileNamePrefix = fileName.substring(0,
                                    fileName.lastIndexOf("."));
                            plogger.info("fileNamePrefix: " + fileNamePrefix);

                            // // ...and the postfix
                            String fileNamePostfix = fileName
                                    .substring(fileName.lastIndexOf("."));
                            plogger.info("fileNamePostfix: " + fileNamePostfix);

                            // // and add the "_[timestamp]" to the filename
                            plogger.info("Adding timestamp to filename: "
                                    + timestamp);
                            String renamedFileName = fileNamePrefix + "_"
                                    + timestamp + fileNamePostfix;

                            plogger.info("New file Name: " + renamedFileName);
                            // create a new URI for the renamed file and...
                            URI renamedFileURI = new URI(outputFolderURI
                                    .toASCIIString()
                                    + "/" + renamedFileName);
                            plogger.info("New file URI: "
                                    + renamedFileURI.toASCIIString());
                            plogger.info("Storing file with new name: "
                                    + renamedFileName + " in DataRegistry...");
                            // store it in the DataRegistry, using the new
                            // filename
                            dataRegistry.storeBinary(renamedFileURI, binary);
                            plogger
                                    .info("Successfully stored binary in DataRegistry: "
                                            + renamedFileName);
                            fileURI = renamedFileURI;
                        }

                        // There have been hits (e.g. files with the same name),
                        // but in a different folder,
                        // so just store the file with its initial name in the
                        // DataRegistry
                        else {
                            plogger
                                    .info("Attempting to store binary in DataRegistry: "
                                            + fileName);
                            // store the file...
                            dataRegistry.storeBinary(fileURI, binary);
                            plogger
                                    .info("Successfully stored binary in DataRegistry: "
                                            + fileName);
                        }
                    }
                }
                // There have been NO search hits, so store the file with its
                // initial filename, too.
                else {
                    plogger.info("Attempting to store binary in DataRegistry: "
                            + fileName);
                    // store the file to the DR...
                    dataRegistry.storeBinary(fileURI, binary);
                    plogger.info("Successfully stored binary in DataRegistry: "
                            + fileName);
                }
            }

        } catch (LoginException e) {
            plogger.error("LoginException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (RepositoryException e) {
            plogger.error("RepositoryException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            plogger.error("URISyntaxException: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        // Last test if the URI created to store the file is a valid URI...
        if (DataModelUtils.isValidReference(fileURI.toASCIIString())) {
            // ...if yes, return it
            plogger.info("Validating the created file URI: "
                    + fileURI.toASCIIString());
            plogger.info("Validataion result: OK!");
            return fileURI;
        } else {
            // ...if no, log out an error and return NULL.
            plogger.error("The URI of the file is not valid!");
            return null;
        }
    }

    /**
     * @param dataRegistry
     * @return
     * @throws NamingException
     */
    private DataManagerLocal createDataRegistry() {
        Context ctx;
        try {
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
        // Creating a Calendar instance for the timestamp used in the
        // storeBinaryInDataRegistry() method.
        Calendar myCALENDAR = Calendar.getInstance();
        int DAY = myCALENDAR.get(Calendar.DAY_OF_MONTH);
        int MONTH = myCALENDAR.get(Calendar.MONTH) + 1;
        int YEAR = myCALENDAR.get(Calendar.YEAR);
        int HOUR = myCALENDAR.get(Calendar.HOUR_OF_DAY);
        int MINUTE = myCALENDAR.get(Calendar.MINUTE);
        int SECOND = myCALENDAR.get(Calendar.SECOND);
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
