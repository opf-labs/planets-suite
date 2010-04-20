/**
 * 
 */
package eu.planets_project.tb.impl.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.ExperimentImpl;


/**
 * This class uses the JAXB system to serialise and de-serialise a
 * Testbed experiment to/from XML.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentViaJAXB {
    private static Log log = LogFactory.getLog(ExperimentViaJAXB.class);
    
    /**
     * 
     */
    private static Class<ExperimentImpl> PACKAGE_CONTEXT = eu.planets_project.tb.impl.model.ExperimentImpl.class;
    
    /**
     * @param input
     * @return
     */
    private static ExperimentImpl readFromFile( File input ) {
        try {
            return readFromInputStream(new FileInputStream( input ));
        } catch (FileNotFoundException e) {
            log.fatal("Reading Experiment from XML failed: "+e);
            return null;
        }
    }
    
    /**
     * @param experiment
     * @param output
     */
    private static void writeToFile( ExperimentImpl exp, File output ) {
        try {
            writeToOutputStream( exp,  new FileOutputStream(output) );
        } catch (FileNotFoundException e) {
            log.fatal("Writing Experiment to XML failed: "+e);
        }
    }
    
    /**
     * A deep copy experiment that used the JAXB serialisation to perform a copy.
     * @param exp The source experiment.
     * @return A new Experiment, detached from the DB with no XmlTransient data set.
     */
    public static ExperimentImpl deepCopy( ExperimentImpl exp ) {
        try {
            File temp = File.createTempFile("tb-experiment", ".xml");
            ExperimentViaJAXB.writeToFile(exp, temp);
            ExperimentImpl exp2 = ExperimentViaJAXB.readFromFile(temp);
            temp.delete();
            return exp2;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @param in
     * @return
     */
    private static ExperimentImpl readFromInputStream( InputStream in ) {
        try {
            JAXBContext jc = JAXBContext.newInstance( PACKAGE_CONTEXT );
            Unmarshaller u = jc.createUnmarshaller();
            ExperimentImpl exp = (ExperimentImpl) u.unmarshal( in );
            return exp;
        } catch (JAXBException e) {
            log.fatal("Reading Experiment from XML failed: "+e);
            return null;
        }
    }

    /**
     * @param exp
     * @param out
     */
    private static void writeToOutputStream( ExperimentImpl exp, OutputStream out ) {
        try {
            JAXBContext jc = JAXBContext.newInstance( PACKAGE_CONTEXT );
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal( exp, out );
        } catch (JAXBException e) {
            log.fatal("Writing Experiment to XML failed: "+e);
        }
    }

    /**
     * Load an experiment from a file and add to the DB.
     * @param uploaded The File containing the experiment XML.
     * @return the ID of the new experiment.
     */
    private static long storeNewExperiment(File uploaded) {
        log.info("Importing experiment from file: "+uploaded.getPath());
        ExperimentImpl exp = ExperimentViaJAXB.readFromFile(uploaded);
        log.info("Parsed into Experiment: "+exp.getExperimentSetup().getBasicProperties().getExperimentName());
        return storeExperiment(exp);
    }
    
    /**
     * Creates a new experiment from the current one.  i.e. a deep copy and store into DB.
     * @param exp The Experiment to copy.
     * @return the ID of the new experiment.
     */
    public static long storeCopyOfExperiment( ExperimentImpl exp ) {
        return storeExperiment( deepCopy(exp) );
    }
    
    /**
     * Looks up the data manager and stores the given experiment in the DB.
     * @param exp The experiment to store.
     * @return the ID of the new experiment.
     */
    private static long storeExperiment( ExperimentImpl exp ) {
        // Merge into DB
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        long lExpID = testbedMan.registerExperiment(exp);
        Experiment newExp = testbedMan.getExperiment(lExpID);
        log.info("Persisted experiment "+newExp.getEntityID());
        return lExpID;
    }
    
}
