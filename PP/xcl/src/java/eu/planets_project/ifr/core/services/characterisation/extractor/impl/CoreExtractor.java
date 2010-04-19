package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ProcessRunner;

/**
 *
 */
public class CoreExtractor {

	private static String XCLTOOLS_HOME = (System.getenv("XCLTOOLS_HOME") + File.separator);
    private static String EXTRACTOR_HOME = (XCLTOOLS_HOME
    										+ File.separator
											+ "extractor"
											+ File.separator).replace(File.separator + File.separator, File.separator);
    private static final String EXTRACTOR_TOOL = "extractor";
    private String thisExtractorName;
    private static Logger log = Logger.getLogger(CoreExtractor.class.getName());
    private static String NO_NORM_DATA_FLAG = "disableNormDataInXCDL";
    private static String RAW_DATA_FLAG = "enableRawDataInXCDL";
    private static String OPTIONAL_XCEL_PARAM = "optionalXCELString";
    public static final FormatRegistry format = FormatRegistryFactory
            .getFormatRegistry();

    /**
     * @param extractorName
     * @param logger
     */
    public CoreExtractor(String extractorName) {
        thisExtractorName = extractorName;
//        extractorWork = extractorName.toUpperCase();
    }

    public static List<URI> getSupportedInputFormats() {
        List<URI> inputFormats = new ArrayList<URI>();
        inputFormats.add(format.createExtensionUri("JPEG"));
        inputFormats.add(format.createExtensionUri("JPG"));
        inputFormats.add(format.createExtensionUri("TIFF"));
        inputFormats.add(format.createExtensionUri("TIF"));
        inputFormats.add(format.createExtensionUri("GIF"));
        inputFormats.add(format.createExtensionUri("PNG"));
        inputFormats.add(format.createExtensionUri("BMP"));
        inputFormats.add(format.createExtensionUri("PDF"));
        // inputFormats.add(Format.extensionToURI("DOC"));
        // inputFormats.add(Format.extensionToURI("DOCX"));
        return inputFormats;
    }

    public static List<URI> getSupportedOutputFormats() {
        List<URI> outputFormats = new ArrayList<URI>();
        outputFormats.add(format.createExtensionUri("XCDL"));
        return outputFormats;
    }

    /**
     * @param input
     * @param inputFormat TODO
     * @param xcelFile
     * @param parameters
     * @return The resulting XCDL file created by the Extractor, or null if no file was written
     */
    public File extractXCDL(DigitalObject input, URI inputFormat,
            File xcelFile, List<Parameter> parameters) {

        if (EXTRACTOR_HOME == null) {
            System.err
                    .println("EXTRACTOR_HOME is not set! Please create an system variable\n"
                            + "and point it to the Extractor installation folder!");
            log
                    .severe("EXTRACTOR_HOME is not set! Please create an system variable\n"
                            + "and point it to the Extractor installation folder!");
        }

        log.info("Starting " + thisExtractorName + " Service...");

        List<String> extractor_arguments = null;
        
        File srcFile = DigitalObjectUtils.toFile(input);

        ProcessRunner shell = new ProcessRunner();
        log.info("EXTRACTOR_HOME = " + EXTRACTOR_HOME);
        log.info("Configuring Commandline");

        extractor_arguments = new ArrayList<String>();
        extractor_arguments.add(EXTRACTOR_HOME + EXTRACTOR_TOOL);
        String srcFilePath = srcFile.getAbsolutePath().replace('\\', '/');
        // System.out.println("Src-file path: " + srcFilePath);
        log.info("Input-Image file path: " + srcFilePath);
        extractor_arguments.add(srcFilePath);

        File output = null;
        try {
            output = File.createTempFile("xcl", ".xcdl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String outputFilePath = output.getAbsolutePath();
        /* If we have no XCEL, let the extractor find the appropriate one: */
        if (xcelFile != null) {
            String xcelFilePath = xcelFile.getAbsolutePath().replace('\\', '/');
            // System.out.println("XCEL-file path: " + xcelFilePath);
            log.info("Input-XCEL file path: " + xcelFilePath);
            extractor_arguments.add(xcelFilePath);
            extractor_arguments.add(outputFilePath);
        } else {
            extractor_arguments.add("-o");
            extractor_arguments.add(outputFilePath);

            // No XCEL -> default output location

            // outputFilePath = EXTRACTOR_HOME + "xcdlOutput.xml";
        }

        // Got Parameters???
        if (parameters != null) {
            if (parameters.size() != 0) {
                log.info("Got additional parameters: ");
                for (Iterator<Parameter> iterator = parameters.iterator(); iterator
                        .hasNext();) {
                    Parameter parameter = (Parameter) iterator.next();
                    String name = parameter.getName();
                    if (name.equalsIgnoreCase(OPTIONAL_XCEL_PARAM)) {
                        log
                                .info("Optional XCEL passed! Using specified XCEL.");
                        continue;
                    }

                    if (name.equalsIgnoreCase(RAW_DATA_FLAG)) {
                        log.info("Got Parameter: " + name + " = "
                                + parameter.getValue());
                        log
                                .info("Configuring Extractor to write RAW data!");
                        extractor_arguments.add(parameter.getValue());
                        continue;
                    }

                    else if (name.equalsIgnoreCase(NO_NORM_DATA_FLAG)) {
                        log.info("Got Parameter: " + name + " = "
                                + parameter.getValue());
                        log.info("Configuring Extractor to skip NormData!");
                        extractor_arguments.add(parameter.getValue());
                        continue;
                    } else {
                        log.warning("Invalid parameter: " + name + " = '"
                                + parameter.getValue()
                                + "'. Ignoring parameter...!");
                        continue;
                    }
                }
            }
        }

        String line = "";
        for (String argument : extractor_arguments) {
            line = line + argument + " ";
        }

        log.info("Setting command to: " + line);
        shell.setCommand(extractor_arguments);

        shell.setStartingDir(new File(EXTRACTOR_HOME));
        log.info("Setting starting Dir to: " + EXTRACTOR_HOME);
        //set timeout to 10 minutes
        shell.setTimeout(600000);
        log.info("Starting Extractor tool...");
        shell.run();
        String processOutput = shell.getProcessOutputAsString();
        String processError = shell.getProcessErrorAsString();
        log.info("Process Output: " + processOutput);
        System.out.println("Process Output: "+processOutput);
        if( ! "".equals(processError ) ) {
            log.severe("Process Error: " + processError);
            System.err.println("Process Error: "+processError);
        }

        log.info("Creating File to return...");
        if(!output.exists()) {
            log.severe("File doesn't exist: " + output.getAbsolutePath());
        	return null;
        }
        return output;
    }
    
    /**
     * @param inputFormat The format
     * @return A service report indicating the format is not supported
     */
    public static ServiceReport unsupportedInputFormatReport(URI inputFormat) {
        return new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                "Unsupported input format: " + inputFormat);
    }

    /**
     * @param format The format
     * @param parameters The parameters
     * @return True, if either the extractor provides an XCEL for the format, or
     *         the XCEL has been given in the parameters
     */
    public static boolean supported(URI format, List<Parameter> parameters) {
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (parameter.getName().toLowerCase().contains("xcel")) {
                    return true;
                }
            }
        }
        if (format == null) {
            return false;
        }
        List<URI> aliases = FormatRegistryFactory.getFormatRegistry()
                .getFormatUriAliases(format);
        List<URI> supported = getSupportedInputFormats();
        for (URI a : aliases) {
            for (URI s : supported) {
                if (a.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}
