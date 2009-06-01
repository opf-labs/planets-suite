package eu.planets_project.tb.gui.backing.exp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.gui.backing.exp.MeasurementPropertyResultsBean.RecordBean;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;

/**
 * Used to display the per digital object / property evaluation entries in a table line
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 15.04.2009
 *
 */
public class EvaluationPropertyResultsBean extends MeasurementPropertyResultsBean{

	private static PlanetsLogger log = PlanetsLogger.getLogger(EvaluationPropertyResultsBean.class, "testbed-log4j.xml");
	private HashMap<Long,HashMap<String,EvalRecordBean>> evalresults = new HashMap<Long,HashMap<String,EvalRecordBean>>();
	private String[] stageNames;
	
	public EvaluationPropertyResultsBean(String inputDigoRef, String mpropertyID, List<Calendar> allRunDates, String[] stageNames) {
		super(inputDigoRef, mpropertyID, allRunDates);
		this.initEvalResults(allRunDates, stageNames);
		this.stageNames = stageNames;
	}
	
	public void addMeasurementResult(Calendar runDate, String stageName, MeasurementRecordImpl result){
		//fetch the hm and add an additional stageName that shall be compared for the propertyID
		this.evalresults.get(runDate.getTimeInMillis()).put(stageName, new EvalRecordBean(result));
	}
	
	/**
	 * HashMap<runDateInMillis,<HashMap<stageName,EvalRecordBean>>
	 * @return
	 */
	public HashMap<Long,HashMap<String,EvalRecordBean>> getAllEvalResults(){
		return evalresults;
	}
	
	public String[] getStageNames(){
		return this.stageNames;
	}
	
	public void setEvalResultValue(Calendar runDate, String[] stageNames, int runEvalValue){
		//a stagename contains different values but all contain the same evalRecBean eval value.
		for(String stageName : stageNames){
			this.evalresults.get(runDate.getTimeInMillis()).get(stageName).setEvalValue(runEvalValue);
		}
	}
	
	Integer propertyLineEvalValue = -1;
	public int getPropertyEvalValue(){
		return this.propertyLineEvalValue;
	}
	
	//FIXME Calculate this or allow to set this with a single button?
	public void setPropertyEvalValue(int evalVal){
		this.propertyLineEvalValue = evalVal;
	}
	
	/**
	 * This prevents the hm of returning null for a EvalRecordBean
	 * @param allRunDates
	 */
	private void initEvalResults(List<Calendar> allRunDates, String[] stageNames){	
		for(Calendar runDate : allRunDates){
			for (String stage : stageNames){
				HashMap<String,EvalRecordBean> hm = new HashMap<String,EvalRecordBean>();
				hm.put(stage, new EvalRecordBean());
				if( runDate != null )
				    this.evalresults.put(runDate.getTimeInMillis(), hm);
			}
		}
	}
	
	/**
	 * TODO AL: currently not using EvalRecordBean's set/get EvalValue - only evaluating the entire line
	 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
	 * @since 16.04.2009
	 *
	 */
	public class EvalRecordBean extends RecordBean{
		
		public EvalRecordBean(){}
		
		public EvalRecordBean(MeasurementRecordImpl mrec){
			super(mrec);
		}
		
		private int evalValue = -1;
		
		public int getEvalValue(){
			return evalValue;
		}
		
		public void setEvalValue(int val){
			this.evalValue = val;
		}
		
	}
	
	/***************** unused methods - overwritten with null*****************/
	//this method is not supported by this bean
	@Override
	public HashMap<Long,RecordBean> getAllResults(){
		return null;
	}
	
	@Override
	public void addResult(Calendar runDate, MeasurementRecordImpl result){
		//this method is not supported by this bean
	}
	
	/****************** end unnused methods ***********************************/

}
