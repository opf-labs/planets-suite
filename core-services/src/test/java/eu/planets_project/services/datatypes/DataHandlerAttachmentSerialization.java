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
package eu.planets_project.services.datatypes;

import java.io.StringWriter;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for experimenting with different settings to make JAXB work with @XmlAttachmentRef.
 * <p/>
 * If this test would pass with the @XmlAttachmentRef, the copying work-around in ImmutableDigitalObject#toXml()
 * would no longer be required.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class DataHandlerAttachmentSerialization {

    @XmlRootElement
    static class RootObject {
        @XmlElement
        @XmlAttachmentRef
        // Works here without this, but required for streaming in web services
        DataHandler dataHandler = new DataHandler(new com.sun.xml.ws.util.ByteArrayDataSource(" ".getBytes(),
                "application/octet-stream"));
    }

    @Test
    public void test() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(RootObject.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(new RootObject(), writer);
        Assert.assertNotNull(writer.toString());
        System.out.println("Marshalled: " + writer.toString());
    }
}