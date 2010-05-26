/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.services.view;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.Property;

/**
 * This class replies with the result of performing the action on the view session.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ViewActionResult {
    
    /**
     * Possible result types.
     */
    public enum ActionResult { SUCCESS, FAILURE, UNKNOWN_ACTION };
    
    ActionResult actionResult;
    
    List<Property> properties;
    
    /** For JAXB. */
    @SuppressWarnings("unused")
    private ViewActionResult() { }
    
    /**
     * @param actionResult The action result
     * @param properties The properties
     */
    public ViewActionResult( ActionResult actionResult, List<Property> properties ) {
        this.actionResult = actionResult;
        this.properties = properties;
    }

    /**
     * @return the actionResult
     */
    public ActionResult getActionResult() {
        return actionResult;
    }

    /**
     * @return the properties 
     */
    public List<Property> getProperties() {
        return properties;
    }
    
}
