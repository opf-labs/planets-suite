package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.util.Base64;

import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.compare.BasicCompareTwoXCDLStrings;

/**
 * PP comparator service.
 * 
 * @author Fabian Steeg
 * 
 */
@WebService(name = Comparator.NAME, serviceName = BasicCompareTwoXCDLStrings.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicCompareTwoXCDLStrings.class)
@Remote(BasicCompareTwoXCDLStrings.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class Comparator implements BasicCompareTwoXCDLStrings,
        Serializable {
    /***/
    private static final long serialVersionUID = 1238447797051780267L;
    /***/
    private static final PlanetsLogger LOG = PlanetsLogger
            .getLogger(Comparator.class);
    /***/
    static final String COMPARATOR_HOME = System.getenv("COMPARATOR_HOME")
            + File.separator;
    /***/
    static final String NAME = "Comparator";
    /** The file names of the result and log files. */
    private static final String LOG_TXT = "log.txt";
    /***/
    private static final String RESULT_ENDING = ".cpr";
    /** The comparator executable, has to be on the path on the server. */
    private static final String COMPARATOR = "comparator";
    /***/
    private static final String BASE64 = "Base64";

    /**
     * @param xcdl1 The first XCDL
     * @param xcdl2 The second XCDL
     * @return Returns the result of comparing the first and the second XCDL
     */
    @WebMethod(operationName = BasicCompareTwoXCDLStrings.NAME, action = PlanetsServices.NS
            + "/" + BasicCompareTwoXCDLStrings.NAME)
    @WebResult(name = BasicCompareTwoXCDLStrings.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareTwoXCDLStrings.NAME, partName = BasicCompareTwoXCDLStrings.NAME
            + "Result")
    public String basicCompareTwoXCDLStrings(final String xcdl1,
            final String xcdl2) {
        /* Create temp files for the XCDLs to be compared: */
        File tempFile1 = tempFile("XCDL1");
        LOG.debug("Temp file 1: " + tempFile1);
        File tempFile2 = tempFile("XCDL2");
        LOG.debug("Temp file 2: " + tempFile2);
        /* For storing the result, we use the temp folder: */
        String tempFolder = tempFile1.getParent();
        /* If we can't read or write to the temp folder, cancel: */
        File f = new File(tempFolder);
        if (!f.canRead() || !f.canWrite()) {
            throw new IllegalStateException("Can't read from or write to: "
                    + f.getAbsolutePath());
        }
        /* Store the given content in the temp files: */
        save(tempFile1.getAbsolutePath(), xcdl1);
        save(tempFile2.getAbsolutePath(), xcdl2);
        /* Compare the temp files: */
        List<String> commands = Arrays.asList(COMPARATOR_HOME + COMPARATOR,
                tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath(),
                tempFolder);
        ProcessRunner pr = new ProcessRunner(commands);
        /* We change into the comparator home directory: */
        File home = new File(COMPARATOR_HOME);
        if (!home.exists()) {
            throw new IllegalStateException("COMPARATOR_HOME does not exist: "
                    + COMPARATOR_HOME);
        }
        pr.setStartingDir(home);
        LOG.debug("Executing: " + commands);
        /* Before calling the command, check if the files exist: */
        if (!new File(tempFile1.getAbsolutePath()).exists()
                || !new File(tempFile2.getAbsolutePath()).exists()) {
            throw new IllegalStateException("Temp files not accessible;");
        }
        pr.run();
        /* Print some debugging info on the call: */
        LOG.debug("Comparator call output: " + pr.getProcessOutputAsString());
        LOG.debug("Comparator call error: " + pr.getProcessErrorAsString());
        /* Read the resulting files: */
        String result = read(tempFolder + File.separator
                + tempFile1.getName().split("\\.")[0] + "-"
                + tempFile2.getName().split("\\.")[0] + RESULT_ENDING);
        String logged = read(COMPARATOR_HOME + LOG_TXT);
        /* Print some debugging info on the results: */
        LOG.info("Comparator result: " + result);
        LOG.debug("Comparator log: " + logged);
        return result;
    }

    /**
     * @param xcdl1Base64 The first XCDL, Base64 encoded
     * @param xcdl2Base64 The second XCDL, Base64 encoded
     * @return Returns the result of comparing the first and the second XCDL,
     *         Base64 encoded
     */
    @WebMethod(operationName = BasicCompareTwoXCDLStrings.NAME + BASE64, action = PlanetsServices.NS
            + "/" + BasicCompareTwoXCDLStrings.NAME + BASE64)
    @WebResult(name = BasicCompareTwoXCDLStrings.NAME + BASE64 + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareTwoXCDLStrings.NAME + BASE64, partName = BasicCompareTwoXCDLStrings.NAME
            + BASE64 + "Result")
    public String basicCompareTwoXCDLBase64Strings(final String xcdl1Base64,
            final String xcdl2Base64) {
        String xcdl1 = new String(Base64.decode(xcdl1Base64));
        String xcdl2 = new String(Base64.decode(xcdl2Base64));
        String result = basicCompareTwoXCDLStrings(xcdl1, xcdl2);
        String resultBase64 = Base64.encodeBytes(result.getBytes());
        return resultBase64;
    }

    /**
     * Helper/mock method for testing, using file locations instead of the
     * actual data. Calls the actual comparison method above.
     * 
     * @param xcdl1Name The location of the first XCDL
     * @param xcdl2Name The location of the second XCDL
     * @return Returns the result of comparing the first and the second XCDL
     */
    @WebMethod
    public String basicCompareTwoXCDLFiles(final String xcdl1Name,
            final String xcdl2Name) {
        String content1 = read(xcdl1Name);
        String content2 = read(xcdl2Name);
        return basicCompareTwoXCDLStrings(content1, content2);
    }

    /**
     * @param location The location of the text file to read
     * @return Return the content of the file at the specified location,
     *         replacing line breaks with blanks
     */
    static String read(final String location) {
        StringBuilder builder = new StringBuilder();
        Scanner s;
        try {
            s = new Scanner(new File(location));
            while (s.hasNextLine()) {
                builder.append(s.nextLine()).append(" ");
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileName The file name to write the specified content to
     * @param content The content to write to a file with the specified name
     */
    private void save(final String fileName, final String content) {
        try {
            FileWriter out = new FileWriter(fileName);
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name The name to use when generating the temp file
     * @return Returns a temp file created using File.createTempFile
     */
    private static File tempFile(final String name) {
        File input;
        try {
            input = File.createTempFile(name, null);
            input.deleteOnExit();
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
