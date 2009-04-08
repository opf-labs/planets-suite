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
    
    public enum ActionResult { SUCCESS, FAILURE, UNKNOWN_ACTION };
    
    ActionResult actionResult;
    
    List<Property> properties;
    
    /** For JAXB */
    @SuppressWarnings("unused")
    private ViewActionResult() { }
    
    public ViewActionResult( ActionResult actionResult, List<Property> properties ) {
        this.actionResult = actionResult;
        this.properties = properties;
    }
}
