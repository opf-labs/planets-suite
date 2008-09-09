package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

@Stateless()
@Local(BasicCharacteriseOneBinaryXCELtoURI.class)
@Remote(BasicCharacteriseOneBinaryXCELtoURI.class)
@LocalBinding(jndiBinding = "planets/Extractor2URI")
@RemoteBinding(jndiBinding = "planets-project.eu/Extractor2URI")
@WebService(name = "Extractor2URI",
// This is not appropriate when using the endpointInterface approach.
serviceName = BasicCharacteriseOneBinaryXCELtoURI.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@MTOM
public class Extractor2URI implements BasicCharacteriseOneBinaryXCELtoURI,
        Serializable {

    private static final long serialVersionUID = 3007130161689982082L;
    private final static PlanetsLogger plogger = PlanetsLogger
            .getLogger(Extractor2URI.class);
    private static final String SINGLE = "JustBinary";
    private static String CALLING_EXTRACTOR_NAME = "EXTRACTOR2URI";
    private static String EXTRACTOR_DR_OUT = CALLING_EXTRACTOR_NAME + "_OUT";
    private static String OUTPUTFILE_NAME = "extractor2uri_xcdl_out.xcdl";

    public Extractor2URI() {

    }

    /**
     * 
     * @param binary a byte[] which contains the image data
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException
     */
    @WebMethod(operationName = BasicCharacteriseOneBinaryXCELtoURI.NAME
            + SINGLE, action = PlanetsServices.NS + "/"
            + BasicCharacteriseOneBinaryXCELtoURI.NAME)
    @WebResult(name = BasicCharacteriseOneBinaryXCELtoURI.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = BasicCharacteriseOneBinaryXCELtoURI.NAME
            + "Result")
    public URI basicCharacteriseOneBinaryXCELtoURI(
            @WebParam(name = "input_image_URI", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = "input_image_URI") URI inputImageURI)
            throws PlanetsException {
        return basicCharacteriseOneBinaryXCELtoURI(inputImageURI, null);
    }

    /**
     * 
     * @param binary a byte[] which contains the image data
     * @param xcel a String holding the Contents of a XCEL file
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException
     */
    @WebMethod(operationName = BasicCharacteriseOneBinaryXCELtoURI.NAME, action = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME)
    @WebResult(name = BasicCharacteriseOneBinaryXCELtoURI.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = BasicCharacteriseOneBinaryXCELtoURI.NAME
            + "Result")
    public URI basicCharacteriseOneBinaryXCELtoURI(
            @WebParam(name = "input_image_URI", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = "input_image_URI") URI inputImageURI,
            @WebParam(name = "input_xcel_URI", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = "input_xcel_URI") URI inputXcelURI)
            throws PlanetsException {

        byte[] input_image = new DataRegistryAccess().read(inputImageURI
                .toASCIIString());
        // byte[] input_xcel = getBinaryFromDataRegistry(inputXcelURI
        // .toASCIIString());

        CoreExtractor extractor = new CoreExtractor(CALLING_EXTRACTOR_NAME,
                plogger);

        String fileName = "";
        StringTokenizer st = new StringTokenizer(inputImageURI.toASCIIString());
        while (st.hasMoreTokens()) {
            fileName = st.nextToken("/");
        }
        int k = fileName.lastIndexOf(".");
        if (k > 0) {
            OUTPUTFILE_NAME = fileName.substring(0, k + 1) + "xcel";
        }

        byte[] outputXCDL = extractor.extractXCDL(input_image,
                inputXcelURI != null ? new DataRegistryAccess()
                        .read(inputXcelURI.toASCIIString()) : null);

        URI outputFileURI = null;
        outputFileURI = new DataRegistryAccess().write(outputXCDL,
                OUTPUTFILE_NAME);
        return outputFileURI;
    }

    // /**
    // * get the src file from the DataRegistry using the file reference
    // contained
    // * in the XML-PDM String. The file is returned as byte[].
    // *
    // * @param fileReference reference to the src-file in the DataRegistry
    // * @return src file as byte[] for conversion
    // */
    // byte[] read(String fileReference) {
    // plogger.debug("Starting to get File from DataRegistry...");
    //
    // URI fileURI = null;
    // try {
    // fileURI = new URI(fileReference);
    // } catch (URISyntaxException e1) {
    // plogger.warn("Exception: " + e1.getLocalizedMessage());
    // e1.printStackTrace();
    // }
    //
    // DataManagerLocal dataRegistry = null;
    //
    // // Binding the DataManagerLocal-Interface to the local
    // // DataManager-Instance via JNDI.
    // plogger.debug("Trying to get InitialContext for JNDI-Lookup...");
    // try {
    // Context ctx = new InitialContext();
    // dataRegistry = (DataManagerLocal) ctx
    // .lookup("planets-project.eu/DataManager/local");
    // plogger.debug("Created dataRegistry-Object...");
    // } catch (NamingException e2) {
    // plogger.error("Could not lookup local DataManager!");
    // e2.printStackTrace();
    // }
    //
    // byte[] srcFileArray = null;
    //
    // try {
    // plogger.debug("Retrieving file from DataRegistry: "
    // + fileURI.toASCIIString());
    // srcFileArray = dataRegistry.retrieveBinary(fileURI);
    // plogger.debug("Successfully retrieved file!");
    // } catch (SOAPException e) {
    // plogger.error("Exception: " + e.getLocalizedMessage());
    // e.printStackTrace();
    // }
    // return srcFileArray;
    // }
    //
    // URI write(byte[] binary, String fileName) {
    // plogger.info("Starting to store File in DataRegistry...");
    // DataManagerLocal dataRegistry = null;
    // URI fileURI = null;
    // URI registryRoot = null;
    // String dataRegistryPath = null;
    //
    // // Binding the DataManagerLocal-Interface to the local
    // // DataManager-Instance via JNDI.
    // plogger.info("Trying to get InitialContext for JNDI-Lookup...");
    // try {
    // Context ctx = new InitialContext();
    // dataRegistry = (DataManagerLocal) ctx
    // .lookup("planets-project.eu/DataManager/local");
    // plogger.info("Created dataRegistry-Object...");
    // try {
    // // Get the root path of the DataRegistry...using an undocumented
    // // "hidden" feature of the DataManager,
    // // which is to return the root path of the DR, when "null" is
    // // passed to the list() method.
    // URI[] storagePaths = dataRegistry.list(null);
    // registryRoot = storagePaths[0];
    // dataRegistryPath = registryRoot.toASCIIString();
    // plogger.info("Registry root: " + dataRegistryPath);
    //
    // } catch (SOAPException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // } catch (NamingException e2) {
    // // TODO Auto-generated catch block
    // plogger.info("Could not lookup local DataManager!");
    // e2.printStackTrace();
    // }
    //
    // try {
    // plogger.info("Creating File URI...");
    // plogger.info("URI will be: " + dataRegistryPath + "/"
    // + EXTRACTOR_DR_OUT + "/" + fileName);
    //
    // // Create the new URI for storing the file to the DataRegistry.
    // fileURI = new URI(dataRegistryPath + "/" + EXTRACTOR_DR_OUT + "/"
    // + fileName);
    //
    // plogger.info("Created File URI: " + fileURI.toASCIIString());
    // } catch (URISyntaxException e) {
    // // TODO Auto-generated catch block
    // plogger.error("Malformed URI...! " + fileURI.toASCIIString());
    // e.printStackTrace();
    // }
    //
    // try {
    // plogger.info("Starting to write binary to DataRegistry...");
    // // URI of the default OUTPUT_FOLDER of this Service, used as search
    // // root when testing
    // // if a file already exists.
    // URI outputFolderURI = new URI(dataRegistryPath + "/"
    // + EXTRACTOR_DR_OUT);
    // plogger.info("Outputfolder: " + outputFolderURI.toASCIIString());
    // plogger.info("Searching for duplicated files...");
    //
    // URI[] searchResults = null;
    // try {
    // searchResults = dataRegistry.findFilesWithNameContaining(
    // registryRoot, fileName);
    // } catch (SOAPException e) {
    // e.printStackTrace();
    // }
    //
    // // debug output
    // StringBuffer sb = new StringBuffer();
    // for (int i = 0; i < searchResults.length; i++) {
    // sb = sb.append(searchResults[i].toASCIIString() + "\n");
    // }
    // // end debug output
    //
    // plogger.info("Found the following hits: " + sb.toString());
    //
    // // The returned URI[] searchResults is not NULL and
    // if (searchResults != null) {
    // // there have been some hits, e.g. files with the same filename,
    // // but maybe in a different path...
    // if (searchResults.length > 0) {
    // for (int i = 0; i < searchResults.length; i++) {
    // String currentURI = searchResults[i].toASCIIString();
    // // Check if there have been hits inside the
    // // OUTPUT_FOLDER
    // if (currentURI.indexOf(EXTRACTOR_DR_OUT) != -1) {
    // // There is (at least) a file with the same name
    // // inside the OUTPUT_FOLDER so...
    // plogger.info("File already exists: " + fileName
    // + ". File will be renamed...");
    //
    // // ...get a timestamp
    // String timestamp = System.currentTimeMillis() + "";// getTimeStamp
    // // (
    // // )
    // // ;
    //
    // // ...split the initial filename in a prefix and...
    // String fileNamePrefix = fileName.substring(0,
    // fileName.lastIndexOf("."));
    // plogger.info("fileNamePrefix: " + fileNamePrefix);
    //
    // // // ...and the postfix
    // String fileNamePostfix = fileName
    // .substring(fileName.lastIndexOf("."));
    // plogger.info("fileNamePostfix: " + fileNamePostfix);
    //
    // // // and add the "_[timestamp]" to the filename
    // plogger.info("Adding timestamp to filename: "
    // + timestamp);
    // String renamedFileName = fileNamePrefix + "_"
    // + timestamp + fileNamePostfix;
    //
    // plogger.info("New file Name: " + renamedFileName);
    // // create a new URI for the renamed file and...
    // URI renamedFileURI = new URI(outputFolderURI
    // .toASCIIString()
    // + "/" + renamedFileName);
    // plogger.info("New file URI: "
    // + renamedFileURI.toASCIIString());
    // plogger.info("Storing file with new name: "
    // + renamedFileName + " in DataRegistry...");
    // // store it in the DataRegistry, using the new
    // // filename
    // dataRegistry.storeBinary(renamedFileURI, binary);
    // plogger
    // .info("Successfully stored binary in DataRegistry: "
    // + renamedFileName);
    // fileURI = renamedFileURI;
    // }
    //
    // // There have been hits (e.g. files with the same name),
    // // but in a different folder,
    // // so just store the file with its initial name in the
    // // DataRegistry
    // else {
    // plogger
    // .info("Attempting to store binary in DataRegistry: "
    // + fileName);
    // // store the file...
    // dataRegistry.storeBinary(fileURI, binary);
    // plogger
    // .info("Successfully stored binary in DataRegistry: "
    // + fileName);
    // }
    // }
    // }
    // // There have been NO search hits, so store the file with its
    // // initial filename, too.
    // else {
    // plogger.info("Attempting to store binary in DataRegistry: "
    // + fileName);
    // // store the file to the DR...
    // dataRegistry.storeBinary(fileURI, binary);
    // plogger.info("Successfully stored binary in DataRegistry: "
    // + fileName);
    // }
    // }
    //
    // } catch (LoginException e) {
    // plogger.error("LoginException: " + e.getLocalizedMessage());
    // e.printStackTrace();
    // } catch (RepositoryException e) {
    // plogger.error("RepositoryException: " + e.getLocalizedMessage());
    // e.printStackTrace();
    // } catch (URISyntaxException e) {
    // plogger.error("URISyntaxException: " + e.getLocalizedMessage());
    // e.printStackTrace();
    // }
    //
    // // Last test if the URI created to store the file is a valid URI...
    // if (DataModelUtils.isValidReference(fileURI.toASCIIString())) {
    // // ...if yes, return it
    // plogger.info("Validating the created file URI: "
    // + fileURI.toASCIIString());
    // plogger.info("Validataion result: OK!");
    // return fileURI;
    // } else {
    // // ...if no, log out an error and return NULL.
    // plogger.error("The URI of the file is not valid!");
    // return null;
    // }
    // }

}
