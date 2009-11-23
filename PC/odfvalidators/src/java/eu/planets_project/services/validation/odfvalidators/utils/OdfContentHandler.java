package eu.planets_project.services.validation.odfvalidators.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ZipUtils;

public class OdfContentHandler {
	
	private static final String MIMETYPE = "mimetype";
	public static String CONTENT_XML = "content.xml";
	public static String STYLES_XML = "styles.xml";
	public static String META_XML = "meta.xml";
	public static String SETTINGS_XML = "settings.xml";
	public static String MANIFEST_XML = "manifest.xml";
	
	private static final String MATHML_MIMETYPE = "application/vnd.oasis.opendocument.formula";
	
//	private static List<File> xmlComponents = null;
	private static List<String> manifestEntries = null;
	private static List<String> missingFileEntries = null;
	
	private static HashMap<String, File> odfSubFiles = null;
	
	private static File manifestXml = null;
	
	private static File mimetype = null;
	
	private static String mimeType = null;
	
	private boolean mimeTypeVerified = false;
	
	private String manifestMimeType = null;
	
	private boolean isNotODF = false;

	private String odfVersion = null;
	private String mathMLVersion = null;
	
	private static File ODF_VALIDATOR_TMP = null;
	private static File xmlTmp = null;
	
	private static Log log = LogFactory.getLog(OdfContentHandler.class);
	
	
	/**
	 * Constructor. Is initialized with the odfFile to be examined. 
	 * @param odfFile the odf file to validate
	 */
	public OdfContentHandler(File odfFile) {
		ODF_VALIDATOR_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
		xmlTmp = FileUtils.createFolderInWorkFolder(ODF_VALIDATOR_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		FileUtils.deleteAllFilesInFolder(xmlTmp);
		
		// 1) get all Odf sub files from zp container
		odfSubFiles = extractOdfSubFiles(odfFile);
		
		if(!isNotODF) {
			// read the Odf mimeType 
			mimeType = getMimeType(mimetype);
			
			// if the mimetype indicates a MathML file, retrieve the MathML version for schema handling
			// AND the ODF version for the other subfiles...
			if(mimeType.equalsIgnoreCase(OdfContentHandler.MATHML_MIMETYPE)) {
				mathMLVersion = getMathMLVersion(odfSubFiles.get("content"));
				
				Set<String> keys = odfSubFiles.keySet();
				
				for (String string : keys) {
					if(string.equalsIgnoreCase("settings") 
							|| string.equalsIgnoreCase("styles") 
							|| string.equalsIgnoreCase("meta")) {
						odfVersion = getOdfVersion(odfSubFiles.get(string));
						break;
					}
				}
			}
			// else, just retrieve the Odf version for schema handling
			else {
				odfVersion = getOdfVersion(odfSubFiles.get("content"));
			}
			
			// check the container integrity, i.e.:
			// 1) check, if all entries in META-INF/manifest.xml are present in
			// the odf container.
			// 2) check if the manifest.xml mimetype is the same as the subfile mimetype. If not, 
			// log out a warning.
			
			missingFileEntries = checkContainerConformity(odfFile);
		}
	}
	
	public static File getXmlTmpDir() {
		return xmlTmp;
	}
	
	private HashMap<String, File> extractOdfSubFiles(File odfFile) {
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		// if there are no files contained, it's not an Odf file ;-)
		// return an empty list and say sorry...
		if(files.length==0) {
			log.error("[OdfContentHandler] extractOdfSubFiles(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			isNotODF = true;
			return new HashMap<String, File>();
		}
		
		HashMap<String, File> subFilesTmp = new HashMap<String, File>();
		for (String currentEntry : files) {
			
			if(currentEntry.endsWith(OdfContentHandler.MIMETYPE)) {
				mimetype = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("content", tmpContent);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.SETTINGS_XML)) {
				File tmpSettings = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("settings", tmpSettings);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.STYLES_XML)) {
				File tmpStyles = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("styles", tmpStyles);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.MANIFEST_XML)) {
				File tmpManifest = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("manifest", tmpManifest);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.META_XML)) {
				File tmpMeta = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("meta", tmpMeta);
				continue;
			}
		}
		return subFilesTmp;
	}
	
	
	private String getMimeType(File odfMimeType) {
		String mime = FileUtils.readTxtFileIntoString(odfMimeType);
		return mime;
	}
	
	public boolean isMimeTypeVerified() {
		return mimeTypeVerified;
	}
	
	public String getManifestMimeType() {
		return manifestMimeType;
	}
	
	private String getMathMLVersion(File contentXml) {
		String contentString = FileUtils.readTxtFileIntoString(contentXml);
		String[] parts = contentString.split(" ");
		String version = null;
		for (int i=0;i<parts.length;i++) {
			if(parts[i].equalsIgnoreCase("MathML")) {
				version = parts[i+1];
				version = "MathML:" + version.substring(0, version.lastIndexOf("//"));
				break;
			}
		}
		log.info("[OdfContentHandler] getMathMLVersion(): Found MathML version = " + version);
		return version;
	}
	
	
	private String getOdfVersion(File odfSubFile) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(odfSubFile);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			Attribute officeVersion = root.getAttribute("version", ns);
			String version = "ODF:" + officeVersion.getValue();
			log.info("[OdfContentHandler] getOdfVersion(): Found ODF version = " + version);
			return version;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isOdfFile() {
		return !isNotODF;
	}

	public List<File> getXmlComponents() {
		List<File> subFiles = new ArrayList<File>(odfSubFiles.values());
		return subFiles;
	}

	public String getOdfVersion() {
		return odfVersion;
	}
	
	public String getMathMLVersion() {
		return mathMLVersion;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public boolean isOdfCompliant() {
		if(missingFileEntries.size()==0) {
			return true;
		}
		else {
			return false;
		}
	}

	public List<String> getMissingManifestEntries() {
		return missingFileEntries;
	}

//	public List<String> getManifestEntries() {
//		return manifestEntries;
//	}

	private List<File> initialize(File odfFile) {
		List<File> odfXmlParts = new ArrayList<File>(); 
		ODF_VALIDATOR_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
		File xmlTmp = FileUtils.createFolderInWorkFolder(ODF_VALIDATOR_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		FileUtils.deleteAllFilesInFolder(xmlTmp);
		
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		// Determine the MimeType
		for (String string : files) {
			if(string.endsWith(MIMETYPE)) {
				File tmpMimetype = ZipUtils.getFileFrom(odfFile, string, xmlTmp);
				mimeType = FileUtils.readTxtFileIntoString(tmpMimetype);
				continue;
			}
		}
		
		if(files.length==0) {
			log.error("[OdfContentHandler] intialize(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			isNotODF = true;
			return odfXmlParts;
		}
		
		for (String currentFragment : files) {
			if(currentFragment.endsWith(CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				odfVersion = getOdfVersion(tmpContent);
				odfXmlParts.add(tmpContent);
				continue;
			}
			if(currentFragment.endsWith(MANIFEST_XML)) {
				manifestXml = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				odfXmlParts.add(manifestXml);
				continue;
			}
			if(currentFragment.endsWith(META_XML) 
					|| currentFragment.endsWith(SETTINGS_XML)
					|| currentFragment.endsWith(STYLES_XML)) {
				odfXmlParts.add(ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp));
				continue;
			}
		}
		return odfXmlParts;
	}
	
	private List<String> checkContainerConformity(File odfFile) {
		mimeTypeVerified = verifyManifestMimeType(mimeType, odfSubFiles.get("manifest"));
		
		manifestEntries = getManifestEntries(odfSubFiles.get("manifest"));
		List<String> missingList = new ArrayList<String>();
		if(!isNotODF) {
			List<String> odfContent = ZipUtils.listZipEntries(odfFile);
			
			for (String currentManifestEntry : manifestEntries) {
				if(!odfContent.contains(currentManifestEntry)) {
					if(!currentManifestEntry.contains("META-INF") && !currentManifestEntry.equalsIgnoreCase("/")) {
						missingList.add(currentManifestEntry);
					}
				}
			}
		}
		return missingList;
	}
	
	private List<String> getManifestEntries(File manifestXml) {
		List<String> manifestEntries = new ArrayList<String>(); 
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(manifestXml);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			List<Element> fileEntries = root.getChildren("file-entry", ns);
			for (Element currentElement : fileEntries) {
				Attribute fileEntryFullPath = currentElement.getAttribute("full-path", ns);
				manifestEntries.add(fileEntryFullPath.getValue());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return manifestEntries;
	}

	private boolean verifyManifestMimeType(String mimeType, File manifest) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		String mediaType = null;
		try {
			doc = builder.build(manifest);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			List<Element> fileEntries = root.getChildren("file-entry", ns);
			
			for (Element currentElement : fileEntries) {
				mediaType = currentElement.getAttributeValue("media-type", ns);
				String path = currentElement.getAttributeValue("full-path", ns);
				if(path.equalsIgnoreCase("/") && mediaType.equalsIgnoreCase(mimeType)) {
					log.info("SUCCESS!!! META-INF/MANIFEST.XML mimeType verified!");
					return true;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.warn("Warning: META-INF/MANIFEST.XML mimetype should be: '" + mimeType + "', BUT is: '" + mediaType + "!");
		manifestMimeType = mediaType;
		return false;
	}

}
