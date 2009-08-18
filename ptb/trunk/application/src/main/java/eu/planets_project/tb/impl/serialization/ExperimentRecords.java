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
package eu.planets_project.tb.impl.serialization;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;

/**
 * @author AnJackson
 *
 */
@XmlRootElement(name = "ExperimentRecords", namespace = "http://www.planets-project.eu/testbed/experiment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExperimentRecords {

    /* The list of experiments */
    List<ExperimentRecord> experimentRecords;
    
    @XmlTransient
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExperimentRecords.class);

    /* For JAXB */
    public ExperimentRecords() {
    }

    /**
     * @param in
     * @return
     */
    public static ExperimentRecords readFromInputStream( InputStream in ) {
        try {
            JAXBContext jc = JAXBContext.newInstance();
            Unmarshaller u = jc.createUnmarshaller();
            ExperimentRecords exp = (ExperimentRecords) u.unmarshal( in );
            return exp;
        } catch (JAXBException e) {
            log.fatal("Reading Experiments from XML failed: "+e);
            return null;
        }
    }

    /**
     * @param exp
     * @param out
     */
    public static void writeToOutputStream( ExperimentRecords exp, OutputStream out ) {
        try {
            JAXBContext jc = JAXBContext.newInstance();
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal( exp, out );
        } catch (JAXBException e) {
            log.fatal("Writing Experiments to XML failed: "+e);
        }
    }
    
}
