package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;

public class ImageMagickMigrations implements Migrate {
	
	public static String[] compressionTypes = new String[11];
	private PlanetsLogger plogger = PlanetsLogger.getLogger(this.getClass());
	private static final int COMPRESSION_TYPE_DEFAULT = 1; 
	private static final String IMAGEMAGICK_TEMP = "ImageMagickService";
	private static final String OUT_FOLDER = "output";
	private static final String INPUT_FILE_NAME = "imageMagickInput";
	private static final String OUTPUT_FILE_NAME = "imageMagickOutput";
	
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
		// TODO Auto-generated method stub
		return null;
	}

	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, Parameters parameters) {
		
		MigrateResult migrateResult = new MigrateResult();
		String inputExt = getFormatExtension(inputFormat);
		String outputExt = getFormatExtension(outputFormat);
		String inputError = null;
		String outputError = null;

		if(inputExt == null){
			plogger.error("The Format: " + inputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!");
			inputError = "The Format: " + inputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!";
		}
		if(outputExt == null) {
			plogger.error("The Format: " + outputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!");
			outputError = "The Format: " + outputFormat.toASCIIString() + " is NOT supported by this ImageMagick-Service!";
		}
		
		if((inputError != null) && (outputError != null)) {
			StringBuffer allError = new StringBuffer();
			allError.append(inputError);
			allError.append("\n");
			allError.append(outputError);
			migrateResult.getReport().setError(allError.toString());
			return migrateResult;
		}
		if((inputError != null) && (outputError == null)) {
			migrateResult.getReport().setError(inputError);
			return migrateResult;
		}
		if((inputError == null) && (outputError != null)) {
			migrateResult.getReport().setError(outputError);
			return migrateResult;
		}
		
		InputStream inputStream = digitalObject.getContent().read();
		
		File inputFile = FileUtils.writeInputStreamToTmpFile(inputStream, INPUT_FILE_NAME, inputExt);
		
		plogger.info("Starting ImageMagick Migration from " + inputExt + " to " + outputExt + "...");
		
		plogger.debug("Initialising ImageInfo Object");
		
	    try {
			ImageInfo imageInfo = new ImageInfo(inputFile.getAbsolutePath());
			MagickImage image = new MagickImage(imageInfo);
			
			String actualSrcFormat = image.getMagick();
			
			if(compareExtensions(inputExt, actualSrcFormat) == false) {
				migrateResult.getReport().setError("The passed input file format does not match the actual format of the file!!!");
				return migrateResult;
			}
			
			if(parameters != null) {
				int compressionType;
				List<Parameter> parameterList = parameters.getParameters();
				for (Iterator<Parameter> iterator = parameterList.iterator(); iterator.hasNext();) {
					Parameter parameter = (Parameter) iterator.next();
					String name = parameter.name;
					if(name.equalsIgnoreCase("compressionType")) {
						compressionType = Integer.parseInt(parameter.value);
						image.setCompression(compressionType);
					}
					else {
						image.setCompression(COMPRESSION_TYPE_DEFAULT);
					}
				}
			}
			else {
				image.setCompression(COMPRESSION_TYPE_DEFAULT);
			}
			
			image.setMagick(outputExt);
			
			File imageMagickTmpFolder = FileUtils.createWorkFolderInSysTemp(IMAGEMAGICK_TEMP);
			File outputFolder = FileUtils.createFolderInWorkFolder(imageMagickTmpFolder, OUT_FOLDER); 
			
			image.setFileName(outputFolder.getAbsolutePath() + File.separator + OUTPUT_FILE_NAME + "." + outputExt);
			image.writeImage(imageInfo);
			
			/** 
			 * QUESTIONS:
			 * 
			 * 1) What should i do now? 
			 * 2) where should the new created file be placed?
			 * 3) how should the result be returned?
			 * 4) What about the service description? How to set up etc...?
			 * 5) Do i have to create a new DigitalObject for the new file?
			 * 
			 */
			
			
		} catch (MagickException e) {
			e.printStackTrace();
		}
	   
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean compareExtensions (String extension1, String extension2) {
		FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
		Set <URI> ext1FormatURIs = formatRegistry.getURIsForExtension(extension1);
		Set <URI> ext2FormatURIs = formatRegistry.getURIsForExtension(extension2);
		boolean success = false;
		
		for(URI currentUri: ext1FormatURIs) {
			if(ext2FormatURIs.contains(currentUri)) {
				success = true;
			}
			else {
				success = false;
			}
		}
		return success;
	}
	
	private String getFormatExtension (URI formatURI) {
		String extension = null;
		FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
		Format fileFormat = formatRegistry.getFormatForURI(formatURI);
		Set <String> extensions = fileFormat.getExtensions();
		if(extensions != null){
			Iterator <String> iterator = extensions.iterator();
			extension = iterator.next();
		}
		return extension;
	}
	

}
