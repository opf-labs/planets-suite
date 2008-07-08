package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoBinary;


@Stateless()
@Local(BasicCharacteriseOneBinaryXCELtoBinary.class)
@Remote(BasicCharacteriseOneBinaryXCELtoBinary.class)
@LocalBinding(jndiBinding = "planets/Extractor2Binary")
@RemoteBinding(jndiBinding = "planets-project.eu/Extractor2Binary")
@WebService(
        name = "Extractor2Binary", 
//      This is not appropriate when using the endpointInterface approach.
        serviceName= BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@MTOM
public class Extractor2Binary implements BasicCharacteriseOneBinaryXCELtoBinary, Serializable {

	private static final long serialVersionUID = 3007130161689982082L;
	private final static PlanetsLogger plogger = PlanetsLogger.getLogger(Extractor2Binary.class);
	private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static String EXTRACTOR_WORK = null;
	private static String EXTRACTOR_IN = null;
	private static String EXTRACTOR_OUT = null;
	private static String OUTPUTFILE_NAME = "extractor2binary_xcdl_out.xcdl";
	
    
	/**
     * 
     * @param binary a byte[] which contains the image data
     * @param xcel a String holding the Contents of a XCEL file
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME)
    @WebResult(
            name = BasicCharacteriseOneBinaryXCELtoBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = BasicCharacteriseOneBinaryXCELtoBinary.NAME + "Result")
    public byte[] basicCharacteriseOneBinaryXCELtoBinary ( 
    @WebParam(
            name = "binary", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = "binary")     
    byte[] binary,
    @WebParam(
            name = "XCEL_String", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = "XCEL_String")
            String xcel
    ) throws PlanetsException {
    	byte[] outputXCDL = extractXCDL(binary,xcel);
    	return outputXCDL;
    }
    

	private byte[] extractXCDL (byte[] binary, String xcel) {
    	if(SYSTEM_TEMP.lastIndexOf(File.separator) == SYSTEM_TEMP.length()-1) {
			EXTRACTOR_WORK = SYSTEM_TEMP + "EXTRACTOR2BINARY" + File.separator;
			EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
			EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
		}
		if (SYSTEM_TEMP.endsWith("/tmp")){
			EXTRACTOR_WORK = SYSTEM_TEMP + File.separator + "EXTRACTOR2BINARY" + File.separator;
			EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
			EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
		}
		if(EXTRACTOR_HOME.endsWith(File.separator + File.separator)) {
			EXTRACTOR_HOME = EXTRACTOR_HOME.replace(File.separator + File.separator, File.separator);
		}
		
		plogger.info("Starting Extractor2Binary Service...");
		
		List <String> extractor_arguments = null;
		File srcFile = null;
		File xcelFile = null;
		File extractor_work_folder = null;
		File extractor_in_folder = null;
		File extractor_out_folder = null;
		
		try {
			extractor_work_folder = new File(EXTRACTOR_WORK);
			extractor_work_folder.mkdir();
			plogger.info("Extractor2Binary work folder created: " + EXTRACTOR_WORK);
			extractor_in_folder = new File(EXTRACTOR_IN);
			extractor_in_folder.mkdir();
			plogger.info("Extractor2Binary input folder created: " + EXTRACTOR_IN);
			extractor_out_folder = new File(EXTRACTOR_OUT);
			extractor_out_folder.mkdir();
			plogger.info("Extractor2Binary output folder created: " + EXTRACTOR_OUT);
			
			srcFile = new File(EXTRACTOR_IN, "extractor2binary_image_in.bin");
			FileOutputStream fos = new FileOutputStream(srcFile);
			fos.write(binary);
			fos.flush();
			fos.close();
			
			xcelFile = new File(EXTRACTOR_IN, "extractor2binary_xcel_in.xml");
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
	//	System.out.println("Src-file path: " + srcFilePath);
		plogger.info("Input-Image file path: " + srcFilePath);
		extractor_arguments.add(srcFilePath);
		String xcelFilePath = xcelFile.getPath().replace('\\', '/');
	//	System.out.println("XCEL-file path: " + xcelFilePath);
		plogger.info("Input-XCEL file path: " + xcelFilePath);
		extractor_arguments.add(xcelFilePath);
		String outputFilePath = EXTRACTOR_OUT + OUTPUTFILE_NAME;
		outputFilePath = outputFilePath.replace('\\', '/');
	//	System.out.println("Output-file path: " + outputFilePath);
		extractor_arguments.add(outputFilePath);
	
		String line = "";
		for (String argument : extractor_arguments) {
			line = line + argument + " "; 
		}
		
		plogger.info("Setting command to: " + line);
		shell.setCommand(extractor_arguments);
		
		shell.setStartingDir(new File(EXTRACTOR_HOME));
		plogger.info("Setting starting Dir to: " + EXTRACTOR_HOME);
		plogger.info("Starting Extractor2Binary...");
		shell.run();
		String processOutput = shell.getProcessOutputAsString();
		String processError = shell.getProcessErrorAsString();
		plogger.info("Process Output: " + processOutput);
		plogger.info("Process Error: " + processError);
		
		byte[] binary_out = null;

		try {
			plogger.info("Creating byte[] to return...");
			binary_out = getByteArrayFromFile(new File(outputFilePath));
			
		} catch (FileNotFoundException e) {
			plogger.error("File not found: " + outputFilePath);
			e.printStackTrace();
		} catch (IOException e) {
			plogger.error("IO Error: ");
			e.printStackTrace();
		}
		
		deleteTempFiles(srcFile, xcelFile, new File(outputFilePath), extractor_in_folder, extractor_out_folder, extractor_work_folder);
		
		plogger.info("Returning XCDL as byte[]");		
		return binary_out;
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
