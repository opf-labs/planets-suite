package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;

public class CoreExtractor {

    private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
//    private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
//    private static final String SYSTEM_TEMP = null;
    private static String EXTRACTOR_WORK = null;
    private static String EXTRACTOR_IN = "INPUT";
    private static String EXTRACTOR_OUT = "OUTPUT";
    private static String OUTPUTFILE_NAME;
    private static String EXTRACTOR_NAME;
    private PlanetsLogger plogger;
    private static String NO_NORM_DATA_FLAG = "disableNormDataInXCDL";
    private static String RAW_DATA_FLAG = "enableRawDataInXCDL";

    public CoreExtractor(String extractorName, PlanetsLogger logger) {
        this.plogger = logger;
//        SYSTEM_TEMP = FileUtils.createWorkFolderInSysTemp(EXTRACTOR_WORK);
        EXTRACTOR_NAME = extractorName;
        OUTPUTFILE_NAME = EXTRACTOR_NAME.toLowerCase() + "_xcdl_out.xcdl";
        EXTRACTOR_WORK = extractorName.toUpperCase();
    }

    public byte[] extractXCDL(byte[] binary, byte[] xcel, Parameters parameters) {
    	
    	if(EXTRACTOR_HOME==null){
    		System.err.println("EXTRACTOR_HOME is not set! Please create an system variable\n" +
    				"and point it to the Extractor installation folder!");
    		plogger.error("EXTRACTOR_HOME is not set! Please create an system variable\n" +
    				"and point it to the Extractor installation folder!");
    	}
    	

        plogger.info("Starting " + EXTRACTOR_NAME + " Service...");

        List<String> extractor_arguments = null;
        File srcFile = null;
        File xcelFile = null;
        File extractor_work_folder = null;
        File extractor_in_folder = null;
        File extractor_out_folder = null;

        try {
            extractor_work_folder = FileUtils.createWorkFolderInSysTemp(EXTRACTOR_WORK);
            
            plogger.info(EXTRACTOR_NAME + " work folder created: "
                    + EXTRACTOR_WORK);
            
            extractor_in_folder = FileUtils.createFolderInWorkFolder(extractor_work_folder, EXTRACTOR_IN);
            plogger.info(EXTRACTOR_NAME + " input folder created: "
                    + EXTRACTOR_IN);
            
            extractor_out_folder = FileUtils.createFolderInWorkFolder(extractor_work_folder, EXTRACTOR_OUT);
            plogger.info(EXTRACTOR_NAME + " output folder created: "
                    + EXTRACTOR_OUT);

            srcFile = new File(extractor_in_folder, "extractor_image_in.bin");
            FileOutputStream fos = new FileOutputStream(srcFile);
            fos.write(binary);
            fos.flush();
            fos.close();

            /*
             * The extractor supports finding the XCEL on its own, so this is
             * optional:
             */
            if (xcel != null) {
                xcelFile = new File(extractor_in_folder, "extractor_xcel_in.xcel");
                FileOutputStream xcelOut = new FileOutputStream(xcelFile);
                xcelOut.write(xcel);
                xcelOut.flush();
                xcelOut.close();
            }

//            plogger.info("System-Temp folder is: " + SYSTEM_TEMP);

        } catch (IOException e) {
            plogger.error("Could not create Temp-file!");
            e.printStackTrace();
        }

        ProcessRunner shell = new ProcessRunner();
        plogger.info("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
        plogger.info("Configuring Commandline");
        
        extractor_arguments = new ArrayList<String>();
        extractor_arguments.add(EXTRACTOR_HOME + "extractor");
        String srcFilePath = srcFile.getAbsolutePath().replace('\\', '/');
        // System.out.println("Src-file path: " + srcFilePath);
        plogger.info("Input-Image file path: " + srcFilePath);
        extractor_arguments.add(srcFilePath);
        
        String outputFilePath = extractor_out_folder.getAbsolutePath() + File.separator + OUTPUTFILE_NAME;
        outputFilePath = outputFilePath.replace('\\', '/');
        // System.out.println("Output-file path: " + outputFilePath);

        /* If we have no XCEL, let the extracto find the appropriate one: */
        if (xcel != null) {
            String xcelFilePath = xcelFile.getAbsolutePath().replace('\\', '/');
            System.out.println("XCEL-file path: " + xcelFilePath);
            plogger.info("Input-XCEL file path: " + xcelFilePath);
            extractor_arguments.add(xcelFilePath);
            extractor_arguments.add(outputFilePath);
        } else {
        	 extractor_arguments.add("-o");
        	 extractor_arguments.add(outputFilePath);
        	
            // No XCEL -> default output location
            
//        	outputFilePath = EXTRACTOR_HOME + "xcdlOutput.xml";
        }
        
        // Got Parameters???
        if(parameters!=null) {
        	List <Parameter> parameterList = parameters.getParameters();
        	for (Iterator<Parameter> iterator = parameterList.iterator(); iterator.hasNext();) {
				Parameter parameter = (Parameter) iterator.next();
				String name = parameter.name;
				if(name.equalsIgnoreCase(RAW_DATA_FLAG)) {
					plogger.info("Got Parameter: " + name + " = " + parameter.value);
					plogger.info("Configuring Extractor to write RAW data!");
					extractor_arguments.add(parameter.value);
					continue;
				}
				else if(name.equalsIgnoreCase(NO_NORM_DATA_FLAG)) {
					plogger.info("Got Parameter: " + name + " = " + parameter.value);
					plogger.info("Configuring Extractor to skip NormData!");
					extractor_arguments.add(parameter.value);
					continue;
				}
				else {
					plogger.warn("Invalid parameter: " + name + " = '" + parameter.value + "'. Ignoring parameter...!");
					continue;
				}
        	}
        }
        

        String line = "";
        for (String argument : extractor_arguments) {
            line = line + argument + " ";
        }

        plogger.info("Setting command to: " + line);
        shell.setCommand(extractor_arguments);

        shell.setStartingDir(new File(EXTRACTOR_HOME));
        plogger.info("Setting starting Dir to: " + EXTRACTOR_HOME);
        plogger.info("Starting Extractor tool...");
        shell.run();
        String processOutput = shell.getProcessOutputAsString();
        String processError = shell.getProcessErrorAsString();
        plogger.info("Process Output: " + processOutput);
        plogger.info("Process Error: " + processError);
        // StringWriter sWriter = new StringWriter();

        byte[] binary_out = null;
        plogger.info("Creating byte[] to return...");
		binary_out = ByteArrayHelper.read(new File(outputFilePath));

        boolean successfullyDeleted = FileUtils.deleteTempFiles(extractor_work_folder, plogger);
        plogger.info("Deleted all temp files = " + successfullyDeleted);
        System.out.println("Deleted all temp files = " + successfullyDeleted);

        return binary_out;
    }
}
