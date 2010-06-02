package eu.planets_project.ifr.core.wee.api.wsinterface;

import java.util.ArrayList;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;

/**
 * WebService Interface for the Planets Workflow Template Regisrty. This allows 
 *  - registering new WorkflowTemplates (=defines the wf's structure and behavior)
 *  - retrieving the Java WorkflowTemplate Class by using its fully qualified name
 *  - list all registered and supported workflowTemplate QNames 
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 12.11.2008
 *
 */
@WebService(name = WftRegistryService.NAME, targetNamespace = PlanetsServices.NS)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")

public interface WftRegistryService {
	
	/** The interface name */
	public static final String NAME = "WftRegistryService";
	/** The qualified name */
	public static final QName QNAME = new QName(PlanetsServices.NS, WftRegistryService.NAME );

    /**
     * Registers a new WorkflowTemplate
     * @param QWorkflowTemplateName the fully qualified name of the submitted class. e.g. 'eu.planet_project.ifr.core.Template1.java'
     * @param javaBinary a byte[] of the java class which will be stored in the registry
     * @result
     * @throws PlanetsException if e.g. no valid fully qualified java name, not valid against interface, etc.
     */
    @WebMethod(
            operationName = WftRegistryService.NAME +"_registerWorkflowTemplate", 
            action = PlanetsServices.NS + "/" + WftRegistryService.NAME+"/registerWorkflowTemplate")
    public void registerWorkflowTemplate ( 
    		@WebParam(
                    name = "QWorkflowTemplateName", 
                    targetNamespace = PlanetsServices.NS + "/" + WftRegistryService.NAME, 
                    partName = "QWorkflowTemplateName")
            String qWorkflowTemplateName,
            @WebParam(
                    name = "javaBinary", 
                    targetNamespace = PlanetsServices.NS + "/" + WftRegistryService.NAME, 
                    partName = "javaBinary")    
            byte[] javaBinary
    ) throws PlanetsException; 
    
    /**
     * Returns a list of all fully QNames of registered WFTemplates
     * @return
     */
    @WebMethod(
            operationName = WftRegistryService.NAME +"_getAllSupportedQNames", 
            action = PlanetsServices.NS + "/" + WftRegistryService.NAME +"/getAllSupportedQNames")
    @WebResult(
            name = WftRegistryService.NAME + "QNames", 
            targetNamespace = PlanetsServices.NS + "/" + WftRegistryService.NAME, 
            partName = WftRegistryService.NAME + "QNames")
    public ArrayList<String> getAllSupportedQNames ();
    
    /**
     * Returns the source (Java Workflow Template .java file) for a registered
     * template
     * @param QWorkflowTemplateName the fully qualified name of a registered class. e.g. 'eu.planet_project.ifr.core.Template1.java'
     * @return
     * @throws PlanetsException
     */
    @WebMethod(
            operationName = WftRegistryService.NAME+ "_getWorkflowTemplate", 
            action = PlanetsServices.NS + "/" + WftRegistryService.NAME+"/getWorkflowTemplate")
    @WebResult(
            name = WftRegistryService.NAME + "Template", 
            targetNamespace = PlanetsServices.NS + "/" + WftRegistryService.NAME, 
            partName = WftRegistryService.NAME + "Template")
    public byte[] getWFTemplate(
    		@WebParam(
                     name = "QWorkflowTemplateName", 
                     targetNamespace = PlanetsServices.NS + "/" + WftRegistryService.NAME, 
                     partName = "QWorkflowTemplateName")   
             String qWorkflowTemplateName
    ) throws PlanetsException;
    
}
