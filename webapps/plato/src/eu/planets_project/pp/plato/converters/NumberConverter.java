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
package eu.planets_project.pp.plato.converters;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * This converter converts an empty value (null or empty string) to 0.0.
 *
 * @author Hannes Kulovits
 */
public class NumberConverter implements Converter, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6674250183273455339L;

    /**
     * Converts an empty string or null value to a double value of 0.0. All other
     * values are converted to the respective double value.
     *
     * @param value to be converted
     *
     * @throws ConverterException if the value cannot be converted to double.
     * @return double The parameter value converted to the respective double <code>value</code>.
     *
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null  || value.trim().length() == 0) { // empty string
            FacesMessage message = new FacesMessage();
            message.setDetail("Please enter a value.");
            message.setSummary("No value provided.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(message);
        } else {                          // some string
                try {
                    return Double.valueOf(Double.parseDouble(value));
                } catch (NumberFormatException e) { // not a number
                    FacesMessage message = new FacesMessage();
                    message.setDetail("The value must be a numeric value.");
                    message.setSummary("Not a double");
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ConverterException(message);
                }
            }
    }

    /**
     * Converts the integer or double value to its string representation. In case the value is zero it is
     * converted to 0.0.
     *
     * @param value int or double representation of the entered value.
     *
     * @return string representation of the parameter <code>value</code>.
     */
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null){
            return "";
        }
        return Double.toString((Double)value);
    }
}
