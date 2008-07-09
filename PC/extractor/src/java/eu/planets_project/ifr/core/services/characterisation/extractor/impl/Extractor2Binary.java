package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoBinary;


@Stateless()
@Local(BasicCharacteriseOneBinaryXCELtoBinary.class)
@Remote(BasicCharacteriseOneBinaryXCELtoBinary.class)
@LocalBinding(jndiBinding = "planets/Extractor2Binary")
@RemoteBinding(jndiBinding = "planets-project.eu/Extractor2Binary")
@WebService(
        name = "Extractor2Binary", 
//      This is not appropriate when using the endpointInterface approach.
        serviceName= BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@MTOM
public class Extractor2Binary implements BasicCharacteriseOneBinaryXCELtoBinary, Serializable {

	private static final long serialVersionUID = 3007130161689982082L;
	private final static PlanetsLogger plogger = PlanetsLogger.getLogger(Extractor2Binary.class);
	private static String CALLING_EXTRACTOR_NAME = "EXTRACTOR2BINARY";
	
    
	/**
     * 
     * @param binary a byte[] which contains the image data
     * @param xcel a String holding the Contents of a XCEL file
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException 
     */
    @WebMethod(
            operationName = BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME)
    @WebResult(
            name = BasicCharacteriseOneBinaryXCELtoBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = BasicCharacteriseOneBinaryXCELtoBinary.NAME + "Result")
    public byte[] basicCharacteriseOneBinaryXCELtoBinary ( 
    @WebParam(
            name = "binary", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = "binary")     
    byte[] binary,
    @WebParam(
            name = "XCEL_String", 
            targetNamespace = PlanetsServices.NS + "/" + BasicCharacteriseOneBinaryXCELtoBinary.NAME, 
            partName = "XCEL_String")
            String xcel
    ) throws PlanetsException {
    	
    	byte[] xcel_in = xcel.getBytes();
    	
    	CoreExtractor extractor = new CoreExtractor(CALLING_EXTRACTOR_NAME, plogger);
    	
    	byte[] outputXCDL = extractor.extractXCDL(binary, xcel_in);
    	
    	return outputXCDL;
    }
}
