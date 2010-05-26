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
package eu.planets_project.ifr.core.techreg.properties;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Property;

/**
 * A list of property definitions, that can be stored as XML.
 * 
 * @author AnJackson
 */
@XmlRootElement(name = "properties", namespace = PlanetsServices.DATATYPES_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyDefinitionFile {

    @XmlElement(name="property", namespace = PlanetsServices.DATATYPES_NS)
    List<Property> properties = null;

    /* For JAXB */
    @SuppressWarnings("unused")
    private PropertyDefinitionFile() { }
    
    /**
     * @param ps A list of properties to encapsulate. Any supplied 
     * values are reset to null, so that only the definition remains.
     */
    public PropertyDefinitionFile( List<Property> ps ) {
        // Copy in the properties, and wipe the values.
        this.properties = new ArrayList<Property>();
        for( Property p : ps ) {
            this.properties.add( new Property.Builder(p).value(null).build() );
        }
    }
    
    /**
     * @return An XML representation (can be used to
     *         instantiate an object using the static factory method)
     */
    public String toXml() {
        return toXml(false);
    }

    /**
     * @return A formatted (pretty-printed) XML representation.
     */
    public String toXmlFormatted() {
        return toXml(true);
    }

    private String toXml(boolean formatted) {
        try {
            /* Marshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(PropertyDefinitionFile.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.setProperty("jaxb.formatted.output", formatted);
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @param xml The XML representation (as created
     *        from calling toXml)
     * @return A digital object instance created from the given XML
     */
    public static PropertyDefinitionFile of(final String xml) {
        try {
            /* Unmarshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(PropertyDefinitionFile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(xml));
            PropertyDefinitionFile unmarshalled = (PropertyDefinitionFile) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
