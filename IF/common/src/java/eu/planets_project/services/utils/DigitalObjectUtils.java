/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;

/**
 * Utils for handling digital objects.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public final class DigitalObjectUtils {
    private DigitalObjectUtils() {/* Util classes are not instantiated */}

    private static final Logger log = Logger.getLogger(DigitalObjectUtils.class.getName());

    private final static FormatRegistry format = FormatRegistryFactory
            .getFormatRegistry();
    
    private static final URI zipType = format.createExtensionUri("zip");
    
    private static File utils_tmp = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "dig-ob-utils-tmp".toUpperCase());

    /**
     * @return The total size, in bytes, of the bytestream contained or referred
     *         to by this Digital Object. Does not include the size of any
     *         associated metadata, or the Java objects etc.
     */
    public static long getContentSize(final DigitalObject dob) {
        long bytes = 0;
        // Get the size at this level, if set:
        byte[] buf = new byte[1024];
        if (dob.getContent() != null) {
            InputStream inputStream = dob.getContent().getInputStream();
            int length = 0;
            try {
                while ((inputStream != null)
                        && ((length = inputStream.read(buf)) != -1)) {
                    bytes += length;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return the total:
        return bytes;
    }

    /**
     * These cases: <br/>
     * - A compound DO, zip as Content, with MD outside the zip, pointing into
     * it via Title. This is to pass between services.<br/>
     * - A zip file containing CDO, MD inside the zip, pointing to the binaries
     * via the Title. This is an pure file 'IP', in effect.<br/>
     * - A compound DO, pulled from such a CDO zip file, with inputstreams for
     * content. Okay, two formats, different contexts and packing/unpacking
     * options.<br/>
     * - (CDO[zip] or CDO) i.e. If no Content, look up to root and unpack?<br/>
     * - DOIP - a special ZIP file containing CDOs. <br/>
     * Operations:<br/>
     * - Packing one or more CDOs into a DOIP, optionally embedding referenced
     * resources. (Value) resources always to be embedded.<br/>
     * - Unpacking a DOIP and getting N CDOs out, optionally embedding binaries,
     * using ZipInputStreams, or unpacking into Files?<br/>
     * TODO Should DO use URI internally got Content.reference, to allow
     * relative resolution?
     */
    public static void main(String args[]) {
        try {
            URI uri = new URI("FAQ.html");
            System.out.println("Got " + uri);
            System.out.println("Got " + uri.isAbsolute());
            uri = new URI("http://localhost/FAQ.html");
            System.out.println("Got " + uri);
            System.out.println("Got " + uri.isAbsolute());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    


        /**
	 * Creates a tmp-file from the content of a DigitalObject.
	 * 
	 * @param digitalObject The digital object
	 * @return A temporary file containing the content of the given digital
	 *         object (Note: As a temporary file, this file will not persist)
	 */
	public static File getAsTmpFile(final DigitalObject digitalObject) {
	    File inputFile = FileUtils.getTempFile("digital-object-content", "bin");
	    FileUtils.writeInputStreamToFile(digitalObject.getContent().getInputStream(),
	            inputFile);
	    return inputFile;
	}
	
	
	/**
	 * Creates a file for the content of a given DigitalObject
	 * @param digitalObject the DigitalObject containing the Content to be written to a File
	 * @param destFolder the Folder to write the File into
	 * @param fileName the name the file should have. If no fileName is specified, a randomized default name will be used (dig-ob-content.bin).
	 * @return the File for the given Content
	 */
	public static File getAsFile(final DigitalObject digitalObject, File destFolder, String fileName) {
		if(fileName==null) {
			fileName = digitalObject.getTitle();
			if(fileName==null || fileName.equalsIgnoreCase("")) {
				fileName = FileUtils.randomizeFileName("dig-ob-content.bin"); 
			}
		}
		File contentFile = new File(destFolder, FileUtils.randomizeFileName(fileName));
		FileUtils.writeInputStreamToFile(digitalObject.getContent().getInputStream(), contentFile);
		return contentFile;
	}
	
	/**
     * A utility method that creates files for the content of "contained"-DigObs in a DigOb.
     * This method returns all contained DigObs one level deep.
     * 
     * @param listOfDigObjs The digital objects to create files from
     * @param targetFolder The folder to store result files in
     * @return The child elements of the given digital object as files
     */
    public static List<File> getDigitalObjectsAsFiles(final List<DigitalObject> listOfDigObjs, final File targetFolder) {
        List<File> containedFiles = new ArrayList<File>();
        log.info("received list of dig obj with lengh: "+ listOfDigObjs.size());
        if (listOfDigObjs.size() > 0) {
            for (DigitalObject currentDigObj : listOfDigObjs) {
                String name = getFileNameFromDigObject(currentDigObj, null);
                log.info("name of dig obj is: "+name);
                containedFiles.add(FileUtils.writeInputStreamToFile(
                        currentDigObj.getContent().getInputStream(), targetFolder, name));
            }
        }
        log.info(String.format("Returning %s files", containedFiles.size()));
        return containedFiles;
    }
	
	/**
	 * Creates a Zip-type DigitalObject either from a given folder or from a zip file.
	 * @param zip_Or_Folder
	 * @param destZipName the name of the created zip file
	 * @param createByReference Create the Content of the DO by reference or by value
	 * @param withChecksum Create a checksum for the zip file to create
	 * @param compress compress the content of the zip file
	 * @return The digital object representing the zipped files
	 */
	public static DigitalObject createZipTypeDigitalObject(File zip_Or_Folder, String destZipName, boolean createByReference, boolean withChecksum, boolean compress) {
		if(zip_Or_Folder.isFile() && ZipUtils.isZipFile(zip_Or_Folder)) {
			return createZipTypeDigitalObjectFromZip(zip_Or_Folder, createByReference, withChecksum);
		}
		else {
			return createZipTypeDigitalObjectFromFolder(zip_Or_Folder, destZipName, createByReference, withChecksum, compress);
		}
	}


	/**
     * This method returns a new DigOb, containing a file that is specified by the fragment. The Fragment points to a file inside the zip.
     * If the passed DigOb is not a ZIP type DigOb, null is returned.
     * 
     * @param digOb the zip type DigOb to get the fragment from
     * @param fragment the fragment (file in the zip) to retrieve
     * @param createByReference create by reference (true) or as stream (false)
     * @return a new DigitalObject containing the extracted fragment as content
     */
    public static DigitalObject getFragment(DigitalObject digOb, String fragment, boolean createByReference) {
    	if(!isZipType(digOb)) {
    		log.severe("The DigitalObject you have passed is NOT a Zip type DigOb. No Fragment could be retrieved!");
    		return null;
    	}
    	// Do all the tmpFolder related stuff....
    	String tmpfolderName = FileUtils.randomizeFileName(getFolderNameFromDigObject(digOb));
    	File digObTmp = FileUtils.createFolderInWorkFolder(utils_tmp, tmpfolderName);
//    	FileUtils.deleteAllFilesInFolder(digObTmp);
    	File zip = getZipAsFile(digOb);
    	
    	File target = ZipUtils.getFileFrom(zip, fragment, digObTmp);    	
		
		DigitalObject resultDigOb = createDigitalObject(target, createByReference);
		
		return resultDigOb;
    }
    
    
    public static DigitalObject insertFragment(DigitalObject zipTypeDigOb, File fragmentFile, String targetPathInZip, boolean createByReference) {
		if(!isZipType(zipTypeDigOb)) {
			log.severe("The DigitalObject you have passed is NOT a Zip type DigOb. No Fragment could be retrieved!");
			return null;
		}
		
		File zip = getZipAsFile(zipTypeDigOb);
		
		File modifiedZip = ZipUtils.insertFileInto(zip, fragmentFile, targetPathInZip);
		DigitalObject result = createZipTypeDigitalObjectFromZip(modifiedZip, createByReference, false);
		return result;
	}
    
    public static DigitalObject removeFragment(DigitalObject zipTypeDigOb, String targetPathInZip, boolean createByReference) {
		if(!isZipType(zipTypeDigOb)) {
			log.severe("The DigitalObject you have passed is NOT a Zip type DigOb. No Fragment could be retrieved!");
			return null;
		}
		
		File zip = getZipAsFile(zipTypeDigOb);
		
		File modifiedZip = ZipUtils.removeFileFrom(zip, targetPathInZip);
		DigitalObject result = createZipTypeDigitalObjectFromZip(modifiedZip, createByReference, false);
		return result;
	}

	public static List<String> listFragments(DigitalObject digOb) {
    	if(!isZipType(digOb)) {
    		log.severe("This DigitalObject is NOT a Zip-type DigOb! No Fragments to return, sorry!!!");
    		return null;
    	}
    	
    	return digOb.getFragments();
    }
    
	
	/**
	 * test if this is a zip type DigitalObject (format-URL == planets:fmt/ext/zip)
	 * @param digOb the DigitalObject to test
	 * @return "true" if the digOb is of type zip, "false" if not ;-)
	 */
	public static boolean isZipType(DigitalObject digOb) {
		if(digOb.getFormat()==null) {
			return false;
		}
		return digOb.getFormat().equals(zipType);
	}
	
	
	/**
	 * Gets the title from the passed digOb and returns a proper folder name (e.g. strip the extension etc.)
	 * @param digOb to get the folder name from
	 * @return the folder name based on "title" in the passed digOb.
	 */
	public static String getFolderNameFromDigObject(DigitalObject digOb) {
		String title = digOb.getTitle();
		if(title==null) {
			return null;
		}
		
		if(title.contains(" ")) {
			title = title.replaceAll(" ", "_");
		}
		
		if(title.equalsIgnoreCase(".svn")) {
			return title;
		}
		if(title.contains(".")) {
			title = title.substring(0, title.lastIndexOf("."));
		}
		return title;
	}

	/**
	 * Gets the title from the passed digOb and returns a proper file name
	 * @param digOb to get the file name from
	 * @param supposedFormatURI This could be the format you believe the file has. Used to create a proper file name.
	 * @return the folder name based on "title" in the passed digOb.
	 */
	public static String getFileNameFromDigObject(DigitalObject digOb, URI supposedFormatURI) {
		String title = digOb.getTitle();
		String ext = null;
		
		
		// I know, this is evil, but this is a workaround for the Zip-DigitalObjectUtils
		if(supposedFormatURI==null) {
			URI digObFormat = digOb.getFormat();
			if(digObFormat==null) {
				ext = "bin";
			}
			else {
				ext = format.getFirstExtension(digObFormat);
			}
		}
		else {
			ext = format.getFirstExtension(supposedFormatURI);
		}
		
		if(title==null) {
			String defaultTitle = "default_input";
			title = defaultTitle + "." + ext;
		}
		
		if(title.contains(" ")) {
			title = title.replaceAll(" ", "_");
		}
		
		if(title.contains(".")) {
			return title;
		}
		else {
			title = title + "." + ext;
		}
		return FileUtils.randomizeFileName(title);
		
	}

	public static boolean cleanDigObUtilsTmp() {
		return FileUtils.deleteAllFilesInFolder(utils_tmp);
	}

	/**
	     * Generates a ZIP-type DigitalObject from a given folder, containing the zip file itself at the top-level
	     * DigitalObject and a list of the files contained in this zip as "fragments" DigitalObjects.
	     * 
	     * @param folder the folder to create a zip from and build the DigitalObject
		 * @param destZipName the name the zip file should have. If no name is specified, the name of the folder will be used.
		 * @param createByReference a flag to set whether you want to create the DigObs by Reference or as stream...
		 * @param withChecksum creates a zip with a checksum. 
		 * @param compress compress the zip content or not.
		 * @return a DigitalObject containing the zip file created from "folder" and a list of the files inside the zip as "fragments".
	     */
	    private static DigitalObject createZipTypeDigitalObjectFromFolder(File folder, String destZipName, boolean createByReference, boolean withChecksum, boolean compress) {
	    	String zipName = null;
	    	if(destZipName==null) {
	    		zipName = folder.getName() + ".zip";
	    	}
	    	else {
	    		if(destZipName.contains(".")) {
	    			String tmpName = destZipName.substring(0, destZipName.lastIndexOf(".")) + ".zip";
	    			zipName = tmpName;
	    		}
	    		else {
	    			zipName = destZipName + ".zip";
	    		}
	    	}
	//    	FileUtils.deleteAllFilesInFolder(utils_tmp);
	    	File zip_tmp = FileUtils.createFolderInWorkFolder(utils_tmp, FileUtils.randomizeFileName("zip_from_folder_tmp"));
	//    	FileUtils.deleteAllFilesInFolder(zip_tmp);
	    	
	    	if(withChecksum) {
		    	ZipResult zipResult = ZipUtils.createZipAndCheck(folder, zip_tmp, zipName, compress);
		    	
		    	if(createByReference) {
		    		DigitalObject digOb = null;
					digOb = new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(zipResult.getZipFile()))
							.withChecksum(zipResult.getChecksum()))
							.title(zipName)
							.format(format.createExtensionUri("zip"))
							.fragments(ZipUtils.getAllFragments(zipResult.getZipFile()))
							.build();
		    		return digOb;
		    	}
		    	else {
		    		DigitalObject digOb = new DigitalObject.Builder(Content.byReference(zipResult.getZipFile())
							.withChecksum(zipResult.getChecksum()))
							.title(zipName)
							.format(format.createExtensionUri("zip"))
							.fragments(ZipUtils.getAllFragments(zipResult.getZipFile()))
							.build();
		    		return digOb;
		    	}
	    	}
	    	else {
	    		File result = ZipUtils.createZip(folder, zip_tmp, zipName, compress);
	    		
	    		if(createByReference) {
		    		DigitalObject digOb = null;
					digOb = new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(result)))
							.title(zipName)
							.format(format.createExtensionUri("zip"))
							.fragments(ZipUtils.getAllFragments(result))
							.build();
		    		return digOb;
		    	}
		    	else {
		    		DigitalObject digOb = new DigitalObject.Builder(Content.byReference(result))
							.title(zipName)
							.format(format.createExtensionUri("zip"))
							.fragments(ZipUtils.getAllFragments(result))
							.build();
		    		return digOb;
		    	}
	    	}
	    }

	/**
	 * Generates a ZIP-type DigitalObject from a given zip file, containing the zip file itself at the top-level
	 * DigitalObject and the files contained in this zip as "contained" DigitalObjects.
	 * 
	 * @param zipFile the zip file to create a DigitalObject with
	 * @param createByReference a flag to set whether you want to create the DigObs by Reference or as stream...
	 * @param withChecksum create DigOb with checksum or not?
	 * @return a DigitalObject containing the zip file and a list of the contained files in this zip as "fragments".
	 */
	private static DigitalObject createZipTypeDigitalObjectFromZip(File zipFile, boolean createByReference, boolean withChecksum) {
		DigitalObject digOb = null;
		if(withChecksum) {
			Checksum checksum = null;
			try {
				checksum = new Checksum("MD5", Arrays.toString(Checksums.md5(zipFile)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(createByReference) {
				digOb = new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(zipFile))
						.withChecksum(checksum))
						.title(zipFile.getName())
						.format(format.createExtensionUri("zip"))
						// lists all entries in this zip file and includes them as "fragments"
						.fragments(ZipUtils.getAllFragments(zipFile)) 
						.build();
			}
			else {
				digOb = new DigitalObject.Builder(Content.byReference(zipFile)
						.withChecksum(checksum))
						.title(zipFile.getName())
						.format(format.createExtensionUri("zip"))
						// lists all entries in this zip file and includes them as "fragments"
						.fragments(ZipUtils.getAllFragments(zipFile)) 
						.build();
			}
		}
		else {
			if(createByReference) {
				digOb = new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(zipFile)))
						.title(zipFile.getName())
						.format(format.createExtensionUri("zip"))
						// lists all entries in this zip file and includes them as "fragments"
						.fragments(ZipUtils.getAllFragments(zipFile)) 
						.build();
			}
			else {
				digOb = new DigitalObject.Builder(Content.byReference(zipFile))
						.title(zipFile.getName())
						.format(format.createExtensionUri("zip"))
						// lists all entries in this zip file and includes them as "fragments"
						.fragments(ZipUtils.getAllFragments(zipFile)) 
						.build();
			}
		}
		return digOb;
	}

	private static File getZipAsFile(DigitalObject digOb) {
	    String folderName = FileUtils.randomizeFileName(getFolderNameFromDigObject(digOb));
	    
		File tmpFolder = FileUtils.createFolderInWorkFolder(utils_tmp, folderName);
		
		File zip = new File(tmpFolder, getFileNameFromDigObject(digOb, null));
		
		FileUtils.writeInputStreamToFile(digOb.getContent().getInputStream(), zip);
		
		return zip;
	}

	/**
	 * Creates a ZIP-type DigitalObject from a given file, by reference or as stream
	 * @param file the file to create the DigitalObject from
	 * @param createByReference create by reference (true) or as stream (false)
	 * @return
	 */
	private static DigitalObject createDigitalObject(File file, boolean createByReference) {
		DigitalObject result = null;
		if(file.isDirectory()) {
			result = createZipTypeDigitalObjectFromFolder(file, FileUtils.getFileNameWithoutExtension(file), createByReference, true, true);
			return result;
		}
		else if(ZipUtils.isZipFile(file)) {
			result = createZipTypeDigitalObjectFromZip(file, createByReference, false);
			return result;
		}
		else {
			if(createByReference) {
				result = new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(file))).title(file.getName()).build();
			}
			else {
				result = new DigitalObject.Builder(Content.byReference(file)).title(file.getName()).build();
			}
		}
		return result;
	}
	
	/**
	 * @param digitalObject
	 *        The digital object to be updated
	 * @param newEvent
	 *        The event to add to the digital object
	 * @return changed digital object with new event
	 */
	public static DigitalObject addEvent(DigitalObject digitalObject, Event newEvent)
    {
		DigitalObject res = null;
		
    	if (digitalObject != null && newEvent != null)
    	{
	    	DigitalObject.Builder b = new DigitalObject.Builder(digitalObject.getContent());
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (digitalObject.getPermanentUri() != null) b.permanentUri(digitalObject.getPermanentUri());
		    if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getMetadata() != null) 
		    	b.metadata((Metadata[]) digitalObject.getMetadata().toArray(new Metadata[0]));
		    if (digitalObject.getEvents() != null)
		    {
				List<Event> eventList = digitalObject.getEvents();
				eventList.add(newEvent);
		    	b.events((Event[]) eventList.toArray(new Event[0]));
		    }
            res = b.build();
    	}
		return res;
	}
	
	/**
	 * This method returns event by summary
	 * 
	 * in the targetObj
	 * @param initObj
	 *        The initial digital object
	 * @param summary
	 *        Event property we are looking for
	 * @return res
	 *         The found event for particular summary
	 */
	public static Event getEventBySummary(DigitalObject initObj, String summary)
	{
		Event res = null;
		
		if (summary != null && initObj != null)
		{		
			// search for the right event 
			for(Event event : initObj.getEvents()) 
			{
				if (event != null)
				{
					if(event.getSummary().equals(summary))
					{
					    res = event;
					}
				}
			}			
		}
		
		return res;
	}
	
	/**
	 * This method evaluates if particular digital object contains an ingest event
	 * 
	 * @param obj
	 *        The digital object
	 * @param summary
	 *        The summary of the event
	 * @return res
	 *        Returns true if digital object contains ingest event otherwise false
	 */
	public static boolean hasEvent(DigitalObject obj, String summary)
	{
		boolean res = false;
		
		if (obj != null && summary != null)
		{		
			// search for the right event 
			for(Event event : obj.getEvents()) 
			{
				if (event != null)
				{
				if(event.getSummary().equals(summary))
				{
				    res = true;
				}
				}
			}			
		}
		
		return res;
	}
	
	/**
     * @param object The digital object to copy to a file
     * @param file The file to copy the digital object's byte stream to
     * @return The number of bytes copied
     */
    public static long toFile(final DigitalObject object, final File file) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            long bytesCopied = IOUtils.copyLarge(object.getContent().getInputStream(), fOut);
            fOut.close();
            return bytesCopied;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * @param object The digital object to copy to a temporary file
     * @return The temporary file the digital object's byte stream was written to
     */
    public static File toFile(final DigitalObject object) {
        try {
            /* TODO: use format registry to set the extension? */
            File file = File.createTempFile("planets", null);
            file.deleteOnExit();
            toFile(object, file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not copy digital object: " + object);
    }
}
