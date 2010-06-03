/**
 * 
 */
package eu.planets_project.tb.impl.services.tags;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.services.tags.ServiceTag;

/**
 * @author Andrew Lindley, ARC
 * ServiceTag corresponds to free annotation of a given tag name and it's corresponding value
 * a list of externally defined tags and priorities can be retrieved by using the
 * DefaultServiceTagHandler.
 */
@Entity
public class ServiceTagImpl implements ServiceTag, java.io.Serializable{
		
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private long id;
	private String sName ="";
	private String sValue="";
	private String sDescription = "";
	private int iPriority = 2;


		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#getName()
		 */
		public String getName() {
			return this.sName;
		}
		
		public void setName(String name){
			if(name!=null){
				this.sName = name;
			}
		}


		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#getValue()
		 */
		public String getValue() {
			return this.sValue;
		}

		
		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#setTag(java.lang.String, java.lang.String)
		 */
		public void setTag(String sTagName, String sTagValue) {
			if((sTagName!=null)&&(sTagValue!=null)){
				this.sName = sTagName;
				this.sValue = sTagValue;
			}
			
		}


		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#getDescription()
		 */
		public String getDescription() {
			return this.sDescription;
		}


		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#setDescription(java.lang.String)
		 */
		public void setDescription(String sDescription) {
			if(sDescription!=null){
				this.sDescription = sDescription;
			}
		}


		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#setTag(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void setTag(String sTagName, String sTagValue, String sDescription) {
			this.setTag(sTagName, sTagValue);
			this.setDescription(sDescription);
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#getPriority()
		 */
		public int getPriority() {
			return this.iPriority;
		}

		/* (non-Javadoc)
		 * @see eu.planets_project.tb.api.services.tags.ServiceTag#setPriority(int)
		 */
		public void setPriority(int i) {
			this.iPriority = i;
		}
}
