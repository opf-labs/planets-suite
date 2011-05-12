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

package eu.planets_project.pp.plato.validators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.mapping.Property;
import org.hibernate.validator.PropertyConstraint;
import org.hibernate.validator.Validator;

/**
 * Implements a hibernate validator for annotation {@link eu.planets_project.pp.plato.validators.NotNullField}
 * that validates a string:
 *
 * The string of field fieldname must not be empty or null
 *
 * @author michael
 *
 */
public class NotNullFieldValidator implements Validator<NotNullField>, PropertyConstraint {

    /**
     * Name of the field that shall be validated
     */
    private String fieldname;

    /**
     * Overrides {@link org.hibernate.validator.Validator#initialize(java.lang.annotation.Annotation)}
     */
    public void initialize(NotNullField parameters) {
        fieldname = parameters.fieldname();

        if (fieldname != null && !"".equals(fieldname)) {
            fieldname = "get" + fieldname.substring(0, 1).toUpperCase();
            int length = parameters.fieldname().length();
            if (length > 1)
                fieldname = fieldname + parameters.fieldname().substring(1, length);
        }
    }

    /**
     * Overrides {@link org.hibernate.validator.Validator#isValid(Object)}
     *
     * @return true if field is not null and not an empty string
     */
    public boolean isValid(Object value) {
        if (value == null) return true;
        try {
            Method getter = value.getClass().getMethod(fieldname);
            Object field = getter.invoke(value);

            return ((field != null) && (!"".equals(field)));
        } catch (SecurityException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (InvocationTargetException e){
            return false;
        } catch (IllegalAccessException e){
            return false;
        }
    }

    /**
     * Overrides {@link org.hibernate.validator.PropertyConstraint#apply(Property)}
     */
    public void apply(Property property){
    }
}
