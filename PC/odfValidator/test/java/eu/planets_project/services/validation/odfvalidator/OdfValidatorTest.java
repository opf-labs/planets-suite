/**
 * 
 */
package eu.planets_project.services.validation.odfvalidator;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;

/**
 * @author melmsp
 *
 */
public class OdfValidatorTest {
	
	private static String WSDL = "/pserv-pc-odfvalidator/OdfValidator?wsdl";
	
	private static Validate validator = null;

//	File v10TestIn = new File("tests/test-files/documents/test_odt/v11/test_file_v11.odt");
	File v10TestIn = new File("tests/test-files/documents/test_pdf/2274192346_4a0a03c5d6.pdf");
	
	static File v10UserDocSchema = new File("tests/test-files/documents/test_odt/v10/schemas/OpenDocument-schema-v1.0-os.rng");
	static File v10UserDocStrictSchema = new File("tests/test-files/documents/test_odt/v10/schemas/OpenDocument-strict-schema-v1.0-os.rng");
	static File v10UserManifestSchema = new File("tests/test-files/documents/test_odt/v10/schemas/OpenDocument-manifest-schema-v1.0-os.rng");

//	File v11TestIn = new File("tests/test-files/documents/test_odt/v11/test_file_v11.odt");
	File v11_text = new File("tests/test-files/documents/test_odt/v11/test_file_v11.odt");
	File v11_presentation = new File("tests/test-files/documents/test_odt/v11/test_presentation_v11.odp");
	File v11_database = new File("tests/test-files/documents/test_odt/v11/test_database_v11.odb");
	File v11_drawing = new File("tests/test-files/documents/test_odt/v11/drawing_v11.odg");
	File v11_table = new File("tests/test-files/documents/test_odt/v11/table_v11.ods");
	File v11_formula = new File("tests/test-files/documents/test_odt/v11/formula_v11.odf");
	static File v11UserDocSchema = new File("tests/test-files/documents/test_odt/v11/schemas/OpenDocument-schema-v1.1.rng");
	static File v11UserDocStrictSchema = new File("tests/test-files/documents/test_odt/v11/schemas/OpenDocument-strict-schema-v1.1.rng");
	static File v11UserManifestSchema = new File("tests/test-files/documents/test_odt/v11/schemas/OpenDocument-manifest-schema-v1.1.rng");
	
	File v12TestIn = new File("tests/test-files/documents/test_odt/v12/test_presentation_v12.odp");
	static File v12UserDocSchema = new File("tests/test-files/documents/test_odt/v12/schemas/OpenDocument-schema-v1.2-cd03.rng");
	static File v12UserManifestSchema = new File("tests/test-files/documents/test_odt/v12/schemas/OpenDocument-manifest-schema-v1.2-draft7.rng");
	
	FormatRegistry techReg = FormatRegistryFactory.getFormatRegistry();
	
	static List<Parameter> params = new ArrayList<Parameter>();
	static List<Parameter> v11_Params = new ArrayList<Parameter>();
	static List<Parameter> v12_Params = new ArrayList<Parameter>();
	
	static File[] testFiles = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		System.setProperty("pserv.test.context", "server");
//		System.setProperty("pserv.test.host", "localhost");
//		System.setProperty("pserv.test.port", "8080");
		
		File testFolder = new File("tests/test-files/documents/test_odt/odf_toolkit_test_files");
		testFiles = testFolder.listFiles();
		
		Parameter strictValidation = new Parameter.Builder("strictValidation", "true").build();
		params.add(strictValidation);
		
//		Parameter v11_user_doc_schema = new Parameter.Builder("user-doc-schema", FileUtils.readTxtFileIntoString(v11UserDocSchema)).build();
//		v11_Params.add(v11_user_doc_schema);
		Parameter v11_user_doc_schema = new Parameter.Builder("user-doc-schema", "doc-schema-url=http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-schema-v1.1.rng").build();
		v11_Params.add(v11_user_doc_schema);
//		Parameter v11_user_doc_strict_schema = new Parameter.Builder("user-doc-strict-schema", FileUtils.readTxtFileIntoString(v11UserDocStrictSchema)).build();
//		v11_Params.add(v11_user_doc_strict_schema);
//		Parameter v11_user_doc_strict_schema = new Parameter.Builder("user-doc-strict-schema", "doc-strict-schema-url=http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-strict-schema-v1.1.rng").build();
//		v11_Params.add(v11_user_doc_strict_schema);
//		Parameter v11_user_manifest_schema = new Parameter.Builder("user-manifest-schema", FileUtils.readTxtFileIntoString(v11UserManifestSchema)).build();
//		v11_Params.add(v11_user_manifest_schema);
//		Parameter v11_strict_validation = new Parameter.Builder("strictValidation", "true").build();
//		v11_Params.add(v11_strict_validation);
//		
//		Parameter v12_user_doc_schema = new Parameter.Builder("user-doc-schema", FileUtils.readTxtFileIntoString(v12UserDocSchema)).build();
//		v12_Params.add(v12_user_doc_schema);
////		Parameter v12_user_doc_strict_schema = new Parameter.Builder("user-doc-strict-schema", FileUtils.readTxtFileIntoString(v12UserDocSchema)).build();
////		v12_Params.add(v12_user_doc_strict_schema);
//		Parameter v12_strict_validation  = new Parameter.Builder("strictValidation", "true").build();
//		v12_Params.add(v12_strict_validation);
////		Parameter v12_user_manifest_schema = new Parameter.Builder("user-manifest-schema", FileUtils.readTxtFileIntoString(v11UserManifestSchema)).build();
////		v12_Params.add(v12_user_manifest_schema);
		validator = ServiceCreator.createTestService(Validate.QNAME, OdfValidator.class, WSDL);
	}
	
	@Test
	public void testDescribe() {
		System.out.println("running Service at: " + validator.QNAME);
        ServiceDescription desc = validator.describe();
        System.out.println("Recieved service description: " + desc.toXmlFormatted());
        assertTrue("The ServiceDescription should not be NULL.", desc != null );
	}
	
	@Test
	public void testOdfValidate() {
		
		for (File currentFile : testFiles) {
			if(currentFile.isHidden()) {
				continue;
			}
			printTestTitle("Testing validation against default schemas: " + currentFile.getName());
			DigitalObject testIn = new DigitalObject.Builder(Content.byReference(currentFile)).title(currentFile.getName()).build();
			URI format = techReg.createExtensionUri(FileUtils.getExtensionFromFile(currentFile));
			
			ValidateResult vr = validator.validate(testIn, format, null);
			
			assertTrue("ValidateResult should not be NULL!", vr!=null);
			System.out.println("[ValidateResult] Input file is ODF file = " + vr.isOfThisFormat());
			System.out.println("[ValidateResult] Input file is VALID ODF file = " + vr.isValidInRegardToThisFormat());
			ServiceReport sr = vr.getReport();
			System.out.println(sr);
		}
	}
	
	private static void printTestTitle(String title) {
		for(int i=0;i<title.length()+4;i++) {
			System.out.print("*");
		}
		System.out.println();
		System.out.println("* " + title + " *");
		for(int i=0;i<title.length()+4;i++) {
			System.out.print("*");
		}
		System.out.println();
	}

}
