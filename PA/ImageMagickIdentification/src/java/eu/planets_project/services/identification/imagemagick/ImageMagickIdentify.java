/**
 * 
 */
package eu.planets_project.services.identification.imagemagick;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.BindingType;

import org.jboss.mx.loading.UnifiedClassLoader;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * @author melmsp
 *
 */

@Local(Identify.class)
@Remote(Identify.class)
@Stateless

@WebService(name = ImageMagickIdentify.NAME, 
        serviceName = Identify.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.identify.Identify" )
        
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")        
public class ImageMagickIdentify implements Identify, Serializable {
	
	public static final String NAME = "ImageMagickIdentify";
	
	private static final long serialVersionUID = -772290809743383420L;
	
	private PlanetsLogger PLOGGER = PlanetsLogger.getLogger(this.getClass()) ;
	
	private static final String WORKFOLDER_NAME = "ImageMagickIdentify";
	private static final String DEFAULT_INPUT_NAME = "ImageMagickIdentify_input";
	private static final String DEFAULT_EXTENSION = "bin";
	private static FormatRegistry fr = null;
	
	
	
	/**
	 * Default Constructor, setting the System.property to tell Jboss to use its own Classloader...
	 */
	public ImageMagickIdentify(){
	    System.setProperty("jmagick.systemclassloader","no"); // Use the JBoss-Classloader, instead of the Systemclassloader.
	    fr = FormatRegistryFactory.getFormatRegistry();
	    PLOGGER.info("Hello! Initializing and starting ImageMagickIdentify service!");
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.services.identify.Identify#describe()
	 */
	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME, Identify.class.getCanonicalName());
        sd.description("A DigitalObject Identification Service based on ImageMagick. \n" +
        		"It returns a list of PRONOM IDs, matching for the identified file format!\n" +
        		"Please note: the first URI in the result list is a PLANETS format URI (e.g. \"planets:fmt/ext/tiff\")\n" +
        		"denoting the file format returned by ImageMagick for the file under consideration.\n" +
        		"The following URIs are the matching PRONOM IDs.");
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.classname(this.getClass().getCanonicalName());
        return sd.build();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject)
	 */
	public IdentifyResult identify(DigitalObject digitalObject) {
		
		if(digitalObject.getContent()==null) {
			PLOGGER.error("The Content of the DigitalObject should NOT be NULL! Returning with ErrorReport");
			return this.returnWithErrorMessage("The Content of the DigitalObject should NOT be NULL! Returning with ErrorReport", null);
		}
		
		String fileName = digitalObject.getTitle();
		PLOGGER.info("Input file to identify: " +fileName);
		URI inputFormat = digitalObject.getFormat();
		PLOGGER.info("Assumed file format: " + inputFormat.toASCIIString());
		String extension = null;
		
		if(inputFormat!=null) {
			extension = getFormatExtension(inputFormat);
			PLOGGER.info("Found extension for input file: " + extension);
		}
		else {
			PLOGGER.info("I am not able to find the file extension, using DEFAULT_EXTENSION instead: " + DEFAULT_EXTENSION);
			extension = DEFAULT_EXTENSION;
		}
		
		if(fileName==null || fileName.equalsIgnoreCase("")) {
			PLOGGER.info("Could not retrieve file name\n(digitalObject.getTitle() returns NULL), using DEFAULT_INPUT_NAME instead: " + DEFAULT_INPUT_NAME + "." + extension);
			fileName = DEFAULT_INPUT_NAME + "." + extension;
		}
		
		File workFolder = FileUtils.createWorkFolderInSysTemp(WORKFOLDER_NAME);
		PLOGGER.info("Created workfolder for temp files: " + workFolder.getAbsolutePath());
		
		File inputFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), workFolder, fileName);
		PLOGGER.info("Created temporary input file: " + inputFile.getAbsolutePath());
		
		ArrayList<URI> uriList = null;
		
		String srcImageFormat = null;
		
		try {
			PLOGGER.info("Initialising ImageInfo Object");
			ImageInfo imageInfo = new ImageInfo(inputFile.getAbsolutePath());
			MagickImage image = new MagickImage(imageInfo);
			
		    // Checking input image format
		    srcImageFormat = image.getMagick();
		    PLOGGER.info("The image format is: '" + srcImageFormat + "'");
		    image.destroyImages();
		    
		} catch (MagickException e) {
			PLOGGER.error("The file seems to have an unsupported file format!");
			e.getLocalizedMessage();
			e.printStackTrace();
		}
		
		Set<URI> uris = fr.getURIsForExtension(srcImageFormat);
//		List<URI> uris = fr.search(srcImageFormat);
	    
	    if(uris.size() <= 0) {
	    	PLOGGER.error("No URI returned for this extension: " + srcImageFormat + ".\n" 
	    			+ "Input file: " + inputFile.getName() + " could not be identified!!!");
	    	return this.returnWithErrorMessage("No URI returned for this extension: " + srcImageFormat + ".\n" 
	    			+ "Input file: " + inputFile.getName() + " could not be identified!!!", null);
	    }
	    
	    uriList = new ArrayList <URI> (uris);
	    
	    ServiceReport sr = new ServiceReport();
	    
	    sr.setErrorState(ServiceReport.SUCCESS);
	    URI formatURI = Format.extensionToURI(srcImageFormat);
	    uriList.add(0, formatURI);
	    String infoString = createFormatInfoString(uris);
	    sr.setInfo("Successfully identified Input file as: " + formatURI.toASCIIString() + "\n" + infoString);
	    PLOGGER.info("Successfully identified Input file as: " + formatURI.toASCIIString() + "\n" + infoString);
		
		IdentifyResult identRes = new IdentifyResult(uriList, IdentifyResult.Method.PARTIAL_PARSE, sr);
		
		PLOGGER.info("Deleting temporary files and folders...");
		FileUtils.deleteTempFiles(workFolder, PLOGGER);
		PLOGGER.info("SUCCESS! Returning IdentifyResult. Goodbye!");
		return identRes;
	}
	
	
	
	private String createFormatInfoString(Set<URI> uris) {
		StringBuffer buf = new StringBuffer();
		Format format = null;
	    buf.append("Matching PRONOM IDs for this extension type: \n");
	    for (URI uri : uris) {
	    	format = fr.getFormatForURI(uri);
	    	buf.append(uri.toASCIIString() + " (\"" + format.getSummaryAndVersion() + "\")\n");
		}
		return buf.toString();
	}
	
	
	
	private IdentifyResult returnWithErrorMessage(String message, Exception e ) {
        if( e == null ) {
            return new IdentifyResult(null, ServiceUtils.createErrorReport(message));
        } else {
            return new IdentifyResult(null, ServiceUtils.createExceptionErrorReport(message, e));
        }
    }
	
	private String getFormatExtension (URI formatURI) {
		Format f = new Format(formatURI);
		String extension = null;
		if(Format.isThisAnExtensionURI(formatURI)) {
			extension = f.getExtensions().iterator().next(); 
		}
		else {
			FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
			Format fileFormat = formatRegistry.getFormatForURI(formatURI);
			Set <String> extensions = fileFormat.getExtensions();
			if(extensions != null){
				Iterator <String> iterator = extensions.iterator();
				extension = iterator.next();
			}
		}
		return extension;
	}

}
