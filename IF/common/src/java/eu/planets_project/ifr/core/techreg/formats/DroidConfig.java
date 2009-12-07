/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.util.logging.Logger;

/**
 * Droid configuration settings.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
class DroidConfig {
    private static Logger log = Logger.getLogger(DroidConfig.class.getName());
    
    /***/
    static final String LOCAL = "PC/droid/src/resources/";
    /***/
    static final String SIG = "DROID_SignatureFile_Planets.xml";
    /***/
    static final String CONF = "/server/default/data/";
    /***/
    static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";

    /**
     * @return The DROID signature file location. If running in JBoss, from the deployment directory, else (like when
     *         running a unit test) from the project directory
     */
    static String signatureFileLocation() {
        String deployedJBossHome = System.getProperty(JBOSS_HOME_DIR_KEY);
        String sigFileFolder = (deployedJBossHome != null ? deployedJBossHome + CONF : LOCAL);
        String sigFileLocation = sigFileFolder + SIG;
        log.info("Opening signature file: " + sigFileLocation);
        return sigFileLocation;
    }

}
