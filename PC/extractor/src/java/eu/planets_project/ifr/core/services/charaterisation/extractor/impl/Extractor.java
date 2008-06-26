package eu.planets_project.ifr.core.services.charaterisation.extractor.impl;

import java.io.Serializable;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCEL;
import eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary;


@Stateless()
@Local(BasicCharacteriseOneBinaryXCEL.class)
@Remote(BasicCharacteriseOneBinaryXCEL.class)
@LocalBinding(jndiBinding = "planets/Extractor")
@RemoteBinding(jndiBinding = "planets-project.eu/Extractor")
@WebService(
        name = "Extractor", 
// This is not appropriate when using the endpointInterface approach.
//        serviceName= BasicCharacteriseOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class Extractor implements BasicCharacteriseOneBinaryXCEL, Serializable {

	private static final long serialVersionUID = -2297819079587876154L;
	private final static String logConfigFile = "eu/planets_project/ifr/core/services/characterisation/extractor/logconfig/extractor-log4j.xml";
	private PlanetsLogger plogger = PlanetsLogger.getLogger(this.getClass(), logConfigFile);
	
	
	/**
     * 
     * @param binary
     * @param xcel a String holding the Contents of a XCEL file
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicCharacteriseOneBinaryXCEL.NAME, 
            action = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME)
    @WebResult(
            name = BasicCharacteriseOneBinaryXCEL.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME, 
            partName = BasicCharacteriseOneBinaryXCEL.NAME + "Result")
    public String basicCharacteriseOneBinary ( 
    @WebParam(
            name = "binary", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME, 
            partName = "binary")     
    byte[] binary,
    
    @WebParam(
            name = "XCEL String", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCEL.NAME, 
            partName = "XCEL String")
    String xcel) throws PlanetsException {
    	ProcessRunner shell = new ProcessRunner();

    	
    	return null;
    }


}
