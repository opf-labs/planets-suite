package eu.planets_project.tb.impl.model.eval.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.planets_project.tb.impl.TestbedManagerImpl;

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
	
	private static TecRegMockup instance;
	//contains all the mockup registry's data in memory
	private static Map<String,Object> mData;
	
	private TecRegMockup(){
		
	}
	
	public static synchronized TecRegMockup getInstance(){
		if (instance == null){
			instance = new TecRegMockup();
			load();
		}
		return instance;
	}
	
	private static void load(){
		mData = new HashMap<String,Object>();
		//information on xcdl metrics
		mData.put("planets:pc/xcdl/metric/levenstheinDistance/id", "15");
		mData.put("planets:pc/xcdl/metric/levenstheinDistance/name", "levenstheinDistance");
		mData.put("planets:pc/xcdl/metric/levenstheinDistance/inputdatatype", new String[]{"XCL:string"});
		mData.put("planets:pc/xcdl/metric/levenstheinDistance/returntdataype", "XCL:int");
		mData.put("planets:pc/xcdl/metric/levenstheinDistance/returndatatype/javaobjecttype", "java.lang.Integer");
		mData.put("planets:pc/xcdl/metric/levenstheinDistance/description", "The Levenshtein distance is a distance measure for strings. Two strings are compared with respect to the three basic operations insert, delete and replace in order to transform string A into string B. The value for this metric is the number of operations needed for transformation.");
		
		mData.put("planets:pc/xcdl/metric/ratDiff/id", "205");
		mData.put("planets:pc/xcdl/metric/ratDiff/name", "ratDiff");
		mData.put("planets:pc/xcdl/metric/ratDiff/inputdatatype", new String[]{"XCL:rational"});
		mData.put("planets:pc/xcdl/metric/ratDiff/returntdataype", "XCL:rational");
		mData.put("planets:pc/xcdl/metric/ratDiff/returndatatype/javaobjecttype", "java.lang.Double");
		mData.put("planets:pc/xcdl/metric/ratDiff/description", "Arithmetical difference of two values (A,B) of data type XCL:rational");
		
		mData.put("planets:pc/xcdl/metric/percDev/id", "210");
		mData.put("planets:pc/xcdl/metric/percDev/name", "percDev");
		mData.put("planets:pc/xcdl/metric/percDev/inputdatatype", new String[]{"XCL:int","XCL:rational"});
		mData.put("planets:pc/xcdl/metric/percDev/returntdataype", "XCL:rational");
		mData.put("planets:pc/xcdl/metric/percDev/returndatatype/javaobjecttype", "java.lang.Double");
		mData.put("planets:pc/xcdl/metric/percDev/description", "Percental deviation of two values (A,B) of data type XCL:int or XCL:rational. The output value indicates the percental deviation of value B from value A. An output value of PercDeviation=0.0 indicates equal values.");
		
		mData.put("planets:pc/xcdl/metric/equal/id", "200");
		mData.put("planets:pc/xcdl/metric/equal/name", "equal");
		mData.put("planets:pc/xcdl/metric/equal/inputdatatype", new String[]{"Any XCL data type"});
		mData.put("planets:pc/xcdl/metric/equal/returntdataype", "XCL:boolean");
		mData.put("planets:pc/xcdl/metric/equal/returndatatype/javaobjecttype", "java.lang.Boolean");
		mData.put("planets:pc/xcdl/metric/equal/description", "Metric 'equal' is a simple comparison of two values (A,B) of any XCL data type on equality");
		
		mData.put("planets:pc/xcdl/metric/intDiff/id", "201");
		mData.put("planets:pc/xcdl/metric/intDiff/name", "intDiff");
		mData.put("planets:pc/xcdl/metric/intDiff/inputdatatype", new String[]{"XCL:int"});
		mData.put("planets:pc/xcdl/metric/intDiff/returntdataype", "XCL:int");
		mData.put("planets:pc/xcdl/metric/intDiff/returndatatype/javaobjecttype", "java.lang.Integer");
		mData.put("planets:pc/xcdl/metric/intDiff/description", "Arithmetical difference of two values (A,B) of data type XCL:int.");
		
		mData.put("planets:pc/xcdl/metric/hammingDistance/id", "10");
		mData.put("planets:pc/xcdl/metric/hammingDistance/name", "hammingDistance");
		mData.put("planets:pc/xcdl/metric/hammingDistance/inputdatatype", new String[]{"XCL:int", "XCL:rational", "XCL:string"});
		mData.put("planets:pc/xcdl/metric/hammingDistance/returntdataype", "XCL:int");
		mData.put("planets:pc/xcdl/metric/hammingDistance/returndatatype/javaobjecttype", "java.lang.Integer");
		mData.put("planets:pc/xcdl/metric/hammingDistance/description", "The hamming distance counts the number of non-corresponding symbols of two ordered sequences. The sequences must have equal length; hammingDistance=0 indicates equal sequences, i.e. no substitutions are required to change a sequence into the other. Hamming distance is often used for string comparison");
	
		mData.put("planets:pc/xcdl/metric/RMSE/id", "50");
		mData.put("planets:pc/xcdl/metric/RMSE/name", "RMSE");
		mData.put("planets:pc/xcdl/metric/RMSE/inputdatatype", new String[]{"XCL:int", "XCL:rational"});
		mData.put("planets:pc/xcdl/metric/RMSE/returntdataype", "XCL:rational");
		mData.put("planets:pc/xcdl/metric/RMSE/returndatatype/javaobjecttype", "java.lang.Double");
		mData.put("planets:pc/xcdl/metric/RMSE/description", "Root mean squared error is a widely used measure for deviation. It is obtained by the square root of the average of the squared differences of single values of two sets.");
	
		//information on xcdl properties
		mData.put("planets:pc/xcdl/property/imageHeight/id", "2");
		mData.put("planets:pc/xcdl/property/imageHeight/name", "imageHeight");
		mData.put("planets:pc/xcdl/property/imageHeight/unit", "pixel");
		mData.put("planets:pc/xcdl/property/imageHeight/metrics", new String[]{"equal","intDiff","percDev"});
		
		mData.put("planets:pc/xcdl/property/imageWidth/id", "30");
		mData.put("planets:pc/xcdl/property/imageWidth/name", "imageWidth");
		mData.put("planets:pc/xcdl/property/imageWidth/unit", "pixel");
		mData.put("planets:pc/xcdl/property/imageWidth/metrics", new String[]{"equal","intDiff","percDev"});
		
		mData.put("planets:pc/xcdl/property/normData/id", "300");
		mData.put("planets:pc/xcdl/property/normData/name", "normData");
		mData.put("planets:pc/xcdl/property/normData/unit", "undefined");
		mData.put("planets:pc/xcdl/property/normData/metrics", new String[]{"hammingDistance","RMSE","levenstheinDistance"});
		
		mData.put("planets:pc/xcdl/property/bitsPerSample/id", "151");
		mData.put("planets:pc/xcdl/property/bitsPerSample/name", "bitsPerSample");
		mData.put("planets:pc/xcdl/property/bitsPerSample/unit", "bit");
		mData.put("planets:pc/xcdl/property/bitsPerSample/metrics", new String[]{"equal","intDiff"});
		
		mData.put("planets:pc/xcdl/property/compression/id", "18");
		mData.put("planets:pc/xcdl/property/compression/name", "compression");
		mData.put("planets:pc/xcdl/property/compression/unit", "undefined");
		mData.put("planets:pc/xcdl/property/compression/metrics", new String[]{"equal"});
		
		mData.put("planets:pc/xcdl/property/imageType/id", "20");
		mData.put("planets:pc/xcdl/property/imageType/name", "imageType");
		mData.put("planets:pc/xcdl/property/imageType/unit", "undefined");
		mData.put("planets:pc/xcdl/property/imageType/metrics", new String[]{"equal"});
		
		mData.put("planets:pc/xcdl/property/orientation/id", "9");
		mData.put("planets:pc/xcdl/property/orientation/name", "orientation");
		mData.put("planets:pc/xcdl/property/orientation/unit", "undefined");
		mData.put("planets:pc/xcdl/property/orientation/metrics", new String[]{"equal"});
		
		mData.put("planets:pc/xcdl/property/planarConfiguration/id", "49");
		mData.put("planets:pc/xcdl/property/planarConfiguration/name", "planarConfiguration");
		mData.put("planets:pc/xcdl/property/planarConfiguration/unit", "undefined");
		mData.put("planets:pc/xcdl/property/planarConfiguration/metrics", new String[]{"equal"});
		
		mData.put("planets:pc/xcdl/property/resolutionUnit/id", "22");
		mData.put("planets:pc/xcdl/property/resolutionUnit/name", "resolutionUnit");
		mData.put("planets:pc/xcdl/property/resolutionUnit/unit", "undefined");
		mData.put("planets:pc/xcdl/property/resolutionUnit/metrics", new String[]{"equal"});
		
		mData.put("planets:pc/xcdl/property/resolutionX/id", "23");
		mData.put("planets:pc/xcdl/property/resolutionX/name", "resolutionX");
		mData.put("planets:pc/xcdl/property/resolutionX/unit", "undefined");
		mData.put("planets:pc/xcdl/property/resolutionX/metrics", new String[]{"equal","ratDiff","percDev"});
	
		mData.put("planets:pc/xcdl/property/resolutionY/id", "24");
		mData.put("planets:pc/xcdl/property/resolutionY/name", "resolutionY");
		mData.put("planets:pc/xcdl/property/resolutionY/unit", "undefined");
		mData.put("planets:pc/xcdl/property/resolutionY/metrics", new String[]{"equal","ratDiff","percDev"});
		
		mData.put("planets:pc/xcdl/property/scanDocName/id", "144");
		mData.put("planets:pc/xcdl/property/scanDocName/name", "scanDocName");
		mData.put("planets:pc/xcdl/property/scanDocName/unit", "undefined");
		mData.put("planets:pc/xcdl/property/scanDocName/metrics", new String[]{"levenstheinDistance"});
	}
	
	/**
	 * Returns the registry's value for a given key or null
	 * Please note case sensitivity 
	 * @param sParamURI
	 * @return
	 */
	public Object getParameterVal(String sParamURI){
		if(this.mData.containsKey(sParamURI))
			return this.mData.get(sParamURI);
		return null;
	}

}
