import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class extractTB3OntologyProperties implements NamespaceContext{

	/**
	 * @param args
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 */
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		//e.g. D:/Implementation/Planets/Planets_Ontology/TB3Ontology.owl
		File input = new File(args[0]);
		System.out.println("working on file: "+input.getAbsolutePath());
		File outputXML = new File(input.getParent()+"/extractedTB3OntologyProperties.xml");
		System.out.println("writing output to: "+outputXML.getAbsolutePath());
		extractPropertiesFromTB3Ontology(input,outputXML);
	}
	
	public static void extractPropertiesFromTB3Ontology(File TB3OntologyXML, File OutputXML) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(OutputXML));
		writer.write("<extractedTB3ontologyProperties>\n");
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		docFactory.setNamespaceAware(true);
		Document TB3XMLdoc = docBuilder.parse(TB3OntologyXML);	

		//apply the xpath statements we're looking for
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		//search for "TextProperties"
		//FIXME: having problems getting the namespace working properly with xpath.
		String[] xPaths = new String[]{"//TextProperties","//ImageProperties","//Thing"};
		//XPathExpression expr = xpath.compile("//resultItems/digoInRef[text()='planets://testbed-dev.planets-project.ait.ac.at:80/dr/experiment-files/testbed/users/experimenter/digitalobjects/00000027.tif']");
		int found=0,missed=0;
		for(String xPathStatement : xPaths){
			System.out.println("Working on xPath: "+xPathStatement);
			xpath.setNamespaceContext(new extractTB3OntologyProperties());
			XPathExpression expr = xpath.compile(xPathStatement);
			Object result = expr.evaluate(TB3XMLdoc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			System.out.println("nodes: "+nodes.getLength());
			//now hook in the data for the nodes we identified with xpath
			
			for (int i = 0; i < nodes.getLength(); i++) {
				String name=null, comment=null;
				NamedNodeMap refAttributes = nodes.item(i).getAttributes();
				//extract the attributes and add them to the new document
				for(int k=0;k<refAttributes.getLength();k++){
					if(refAttributes.item(k).getNodeName().equals("rdf:about")){
						name=refAttributes.item(k).getNodeValue();
						if(name.indexOf("#")!=-1){
							name = name.substring(name.indexOf("#")+1);
						}
					}
				}
				NodeList childnodes = nodes.item(i).getChildNodes();
				for (int p = 0; p < childnodes.getLength(); p++) {
					if(childnodes.item(p).getNodeName().equals("rdfs:comment")){
						comment = childnodes.item(p).getTextContent();
					}
				}
				//check if we're having at least name and comment
				if((name!=null)&&(comment!=null)){
					found++;
					String uri = "planets://testbed/properties/ontology/"+name;
					writer.write("<property tburi=\""+uri+"\">\n");
					writer.write("<name>"+name+"</name>\n");		
					writer.write("<description>"+comment+"</description>\n");	
					writer.write("</property>\n");		
				}else{
					missed++;
				}
			}
		}

		writer.write("</extractedTB3ontologyProperties>");
		writer.close();

		System.out.println("nr of properties found: "+found+" missed: "+missed);
	}

	public String getNamespaceURI(String prefix) {
		System.out.println("requesting prefix: "+prefix+" equals owl?"+prefix.equals("owl"));
		 if (prefix.equals("owl"))
            return "http://www.w3.org/2002/07/owl";
        else
            return XMLConstants.NULL_NS_URI;
	}

	public String getPrefix(String namespaceURI) {
		System.out.println("requesting namespaceURI: "+namespaceURI);
		 if (namespaceURI.equals("http://www.w3.org/2002/07/owl"))
	            return "owl";
	        else
	            return null;
	}

	public Iterator getPrefixes(String namespaceURI) {
		System.out.println("get prefixes");
		// TODO Auto-generated method stub
		return null;
	}

}
