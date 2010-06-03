package eu.planets_project.tb.gui.backing.admin.wsclient.util;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMResult;


/**
 * A generic SOAP Web Services client. Uses the SAAJ library to talk to WebService endpoints.
 *
 * @author Markus Reis, ARC
 */

public class WSClient
{

	private static Log log = LogFactory.getLog(WSClient.class);	
	/** The prefix to use for XML Schema instance namespace */
	public static final String XSI_NAMESPACE_PREFIX = "xsi";

	/** The URI for XML Schema instance namespace */
	public static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";

	/** The prefix to use for XML Schema namespace */
	public static final String XSD_NAMESPACE_PREFIX = "xsd";

	/** The URI for XML Schema namespace */
	public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";

	/**
	 * Constructor
	 */
	public WSClient()
	{
	}

	/**
	 * Invokes an operation using SAAJ
	 *
	 * @param operation The operation to invoke
	 */
	public static String invokeOperation(OperationInfo operation) throws Exception
	{
		try
		{
			// Determine if the operation style is RPC
			boolean isRPC = false;
			if (operation.getStyle() != null)
				isRPC = operation.getStyle().equalsIgnoreCase("rpc");
			else ;

			// All connections are created by using a connection factory
			SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();

			// Now we can create a SOAPConnection object using the connection factory
			SOAPConnection connection = conFactory.createConnection();

			// All SOAP messages are created by using a message factory
			MessageFactory msgFactory = MessageFactory.newInstance();

			// Now we can create the SOAP message object
			SOAPMessage msg = msgFactory.createMessage();

			// Get the SOAP part from the SOAP message object
			SOAPPart soapPart = msg.getSOAPPart();

			// The SOAP part object will automatically contain the SOAP envelope
			SOAPEnvelope envelope = soapPart.getEnvelope();
			//my extension - START
			//envelope.addNamespaceDeclaration("_ns1", operation.getNamespaceURI());
			//my extension - END

			if(isRPC)
			{
				// Add namespace declarations to the envelope, usually only required for RPC/encoded
				envelope.addNamespaceDeclaration(XSI_NAMESPACE_PREFIX, XSI_NAMESPACE_URI);
				envelope.addNamespaceDeclaration(XSD_NAMESPACE_PREFIX, XSD_NAMESPACE_URI);
			}

			// Get the SOAP header from the envelope
			SOAPHeader header = envelope.getHeader();         

			// The client does not yet support SOAP headers
			header.detachNode();

			// Get the SOAP body from the envelope and populate it
			SOAPBody body = envelope.getBody();

			// Create the default namespace for the SOAP body
			//body.addNamespaceDeclaration("", operation.getNamespaceURI());

			// Add the service information
			String targetObjectURI = operation.getTargetObjectURI();

			if(targetObjectURI == null)
			{
				// The target object URI should not be null
				targetObjectURI = "";
			}

			// Add the service information         
			//Name svcInfo = envelope.createName(operation.getTargetMethodName(), "", targetObjectURI);
			Name svcInfo = envelope.createName(operation.getTargetMethodName(), "ns1", operation.getNamespaceURI());
			SOAPElement svcElem = body.addChildElement(svcInfo);

			if(isRPC)
			{
				// Set the encoding style of the service element
				svcElem.setEncodingStyle(operation.getEncodingStyle());
			}

			// Add the message contents to the SOAP body
			Document doc = XMLSupport.readXML(operation.getInputMessageText());

			if(doc.hasRootElement())
			{
				// Begin building content
				buildSoapElement(envelope, svcElem, doc.getRootElement(), isRPC);
			}

			//svcElem.addTextNode(operation.getInputMessageText());
			//svcElem.

			// Check for a SOAPAction
			String soapActionURI = operation.getSoapActionURI();

			if(soapActionURI != null && soapActionURI.length() > 0)
			{
				// Add the SOAPAction value as a MIME header
				MimeHeaders mimeHeaders = msg.getMimeHeaders();
				mimeHeaders.setHeader("SOAPAction", "\"" + operation.getSoapActionURI() + "\"");
			}

			// Save changes to the message we just populated
			msg.saveChanges();

			// Get ready for the invocation
			URLEndpoint endpoint = new URLEndpoint(operation.getTargetURL());

			// Show the URL endpoint message in the log
			ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
			msg.writeTo(msgStream);

			log.debug("SOAP Message MeasurementTarget URL: " + endpoint.getURL());
			log.debug("SOAP Request: " + msgStream.toString());

			// Make the call
			SOAPMessage response = connection.call(msg, endpoint);

			// Close the connection, we are done with it
			connection.close();

			// Get the content of the SOAP response
			Source responseContent = response.getSOAPPart().getContent();

			// Convert the SOAP response into a JDOM
			TransformerFactory tFact = TransformerFactory.newInstance();
			Transformer transformer = tFact.newTransformer();

			JDOMResult jdomResult = new JDOMResult();
			transformer.transform(responseContent, jdomResult);

			// Get the document created by the transform operation
			Document responseDoc = jdomResult.getDocument();

			// Send the response to the Log
			String strResponse = XMLSupport.outputString(responseDoc);
			log.debug("SOAP Response from: " + operation.getTargetMethodName() + ": " + strResponse);

			// Set the response as the output message
			operation.setOutputMessageText(strResponse);

			// Return the response generated
			return strResponse;
		}

		catch(Throwable ex)
		{
			throw new Exception("Error invoking operation", ex);
		}

	}

	/**
	 * Builds a hierarchy of SOAPElements given a complex value JDOM Element
	 *
	 * @param   envelope   The SOAP Envelope
	 * @param   rootElem   The root SOAP Element to build content for
	 * @param   jdomElem   A JDOM Element that represents a complex value
	 * @param   isRPC      Pass true when building for RPC encoded messages
	 *
	 * @throws SOAPException
	 */
	protected static void buildSoapElement(SOAPEnvelope envelope, SOAPElement soapElem, Element jdomElem, boolean isRPC) throws SOAPException
	{
		// If the source node has text use its value
		String elemText = jdomElem.getText();

		if(elemText != null)
		{
			if(isRPC == true)
			{
				// Set the type attribute for this element
				String type = jdomElem.getAttributeValue("type");

				if(type != null)
				{
					soapElem.addAttribute(envelope.createName(XSI_NAMESPACE_PREFIX + ":type"), XSD_NAMESPACE_PREFIX + ":" + type);
				}
			}

			// Add the element text value
			soapElem.addTextNode(elemText);
		}

		// If the source node has attributes add the attribute values
		List attrs = jdomElem.getAttributes();

		if(attrs != null)
		{
			Iterator attrIter = attrs.iterator();

			while(attrIter.hasNext())
			{
				// Get the attribute to add
				Attribute attr = (Attribute)attrIter.next();

				// Create a name for the attribute
				Name attrName = envelope.createName(attr.getName(), attr.getNamespacePrefix(), attr.getNamespaceURI());

				// Add the attribute and its value to the element
				soapElem.addAttribute(attrName, attr.getValue());
			}
		}

		// Build children
		List children = jdomElem.getChildren();

		if(children != null)
		{
			Iterator childIter = children.iterator();

			while(childIter.hasNext())
			{
				Element jdomChildElem = (Element)childIter.next();

				// Create a new SOAPElement as a child of the current one
				//SOAPElement soapChildElem = soapElem.addChildElement(jdomChildElem.getName());
				SOAPElement soapChildElem = soapElem.addChildElement(jdomChildElem.getName(), jdomChildElem.getNamespacePrefix(), jdomChildElem.getNamespaceURI());

				// Build it
				buildSoapElement(envelope, soapChildElem, jdomChildElem, isRPC);
			}
		}
	}

	/**
	 * Test!!
	 *
	 * @param wsdl The WSDL to load and run!
	 */
	public static void test(String wsdl)
	{
		try
		{
			ComponentBuilder builder = new ComponentBuilder();
			List components = builder.buildComponents(wsdl);

			Iterator iter = components.iterator();

			while(iter.hasNext())
			{
				ServiceInfo info = (ServiceInfo)iter.next();
				Iterator iter2 = info.getOperations();

				while(iter2.hasNext())
				{
					OperationInfo oper = (OperationInfo)iter2.next();
					invokeOperation(oper);
				}
			}
		}

		catch(Exception e)
		{
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * Invokes an operation using SAAJ
	 *
	 * @param operation The operation to invoke
	 */
	public static void main(String[] args)
	{
		try
		{
			/*OperationInfo operation = new OperationInfo();
    	 operation.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
    	 operation.setInputMessageName("HelloWorld_sayHello");
    	 operation.setInputMessageText("<urn:sayHello xmlns:urn='urn:jbosstest'><arg0>Markus</arg0></urn:sayHello>");
    	 operation.setNamespaceURI("urn:jbosstest");
    	 operation.setOutputMessageName("HelloWorld_sayHelloResponse");
    	 operation.setOutputMessageText("<sayHello><return>0</return></sayHello>");
    	 operation.setSoapActionURI("");
    	 operation.setStyle("document");
    	 operation.setTargetMethodName("sayHello");
    	 operation.setTargetObjectURI(null);
    	 operation.setTargetURL("http://localhost:8080/HelloWorld/HelloWorld");*/

			OperationInfo operation = new OperationInfo();
			operation.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
			operation.setInputMessageName("ConversionRateSoapIn");
			operation.setInputMessageText("<ns5:ConversionRate xmlns:ns5='http://www.webserviceX.NET/'><ns5:FromCurrency>EUR</ns5:FromCurrency><ns5:ToCurrency>SKK</ns5:ToCurrency></ns5:ConversionRate>");
			operation.setNamespaceURI("http://www.webserviceX.NET/");    	 
			operation.setOutputMessageName("ConversionRateSoapOut");
			operation.setOutputMessageText("<ConversionRate><ConversionRateResult>0</ConversionRateResult></ConversionRate>");
			operation.setSoapActionURI("http://www.webserviceX.NET/ConversionRate");
			operation.setStyle("document");
			operation.setTargetMethodName("ConversionRate");
			operation.setTargetObjectURI(null);
			operation.setTargetURL("http://www.webservicex.net/CurrencyConvertor.asmx");    	 


			// Determine if the operation style is RPC
			boolean isRPC = operation.getStyle().equalsIgnoreCase("rpc");

			// All connections are created by using a connection factory
			SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();

			// Now we can create a SOAPConnection object using the connection factory
			SOAPConnection connection = conFactory.createConnection();

			// All SOAP messages are created by using a message factory
			MessageFactory msgFactory = MessageFactory.newInstance();

			// Now we can create the SOAP message object
			SOAPMessage msg = msgFactory.createMessage();

			// Get the SOAP part from the SOAP message object
			SOAPPart soapPart = msg.getSOAPPart();

			// The SOAP part object will automatically contain the SOAP envelope
			SOAPEnvelope envelope = soapPart.getEnvelope();
			//envelope.addNamespaceDeclaration("", operation.getNamespaceURI());

			if(isRPC)
			{
				// Add namespace declarations to the envelope, usually only required for RPC/encoded
				envelope.addNamespaceDeclaration(XSI_NAMESPACE_PREFIX, XSI_NAMESPACE_URI);
				envelope.addNamespaceDeclaration(XSD_NAMESPACE_PREFIX, XSD_NAMESPACE_URI);
			}

			// Get the SOAP header from the envelope
			SOAPHeader header = envelope.getHeader();         

			// The client does not yet support SOAP headers
			header.detachNode();

			// Get the SOAP body from the envelope and populate it
			SOAPBody body = envelope.getBody();

			// Create the default namespace for the SOAP body
			//body.addNamespaceDeclaration("", operation.getNamespaceURI());

			// Add the service information
			String targetObjectURI = operation.getTargetObjectURI();

			if(targetObjectURI == null)
			{
				// The target object URI should not be null
				targetObjectURI = "";
			}

			// Add the service information
			//Name svcInfo = envelope.createName(operation.getTargetMethodName(), "", targetObjectURI);
			Name svcInfo = envelope.createName(operation.getTargetMethodName(), "ns2", operation.getNamespaceURI());
			SOAPElement svcElem = body.addChildElement(svcInfo);

			if(isRPC)
			{
				// Set the encoding style of the service element
				svcElem.setEncodingStyle(operation.getEncodingStyle());
			}

			// Add the message contents to the SOAP body
			Document doc = XMLSupport.readXML(operation.getInputMessageText());

			if(doc.hasRootElement())
			{
				// Begin building content
				buildSoapElement(envelope, svcElem, doc.getRootElement(), isRPC);
			}

			//svcElem.addTextNode(operation.getInputMessageText());
			//svcElem.

			// Check for a SOAPAction
			String soapActionURI = operation.getSoapActionURI();

			if(soapActionURI != null && soapActionURI.length() > 0)
			{
				// Add the SOAPAction value as a MIME header
				MimeHeaders mimeHeaders = msg.getMimeHeaders();
				mimeHeaders.setHeader("SOAPAction", "\"" + operation.getSoapActionURI() + "\"");
			}

			// Save changes to the message we just populated
			msg.saveChanges();

			// Get ready for the invocation
			URLEndpoint endpoint = new URLEndpoint(operation.getTargetURL());

			// Show the URL endpoint message in the log
			ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
			msg.writeTo(msgStream);

			log.debug("SOAP Message MeasurementTarget URL: " + endpoint.getURL());
			log.debug("SOAP Request: " + msgStream.toString());

			// Make the call
			SOAPMessage response = connection.call(msg, endpoint);

			// Close the connection, we are done with it
			connection.close();

			// Get the content of the SOAP response
			Source responseContent = response.getSOAPPart().getContent();

			// Convert the SOAP response into a JDOM
			TransformerFactory tFact = TransformerFactory.newInstance();
			Transformer transformer = tFact.newTransformer();

			JDOMResult jdomResult = new JDOMResult();
			transformer.transform(responseContent, jdomResult);

			// Get the document created by the transform operation
			Document responseDoc = jdomResult.getDocument();

			// Send the response to the Log
			String strResponse = XMLSupport.outputString(responseDoc);
			log.debug("SOAP Response from: " + operation.getTargetMethodName() + ": " + strResponse);

			// Set the response as the output message
			operation.setOutputMessageText(strResponse);

			// Return the response generated
			//return strResponse;
		}

		catch(Throwable ex)
		{
			log.error("Error invoking operation:");
			log.error(ex.getMessage());
		}

		//return "";
	}   


}