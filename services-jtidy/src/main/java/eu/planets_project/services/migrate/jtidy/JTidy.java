package eu.planets_project.services.migrate.jtidy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import org.apache.commons.io.IOUtils;
import org.w3c.tidy.Tidy;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * JTidy migration service.
 * @author Peter Melms
 */
@WebService(name = JTidy.NAME, serviceName = Migrate.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.migrate.Migrate")
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public class JTidy implements Migrate, Serializable {

    /**
     * the service name.
     */
    public static final String NAME = "JTidy";

    private static final long serialVersionUID = 4563930985924810312L;
    private static final Logger log = Logger.getLogger(JTidy.class.getName());
    private static final String CONFIG_FILE_PARAM = "configFile";
    private static final String LOG_FILE_NAME = "jTidy_log.txt";
    private static final String DEFAULT_CONFIGURATION = "config_file.properties";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                JTidy.NAME, Migrate.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd
                .description("This is a wrapper for the JTidy Java API.\n"
                        + "JTidy converts older or not compliant HTML files to XHTML compliant files.\n"
                        + "Could be used as HTML pretty printer as well.\n"
                        + "JTidy can be configured by passing a config file. In this case"
                        + "the config file has be passed using the Parameters, i.e. reading the config.txt into "
                        + "a String.");

        sd.classname(this.getClass().getCanonicalName());
        sd.version("0.1");

        List<URI> inputFormatsPUID = new ArrayList<URI>();
        List<URI> outputFormatsPUID = new ArrayList<URI>();
        FormatRegistry formatRegistry = FormatRegistryFactory
                .getFormatRegistry();

        inputFormatsPUID.add(formatRegistry.createPronomUri("fmt/96")); // HTML
        // versions
        // older than
        // 2.0
        inputFormatsPUID.add(formatRegistry.createPronomUri("fmt/97")); // HTML 2.0
        inputFormatsPUID.add(formatRegistry.createPronomUri("fmt/98")); // HTML 3.2
        inputFormatsPUID.add(formatRegistry.createPronomUri("fmt/99")); // HTML 4.0
        inputFormatsPUID.add(formatRegistry.createPronomUri("fmt/100")); // HTML 4.01

        outputFormatsPUID.add(formatRegistry.createPronomUri("fmt/102")); // XHTML 1.0

        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter configFile = new Parameter.Builder("configFile",
                "The content of a config file read into a String.").description(
                "Allows configurating JTidy by passing it a config file consisting of name=value pairs.\n"
                + "like 'show-warnings=yes'\n"
                + "\nFor further instructions on configurating JTidy please see the "
                + "JTidy (http://jtidy.sourceforge.net/)and "
                + "Tidy (http://www.w3.org/People/Raggett/tidy/) website!").build();
        parameterList.add(configFile);

        MigrationPath[] migrationPathwaysPUID = createMigrationPathwayMatrix(
                inputFormatsPUID, outputFormatsPUID, parameterList);

        sd.paths(migrationPathwaysPUID);
        sd.tool(Tool.create(null, "JTidy", "rc820", "JTidy converts older or not compliant HTML files to XHTML compliant files.\n"
                + "Could be used as HTML pretty printer as well.\n"
                + "JTidy can be configured by passing a config file. In this case"
                + "the config file has be passed using the Parameters, i.e. reading the config.txt into "
                + "a String.", "http://jtidy.sourceforge.net"));
        sd.logo(URI.create("http://jtidy.sf.net/images/logo.png"));

        sd.parameters(parameterList);

        return sd.build();
    }

    /**
     * {@inheritDoc}
     */
    public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, List<Parameter> parameters) {

        DigitalObject result = null;
        Properties userProps = null;
        InputStream stream = null;
        File outHTML = null;
        File inHTML = DigitalObjectUtils.toFile(digitalObject);
        File logFile = new File(inHTML.getParentFile(), LOG_FILE_NAME);
        Tidy tidy = new Tidy();
        try {
            outHTML = File.createTempFile("out", null);

            if (parameters != null) {
                if (parameters.size() > 0) {
                    log.info("Got additional parameters!");
                    for (Parameter parameter : parameters) {
                        String name = parameter.getName();
                        if (name.equalsIgnoreCase(CONFIG_FILE_PARAM)) {
                            userProps = new Properties();
                            try {
                                userProps.load(new ByteArrayInputStream(parameter.getValue()
                                        .getBytes()));
                                log.info("Using this parameters for JTidy: \n"
                                        + "-----------------------------------\n"
                                        + parameter.getValue());
                            } catch (IOException e1) {
                                log.severe("Could not write config file!!!");
                                e1.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }

            BufferedInputStream inHTMLStream;

            inHTMLStream = new BufferedInputStream(new FileInputStream(inHTML));
            BufferedOutputStream outHTMLStream = new BufferedOutputStream(
                    new FileOutputStream(outHTML));

            if (userProps != null) {
                tidy.setConfigurationFromProps(userProps);
            }

            // use default config
            else {
                Properties defaultProps = new Properties();
                stream = this.getClass().getResourceAsStream(
                        DEFAULT_CONFIGURATION);
                defaultProps.load(stream);
                tidy.setConfigurationFromProps(defaultProps);
                log
                        .info("no additional configuration file passed, using DEFAULT config instead!\n");
                String defConfig = IOUtils.toString(this.getClass()
                                .getResourceAsStream(DEFAULT_CONFIGURATION));
                log.info(defConfig);
            }

            tidy.setShowWarnings(true);
            tidy
                    .setErrout(new PrintWriter(new FileOutputStream(logFile),
                            true));
            tidy.setWriteback(true);
            tidy.setOnlyErrors(false);
            tidy.setInputStreamName(inHTML.getAbsolutePath());
            tidy.setTidyMark(true);
            tidy.parse(inHTMLStream, outHTMLStream);
            inHTMLStream.close();
            outHTMLStream.flush();
            outHTMLStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            IOUtils.closeQuietly(stream);
        }

        if (outHTML != null && outHTML.canRead()) {
            result = new DigitalObject.Builder(Content
                        .byValue(outHTML)).title(outHTML.getName()).format(
                        outputFormat)
                        .permanentUri(
                                URI.create(PlanetsServices.SERVICES_NS
                                        + "/pserv-pa-jtidy")).build();

            ServiceReport sr;

            String message = createStringFromLogFile(logFile, true);
            if (tidy.getParseErrors() > 0) {
                sr = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, message);
            } else {
                sr = new ServiceReport(Type.INFO, Status.SUCCESS, message);
            }

            MigrateResult mr = new MigrateResult(result, sr);

            return mr;
        } else {
            return this.returnWithErrorMessage(
                    "There seems to be a problem: No Result file created!",
                    null);
        }

    }

    /**
     * @param message an optional message on what happened to the service
     * @param e the Exception e which causes the problem
     * @return CharacteriseResult containing a Error-Report
     */
    private MigrateResult returnWithErrorMessage(final String message,
            final Exception e) {
        if (e == null) {
            return new MigrateResult(null, ServiceUtils
                    .createErrorReport(message));
        } else {
            return new MigrateResult(null, ServiceUtils
                    .createExceptionErrorReport(message, e));
        }
    }

    private String createStringFromLogFile(File errorLogFile,
            boolean enableDebugLog) {
        File debugLog = null;
        PrintWriter debugLogger = null;
        if (enableDebugLog) {
            FileOutputStream out = null;
            try {
                debugLog = File.createTempFile("jTidyDebugLog", ".txt");
                out = new FileOutputStream(debugLog, true);
                debugLogger = new PrintWriter(out, true);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(debugLogger);
            }
        }
        StringBuffer buf = new StringBuffer();
        if (errorLogFile.canRead()) {
            try {
                BufferedReader errorLogIn = new BufferedReader(new FileReader(
                        errorLogFile));
                String line = null;

                buf.append(ServiceUtils.getSystemDateAndTimeFormatted() + ": ");
                buf
                        .append("JTidy output:\n----------------------------------------------");

                while ((line = errorLogIn.readLine()) != null) {
                    buf.append(line);
                    buf.append("\n");
                }
                errorLogIn.close();

            } catch (FileNotFoundException e) {
                log.severe("Could not find errorLog file: "
                        + errorLogFile.getAbsolutePath());
                e.printStackTrace();
            } catch (IOException e) {
                log.severe("Could not read errorLog file: "
                        + errorLogFile.getAbsolutePath());
                e.printStackTrace();
            }

            buf.deleteCharAt(buf.lastIndexOf("\n"));
            buf.append("----------------------------------------------");

            if (enableDebugLog) {
                debugLogger.write(buf.toString());
                debugLogger.println();
                debugLogger.println();
                debugLogger.flush();
                debugLogger.close();
            }
            return buf.toString();
        } else {
            log.severe("No error log file found!!!");
            return "";
        }
    }

    private MigrationPath[] createMigrationPathwayMatrix(
            List<URI> inputFormats, List<URI> outputFormats,
            List<Parameter> parameters) {
        List<MigrationPath> paths = new ArrayList<MigrationPath>();

        for (Iterator<URI> iterator = inputFormats.iterator(); iterator
                .hasNext();) {
            URI input = iterator.next();

            for (Iterator<URI> iterator2 = outputFormats.iterator(); iterator2
                    .hasNext();) {
                URI output = iterator2.next();

                MigrationPath path = new MigrationPath(input, output,
                        parameters);
                // Debug...
                // System.out.println(path.getInputFormat() + " --> " +
                // path.getOutputFormat());
                paths.add(path);
            }
        }
        return paths.toArray(new MigrationPath[] {});
    }

}
