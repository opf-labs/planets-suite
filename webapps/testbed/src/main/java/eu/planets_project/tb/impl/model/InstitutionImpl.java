/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.impl.model;

import eu.planets_project.tb.impl.model.finals.PlanetsInstitutionsImpl;

/**
 * @author alindley
 *
 */
//@Entity
public class InstitutionImpl implements eu.planets_project.tb.api.model.Institution {

	//@Id
	private long lInstitutionID;
	private int iInstitutionType;
	private String sAddress, sName;
	private String sPrimaryContactID;
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#checkInstitutionAllowed(int)
	 */
	public boolean checkInstitutionAllowed(int instID) {
		PlanetsInstitutionsImpl institutions = new PlanetsInstitutionsImpl();
		return institutions.checkInstitutionIDisValid(instID);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.Institution#checkInstitutionTypeAllowed(int)
	 */
	public boolean checkInstitutionTypeAllowed(int instID) {
		PlanetsInstitutionsImpl institutions = new PlanetsInstitutionsImpl();
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
		PlanetsInstitutionsImpl inst = new PlanetsInstitutionsImpl();
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
		PlanetsInstitutionsImpl inst = new PlanetsInstitutionsImpl();
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
		PlanetsInstitutionsImpl inst = new PlanetsInstitutionsImpl();
		boolean b2 = inst.checkInstitutionTypeIsValid(typeID);
		
		if (b2)
			this.iInstitutionType = typeID;
	}

	public String getPrimaryContact() {
		return this.sPrimaryContactID;
	}

	public void setPrimaryContact(String userID) {
		this.sPrimaryContactID = userID;
	}

}
