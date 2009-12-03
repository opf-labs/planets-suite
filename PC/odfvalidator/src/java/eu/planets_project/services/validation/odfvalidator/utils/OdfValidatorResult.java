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
	
	private static final String DSIG_LABEL = "dsig";
	private static final String USER_DSIG_LABEL = "user_dsig";
	private static final String DOC_LABEL = "doc";
	private static final String USER_DOC_LABEL = "userDoc";
	private static final String MATHML_LABEL = "mathML";
	private static final String USER_MATHML_LABEL = "userMathML";
	private static final String MANIFEST_LABEL = "manifest";
	private static final String USER_MANIFEST_LABEL = "userManifest";
	private static final String STRICT_LABEL = "strict";
	private static final String USER_STRICT_LABEL = "userStrict";
	private boolean isOdfFile = false;
	private File odfInputFile = null;
	
	private List<File> xmlComponents = new ArrayList<File>();
	private HashMap<File, Boolean> validatedList = new HashMap<File, Boolean>();
	private HashMap<File, List<String>> errorList = new HashMap<File, List<String>>();
	private HashMap<File, List<String>> warningList = new HashMap<File, List<String>>();
	private List<File> invalidComponents = new ArrayList<File>();
	private List<String> missingManifestEntries = new ArrayList<String>();
	private HashMap<String, File> schemaFiles = new HashMap<String, File>();
	
	private boolean isOdfCompliant = false;
	private boolean usedStrictValidation = false;
	private boolean allNamespacesCorrect = true;
	private String odfVersion = "unknown";
	private String mathMLVersion = "unknown";
	private String mimeType = "unknown";
	private String odfGenerator = "unknown";
	private String NEWLINE = System.getProperty("line.separator");
	
	public OdfValidatorResult(File odfInputFile) {
		this.odfInputFile = odfInputFile;
	}
	
	public boolean componentIsValid(File odfXmlComponent) {
		Boolean valid = validatedList.get(odfXmlComponent);
		return valid.booleanValue();
	}
	
	public void setOdfGenerator(String generator) {
		this.odfGenerator = generator;
	}
	
	public void setAllNamespacesCorrect(boolean b) {
		this.allNamespacesCorrect = b;
	}
	
	public boolean allNamespacesCorrect() {
		return allNamespacesCorrect;
	}
	
	public String getOdfGenerator() {
		return odfGenerator;
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
	
	public String getMathMLVersion() {
		return mathMLVersion;
	}
	
	public List<String> getSchemaNames() {
		List<String> schemas = new ArrayList<String>();
		
		Set<String> schemaKeys = schemaFiles.keySet();
		for (String string : schemaKeys) {
			if(string.equalsIgnoreCase(DOC_LABEL)) {
				schemas.add("[Document schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(USER_DOC_LABEL)) {
				schemas.add("[User Doc Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(STRICT_LABEL)) {
				schemas.add("[Strict Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(USER_STRICT_LABEL)) {
				schemas.add("[User Strict Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(MANIFEST_LABEL)) {
				schemas.add("[Manifest Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(USER_MANIFEST_LABEL)) {
				schemas.add("[User Manifest Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(MATHML_LABEL)) {
				schemas.add("[MathML Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(USER_MATHML_LABEL)) {
				schemas.add("[User MathML Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(DSIG_LABEL)) {
				schemas.add("[DSIG Schema] = " + schemaFiles.get(string).getName());
				continue;
			}
			if(string.equalsIgnoreCase(USER_DSIG_LABEL)) {
				schemas.add("[User DSIG Schema] = " + schemaFiles.get(string).getName());
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
			if(label.equalsIgnoreCase(DOC_LABEL)) {
				String tmpDoc = "[Document Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpDoc);
			}
			if(label.equalsIgnoreCase(USER_DOC_LABEL)) {
				String tmpUserDoc = "[User Document Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserDoc);
			}
			if(label.equalsIgnoreCase(STRICT_LABEL)) {
				String tmpStrict = "[Strict Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpStrict);
			}
			if(label.equalsIgnoreCase(USER_STRICT_LABEL)) {
				String tmpUserStrict = "[User Strict Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserStrict);
			}
			if(label.equalsIgnoreCase(MANIFEST_LABEL)) {
				String tmpManifest = "[Manifest Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpManifest);
			}
			if(label.equalsIgnoreCase(USER_MANIFEST_LABEL)) {
				String tmpUserManifest = "[User Manifest Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserManifest);
			}
			if(label.equalsIgnoreCase(MATHML_LABEL)) {
				String tmpMath = "[MathML Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpMath);
			}
			if(label.equalsIgnoreCase(USER_MATHML_LABEL)) {
				String tmpUserMathML = "[User MathML Schema] = "
								+ NEWLINE
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserMathML);
			}
			if(label.equalsIgnoreCase(DSIG_LABEL)) {
				String tmpDSIG = "[DSIG Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpDSIG);
			}
			if(label.equalsIgnoreCase(USER_DSIG_LABEL)) {
				String tmpUserDSIG = "[User DSIG Schema] = " 
								+ NEWLINE 
								+ FileUtils.readTxtFileIntoString(schemaFiles.get(label));
				schemaContents.add(tmpUserDSIG);
			}
		}
		return schemaContents;
	}
	

	public String getValidationResultAsString () {
		StringBuffer buf = new StringBuffer();
		buf.append(NEWLINE);
		buf.append(NEWLINE);
		buf.append("========== Validation Results ==========" + NEWLINE);
		buf.append("---------- Document Validity ----------" + NEWLINE);
		buf.append("[Document is valid] = " + this.documentIsValid() + NEWLINE);
		buf.append("[Document is ODF file] = " + this.isOdfFile + NEWLINE);
		buf.append(NEWLINE);
		buf.append("---------- Odf Sub File Validity ----------" + NEWLINE);
		for (File component : xmlComponents) {
				buf.append("['" + checkParentName(component) + component.getName() + "' is valid] = " + componentIsValid(component) + NEWLINE);
		}
		buf.append(NEWLINE);
		buf.append("---------- Odf Compliance ----------" + NEWLINE);
		buf.append("[Document is ODF compliant] = " + this.isOdfCompliant + NEWLINE);
		buf.append("[All Namespaces validated] = " + this.allNamespacesCorrect() + NEWLINE);
		if(!this.isOdfCompliant) {
			for (String currentEntry : missingManifestEntries) {
				buf.append("[Missing Manifest entry] = " + currentEntry);
			}
		}
		buf.append(NEWLINE);
		buf.append("---------- General Information ----------" + NEWLINE);
		buf.append("[Input File] = " + odfInputFile.getName() + NEWLINE);
		buf.append("[ODF Generator] = " + this.getOdfGenerator() + NEWLINE);
		buf.append("[MimeType] = " + this.getMimeType() + NEWLINE);
		buf.append("[ODF Version] = " + this.getOdfVersion() + NEWLINE);
		if(!mathMLVersion.equalsIgnoreCase("unknown")) {
			buf.append("[MathML Version] = " + this.getMathMLVersion() + NEWLINE);
		}
		buf.append("[Used strict Validation] = " + this.usedStrictValidation() + NEWLINE);
		buf.append(NEWLINE);
		buf.append("---------- Used Schemas ----------" + NEWLINE);
		List<String> schemaNames = getSchemaNames();
		for (String schemaName : schemaNames) {
			buf.append(schemaName + NEWLINE);
		}
		buf.append(NEWLINE);
		if(warningList.size()>0) {
			buf.append("---------- Warnings ----------" + NEWLINE);
			Set<File> files = warningList.keySet();
			for (File file : files) {
				buf.append(getWarnings(file));
			}
			buf.append(NEWLINE);
		}
		if(!this.documentIsValid()) {
			buf.append("---------- Errors ----------" + NEWLINE);
			for (File invalidComponent : invalidComponents) {
				buf.append(getErrors(invalidComponent));
			}
			buf.append(NEWLINE);
		}
		buf.append("========== Validation Results End ==========");
		buf.append(NEWLINE);
		return buf.toString();
	}

	public List<File> getXmlComponents() {
		return xmlComponents;
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
			schemaFiles.put(USER_DOC_LABEL, docSchema);
		}
		else {
			schemaFiles.put(DOC_LABEL, docSchema);
		}
	}
	
	public void setStrictDocSchema(File strictSchema) {
		if(strictSchema.getName().contains("user")) {
			schemaFiles.put(USER_STRICT_LABEL, strictSchema);
		}
		else {
			schemaFiles.put(STRICT_LABEL, strictSchema);
		}
	}
	
	public void setDsigSchema(File dsigSchema) {
		if(dsigSchema.getName().contains("user")) {
			schemaFiles.put(USER_DSIG_LABEL, dsigSchema);
		}
		else {
			schemaFiles.put(DSIG_LABEL, dsigSchema);
		}
	}
	
	public void setManifestSchema(File manifestSchema) {
		if(manifestSchema.getName().contains("user")) {
			schemaFiles.put(USER_MANIFEST_LABEL, manifestSchema);
		}
		else {
			schemaFiles.put(MANIFEST_LABEL, manifestSchema);
		}
	}
	
	public void setMathMLSchema(File mathmlSchema) {
		if(mathmlSchema.getName().contains("user")) {
			schemaFiles.put(USER_MATHML_LABEL, mathmlSchema);
		}
		else {
			schemaFiles.put(MATHML_LABEL, mathmlSchema);
		}
	}

	public void setError(File file, String errorMessage) {
		List<String> errors = errorList.get(file);
		if(errors==null) {
			errors = new ArrayList<String>();
		}
		errors.add(errorMessage);
		errorList.put(file, errors);
		invalidComponents.add(file);
	}
	
	private static String checkParentName(File subFile) {
		String parentName = subFile.getParentFile().getName();
		if(!parentName.contains("XML_CONTENT_") 
				&& !parentName.contains("META-INF")
				&& !parentName.contains("ODFVALIDATOR_INPUT")) {
			return parentName + "/";
		}
		else {
			return "";
		}
	}
	
	public String getWarnings(File file) {
		List<String> warnings = warningList.get(file);
		StringBuffer buf = new StringBuffer();
		int i=1;
		buf.append("[Warnings] '" + checkParentName(file) + file.getName() + "':" + NEWLINE);
		for (String string : warnings) {
			buf.append(i + ") " + string + NEWLINE);
			i++;
		}
		return buf.toString();
	}
	
	public String getErrors(File file) {
		List<String> errors = errorList.get(file);
		StringBuffer buf = new StringBuffer();
		int i=1;
		buf.append("[Errors] '" + checkParentName(file) + file.getName() + "':" + NEWLINE);
		for (String string : errors) {
			buf.append(i + ") " + string.trim() + NEWLINE);
			i++;
		}
		return buf.toString();
	}
	
	public void setWarning(File file, String warningMessage) {
		List<String> entries = warningList.get(file);
		if(entries==null) {
			entries = new ArrayList<String>();
		}
		entries.add(warningMessage);
		warningList.put(file, entries);
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
	
	public void setMathMLVersion(String mathMLVersion) {
		this.mathMLVersion = mathMLVersion;
	}

	public void setUsedStrictValidation(boolean usedStrictValidation) {
		this.usedStrictValidation = usedStrictValidation;
	}

	public void setValid(File odfXmlComponent, boolean valid) {
		validatedList.put(odfXmlComponent, new Boolean(valid));
	}

	public void setOdfSubFiles(List<File> xmlParts) {
		xmlComponents = xmlParts;
	}
	
	public boolean usedStrictValidation() {
		return usedStrictValidation;
	}
	
	@Override
	public String toString() {
		return getValidationResultAsString();
	}
}
