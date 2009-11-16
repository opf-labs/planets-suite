package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.utils.FileUtils;

public class OdfSchemaHandler {
	
	
	public static final String v1_0 = "1.0";
	public static final String v1_1 = "1.1";
	public static final String v1_2 = "1.2";
	
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
	private static String v12_STRICT_DOC_SCHEMA = "v12_STRICT_DOC_SCHEMA";
	
	private static final String INCLUDE_HREF = "<include href=\"";
	
	private static HashMap<String, File> schemaFiles = null;
	
	private String ODF_SCHEMA_LIST = "schema_list.properties";
	
	private static File ODF_SH_TMP = null;
	private static String ODF_SH_TMP_NAME = "ODF_VALIDATOR_TMP";
	
	private static File SCHEMAS = null;
	private static String SCHEMAS_NAME = "SCHEMAS";
	private static boolean schemasProvided = false;
	
	private static Log log = LogFactory.getLog(OdfSchemaHandler.class);
	
	public OdfSchemaHandler() {
		ODF_SH_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), ODF_SH_TMP_NAME);
		SCHEMAS = FileUtils.createFolderInWorkFolder(ODF_SH_TMP, SCHEMAS_NAME);
		FileUtils.deleteAllFilesInFolder(SCHEMAS);
	}

	public File retrieveOdfDocSchemaFile(String version, boolean strictValidation) {
		if(!version.equalsIgnoreCase(v1_2) && strictValidation) {
			log.info("Strict Validation enabled!");
			if(version.equalsIgnoreCase(v1_0)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Strict Schema file: " + schemaFiles.get(v10_STRICT_DOC_SCHEMA).getName());
				return schemaFiles.get(v10_STRICT_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(v1_1)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Strict Schema file: " + schemaFiles.get(v11_STRICT_DOC_SCHEMA).getName());
				return schemaFiles.get(v11_STRICT_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(v1_2)) {
				log.info("Strict Validation not applicable for v1.2!");
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v12_DOC_SCHEMA).getName());
				return schemaFiles.get(v12_DOC_SCHEMA);
			}
			log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): Could NOT retrieve Strict Schema file for this ODF version: " + version);
		}
		else {
			if(version.equalsIgnoreCase(v1_0)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v10_DOC_SCHEMA).getName());
				return schemaFiles.get(v10_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(v1_1)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v11_DOC_SCHEMA).getName());
				return schemaFiles.get(v11_DOC_SCHEMA);
			}
			if(version.equalsIgnoreCase(v1_2)) {
				log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): using Schema file: " + schemaFiles.get(v12_DOC_SCHEMA).getName());
				return schemaFiles.get(v12_DOC_SCHEMA);
			}
			log.info("[CoreOdfValidator] retrieveOdfDocSchemaFile(): Could NOT retrieve Schema file for this ODF version: " + version);
		}
		return null;
	}

	public File retrieveOdfManifestSchemaFile(String version) {
		if(version.equalsIgnoreCase(v1_0)) {
			log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): using Manifest Schema file: " + schemaFiles.get(v10_MANIFEST_SCHEMA).getName());
			return schemaFiles.get(v10_MANIFEST_SCHEMA);
		}
		if(version.equalsIgnoreCase(v1_1)) {
			log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): using Manifest Schema file: " + schemaFiles.get(v11_MANIFEST_SCHEMA).getName());
			return schemaFiles.get(v11_MANIFEST_SCHEMA);
		}
		if(version.equalsIgnoreCase(v1_2)) {
			log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): using Manifest Schema file: " + schemaFiles.get(v12_MANIFEST_SCHEMA).getName());
			return schemaFiles.get(v12_MANIFEST_SCHEMA);
		}
		log.info("[OdfSchemaHandler] retrieveOdfManifestSchemaFile(): Could NOT retrieve Manifest Schema for this version: " + version);
		return null;
	}
	
	public File createUserDocSchema(String version, String schemaContent) {
		File userDocSchema = null;
		if(schemaContent.equalsIgnoreCase("")|| schemaContent==null) {
			log.warn("WARN: User schema not found! Received String is empty!");
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
			log.error("ERROR: Could not open URL: " + docSchemaURL.toString() + "!");
			userDocSchema = retrieveOdfDocSchemaFile(version, false);
		}
		return userDocSchema;
	}
	
	public File createUserDocStrictSchema(String version, String schemaContent, File userDocSchema) {
		File userDocStrictSchema = null;
		if(schemaContent.equalsIgnoreCase("")
				|| schemaContent==null) {
			log.warn("WARN: User strict schema not found! Received String is empty!");
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
			log.error("Could not open URL: " + strictSchemaUrl.toString() + "!");
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
			log.error("ERROR: Could not open URL: " + manifestSchemaUrl.toString() + "!");
			userManifestSchema = retrieveOdfDocSchemaFile(version, false);
		}
		return userManifestSchema;
	}

	public boolean provideSchemas(){
		if(schemasProvided) {
			return true;
		}
		String schemaList = new String(FileUtils.writeInputStreamToBinary(this.getClass().getResourceAsStream(ODF_SCHEMA_LIST)));
		String[] lines = schemaList.split(System.getProperty("line.separator"));
		
		int size = lines.length;
		boolean[] success = new boolean[size];
		schemaFiles = new HashMap<String, File>();
		int i = 0;
		for (String currentLine : lines) {
			String[] parts = currentLine.split("=");
			String schemaLabel = parts[0].trim();
			String schemaName = parts[1].trim();
			File schema = new File(SCHEMAS, schemaName);
			FileUtils.writeInputStreamToFile(this.getClass().getResourceAsStream(schemaName.replace(".rng", ".xml")), schema);
			schemaFiles.put(schemaLabel, schema);
			success[i]=true;
			i++;
		}
		
		int count = 0;
		for (boolean b : success) {
			if(b==true) {
				count++;
			}
		}
		if(count==success.length) {
			schemasProvided = true;
			return true;
		}
		else {
			return false;
		}
	}

}
