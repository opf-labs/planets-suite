package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;


@Stateless()
@Local(Migrate.class)
@Remote(Migrate.class)

@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = ImageMagickMigrations.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class ImageMagickMigrations implements Migrate {
	
	public static final String NAME = "ImageMagickMigrations";
	
	public static String[] compressionTypes = new String[11];
	private PlanetsLogger plogger = PlanetsLogger.getLogger(this.getClass());
	private static final String COMPRESSION_TYPE_PARAM = "compressionType";
	private static final int COMPRESSION_TYPE_PARAM_DEFAULT = 1; 
	private static final String IMAGEMAGICK_TEMP = "ImageMagickService";
	private static final String OUT_FOLDER = "output";
	private static final String INPUT_FILE_NAME = "imageMagickInput";
	private static final String OUTPUT_FILE_NAME = "imageMagickOutput";
	private static final String IMAGE_MAGICK_URI = "http://www.imagemagick.org";
	
	public ImageMagickMigrations() {
		System.setProperty("jmagick.systemclassloader","no"); // Use the JBoss-Classloader, instead of the Systemclassloader.

	    compressionTypes[0] = "Undefined Compression";
		compressionTypes[1] = "No Compression";
		compressionTypes[2] = "BZip Compression";
		compressionTypes[3] = "Fax Compression";
		compressionTypes[4] = "Group4 Compression";
		compressionTypes[5] = "JPEG Compression";
		compressionTypes[6] = "JPEG2000 Compression";
		compressionTypes[7] = "LosslessJPEG Compression";
		compressionTypes[8] = "LZW Compression";
		compressionTypes[9] = "RLE Compression";
		compressionTypes[10] = "Zip Compression";
	}

	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME,Migrate.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.description("A wrapper for ImageMagick file format conversions. Using ImageMagick v6.3.9 and JMagick v6.3.9.");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("0.1");
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("compressionType", "0"));
        parameterList.add(new Parameter("compressionType", "1"));
        parameterList.add(new Parameter("compressionType", "2"));
        parameterList.add(new Parameter("compressionType", "3"));
        parameterList.add(new Parameter("compressionType", "4"));
        parameterList.add(new Parameter("compressionType", "5"));
        parameterList.add(new Parameter("compressionType", "6"));
        parameterList.add(new Parameter("compressionType", "7"));
        parameterList.add(new Parameter("compressionType", "8"));
        parameterList.add(new Parameter("compressionType", "9"));
        parameterList.add(new Parameter("compressionType", "10"));
        
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        
        sd.parameters(parameters);
        
        try {
			sd.tool(new URI(IMAGE_MAGICK_URI));
		} catch (URISyntaxException e) {
			plogger.error("Erroneous URI: " + IMAGE_MAGICK_URI, e);
		}
        
        // Migration Paths: List all combinations:
        List<MigrationPath> paths = new ArrayList<MigrationPath>();
        
        paths.add(new MigrationPath(Format.extensionToURI("JPEG"), Format.extensionToURI("TIFF"), null));
        paths.add(new MigrationPath(Format.extensionToURI("PNG"), Format.extensionToURI("TIFF"), null));
        paths.add(new MigrationPath(Format.extensionToURI("TIFF"), Format.extensionToURI("PNG"), null));
        paths.add(new MigrationPath(Format.extensionToURI("JPEG"), Format.extensionToURI("PNG"), null));
        paths.add(new MigrationPath(Format.extensionToURI("TIFF"), Format.extensionToURI("JPEG"), null));
        paths.add(new MigrationPath(Format.extensionToURI("GIF"), Format.extensionToURI("PNG"), null));
        paths.add(new MigrationPath(Format.extensionToURI("GIF"), Format.extensionToURI("TIFF"), null));
        paths.add(new MigrationPath(Format.extensionToURI("TIFF"), Format.extensionToURI("GIF"), null));
        
        sd.paths(paths.toArray(new MigrationPath[]{}));
        
        return sd.build();
	}

	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, Parameters parameters) {
		
//		MigrateResult migrateResult = new MigrateResult();
		String inputExt = getFormatExtension(inputFormat);
		String outputExt = getFormatExtension(outputFormat);
		String inputError = null;
		String outputError = null;
		String outputFilePath = null;
		
		File imageMagickTmpFolder = FileUtils.createWorkFolderInSysTemp(IMAGEMAGICK_TEMP);
		plogger.info("Created tmp folder: " + imageMagickTmpFolder.getAbsolutePath());
		
		File outputFolder = FileUtils.createFolderInWorkFolder(imageMagickTmpFolder, OUT_FOLDER); 
		plogger.info("Created output folder: " + outputFolder.getAbsolutePath());

		// test if the wanted migrationpath is supported by this service...for the input file
		if(inputExt == null){
			plogger.error("The Format: " + inputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!");
			inputError = "The Format: " + inputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!";
		}
		
		// test if the wanted migrationpath is supported by this service...for the output file
		if(outputExt == null) {
			plogger.error("The Format: " + outputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!");
			outputError = "The Format: " + outputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!";
		}
		
		// src and target format aren't supported by this service...
		if((inputError != null) && (outputError != null)) {
			StringBuffer allError = new StringBuffer();
			allError.append(inputError);
			allError.append("\n");
			allError.append(outputError);
			return this.returnWithErrorMessage(allError.toString(), null);
		}
		
		// only the input format is not supported...
		if((inputError != null) && (outputError == null)) {
			return this.returnWithErrorMessage(inputError, null);
		}
		
		// only the target format isn't supported...
		if((inputError == null) && (outputError != null)) {
			return this.returnWithErrorMessage(outputError, null);
		}
		
		plogger.info("Getting content from DigitalObject as InputStream...");
		InputStream inputStream = digitalObject.getContent().read();
		
		plogger.info("Writing content to tmp file.");
		File inputFile = FileUtils.writeInputStreamToTmpFile(inputStream, INPUT_FILE_NAME, inputExt);
		
		plogger.info("Temp file created for input: " + inputFile.getAbsolutePath());
		
		plogger.info("Starting ImageMagick Migration from " + inputExt + " to " + outputExt + "...");
		
		plogger.debug("Initialising ImageInfo Object");
				
	    try {
			ImageInfo imageInfo = new ImageInfo(inputFile.getAbsolutePath());
			MagickImage image = new MagickImage(imageInfo);
			plogger.info("ImageInfo object created for file: " + inputFile.getAbsolutePath());
			String actualSrcFormat = image.getMagick();
			plogger.info("ImageMagickMigrations: Given src file format extension is: " + inputExt);
			plogger.info("ImageMagickMigrations: Actual src format is: " + actualSrcFormat);
			
			// Has the input file the format it claims it has?
			if(compareExtensions(inputExt, actualSrcFormat) == false) {
				// if NOT just return without doing anything...
				return this.returnWithErrorMessage("The passed input file format does not match the actual format of the file!\n" +
						"This could cause unpredictable behaviour. Nothing has been done!", null);
			}
			
			// Are there any additional parameters for us?
			if(parameters != null) {
				plogger.info("Got additional parameters:");
				int compressionType;
				List<Parameter> parameterList = parameters.getParameters();
				for (Iterator<Parameter> iterator = parameterList.iterator(); iterator.hasNext();) {
					Parameter parameter = (Parameter) iterator.next();
					String name = parameter.name;
					plogger.info("Got parameter: " + name + " with value: " + parameter.value);
					
					if(name.equalsIgnoreCase(COMPRESSION_TYPE_PARAM)) {
						compressionType = Integer.parseInt(parameter.value);
						if(compressionType >= 0 && compressionType <= 10) {
							plogger.info("Trying to set Compression type to: " + compressionTypes[compressionType]);
							image.setCompression(compressionType);
						}
						else {
							plogger.info("Invalid value for Compression type: " + parameter.value);
							plogger.info("Setting Compression Type to Default value: " + COMPRESSION_TYPE_PARAM_DEFAULT + compressionTypes[COMPRESSION_TYPE_PARAM_DEFAULT]);
							image.setCompression(COMPRESSION_TYPE_PARAM_DEFAULT);
						}
					}
					else {
						plogger.info("Invalid parameter with name: " + parameter.name);
						plogger.info("Setting Compression Type to Default value: " + COMPRESSION_TYPE_PARAM_DEFAULT + compressionTypes[COMPRESSION_TYPE_PARAM_DEFAULT]);
						image.setCompression(COMPRESSION_TYPE_PARAM_DEFAULT);
					}
				}
			}
			else {
				image.setCompression(COMPRESSION_TYPE_PARAM_DEFAULT);
			}
			
			image.setMagick(outputExt);
			plogger.info("Setting new file format for output file to: " + outputExt);
			
			outputFilePath = outputFolder.getAbsolutePath() + File.separator + OUTPUT_FILE_NAME + "." + outputExt;
			plogger.info("Starting to write result file to: " + outputFilePath);
			
			image.setFileName(outputFilePath);
			image.writeImage(imageInfo);
			plogger.info("Successfully created result file at: " + outputFilePath);
			
		} catch (MagickException e) {
			return this.returnWithErrorMessage("Something went terribly wrong with ImageMagick: ", e);
		}
		
		byte[] bytes = ByteArrayHelper.read(new File(outputFilePath));
		plogger.info("Created byte[] from result file...");
		
		DigitalObject newDigObj = this.createDigitalObject(null, bytes);
		plogger.info("Created new DigitalObject for result file...");
		
		ServiceReport report = new ServiceReport();
		report.setErrorState(0);
		plogger.info("Created Service report...");
		plogger.info("Success!! Returning results!");
		
		FileUtils.deleteTempFiles(imageMagickTmpFolder, plogger);
		
		return new MigrateResult(newDigObj, report);
	}
	
	
	
	private MigrateResult returnWithErrorMessage(String message, Exception e ) {
        if( e == null ) {
            return new MigrateResult(null, ServiceUtils.createErrorReport(message));
        } else {
            return new MigrateResult(null, ServiceUtils.createExceptionErrorReport(message, e));
        }
    }
	
	
	private DigitalObject createDigitalObject(URL permanentURL, byte[] resultFileBlob) {
		DigitalObject digObj =  new DigitalObject.Builder(null, Content.byValue(resultFileBlob)).build();
		return digObj;
	}
	
	private boolean compareExtensions (String extension1, String extension2) {
		plogger.info("Starting to compare these two extensions: " + extension1 + " and " + extension2);
		FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
		
		Set <URI> ext1FormatURIs = formatRegistry.getURIsForExtension(extension1.toLowerCase());
		plogger.info("Got list of URIs for " + extension1);
		for (Iterator iterator = ext1FormatURIs.iterator(); iterator.hasNext();) {
			URI uri = (URI) iterator.next();
			plogger.info("Got: " + uri.toASCIIString());
		}
		
		Set <URI> ext2FormatURIs = formatRegistry.getURIsForExtension(extension2.toLowerCase());
		plogger.info("Got list of URIs for " + extension2);
		for (Iterator iterator = ext2FormatURIs.iterator(); iterator.hasNext();) {
			URI uri = (URI) iterator.next();
			plogger.info("Got: " + uri.toASCIIString());
		}
		
		boolean success = false;
		
		plogger.info("Trying to match URIs...");
		for(URI currentUri: ext1FormatURIs) {
			plogger.info("current URI: " + currentUri.toASCIIString());
			if(ext2FormatURIs.contains(currentUri)) {
				success = true;
				break;
			}
			else {
				success = false;
			}
		}
		if(success) {
			plogger.info("Success!");
		}
		else {
			plogger.info("No success.");
		}
		
		return success;
	}
	
	private String getFormatExtension (URI formatURI) {
		plogger.info("Getting extension for given format URI: " + formatURI.toASCIIString());
		Format f = new Format(formatURI);
		String extension = null;
		if(Format.isThisAnExtensionURI(formatURI)) {
			plogger.info("URI is an Extension-URI.");
			extension = f.getExtensions().iterator().next(); 
			plogger.info("Got Extension for format URI: " + formatURI.toASCIIString() + "--> " + extension );
		}
		else {
			plogger.info("URI is of another supported type.");
			FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
			Format fileFormat = formatRegistry.getFormatForURI(formatURI);
			Set <String> extensions = fileFormat.getExtensions();
			if(extensions != null){
				Iterator <String> iterator = extensions.iterator();
				extension = iterator.next();
				plogger.info("Got Extension for format URI: " + formatURI.toASCIIString() + "--> " + extension );
			}
		}
		return extension;
	}
	

}
