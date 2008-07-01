package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
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
	private final static PlanetsLogger plogger = PlanetsLogger.getLogger(Extractor.class, LOG_CONFIG_FILE);
	private static final String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
	private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
	private static String EXTRACTOR_WORK = null;
	private static String EXTRACTOR_IN = null;
	private static String EXTRACTOR_OUT = null;
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
    public String basicCharacteriseOneBinaryXCEL ( 
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
		String outputFilePath = EXTRACTOR_OUT + "xcdl_out.xcdl";
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
		plogger.info(shell.getProcessOutputAsString());
		StringBuffer sb = new StringBuffer();
		String in = "";
		String xcdl = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(outputFilePath)));
			
			while((in = reader.readLine())!=null) {
				sb.append(in);
			}
			xcdl = sb.toString();
			plogger.info("XCDL String created.");
			plogger.info("XCDL: " + xcdl);
		} catch (FileNotFoundException e) {
			plogger.error("File not found: " + outputFilePath);
			e.printStackTrace();
		} catch (IOException e) {
			plogger.error("IO Error: ");
			e.printStackTrace();
		}
		
//		deleteTempFiles(srcFile, xcelFile, new File(outputFilePath), extractor_in_folder, extractor_out_folder, extractor_work_folder);
		
		plogger.info("Returning XCDL String.");		
		return xcdl;
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
