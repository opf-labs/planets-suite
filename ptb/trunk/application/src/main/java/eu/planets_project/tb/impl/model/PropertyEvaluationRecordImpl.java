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
	private static Log log;
	@Transient @XmlTransient
	public static final String EVALUATION_MODE_COMPARE = "compare";
	@Transient @XmlTransient
	public static final String EVALUATION_MODE_SINGLE = "single";
	
	private static final long serialVersionUID = -6342964354359585615L;
	
	//@Id
	//@GeneratedValue
    @XmlTransient
	private long id;
	
	private String propertyURI = new String();
	private String evalMode = new String();
	private HashMap<String,String> propEvaluationRecords = new HashMap<String,String>();
	
	
	/**
	 * A no prop constructor required by JPA - don't use.
	 */
	public PropertyEvaluationRecordImpl(){}
	
	public PropertyEvaluationRecordImpl(String propertyID){
		this.propertyURI = propertyID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.PropertyEvaluationRecord#getPropertyID()
	 */
	public String getPropertyID() {
		return propertyURI;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.PropertyEvaluationRecord#getEvaluationMode()
	 */
	public String getEvaluationMode() {
		return this.evalMode;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.PropertyEvaluationRecord#setEvaluationMode(java.lang.String)
	 */
	public void setEvaluationMode(String evalMode) {
		this.evalMode = evalMode;
	}

	/**
	 * If EvaluationMode is compare
	 * i.e. to answer question like: how well has a certain property been preserved measured in pre-migrate and post-migrate
	 * @param digObjectRef
	 * @param execRun
	 * @param expStageName1 stageName to extract the propertie's value from
	 * @param expStageName2 stageName to extract the second's value from
	 * @return
	 */
	public String getEvaluationValue(String inputDigObjectRef, Calendar execRun,
			String expStageName1, String expStageName2) {
		if(this.propEvaluationRecords!=null){
			//the key to store the value
			String key = this.propertyURI+evalMode+inputDigObjectRef+execRun.getTimeInMillis()+expStageName1+expStageName2;
			
			return this.propEvaluationRecords.get(key);
		}
		return "";
	}


	/**
	 * If EvaluationMode is Single
	 * @param digObjectRef
	 * @param execRun
	 * @param expStageName1
	 * @return
	 */
	public String getEvaluationValue(String inputDigObjectRef, Calendar execRun,
			String expStageName1) {
		return getEvaluationValue(inputDigObjectRef, execRun, expStageName1);
	}


	
}
