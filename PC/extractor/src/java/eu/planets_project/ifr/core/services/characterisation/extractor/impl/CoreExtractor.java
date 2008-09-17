package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

public class CoreExtractor {

    private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME")
            + File.separator;
    private static final String SYSTEM_TEMP = System
            .getProperty("java.io.tmpdir");
    private static String EXTRACTOR_WORK = null;
    private static String EXTRACTOR_IN = null;
    private static String EXTRACTOR_OUT = null;
    private static String OUTPUTFILE_NAME;
    private static String EXTRACTOR_NAME;
    private PlanetsLogger plogger;

    public CoreExtractor(String extractorName, PlanetsLogger logger) {
        this.plogger = logger;
        EXTRACTOR_NAME = extractorName;
        OUTPUTFILE_NAME = EXTRACTOR_NAME.toLowerCase() + "_xcdl_out.xcdl";

    }

    public byte[] extractXCDL(byte[] binary, byte[] xcel) {
        if (SYSTEM_TEMP.endsWith(File.separator)) {
            EXTRACTOR_WORK = SYSTEM_TEMP + EXTRACTOR_NAME + File.separator;
            EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
            EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
        } else {
            EXTRACTOR_WORK = SYSTEM_TEMP + File.separator + EXTRACTOR_NAME
                    + File.separator;
            EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
            EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
        }
        if (SYSTEM_TEMP.endsWith(File.separator + "tmp")) {
            EXTRACTOR_WORK = SYSTEM_TEMP + File.separator + EXTRACTOR_NAME
                    + File.separator;
            EXTRACTOR_IN = EXTRACTOR_WORK + "IN" + File.separator;
            EXTRACTOR_OUT = EXTRACTOR_WORK + "OUT" + File.separator;
        }
        if (EXTRACTOR_HOME.endsWith(File.separator + File.separator)) {
            EXTRACTOR_HOME = EXTRACTOR_HOME.replace(File.separator
                    + File.separator, File.separator);
        }

        plogger.info("Starting " + EXTRACTOR_NAME + " Service...");

        List<String> extractor_arguments = null;
        File srcFile = null;
        File xcelFile = null;
        File extractor_work_folder = null;
        File extractor_in_folder = null;
        File extractor_out_folder = null;

        try {
            extractor_work_folder = new File(EXTRACTOR_WORK);
            extractor_work_folder.mkdir();
            plogger.info(EXTRACTOR_NAME + " work folder created: "
                    + EXTRACTOR_WORK);
            extractor_in_folder = new File(EXTRACTOR_IN);
            extractor_in_folder.mkdir();
            plogger.info(EXTRACTOR_NAME + " input folder created: "
                    + EXTRACTOR_IN);
            extractor_out_folder = new File(EXTRACTOR_OUT);
            extractor_out_folder.mkdir();
            plogger.info(EXTRACTOR_NAME + " output folder created: "
                    + EXTRACTOR_OUT);

            srcFile = new File(EXTRACTOR_IN, "extractor_image_in.bin");
            FileOutputStream fos = new FileOutputStream(srcFile);
            fos.write(binary);
            fos.flush();
            fos.close();

            /*
             * The extractor supports finding the XCEL on its own, so this is
             * optional:
             */
            if (xcel != null) {
                xcelFile = new File(EXTRACTOR_IN, "extractor_xcel_in.xcel");
                FileOutputStream xcelOut = new FileOutputStream(xcelFile);
                xcelOut.write(xcel);
                xcelOut.flush();
                xcelOut.close();
            }

            plogger.info("System-Temp folder is: " + SYSTEM_TEMP);

        } catch (IOException e) {
            plogger.error("Could not create Temp-file!");
            e.printStackTrace();
        }

        ProcessRunner shell = new ProcessRunner();
        plogger.info("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
        plogger.info("Configuring Commandline");

        extractor_arguments = new ArrayList<String>();
        extractor_arguments.add(EXTRACTOR_HOME + "extractor");
        String srcFilePath = srcFile.getPath().replace('\\', '/');
        // System.out.println("Src-file path: " + srcFilePath);
        plogger.info("Input-Image file path: " + srcFilePath);
        extractor_arguments.add(srcFilePath);

        String outputFilePath = EXTRACTOR_OUT + OUTPUTFILE_NAME;
        outputFilePath = outputFilePath.replace('\\', '/');
        // System.out.println("Output-file path: " + outputFilePath);

        /* If we have no XCEL, let the extracto find the appropriate one: */
        if (xcel != null) {
            String xcelFilePath = xcelFile.getPath().replace('\\', '/');
            // System.out.println("XCEL-file path: " + xcelFilePath);
            plogger.info("Input-XCEL file path: " + xcelFilePath);
            extractor_arguments.add(xcelFilePath);
            extractor_arguments.add(outputFilePath);
        } else {
        	 extractor_arguments.add("-o");
        	 extractor_arguments.add(outputFilePath);
        	
            // No XCEL -> default output location
            
//        	outputFilePath = EXTRACTOR_HOME + "xcdlOutput.xml";
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
        try {
            plogger.info("Creating byte[] to return...");
            binary_out = getByteArrayFromFile(new File(outputFilePath));

            // outputURI = storeBinaryInDataRegistry(binary_out,
            // OUTPUTFILE_NAME);

        } catch (FileNotFoundException e) {
            plogger.error("File not found: " + outputFilePath);
            e.printStackTrace();
        } catch (IOException e) {
            plogger.error("IO Error: ");
            e.printStackTrace();
        }

        deleteTempFiles(srcFile, xcelFile, new File(outputFilePath),
                extractor_in_folder, extractor_out_folder,
                extractor_work_folder);

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
            // throw new IllegalArgumentException(
            // "getBytesFromFile@JpgToTiffConverter:: The file is too large (i.e. larger than 2 GB!"
            // );
            System.out.println("Datei ist zu gross (e.g. groesser als 2GB)!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    private void deleteTempFiles(File srcFile, File xcelFile, File outputFile,
            File in_folder, File out_folder, File work_folder) {
        boolean success = false;
        plogger.info("Deleting temp-files...:");
        plogger.info("Deleting file: " + srcFile.getName());
        success = srcFile.delete();
        if (success) {
            plogger.info("Success!");
        } else {
            plogger.info("File could not be deleted.");
        }
        if (xcelFile != null) {
            plogger.info("Deleting file: " + xcelFile.getName());
            success = xcelFile.delete();
            if (success) {
                plogger.info("Success!");
            } else {
                plogger.info("File could not be deleted.");
            }
        }
        plogger.info("Deleting file: " + outputFile.getName());
        success = outputFile.delete();
        if (success) {
            plogger.info("Success!");
        } else {
            plogger.info("File could not be deleted.");
        }
        plogger.info("Deleting folder: " + in_folder.getName());
        success = in_folder.delete();
        if (success) {
            plogger.info("Success!");
        } else {
            plogger.info("Folder could not be deleted.");
        }
        plogger.info("Deleting folder: " + out_folder.getName());
        success = out_folder.delete();
        if (success) {
            plogger.info("Success!");
        } else {
            plogger.info("Folder could not be deleted.");
        }
        plogger.info("Deleting folder: " + work_folder.getName());
        success = work_folder.delete();
        if (success) {
            plogger.info("Success!");
        } else {
            plogger.info("Folder could not be deleted.");
        }
    }

}
