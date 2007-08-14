/**
 * 
 */
package eu.planets_project.tb.impl.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import eu.planets_project.tb.impl.TestbedManager;
import eu.planets_project.tb.impl.model.finals.PlanetsInstitutions;

/**
 * @author alindley
 *
 */
@Entity
public class Institution implements eu.planets_project.tb.api.model.Institution {

	@Id
	private long lInstitutionID;
	private int iInstitutionType;
	private String sAddress, sName;
	private long lPrimaryContactID;
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#checkInstitutionAllowed(int)
	 */
	public boolean checkInstitutionAllowed(int instID) {
		PlanetsInstitutions institutions = new PlanetsInstitutions();
		return institutions.checkInstitutionIDisValid(instID);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#checkInstitutionTypeAllowed(int)
	 */
	public boolean checkInstitutionTypeAllowed(int instID) {
		PlanetsInstitutions institutions = new PlanetsInstitutions();
		return institutions.checkInstitutionTypeIsValid(instID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#getInstitutionAddress()
	 */
	public String getInstitutionAddress() {
		return this.sAddress;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#getInstitutionID()
	 */
	public long getInstitutionID() {
		return lInstitutionID;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#getInstitutionName()
	 */
	public String getInstitutionName() {
		return this.sName;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#getInstitutionType()
	 */
	public String getInstitutionTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#getInstitutionTypeID()
	 */
	public int getInstitutionTypeID() {
		return this.iInstitutionType;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#setInstitution(int, int)
	 */
	public void setInstitution(int instID, int instTypeID) {
		PlanetsInstitutions inst = new PlanetsInstitutions();
		boolean b1 = inst.checkInstitutionIDisValid(instID);
		boolean b2 = inst.checkInstitutionTypeIsValid(instTypeID);
		
		if (b1||b2){
			this.iInstitutionType = instTypeID;
			//need to convert from int used in finals - to long, used for EJB persistent ID
			this.lInstitutionID = Long.valueOf(instID);
		}
			
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#setInstitution(int)
	 */
	public void setInstitution(int instID) {
		PlanetsInstitutions inst = new PlanetsInstitutions();
		boolean b1 = inst.checkInstitutionIDisValid(instID);
		
		if (b1){
			//need to convert from int used in finals - to long, used for EJB persistent ID
			this.lInstitutionID = Long.valueOf(instID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#setInstitutionType(int)
	 */
	public void setInstitutionType(int typeID) {
		PlanetsInstitutions inst = new PlanetsInstitutions();
		boolean b2 = inst.checkInstitutionTypeIsValid(typeID);
		
		if (b2)
			this.iInstitutionType = typeID;
	}

	public eu.planets_project.tb.api.model.User getPrimaryContact() {
		TestbedManager tbManager = TestbedManager.getInstance();
		eu.planets_project.tb.api.UserManager manager = tbManager.getUserManager();
		return manager.getUser(this.lPrimaryContactID);
	}

	public void setPrimaryContact(eu.planets_project.tb.api.model.User user) {
		this.lPrimaryContactID = user.getUserID();
	}

	public void setPrimaryContact(long userID) {
		this.lPrimaryContactID = userID;
	}

}
