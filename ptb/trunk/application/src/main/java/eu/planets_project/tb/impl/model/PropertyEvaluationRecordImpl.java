package eu.planets_project.tb.impl.model;

import java.util.Calendar;
import java.util.HashMap;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.Log;

/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 14.04.2009
 *
 */
@Embeddable
//@Entity
@XmlRootElement(name = "PropertyEvaluationRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class PropertyEvaluationRecordImpl implements java.io.Serializable {
	
	@Transient @XmlTransient
	private static Log log = LogFactory.getLog(PropertyEvaluationRecordImpl.class);
	private static final long serialVersionUID = -6342964354359585615L;
	
	private String propertyURI = new String();
	//a sub record for a given execution 
	private HashMap<Long, PropertyRunEvaluationRecordImpl> propertyRunEvalRecords = new HashMap<Long, PropertyRunEvaluationRecordImpl>();
	private Integer propEvalValue = -1;
	
	//@Id
	//@GeneratedValue
    //@XmlTransient
	//private long id;
	
	
	/**
	 * A no prop constructor required by JPA - don't use.
	 */
	public PropertyEvaluationRecordImpl(){}
	
	public PropertyEvaluationRecordImpl(String propertyID){
		this.propertyURI = propertyID;
	}

	
	
	public String getPropertyID() {
		return propertyURI;
	}
	
	/**
	 * an evaluation value for this property (for a pre-given digitalObjectInputRecord)
	 * over all experiment executions
	 * @return
	 */
	public int getPropertyEvalValue(){
		return this.propEvalValue;
	}
	
	public void setPropertyEvalValue(int evalVal){
		this.propEvalValue = evalVal;
	}
	
	/**
	 * Contains the evaluation information for this property broken down by the execution dates
	 * @param runDate
	 * @param propRunEvalRec
	 */
	public void addPropertyRunEvalRecord(Calendar runDate, PropertyRunEvaluationRecordImpl propRunEvalRec){
		if(propertyRunEvalRecords!=null){
			this.propertyRunEvalRecords.put(runDate.getTimeInMillis(), propRunEvalRec);
		}
	}
	
	public PropertyRunEvaluationRecordImpl getPropertyRunEvalRecord(Calendar runDate){
		if((propertyRunEvalRecords==null)&&(propertyRunEvalRecords.containsKey(runDate.getTimeInMillis()))){
			return propertyRunEvalRecords.get(runDate.getTimeInMillis());
		}
		return null;
	}
	
	public HashMap<Long, PropertyRunEvaluationRecordImpl> getPropertyRunEvalRecords(){
		if(propertyRunEvalRecords==null){
			return new HashMap<Long, PropertyRunEvaluationRecordImpl>();
		}
		return this.propertyRunEvalRecords;
	}

}
