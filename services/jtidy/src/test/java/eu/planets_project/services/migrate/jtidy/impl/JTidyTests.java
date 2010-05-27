package eu.planets_project.services.migrate.jtidy.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.Format.UriType;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.migrate.jtidy.JTidy;
import eu.planets_project.services.utils.DigitalObjectUtils;

public class JTidyTests {
	
	/***/
    static final String WSDL = "/pserv-pa-jtidy/JTidy?wsdl";
    
    static Migrate JTIDY;
    
    static String TEST_OUT_PATH;
    
//    static File TEST_OUT_FOLDER;
    
    static URI HTML_OLD_URI;
    static URI HTML_20_URI;
    static URI HTML_32_URI;
    static URI HTML_40_URI;
    static URI HTML_401_URI;
    static URI WORD_HTML_URI;
    static URI XHTML_10_URI;
    
    static String CONFIG_STRING;
    
    static MigrationPath[] migrationPaths;
    
    

	@BeforeClass
	public static void setUp() throws Exception {
    	System.out.println("*******************************");
    	System.out.println("* JTidy: Running tests: *");
    	System.out.println("*******************************");
    	System.out.println();
    	
    	TEST_OUT_PATH = JTidyUnitHelper.JTIDY_LOCAL_TEST_OUT;
    	
//    	TEST_OUT_FOLDER = FileUtils.createWorkFolderInSysTemp(TEST_OUT_PATH);
    	
    	JTIDY = new JTidy();
    	
    	migrationPaths = JTIDY.describe().getPaths().toArray(new MigrationPath[]{});
    	
    	HTML_OLD_URI = new URI("info:pronom/fmt/96");
    	HTML_20_URI = new URI("info:pronom/fmt/97");
    	HTML_32_URI = new URI("info:pronom/fmt/98");
    	HTML_40_URI = new URI("info:pronom/fmt/99");
    	HTML_401_URI = new URI("info:pronom/fmt/100");
    	WORD_HTML_URI = new URI("planets:fmt/ext/word-html");
    	XHTML_10_URI = new URI("info:pronom/fmt/102");
    	
    	CONFIG_STRING = FileUtils.readFileToString(JTidyUnitHelper.CONFIG_FILE);
	}

	@Test
	public void testDescribe() {
		ServiceDescription sd = JTIDY.describe();
		assertTrue("The ServiceDescription should not be NULL.", sd != null );
    	System.out.println("test: describe()");
    	System.out.println("--------------------------------------------------------------------");
    	System.out.println();
    	System.out.println("Received ServiceDescription from: " + JTIDY.getClass().getName());
    	System.out.println(sd.toXmlFormatted());
    	System.out.println("--------------------------------------------------------------------");
	}
	
	@Test
	public void testAllPossibleMigrationPathways() {
		System.out.println("Testing all possible pathways...START");
		for(int i=0;i<migrationPaths.length;i++) {
			MigrationPath path = migrationPaths[i];
			URI inputFormat = path.getInputFormat();
			URI outputFormat = path.getOutputFormat();
			
			System.out.println();
			System.out.println("Testing migrationPath: [" + inputFormat.toASCIIString() + " --> " + outputFormat.toASCIIString() + "]");
			System.out.println();
			
			List<Parameter> parameters = createParameters(true);
			
			testMigrate(inputFormat, outputFormat, parameters);
		}
		
		System.out.println();
		System.out.println("Testing migrationPath: [" + WORD_HTML_URI.toASCIIString() + " --> " + XHTML_10_URI.toASCIIString() + "]");
		System.out.println();
		
		List<Parameter> parameters = createParameters(false);
		testMigrate(WORD_HTML_URI, XHTML_10_URI, parameters);
	}
	
	
	
	
	private void testMigrate(URI inputFormat, URI outputFormat, List<Parameter> parameters) {
		String htmlVersion = getHtmlVersion(inputFormat);
		Assert.assertNotNull("HTML version is null!", htmlVersion);
		DigitalObject digObj = createDigitalObject(inputFormat);
		
		MigrateResult mr = JTIDY.migrate(digObj, inputFormat, outputFormat, parameters);

		ServiceReport sr = mr.getReport();
		
		if(sr.getType() == Type.ERROR) {
			System.err.println("FAILED: " + sr);
		}
		else {
			System.out.println("SUCCESS! Got Report: " + sr);
        
			DigitalObject doOut = mr.getDigitalObject();

			assertTrue("Resulting digital object is null.", doOut != null);
			
			File result = DigitalObjectUtils.toFile(doOut);
			
			System.out.println("Resulting file size: " + result.length() + " bytes.");
			System.out.println("Resulting file path: " + result.getAbsolutePath());
			System.out.println("Result: " + doOut);
			
//			System.out.println("Result.content: " + doOut.getContent());
//			System.out.println("Result.content.isByValue: " + doOut.getContent().isByValue());
//			System.out.println("Result.content.getReference: " + doOut.getContent().getReference());
		}
	}
	
	private List<Parameter> createParameters(boolean useConfigFile) {
    	List<Parameter> parameterList = new ArrayList<Parameter>();
        
    	if(useConfigFile) {
    		Parameter configFile = new Parameter("configFile", CONFIG_STRING);
            parameterList.add(configFile);
            return parameterList;
    	}
    	else {
    		return null;
    	}
        
        
    }
	
	private String getHtmlVersion(URI pronomID) {
		if(pronomID.equals(HTML_OLD_URI)) {
			return "older_than_2.0";
		}
		if(pronomID.equals(HTML_20_URI)) {
			return "2.0";
		}
		if(pronomID.equals(HTML_32_URI)) {
			return "3.2";
		}
		if(pronomID.equals(HTML_40_URI)) {
			return "4.0";
		}
		if(pronomID.equals(HTML_401_URI)) {
			return "4.01";
		}
		if(pronomID.equals(WORD_HTML_URI)) {
			return "ms_word_generated_html";
		}
		return null;
	}
	
	private DigitalObject createDigitalObject(URI pronomID) {
		
		File inputFile = getTestFile(pronomID);
    	
        DigitalObject input = null;
        
        input = new DigitalObject.Builder(Content.byValue(inputFile))
			.permanentUri(URI.create(PlanetsServices.NS+"/pserv-pa-jtidy"))
			.format(pronomID)
			.title(inputFile.getName())
			.build();
        
        return input;
	}
	
	private File getTestFile(URI pronomID) {
		if (FormatRegistryFactory.getFormatRegistry().isUriOfType(pronomID,
                UriType.PRONOM)) {
	    	if(pronomID.equals(HTML_OLD_URI)) {
	    		return JTidyUnitHelper.HTML_OLD_FILE;
	    	}
	    	if(pronomID.equals(HTML_20_URI)) {
	    		return JTidyUnitHelper.HTML20_FILE;
	    	}
	    	if(pronomID.equals(HTML_32_URI)) {
	    		return JTidyUnitHelper.HTML32_FILE;
	    	}
	    	if(pronomID.equals(HTML_40_URI)) {
	    		return JTidyUnitHelper.HTML40_FILE;
	    	}
	    	if(pronomID.equals(HTML_401_URI)) {
	    		return JTidyUnitHelper.HTML401_FILE;
	    	}
		}
		else {
			if(pronomID.equals(WORD_HTML_URI)) {
				return JTidyUnitHelper.WORD_HTML_FILE;
			}
		}
		return null;
    }
    
}
