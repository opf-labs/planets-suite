/**
 * 
 */
package eu.planets_project.tb.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class XCDLParser {
    
    /**
     * 
     * @param xcdlDoc
     * @return
     * @throws XPathExpressionException
     */
    public static List<MeasurementRecordImpl> parseXCDL(Document xcdlDoc) throws XPathExpressionException {
        List<MeasurementRecordImpl> props = new Vector<MeasurementRecordImpl>();

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate("/*//property", xcdlDoc, 
                                                    XPathConstants.NODESET);
        
        for( int i = 0; i < nodes.getLength(); i++ ) {
            Node n = nodes.item(i);
            String name = (String) xpath.evaluate( "./name", n,  XPathConstants.STRING);
            String id = (String) xpath.evaluate( "./name/@id", n,  XPathConstants.STRING);
            // Loop through the property definitions and patch them into Property objects.
            MeasurementRecordImpl m = new MeasurementRecordImpl();
            // FIXME Unify this construction: See also XCDLService.createPropertyFromFFProp
            m.setIdentifier(makePropertyUri(id, name).toString()); 
            m.setValue( (String) xpath.evaluate( "./valueSet/labValue/val", n,  XPathConstants.STRING) );
//            m.setType( "xcdl:" + (String) xpath.evaluate( "./valueSet/labValue/type", n,  XPathConstants.STRING) );
            
            props.add(m);
        }
        
        
        return props;
    }
    
    public static URI makePropertyUri( String id, String name ) {
        try {
            return new URI( TecRegMockup.URI_XCDL_PROP_ROOT + id + "/" + name );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param xcdlFile
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static List<MeasurementRecordImpl> parseXCDL( File xcdlFile ) 
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        String xcdl = FileUtils.readTxtFileIntoString(xcdlFile);
        return XCDLParser.parseXCDL(xcdl);
    }

    /**
     * 
     * @param xcdl
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static List<MeasurementRecordImpl> parseXCDL( String xcdl ) 
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory =   DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(false);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Reader reader = new StringReader(xcdl);
        InputSource inputSource = new InputSource(reader);
        Document xcdlDoc = builder.parse(inputSource);
        return XCDLParser.parseXCDL(xcdlDoc);
    }

    /**
     * @param read
     * @return
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public static List<MeasurementRecordImpl> parseXCDL(InputStream input) 
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory =   DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(false);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Reader reader = new InputStreamReader(input);
        InputSource inputSource = new InputSource(reader);
        Document xcdlDoc = builder.parse(inputSource);
        return XCDLParser.parseXCDL(xcdlDoc);
    }
    
}
