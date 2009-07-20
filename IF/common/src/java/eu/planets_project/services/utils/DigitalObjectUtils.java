/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Checksum;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Fragment;

/**
 * Utils for handling digital objects.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public final class DigitalObjectUtils {
    private DigitalObjectUtils() {/* Util classes are not instantiated */}

    private static final Log LOG = LogFactory.getLog(DigitalObjectUtils.class);

    private final static FormatRegistry format = FormatRegistryFactory
            .getFormatRegistry();
    
    private static final URI zipType = format.createExtensionUri("zip");
    private static final URI folderType = format.createFolderTypeUri();
    
    private static final List<URI> containerTypes = new ArrayList<URI>(Arrays.asList(new URI[] {zipType, folderType}));
    
    private static File utils_tmp = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "dig-ob-utils-tmp".toUpperCase());

    /**
     * @return The total size, in bytes, of the bytestream contained or referred
     *         to by this Digital Object. Does not include the size of any
     *         associated metadata, or the Java objects etc. Recursive method
     *         for computing the total size. TODO A badly-formed DigitalObject
     *         could cause this method to recurse forever. Can that be stopped?
     */
    public static long getContentSize(final DigitalObject dob) {
        long bytes = 0;
        // Get the size at this level, if set:
        byte[] buf = new byte[1024];
        if (dob.getContent() != null) {
            InputStream inputStream = dob.getContent().read();
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
        // Recurse into sub-dobs:
        if (dob.getContained() != null) {
            for (DigitalObject cdob : dob.getContained()) {
                bytes += getContentSize(cdob);
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
     * FIXME DO must know if it has a parent in order to be able to look things
     * up?<br/>
     * FIXME Should DO use URI internally got Content.reference, to allow
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
     * Creates the "contained" DigitalObjects for a complex DigOb as a Stream
     * 
     * @param files The files to wrap as stream-based digital objects
     * @return A list of digital object wrapping the given files, streaming the
     *         content when read
     */
    public static List<DigitalObject> createContainedAsStream(
            final List<File> files) {
        List<DigitalObject> list = new ArrayList<DigitalObject>();
        for (File file : files) {
        	DigitalObject currentDigObj = null;
        	if(file.isDirectory()) {
        		currentDigObj = createFolderTypeDigitalObject(file, false);
        		list.add(currentDigObj);
        	}
        	else {
        		currentDigObj = 
        			new DigitalObject.Builder(Content.byReference(file))
                        .title(file.getName())
                        .build();
        		list.add(currentDigObj);
        	}
        }
        return list;
    }

    /**
     * * Creates the "contained" DigitalObjects for a complex DigOb by Reference
     * 
     * @param files The files to wrap as stream-based digital objects
     * @return A list of digital object with a URL pointing to a location where the files could be downloaded.
     */
    public static List<DigitalObject> createContainedbyReference(
            final List<File> files) {
        List<DigitalObject> list = new ArrayList<DigitalObject>();
        for (File file : files) {
            DigitalObject currentDigObj = null;
            if(file.isDirectory()) {
            	currentDigObj = createFolderTypeDigitalObject(file, true);
            	list.add(currentDigObj);
            }
            else {
				currentDigObj = new DigitalObject.Builder(
				        Content.byReference(FileUtils.getUrlFromFile(file)))
				        .title(file.getName())
				        .build();
				list.add(currentDigObj);
            }
        }
        return list;
    }

        /**
	 * Creates a tmp-file from the content of a DigitalObject.
	 * 
	 * @param digitalObject The digital object
	 * @return A temporary file containing the content of the given digital
	 *         object (Note: As a temporary file, this file will not persist)
	 */
	public static File getContentAsTempFile(final DigitalObject digitalObject) {
	    File inputFile = FileUtils.getTempFile("digital-object-content", "bin");
	    FileUtils.writeInputStreamToFile(digitalObject.getContent().read(),
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
	public static File getContentAsFile(final DigitalObject digitalObject, File destFolder, String fileName) {
		if(fileName==null) {
			fileName = digitalObject.getTitle();
			if(fileName==null || fileName.equalsIgnoreCase("")) {
				fileName = FileUtils.randomizeFileName("dig-ob-content.bin"); 
			}
		}
		File contentFile = new File(destFolder, FileUtils.randomizeFileName(fileName));
		FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), contentFile);
		return contentFile;
	}
	
	/**
	 * Creates a Zip-type DigitalObject either from a given folder or from a zip file.
	 * @param zip_Or_Folder
	 * @param destZipName
	 * @param createByReference
	 * @param withChecksum
	 * @param compress TODO
	 * @return
	 */
	public static DigitalObject createZipTypeDigOb(File zip_Or_Folder, String destZipName, boolean createByReference, boolean withChecksum, boolean compress) {
		if(zip_Or_Folder.isFile() && ZipUtils.isZipFile(zip_Or_Folder)) {
			return createZipTypeDigitalObjectFromZip(zip_Or_Folder, createByReference, withChecksum);
		}
		else {
			return createZipTypeDigitalObjectFromFolder(zip_Or_Folder, destZipName, createByReference, withChecksum, compress);
		}
	}

	/**
     * Generates a ZIP-type DigitalObject from a given folder, containing the zip file itself at the top-level
     * DigitalObject and a list of the files contained in this zip as "fragments" DigitalObjects.
     * 
     * @param folder the folder to create a zip from and build the DigitalObject
	 * @param destZipName the name the zip file should have. If no name is specified, the name of the folder will be used.
	 * @param createByReference a flag to set whether you want to create the DigObs by Reference or as stream...
	 * @param withChecksum creates a zip with a checksum. 
	 * @param compress TODO
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
    	FileUtils.deleteAllFilesInFolder(utils_tmp);
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
				// TODO Auto-generated catch block
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

	
	/**
	 * Creates a folder-type DigOb, with the passed folder as Content and all files in this folder as "contained" DigObs.
	 * @param folder the folder to create a DigOb from
	 * @param createByReference create this DigObs content by Reference (true) or as Stream (false)
	 * @return a new folder type DigitalObject, containing all files in folder as "contained" DigitalObjects.
	 */
	public static DigitalObject createFolderTypeDigitalObject(File folder, boolean createByReference) {
		DigitalObject folderDigOb = null;
		if(folder.isDirectory()) {
			List<File> folderContent = Arrays.asList(folder.listFiles());
			
			if(folderContent!=null && folderContent.size()>0) {
				if(createByReference) {
					folderDigOb = 
						new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(folder)))
							.format(format.createFolderTypeUri())
							.title(folder.getName())
							// recurse into all contained files and create DigObs for them as well
							.contains(createContainedbyReference(folderContent).toArray(new DigitalObject[]{}))
							.build();
				}
				else {
					folderDigOb = 
						new DigitalObject.Builder(Content.byReference(folder))
							.format(format.createFolderTypeUri())
							.title(folder.getName())
							// recurse into all contained files and create DigObs for them as well							
							.contains(createContainedAsStream(folderContent).toArray(new DigitalObject[]{}))
							.build();
				}
			}
			else {
				if(createByReference) {
					folderDigOb = 
						new DigitalObject.Builder(Content.byReference(FileUtils.getUrlFromFile(folder)))
							.format(format.createFolderTypeUri())
							.title(folder.getName())
							.build();
				}
				else {
					folderDigOb = 
						new DigitalObject.Builder(Content.byReference(folder))
							.format(format.createFolderTypeUri())
							.title(folder.getName())
							.build();
				}
			}
		}
		else {
			return null;
		}
		return folderDigOb;
	}
	
	

	/**
	 * This method return the content of ALL DigObs which might be included in the passed digOb as
	 * files. DigObs are checked recursively.
	 * @param digOb the DigitalObject to get the files from (including all nested DigObs)
	 * @return a list of Files. the files are nested in folders corresponding to their nesting in the DigOb structure
	 */
	public static List<File> getAllFilesFromDigitalObject(DigitalObject digOb) {
		if(digOb.getFormat().equals(zipType)) {
			List<File> zipFiles = new ArrayList<File>();
//			File target = FileUtils.getTempFile(getFileNameFromDigObject(digOb, null), "");
			File target = new File(FileUtils.getPlanetsTmpStoreFolder(), getFileNameFromDigObject(digOb, null));
			FileUtils.writeInputStreamToFile(digOb.getContent().read(), target);
			zipFiles.add(target);
			return zipFiles;
		}
		if(digOb.getFormat().equals(folderType)) {
			return getAllFilesFromFolderTypeDigitalObject(digOb);
		}
		else {
			List<File> files = new ArrayList<File>();
			FileUtils.deleteAllFilesInFolder(utils_tmp);
			File tmp = FileUtils.createFolderInWorkFolder(utils_tmp, FileUtils.randomizeFileName("get-all-files-tmp"));
//			FileUtils.deleteAllFilesInFolder(tmp);
			File content = new File(tmp, getFileNameFromDigObject(digOb, null)); 
			FileUtils.writeInputStreamToFile(digOb.getContent().read(), content);
			files.add(content);
			
			List<DigitalObject> contained = digOb.getContained();
			
			File digOb_contained = FileUtils.createFolderInWorkFolder(tmp, getFolderNameFromDigObject(digOb));
			if(contained.size()>0) {
				return getDigitalObjectsAsFiles(digOb.getContained(), digOb_contained);
			}
			else {
				return files;
			}
		}
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
        LOG.info("received list of dig obj with lengh: "+ listOfDigObjs.size());
        if (listOfDigObjs.size() > 0) {
            for (DigitalObject currentDigObj : listOfDigObjs) {
                String name = currentDigObj.getTitle();
                if (name == null) {
                    //return null;
                    name = "name_is_null";
                }
                LOG.info("name of dig obj is: "+name);
                containedFiles.add(FileUtils.writeInputStreamToFile(
                        currentDigObj.getContent().read(), targetFolder, name));
            }
        }
        LOG.info(String.format("Returning %s files", containedFiles.size()));
        return containedFiles;
    }
    
    
    
    /**
     * This method return a new DigOb, containing a file that is specified by the fragment. The Fragment points to a file inside the zip.
     * If the passed DigOb is not a ZIP type DigOb, null is returned.
     * 
     * @param digOb the zip type DigOb to get the fragment from
     * @param fragment the fragment (file in the zip) to retrieve
     * @param createByReference create by reference (true) or as stream (false)
     * @return a new DigitalObject containing the extracted fragment as content
     */
    public static DigitalObject getFragmentFromZipTypeDigitalObject(DigitalObject digOb, Fragment fragment, boolean createByReference) {
    	if(!isZipTypeDigitalObject(digOb)) {
    		LOG.error("The DigitalObject you have passed is NOT a Zip type DigOb. No Fragment could be retrieved!");
    		return null;
    	}
    	// Do all the tmpFolder related stuff....
    	String tmpfolderName = getFolderNameFromDigObject(digOb);
    	File digObTmp = FileUtils.createFolderInWorkFolder(utils_tmp, tmpfolderName);
    	FileUtils.deleteAllFilesInFolder(digObTmp);
    	File zip = getZipAsFile(digOb);
    	
    	File target = ZipUtils.getFileFrom(zip, fragment.getId(), digObTmp);    	
		
		DigitalObject resultDigOb = createDigitalObject(target, createByReference);
		
		return resultDigOb;
    }
    
    
    public static DigitalObject insertFragmentInZipTypeDigitalObject(DigitalObject zipTypeDigOb, File fragmentFile, Fragment targetPathInZip, boolean createByReference) {
		if(!isZipTypeDigitalObject(zipTypeDigOb)) {
			LOG.error("The DigitalObject you have passed is NOT a Zip type DigOb. No Fragment could be retrieved!");
			return null;
		}
		
		File zip = getZipAsFile(zipTypeDigOb);
		
		File modifiedZip = ZipUtils.insertFileInto(zip, fragmentFile, targetPathInZip.getId());
		DigitalObject result = createZipTypeDigitalObjectFromZip(modifiedZip, createByReference, false);
		return result;
	}
    
    public static DigitalObject removeFragmentFromZipTypeDigitalObject(DigitalObject zipTypeDigOb, Fragment targetPathInZip, boolean createByReference) {
		if(!isZipTypeDigitalObject(zipTypeDigOb)) {
			LOG.error("The DigitalObject you have passed is NOT a Zip type DigOb. No Fragment could be retrieved!");
			return null;
		}
		
		File zip = getZipAsFile(zipTypeDigOb);
		
		File modifiedZip = ZipUtils.removeFileFrom(zip, targetPathInZip.getId());
		DigitalObject result = createZipTypeDigitalObjectFromZip(modifiedZip, createByReference, false);
		return result;
	}

	public static List<Fragment> listFragments(DigitalObject digOb) {
    	if(!isZipTypeDigitalObject(digOb)) {
    		LOG.error("This DigitalObject is NOT a Zip-type DigOb! No Fragments to return, sorry!!!");
    		return null;
    	}
    	
    	return digOb.getFragments();
    }
    
    /**
	 * test if this is a Container type DigitalObject (format-URL == planets:fmt/ext/zip || planets:fmt/folder)
	 * @param digOb the DigitalObject to test
	 * @return "true" if it is, "false" if not ;-)
	 */
	public static boolean isContainerTypeDigitalObject(DigitalObject digOb) {
		if(digOb.getFormat()==null) {
			return false;
		}
		if(containerTypes.contains(digOb.getFormat())) {
			return true;
		}
		else { 
			return false;
		}
	}
	
	
	/**
	 * test if this is a zip type DigitalObject (format-URL == planets:fmt/ext/zip)
	 * @param digOb the DigitalObject to test
	 * @return "true" if the digOb is of type zip, "false" if not ;-)
	 */
	public static boolean isZipTypeDigitalObject(DigitalObject digOb) {
		if(digOb.getFormat()==null) {
			return false;
		}
		return digOb.getFormat().equals(zipType);
	}
	
	/**
	 * test if this is a folder type DigitalObject (format-URL == planets:fmt/folder))
	 * @param digOb the DigitalObject to test
	 * @return "true" if the digOb is of type zip, "false" if not ;-)
	 */
	public static boolean isFolderTypeDigitalObject(DigitalObject digOb) {
		if(digOb.getFormat()==null) {
			return false;
		}
		return digOb.getFormat().equals(format.createFolderTypeUri());
	}
	
	
	private static File getZipAsFile(DigitalObject digOb) {
	    String folderName = FileUtils.randomizeFileName(getFolderNameFromDigObject(digOb));
	    
		File tmpFolder = FileUtils.createFolderInWorkFolder(utils_tmp, folderName);
		
		File zip = new File(tmpFolder, getFileNameFromDigObject(digOb, null));
		
		FileUtils.writeInputStreamToFile(digOb.getContent().read(), zip);
		
		return zip;
	}

	/**
	 * Creates a DigitalObject from a given file, by reference or as stream
	 * @param file the file to create the DigitalObject from
	 * @param createByReference create by reference (true) or as stream (false)
	 * @return
	 */
	private static DigitalObject createDigitalObject(File file, boolean createByReference) {
		DigitalObject result = null;
		if(file.isDirectory()) {
			result = createFolderTypeDigitalObject(file, createByReference);
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
	 * Get the file name from a Fragment-Id
	 * @param fragment the fragment to get the name from
	 * @return the name of the file specified by this fragment
	 */
	private static String getFragmentName(Fragment fragment) {
		String fragmentID = fragment.getId();
		String name = null;
		if(fragmentID.contains(File.separator)) {
			name = fragmentID.substring(fragmentID.lastIndexOf(File.separator)+1);
			return name;
		}
		else {
			return fragmentID;
		}
	}


	
	/**
	 * Retrieves all files in a folder type DigitalObject recursively and returns them as a list.
	 * @param digOb the DigitalObject to retrieve the files from
	 * @return a list of files contained in this folder-type DigitalObject
	 */
	private static List<File> getAllFilesFromFolderTypeDigitalObject(DigitalObject digOb) {
		if(!digOb.getFormat().equals(folderType)) {
			return null;
		}
		else {
			FileUtils.deleteAllFilesInFolder(utils_tmp);
			File tmpFolder = FileUtils.createFolderInWorkFolder(utils_tmp, FileUtils.randomizeFileName("all-files-from-folder-digob"));
			List<File> allContainedFiles = new ArrayList<File> ();
			return getAllFilesFromContainerDigitalObject(digOb, tmpFolder, allContainedFiles);
		}
	}
	
	/**
	 * Retrieves all files in a container type DigitalObject recursively and returns them as a list.
	 * @param digOb the DigitalObject to retrieve the files from
	 * @return a list of files contained in this container-type DigitalObject
	 */
	private static List<File> getAllFilesFromContainerDigitalObject(DigitalObject digOb, File targetFolder, List<File> allContainedFiles) {
		if(isContainerTypeDigitalObject(digOb)) {
			File topFolder = FileUtils.createFolderInWorkFolder(targetFolder, FileUtils.randomizeFileName(getFolderNameFromDigObject(digOb)));
			List<DigitalObject> containedDigObs = digOb.getContained();
			
			for (DigitalObject digitalObject : containedDigObs) {
				getAllFilesFromContainerDigitalObject(digitalObject, topFolder, allContainedFiles);
			}
		}
		else {
			File currentContent = new File(targetFolder, getFileNameFromDigObject(digOb, null));
			FileUtils.writeInputStreamToFile(digOb.getContent().read(), currentContent);
			allContainedFiles.add(currentContent);
		}
		return allContainedFiles;
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
	 * @param supposedFormatURI TODO
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
}
