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
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * This converter converts an empty value (null or empty string) to 0.0.
 *
 * @author Hannes Kulovits
 */
public class EmptyStringToZeroConverter implements Converter, Serializable {

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

        Double result;


        if (value.trim().length() == 0) { // empty string
            result = Double.valueOf(0);
        } else {                          // some string
            try {
                result = Double.valueOf(Double.parseDouble(value));
            } catch (NumberFormatException e) { // not a number

                FacesMessage message = new FacesMessage();
                message.setDetail("The value must be a numeric value.");
                message.setSummary("Not a double");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ConverterException(message);
            }
        }
        return result;
    }

    /**
     * Converts the double value to its string representation. In case the value is zero it is
     * converted to 0.0.
     *
     * @param value Double representation of the entered value. must not be null!
     *
     * @return string representation of the parameter <code>value</code>.
     */
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null){
            return "";
        }
        double v = ((Double) value).doubleValue();
        if (v == 0) {
            return "0.0";
        } else {
            return Double.toString(v);
        }
    }
}
