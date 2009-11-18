package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import eu.planets_project.services.utils.FileUtils;

/**
 * @author melmsp
 *
 */
public class OdfValidatorResult {
	
	private boolean isOdfFile = false;
	
	private File odfInputFile = null;
	
	private List<File> xmlComponents = new ArrayList<File>();
	private HashMap<File, Boolean> validatedList = new HashMap<File, Boolean>();
	private HashMap<File, String> errorList = new HashMap<File, String>();
	private List<File> invalidComponents = new ArrayList<File>();
	private List<String> missingManifestEntries = new ArrayList<String>();
	private HashMap<String, File> schemaFiles = new HashMap<String, File>();
	
	private boolean isOdfCompliant = false;
	
	private boolean usedStrictValidation = false;
	
	private String odfVersion = "unknown";
	
	private String mimeType = "unknown";
	
	private String NEWLINE = System.getProperty("line.separator");
	
	public OdfValidatorResult(File odfInputFile) {
		this.odfInputFile = odfInputFile;
	}
	
	public boolean componentIsValid(File odfXmlComponent) {
		Boolean valid = validatedList.get(odfXmlComponent);
		return valid.booleanValue();
	}

	public boolean documentIsValid() {
			if(validatedList.size() == 0 || validatedList.containsValue(Boolean.FALSE)) {
				return false;
			}
			else {
				return true;
			}
		}

	public String getComponentError(File odfXmlComponent) {
		String odfXmlComponentError = "";
		if(errorList.containsKey(odfXmlComponent)) {
			odfXmlComponentError = "ERROR for '" + odfXmlComponent.getName() + "': " + errorList.get(odfXmlComponent);
		}
		return odfXmlComponentError;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	public File getOdfInputFile() {
		return this.odfInputFile;
	}

	public String getOdfVersion() {
		return odfVersion;
	}
	
	public List<String> getSchemaNames() {
		List<String> schemas = new ArrayList<String>();
		
		Set<String> schemaKeys = schemaFiles.keySet();
		for (String string : schemaKeys) {
			if(string.equalsIgnoreCase("doc")) {
				schemas.add("[Document schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase("userDoc")) {
				schemas.add("[User Doc Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase("strict")) {
				schemas.add("[Strict Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase("userStrict")) {
				schemas.add("[User Strict Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase("manifest")) {
				schemas.add("[Manifest Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase("userManifest")) {
				schemas.add("[User Manifest Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
		}
		return schemas;
	}
	
	public List<File> getSchemaFiles() {
		List<File> resultList = new ArrayList<File>();
		List<File> schemas = new ArrayList<File>(this.schemaFiles.values());
		if(schemas.size()>0) {
			resultList.addAll(schemas);
		}
		return resultList;
	}
	
	public List<String> getSchemasAsString() {
		List<String> schemaContents = new ArrayList<String>();
		
		Set<String> schemaEntries = schemaFiles.keySet();
		
		for (String label : schemaEntries) {
			if(label.equalsIgnoreCase("doc")) {
				String tmpDoc = "[Document Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpDoc);
			}
			if(label.equalsIgnoreCase("userDoc")) {
				String tmpUserDoc = "[User Document Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserDoc);
			}
			if(label.equalsIgnoreCase("strict")) {
				String tmpStrict = "[Strict Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpStrict);
			}
			if(label.equalsIgnoreCase("userStrict")) {
				String tmpUserStrict = "[User Strict Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserStrict);
			}
			if(label.equalsIgnoreCase("manifest")) {
				String tmpManifest = "[Manifest Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpManifest);
			}
			if(label.equalsIgnoreCase("userManifest")) {
				String tmpUserManifest = "[User Manifest Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserManifest);
			}
		}
		return schemaContents;
	}
	

	public String getValidationResultAsString () {
		StringBuffer buf = new StringBuffer();
		buf.append(NEWLINE);
		buf.append(NEWLINE);
		buf.append("========== Validation Results ==========" + NEWLINE);
		buf.append("---------- General Information ----------" + NEWLINE);
		buf.append("[getOdfInputFile()] = " + odfInputFile.getName() + NEWLINE);
		buf.append("[getMimeType()] = " + this.getMimeType() + NEWLINE);
		buf.append("[getOdfVersion()] = " + this.getOdfVersion() + NEWLINE);
		buf.append("[usedStrictValidation()] = " + this.usedStrictValidation() + NEWLINE);
		buf.append("---------- Used Schemas ----------" + NEWLINE);
		List<String> schemaNames = getSchemaNames();
		for (String schemaName : schemaNames) {
			buf.append(schemaName + NEWLINE);
		}
		buf.append("---------- Odf Component Validity ----------" + NEWLINE);
		for (File component : xmlComponents) {
			buf.append("['" + component.getName() + "' is valid] = " + componentIsValid(component) + NEWLINE);
		}
		buf.append("---------- Document Odf Conformance ----------" + NEWLINE);
		buf.append("[isOdfCompliant()] = " + this.isOdfCompliant + NEWLINE);
		if(!this.isOdfCompliant) {
			for (String currentEntry : missingManifestEntries) {
				buf.append("[Missing Manifest entry] = " + currentEntry);
			}
		}
		buf.append("---------- Document Validity ----------" + NEWLINE);
		buf.append("[IsOdfFile()] = " + this.isOdfFile + NEWLINE);
		buf.append("[documentIsValid()] = " + this.documentIsValid() + NEWLINE);
		if(!this.documentIsValid()) {
			buf.append("---------- Error Messages ----------" + NEWLINE);
			for (File invalidComponent : invalidComponents) {
				String componentName = invalidComponent.getName();
				String error = errorList.get(invalidComponent);
				buf.append("[ERROR " + componentName + "] = " + error + NEWLINE);
			}
		}
		buf.append(NEWLINE);
		buf.append(NEWLINE);
		return buf.toString();
	}

	public List<File> getXmlComponents() {
		return xmlComponents;
	}
	
	public boolean isOdfCompliant() {
		return isOdfCompliant;
	}

	public boolean isOdfFile() {
		if(odfVersion.equalsIgnoreCase("unknown") && !isOdfFile) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void setDocumentSchema(File docSchema) {
		if(docSchema.getName().contains("user")) {
			schemaFiles.put("userDoc", docSchema);
		}
		else {
			schemaFiles.put("doc", docSchema);
		}
	}
	
	public void setStrictDocSchema(File strictSchema) {
		if(strictSchema.getName().contains("user")) {
			schemaFiles.put("userStrict", strictSchema);
		}
		else {
			schemaFiles.put("strict", strictSchema);
		}
	}
	
	public void setManifestSchema(File manifestSchema) {
		if(manifestSchema.getName().contains("user")) {
			schemaFiles.put("userManifest", manifestSchema);
		}
		else {
			schemaFiles.put("manifest", manifestSchema);
		}
	}

	public void setError(File file, String errorMessage) {
		errorList.put(file, errorMessage);
		invalidComponents.add(file);
	}
	
	public void setIsOdfCompliant(boolean compliant) {
		this.isOdfCompliant = compliant;
	}

	public void setIsOdfFile(boolean isOdf) {
		this.isOdfFile = isOdf;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public void setMissingManifestEntries(List<String> missingManifestEntries) {
		this.missingManifestEntries = missingManifestEntries;
	}
	
	public void setOdfInputFile(File odfInputFile) {
		this.odfInputFile = odfInputFile;
	}

	public void setOdfVersion(String odfVersion) {
		this.odfVersion = odfVersion;
	}

	public void setUsedStrictValidation(boolean usedStrictValidation) {
		this.usedStrictValidation = usedStrictValidation;
	}

	public void setValid(File odfXmlComponent, boolean valid) {
		validatedList.put(odfXmlComponent, new Boolean(valid));
	}

	public void setXmlComponents(List<File> xmlParts) {
		xmlComponents = xmlParts;
	}
	
	public boolean usedStrictValidation() {
		return usedStrictValidation;
	}
	
//	public String getContentErrors() {
//		return contentErrors;
//	}
//
//	public String getStylesErrors() {
//		return stylesErrors;
//	}
//
//	public String getMetaErrors() {
//		return metaErrors;
//	}
//
//	public String getSettingsErrors() {
//		return settingsErrors;
//	}
//
//	public String getManifestErrors() {
//		return manifestErrors;
//	}

//	@Override
//	public String toString() {
//		return "OdfValidatorResult ["
//				+ (odfVersion != null ? "odfVersion=" + odfVersion + ", " : "")
//				+ "isOdfFile()=" + isOdfFile() 
//				+ ", getMimeType()=" + getMimeType()
//				+ ", usedStrictValidation()=" + usedStrictValidation() 
//				+ ", documentIsValid()=" + documentIsValid()
//				+ ", isContentValid()=" + isContentValid() 
//				+ ", isManifestValid()=" + isManifestValid()
//				+ ", isMetaValid()=" + isMetaValid() 
//				+ ", isSettingsValid()=" + isSettingsValid()
//				+ ", isStylesValid()=" + isStylesValid()
//				+ (getContentErrors() != null ? ", getContentErrors()="
//						+ getContentErrors() + ", " : "")
//				+ (getManifestErrors() != null ? "getManifestErrors()="
//						+ getManifestErrors() + ", " : "")
//				+ (getMetaErrors() != null ? "getMetaErrors()="
//						+ getMetaErrors() + ", " : "")
//				+ (getSettingsErrors() != null ? "getSettingsErrors()="
//						+ getSettingsErrors() + ", " : "")
//				+ (getStylesErrors() != null ? "getStylesErrors()="
//						+ getStylesErrors() + ", " : "" + "]");
//				
//	}

//	private boolean isContentValid() {
//		return contentValid;
//	}
//
//	private boolean isStylesValid() {
//		return stylesValid;
//	}
//
//	private boolean isMetaValid() {
//		return metaValid;
//	}
//
//	private boolean isSettingsValid() {
//		return settingsValid;
//	}
//
//	private boolean isManifestValid() {
//		return manifestValid;
//	}
	
	
	/* ------------------------------------------------------------------------
	 * Get the error messages for different ODF components, 
	 * (content.xml, meta.xml, styles.xml, settings.xml, manifest.xml)
	 * ------------------------------------------------------------------------ 
	 */
	
//	private void setContentValid(boolean contentValid) {
//		this.contentValid = contentValid;
//	}
//
//	private void setStylesValid(boolean stylesValid) {
//		this.stylesValid = stylesValid;
//	}
//
//	private void setMetaValid(boolean metaValid) {
//		this.metaValid = metaValid;
//	}
//
//	private void setSettingsValid(boolean settingsValid) {
//		this.settingsValid = settingsValid;
//	}
//
//	private void setManifestValid(boolean manifestValid) {
//		this.manifestValid = manifestValid;
//	}
//
//	private void setContentErrors(String contentErrors) {
//		this.contentErrors = contentErrors;
//	}
//
//	private void setStylesErrors(String stylesErrors) {
//		this.stylesErrors = stylesErrors;
//	}
//
//	private void setMetaErrors(String metaErrors) {
//		this.metaErrors = metaErrors;
//	}
//
//	private void setSettingsErrors(String settingsErrors) {
//		this.settingsErrors = settingsErrors;
//	}
//
//	private void setManifestErrors(String manifestErrors) {
//		this.manifestErrors = manifestErrors;
//	}
}
