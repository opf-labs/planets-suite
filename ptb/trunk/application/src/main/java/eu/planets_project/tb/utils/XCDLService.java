/**
 * 
 */
package eu.planets_project.tb.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.xml.ws.Service;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.system.BackendProperties;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class XCDLService implements Characterise {

    /** */
    private URL extractorWsdl;
    /** */
    private Characterise extractor;
    
    /** */
    public XCDLService() {
        BackendProperties bp = new BackendProperties();
        try {
            extractorWsdl = new URL( bp.getProperty("extractor.endpoint.xcdl") );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Got Extractor endpoint: "+extractorWsdl);
        // extractorWsdl = new URI( "http://localhost:8080/pserv-pc-extractor/Extractor?wsdl" );
        Service srv = Service.create(extractorWsdl, Characterise.QNAME);
        extractor = srv.getPort(Characterise.class);
    }

    /** */
    public XCDLService( URL endpoint ) {
        this.extractorWsdl = endpoint;
        Service srv = Service.create(extractorWsdl, Characterise.QNAME);
        extractor = srv.getPort(Characterise.class);
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#describe()
     */
    public ServiceDescription describe() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#getMeasurableProperties(java.net.URI)
     */
    public List<Property> listProperties(URI formatURI) {
        // Only cope with PRONOM IDs:
        if( ! FormatRegistryFactory.getFormatRegistry().isPronomUri(formatURI) ) {
            return null;
        }
        
        // Extract the list:
        List<Property> properties = extractor.listProperties(formatURI);
        
        /*
        if( properties == null ) return null;
        // Now create list and copy the properties into it:
        List<Property> props = new Vector<Property>();
        for( FileFormatProperty ffp : properties ) {
            //System.out.println("Got property "+ffp.getId() + ", " +ffp.getName() + ", " + ffp.getDescription() );
            props.add( this.createPropertyFromFFProp(ffp) );
        }
        */
        return properties;
    }
    
    // FIXME Unify this construction: See also XCDLParser.parseXCDL
    /*
    private Property createPropertyFromFFProp( FileFormatProperty ffp ) {
        Property p = new Property( 
                XCDLParser.makePropertyUri(ffp.getId(), ffp.getName()), 
                "id"+ffp.getId()+"/"+ffp.getName(), 
                ffp.getValue() );
        p.setDescription(ffp.getDescription());
        p.setType(ffp.getType());
        p.setUnit(ffp.getUnit());
        return p;
    }
    */
    
    /* (non-Javadoc)
     * @see eu.planets_project.services.characterise.DetermineProperties#measure(eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.services.datatypes.Properties, eu.planets_project.services.datatypes.Parameters)
     */
    public CharacteriseResult characterise(DigitalObject dob, List<Parameter> params) {
        
        CharacteriseResult characteriseResult = extractor.characterise(dob, params);

        // FIXME Use the properties interface / generally update the invoker as this code is not needed now...
        /*
        for( Property p : characteriseResult.getProperties() ) {
            log.info("Got p = "+p);
        }
        */

        List<MeasurementRecordImpl> list;
        try {
            list = XCDLParser.parseXCDL(characteriseResult.getDigitalObject().getContent().read());
        } catch (Exception e) {
            e.printStackTrace();
            list = null;
        }
        List<Property> mprops = new Vector<Property>();
        if( list != null ) {
            for( MeasurementRecordImpl m : list ) {
                Property p;
                try {
                    p = new Property( new URI( m.getIdentifier()), m.getIdentifier(), m.getValue() );
                    mprops.add(p);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // FIXME Interface is a problem!  We really want to return simpler entities than full property descriptions.

        ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");
        
        return new CharacteriseResult(mprops, report);
    }

}
