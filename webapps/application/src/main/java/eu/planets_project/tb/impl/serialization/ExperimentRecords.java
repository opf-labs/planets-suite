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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Experiment;

/**
 * @author AnJackson
 *
 */
@XmlRootElement(name = "ExperimentRecords", namespace = "http://www.planets-project.eu/testbed/experiment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExperimentRecords {

    /* The list of experiments */
    @XmlElement(name="experimentRecord", type=ExperimentRecord.class)
    List<ExperimentRecord> experimentRecords = new ArrayList<ExperimentRecord>();
    
    @XmlTransient
    private static Log log = LogFactory.getLog(ExperimentRecords.class);

    /* For JAXB */
    protected ExperimentRecords() {
    }
    
    /* Main */
    public ExperimentRecords( long eid ) {
    	experimentRecords.add( new ExperimentRecord(eid));
    }
    /* */
    public ExperimentRecords( Experiment ... exps ) {
    	for( Experiment e : exps ) {
    		experimentRecords.add( new ExperimentRecord(e.getEntityID()));
    	}
    }
   
    /**
     * 
     */
    public void storeInDatabase() {
    	for( ExperimentRecord e : experimentRecords ) {
    		ExperimentRecord.importExperimentRecord(e);
    	}
    }

    /**
     * @param in
     * @return
     */
    public static ExperimentRecords readFromInputStream( InputStream in ) {
        try {
            JAXBContext jc = JAXBContext.newInstance(ExperimentRecords.class);
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
    private static void writeToOutputStream( ExperimentRecords exp, OutputStream out ) {
        try {
            JAXBContext jc = JAXBContext.newInstance(ExperimentRecords.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal( exp, out );
        } catch (JAXBException e) {
            log.fatal("Writing Experiments to XML failed: "+e);
        }
    }

    /**
     * @param out
     * @param exps
     */
	public static void writeExperimentsToOutputStream(OutputStream out, Experiment ... exps) {
		writeToOutputStream( new ExperimentRecords( exps ), out);
	}

	/**
	 * 
	 * @param out
	 * @param ecol
	 */
	public static void writeExperimentsToOutputStream( OutputStream out, Collection<Experiment> ecol ) {
		writeExperimentsToOutputStream(out, ecol.toArray(new Experiment[ecol.size()]));
	}
    
}
