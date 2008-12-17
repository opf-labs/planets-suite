package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperties;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ServiceUtils;


/**
 * @author melmsp
 *
 */
@Stateless()
@Local(Characterise.class)
@Remote(Characterise.class)

@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
//@MTOM
//@BindingType(value=SOAPBinding.SOAP12HTTP_MTOM_BINDING)
//@StreamingAttachment(parseEagerly=true, memoryThreshold=5000000L)
@WebService(
        name = Extractor.NAME, 
        serviceName = Characterise.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.characterise.Characterise")

public class Extractor implements Characterise, Serializable {
	
	private static final long serialVersionUID = -8537596616209516979L;
	
	public static final String NAME = "Extractor";
	public static final String OUT_DIR = NAME.toUpperCase() + "_OUT" + File.separator;
	public static final PlanetsLogger LOG = PlanetsLogger.getLogger(Extractor.class);
	public static final int MAX_FILE_SIZE = 10240;

	public CharacteriseResult characterise(DigitalObject digitalObject,
			String optionalFormatXCEL, Parameters parameters) {
		
		DigitalObject resultDigOb = null;
		ServiceReport sReport = new ServiceReport();
		CharacteriseResult characteriseResult = null;
		
		CoreExtractor coreExtractor = new CoreExtractor(Extractor.NAME, LOG);
		
		byte[] inputData = null;
		
		if(digitalObject.getContent().isByValue()) {
			inputData = digitalObject.getContent().getValue();
		}
		else {
			inputData = FileUtils.writeInputStreamToBinary(digitalObject.getContent().read());
		}
		
		byte[] result = null;
		
		if(optionalFormatXCEL!=null) {
			result = coreExtractor.extractXCDL(inputData, optionalFormatXCEL.getBytes(), parameters);
		}
		else {
			result = coreExtractor.extractXCDL(inputData, null, parameters);
		}
		
		int sizeInKB = 0;
		
		if (result != null) {
			sizeInKB = result.length / 1024;
			
			// output Files smaller than 10Mb
			if (sizeInKB < MAX_FILE_SIZE) {
				try {
					resultDigOb = new DigitalObject.Builder(Content.byValue(result)).permanentUrl(new URL("http://planets-project.eu/services/pc/planets-extractor-service")).build();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				sReport.setInfo("Success!!! Extracted XCDL from ");
				sReport.setErrorState(0);
				characteriseResult = new CharacteriseResult(resultDigOb, sReport);
			}
			else {
				File tmpResult = FileUtils.getTempFile(result, "tmpResult", "tmp"); 
				try {
					resultDigOb = new DigitalObject.Builder(Content.byReference(tmpResult.toURL())).build();
					sReport.setInfo("Success!!!");
					sReport.setErrorState(0);
					characteriseResult = new CharacteriseResult(resultDigOb, sReport);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			this.returnWithErrorMessage("ERROR: No XCDL created!", null);
		}
		
		return characteriseResult;
	}
	
	
	/**
	 * @param message an optional message on what happened to the service
	 * @param e the Exception e which causes the problem
	 * @return CharacteriseResult containing a Error-Report
	 */
	private CharacteriseResult returnWithErrorMessage(final String message, final Exception e) {
        if (e == null) {
            return new CharacteriseResult(null, ServiceUtils.createErrorReport(message));
        } else {
            return new CharacteriseResult(null, ServiceUtils.createExceptionErrorReport(message, e));
        }
    }
	

	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(Extractor.NAME, Characterise.class.getCanonicalName());
        sd.author("Peter Melms, mailto:peter.melms@uni-koeln.de");
        sd.description("A wrapper for the Extractor tool. The tool returns the extracted properties\n" 
        		+ "in a XCDL file");        
        sd.classname(this.getClass().getCanonicalName());
        sd.version("0.1");
        
        List<Parameter> parameterList = new ArrayList<Parameter>();
        Parameter normDataFlag = new Parameter("disableNormDataInXCDL", "-n");
        normDataFlag.setDescription("Disables NormData output in result XCDL. Reduces file size. Allowed value: '-n'");
        parameterList.add(normDataFlag);
        
        Parameter enableRawData = new Parameter("enableRawDataInXCDL", "-r");
        enableRawData.setDescription("Enables the output of RAW Data in XCDL file. Allowed value: '-r'");
        parameterList.add(enableRawData);
        
        Parameters parameters = new Parameters();
        parameters.setParameters(parameterList);
        
        sd.parameters(parameters);
        
		return sd.build();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.services.characterise.Characterise#listProperties(java.net.URI)
	 */
	public final List<FileFormatProperty> listProperties(final URI formatURI) {
		FileFormatProperties fileFormatProperties = ExtractorPropertiesLister.getFileFormatProperties(formatURI);
		List<FileFormatProperty> properties = fileFormatProperties.getProperties();
		return properties;
	}

}
