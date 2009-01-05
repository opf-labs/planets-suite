/**
 * 
 */
package eu.planets_project.webui;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.facelets.impl.DefaultResourceResolver;

/**
 * 
 * Allows templates to be shared, as using this resolver will patch the resources from this package 
 * to a specific local URI /planets-webui-shared/*
 * 
 * Add this to your web.xml to use the Planets resource resolver from Facelets.
 * 
 * 
  <context-param>
    <param-name>facelets.RESOURCE_RESOLVER</param-name>
    <param-value>
        eu.planets_project.webui.PlanetsFaceletsResourceResolver
    </param-value>
  </context-param>
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PlanetsFaceletsResourceResolver extends DefaultResourceResolver {
    private final Log log = LogFactory.getLog(getClass().getName());

    /**
     * 
     * @return path prefix for shared web ui bits
     */
    public String getViewIdPrefix() {
        return "/planets-webui-shared/";
    }
    
    /**
     * @return Where on the classpath to find the results.
     */
    public String getClassPrefix() {
        return "/eu/planets_project/webui/";
    }

    /**
     * @param path 
     * @return the resolved URL for passed path
     */
    public URL resolveUrl(String path) {
        log.debug("resolving: " + path);
        if (path.startsWith(getViewIdPrefix())) {
            log.info("viewId '" + path + "' begins with '" + getViewIdPrefix()
                    + "', so using file from an internal jar");
            String file = path.substring(getViewIdPrefix().length());
            URL xhtml = this.getClass().getResource(getClassPrefix() + file);
            log.debug("xhtml url:  " + xhtml.toString());
            return xhtml;
        }
        return super.resolveUrl(path);
    }
}
