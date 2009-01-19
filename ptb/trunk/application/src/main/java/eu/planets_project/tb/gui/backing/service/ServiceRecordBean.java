/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import java.net.URI;
import java.util.List;

import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceRecordBean {

    /** Any active service has a description. */
    ServiceDescription sd;
    
    /** Any tested service should have a service record. */
    ServiceRecordImpl sr;
    

    /**
     * @param sr
     */
    public ServiceRecordBean(ServiceRecordImpl sr) {
        this.sr = sr;
    }

    /**
     * @param sd
     */
    public ServiceRecordBean(ServiceDescription sd) {
        this.sd = sd;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        if( sd != null ) {
            return sd.getName();
        }
        if( sr != null ) {
            return sr.getServiceName();
        }
        return "Unknown";
    }

    /**
     * 
     * @return
     */
    public String getType(){
        // FIXME Map these to pretty strings.
        if( sd != null ) {
            return mapTypeName( sd.getType() );
        }
        if( sr != null ) {
            return mapTypeName( sr.getServiceDescription().getType() );
        }
        return "Unknown";
    }

    /**
     * @param type
     * @return
     */
    private String mapTypeName( String type ) {
        return type.substring( type.lastIndexOf(".")+1 );
    }

    /**
     * 
     * @return
     */
    public String getDescription(){
        if( sd != null ) {
            return sd.getDescription();
        }
        if( sr != null ) {
            return sr.getServiceDescription().getDescription();
        }
        return "Unknown";
    }
    
    /**
     * @return
     */
    public URI getFurtherInfo() {
        if( sd != null ) {
            return sd.getFurtherInfo();
        }
        if( sr != null ) {
            return sr.getServiceDescription().getFurtherInfo();
        }
        return null;
    }
    
    /**
     * 
     * @return
     */
    public boolean isActive() {
        return (sd != null);
    }
    
    /**
     * 
     * @return
     */
    public boolean isUsed() {
        return (sr != null);
    }
    
    /**
     * 
     * @return
     */
    public List<Experiment> getExperiments() {
        return null;
    }

    /**
     * @return the sd
     */
    public ServiceDescription getServiceDescription() {
        return sd;
    }

    /**
     * @param sd the sd to set
     */
    public void setServiceDescription(ServiceDescription sd) {
        this.sd = sd;
    }

    /**
     * @return the sr
     */
    public ServiceRecordImpl getServiceRecord() {
        return sr;
    }

    /**
     * @param sr the sr to set
     */
    public void setServiceRecord(ServiceRecordImpl sr) {
        this.sr = sr;
    }
    
}
