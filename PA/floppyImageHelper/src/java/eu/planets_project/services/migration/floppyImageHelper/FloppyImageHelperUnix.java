package eu.planets_project.services.migration.floppyImageHelper;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.*;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.*;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Melms
 * @author Klaus Rechert
 *
 * requires pmount http://www.piware.de/projects/pmount-0.9.13.tar.gz
 * requires losetup
 * requires dd
 *
 * preparation:
 * modprobe loop
 * sudo echo '/dev/loop[01234567]' &gt;&gt; /dev/pmount.allow
 * on deb-distris add jboss-user to plugdev, disk
 */
@Stateless()
@Local(Migrate.class)
@Remote(Migrate.class)

@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = FloppyImageHelperWin.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")


public class FloppyImageHelperUnix implements Migrate {
	
	public FloppyImageHelperUnix() {
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
		FileUtils.deleteTempFiles(TEMP_FOLDER);
		TEMP_FOLDER = FileUtils.createWorkFolderInSysTemp(TEMP_FOLDER_NAME);
	}
	                 
	public static final String NAME = "FloppyImageHelperUnix";
	
	private static File TEMP_FOLDER = null;
	private static String TEMP_FOLDER_NAME = "FLOPPY_IMAGE_HELPER";
	
	private static String DEFAULT_INPUT_NAME = "inputFile";
	private static String INPUT_EXT = null;
	
	private static String DEFAULT_FLOPPY_IMAGE_NAME = "floppy144.ima";
	private static final long FLOPPY_SIZE = 1474560;
	
	private static boolean MODIFY_IMAGE = false;
	
	private static String PROCESS_ERROR = null;
	private static String PROCESS_OUT = null;
	
	private static PlanetsLogger log = PlanetsLogger.getLogger(FloppyImageHelperUnix.class);
	private static int LOOP_DEV_MAX = 5;

	static {
		TEMP_FOLDER = new File("/tmp");
	}

	/**
	* @see eu.planets_project.services.migrate.Migrate#describe()
	*/
	public ServiceDescription describe() 
	{
		ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME, Migrate.class.getCanonicalName());
		sd.author("Klaus Rechert, mailto:klaus.rechert@rz.uni-freiburg.de");
		sd.description("This service is a wrapper for creating floppy images with UNIX dd and fs-tools\n" +
			"This tools is able to create Floppy disk images (1.44 MB) from scratch," + 
			"containing files of your choice (up to 1.44 MB!).\n" +
        		"This is the first possible direction. The other one is the Extraction of files from a " + 
			"floppy disk image without mounting that image as a disk drive." +
        		"This service accepts:\n\n" +

        		"1) ZIP files, containing the files you want to be written on the floppy image. " + 
			"The service will unpack the ZIP file and write the contained files to the floppy image, " +
       			"which is returned, if the files in the ZIP do not exceed the capacity limit of 1.44 MB.\n" +

        		"2) a single file which should be written on the floppy image. This file could be of ANY " + 
			"type/format (except the '.ima/.img' type!)\n" +

        		"3) An '.IMA'/'.IMG' file. In this case, the service will extract all files from that floppy " + 
			"image and return a set of files (as a ZIP)." +
        		"'Extract.exe' is using the WinImage SDK.");

	        sd.classname(this.getClass().getCanonicalName());
       		sd.version("1.0");

        	List<MigrationPath> pathways = new ArrayList<MigrationPath>();
		pathways.add(new MigrationPath(Format.extensionToURI("ZIP"), Format.extensionToURI("IMA"), null));
		pathways.add(new MigrationPath(Format.extensionToURI("ANY"), Format.extensionToURI("IMA"), null));
		pathways.add(new MigrationPath(Format.extensionToURI("IMA"), Format.extensionToURI("ZIP"), null));
		pathways.add(new MigrationPath(Format.extensionToURI("IMG"), Format.extensionToURI("ZIP"), null));
        
		sd.paths(pathways.toArray(new MigrationPath[] {}));
		return sd.build();
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
	 */
	public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
			URI outputFormat, List<Parameter> parameters) 
	{
		
		String inFormat = Format.getFirstMatchingFormatExtension(inputFormat).toUpperCase();
		String outFormat = Format.getFirstMatchingFormatExtension(outputFormat).toUpperCase();
		
		if(parameters!=null && parameters.size()>0) {
			for (Parameter parameter : parameters) {
				if(parameter.name.equalsIgnoreCase("modifyImage")) {
					if(parameter.value.equalsIgnoreCase("true")) {
						MODIFY_IMAGE = true;
					}
				}
			}
		}
		
		ImmutableContent content = (ImmutableContent)digitalObject.getContent();
		Checksum checksum = content.getChecksum();
		
		String fileName = digitalObject.getTitle();
		if(fileName == null) 
		{
			INPUT_EXT = Format.getFirstMatchingFormatExtension(inputFormat);
			fileName = DEFAULT_INPUT_NAME + "." + INPUT_EXT;
		}
		File inputFile = FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), TEMP_FOLDER, fileName);
		
		if((inFormat.endsWith("IMA")) || inFormat.endsWith("IMG")) {
			ZipResult zippedResult = this.extractFilesFromFloppyImage(inputFile);
			
			Content zipContent = ImmutableContent.asStream(zippedResult.getZipFile()).withChecksum(zippedResult.getChecksum());
			
			DigitalObject resultDigObj = new DigitalObject.Builder(zipContent)
			.format(Format.extensionToURI("zip"))
			.title(zippedResult.getZipFile().getName())
			.build();

			ServiceReport report = new ServiceReport();
			report.setErrorState(0);
			report.setInfo(PROCESS_OUT);
			return new MigrateResult(resultDigObj, report);
		}
		
		List<File> files = null;
		if(inFormat.endsWith("ZIP")) // x-fmt/263
		{
			if(checksum != null) 
			{
				long check = Long.parseLong(checksum.getValue());
				files = FileUtils.extractFilesFromZipAndCheck(inputFile, TEMP_FOLDER, check);
			}
			else 
				files = FileUtils.extractFilesFromZip(inputFile, TEMP_FOLDER);
		}
		else {
			files = new ArrayList<File>();
			files.add(inputFile);
		}

		File floppy = this.createFloppyImageWithFiles(files);
		if(floppy == null) 
			return this.returnWithErrorMessage(PROCESS_ERROR, null);

		DigitalObject resultDigObj = new DigitalObject.Builder(ImmutableContent.asStream(floppy))
										.format(outputFormat)
										.title(floppy.getName())
										.build();
		
		ServiceReport report = new ServiceReport();
        	report.setErrorState(0);
        	report.setInfo(PROCESS_OUT);
		return new MigrateResult(resultDigObj, report);
	}
	
	private static ZipResult extractFilesFromFloppyImage(File image) 
	{
		int loopdev;
		String mountlabel;

		if(!image.exists()) 
		{
			log.error("Image: " + image.getAbsolutePath() + "not found");
			return null;
		}
	
		loopdev = bindLoop(image.getAbsolutePath());
		if(loopdev < 0)
			return null;
		
		mountlabel = mount(loopdev);
		if(mountlabel == null)
		{
			unbindLoop(loopdev);
			return null;
		}

		log.info("image: " + image + " mounted to " + mountlabel);
		File srcdir = new File(mountlabel);
		ZipResult result = FileUtils.createZipFileWithChecksum(srcdir, TEMP_FOLDER, "extractedFiles.zip");

		umount(mountlabel);
		unbindLoop(loopdev);

		return result;
	}
	
	private static File createFloppyImageWithFiles(List<File> files) 
	{
		int loopdev;
		String mountlabel;

		if(FileUtils.filesTooLargeForMedium(files, FLOPPY_SIZE)) 
		{
			log.error("Sorry! File set too large to be written to a Floppy (1.44 MB).");
			return null;
		}
			
		File image = new File("/tmp", DEFAULT_FLOPPY_IMAGE_NAME);
		createFloppy(image);
		if(!image.exists()) 
		{
			log.error("Creating image failed");
			return null;
		}
	
		loopdev = bindLoop(image.getAbsolutePath());
		if(loopdev < 0)
			return null;
		
		mountlabel = mount(loopdev);
		if(mountlabel == null)
		{
			log.error("mount failed");
			unbindLoop(loopdev);
			return null;
		}

		log.info("image: " + image + " mounted to " + mountlabel);
		boolean success = copyFiles(files, mountlabel);

		umount(mountlabel);
		unbindLoop(loopdev);

		if(success)
		{
			log.info("successfuly created floppy image");
			return image;
		}
		else
			return null;
	}

	/**
	 * @param message an optional message on what happened to the service
	 * @param e the Exception e which causes the problem
	 * @return CharacteriseResult containing a Error-Report
	 */
	private MigrateResult returnWithErrorMessage(final String message,
	        final Exception e) {
	    if (e == null) {
	        return new MigrateResult(null, ServiceUtils
	                .createErrorReport(message));
	    } else {
	        return new MigrateResult(null, ServiceUtils
	                .createExceptionErrorReport(message, e));
	    }
	}

	private static boolean copyFiles(List<File> files, String dest)
	{
		ArrayList<String> commands = new ArrayList<String>();
		ProcessRunner cmd = new ProcessRunner();
		cmd.setCommand(commands);

		for(File file : files)
		{
			commands.clear();
			commands.add("/bin/cp");
			commands.add(file.getAbsolutePath());
			commands.add(dest);	
		
			cmd.run();

			if(cmd.getReturnCode() != 0)
			{
				log.info("cp " + cmd.getProcessOutputAsString());
				log.info("err: " + cmd.getProcessErrorAsString() + 
					"(" + cmd.getReturnCode() + ")");
				return false;
			}
		}
		return true;
	}

	private static File createFloppy(File imageFile)
	{
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("/bin/dd");
		commands.add("if=/dev/zero");
		commands.add("of=" + imageFile.getAbsolutePath());
		commands.add("bs=512");
		commands.add("count=2880");		

		ProcessRunner cmd = new ProcessRunner();
		cmd.setCommand(commands);
		cmd.run();

		if(cmd.getReturnCode() != 0)
		{
			log.error("dd: " + cmd.getProcessOutputAsString());	
			log.error("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return null;
		}

		commands.clear();
		commands.add("/sbin/mkfs.vfat");
		commands.add(imageFile.getAbsolutePath());
		cmd.run();

		// if return code is zero this loop device is ocupied
		if(cmd.getReturnCode() != 0)
		{
			log.info("mkfs " + cmd.getProcessOutputAsString());
			log.info("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return null;
		}
		return null;	
	}

	public static boolean isLoopDevAvailable(int i)
	{
		// check write permission
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("/usr/bin/touch");
		commands.add("/dev/loop" + i);		

		ProcessRunner cmd = new ProcessRunner();
		cmd.setCommand(commands);
		cmd.run();

		if(cmd.getReturnCode() != 0)
		{
			log.error("/dev/loop" + i + " not writeable");	
			log.error("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return false;
		}

		commands.clear();
		commands.add("/sbin/losetup");
		commands.add("/dev/loop" + i);
		cmd.run();

		// if return code is zero this loop device is ocupied
		if(cmd.getReturnCode() == 0)
		{
			log.info("losetup " + cmd.getProcessOutputAsString());
			log.info("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return false;
		}
		return true;	
	}

	private static int bindLoop(String imageFile)
	{
		ProcessRunner cmd = new ProcessRunner();
		ArrayList<String> commands = new ArrayList<String>();
		int loopdev;

		for(loopdev = 0; loopdev <= LOOP_DEV_MAX; loopdev++)
		{
			if(isLoopDevAvailable(loopdev))
				break;
		}
		
		if(loopdev > LOOP_DEV_MAX)
		{
			log.error("no suitable loop dev found");
			return -1;
		}

		commands.add("/sbin/losetup");
		commands.add("/dev/loop" + loopdev);
		commands.add(imageFile);
		cmd.setCommand(commands);
		cmd.run();

		if(cmd.getReturnCode() != 0)
		{
			log.error("losetup " + cmd.getProcessOutputAsString());
			log.error("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return -1;
		}
		return loopdev;
	}

	private static boolean unbindLoop(int loopdev)
	{
		ProcessRunner cmd = new ProcessRunner();
		ArrayList<String> commands = new ArrayList<String>();
		

		commands.add("/sbin/losetup");
		commands.add("-d");
		commands.add("/dev/loop" + loopdev);
		cmd.setCommand(commands);
		cmd.run();

		if(cmd.getReturnCode() != 0)
		{
			log.error("losetup unbind" + cmd.getProcessOutputAsString());
			log.error("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return false;
		}
		return true;
	}

	private static String mount(int loopdev)
	{
		ProcessRunner cmd = new ProcessRunner();
		ArrayList<String> commands = new ArrayList<String>();
		String mountlabel = "/media/loop" + loopdev;

		commands.add("/usr/bin/pmount");
		commands.add("/dev/loop" + loopdev);
		commands.add(mountlabel);
		cmd.setCommand(commands);
		
		cmd.run();
		if(cmd.getReturnCode() != 0)
		{
			log.error("pmount " + cmd.getProcessOutputAsString());
			log.error("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return null;
		}
		return mountlabel;
	}

	private static boolean umount(String mountlabel)
	{
		ProcessRunner cmd = new ProcessRunner();
		ArrayList<String> commands = new ArrayList<String>();

		commands.add("/usr/bin/pumount");
		commands.add(mountlabel);
		cmd.setCommand(commands);
		
		cmd.run();
		if(cmd.getReturnCode() != 0)
		{
			log.error("pumount " + cmd.getProcessOutputAsString());
			log.error("err: " + cmd.getProcessErrorAsString() + "(" + cmd.getReturnCode() + ")");
			return false;
		}
		return true;
	}
}
