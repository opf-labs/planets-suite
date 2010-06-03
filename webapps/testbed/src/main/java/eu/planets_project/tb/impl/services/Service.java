package eu.planets_project.tb.impl.services;

import java.net.URL;
import java.util.Date;

import eu.planets_project.services.datatypes.ServiceDescription;
//import eu.planets_project.services.utils.DiscoveryUtils;
import eu.planets_project.tb.impl.services.util.DiscoveryUtils;

/**
 * The purpose of the service class is to record the information we wish to 
 * store about a service in the long term, and provide access to any extra
 * information that the service provides at present.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class Service {
    // Stored:
    URL endpoint;
    String name;
    String type;
    Date dateOfUse;
    
    // Transient:
    ServiceDescription sd;
    
    protected Service() {} 
    
    @Deprecated
    public Service(URL endpoint) {
        this.endpoint = endpoint;
        // Attempt to query the endpoint for a description.
        sd = DiscoveryUtils.getServiceDescription(endpoint);
        fillFromServiceDescription();
    }
    
    @Deprecated
    public Service(URL endpoint, ServiceDescription sd ) {
        this.endpoint = endpoint;
        this.sd = sd;
        fillFromServiceDescription();
        // Also add in the date that this service was inspected:
    }
    
    public Service(ServiceDescription serDescr){
    	sd = serDescr;
    	this.endpoint = sd.getEndpoint();
    	fillFromServiceDescription();
    }
    
    /**
     * This copies the important fields from the Service Description so that they might be stored in the TB records.
     */
    private void fillFromServiceDescription() {
        if( sd == null ){
            // If there is not service description, record what we can.
            this.name = this.endpoint.getPath();
            this.type = "unknown";
        } else {
            this.name = sd.getName();
            this.type = sd.getType();
            if( this.name == null || "".equals( this.name.trim() ) ) {
                this.name = this.endpoint.getPath();
            }            
        }
        this.dateOfUse = new Date();
    }

    /**
     * @return the endpoint
     */
    public URL getEndpoint() {
        return endpoint;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the dateOfUse
     */
    public Date getDateOfUse() {
        return dateOfUse;
    }
    
    /**
     * 
     * @return
     */
    public String getDateOfUseString() {
        if( dateOfUse != null )
            return dateOfUse.toString();
        return "";
    }

    /**
     * 
     * @return true if this Service has an associated Service Description.
     */
    public boolean hasServiceDescription() {
        if( sd == null ) return false;
        return true;
    }

    /**
     * @return the sd
     */
    public ServiceDescription getServiceDescription() {
        return sd;
    }

}