/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Embeddable
@XmlRootElement(name = "ExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ServiceRecordImpl implements Serializable {
    /** */
    private static final long serialVersionUID = -510307823143330587L;

//    @Id
//    @GeneratedValue
    @XmlTransient
    private long id;

    private String serviceName;
    
    private String serviceVersion;
    
    private String toolVersion;
    
    private String serviceHash;
    
    private String host;
    
    private String serviceDescription;
    
    private Calendar dateFirstSeen;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceVersion
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * @param serviceVersion the serviceVersion to set
     */
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * @return the toolVersion
     */
    public String getToolVersion() {
        return toolVersion;
    }

    /**
     * @param toolVersion the toolVersion to set
     */
    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    /**
     * @return the serviceHash
     */
    public String getServiceHash() {
        return serviceHash;
    }

    /**
     * @param serviceHash the serviceHash to set
     */
    public void setServiceHash(String serviceHash) {
        this.serviceHash = serviceHash;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the serviceDescription
     */
    public ServiceDescription getServiceDescription() {
        return ServiceDescription.of(serviceDescription);
    }

    /**
     * @param serviceDescription the serviceDescription to set
     */
    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
    
    /**
     * 
     * @param sd
     */
    public void setServiceDescription(ServiceDescription sd ) {
        this.serviceDescription = sd.toXml();
    }

    /**
     * @return the dateFirstSeen
     */
    public Calendar getDateFirstSeen() {
        return dateFirstSeen;
    }

    /**
     * @param dateFirstSeen the dateFirstSeen to set
     */
    public void setDateFirstSeen(Calendar dateFirstSeen) {
        this.dateFirstSeen = dateFirstSeen;
    }
    
}
