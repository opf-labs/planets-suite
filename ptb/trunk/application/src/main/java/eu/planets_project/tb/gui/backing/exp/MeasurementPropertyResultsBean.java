package eu.planets_project.tb.gui.backing.exp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.model.measure.MeasurementImpl;

/**
 * Contains for a measurement propertyID all MeasurementResults over all execution runs in a way the
 * HtmlDataTable can handle it. (line information)
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 04.04.2009
 *
 */
public class MeasurementPropertyResultsBean {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(MeasurementPropertyResultsBean.class);
	
	public MeasurementPropertyResultsBean(String inputDigoRef, String mpropertyID,List<Calendar> allRunDates){
		initResultsHM(allRunDates);
		this.setMeasurementPropertyID(mpropertyID);
		this.setInputDigoRef(inputDigoRef);
	}
	
	private String mID="";
	
	public String getMeasurementPropertyID(){
		return mID;
	}
	public void setMeasurementPropertyID(String mID){
		this.mID = mID;
	}
	
	private String inputDigoRef="";
	/**
	 * For the inputDigitalObject ref this record belongs to
	 */
	public String getInputDigoRef(){
		return inputDigoRef;
	}
	
	public void setInputDigoRef(String inputDigoRef){
		this.inputDigoRef = inputDigoRef;
	}
	
	HashMap<Long,RecordBean> results = new HashMap<Long,RecordBean>();
	
	public HashMap<Long,RecordBean> getAllResults(){
		return results;
	}
	
	public void addResult(Calendar runDate, MeasurementImpl result){
	    if( this.results != null ) {
	        this.results.put(runDate.getTimeInMillis(), new RecordBean(result));
	    }
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
	
	/**
	 * This prevents the hm of returning null for a RecordBean
	 * @param allRunDates
	 */
	private void initResultsHM(List<Calendar> allRunDates){	
	    if( this.results != null && allRunDates != null ) {
	        for(Calendar runDate : allRunDates){
	            if( runDate != null ) {
	                this.results.put(runDate.getTimeInMillis(), new RecordBean());
	            }
	        }
	    }
	}
	
	public class RecordBean{
		
		public RecordBean(){}
		
		public RecordBean(MeasurementImpl mrec){
			this.setRecordValue(mrec.getValue());
		}
		
		private String value = null;
		
		/**
		 * Note: In case of a String > 60 chars a modified Substring 
		 * for GUI representation is returned.
		 * @return
		 */
		public String getRecordValue(){
			return getWordWrappedString(this.value);
		}
		
		public void setRecordValue(String value){
			this.value = value;
		}
		
		//FIXME: Firefox does not support word-wrap in tables - therefore cut off after 60 chars...
		//http://www.w3.org/TR/css3-text/#word-wrap
		private String getWordWrappedString(String s){
			String ret = s;
			if((s!=null)&&(s.length()>60)){
				StringBuffer sb = new StringBuffer(s);
				sb.insert(30, "[...]");
				ret = sb.substring(0, 65);
			}
			return ret;
		}
		
	}

}
