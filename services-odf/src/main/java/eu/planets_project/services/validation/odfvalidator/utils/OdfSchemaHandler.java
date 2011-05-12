package eu.planets_project.services.validation.odfvalidator.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class OdfSchemaHandler {
	
	
	
	public static final String ODF_v1_0 = "ODF:1.0";
	public static final String ODF_v1_1 = "ODF:1.1";
	public static final String ODF_v1_2 = "ODF:1.2";
	public static final String MATHML_v101 = "MATHML:1.01";
	public static final String MATHML_v2 = "MATHML:2.0";
	
	// The path to the schema files
	private static final String ODF_SCHEMAS_PATH = "schemas" + File.separator + "odf" + File.separator;
	private static final String MATHML_SCHEMAS_PATH = "schemas" + File.separator + "mathml" + File.separator;
	
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
	
	public static HashMap<String, String> v10_namespaces = null;
	public static HashMap<String, String> v11_namespaces = null;
	public static HashMap<String, String> v12_namespaces = null;
	
	private static final String INCLUDE_HREF = "<include href=\"";
	
	private static HashMap<String, File> schemaFiles = new HashMap<String, File>();
	
	private static final String ODF_SCHEMA_LIST = "schema_list.properties";
	private static final String MATHMLSCHEMAS_PROPERTIES = "mathmlschemas.properties";
	private static final String NAMESPACES_PROPERTIES_NAME = "namespaces.properties";
	
	private static String ODF_SH_TMP_NAME = "ODF_SCHEMA_HANDLER";
	
	private static File MATHML_SCHEMAS = null;
	private static File ODF_SCHEMAS = null;
	private static String SCHEMAS_NAME = "SCHEMAS";
	
	private static Logger log = Logger.getLogger(OdfSchemaHandler.class.getName());
	
	public OdfSchemaHandler() {
		log.setLevel(Level.INFO);
		try {
		    File tempFile = File.createTempFile("dummy", null);
            ODF_SCHEMAS = new File(tempFile.getParentFile(), "odf");
            MATHML_SCHEMAS = new File(tempFile.getParentFile(), "mathml");
        } catch (IOException e) {
            e.printStackTrace();
        }
		boolean provided = provideSchemas();
		boolean ns_provided = prepareNamespaceTables(NAMESPACES_PROPERTIES_NAME);
		log.info("All schemas provided = " + provided);
		log.info("Prepared Namespace lookup tables: " + ns_provided);
	}
	
	public File getDocumentSchema(String version, boolean strictValidation) {
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
	
	public File getMathMLSchema() {
		log.info("[OdfSchemaHandler] retrieveMathMLSchemaFile(): using MathML 2 Schema file: " + schemaFiles.get(MATHML2_SCHEMA).getName());
		return schemaFiles.get(MATHML2_SCHEMA);
	}
	
	public File getDsigSchema(String version) {
		return schemaFiles.get(v12_DSIG_SCHEMA);
	}
	
	public File getManifestSchema(String version) {
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
			userDocSchema = getDocumentSchema(version, false);
		}
		else {
			try {
                userDocSchema = File.createTempFile("userDocSchema",".rng");
                FileUtils.writeStringToFile(userDocSchema, schemaContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		return userDocSchema;
	}
	
	
	public File createUserDocSchemaFromUrl(String version, URL docSchemaURL) {
		File userDocSchema = null;
		try {
			userDocSchema = File.createTempFile("userDocSchema",".rng");
			log.info("Reading content from URL (" + docSchemaURL.toString() + ")...please hang on!");
			FileUtils.copyURLToFile(docSchemaURL, userDocSchema);
		} catch (IOException e) {
			log.severe("ERROR: Could not open URL: " + docSchemaURL.toString() + "!");
			userDocSchema = getDocumentSchema(version, false);
		}
		return userDocSchema;
	}
	
	public File createUserDocStrictSchema(String version, String schemaContent, File userDocSchema) {
		File userDocStrictSchema = null;
		if(schemaContent.equalsIgnoreCase("")
				|| schemaContent==null) {
			log.warning("WARN: User strict schema not found! Received String is empty!");
			log.info("Trying to lookup DEFAULT doc-strict-schema...");
			userDocStrictSchema = getDocumentSchema(version, true);
		}
		else {
			if(schemaContent.contains(INCLUDE_HREF)) {
				int start = schemaContent.indexOf(INCLUDE_HREF) + 1;
				int end = schemaContent.indexOf("\"", start + INCLUDE_HREF.length());
				String toReplace = schemaContent.substring(start + INCLUDE_HREF.length()-1, end);
				schemaContent = schemaContent.replace(toReplace, userDocSchema.getName());
			}
			try {
                userDocStrictSchema = File.createTempFile("userDocStrictSchema",".rng");
                FileUtils.writeStringToFile(userDocStrictSchema, schemaContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		return userDocStrictSchema;
	}
	
	public File createUserDocStrictSchemaFromUrl(String version, URL strictSchemaUrl, File userDocSchema) {
		String schemaContent = null;
		File userDocStrictSchema = null;
		try {
			log.info("Reading content from URL (" + strictSchemaUrl.toString() + ")...please hang on!");
			schemaContent = writeInputStreamToString(strictSchemaUrl.openStream());
			userDocStrictSchema = createUserDocStrictSchema(version, schemaContent, userDocSchema);
		} catch (IOException e) {
			log.severe("Could not open URL: " + strictSchemaUrl.toString() + "!");
			userDocStrictSchema = getDocumentSchema(version, true);
		}
		return userDocStrictSchema;
	}
	
	public File createUserDsigSchema(String schemaContent) {
		try {
            File userDsigSchema = File.createTempFile("userDsigSchema",".rng");
            FileUtils.writeStringToFile(userDsigSchema, schemaContent);
            return userDsigSchema;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public File createUserDsigSchemaFromUrl(String version, URL dsigSchemaUrl) {
		File userDsigSchema = null;
		try {
			userDsigSchema = File.createTempFile("userDsigSchema",".rng");
			log.info("Reading content from URL (" + dsigSchemaUrl.toString() + ")...please hang on!");
			FileUtils.copyURLToFile(dsigSchemaUrl, userDsigSchema);
		} catch (IOException e) {
			log.severe("ERROR: Could not open URL: " + dsigSchemaUrl.toString() + "!");
			userDsigSchema = getDsigSchema(version);
		}
		return userDsigSchema;
	}
	
	public File createUserManifestSchema(String schemaContent) {
		try {
            File userManifestSchema = File.createTempFile("userManifestSchema",".rng");
            FileUtils.writeStringToFile(userManifestSchema, schemaContent);
            return userManifestSchema;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public File createUserManifestSchemaFromUrl(String version, URL manifestSchemaUrl) {
		File userManifestSchema = null;
		try {
			userManifestSchema = File.createTempFile("userManifestSchema",".rng");
			log.info("Reading content from URL (" + manifestSchemaUrl.toString() + ")...please hang on!");
			FileUtils.copyURLToFile(manifestSchemaUrl, userManifestSchema);
		} catch (IOException e) {
			log.severe("ERROR: Could not open URL: " + manifestSchemaUrl.toString() + "!");
			userManifestSchema = getDocumentSchema(version, false);
		}
		return userManifestSchema;
	}
	
	public boolean provideSchemas(){
		boolean odfSchemasProvided = provideOdfSchemas(ODF_SCHEMA_LIST);
		
		boolean mathmlSchemasProvided = provideMathMLSchemas(MATHMLSCHEMAS_PROPERTIES);
		
		if(!odfSchemasProvided) {
			log.severe("ERROR: Unable to provide ODF schemas listed in '" + ODF_SCHEMA_LIST + "'!");
		}
		if(!mathmlSchemasProvided) {
			log.severe("ERROR: Unable to provide MathML schemas listed in '" + MATHMLSCHEMAS_PROPERTIES + "'!");
		}
		if(odfSchemasProvided && mathmlSchemasProvided) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean prepareNamespaceTables(String namespacePropertiesFileName) {
	    String namespaces = writeInputStreamToString(this.getClass().getResourceAsStream(namespacePropertiesFileName));
		String[] versionedNamespaces = namespaces.split("####");
		
		for (String currentPart : versionedNamespaces) {
			currentPart = currentPart.trim();
			if(currentPart.contains("v1.0")) {
				if(v10_namespaces==null) {
					v10_namespaces = new HashMap<String, String>();
				}
				String[] nsPairs = currentPart.split(System.getProperty("line.separator")); 
				for (int i=1;i<nsPairs.length;i++) {
					String[] lineParts = nsPairs[i].split("=");
					String name = lineParts[0].trim();
					String value = lineParts[1].trim();
					v10_namespaces.put(name, value);
				}
			}
			if(currentPart.contains("v1.1")) {
				if(v11_namespaces==null) {
					v11_namespaces = new HashMap<String, String>();
				}
				String[] nsPairs = currentPart.split(System.getProperty("line.separator")); 
				for (int i=1;i<nsPairs.length;i++) {
					String[] lineParts = nsPairs[i].split("=");
					String name = lineParts[0].trim();
					String value = lineParts[1].trim();
					v11_namespaces.put(name, value);
				}
			}
			if(currentPart.contains("v1.2")) {
				if(v12_namespaces==null) {
					v12_namespaces = new HashMap<String, String>();
				}
				String[] nsPairs = currentPart.split(System.getProperty("line.separator")); 
				for (int i=1;i<nsPairs.length;i++) {
					String[] lineParts = nsPairs[i].split("=");
					String name = lineParts[0].trim();
					String value = lineParts[1].trim();
					v12_namespaces.put(name, value);
				}
			}			
		}
		return true;
	}

    private String writeInputStreamToString(InputStream stream) {
        StringWriter stringWriter = new StringWriter();
	    try {
            IOUtils.copy(stream, stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
		String namespaces = stringWriter.toString().trim();
        return namespaces;
    }

	
	private boolean provideMathMLSchemas(String mathmlSchemaListName) {
		String mathmlSchemaList = writeInputStreamToString(this.getClass().getResourceAsStream(mathmlSchemaListName));
		String[] entries = mathmlSchemaList.split(System.getProperty("line.separator"));
		int entryCount = entries.length;
		boolean[] success = new boolean[entryCount];
		int i=0;
		for (String currentEntry : entries) {
			String[] parts = currentEntry.split("/");
			String parent = parts[0].trim();
			String name = parts[1].trim();
			File parentDir = new File(MATHML_SCHEMAS, parent);
			if(!parentDir.exists()) {
				try {
                    parentDir.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
			File schema = new File(parentDir, name);
			if(!schema.exists()) {
				writeInputStreamToFile(this.getClass().getResourceAsStream(MATHML_SCHEMAS_PATH + parent + File.separator + name), schema);
			}
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
	
    private void writeInputStreamToFile(InputStream stream, File file) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            IOUtils.copy(stream, fOut);
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean provideOdfSchemas(String odfSchemaListName) {
		String odfSchemaList = writeInputStreamToString(this.getClass().getResourceAsStream(odfSchemaListName));
		String[] entries = odfSchemaList.split(System.getProperty("line.separator"));
		int entryCount = entries.length;
		boolean[] success = new boolean[entryCount];
		int i=0;
		for (String currentEntry : entries) {
			String[] labelsAndValues = currentEntry.split("=");
			String label = labelsAndValues[0].trim();
			String name = labelsAndValues[1].trim();
			File schema = null;
			if(label.equalsIgnoreCase(MATHML2_SCHEMA)) {
				schema = new File(MATHML_SCHEMAS, name);
				if(!schema.exists()) {
					writeInputStreamToFile(this.getClass().getResourceAsStream(MATHML_SCHEMAS_PATH + name), schema);
				}
			}
			else {
				schema = new File(ODF_SCHEMAS, name);
				if(!schema.exists()) {
					writeInputStreamToFile(this.getClass().getResourceAsStream(ODF_SCHEMAS_PATH + name), schema);
				}
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
			return true;
		}
		else {
			return false;
		}
	}
}
