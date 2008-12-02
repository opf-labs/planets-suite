/**
 * 
 */
package eu.planets_project.tb.impl.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistry;
import eu.planets_project.ifr.core.registry.api.jaxr.ServiceRegistryFactory;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsService;
import eu.planets_project.tb.impl.AdminManagerImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceRegistryManager {
    /** */
    private static final Log log = LogFactory.getLog(ServiceRegistryManager.class);
    
    /** */
    private static final Pattern urlpattern = Pattern.compile("http://[^><\\s]*?\\?wsdl");

    /** Properties for the IF Service Registry */
    protected static final String USERNAME = "provider";
    protected static final String PASSWORD = "provider";
    protected static final String WILDCARD = "%";
    
    /**
     * 
     * @return
     */
    public static List<Service> listAvailableServices() {
        List<Service> sl = new ArrayList<Service>();
        
        for( URI endpoint : listAvailableEndpoints()) {
            log.info("Inspecting endpoint: "+endpoint);
            Service s;
            try {
                s = new Service(endpoint.toURL());
                if( s != null ) sl.add(s);
            } catch (MalformedURLException e) { }
        }
        // Look in the IF Service Registry, for new Endpoints only.
        ServiceRegistry if_sr = ServiceRegistryFactory.getInstance();
        if( if_sr != null ) {
            List<PsService> retrievedServices = if_sr.findServices(USERNAME,
                PASSWORD, WILDCARD, "").services;
            for( PsService if_s : retrievedServices ) {
                log.info("Found service: " + if_s.getName() );
            }
        }
        return sl;
    }

    /**
     * 
     * @return
     */
    public static List<URI> listAvailableEndpoints(){
        Set<URI> uniqueSE = new HashSet<URI>();
        
        // Inspect the local JBossWS endpoints:
        try {
            String authority = PlanetsServerConfig.getHostname() + ":" + PlanetsServerConfig.getPort();
            URI uriPage = new URI("http",authority,"/jbossws/services",null,null);
            //2) extract the page's content: note: not well-formed --> modifications
            String pageContent = readUrlContents(uriPage);
            //3) build a dom tree and extract the text nodes
            //String xPath = new String("/*//fieldset/table/tbody/tr/td/a/@href");
             uniqueSE.addAll( extractEndpointsFromWebPage(pageContent) );
        } catch (Exception e) {
            log.error("Lookup of JBossWS services failed! : "+e);
        }
                
        // Now sort the list and return it.
        List<URI> sList = new ArrayList<URI>(uniqueSE);
        java.util.Collections.sort( sList );
        return sList;
    }
    
    /**
     * Takes a given http URI and extracts the page's content which is returned as String
     * @param uri
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String readUrlContents(URI uri)throws FileNotFoundException, IOException{

        InputStream in = null;
        try{
            if(!uri.getScheme().equals("http")){
                throw new FileNotFoundException("URI schema "+uri.getScheme()+" not supported");
            }
            in = uri.toURL().openStream();
            boolean eof = false;
            String content = "";
            StringBuffer sb = new StringBuffer();
            while(!eof){
                int byteValue = in.read();
                if(byteValue != -1){
                    char b = (char)byteValue;
                    sb.append(b);
                }
                else{
                    eof = true;
                }
            }
            content = sb.toString();
            if(content!=null){
                //now return the services WSDL content
                return content;
            }
            else{
                throw new FileNotFoundException("extracted content is null");
            }
        }
        finally{
            in.close();
        }
    }
    
    /**
     * As the wsdlcontent is not well-formed we're not able to use DOM here.
     * Parse through the xml manually to return a list of given ServiceEndpointAddresses
     * @param xhtml
     * @return
     * @throws Exception
     */
    private static Set<URI> extractEndpointsFromWebPage(String xhtml){
        Set<URI> ret = new HashSet<URI>();

        // Pull out all matching URLs:
        Matcher matcher = urlpattern.matcher(xhtml);
        while( matcher.find() ) { 
            //log.info("Found match: "+matcher.group());
            try {
                URL wsdlUrl = new URL(matcher.group());
                // Switch the authority for the 'proper' one:
                wsdlUrl = new URL( wsdlUrl.getProtocol(), 
                        PlanetsServerConfig.getHostname(), 
                        PlanetsServerConfig.getPort() , 
                        wsdlUrl.getFile());
                //log.info("Got matching URL: "+wsdlUrl);
                try {
                    ret.add(wsdlUrl.toURI());
                } catch (URISyntaxException e) { }
            } catch (MalformedURLException e) {
                log.warn("Could not parse URL from "+matcher.group());
            }
        }
        
        return ret;
    }
    
}
