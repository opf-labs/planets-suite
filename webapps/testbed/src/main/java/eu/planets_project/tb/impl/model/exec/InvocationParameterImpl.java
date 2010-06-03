/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.services.datatypes.Parameter;

/**
 * @author AnJackson
 *
 */
@Entity
@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD) 
public class InvocationParameterImpl implements Serializable {
    /** */
    private static final long serialVersionUID = 2902934467655678164L;

    @Id
    @GeneratedValue
    @XmlTransient
    private long id = -1;

    @ManyToOne
    private ExecutionStageRecordImpl invocation;
    
    private String identifier;
    
    private String name;
    
    private String value;

    private String type;
    
    private String description;

    /** For JAXB */
    @SuppressWarnings("unused")
    private InvocationParameterImpl() {
    }
    /**
     * @param p
     */
    protected InvocationParameterImpl( Parameter p ) {
        this.identifier = "planets:tb/srv/input/"+p.getName();
        this.name = p.getName();
        this.value = p.getValue();
        this.type = p.getType();
        this.description = p.getDescription();
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the invocation
     */
    public ExecutionStageRecordImpl getInvocation() {
        return invocation;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param id the id to set
     */
    protected void setId(long id) {
        this.id = id;
    }

    /**
     * @param invocation the invocation to set
     */
    protected void setInvocation(ExecutionStageRecordImpl invocation) {
        this.invocation = invocation;
    }

    /**
     * @param identifier the identifier to set
     */
    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @param name the name to set
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * @param value the value to set
     */
    protected void setValue(String value) {
        this.value = value;
    }

    /**
     * @param type the type to set
     */
    protected void setType(String type) {
        this.type = type;
    }

    /**
     * @param description the description to set
     */
    protected void setDescription(String description) {
        this.description = description;
    }
    
    

}
