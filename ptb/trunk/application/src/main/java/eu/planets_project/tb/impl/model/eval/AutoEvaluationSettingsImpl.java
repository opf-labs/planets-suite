package eu.planets_project.tb.impl.model.eval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.EvalError;
import bsh.Interpreter;

import eu.planets_project.tb.api.model.eval.AutoEvaluationSettings;
import eu.planets_project.tb.api.model.eval.Metric;
import eu.planets_project.tb.api.model.eval.TBEvaluationTypes;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;

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
public class AutoEvaluationSettingsImpl implements AutoEvaluationSettings,Serializable{
	
	/**
     * A suitable version ID.
     */
    private static final long serialVersionUID = 5821566896694386631L;
    
    
    //the evaluationTBServiceTemplate containing all information on 
	// * executing and extracting values from the execution's result
	// * list of all supported metrics and their BMGoal mapping
	private EvaluationTestbedServiceTemplateImpl evaluationService;	
	private Map<TBEvaluationTypes,List<Config>> mapTBTypesMetricConfig = new HashMap<TBEvaluationTypes,List<Config>>();
	@Transient
    @XmlTransient
	private static Log log;
	/**
	 * A no-arg constructor for JAXB.
	 */
	public AutoEvaluationSettingsImpl() {};
	
	public AutoEvaluationSettingsImpl(TestbedServiceTemplate template){
		log = LogFactory.getLog(this.getClass());
		
		this.evaluationService = ((EvaluationTestbedServiceTemplateImpl) template).clone();
		
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
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.eval.AutoEvaluationSettings#autoValidate(java.util.Map)
	 */
	public TBEvaluationTypes autoValidate(
			Map<String, String> extractedMetricData) {
		
		//iterate over all types
		for(TBEvaluationTypes type : TBEvaluationTypes.values()){
			
			//all settings that have been configured
			List<Config> lc = this.getConfig(type);
			if((lc!=null)&&(lc.size()>0)){
				
				//check if all conditions for a given evaluation Type
				boolean bOK = true;
				for(Config c : lc){
					String sName = c.getMetric().getName();
					if(extractedMetricData.containsKey(sName)){

						String mathExpr = c.getMathExpr();
						String boundary = c.getEvalBoundary();
						String extractedData =  extractedMetricData.get(sName);
						
						//is either '<' or '>' or '='. '=' can be either be interpreted as '=' or 'equals' dependent on the metric type
						if(c.getMetric().getNumericTypes().contains(c.getMetric().getType())){
							//evaluate a numeric expression
							//build the expression: e.g. 20 < 21
							String expression = extractedData + mathExpr + boundary;
							try {
								//evaluate = as equals
								if(mathExpr.equals("=")){
									if(!(boundary.equals(extractedData))){
									bOK = false;
									}
								}
								//evaluate as math expression
								else if(!validateNumericExpr(expression)){
									bOK = false;
								}
							} catch (EvalError e) {
									log.error("Malformed metric evaluation expression: "+expression+" "+e);
									bOK = false;
							}
						}
						else{
							//evaluate an textual equals expression
							if(!(boundary.equals(extractedData))){
								bOK = false;
							}
						}				
					}
					else
						bOK = false;
				}
				
				//check if all conditions were true
				if(bOK)
					return type;
			}
		}
		
		//if no type was identified, return null
		return null;
	}
	
	/**
	 * This method uses the beanshell for validating string expressions as "(5 + 2) > 5"
	 * @param mathExpr e.g. "5 < 4" returns false
	 * @return
	 * @throws EvalError wrong syntax in expression
	 */
	private boolean validateNumericExpr(String mathExpr)throws EvalError{
		 // http://www.beanshell.org/javadoc/bsh/Interpreter.html
        Interpreter interpreter = new Interpreter();
        return (Boolean)interpreter.eval(mathExpr);
        /*
            interpreter.eval("n = 3 + 4");
	        System.out.println("n = "+interpreter.get("n"));
	        
	        interpreter.set("a", 1);
        	interpreter.set("b", 2);
        	String expression = "(a + b) > 2";
        	Object result = interpreter.eval(expression);
        	System.out.println(expression+" ? "+result);
         */
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

}
