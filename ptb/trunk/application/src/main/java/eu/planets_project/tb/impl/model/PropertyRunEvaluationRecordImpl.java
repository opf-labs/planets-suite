package eu.planets_project.tb.impl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Embeddable
//@Entity
@XmlRootElement(name = "PropertyRunEvaluationRecord")
@XmlAccessorType(XmlAccessType.FIELD) 
public class PropertyRunEvaluationRecordImpl implements java.io.Serializable {
	
	@Transient @XmlTransient
	private static Log log = LogFactory.getLog(PropertyRunEvaluationRecordImpl.class);
	private static final long serialVersionUID = -6334296433243258565L;
	
	private Integer runEvalValue = -1;
	//a evaluation record for a run may compare values for a property in 1..n stages
	private List<String> stageNames = new ArrayList<String>();
	
	/**
	 * 1 stageName to evaluate a single property of a single stage
	 * 2..n stageNames to look for properties to compare
	 * i.e. to answer question like: how well has a certain property been preserved measured in pre-migrate and post-migrate
	 * @param digObjectRef
	 * @param execRun
	 * @param expStageName 1..n stageNames the property shall be taken for comparisson
	 * @return
	 */
	public void setRunEvalValue(int evalVal, String...expStageName) {
		stageNames = Arrays.asList(expStageName);
		runEvalValue = evalVal;
	}
	
	public int getRunEvalValue(){
		return this.runEvalValue;
	}
	
	public List<String> getEvalForStages(){
		return this.stageNames;
	}

}
