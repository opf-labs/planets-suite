package eu.planets_project.tb.impl.model.eval.mockup;

import java.util.HashMap;
import java.util.Map;

/**
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
	
    public static final String URIDigitalObjectPropertyRoot = "planets:pc/";
    public static final String URIXCDLPropertyRoot = "planets:pc/xcdl/property/";
    public static final String URIXCDLMetricRoot = "planets:pc/xcdl/metric/";
    public static final String URIServicePropertyRoot = "planets:tb/srv/";

    /** */
	private TecRegMockup(){
		
	}
	
	/** */
	static {
		metrics = new HashMap<String,Object>();
		//information on xcdl metrics
		metrics.put("planets:pc/xcdl/metric/levenstheinDistance/id", "15");
		metrics.put("planets:pc/xcdl/metric/levenstheinDistance/name", "levenstheinDistance");
		metrics.put("planets:pc/xcdl/metric/levenstheinDistance/inputdatatype", new String[]{"XCL:string"});
		metrics.put("planets:pc/xcdl/metric/levenstheinDistance/returntdataype", "XCL:int");
		metrics.put("planets:pc/xcdl/metric/levenstheinDistance/returndatatype/javaobjecttype", "java.lang.Integer");
		metrics.put("planets:pc/xcdl/metric/levenstheinDistance/description", "The Levenshtein distance is a distance measure for strings. Two strings are compared with respect to the three basic operations insert, delete and replace in order to transform string A into string B. The value for this metric is the number of operations needed for transformation.");
		
		metrics.put("planets:pc/xcdl/metric/ratDiff/id", "205");
		metrics.put("planets:pc/xcdl/metric/ratDiff/name", "ratDiff");
		metrics.put("planets:pc/xcdl/metric/ratDiff/inputdatatype", new String[]{"XCL:rational"});
		metrics.put("planets:pc/xcdl/metric/ratDiff/returntdataype", "XCL:rational");
		metrics.put("planets:pc/xcdl/metric/ratDiff/returndatatype/javaobjecttype", "java.lang.Double");
		metrics.put("planets:pc/xcdl/metric/ratDiff/description", "Arithmetical difference of two values (A,B) of data type XCL:rational");
		
		metrics.put("planets:pc/xcdl/metric/percDev/id", "210");
		metrics.put("planets:pc/xcdl/metric/percDev/name", "percDev");
		metrics.put("planets:pc/xcdl/metric/percDev/inputdatatype", new String[]{"XCL:int","XCL:rational"});
		metrics.put("planets:pc/xcdl/metric/percDev/returntdataype", "XCL:rational");
		metrics.put("planets:pc/xcdl/metric/percDev/returndatatype/javaobjecttype", "java.lang.Double");
		metrics.put("planets:pc/xcdl/metric/percDev/description", "Percental deviation of two values (A,B) of data type XCL:int or XCL:rational. The output value indicates the percental deviation of value B from value A. An output value of PercDeviation=0.0 indicates equal values.");
		
		metrics.put("planets:pc/xcdl/metric/equal/id", "200");
		metrics.put("planets:pc/xcdl/metric/equal/name", "equal");
		metrics.put("planets:pc/xcdl/metric/equal/inputdatatype", new String[]{"Any XCL data type"});
		metrics.put("planets:pc/xcdl/metric/equal/returntdataype", "XCL:boolean");
		metrics.put("planets:pc/xcdl/metric/equal/returndatatype/javaobjecttype", "java.lang.Boolean");
		metrics.put("planets:pc/xcdl/metric/equal/description", "Metric 'equal' is a simple comparison of two values (A,B) of any XCL data type on equality");
		
		metrics.put("planets:pc/xcdl/metric/intDiff/id", "201");
		metrics.put("planets:pc/xcdl/metric/intDiff/name", "intDiff");
		metrics.put("planets:pc/xcdl/metric/intDiff/inputdatatype", new String[]{"XCL:int"});
		metrics.put("planets:pc/xcdl/metric/intDiff/returntdataype", "XCL:int");
		metrics.put("planets:pc/xcdl/metric/intDiff/returndatatype/javaobjecttype", "java.lang.Integer");
		metrics.put("planets:pc/xcdl/metric/intDiff/description", "Arithmetical difference of two values (A,B) of data type XCL:int.");
		
		metrics.put("planets:pc/xcdl/metric/hammingDistance/id", "10");
		metrics.put("planets:pc/xcdl/metric/hammingDistance/name", "hammingDistance");
		metrics.put("planets:pc/xcdl/metric/hammingDistance/inputdatatype", new String[]{"XCL:int", "XCL:rational", "XCL:string"});
		metrics.put("planets:pc/xcdl/metric/hammingDistance/returntdataype", "XCL:int");
		metrics.put("planets:pc/xcdl/metric/hammingDistance/returndatatype/javaobjecttype", "java.lang.Integer");
		metrics.put("planets:pc/xcdl/metric/hammingDistance/description", "The hamming distance counts the number of non-corresponding symbols of two ordered sequences. The sequences must have equal length; hammingDistance=0 indicates equal sequences, i.e. no substitutions are required to change a sequence into the other. Hamming distance is often used for string comparison");
	
		metrics.put("planets:pc/xcdl/metric/RMSE/id", "50");
		metrics.put("planets:pc/xcdl/metric/RMSE/name", "RMSE");
		metrics.put("planets:pc/xcdl/metric/RMSE/inputdatatype", new String[]{"XCL:int", "XCL:rational"});
		metrics.put("planets:pc/xcdl/metric/RMSE/returntdataype", "XCL:rational");
		metrics.put("planets:pc/xcdl/metric/RMSE/returndatatype/javaobjecttype", "java.lang.Double");
		metrics.put("planets:pc/xcdl/metric/RMSE/description", "Root mean squared error is a widely used measure for deviation. It is obtained by the square root of the average of the squared differences of single values of two sets.");
	
        properties = new HashMap<String,Object>();
		//information on xcdl properties
		properties.put("planets:pc/xcdl/property/imageHeight/id", "2");
		properties.put("planets:pc/xcdl/property/imageHeight/name", "imageHeight");
		properties.put("planets:pc/xcdl/property/imageHeight/unit", "pixel");
		properties.put("planets:pc/xcdl/property/imageHeight/metrics", new String[]{"equal","intDiff","percDev"});
		
		properties.put("planets:pc/xcdl/property/imageWidth/id", "30");
		properties.put("planets:pc/xcdl/property/imageWidth/name", "imageWidth");
		properties.put("planets:pc/xcdl/property/imageWidth/unit", "pixel");
		properties.put("planets:pc/xcdl/property/imageWidth/metrics", new String[]{"equal","intDiff","percDev"});
		
		properties.put("planets:pc/xcdl/property/normData/id", "300");
		properties.put("planets:pc/xcdl/property/normData/name", "normData");
		properties.put("planets:pc/xcdl/property/normData/unit", "undefined");
		properties.put("planets:pc/xcdl/property/normData/metrics", new String[]{"hammingDistance","RMSE","levenstheinDistance"});
		
		properties.put("planets:pc/xcdl/property/bitsPerSample/id", "151");
		properties.put("planets:pc/xcdl/property/bitsPerSample/name", "bitsPerSample");
		properties.put("planets:pc/xcdl/property/bitsPerSample/unit", "bit");
		properties.put("planets:pc/xcdl/property/bitsPerSample/metrics", new String[]{"equal","intDiff"});
		
		properties.put("planets:pc/xcdl/property/compression/id", "18");
		properties.put("planets:pc/xcdl/property/compression/name", "compression");
		properties.put("planets:pc/xcdl/property/compression/unit", "undefined");
		properties.put("planets:pc/xcdl/property/compression/metrics", new String[]{"equal"});
		
		properties.put("planets:pc/xcdl/property/imageType/id", "20");
		properties.put("planets:pc/xcdl/property/imageType/name", "imageType");
		properties.put("planets:pc/xcdl/property/imageType/unit", "undefined");
		properties.put("planets:pc/xcdl/property/imageType/metrics", new String[]{"equal"});
		
		properties.put("planets:pc/xcdl/property/orientation/id", "9");
		properties.put("planets:pc/xcdl/property/orientation/name", "orientation");
		properties.put("planets:pc/xcdl/property/orientation/unit", "undefined");
		properties.put("planets:pc/xcdl/property/orientation/metrics", new String[]{"equal"});
		
		properties.put("planets:pc/xcdl/property/planarConfiguration/id", "49");
		properties.put("planets:pc/xcdl/property/planarConfiguration/name", "planarConfiguration");
		properties.put("planets:pc/xcdl/property/planarConfiguration/unit", "undefined");
		properties.put("planets:pc/xcdl/property/planarConfiguration/metrics", new String[]{"equal"});
		
		properties.put("planets:pc/xcdl/property/resolutionUnit/id", "22");
		properties.put("planets:pc/xcdl/property/resolutionUnit/name", "resolutionUnit");
		properties.put("planets:pc/xcdl/property/resolutionUnit/unit", "undefined");
		properties.put("planets:pc/xcdl/property/resolutionUnit/metrics", new String[]{"equal"});
		
		properties.put("planets:pc/xcdl/property/resolutionX/id", "23");
		properties.put("planets:pc/xcdl/property/resolutionX/name", "resolutionX");
		properties.put("planets:pc/xcdl/property/resolutionX/unit", "undefined");
		properties.put("planets:pc/xcdl/property/resolutionX/metrics", new String[]{"equal","ratDiff","percDev"});
	
		properties.put("planets:pc/xcdl/property/resolutionY/id", "24");
		properties.put("planets:pc/xcdl/property/resolutionY/name", "resolutionY");
		properties.put("planets:pc/xcdl/property/resolutionY/unit", "undefined");
		properties.put("planets:pc/xcdl/property/resolutionY/metrics", new String[]{"equal","ratDiff","percDev"});
		
		properties.put("planets:pc/xcdl/property/scanDocName/id", "144");
		properties.put("planets:pc/xcdl/property/scanDocName/name", "scanDocName");
		properties.put("planets:pc/xcdl/property/scanDocName/unit", "undefined");
		properties.put("planets:pc/xcdl/property/scanDocName/metrics", new String[]{"levenstheinDistance"});

		// Additional TB properties.  FIXME How should these work?
		properties.put(URIServicePropertyRoot+"wallclock/name", "service exec time");
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

}
