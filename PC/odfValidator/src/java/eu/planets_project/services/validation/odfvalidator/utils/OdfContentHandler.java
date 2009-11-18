package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	private static List<File> xmlComponents = null;
	private static List<String> manifestEntries = null;
	private static List<String> missingFileEntries = null;
	
	private static File manifestXml = null;
	
	private static String mimeType = null;
	
	private boolean isNotODF = false;

	private String version = null;
	
	private static File ODF_VALIDATOR_TMP = null;
	
	private static Log log = LogFactory.getLog(OdfContentHandler.class);
	
	
	/**
	 * Constructor. Is initialized with the odfFile to be examined. 
	 * @param odfFile the odf file to validate
	 */
	public OdfContentHandler(File odfFile) {
		xmlComponents = initialize(odfFile);
		
		if(!isNotODF) {
			missingFileEntries = checkContainerConformity(odfFile);
		}
	}
	
	public boolean isOdfFile() {
		return !isNotODF;
	}

	public List<File> getXmlComponents() {
		return xmlComponents;
	}

	public String getOdfVersion() {
		return version;
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
		
		if(files.length==0) {
			log.error("[OdfContentHandler] extractXmlFiles(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			isNotODF = true;
			return odfXmlParts;
		}
		
		for (String currentFragment : files) {
			if(currentFragment.endsWith(MIMETYPE)) {
				File tmpMimetype = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				mimeType = FileUtils.readTxtFileIntoString(tmpMimetype);
				continue;
			}
			if(currentFragment.endsWith(CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				version = getOdfVersion(tmpContent);
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
		manifestEntries = getManifestEntries(manifestXml);
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
	
	private String getOdfVersion(File contentXml) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(contentXml);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			Attribute officeVersion = root.getAttribute("version", ns);
			String version = officeVersion.getValue();
			log.info("[OdfContentHandler] getOdfVersion(): Found ODF version = " + version);
			return version;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
