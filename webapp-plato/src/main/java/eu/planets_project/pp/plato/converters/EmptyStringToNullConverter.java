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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;

/**
 * This converter is needed in order to display null as an option in selectMenus.
 *
 * So in case of a null value an empty string will be displayed.
 */
@Name("emptyStringToNullConverter")
//@org.jboss.seam.annotations.jsf.Converter(forClass=java.lang.String.class)
@org.jboss.seam.annotations.faces.Converter(forClass=java.lang.String.class)
public class EmptyStringToNullConverter implements Converter, Serializable {

    private static final long serialVersionUID = 2665071598357929282L;

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
            throws ConverterException {
        // If it is the empty string, return null, the string itself otherwise
        return arg2.equals("") ? null : arg2;
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ConverterException {
        // If it is null, return the empty string, object.toString() otherwise
        return arg2 == null ? "" : arg2.toString();
    }

}
