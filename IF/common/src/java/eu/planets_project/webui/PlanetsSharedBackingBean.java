/**
 * 
 */
package eu.planets_project.webui;

import java.io.File;
import java.net.MalformedURLException;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PlanetsSharedBackingBean {
    
    /**
     * A helper bean to look-up paths to shared resources.
     * 
     * @return String containing the absolute file URI.
     */
    public String getSharedFileBasePath() {
        File homedir = new File(System.getProperty("jboss.server.home.dir"));
        File shareddir = new File( homedir, "deploy/jboss-web.deployer/ROOT.war");
        try {
            return shareddir.toURL().toString();
        } catch (MalformedURLException e) {
            return "";
        }
    }

}
