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
	
	public OdfContentHandler(File odfFile) {
		xmlComponents = extractXmlFiles(odfFile);
	}
	
	public static String CONTENT_XML = "content.xml";
	public static String STYLES_XML = "styles.xml";
	public static String META_XML = "meta.xml";
	public static String SETTINGS_XML = "settings.xml";
	public static String MANIFEST_XML = "manifest.xml";
	
	private static List<File> xmlComponents = null;
	
	private String version = null;
	
	private static File ODF_VALIDATOR_TMP = null;
	
	private static Log log = LogFactory.getLog(OdfContentHandler.class);

	private List<File> extractXmlFiles(File odfFile) {
		List<File> odfXmlParts = new ArrayList<File>(); 
		ODF_VALIDATOR_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
		File xmlTmp = FileUtils.createFolderInWorkFolder(ODF_VALIDATOR_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		FileUtils.deleteAllFilesInFolder(xmlTmp);
		
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		if(files.length==0) {
			log.error("[OdfContentHandler] extractXmlFiles(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			return new ArrayList<File>();
		}
		
		for (String currentFragment : files) {
			if(currentFragment.endsWith(CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				version = getOdfVersion(tmpContent);
				odfXmlParts.add(tmpContent);
				continue;
			}
			if(currentFragment.endsWith(META_XML) 
					|| currentFragment.endsWith(SETTINGS_XML)
					|| currentFragment.endsWith(MANIFEST_XML)
					|| currentFragment.endsWith(STYLES_XML)) {
				odfXmlParts.add(ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp));
			}
		}
		return odfXmlParts;
	}
	
	public List<File> getXmlComponents() {
		return xmlComponents;
	}
	
	public String getOdfVersion() {
		return version;
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
