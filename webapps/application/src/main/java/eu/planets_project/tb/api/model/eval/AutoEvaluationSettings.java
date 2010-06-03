/**
 * 
 */
package eu.planets_project.tb.api.model.eval;

import java.util.List;
import java.util.Map;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;

/**
 * Contains the data and settings for automated BMGoal evaluation
 * data: 
 *  - the evalservicetemplate i.e. all information to call the evaluation service
 *  settings:
 *  - mapping of metrics to the Testbed's evaluation criteria as e.g. very good
 * @author lindleya
 */
public interface AutoEvaluationSettings {
	
	/**
	 * Returns the configuration on how to determine the results for a given
	 * TBEvaluationType e.g. 'very good'
	 * @param type
	 * @return
	 */
	public List<Config> getConfig(TBEvaluationTypes type);
	public void setEvaluationService(TestbedServiceTemplate template);
	public TestbedServiceTemplate getEvaluationService();
	
	/**
	 * Adds a given configuration (mapping of metrics to TB evaluation criteria) for the TB evaluation criteria
	 */
	public void addConfiguration(TBEvaluationTypes type, Config config);
	public void removeConfiguration(TBEvaluationTypes type, Config config);
	public void removeAllConfigurations(TBEvaluationTypes type);
	public void removeAllConfigurations();
	
	/**
	 * This method is used for automatically validating the provided extracted metric data
	 * according to these AutoEvaluationSettings
	 * Please note: 
	 * a) all metric configurations need to evaluate positively for a TBEvaluationType;
	 * b) if the extractedMetricData does not contain all required data for reasoning then see a)
	 * b) if no configuration evaluates positively null is returned
	 * @param extractedMetricData
	 * @return evaluation result: e.g. 'very good' or null if no automated result could be extracted
	 */
	public TBEvaluationTypes autoValidate(Map<String,String> extractedMetricData);
	
	/**
	 * @author lindleyA
	 * Contains the configuration for evaluating a single metric
	 * i.e. metric (name, type), math expression (lt,gt,eq) and the boundary value
	 */
	public interface Config{
		
		public void setMetric(Metric metric);
		public Metric getMetric();
		
		/**
		 * The math expression as gt, lt, eq
		 * @return
		 */
		public String getMathExpr();
		public void setMathExpr(String expr);
		
		/**
		 * The specified boundary value for evaluating the success
		 * e.g. 10 (=extracted autoEvalValue) < (=getMathExpr) 20 (=getEvalBoundary)
		 * @return
		 */
		public String getEvalBoundary();

		/**
		 * @param boundary
		 * e.g. "xyz" for java.lang.Integer
		 */
		public void setEvalBoundary(String boundary);
	}

}
