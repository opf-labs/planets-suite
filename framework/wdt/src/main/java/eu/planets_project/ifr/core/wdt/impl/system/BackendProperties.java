package eu.planets_project.ifr.core.wdt.impl.system;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wrap the BackendProperties for reuse over the code.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class BackendProperties {
    private Log log = LogFactory.getLog(BackendProperties.class);
    // The properties:
    Properties properties = new Properties();

    // JBOSS and files:
    public static final String JBOSS_FILEDIRBASE = "Jboss.FiledirBase";
    public static final String JBOSS_FILEINBASE = "JBoss.FileInDir";
    public static final String JBOSS_FILEOUTBASE = "JBoss.FileOutDir";
    public static final String JBOSS_LOCALDATADIR = "JBoss.LocalDataDir";
    public static final String JBOSS_ALTLOCALDATADIR = "JBoss.AltLocalDataDir";
    
    // Testbed properties
    public static final String TB_VERSION = "testbed.version";
   

    // Experiment properties.
    // The max number of input files before admin approval is required to run the experiment.
    public static final String EXP_ADMIN_NOINPUTS = "experiment.adminThreshold.noInputs";

    //Note: sFileDirBase = ifr_server/bin/../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
    String sFileDirBase = null;

    // Constructor reads the properties.
    public BackendProperties() {
        try {
            java.io.InputStream ResourceFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BackendResources.properties");
            properties.load(ResourceFile);
            ResourceFile.close();
        } catch ( Exception e ) {
            log.error("read BackendResources.properties failed!"+e.toString());
        }
        // This defines the location of the directory:
        this.sFileDirBase = System.getProperty("jboss.server.home.dir")+"/"+properties.getProperty("Jboss.FiledirBase");
    }

    /**
     * @return
     */
    public static String getTBFileDir() {
        BackendProperties bp = new BackendProperties();
        return bp.sFileDirBase;
    }

    /**
     * @param string
     * @return
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @return the JBOSS_FILEDIRBASE
     */
    public String getJbossFiledirBase() {
        return properties.getProperty(JBOSS_FILEDIRBASE);
    }

    /**
     * @return the JBOSS_FILEINBASE
     */
    public String getJbossFileInBase() {
        return properties.getProperty(JBOSS_FILEINBASE);
    }

    /**
     * @return the JBOSS_FILEOUTBASE
     */
    public String getJbossFileOutBase() {
        return properties.getProperty(JBOSS_FILEOUTBASE);
    }

    /**
     * @return the JBOSS_LOCALDATADIR
     */
    public String getJbossLocalDataDir() {
        return properties.getProperty(JBOSS_LOCALDATADIR);
    }

    /**
     * @return the JBOSS_ALTLOCALDATADIR
     */
    public String getJbossAltLocalDataDir() {
        return properties.getProperty(JBOSS_ALTLOCALDATADIR);
    }

    /**
     * @return the EXP_ADMIN_NOINPUTS
     */
    public  int getExpAdminNoInputs() {
        return Integer.valueOf( properties.getProperty(EXP_ADMIN_NOINPUTS) );
    }

    /**
     * 
     * @return
     */
    public String getTestbedVersion() {
        return properties.getProperty(TB_VERSION);
    }
    
}
