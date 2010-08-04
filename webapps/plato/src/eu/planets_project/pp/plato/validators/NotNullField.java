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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.validator.ValidatorClass;

/**
 * Annotation that specifies the field <tt>fieldname</tt> (which can be set) as not nullable.
 * Respective validator is implemented in 
 * {@link eu.planets_project.pp.plato.validators.NotNullFieldValidator}
 *
 * @author Hannes Kulovits
 */
@ValidatorClass(NotNullFieldValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNullField {
    String fieldname();
    String message() default "Please enter a value for '{fieldname}'";//"{validator.notnullfield}";
}
