package eu.planets_project.tb.gui.backing.exp;

import java.util.Calendar;
import java.util.HashMap;

import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;

/**
 * Contains for a measurement propertyID all MeasurementResults over all execution runs in a way the
 * HtmlDataTable can handle it. (line information)
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 04.04.2009
 *
 */
public class MeasurementPropertyResultsBean {
	
	public MeasurementPropertyResultsBean(String mpropertyID){
		this.setMeasurementPropertyID(mpropertyID);
	}
	
	private String mID="";
	
	public String getMeasurementPropertyID(){
		return mID;
	}
	public void setMeasurementPropertyID(String mID){
		this.mID = mID;
	}
	
	HashMap<Long,MeasurementRecordImpl> results = new HashMap<Long,MeasurementRecordImpl>();
	
	public HashMap<Long,MeasurementRecordImpl> getAllResults(){
		return results;
	}
	
	public void addResult(Calendar runDate, MeasurementRecordImpl result){
		this.results.put(runDate.getTimeInMillis(), result);
	}
	
	private MeasurementImpl m;
	/**
	 * Not there to retrieve the value, but all other information as name, description, etc.
	 * @return
	 */
	public MeasurementImpl getMeasurementInfo(){
		return m;
	}
	
	public void setMeasurementInfo(MeasurementImpl m){
		this.m = m;
	}

}
