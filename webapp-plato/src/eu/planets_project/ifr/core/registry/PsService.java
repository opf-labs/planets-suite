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
 * <p>Java class for psService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="psService">
 *   &lt;complexContent>
 *     &lt;extension base="{http://planets-project.eu/ifr/core/registry}psRegistryObject">
 *       &lt;sequence>
 *         &lt;element name="organization" type="{http://planets-project.eu/ifr/core/registry}psOrganization" minOccurs="0"/>
 *         &lt;element name="parentCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "psService", propOrder = {
    "organization",
    "parentCategory"
})
public class PsService
    extends PsRegistryObject
{

    protected PsOrganization organization;
    protected String parentCategory;

    /**
     * Gets the value of the organization property.
     * 
     * @return
     *     possible object is
     *     {@link PsOrganization }
     *     
     */
    public PsOrganization getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *     allowed object is
     *     {@link PsOrganization }
     *     
     */
    public void setOrganization(PsOrganization value) {
        this.organization = value;
    }

    /**
     * Gets the value of the parentCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentCategory() {
        return parentCategory;
    }

    /**
     * Sets the value of the parentCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentCategory(String value) {
        this.parentCategory = value;
    }

}
