package eu.planets_project.ifr.core.services.identification.jhove.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.handler.TextHandler;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.FileUtils;

/**
 * JHOVE identification service.
 * @author Fabian Steeg
 */
@WebService(name = JhoveIdentification.NAME, serviceName = Identify.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.identify.Identify")
@Local(Identify.class)
@Remote(Identify.class)
@Stateless()
public final class JhoveIdentification implements Identify, Serializable {
    private static Log log = LogFactory.getLog(JhoveIdentification.class);
    /***/
    static final String NAME = "JhoveIdentification";
    /***/
    private static final String ENCODING = "utf-8";
    /***/
    private static final String RESOURCES = "PC/jhove/src/resources/";
    /***/
    private static final String CONFIG_FILE = "jhove.conf";
    /***/
    private static final long serialVersionUID = 1127650680714441971L;
    /***/
    private static final String PLANETS = "Planets";
    /***/
    private static final String CONF = "/server/default/conf/";
    /***/
    private static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";
    /***/
    private static final String OUTPUT = "planets-jhove-output";
    private String status;

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject,
     *      java.util.List)
     */
    public IdentifyResult identify(final DigitalObject digitalObject,
            final List<Parameter> parameters) {
        File file = FileUtils.writeInputStreamToTmpFile(digitalObject
                .getContent().read(), "jhove-temp", "bin");
        List<URI> types = identifyOneBinary(file);
        log.info("JHOVE Identification, got types: " + types);
        ServiceReport report;
        if (types == null || types.size() == 0 || types.contains(null)) {
            report = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                    "Could not identify " + file.getAbsolutePath()
                            + " (is the required Jhove module installed?)");
        } else {
            report = new ServiceReport(Type.INFO, Status.SUCCESS, status);
        }
        return new IdentifyResult(types, null, report);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.identify.Identify#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                "JHOVE Identification Service", Identify.class
                        .getCanonicalName());
        sd.classname(this.getClass().getCanonicalName());
        sd.description("Identification service using JHOVE (1.1).");
        sd.author("Fabian Steeg");
        sd.tool(Tool.create(null, "JHOVE", "1.1", null,
                "http://hul.harvard.edu/jhove/"));
        sd.furtherInfo(URI.create("http://hul.harvard.edu/jhove/"));
        sd.inputFormats(inputFormats());
        sd.serviceProvider("The Planets Consortium");
        return sd.build();
    }

    /**
     * Simple enumeration of MIME types recognized by JHOVE.
     */
    private static enum SupportedFileType {
        AIFF("audio/x-aiff"),
        ASCII("text/plain"),
        GIF("image/gif"),
        HTML("text/html"),
        JPEG("image/jpeg"),
        PDF("application/pdf"),
        TIFF("image/tiff"),
        WAVE("audio/x-wav"),
        XML("text/xml");
        /***/
        private String mime;

        /**
         * @param mime The mime type
         */
        private SupportedFileType(final String mime) {
            this.mime = mime;
        }

        
    }

    /**
     * @param format The format to check for support
     * @return True, if the given format is supported
     */
    public static boolean supported(final URI format) {
        for (URI uri : JhoveIdentification.inputFormats()) {
            FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
            if (registry.getFormatUriAliases(uri).contains(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return An array of Pronom IDs supported as input formats by Jhove
     */
    public static URI[] inputFormats() {
        List<URI> result = new ArrayList<URI>();
        for (SupportedFileType type : SupportedFileType.values()) {
            // String[] split = type.getSample().split("\\.");
            FormatRegistry formatRegistry = FormatRegistryFactory
                    .getFormatRegistry();
            Set<URI> urisForMimeType = formatRegistry
                    .getUrisForMimeType(type.mime);
            if (urisForMimeType != null && urisForMimeType.size() > 0) {
                result.addAll(urisForMimeType);
            }
        }
        return result.toArray(new URI[] {});
    }

    /**
     * The actual JHOVE identification method.
     * @param temporary The file to be identified
     * @return Returns a Types object containing the result of the JHOVE
     *         identification
     */
    private List<URI> identifyOneBinary(final File temporary) {
        try {
            /* And use the JHOVE API to identify it: */
            JhoveBase base = new JhoveBase();
            base.setEncoding(ENCODING);
            Calendar calendar = Calendar.getInstance();
            int[] date = new int[] { calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH) };
            App app = new App(PLANETS, PLANETS, date, PLANETS, PLANETS);
            base.init(config() + CONFIG_FILE, null);
            /* For JHOVE's output, we create another temporary file: */
            File output = File.createTempFile(OUTPUT, null);
            output.deleteOnExit();
            base.dispatch(app, null, null, new TextHandler(), output
                    .getAbsolutePath(), new String[] { temporary
                    .getAbsolutePath() });
            /* And finally we create our result object with the PRONOM ID: */
            return result(base);
        } catch (JhoveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the results of a JhoveBase (by extracting from the resulting
     * text file, I have no idea how to get it straight from the JhoveBase,
     * there is no API for this).
     * @param base The JhoveBase to get the results from
     * @return Returns a Types object with the result for the JhoveBase
     */
    private List<URI> result(final JhoveBase base) {
        String file = base.getOuputFile();
        File f = new File(file);
        Scanner s = null;
        try {
            s = new Scanner(f);
        } catch (FileNotFoundException e) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "JhoveBase in use has no proper output file;");
            exception.initCause(e);
            throw exception;
        }
        String mime = null;
        String status = null;
        StringBuilder builder = new StringBuilder();
        while (s.hasNextLine()) {
            String line = s.nextLine().trim().toLowerCase();
            if (line.startsWith("mimetype:")) {
                mime = value(line);
            } else if (line.startsWith("status:")) {
                status = value(line);
            }
            builder.append(line);
        }
        if (mime == null) {
            String output = builder.toString();
            throw new IllegalStateException(
                    "Identification failed with status: " + status
                            + " (no mime type in "
                            + (output.length() == 0 ? "empty output" : output)
                            + ")");
        }
        /* Jhove output uses 'x-wave' while droid uses 'x-wav': */
        mime = mime.replace("x-wave", "x-wav");
        log.info("Got mime type: " + mime);
        log.info("Got status: " + status);
        this.status = status;
        List<URI> types = new ArrayList<URI>();
        FormatRegistry registry = FormatRegistryFactory.getFormatRegistry();
        Set<URI> uris = registry.getUrisForMimeType(mime);
        types.addAll(uris);
        return types;
    }

    /**
     * @param line The line to retrieve the value from
     * @return Returns the value of the line (after the colon, before a
     *         semicolon)
     */
    private String value(final String line) {
        return line.split(":")[1].trim().split(";")[0].trim();
    }

    /**
     * @return If running in JBoss, returns the deployment directory, else (like
     *         when running a unit test) returns the project directory to
     *         retrieve the concepts file
     */
    private static String config() {
        String deployedJBossHome = System.getProperty(JBOSS_HOME_DIR_KEY);
        String folder = (deployedJBossHome != null ? deployedJBossHome + CONF
                : RESOURCES);
        return folder;
    }
}
