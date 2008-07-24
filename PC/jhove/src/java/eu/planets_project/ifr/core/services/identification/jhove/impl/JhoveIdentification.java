package eu.planets_project.ifr.core.services.identification.jhove.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Scanner;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.handler.TextHandler;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Types;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;

/**
 * JHOVE identification service.
 * 
 * @author Fabian Steeg
 */
@WebService(name = JhoveIdentification.NAME, serviceName = IdentifyOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(IdentifyOneBinary.class)
@Remote(IdentifyOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class JhoveIdentification implements IdentifyOneBinary,
        Serializable {
    /***/
    public static final String NAME = "JhoveIdentification";
    /***/
    public static final QName QNAME = new QName(PlanetsServices.NS,
            IdentifyOneBinary.NAME);
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

    /**
     * Simple enumeration of MIME types, PRONOM IDs and sample file locations
     * (for testing, see the tests directory) for types recognized by JHOVE
     * (this is a simplified version of what JHOVE does, which does not
     * distinguish between different formats of the same MIME type, but JHOVE
     * can - this will need to be added in the future).
     */
    public enum FileType {
        /***/
        AIFF("audio/x-aiff", "info:pronom/x-fmt/135", "aiff/wind.aiff"),
        /***/
        ASCII("text/plain", "info:pronom/x-fmt/111", "ascii/control.txt"),
        /***/
        GIF("image/gif", "info:pronom/fmt/3", "gif/AA_Banner.gif"),
        /***/
        HTML("text/html", "info:pronom/fmt/96", "html/sample.html"),
        /***/
        JPEG("image/jpeg", "info:pronom/fmt/42", "jpeg/AA_Banner.jpg"),
        /***/
        PDF("application/pdf", "info:pronom/fmt/14", "pdf/AA_Banner-single.pdf"),
        /***/
        TIFF("image/tiff", "info:pronom/fmt/10", "tiff/AA_Banner.tif"),
        /***/
        WAVE("audio/x-wave", "info:pronom/fmt/6", "wav/comet.wav"),
        /***/
        XML("text/xml", "info:pronom/fmt/101", "xml/sample.xml");
        /***/
        private String pronom;
        /***/
        private String mime;
        /***/
        private String sample;

        /**
         * @param mime The mime type
         * @param pronom A pronom URI
         * @param sample A sample file
         */
        private FileType(final String mime, final String pronom,
                final String sample) {
            this.mime = mime;
            this.pronom = pronom;
            this.sample = sample;
        }

        /**
         * @return Returns the pronom URI
         */
        public String getPronom() {
            return pronom;
        }

        /**
         * @return Returns the sample file location
         */
        public String getSample() {
            return RESOURCES + sample;
        }
    }

    /**
     * The actual JHOVE identification method.
     * 
     * @param binary The file to be identified, as a byte array
     * @return Returns a Types object containing the result of the JHOVE
     *         identification
     */
    @WebMethod(operationName = IdentifyOneBinary.NAME, action = PlanetsServices.NS
            + "/" + IdentifyOneBinary.NAME)
    @WebResult(name = IdentifyOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + IdentifyOneBinary.NAME, partName = IdentifyOneBinary.NAME
            + "Result")
    public Types identifyOneBinary(final byte[] binary) {
        try {
            /* We store the bytes in a temporary file: */
            File temporary = ByteArrayHelper.write(binary);
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
     * Method for simpler SOAP-based testing.
     * 
     * @param fileName The name of the local (on the server) file to identify
     * @return Returns a Types object, resulting from calling the actual
     *         identification method
     */
    @WebMethod
    public Types identifyOneFile(final String fileName) {
        byte[] bytes = ByteArrayHelper.read(new File(fileName));
        return identifyOneBinary(bytes);
    }

    /**
     * Retrieves the results of a JhoveBase (by extracting from the resulting
     * text file, I have no idea how to get it straight from the JhoveBase,
     * there is no API for this).
     * 
     * @param base The JhoveBase to get the results from
     * @return Returns a Types object with the result for the JhoveBase
     */
    private Types result(final JhoveBase base) {
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
        while (s.hasNextLine()) {
            String line = s.nextLine().trim().toLowerCase();
            if (line.startsWith("mimetype:")) {
                mime = value(line);
            } else if (line.startsWith("status:")) {
                status = value(line);
            }
        }
        if (mime == null) {
            throw new IllegalStateException("Identification failed: " + status
                    + " (details in " + base.getOuputFile() + ")");
        }
        Types t = new Types();
        t.status = status;
        t.types = new URI[] { uri(mime) };
        return t;
    }

    /**
     * @param mime The MIME type as retrieved from the JHOVE result file
     * @return Returns a URI containing a PRONOM ID for the MIME type, or null
     * @throws URISyntaxException
     */
    private URI uri(final String mime) {
        for (FileType type : FileType.values()) {
            if (mime.trim().toLowerCase().equals(type.mime)) {
                try {
                    return new URI(type.pronom);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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
