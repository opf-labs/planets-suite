package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.mulgara.itql.node.TMinus;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.ProcessRunner;

public class CoreOdfValidator {
	
	
	private static final String DOC_STRICT_SCHEMA_URL_MARKER = "doc-strict-schema-url=";
	private static final String MANIFEST_SCHEMA_URL_MARKER = "manifest-schema-url=";
	private static final String DOC_SCHEMA_URL_MARKER = "doc-schema-url=";
	private static final String DSIG_SCHEMA_URL_MARKER = "dsig-schema-url=";
	
	private static final String USER_DSIG_SCHEMA_PARAM = "user-dsig-schema";
	private static final String USER_DOC_STRICT_SCHEMA_PARAM = "user-doc-strict-schema";
	private static final String USER_DOC_SCHEMA_PARAM = "user-doc-schema";
	private static final String USER_MANIFEST_SCHEMA_PARAM = "user-manifest-schema";
	private static String STRICT_VALIDATION_PARAM = "strict-validation";
	
	private static final String FORMULA_MIMETYPE = "application/vnd.oasis.opendocument.formula";
	
	// Flag section
	private static boolean STRICT_VALIDATION = false;
	private static boolean USE_USER_DOC_SCHEMA = false;
	private static boolean USE_USER_DOC_STRICT_SCHEMA = false;
	private static boolean USE_USER_MANIFEST_SCHEMA = false;
	private static boolean USE_USER_DSIG_SCHEMA = false;
	
	// User schema files, if provided
	private static File USER_DOC_SCHEMA = null;
	private static File USER_DOC_STRICT_SCHEMA = null;
	private static File USER_MANIFEST_SCHEMA = null;
	private static File USER_DSIG_SCHEMA = null;
	
	private static HashMap<String, File> schemaList = new HashMap<String, File>();
	
	private static OdfSchemaHandler schemaHandler = new OdfSchemaHandler();
	private static OdfContentHandler contentHandler = null;
	
	private static String version = null;
		
	private static final String JING_HOME = System.getenv("JING_HOME");
	private static final String JING = "jing.jar";
	
	private static Logger log = Logger.getLogger(CoreOdfValidator.class.getName());
	
	private static OdfValidatorResult result = null;
	
	private static String mimeType = null;
	
	
	public OdfValidatorResult validate(File odfFile, List<Parameter> parameters) {
		log.setLevel(Level.INFO);
		contentHandler = new OdfContentHandler(odfFile);
		result = new OdfValidatorResult(odfFile);
		
		// check if the input file is an ODF file at all
		if(!contentHandler.isOdfFile()) {
			result.setError(odfFile, "The input file '" + odfFile.getName() + "' is NOT an ODF file!");
			return result;
		}
		result.setIsOdfFile(contentHandler.isOdfFile());
		
		
		// File is ODF spec compliant, i.e. all mandatory files are included in container?
		boolean isCompliant = contentHandler.isOdfCompliant();
		result.setIsOdfCompliant(isCompliant);
		// if it is not compliant, e.g. if manifest entries are found that are not present in the container,
		// list the missing entries in the result.
		if(!isCompliant) {
			result.setMissingManifestEntries(contentHandler.getMissingManifestEntries());
		}
		
		// list the contained subfiles in this ODF container, as not all sub files are mandatory!
		List<File> xmlParts = new ArrayList<File>(contentHandler.getOdfSubFiles());
		// list them in the result
		result.setOdfSubFiles(xmlParts);
		
		// get the version of this ODF file and note in result
		version = contentHandler.getOdfVersion();
		result.setOdfVersion(version);
		
		// get the mimetype of this ODF file
		mimeType = contentHandler.getMimeType();
		// if this is a formula file, get the version of the embedded MathML
		if(mimeType.equalsIgnoreCase(FORMULA_MIMETYPE) || contentHandler.containsEmbeddedMathML()) {
			result.setMathMLVersion(contentHandler.getMathMLVersion()); // and set it in the result
		}
		// set the mimetype
		result.setMimeType(mimeType);
		
		// check parameters
		parseParameters(parameters);
		
		// get all necessary schemas, depending on ODF version and mimetype (MathML/formula) 
		collectSchemas();
		
		log.info("Validating input file of mimeType = '" + mimeType + "'");
		
		// validate all relevant sub files in this ODF container
		for (File file : xmlParts) {
			result = validateFile(file, result);
		}
		
		// validated in strict mode? Note it in result...
		if(STRICT_VALIDATION) {
			result.setUsedStrictValidation(STRICT_VALIDATION);
		}
		reset();
		return result;
	}
	
	private static OdfValidatorResult validateFile(File odfSubFile, OdfValidatorResult result) {
		String name = odfSubFile.getName();

		// Do we have a FORMULA (MathML) file?		
		if(name.equalsIgnoreCase(OdfContentHandler.CONTENT_XML)) {
			if(mimeType.equalsIgnoreCase(FORMULA_MIMETYPE) || contentHandler.subFileContainsMathML(odfSubFile)) {
				result = validateMathML(odfSubFile, schemaList.get("mathml"), result);
			}
			else {
				result = validateSubFile(odfSubFile, schemaList.get("doc"), result);
			}
		}
		
		// do we have the manifest.xml file here? Then validate it against the manifest schema!
		if(name.equalsIgnoreCase(OdfContentHandler.MANIFEST_XML)) {
			result = validateSubFile(odfSubFile, schemaList.get("manifest"), result);
		}
		
		// do we have a signature file here, then validate it against the dsig schema
		if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2) && name.equalsIgnoreCase(OdfContentHandler.DOC_DSIGS_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.MACRO_DSIGS_XML)) {
			result = validateSubFile(odfSubFile, schemaList.get("dsig"), result);
		}
		
		// Or do we have a 'normal' ODF subfile (content.xml, settings.xml, styles.xml, meta.xml)?
		if(name.equalsIgnoreCase(OdfContentHandler.SETTINGS_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.STYLES_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.META_XML)) {
			result = validateSubFile(odfSubFile, schemaList.get("doc"), result);
		}
		return result;
	}
	
	
	private static OdfValidatorResult validateSubFile(File odfSubFile, File schema, OdfValidatorResult result) {
		ProcessRunner validator = new ProcessRunner();
		validator.setCommand(getJingValidateCmd(odfSubFile, schema));
		validator.run();
		
		String out = validator.getProcessOutputAsString();
	
		if(out.equalsIgnoreCase("")) {
			result.setValid(odfSubFile, true);
			String parentName = odfSubFile.getParentFile().getName();
			if(!parentName.contains(contentHandler.getCurrentXmlTmpDir().getName()) && 
					! parentName.contains("META-INF")) {
				log.info("'" + parentName + "/" + odfSubFile.getName() + "' is valid: " + result.componentIsValid(odfSubFile));
			}
			else {
				log.info("'" + odfSubFile.getName() + "' is valid: " + result.componentIsValid(odfSubFile));
			}
			
		}
		else {
			result.setValid(odfSubFile, false);
			String parentName = odfSubFile.getParentFile().getName();
			result.setError(odfSubFile, out);
			if(!parentName.contains(contentHandler.getCurrentXmlTmpDir().getName()) && 
					! parentName.contains("META-INF")) {
				log.severe("'" + parentName + "/" + odfSubFile.getName() + "' is valid: " + result.componentIsValid(odfSubFile));
			}
			else {
				log.severe("'" + odfSubFile.getName() + "' is valid: " + result.componentIsValid(odfSubFile));
			}
			log.severe("Message: " + out);
		}
		return result;
	}

	private static OdfValidatorResult validateMathML(File mathmlFile, File mathmlSchema, OdfValidatorResult result) {

		SchemaFactory factory = 
		    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		spf.setNamespaceAware(true);
		
		SAXParser parser;
		try {
			schema = factory.newSchema(mathmlSchema);
			spf.setSchema(schema);
			parser = spf.newSAXParser();
			
			File cleanedMathML = contentHandler.cleanUpXmlForValidation(mathmlFile);
			
			if(contentHandler.containsDocTypeDeclaration()) {
				result.setWarning(mathmlFile, "Detected MathML version = '" + result.getMathMLVersion() + "': To enable validation against the " +
						"MathML 2.0 Schema, DOCTYPE declaration is ignored!");
			}
			else {
				result.setWarning(mathmlFile, "Detected MathML version = '" + result.getMathMLVersion() + "': Using MathML 2.0 schema for validation");
			}
			
			
			parser.parse(cleanedMathML, new DefaultHandler());
		} catch (ParserConfigurationException e1) {
			result.setValid(mathmlFile, false);
			result.setError(mathmlFile, e1.getMessage());
			log.info("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
			return result;
		} catch (SAXException e1) {
			result.setValid(mathmlFile, false);
			result.setError(mathmlFile, e1.getMessage());
			log.info("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
			return result;
		} catch (IOException e) {
			result.setValid(mathmlFile, false);
			result.setError(mathmlFile, e.getMessage());
			log.info("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
			return result;
		}
		
		result.setValid(mathmlFile, true);
		log.info("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
		return result;
	}
	
	
	private static ArrayList<String> getJingValidateCmd(File odfXmlFile, File schemaFile) {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(JING_HOME + File.separator + JING);
		cmd.add("-i");
		cmd.add(schemaFile.getAbsolutePath());
		cmd.add(odfXmlFile.getAbsolutePath());
		return cmd;
	}
	
	private void collectSchemas() {
		if(mimeType.equalsIgnoreCase(FORMULA_MIMETYPE)
				|| contentHandler.containsEmbeddedMathML()) {
			schemaList.put("mathml", schemaHandler.retrieveMathMLSchema());
			result.setMathMLSchema(schemaList.get("mathml"));
		}
		
		if(USE_USER_DOC_SCHEMA) {
			if(USE_USER_DOC_STRICT_SCHEMA) {
				schemaList.put("doc", USER_DOC_STRICT_SCHEMA);
				result.setStrictDocSchema(USER_DOC_STRICT_SCHEMA);
				result.setDocumentSchema(USER_DOC_STRICT_SCHEMA);
			}
			else {
				schemaList.put("doc", USER_DOC_SCHEMA);
				result.setDocumentSchema(USER_DOC_SCHEMA);
			}
		}
		else {
			schemaList.put("doc", schemaHandler.retrieveOdfDocSchemaFile(version, STRICT_VALIDATION));
			result.setDocumentSchema(schemaList.get("doc"));
		}
		if(USE_USER_MANIFEST_SCHEMA) {
			schemaList.put("manifest", USER_MANIFEST_SCHEMA);
			result.setManifestSchema(USER_MANIFEST_SCHEMA);
		}
		else {
			schemaList.put("manifest", schemaHandler.retrieveOdfManifestSchemaFile(version));
			result.setManifestSchema(schemaList.get("manifest"));
		}
		if(contentHandler.containsDsigSubFiles()) {
			if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2)) {
				schemaList.put("dsig", schemaHandler.retrieveDsigSchema(version));
				result.setDsigSchema(schemaList.get("dsig"));
			}
		}
	}

	private static void parseParameters(List<Parameter> parameters) {
		if(parameters!=null && parameters.size()>0) {
			for (Parameter parameter : parameters) {
				String name = parameter.getName();
				// Check if a custom user DSIG schema for validation is passed...
				if(name.equalsIgnoreCase(USER_DSIG_SCHEMA_PARAM)) {
					if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2)) {
						String value = parameter.getValue();
						URL dsig_schema_url = parseForURL(value);
						if(dsig_schema_url!=null) {
							USER_DSIG_SCHEMA = schemaHandler.createUserDsigSchemaFromUrl(version, dsig_schema_url);
						}
						else {
							USER_DSIG_SCHEMA = schemaHandler.createUserDsigSchema(value);
						}
						USE_USER_DSIG_SCHEMA = true;
						continue;
					}
				}
				if(name.equalsIgnoreCase(STRICT_VALIDATION_PARAM)) {
					if(!version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2)) {
						STRICT_VALIDATION = Boolean.parseBoolean(parameter.getValue());
					}
					continue;
				}
				// Check for USER_MANIFEST_SCHEMA
				if(name.equalsIgnoreCase(USER_MANIFEST_SCHEMA_PARAM)) {
					String value = parameter.getValue();
					// check if a URL to a schema is passed?
					URL manifest_schema_url = parseForURL(value);
					if(manifest_schema_url!=null) {
						USER_MANIFEST_SCHEMA = schemaHandler.createUserManifestSchemaFromUrl(version, manifest_schema_url);
					}
					else {
						USER_MANIFEST_SCHEMA = schemaHandler.createUserManifestSchema(value);
					}
					USE_USER_MANIFEST_SCHEMA = true;
					continue;
				}
				// check for USER_DOC_SCHEMA?
				if(name.equalsIgnoreCase(USER_DOC_SCHEMA_PARAM)) {
					String value = parameter.getValue();
					URL doc_schema_url = parseForURL(value);
					if(doc_schema_url!=null) {
						USER_DOC_SCHEMA = schemaHandler.createUserDocSchemaFromUrl(version, doc_schema_url);
					}
					else {
						USER_DOC_SCHEMA = schemaHandler.createUserDocSchema(version, value);
					}
					USE_USER_DOC_SCHEMA = true;
					continue;
				}
				
				if(name.equalsIgnoreCase(USER_DOC_STRICT_SCHEMA_PARAM)) {
					String value = parameter.getValue();
					URL strict_schema_url = parseForURL(value);
					
					if(USE_USER_DOC_SCHEMA) {
						if(strict_schema_url!=null) {
							USER_DOC_STRICT_SCHEMA = schemaHandler.createUserDocStrictSchemaFromUrl(version, strict_schema_url, USER_DOC_SCHEMA);
						}
						else {
							USER_DOC_STRICT_SCHEMA = schemaHandler.createUserDocStrictSchema(version, value, USER_DOC_SCHEMA);
						}
						USE_USER_DOC_STRICT_SCHEMA = true;
						STRICT_VALIDATION = true;
						continue;
					}
					else {
						log.warning("Strict user schema provided, but missing doc schema! Please provide the doc schema first, because it is referenced in the strict schema! Then try again, thanks!");
						log.warning("Using default schemas instead!");
					}
				}
			}
			if(STRICT_VALIDATION && USE_USER_DOC_SCHEMA && !USE_USER_DOC_STRICT_SCHEMA) {
				log.warning("WARNING: You have enabled STRICT VALIDATION and passed only a not-strict user-doc-schema! Disabling STRICT_VALIDATION!");
				STRICT_VALIDATION = false;
			}
		}
		
	}

	private static URL parseForURL(String parameterValue) {
		URL url = null;
		if(parameterValue.contains(DOC_SCHEMA_URL_MARKER) 
				|| parameterValue.contains(MANIFEST_SCHEMA_URL_MARKER)
				|| parameterValue.contains(DOC_STRICT_SCHEMA_URL_MARKER)
				|| parameterValue.contains(DSIG_SCHEMA_URL_MARKER)) {
			try {
				url = URI.create(parameterValue.substring(parameterValue.indexOf("=")+1)).toURL();
			} catch (MalformedURLException e) {
				log.severe("No valid URL found in this Parameter!");
				return null;
			}
		}
		return url;
	}

	private void reset() {
		STRICT_VALIDATION = false;
		USE_USER_DOC_SCHEMA = false;
		USE_USER_DOC_STRICT_SCHEMA = false;
		USE_USER_MANIFEST_SCHEMA = false;
		USER_DOC_SCHEMA = null;
		USER_DOC_STRICT_SCHEMA = null;
		USER_MANIFEST_SCHEMA = null;
		mimeType = null;
	}
}
