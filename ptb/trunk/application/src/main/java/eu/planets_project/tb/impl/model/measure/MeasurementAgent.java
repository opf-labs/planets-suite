package eu.planets_project.tb.impl.model.measure;

import java.io.Serializable;
import java.net.URL;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * 
 */
@Embeddable
@XmlRootElement(name = "MeasurementAgent")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MeasurementAgent  implements Serializable {

    /** */
    private static final long serialVersionUID = -8586082966334195291L; 

    /** */
    public static enum AgentType { 
        /** This measurement event was carried out by a human testbed user. */
        USER,
        /** This measurement event was carried out by a service. */
        SERVICE,
        /** This measurement event was carried out by workflow. */
        WORKFLOW
    }
    
    // Agent is service, invoked by user?
    
    /** The Agent that took these measurements. */
    private AgentType type;

    /** A record of the identity of the Agent, if it is a User */
    private String username;
    
    /** A record of the user's environment. */
    private String userEnvironmentDescription;
    
    /** Record the identity of the Agent, if it is a Service */
    private String serviceName;
    private URL serviceEndpoint;

    /**
     * @param describe
     */
    public MeasurementAgent(ServiceDescription sd) {
        this.type = AgentType.SERVICE;
        /// TODO ...
        this.serviceName = sd.getName();
        this.serviceEndpoint = sd.getEndpoint();
    }
    
    public MeasurementAgent(UserBean user) {
        this.type = AgentType.USER;
        if( user != null ) {
            this.username = user.getUserid();
        }
    }

    /** */
    public MeasurementAgent() { }

    /**
     * @return the agentType
     */
    public AgentType getType() {
        return type;
    }

    /**
     * @param agentType the agentType to set
     */
    public void setType(AgentType agentType) {
        this.type = agentType;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the userEnvironmentDescription
     */
    public String getUserEnvironmentDescription() {
        return userEnvironmentDescription;
    }

    /**
     * @param userEnvironmentDescription the userEnvironmentDescription to set
     */
    public void setUserEnvironmentDescription(String userEnvironmentDescription) {
        this.userEnvironmentDescription = userEnvironmentDescription;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the serviceEndpoint
     */
    public URL getServiceEndpoint() {
        return serviceEndpoint;
    }

    /**
     * @return
     */
    public String getName() {
        if( this.type == AgentType.USER ) return this.username;
        return this.serviceName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MeasurementAgent [type=" + type + ", name=" + getName()
                + "]";
    }

}