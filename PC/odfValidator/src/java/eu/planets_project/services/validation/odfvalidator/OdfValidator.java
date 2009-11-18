/**
 * 
 */
package eu.planets_project.services.validation.odfvalidator;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;
import eu.planets_project.services.validation.odfvalidator.utils.CoreOdfValidator;
import eu.planets_project.services.validation.odfvalidator.utils.OdfValidatorResult;


/**
 * @author Peter Melms, peter.melms@uni-koeln.de
 *
 */
@WebService(
		name = OdfValidator.NAME, 
		serviceName = Validate.NAME, 
		targetNamespace = PlanetsServices.NS, 
		endpointInterface = "eu.planets_project.services.validate.Validate")
@StreamingAttachment(parseEagerly = true)
@Stateless
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class OdfValidator implements Validate {
	
	public static final String NAME = "OdfValidator";
	
	private static Log log = LogFactory.getLog(OdfValidator.class);
	
	private static FormatRegistry techReg = FormatRegistryFactory.getFormatRegistry();
	
	private static String NEWLINE = System.getProperty("line.separator");
	
	private String usedSchemas = new String(FileUtils.writeInputStreamToBinary(CoreOdfValidator.class.getResourceAsStream("schema_list.properties")));

	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME,Validate.class.getCanonicalName());
	    sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
	    sd.description("This is an ODF Validator service. It uses the tool 'jing' to check all components of a ODF file for their validity." +
	    		"It supports ODF 1.0, ODF 1.1 fully and ODF 1.2 in a preliminary state." + NEWLINE + 
	    		"You can pass custom RelaxNG schema files to validate against. " + NEWLINE +
	    		"You have two ways of providing the custom RelaxNG schema: 1) You can pass the schema as a String or " +
	    		"												   2) pass a URL where the schema can be retrieved." + NEWLINE + 
	    		"If you don't pass a custom schema, the official ODF schemas are used for validation: " + NEWLINE +
	    		"---------------------------------------------------------" + NEWLINE + 
	    		usedSchemas + NEWLINE + 
	    		"---------------------------------------------------------" + NEWLINE +
	    		"The schemas are retrieved automatically, depending on the version of the ODF input file.");
	    sd.classname(this.getClass().getCanonicalName());
	    sd.version("1.0");
	    sd.name(NAME);
	    sd.type(Validate.class.getCanonicalName());
	    List<Parameter> parameterList = new ArrayList<Parameter>();
	    
	    Parameter user_doc_schema_param = new Parameter.Builder("user-doc-schema", "[1) The RwlxNG-Schema read to a String / 2) a URL where the schema can be retrieved from.]")
	    									.type("String")
	            							.description("1) You can pass a custom doc-schema file to validate against, read into a String." + NEWLINE + 
	            										 "2) You can also pass a URL to load the schema. " + NEWLINE + 
	            										 "To indicate this, please use 'doc-schema-url=' marker. Please see the following example:" + NEWLINE +
	            										 "EXAMPLE:" + NEWLINE +  
	            										 "--------" + NEWLINE +
	            										 "user-doc-schema=doc-schema-url=http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-schema-v1.1.rng")
	            							.build();
	    parameterList.add(user_doc_schema_param);
	    
	    Parameter user_doc_strict_schema_param = new Parameter.Builder("user-doc-strict-schema", "[1) The Schema read to a String / 2) a URL where the schema can be retrieved from.]")
											.type("String")
											.description("1) You can pass a custom doc-strict-schema file to validate against, read into a String." + NEWLINE + 
	            										 "2) You can also pass a URL to load the schema. " + NEWLINE + 
	            										 "To indicate this, please use 'doc-strict-schema-url=' marker. Please see the following example:" + NEWLINE +
	            										 "EXAMPLE:" + NEWLINE +  
	            										 "--------" + NEWLINE +
	            										 "user-doc-strict-schema=doc-strict-schema-url=http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-strict-schema-v1.1.rng" + NEWLINE + 
	            										 "PLEASE NOTE: If you pass a [user-doc-strict-schema] file, you have to pass a [user-doc-schema] as well, as the strict schema usually references the doc-schema, " +
	            										 "obviously this won't work, if you haven't passed any ;-).")
											.build();
	    parameterList.add(user_doc_strict_schema_param);
	    
	    Parameter user_manifest_schema_param = new Parameter.Builder("user-manifest-schema", "[1) The Schema read to a String / 2) a URL where the schema can be retrieved from.]")
											.type("String")
											.description("1) You can pass a custom manifest-schema file to validate against, read into a String." + NEWLINE + 
	            										 "2) You can also pass a URL to load the schema. " + NEWLINE + 
	            										 "To indicate this, please use 'manifest-schema-url=' marker. Please see the following example:" + NEWLINE +
	            										 "EXAMPLE:" + NEWLINE +  
	            										 "--------" + NEWLINE +
	            										 "user-manifest-schema=manifest-schema-url=http://docs.oasis-open.org/office/v1.1/OS/OpenDocument-manifest-schema-v1.1.rng")
											.build();
	    parameterList.add(user_manifest_schema_param);
	    
	    Parameter strict_validation_param = new Parameter.Builder("strictValidation", "true/false")
	    									.type("boolean")
	    									.description("Enable STRICT Validation (i.e. validate against the strict-schema. Default is false/disabled." + NEWLINE + 
	    											"PLEASE NOTE: 1) If you enable STRICT validation and pass a [user-doc-schema] without passing a [user-doc-strict-schema], STRICT validation will be disabled." + NEWLINE + 
	    											"             2) Enabling STRICT validation for a ODF v1.2 file will have no effect and thus this parameter is ignored for ODF v1.2 files!")
	    									.build();
	    parameterList.add(strict_validation_param);
	    
	    sd.parameters(parameterList);
	
	    sd.tool(Tool.create(null, "Jing", "20090818", null, "http://www.thaiopensource.com/relaxng/jing.html"));
	    List<URI> inputList = new ArrayList<URI>();
	    inputList.add(techReg.createExtensionUri("odt"));
	    inputList.add(techReg.createExtensionUri("ods"));
	    inputList.add(techReg.createExtensionUri("odp"));
	    inputList.add(techReg.createExtensionUri("odg"));
	    inputList.add(techReg.createExtensionUri("odm"));
	    inputList.add(techReg.createExtensionUri("odb"));
	    inputList.add(techReg.createExtensionUri("odf"));
		sd.inputFormats(inputList.toArray(new URI[]{}));
		return sd.build();
	}



	public ValidateResult validate(DigitalObject digitalObject, URI format,
			List<Parameter> parameters) {
		
		if(digitalObject==null || digitalObject.getContent() == null) {
			return this.returnWithErrorMessage(format, "[OdfValidator] ERROR: No input file found!", null);
		}
		
		String name = DigitalObjectUtils.getFileNameFromDigObject(digitalObject, format);
		
		File odfValidatorTmp = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
		
		File inputOdfFile = new File(odfValidatorTmp, FileUtils.randomizeFileName(name));
		FileUtils.writeInputStreamToFile(digitalObject.getContent().read(), inputOdfFile); 
		
		CoreOdfValidator odfValidator = new CoreOdfValidator();
		
		OdfValidatorResult result = odfValidator.validate(inputOdfFile, parameters);
		
		ValidateResult vr = null;
		
		if(result.documentIsValid()) {
			vr = new ValidateResult.Builder(format,
	                new ServiceReport(Type.INFO, Status.SUCCESS, result.getValidationResultAsString()))
	                .ofThisFormat(result.isOdfFile())
	                .validInRegardToThisFormat(result.documentIsValid()).build();
		}
		else {
			vr = new ValidateResult.Builder(format, new ServiceReport(Type.INFO, Status.SUCCESS, result.getValidationResultAsString()))
			.ofThisFormat(result.isOdfFile())
			.validInRegardToThisFormat(result.documentIsValid()).build();
		}
		return vr;
	}
	
	 /**
     * @param message an optional message on what happened to the service
     * @param e the Exception e which causes the problem
     * @return CharacteriseResult containing a Error-Report
     */
    private ValidateResult returnWithErrorMessage(final URI format, final String message,
            final Exception e) {
        if (e == null) {
            return new ValidateResult.Builder(format, ServiceUtils
                    .createErrorReport(message)).build();
        } else {
            return new ValidateResult.Builder(format, ServiceUtils
                    .createExceptionErrorReport(message, e)).build();
        }
    }

}
