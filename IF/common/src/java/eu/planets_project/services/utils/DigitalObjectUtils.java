/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public final class DigitalObjectUtils {
    private DigitalObjectUtils() {/* Util classes are not instantiated */}

    private static final Log LOG = LogFactory.getLog(DigitalObjectUtils.class);
    
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
     * @param files The files to wrap as stram-based digital objects
     * @return A list of digital object wrapping the given files, streaming the
     *         content when read
     */
    public static List<DigitalObject> createContainedAsStream(
            final List<File> files) {
    	FormatRegistry formatReg = FormatRegistryFactory.getFormatRegistry(); 
        List<DigitalObject> list = new ArrayList<DigitalObject>();
        for (File file : files) {
            DigitalObject currentDigObj = new DigitalObject.Builder(
                    ImmutableContent.asStream(file))
                    .title(file.getName())
                    .format(formatReg.createExtensionUri(FileUtils.getExtensionFromFile(file)))
                    .build();
            list.add(currentDigObj);
        }
        return list;
    }
    
    /**
     * @param files The files to wrap as stram-based digital objects
     * @return A list of digital object wrapping the given files, streaming the
     *         content when read
     */
    public static List<DigitalObject> createContainedbyReference(
            final List<File> files) {
    	FormatRegistry formatReg = FormatRegistryFactory.getFormatRegistry(); 
        List<DigitalObject> list = new ArrayList<DigitalObject>();
        for (File file : files) {
            DigitalObject currentDigObj = new DigitalObject.Builder(
                    ImmutableContent.byReference(file))
                    .title(file.getName())
                    .format(formatReg.createExtensionUri(FileUtils.getExtensionFromFile(file)))
                    .build();
            list.add(currentDigObj);
        }
        return list;
    }

    /**
     * @param listOfDigObjs The digital objects
     * @param targetFolder The folder to store result files in
     * @return The child elements of the given digital object as files
     */
    public static List<File> getContainedAsFiles(
            final List<DigitalObject> listOfDigObjs, final File targetFolder) {
        List<File> containedFiles = new ArrayList<File>();
        if (listOfDigObjs.size() > 0) {
            for (DigitalObject currentDigObj : listOfDigObjs) {
                String name = currentDigObj.getTitle();
                if (name == null) {
                    return null;
                }
                containedFiles.add(FileUtils.writeInputStreamToFile(
                        currentDigObj.getContent().read(), targetFolder, name));
            }
        }
        LOG.debug(String.format("Returning %s files", containedFiles.size()));
        return containedFiles;
    }

    /**
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
    
    public static DigitalObject getZipDigitalObjectFromFolder(File folder, String destZipName, boolean createByReference) {
    	FormatRegistry formatReg = FormatRegistryFactory.getFormatRegistry(); 
    	List<DigitalObject> containedDigObs = null;
    	List<File> filesInFolder = new ArrayList<File>();
    	FileUtils.listAllFiles(folder, filesInFolder);
    	String zipName = null;
    	if(destZipName==null) {
    		zipName = folder.getName() + ".zip";
    	}
    	else {
    		zipName = null;
    		if(destZipName.contains(".")) {
    			String tmpName = destZipName.substring(0, destZipName.lastIndexOf(".")) + ".zip";
    			zipName = tmpName;
    		}
    		else {
    			zipName = destZipName + ".zip";
    		}
    		
    	}
    	
    	if(createByReference) {
    		containedDigObs = createContainedbyReference(filesInFolder);
    		File tmp = FileUtils.createWorkFolderInSysTemp("DigitalObjectUtils-tmp");
        	ZipResult zipResult = FileUtils.createZipFileWithChecksum(folder, tmp, zipName);
    		DigitalObject digOb = new DigitalObject.Builder(ImmutableContent.byReference(zipResult.getZipFile())
					.withChecksum(zipResult.getChecksum()))
					.title(zipName)
					.format(formatReg.createExtensionUri("zip"))
					.contains(containedDigObs.toArray(new DigitalObject[]{}))
					.build();
    		return digOb;
    	}
    	else {
    		containedDigObs = createContainedAsStream(filesInFolder);
    		File tmpFolder = FileUtils.createWorkFolderInSysTemp("DigitalObjectUtils-tmp");
        	ZipResult zipResult = FileUtils.createZipFileWithChecksum(folder, tmpFolder, zipName);
    		DigitalObject digOb = new DigitalObject.Builder(ImmutableContent.byValue(zipResult.getZipFile())
					.withChecksum(zipResult.getChecksum()))
					.title(zipName)
					.format(formatReg.createExtensionUri("zip"))
					.contains(containedDigObs.toArray(new DigitalObject[]{}))
					.build();
    		return digOb;
    	}
    }

}
