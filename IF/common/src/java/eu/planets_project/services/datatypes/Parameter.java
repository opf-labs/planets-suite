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
/**
 *
 */
package eu.planets_project.services.datatypes;

import eu.planets_project.services.PlanetsServices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This wraps the concept of a service parameter. When retrieved from a service,
 * the default values should be set. This form does not allow optional v.
 * required parameters, as ALL parameters should be explicitly specified. An
 * 'optional' parameter implies an implicit default that would end up not being
 * recorded in the audit trail.
 * 
 * @author AnJackson
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = PlanetsServices.SERVICES_NS)
public final class Parameter {

    private String name;
    private String value;
    private String type;
    private String description;

    /* ------------------------------------------------------------------------- */

    /**
     * For JAXB.
     */
    @SuppressWarnings("unused")
    private Parameter() {
    }

    /**
     * Constructor for the most common case: a Parameter with name and value.
     * For parameters with optional values (type, description), use a
     * Parameter.Builder.
     * 
     * @param name
     *            A name for the parameter. Must be uniquely meaningful to the
     *            service, but is not expected to carry any meaning outwith the
     *            service.
     * @param value
     *            The value for this parameter. Should be set to the default by
     *            the service when parameter discovery is happening.
     */
    public Parameter(final String name, final String value) {
	this.name = name;
	this.value = value;
    }

    /* ------------------------------------------------------------------------- */

    /**
     * @param builder
     *            The builder to create a Parameter from
     */
    private Parameter(final Builder builder) {
	this.name = builder.name;
	this.value = builder.value;
	this.type = builder.type;
	this.description = builder.description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof Parameter)) {

	    final Parameter foreignParameter = (Parameter) obj;
	    boolean isEqual = true;

	    if (name == null) {
		isEqual = isEqual && (foreignParameter.name == null);
	    } else {
		isEqual = isEqual && name.equals(foreignParameter.name);
	    }

	    if (value == null) {
		isEqual = isEqual && (foreignParameter.value == null);
	    } else {
		isEqual = isEqual && value.equals(foreignParameter.value);
	    }

	    if (type == null) {
		isEqual = isEqual && (foreignParameter.type == null);
	    } else {
		isEqual = isEqual && type.equals(foreignParameter.type);
	    }

	    if (description == null) {
		isEqual = isEqual && (foreignParameter.description == null);
	    } else {
		isEqual = isEqual
			&& description.equals(foreignParameter.description);
	    }

	    return isEqual;
	} else {
	    return false;
	}
    }

    /**
     * @return the value
     */
    public String getValue() {
	return value;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

	int hash = 0;
	if (name != null) {
	    hash = name.hashCode();
	}

	if (value != null) {
	    hash = hash * 17 + value.hashCode();
	}

	if (type != null) {
	    hash = hash * 19 + type.hashCode();
	}

	if (description != null) {
	    hash = hash * 23 + description.hashCode();
	}

	if (hash == 0) {
	    return super.hashCode();
	} else {
	    return hash;
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return this.name + " = " + this.value;
    }

    /**
     * Builder for Parameters with optional values (type, description).
     * 
     * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
     */
    public static final class Builder {
	private String name;
	private String value;
	private String type;
	private String description;

	/** For JAXB. */
	@SuppressWarnings("unused")
	private Builder() {
	}

	/**
	 * @param name
	 *            The Parameter name, see
	 *            {@link Parameter#Parameter(String, String)}
	 * @param value
	 *            The Parameter value, see
	 *            {@link Parameter#Parameter(String, String)}
	 */
	public Builder(final String name, final String value) {
	    this.name = name;
	    this.value = value;
	    this.type = "";
	    this.description = "";
	}

	/**
	 * @param type
	 *            This is a String to hold the type, which should map to the
	 *            xsd types and should be assumed to be a String if empty or
	 *            null. In the future, we might add limits/validation?
	 *            XSD-style?
	 * @return This builder, for cascaded calls
	 */
	public Builder type(final String type) {
	    this.type = type;
	    return this;
	}

	/**
	 * @param description
	 *            The description of this parameter/value pair. Might be
	 *            used to give further information on the possible values
	 *            and their meaning.
	 * @return This builder, for cascaded calls
	 */
	public Builder description(final String description) {
	    this.description = description;
	    return this;
	}

	/**
	 * @return The built Parameter object
	 */
	public Parameter build() {
	    return new Parameter(this);
	}
    }

}
