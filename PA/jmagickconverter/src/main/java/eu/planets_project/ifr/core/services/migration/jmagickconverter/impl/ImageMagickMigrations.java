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
	private static final String COMPRESSION_TYPE = "compressionType";
	private static final int COMPRESSION_TYPE_PARAM_DEFAULT = 1; 
	private static final String COMPRESSION_QUALITY_LEVEL = "compressionQuality";
	private static final int COMPRESSION_QUALITY_LEVEL_DEFAULT = 100;
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
        sd.description("A wrapper for ImageMagick file format conversions. Using ImageMagick v6.3.9 and JMagick v6.3.9.\n" +
        		"This service accepts input and target formats of this shape: 'planets:fmt/ext/[extension]'\n" +
        		"e.g. 'planets:fmt/ext/tiff' or 'planets:fmt/ext/tif'");
        
        sd.classname(this.getClass().getCanonicalName());
        sd.version("0.1");
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter compressionTypeParam = new Parameter("compressionType", "0-10");
        compressionTypeParam.setDescription("Allowed int values: 0 - 10");
        parameterList.add(compressionTypeParam);
        
        Parameter compressionLevelParam = new Parameter("compressionQuality", "0-100");
        compressionLevelParam.setDescription("This should be an int value between: 0 - 100, representing the compression quality in percent.");
        parameterList.add(compressionLevelParam);
        
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        
        sd.parameters(parameters);
        
        try {
			sd.tool(new URI(IMAGE_MAGICK_URI));
		} catch (URISyntaxException e) {
			plogger.error("Erroneous URI: " + IMAGE_MAGICK_URI, e);
		}
        
		// Migration Paths: List all combinations:
		List<String> inputFormats = new ArrayList<String> ();
		
		inputFormats.add("JPEG");
        inputFormats.add("JP2");
		inputFormats.add("TIFF");
		inputFormats.add("GIF");
		inputFormats.add("PNG");
		inputFormats.add("BMP");
//		inputFormats.add("RAW");
		inputFormats.add("PCX");
		inputFormats.add("TGA");
		inputFormats.add("PDF");
		
		List<String> outputFormats = new ArrayList<String> ();
		
		outputFormats.add("TIFF");
		outputFormats.add("PNG");
        outputFormats.add("JPEG");
        outputFormats.add("JP2");
		outputFormats.add("GIF");
		outputFormats.add("PDF");
//		outputFormats.add("RAW");
		outputFormats.add("PCX");
		outputFormats.add("TGA");
		outputFormats.add("BMP");
        
        sd.paths(createMigrationPathwayMatrix(inputFormats, outputFormats));
        
        return sd.build();
	}
	
	private MigrationPath[] createMigrationPathwayMatrix (List<String> inputFormats, List<String> outputFormats) {
		List<MigrationPath> paths = new ArrayList<MigrationPath>();

		for (Iterator<String> iterator = inputFormats.iterator(); iterator.hasNext();) {
			String input = iterator.next();
			
			for (Iterator<String> iterator2 = outputFormats.iterator(); iterator2.hasNext();) {
				String output = iterator2.next();
				MigrationPath path = new MigrationPath(Format.extensionToURI(input), Format.extensionToURI(output), null);
				// Debug...
//				System.out.println(path.getInputFormat() + " --> " + path.getOutputFormat());
				paths.add(path);
			}
			
		}
		
		return paths.toArray(new MigrationPath[]{});
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
				int compressionQuality; 
				List<Parameter> parameterList = parameters.getParameters();
				for (Iterator<Parameter> iterator = parameterList.iterator(); iterator.hasNext();) {
					Parameter parameter = (Parameter) iterator.next();
					String name = parameter.name;
					plogger.info("Got parameter: " + name + " with value: " + parameter.value);
					if(!name.equalsIgnoreCase(COMPRESSION_QUALITY_LEVEL) && !name.equalsIgnoreCase(COMPRESSION_TYPE)) {
						plogger.info("Invalid parameter with name: " + parameter.name);
						
						plogger.info("Setting compressionQualilty to Default value: " + COMPRESSION_QUALITY_LEVEL_DEFAULT);
						imageInfo.setQuality(COMPRESSION_QUALITY_LEVEL_DEFAULT);
						
						plogger.info("Setting Compression Type to Default value: " + COMPRESSION_TYPE_PARAM_DEFAULT + compressionTypes[COMPRESSION_TYPE_PARAM_DEFAULT]);
						image.setCompression(COMPRESSION_TYPE_PARAM_DEFAULT);
					}
					
					
					if(name.equalsIgnoreCase(COMPRESSION_QUALITY_LEVEL)) {
						compressionQuality = Integer.parseInt(parameter.value);
						if(compressionQuality >=0 && compressionQuality <=100) {
							plogger.info("Setting compressionQualilty to: " + compressionQuality);
							imageInfo.setQuality(compressionQuality);
						}
						else {
							plogger.info("Invalid value for compressionQualilty: " + parameter.value);
							plogger.info("Setting compressionQualilty to Default value: " + COMPRESSION_QUALITY_LEVEL_DEFAULT);
							imageInfo.setQuality(COMPRESSION_QUALITY_LEVEL_DEFAULT);
						}
						
					}
					
					if(name.equalsIgnoreCase(COMPRESSION_TYPE)) {
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
				}
			}
			else {
				plogger.info("No parameters passed! Setting default values for compressionType and compressionQuality");
				image.setCompression(COMPRESSION_TYPE_PARAM_DEFAULT);
				imageInfo.setQuality(COMPRESSION_QUALITY_LEVEL_DEFAULT);
			}
			
			image.setImageFormat(outputExt);
			plogger.info("Setting new file format for output file to: " + outputExt);
			
			outputFilePath = outputFolder.getAbsolutePath() + File.separator + OUTPUT_FILE_NAME + "." + outputExt;
			plogger.info("Starting to write result file to: " + outputFilePath);
			
			image.setFileName(outputFilePath);
			boolean imageMagickSuccess = image.writeImage(imageInfo);
			if(imageMagickSuccess)
				plogger.info("Successfully created result file at: " + outputFilePath);
			else 
				return this.returnWithErrorMessage("Something went terribly wrong with ImageMagick: No output file created!!!", null);
			
		} catch (MagickException e) {
		    e.printStackTrace();
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
		DigitalObject digObj =  new DigitalObject.Builder(Content.byValue(resultFileBlob)).build();
		return digObj;
	}
	
	private boolean compareExtensions (String extension1, String extension2) {
		
		plogger.info("Starting to compare these two extensions: " + extension1 + " and " + extension2);
		FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
		
		Set <URI> ext1FormatURIs = formatRegistry.getURIsForExtension(extension1.toLowerCase());
		plogger.info("Got list of URIs for " + extension1);
//		for (Iterator iterator = ext1FormatURIs.iterator(); iterator.hasNext();) {
//			URI uri = (URI) iterator.next();
//			plogger.info("Got: " + uri.toASCIIString());
//		}
		
		Set <URI> ext2FormatURIs = formatRegistry.getURIsForExtension(extension2.toLowerCase());
		plogger.info("Got list of URIs for " + extension2);
//		for (Iterator iterator = ext2FormatURIs.iterator(); iterator.hasNext();) {
//			URI uri = (URI) iterator.next();
//			plogger.info("Got: " + uri.toASCIIString());
//		}
		
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
