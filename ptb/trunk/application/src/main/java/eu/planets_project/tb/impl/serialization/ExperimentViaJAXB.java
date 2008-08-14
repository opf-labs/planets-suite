/**
 * 
 */
package eu.planets_project.tb.impl.serialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.model.ExperimentImpl;


/**
 * This class uses the JAXB system to serialise and de-serialise a
 * Testbed experiment to/from XML.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentViaJAXB {
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExperimentViaJAXB.class);
    
    /**
     * 
     */
    private static Class<ExperimentImpl> PACKAGE_CONTEXT = eu.planets_project.tb.impl.model.ExperimentImpl.class;
    
    /**
     * 
     * @param input
     * @return
     */
    public static ExperimentImpl readFromFile( File input ) {
        try {
            JAXBContext jc = JAXBContext.newInstance( PACKAGE_CONTEXT );
            Unmarshaller u = jc.createUnmarshaller();
            ExperimentImpl exp = (ExperimentImpl) u.unmarshal( new FileInputStream( input ) );
            return exp;
        } catch (JAXBException e) {
            log.error("Reading Experiment from XML failed: "+e);
            return null;
        } catch (FileNotFoundException e) {
            log.error("Reading Experiment from XML failed: "+e);
            return null;
        }
    }
    
    /**
     * 
     * @param experiment
     * @param output
     */
    public static void writeToFile( Experiment experiment, File output ) {
        ExperimentImpl exp = (ExperimentImpl) experiment;
        
        try {
            JAXBContext jc = JAXBContext.newInstance( PACKAGE_CONTEXT );
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal( exp, new FileOutputStream(output) );
        } catch (JAXBException e) {
            log.error("Writing Experiment to XML failed: "+e);
        } catch (FileNotFoundException e) {
            log.error("Writing Experiment to XML failed: "+e);
        }
    }
    
    
}
