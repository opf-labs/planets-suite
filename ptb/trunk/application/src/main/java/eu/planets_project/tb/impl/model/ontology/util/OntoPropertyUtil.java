package eu.planets_project.tb.impl.model.ontology.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * Helper for converting data from the ontology into TB objects
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 25.03.2009
 *
 */
public class OntoPropertyUtil {
	
	private static Log log = LogFactory.getLog(OntoPropertyUtil.class);
	
	 /**
     * Takes a OntologyProperty that's used 
     * and converts it into the Testbed's Property model element: MeasurementImpl
     * @param p eu.planets_project.services.datatypes.Property
     * @return
     */
    public static MeasurementImpl createMeasurementFromOntologyProperty(OntologyProperty p) throws Exception{
    	 MeasurementImpl m = new MeasurementImpl();
    	 if( p == null ) throw new Exception("invalid OntologyProperty: null");
    	 String propURI = p.getURI();
    	 // Invent a uri if required - shouldn't be the case:
    	 if( propURI == null ) {
    	     propURI = TecRegMockup.URI_ONTOLOGY_PROP_ROOT + p.getName();
    	 }
    	 URI pURI;
    	 try {
    	     pURI = new URI(propURI);
    	 } catch (URISyntaxException e) {
    	     log.debug(e);
    	     return m;
    	 } 
		// Copy into measurement property:
    	m.setProperty( OntoPropertyUtil.createPropertyFromOntoProperty(pURI, p) );
        
        return m;
    }
    
    private static Property createPropertyFromOntoProperty(URI identifier,
            OntologyProperty p) {
        return new Property.Builder(identifier)
        .name(p.getName())
        .description(p.getComment())
        .type(p.getType())
        .unit(p.getUnit())
        .value(null).build();
    }

}
