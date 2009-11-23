package eu.planets_project.services.validation.odfvalidators.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import odfvalidator.ODFFileValidator;
import odfvalidator.ODFValidator;
import odfvalidator.ODFValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;

public class CoreOdfValidator {
	
	private static final String DOC_STRICT_SCHEMA_URL_MARKER = "doc-strict-schema-url=";
	private static final String MANIFEST_SCHEMA_URL_MARKER = "manifest-schema-url=";
	private static final String DOC_SCHEMA_URL_MARKER = "doc-schema-url=";
	
	private static final String USER_DOC_STRICT_SCHEMA_PARAM = "user-doc-strict-schema";
	private static final String USER_DOC_SCHEMA_PARAM = "user-doc-schema";
	private static final String USER_MANIFEST_SCHEMA_PARAM = "user-manifest-schema";
	private static String STRICT_PARAM_NAME = "strictValidation";
	
	private static final String MATHML_MIMETYPE = "application/vnd.oasis.opendocument.formula";
	
	// Flag section
	private static boolean STRICT_VALIDATION = false;
	private static boolean USE_USER_DOC_SCHEMA = false;
	private static boolean USE_USER_DOC_STRICT_SCHEMA = false;
	private static boolean USE_USER_MANIFEST_SCHEMA = false;
	
	// User schema files, if provided
	private static File USER_DOC_SCHEMA = null;
	private static File USER_DOC_STRICT_SCHEMA = null;
	private static File USER_MANIFEST_SCHEMA = null;
	
	private static OdfSchemaHandler schemaHandler = new OdfSchemaHandler();
	private static OdfContentHandler contentHandler = null;
	
	private static String version = null;
		
	private static final String JING_HOME = System.getenv("JING_HOME");
	private static final String JING = "jing.jar";
	
	private static final String MSV_HOME = "E:/MSV/msv.20090415/msv-20090415";
	private static final String MSV = "msv.jar";
	
	private static final String ODF_TOOLKIT_VALIDATOR_HOME = "E:/ODF_TOOLKIT_HOME";
	private static final String ODF_TOOLKIT = "odfvalidator.jar";
	
	private static File ODF_VALIDATOR_TMP = null;
	
	private static Log log = LogFactory.getLog(CoreOdfValidator.class);
	
	
	
	public CoreOdfValidator() {
		
		ODF_VALIDATOR_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
//		schemaHandler.provideSchemas();
	}
	
	public OdfValidatorResult validate(File odfFile, List<Parameter> parameters) {
		contentHandler = new OdfContentHandler(odfFile);
		OdfValidatorResult result = new OdfValidatorResult(odfFile);
		
		// check if the input file is an ODF file at all
		if(!contentHandler.isOdfFile()) {
			result.setError(odfFile, "The input file '" + odfFile.getName() + "' is NOT an ODF file!");
			return result;
		}
		
		result.setIsOdfFile(contentHandler.isOdfFile());
		
		boolean isCompliant = contentHandler.isOdfCompliant();
		result.setIsOdfCompliant(isCompliant);
		if(!isCompliant) {
			result.setMissingManifestEntries(contentHandler.getMissingManifestEntries());
		}
		
		List<File> xmlParts = contentHandler.getXmlComponents();
		result.setXmlComponents(xmlParts);
		version = contentHandler.getOdfVersion();
		result.setOdfVersion(version);
		
		String mimeType = contentHandler.getMimeType();
		
		if(mimeType.equalsIgnoreCase(MATHML_MIMETYPE)) {
			result.setMathMLVersion(contentHandler.getMathMLVersion());
		}
		
		result.setMimeType(mimeType);
		
		parseParameters(parameters);
		
		File doc_schema = null;
		File mathml_schema = null;
		File manifest_schema = null;
		
		if(mimeType.equalsIgnoreCase(MATHML_MIMETYPE)) {
			mathml_schema = schemaHandler.retrieveMathMLSchemaOrDtdFile(result.getMathMLVersion());
			File xmlTmpDir = contentHandler.getXmlTmpDir();
			FileUtils.copyFileTo(mathml_schema, new File(xmlTmpDir, mathml_schema.getName()));
			result.setMathMLSchema(mathml_schema);
		}
		
		if(USE_USER_DOC_SCHEMA) {
			if(USE_USER_DOC_STRICT_SCHEMA) {
				doc_schema = USER_DOC_STRICT_SCHEMA;
				result.setStrictDocSchema(doc_schema);
				result.setDocumentSchema(USER_DOC_SCHEMA);
			}
			else {
				doc_schema = USER_DOC_SCHEMA;
				result.setDocumentSchema(USER_DOC_SCHEMA);
			}
		}
		else {
			doc_schema = schemaHandler.retrieveOdfDocSchemaFile(version, STRICT_VALIDATION);
			result.setDocumentSchema(doc_schema);
		}
		if(USE_USER_MANIFEST_SCHEMA) {
			manifest_schema = USER_MANIFEST_SCHEMA;
			result.setManifestSchema(manifest_schema);
		}
		else {
			manifest_schema = schemaHandler.retrieveOdfManifestSchemaFile(version);
			result.setManifestSchema(manifest_schema);
		}
		
		log.info("input MIME type = '" + mimeType + "'");
		
		for (File file : xmlParts) {
			result = validateFile(file, doc_schema, manifest_schema, mathml_schema, result);
		}
		
		if(STRICT_VALIDATION) {
			result.setUsedStrictValidation(STRICT_VALIDATION);
		}
		reset();
		return result;
	}

	private void reset() {
		STRICT_VALIDATION = false;
		USE_USER_DOC_SCHEMA = false;
		USE_USER_DOC_STRICT_SCHEMA = false;
		USE_USER_MANIFEST_SCHEMA = false;
	}
	
	
	private static void parseParameters(List<Parameter> parameters) {
		if(parameters!=null && parameters.size()>0) {
			for (Parameter parameter : parameters) {
				String name = parameter.getName();
				if(name.equalsIgnoreCase(STRICT_PARAM_NAME)) {
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
						log.warn("Strict user schema provided, but missing doc schema! Please provide the doc schema first, because it is referenced in the strict schema! Then try again, thanks!");
						log.warn("Using default schemas instead!");
					}
				}
			}
			if(STRICT_VALIDATION && USE_USER_DOC_SCHEMA && !USE_USER_DOC_STRICT_SCHEMA) {
				log.warn("WARNING: You have enabled STRICT VALIDATION and passed only a not-strict user-doc-schema! Disabling STRICT_VALIDATION!");
				STRICT_VALIDATION = false;
			}
		}
		
	}
	
	private static URL parseForURL(String parameterValue) {
		URL url = null;
		if(parameterValue.contains(DOC_SCHEMA_URL_MARKER) 
				|| parameterValue.contains(MANIFEST_SCHEMA_URL_MARKER)
				|| parameterValue.contains(DOC_STRICT_SCHEMA_URL_MARKER)) {
			try {
				url = URI.create(parameterValue.substring(parameterValue.indexOf("=")+1)).toURL();
			} catch (MalformedURLException e) {
				log.error("No valid URL found in this Parameter!");
				return null;
			}
		}
		return url;
	}
	
	private static OdfValidatorResult validateFile(File odfXmlComponent, File docSchema, File manifestSchema, File mathMLSchema, OdfValidatorResult result) {
		String javaVersion = System.getProperty("java.version");
    	
    	if(javaVersion.startsWith("1.6")) {
    		System.setProperty("javax.xml.validation.SchemaFactory:http://relaxng.org/ns/structure/1.0", "org.iso_relax.verifier.jaxp.validation.RELAXNGSchemaFactoryImpl");
    		System.setProperty("org.iso_relax.verifier.VerifierFactoryLoader", "com.sun.msv.verifier.jarv.FactoryLoaderImpl");
    	}
    	
		ProcessRunner validator = null;
		String name = odfXmlComponent.getName();
		
		if(result.getMimeType().equalsIgnoreCase(MATHML_MIMETYPE)) {
			if(name.equalsIgnoreCase(OdfContentHandler.CONTENT_XML)) {
				validator = new ProcessRunner();
				validator.setCommand(getMsvValidateMathMLContentCmd(odfXmlComponent, mathMLSchema));
			}
		}
		else {
			validator = new ProcessRunner();
			validator.setCommand(getValidateOdfXmlPartCmd(odfXmlComponent, docSchema));
		}
		
		if(name.equalsIgnoreCase(OdfContentHandler.SETTINGS_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.STYLES_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.META_XML)) {
			validator = new ProcessRunner();
			validator.setCommand(getValidateOdfXmlPartCmd(odfXmlComponent, docSchema));
		}
		
		if(name.equalsIgnoreCase(OdfContentHandler.MANIFEST_XML)) {
			validator = new ProcessRunner();
			validator.setCommand(getValidateOdfXmlPartCmd(odfXmlComponent, manifestSchema));
		}
		
		validator.run();
		
		String out = validator.getProcessOutputAsString();
		String error = validator.getProcessErrorAsString();

		if(out.equalsIgnoreCase("")) {
			result.setValid(odfXmlComponent, true);
			log.info("'" + name + "' is valid: " + result.componentIsValid(odfXmlComponent));
		}
		else {
			result.setValid(odfXmlComponent, false);
			result.setError(odfXmlComponent, out);
			log.error("'" + name + "' is valid: " + result.componentIsValid(odfXmlComponent));
			log.error("Message: " + out);
		}
		return result;
	}
	
	
	private static ArrayList<String> getValidateOdfXmlPartCmd(File odfXmlFile, File schemaFile) {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(JING_HOME + File.separator + JING);
		cmd.add("-i");
		cmd.add(schemaFile.getAbsolutePath());
		cmd.add(odfXmlFile.getAbsolutePath());
		return cmd;
	}
	
	private static ArrayList<String> getMsvValidateMathMLContentCmd(File contentXml, File schemaOrDTD) {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(MSV_HOME + File.separator + MSV);
		cmd.add("-standalone");
		cmd.add(schemaOrDTD.getAbsolutePath());
		cmd.add(contentXml.getAbsolutePath());
		return cmd;
	}
	
//	private static ArrayList<String> getOdfToolkitMathMLCmd(File contentXml, File schemaOrDTD) {
//		ArrayList<String> cmd = new ArrayList<String>();
//		cmd.add("java");
//		cmd.add("-jar");
//		cmd.add(ODF_TOOLKIT_VALIDATOR_HOME + File.separator + ODF_TOOLKIT);
//		cmd.add(contentXml.getAbsolutePath());
//		return cmd;
//	}
	
//	private static ArrayList<String> getValidateManifestCmd(File manifestXml, File manifestSchema) {
//		ArrayList<String> cmd = new ArrayList<String>();
//		cmd.add("java");
//		cmd.add("-jar");
//		cmd.add(JING_HOME + File.separator + JING);
//		cmd.add("-i");
//		cmd.add(manifestSchema.getAbsolutePath());
//		cmd.add(manifestXml.getAbsolutePath());
//		return cmd;
//	}
	

}
