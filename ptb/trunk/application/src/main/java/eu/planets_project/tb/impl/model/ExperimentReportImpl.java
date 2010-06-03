/**
 * 
 */
package eu.planets_project.tb.impl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.model.ExperimentReport;

/**
 * @author alindley
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExperimentReportImpl implements ExperimentReport, java.io.Serializable {

	@Id
	@GeneratedValue
    @XmlTransient
	private long id;
	
	private String sHeader;
	private String sBodyText;
	
	public ExperimentReportImpl(){
		sHeader = new String();
		sBodyText = new String();
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentReport#setHeader(java.lang.String)
	 */
	public void setHeader(String text){
		this.sHeader = text;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentReport#getHeader()
	 */
	public String getHeader(){
		return this.sHeader;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentReport#setBodyText(java.lang.String)
	 */
	public void setBodyText(String text){
		this.sBodyText = text;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentReport#getBodyText()
	 */
	public String getBodyText(){
		return this.sBodyText;
	}

}
