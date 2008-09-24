package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.Serializable;
import java.net.URI;

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
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;
import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;

@Stateless()
@Local(BasicCharacteriseOneBinaryXCELtoURI.class)
@Remote(BasicCharacteriseOneBinaryXCELtoURI.class)
@LocalBinding(jndiBinding = "planets/Extractor2URI")
@RemoteBinding(jndiBinding = "planets-project.eu/Extractor2URI")
@WebService(name = "Extractor2URI",
// This is not appropriate when using the endpointInterface approach.
serviceName = BasicCharacteriseOneBinaryXCELtoURI.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@MTOM
public class Extractor2URI implements BasicCharacteriseOneBinaryXCELtoURI,
        Serializable {

    private static final long serialVersionUID = 3007130161689982082L;
    private final static PlanetsLogger plogger = PlanetsLogger
            .getLogger(Extractor2URI.class);
    private static final String SINGLE = "JustBinary";
    private static String CALLING_EXTRACTOR_NAME = "EXTRACTOR2URI";
    private static String EXTRACTOR_DR_OUT = CALLING_EXTRACTOR_NAME + "_OUT";
    private static String OUTPUTFILE_NAME = "extractor2uri_xcdl_out.xcdl";

    public Extractor2URI() {

    }

    /**
     * 
     * @param binary a byte[] which contains the image data
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException
     */
    @WebMethod(operationName = BasicCharacteriseOneBinaryXCELtoURI.NAME
            + SINGLE, action = PlanetsServices.NS + "/"
            + BasicCharacteriseOneBinaryXCELtoURI.NAME)
    @WebResult(name = BasicCharacteriseOneBinaryXCELtoURI.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = BasicCharacteriseOneBinaryXCELtoURI.NAME
            + "Result")
    public URI basicCharacteriseOneBinaryXCELtoURI(
            @WebParam(name = "input_image_URI", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = "input_image_URI") URI inputImageURI)
            throws PlanetsException {
        return basicCharacteriseOneBinaryXCELtoURI(inputImageURI, null);
    }

    /**
     * 
     * @param binary a byte[] which contains the image data
     * @param xcel a String holding the Contents of a XCEL file
     * @return a String holding the contents of a XCDL file
     * @throws PlanetsException
     */
    @WebMethod(operationName = BasicCharacteriseOneBinaryXCELtoURI.NAME, action = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME)
    @WebResult(name = BasicCharacteriseOneBinaryXCELtoURI.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = BasicCharacteriseOneBinaryXCELtoURI.NAME
            + "Result")
    public URI basicCharacteriseOneBinaryXCELtoURI(
            @WebParam(name = "input_image_URI", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = "input_image_URI") URI inputImageURI,
            @WebParam(name = "input_xcel_URI", targetNamespace = PlanetsServices.NS
                    + "/" + BasicCharacteriseOneBinaryXCELtoURI.NAME, partName = "input_xcel_URI") URI inputXcelURI)
            throws PlanetsException {

        byte[] input_image = new DataRegistryAccessHelper().read(inputImageURI
                .toASCIIString());
        // byte[] input_xcel = getBinaryFromDataRegistry(inputXcelURI
        // .toASCIIString());

        CoreExtractor extractor = new CoreExtractor(CALLING_EXTRACTOR_NAME,
                plogger);

        String inputURIString = inputImageURI.toASCIIString(); 
        String[] fileNameTokens = inputURIString.split("/");
        
        
        // Creating the Outputfilename by using the inputImage Name and appending "OUTPUT_" [filename] ".xcdl"
        // So the Outputfile could be related to its inputfile, when more files are stored in the DataRegistry.
    	OUTPUTFILE_NAME = "OUTPUT_" + fileNameTokens[fileNameTokens.length-1].concat(".xcdl");
        
        byte[] outputXCDL = extractor.extractXCDL(input_image,
                inputXcelURI != null ? new DataRegistryAccessHelper()
                        .read(inputXcelURI.toASCIIString()) : null);

        URI outputFileURI = null;
        outputFileURI = new DataRegistryAccessHelper().write(outputXCDL,
                OUTPUTFILE_NAME, EXTRACTOR_DR_OUT);
        return outputFileURI;
    }
}
