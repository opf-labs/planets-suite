/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.ifr.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for psBinding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="psBinding">
 *   &lt;complexContent>
 *     &lt;extension base="{http://planets-project.eu/ifr/core/registry}psRegistryObject">
 *       &lt;sequence>
 *         &lt;element name="accessuri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="service" type="{http://planets-project.eu/ifr/core/registry}psService" minOccurs="0"/>
 *         &lt;element name="targetbinding" type="{http://planets-project.eu/ifr/core/registry}psBinding" minOccurs="0"/>
 *         &lt;element name="validateuri" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "psBinding", propOrder = {
    "accessuri",
    "service",
    "targetbinding",
    "validateuri"
})
public class PsBinding
    extends PsRegistryObject
{

    protected String accessuri;
    protected PsService service;
    protected PsBinding targetbinding;
    protected boolean validateuri;

    /**
     * Gets the value of the accessuri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessuri() {
        return accessuri;
    }

    /**
     * Sets the value of the accessuri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessuri(String value) {
        this.accessuri = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link PsService }
     *     
     */
    public PsService getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link PsService }
     *     
     */
    public void setService(PsService value) {
        this.service = value;
    }

    /**
     * Gets the value of the targetbinding property.
     * 
     * @return
     *     possible object is
     *     {@link PsBinding }
     *     
     */
    public PsBinding getTargetbinding() {
        return targetbinding;
    }

    /**
     * Sets the value of the targetbinding property.
     * 
     * @param value
     *     allowed object is
     *     {@link PsBinding }
     *     
     */
    public void setTargetbinding(PsBinding value) {
        this.targetbinding = value;
    }

    /**
     * Gets the value of the validateuri property.
     * 
     */
    public boolean isValidateuri() {
        return validateuri;
    }

    /**
     * Sets the value of the validateuri property.
     * 
     */
    public void setValidateuri(boolean value) {
        this.validateuri = value;
    }

}
