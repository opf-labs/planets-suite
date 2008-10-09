package eu.planets_project.tb.impl.services;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;


/**
 * @author lindleya
 * This class provides information on the structure of the underlying evalution service
 * and how to access it's relevant information in it's service responds and beyond this
 * contains methods to apply this on a given DOM document.
 * 
 * for example a XCDL structure 
 * <property id="2" name="imageHeight" unit="pixel" compStatus="complete">
 *			<values type="int">
 *				<src>32</src>
 *				<tar>32</tar>
 *			</values>
 *			<metric id="200" name="equal" result="true"/>
 *			<metric id="201" name="intDiff" result="0"/>
 *			<metric id="210" name="percDev" result="0.000000"/>
 * </property>
 * The structure should of individual BMGoals used within this class must be the same
 */
@Entity
public class EvaluationTestbedServiceTemplateImpl extends TestbedServiceTemplateImpl implements Serializable,Cloneable{
	
	//contains a mapping of the TB BenchmarkGoalID to the ID(name) used within the service's BMGoal result
	private HashMap<String, String> mappingGoalIDToPropertyID = new HashMap<String, String>(); 
	private String sXPathForBMGoalRootNodes = "/*//property";
	//the extracted property's name
	private String sXPathForBMGoalName = "@name";
	private String sSrcXpath = "./values/src";
	private String sTarXpath = "./values/tar";
	private String sMetric = "./metric";
	private String sMetricName = "@name";
	private String sMetricResult = "@result";
	// This annotation specifies that the property or field is not persistent.
	@Transient
    @XmlTransient
	private static Log log;
	
	public EvaluationTestbedServiceTemplateImpl(){
		log = PlanetsLogger.getLogger(this.getClass(),"testbed-log4j.xml");
	}
	//the default (as used in XCDL) for compStatus success and failure
	public static String sCompStatusSuccess = "complete";
	private String sCompStatusXpath = "@compStatus";
	
	/**
	 * Defines the Xpath Statement to look up the root note of a given TB Benchmark Goal
	 * where all evaluation information can be found beneath - within the service's output
	 * @param xPath
	 */
	public void setXPathForBMGoalRootNodes(String xPath){
		//in this example: <property>
		this.sXPathForBMGoalRootNodes = xPath;
	}
	
	public String getXPathForBMGoalRootNodes(){
		return sXPathForBMGoalRootNodes;
	}
	
	/**
	 * Method for applying the XPathForBMGoalRootNodes on a given DOM Document
	 * @param doc
	 * @return
	 * @throws XPathExpressionException
	 */
	public NodeList getAllEvalResultsRootNodes(Document doc) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xpath.evaluate(sXPathForBMGoalRootNodes, 
													doc, 
													XPathConstants.NODESET);
		return nodes;
	}
	
	/**
	 * Returns the root node containing all evaluation results for a given TB Benchmarkgoal
	 * in the default case e.g. <property> would be returned
	 * @param bmGoal
	 */
	/*public void getBMGoalRootNode(BenchmarkGoal bmGoal){
		
	}*/
	
	/**
	 * Defines the Xpath Statement to look up the name of a given TB Benchmark Goal
	 * starting from the BMGoalRootNode. 
	 * [no absolute path, relative to the property's root node]
	 * @param xPath
	 */
	public void setXPathForName(String xPath){
		//in this example attribute: 'name'
		sXPathForBMGoalName = xPath;
	}
	
	public String getXPathForNameConfig(){
		return sXPathForBMGoalName;
	}	
	
	/**
	 * @param node a Node obtained by getAllEvalResultsRootNodes()
	 * @return
	 * @throws XPathExpressionException
	 */
	public String getEvalResultName(Node node) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String result = (String)xpath.evaluate(sXPathForBMGoalName, 
											   node, 
											   XPathConstants.STRING);
		return result;
	}
	
	/**
	 * Defines an XPath how to look up the value of of the src file within the service's output.
	 * [no absolute path, relative to the property's root node]
	 * @param xPath
	 */
	public void setXPathForSrcValue(String xPath){
		this.sSrcXpath = xPath;

	}
	
	public String getXPathForSrcConfig(){
		return sSrcXpath;
	}	
	
	/**
	 * @param node a Node obtained by getAllEvalResultsRootNodes()
	 * @return
	 * @throws XPathExpressionException
	 */
	public String getEvalResultSrcValue(Node node) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String result = (String)xpath.evaluate(sSrcXpath, 
											   node, 
											   XPathConstants.STRING);
		return result;
	}
	
	/**
	 * Defines an XPath how to look up the value of of the tar file within the service's output.
	 * [no absolute path, relative to the property's root node]
	 * @param xPath
	 */
	public void setXPathForTarValue(String xPath){
		this.sTarXpath = xPath;

	}
	
	public String getXPathForTarConfig(){
		return sTarXpath;
	}	
	
	/**
	 * @param node a Node obtained by getAllEvalResultsRootNodes()
	 * @return
	 * @throws XPathExpressionException
	 */
	public String getEvalResultTarValue(Node node) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String result = (String)xpath.evaluate(sTarXpath, 
											   node, 
											   XPathConstants.STRING);
		return result;
	}
	
	
	/**
	 * Defines an XPath how to look up the status of the computation for a property within the service's output.
	 * [no absolute path, relative to the property's root node]
	 * @param xPath
	 */
	public void setXPathToCompStatus(String xPath){
		sCompStatusXpath = xPath;
	}
	
	public String getXPathToCompStatus(){
		return sCompStatusXpath;
	}	
	
	/**
	 * @param node a Node obtained by getAllEvalResultsRootNodes()
	 * @return
	 * @throws XPathExpressionException
	 */
	public String getEvalResultCompStatus(Node node) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		String result = (String)xpath.evaluate(sCompStatusXpath, 
											   node, 
											   XPathConstants.STRING);
		return result;
	}
	
	/**
	 * The string used to indicate success e.g. "complete" to compare the status of a property
	 * within the extracted service output.
	 * @param complete
	 */
	public void setStringForCompStatusSuccess(String complete){
		sCompStatusSuccess = complete;
	}
	
	public String getStringForCompStatusSuccess(){
		return sCompStatusSuccess;
	}
	
	
	/**
	 * NOTE: This is currently a mock implementation --> replace by a service or file
	 * Returns a list of all available metrics for a specified bmGoal
	 * @param bmGoal
	 * @return <MetricName,java type as: java.lang.Integer> 
	 */
	public Map<String,String> getAllAvailableMetricsForBMGoal(String bmGoalID){
		//TODO: replace temporarily a mockup - returns a fixed list of elements
		//TODO: read this data from a service
		Map<String,String> ret = new HashMap<String,String>();
		BenchmarkGoalsHandler bmGoalHandler = BenchmarkGoalsHandlerImpl.getInstance();
		BenchmarkGoal bmGoal = bmGoalHandler.getBenchmarkGoal(bmGoalID);
		//MOCK STARTING FROM HERE - Descriptions of Metric vals given in PP5/D1
		//Should either be provided by xml, the imported template or a service
		if(bmGoal.getName().equals("XCDLimageHeight")){
			ret.put("equal","java.lang.Boolean");
			ret.put("intDiff","java.lang.Integer");
			ret.put("percDev","java.lang.Double");
		}
		
		if(bmGoal.getName().equals("XCDLimageWidth")){
			ret.put("equal","java.lang.Boolean");
			ret.put("intDiff","java.lang.Integer");
			ret.put("percDev","java.lang.Double");
		}
		
		if(bmGoal.getName().equals("XCDLnormData")){
			ret.put("hammingDistance","java.lang.Integer");
			ret.put("RMSE","java.lang.Double");
		}
		
		return ret;
	}
	
	
	/**
	 * Defines an XPath how to look up the value of metric within the service's output.
	 * [no absolute path, relative to the metrics's root node]
	 * @param xPath
	 */
	public void setXPathToMetricValue(String xPath){
		sMetricResult = xPath;
	}
	
	public String getXPathToMetricResultConfig(){
		return sMetricResult;
	}
	
	
	/**
	 * Defines an XPath how to look up the metric nodes within the service's output for a given property
	 * [no absolute path, relative to the properties's root node]
	 * @param xPath
	 */
	public void setXPathToMetricNode(String xPath){
		sMetric = xPath;
	}
	
	public String getXPathToMetricNodeConfig(){
		return sMetric;
	}
	
	
	/**
	 * Defines an XPath how to look up the value of metric within the service's output.
	 * [no absolute path, relative to the metrics's root node]
	 * @param xPath
	 */
	public void setXPathToMetricName(String xPath){
		sMetricName = xPath;
	}
	
	public String getXPathToMetricNameConfig(){
		return sMetricName;
	}	
	
	/**
	 * @param node a Node obtained by getAllEvalResultsRootNodes()
	 * @return LinkedHashMap<MetricName,MetricResult>
	 * @throws XPathExpressionException
	 */
	public Map<String,String> getEvalResultMetricNamesAndValues(Node node) throws XPathExpressionException{
		//get all metric nodes for this property
		NodeList metrics = getEvalResultMetricNodes(node);
		
		Map<String,String> ret = new LinkedHashMap<String,String>();
		
		if((metrics!=null)&&(metrics.getLength()>0)){
			for(int i=0;i<metrics.getLength();i++){
				//metric node
				Node n = metrics.item(i);
				
				//query the name
				XPath xpathName = XPathFactory.newInstance().newXPath();
				String name = (String)xpathName.evaluate(sMetricName, 
													   n, 
													   XPathConstants.STRING);
				//query the result
				XPath xpathResult = XPathFactory.newInstance().newXPath();
				String value = (String)xpathResult.evaluate(sMetricResult, 
													   n, 
													   XPathConstants.STRING);
				ret.put(name, value);
				
			}
		}
		return ret;
	}
	
	
	/**
	 * @param node a Node obtained by getAllEvalResultsRootNodes()
	 * @return
	 * @throws XPathExpressionException
	 */
	public NodeList getEvalResultMetricNodes(Node node) throws XPathExpressionException{
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList result = (NodeList)xpath.evaluate(sMetric,
											   node, 
											   XPathConstants.NODESET);
		return result;
	}

	
	/**
	 * Specifies a mapping between a TB Benchmark Goal object (identified through it's name + id) and a PropertyName(=Metric)
	 * Please note: the TB BMGoal must be available on this TB instance to create this mapping
	 * @param BMGoalName
	 * @param BMGoalID
	 * @param PropertyName
	 */
	public void setBMGoalPropertyNameMapping(String BMGoalName, String BMGoalID, String PropertyName){
		BenchmarkGoalsHandler handler = BenchmarkGoalsHandlerImpl.getInstance();
		List<String> ids = handler.getAllBenchmarkGoalIDs();
		if(ids.contains(BMGoalID)){
			BenchmarkGoal goal = handler.getBenchmarkGoal(BMGoalID);
			if(goal.getName().equals(BMGoalName)){
				//now store the mapping
				this.mappingGoalIDToPropertyID.put(goal.getID(), PropertyName);
			}
			else{
				//ignore - TB internal BM goals are different ones.
				return;
			}
		}
		else{
			//ignore - BM Goal not supported on this machine
			return;
		}
	}
	
	/**
	 * Returns a list of all BenchmarkGoalsIDs that contain a parameter mapping
	 * within the service's output
	 * @return Collection<BenchmarkGoalID>
	 */
	public Collection<String> getAllMappedBenchmarkGoalIDs(){
		return this.mappingGoalIDToPropertyID.keySet();
	}
	
	
	/**
	 * Returns a list of all Metrics available for a certain BMGoal
	 * @param BMGoalID
	 * @return
	 */
	public String getMappedPropertyName(String BMGoalID){
		if(this.mappingGoalIDToPropertyID.containsKey(BMGoalID)){
			return this.mappingGoalIDToPropertyID.get(BMGoalID);
		}
		return "";
	}
	
	public EvaluationTestbedServiceTemplateImpl clone(){
		EvaluationTestbedServiceTemplateImpl template = new EvaluationTestbedServiceTemplateImpl();
		template = (EvaluationTestbedServiceTemplateImpl) super.clone();
		
		return template;
	}

}
