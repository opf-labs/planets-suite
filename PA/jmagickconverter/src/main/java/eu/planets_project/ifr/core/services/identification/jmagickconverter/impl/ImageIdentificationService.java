package eu.planets_project.ifr.core.services.identification.jmagickconverter.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.identify.BasicIdentifyOneBinary;
import eu.planets_project.services.utils.PlanetsLogger;

/**
 * The purpose of this class is to identify the format of a given image, passed as byte[].
 * It delivers a planets-Format-URI, which is composed like this:
 * planets://type/extension/[Format Identifier, e.g. TIFF / JPEG / PNG ...]  
 * 
 * This Class is a Webservice realised using an EJB3.0 Bean.
 *  
 * @author : Peter Melms
 * Email : peter.melms@uni-koeln.de 
 * Created : 27.05.2008
 */
@Stateless()
@Local(BasicIdentifyOneBinary.class)
@Remote(BasicIdentifyOneBinary.class)
@LocalBinding(jndiBinding = "planets/ImageIdentificationService")
@RemoteBinding(jndiBinding = "planets-project.eu/ImageIdentificationService")
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = "ImageIdentificationService", 
        serviceName = BasicIdentifyOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
public class ImageIdentificationService implements Serializable, BasicIdentifyOneBinary {

	// Default Constructor, setting the System.property to tell Jboss to use its own Classloader...
	public ImageIdentificationService(){
	    System.setProperty("jmagick.systemclassloader","no"); // Use the JBoss-Classloader, instead of the Systemclassloader.
	}
	
    private static final long serialVersionUID = -8344078893579549092L;
    // Creating a PlanetsLogger...
    private final static String logConfigFile = "eu/planets_project/ifr/core/services/identification/jmagickconverter/logconfig/imageidentificationservice-log4j.xml";
    private PlanetsLogger plogger = PlanetsLogger.getLogger(this.getClass(), logConfigFile);
    

    @WebMethod(
            operationName = BasicIdentifyOneBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicIdentifyOneBinary.NAME)
    @WebResult(
            name = BasicIdentifyOneBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicIdentifyOneBinary.NAME, 
            partName = BasicIdentifyOneBinary.NAME + "Result")
    public URI basicIdentifyOneBinary ( 
            @WebParam(name = "binary", targetNamespace = PlanetsServices.NS + "/" + BasicIdentifyOneBinary.NAME, partName = "binary")
            byte[] binary ) throws PlanetsException {
    		
    		URI planetsFormatURI = null;
			
			try {
				plogger.debug("Initialising ImageInfo Object");
			    ImageInfo mInfo;
				mInfo = new ImageInfo();
				MagickImage image = new MagickImage();
			    // Creating an MagickImage object from "srcFileData" byte[].
			    plogger.debug("Create image object from blob");
			    image.blobToImage(mInfo, binary);
			    // Reading src-image format
			    String srcImageFormat = image.getMagick();
			    plogger.debug("The image format is: '" + srcImageFormat + "'");
			    planetsFormatURI = createPlanetsFormatURI(srcImageFormat);
			    plogger.debug("The resulting planets type URI is: " + planetsFormatURI.toASCIIString());
			    image.destroyImages();
			} catch (MagickException e) {
				plogger.error("The file seems to have an unsupported file format!");
				e.getLocalizedMessage();
				e.printStackTrace();
			}
			return planetsFormatURI;
    }


	private URI createPlanetsFormatURI(String srcImageFormat) {
		URI newURI = null;
		String uriPrefix = "planets://type/extension/";
		try {
			newURI = new URI(uriPrefix + srcImageFormat);
		} catch (URISyntaxException e) {
			plogger.error("Malformed URI, see StackTrace for Details: ");
			e.getLocalizedMessage();
			e.printStackTrace();
		}
		return newURI;
	}
}
