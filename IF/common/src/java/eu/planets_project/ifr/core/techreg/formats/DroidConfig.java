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
    public static final String LOCAL = "PC/droid/src/resources/";
    /***/
    public static final String SIG = "DROID_SignatureFile_Planets.xml";
    /***/
    public static final String CONF = "/server/default/data/";
    /***/
    public static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";

    /**
     * @return If running in JBoss, returns the deployment directory, else (like
     *         when running a unit test) returns the project directory to
     *         retrieve the concepts file
     */
    public static String configFolder() {
        String deployedJBossHome = System.getProperty(JBOSS_HOME_DIR_KEY);
        String sigFileFolder = (deployedJBossHome != null ? deployedJBossHome
                + CONF : LOCAL);
        String sigFileLocation = sigFileFolder + SIG;
        log.info("Opening signature file: "+sigFileLocation);
        return sigFileLocation;
    }

}
