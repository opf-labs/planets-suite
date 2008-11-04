/**
 * 
 */
package eu.planets_project.webui;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Utility backing bean for Planets web components.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PlanetsSharedBackingBean {
    
    /**
     * A helper method to look-up the file path the to shared resources, e.g. facelets templates.
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
    

    /**
     * A helper to find the context that the shared resources are available from, e.g. css or images. 
     * 
     * @return String containing the shared resoure web context.
     */
    public String getSharedFileContext() {
        return "/";
    }

}
