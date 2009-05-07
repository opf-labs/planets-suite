/**
 * 
 */
package eu.planets_project.services.modification.floppyImageModify.impl;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migration.floppyImageHelper.impl.VfdCommandWrapper;
import eu.planets_project.services.migration.floppyImageHelper.impl.VfdCommandWrapperResult;
import eu.planets_project.services.modification.floppyImageModify.api.FloppyImageModify;
import eu.planets_project.services.modify.Modify;
import eu.planets_project.services.modify.ModifyResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * @author Peter Melms
 *
 */
@Stateless()

@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = FloppyImageModifyWin.NAME, 
        serviceName = Modify.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.modify.Modify")
public class FloppyImageModifyWin implements Modify, FloppyImageModify {
	
	
	public static final String NAME = "FloppyImageModifyWin";
	
	private File TEMP_FOLDER = null;
	private String TEMP_FOLDER_NAME = "FLOPPY_IMAGE_MODIFY";
	
	private String DEFAULT_INPUT_NAME = "floppy144.ima";
	private String INPUT_EXT = null;
	
    private PlanetsLogger log = PlanetsLogger.getLogger(this.getClass());
    
    private static FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();
    
    private static VfdCommandWrapper vfd = new VfdCommandWrapper();

    
    public FloppyImageModifyWin() {
    	// clean the temp folder for this app at startup...
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteTempFiles(TEMP_FOLDER);
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.services.PlanetsService#describe()
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.FloppyImageModifyApi#describe()
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.FloppyImageModify#describe()
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.impl.FloppyImageModify#describe()
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.impl.FloppyImageModify#describe()
	 */
	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME, Modify.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.description("This service is a wrapper for the 'fat_imgen' Windows Commandline tool.\n" +
        				"This tool is able to add files to existing Floppy disk images (1.44 MB).\n" +
        				"The service accepts: 1) IMA or IMG files as input floppy image. The files you like to add to that image should be embedded as \"contained\" DigitalObjects and could be of ANY type/format!" +
        				"The Service returns the modified input floppy image");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("1.0");
        sd.tool( Tool.create(null, "fat_imgen.exe", "v1.0.4", null, "http://wiki.osdev.org/Fat_imgen"));
        sd.inputFormats(formatRegistry.createExtensionUri("IMA"), formatRegistry.createExtensionUri("IMG"));
        return sd.build();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.services.modify.Modify#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.FloppyImageModifyApi#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.FloppyImageModify#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.impl.FloppyImageModify#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.impl.FloppyImageModify#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	public ModifyResult modify(DigitalObject digitalObject, URI inputFormat, URI actionURI,
			List<Parameter> parameters) {
		
        String inFormat = formatRegistry.getExtensions(inputFormat).iterator().next().toUpperCase();
		
		String fileName = digitalObject.getTitle();
		
		if(fileName==null) {
			INPUT_EXT = formatRegistry.getExtensions(inputFormat).iterator().next();
			fileName = DEFAULT_INPUT_NAME + "." + INPUT_EXT;
		}
		
		if(!inFormat.equalsIgnoreCase("IMA") && !inFormat.equalsIgnoreCase("IMG")) {
			log.error("ERROR: Input file ' " + fileName + "' is NOT an '.ima' or '.img' file or is no floppy image at all." +
					"\nThis service is able to deal with ima/img files only!!!" +
					"\nSorry, returning with error!");
			return this.returnWithErrorMessage("ERROR: Input file ' " + fileName + "' is NOT an '.ima' or '.img' file or is no floppy image at all." +
					"\nThis service is able to deal with ima/img files only!!!" +
					"\nSorry, returning with error!", null);
		}
		
		File originalImageFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), TEMP_FOLDER, fileName);
		
		List<DigitalObject> contained = digitalObject.getContained();
		
		List<File> containedFiles = DigitalObjectUtils.getContainedAsFiles(contained, TEMP_FOLDER);
		
		VfdCommandWrapperResult vfdResult = vfd.addFilesToFloppyImage(originalImageFile, containedFiles);
		
		File modifiedImage = vfdResult.getResultFile();
		
		DigitalObject result = null;
		
		if(modifiedImage!=null) {
			result = new DigitalObject.Builder(ImmutableContent.asStream(modifiedImage))
									.title(modifiedImage.getName())
									.format(formatRegistry.createExtensionUri(FileUtils.getExtensionFromFile(modifiedImage)))
									.build();
		}
		else {
			return this.returnWithErrorMessage("Received NO result file from service. Something went terribly wrong somewhere!", null);
		}
		
		
		
		if(vfdResult.getState()==VfdCommandWrapperResult.SUCCESS) {
			ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, vfdResult.getMessage());
	        log.info("Created Service report...");
	        return new ModifyResult(result, report);
		}
		else {
			return this.returnWithErrorMessage(vfdResult.getMessage(), null);
		}
		
		
		
	}

//	private File addFilesToFloppyImage(File floppyImage, List<File> filesToAdd) {
//		if(FileUtils.filesTooLargeForMedium(filesToAdd, FLOPPY_SIZE)) {
//			log.error("Sorry! File compilation too large to be written to a Floppy (1.44 MB). Returning with error: " + PROCESS_ERROR);
//			return null;
//		}
//		if(floppyImage == null) {
//			return null;
//		}
//		else {
//			for (File file : filesToAdd) {
//				log.info("Writing file to image: " + file.getName());
//				String fileName = file.getName();
//				if(fileName.contains(".")) {
//					String suffix = fileName.substring(fileName.lastIndexOf("."));
//					String prefix = fileName.substring(0, fileName.indexOf("."));
//					if(prefix.length()>8) {
//						String truncated = prefix.substring(0, 8) + suffix;
//						log.warn("Warning: file name '" + fileName + "' longer then 8 characters. Name will be truncated to: " + truncated);
//					}
//				}
//				
//				ProcessRunner cmd = new ProcessRunner();
//				cmd.setCommand(this.getModifyCommandLine(floppyImage, file));
//				cmd.setStartingDir(TEMP_FOLDER);
//				cmd.run();
//				PROCESS_OUT = cmd.getProcessOutputAsString();
//				log.info("Tool output: \n" + PROCESS_OUT);
//				PROCESS_ERROR = cmd.getProcessErrorAsString();
//				
//				if(!PROCESS_ERROR.equalsIgnoreCase("")) {
//					log.error(PROCESS_ERROR);
//					return null;
//				}
//			}
//			return floppyImage;
//		}
//	}
	
//	private ArrayList<String> getModifyCommandLine(File floppyImage, File fileToAdd) {
//		ArrayList<String> commands = new ArrayList<String>();
//		commands.add("\"" + TOOL_DIR.getAbsolutePath() + File.separator + "FAT_IMGEN.EXE" + "\"");
//		commands.add("modify");
//		commands.add("\"" + floppyImage.getAbsolutePath() + "\"");
//		commands.add("-f");
//		commands.add("\"" + fileToAdd.getAbsolutePath() + "\"");
//		return commands;
//	}
	
	
	
	/**
	 * @param message an optional message on what happened to the service
	 * @param e the Exception e which causes the problem
	 * @return CharacteriseResult containing a Error-Report
	 */
	private ModifyResult returnWithErrorMessage(final String message,
	        final Exception e) {
	    if (e == null) {
	        return new ModifyResult(null, ServiceUtils
	                .createErrorReport(message));
	    } else {
	        return new ModifyResult(null, ServiceUtils
	                .createExceptionErrorReport(message, e));
	    }
	}

}
