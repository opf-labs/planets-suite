package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ZipUtils;

public class OdfContentHandler {
	
	private static String MIMETYPE_XML = "mimetype";
	public static String CONTENT_XML = "content.xml";
	public static String STYLES_XML = "styles.xml";
	public static String META_XML = "meta.xml";
	public static String SETTINGS_XML = "settings.xml";
	public static String MANIFEST_XML = "manifest.xml";
	public static String DOC_DSIGS_XML = "documentsignatures.xml";
	public static String MACRO_DSIGS_XML = "macrosignatures.xml";
	
	private static final String MATHML_MIMETYPE = "application/vnd.oasis.opendocument.formula";
	private static final String SXM_MATHML_MIMETYPE = "application/vnd.sun.xml.math";
	
	private static List<String> manifestEntries = null;
	private static List<String> missingFileEntries = null;
	private static HashMap<File, String> nsWarningList = null;
	
	private static HashMap<String, List<File>> odfSubFiles = null;
	
	private static File manifestXml = null;
	
	private static File mimetype_file = null;
	
	private static String mimeType_string = null;
	
	private boolean mimeTypeVerified = false;
	
	private boolean detectedDsigSubFiles = false;
	
	private boolean containsEmdeddedMathML = false;
	
	private boolean containsMathMLDoctype = false;
	
	private String manifestMimeType = null;
	
	private boolean isNotODF = false;

	private String odfVersion = null;
	private String mathMLVersion = null;
	
	private static File CONTENT_HANDLER_TMP = null;
	private static File xmlTmp = null;
	
	private static Logger log = Logger.getLogger(OdfContentHandler.class.getName());
	private String generator = null;
	private boolean allNamespacesCorrect = false;
	
	
	/**
	 * Constructor. Is initialized with the odfFile to be examined. 
	 * @param odfFile the odf file to validate
	 */
	public OdfContentHandler(File odfFile) {
		nsWarningList = new HashMap<File, String>();
		CONTENT_HANDLER_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_CONTENT_HANDLER");
		FileUtils.deleteAllFilesInFolder(CONTENT_HANDLER_TMP);
		xmlTmp = FileUtils.createFolderInWorkFolder(CONTENT_HANDLER_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		
		// 1) get all Odf sub files from zip container
		odfSubFiles = extractOdfSubFiles(odfFile);
		
		generator = getGenerator(odfSubFiles);
		
		containsEmdeddedMathML = checkForEmbeddedMathML(odfSubFiles);
		
		containsMathMLDoctype = checkForMathMLDoctype(odfSubFiles);
		
		if(!isNotODF) {
			
			getVersions(odfSubFiles);
			
			missingFileEntries = checkContainerConformity(odfFile);
			
			boolean deleted = odfFile.delete();
			
			allNamespacesCorrect = validateNamespaces(odfSubFiles);
		}
	}
	
	public boolean allNamespacesCorrect() {
		return allNamespacesCorrect;
	}
	
	public HashMap<File, String> getNsWarnings () {
		return nsWarningList;
	}
	
	private boolean validateNamespaces(HashMap<String, List<File>> odfSubFiles) {
		Set<String> entries = odfSubFiles.keySet();
		List<File> allFiles = new ArrayList<File>();
		// get all included sub files
		for (String entry : entries) {
			allFiles.addAll(odfSubFiles.get(entry));
		}
		
		List<Boolean> success = new ArrayList<Boolean>();
		for (File file : allFiles) {
			 success.add(Boolean.valueOf(checkNamespaces(file, odfVersion)));
		}
		return !success.contains(Boolean.FALSE);
	}
	
	private boolean checkNamespaces(File odfSubFile, String version) {
		SAXBuilder builder = new SAXBuilder(false);
		builder.setValidation(false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
		Document doc = null;
		List<Namespace> namespaces = new ArrayList<Namespace>();
		try {
			doc = builder.build(odfSubFile);
			Element root = doc.getRootElement();
			namespaces.add(root.getNamespace());
			namespaces.addAll(root.getAdditionalNamespaces());
			for (Namespace namespace : namespaces) {
				String prefix = namespace.getPrefix();
				String value = namespace.getURI();
				if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_0)) {
					if(OdfSchemaHandler.v10_namespaces.containsKey(prefix)) {
						String testValue = OdfSchemaHandler.v10_namespaces.get(prefix);
						if(!testValue.equalsIgnoreCase(value)) {
							nsWarningList.put(odfSubFile, "[WARNING] ODF v1.0 Namespace incorrect: Prefix '" + prefix + "'" +
									" should be mapped to '" + testValue + " but actually points to '" + value + "' !!!");
						}
					}
					continue;
				}
				if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_1)) {
					if(OdfSchemaHandler.v11_namespaces.containsKey(prefix)) {
						String testValue = OdfSchemaHandler.v11_namespaces.get(prefix);
						if(!testValue.equalsIgnoreCase(value)) {
							nsWarningList.put(odfSubFile, "[WARNING] ODF v1.1 Namespace incorrect: Prefix '" + prefix + "'" +
									" should be mapped to '" + testValue + " but actually points to '" + value + "' !!!");
						}
					}
					continue;
				}
				if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2)) {
					if(OdfSchemaHandler.v12_namespaces.containsKey(prefix)) {
						String testValue = OdfSchemaHandler.v12_namespaces.get(prefix);
						if(!testValue.equalsIgnoreCase(value)) {
							nsWarningList.put(odfSubFile, "[WARNING] ODF v1.2 Namespace incorrect: Prefix '" + prefix + "'" +
									" should be mapped to '" + testValue + " but actually points to '" + value + "' !!!");
						}
					}
					continue;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nsWarningList.size()==0;
	}
	
	private String getGenerator(HashMap<String, List<File>> odfSubFiles) {
		if(odfSubFiles.containsKey("meta")) {
			SAXBuilder builder = new SAXBuilder(false);
			builder.setValidation(false);
			builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			builder.setFeature("http://xml.org/sax/features/validation", false);
			Document doc = null;
			try {
				doc = builder.build(odfSubFiles.get("meta").get(0));
				Element root = doc.getRootElement();
				Namespace meta_ns = root.getNamespace("meta");
				Namespace office_ns = root.getNamespace("office");
				Element office = root.getChild("meta", office_ns);
				Element generator = office.getChild("generator", meta_ns);
				String generatorStr = generator.getTextTrim();
				return generatorStr;
			} catch (JDOMException e) {
				return "unknown";
			} catch (IOException e) {
				return "unknown";
			}
		}
		else {
			return "unknown";
		}
	}
	
	public String getOdfGenerator() {
		return generator;
	}
	
	private boolean checkForMathMLDoctype(HashMap<String, List<File>> odfSubFiles) { 
		List<File> subFiles = getOdfSubFiles();
		HashMap<File, Boolean> doctypeContainingFiles = new HashMap<File, Boolean>();
		for (File file : subFiles) {
			boolean docTypeContained = containsDocTypeDeclaration(file);
			doctypeContainingFiles.put(file, Boolean.valueOf(docTypeContained));
		}
		
		return doctypeContainingFiles.containsValue(Boolean.TRUE);
	}
	
	
	private boolean checkForEmbeddedMathML(HashMap<String, List<File>> odfSubFiles) {
		List<File> subFiles = getOdfSubFiles();
		HashMap<File, Boolean> mathMLContainingFiles = new HashMap<File, Boolean>();
		
		for (File file : subFiles) {
			boolean mathMLEmbedded = subFileContainsMathML(file);
			mathMLContainingFiles.put(file, Boolean.valueOf(mathMLEmbedded));
		}
		
		return mathMLContainingFiles.containsValue(Boolean.TRUE);
	}
	
	public File getCurrentXmlTmpDir() {
		return xmlTmp;
	}
	
	private HashMap<String, List<File>> extractOdfSubFiles(File odfFile) {
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		// if there are no files contained, it's not an Odf file ;-)
		// return an empty list and say sorry...
		if(files.length==0) {
			log.severe("[OdfContentHandler] extractOdfSubFiles(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			isNotODF = true;
			return new HashMap<String, List<File>>();
		}
		
		HashMap<String, List<File>> subFilesTmp = new HashMap<String, List<File>>();
		for (String currentEntry : files) {
			
			if(currentEntry.endsWith(OdfContentHandler.MIMETYPE_XML)) {
				mimetype_file = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("content");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpContent);
				subFilesTmp.put("content", tmpList);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.SETTINGS_XML)) {
				File tmpSettings = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("settings");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpSettings);
				subFilesTmp.put("settings", tmpList);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.STYLES_XML)) {
				File tmpStyles = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("styles");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpStyles);
				subFilesTmp.put("styles", tmpList);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.MANIFEST_XML)) {
				File tmpManifest = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("manifest");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpManifest);
				subFilesTmp.put("manifest", tmpList);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.META_XML)) {
				File tmpMeta = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("meta");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpMeta);
				subFilesTmp.put("meta", tmpList);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.DOC_DSIGS_XML)) {
				File tmpDocDsig = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("doc_dsigs");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpDocDsig);
				subFilesTmp.put("doc_dsigs", tmpList);
				detectedDsigSubFiles = true;
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.MACRO_DSIGS_XML)) {
				File tmpMacroDsig = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				List<File> tmpList = subFilesTmp.get("macro_dsigs");
				if(tmpList==null) {
					tmpList = new ArrayList<File>();
				}
				tmpList.add(tmpMacroDsig);
				subFilesTmp.put("macro_dsigs", tmpList);
				detectedDsigSubFiles = true;
				continue;
			}
		}
		return subFilesTmp;
	}
	
	public boolean containsDsigSubFiles() {
		return detectedDsigSubFiles;
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
			String debug = parts[i];
			if(parts[i].equalsIgnoreCase("MathML")) {
				version = parts[i+1];
				version = "MathML:" + version.substring(0, version.lastIndexOf("//"));
				break;
			}
			else {
				version = "MathML:unknown";
			}
		}
		log.info("[OdfContentHandler] getMathMLVersion(): Found MathML version = " + version);
		return version;
	}
	
	public boolean containsDocTypeDeclaration() {
		return containsMathMLDoctype;
	}
	
	private boolean containsDocTypeDeclaration(File mathmlXml) {
		String contentString = FileUtils.readTxtFileIntoString(mathmlXml);
		String docTypePattern = "<!DOCTYPE";
		return contentString.contains(docTypePattern);
	}
	
	
	public File cleanUpXmlForValidation(File mathmlXML) {
		String contentString = FileUtils.readTxtFileIntoString(mathmlXML);
		contentString = contentString.replaceAll(">", ">" + System.getProperty("line.separator"));
		String[] lines = contentString.split(System.getProperty("line.separator"));
		String docTypePattern = "<!DOCTYPE";
		
		String dest = "<math:math xmlns:math=\"http://www.w3.org/1998/Math/MathML\"" 
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://www.w3.org/1998/Math/MathML http://www.w3.org/Math/XMLSchema/mathml2/mathml2.xsd\">";
		
		for (int i=0;i<lines.length;i++) {
			if(lines[i].contains(docTypePattern)) {
				lines[i]="";
				continue;
			}
		}
		
		StringBuffer content = new StringBuffer();
		
		for (String string : lines) {
			content.append(string.trim());
		}
		
		File cleanedTmp = new File(xmlTmp, FileUtils.randomizeFileName(mathmlXML.getName()));
		FileUtils.writeStringToFile(content.toString(), cleanedTmp);
		
		return cleanedTmp;
	}
	
	private String getOdfVersion(File odfSubFile) {
		SAXBuilder builder = new SAXBuilder(false);
		builder.setValidation(false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
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
	
	public boolean subFileContainsMathML(File odfSubfile) {
		String content = FileUtils.readTxtFileIntoString(odfSubfile);
		if(content.contains("xmlns=\"http://www.w3.org/1998/Math/MathML\"")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean containsEmbeddedMathML() {
		return containsEmdeddedMathML;
	}

	public List<File> getOdfSubFiles() {
		List<File> subFiles = new ArrayList<File>();
		Set<String> keys = odfSubFiles.keySet();
		for (String string : keys) {
			subFiles.addAll(odfSubFiles.get(string));
		}
		return subFiles;
	}

	private void getVersions(HashMap<String, List<File>> odfSubFiles) {
		// read the Odf mimeType 
		mimeType_string = getMimeType(mimetype_file);
		
		// if the mimetype indicates a MathML file, retrieve the MathML version for schema handling
		// AND the ODF version for the other subfiles...
		if(mimeType_string.equalsIgnoreCase(OdfContentHandler.MATHML_MIMETYPE)
				|| this.containsMathMLDoctype
				|| this.containsEmdeddedMathML) {
				
			mathMLVersion = getMathMLVersion(odfSubFiles.get("content").get(0)); 
			
			// And now check the version of the other files...
			Set<String> keys = odfSubFiles.keySet();
			
			for (String string : keys) {
				if(string.equalsIgnoreCase("settings") 
						|| string.equalsIgnoreCase("styles") 
						|| string.equalsIgnoreCase("meta")) {
					odfVersion = getOdfVersion(odfSubFiles.get(string).get(0));
					break;
				}
			}
		}
		// if we don't have a formula file here just retrieve the Odf version from 'content.xml' for schema handling
		else {
			odfVersion = getOdfVersion(odfSubFiles.get("content").get(0));
		}
	}

	public String getOdfVersion() {
		return odfVersion;
	}
	
	public String getMathMLVersion() {
		if(mathMLVersion!=null) {
			return mathMLVersion;
		}
		else {
			return "unknown";
		}
	}
	
	public String getMimeType() {
		return mimeType_string;
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


	private List<File> initialize(File odfFile) {
		List<File> odfXmlParts = new ArrayList<File>(); 
		CONTENT_HANDLER_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
		File xmlTmp = FileUtils.createFolderInWorkFolder(CONTENT_HANDLER_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		FileUtils.deleteAllFilesInFolder(xmlTmp);
		
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		// Determine the MimeType
		for (String string : files) {
			if(string.endsWith(MIMETYPE_XML)) {
				File tmpMimetype = ZipUtils.getFileFrom(odfFile, string, xmlTmp);
				mimeType_string = FileUtils.readTxtFileIntoString(tmpMimetype);
				continue;
			}
		}
		
		if(files.length==0) {
			log.severe("[OdfContentHandler] intialize(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
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
		mimeTypeVerified = verifyManifestMimeType(mimeType_string, odfSubFiles.get("manifest").get(0));
		
		manifestEntries = getManifestEntries(odfSubFiles.get("manifest").get(0));
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
		if(missingList.size()==0) {
			log.info("[OdfContentHandler] checkContainerIntegrity(): Congratulations!!! Successfully checked container integrity of file '" + odfFile.getName() + "'");
		}
		return missingList;
	}
	
	
	private List<String> getManifestEntries(File manifestXml) {
		List<String> manifestEntries = new ArrayList<String>(); 
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
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
		builder.setValidation(false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
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
					log.info("[OdfContentHandler] verifyManifestMimeType(): SUCCESS!!! 'META-INF/manifest.xml' mimeType verified!");
					return true;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.warning("Mismatch warning: 'META-INF/manifest.xml' mimetype should be: '" + mimeType + "', BUT is: '" + mediaType + "!");
		manifestMimeType = mediaType;
		return false;
	}

}
