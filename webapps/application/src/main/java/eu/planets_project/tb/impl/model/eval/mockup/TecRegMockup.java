package eu.planets_project.tb.impl.model.eval.mockup;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget;

/**
 * FIXME This should really be some kind of shared resource, of course.
 * 
 * @author lindleyA
 * A quick mockup implementation of a technical registry which is able to return registered
 * values for unique parameter names e.g. 
 * parameter: 'planets:pc/xcdl/imageHeight/scale'
 * return: 'pixel'
 * currently used for returning xcdl metric return values and descriptions
 * Known limitations: fixed return type to String which is not suitable for all parameter values
 * -> could be addressed by returning a java.lang.Object together with a returnTypeInfo which fully qualified java type it is 
 *
 */
public class TecRegMockup {
	
	//contains all the mockup registry's data in memory
    private static Map<String,Object> metrics;
    private static Map<String,Object> properties;
    
    /** */
    public static final String URI_PREFIX = "planets:";
	
    /** */
    private static final String URI_SERVICE_PROP_ROOT = URI_PREFIX+"tb/srv/";
    private static final String URI_DO_PROP_ROOT = URI_PREFIX+"pc/";
    public static final String URI_XCDL_PROP_ROOT = URI_PREFIX+"pc/xcdl/property/";
    public static final String URI_XCDL_METRIC_ROOT = URI_PREFIX+"pc/xcdl/metric/";
    public static final String URI_ONTOLOGY_PROP_ROOT = URI_PREFIX+"pc/xcdl/ontology/";
    
    // Testbed Service properties.
    public static URI PROP_SERVICE_EXECUTION_SUCEEDED;
    public static URI PROP_SERVICE_VALID_RESPONSE;
    public static URI PROP_SERVICE_VALID_RESULT;
    public static URI PROP_SERVICE_TIME;
    public static final URI PROP_SERVICE_IDENTIFY_METHOD;
    
    // Testbed Digital Object Properties
    public static URI PROP_DO_FORMAT;
    public static URI PROP_DO_SIZE;

    /** Statically define the observable properties. FIXME Should be built from the TechReg */
    private static HashMap<URI,MeasurementImpl> observables;
    static {
        // Set up the properties:
        
        // Testbed Service properties.
        PROP_SERVICE_EXECUTION_SUCEEDED = URI.create( TecRegMockup.URI_SERVICE_PROP_ROOT+"call/success" );
        PROP_SERVICE_TIME = URI.create( TecRegMockup.URI_SERVICE_PROP_ROOT+"wallclock" );
        PROP_SERVICE_VALID_RESPONSE = URI.create( TecRegMockup.URI_SERVICE_PROP_ROOT+"response/valid" );
        PROP_SERVICE_VALID_RESULT = URI.create( TecRegMockup.URI_SERVICE_PROP_ROOT+"result/valid" );
        PROP_SERVICE_IDENTIFY_METHOD = URI.create( TecRegMockup.URI_SERVICE_PROP_ROOT + "identify/method" );
        
        // Testbed Digital Object Properties
        PROP_DO_FORMAT = URI.create( TecRegMockup.URI_DO_PROP_ROOT+"basic/format" );
        PROP_DO_SIZE = URI.create( TecRegMockup.URI_DO_PROP_ROOT+"basic/size" );
        
        //  Set up the hash map:
        observables = new HashMap<URI,MeasurementImpl>();
        // The service execution succeeded?
        observables.put( 
                PROP_SERVICE_EXECUTION_SUCEEDED,
                MeasurementImpl.create(
                        PROP_SERVICE_EXECUTION_SUCEEDED, 
                        "Service succeeded", "",
                        "Did this service execute successfully? i.e. no warnings or errors, even if  Value is true/false.", 
                        null, MeasurementTarget.SERVICE_TARGET )
        );
        // The service response was valid?
        observables.put( 
                PROP_SERVICE_VALID_RESPONSE,
                MeasurementImpl.create(
                        PROP_SERVICE_VALID_RESPONSE, 
                        "Service returned a valid response.", "",
                        "Did this service execute appropriately? Was the response appropriate, given the parameters? For example, if a migration service produced a result, but should not have done so as the arguments were not valid, then this should be set to 'false'. Value is true/false.", 
                        null, MeasurementTarget.SERVICE_TARGET )
        );
        // The service result was valid?
        observables.put( 
                PROP_SERVICE_VALID_RESULT,
                MeasurementImpl.create(
                        PROP_SERVICE_VALID_RESULT, 
                        "Service returned a valid result.", "",
                        "Was the result of the service operation valid. e.g. may be determined to be true if a user thinks the result of a migration is a good one. Value is true/false.", 
                        null, MeasurementTarget.SERVICE_TARGET )
        );
        // The service time
        observables.put( 
                PROP_SERVICE_TIME,
                MeasurementImpl.create(
                        PROP_SERVICE_TIME, 
                        "Service execution time", "seconds",
                        "The wall-clock time taken to execute the service, in seconds.", 
                        null, MeasurementTarget.SERVICE_TARGET )
        );

        // The measured type
        observables.put( 
                PROP_DO_FORMAT,
                MeasurementImpl.create(
                        PROP_DO_FORMAT, 
                        "The format of the Digital Object", "",
                        "The format of a Digital Object, specified as a Planets Format URI.", 
                        null, MeasurementTarget.SERVICE_DOB )
        );

        // The size
        observables.put( 
                PROP_DO_SIZE,
                MeasurementImpl.create(
                        PROP_DO_SIZE, 
                        "The size of the Digital Object", "bytes",
                        "The total size of a particular Digital Object.", 
                        null, MeasurementTarget.SERVICE_DOB )
        );
    }

	
	/** */
	static {
		metrics = new HashMap<String,Object>();
		//information on xcdl metrics
		metrics.put(URI_XCDL_METRIC_ROOT+"/levenstheinDistance/id", "15");
		metrics.put(URI_XCDL_METRIC_ROOT+"/levenstheinDistance/name", "levenstheinDistance");
		metrics.put(URI_XCDL_METRIC_ROOT+"/levenstheinDistance/inputdatatype", new String[]{"XCL:string"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/levenstheinDistance/returntdataype", "XCL:int");
		metrics.put(URI_XCDL_METRIC_ROOT+"/levenstheinDistance/returndatatype/javaobjecttype", "java.lang.Integer");
		metrics.put(URI_XCDL_METRIC_ROOT+"/levenstheinDistance/description", "The Levenshtein distance is a distance measure for strings. Two strings are compared with respect to the three basic operations insert, delete and replace in order to transform string A into string B. The value for this metric is the number of operations needed for transformation.");
		
		metrics.put(URI_XCDL_METRIC_ROOT+"/ratDiff/id", "205");
		metrics.put(URI_XCDL_METRIC_ROOT+"/ratDiff/name", "ratDiff");
		metrics.put(URI_XCDL_METRIC_ROOT+"/ratDiff/inputdatatype", new String[]{"XCL:rational"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/ratDiff/returntdataype", "XCL:rational");
		metrics.put(URI_XCDL_METRIC_ROOT+"/ratDiff/returndatatype/javaobjecttype", "java.lang.Double");
		metrics.put(URI_XCDL_METRIC_ROOT+"/ratDiff/description", "Arithmetical difference of two values (A,B) of data type XCL:rational");
		
		metrics.put(URI_XCDL_METRIC_ROOT+"/percDev/id", "210");
		metrics.put(URI_XCDL_METRIC_ROOT+"/percDev/name", "percDev");
		metrics.put(URI_XCDL_METRIC_ROOT+"/percDev/inputdatatype", new String[]{"XCL:int","XCL:rational"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/percDev/returntdataype", "XCL:rational");
		metrics.put(URI_XCDL_METRIC_ROOT+"/percDev/returndatatype/javaobjecttype", "java.lang.Double");
		metrics.put(URI_XCDL_METRIC_ROOT+"/percDev/description", "Percental deviation of two values (A,B) of data type XCL:int or XCL:rational. The output value indicates the percental deviation of value B from value A. An output value of PercDeviation=0.0 indicates equal values.");
		
		metrics.put(URI_XCDL_METRIC_ROOT+"/equal/id", "200");
		metrics.put(URI_XCDL_METRIC_ROOT+"/equal/name", "equal");
		metrics.put(URI_XCDL_METRIC_ROOT+"/equal/inputdatatype", new String[]{"Any XCL data type"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/equal/returntdataype", "XCL:boolean");
		metrics.put(URI_XCDL_METRIC_ROOT+"/equal/returndatatype/javaobjecttype", "java.lang.Boolean");
		metrics.put(URI_XCDL_METRIC_ROOT+"/equal/description", "Metric 'equal' is a simple comparison of two values (A,B) of any XCL data type on equality");
		
		metrics.put(URI_XCDL_METRIC_ROOT+"/intDiff/id", "201");
		metrics.put(URI_XCDL_METRIC_ROOT+"/intDiff/name", "intDiff");
		metrics.put(URI_XCDL_METRIC_ROOT+"/intDiff/inputdatatype", new String[]{"XCL:int"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/intDiff/returntdataype", "XCL:int");
		metrics.put(URI_XCDL_METRIC_ROOT+"/intDiff/returndatatype/javaobjecttype", "java.lang.Integer");
		metrics.put(URI_XCDL_METRIC_ROOT+"/intDiff/description", "Arithmetical difference of two values (A,B) of data type XCL:int.");
		
		metrics.put(URI_XCDL_METRIC_ROOT+"/hammingDistance/id", "10");
		metrics.put(URI_XCDL_METRIC_ROOT+"/hammingDistance/name", "hammingDistance");
		metrics.put(URI_XCDL_METRIC_ROOT+"/hammingDistance/inputdatatype", new String[]{"XCL:int", "XCL:rational", "XCL:string"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/hammingDistance/returntdataype", "XCL:int");
		metrics.put(URI_XCDL_METRIC_ROOT+"/hammingDistance/returndatatype/javaobjecttype", "java.lang.Integer");
		metrics.put(URI_XCDL_METRIC_ROOT+"/hammingDistance/description", "The hamming distance counts the number of non-corresponding symbols of two ordered sequences. The sequences must have equal length; hammingDistance=0 indicates equal sequences, i.e. no substitutions are required to change a sequence into the other. Hamming distance is often used for string comparison");
	
		metrics.put(URI_XCDL_METRIC_ROOT+"/RMSE/id", "50");
		metrics.put(URI_XCDL_METRIC_ROOT+"/RMSE/name", "RMSE");
		metrics.put(URI_XCDL_METRIC_ROOT+"/RMSE/inputdatatype", new String[]{"XCL:int", "XCL:rational"});
		metrics.put(URI_XCDL_METRIC_ROOT+"/RMSE/returntdataype", "XCL:rational");
		metrics.put(URI_XCDL_METRIC_ROOT+"/RMSE/returndatatype/javaobjecttype", "java.lang.Double");
		metrics.put(URI_XCDL_METRIC_ROOT+"/RMSE/description", "Root mean squared error is a widely used measure for deviation. It is obtained by the square root of the average of the squared differences of single values of two sets.");
	
        properties = new HashMap<String,Object>();
		//information on xcdl properties
		properties.put(URI_XCDL_PROP_ROOT+"imageHeight/id", "2");
		properties.put(URI_XCDL_PROP_ROOT+"imageHeight/name", "imageHeight");
		properties.put(URI_XCDL_PROP_ROOT+"imageHeight/unit", "pixel");
		properties.put(URI_XCDL_PROP_ROOT+"imageHeight/metrics", new String[]{"equal","intDiff","percDev"});
		
		properties.put(URI_XCDL_PROP_ROOT+"imageWidth/id", "30");
		properties.put(URI_XCDL_PROP_ROOT+"imageWidth/name", "imageWidth");
		properties.put(URI_XCDL_PROP_ROOT+"imageWidth/unit", "pixel");
		properties.put(URI_XCDL_PROP_ROOT+"imageWidth/metrics", new String[]{"equal","intDiff","percDev"});
		
		properties.put(URI_XCDL_PROP_ROOT+"normData/id", "300");
		properties.put(URI_XCDL_PROP_ROOT+"normData/name", "normData");
		properties.put(URI_XCDL_PROP_ROOT+"normData/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"normData/metrics", new String[]{"hammingDistance","RMSE","levenstheinDistance"});
		
		properties.put(URI_XCDL_PROP_ROOT+"bitsPerSample/id", "151");
		properties.put(URI_XCDL_PROP_ROOT+"bitsPerSample/name", "bitsPerSample");
		properties.put(URI_XCDL_PROP_ROOT+"bitsPerSample/unit", "bit");
		properties.put(URI_XCDL_PROP_ROOT+"bitsPerSample/metrics", new String[]{"equal","intDiff"});
		
		properties.put(URI_XCDL_PROP_ROOT+"compression/id", "18");
		properties.put(URI_XCDL_PROP_ROOT+"compression/name", "compression");
		properties.put(URI_XCDL_PROP_ROOT+"compression/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"compression/metrics", new String[]{"equal"});
		
		properties.put(URI_XCDL_PROP_ROOT+"imageType/id", "20");
		properties.put(URI_XCDL_PROP_ROOT+"imageType/name", "imageType");
		properties.put(URI_XCDL_PROP_ROOT+"imageType/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"imageType/metrics", new String[]{"equal"});
		
		properties.put(URI_XCDL_PROP_ROOT+"orientation/id", "9");
		properties.put(URI_XCDL_PROP_ROOT+"orientation/name", "orientation");
		properties.put(URI_XCDL_PROP_ROOT+"orientation/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"orientation/metrics", new String[]{"equal"});
		
		properties.put(URI_XCDL_PROP_ROOT+"planarConfiguration/id", "49");
		properties.put(URI_XCDL_PROP_ROOT+"planarConfiguration/name", "planarConfiguration");
		properties.put(URI_XCDL_PROP_ROOT+"planarConfiguration/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"planarConfiguration/metrics", new String[]{"equal"});
		
		properties.put(URI_XCDL_PROP_ROOT+"resolutionUnit/id", "22");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionUnit/name", "resolutionUnit");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionUnit/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionUnit/metrics", new String[]{"equal"});
		
		properties.put(URI_XCDL_PROP_ROOT+"resolutionX/id", "23");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionX/name", "resolutionX");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionX/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionX/metrics", new String[]{"equal","ratDiff","percDev"});
	
		properties.put(URI_XCDL_PROP_ROOT+"resolutionY/id", "24");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionY/name", "resolutionY");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionY/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"resolutionY/metrics", new String[]{"equal","ratDiff","percDev"});
		
		properties.put(URI_XCDL_PROP_ROOT+"scanDocName/id", "144");
		properties.put(URI_XCDL_PROP_ROOT+"scanDocName/name", "scanDocName");
		properties.put(URI_XCDL_PROP_ROOT+"scanDocName/unit", "undefined");
		properties.put(URI_XCDL_PROP_ROOT+"scanDocName/metrics", new String[]{"levenstheinDistance"});

	}

    /** */
    private TecRegMockup(){
        
    }

	/**
	 * Returns the registry's value for a given key or null
	 * Please note case sensitivity 
	 * @param sParamURI
	 * @return
	 */
	public static Object getParameterVal(String sParamURI){
        if( properties.containsKey(sParamURI))
            return properties.get(sParamURI);
        if( metrics.containsKey(sParamURI))
            return metrics.get(sParamURI);
		return null;
	}

    /**
     * @param observable
     * @return
     */
    public static MeasurementImpl getObservable( URI observable ) {
        return new MeasurementImpl(null, observables.get(observable));
    }
    
}
