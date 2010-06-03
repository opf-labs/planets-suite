/**
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.api.persistency.ServiceRecordPersistencyRemote;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;
import eu.planets_project.tb.impl.persistency.ServiceRecordPersistencyImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Entity
@XmlRootElement(name = "ExecutionRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceRecordImpl implements Serializable {
    /** */
    private static final Log log = LogFactory.getLog(ServiceRecordImpl.class);
    /** */
    private static final long serialVersionUID = -510307823143330587L;
    
    @Id
    @GeneratedValue
    @XmlTransient
    private long id = -1;

    private String serviceName;
    
    private String serviceVersion;
    
    private String serviceType;
    
    private String endpoint;
    
    private String toolIdentifier;

    private String toolName;

    private String toolVersion;

    private String serviceHash;
    
    private String host;
    
    @Column(columnDefinition=ExperimentPersistencyImpl.TEXT_TYPE)
    private String serviceDescription;
    
    private Calendar dateFirstSeen;
    
    /** List of experiment IDs that saw this service record. */
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
    HashSet<Long> experimentIds = new HashSet<Long>();
    
    /** The service invocation records */
    /*
    @OneToMany(cascade=CascadeType.ALL, mappedBy="serviceRecord", fetch=FetchType.EAGER)
    private Set<ExecutionStageRecordImpl> invocations = new HashSet<ExecutionStageRecordImpl>();
    */
    
    
    /** Also cache the expanded service description */
    @Transient
    @XmlTransient
    ServiceDescription cached_sd = null;

    /** 
     * Default constructor. For JAXB.
     */
    private ServiceRecordImpl() {
    }
    
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
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
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
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @param toolName the toolName to set
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * @return the toolName
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * @param toolIdentifier the toolIdentifier to set
     */
    public void setToolIdentifier(String toolIdentifier) {
        this.toolIdentifier = toolIdentifier;
    }

    /**
     * @return the toolIdentifier
     */
    public String getToolIdentifier() {
        return toolIdentifier;
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
        this.createCachedServiceDescription();
        return cached_sd;
    }

    /** */
    private void createCachedServiceDescription() {
        if( cached_sd == null ) {
            try {
                cached_sd = ServiceDescription.of(serviceDescription);
            }  catch( Exception e ) {
                log.error("Failed to parse serviceDescription: "+e);
                cached_sd = null;
            }
            if( cached_sd == null ) {
                cached_sd = this.serviceDescriptionFromRecord();
            }
        }
    }

    /** */
    private ServiceDescription serviceDescriptionFromRecord() {
        ServiceDescription.Builder sdb = new ServiceDescription.Builder(this.serviceName, this.serviceType);
        sdb.description("This old service is not longer available.");
        sdb.version(this.serviceVersion);
        // The endpoint:
        try {
            sdb.endpoint( new URL(this.endpoint));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // Tool info:
        URI toolUri = null;
        try {
            if( this.toolIdentifier != null ) {
                toolUri = new URI(this.toolIdentifier);
            }
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        sdb.tool( new Tool(toolUri, this.toolName, this.toolVersion, null, null) );
        return sdb.build();
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
    
    /**
     * @return the invocations
     */
    public Set<Long> getExperimentIds() {
        if( experimentIds == null ) experimentIds = new HashSet<Long>();
        return experimentIds;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dateFirstSeen == null) ? 0 : dateFirstSeen.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime
                * result
                + ((serviceDescription == null) ? 0 : serviceDescription
                        .hashCode());
        result = prime * result
                + ((serviceName == null) ? 0 : serviceName.hashCode());
        result = prime * result
                + ((serviceVersion == null) ? 0 : serviceVersion.hashCode());
        result = prime * result
                + ((toolVersion == null) ? 0 : toolVersion.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceRecordImpl other = (ServiceRecordImpl) obj;
        if (dateFirstSeen == null) {
            if (other.dateFirstSeen != null)
                return false;
        } else if (!dateFirstSeen.equals(other.dateFirstSeen))
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (serviceDescription == null) {
            if (other.serviceDescription != null)
                return false;
        } else if (!serviceDescription.equals(other.serviceDescription))
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        if (serviceVersion == null) {
            if (other.serviceVersion != null)
                return false;
        } else if (!serviceVersion.equals(other.serviceVersion))
            return false;
        if (toolVersion == null) {
            if (other.toolVersion != null)
                return false;
        } else if (!toolVersion.equals(other.toolVersion))
            return false;
        return true;
    }
    
    /* ------------------------------------------------------------------------------- */
    
    /**
     * 
     * @param sd
     * @return
     */
    public static ServiceRecordImpl createServiceRecordFromDescription( long eid, ServiceDescription sd, Calendar date ) {
        log.info("Creating service record for SD = "+sd.getName());        
        // Look to see if there is already a matching ServiceRecord...
        ServiceRecordPersistencyRemote srp = ServiceRecordPersistencyImpl.getInstance();
        
        // This is the unique service identifier:
        String serviceHash = ""+sd.hashCode();
        log.info("Looking for existing service record with hash: "+serviceHash);
        
        // Ensure service records are recorded.
        ServiceRecordImpl sr = srp.findServiceRecordByHashcode(serviceHash);
        if( sr != null ) {
            log.info("Adding eid "+eid+" to service record for "+sr.getServiceName());
            sr.getExperimentIds().add(Long.valueOf(eid));
            log.info("Got "+sr.getExperimentIds().size());
            srp.updateServiceRecord(sr);
            return sr;
        }

        // Otherwise, create a new one.
        sr = new ServiceRecordImpl();
        log.info("Creating new Service Record...");
//      sr.getExperiments().get(0).getExperimentSetup().getBasicProperties().getExperimentName();
//      sr.getExperiments().get(0).getExperimentExecutable().getNumBatchExecutionRecords();
        
        // Fill out:
        sr.setServiceName( sd.getName() );
        sr.setServiceVersion( sd.getVersion() );
        sr.setServiceType( sd.getType() );
        sr.setEndpoint(sd.getEndpoint().toString());
        if( sd.getTool() != null ) {
            sr.setToolName(sd.getTool().getName());
            sr.setToolVersion(sd.getTool().getVersion());
            if( sd.getTool().getIdentifier() != null ) {
                sr.setToolIdentifier(sd.getTool().getIdentifier().toString());
            }
        }
        sr.setServiceHash(serviceHash);
        sr.setHost(sd.getEndpoint().getHost());
        sr.setServiceDescription(sd);
        sr.setDateFirstSeen(date);
        if( eid > -1 ) {
            sr.getExperimentIds().add( Long.valueOf(eid));
        }
        // Persist:
        long srid = srp.persistServiceRecord(sr);
        
        // Return the persisted record:
        return srp.findServiceRecord(srid);
    }

    /**
     * @return
     */
    public List<Experiment> getExperiments() {
        ExperimentPersistencyRemote ep = ExperimentPersistencyImpl.getInstance();
        List<Experiment> exps = new Vector<Experiment>();
        for( Long eid : this.getExperimentIds() ) {
            Experiment exp = ep.findExperiment( eid.longValue() );
            if( exp != null )
                exps.add( exp );
        }
        return exps;
    }

    /**
     * @return the invocations
     */
    /*
    public Set<InvocationRecordImpl> getInvocations() {
        return invocations;
    }
    */

    /**
     * @param invocations the invocations to set
     */
    /*
    public void setInvocations(Set<InvocationRecordImpl> invocations) {
        this.invocations = invocations;
    }
    */
    
}
