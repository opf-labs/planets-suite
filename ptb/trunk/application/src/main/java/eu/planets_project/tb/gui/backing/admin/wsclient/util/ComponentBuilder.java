package eu.planets_project.tb.gui.backing.admin.wsclient.util;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOutput;
import javax.xml.namespace.QName;
import javax.wsdl.Operation;
import javax.wsdl.Input;
import javax.wsdl.Output;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPBinding;

import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.input.DOMBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.XMLType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Structure;
import org.exolab.castor.xml.schema.SimpleTypesFactory;



//import com.ibm.wsdl.extensions.schema.SchemaImpl;


/**
 * This class defines methods for building components to invoke a web service
 * by analyzing a WSDL contract document.
 *
 * @author Markus Reis, ARC
 */

public class ComponentBuilder
{

	private Log log = LogFactory.getLog(ComponentBuilder.class);
	
	/** JWSDL Factory instance */
	WSDLFactory wsdlFactory = null;

	/** Cator simple types factory */
	SimpleTypesFactory simpleTypesFactory = null;

	/** WSDL type schema */
	private Schema wsdlTypes = null;

	/** The default SOAP encoding to use. */
	public final static String DEFAULT_SOAP_ENCODING_STYLE = "http://schemas.xmlsoap.org/soap/encoding/";

	/**
	 * Constructor
	 */
	public ComponentBuilder()
	{
		try
		{
			wsdlFactory = WSDLFactory.newInstance();
			simpleTypesFactory = new SimpleTypesFactory();
		}

		catch(Throwable t)
		{
			log.error(t.getMessage());
		}
	}

	/**
	 * Builds a List of ServiceInfo components for each Service defined in a WSDL Document
	 *
	 * @param wsdlURI A URI that points to a WSDL XML Definition. Can be a filename or URL.
	 *
	 * @return A List of SoapComponent objects populated for each service defined
	 *         in a WSDL document. A null is returned if the document can't be read.
	 */
	public List buildComponents(String wsdlURI) throws Exception
	{
		// The list of components that will be returned
		List serviceList = Collections.synchronizedList(new ArrayList());
		// Create the WSDL Reader object
		WSDLReader reader = wsdlFactory.newWSDLReader();
		// Read the WSDL and get the top-level Definition object
		Definition def = reader.readWSDL(null, wsdlURI);
		// Create a castor schema from the types element defined in WSDL
		// This method will return null if there are types defined in the WSDL
		wsdlTypes = createSchemaFromTypes(def);
		// Get the services defined in the document
		Map services = def.getServices();
		if(services != null)
		{
			// Create a component for each service defined
			Iterator svcIter = services.values().iterator();

			for(int i = 0; svcIter.hasNext(); i++)
			{
				// Create a new ServiceInfo component for each service found
				ServiceInfo serviceInfo = new ServiceInfo();

				// Populate the new component from the WSDL Definition read
				populateComponent(serviceInfo, (Service)svcIter.next());

				// Add the new component to the List to be returned
				serviceList.add(serviceInfo);
			}
		}
		// return the List of services we created
		return serviceList;
	}

	/**
	 * Creates a castor schema based on the types defined by a WSDL document
	 *
	 * @param   wsdlDefinition    The WSDL4J instance of a WSDL definition.
	 *
	 * @return  A castor schema is returned if the WSDL definition contains
	 *          a types element. null is returned otherwise.
	 */
	protected Schema createSchemaFromTypes(Definition wsdlDefinition)
	{
		// Get the schema element from the WSDL definition
		org.w3c.dom.Element schemaElement = null;

		if(wsdlDefinition.getTypes() != null)
		{
			ExtensibilityElement schemaExtElem = findExtensibilityElement(wsdlDefinition.getTypes().getExtensibilityElements(), "schema");

			log.debug(schemaExtElem.getClass().getCanonicalName());

			//if(schemaExtElem != null && schemaExtElem instanceof UnknownExtensibilityElement)
			if(schemaExtElem != null && schemaExtElem instanceof javax.wsdl.extensions.schema.Schema)
			{
				schemaElement = ((javax.wsdl.extensions.schema.Schema)schemaExtElem).getElement();
			}

			/*if(schemaExtElem != null && schemaExtElem instanceof ExtensibilityElement)
         {
            schemaElement = ((UnknownExtensibilityElement)schemaExtElem).getElement();
         } */        
		}

		if(schemaElement == null)
		{
			// No schema to read
			log.error("Unable to find schema extensibility element in WSDL");
			return null;
		}

		// Convert from DOM to JDOM
		DOMBuilder domBuilder = new DOMBuilder();
		org.jdom.Element jdomSchemaElement = domBuilder.build(schemaElement);;

		if(jdomSchemaElement == null)
		{
			log.error("Unable to read schema defined in WSDL");
			return null;
		}

		// Add namespaces from the WSDL
		Map namespaces = wsdlDefinition.getNamespaces();

		if(namespaces != null && !namespaces.isEmpty())
		{
			Iterator nsIter = namespaces.keySet().iterator();

			while(nsIter.hasNext())
			{
				String nsPrefix = (String)nsIter.next();
				String nsURI = (String)namespaces.get(nsPrefix);

				if(nsPrefix != null && nsPrefix.length() > 0)
				{
					org.jdom.Namespace nsDecl = org.jdom.Namespace.getNamespace(nsPrefix, nsURI);
					jdomSchemaElement.addNamespaceDeclaration(nsDecl);
				}
			}
		}

		// Make sure that the types element is not processed
		jdomSchemaElement.detach();

		// Convert it into a Castor schema instance
		Schema schema = null;

		try
		{
			schema = XMLSupport.convertElementToSchema(jdomSchemaElement);
		}

		catch(Exception e)
		{
			log.error(e.getMessage());
		}

		// Return it
		return schema;
	}

	/**
	 * Populates a ServiceInfo instance from the specified Service definiition
	 *
	 * @param   component      The component to populate
	 * @param   service        The Service to populate from
	 *
	 * @return The populated component is returned representing the Service parameter
	 */
	private ServiceInfo populateComponent(ServiceInfo component, Service service)
	{
		// Get the qualified service name information
		QName qName = service.getQName();

		// Get the service's namespace URI
		String namespace = qName.getNamespaceURI();

		// Use the local part of the qualified name for the component's name
		String name = qName.getLocalPart();

		// Set the name
		component.setName(name);
		// Get the defined ports for this service
		Map ports = service.getPorts();

		// Use the Ports to create OperationInfos for all request/response messages defined
		Iterator portIter = ports.values().iterator();

		while(portIter.hasNext())
		{
			// Get the next defined port
			Port port = (Port)portIter.next();

			// Get the Port's Binding
			Binding binding = port.getBinding();

			// Now we will create operations from the Binding information
			List operations = buildOperations(binding);

			// Process objects built from the binding information
			Iterator operIter = operations.iterator();

			while(operIter.hasNext())
			{
				OperationInfo operation = (OperationInfo)operIter.next();

				// Set the namespace URI for the operation.
				operation.setNamespaceURI(namespace);

				// Find the SOAP target URL
				ExtensibilityElement addrElem = findExtensibilityElement(port.getExtensibilityElements(), "address");

				if(addrElem != null && addrElem instanceof SOAPAddress)
				{
					// Set the SOAP target URL
					SOAPAddress soapAddr = (SOAPAddress)addrElem;
					operation.setTargetURL(soapAddr.getLocationURI());
				}

				// Add the operation info to the component
				component.addOperation(operation);
			}
		}

		return component;
	}

	/**
	 * Creates Info objects for each Binding Operation defined in a Port Binding
	 *
	 * @param binding The Binding that defines Binding Operations used to build info objects from
	 *
	 * @return A List of built and populated OperationInfos is returned for each Binding Operation
	 */
	private List buildOperations(Binding binding)
	{
		// Create the array of info objects to be returned
		List operationInfos = new ArrayList();

		// Get the list of Binding Operations from the passed binding
		List operations = binding.getBindingOperations();

		if(operations != null && !operations.isEmpty())
		{
			// Determine encoding
			ExtensibilityElement soapBindingElem = findExtensibilityElement(binding.getExtensibilityElements(), "binding");
			String style = "document"; // default

			if(soapBindingElem != null && soapBindingElem instanceof SOAPBinding)
			{
				SOAPBinding soapBinding = (SOAPBinding)soapBindingElem;
				style = soapBinding.getStyle();
			}

			// For each binding operation, create a new OperationInfo
			Iterator opIter = operations.iterator();
			int i = 0;

			while(opIter.hasNext())
			{
				BindingOperation oper = (BindingOperation)opIter.next();

				// We currently only support soap:operation bindings
				// filter out http:operations for now until we can dispatch them properly
				ExtensibilityElement operElem = findExtensibilityElement(oper.getExtensibilityElements(), "operation");

				if(operElem != null && operElem instanceof SOAPOperation)
				{
					// Create a new operation info
					OperationInfo operationInfo = new OperationInfo(style);

					// Populate it from the Binding Operation
					buildOperation(operationInfo, oper);

					// Add to the return list
					operationInfos.add(operationInfo);
				}
			}
		}

		return operationInfos;
	}

	/**
	 * Populates an OperationInfo from the specified Binding Operation
	 *
	 * @param   operationInfo      The component to populate
	 * @param   bindingOper        A Binding Operation to define the OperationInfo from
	 *
	 * @return The populated OperationInfo object is returned.
	 */
	private OperationInfo buildOperation(OperationInfo operationInfo, BindingOperation bindingOper)
	{
		// Get the operation
		Operation oper = bindingOper.getOperation();

		// Set the name using the operation name
		operationInfo.setTargetMethodName(oper.getName());

		// Set the action URI
		ExtensibilityElement operElem = findExtensibilityElement(bindingOper.getExtensibilityElements(), "operation");

		if(operElem != null && operElem instanceof SOAPOperation)
		{
			SOAPOperation soapOperation = (SOAPOperation)operElem;
			operationInfo.setSoapActionURI(soapOperation.getSoapActionURI());
		}

		// Get the Binding Input
		BindingInput bindingInput = bindingOper.getBindingInput();

		// Get the Binding Output
		BindingOutput bindingOutput = bindingOper.getBindingOutput();

		// Get the SOAP Body
		ExtensibilityElement bodyElem = findExtensibilityElement(bindingInput.getExtensibilityElements(), "body");

		if(bodyElem != null && bodyElem instanceof SOAPBody)
		{
			SOAPBody soapBody = (SOAPBody)bodyElem;

			// The SOAP Body contains the encoding styles
			List styles = soapBody.getEncodingStyles();
			String encodingStyle = null;

			if(styles != null)
			{
				// Use the first in the list
				encodingStyle = styles.get(0).toString();
			}

			if(encodingStyle == null)
			{
				// An ecoding style was not found, give it a default
				encodingStyle = DEFAULT_SOAP_ENCODING_STYLE;
			}

			// Assign the encoding style value
			operationInfo.setEncodingStyle(encodingStyle.toString());

			// The SOAP Body contains the target object's namespace URI.
			operationInfo.setTargetObjectURI(soapBody.getNamespaceURI());
		}

		// Get the Operation's Input definition
		Input inDef = oper.getInput();

		if(inDef != null)
		{
			// Build input parameters
			Message inMsg = inDef.getMessage();

			if(inMsg != null)
			{
				// Set the name of the operation's input message
				operationInfo.setInputMessageName(inMsg.getQName().getLocalPart());

				// Set the body of the operation's input message
				operationInfo.setInputMessageText(buildMessageText(operationInfo, inMsg));
			}
		}

		// Get the Operation's Output definition
		Output outDef = oper.getOutput();

		if(outDef != null)
		{
			// Build output parameters
			Message outMsg = outDef.getMessage();

			if(outMsg != null)
			{
				// Set the name of the output message
				operationInfo.setOutputMessageName(outMsg.getQName().getLocalPart());

				// Set the body of the operation's output message
				operationInfo.setOutputMessageText(buildMessageText(operationInfo, outMsg));
			}
		}

		// Finished, return the populated object
		return operationInfo;
	}

	/**
	 * Builds and adds parameters to the supplied info object
	 * given a SOAP Message definition (from WSDL)
	 *
	 * @param   operationInfo   The component to build message text for
	 * @param   msg    The SOAP Message definition that has parts to defined parameters for
	 */
	private String buildMessageText(OperationInfo operationInfo, Message msg)
	{
		// Create the root message element
		Element rootElem = null;
		if (msg.getQName() != null) {
			if (msg.getQName().getNamespaceURI() != null) {
				if (msg.getQName().getNamespaceURI().length() > 0) {
					rootElem = new Element(operationInfo.getTargetMethodName(), "ns1", msg.getQName().getNamespaceURI());
				}
				else 
					new Element(operationInfo.getTargetMethodName());
			}
			else 
				new Element(operationInfo.getTargetMethodName());
		}
		else 
			new Element(operationInfo.getTargetMethodName());

		// Get the message parts
		List msgParts = msg.getOrderedParts(null);

		// Process each part
		Iterator iter = msgParts.iterator();

		while(iter.hasNext())
		{
			// Get each part
			Part part = (Part)iter.next();

			// Add content for each message part
			String partName = part.getName();

			if(partName != null)
			{
				// Determine if this message part's type is complex
				XMLType xmlType = getXMLType(part);

				if(xmlType != null && xmlType.isComplexType())
				{
					// Build the message structure
					buildComplexPart((ComplexType)xmlType, rootElem);
				}
				else
				{
					// Build the element that will be added to the message
					Element partElem;
					if (part.getElementName() != null) {
						if ((part.getElementName().getLocalPart() != null) && (part.getElementName().getNamespaceURI() != null))
							partElem = new Element(part.getElementName().getLocalPart(), part.getElementName().getNamespaceURI());
						else 
							partElem = new Element(partName);
					}
					else 
						partElem = new Element(partName);

					// Add some default content as just a place holder
					partElem.addContent("?");

					if(operationInfo.getStyle().equalsIgnoreCase("rpc"))
					{
						// If this is an RPC style operation, we need to include some type information
						partElem.setAttribute("type", part.getTypeName().getLocalPart());
					}

					// Add this message part
					rootElem.addContent(partElem);
				}
			}
		}

		return XMLSupport.outputString(rootElem);
	}

	/**
	 * Populate a JDOM element using the complex XML type passed in
	 *
	 * @param   complexType  The complex XML type to build the element for
	 * @param   partElem     The JDOM element to build content for
	 */
	protected void buildComplexPart(ComplexType complexType, Element partElem)
	{
		// Find the group
		Enumeration particleEnum = complexType.enumerate();
		Group group = null;

		while(particleEnum.hasMoreElements())
		{
			Particle particle = (Particle)particleEnum.nextElement();

			if (particle instanceof Group)
			{
				group = (Group)particle;
				break;
			}
		}

		if (group != null)
		{
			Enumeration groupEnum = group.enumerate();

			while (groupEnum.hasMoreElements())
			{
				Structure item = (Structure)groupEnum.nextElement();

				if (item.getStructureType() == Structure.ELEMENT)
				{
					ElementDecl elementDecl = (ElementDecl)item;
					Element childElem;
					if (elementDecl.getSchema() != null) {
						if (elementDecl.getSchema().getElementFormDefault() != null) {
							if (elementDecl.getSchema().getElementFormDefault().isQualified()) {
								childElem = new Element(elementDecl.getName(), "ns1", partElem.getNamespaceURI());
							}
							else 
								childElem = new Element(elementDecl.getName());            		   
						}
						else 
							childElem = new Element(elementDecl.getName());
					}
					else 
						childElem = new Element(elementDecl.getName());


					XMLType xmlType = elementDecl.getType();

					if(xmlType != null && xmlType.isComplexType())
					{
						buildComplexPart((ComplexType)xmlType, childElem);
					}
					else
					{
						childElem.addContent("?");
					}

					partElem.addContent(childElem);
				}
			}
		}
	}

	/**
	 * Gets an XML Type from a SOAP Message Part read from WSDL
	 *
	 * @param   part     The SOAP Message part
	 *
	 * @return The corresponding XML Type is returned.
	 *         null is returned if not found or if a simple type
	 */
	protected XMLType getXMLType(Part part)
	{
		if(wsdlTypes == null)
		{
			// No defined types, Nothing to do
			return null;
		}

		// Find the XML type
		XMLType xmlType = null;

		// First see if there is a defined element
		if(part.getElementName() != null)
		{
			// Get the element name
			String elemName = part.getElementName().getLocalPart();

			// Find the element declaration
			ElementDecl elemDecl = wsdlTypes.getElementDecl(elemName);

			if(elemDecl != null)
			{
				// From the element declaration get the XML type
				xmlType = elemDecl.getType();
			}
		}

		return xmlType;
	}

	/**
	 * Returns the desired ExtensibilityElement if found in the List
	 *
	 * @param   extensibilityElements   The list of extensibility elements to search
	 * @param   elementType             The element type to find
	 *
	 * @return  Returns the first matching element of type found in the list
	 */
	private static ExtensibilityElement findExtensibilityElement(List extensibilityElements, String elementType)
	{
		if(extensibilityElements != null)
		{
			Iterator iter = extensibilityElements.iterator();

			while(iter.hasNext())
			{
				ExtensibilityElement element = (ExtensibilityElement)iter.next();

				if(element.getElementType().getLocalPart().equalsIgnoreCase(elementType))
				{
					// Found it
					return element;
				}
			}
		}

		return null;
	}
}
