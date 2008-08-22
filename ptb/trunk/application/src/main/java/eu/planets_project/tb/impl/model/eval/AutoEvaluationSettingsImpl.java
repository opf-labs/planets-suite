package eu.planets_project.tb.impl.model.eval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.planets_project.tb.api.model.eval.AutoEvaluationSettings;
import eu.planets_project.tb.api.model.eval.TBEvaluationTypes;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.TestbedServiceTemplateImpl;

/**
 * Contains the data and settings for automated BMGoal evaluation
 * data: 
 *  - the evalservicetemplate i.e. all information to call the evaluation service
 *  
 *  settings:
 *  - mapping of metrics to the Testbed's evaluation criteria as e.g. very good
 * @author lindleya
 *
 */
//TODO Andrew: change the class variables to the Impl classes for DB preservation
public class AutoEvaluationSettingsImpl implements AutoEvaluationSettings,Serializable{
	
	//the evaluationTBServiceTemplate containing all information on 
	// * executing and extracting values from the execution's result
	// * list of all supported metrics and their BMGoal mapping
	private EvaluationTestbedServiceTemplateImpl evaluationService;	
	private Map<TBEvaluationTypes,List<Config>> mapTBTypesMetricConfig = new HashMap<TBEvaluationTypes,List<Config>>();
	
	
	public AutoEvaluationSettingsImpl(TestbedServiceTemplate template){
		this.evaluationService = (EvaluationTestbedServiceTemplateImpl) template;
		
		//init the map TBTypes_Config_mapping
		for(TBEvaluationTypes type: TBEvaluationTypes.values()){
			this.mapTBTypesMetricConfig.put(type,new ArrayList<Config>());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#getConfig(eu.planets_project.tb.api.model.eval.TBEvaluationTypes)
	 */
	public List<Config> getConfig(TBEvaluationTypes type){
		return this.mapTBTypesMetricConfig.get(type);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#setEvaluationService(eu.planets_project.tb.api.services.TestbedServiceTemplate)
	 */
	public void setEvaluationService(TestbedServiceTemplate template){
		this.evaluationService = (EvaluationTestbedServiceTemplateImpl) template;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#getEvaluationService()
	 */
	public TestbedServiceTemplate getEvaluationService(){
		return  this.evaluationService;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#addConfiguration(eu.planets_project.tb.api.model.eval.TBEvaluationTypes, eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config)
	 */
	public void addConfiguration(TBEvaluationTypes type, Config config){
		this.getConfig(type).add(config);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#removeAllConfigurations(eu.planets_project.tb.api.model.eval.TBEvaluationTypes)
	 */
	public void removeAllConfigurations(TBEvaluationTypes type) {
		this.mapTBTypesMetricConfig.put(type, new ArrayList<Config>());
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#removeAllConfigurations()
	 */
	public void removeAllConfigurations() {
		for(TBEvaluationTypes type: TBEvaluationTypes.values()){
			this.removeAllConfigurations(type);
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#removeConfiguration(eu.planets_project.tb.api.model.eval.TBEvaluationTypes, eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config)
	 */
	public void removeConfiguration(TBEvaluationTypes type, Config config) {
		if(this.getConfig(type).contains(config)){
			this.getConfig(type).remove(config);
		}
	}

	/**
	 * @author lindleyA
	 * Contains the configuration for evaluating a single metric
	 * i.e. metric (name, type), math expression (lt,gt,eq) and the boundary value
	 */
	public class ConfigImpl implements Config, Serializable{
	
		private Metric metric;
		private String sMathExpr ="";
		private String sEvalBoundary ="";
		
		public ConfigImpl(String sMathExpr, String sEvalBoundary, Metric metric){
			if(sMathExpr!=null)
				this.setMathExpr(sMathExpr);
			if(sEvalBoundary!=null)
				this.setEvalBoundary(sEvalBoundary);
			if(metric!=null)
				this.setMetric(metric);
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config#setMetric(eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric)
		 */
		public void setMetric(Metric metric){
			this.metric = metric;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config#getMetric()
		 */
		public Metric getMetric(){
			return this.metric;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config#getMathExpr()
		 */
		public String getMathExpr(){
			return this.sMathExpr;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config#setMathExpr(java.lang.String)
		 */
		public void setMathExpr(String expr){
			this.sMathExpr = expr;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config#getEvalBoundary()
		 */
		public String getEvalBoundary(){
			return this.sEvalBoundary;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Config#setEvalBoundary(java.lang.String)
		 */
		public void setEvalBoundary(String boundary){
			this.sEvalBoundary = boundary;
		}
	}
	
	public class MetricImpl implements Metric,Serializable{
		
		private String sName = "";
		private String sType ="";
		private String sDescription ="";
		
		public MetricImpl(String sName, String sType){
			init(sName,sType,null);
		}
		
		public MetricImpl(String sName, String sType, String sDescription){
			init(sName,sType,sDescription);	
		}
		
		private void init(String sName, String sType, String sDescription){
			if(sName!=null)
				this.setName(sName);
			if(sType!=null)
				this.setType(sType);
			if(sDescription!=null)
				this.setDescription(sDescription);
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#setName(java.lang.String)
		 */
		public void setName(String sName){
			this.sName = sName;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getName()
		 */
		public String getName(){
			return this.sName;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#setType(java.lang.String)
		 */
		public void setType(String sType){
			this.sType = sType;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getType()
		 */
		public String getType(){
			return this.sType;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#setDescription(java.lang.String)
		 */
		public void setDescription(String sDescr){
			this.sDescription = sDescr;
		}
		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings.Metric#getDescription()
		 */
		public String getDescription(){
			return this.sDescription;
		}
	}

}
