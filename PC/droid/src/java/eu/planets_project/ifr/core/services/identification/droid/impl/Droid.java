package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Types;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;

/**
 * Droid identification service.
 * 
 * @author Fabian Steeg, Carl Wilson
 * 
 */
@WebService(name = Droid.NAME, serviceName = IdentifyOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(IdentifyOneBinary.class)
@Remote(IdentifyOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public final class Droid implements IdentifyOneBinary, Serializable {
    /**
     * The e number of ms. we want to wait in one waiting step for the
     * identification to finish
     */
    private static final int WAIT_INTERVAL = 100;
    /**
     * The maximum times we want to sleep for WAIT_INTERVAL ms. to wait for the
     * identification to finish
     */
    private static final int WAIT_MAX = 300;
    /***/
    private static final long serialVersionUID = -7116493742376868770L;
    /***/
    public static final String NAME = "Droid";
    /***/
    public static final QName QNAME = new QName(PlanetsServices.NS,
            IdentifyOneBinary.NAME);
    /***/
    public static final String LOCAL = "PC/droid/src/resources/";
    /***/
    private static final String SIG = "DROID_SignatureFile_Planets.xml";
    /***/
    private static final String CONF = "/server/default/conf/";
    /***/
    private static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";

    /**
     * Identify a file represented as a byte array using Droid.
     * 
     * @param bytes The file to identify using Droid (as a byte array)
     * @return Returns the Pronom IDs found for the file as URIs in a Types
     *         object
     */
    @WebMethod(operationName = IdentifyOneBinary.NAME, action = PlanetsServices.NS
            + "/" + IdentifyOneBinary.NAME)
    @WebResult(name = IdentifyOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + IdentifyOneBinary.NAME, partName = IdentifyOneBinary.NAME
            + "Result")
    public Types identifyOneBinary(final byte[] bytes) {
        // Determine the config directory:
        String sigFileLocation = configFolder();
        // Store the bytes into a temorary folder;
        File tempFile = ByteArrayHelper.write(bytes);
        // Here we start using the Droid API:
        AnalysisController controller = new AnalysisController();
        try {
            controller.readSigFile(sigFileLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.addFile(tempFile.getAbsolutePath());
        controller.setVerbose(false);
        controller.runFileFormatAnalysis();
        Iterator<IdentificationFile> iterator = controller.getFileCollection()
                .getIterator();
        Types retVal = null;
        // We identify one file only:
        if (iterator.hasNext()) {
            IdentificationFile file = iterator.next();
            waitFor(file);
            URI[] uris = new URI[file.getNumHits()];
            // Retrieve the results:
            try {
                for (int hitCounter = 0; hitCounter < file.getNumHits(); hitCounter++) {
                    FileFormatHit formatHit = file.getHit(hitCounter);
                    uris[hitCounter] = new URI("info:pronom/"
                            + formatHit.getFileFormatPUID());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            retVal = new Types(uris, file.getClassificationText());
        }
        return retVal;
    }

    /**
     * Identify a file represented as a file name using Droid. This is a utility
     * method to enable SOAP-based testing, it converts the specified file into
     * a byte array and calls the actual identify method with that
     * 
     * @param fileName The file name of the file to identify
     * @return Returns a Types object containing an array with the Pronom IDs as
     *         URIs for the specified file
     */
    @WebMethod()
    public Types identifyOneFile(final String fileName) {
        byte[] array = ByteArrayHelper.read(new File(fileName));
        return identifyOneBinary(array);
    }

    /**
     * @param file The identification file to wait for
     */
    private void waitFor(final IdentificationFile file) {
        int slept = 0;
        /*
         * Droid runs the identification in a Thread, so we have to wait until
         * it finishes...
         */
        while (file.getClassification() == AnalysisController.FILE_CLASSIFICATION_NOTCLASSIFIED
                /* ...but we won't wait forever */
                && slept < WAIT_MAX) {
            try {
                Thread.sleep(WAIT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            slept++;
        }
    }

    /**
     * @return If running in JBoss, returns the deployment directory, else (like
     *         when running a unit test) returns the project directory to
     *         retrieve the concepts file
     */
    private static String configFolder() {
        String deployedJBossHome = System.getProperty(JBOSS_HOME_DIR_KEY);
        String sigFileFolder = (deployedJBossHome != null ? deployedJBossHome
                + CONF : LOCAL);
        String sigFileLocation = sigFileFolder + SIG;
        return sigFileLocation;
    }

}
