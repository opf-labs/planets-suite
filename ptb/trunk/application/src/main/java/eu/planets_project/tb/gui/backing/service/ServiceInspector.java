/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;

/**
 * 
 * A request-scope bean that handles inspection of a service.  The URLs and JSF links pass an
 * f:param to this bean, which looks up the service and makes it available to the page.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceInspector {
    /** */
    private static final Log log = LogFactory.getLog(ServiceInspector.class);

    private String serviceName;
    
    private ServiceRecordBean srb = null;

    /**
     * @param serviceName
     */
    public void setServiceName(String serviceName) { 
        this.serviceName = serviceName; 
        lookForService(); 
    }

    /**
     * @return
     */
    public String getServiceName() { 
        return serviceName; 
    }

    /**
     * 
     */
    private void lookForService() {
        log.info("Looking up service: " + this.serviceName);
        if( this.serviceName == null ) this.srb = null;
        
        // Get the service browser:
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        
        // Need a consistent way of getting the full record...
        for( ServiceRecordBean srb : sb.getAllServicesAndRecords() ) {
            if( this.serviceName.equals(srb.getName()) ) {
                 this.srb = srb;
                 // FIXME Return the first hit:
                 return;
            }
        }
        this.srb = null;
    }

    /**
     * @return
     */
    public ServiceRecordBean getService() {
        return this.srb;
    }

}
