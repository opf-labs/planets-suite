package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCEL;


@Stateless()
@Local(BasicCharacteriseOneBinaryXCEL.class)
@Remote(BasicCharacteriseOneBinaryXCEL.class)
@LocalBinding(jndiBinding = "planets/Extractor")
@RemoteBinding(jndiBinding = "planets-project.eu/Extractor")
@WebService(
        name = "Extractor", 
//      This is not appropriate when using the endpointInterface approach.
        serviceName= BasicCharacteriseOneBinaryXCEL.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class Extractor implements BasicCharacteriseOneBinaryXCEL, Serializable {

	private static final long serialVersionUID = 3007130161689982082L;
	private final static String LOG_CONFIG_FILE = "eu/planets_project/ifr/core/services/characterisation/extractor/logconfig/extractor-log4j.xml";
	private final static PlanetsLogger plogger = PlanetsLogger.getLogger(Extractor.class);
	private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static String EXTRACTOR_WORK = null;
	private static String EXTRACTOR_IN = null;
	private static String EXTRACTOR_OUT = null;
	private static String EXTRACTOR_DR_OUT = "EXTRACTOR_OUT";
	private static String OUTPUTFILE_NAME = "xcdl_out.xcdl";
	
	private static int MONTH;
    private static int DAY;
    private static int YEAR;
    private static int HOUR;
    private static int MINUTE;
    private static int SECOND;
    private static Calendar myCALENDAR;
	
    public Extractor() {
    	// Creating a Calendar instance for the timestamp used in the storeBinaryInDataRegistry() method.
    	myCALENDAR = Calendar.getInstance();
    	DAY = myCALENDAR.get(Calendar.DAY_OF_MONTH);
    	MONTH = myCALENDAR.get(Calendar.MONTH) + 1;
    	YEAR = myCALENDAR.get(Calendar.YEAR);
    	HOUR = myCALENDAR.get(Calendar.HOUR_OF_DAY);
    	MINUTE = myCALENDAR.get(Calendar.MINUTE);
    	SECOND = myCALENDAR.get(Calendar.SECOND);
    }
    
	/**
     * 
     * @param binary a byte[] which contains the image data
     * @param xcel a String holding the Contents of a XCEL file
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicCharacteriseOneBinaryXCEL.NAME, 
            action = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME)
    @WebResult(
            name = BasicCharacteriseOneBinaryXCEL.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME, 
            partName = BasicCharacteriseOneBinaryXCEL.NAME + "Result")
    public URI basicCharacteriseOneBinaryXCEL ( 
    @WebParam(
            name = "binary", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME, 
            partName = "binary")     
    byte[] binary,
    @WebParam(
            name = "XCEL_String", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME, 
            partName = "XCEL_String")
            String xcel
    ) throws PlanetsException {
    	
    	if(SYSTEM_TEMP.lastIndexOf(File.separator) == SYSTEM_TEMP.length()-1) {
    			EXTRACTOR_WORK = SYSTEM_TEMP + "EXTRACTOR" + File.separator;
    			EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
    			EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
    	}
    	if (SYSTEM_TEMP.endsWith("/tmp")){
    		EXTRACTOR_WORK = SYSTEM_TEMP + File.separator + "EXTRACTOR" + File.separator;
    		EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
			EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
    	}
    	if(EXTRACTOR_HOME.endsWith(File.separator + File.separator)) {
    		EXTRACTOR_HOME = EXTRACTOR_HOME.replace(File.separator + File.separator, File.separator);
    	}
    	
    	plogger.info("Starting Extractor Service...");
    	
    	List <String> extractor_arguments = null;
    	File srcFile = null;
    	File xcelFile = null;
    	File extractor_work_folder = null;
    	File extractor_in_folder = null;
    	File extractor_out_folder = null;
    	
    	try {
    		extractor_work_folder = new File(EXTRACTOR_WORK);
    		extractor_work_folder.mkdir();
    		plogger.info("Extractor work folder created: " + EXTRACTOR_WORK);
    		extractor_in_folder = new File(EXTRACTOR_IN);
    		extractor_in_folder.mkdir();
    		plogger.info("Extractor input folder created: " + EXTRACTOR_IN);
    		extractor_out_folder = new File(EXTRACTOR_OUT);
			extractor_out_folder.mkdir();
			plogger.info("Extractor output folder created: " + EXTRACTOR_OUT);
    		
			srcFile = new File(EXTRACTOR_IN, "extractor_image_in.bin");
//    		srcFile = File.createTempFile(tmpFilePrefix, tmpFilePostfix);
			FileOutputStream fos = new FileOutputStream(srcFile);
			fos.write(binary);
			fos.flush();
			fos.close();
			
			xcelFile = new File(EXTRACTOR_IN, "extractor_xcel_in.xml");
//			xcelFile = File.createTempFile("xcelFile", ".xml");
			FileWriter fw = new FileWriter(xcelFile);
			fw.write(xcel);
			fw.flush();
			fw.close();
			
			plogger.info("System-Temp folder is: " + SYSTEM_TEMP);
			
		} catch (IOException e) {
			plogger.error("Could not create Temp-file!");
			e.printStackTrace();
		}
		
		
		ProcessRunner shell = new ProcessRunner();
		plogger.info("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
		plogger.info("Configuring Commandline");
		
		extractor_arguments = new ArrayList <String>();
		extractor_arguments.add(EXTRACTOR_HOME + "extractor");
		String srcFilePath = srcFile.getPath().replace('\\', '/');
//		System.out.println("Src-file path: " + srcFilePath);
		plogger.info("Input-Image file path: " + srcFilePath);
		extractor_arguments.add(srcFilePath);
		String xcelFilePath = xcelFile.getPath().replace('\\', '/');
//		System.out.println("XCEL-file path: " + xcelFilePath);
		plogger.info("Input-XCEL file path: " + xcelFilePath);
		extractor_arguments.add(xcelFilePath);
		String outputFilePath = EXTRACTOR_OUT + OUTPUTFILE_NAME;
		outputFilePath = outputFilePath.replace('\\', '/');
//		System.out.println("Output-file path: " + outputFilePath);
		extractor_arguments.add(outputFilePath);

		String line = "";
		for (String argument : extractor_arguments) {
			line = line + argument + " "; 
		}
		
		plogger.info("Setting command to: " + line);
		shell.setCommand(extractor_arguments);
		
		shell.setStartingDir(new File(EXTRACTOR_HOME));
		plogger.info("Setting starting Dir to: " + EXTRACTOR_HOME);
		plogger.info("Starting Extractor...");
		shell.run();
		String processOutput = shell.getProcessOutputAsString();
		String processError = shell.getProcessErrorAsString();
		plogger.info("Process Output: " + processOutput);
		plogger.info("Process Error: " + processError);
//		StringWriter sWriter = new StringWriter();
		StringBuffer sb = new StringBuffer();
		
		String in = "";
		String xcdl = null;
		byte[] binary_out = null;
		byte[] test = null;
		URI outputURI = null;
		try {
			plogger.info("Creating byte[] to return...");
			binary_out = getByteArrayFromFile(new File(outputFilePath));
			
// START TESTING
//			File test_out = new File(EXTRACTOR_OUT + "test_out.xml");
//			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(test_out));
//			bos.write(binary_out);
//			bos.flush();
//			bos.close();
//			
//			BufferedReader reader = new BufferedReader(new FileReader(test_out));
//			
//			while((in = reader.readLine())!=null) {
//				sb.append(in);
//			}
//			reader.close();
//			
//			xcdl = sb.toString();
//			plogger.info("XCDL String created.");
//			plogger.info("XCDL: " + xcdl.substring(0, 1000) + "...." + xcdl.substring(xcdl.length()-1001, xcdl.length()-1));
//			test = getByteArrayFromFile(test_out);
//			
// END TESTING
			
			outputURI = storeBinaryInDataRegistry(binary_out, OUTPUTFILE_NAME);
			
		} catch (FileNotFoundException e) {
			plogger.error("File not found: " + outputFilePath);
			e.printStackTrace();
		} catch (IOException e) {
			plogger.error("IO Error: ");
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deleteTempFiles(srcFile, xcelFile, new File(outputFilePath), extractor_in_folder, extractor_out_folder, extractor_work_folder);
		
		plogger.info("XCDL Outputfile stored in: " + outputURI.toASCIIString());		
//		return test;
		return outputURI;
    }
    
    private URI storeBinaryInDataRegistry (byte[] binary, String fileName) throws SOAPException {
		plogger.info("Starting to store File in DataRegistry...");
		DataManagerLocal dataRegistry = null;
		URI fileURI = null;
		URI registryRoot = null;
		String dataRegistryPath = null;
		
		// Binding the DataManagerLocal-Interface to the local DataManager-Instance via JNDI.
		plogger.info("Trying to get InitialContext for JNDI-Lookup...");
		try {
			Context ctx = new InitialContext();
			dataRegistry = (DataManagerLocal)ctx.lookup("planets-project.eu/DataManager/local");
			plogger.info("Created dataRegistry-Object...");
			try {
				// Get the root path of the DataRegistry...using an undocumented "hidden" feature of the DataManager,
				// which is to return the root path of the DR, when "null" is passed to the list() method.
				URI[] storagePaths = dataRegistry.list(null);
				registryRoot = storagePaths[0];
				dataRegistryPath = registryRoot.toASCIIString();
				plogger.info("Registry root: " + dataRegistryPath);		
				
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NamingException e2) {
			// TODO Auto-generated catch block
			plogger.info("Could not lookup local DataManager!");
			e2.printStackTrace();
		}
		
		try {
			plogger.info("Creating File URI...");
			plogger.info("URI will be: " + dataRegistryPath + "/" + EXTRACTOR_DR_OUT + "/" + fileName);
			
			// Create the new URI for storing the file to the DataRegistry.
			fileURI = new URI(dataRegistryPath + "/" + EXTRACTOR_DR_OUT + "/" + fileName);
			
			plogger.info("Created File URI: " + fileURI.toASCIIString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			plogger.error("Malformed URI...! " + fileURI.toASCIIString());
			e.printStackTrace();
		}
		
		try {
			plogger.info("Starting to write binary to DataRegistry...");
			// URI of the default OUTPUT_FOLDER of this Service, used as search root when testing
			// if a file already exists.
			URI outputFolderURI = new URI(dataRegistryPath + "/" + EXTRACTOR_DR_OUT);
			plogger.info("Outputfolder: " + outputFolderURI.toASCIIString());
			plogger.info("Searching for duplicated files...");
			
			URI[] searchResults = dataRegistry.findFilesWithNameContaining(registryRoot, fileName);
			
			
			// debug output
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<searchResults.length;i++){
				sb = sb.append(searchResults[i].toASCIIString() + "\n");
			}
			// end debug output
			
			
			plogger.info("Found the following hits: " + sb.toString());
			
			// The returned URI[] searchResults is not NULL and
			if(searchResults != null) {
				// there have been some hits, e.g. files with the same filename, but maybe in a different path...
				if(searchResults.length > 0) {
					for(int i=0;i < searchResults.length;i++) {
						String currentURI = searchResults[i].toASCIIString();
						// Check if there have been hits inside the OUTPUT_FOLDER
						if(currentURI.indexOf(EXTRACTOR_DR_OUT)!=-1) {
							// There is (at least) a file with the same name inside the OUTPUT_FOLDER so...
							plogger.info("File already exists: " + fileName + ". File will be renamed...");
							
							// ...get a timestamp
							String timestamp = getTimeStamp();   
							
							// ...split the initial filename in a prefix and...
							String fileNamePrefix = fileName.substring(0, fileName.lastIndexOf("."));
					        plogger.info("fileNamePrefix: " + fileNamePrefix);
					        
	//				        // ...and the postfix
					        String fileNamePostfix = fileName.substring(fileName.lastIndexOf("."));
					        plogger.info("fileNamePostfix: " + fileNamePostfix);
					        
	//				        // and add the "_[timestamp]" to the filename
					        plogger.info("Adding timestamp to filename: " + timestamp);
							String renamedFileName = fileNamePrefix + "_" + timestamp + fileNamePostfix;
							
						    plogger.info("New migratedFileName: " + renamedFileName);
						    // create a new URI for the renamed file and...
						    URI renamedFileURI = new URI(outputFolderURI.toASCIIString() + "/" + renamedFileName);
							plogger.info("New migratedFileURI: " + renamedFileURI.toASCIIString());
							plogger.info("Storing file with new name: " + renamedFileName + " to DataRegistry...");
							// store it in the DataRegistry, using the new filename
							dataRegistry.storeBinary(renamedFileURI, binary);
							plogger.info("Successfully stored binary to DataRegistry: " + renamedFileName);
							fileURI = renamedFileURI;
						}
						
						// There have been hits (e.g. files with the same name), but in a different folder,
						// so just store the file with its initial name to the DataRegistry
						else {
							plogger.info("Attempting to store binary to DataRegistry: " + fileName);
							// store the file...
							dataRegistry.storeBinary(fileURI, binary);
							plogger.info("Successfully stored binary to DataRegistry: " + fileName);
						}
					}
				}
				// There have been NO search hits, so store the file with its initial filename, too.
				else {
					plogger.info("Attempting to store binary to DataRegistry: " + fileName);
					// store the file to the DR...
					dataRegistry.storeBinary(fileURI, binary);
					plogger.info("Successfully stored binary to DataRegistry: " + fileName);
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
		if(DataModelUtils.isValidReference(fileURI.toASCIIString())) {
			// ...if yes, return it
			plogger.info("Validating the created file URI: " + fileURI.toASCIIString());
			plogger.info("Validataion result: OK!");
			return fileURI;
		}
		else {
			// ...if no, log out an error and return NULL.
			plogger.error("The URI of the migrated file is not valid!");
			return null;
		}
	}
    
    private String getTimeStamp() {
		String day, month, year, hour, minute, second, millisecond = null;
		
		if(DAY > 9) {
			day = "" + DAY; 
		}
		else {
			day = "0" + DAY;
		}
		if(MONTH > 9) {
			month = "" + MONTH;
		}
		else {
			month = "0" + MONTH;
		}

		year = "" + YEAR;

		if(HOUR > 9) {
			hour = "" + HOUR;
		}
		else {
			hour = "0" + HOUR;
		}
		if(MINUTE > 9) {
			minute = "" + MINUTE;
		}
		else {
			minute = "0" + MINUTE;
		}
		if(SECOND > 9) {
			second = "" + SECOND;
		}
		else {
			second = "0" + SECOND;
		}
		Calendar now = Calendar.getInstance();
		millisecond = "" + now.get(Calendar.MILLISECOND) + "ms";
		
		String timestamp = day + "-" + month + "-" + year + "_" + hour + "-" + minute + "-" + second + "_" + millisecond;
		return timestamp;
	}
    
    private static byte[] getByteArrayFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            //throw new IllegalArgumentException("getBytesFromFile@JpgToTiffConverter:: The file is too large (i.e. larger than 2 GB!");
        	System.out.println("Datei ist zu gross (e.g. groesser als 2GB)!");
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    private void deleteTempFiles(File srcFile, File xcelFile, File outputFile, File in_folder, File out_folder, File work_folder) {
    	boolean success = false;
		plogger.info("Deleting temp-files...:");
		plogger.info("Deleting file: " + srcFile.getName());
		success = srcFile.delete();
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("File could not be deleted.");
		}
		
		plogger.info("Deleting file: " + xcelFile.getName());	
		success = xcelFile.delete();
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("File could not be deleted.");
		}
		plogger.info("Deleting file: " + outputFile.getName());	
		success = outputFile.delete();
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("File could not be deleted.");
		}
		plogger.info("Deleting folder: " + in_folder.getName());	
		success = in_folder.delete();
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("Folder could not be deleted.");
		}
		plogger.info("Deleting folder: " + out_folder.getName());	
		success = out_folder.delete();
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("Folder could not be deleted.");
		}
		plogger.info("Deleting folder: " + work_folder.getName());	
		success = work_folder.delete();
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("Folder could not be deleted.");
		}
    }


}
