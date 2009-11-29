package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

import eu.planets_project.services.utils.FileUtils;

public class OdfSchemaHandler {
	
	public static final String ODF_v1_0 = "ODF:1.0";
	public static final String ODF_v1_1 = "ODF:1.1";
	public static final String ODF_v1_2 = "ODF:1.2";
	public static final String MATHML_v101 = "MATHML:1.01";
	public static final String MATHML_v2 = "MATHML:2.0";
	
	// Manifest Schemas
	private static String v10_MANIFEST_SCHEMA = "v10_MANIFEST_SCHEMA";
	private static String v11_MANIFEST_SCHEMA = "v11_MANIFEST_SCHEMA";
	private static String v12_MANIFEST_SCHEMA = "v12_MANIFEST_SCHEMA";
		
	// Document Schemas
	private static String v10_DOC_SCHEMA = "v10_DOC_SCHEMA";
	private static String v11_DOC_SCHEMA = "v11_DOC_SCHEMA";
	private static String v12_DOC_SCHEMA = "v12_DOC_SCHEMA";
	
	// Strict Document Schemas
	private static String v10_STRICT_DOC_SCHEMA = "v10_STRICT_DOC_SCHEMA";
	private static String v11_STRICT_DOC_SCHEMA = "v11_STRICT_DOC_SCHEMA";
	private static String v12_DSIG_SCHEMA = "v12_DSIG_SCHEMA";
	private static final String MATHML2_SCHEMA = "MATHML2_SCHEMA";
	private static final String MATH101_DTD = "MATH101_DTD";
	
	private static final String INCLUDE_HREF = "<include href=\"";
	
	private static HashMap<String, File> schemaFiles = new HashMap<String, File>();
	
	private String ODF_SCHEMA_LIST = "schema_list.properties";
	private static final String MATHMLSCHEMAS_PROPERTIES = "mathmlschemas.properties";
	
	private static File ODF_SH_TMP = null;
	private static String ODF_SH_TMP_NAME = "ODF_SCHEMA_HANDLER";
	
	private static File SCHEMAS = null;
	private static String SCHEMAS_NAME = "SCHEMAS";
	private static boolean schemasProvided = false;
	
	private static Logger log = Logger.getLogger(OdfSchemaHandler.class.getName());
	
	public OdfSchemaHandler() {
		ODF_SH_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), ODF_SH_TMP_NAME);
		SCHEMAS = FileUtils.createFolderInWorkFolder(ODF_SH_TMP, SCHEMAS_NAME);
		FileUtils.deleteAllFilesInFolder(SCHEMAS);
		boolean provided = provideSchemas();
	}

	public File retrieveOdfDocSchemaFile(String version, boolean strictValidation) {
		if(!version.equalsIgnoreCase(ODF_v1_2) && strictValidation) {
			log.info("Strict Validation enabled!");
			if(version.equalsIgnoreCase(ODF_v1_0)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Strict Schema file: " + schemaFiles.get(v10_STRICT_DOC_SCHEMA).getName());
				return schemaFiles.get(v10_STRICT_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(ODF_v1_1)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Strict Schema file: " + schemaFiles.get(v11_STRICT_DOC_SCHEMA).getName());
				return schemaFiles.get(v11_STRICT_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(ODF_v1_2)) {
				log.info("Strict Validation not applicable for v1.2!");
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v12_DOC_SCHEMA).getName());
				return schemaFiles.get(v12_DOC_SCHEMA);
			}
			log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): Could NOT retrieve Strict Schema file for this ODF version: " + version);
		}
		else {
			if(version.equalsIgnoreCase(ODF_v1_0)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v10_DOC_SCHEMA).getName());
				return schemaFiles.get(v10_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(ODF_v1_1)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v11_DOC_SCHEMA).getName());
				return schemaFiles.get(v11_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(ODF_v1_2)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v12_DOC_SCHEMA).getName());
				return schemaFiles.get(v12_DOC_SCHEMA);
			}
			log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): Could NOT retrieve Schema file for this ODF version: " + version);
		}
		return null;
	}
	
	public File retrieveMathML101Dtd() {
//		log.info("[OdfSchemaHandler] retrieveMathMLSchemaFile(): using MathML v1.01 DTD file: " + schemaFiles.get(MATH101_DTD).getName());
		return schemaFiles.get(MATH101_DTD);
	}
	
	public File retrieveMathMLSchemaOrDtdFile(String mathMLVersion) {
		if(mathMLVersion.equalsIgnoreCase(MATHML_v101)) {
//			log.info("[OdfSchemaHandler] retrieveMathMLSchemaFile(): using MathML v1.01 DTD file: " + schemaFiles.get(MATHML2_SCHEMA).getName());
			log.info("[OdfSchemaHandler] retrieveMathMLSchemaFile(): using MathML 2 Schema file: " + schemaFiles.get(MATHML2_SCHEMA).getName());
//			return schemaFiles.get(MATH101_DTD);
			return schemaFiles.get(MATHML2_SCHEMA);
		}
		if(mathMLVersion.equalsIgnoreCase(MATHML_v2)) {
			log.info("[OdfSchemaHandler] retrieveMathMLSchemaFile(): using MathML 2 Schema file: " + schemaFiles.get(MATHML2_SCHEMA).getName());
			return schemaFiles.get(MATHML2_SCHEMA);
		}
		log.severe("Unable to retrieve MathML schema or DTD for this version!!!");
		return null;
	}
	
	public File retrieveDsigSchema(String version) {
		return schemaFiles.get(v12_DSIG_SCHEMA);
	}
	
	public File getSchemaDir() {
		return SCHEMAS;
	}

	public File retrieveOdfManifestSchemaFile(String version) {
		if(version.equalsIgnoreCase(ODF_v1_0)) {
			log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): using Manifest Schema file: " + schemaFiles.get(v10_MANIFEST_SCHEMA).getName());
			return schemaFiles.get(v10_MANIFEST_SCHEMA);
		}
		if(version.equalsIgnoreCase(ODF_v1_1)) {
			log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): using Manifest Schema file: " + schemaFiles.get(v11_MANIFEST_SCHEMA).getName());
			return schemaFiles.get(v11_MANIFEST_SCHEMA);
		}
		if(version.equalsIgnoreCase(ODF_v1_2)) {
			log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): using Manifest Schema file: " + schemaFiles.get(v12_MANIFEST_SCHEMA).getName());
			return schemaFiles.get(v12_MANIFEST_SCHEMA);
		}
		log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): Could NOT retrieve Manifest Schema for this version: " + version);
		return null;
	}
	
	public File createUserDocSchema(String version, String schemaContent) {
		File userDocSchema = null;
		if(schemaContent.equalsIgnoreCase("")|| schemaContent==null) {
			log.warning("WARN: User schema not found! Received String is empty!");
			log.info("Trying to lookup DEFAULT doc-schema...");
			userDocSchema = retrieveOdfDocSchemaFile(version, false);
		}
		else {
			userDocSchema = new File(SCHEMAS, FileUtils.randomizeFileName("userDocSchema.rng"));
			FileUtils.writeStringToFile(schemaContent, userDocSchema);
		}
		return userDocSchema;
	}
	
	
	public File createUserDocSchemaFromUrl(String version, URL docSchemaURL) {
		File userDocSchema = null;
		try {
			userDocSchema = new File(SCHEMAS, FileUtils.randomizeFileName("userDocSchema.rng"));
			log.info("Reading content from URL (" + docSchemaURL.toString() + ")...please hang on!");
			FileUtils.writeInputStreamToFile(docSchemaURL.openStream(), userDocSchema);
		} catch (IOException e) {
			log.severe("ERROR: Could not open URL: " + docSchemaURL.toString() + "!");
			userDocSchema = retrieveOdfDocSchemaFile(version, false);
		}
		return userDocSchema;
	}
	
	public File createUserDocStrictSchema(String version, String schemaContent, File userDocSchema) {
		File userDocStrictSchema = null;
		if(schemaContent.equalsIgnoreCase("")
				|| schemaContent==null) {
			log.warning("WARN: User strict schema not found! Received String is empty!");
			log.info("Trying to lookup DEFAULT doc-strict-schema...");
			userDocStrictSchema = retrieveOdfDocSchemaFile(version, true);
		}
		else {
			if(schemaContent.contains(INCLUDE_HREF)) {
				int start = schemaContent.indexOf(INCLUDE_HREF) + 1;
				int end = schemaContent.indexOf("\"", start + INCLUDE_HREF.length());
				String toReplace = schemaContent.substring(start + INCLUDE_HREF.length()-1, end);
				schemaContent = schemaContent.replace(toReplace, userDocSchema.getName());
			}
			userDocStrictSchema = new File(SCHEMAS, FileUtils.randomizeFileName("userDocStrictSchema.rng"));
			FileUtils.writeStringToFile(schemaContent, userDocStrictSchema);
		}
		return userDocStrictSchema;
	}
	
	public File createUserDocStrictSchemaFromUrl(String version, URL strictSchemaUrl, File userDocSchema) {
		String schemaContent = null;
		File userDocStrictSchema = null;
		try {
			log.info("Reading content from URL (" + strictSchemaUrl.toString() + ")...please hang on!");
			schemaContent = new String(FileUtils.writeInputStreamToBinary(strictSchemaUrl.openStream()));
			userDocStrictSchema = createUserDocStrictSchema(version, schemaContent, userDocSchema);
		} catch (IOException e) {
			log.severe("Could not open URL: " + strictSchemaUrl.toString() + "!");
			userDocStrictSchema = retrieveOdfDocSchemaFile(version, true);
		}
		return userDocStrictSchema;
	}
	
	public File createUserManifestSchema(String schemaContent) {
		File userManifestSchema = new File(SCHEMAS, FileUtils.randomizeFileName("userManifestSchema.rng"));
		FileUtils.writeStringToFile(schemaContent, userManifestSchema);
		return userManifestSchema;
	}
	
	public File createUserManifestSchemaFromUrl(String version, URL manifestSchemaUrl) {
		File userManifestSchema = null;
		try {
			userManifestSchema = new File(SCHEMAS, FileUtils.randomizeFileName("userManifestSchema.rng"));
			log.info("Reading content from URL (" + manifestSchemaUrl.toString() + ")...please hang on!");
			FileUtils.writeInputStreamToFile(manifestSchemaUrl.openStream(), userManifestSchema);
		} catch (IOException e) {
			log.severe("ERROR: Could not open URL: " + manifestSchemaUrl.toString() + "!");
			userManifestSchema = retrieveOdfDocSchemaFile(version, false);
		}
		return userManifestSchema;
	}

	public boolean provideSchemas(){
		if(schemasProvided) {
			return true;
		}
		boolean odfSchemasProvided = provideOdfSchemas(ODF_SCHEMA_LIST);
		
		boolean mathmlSchemasProvided = provideMathMLSchemas(MATHMLSCHEMAS_PROPERTIES);
		
		if(!odfSchemasProvided) {
			log.severe("ERROR: Unable to provide ODF schemas listed in '" + ODF_SCHEMA_LIST + "'!");
		}
		if(!mathmlSchemasProvided) {
			log.severe("ERROR: Unable to provide MathML schemas listed in '" + MATHMLSCHEMAS_PROPERTIES + "'!");
		}
		if(odfSchemasProvided && mathmlSchemasProvided) {
			schemasProvided = true;
			return true;
		}
		else {
			schemasProvided = false;
			return false;
		}
	}
	
	private boolean provideMathMLSchemas(String mathmlSchemaListName) {
		String mathmlSchemaList = new String(FileUtils.writeInputStreamToBinary(this.getClass().getResourceAsStream(mathmlSchemaListName)));
		String[] entries = mathmlSchemaList.split(System.getProperty("line.separator"));
		int entryCount = entries.length;
		boolean[] success = new boolean[entryCount];
		int i=0;
		for (String currentEntry : entries) {
//			if(currentEntry.equalsIgnoreCase("mmlents.zip")) {
//				File entZip = new File(SCHEMAS, "mmlents.zip");
//				FileUtils.writeInputStreamToFile(this.getClass().getResourceAsStream("schemas/mathml/" + currentEntry.trim()), entZip);
//				ZipUtils.unzipTo(entZip, SCHEMAS);
//				continue;
//			}
			String[] parts = currentEntry.split("/");
			String parent = parts[0];
			String name = parts[1];
			File parentDir = new File(SCHEMAS, parent);
			if(!parentDir.exists()) {
				FileUtils.createFolderInWorkFolder(SCHEMAS, parent);
			}
			File schema = new File(parentDir, name);
			FileUtils.writeInputStreamToFile(this.getClass().getResourceAsStream("schemas/mathml/" + parent + "/" + name), schema);
			success[i] = schema.exists();
			i++;
		}
		int count = 0;
		for (boolean b : success) {
			if(b==true) {
				count++;
			}
		}
		
		if(count==entries.length) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	private boolean provideOdfSchemas(String odfSchemaListName) {
		String odfSchemaList = new String(FileUtils.writeInputStreamToBinary(this.getClass().getResourceAsStream(odfSchemaListName)));
		String[] entries = odfSchemaList.split(System.getProperty("line.separator"));
		int entryCount = entries.length;
		boolean[] success = new boolean[entryCount];
		int i=0;
		for (String currentEntry : entries) {
			String[] labelsAndValues = currentEntry.split("=");
			String label = labelsAndValues[0].trim();
			String name = labelsAndValues[1].trim();
			File schema = new File(SCHEMAS, name);
			if(label.equalsIgnoreCase("MATHML2_SCHEMA") || label.equalsIgnoreCase("MATH101_DTD")) {
				FileUtils.writeInputStreamToFile(this.getClass().getResourceAsStream("schemas/mathml/" + name), schema);
			}
			else {
				FileUtils.writeInputStreamToFile(this.getClass().getResourceAsStream("schemas/odf/" + name), schema);
			}
			schemaFiles.put(label, schema);
			success[i] = schema.exists();
			i++;
		}
		
		int count = 0;
		for (boolean b : success) {
			if(b==true) {
				count++;
			}
		}
		
		if(count==entries.length) {
//			schemasProvided = true;
			return true;
		}
		else {
//			schemasProvided = false;
			return false;
		}
	}
}
