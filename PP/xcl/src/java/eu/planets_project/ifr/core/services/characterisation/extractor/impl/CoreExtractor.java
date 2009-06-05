package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;

/**
 *
 */
public class CoreExtractor {

    private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME")
            + File.separator;
    // private static final String SYSTEM_TEMP =
    // System.getProperty("java.io.tmpdir");
    // private static final String SYSTEM_TEMP = null;
    private String extractorWork = null;
    private static String EXTRACTOR_IN = "INPUT";
    private static String EXTRACTOR_OUT = "OUTPUT";
    private String outputFileName;
    private String thisExtractorName;
    private Log plogger;
    private static String NO_NORM_DATA_FLAG = "disableNormDataInXCDL";
    private boolean normDataDisabled = false;
    private static String RAW_DATA_FLAG = "enableRawDataInXCDL";
    private static String OPTIONAL_XCEL_PARAM = "optionalXCELString";
    private static final FormatRegistry format = FormatRegistryFactory
            .getFormatRegistry();

    /**
     * @param extractorName
     * @param logger
     */
    public CoreExtractor(String extractorName, Log logger) {
        this.plogger = logger;
        // SYSTEM_TEMP = FileUtils.createWorkFolderInSysTemp(EXTRACTOR_WORK);
        thisExtractorName = extractorName;
        outputFileName = thisExtractorName.toLowerCase() + "_xcdl_out.xcdl";
        extractorWork = extractorName.toUpperCase();
    }

    // this is a work around to disable norm data output in XCDL,
    // as long as flag in extractor does not work
    @SuppressWarnings("unchecked")
    private byte[] removeNormData(byte[] XCDLtoRemoveNormDataFrom) {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document xcdlDoc;

        XMLOutputter xmlOut = new XMLOutputter();
        BufferedWriter xmlWriter;

        File xcdlTmp = FileUtils
                .writeByteArrayToTempFile(XCDLtoRemoveNormDataFrom);
        File cleanedXCDL = FileUtils.getTempFile("cleanedXCDL", "xcdl");
        byte[] result = null;

        try {
            xcdlDoc = saxBuilder.build(xcdlTmp);

            Element rootXCDL = xcdlDoc.getRootElement();

            List<Element> content = rootXCDL.getChildren();

            for (Element objectElement : content) {
                List<Element> objectChildren = objectElement.getChildren();
                for (Element level2Element : objectChildren) {
                    if (level2Element.getName().equalsIgnoreCase("normData")) {
                        level2Element.detach();
                        break;
                    }

                }
            }
            xmlWriter = new BufferedWriter(new FileWriter(cleanedXCDL));
            xmlOut.output(xcdlDoc, xmlWriter);
            result = FileUtils.readFileIntoByteArray(cleanedXCDL);

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        normDataDisabled = false;
        return result;
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
     * @param binary
     * @param xcel
     * @param parameters
     * @return xcdl as byte array
     */
    public byte[] extractXCDL(byte[] binary, byte[] xcel,
            List<Parameter> parameters) {

        if (EXTRACTOR_HOME == null) {
            System.err
                    .println("EXTRACTOR_HOME is not set! Please create an system variable\n"
                            + "and point it to the Extractor installation folder!");
            plogger
                    .error("EXTRACTOR_HOME is not set! Please create an system variable\n"
                            + "and point it to the Extractor installation folder!");
        }

        plogger.info("Starting " + thisExtractorName + " Service...");

        List<String> extractor_arguments = null;
        File srcFile = null;
        File xcelFile = null;
        File extractor_work_folder = null;
        File extractor_in_folder = null;
        File extractor_out_folder = null;

        try {
            extractor_work_folder = FileUtils
                    .createWorkFolderInSysTemp(extractorWork);

            plogger.info(thisExtractorName + " work folder created: "
                    + extractorWork);

            extractor_in_folder = FileUtils.createFolderInWorkFolder(
                    extractor_work_folder, EXTRACTOR_IN);
            plogger.info(thisExtractorName + " input folder created: "
                    + EXTRACTOR_IN);

            extractor_out_folder = FileUtils.createFolderInWorkFolder(
                    extractor_work_folder, EXTRACTOR_OUT);
            plogger.info(thisExtractorName + " output folder created: "
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
                xcelFile = new File(extractor_in_folder,
                        "extractor_xcel_in.xcel");
                FileOutputStream xcelOut = new FileOutputStream(xcelFile);
                xcelOut.write(xcel);
                xcelOut.flush();
                xcelOut.close();
            }

            // plogger.info("System-Temp folder is: " + SYSTEM_TEMP);

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

        String outputFilePath = extractor_out_folder.getAbsolutePath()
                + File.separator + outputFileName;
        outputFilePath = outputFilePath.replace('\\', '/');
        // System.out.println("Output-file path: " + outputFilePath);

        /* If we have no XCEL, let the extracto find the appropriate one: */
        if (xcel != null) {
            String xcelFilePath = xcelFile.getAbsolutePath().replace('\\', '/');
            // System.out.println("XCEL-file path: " + xcelFilePath);
            plogger.info("Input-XCEL file path: " + xcelFilePath);
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
                plogger.info("Got additional parameters: ");
                for (Iterator<Parameter> iterator = parameters.iterator(); iterator
                        .hasNext();) {
                    Parameter parameter = (Parameter) iterator.next();
                    String name = parameter.getName();
                    if (name.equalsIgnoreCase(OPTIONAL_XCEL_PARAM)) {
                        plogger
                                .info("Optional XCEL passed! Using specified XCEL.");
                        continue;
                    }

                    if (name.equalsIgnoreCase(RAW_DATA_FLAG)) {
                        plogger.info("Got Parameter: " + name + " = "
                                + parameter.getValue());
                        plogger
                                .info("Configuring Extractor to write RAW data!");
                        extractor_arguments.add(parameter.getValue());
                        continue;
                    }

                    else if (name.equalsIgnoreCase(NO_NORM_DATA_FLAG)) {
                        plogger.info("Got Parameter: " + name + " = "
                                + parameter.getValue());
                        plogger.info("Configuring Extractor to skip NormData!");
                        extractor_arguments.add(parameter.getValue());
                        normDataDisabled = true;
                        continue;
                    } else {
                        plogger.warn("Invalid parameter: " + name + " = '"
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

        plogger.info("Setting command to: " + line);
        shell.setCommand(extractor_arguments);

        shell.setStartingDir(new File(EXTRACTOR_HOME));
        plogger.info("Setting starting Dir to: " + EXTRACTOR_HOME);
        plogger.info("Starting Extractor tool...");
        shell.run();
        String processOutput = shell.getProcessOutputAsString();
        String processError = shell.getProcessErrorAsString();
        plogger.info("Process Output: " + processOutput);
        if( ! "".equals(processError ) ) {
            plogger.error("Process Error: " + processError);
            System.err.println("Process Errror: "+processError);
        }
        // StringWriter sWriter = new StringWriter();

        byte[] binary_out = null;
        plogger.info("Creating byte[] to return...");

        binary_out = FileUtils.readFileIntoByteArray(new File(outputFilePath));

        byte[] cleanedXCDL = null;

        if (normDataDisabled) {
            cleanedXCDL = removeNormData(binary_out);
            binary_out = cleanedXCDL;
        }

        return binary_out;
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
